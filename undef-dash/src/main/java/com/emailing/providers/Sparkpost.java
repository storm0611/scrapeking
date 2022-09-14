package com.emailing.providers;

import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.dash.model.Campaign;
import com.dash.model.CampaignGmailCredential;
import com.dash.model.Email;
import com.dash.model.EmailServer;
import com.dash.model.EmailServerParameter;
import com.emailing.model.EventType;
import com.emailing.model.Subject;
import com.emailing.model.GmailTrigger1Data;
import com.emailing.model.GmailTrigger2Data;
import com.emailing.model.GmailTrigger3Data;
import com.emailing.providers.model.ProxyEvent;
import com.emailing.providers.model.ProxyMessage;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.sparkpost.Client;
import com.sparkpost.model.AddressAttributes;
import com.sparkpost.model.RecipientAttributes;
import com.sparkpost.model.TemplateContentAttributes;
import com.sparkpost.model.TransmissionWithRecipientArray;
import com.sparkpost.model.responses.TransmissionCreateResponse;
import com.sparkpost.resources.ResourceTransmissions;
import com.sparkpost.transport.IRestConnection;
import com.sparkpost.transport.RestConnection;

@Component
public class Sparkpost extends AbstractEmailService {
	private static final Logger logger = LoggerFactory.getLogger(Sparkpost.class);

	// SPARKPOST SERVERS USE EHCACHE TO KEEP TRACK OF EVENTS SCRAPING SESSION ...

	public static List<String> listRequiredParameters;
	static {
		listRequiredParameters = new ArrayList<String>();
		listRequiredParameters.add("api_key");
		listRequiredParameters.add("from");
	}

	@Override
	public Optional<ProxyMessage> sendEmail(EmailServer emailServer, Campaign campaign, Subject subject, Email email) {
		// TODO Auto-generated method stub
		try {

			logger.info("                                 " + subject.getEmail());
			String name = subject.getName();
			if (name == null)
				name = "";
			// add tags later
			String emailSubject = email.getEmailTemplate().getSubject().replaceAll(this.nameTag, name);

			String emailContent = email.getEmailTemplate().getContent().replaceAll(this.nameTag, name);

			// prepare UNSUBSCRIBE directive
			String unsubscribeToken = campaign.getCampaignId() + "-" + UUID.randomUUID().toString().replace("-", "");
			String unsubscribeURL = "http://" + getAppFactory().getServerIp() + ":" + getAppFactory().getServerPort()
					+ "/emailing/unsubscribe?token=" + unsubscribeToken;
			String unsubscribeDirective = "<a href = \"" + unsubscribeURL + "\">unsubscribe</a>";

			emailContent = emailContent.replaceAll(this.unsubscribeTag, unsubscribeDirective);

			String API_KEY = "";
			String FROM = "";

			for (EmailServerParameter param : emailServer.getListServerParameters()) {
				if (param.getKey().equalsIgnoreCase("api_key")) {
					API_KEY = param.getValue();
				} else if (param.getKey().equalsIgnoreCase("from")) {
					FROM = param.getValue();
				}
			}

			Client client = new Client(API_KEY);
			client.setFromEmail(FROM);

			TransmissionWithRecipientArray transmission = new TransmissionWithRecipientArray();
			// Populate Recipients

			List<RecipientAttributes> recipientArray = new ArrayList<RecipientAttributes>();

			String[] recipients = new String[] { subject.getEmail() };
			for (String recipient : recipients) {
				RecipientAttributes recipientAttribs = new RecipientAttributes();
				recipientAttribs.setAddress(new AddressAttributes(StringUtils.normalizeSpace(recipient).trim()));
				HashMap<String, String> mapMeta = new HashMap<String, String>();
				// pass CAMPAING ID as a mete-data
				mapMeta.put("campaign_id", String.valueOf(campaign.getCampaignId()));
				mapMeta.put("subject_id", String.valueOf(subject.getSubjectId()));
				recipientAttribs.setMetadata(mapMeta);
				recipientArray.add(recipientAttribs);
			}

			transmission.setRecipientArray(recipientArray);

			/// now use email object
			TemplateContentAttributes contentAttributes = new TemplateContentAttributes();
			contentAttributes.setFrom(new AddressAttributes(client.getFromEmail()));
			// set subject
			contentAttributes.setSubject(emailSubject);
			// set content
			contentAttributes.setHtml(emailContent);

			transmission.setContentAttributes(contentAttributes);
			transmission.setContentAttributes(contentAttributes);
			// send now
			IRestConnection connection = new RestConnection(client);
			TransmissionCreateResponse transmissionResponse = ResourceTransmissions.create(connection, 0, transmission);

			String messageId = "";
			Pattern pattern = Pattern.compile("id=(.*?)\\)");
			Matcher matcher = pattern.matcher(transmissionResponse.toString());
			if (matcher.find())
				messageId = matcher.group(1);

			if (messageId != null && messageId.trim().isEmpty() == false) {
				ProxyMessage proxyMessage = new ProxyMessage();
				proxyMessage.setMessageId(messageId);
				proxyMessage.setUnsubscribeToken(unsubscribeToken);
				return Optional.of(proxyMessage);
			} else {
				return Optional.empty();
			}

		} catch (Exception ex) {
			logger.error("method error : ", ex);
			return Optional.empty();
		}

	}

	// normally this method will download / fetch evens
	@Override
	public Optional<List<ProxyEvent>> fetchServerEvents(EmailServer emailServer) {
		// TODO Auto-generated method stub
		try {

			SimpleDateFormat eventDateFormater = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
			List<ProxyEvent> listProxyEvent = new ArrayList<ProxyEvent>();

			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.HOUR_OF_DAY, -4);

			// prepare URL
			SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

			String endpoint = "https://api.sparkpost.com/api/v1/events/message?per_page=1000&cursor=initial&from="
					+ formater.format(cal.getTime()) + "&to=" + formater.format(new Date());

			String API_KEY = "";
			for (EmailServerParameter param : emailServer.getListServerParameters()) {
				if (param.getKey().equalsIgnoreCase("api_key")) {
					API_KEY = param.getValue();
				}
			}

			boolean keepGoing = true;
			while (keepGoing) {

				Document response = Jsoup.connect(endpoint).header("Authorization", API_KEY).ignoreContentType(true)
						.timeout(80 * 1000).get();

				JsonObject jsonPayload = new JsonParser().parse(new JsonReader(new StringReader(response.text())))
						.getAsJsonObject();

				// preparing for next page
				String nextPageToken = "";
				if (jsonPayload.has("links") && jsonPayload.get("links").isJsonObject()
						&& jsonPayload.get("links").isJsonNull() == false) {
					JsonObject links = jsonPayload.get("links").getAsJsonObject();
					if (links.has("next") && links.get("next").isJsonNull() == false)
						nextPageToken = links.get("next").getAsString();
				}
				if (nextPageToken.isEmpty())
					keepGoing = false;
				else
					endpoint = "https://api.sparkpost.com" + nextPageToken;
				////////////////////////////////////////

				if (jsonPayload.has("results") && jsonPayload.get("results").isJsonNull() == false
						&& jsonPayload.get("results").isJsonArray()) {

					for (JsonElement element : jsonPayload.get("results").getAsJsonArray()) {
						JsonObject item = element.getAsJsonObject();
						if (item.has("rcpt_meta") && item.get("rcpt_meta").isJsonNull() == false) {

							JsonObject rcpt_meta = item.get("rcpt_meta").getAsJsonObject();
							if (rcpt_meta.has("campaign_id") && rcpt_meta.get("campaign_id").isJsonNull() == false) {
								// these two are from metadata

								ProxyEvent proxyEvent = new ProxyEvent(rcpt_meta.get("campaign_id").getAsInt(),
										rcpt_meta.get("subject_id").getAsString(),
										item.get("transmission_id").getAsString(), item.get("event_id").getAsString(),
										eventDateFormater.parse(item.get("timestamp").getAsString()));

								String type = item.get("type").getAsString();

								if (type.equalsIgnoreCase("delivery"))
									proxyEvent.setEventType(EventType.delivered);

								else if (type.equalsIgnoreCase("bounce") || type.equalsIgnoreCase("policy_rejection")
										|| type.equalsIgnoreCase("out_of_band")
										|| type.equalsIgnoreCase("spam_complaint"))
									proxyEvent.setEventType(EventType.bounce);

								else if (type.equalsIgnoreCase("initial_open") || type.equalsIgnoreCase("open"))
									proxyEvent.setEventType(EventType.open);

								else if (type.equalsIgnoreCase("click"))
									proxyEvent.setEventType(EventType.click);

								else if (type.equalsIgnoreCase("list_unsubscribe")
										|| type.equalsIgnoreCase("link_unsubscribe"))
									proxyEvent.setEventType(EventType.unsubscribe);
								else
									proxyEvent.setEventType(EventType.other);

								listProxyEvent.add(proxyEvent);
							}
						}
					}

				}
			}

//			cache.put(emailServer.getEmailServerId(), toDate);
//			db.close();

			return Optional.of(listProxyEvent);

		} catch (Exception ex) {
			logger.error("method fetchServerEvents error : ", ex);
			return Optional.empty();
		}
	}

	@Override
	public Optional<GmailTrigger1Data> sendEmailFromGmailServer(CampaignGmailCredential campaignGmailCredential, Campaign campaign, Subject subject,
			Email email, String content, int sentEmailIndex) {
		
		logger.info("                                 " + subject.getEmail());
		String name = subject.getName();
		if (name == null)
			name = "";
		// add tags later
		String emailSubject = email.getEmailTemplate().getSubject().replaceAll(this.nameTag, name);

		String emailContent = content.replaceAll(this.nameTag, name);
		
		// Recipient's email ID needs to be mentioned.
		String to = subject.getEmail();

		// Sender's email ID needs to be mentioned
		String from = campaignGmailCredential.getGmailCredential().getUsername();
		final String username = campaignGmailCredential.getGmailCredential().getUsername();//change accordingly
		final String password = campaignGmailCredential.getGmailCredential().getPassword();//change accordingly

	    Properties props = new Properties();
	    props.put("mail.smtp.auth", "true");
	    props.put("mail.smtp.starttls.enable", "true");
	    props.put("mail.smtp.host", "smtp.gmail.com");
	    props.put("mail.smtp.port", "587");
	    props.put("mail.smtp.ssl.protocols", "TLSv1.2");
	    props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
	    
	    props.setProperty("proxySet","true");
        props.setProperty("socksProxyHost",campaignGmailCredential.getGmailCredential().getProxyIp());
        props.setProperty("socksProxyPort",campaignGmailCredential.getGmailCredential().getProxyPort().toString());

	    Session session = Session.getInstance(props,
	      new javax.mail.Authenticator() {
	        protected PasswordAuthentication getPasswordAuthentication() {
	            return new PasswordAuthentication(username, password);
	        }
	      });

	    String unsubscribeToken = "";
	    try {
			// prepare UNSUBSCRIBE directive
			unsubscribeToken = campaign.getCampaignId() + "-" + UUID.randomUUID().toString().replace("-", "");
			String unsubscribeURL = "http://" + getAppFactory().getServerIp() + ":" + getAppFactory().getServerPort()
					+ "/emailing/trigger1/unsubscribe?token=" + unsubscribeToken;
			String unsubscribeDirective = "<a href = \"" + unsubscribeURL + "\">unsubscribe</a>";

			emailContent = emailContent.replaceAll(this.unsubscribeTag, unsubscribeDirective);
			
			//Create unique message id
			String messageId = UUID.randomUUID().toString().replace("-", "") + "_" + new Date().getTime();
			
			String serverUrl = "http://" + getAppFactory().getServerIp() + ":" + getAppFactory().getServerPort();
//			String imageTag = "<img src='" + serverUrl + "/assets/img/profile.jpg' style='height: 13%; width: 35%;' width='42' />";
			String imageTag = "<img src='" + serverUrl + "/emailing/trackEmail?token=" + unsubscribeToken + "' style='display:none; height: 13%; width: 35%;' width='42' />";
			emailContent = emailContent.replaceAll(this.serverUrlTag, imageTag);
			
			//Create analytic url
			String analyticUrlTag = "<img src='https://www.google-analytics.com/collect?v=1&tid=" + campaignGmailCredential.getGmailCredential().getAnalyticsId() + "&cid=" + messageId + "&t=event&ec=email&ea=open&el='"+ unsubscribeToken +"'&cs=newsletter&cm=email&cn=test&dp=%2Femail%2Fnewsletter1&dt=My%20Newsletter' />";
			emailContent = emailContent.replaceAll(this.analyticUrlTag, analyticUrlTag);

	        Message message = new MimeMessage(session);
	        message.setFrom(new InternetAddress(from));
	        message.addRecipient(Message.RecipientType.TO,  new InternetAddress(to));
	        message.setSubject(emailSubject);
	        message.setContent(emailContent, "text/html");

	        Transport.send(message);

	        logger.info("Sent message successfully.... trigger1");
			
			if (messageId != null && messageId.trim().isEmpty() == false) {
				//Create track send email object
				GmailTrigger1Data trackGmailMail = new GmailTrigger1Data(0, messageId, subject.getSubjectId(), new Date(), from, to, email.getEmailTemplate().getEmailTemplateId(), sentEmailIndex, "");
				//Sent email success and unsubscribeToken
				trackGmailMail.setSent1(true);
				trackGmailMail.setUnsubscribeToken(unsubscribeToken);
				return Optional.of(trackGmailMail);
			} else {
				return Optional.empty();
			}

		} catch (NoSuchProviderException e) {
			logger.error("method trigger1 error : ", e);
	        //Create track send email object
			GmailTrigger1Data trackGmailMail = new GmailTrigger1Data(0, null, subject.getSubjectId(), new Date(), from, to, email.getEmailTemplate().getEmailTemplateId(), sentEmailIndex, e.getMessage());
			//Sent email fail and unsubscribeToken
			trackGmailMail.setSent1(false);
			trackGmailMail.setUnsubscribeToken(unsubscribeToken);
			return Optional.of(trackGmailMail);
		} catch (MessagingException e) {
			logger.error("method trigger1 error : ", e);
			//Create track send email object
			GmailTrigger1Data trackGmailMail = new GmailTrigger1Data(0, null, subject.getSubjectId(), new Date(), from, to, email.getEmailTemplate().getEmailTemplateId(), sentEmailIndex, e.getMessage());
			//Sent email fail and unsubscribeToken
			trackGmailMail.setSent1(false);
			trackGmailMail.setUnsubscribeToken(unsubscribeToken);
			return Optional.of(trackGmailMail);
		} catch (Exception e) {
			logger.error("method trigger1 error : ", e);
			//Create track send email object
			GmailTrigger1Data trackGmailMail = new GmailTrigger1Data(0, null, subject.getSubjectId(), new Date(), from, to, email.getEmailTemplate().getEmailTemplateId(), sentEmailIndex, e.getMessage());
			//Sent email fail and unsubscribeToken
			trackGmailMail.setSent1(false);
			trackGmailMail.setUnsubscribeToken(unsubscribeToken);
			return Optional.of(trackGmailMail);
		}
	}

	@Override
	public Optional<GmailTrigger2Data> sendEmailFromGmailServerForTrigger2(
			CampaignGmailCredential campaignGmailCredential, Campaign campaign, Subject subject, Email email,
			String content, int sentEmailIndex) {
		logger.info("                                 " + subject.getEmail());
		String name = subject.getName();
		if (name == null)
			name = "";
		// add tags later
		String emailSubject = email.getEmailTemplate().getSubject().replaceAll(this.nameTag, name);

		String emailContent = content.replaceAll(this.nameTag, name);
		
		// Recipient's email ID needs to be mentioned.
		String to = subject.getEmail();

		// Sender's email ID needs to be mentioned
		String from = campaignGmailCredential.getGmailCredential().getUsername();
		final String username = campaignGmailCredential.getGmailCredential().getUsername();//change accordingly
		final String password = campaignGmailCredential.getGmailCredential().getPassword();//change accordingly

	    Properties props = new Properties();
	    props.put("mail.smtp.auth", "true");
	    props.put("mail.smtp.starttls.enable", "true");
	    props.put("mail.smtp.host", "smtp.gmail.com");
	    props.put("mail.smtp.port", "587");

	    Session session = Session.getInstance(props,
	      new javax.mail.Authenticator() {
	        protected PasswordAuthentication getPasswordAuthentication() {
	            return new PasswordAuthentication(username, password);
	        }
	      });

	    String unsubscribeToken = "";
	    try {
			// prepare UNSUBSCRIBE directive
			unsubscribeToken = campaign.getCampaignId() + "-" + UUID.randomUUID().toString().replace("-", "");
			String unsubscribeURL = "http://" + getAppFactory().getServerIp() + ":" + getAppFactory().getServerPort()
					+ "/emailing/trigger2/unsubscribe?token=" + unsubscribeToken;
			String unsubscribeDirective = "<a href = \"" + unsubscribeURL + "\">unsubscribe</a>";

			emailContent = emailContent.replaceAll(this.unsubscribeTag, unsubscribeDirective);
			
			//Create unique message id
			String messageId = UUID.randomUUID().toString().replace("-", "") + "_" + new Date().getTime();
			
			String serverUrl = "http://" + getAppFactory().getServerIp() + ":" + getAppFactory().getServerPort();
//			String imageTag = "<img src='" + serverUrl + "/assets/img/profile.jpg' style='height: 13%; width: 35%;' width='42' />";
			String imageTag = "<img src='" + serverUrl + "/emailing/trackTrigger2Email?token=" + unsubscribeToken + "' style='display:none; height: 13%; width: 35%;' width='42' />";
			emailContent = emailContent.replaceAll(this.serverUrlTag, imageTag);
			
			//Create analytic url
			String analyticUrlTag = "<img src='https://www.google-analytics.com/collect?v=1&tid=" + campaignGmailCredential.getGmailCredential().getAnalyticsId() + "&cid=" + messageId + "&t=event&ec=email&ea=open&el='"+ unsubscribeToken +"'&cs=newsletter&cm=email&cn=test&dp=%2Femail%2Fnewsletter1&dt=My%20Newsletter' />";
			emailContent = emailContent.replaceAll(this.analyticUrlTag, analyticUrlTag);

	        Message message = new MimeMessage(session);
	        message.setFrom(new InternetAddress(from));
	        message.addRecipient(Message.RecipientType.TO,  new InternetAddress(to));
	        message.setSubject(emailSubject);
	        message.setContent(emailContent, "text/html");

	        Transport.send(message);

	        logger.info("Sent message successfully.... trigger2");
			
			if (messageId != null && messageId.trim().isEmpty() == false) {
				//Create track send email object
				GmailTrigger2Data trackGmailMail = new GmailTrigger2Data(0, messageId, subject.getSubjectId(), new Date(), from, to, email.getEmailTemplate().getEmailTemplateId(), sentEmailIndex, "");
				//Sent email success and unsubscribeToken
				trackGmailMail.setSent2(true);
				trackGmailMail.setUnsubscribeToken(unsubscribeToken);
				return Optional.of(trackGmailMail);
			} else {
				return Optional.empty();
			}

		} catch (NoSuchProviderException e) {
			logger.error("method trigger2 error : ", e);
	        //Create track send email object
			GmailTrigger2Data trackGmailMail = new GmailTrigger2Data(0, null, subject.getSubjectId(), new Date(), from, to, email.getEmailTemplate().getEmailTemplateId(), sentEmailIndex, e.getMessage());
			//Sent email fail and unsubscribeToken
			trackGmailMail.setSent2(false);
			trackGmailMail.setUnsubscribeToken(unsubscribeToken);
			return Optional.of(trackGmailMail);
		} catch (MessagingException e) {
			logger.error("method trigger2 error : ", e);
			//Create track send email object
			GmailTrigger2Data trackGmailMail = new GmailTrigger2Data(0, null, subject.getSubjectId(), new Date(), from, to, email.getEmailTemplate().getEmailTemplateId(), sentEmailIndex, e.getMessage());
			//Sent email fail and unsubscribeToken
			trackGmailMail.setSent2(false);
			trackGmailMail.setUnsubscribeToken(unsubscribeToken);
			return Optional.of(trackGmailMail);
		} catch (Exception e) {
			logger.error("method trigger2 error : ", e);
			//Create track send email object
			GmailTrigger2Data trackGmailMail = new GmailTrigger2Data(0, null, subject.getSubjectId(), new Date(), from, to, email.getEmailTemplate().getEmailTemplateId(), sentEmailIndex, e.getMessage());
			//Sent email fail and unsubscribeToken
			trackGmailMail.setSent2(false);
			trackGmailMail.setUnsubscribeToken(unsubscribeToken);
			return Optional.of(trackGmailMail);
		}
	}

	@Override
	public Optional<GmailTrigger3Data> sendEmailFromGmailServerForTrigger3(
			CampaignGmailCredential campaignGmailCredential, Campaign campaign, Subject subject, Email email,
			String content, int sentEmailIndex) {
		logger.info("                                 " + subject.getEmail());
		String name = subject.getName();
		if (name == null)
			name = "";
		// add tags later
		String emailSubject = email.getEmailTemplate().getSubject().replaceAll(this.nameTag, name);

		String emailContent = content.replaceAll(this.nameTag, name);
		
		// Recipient's email ID needs to be mentioned.
		String to = subject.getEmail();

		// Sender's email ID needs to be mentioned
		String from = campaignGmailCredential.getGmailCredential().getUsername();
		final String username = campaignGmailCredential.getGmailCredential().getUsername();//change accordingly
		final String password = campaignGmailCredential.getGmailCredential().getPassword();//change accordingly

	    Properties props = new Properties();
	    props.put("mail.smtp.auth", "true");
	    props.put("mail.smtp.starttls.enable", "true");
	    props.put("mail.smtp.host", "smtp.gmail.com");
	    props.put("mail.smtp.port", "587");

	    Session session = Session.getInstance(props,
	      new javax.mail.Authenticator() {
	        protected PasswordAuthentication getPasswordAuthentication() {
	            return new PasswordAuthentication(username, password);
	        }
	      });

	    String unsubscribeToken = "";
	    try {
			// prepare UNSUBSCRIBE directive
			unsubscribeToken = campaign.getCampaignId() + "-" + UUID.randomUUID().toString().replace("-", "");
			String unsubscribeURL = "http://" + getAppFactory().getServerIp() + ":" + getAppFactory().getServerPort()
					+ "/emailing/trigger3/unsubscribe?token=" + unsubscribeToken;
			String unsubscribeDirective = "<a href = \"" + unsubscribeURL + "\">unsubscribe</a>";

			emailContent = emailContent.replaceAll(this.unsubscribeTag, unsubscribeDirective);
			
			//Create unique message id
			String messageId = UUID.randomUUID().toString().replace("-", "") + "_" + new Date().getTime();
			
			String serverUrl = "http://" + getAppFactory().getServerIp() + ":" + getAppFactory().getServerPort();
//			String imageTag = "<img src='" + serverUrl + "/assets/img/profile.jpg' style='height: 13%; width: 35%;' width='42' />";
			String imageTag = "<img src='" + serverUrl + "/emailing/trackTrigger3Email?token=" + unsubscribeToken + "' style='display:none; height: 13%; width: 35%;' width='42' />";
			emailContent = emailContent.replaceAll(this.serverUrlTag, imageTag);
			
			//Create analytic url
			String analyticUrlTag = "<img src='https://www.google-analytics.com/collect?v=1&tid=" + campaignGmailCredential.getGmailCredential().getAnalyticsId() + "&cid=" + messageId + "&t=event&ec=email&ea=open&el='"+ unsubscribeToken +"'&cs=newsletter&cm=email&cn=test&dp=%2Femail%2Fnewsletter1&dt=My%20Newsletter' />";
			emailContent = emailContent.replaceAll(this.analyticUrlTag, analyticUrlTag);

	        Message message = new MimeMessage(session);
	        message.setFrom(new InternetAddress(from));
	        message.addRecipient(Message.RecipientType.TO,  new InternetAddress(to));
	        message.setSubject(emailSubject);
	        message.setContent(emailContent, "text/html");

	        Transport.send(message);

	        logger.info("Sent message successfully.... trigger3");
			
			if (messageId != null && messageId.trim().isEmpty() == false) {
				//Create track send email object
				GmailTrigger3Data trackGmailMail = new GmailTrigger3Data(0, messageId, subject.getSubjectId(), new Date(), from, to, email.getEmailTemplate().getEmailTemplateId(), sentEmailIndex, "");
				//Sent email success and unsubscribeToken
				trackGmailMail.setSent3(true);
				trackGmailMail.setUnsubscribeToken(unsubscribeToken);
				return Optional.of(trackGmailMail);
			} else {
				return Optional.empty();
			}
			
		} catch (NoSuchProviderException e) {
			logger.error("method trigger3 error : ", e);
	        //Create track send email object
			GmailTrigger3Data trackGmailMail = new GmailTrigger3Data(0, null, subject.getSubjectId(), new Date(), from, to, email.getEmailTemplate().getEmailTemplateId(), sentEmailIndex, e.getMessage());
			//Sent email fail and unsubscribeToken
			trackGmailMail.setSent3(false);
			trackGmailMail.setUnsubscribeToken(unsubscribeToken);
			return Optional.of(trackGmailMail);
		} catch (MessagingException e) {
			logger.error("method trigger3 error : ", e);
			//Create track send email object
			GmailTrigger3Data trackGmailMail = new GmailTrigger3Data(0, null, subject.getSubjectId(), new Date(), from, to, email.getEmailTemplate().getEmailTemplateId(), sentEmailIndex, e.getMessage());
			//Sent email fail and unsubscribeToken
			trackGmailMail.setSent3(false);
			trackGmailMail.setUnsubscribeToken(unsubscribeToken);
			return Optional.of(trackGmailMail);
		} catch (Exception e) {
			logger.error("method trigger3 error : ", e);
			//Create track send email object
			GmailTrigger3Data trackGmailMail = new GmailTrigger3Data(0, null, subject.getSubjectId(), new Date(), from, to, email.getEmailTemplate().getEmailTemplateId(), sentEmailIndex, e.getMessage());
			//Sent email fail and unsubscribeToken
			trackGmailMail.setSent3(false);
			trackGmailMail.setUnsubscribeToken(unsubscribeToken);
			return Optional.of(trackGmailMail);
		}

	}

}
