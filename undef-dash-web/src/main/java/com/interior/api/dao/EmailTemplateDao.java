package com.interior.api.dao;

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
import com.interior.model.EmailTemplate;

public class EmailTemplateDao {
	final static Logger logger = Logger.getLogger(EmailTemplateDao.class);

	public static final String ADD_EMAIL_TEMPLATE = "/email-template/addEmailTemplate";
	public static final String EDIT_EMAIL_TEMPLATE = "/email-template/editEmailTemplate";

	public static final String DELETE_EMAIL_TEMPLATE = "/email-template/deleteEmailTemplate";
	public static final String LIST_EMAIL_TEMPLATE = "/email-template/getListEmailTemplates";

	public static Response<EmailTemplate> addEmailTemplate(EmailTemplate emailTemplate) {
		Response<EmailTemplate> response = new Response<EmailTemplate>();
		try {

			Form form = new Form();
			form.param("label", String.valueOf(emailTemplate.getLabel()));
			form.param("subject", String.valueOf(emailTemplate.getSubject()));
			form.param("content", String.valueOf(emailTemplate.getContent()));
			form.param("content2", String.valueOf(emailTemplate.getContent2()));
			form.param("content3", String.valueOf(emailTemplate.getContent3()));
			form.param("content4", String.valueOf(emailTemplate.getContent4()));
			form.param("content5", String.valueOf(emailTemplate.getContent5()));
			form.param("content6", String.valueOf(emailTemplate.getContent6()));

			Optional<String> optResponseBody = APIUtils.doPost(ADD_EMAIL_TEMPLATE, form);

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
							if (obj.has("emailTemplateId") && obj.get("emailTemplateId").isJsonNull() == false)
								emailTemplate.setEmailTemplateId(obj.get("emailTemplateId").getAsLong());
						}
					}
				}
				response.setData(Arrays.asList(emailTemplate));
				return response;
			}

		} catch (Exception ex) {
			logger.error("method addEmailTemplate error : ", ex);
			response.setStatus(Status.INTERNAL_WEB_APPLICATION_ERROR);
			response.setMessage(ex.getMessage());
			return response;
		}
	}

	public static Response<EmailTemplate> getListEmailTemplate() {
		Response<EmailTemplate> response = new Response<EmailTemplate>();
		try {

			Optional<String> optResponseBody = APIUtils.doGet(LIST_EMAIL_TEMPLATE, new HashMap<String, Object>());

			if (optResponseBody.isPresent() == false) {
				response.setStatus(Status.API_CONNECTION_ERROR);
				return response;
			} else {

				List<EmailTemplate> listEmailTemplate = new ArrayList<EmailTemplate>();
				JsonObject json = new JsonParser().parse(new JsonReader(new StringReader(optResponseBody.get())))
						.getAsJsonObject();

				if (json.has("status") && json.get("status").isJsonNull() == false) {

					String status = json.get("status").getAsString();
					response.setStatus(Status.valueOf(status));

					if (status.equalsIgnoreCase("success")) {
						if (json.has("data") && json.get("data").isJsonArray()
								&& json.get("data").isJsonNull() == false) {

							JsonArray dataArray = json.get("data").getAsJsonArray();
							for (JsonElement etElement : dataArray) {
								JsonObject etObject = etElement.getAsJsonObject();

								EmailTemplate emailTemplate = new EmailTemplate();
								if (etObject.has("emailTemplateId")
										&& etObject.get("emailTemplateId").isJsonNull() == false)
									emailTemplate.setEmailTemplateId(etObject.get("emailTemplateId").getAsLong());

								if (etObject.has("label") && etObject.get("label").isJsonNull() == false)
									emailTemplate.setLabel(etObject.get("label").getAsString());

								if (etObject.has("subject") && etObject.get("subject").isJsonNull() == false)
									emailTemplate.setSubject(etObject.get("subject").getAsString());

								if (etObject.has("content") && etObject.get("content").isJsonNull() == false)
									emailTemplate.setContent(etObject.get("content").getAsString());
								
								if (etObject.has("content2") && etObject.get("content2").isJsonNull() == false)
									emailTemplate.setContent2(etObject.get("content2").getAsString());
								
								if (etObject.has("content3") && etObject.get("content3").isJsonNull() == false)
									emailTemplate.setContent3(etObject.get("content3").getAsString());
								
								if (etObject.has("content4") && etObject.get("content4").isJsonNull() == false)
									emailTemplate.setContent4(etObject.get("content4").getAsString());
								
								if (etObject.has("content5") && etObject.get("content5").isJsonNull() == false)
									emailTemplate.setContent5(etObject.get("content5").getAsString());
								
								if (etObject.has("content6") && etObject.get("content6").isJsonNull() == false)
									emailTemplate.setContent6(etObject.get("content6").getAsString());

								listEmailTemplate.add(emailTemplate);
							}
						}
					}
				}
				response.setData(listEmailTemplate);
			}

			return response;

		} catch (Exception ex) {
			logger.error("method getListEmailTemplate error : ", ex);
			response.setStatus(Status.INTERNAL_WEB_APPLICATION_ERROR);
			response.setMessage(ex.getMessage());
			return response;
		}
	}

	public static Response<EmailTemplate> deleteEmailTemplate(EmailTemplate EmailTemplate) {
		Response<EmailTemplate> response = new Response<EmailTemplate>();
		try {

			Form form = new Form();
			form.param("emailTemplateId", String.valueOf(EmailTemplate.getEmailTemplateId()));

			Optional<String> optResponseBody = APIUtils.doPost(DELETE_EMAIL_TEMPLATE, form);

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
			logger.error("method deleteEmailTemplate error : ", ex);
			response.setStatus(Status.INTERNAL_WEB_APPLICATION_ERROR);
			response.setMessage(ex.getMessage());
			return response;
		}
	}

	public static Response<EmailTemplate> editEmailTemplate(EmailTemplate emailTemplate) {
		Response<EmailTemplate> response = new Response<EmailTemplate>();
		try {

			Form form = new Form();
			form.param("emailTemplateId", String.valueOf(emailTemplate.getEmailTemplateId()));
			form.param("label", String.valueOf(emailTemplate.getLabel()));
			form.param("subject", String.valueOf(emailTemplate.getSubject()));
			form.param("content", String.valueOf(emailTemplate.getContent()));
			form.param("content2", String.valueOf(emailTemplate.getContent2()));
			form.param("content3", String.valueOf(emailTemplate.getContent3()));
			form.param("content4", String.valueOf(emailTemplate.getContent4()));
			form.param("content5", String.valueOf(emailTemplate.getContent5()));
			form.param("content6", String.valueOf(emailTemplate.getContent6()));

			Optional<String> optResponseBody = APIUtils.doPost(EDIT_EMAIL_TEMPLATE, form);

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
			logger.error("method editEmailTemplate error : ", ex);
			response.setStatus(Status.INTERNAL_WEB_APPLICATION_ERROR);
			response.setMessage(ex.getMessage());
			return response;
		}
	}
}
