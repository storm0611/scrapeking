package com.source.cache;

import java.io.File;
import java.util.List;
import java.util.Properties;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CacheUtils {

	private static final Logger logger = LoggerFactory.getLogger(CacheUtils.class);

	private String cacheName = "";
	private Session session;
	public String mode = "update";

	public String getCacheName() {
		return cacheName;
	}

	public void setCacheName(String cacheName) {
		this.cacheName = cacheName;
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public CacheUtils(String cacheName) {
		super();
		try {
			this.cacheName = cacheName;
			this.session = this.getSessionFactory().openSession();
		} catch (Exception ex) {
			logger.warn("unable to create/connect to cache database");
		}

	}

	public SessionFactory getSessionFactory() {
		try {

			File cacheFolder = new File(
					System.getProperty("user.dir") + System.getProperty("file.separator") + "cache");
			if (cacheFolder.exists() == false)
				cacheFolder.mkdir();

			Properties properties = new Properties();

			String dbURL = "jdbc:h2:file:" + cacheFolder.getAbsolutePath() + System.getProperty("file.separator")
					+ "cache-" + this.cacheName + ";TRACE_LEVEL_FILE=0;AUTO_SERVER=TRUE;DB_CLOSE_ON_EXIT=FALSE";

			properties.setProperty("hibernate.connection.url", dbURL);

			System.out.println(dbURL);

			properties.setProperty("hibernate.connection.username", "cacheclient");
			properties.setProperty("hibernate.connection.password", "1234");
			properties.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
			properties.setProperty("show_sql", "true");
			properties.setProperty("hibernate.hbm2ddl.auto", mode);

			Configuration configuration = new Configuration();
			configuration.setProperties(properties);

			configuration.addAnnotatedClass(Entry.class);

			return configuration.buildSessionFactory();

		} catch (Exception ex) {
			logger.error("Error in creating session factory : ", ex);
		}
		return null;
	}

	public void saveEntry(Entry entry) {
		try {
			if (session.getTransaction().isActive() == false)
				session.beginTransaction();

			session.save(entry);
			session.getTransaction().commit();

		} catch (Exception ex) {

			session.close();
			session = getSessionFactory().openSession();

			logger.error("Method : saveEntry : error : " + ex);
		}
	}

	public boolean checkIfEntryExist(String url) {
		try {
			String req = "SELECT * FROM ENTRY WHERE URL LIKE  '" + url + "'";
			SQLQuery query = session.createSQLQuery(req);
			query.addEntity(Entry.class);

			List<Entry> results = query.list();
			if (results != null && results.size() != 0)
				return true;
			else {
				return false;
			}

		} catch (Exception ex) {
			logger.error("method checkIfEntryExist error : ", ex);
			return false;
		}
	}

}
