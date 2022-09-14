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
import com.interior.model.GmailCredential;

public class GmailCredentialDao implements Serializable {

	private static final long serialVersionUID = 1L;
	final static Logger logger = Logger.getLogger(GmailCredentialDao.class);

	public static final String ADD_GMAIL_CREDENTIAL = "/gmailCredential/addGmailCredential";
	public static final String UPDATE_GMAIL_CREDENTIAL = "/gmailCredential/updateGmailCredential";
	public static final String DELETE_GMAIL_CREDENTIAL = "/gmailCredential/deleteGmailCredential";
	public static final String LIST_GMAIL_CREDENTIALS = "/gmailCredential/getListGmailCredentials";

	public static Response<GmailCredential> addGmailCredential(GmailCredential gmailCredential) {
		Response<GmailCredential> response = new Response<GmailCredential>();
		try {

			// API PARAMETERS
			Form form = new Form();
			form.param("username", gmailCredential.getUsername());
			form.param("password", gmailCredential.getPassword());
			form.param("perDay", String.valueOf(gmailCredential.getPerDay()));
			form.param("analyticsId", gmailCredential.getAnalyticsId());
			form.param("proxyIp", gmailCredential.getProxyIp());
			form.param("proxyPort", String.valueOf(gmailCredential.getProxyPort()));

			Optional<String> optResponseBody = APIUtils.doPost(ADD_GMAIL_CREDENTIAL, form);

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
							gmailCredential.setGmailCredentialId(obj.get("gmailCredentialId").getAsLong());
						}
					}
				}
				response.setData(Arrays.asList(gmailCredential));
				response.setMessage(json.get("message").getAsString());
				return response;
			}

		} catch (Exception ex) {
			logger.error("method addGmailCredential error : ", ex);
			response.setStatus(Status.INTERNAL_WEB_APPLICATION_ERROR);
			response.setMessage(ex.getMessage());
			return response;
		}

	}

	public static Response<GmailCredential> getListGmailCredentials() {
		Response<GmailCredential> response = new Response<GmailCredential>();
		try {

			Optional<String> optResponseBody = APIUtils.doGet(LIST_GMAIL_CREDENTIALS, new HashMap<String, Object>());
			if (optResponseBody.isPresent() == false) {
				response.setStatus(Status.API_CONNECTION_ERROR);
				return response;
			} else {

				List<GmailCredential> listGmailCredentials = new ArrayList<GmailCredential>();
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
						for (JsonElement gmailCredentialElement : dataArray) {

							JsonObject gmailCredentialObj = gmailCredentialElement.getAsJsonObject();

							GmailCredential gmailCredential = new GmailCredential(gmailCredentialObj.get("gmailCredentialId").getAsLong(),
									gmailCredentialObj.get("username").getAsString(), gmailCredentialObj.get("password").getAsString(),
									gmailCredentialObj.get("perDay").getAsLong(), gmailCredentialObj.get("analyticsId").getAsString(),
									APIUtils.API_DATE_FORMATER.parse(gmailCredentialObj.get("created").getAsString()), gmailCredentialObj.get("unReadMessageCount").getAsInt(), gmailCredentialObj.get("proxyIp").getAsString(), gmailCredentialObj.get("proxyPort").getAsLong());

							listGmailCredentials.add(gmailCredential);
						}
					}
				}
				response.setData(listGmailCredentials);

				return response;
			}

		} catch (Exception ex) {
			logger.error("method getListGmailCredential error : ", ex);
			response.setStatus(Status.INTERNAL_WEB_APPLICATION_ERROR);
			response.setMessage(ex.getMessage());
			return response;
		}
	}

	public static Response<GmailCredential> deleteGmailCredential(GmailCredential gmailCredential) {
		Response<GmailCredential> response = new Response<GmailCredential>();
		try {

			HashMap<String, Object> mapParameter = new HashMap<String, Object>();
			mapParameter.put("gmailCredentialId", gmailCredential.getGmailCredentialId());

			Optional<String> optResponseBody = APIUtils.doGet(DELETE_GMAIL_CREDENTIAL, mapParameter);

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
			logger.error("method deleteGmailCredential error : ", ex);
			response.setStatus(Status.INTERNAL_WEB_APPLICATION_ERROR);
			response.setMessage(ex.getMessage());
			return response;
		}
	}

	public static Response<GmailCredential> editGmailCredential(GmailCredential gmailCredential) {
		Response<GmailCredential> response = new Response<GmailCredential>();
		try {

			// API PARAMETERS
			Form form = new Form();
			form.param("gmailCredentialId", String.valueOf(gmailCredential.getGmailCredentialId()));
			form.param("username", gmailCredential.getUsername());
			form.param("password", gmailCredential.getPassword());
			form.param("perDay", String.valueOf(gmailCredential.getPerDay()));
			form.param("analyticsId", gmailCredential.getAnalyticsId());
			form.param("proxyIp", gmailCredential.getProxyIp());
			form.param("proxyPort", String.valueOf(gmailCredential.getProxyPort()));

			Optional<String> optResponseBody = APIUtils.doPost(UPDATE_GMAIL_CREDENTIAL, form);

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
			logger.error("method editGmailCredential error : ", ex);
			response.setStatus(Status.INTERNAL_WEB_APPLICATION_ERROR);
			response.setMessage(ex.getMessage());
			return response;
		}
	}

}
