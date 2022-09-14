package com.emailing.utils;

import java.util.Date;
import java.util.HashMap;
import java.util.Optional;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
//import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;
import javax.mail.search.FromTerm;
import javax.mail.search.SearchTerm;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dash.model.Campaign;
import com.emailing.model.Event;
import com.emailing.model.GmailTrigger1Data;
import com.emailing.model.GmailTrigger2Data;
import com.emailing.model.GmailTrigger3Data;
import com.emailing.model.Message;
import com.emailing.model.Subject;

public class EmailingDBUtils {

	private static final Logger logger = LoggerFactory.getLogger(EmailingDBUtils.class);
	
	// singleton
	private static HashMap<Long, SessionFactory> mapSessionFactory = new HashMap<Long, SessionFactory>();

	public static Optional<Session> getSession(Campaign campaign) {

		try {

			if (mapSessionFactory.containsKey(campaign.getCampaignId()) == false) {

				Properties properties = new Properties();
				properties.setProperty("hibernate.connection.url", campaign.getEmailingDBConnectionURL());
				properties.setProperty("hibernate.connection.username", campaign.getUsername());
				properties.setProperty("hibernate.connection.password", campaign.getPassword());
				properties.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
				properties.setProperty("show_sql", "true");
				properties.setProperty("hibernate.hbm2ddl.auto", "update");
				properties.setProperty("hibernate.current_session_context_class", "thread");

				Configuration configuration = new Configuration();
				configuration.setProperties(properties);

				configuration.addAnnotatedClass(Subject.class);
				configuration.addAnnotatedClass(Message.class);
				configuration.addAnnotatedClass(Event.class);
				configuration.addAnnotatedClass(GmailTrigger1Data.class);
				configuration.addAnnotatedClass(GmailTrigger2Data.class);
				configuration.addAnnotatedClass(GmailTrigger3Data.class);
				
				
				ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(

			            configuration.getProperties()).build();

				SessionFactory sessionFactory = configuration.buildSessionFactory(serviceRegistry);
			    sessionFactory.openSession();
			    
//				this.sessionFactory = configuration.buildSessionFactory(serviceRegistry);

				mapSessionFactory.put(campaign.getCampaignId(), sessionFactory);
			}

			return Optional.of(mapSessionFactory.get(campaign.getCampaignId()).openSession());

		} catch (Exception ex) {
			logger.error("error : ", ex);
			return Optional.empty();
		}

	}
	
	/**
	 * compare date to current date returns diffrece in days Long date format "dd-MM-yyyy HH:mm:ss"
	 */
	public static Long getDiffBetTimeInDays(Date registerDate) {
		
		try {
			
			// in milliseconds
			long diff = new Date().getTime() - registerDate.getTime();
			
			long diffDays = diff / (24 * 60 * 60 * 1000);
			return diffDays;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
		
	}
	
	public static boolean checkReply(String username, String password, String fromEmail) {
		 try {
			  Properties props = new Properties();
			  props.setProperty("mail.store.protocol", "imaps");
			  javax.mail.Session session = javax.mail.Session.getDefaultInstance(props, null);
	
			  Store store = session.getStore("imaps");
			  store.connect("smtp.gmail.com", username, password);
			  
			  Folder[] f = store.getDefaultFolder().list();
			  for(Folder fd:f)
			      System.out.println(">> "+fd.getName());
			  
			  Folder folder = store.getFolder("INBOX");
			  folder.open(Folder.READ_ONLY);
			  
			  SearchTerm searchTerm = new FromTerm(new InternetAddress(fromEmail));
		      javax.mail.Message[] messages = folder.search(searchTerm);
	
		      int length = messages.length;
		      // close the store and folder objects
		      folder.close(false);
		      store.close();
		      
		      if(length>0)
		    	  return true;
		      return false;
				
		} catch (NoSuchProviderException e) {
	         e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}
