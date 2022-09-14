package com.interior.api.dao;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;

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
import com.interior.model.Campaign;
import com.interior.model.EmailServer;
import com.interior.model.EmailTemplate;
import com.interior.model.GmailCredential;
import com.interior.model.Source;

public class CampaignDao {

	final static Logger logger = Logger.getLogger(CampaignDao.class);

	public static final String ADD_CAMPAIGN = "/campaign/addCampaign";
	public static final String UPDATE_CAMPAIGN = "/campaign/editCampaign";
	public static final String DELETE_CAMPAIGN = "/campaign/deleteCampaign";
	public static final String LIST_CAMPAIGNS = "/campaign/getListCampaigns";

	public static Response<Campaign> getListCampaigns() {
		Response<Campaign> response = new Response<Campaign>();
		try {

			Optional<String> optResponseBody = APIUtils.doGet(LIST_CAMPAIGNS, new HashMap<String, Object>());
			if (optResponseBody.isPresent() == false) {
				response.setStatus(Status.API_CONNECTION_ERROR);
				return response;
			} else {

				List<Campaign> listCampaigns = new ArrayList<Campaign>();
				JsonObject json = new JsonParser().parse(new JsonReader(new StringReader(optResponseBody.get())))
						.getAsJsonObject();
				if (json.has("status")) {
					String status = json.get("status").getAsString();
					response.setStatus(Status.valueOf(status));
					// if its success parse data
					if (status.equalsIgnoreCase("success")) {
						JsonArray dataArray = json.get("data").getAsJsonArray();
						for (JsonElement campaignElement : dataArray) {

							JsonObject campaignObj = campaignElement.getAsJsonObject();
							Campaign campaign = new Campaign();
							campaign.setCampaignId(campaignObj.get("campaignId").getAsLong());
							campaign.setName(campaignObj.get("name").getAsString());
							campaign.setDescription(campaignObj.get("description").getAsString());
							campaign.setCreated(
									APIUtils.API_DATE_FORMATER.parse(campaignObj.get("created").getAsString()));
							campaign.setStatus(com.interior.model.Status
									.valueOf(campaignObj.get("status").getAsString().toLowerCase()));
							campaign.setEnableEmailing(campaignObj.get("enableEmailing").getAsBoolean());
							campaign.setTimeIntervalUntilNextEmail(
									campaignObj.get("timeIntervalUntilNextEmail").getAsInt());
							campaign.setMaxSendingPerDay(campaignObj.get("maxSendingPerDay").getAsInt());

							campaign.setListEmailServer(new ArrayList<EmailServer>());
							campaign.setEmailingDays(new ArrayList<>());

							if (campaignObj.get("blacklistedWords").isJsonNull() == false)
								campaign.setBlacklistedWords(campaignObj.get("blacklistedWords").getAsString());

							if (campaignObj.get("emailingDays").isJsonNull() == false) {
								for (String day : campaignObj.get("emailingDays").getAsString().split(",")) {
									campaign.getEmailingDays().add(day);
								}
							}

							if (campaignObj.has("listCampaignEmailServer")
									&& campaignObj.get("listCampaignEmailServer").isJsonNull() == false) {
								for (JsonElement jsonElement : campaignObj.get("listCampaignEmailServer")
										.getAsJsonArray()) {
									if (jsonElement.getAsJsonObject().has("emailServer")
											&& jsonElement.getAsJsonObject().get("emailServer").isJsonNull() == false) {
										JsonObject emailServerObj = jsonElement.getAsJsonObject().get("emailServer")
												.getAsJsonObject();

										EmailServer server = new EmailServer();
										server.setEmailServerId(emailServerObj.get("emailServerId").getAsLong());
										server.setLabel(emailServerObj.get("label").getAsString());
										campaign.getListEmailServer().add(server);
									}
								}
							}

							if (campaignObj.has("listEmail") && campaignObj.get("listEmail").isJsonNull() == false) {
								for (JsonElement jsonElement : campaignObj.get("listEmail").getAsJsonArray()) {

									if (jsonElement.getAsJsonObject().has("emailTemplate") && jsonElement
											.getAsJsonObject().get("emailTemplate").isJsonNull() == false) {

										JsonObject emailTemplate = jsonElement.getAsJsonObject().get("emailTemplate")
												.getAsJsonObject();

										EmailTemplate template = new EmailTemplate();
										template.setEmailTemplateId(emailTemplate.get("emailTemplateId").getAsLong());
										template.setLabel(emailTemplate.get("label").getAsString());

										int rank = jsonElement.getAsJsonObject().get("rank").getAsInt();
										if (rank == 1) {
											campaign.setFirstAttemptEmail(template);
										} else if (rank == 2) {
											campaign.setFollowupEmail(template);
										} else {
											campaign.setSecondAttemptEmail(template);
										}

									}
								}
							}

							listCampaigns.add(campaign);
						}
					}
				}
				response.setData(listCampaigns);

				return response;
			}

		} catch (Exception ex) {
			logger.error("method getListCampaigns error : ", ex);
			response.setStatus(Status.INTERNAL_WEB_APPLICATION_ERROR);
			response.setMessage(ex.getMessage());
			return response;
		}
	}

	public static Response<Campaign> crudCampaign(Campaign campaign, String operation) {
		Response<Campaign> response = new Response<Campaign>();
		try {

			// prepare request param
			Form form = new Form();
			form.param("name", campaign.getName());
			form.param("description", campaign.getDescription());
			form.param("enableEmailing", String.valueOf(campaign.isEnableEmailing()));
			form.param("expireDate", String.valueOf(APIUtils.API_DATE_FORMATER.format(new Date())));

			if (campaign.isEnableEmailing()) {

				form.param("blacklistedWords", campaign.getBlacklistedWords());

				String serversId = "";
				for (EmailServer server : campaign.getListEmailServer())
					serversId = server.getEmailServerId() + "," + serversId;

				serversId = serversId.trim();
				if (serversId.endsWith(","))
					serversId = serversId.substring(0, serversId.length() - 1);

				form.param("listEmailServer", serversId);
				
				String credentialsId = "";
				if(campaign.getSelectedCredentials() != null) {
					StringJoiner joiner = new StringJoiner(",");
					for (GmailCredential credential : campaign.getSelectedCredentials()) {
						joiner.add(String.valueOf(credential.getGmailCredentialId()));
					}
					credentialsId = joiner.toString();
				}
				form.param("credentialIds", credentialsId);
				

				String emailingDays = "";
				for (String day : campaign.getEmailingDays())
					emailingDays = day + "," + emailingDays;

				if (emailingDays.endsWith(","))
					emailingDays = emailingDays.substring(0, emailingDays.length() - 1);
				form.param("emailingDays", String.valueOf(emailingDays));

				if (campaign.getFirstAttemptEmail() != null)
					form.param("firstEmailTemplateId",
							String.valueOf(campaign.getFirstAttemptEmail().getEmailTemplateId()));

				if (campaign.getFollowupEmail() != null)
					form.param("secondEmailTemplateId",
							String.valueOf(campaign.getFollowupEmail().getEmailTemplateId()));

				if (campaign.getSecondAttemptEmail() != null)
					form.param("thirdEmailTemplateId",
							String.valueOf(campaign.getSecondAttemptEmail().getEmailTemplateId()));

				form.param("timeIntervalUntilNextEmail", String.valueOf(campaign.getTimeIntervalUntilNextEmail()));

			}

			String targetURL = "";
			if (operation.equals("create")) {
				targetURL = ADD_CAMPAIGN;
			} else {
				form.param("campaignId", String.valueOf(campaign.getCampaignId()));
				targetURL = UPDATE_CAMPAIGN;
			}

			Optional<String> optResponseBody = APIUtils.doPost(targetURL, form);

			if (optResponseBody.isPresent() == false) {
				response.setStatus(Status.API_CONNECTION_ERROR);
				return response;
			} else {

				JsonObject json = new JsonParser().parse(new JsonReader(new StringReader(optResponseBody.get())))
						.getAsJsonObject();
				if (json.has("status")) {
					// API is up , WE GOT IT !!
					String strStatus = json.get("status").getAsString();
					response.setStatus(Status.valueOf(strStatus.toUpperCase()));

					// if its success parse data
					if (strStatus.equalsIgnoreCase("success")) {
						JsonArray dataArray = json.get("data").getAsJsonArray();
						if (dataArray.isJsonNull() == false && dataArray.size() > 0) {
							JsonObject obj = dataArray.get(0).getAsJsonObject();
							campaign.setCampaignId(obj.get("campaignId").getAsLong());
						}
					}
				}
				response.setData(Arrays.asList(campaign));
				return response;
			}

		} catch (Exception ex) {
			logger.error("method addCampaign error : ", ex);
			response.setStatus(Status.INTERNAL_WEB_APPLICATION_ERROR);
			response.setMessage(ex.getMessage());
			return response;
		}
	}

	public static Response<Campaign> deleteCampaign(Campaign campaign) {
		Response<Campaign> response = new Response<Campaign>();
		try {

			Form form = new Form();
			form.param("campaignId", String.valueOf(campaign.getCampaignId()));

			Optional<String> optResponseBody = APIUtils.doPost(DELETE_CAMPAIGN, form);

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
			logger.error("method deleteCampaign error : ", ex);
			response.setStatus(Status.INTERNAL_WEB_APPLICATION_ERROR);
			response.setMessage(ex.getMessage());
			return response;
		}
	}

}
