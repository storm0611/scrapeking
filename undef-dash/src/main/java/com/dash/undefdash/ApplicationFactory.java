package com.dash.undefdash;

import java.util.ArrayList;
import java.util.Properties;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.ApplicationScope;

import com.dash.model.Account;
import com.dash.model.AccountGmailCredential;
import com.dash.model.AccountSource;
import com.dash.model.Campaign;
import com.dash.model.CampaignEmailServer;
import com.dash.model.CampaignGmailCredential;
import com.dash.model.Connection;
import com.dash.model.Email;
import com.dash.model.EmailServer;
import com.dash.model.EmailServerParameter;
import com.dash.model.EmailService;
import com.dash.model.EmailTemplate;
import com.dash.model.GmailCredential;
import com.dash.model.Instance;
import com.dash.model.InstanceHistory;
import com.dash.model.Log;
import com.dash.model.LoginAttempt;
import com.dash.model.Role;
import com.dash.model.Source;
import com.dash.model.Task;
import com.dash.model.TaskHistory;
import com.dash.model.TaskParameter;
import com.dash.statistics.model.Figure;
import com.dash.statistics.model.Pull;

@Component
@ApplicationScope
public class ApplicationFactory {

	private static final Logger logger = LoggerFactory.getLogger(ApplicationFactory.class);

	private ArrayList<AuthorizedSource> listAuthorizedSource;

	// directory for H2 databases , the same directly will be exposed when
	// creating/starting h2 in TCP mode
	public String databaseDirectoryPath;
	private SessionFactory sessionFactory;

	// used when building UNSUBSCRIBE URLs for Emailing
	@Value("${server.ip}")
	private String serverIp;
	@Value("${server.port}")
	private int serverPort;

	@Value("${db.mode}")
	private String mode;

	public ApplicationFactory() {
		this.databaseDirectoryPath = System.getProperty("user.dir") + System.getProperty("file.separator") + "database";
		this.listAuthorizedSource = new ArrayList<AuthorizedSource>();
	}

	public ArrayList<AuthorizedSource> getListAuthorizedSource() {
		return listAuthorizedSource;
	}

	public void setListAuthorizedSource(ArrayList<AuthorizedSource> listAuthorizedSource) {
		this.listAuthorizedSource = listAuthorizedSource;
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public String getServerIp() {
		return serverIp;
	}

	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}

	public int getServerPort() {
		return serverPort;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getDatabaseDirectoryPath() {
		return databaseDirectoryPath;
	}

	public void setDatabaseDirectoryPath(String databaseDirectoryPath) {
		this.databaseDirectoryPath = databaseDirectoryPath;
	}

	public Session getSession() {
		if (sessionFactory == null)
			initSessionFactory();
		return sessionFactory.openSession();
	}

	private void initSessionFactory() {
		try {

			String connection_url = "jdbc:h2:file:" + System.getProperty("user.dir")
					+ System.getProperty("file.separator") + "localdb"
					+ ";TRACE_LEVEL_FILE=0;AUTO_SERVER=TRUE;DB_CLOSE_ON_EXIT=FALSE";

			logger.info(connection_url);

			Properties properties = new Properties();
			properties.setProperty("hibernate.connection.url", connection_url);
			properties.setProperty("hibernate.connection.username", "client");
			properties.setProperty("hibernate.connection.password", "1234");
			properties.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
			properties.setProperty("show_sql", "true");
			properties.setProperty("hibernate.hbm2ddl.auto", mode);
			properties.setProperty("hibernate.c3p0.min_size", "5");
			properties.setProperty("hibernate.c3p0.max_size", "30");
			properties.setProperty("hibernate.c3p0.timeout", "7000");
			properties.setProperty("hibernate.current_session_context_class", "thread");
			
			Configuration configuration = new Configuration();
			configuration.setProperties(properties);

			configuration.addAnnotatedClass(Account.class);
			configuration.addAnnotatedClass(Connection.class);
			configuration.addAnnotatedClass(Log.class);
			configuration.addAnnotatedClass(Role.class);
			configuration.addAnnotatedClass(LoginAttempt.class);
			configuration.addAnnotatedClass(Campaign.class);
			configuration.addAnnotatedClass(EmailTemplate.class);
			configuration.addAnnotatedClass(Email.class);
			configuration.addAnnotatedClass(CampaignEmailServer.class);
			configuration.addAnnotatedClass(EmailService.class);
			configuration.addAnnotatedClass(EmailServer.class);
			configuration.addAnnotatedClass(EmailServerParameter.class);
			configuration.addAnnotatedClass(Source.class);
			configuration.addAnnotatedClass(AccountSource.class);
			configuration.addAnnotatedClass(Instance.class);
			configuration.addAnnotatedClass(InstanceHistory.class);
			configuration.addAnnotatedClass(Task.class);
			configuration.addAnnotatedClass(TaskParameter.class);
			configuration.addAnnotatedClass(TaskHistory.class);
			configuration.addAnnotatedClass(Pull.class);
			configuration.addAnnotatedClass(Figure.class);
			configuration.addAnnotatedClass(AccountGmailCredential.class);
			configuration.addAnnotatedClass(GmailCredential.class);
			configuration.addAnnotatedClass(CampaignGmailCredential.class);
			
			//configuration.configure();
			
			ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(

		            configuration.getProperties()).build();

			this.sessionFactory = configuration.buildSessionFactory(serviceRegistry);
			this.sessionFactory.openSession();

//			this.sessionFactory = configuration.buildSessionFactory();

		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Error in creating sessionFactory : ", ex);
		}

	}

}
