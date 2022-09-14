package com.dash.scheduledtasks;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.mail.Folder;
import javax.mail.Store;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.dash.dao.CampaignDao;
import com.dash.dao.CampaignGmailCredentialDao;
import com.dash.dao.EmailDao;
import com.dash.dao.EmailServerDao;
import com.dash.dao.GmailCredentialDao;
import com.dash.model.Account;
import com.dash.model.Campaign;
import com.dash.model.CampaignEmailServer;
import com.dash.model.CampaignGmailCredential;
import com.dash.model.Email;
import com.dash.model.EmailServer;
import com.dash.model.GmailCredential;
import com.dash.undefdash.ApplicationFactory;
import com.emailing.dao.EventDao;
import com.emailing.dao.GmailTrigger1DataDao;
import com.emailing.dao.GmailTrigger2DataDao;
import com.emailing.dao.GmailTrigger3DataDao;
import com.emailing.dao.MessageDao;
import com.emailing.dao.SubjectDao;
import com.emailing.model.Event;
import com.emailing.model.EventType;
import com.emailing.model.GmailTrigger1Data;
import com.emailing.model.GmailTrigger2Data;
import com.emailing.model.GmailTrigger3Data;
import com.emailing.model.Message;
import com.emailing.model.Subject;
import com.emailing.providers.AbstractEmailService;
import com.emailing.providers.Sparkpost;
import com.emailing.providers.model.ProxyEvent;
import com.emailing.providers.model.ProxyMessage;
import com.emailing.utils.EmailingDBUtils;

@Component
public class EmailingTask {

	private static final Logger logger = LoggerFactory.getLogger(EmailingTask.class);
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private ApplicationFactory appFactory;

	// JUST FOR TESTS
//	@Scheduled(cron = "0 * * * * *")
//	@Scheduled(cron = "0 0 9 * * *")
//	@Scheduled(cron = "0 0 13 * * *")
//	@Scheduled(cron = "0 0 18 * * *")
	public void start() {
		try {
			logger.info("Emailing at :  {}", dateFormat.format(new Date()));
			activateNewSubjects();
			manageActiveSubjects();
		} catch (Exception ex) {
			logger.error("method start error : ", ex);
		}
	}

	@Scheduled(cron = "0 0 */2 * * *")
	public void consumeEvents() {
		try {
			logger.info("     consume events at :  {}", dateFormat.format(new Date()));

			Session session = appFactory.getSession();
			Optional<List<EmailServer>> optListEmailServers = EmailServerDao.getListAllEmailServers(session);

			// for each created email server
			if (optListEmailServers.isPresent()) {
				for (EmailServer emailServer : optListEmailServers.get()) {
					logger.info("          server : " + emailServer.toString());

					/// only SPARKPOST implemented
					AbstractEmailService serviceToUse = null;
					if (emailServer.getEmailService().getName().equalsIgnoreCase("sparkpost"))
						serviceToUse = applicationContext.getBean(Sparkpost.class);

					// all providers (SPARKPOST , MAILGUN , AMAZON SES ...)
					// must map events to a list of ProxyEvent.java

					// use email server provider class to get events
					Optional<List<ProxyEvent>> optListProxyEvent = serviceToUse.fetchServerEvents(emailServer);
					if (optListProxyEvent.isPresent()) {
						// fore each PROXY event
						for (ProxyEvent proxyEvent : optListProxyEvent.get()) {
							try {
								// get campaign object from PROXY event
								Optional<Campaign> optCampaign = CampaignDao.getCampaignById(session,
										proxyEvent.getCampaignId());
								if (optCampaign.isPresent()) {
									// get EMAILING database session
									Optional<Session> optEmailingDBSession = EmailingDBUtils
											.getSession(optCampaign.get());

									if (optEmailingDBSession.isPresent()) {
//										 check if event is already SYNCED
										boolean eventAlreadyExist = EventDao.checkIfExist(optEmailingDBSession.get(),
												proxyEvent.getEventId());
										if (eventAlreadyExist == false) {
											// get message object associated with event
											Optional<Message> optMessage = MessageDao.getMessageById(
													optEmailingDBSession.get(), proxyEvent.getMessageId());
											if (optMessage.isPresent()) {
												// save new eventF
												Event event = new Event(proxyEvent.getEventId(),
														proxyEvent.getEventType(), proxyEvent.getEventDate(),
														optMessage.get());
												EventDao.saveEvent(optEmailingDBSession.get(), event);
											}
										}
									}
								}
							} catch (Exception ex) {
								logger.warn("warn : ", ex);
							}
						}
					}

				}
			}

			System.gc();
		} catch (Exception ex) {
			logger.error("method consumeEvents error : ", ex);
		}
	}

	public void activateNewSubjects() {
		try {

			logger.info("     activate new subjects at :  {}", dateFormat.format(new Date()));
			Session session = appFactory.getSession();
			Optional<List<Campaign>> optListCampaigns = CampaignDao.getListCampaignsFromAllAccounts(session);
			// get all campaigns
			if (optListCampaigns.isPresent()) {
				for (Campaign campaign : optListCampaigns.get()) {
					try {

						SimpleDateFormat formt = new SimpleDateFormat("EEEE");
						// for each campaign check if EMAILING is enabled
						if (campaign.isEnableEmailing() && campaign.getListCampaignEmailServer().size() > 0
								&& (campaign.getEmailingDays().isEmpty() || campaign.getEmailingDays().toLowerCase()
										.contains(formt.format(new Date()).toLowerCase()))) {

							logger.info("name = " + campaign.getName());

							// get first email template
							Optional<Email> optFirstEmail = EmailDao.getEmailByRankAndCampaign(session,
									campaign.getCampaignId(), 1);
							if (optFirstEmail.isPresent()) {

								// open EMAILING database
								Optional<Session> optEmailingDBSession = EmailingDBUtils.getSession(campaign);

								if (optEmailingDBSession.isPresent()) {
									// get first top 1000 new subjects
									Optional<List<Subject>> optListSubjects = SubjectDao
											.getTopXSubjectsPerCampaign(optEmailingDBSession.get(), 1000);

									if (optListSubjects.isPresent()) {
										int rotateIndex = -1;
										logger.info(" subjects count (" + optListSubjects.get().size() + ")");
										for (Subject subject : optListSubjects.get()) {
											try {
												// check if subject match a blacklisted word
												subject.setBlacklistedWord(false);
												for (String word : campaign.getBlacklistedWords().split(",")) {
													if (subject.getEmail().toLowerCase().contains(word.toLowerCase())) {
														subject.setBlacklistedWord(true);
														break;
													}
												}

												if (subject.isBlacklistedWord()) {
													SubjectDao.updateSubject(optEmailingDBSession.get(), subject);
												} else {

													// if its not blacklisted
													rotateIndex += 1;
													if (rotateIndex >= campaign.getListCampaignEmailServer().size())
														rotateIndex = 0;

													// get email server to use
													AbstractEmailService serviceToUse = null;
													EmailServer emailServer = campaign.getListCampaignEmailServer()
															.get(rotateIndex).getEmailServer();

													if (emailServer.getEmailService().getName().equals("sparkpost"))
														serviceToUse = applicationContext.getBean(Sparkpost.class);
													// send new first email
													Optional<ProxyMessage> optProxyMessage = serviceToUse.sendEmail(
															emailServer, campaign, subject, optFirstEmail.get());

													if (optProxyMessage.isPresent()) {
														// add email object to EMAILING database
														Message message = new Message(
																optProxyMessage.get().getMessageId(), new Date(), 1,
																subject);
														message.setUnsubscribeToken(
																optProxyMessage.get().getUnsubscribeToken());
														MessageDao.saveMessage(optEmailingDBSession.get(), message);
													}

												}

											} catch (Exception ex) {
												logger.warn("issue : " + ex.getMessage());
											}
										}
									}

								}
							}

						}

					} catch (Exception ex) {
						logger.warn("issue : " + ex.getMessage());
					}
				}
			}

			System.gc();
		} catch (Exception ex) {
			logger.error("method activateNewSubjects error : ", ex);
		}
	}

	public void manageActiveSubjects() {
		try {
			logger.info("     manage active subjects at :  {}", dateFormat.format(new Date()));

			Session session = appFactory.getSession();
			Optional<List<Campaign>> optListCampaigns = CampaignDao.getListCampaignsFromAllAccounts(session);
			// get all campaigns
			if (optListCampaigns.isPresent()) {
				for (Campaign campaign : optListCampaigns.get()) {
					try {

						SimpleDateFormat formt = new SimpleDateFormat("EEEE");

						if (campaign.isEnableEmailing() && campaign.getListCampaignEmailServer().size() > 0
								&& (campaign.getEmailingDays().isEmpty() || campaign.getEmailingDays().toLowerCase()
										.contains(formt.format(new Date()).toLowerCase()))) {

							Optional<Session> optEmailingDBSession = EmailingDBUtils.getSession(campaign);
							if (optEmailingDBSession.isPresent()) {

								Optional<List<Subject>> optListSubjects = SubjectDao
										.getTop1000ActiveSubject(optEmailingDBSession.get());

								if (optListSubjects.isPresent()) {
									int rotateIndex = -1;

									for (Subject subject : optListSubjects.get()) {

										rotateIndex += 1;
										if (rotateIndex >= campaign.getListCampaignEmailServer().size())
											rotateIndex = 0;

										AbstractEmailService serviceToUse = null;
										EmailServer emailServer = campaign.getListCampaignEmailServer().get(rotateIndex)
												.getEmailServer();

										/// ADD OTHER SERVICE INSTANTICATION LATER
										if (emailServer.getEmailService().getName().equals("sparkpost"))
											serviceToUse = applicationContext.getBean(Sparkpost.class);

										Optional<Message> optLastMessage = MessageDao
												.getLastMessage(optEmailingDBSession.get(), subject);

										if (optLastMessage.isPresent()) {

											Message lastMessage = optLastMessage.get();
											Optional<Email> optNextEmail = EmailDao.getEmailByRankAndCampaign(session,
													campaign.getCampaignId(), lastMessage.getRank() + 1);

											if (optNextEmail.isPresent() == false) {
												// no more followup email , close the subject by adding close event to
												// last sent message
												Event event = new Event(UUID.randomUUID().toString().replace("-", ""),
														EventType.close, new Date(), lastMessage);
												EventDao.saveEvent(optEmailingDBSession.get(), event);
											} else {

												Optional<Event> optValuableEvent = EventDao.getMessageValuableEvent(
														optEmailingDBSession.get(), lastMessage);

												if (optValuableEvent.isPresent()) {

													Event event = optValuableEvent.get();
													int daysDiff = Math.abs((int) TimeUnit.DAYS.convert(
															new Date().getTime() - event.getDate().getTime(),
															TimeUnit.MILLISECONDS));

													boolean sendNextEmail = false;
													if (daysDiff > campaign.getTimeIntervalUntilNextEmail()
															&& (event.getType().equals(EventType.open)
																	|| event.getType().equals(EventType.click))) {
														sendNextEmail = true;
													} else if (daysDiff > 3
															&& event.getType().equals(EventType.delivered)) {
														sendNextEmail = true;
													}

													if (sendNextEmail) {
														if (optNextEmail.isPresent()) {
															Optional<ProxyMessage> optProxyMessage = serviceToUse
																	.sendEmail(emailServer, campaign, subject,
																			optNextEmail.get());
															if (optProxyMessage.isPresent()) {

																Message message = new Message(
																		optProxyMessage.get().getMessageId(),
																		new Date(), optNextEmail.get().getRank(),
																		subject);
																MessageDao.saveMessage(optEmailingDBSession.get(),
																		message);
															}
														}
													}

												}

											}

										}

									}

								}

							}

						}

					} catch (Exception ex) {
						logger.warn("error : ", ex);
					}
				}
			}
			System.gc();
		} catch (Exception ex) {
			logger.error("method manageActiveSubjects error : ", ex);
		}
	}
	
	@Scheduled(cron = "0 0 */3 * * *")
	private void manageCampaignGmailCredentialSubjects() {
		try {
			if(!getCureentTimeBetween9amTo6pm())
				return;
			
			logger.info("     manage campaign gmail credential subjects at :  {}", dateFormat.format(new Date()));

			Session session = appFactory.getSession();
			Optional<List<Campaign>> optListCampaigns = CampaignDao.getListCampaignsFromAllAccounts(session);
			// get all campaigns
			if (optListCampaigns.isPresent()) {
				for (Campaign campaign : optListCampaigns.get()) {
					try {

						SimpleDateFormat formt = new SimpleDateFormat("EEEE");
						
						//Check campaign assign gmail server not if campaign contains gmail server then continue
						boolean gmailServer = false;
						EmailServer emailServer = null;
						for (CampaignEmailServer campaignEmailServer : campaign.getListCampaignEmailServer()) {
							if(campaignEmailServer.getEmailServer().getLabel().toLowerCase().equals("gmail")) {
								gmailServer = true;
								emailServer = campaignEmailServer.getEmailServer();
							}
						}
						
						if(!gmailServer)
							continue;
						
						Optional<List<CampaignGmailCredential>> optListCampaignGmailCredentials = CampaignGmailCredentialDao.getCampaignGmailCredentialsByCampaignId(session, campaign.getCampaignId());
						
						long perDayTotalCount = CampaignGmailCredentialDao.getPerDayTotalCountByCampaign(session, campaign.getCampaignId());
						
						if (campaign.isEnableEmailing() && campaign.getListCampaignEmailServer().size() > 0
								&& (campaign.getEmailingDays().isEmpty() || campaign.getEmailingDays().toLowerCase()
										.contains(formt.format(new Date()).toLowerCase())) && gmailServer && optListCampaignGmailCredentials.isPresent()) {

							Optional<Session> optEmailingDBSession = EmailingDBUtils.getSession(campaign);
							if (optEmailingDBSession.isPresent()) {
								
								// get subjects
								List<Subject> optListSubjects = SubjectDao.getSubjectsPerCampaignAndPerDayLimit(optEmailingDBSession.get(), perDayTotalCount);

								if (!CollectionUtils.isEmpty(optListSubjects)) {
									
									// get trigger1 email template
									Optional<Email> optFirstEmail = EmailDao.getEmailByRankAndCampaign(session, campaign.getCampaignId(), 1);
									
									if(!optFirstEmail.isPresent())
										continue;
									
									//Get trigger1 email template
									Email emailTemplate = optFirstEmail.get();

									/// ADD OTHER SERVICE INSTANTICATION LATER
									AbstractEmailService serviceToUse = applicationContext.getBean(Sparkpost.class);
									
									//aa content list ma bdha content add krvana
									List<String> contents = new ArrayList<>(); 
									contents.add(emailTemplate.getEmailTemplate().getContent());
									contents.add(emailTemplate.getEmailTemplate().getContent2());
									contents.add(emailTemplate.getEmailTemplate().getContent3());
									contents.add(emailTemplate.getEmailTemplate().getContent4());
									contents.add(emailTemplate.getEmailTemplate().getContent5());
									contents.add(emailTemplate.getEmailTemplate().getContent6());
									
									List<CampaignGmailCredential> campaignGmailCredentials = optListCampaignGmailCredentials.get();
									
									int templateContentIndex = 0;
									int fromEmailIndex = 0;
									int sentEmailCount = 0;
									
									//last email send content index
									templateContentIndex = getTemplateIndex(optEmailingDBSession);
									//last email send from email index
									fromEmailIndex = getFromEmailIndex(optEmailingDBSession, campaignGmailCredentials);
									
									for (Subject subject : optListSubjects) {
										
										// check if subject match a blacklisted word
										subject.setBlacklistedWord(false);
										for (String word : campaign.getBlacklistedWords().split(",")) {
											if (subject.getEmail().toLowerCase().contains(word.toLowerCase())) {
												subject.setBlacklistedWord(true);
												break;
											}
										}

										if (subject.isBlacklistedWord()) {
											SubjectDao.updateSubject(optEmailingDBSession.get(), subject);
										} else {
										
											String content = contents.get(templateContentIndex);
											templateContentIndex++;
											int sentEmailIndex = templateContentIndex;
											if(templateContentIndex > 5)
												templateContentIndex = 0;
											
											List<CampaignGmailCredential> result = null;
											while (1 == 1) {
										        result = campaignGmailCredentials.stream()
										                .filter(line -> (line.getGmailCredential().getPerDayPendingCount() == null || line.getGmailCredential().getPerDayPendingCount() != 0))
										                .collect(Collectors.toList()); 
										        
										        if(CollectionUtils.isEmpty(result))
										        	break;
												
												if(campaignGmailCredentials.get(fromEmailIndex).getGmailCredential().getPerDayPendingCount() != null && campaignGmailCredentials.get(fromEmailIndex).getGmailCredential().getPerDayPendingCount() == 0) {
													fromEmailIndex++;
													if(fromEmailIndex > campaignGmailCredentials.size()-1) {
														fromEmailIndex = 0;
													}
												} else {
													break;
												}
											}
											
											if(CollectionUtils.isEmpty(result))
									        	break;
											
											CampaignGmailCredential campaignGmailCredential = campaignGmailCredentials.get(fromEmailIndex);
											
											//Send email
											Optional<GmailTrigger1Data> optTrackGmailMail = serviceToUse
													.sendEmailFromGmailServer(campaignGmailCredential, campaign, subject,
															optFirstEmail.get(), content, sentEmailIndex);
											
											if (optTrackGmailMail.isPresent()) {
												
												sentEmailCount++;
												if(campaign.getTimeIntervalUntilNextEmail() == sentEmailCount) {
													//Sleep for 5 miniutes after campaign time interval email
													Thread.sleep(300000);
													sentEmailCount = 0;
												}
	
												//Save sent1 email
												GmailTrigger1DataDao.saveTrackGmailMail(optEmailingDBSession.get(),
														optTrackGmailMail.get());
												
												//Set sent1 succeess or fail in subject table
												Subject subject1 = SubjectDao.getBySubjectId(optEmailingDBSession.get(), optTrackGmailMail.get().getSubjectId());
												subject1.setSent1(optTrackGmailMail.get().isSent1());
												subject1.setUnsubscribeToken(optTrackGmailMail.get().getUnsubscribeToken());
												SubjectDao.updateSubject(optEmailingDBSession.get(), subject1);
											}
											
											
											if(campaignGmailCredential.getGmailCredential().getPerDayPendingCount() == null) {
												
												Long diffDays = EmailingDBUtils.getDiffBetTimeInDays(campaignGmailCredential.getGmailCredential().getCreated());
												if (diffDays < 1) {
													campaignGmailCredential.getGmailCredential().setPerDayPendingCount(0L);
												} else {
													Long doubleDays = diffDays + diffDays;
													if(doubleDays < campaignGmailCredential.getGmailCredential().getPerDay())
														campaignGmailCredential.getGmailCredential().setPerDayPendingCount(doubleDays-1);
													else
														campaignGmailCredential.getGmailCredential().setPerDayPendingCount(campaignGmailCredential.getGmailCredential().getPerDay()-1);
												}
											} else {
												campaignGmailCredential.getGmailCredential().setPerDayPendingCount(campaignGmailCredential.getGmailCredential().getPerDayPendingCount()-1);
											}
											
											fromEmailIndex++;
											if(fromEmailIndex > campaignGmailCredentials.size()-1) {
												fromEmailIndex = 0;
											}
										}
									}

								}

							}

						}

					} catch (Exception ex) {
						logger.warn("error : ", ex);
					}
				}
			}
			System.gc();
		} catch (Exception ex) {
			logger.error("method manageCampaignGmailCredentialSubjects error : ", ex);
		}
	}
	 
	public boolean getCureentTimeBetween9amTo6pm() {
		// Getting the current current time
        Date date = new Date();
        
        // set format in 12 hours
        SimpleDateFormat formatTime = new SimpleDateFormat("hh aa");
  
        String time = formatTime.format(date);
        
        if(time.equalsIgnoreCase("09 AM") || time.equalsIgnoreCase("10 AM") || time.equalsIgnoreCase("11 AM")
        	|| time.equalsIgnoreCase("12 PM") || time.equalsIgnoreCase("01 PM") || time.equalsIgnoreCase("02 PM")
        		|| time.equalsIgnoreCase("03 PM") || time.equalsIgnoreCase("04 PM") || time.equalsIgnoreCase("05 PM") || time.equalsIgnoreCase("06 PM")) {
        	return true;
        }
		return false;
	}
	
	private int getFromEmailIndex(Optional<Session> optEmailingDBSession, List<CampaignGmailCredential> campaignGmailCredentials) {
		
		//Get last sent email
		GmailTrigger1Data trackGmailMail1 = GmailTrigger1DataDao.getLastSubject(optEmailingDBSession.get());
		
		if(trackGmailMail1 == null) {
			return 0;
		} else {
			int fromEmailIndex = 0;
			for (int i = 0; i < campaignGmailCredentials.size(); i++) {
				
				if(trackGmailMail1.getSentFrom().equals(campaignGmailCredentials.get(i).getGmailCredential().getUsername())) {
					fromEmailIndex = i;
					break;
				}
			}
			fromEmailIndex++;
			if(fromEmailIndex > campaignGmailCredentials.size()-1) 
				return 0;
			return fromEmailIndex;
		}
	}

	private int getTemplateIndex(Optional<Session> optEmailingDBSession) {
		
		//Get last sent email
		GmailTrigger1Data trackGmailMail1 = GmailTrigger1DataDao.getLastSubject(optEmailingDBSession.get());
		
		if(trackGmailMail1 == null) {
			return 0;
		} else {
			if(trackGmailMail1.getContentNumber() > 5)
				return 0;
			return trackGmailMail1.getContentNumber();
		}
	}

	// JUST FOR TESTS
//	@Scheduled(cron = "0 0 */3 * * *")
	public void saveGmailCredentialMessageUnreadCount() {
		try {
			Session session = appFactory.getSession();

			Optional<List<GmailCredential>> optListGmailCredentials = GmailCredentialDao.getListGmailCredentialsPerAccount(session, 1);

			if (optListGmailCredentials.isPresent() && !CollectionUtils.isEmpty(optListGmailCredentials.get())) {
				
				for (GmailCredential gmailCredential : optListGmailCredentials.get()) {
					try {
						
						Properties props = new Properties();
					    props.put("mail.smtp.ssl.protocols", "TLSv1.2");
					    props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
						props.setProperty("mail.store.protocol", "imaps");
						javax.mail.Session ssession = javax.mail.Session.getDefaultInstance(props, null);
				
						Store store = ssession.getStore("imaps");
						store.connect("smtp.gmail.com", gmailCredential.getUsername(), gmailCredential.getPassword());
				
						// create the folder object and open it
						Folder folder = store.getFolder("INBOX");
						folder.open(Folder.READ_WRITE);
					      
						// now set unread count
						gmailCredential.setUnReadMessageCount(folder.getUnreadMessageCount());
						GmailCredentialDao.updateGmailCredential(session, gmailCredential);
						
					    // close the store and folder objects
						folder.close(false);
						store.close();
					} catch (Exception e) {
						logger.error("method saveGmailCredentialMessageUnreadCount error email id " + gmailCredential.getUsername() + ": ", e);
					}
				}
			}
			
			System.gc();
		} catch (Exception ex) {
			logger.error("method saveGmailCredentialMessageUnreadCount error : ", ex);
		}
	}

	@Scheduled(cron = "0 0 */3 * * *")
	public void triger2() {
		try {
			if(!getCureentTimeBetween9amTo6pm())
				return;
			
			logger.info("     manage trigger 2 at :  {}", dateFormat.format(new Date()));
			
			Session session = appFactory.getSession();
			Optional<List<Campaign>> optListCampaigns = CampaignDao.getListCampaignsFromAllAccounts(session);
			// get all campaigns
			if (optListCampaigns.isPresent()) {
				for (Campaign campaign : optListCampaigns.get()) {
					try {
						
						SimpleDateFormat formt = new SimpleDateFormat("EEEE");
						
						//Check campaign assign gmail server not if campaign contains gmail server then continue
						boolean gmailServer = false;
						EmailServer emailServer = null;
						for (CampaignEmailServer campaignEmailServer : campaign.getListCampaignEmailServer()) {
							if(campaignEmailServer.getEmailServer().getLabel().toLowerCase().equals("gmail")) {
								gmailServer = true;
								emailServer = campaignEmailServer.getEmailServer();
							}
						}
						
						if(!gmailServer)
							continue;
						
						Optional<List<CampaignGmailCredential>> optListCampaignGmailCredentials = CampaignGmailCredentialDao.getCampaignGmailCredentialsByCampaignId(session, campaign.getCampaignId());
						
						long perDayTotalCount = CampaignGmailCredentialDao.getPerDayTotalCountByCampaign(session, campaign.getCampaignId());
						
						if (campaign.isEnableEmailing() && campaign.getListCampaignEmailServer().size() > 0
								&& (campaign.getEmailingDays().isEmpty() || campaign.getEmailingDays().toLowerCase()
										.contains(formt.format(new Date()).toLowerCase())) && gmailServer && optListCampaignGmailCredentials.isPresent()) {
							
							Optional<Session> optEmailingDBSession = EmailingDBUtils.getSession(campaign);
							if (optEmailingDBSession.isPresent()) {
								
								// get subjects
								List<Subject> optListSubjects = SubjectDao.getSubjectsForTrigger2(optEmailingDBSession.get(), perDayTotalCount);
								
								if (!CollectionUtils.isEmpty(optListSubjects)) {
									
									// get trigger2 email template
									Optional<Email> optFirstEmail = EmailDao.getEmailByRankAndCampaign(session, campaign.getCampaignId(), 2);
									
									if(!optFirstEmail.isPresent())
										continue;
									
									//Get trigger2 email template
									Email emailTemplate = optFirstEmail.get();
									
									/// ADD OTHER SERVICE INSTANTICATION LATER
									AbstractEmailService serviceToUse = applicationContext.getBean(Sparkpost.class);
									
									//aa content list ma bdha content add krvana
									List<String> contents = new ArrayList<>(); 
									contents.add(emailTemplate.getEmailTemplate().getContent());
									contents.add(emailTemplate.getEmailTemplate().getContent2());
									contents.add(emailTemplate.getEmailTemplate().getContent3());
									contents.add(emailTemplate.getEmailTemplate().getContent4());
									contents.add(emailTemplate.getEmailTemplate().getContent5());
									contents.add(emailTemplate.getEmailTemplate().getContent6());
									
									List<CampaignGmailCredential> campaignGmailCredentials = optListCampaignGmailCredentials.get();
									
									int templateContentIndex = 0;
									int fromEmailIndex = 0;
									int sentEmailCount = 0;
									
									//last email send content index
									templateContentIndex = getTrigger2TemplateIndex(optEmailingDBSession);
									//last email send from email index
									fromEmailIndex = getTrigger2FromEmailIndex(optEmailingDBSession, campaignGmailCredentials);
									
									for (Subject subject : optListSubjects) {
										
										// check if subject match a blacklisted word
										subject.setBlacklistedWord(false);
										for (String word : campaign.getBlacklistedWords().split(",")) {
											if (subject.getEmail().toLowerCase().contains(word.toLowerCase())) {
												subject.setBlacklistedWord(true);
												break;
											}
										}

										if (subject.isBlacklistedWord()) {
											SubjectDao.updateSubject(optEmailingDBSession.get(), subject);
										} else {
										
											Long diffDays = EmailingDBUtils.getDiffBetTimeInDays(subject.getInsertDate());
											if (diffDays < campaign.getTimeIntervalUntilNextEmail()) 
												continue;
											
											//Get trigger1 email
											GmailTrigger1Data trackGmailMail1 = GmailTrigger1DataDao.getBySubjectId(optEmailingDBSession.get(), subject.getSubjectId());
											
											List<CampaignGmailCredential> account = campaignGmailCredentials.stream()
													.filter(line -> trackGmailMail1.getSentFrom().equals(line.getGmailCredential().getUsername()))
													.collect(Collectors.toList()); 
											
											boolean gettingReply = EmailingDBUtils.checkReply(account.get(0).getGmailCredential().getUsername(), account.get(0).getGmailCredential().getPassword(), trackGmailMail1.getSentTo());
											
											if(gettingReply) {
												subject.setSent1Reply(true);
												SubjectDao.updateSubject(optEmailingDBSession.get(), subject);
												continue;
											}
											
											String content = contents.get(templateContentIndex);
											templateContentIndex++;
											int sentEmailIndex = templateContentIndex;
											if(templateContentIndex > 5)
												templateContentIndex = 0;
											
											List<CampaignGmailCredential> result = null;
											while (1 == 1) {
												result = campaignGmailCredentials.stream()
														.filter(line -> (line.getGmailCredential().getPerDayPendingCount() == null || line.getGmailCredential().getPerDayPendingCount() != 0))
														.collect(Collectors.toList()); 
												
												if(CollectionUtils.isEmpty(result))
													break;
												
												if(campaignGmailCredentials.get(fromEmailIndex).getGmailCredential().getPerDayPendingCount() != null && campaignGmailCredentials.get(fromEmailIndex).getGmailCredential().getPerDayPendingCount() == 0) {
													fromEmailIndex++;
													if(fromEmailIndex > campaignGmailCredentials.size()-1) {
														fromEmailIndex = 0;
													}
												} else {
													break;
												}
											}
											
											if(CollectionUtils.isEmpty(result))
												break;
											
											CampaignGmailCredential campaignGmailCredential = campaignGmailCredentials.get(fromEmailIndex);
											
											//Send email
											Optional<GmailTrigger2Data> optTrackGmailMail = serviceToUse
													.sendEmailFromGmailServerForTrigger2(campaignGmailCredential, campaign, subject,
															optFirstEmail.get(), content, sentEmailIndex);
											
											if (optTrackGmailMail.isPresent()) {
												
												sentEmailCount++;
												if(campaign.getTimeIntervalUntilNextEmail() == sentEmailCount) {
													//Sleep for 5 miniutes after campaign time interval email
													Thread.sleep(300000);
													sentEmailCount = 0;
												}
												
												//Save sent2 email
												GmailTrigger2DataDao.saveTrackGmailMail(optEmailingDBSession.get(),
														optTrackGmailMail.get());
												
												//Set sent2 succeess or fail in subject table
												Subject subject1 = SubjectDao.getBySubjectId(optEmailingDBSession.get(), optTrackGmailMail.get().getSubjectId());
												subject1.setSent2(optTrackGmailMail.get().isSent2());
												subject1.setUnsubscribeToken(optTrackGmailMail.get().getUnsubscribeToken());
												SubjectDao.updateSubject(optEmailingDBSession.get(), subject1);
											}
											
											if(campaignGmailCredential.getGmailCredential().getPerDayPendingCount() == null) {
												
												Long diffDays1 = EmailingDBUtils.getDiffBetTimeInDays(campaignGmailCredential.getGmailCredential().getCreated());
												if (diffDays1 < 1) {
													campaignGmailCredential.getGmailCredential().setPerDayPendingCount(0L);
												} else {
													Long doubleDays = diffDays1 + diffDays1;
													if(doubleDays < campaignGmailCredential.getGmailCredential().getPerDay())
														campaignGmailCredential.getGmailCredential().setPerDayPendingCount(doubleDays-1);
													else
														campaignGmailCredential.getGmailCredential().setPerDayPendingCount(campaignGmailCredential.getGmailCredential().getPerDay()-1);
												}
											} else {
												campaignGmailCredential.getGmailCredential().setPerDayPendingCount(campaignGmailCredential.getGmailCredential().getPerDayPendingCount()-1);
											}
											
											fromEmailIndex++;
											if(fromEmailIndex > campaignGmailCredentials.size()-1) {
												fromEmailIndex = 0;
											}
										}
									}
									
								}
								
							}
							
						}
						
					} catch (Exception ex) {
						logger.warn("error : ", ex);
					}
				}
			}
			System.gc();
		} catch (Exception ex) {
			logger.error("method triger2 error : ", ex);
		}
	}
	
	private int getTrigger2FromEmailIndex(Optional<Session> optEmailingDBSession,
			List<CampaignGmailCredential> campaignGmailCredentials) {
		
		//Get last sent email
		GmailTrigger2Data trackGmailMail2 = GmailTrigger2DataDao.getLastSubject(optEmailingDBSession.get());
		
		if(trackGmailMail2 == null) {
			return 0;
		} else {
			int fromEmailIndex = 0;
			for (int i = 0; i < campaignGmailCredentials.size(); i++) {
				
				if(trackGmailMail2.getSentFrom().equals(campaignGmailCredentials.get(i).getGmailCredential().getUsername())) {
					fromEmailIndex = i;
					break;
				}
			}
			fromEmailIndex++;
			if(fromEmailIndex > campaignGmailCredentials.size()-1) 
				return 0;
			return fromEmailIndex;
		}
	}
	
	private int getTrigger2TemplateIndex(Optional<Session> optEmailingDBSession) {
		
		//Get last sent email
		GmailTrigger2Data trackGmailMail2 = GmailTrigger2DataDao.getLastSubject(optEmailingDBSession.get());
		
		if(trackGmailMail2 == null) {
			return 0;
		} else {
			if(trackGmailMail2.getContentNumber() > 5)
				return 0;
			return trackGmailMail2.getContentNumber();
		}
	}
	
	@Scheduled(cron = "0 0 */3 * * *")
	public void triger3() {
		try {
			if(!getCureentTimeBetween9amTo6pm())
				return;
			
			logger.info("     manage trigger 3 at :  {}", dateFormat.format(new Date()));

			Session session = appFactory.getSession();
			Optional<List<Campaign>> optListCampaigns = CampaignDao.getListCampaignsFromAllAccounts(session);
			// get all campaigns
			if (optListCampaigns.isPresent()) {
				for (Campaign campaign : optListCampaigns.get()) {
					try {

						SimpleDateFormat formt = new SimpleDateFormat("EEEE");
						
						//Check campaign assign gmail server not if campaign contains gmail server then continue
						boolean gmailServer = false;
						EmailServer emailServer = null;
						for (CampaignEmailServer campaignEmailServer : campaign.getListCampaignEmailServer()) {
							if(campaignEmailServer.getEmailServer().getLabel().toLowerCase().equals("gmail")) {
								gmailServer = true;
								emailServer = campaignEmailServer.getEmailServer();
							}
						}
						
						if(!gmailServer)
							continue;
						
						Optional<List<CampaignGmailCredential>> optListCampaignGmailCredentials = CampaignGmailCredentialDao.getCampaignGmailCredentialsByCampaignId(session, campaign.getCampaignId());
						
						long perDayTotalCount = CampaignGmailCredentialDao.getPerDayTotalCountByCampaign(session, campaign.getCampaignId());
						
						if (campaign.isEnableEmailing() && campaign.getListCampaignEmailServer().size() > 0
								&& (campaign.getEmailingDays().isEmpty() || campaign.getEmailingDays().toLowerCase()
										.contains(formt.format(new Date()).toLowerCase())) && gmailServer && optListCampaignGmailCredentials.isPresent()) {

							Optional<Session> optEmailingDBSession = EmailingDBUtils.getSession(campaign);
							if (optEmailingDBSession.isPresent()) {
								
								// get subjects
								List<Subject> optListSubjects = SubjectDao.getSubjectsForTrigger3(optEmailingDBSession.get(), perDayTotalCount);

								if (!CollectionUtils.isEmpty(optListSubjects)) {
									
									// get trigger3 email template
									Optional<Email> optFirstEmail = EmailDao.getEmailByRankAndCampaign(session, campaign.getCampaignId(), 3);
									
									if(!optFirstEmail.isPresent())
										continue;
									
									//Get trigger3 email template
									Email emailTemplate = optFirstEmail.get();

									/// ADD OTHER SERVICE INSTANTICATION LATER
									AbstractEmailService serviceToUse = applicationContext.getBean(Sparkpost.class);
									
									//aa content list ma bdha content add krvana
									List<String> contents = new ArrayList<>(); 
									contents.add(emailTemplate.getEmailTemplate().getContent());
									contents.add(emailTemplate.getEmailTemplate().getContent2());
									contents.add(emailTemplate.getEmailTemplate().getContent3());
									contents.add(emailTemplate.getEmailTemplate().getContent4());
									contents.add(emailTemplate.getEmailTemplate().getContent5());
									contents.add(emailTemplate.getEmailTemplate().getContent6());
									
									List<CampaignGmailCredential> campaignGmailCredentials = optListCampaignGmailCredentials.get();
									
									int templateContentIndex = 0;
									int fromEmailIndex = 0;
									int sentEmailCount = 0;
									
									//last email send content index
									templateContentIndex = getTrigger3TemplateIndex(optEmailingDBSession);
									//last email send from email index
									fromEmailIndex = getTrigger3FromEmailIndex(optEmailingDBSession, campaignGmailCredentials);
									
									for (Subject subject : optListSubjects) {
										
										// check if subject match a blacklisted word
										subject.setBlacklistedWord(false);
										for (String word : campaign.getBlacklistedWords().split(",")) {
											if (subject.getEmail().toLowerCase().contains(word.toLowerCase())) {
												subject.setBlacklistedWord(true);
												break;
											}
										}

										if (subject.isBlacklistedWord()) {
											SubjectDao.updateSubject(optEmailingDBSession.get(), subject);
										} else {
										
											Long diffDays = EmailingDBUtils.getDiffBetTimeInDays(subject.getInsertDate());
											if (diffDays < campaign.getTimeIntervalUntilNextEmail()) 
												continue;
											
											//Get trigger2 email
											GmailTrigger2Data trackGmailMail2 = GmailTrigger2DataDao.getBySubjectId(optEmailingDBSession.get(), subject.getSubjectId());
											
											List<CampaignGmailCredential> account = campaignGmailCredentials.stream()
									                .filter(line -> trackGmailMail2.getSentFrom().equals(line.getGmailCredential().getUsername()))
									                .collect(Collectors.toList()); 
											
											boolean gettingReply = EmailingDBUtils.checkReply(account.get(0).getGmailCredential().getUsername(), account.get(0).getGmailCredential().getPassword(), trackGmailMail2.getSentTo());
											
											if(gettingReply) {
												subject.setSent2Reply(true);
												SubjectDao.updateSubject(optEmailingDBSession.get(), subject);
												continue;
											}
											
											String content = contents.get(templateContentIndex);
											templateContentIndex++;
											int sentEmailIndex = templateContentIndex;
											if(templateContentIndex > 5)
												templateContentIndex = 0;
											
											List<CampaignGmailCredential> result = null;
											while (1 == 1) {
										        result = campaignGmailCredentials.stream()
										                .filter(line -> (line.getGmailCredential().getPerDayPendingCount() == null || line.getGmailCredential().getPerDayPendingCount() != 0))
										                .collect(Collectors.toList()); 
										        
										        if(CollectionUtils.isEmpty(result))
										        	break;
												
												if(campaignGmailCredentials.get(fromEmailIndex).getGmailCredential().getPerDayPendingCount() != null && campaignGmailCredentials.get(fromEmailIndex).getGmailCredential().getPerDayPendingCount() == 0) {
													fromEmailIndex++;
													if(fromEmailIndex > campaignGmailCredentials.size()-1) {
														fromEmailIndex = 0;
													}
												} else {
													break;
												}
											}
											
											if(CollectionUtils.isEmpty(result))
									        	break;
											
											CampaignGmailCredential campaignGmailCredential = campaignGmailCredentials.get(fromEmailIndex);
											
											//Send email
											Optional<GmailTrigger3Data> optTrackGmailMail = serviceToUse
													.sendEmailFromGmailServerForTrigger3(campaignGmailCredential, campaign, subject,
															optFirstEmail.get(), content, sentEmailIndex);
											
											if (optTrackGmailMail.isPresent()) {
	
												sentEmailCount++;
												if(campaign.getTimeIntervalUntilNextEmail() == sentEmailCount) {
													//Sleep for 5 miniutes after campaign time interval email
													Thread.sleep(300000);
													sentEmailCount = 0;
												}
												
												//Save sent3 email
												GmailTrigger3DataDao.saveTrackGmailMail(optEmailingDBSession.get(),
														optTrackGmailMail.get());
												
												//Set sent3 succeess or fail in subject table
												Subject subject1 = SubjectDao.getBySubjectId(optEmailingDBSession.get(), optTrackGmailMail.get().getSubjectId());
												subject1.setSent3(optTrackGmailMail.get().isSent3());
												subject1.setUnsubscribeToken(optTrackGmailMail.get().getUnsubscribeToken());
												SubjectDao.updateSubject(optEmailingDBSession.get(), subject1);
											}
											
											
											if(campaignGmailCredential.getGmailCredential().getPerDayPendingCount() == null) {
												
												Long diffDays1 = EmailingDBUtils.getDiffBetTimeInDays(campaignGmailCredential.getGmailCredential().getCreated());
												if (diffDays1 < 1) {
													campaignGmailCredential.getGmailCredential().setPerDayPendingCount(0L);
												} else {
													Long doubleDays = diffDays1 + diffDays1;
													if(doubleDays < campaignGmailCredential.getGmailCredential().getPerDay())
														campaignGmailCredential.getGmailCredential().setPerDayPendingCount(doubleDays-1);
													else
														campaignGmailCredential.getGmailCredential().setPerDayPendingCount(campaignGmailCredential.getGmailCredential().getPerDay()-1);
												}
											} else {
												campaignGmailCredential.getGmailCredential().setPerDayPendingCount(campaignGmailCredential.getGmailCredential().getPerDayPendingCount()-1);
											}
											
											fromEmailIndex++;
											if(fromEmailIndex > campaignGmailCredentials.size()-1) {
												fromEmailIndex = 0;
											}
										}
									}

								}

							}

						}

					} catch (Exception ex) {
						logger.warn("error : ", ex);
					}
				}
			}
			
			System.gc();
		} catch (Exception ex) {
			logger.error("method triger3 error : ", ex);
		}
	}

	private int getTrigger3FromEmailIndex(Optional<Session> optEmailingDBSession,
			List<CampaignGmailCredential> campaignGmailCredentials) {
		
		//Get last sent email
		GmailTrigger3Data trackGmailMail3 = GmailTrigger3DataDao.getLastSubject(optEmailingDBSession.get());
		
		if(trackGmailMail3 == null) {
			return 0;
		} else {
			int fromEmailIndex = 0;
			for (int i = 0; i < campaignGmailCredentials.size(); i++) {
				
				if(trackGmailMail3.getSentFrom().equals(campaignGmailCredentials.get(i).getGmailCredential().getUsername())) {
					fromEmailIndex = i;
					break;
				}
			}
			fromEmailIndex++;
			if(fromEmailIndex > campaignGmailCredentials.size()-1) 
				return 0;
			return fromEmailIndex;
		}
	}

	private int getTrigger3TemplateIndex(Optional<Session> optEmailingDBSession) {
		
		//Get last sent email
		GmailTrigger3Data trackGmailMail3 = GmailTrigger3DataDao.getLastSubject(optEmailingDBSession.get());
		
		if(trackGmailMail3 == null) {
			return 0;
		} else {
			if(trackGmailMail3.getContentNumber() > 5)
				return 0;
			return trackGmailMail3.getContentNumber();
		}
	}
	
}
