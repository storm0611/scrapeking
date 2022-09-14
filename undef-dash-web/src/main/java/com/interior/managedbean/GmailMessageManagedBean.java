package com.interior.managedbean;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.faces.annotation.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.interior.api.dao.GmailCredentialDao;
import com.interior.api.model.Response;
import com.interior.api.model.Status;
import com.interior.model.GmailCredential;

@Named
@ViewScoped
public class GmailMessageManagedBean implements Serializable {

	private static final long serialVersionUID = 1L;
	final static Logger logger = Logger.getLogger(GmailMessageManagedBean.class);

	public static final String GMAIL_INBOX_PAGE_REDIRECT = "gmail-inbox.xhtml";

	@Inject
	@ManagedProperty("#{applicationManagedBean}")
	private ApplicationManagedBean applicationManagedBean;

	@Inject
	@ManagedProperty("#{sessionManagedBean}")
	private SessionManagedBean sessionManagedBean;
	
	private String htmlString;
	
	private String from;
	
	private String to;
	
	private String subject;
	
	private String replyText;
	
	private String messageId;
	
	private String credentialId;
	
	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public String getCredentialId() {
		return credentialId;
	}

	public void setCredentialId(String credentialId) {
		this.credentialId = credentialId;
	}

	public String getReplyText() {
		return replyText;
	}

	public void setReplyText(String replyText) {
		this.replyText = replyText;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public ApplicationManagedBean getApplicationManagedBean() {
		return applicationManagedBean;
	}

	public void setApplicationManagedBean(ApplicationManagedBean applicationManagedBean) {
		this.applicationManagedBean = applicationManagedBean;
	}

	public SessionManagedBean getSessionManagedBean() {
		return sessionManagedBean;
	}

	public void setSessionManagedBean(SessionManagedBean sessionManagedBean) {
		this.sessionManagedBean = sessionManagedBean;
	}


    public String getHtmlString() {
		return htmlString;
	}

	public void setHtmlString(String htmlString) {
		this.htmlString = htmlString;
	}

	@PostConstruct
	public void init() {
		try {
			
			HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext()
			        .getRequest();

			String messageIds = request.getParameter("messageId");
			String array[] = messageIds.split("-");
			this.messageId = array[0];
			this.credentialId = array[1];
			
			// load gmail credentials
			Response<GmailCredential> respListGmailCredential = GmailCredentialDao.getListGmailCredentials();
			Status responseStatus = respListGmailCredential.getStatus();
			if (responseStatus.equals(Status.SUCCESS)) {
				List<GmailCredential> result = respListGmailCredential.getData().stream()                // convert list to stream
		                .filter(line -> String.valueOf(line.getGmailCredentialId()).equals(this.credentialId))
		                .collect(Collectors.toList()); 
			
				Properties props = new Properties();
				props.setProperty("mail.store.protocol", "imaps");
				Session session = Session.getDefaultInstance(props, null);
		
				Store store = session.getStore("imaps");
				store.connect("smtp.gmail.com", result.get(0).getUsername(), result.get(0).getPassword());
	
				// create the folder object and open it
				Folder emailFolder = store.getFolder("INBOX");
				emailFolder.open(Folder.READ_WRITE);
				Message message = emailFolder.getMessage(Integer.parseInt(this.messageId));
				message.setFlag(Flags.Flag.SEEN, true);
				writePart(message);
			}
			

		} catch (Exception ex) {
			logger.error("method init error : ", ex);
		}
	}
	
	/*
	 * This method checks for content-type 
	 * based on which, it processes and
	 * fetches the content of the message
	 */
	public void writePart(Part p) throws Exception {
	      if (p instanceof Message)
	         //Call methos writeEnvelope
	         writeEnvelope((Message) p);
	
	      logger.info("----------------------------");
	      logger.info("CONTENT-TYPE: " + p.getContentType());
	
	      //check if the content is plain text
	      if (p.isMimeType("text/plain")) {
	         logger.info("This is plain text");
	         logger.info("---------------------------");
	         logger.info((String) p.getContent());
	      } 
	      //check if the content has attachment
	      else if (p.isMimeType("multipart/*")) {
	         logger.info("This is a Multipart");
	         logger.info("---------------------------");
	         Multipart mp = (Multipart) p.getContent();
	         int count = mp.getCount();
	         for (int i = 0; i < count; i++)
	            writePart(mp.getBodyPart(i));
	      } 
	      //check if the content is a nested message
	      else if (p.isMimeType("message/rfc822")) {
	         logger.info("This is a Nested Message");
	         logger.info("---------------------------");
	         writePart((Part) p.getContent());
	      } 
	      //check if the content is an inline image
	      else if (p.isMimeType("image/jpeg")) {
	          logger.info("--------> image/jpeg");
	          Object o = p.getContent();

	          InputStream x = (InputStream) o;
	          // Construct the required byte array
	          logger.info("x.length = " + x.available());
	          int i;
			byte[] bArray = null;
			while ((i = (int) ((InputStream) x).available()) > 0) {
	             int result = (int) (((InputStream) x).read(bArray));
	             if (result == -1)
	           i = 0;
	          bArray = new byte[x.available()];

	             break;
	          }
	          FileOutputStream f2 = new FileOutputStream("/tmp/image.jpg");
	          f2.write(bArray);
	      } 
	      else if (p.getContentType().contains("image/")) {
	         logger.info("content type" + p.getContentType());
	         File f = new File("image" + new Date().getTime() + ".jpg");
	         DataOutputStream output = new DataOutputStream(
	            new BufferedOutputStream(new FileOutputStream(f)));
	            com.sun.mail.util.BASE64DecoderStream test = 
	                 (com.sun.mail.util.BASE64DecoderStream) p
	                  .getContent();
	         byte[] buffer = new byte[1024];
	         int bytesRead;
	         while ((bytesRead = test.read(buffer)) != -1) {
	            output.write(buffer, 0, bytesRead);
	         }
	      } 
	      else {
	         Object o = p.getContent();
	         if (o instanceof String) {
	            logger.info("This is a string");
	            logger.info("---------------------------");
	            logger.info("htmlString :- " +  (String) o);
	            this.htmlString = (String) o;
	         } 
	         else if (o instanceof InputStream) {
	            logger.info("This is just an input stream");
	            logger.info("---------------------------");
	            InputStream is = (InputStream) o;
	            is = (InputStream) o;
	            int c;
	            while ((c = is.read()) != -1)
	               System.out.write(c);
	         } 
	         else {
	            logger.info("This is an unknown type");
	            logger.info("---------------------------");
	            logger.info(o.toString());
	         }
	      }

	}
	/*
	 * This method would print FROM,TO and SUBJECT of the message
	 */
	public void writeEnvelope(Message m) throws Exception {
	      logger.info("This is the message envelope");
	      logger.info("---------------------------");
	      Address[] a;
	
	      // FROM
	      if ((a = m.getFrom()) != null) {
	         for (int j = 0; j < a.length; j++) {
		         logger.info("FROM: " + a[j].toString());
		         this.from = a[j].toString();
	         }
	      }
	
	      // TO
	      if ((a = m.getRecipients(Message.RecipientType.TO)) != null) {
	         for (int j = 0; j < a.length; j++) {
		         logger.info("TO: " + a[j].toString());
		         this.to = a[j].toString();
	         }
	      }
	
	      // SUBJECT
	      if (m.getSubject() != null) {
	         logger.info("SUBJECT: " + m.getSubject());
	         this.subject = m.getSubject();
	      }
	}
	
	public void btnSentMail() {
		try {
			
			// load gmail credentials
			Response<GmailCredential> respListGmailCredential = GmailCredentialDao.getListGmailCredentials();
			Status responseStatus = respListGmailCredential.getStatus();
			if (responseStatus.equals(Status.SUCCESS)) {
				List<GmailCredential> result = respListGmailCredential.getData().stream()                // convert list to stream
		                .filter(line -> String.valueOf(line.getGmailCredentialId()).equals(this.credentialId)) 
		                .collect(Collectors.toList()); 
			
				final String username = result.get(0).getUsername();
			    final String password = result.get(0).getPassword();

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

			    try {

			        Message message = new MimeMessage(session);
			        message.setFrom(new InternetAddress(this.from));
			        message.addRecipient(Message.RecipientType.TO,  new InternetAddress(this.to));
			        message.setSubject(this.subject);
			        message.setContent(this.replyText, "text/html");

			        Transport.send(message);

			        logger.info("message replied successfully ....");

			    } catch (MessagingException e) {
			        throw new RuntimeException(e);
			    }

			}
			

		} catch (Exception ex) {
			logger.error("method init error : ", ex);
		}
	}
	
}
