package com.interior.api.dao;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.ws.rs.core.Form;

import org.apache.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.interior.api.model.Response;
import com.interior.api.model.Status;
import com.interior.api.utils.APIUtils;
import com.interior.model.Instance;
import com.interior.model.Task;

public class InstanceDao {

	final static Logger logger = Logger.getLogger(InstanceDao.class);

	private static final String ADD_INSTANCE = "/instance/addInstance";
	private static final String DELETE_INSTANCE = "/instance/deleteInstance";
	private static final String INSTANCE_DETAILS = "/instance/instance-details";

	private static final String INSTANCE_API_PARAMETERS = "/instance/getInstanceParameters";

	public static Response<String> getInstanceAPIParameters(Instance instance) {
		Response<String> response = new Response<String>();
		try {

			Form form = new Form();
			form.param("instanceId", String.valueOf(instance.getInstanceId()));
			Optional<String> optResponseBody = APIUtils.doPost(INSTANCE_API_PARAMETERS, form);

			if (optResponseBody.isPresent() == false) {
				response.setStatus(Status.API_CONNECTION_ERROR);
				return response;
			} else {
				// parse json array of parameters ["locations","keywords"]
				List<String> parameters = new ArrayList<String>();
				try {
					response.setStatus(Status.SUCCESS);
					response.setData(parameters);

					JsonArray array = new JsonParser().parse(new JsonReader(new StringReader(optResponseBody.get())))
							.getAsJsonArray();
					for (JsonElement element : array)
						parameters.add(element.getAsString());

				} catch (Exception ex) {
					response.setStatus(Status.ERROR);
				}

				return response;
			}

		} catch (Exception ex) {
			logger.error("method getInstanceAPIParameters error : ", ex);
			response.setStatus(Status.INTERNAL_WEB_APPLICATION_ERROR);
			response.setMessage(ex.getMessage());
			return response;
		}
	}

	public static Response<Instance> updateInstanceStatus(Instance instance) {
		Response<Instance> response = new Response<Instance>();
		try {

			Form form = new Form();
			form.param("instanceId", String.valueOf(instance.getInstanceId()));

			Optional<String> optResponseBody = APIUtils.doPost(INSTANCE_DETAILS, form);

			if (optResponseBody.isPresent() == false) {
				response.setStatus(Status.API_CONNECTION_ERROR);
				return response;
			} else {
				JsonObject json = new JsonParser().parse(new JsonReader(new StringReader(optResponseBody.get())))
						.getAsJsonObject();

				String strStatus = json.get("status").getAsString();
				response.setStatus(Status.valueOf(strStatus.toUpperCase()));

				// if its success parse data
				if (strStatus.equalsIgnoreCase("error")) {
					response.setMessage(json.get("message").getAsString());
				} else {
					JsonArray dataArray = json.get("data").getAsJsonArray();
					if (dataArray.isJsonNull() == false && dataArray.size() > 0) {
						JsonObject obj = dataArray.get(0).getAsJsonObject();
						instance.setInstanceId(obj.get("instanceId").getAsLong());
						instance.setOnline(obj.get("online").getAsBoolean());
					}
				}

				response.setData(Arrays.asList(instance));
				return response;
			}

		} catch (Exception ex) {
			logger.error("method updateInstanceStatus error : ", ex);
			response.setStatus(Status.INTERNAL_WEB_APPLICATION_ERROR);
			response.setMessage(ex.getMessage());
			return response;
		}
	}

	public static Response<Instance> addInstance(Instance instance) {
		Response<Instance> response = new Response<Instance>();
		try {

			Form form = new Form();
			form.param("url", instance.getUrl());
			form.param("sourceId", String.valueOf(instance.getSource().getSourceId()));

			Optional<String> optResponseBody = APIUtils.doPost(ADD_INSTANCE, form);

			if (optResponseBody.isPresent() == false) {
				response.setStatus(Status.API_CONNECTION_ERROR);
				return response;
			} else {
				JsonObject json = new JsonParser().parse(new JsonReader(new StringReader(optResponseBody.get())))
						.getAsJsonObject();

				String strStatus = json.get("status").getAsString();
				response.setStatus(Status.valueOf(strStatus.toUpperCase()));

				if (strStatus.equalsIgnoreCase("error")) {
					response.setMessage(json.get("message").getAsString());
				} else {
					JsonArray dataArray = json.get("data").getAsJsonArray();
					if (dataArray.isJsonNull() == false && dataArray.size() > 0) {
						JsonObject obj = dataArray.get(0).getAsJsonObject();
						instance.setInstanceId(obj.get("instanceId").getAsLong());
					}
				}

				response.setData(Arrays.asList(instance));
				return response;
			}

		} catch (Exception ex) {
			logger.error("method addSource error : ", ex);
			response.setStatus(Status.INTERNAL_WEB_APPLICATION_ERROR);
			response.setMessage(ex.getMessage());
			return response;
		}
	}

	public static Response<Instance> deleteInstance(Instance instance) {
		Response<Instance> response = new Response<Instance>();
		try {

			Form form = new Form();
			form.param("instanceId", String.valueOf(instance.getInstanceId()));

			Optional<String> optResponseBody = APIUtils.doPost(DELETE_INSTANCE, form);

			if (optResponseBody.isPresent() == false) {
				response.setStatus(Status.API_CONNECTION_ERROR);
				return response;
			} else {
				JsonObject json = new JsonParser().parse(new JsonReader(new StringReader(optResponseBody.get())))
						.getAsJsonObject();
				String strStatus = json.get("status").getAsString();
				response.setStatus(Status.valueOf(strStatus.toUpperCase()));
				response.setMessage(json.get("message").getAsString());
				return response;
			}

		} catch (Exception ex) {
			logger.error("method deleteInstance error : ", ex);
			response.setStatus(Status.INTERNAL_WEB_APPLICATION_ERROR);
			response.setMessage(ex.getMessage());
			return response;
		}
	}
}
