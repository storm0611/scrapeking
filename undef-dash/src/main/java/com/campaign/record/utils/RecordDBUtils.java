package com.campaign.record.utils;

import java.util.HashMap;
import java.util.Optional;
import java.util.Properties;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.campaign.record.model.Record;
import com.dash.model.Campaign;

public class RecordDBUtils {

	private static final Logger logger = LoggerFactory.getLogger(RecordDBUtils.class);

	// singleton
	private static HashMap<Long, SessionFactory> mapSessionFactory = new HashMap<Long, SessionFactory>();

	public static Optional<Session> getSession(Campaign campaign) {

		try {
			if (mapSessionFactory.containsKey(campaign.getCampaignId()) == false) {

				Properties properties = new Properties();
				properties.setProperty("hibernate.connection.url", campaign.getRecordsDBConnectionURL());
				properties.setProperty("hibernate.connection.username", campaign.getUsername());
				properties.setProperty("hibernate.connection.password", campaign.getPassword());
				properties.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
				properties.setProperty("show_sql", "true");
				properties.setProperty("hibernate.hbm2ddl.auto", "update");
				properties.setProperty("hibernate.current_session_context_class", "thread");

				Configuration configuration = new Configuration();
				configuration.setProperties(properties);

				configuration.addAnnotatedClass(Record.class);
				
			    ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(

			            configuration.getProperties()).build();

			    SessionFactory sessionFactory = configuration.buildSessionFactory(serviceRegistry);
			    sessionFactory.openSession();
			   
				mapSessionFactory.put(campaign.getCampaignId(), sessionFactory);

			}

			return Optional.of(mapSessionFactory.get(campaign.getCampaignId()).openSession());

		} catch (Exception ex) {
			logger.error("error : ", ex);
			return Optional.empty();
		}

	}

}
