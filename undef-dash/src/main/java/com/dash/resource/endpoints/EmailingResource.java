package com.dash.resource.endpoints;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dash.dao.CampaignDao;
import com.dash.jwt.JwtTokenUtil;
import com.dash.model.Campaign;
import com.dash.undefdash.ApplicationFactory;
import com.emailing.dao.EventDao;
import com.emailing.dao.MessageDao;
import com.emailing.dao.SubjectDao;
import com.emailing.model.Event;
import com.emailing.model.EventType;
import com.emailing.model.Message;
import com.emailing.model.Subject;
import com.emailing.utils.EmailingDBUtils;

@RestController
@RequestMapping("/emailing")
public class EmailingResource {
	private static final Logger logger = LoggerFactory.getLogger(EmailingResource.class);

	@Autowired
	private ApplicationFactory appFactory;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@GetMapping("/unsubscribe")
	public String unsubscribe(@RequestParam("token") String token) {
		try {
			
			Session session = appFactory.getSession();
			
			if (token.contains("-") == false) {
				return "invalid token";
			} else {
				String campaignId = token.split("-")[0];
				
				Optional<Campaign> optCampaign = CampaignDao.getCampaignById(session, Long.valueOf(campaignId));
				if (optCampaign.isPresent()) {
					Campaign campaign = optCampaign.get();
					Optional<Session> optEmailingSessionDB = EmailingDBUtils.getSession(campaign);
					if (optEmailingSessionDB.isPresent() && optEmailingSessionDB.get().isConnected()) {
						
						Optional<Message> optMessage = MessageDao
								.getMessageByUnsubscribeToken(optEmailingSessionDB.get(), token);
						if (optMessage.isPresent()) {
							Message message = optMessage.get();
							Event event = new Event(UUID.randomUUID().toString().replace("-", ""),
									EventType.unsubscribe, new Date(), message);
							EventDao.saveEvent(optEmailingSessionDB.get(), event);
							return "Thank you, have a great day!";
						}
					}
				}
			}
			return "Thank you, have a great day!";
			
		} catch (Exception ex) {
			logger.error("method unsubscribe error : ", ex);
			return "";
		}
	}
	
	@GetMapping("/trigger1/unsubscribe")
	public String gmailUnsubscribeTrigger1(@RequestParam("token") String token) {
		try {
			
			Session session = appFactory.getSession();
			
			if (token.contains("-") == false) {
				return "invalid token";
			} else {
				String campaignId = token.split("-")[0];
				
				Optional<Campaign> optCampaign = CampaignDao.getCampaignById(session, Long.valueOf(campaignId));
				if (optCampaign.isPresent()) {
					Campaign campaign = optCampaign.get();
					Optional<Session> optEmailingSessionDB = EmailingDBUtils.getSession(campaign);
					if (optEmailingSessionDB.isPresent() && optEmailingSessionDB.get().isConnected()) {
						
						//Get subject by token
						Subject subject = SubjectDao.getSubjectByUnsubscribeToken(optEmailingSessionDB.get(), token);
						
						if (subject != null) {
							subject.setUnsubscribe(true);
							subject.setSent1Read(true);
							SubjectDao.updateSubject(optEmailingSessionDB.get(), subject);
							return "Thank you, have a great day!";
						}
					}
				}
			}
			return "Thank you, have a great day!";
			
		} catch (Exception ex) {
			logger.error("method gmailUnsubscribeTrigger1 error : ", ex);
			return "";
		}
	}
	
	@GetMapping("/trackEmail")
	public String trackEmail(@RequestParam("token") String token) {
		try {
			
			Session session = appFactory.getSession();
			
			if (token.contains("-") == false) {
				return "invalid token";
			} else {
				String campaignId = token.split("-")[0];
				
				Optional<Campaign> optCampaign = CampaignDao.getCampaignById(session, Long.valueOf(campaignId));
				if (optCampaign.isPresent()) {
					Campaign campaign = optCampaign.get();
					Optional<Session> optEmailingSessionDB = EmailingDBUtils.getSession(campaign);
					if (optEmailingSessionDB.isPresent() && optEmailingSessionDB.get().isConnected()) {
						
						//Get subject by token
						Subject subject = SubjectDao.getSubjectByUnsubscribeToken(optEmailingSessionDB.get(), token);
						
						if (subject != null) {
							subject.setSent1Read(true);
							SubjectDao.updateSubject(optEmailingSessionDB.get(), subject);
							return "";
						}
					}
				}
			}
			return "";
			
		} catch (Exception ex) {
			logger.error("method trackEmail error : ", ex);
			return "";
		}
	}
	
	@GetMapping("/trigger2/unsubscribe")
	public String gmailUnsubscribeTrigger2(@RequestParam("token") String token) {
		try {
			
			Session session = appFactory.getSession();
			
			if (token.contains("-") == false) {
				return "invalid token";
			} else {
				String campaignId = token.split("-")[0];
				
				Optional<Campaign> optCampaign = CampaignDao.getCampaignById(session, Long.valueOf(campaignId));
				if (optCampaign.isPresent()) {
					Campaign campaign = optCampaign.get();
					Optional<Session> optEmailingSessionDB = EmailingDBUtils.getSession(campaign);
					if (optEmailingSessionDB.isPresent() && optEmailingSessionDB.get().isConnected()) {
						
						//Get subject by token
						Subject subject = SubjectDao.getSubjectByUnsubscribeToken(optEmailingSessionDB.get(), token);
						
						if (subject != null) {
							subject.setUnsubscribe(true);
							subject.setSent2Read(true);
							SubjectDao.updateSubject(optEmailingSessionDB.get(), subject);
							return "Thank you, have a great day!";
						}
					}
				}
			}
			return "Thank you, have a great day!";
			
		} catch (Exception ex) {
			logger.error("method gmailUnsubscribeTrigger2 error : ", ex);
			return "";
		}
	}
	
	@GetMapping("/trackTrigger2Email")
	public String trackTrigger2Email(@RequestParam("token") String token) {
		try {
			
			Session session = appFactory.getSession();
			
			if (token.contains("-") == false) {
				return "invalid token";
			} else {
				String campaignId = token.split("-")[0];
				
				Optional<Campaign> optCampaign = CampaignDao.getCampaignById(session, Long.valueOf(campaignId));
				if (optCampaign.isPresent()) {
					Campaign campaign = optCampaign.get();
					Optional<Session> optEmailingSessionDB = EmailingDBUtils.getSession(campaign);
					if (optEmailingSessionDB.isPresent() && optEmailingSessionDB.get().isConnected()) {
						
						//Get subject by token
						Subject subject = SubjectDao.getSubjectByUnsubscribeToken(optEmailingSessionDB.get(), token);
						
						if (subject != null) {
							subject.setSent2Read(true);
							SubjectDao.updateSubject(optEmailingSessionDB.get(), subject);
							return "";
						}
					}
				}
			}
			return "";
			
		} catch (Exception ex) {
			logger.error("method trackTrigger2Email error : ", ex);
			return "";
		}
	}
	
	@GetMapping("/trigger3/unsubscribe")
	public String gmailUnsubscribeTrigger3(@RequestParam("token") String token) {
		try {
			
			Session session = appFactory.getSession();
			
			if (token.contains("-") == false) {
				return "invalid token";
			} else {
				String campaignId = token.split("-")[0];
				
				Optional<Campaign> optCampaign = CampaignDao.getCampaignById(session, Long.valueOf(campaignId));
				if (optCampaign.isPresent()) {
					Campaign campaign = optCampaign.get();
					Optional<Session> optEmailingSessionDB = EmailingDBUtils.getSession(campaign);
					if (optEmailingSessionDB.isPresent() && optEmailingSessionDB.get().isConnected()) {
						
						//Get subject by token
						Subject subject = SubjectDao.getSubjectByUnsubscribeToken(optEmailingSessionDB.get(), token);
						
						if (subject != null) {
							subject.setUnsubscribe(true);
							subject.setSent3Read(true);
							SubjectDao.updateSubject(optEmailingSessionDB.get(), subject);
							return "Thank you, have a great day!";
						}
					}
				}
			}
			return "Thank you, have a great day!";
			
		} catch (Exception ex) {
			logger.error("method gmailUnsubscribeTrigger3 error : ", ex);
			return "";
		}
	}
	
	@GetMapping("/trackTrigger3Email")
	public String trackTrigger3Email(@RequestParam("token") String token) {
		try {
			
			Session session = appFactory.getSession();
			
			if (token.contains("-") == false) {
				return "invalid token";
			} else {
				String campaignId = token.split("-")[0];
				
				Optional<Campaign> optCampaign = CampaignDao.getCampaignById(session, Long.valueOf(campaignId));
				if (optCampaign.isPresent()) {
					Campaign campaign = optCampaign.get();
					Optional<Session> optEmailingSessionDB = EmailingDBUtils.getSession(campaign);
					if (optEmailingSessionDB.isPresent() && optEmailingSessionDB.get().isConnected()) {
						
						//Get subject by token
						Subject subject = SubjectDao.getSubjectByUnsubscribeToken(optEmailingSessionDB.get(), token);
						
						if (subject != null) {
							subject.setSent3Read(true);
							SubjectDao.updateSubject(optEmailingSessionDB.get(), subject);
							return "";
						}
					}
				}
			}
			return "";
			
		} catch (Exception ex) {
			logger.error("method trackTrigger3Email error : ", ex);
			return "";
		}
	}

}
