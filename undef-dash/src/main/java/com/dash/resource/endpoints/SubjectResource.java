package com.dash.resource.endpoints;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.campaign.record.dao.RecordDao;
import com.campaign.record.model.Record;
import com.campaign.record.utils.RecordDBUtils;
import com.dash.dao.CampaignDao;
import com.dash.jwt.JwtTokenUtil;
import com.dash.model.Account;
import com.dash.model.Campaign;
import com.dash.model.CampaignEmailsCount;
import com.dash.model.GmailFailedEmail;
import com.dash.resource.model.Response;
import com.dash.resource.model.Status;
import com.dash.undefdash.ApplicationFactory;
import com.emailing.dao.GmailTrigger1DataDao;
import com.emailing.dao.GmailTrigger2DataDao;
import com.emailing.dao.GmailTrigger3DataDao;
import com.emailing.dao.SubjectDao;
import com.emailing.model.GmailTrigger1Data;
import com.emailing.model.GmailTrigger2Data;
import com.emailing.model.GmailTrigger3Data;
import com.emailing.model.Subject;
import com.emailing.utils.EmailingDBUtils;
import com.google.gson.Gson;

@RestController
@RequestMapping("/subject")
public class SubjectResource {
	private static final Logger logger = LoggerFactory.getLogger(SubjectResource.class);

	@Autowired
	private ApplicationFactory appFactory;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@PostMapping("/uploadListSubjects")
	public Response<Subject> uploadListSubjects(@RequestParam("campaignId") long campaignId,
			@RequestParam("subjects") String subjects) {
		Response<Subject> response = new Response<Subject>();
		try {

			Session session = appFactory.getSession();

			Optional<Campaign> optCampaign = CampaignDao.getCampaignbyIdAndAccount(session, campaignId,1);
			if (optCampaign.isPresent() == false) {

				response.setMessage("campaign not found");
				response.setStatus(Status.ERROR);
				return response;

			} else {

				Campaign campaign = optCampaign.get();
				Optional<Session> optRecordDBSession = RecordDBUtils.getSession(campaign);
				Optional<Session> optEmailingDBSession = EmailingDBUtils.getSession(campaign);

				if (optRecordDBSession.isPresent() && optRecordDBSession.get().isConnected()
						&& optEmailingDBSession.isPresent() && optEmailingDBSession.get().isConnected()) {

					Subject[] listSubjects = new Gson().fromJson(subjects, Subject[].class);
					for (Subject newSubject : listSubjects) {
						String uid = UUID.randomUUID().toString().replace("-", "");
						Date insertDate = new Date();

						newSubject.setSubjectId(uid);
						newSubject.setInsertDate(insertDate);
						newSubject.setSource("uploaded file " + new Date());
						SubjectDao.saveSubject(optEmailingDBSession.get(), newSubject);

						Record record = new Record();
						record.setRecordId(uid);
						record.setInsertDate(insertDate);
						record.setData("{\"name\":\"" + newSubject.getName() + "\",\"email\":\"" + newSubject.getEmail()
								+ "\"}");
						record.setSchema("B2BLead");
						record.setSource("uploaded");
						RecordDao.saveRecord(optRecordDBSession.get(), record);
					}
				}

				response.setMessage("saved successfully");
				response.setStatus(Status.SUCCESS);
				return response;
			}

		} catch (Exception ex) {
			logger.error("method uploadListSubjects error : ", ex);
			response.setStatus(Status.ERROR);
			response.setMessage("internal server error : " + ex.getMessage());
			return response;
		}
	}
	
	@GetMapping("/campaignListSubjects")
	public Response<Map<String, Long>> getCampaignListSubjects(@RequestParam("campaignId") long campaignId) {
		Response<Map<String, Long>> response = new Response<Map<String, Long>>();
		response.setData(new ArrayList<Map<String, Long>>());
		
		try {

			Session session = appFactory.getSession();

			Optional<Campaign> optCampaign = CampaignDao.getCampaignbyIdAndAccount(session, campaignId,1);
			if (optCampaign.isPresent() == false) {
				response.setMessage("campaign not found");
				response.setStatus(Status.ERROR);
				return response;
			} else {

				Optional<Session> optEmailDBSession = EmailingDBUtils.getSession(optCampaign.get());
				if (optEmailDBSession.isPresent() && optEmailDBSession.get().isConnected()) {
					response.setStatus(Status.SUCCESS);
					
					Map<String, Long> all = new HashMap<String, Long>();
					
					// get counts
					long trigge1SuccessEmailCount = SubjectDao.getTrigger1SendSuccessEmailCount(optEmailDBSession.get());
					long trigge2SuccessEmailCount = SubjectDao.getTrigger2SendSuccessEmailCount(optEmailDBSession.get());
					long trigge3SuccessEmailCount = SubjectDao.getTrigger3SendSuccessEmailCount(optEmailDBSession.get());
					
					long trigge1FailEmailCount = SubjectDao.getTrigger1FailEmailCount(optEmailDBSession.get());
					long trigge2FailEmailCount = SubjectDao.getTrigger2FailEmailCount(optEmailDBSession.get());
					long trigge3FailEmailCount = SubjectDao.getTrigger3FailEmailCount(optEmailDBSession.get());
					
					long unsubscribeCount = SubjectDao.getUnsubscribeEmailCount(optEmailDBSession.get());
					
					all.put("trigge1SuccessEmailCount", trigge1SuccessEmailCount);
					all.put("trigge2SuccessEmailCount", trigge2SuccessEmailCount);
					all.put("trigge3SuccessEmailCount", trigge3SuccessEmailCount);
					all.put("trigge1FailEmailCount", trigge1FailEmailCount);
					all.put("trigge2FailEmailCount", trigge2FailEmailCount);
					all.put("trigge3FailEmailCount", trigge3FailEmailCount);
					all.put("unsubscribeCount", unsubscribeCount);
					
					List<Map<String, Long>> m = new ArrayList<Map<String, Long>>(); 
					m.add(all);
					response.setData(m);
				}

			}

			return response;

		} catch (Exception ex) {
			logger.error("method getCampaignListSubjects error : ", ex);
			response.setStatus(Status.ERROR);
			response.setMessage("internal server error : " + ex.getMessage());
			return response;
		}
	}
	
	@GetMapping("/campaignFailedEmails")
	public Response<GmailFailedEmail> getCampaignFailedEmails(@RequestParam("campaignId") long campaignId) {
		Response<GmailFailedEmail> response = new Response<GmailFailedEmail>();
		try {
			
			Session session = appFactory.getSession();

			Optional<Campaign> optCampaign = CampaignDao.getCampaignbyIdAndAccount(session, campaignId,1);
			if (optCampaign.isPresent() == false) {
				response.setMessage("campaign not found");
				response.setStatus(Status.ERROR);
				return response;
			} else {
				
				List<GmailFailedEmail> gmailFailedEmails = new ArrayList<>();
				
				Optional<Session> optEmailDBSession = EmailingDBUtils.getSession(optCampaign.get());
				if (optEmailDBSession.isPresent() && optEmailDBSession.get().isConnected()) {
					response.setStatus(Status.SUCCESS);
					
					List<GmailTrigger1Data> trigger1Datas = GmailTrigger1DataDao.getFailedEmails(optEmailDBSession.get());
					
					if(!CollectionUtils.isEmpty(trigger1Datas) ) {
						for (GmailTrigger1Data gmailTrigger1Data : trigger1Datas) {
							GmailFailedEmail gmailFailedEmail = new GmailFailedEmail(gmailTrigger1Data.getSubjectId(), gmailTrigger1Data.getSentTo(), gmailTrigger1Data.getSentFrom(), gmailTrigger1Data.getFailedReason(), gmailTrigger1Data.getDate());
							gmailFailedEmails.add(gmailFailedEmail);
						}
					}
					
					List<GmailTrigger2Data> trigger2Datas = GmailTrigger2DataDao.getFailedEmails(optEmailDBSession.get());
					
					if(!CollectionUtils.isEmpty(trigger2Datas) ) {
						for (GmailTrigger2Data gmailTrigger2Data : trigger2Datas) {
							GmailFailedEmail gmailFailedEmail = new GmailFailedEmail(gmailTrigger2Data.getSubjectId(), gmailTrigger2Data.getSentTo(), gmailTrigger2Data.getSentFrom(), gmailTrigger2Data.getFailedReason(), gmailTrigger2Data.getDate());
							gmailFailedEmails.add(gmailFailedEmail);
						}
					}
					
					List<GmailTrigger3Data> trigger3Datas = GmailTrigger3DataDao.getFailedEmails(optEmailDBSession.get());
					
					if(!CollectionUtils.isEmpty(trigger3Datas) ) {
						for (GmailTrigger3Data gmailTrigger3Data : trigger3Datas) {
							GmailFailedEmail gmailFailedEmail = new GmailFailedEmail(gmailTrigger3Data.getSubjectId(), gmailTrigger3Data.getSentTo(), gmailTrigger3Data.getSentFrom(), gmailTrigger3Data.getFailedReason(), gmailTrigger3Data.getDate());
							gmailFailedEmails.add(gmailFailedEmail);
						}
					}
					response.setData(gmailFailedEmails);
				}
				
			}
			
			return response;
			
		} catch (Exception ex) {
			logger.error("method getCampaignFailedEmails error : ", ex);
			response.setStatus(Status.ERROR);
			response.setMessage("internal server error : " + ex.getMessage());
			return response;
		}
	}
	
	@GetMapping("/campaignEmailsCount")
	public Response<CampaignEmailsCount> getCampaignEmailsCount(@RequestParam("campaignId") long campaignId) {
		Response<CampaignEmailsCount> response = new Response<CampaignEmailsCount>();
		try {

			Session session = appFactory.getSession();

			Optional<Campaign> optCampaign = CampaignDao.getCampaignbyIdAndAccount(session, campaignId,1);
			if (optCampaign.isPresent() == false) {
				response.setMessage("campaign not found");
				response.setStatus(Status.ERROR);
				return response;
			} else {

				List<CampaignEmailsCount> campaignEmailsCounts = new ArrayList<>();
				List<CampaignEmailsCount> campaignEmailsCounts2 = new ArrayList<>();
				
				Optional<Session> optEmailDBSession = EmailingDBUtils.getSession(optCampaign.get());
				if (optEmailDBSession.isPresent() && optEmailDBSession.get().isConnected()) {
					response.setStatus(Status.SUCCESS);
					
					List<String> successEmails = GmailTrigger1DataDao.getCampaignUniqueEmails(optEmailDBSession.get());
					
					if(!CollectionUtils.isEmpty(successEmails) ) {
						for (String email : successEmails) {
							CampaignEmailsCount campaignEmailsCount = new CampaignEmailsCount(email, GmailTrigger1DataDao.getSent1TrueCountByEmail(optEmailDBSession.get(), email), GmailTrigger1DataDao.getSent1FalseCountByEmail(optEmailDBSession.get(), email));
							campaignEmailsCounts.add(campaignEmailsCount);
						}
					}
					
					List<String> successEmails2 = GmailTrigger2DataDao.getCampaignUniqueEmails(optEmailDBSession.get());
					
					if(!CollectionUtils.isEmpty(successEmails2) ) {
						for (String email : successEmails2) {
				        	CampaignEmailsCount campaignEmailsCount = new CampaignEmailsCount(email, GmailTrigger2DataDao.getSent2TrueCountByEmail(optEmailDBSession.get(), email), GmailTrigger2DataDao.getSent2FalseCountByEmail(optEmailDBSession.get(), email));
							campaignEmailsCounts.add(campaignEmailsCount);
						}
					}
					
					List<String> successEmails3 = GmailTrigger3DataDao.getCampaignUniqueEmails(optEmailDBSession.get());
					
					if(!CollectionUtils.isEmpty(successEmails3) ) {
						for (String email : successEmails3) {
							CampaignEmailsCount campaignEmailsCount = new CampaignEmailsCount(email, GmailTrigger3DataDao.getSent3TrueCountByEmail(optEmailDBSession.get(), email), GmailTrigger3DataDao.getSent3FalseCountByEmail(optEmailDBSession.get(), email));
							campaignEmailsCounts.add(campaignEmailsCount);
						}
					}
					
					Map<String, Long> successSum = campaignEmailsCounts.stream().collect(
							Collectors.groupingBy(CampaignEmailsCount::getFromEmail, Collectors.summingLong(CampaignEmailsCount::getSuccessCount)));
					
				   Map<String, Long> failedSum = campaignEmailsCounts.stream().collect(
			                Collectors.groupingBy(CampaignEmailsCount::getFromEmail, Collectors.summingLong(CampaignEmailsCount::getFailCount)));
				   
				   for (Map.Entry<String,Long> entry : successSum.entrySet()) {
					   CampaignEmailsCount campaignEmailsCount = new CampaignEmailsCount(entry.getKey(), entry.getValue(), 0);
					   campaignEmailsCounts2.add(campaignEmailsCount);
				   }
				   
				   for (Map.Entry<String,Long> entry : failedSum.entrySet()) {
					   for(CampaignEmailsCount campaignEmailsCount: campaignEmailsCounts2) {
						   if(campaignEmailsCount.getFromEmail().equals(entry.getKey())) {
							   campaignEmailsCount.setFailCount(entry.getValue());
						   }
					   }
				   }
				   
					response.setData(campaignEmailsCounts2);
				}

			}

			return response;

		} catch (Exception ex) {
			logger.error("method getCampaignEmailsCount error : ", ex);
			response.setStatus(Status.ERROR);
			response.setMessage("internal server error : " + ex.getMessage());
			return response;
		}
	}

}
