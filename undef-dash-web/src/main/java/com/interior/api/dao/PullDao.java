package com.interior.api.dao;

import java.io.StringReader;
import java.util.Optional;

import javax.ws.rs.core.Form;

import org.apache.log4j.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.interior.api.utils.APIUtils;
import com.interior.model.Campaign;
import com.interior.model.Pull;

public class PullDao {
	final static Logger logger = Logger.getLogger(PullDao.class);
	public static final String GET_LAST_PULL = "/statistics/get-last-pull";

	public static boolean getLastPull(Campaign campaign) {
		try {

			campaign.setPull(new Pull());

			Form form = new Form();
			form.param("campaignId", String.valueOf(campaign.getCampaignId()));

			Optional<String> optResponseBody = APIUtils.doPost(GET_LAST_PULL, form);

			if (optResponseBody.isPresent() == false) {
//				response.setStatus(Status.API_CONNECTION_ERROR);
				return false;
			} else {

				JsonObject json = new JsonParser().parse(new JsonReader(new StringReader(optResponseBody.get())))
						.getAsJsonObject();
				if (json.has("status") && json.get("status").isJsonNull() == false) {
					String strStatus = json.get("status").getAsString();
					if (strStatus.equalsIgnoreCase("success")) {

						JsonArray dataArray = json.get("data").getAsJsonArray();
						if (dataArray.isJsonNull() == false && dataArray.size() > 0) {

							JsonObject obj = dataArray.get(0).getAsJsonObject();
							campaign.getPull().setDate(APIUtils.API_DATE_FORMATER.parse(obj.get("date").getAsString()));

							JsonArray figuresArray = obj.get("listFigures").getAsJsonArray();
							for (JsonElement element : figuresArray) {
								campaign.getPull().getMapFigures().put(
										element.getAsJsonObject().get("key").getAsString(),
										element.getAsJsonObject().get("value").getAsString());
							}
						}
					}
				}

				return true;
			}

		} catch (Exception ex) {
			logger.error("method getLastPull error : ", ex);
			return false;
		}
	}

}
