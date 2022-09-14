package com.interior.api.dao;

import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
import com.interior.model.Source;

public class SourceDao implements Serializable {

	private static final long serialVersionUID = 1L;
	final static Logger logger = Logger.getLogger(SourceDao.class);

	public static final String ADD_SOURCE = "/source/addSource";
	public static final String DELETE_SOURCE = "/source/deleteSource";
	public static final String LIST_SOURCES = "/source/getListSources";

	public static Response<Source> addSource(Source source) {
		Response<Source> response = new Response<Source>();
		try {

			// API PARAMETERS
			Form form = new Form();
			form.param("title", source.getTitle());
			form.param("website", source.getWebsite());

			Optional<String> optResponseBody = APIUtils.doPost(ADD_SOURCE, form);

			if (optResponseBody.isPresent() == false) {
				response.setStatus(Status.API_CONNECTION_ERROR);
				response.setMessage("api connection error");
				return response;
			} else {
				// parse response body now
				// its either success or error but i think in all cases we must give back this
				// to bean layer
				JsonObject json = new JsonParser().parse(new JsonReader(new StringReader(optResponseBody.get())))
						.getAsJsonObject();
				if (json.has("status")) {
					String strStatus = json.get("status").getAsString();
					response.setStatus(Status.valueOf(strStatus.toUpperCase()));
					if (strStatus.equalsIgnoreCase("success")) {
						JsonArray dataArray = json.get("data").getAsJsonArray();
						if (dataArray.isJsonNull() == false && dataArray.size() > 0) {
							JsonObject obj = dataArray.get(0).getAsJsonObject();
							source.setSourceId(obj.get("sourceId").getAsLong());
						}
					}
				}
				response.setData(Arrays.asList(source));
				return response;
			}

		} catch (Exception ex) {
			logger.error("method addSource error : ", ex);
			response.setStatus(Status.INTERNAL_WEB_APPLICATION_ERROR);
			response.setMessage(ex.getMessage());
			return response;
		}

	}

	public static Response<Source> getListSources() {
		Response<Source> response = new Response<Source>();
		try {

			Optional<String> optResponseBody = APIUtils.doGet(LIST_SOURCES, new HashMap<String, Object>());
			if (optResponseBody.isPresent() == false) {
				response.setStatus(Status.API_CONNECTION_ERROR);
				return response;
			} else {

				List<Source> listSources = new ArrayList<Source>();
				// parse response body now
				// its either success or error but i think in all cases we must give back this
				// to bean layer

				JsonObject json = new JsonParser().parse(new JsonReader(new StringReader(optResponseBody.get())))
						.getAsJsonObject();

				if (json.has("status")) {
					// API is up , WE GOT IT !!
					String status = json.get("status").getAsString();
					response.setStatus(Status.valueOf(status));
					// if its success parse data
					if (status.equalsIgnoreCase("success")) {
						JsonArray dataArray = json.get("data").getAsJsonArray();
						for (JsonElement sourceElement : dataArray) {

							JsonObject sourceObj = sourceElement.getAsJsonObject();

							Source source = new Source(sourceObj.get("sourceId").getAsLong(),
									sourceObj.get("title").getAsString(), sourceObj.get("website").getAsString(),
									APIUtils.API_DATE_FORMATER.parse(sourceObj.get("created").getAsString()));

							JsonArray instancesArray = sourceObj.get("listInstances").getAsJsonArray();
							source.setListInstance(new ArrayList<Instance>());
							for (JsonElement instanceElement : instancesArray) {
								JsonObject instanceObj = instanceElement.getAsJsonObject();
								Instance instance = new Instance(instanceObj.get("instanceId").getAsLong(),
										APIUtils.API_DATE_FORMATER.parse(sourceObj.get("created").getAsString()),
										instanceObj.get("url").getAsString(), instanceObj.get("online").getAsBoolean(),
										source);
								source.getListInstance().add(instance);
							}

							listSources.add(source);
						}
					}
				}
				response.setData(listSources);

				return response;
			}

		} catch (Exception ex) {
			logger.error("method getListSources error : ", ex);
			response.setStatus(Status.INTERNAL_WEB_APPLICATION_ERROR);
			response.setMessage(ex.getMessage());
			return response;
		}
	}

	public static Response<Source> deleteSource(Source source) {
		Response<Source> response = new Response<Source>();
		try {

			HashMap<String, Object> mapParameter = new HashMap<String, Object>();
			mapParameter.put("sourceId", source.getSourceId());

			Optional<String> optResponseBody = APIUtils.doGet(DELETE_SOURCE, mapParameter);

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
			logger.error("method deleteSource error : ", ex);
			response.setStatus(Status.INTERNAL_WEB_APPLICATION_ERROR);
			response.setMessage(ex.getMessage());
			return response;
		}
	}

}
