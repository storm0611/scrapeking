package com.interior.api.dao;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
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
import com.interior.model.CampaignEmailsCount;
import com.interior.model.GmailFailedEmail;
import com.interior.model.Subject;

public class SubjectDao {

	final static Logger logger = Logger.getLogger(SubjectDao.class);

	public static final String UPLOAD_LIST_SUBJECTS = "/subject/uploadListSubjects";
	public static final String CAMPAIGN_LIST_SUBJECTS = "/subject/campaignListSubjects";
	public static final String CAMPAIGN_FAILED_EMAILS = "/subject/campaignFailedEmails";
	public static final String CAMPAIGN_EMAILS_COUNT = "/subject/campaignEmailsCount";

	public static Response<Subject> uploadListSubjects(Campaign campaign, List<Subject> listSubjects) {
		Response<Subject> response = new Response<Subject>();
		try {

			Form form = new Form();

			form.param("subjects", String.valueOf(new Gson().toJson(listSubjects)));
			form.param("campaignId", String.valueOf(campaign.getCampaignId()));

			Optional<String> optResponseBody = APIUtils.doPost(UPLOAD_LIST_SUBJECTS, form);
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
			logger.error("method uploadListSubjects error : ", ex);
			response.setStatus(Status.INTERNAL_WEB_APPLICATION_ERROR);
			response.setMessage(ex.getMessage());
			return response;
		}
	}

	public static Response<Subject> getCampaignSubjects(long campaignId) {
		Response<Subject> response = new Response<Subject>();
		try {

			HashMap<String, Object> mapParameter = new HashMap<String, Object>();
			mapParameter.put("campaignId", campaignId);

			Optional<String> optResponseBody = APIUtils.doGet(CAMPAIGN_LIST_SUBJECTS, mapParameter);

			if (optResponseBody.isPresent() == false) {
				response.setStatus(Status.API_CONNECTION_ERROR);
				return response;
			} else {
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
								JsonObject rdObject = serviceElement.getAsJsonObject();
								
								response.setMessage(rdObject.toString());
							}
						}
					}
				}

				return response;
			}

		} catch (Exception ex) {
			logger.error("method getCampaignSubjects error : ", ex);
			response.setStatus(Status.INTERNAL_WEB_APPLICATION_ERROR);
			response.setMessage(ex.getMessage());
			return response;
		}
	}

	public static Response<GmailFailedEmail> getCampaignFailedEmails(long campaignId) {
		Response<GmailFailedEmail> response = new Response<GmailFailedEmail>();
		try {

			HashMap<String, Object> mapParameter = new HashMap<String, Object>();
			mapParameter.put("campaignId", campaignId);
			
			Optional<String> optResponseBody = APIUtils.doGet(CAMPAIGN_FAILED_EMAILS, mapParameter);
			if (optResponseBody.isPresent() == false) {
				response.setStatus(Status.API_CONNECTION_ERROR);
				return response;
			} else {

				List<GmailFailedEmail> gmailFailedEmails = new ArrayList<GmailFailedEmail>();

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

							JsonObject emailObj = sourceElement.getAsJsonObject();

							GmailFailedEmail source = new GmailFailedEmail(emailObj.get("subjectId").getAsString(),
									emailObj.get("leadEmail").getAsString(), emailObj.get("fromEmail").getAsString(), emailObj.get("reason").getAsString(),
									APIUtils.API_DATE_FORMATER.parse(emailObj.get("created").getAsString()));

							gmailFailedEmails.add(source);
						}
					}
				}
				response.setData(gmailFailedEmails);

				return response;
			}

		} catch (Exception ex) {
			logger.error("method getCampaignFailedEmails error : ", ex);
			response.setStatus(Status.INTERNAL_WEB_APPLICATION_ERROR);
			response.setMessage(ex.getMessage());
			return response;
		}
	}

	public static Response<CampaignEmailsCount> getCampaignEmailsCount(long campaignId) {
		Response<CampaignEmailsCount> response = new Response<CampaignEmailsCount>();
		try {

			HashMap<String, Object> mapParameter = new HashMap<String, Object>();
			mapParameter.put("campaignId", campaignId);
			
			Optional<String> optResponseBody = APIUtils.doGet(CAMPAIGN_EMAILS_COUNT, mapParameter);
			if (optResponseBody.isPresent() == false) {
				response.setStatus(Status.API_CONNECTION_ERROR);
				return response;
			} else {

				List<CampaignEmailsCount> campaignEmailsCounts = new ArrayList<CampaignEmailsCount>();

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

							JsonObject emailObj = sourceElement.getAsJsonObject();

							CampaignEmailsCount source = new CampaignEmailsCount(
									emailObj.get("fromEmail").getAsString(), emailObj.get("successCount").getAsLong(),
									emailObj.get("failCount").getAsLong());

							campaignEmailsCounts.add(source);
						}
					}
				}
				response.setData(campaignEmailsCounts);

				return response;
			}

		} catch (Exception ex) {
			logger.error("method getCampaignEmailsCount error : ", ex);
			response.setStatus(Status.INTERNAL_WEB_APPLICATION_ERROR);
			response.setMessage(ex.getMessage());
			return response;
		}
	}

}
