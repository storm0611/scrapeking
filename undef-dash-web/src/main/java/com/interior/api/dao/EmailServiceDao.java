package com.interior.api.dao;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.apache.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.interior.api.model.Response;
import com.interior.api.model.Status;
import com.interior.api.utils.APIUtils;
import com.interior.model.EmailService;

public class EmailServiceDao {

	final static Logger logger = Logger.getLogger(EmailServiceDao.class);

	public static final String LIST_EMAIL_SERVICES = "/email-service/getListServices";

	public static Response<EmailService> getListEmailServices() {
		Response<EmailService> response = new Response<EmailService>();
		try {

			Optional<String> optResponseBody = APIUtils.doGet(LIST_EMAIL_SERVICES, new HashMap<String, Object>());

			if (optResponseBody.isPresent() == false) {
				response.setStatus(Status.API_CONNECTION_ERROR);
				return response;
			} else {

				List<EmailService> listEmailServices = new ArrayList<EmailService>();
				JsonObject json = new JsonParser().parse(new JsonReader(new StringReader(optResponseBody.get())))
						.getAsJsonObject();

				if (json.has("status") && json.get("status").isJsonNull() == false) {

					String status = json.get("status").getAsString();
					response.setStatus(Status.valueOf(status));

					if (status.equalsIgnoreCase("success")) {
						if (json.has("data") && json.get("data").isJsonArray()
								&& json.get("data").isJsonNull() == false) {

							JsonArray dataArray = json.get("data").getAsJsonArray();
							for (JsonElement serviceElement : dataArray) {
								JsonObject serviceObject = serviceElement.getAsJsonObject();

								EmailService emailService = new EmailService();
								if (serviceObject.has("emailServiceId")
										&& serviceObject.get("emailServiceId").isJsonNull() == false)
									emailService.setEmailServiceId(serviceObject.get("emailServiceId").getAsLong());

								if (serviceObject.has("name") && serviceObject.get("name").isJsonNull() == false)
									emailService.setName(serviceObject.get("name").getAsString());

								if (serviceObject.has("parameters")
										&& serviceObject.get("parameters").isJsonNull() == false
										&& serviceObject.get("parameters").isJsonArray()) {

									JsonArray parametersArray = serviceObject.get("parameters").getAsJsonArray();
									for (JsonElement paramElement : parametersArray)
										emailService.getListParameters().add(paramElement.getAsString());

								}

								listEmailServices.add(emailService);

							}
						}
					}
				}
				response.setData(listEmailServices);

				return response;
			}

		} catch (Exception ex) {
			logger.error("method getListEmailServices error : ", ex);
			response.setStatus(Status.INTERNAL_WEB_APPLICATION_ERROR);
			response.setMessage(ex.getMessage());
			return response;

		}
	}
}
