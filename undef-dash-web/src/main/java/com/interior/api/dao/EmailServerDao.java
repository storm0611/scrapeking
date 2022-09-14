package com.interior.api.dao;

import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
import com.interior.model.EmailServer;
import com.interior.model.EmailService;

public class EmailServerDao implements Serializable {
	private static final long serialVersionUID = 1L;

	final static Logger logger = Logger.getLogger(EmailServerDao.class);

	public static final String ADD_EMAIl_SERVER = "/email-server/addEmailServer";
	public static final String DELETE_EMAIL_SERVER = "/email-server/deleteEmailServer";
	public static final String LIST_EMAIL_SERVERS = "/email-server/getListEmailServers";
	public static final String EDIT_EMAIL_SERVER = "/email-server/editEmailServer";

	// parameters should respect the following structure :
	// key=value,key=value (separated by , )
	public static Response<EmailServer> addEmailServer(EmailServer emailServer) {
		Response<EmailServer> response = new Response<EmailServer>();
		try {

			Form form = new Form();
			form.param("emailServiceId", String.valueOf(emailServer.getEmailService().getEmailServiceId()));
			form.param("label", String.valueOf(emailServer.getLabel()));

			String parameters = "";
			for (Map.Entry<String, String> entry : emailServer.getMapParameters().entrySet())
				parameters = parameters + entry.getKey() + "=" + entry.getValue() + ",";
			if (parameters.endsWith(","))
				parameters = parameters.substring(0, parameters.length() - 1);

			form.param("parameters", String.valueOf(parameters));

			Optional<String> optResponseBody = APIUtils.doPost(ADD_EMAIl_SERVER, form);

			if (optResponseBody.isPresent() == false) {
				response.setStatus(Status.API_CONNECTION_ERROR);
				return response;
			} else {

				JsonObject json = new JsonParser().parse(new JsonReader(new StringReader(optResponseBody.get())))
						.getAsJsonObject();
				if (json.has("status") && json.get("status").isJsonNull() == false) {

					String strStatus = json.get("status").getAsString();
					response.setStatus(Status.valueOf(strStatus.toUpperCase()));

					if (strStatus.equalsIgnoreCase("success")) {
						JsonArray dataArray = json.get("data").getAsJsonArray();
						if (dataArray.isJsonNull() == false && dataArray.size() > 0) {
							JsonObject obj = dataArray.get(0).getAsJsonObject();
							if (obj.has("emailServerId") && obj.get("emailServerId").isJsonNull() == false)
								emailServer.setEmailServerId(obj.get("emailServerId").getAsLong());
						}
					}
				}

				response.setData(Arrays.asList(emailServer));
				return response;
			}

		} catch (Exception ex) {
			logger.error("method addEmailServer error : ", ex);
			response.setStatus(Status.INTERNAL_WEB_APPLICATION_ERROR);
			response.setMessage(ex.getMessage());
			return response;
		}
	}

	public static Response<EmailServer> editEmailServer(EmailServer emailServer) {
		Response<EmailServer> response = new Response<EmailServer>();
		try {

			Form form = new Form();
			form.param("emailServerId", String.valueOf(emailServer.getEmailServerId()));
			form.param("label", String.valueOf(emailServer.getLabel()));

			String parameters = "";
			for (Map.Entry<String, String> entry : emailServer.getMapParameters().entrySet())
				parameters = parameters + entry.getKey() + "=" + entry.getValue() + ",";
			if (parameters.endsWith(","))
				parameters = parameters.substring(0, parameters.length() - 1);

			form.param("parameters", String.valueOf(parameters));

			Optional<String> optResponseBody = APIUtils.doPost(EDIT_EMAIL_SERVER, form);

			if (optResponseBody.isPresent() == false) {
				response.setStatus(Status.API_CONNECTION_ERROR);
				return response;
			} else {
				JsonObject json = new JsonParser().parse(new JsonReader(new StringReader(optResponseBody.get())))
						.getAsJsonObject();
				response.setStatus(Status.valueOf(json.get("status").getAsString().toUpperCase()));
				return response;
			}

		} catch (Exception ex) {
			logger.error("method editEmailServer error : ", ex);
			response.setStatus(Status.INTERNAL_WEB_APPLICATION_ERROR);
			response.setMessage(ex.getMessage());
			return response;
		}
	}

	public static Response<EmailServer> getListEmailServers() {
		Response<EmailServer> response = new Response<EmailServer>();
		try {
			Optional<String> optResponseBody = APIUtils.doGet(LIST_EMAIL_SERVERS, new HashMap<String, Object>());
			if (optResponseBody.isPresent() == false) {
				response.setStatus(Status.API_CONNECTION_ERROR);
				return response;
			} else {

				List<EmailServer> listEmailServers = new ArrayList<EmailServer>();
				JsonObject json = new JsonParser().parse(new JsonReader(new StringReader(optResponseBody.get())))
						.getAsJsonObject();

				if (json.has("status")) {

					String status = json.get("status").getAsString();
					response.setStatus(Status.valueOf(status));

					if (status.equalsIgnoreCase("success")) {
						JsonArray dataArray = json.get("data").getAsJsonArray();
						for (JsonElement esElement : dataArray) {
							JsonObject serverObject = esElement.getAsJsonObject();

							EmailServer newEmailServer = new EmailServer();
							newEmailServer.setEmailService(new EmailService());
							newEmailServer.setMapParameters(new LinkedHashMap<String, String>());

							if (serverObject.has("emailServerId")
									&& serverObject.get("emailServerId").isJsonNull() == false)
								newEmailServer.setEmailServerId(serverObject.get("emailServerId").getAsLong());

							if (serverObject.has("label") && serverObject.get("label").isJsonNull() == false)
								newEmailServer.setLabel(serverObject.get("label").getAsString());

							// fill email service
							if (serverObject.has("emailService")
									&& serverObject.get("emailService").isJsonNull() == false) {
								JsonObject serviceObject = serverObject.get("emailService").getAsJsonObject();
								if (serviceObject.has("emailServiceId")
										&& serviceObject.get("emailServiceId").isJsonNull() == false) {
									newEmailServer.getEmailService()
											.setEmailServiceId(serviceObject.get("emailServiceId").getAsLong());
								}
								if (serviceObject.has("name") && serviceObject.get("name").isJsonNull() == false)
									newEmailServer.getEmailService().setName(serviceObject.get("name").getAsString());

							}

							// fill parameters
							if (serverObject.has("listServerParameters")
									&& serverObject.get("listServerParameters").isJsonNull() == false
									&& serverObject.get("listServerParameters").isJsonArray()) {
								for (JsonElement jsonElement : serverObject.get("listServerParameters")
										.getAsJsonArray()) {
									JsonObject paramObj = jsonElement.getAsJsonObject();
									newEmailServer.getMapParameters().put(paramObj.get("key").getAsString(),
											paramObj.get("value").getAsString());
								}
							}

							listEmailServers.add(newEmailServer);
						}
					}
				}
				response.setData(listEmailServers);

				return response;
			}

		} catch (Exception ex) {
			logger.error("method getListEmailServers error : ", ex);
			response.setStatus(Status.INTERNAL_WEB_APPLICATION_ERROR);
			response.setMessage(ex.getMessage());
			return response;
		}
	}

	public static Response<EmailServer> deleteEmailServer(EmailServer emailServer) {
		Response<EmailServer> response = new Response<EmailServer>();
		try {

			Form form = new Form();
			form.param("emailServerId", String.valueOf(emailServer.getEmailServerId()));

			Optional<String> optResponseBody = APIUtils.doPost(DELETE_EMAIL_SERVER, form);

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
			logger.error("method deleteEmailServer error : ", ex);
			response.setStatus(Status.INTERNAL_WEB_APPLICATION_ERROR);
			response.setMessage(ex.getMessage());
			return response;
		}
	}

}
