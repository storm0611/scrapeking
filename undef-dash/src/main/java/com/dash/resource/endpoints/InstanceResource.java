package com.dash.resource.endpoints;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.validator.routines.UrlValidator;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dash.dao.InstanceDao;
import com.dash.dao.SourceDao;
import com.dash.dao.TaskDao;
import com.dash.model.Account;
import com.dash.model.Instance;
import com.dash.model.Source;
import com.dash.model.Task;
import com.dash.resource.model.Response;
import com.dash.resource.model.Status;
import com.dash.undefdash.ApplicationFactory;
import com.source.utils.SourceUtils;

@RestController
@RequestMapping("/instance")
public class InstanceResource {
	private static final Logger logger = LoggerFactory.getLogger(InstanceResource.class);

	@Autowired
	private ApplicationFactory appFactory;

	@PostMapping("/getInstanceParameters")
	public ResponseEntity<String> getInstanceParameters(@RequestParam("instanceId") long instanceId) {
		try {

			Session session = appFactory.getSession();
			Account account = (Account) session.load(Account.class, 1);

			Optional<Instance> optInstance = InstanceDao.getInstanceByIdAndAccount(session, instanceId,
					account.getAccountId());
			if (optInstance.isPresent() == false) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("instance not found");
			} else {
				Instance instance = optInstance.get();
				Optional<String> optJsonConfig = SourceUtils.getListParameters(instance);
				if (optJsonConfig.isPresent() == false) {
					return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
							.body("cannot fetch parameters from instance");
				} else {
					return ResponseEntity.status(HttpStatus.OK).body(optJsonConfig.get());
				}
			}

		} catch (Exception ex) {
			logger.error("method getInstanceParameters error : ", ex);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
		}

	}

	@PostMapping("/addInstance")
	public Response<Instance> addInstance(@RequestParam("url") String url, @RequestParam("sourceId") long sourceId) {
		Response<Instance> response = new Response<Instance>();
		try {

			UrlValidator defaultValidator = new UrlValidator(); // default schemes
			boolean isValidURL = defaultValidator.isValid(url);
			if (isValidURL == false) {
				response.setStatus(Status.ERROR);
				response.setMessage("invalid URL");
				return response;
			}

			Session session = appFactory.getSession();
			Account account = (Account) session.load(Account.class, 1);

			Optional<Source> optSource = SourceDao.getSourceById(session, sourceId);
			if (optSource.isPresent() == false) {
				response.setMessage("source not found");
				response.setStatus(Status.ERROR);
				return response;
			} else {
				Instance instance = new Instance(new Date(), url, optSource.get());
				instance.setOnline(false);
				InstanceDao.saveInstance(session, instance);
				response.setStatus(Status.SUCCESS);
				response.setData(Arrays.asList(instance));
				return response;
			}

		} catch (Exception ex) {
			logger.error("method addInstance error : ", ex);
			response.setStatus(Status.ERROR);
			response.setMessage("internal server error : " + ex.getMessage());
			return response;
		}
	}

	@PostMapping("/deleteInstance")
	public Response<String> deleteInstance(@RequestParam("instanceId") int instanceId) {
		Response<String> response = new Response<String>();
		try {

			Session session = appFactory.getSession();
			Account account = (Account) session.load(Account.class, 1);

			Optional<Instance> optInstance = InstanceDao.getInstanceById(session, instanceId);
			if (optInstance.isPresent() == false) {
				response.setStatus(Status.ERROR);
				response.setMessage("instance not found");
				return response;
			} else {

				Instance instance = optInstance.get();
				if (instance.isDeleted() == false) {
					Optional<List<Task>> optListTasks = TaskDao.getListTasksPerInstance(session,
							instance.getInstanceId());
					if (optListTasks.isPresent()) {
						for (Task task : optListTasks.get()) {
							if (task.getStatus().equals(com.dash.model.Status.active)) {
								// update database deleted attribute
								task.setStatus(com.dash.model.Status.inactive);
								task.setDeleted(true);
								TaskDao.updateTask(appFactory.getSession(), task);
								SourceUtils.apiUnregister(task);
							}
						}
					}
					instance.setDeleted(true);
					InstanceDao.updateInstance(session, instance);
				}

				response.setStatus(Status.SUCCESS);
				response.setMessage("deleted successfully");
				return response;
			}

		} catch (Exception ex) {
			logger.error("method deleteInstance error : ", ex);
			response.setStatus(Status.ERROR);
			response.setMessage("internal server error : " + ex.getMessage());
			return response;
		}
	}

}
