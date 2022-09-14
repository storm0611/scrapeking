package com.interior.api.dao;

import java.io.Serializable;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import javax.ws.rs.core.Form;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.interior.api.model.Response;
import com.interior.api.model.Status;
import com.interior.api.utils.APIUtils;
import com.interior.model.Campaign;
import com.interior.model.EmailServer;
import com.interior.model.EmailTemplate;
import com.interior.model.Instance;
import com.interior.model.Task;

public class TaskDao implements Serializable {
	private static final long serialVersionUID = 1L;
	final static Logger logger = Logger.getLogger(TaskDao.class);

	public static final String GET_TASK_BY_ID = "/task/getTaskById";

	public static final String EDIT_TASK = "/task/editTask";

	public static final String CREATE_TASK = "/task/scheduleTask";
	public static final String DELETE_TASK = "/task/deleteTask";
	public static final String START_STOP_TASK = "/task/startStopTask";
	public static final String LIST_TASK_PER_CAMPAIGN = "/task/getCampaignListTasks";

	public static Response<Task> getTaskById(String taskId) {
		Response<Task> response = new Response<Task>();
		try {

			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("taskId", String.valueOf(taskId));

			Optional<String> optResponseBody = APIUtils.doGet(GET_TASK_BY_ID, map);
			if (optResponseBody.isPresent() == false) {
				response.setStatus(Status.API_CONNECTION_ERROR);
				return response;
			} else {

				List<Task> listTasks = new ArrayList<Task>();
				JsonObject json = new JsonParser().parse(new JsonReader(new StringReader(optResponseBody.get())))
						.getAsJsonObject();
				String strStatus = json.get("status").getAsString();
				if (strStatus.equalsIgnoreCase("success")) {

					if (json.has("data") && json.get("data").isJsonNull() == false) {
						JsonArray data = json.get("data").getAsJsonArray();
						for (JsonElement element : data) {
							JsonObject item = element.getAsJsonObject();

							Task task = new Task();
							task.setTaskId(item.get("taskId").getAsString());
							task.setCreated(APIUtils.API_DATE_FORMATER.parse(item.get("created").getAsString()));

							Instance instance = new Instance();
							instance.setInstanceId(
									item.get("instance").getAsJsonObject().get("instanceId").getAsLong());
							instance.setUrl(item.get("instance").getAsJsonObject().get("url").getAsString());
							task.setInstance(instance);

							task.setRepeatForever(item.get("repeatForever").getAsBoolean());

							if (item.get("cronUnit").isJsonNull() == false)
								task.setCronUnit(item.get("cronUnit").getAsString());

							if (item.get("cronValue").isJsonNull() == false)
								task.setCronValue(item.get("cronValue").getAsInt());

							if (item.get("status").isJsonNull() == false)
								task.setStatus(com.interior.model.Status.valueOf(item.get("status").getAsString()));

							task.setParameters(new LinkedHashMap<String, String>());
							if (item.has("listTaskParameters")
									&& item.get("listTaskParameters").isJsonNull() == false) {
								for (JsonElement paramElement : item.get("listTaskParameters").getAsJsonArray()) {
									task.getParameters().put(paramElement.getAsJsonObject().get("key").getAsString(),
											paramElement.getAsJsonObject().get("value").getAsString());
								}
							}

							listTasks.add(task);
						}
					}
				}
				response.setStatus(Status.valueOf(strStatus.toUpperCase()));
				response.setData(listTasks);
				return response;

			}

		} catch (Exception ex) {
			logger.error("method getListCampaigns error : ", ex);
			response.setStatus(Status.INTERNAL_WEB_APPLICATION_ERROR);
			response.setMessage(ex.getMessage());
			return response;
		}
	}

	public static Response<Task> editTask(Task task) {
		Response<Task> response = new Response<Task>();
		try {

			Form form = new Form();

			form.param("taskId", String.valueOf(task.getTaskId()));

			if (task.getExpireDate() == null)
				form.param("expireDate", "");
			else
				form.param("expireDate", APIUtils.API_DATE_FORMATER.format(task.getExpireDate()));

			form.param("cronUnit", task.getCronUnit());
			form.param("cronValue", String.valueOf(task.getCronValue()));
			form.param("repeatForever", String.valueOf(task.isRepeatForever()));
			form.param("parameters", new Gson().toJson(task.getParameters()));

			Optional<String> optResponseBody = APIUtils.doPost(EDIT_TASK, form);
			if (optResponseBody.isPresent() == false) {
				response.setStatus(Status.API_CONNECTION_ERROR);
				return response;
			} else {
				JsonObject json = new JsonParser().parse(new JsonReader(new StringReader(optResponseBody.get())))
						.getAsJsonObject();
				response.setStatus(Status.valueOf(json.get("status").getAsString()));
				response.setMessage(json.get("message").getAsString());
				return response;
			}

		} catch (Exception ex) {
			logger.error("method editTask error : ", ex);
			response.setStatus(Status.INTERNAL_WEB_APPLICATION_ERROR);
			response.setMessage(ex.getMessage());
			return response;
		}
	}

	public static Response<Task> createTask(Task task) {
		Response<Task> response = new Response<Task>();
		try {

			Form form = new Form();

			form.param("campaignId", String.valueOf(task.getCampaign().getCampaignId()));
			form.param("instanceId", String.valueOf(task.getInstance().getInstanceId()));

			if (task.getExpireDate() == null)
				form.param("expireDate", "");
			else
				form.param("expireDate", APIUtils.API_DATE_FORMATER.format(task.getExpireDate()));

			form.param("cronUnit", task.getCronUnit());
			form.param("cronValue", String.valueOf(task.getCronValue()));
			form.param("repeatForever", String.valueOf(task.isRepeatForever()));
			form.param("parameters", new Gson().toJson(task.getParameters()));

			Optional<String> optResponseBody = APIUtils.doPost(CREATE_TASK, form);
			if (optResponseBody.isPresent() == false) {
				response.setStatus(Status.API_CONNECTION_ERROR);
				return response;
			} else {

				JsonObject json = new JsonParser().parse(new JsonReader(new StringReader(optResponseBody.get())))
						.getAsJsonObject();

				if (json.has("status")) {
					response.setStatus(Status.valueOf(json.get("status").getAsString()));
					if (json.get("status").getAsString().equalsIgnoreCase("success")) {
						JsonArray dataArray = json.get("data").getAsJsonArray();
						if (dataArray.isJsonNull() == false && dataArray.size() > 0) {
							JsonObject obj = dataArray.getAsJsonArray().get(0).getAsJsonObject();
							task.setTaskId(obj.get("taskId").getAsString());
							response.setData(Arrays.asList(task));
						}
					} else {
						response.setMessage(json.get("message").getAsString());
					}
				}
				return response;
			}

		} catch (Exception ex) {
			logger.error("method createTask error : ", ex);
			response.setStatus(Status.INTERNAL_WEB_APPLICATION_ERROR);
			response.setMessage(ex.getMessage());
			return response;
		}
	}

	public static Response<Task> deleteTask(Task task) {
		Response<Task> response = new Response<Task>();
		try {

			Form form = new Form();
			form.param("taskId", String.valueOf(task.getTaskId()));

			Optional<String> optResponseBody = APIUtils.doPost(DELETE_TASK, form);
			if (optResponseBody.isPresent() == false) {
				response.setStatus(Status.API_CONNECTION_ERROR);
				return response;
			} else {

				JsonObject json = new JsonParser().parse(new JsonReader(new StringReader(optResponseBody.get())))
						.getAsJsonObject();
				response.setStatus(Status.valueOf(json.get("status").getAsString()));
				response.setMessage(json.get("message").getAsString());
				return response;
			}

		} catch (Exception ex) {
			logger.error("method deleteTask error : ", ex);
			response.setStatus(Status.INTERNAL_WEB_APPLICATION_ERROR);
			response.setMessage(ex.getMessage());
			return response;
		}
	}

	public static Response<Task> startStopTask(Task task, String operation) {
		Response<Task> response = new Response<Task>();
		try {

			Form form = new Form();

			form.param("taskId", String.valueOf(task.getTaskId()));
			form.param("operation", operation);

			Optional<String> optResponseBody = APIUtils.doPost(START_STOP_TASK, form);
			if (optResponseBody.isPresent() == false) {
				response.setStatus(Status.API_CONNECTION_ERROR);
				return response;
			} else {
				// take the message and forward it to the bean
				JsonObject json = new JsonParser().parse(new JsonReader(new StringReader(optResponseBody.get())))
						.getAsJsonObject();
				String strStatus = json.get("status").getAsString();
				response.setStatus(Status.valueOf(strStatus.toUpperCase()));
				response.setMessage(json.get("message").getAsString());
				return response;
			}

		} catch (Exception ex) {
			logger.error("method startStopTask error : ", ex);
			response.setStatus(Status.INTERNAL_WEB_APPLICATION_ERROR);
			response.setMessage(ex.getMessage());
			return response;
		}
	}

	public static Response<Task> getCampaignListTasks(Campaign campaign) {
		Response<Task> response = new Response<Task>();
		try {

			Form form = new Form();
			form.param("campaignId", String.valueOf(campaign.getCampaignId()));

			Optional<String> optResponseBody = APIUtils.doPost(LIST_TASK_PER_CAMPAIGN, form);

			if (optResponseBody.isPresent() == false) {
				response.setStatus(Status.API_CONNECTION_ERROR);
				return response;
			} else {

				List<Task> listTasks = new ArrayList<Task>();
				JsonObject json = new JsonParser().parse(new JsonReader(new StringReader(optResponseBody.get())))
						.getAsJsonObject();
				String strStatus = json.get("status").getAsString();
				if (strStatus.equalsIgnoreCase("success")) {

					if (json.has("data") && json.get("data").isJsonNull() == false) {
						JsonArray data = json.get("data").getAsJsonArray();
						for (JsonElement element : data) {
							JsonObject item = element.getAsJsonObject();

							Task task = new Task();
							task.setTaskId(item.get("taskId").getAsString());
							task.setCreated(APIUtils.API_DATE_FORMATER.parse(item.get("created").getAsString()));

							task.setCampaign(campaign);

							Instance instance = new Instance();
							instance.setInstanceId(
									item.get("instance").getAsJsonObject().get("instanceId").getAsLong());
							instance.setUrl(item.get("instance").getAsJsonObject().get("url").getAsString());
							task.setInstance(instance);

							task.setRepeatForever(item.get("repeatForever").getAsBoolean());

							if (item.get("cronUnit").isJsonNull() == false)
								task.setCronUnit(item.get("cronUnit").getAsString());

							if (item.get("cronValue").isJsonNull() == false)
								task.setCronValue(item.get("cronValue").getAsInt());

							if (item.get("status").isJsonNull() == false)
								task.setStatus(com.interior.model.Status.valueOf(item.get("status").getAsString()));

							task.setParameters(new LinkedHashMap<String, String>());
							if (item.has("listTaskParameters")
									&& item.get("listTaskParameters").isJsonNull() == false) {
								for (JsonElement paramElement : item.get("listTaskParameters").getAsJsonArray()) {
									task.getParameters().put(paramElement.getAsJsonObject().get("key").getAsString(),
											paramElement.getAsJsonObject().get("value").getAsString());
								}
							}

							listTasks.add(task);
						}
					}
				}
				response.setStatus(Status.valueOf(strStatus.toUpperCase()));
				response.setData(listTasks);
				return response;
			}

		} catch (Exception ex) {
			logger.error("method getCampaignListTasks error : ", ex);
			response.setStatus(Status.INTERNAL_WEB_APPLICATION_ERROR);
			response.setMessage(ex.getMessage());
			return response;
		}
	}

}
