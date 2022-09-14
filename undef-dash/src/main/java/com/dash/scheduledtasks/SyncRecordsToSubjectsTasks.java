package com.dash.scheduledtasks;

import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.campaign.record.dao.RecordDao;
import com.campaign.record.model.Record;
import com.campaign.record.utils.RecordDBUtils;
import com.dash.dao.CampaignDao;
import com.dash.model.Campaign;
import com.dash.undefdash.ApplicationFactory;
import com.emailing.dao.SubjectDao;
import com.emailing.model.Subject;
import com.emailing.utils.EmailingDBUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

@Component
public class SyncRecordsToSubjectsTasks {

	private static final Logger logger = LoggerFactory.getLogger(SyncRecordsToSubjectsTasks.class);
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	@Autowired
	private ApplicationFactory appFactory;

//	@Scheduled(cron = "0 * * * * *")
	@Scheduled(cron = "0 0 */2 * * *")
	public void syncNow() {
		try {
			// for each campaign this task will establish two database connections , one for
			// records database and the second one for emailing database

			// all records with schema field equal to "B2Blead" will be copied from records
			// database into emailing database

			logger.info("sync records to subjects DB :  {}", dateFormat.format(new Date()));
			Session session = appFactory.getSession();
			// get all campaigns
			Optional<List<Campaign>> optListCampaign = CampaignDao.getListCampaignsFromAllAccounts(session);
			if (optListCampaign.isPresent()) {
				for (Campaign campaign : optListCampaign.get()) {
					try {
						// for each campaign , establish two database connections one for emailing DB
						// and the other one for record database
						Optional<Session> optEmailingDB = EmailingDBUtils.getSession(campaign);
						Optional<Session> optRecordDBSession = RecordDBUtils.getSession(campaign);

						// check if both connections are available
						if (optEmailingDB.isPresent() && optEmailingDB.get().isConnected()
								&& optRecordDBSession.isPresent() && optRecordDBSession.get().isConnected()) {

							// get records from record database with schema equal to "B2Blead" , all the
							// records marked with B2Blead can be used for emailing
							Optional<List<Record>> optListRecord = RecordDao
									.getListRecordsBySchema(optRecordDBSession.get(), "B2Blead");

							if (optListRecord.isPresent()) {

								for (Record record : optListRecord.get()) {
									// check if there's a subject with the record ID
									Optional<Subject> optSubject = SubjectDao.getSubjectById(optEmailingDB.get(),
											record.getRecordId());
									// if not
									if (optSubject.isPresent() == false) {
										// create new subject
										Subject subject = new Subject();
										// use record ID as subject id
										subject.setSubjectId(record.getRecordId());
										subject.setSource(record.getSource());
										subject.setInsertDate(record.getInsertDate());
										// parse data column to construct subject object and save it into emailing
										JsonObject jsonPayload = new JsonParser()
												.parse(new JsonReader(new StringReader(record.getData())))
												.getAsJsonObject();

										if (jsonPayload.has("name") && jsonPayload.get("name").isJsonNull() == false)
											subject.setName(jsonPayload.get("name").getAsString());

										if (jsonPayload.has("email") && jsonPayload.get("email").isJsonNull() == false)
											subject.setEmail(jsonPayload.get("email").getAsString());

										if (jsonPayload.has("phone") && jsonPayload.get("phone").isJsonNull() == false)
											subject.setPhone(jsonPayload.get("phone").getAsString());

										SubjectDao.saveSubject(optEmailingDB.get(), subject);
									}
								}
							}
						}
					} catch (Exception ex) {
						logger.warn("issue on : ", ex);
					}
				}
			}

			System.gc();

		} catch (Exception ex) {
			logger.error("method syncNow error : ", ex);
		}
	}
}
