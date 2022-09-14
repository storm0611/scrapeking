package com.dash.resource.endpoints;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.campaign.record.dao.RecordDao;
import com.campaign.record.model.Record;
import com.campaign.record.utils.RecordDBUtils;
import com.dash.dao.CampaignDao;
import com.dash.dao.TaskDao;
import com.dash.model.Account;
import com.dash.model.Campaign;
import com.dash.model.Task;
import com.dash.resource.model.Response;
import com.dash.resource.model.Status;
import com.dash.undefdash.ApplicationFactory;
import com.dash.undefdash.AuthorizedSource;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

@RestController
@RequestMapping("/record")
public class RecordResource {
	private static final Logger logger = LoggerFactory.getLogger(RecordResource.class);

	@Autowired
	private ApplicationFactory appFactory;

	@PostMapping("/saveRecord")
	public Response<String> saveRecord(@RequestParam("taskId") String taskId, @RequestParam("apiKey") String apiKey,
			@RequestParam("schema") String schema, @RequestParam("data") String data) {
		Response<String> response = new Response<String>();
		try {
			logger.info("taskId : " + taskId);
			logger.info("apiKey : " + apiKey);
			logger.info("schema : " + schema);
			logger.info("data : " + data);
			logger.info("");

			AuthorizedSource source = null;
			for (AuthorizedSource scraper : appFactory.getListAuthorizedSource()) {
				if (scraper.getApiKey().equals(apiKey)) {
					source = scraper;
					break;
				}
			}

			if (source == null) {
				response.setMessage("invalid API KEY");
				response.setStatus(Status.ERROR);
				return response;
			} else {

				Session session = appFactory.getSession();

				Optional<Task> optTask = TaskDao.getTaskById(session, taskId);

				if (source.isForTest() && optTask.isPresent() == false) {
					response.setMessage("saved successfully - (provided taskId doesn't exist)");
					response.setStatus(Status.SUCCESS);
					return response;
				}

				if (optTask.isPresent() == false) {
					response.setMessage("taskId " + taskId + " not found");
					response.setStatus(Status.ERROR);
					return response;
				} else {

					Campaign campaign = optTask.get().getCampaign();
					try {
						// parse data column to construct subject object and save it into emailing
						new JsonParser().parse(new JsonReader(new StringReader(data))).getAsJsonObject();

						Record record = new Record();
						record.setRecordId(UUID.randomUUID().toString());
						record.setInsertDate(new Date());
						record.setSource(source.getApplication());
						record.setData(data);
						record.setSchema(schema);

						if (source.isForTest() == false) {
							Optional<Session> optRecordDBSession = RecordDBUtils.getSession(campaign);
							if (optRecordDBSession.isPresent() && optRecordDBSession.get().isConnected())
								RecordDao.saveRecord(optRecordDBSession.get(), record);
						}

					} catch (Exception ex) {
						response.setMessage("invalid JSON syntax error on Data parameter");
						response.setStatus(Status.ERROR);
						return response;
					}

				}
			}

			response.setMessage("saved successfully");
			response.setStatus(Status.SUCCESS);
			return response;

		} catch (Exception ex) {
			logger.error("method saveRecord error : ", ex);
			response.setStatus(Status.ERROR);
			response.setMessage("internal server error : " + ex.getMessage());
			return response;
		}

	}

	@GetMapping("/getRecords")
	public Response<Record> getCampaignListRecords(@RequestParam("campaignId") long campaignId,
			@RequestParam(required = false, defaultValue = "1970-01-01") @DateTimeFormat(pattern = "yyyy-MM-dd") Date insertDate,
			@RequestParam(required = false, defaultValue = "-1") int page) {
		Response<Record> response = new Response<Record>();
		response.setData(new ArrayList<Record>());
		try {

			Session session = appFactory.getSession();
			Account account = (Account) session.load(Account.class, (int) 1);

			Optional<Campaign> optCampaign = CampaignDao.getCampaignbyIdAndAccount(session, campaignId,
					account.getAccountId());
			if (optCampaign.isPresent() == false) {
				response.setMessage("campaign not found");
				response.setStatus(Status.ERROR);
				return response;
			} else {

				Optional<Session> optRecordDBSession = RecordDBUtils.getSession(optCampaign.get());
				if (optRecordDBSession.isPresent() && optRecordDBSession.get().isConnected()) {
					response.setStatus(Status.SUCCESS);
					Optional<List<Record>> optListRecord = Optional.empty();
					if (page != -1)
						optListRecord = RecordDao.getListRecordsWithPagination(optRecordDBSession.get(), insertDate,
								page);
					else
						optListRecord = RecordDao.getListRecordsWithNOPagination(optRecordDBSession.get(), insertDate);

					if (optListRecord.isPresent())
						response.setData(optListRecord.get());
				}

			}

			return response;

		} catch (Exception ex) {
			logger.error("method getCampaignListRecords error : ", ex);
			response.setStatus(Status.ERROR);
			response.setMessage("internal server error : " + ex.getMessage());
			return response;
		}
	}

}
