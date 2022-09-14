package com.dash.resource.endpoints;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dash.dao.CampaignDao;
import com.dash.dao.InstanceDao;
import com.dash.dao.TaskDao;
import com.dash.dao.TaskParameterDao;
import com.dash.jwt.JwtTokenUtil;
import com.dash.model.Account;
import com.dash.model.Campaign;
import com.dash.model.CronUnit;
import com.dash.model.Instance;
import com.dash.model.Task;
import com.dash.model.TaskParameter;
import com.dash.resource.model.Response;
import com.dash.resource.model.Status;
import com.dash.undefdash.ApplicationFactory;
import com.source.utils.SourceUtils;

@RestController
@RequestMapping("/task")
public class TaskResource {
	private static final Logger logger = LoggerFactory.getLogger(TaskResource.class);

	@Autowired
	private ApplicationFactory appFactory;

	@GetMapping("/getTaskById")
	public Response<Task> getTaskById(@RequestParam("taskId") String taskId) {
		Response<Task> response = new Response<Task>();
		response.setData(new ArrayList<Task>());
		try {

			Session session = appFactory.getSession();
			
			Optional<Task> optTask = TaskDao.getTaskByIdAndAccount(session, taskId, 1);
			if (optTask.isPresent() == false) {
				response.setMessage("task not found");
				response.setStatus(Status.ERROR);
				return response;
			} else {
				Task task = optTask.get();
				response.setStatus(Status.SUCCESS);
				response.setData(Arrays.asList(task));
				return response;
			}

		} catch (Exception ex) {
			logger.error("method getTaskById error : ", ex);
			response.setStatus(Status.ERROR);
			response.setMessage("internal server error : " + ex.getMessage());
			return response;
		}
	}

	@PostMapping("/getCampaignListTasks")
	public Response<Task> getCampaignListTasks(@RequestParam("campaignId") long campaignId) {

		Response<Task> response = new Response<Task>();
		response.setData(new ArrayList<Task>());

		try {

			Session session = appFactory.getSession();

			Optional<Campaign> optCampaign = CampaignDao.getCampaignbyIdAndAccount(session, campaignId, 1);
			if (optCampaign.isPresent() == false) {
				response.setStatus(Status.ERROR);
				response.setMessage("campaign not found");
				return response;
			} else {

				response.setStatus(Status.SUCCESS);
				Campaign campaign = optCampaign.get();
				Optional<List<Task>> optListTasks = TaskDao.getListTasksPerCampaign(session, campaign.getCampaignId());
				if (optListTasks.isPresent())
					response.setData(optListTasks.get());

				return response;
			}

		} catch (Exception ex) {
			logger.error("method getCampaignListTasks error : ", ex);
			response.setStatus(Status.ERROR);
			response.setMessage("internal server error : " + ex.getMessage());
			return response;
		}
	}

	@PostMapping(value = "/scheduleTask")
	public Response<Task> scheduleTask(@RequestParam("campaignId") long campaignId,
			@RequestParam("instanceId") long instanceId,
			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'hh:mm:ss") Date expireDate,
			@RequestParam(name = "cronUnit", required = false, defaultValue = "HOURS") String cronUnit,
			@RequestParam(name = "cronValue", required = false, defaultValue = "10") int cronValue,
			@RequestParam("repeatForever") boolean repeatForever,
			@RequestParam("parameters") Map<String, String> parameters) {

		Response<Task> response = new Response<Task>();
		try {

			Session session = appFactory.getSession();

			Optional<Campaign> optCampaign = CampaignDao.getCampaignbyIdAndAccount(session, campaignId,
					1);
			if (optCampaign.isPresent() == false) {
				response.setStatus(Status.ERROR);
				response.setMessage("campaign not found");
				return response;
			}

			Optional<Instance> optInstance = InstanceDao.getInstanceByIdAndAccount(session, instanceId,
					1);
			if (optInstance.isPresent() == false) {
				response.setStatus(Status.ERROR);
				response.setMessage("instance not found");
				return response;
			} else if (optInstance.get().isOnline() == false) {
				// try to ping it
				if (SourceUtils.instanceStatus(optInstance.get()) == false) {
					response.setStatus(Status.ERROR);
					response.setMessage("instance API is offline , cannot perform this operation ");
					return response;
				}
			}

			// database task
			Task newTask = new Task();
			newTask.setTaskId(UUID.randomUUID().toString());
			newTask.setCreated(new Date());
			newTask.setCampaign(optCampaign.get());
			newTask.setInstance(optInstance.get());
			newTask.setExpireDate(expireDate);
			newTask.setRepeatForever(repeatForever);

			if (repeatForever) {
				newTask.setCronUnit(CronUnit.valueOf(cronUnit));
				newTask.setCronValue(cronValue);
			}

			newTask.setStatus(com.dash.model.Status.active);
			newTask.setDeleted(false);
			newTask.setListTaskParameters(new ArrayList<TaskParameter>());

			for (Map.Entry<String, String> entry : parameters.entrySet()) {
				TaskParameter parameter = new TaskParameter(entry.getKey(), entry.getValue(), newTask);
				newTask.getListTaskParameters().add(parameter);
			}

			Response<String> apiResponse = SourceUtils.registerTask(newTask);
			if (apiResponse.getStatus().equals(Status.SUCCESS)) {

				TaskDao.saveTask(session, newTask);

				for (TaskParameter taskParameter : newTask.getListTaskParameters())
					TaskParameterDao.saveTaskParameter(session, taskParameter);

				response.setStatus(apiResponse.getStatus());
				response.setData(Arrays.asList(newTask));

				return response;

			} else {

				response.setStatus(apiResponse.getStatus());
				response.setMessage(apiResponse.getMessage());
				return response;
			}

		} catch (Exception ex) {
			logger.error("method scheduleTask error : ", ex);
			response.setStatus(Status.ERROR);
			response.setMessage("internal server error : " + ex.getMessage());
			return response;
		}
	}

	@PostMapping("/startStopTask")
	public Response<String> startStopTask(@RequestParam("taskId") String taskId,
			@RequestParam("operation") String operation) {
		Response<String> response = new Response<String>();
		try {

			Session session = appFactory.getSession();
			Optional<Task> optTask = TaskDao.getTaskByIdAndAccount(session, taskId, 1);
			if (optTask.isPresent() == false) {
				response.setMessage("task not found");
				response.setStatus(Status.ERROR);
				return response;
			} else {

				Task task = optTask.get();
				if (operation.equalsIgnoreCase("stop") && task.getStatus().equals(com.dash.model.Status.active)) {
					task.setStatus(com.dash.model.Status.inactive);
					TaskDao.updateTask(session, task);
					SourceUtils.apiUnregister(task);
				} else if (operation.equalsIgnoreCase("start")
						&& task.getStatus().equals(com.dash.model.Status.inactive)) {

					task.setStatus(com.dash.model.Status.active);
					TaskDao.updateTask(session, task);
					SourceUtils.registerTask(task);
				}

				response.setMessage("operation performed successfully");
				response.setStatus(Status.SUCCESS);
				return response;

			}

		} catch (Exception ex) {
			logger.error("method startStopTask error : ", ex);
			response.setStatus(Status.ERROR);
			response.setMessage("internal server error : " + ex.getMessage());
			return response;
		}
	}

	// admin can delete task from all over place
	@PostMapping("/deleteTask")
	public Response<String> deleteTask(@RequestParam("taskId") String taskId) {
		Response<String> response = new Response<String>();
		try {

			Session session = appFactory.getSession();

			Optional<Task> optTask = TaskDao.getTaskByIdAndAccount(session, taskId, 1);
			if (optTask.isPresent() == false) {
				response.setStatus(Status.ERROR);
				response.setMessage("task not found");
				return response;
			} else {

				Task task = optTask.get();
				task.setDeleted(true);
				TaskDao.updateTask(session, task);

				if (task.getStatus().equals(com.dash.model.Status.active))
					SourceUtils.apiUnregister(task);

				response.setStatus(Status.SUCCESS);
				response.setMessage("task deleted");
				return response;
			}

		} catch (Exception ex) {
			logger.error("method deleteTask error : ", ex);
			response.setStatus(Status.ERROR);
			response.setMessage("internal server error : " + ex.getMessage());
			return response;
		}
	}

	// admin can delete task from all over place
	@PostMapping("/editTask")
	public Response<String> editTask(@RequestParam("taskId") String taskId,
			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'hh:mm:ss") Date expireDate,
			@RequestParam(name = "cronUnit", required = false, defaultValue = "HOURS") String cronUnit,
			@RequestParam(name = "cronValue", required = false, defaultValue = "10") int cronValue,
			@RequestParam("repeatForever") boolean repeatForever,
			@RequestParam("parameters") Map<String, String> parameters) {
		Response<String> response = new Response<String>();
		try {

			Session session = appFactory.getSession();

			Optional<Task> optTask = TaskDao.getTaskByIdAndAccount(session, taskId, 1);
			if (optTask.isPresent() == false) {
				response.setStatus(Status.ERROR);
				response.setMessage("task not found");
				return response;
			} else {

				Task task = optTask.get();
				// unregister first
				if (SourceUtils.apiUnregister(task)) {

					task.setExpireDate(expireDate);
					task.setRepeatForever(repeatForever);

					if (repeatForever) {
						task.setCronUnit(CronUnit.valueOf(cronUnit));
						task.setCronValue(cronValue);
					}

					task.setStatus(com.dash.model.Status.active);
					task.setDeleted(false);
					task.setListTaskParameters(new ArrayList<TaskParameter>());

					for (Map.Entry<String, String> entry : parameters.entrySet()) {
						TaskParameter parameter = new TaskParameter(entry.getKey(), entry.getValue(), task);
						task.getListTaskParameters().add(parameter);
					}

					Response<String> apiResponse = SourceUtils.registerTask(task);
					if (apiResponse.getStatus().equals(Status.SUCCESS)) {
						response.setStatus(Status.SUCCESS);
						response.setMessage("task edited");

						TaskDao.updateTask(session, task);
						return response;
					}
				}

			}

			response.setStatus(Status.ERROR);
			response.setMessage("unable to edit task");
			return response;

		} catch (Exception ex) {
			logger.error("method editTask error : ", ex);
			response.setStatus(Status.ERROR);
			response.setMessage("internal server error : " + ex.getMessage());
			return response;
		}
	}

}
