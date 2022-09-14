package com.interior.api.dao;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
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
import com.interior.model.Campaign;
import com.interior.model.Record;

public class RecordDao {
	final static Logger logger = Logger.getLogger(RecordDao.class);
	public static final String LIST_CAMPAIGN_SUBJECTS = "/record/getRecords";

	public static Response<Record> getListCampaignSubjects(Campaign campaign) {
		Response<Record> response = new Response<Record>();
		response.setData(new ArrayList<Record>());
		try {

			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("campaignId", String.valueOf(campaign.getCampaignId()));

			Optional<String> optResponseBody = APIUtils.doGet(LIST_CAMPAIGN_SUBJECTS, map);
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

								Record record = new Record();

								if (rdObject.has("recordId") && rdObject.get("recordId").isJsonNull() == false)
									record.setRecordId(rdObject.get("recordId").getAsString());

								if (rdObject.has("schema") && rdObject.get("schema").isJsonNull() == false)
									record.setSchema(rdObject.get("schema").getAsString());

								if (rdObject.has("data") && rdObject.get("data").isJsonNull() == false)
									record.setData(rdObject.get("data").getAsString());

								if (rdObject.has("insertDate") && rdObject.get("insertDate").isJsonNull() == false)
									record.setInsertDate(
											APIUtils.API_DATE_FORMATER.parse(rdObject.get("insertDate").getAsString()));

								if (rdObject.has("source") && rdObject.get("source").isJsonNull() == false)
									record.setSource(rdObject.get("source").getAsString());

								response.getData().add(record);

							}
						}
					}
				}

				return response;
			}

		} catch (Exception ex) {
			logger.error("method getListCampaignSubjects error : ", ex);
			response.setStatus(Status.INTERNAL_WEB_APPLICATION_ERROR);
			response.setMessage(ex.getMessage());
			return response;
		}
	}

	public static Response<Record> getListRecordsPagination(Campaign campaign, int page) {
		Response<Record> response = new Response<Record>();
		response.setData(new ArrayList<Record>());
		try {

			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("campaignId", String.valueOf(campaign.getCampaignId()));
			map.put("page", String.valueOf(page));

			Optional<String> optResponseBody = APIUtils.doGet(LIST_CAMPAIGN_SUBJECTS, map);
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

								Record record = new Record();

								if (rdObject.has("recordId") && rdObject.get("recordId").isJsonNull() == false)
									record.setRecordId(rdObject.get("recordId").getAsString());

								if (rdObject.has("schema") && rdObject.get("schema").isJsonNull() == false)
									record.setSchema(rdObject.get("schema").getAsString());

								if (rdObject.has("data") && rdObject.get("data").isJsonNull() == false)
									record.setData(rdObject.get("data").getAsString());

								if (rdObject.has("insertDate") && rdObject.get("insertDate").isJsonNull() == false)
									record.setInsertDate(
											APIUtils.API_DATE_FORMATER.parse(rdObject.get("insertDate").getAsString()));

								if (rdObject.has("source") && rdObject.get("source").isJsonNull() == false)
									record.setSource(rdObject.get("source").getAsString());

								response.getData().add(record);

							}
						}
					}
				}

				return response;
			}

		} catch (Exception ex) {
			logger.error("method getListRecordsPagination error : ", ex);
			response.setStatus(Status.INTERNAL_WEB_APPLICATION_ERROR);
			response.setMessage(ex.getMessage());
			return response;
		}
	}

}
