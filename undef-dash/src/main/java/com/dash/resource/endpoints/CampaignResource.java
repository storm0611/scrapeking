package com.dash.resource.endpoints;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.RandomStringUtils;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.campaign.record.utils.RecordDBUtils;
import com.dash.dao.AccountDao;
import com.dash.dao.CampaignDao;
import com.dash.dao.CampaignEmailServerDao;
import com.dash.dao.CampaignGmailCredentialDao;
import com.dash.dao.EmailDao;
import com.dash.dao.EmailServerDao;
import com.dash.dao.EmailTemplateDao;
import com.dash.dao.GmailCredentialDao;
import com.dash.dao.TaskDao;
import com.dash.jwt.JwtTokenUtil;
import com.dash.model.Account;
import com.dash.model.Campaign;
import com.dash.model.CampaignEmailServer;
import com.dash.model.CampaignGmailCredential;
import com.dash.model.Email;
import com.dash.model.EmailServer;
import com.dash.model.EmailTemplate;
import com.dash.model.GmailCredential;
import com.dash.model.Task;
import com.dash.resource.model.Response;
import com.dash.resource.model.Status;
import com.dash.undefdash.ApplicationFactory;
import com.emailing.utils.EmailingDBUtils;
import com.source.utils.SourceUtils;

@RestController
@RequestMapping("/campaign")
public class CampaignResource {
	private static final Logger logger = LoggerFactory.getLogger(CampaignResource.class);

	@Autowired
	private ApplicationFactory appFactory;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@GetMapping("/getListCampaigns")
	public Response<Campaign> getListCampaigns() {
		Response<Campaign> response = new Response<Campaign>();
		try {
			// int accountId = jwtTokenUtil.getClaimAccountIdFromToken(token)
			System.out.println("Inside method");
			Session session = appFactory.getSession();
			//Account account = (Account) session.load(Account.class, (int) 1);

			Optional<List<Campaign>> optListCampaigns = CampaignDao.getListCampaignsPerAccount(session,
					1);
			response.setStatus(Status.SUCCESS);

			response.setData(new ArrayList<Campaign>());
			if (optListCampaigns.isPresent())
				response.setData(optListCampaigns.get());

			System.out.println("returning method");
			return response;

		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("method getListCampaigns error : ", ex);
			response.setStatus(Status.ERROR);
			response.setMessage("internal server error : " + ex.getMessage());
			return response;
		}
	}

	@PostMapping("/addCampaign")
	public Response<Campaign> addCampaign(@RequestParam("name") String name,
			@RequestParam(name = "description", required = false, defaultValue = "") String description,
			@RequestParam("enableEmailing") boolean enableEmailing,
			@RequestParam(required = false, defaultValue = "xp9", name = "blacklistedWords") String blacklistedWords,
			@RequestParam(required = false, defaultValue = "0", name = "listEmailServer") List<Long> listEmailServer,
			@RequestParam(required = false, defaultValue = "null", name = "credentialIds") List<Long> credentialIds,
			@RequestParam(required = false, defaultValue = "-1", name = "firstEmailTemplateId") long firstEmailTemplateId,
			@RequestParam(required = false, defaultValue = "-1", name = "secondEmailTemplateId") long secondEmailTemplateId,
			@RequestParam(required = false, defaultValue = "-1", name = "thirdEmailTemplateId") long thirdEmailTemplateId,
			@RequestParam(required = false, defaultValue = "500", name = "maxSendingPerDay") int maxSendingPerDay,
			@RequestParam(name = "emailingDays", required = false, defaultValue = "") String emailingDays,
			@RequestParam(required = false, defaultValue = "0", name = "timeIntervalUntilNextEmail") int timeIntervalUntilNextEmail) {
		Response<Campaign> response = new Response<Campaign>();
		try {

			Session session = appFactory.getSession();
			Account account = (Account) session.load(Account.class, (int) 1);

			Campaign campaign = new Campaign(name, description, new Date(), account, enableEmailing,
					com.dash.model.Status.active);
			CampaignDao.saveCampaign(session, campaign);

			if (campaign.isEnableEmailing()) {
				
				campaign.setTimeIntervalUntilNextEmail(timeIntervalUntilNextEmail);
				campaign.setBlacklistedWords(blacklistedWords);
				campaign.setMaxSendingPerDay(maxSendingPerDay);
				campaign.setEmailingDays(emailingDays);
				campaign.setListEmail(new ArrayList<Email>());

				Optional<EmailTemplate> optFirstTemplate = EmailTemplateDao.getEmailTemplateByIdAndAccount(session,
						firstEmailTemplateId, account.getAccountId());
				if (optFirstTemplate.isPresent()) {

					Email firstEmail = new Email(1, campaign, optFirstTemplate.get());
					EmailDao.saveEmail(session, firstEmail);
					campaign.getListEmail().add(firstEmail);

					Optional<EmailTemplate> optSecondTemplate = EmailTemplateDao.getEmailTemplateByIdAndAccount(session,
							secondEmailTemplateId, account.getAccountId());
					if (optSecondTemplate.isPresent()) {

						Email secondEmail = new Email(2, campaign, optSecondTemplate.get());
						EmailDao.saveEmail(session, secondEmail);
						campaign.getListEmail().add(secondEmail);

						Optional<EmailTemplate> optThirdTemplate = EmailTemplateDao
								.getEmailTemplateByIdAndAccount(session, thirdEmailTemplateId, account.getAccountId());
						if (optThirdTemplate.isPresent()) {
							Email thirdEmail = new Email(3, campaign, optThirdTemplate.get());
							EmailDao.saveEmail(session, thirdEmail);
							campaign.getListEmail().add(thirdEmail);
						}

					}

				}

				for (long serverId : listEmailServer) {
					Optional<EmailServer> optEmailServer = EmailServerDao.getEmailServerById(session, serverId);

					if (optEmailServer.isPresent()
							&& optEmailServer.get().getAccount().getAccountId() == account.getAccountId()) {

						CampaignEmailServer cs = new CampaignEmailServer(campaign, optEmailServer.get(), new Date());
						CampaignEmailServerDao.saveCampaignEmailServer(session, cs);
						campaign.getListCampaignEmailServer().add(cs);
					}
				}
				
				if(!CollectionUtils.isEmpty(credentialIds))  {
					
					for (long credentialId : credentialIds) {
						Optional<GmailCredential> optGmailCredential = GmailCredentialDao.getGmailCredentialById(session, credentialId);

						if (optGmailCredential.isPresent()
								&& optGmailCredential.get().getAccount().getAccountId() == account.getAccountId()) {

							CampaignGmailCredential cgs = new CampaignGmailCredential(campaign, optGmailCredential.get(), new Date());
							CampaignGmailCredentialDao.saveGmailCredentialServer(session, cgs);
//							campaign.getListCampaignEmailServer().add(cgs);
						}
					}
				}

			}

			// will create two databases
			// 1- first database is record database
			// 2- second database is EMAILING database

			// generate database login credentials

			campaign.setUsername(RandomStringUtils.randomAlphabetic(10));
			campaign.setPassword(RandomStringUtils.randomAlphabetic(12));

			// build database access URLs
//			String emailig_connection_url = "jdbc:h2:file:" + System.getProperty("user.dir")
//			+ System.getProperty("file.separator") + campaign.getUsername() + "_emailing"
//					+ ";TRACE_LEVEL_FILE=0;AUTO_SERVER=TRUE;DB_CLOSE_ON_EXIT=FALSE";
			
			String emailig_connection_url = "jdbc:h2:tcp://localhost/" + appFactory.getDatabaseDirectoryPath()
					+ System.getProperty("file.separator") + campaign.getUsername() + "_emailing"
					+ ";TRACE_LEVEL_FILE=0;AUTO_SERVER=TRUE;DB_CLOSE_ON_EXIT=FALSE";
			
//			String records_connection_url = "jdbc:h2:file:" + System.getProperty("user.dir")
//			+ System.getProperty("file.separator") + campaign.getPassword() + "_records"
//					+ ";TRACE_LEVEL_FILE=0;AUTO_SERVER=TRUE;DB_CLOSE_ON_EXIT=FALSE";

			String records_connection_url = "jdbc:h2:tcp://localhost/" + appFactory.getDatabaseDirectoryPath()
					+ System.getProperty("file.separator") + campaign.getPassword() + "_records"
					+ ";TRACE_LEVEL_FILE=0;AUTO_SERVER=TRUE;DB_CLOSE_ON_EXIT=FALSE";

			campaign.setEmailingDBConnectionURL(emailig_connection_url);
			campaign.setRecordsDBConnectionURL(records_connection_url);

			CampaignDao.updateCampaign(session, campaign);

			EmailingDBUtils.getSession(campaign);
			RecordDBUtils.getSession(campaign);

			response.setStatus(Status.SUCCESS);
			response.setData(Arrays.asList(campaign));

			return response;

		} catch (Exception ex) {
			logger.error("method addCampaign error : ", ex);
			response.setStatus(Status.ERROR);
			response.setMessage("internal server error : " + ex.getMessage());
			return response;
		}
	}

	@PostMapping("/editCampaign")
	public Response<Campaign> editCampaign(@RequestParam("campaignId") long campaignId,
			@RequestParam("name") String name,
			@RequestParam(name = "description", required = false, defaultValue = "") String description,
			@RequestParam("enableEmailing") boolean enableEmailing,
			@RequestParam(required = false, defaultValue = "xp9", name = "blacklistedWords") String blacklistedWords,
			@RequestParam(required = false, defaultValue = "0", name = "listEmailServer") List<Long> listEmailServer,
			@RequestParam(required = false, defaultValue = "-1", name = "firstEmailTemplateId") long firstEmailTemplateId,
			@RequestParam(required = false, defaultValue = "-1", name = "secondEmailTemplateId") long secondEmailTemplateId,
			@RequestParam(required = false, defaultValue = "-1", name = "thirdEmailTemplateId") long thirdEmailTemplateId,
			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'hh:mm:ss") Date startEmailingDate,
			@RequestParam(required = false, defaultValue = "1000", name = "maxSendingPerDay") int maxSendingPerDay,
			@RequestParam(name = "emailingDays", required = false, defaultValue = "") String emailingDays,
			@RequestParam(required = false, defaultValue = "0", name = "timeIntervalUntilNextEmail") int timeIntervalUntilNextEmail) {
		// TODO Auto-generated method stub
		Response<Campaign> response = new Response<Campaign>();
		try {

			Session session = appFactory.getSession();

			Optional<Campaign> optCampaign = CampaignDao.getCampaignbyIdAndAccount(session, campaignId, 1);
			if (optCampaign.isPresent() == false) {
				response.setStatus(Status.ERROR);
				response.setMessage("campaign not found");
				return response;
			} else {

				Campaign campaign = optCampaign.get();
				campaign.setName(name);
				campaign.setDescription(description);
				campaign.setEnableEmailing(enableEmailing);

				if (campaign.isEnableEmailing()) {

					campaign.setTimeIntervalUntilNextEmail(timeIntervalUntilNextEmail);
					campaign.setBlacklistedWords(blacklistedWords);
					campaign.setMaxSendingPerDay(maxSendingPerDay);
					campaign.setEmailingDays(emailingDays);

					for (CampaignEmailServer c : campaign.getListCampaignEmailServer()) {
						CampaignEmailServerDao.deleteCampaignEmailServer(session, c);
					}

					for (long serverId : listEmailServer) {
						Optional<EmailServer> optEmailServer = EmailServerDao.getEmailServerById(session, serverId);
						if (optEmailServer.isPresent()
								&& optEmailServer.get().getAccount().getAccountId() == 1) {
							CampaignEmailServer cs = new CampaignEmailServer(campaign, optEmailServer.get(),
									new Date());
							CampaignEmailServerDao.saveCampaignEmailServer(session, cs);
							campaign.getListCampaignEmailServer().add(cs);
						}
					}

					// delete first all email template
					Optional<List<Email>> optListEmail = EmailDao.getListEmailByCampaign(session, campaignId);
					if (optListEmail.isPresent()) {
						for (Email email : optListEmail.get()) {
							EmailDao.deleteEmail(session, email);
						}
					}

					Optional<EmailTemplate> optFirstTemplate = EmailTemplateDao.getEmailTemplateByIdAndAccount(session,
							firstEmailTemplateId, 1);
					if (optFirstTemplate.isPresent()) {
						Email firstEmail = new Email(1, campaign, optFirstTemplate.get());
						EmailDao.saveEmail(session, firstEmail);
						campaign.getListEmail().add(firstEmail);
					}

					Optional<EmailTemplate> optSecondTemplate = EmailTemplateDao.getEmailTemplateByIdAndAccount(session,
							secondEmailTemplateId, 1);
					if (optSecondTemplate.isPresent()) {
						Email secondEmail = new Email(2, campaign, optSecondTemplate.get());
						EmailDao.saveEmail(session, secondEmail);
						campaign.getListEmail().add(secondEmail);
					}

					Optional<EmailTemplate> optThirdTemplate = EmailTemplateDao.getEmailTemplateByIdAndAccount(session,
							thirdEmailTemplateId, 1);
					if (optThirdTemplate.isPresent()) {
						Email thirdEmail = new Email(3, campaign, optThirdTemplate.get());
						EmailDao.saveEmail(session, thirdEmail);
						campaign.getListEmail().add(thirdEmail);
					}

				}

				CampaignDao.updateCampaign(session, campaign);

				response.setStatus(Status.SUCCESS);
				response.setData(Arrays.asList(campaign));
				return response;
			}

		} catch (Exception ex) {
			logger.error("method editCampaign error : ", ex);
			response.setStatus(Status.ERROR);
			response.setMessage("internal server error : " + ex.getMessage());
			return response;
		}
	}

	@PostMapping("/deleteCampaign")
	public Response<String> deleteCampaign(@RequestParam("campaignId") long campaignId) {
		Response<String> response = new Response<String>();
		try {

			Session session = appFactory.getSession();
			Account account = (Account) session.load(Account.class, (int) 1);

			// if the user is admin he can delete whenever he wants
			Optional<Campaign> optCampaign = Optional.empty();
			if (AccountDao.isAdmin(session, account))
				optCampaign = CampaignDao.getCampaignById(session, campaignId);
			else
				optCampaign = CampaignDao.getCampaignbyIdAndAccount(session, campaignId, account.getAccountId());

			if (optCampaign.isPresent() == false) {
				response.setStatus(Status.ERROR);
				response.setMessage("campaign not found");
				return response;
			} else {

				Campaign campaign = optCampaign.get();
				// first unregister all tasks
				Optional<List<Task>> optListTasks = TaskDao.getListTasksPerCampaign(session, campaign.getCampaignId());
				if (optListTasks.isPresent()) {
					for (Task task : optListTasks.get()) {
						// check if task is already deleted
						if (task.getStatus().equals(com.dash.model.Status.active)) {
							task.setDeleted(true);
							TaskDao.updateTask(session, task);
							SourceUtils.apiUnregister(task);
						}
					}
				}
				// set deleted and update
				campaign.setDeleted(true);
				CampaignDao.updateCampaign(session, campaign);
				response.setStatus(Status.SUCCESS);
				response.setMessage("deleted successfully");
				return response;
			}
		} catch (Exception ex) {
			logger.error("method deleteCampaign error : ", ex);
			response.setStatus(Status.ERROR);
			response.setMessage("internal server error : " + ex.getMessage());
			return response;
		}
	}

}
