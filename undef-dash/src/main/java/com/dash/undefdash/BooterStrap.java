package com.dash.undefdash;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.h2.tools.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.dash.dao.AccountDao;
import com.dash.dao.EmailServiceDao;
import com.dash.dao.RoleDao;
import com.dash.model.Account;
import com.dash.model.EmailService;
import com.dash.model.Role;
import com.emailing.providers.Sparkpost;

@Component
public class BooterStrap implements CommandLineRunner {
	private static final Logger logger = LoggerFactory.getLogger(BooterStrap.class);

	@Autowired
	private ApplicationFactory applicationFactory;

	@Override
	public void run(String... args) throws Exception {

		logger.info("starting application now");

		try {
			// start H2 Server
			Server server = Server.createTcpServer("-tcpPort", "9092", "-tcpAllowOthers");
			server.start();
		} catch (Exception ex) {
			if (ex.getCause().toString().contains("Address already in use: JVM_Bind") == false) {
				logger.error("cannot start h2 tcp server : ", ex);
				System.exit(0);
			}
		}

		// load authorized sources from a local CSV file within the application folder ,
		// the CSV file structure is
		// "application(String)","key(String)","forTest(boolean)"
		// check file "sources_authorization.csv" for a sample
		this.applicationFactory.setListAuthorizedSource(new ArrayList<AuthorizedSource>());
		String path = System.getProperty("user.dir") + System.getProperty("file.separator")
				+ "sources_authorization.csv";

		Reader reader = Files.newBufferedReader(Paths.get(path));
		CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withSkipHeaderRecord());
		for (CSVRecord csvRecord : csvParser) {
			this.applicationFactory.getListAuthorizedSource()
					.add(new AuthorizedSource(csvRecord.get(0), csvRecord.get(1), Boolean.valueOf(csvRecord.get(2))));
		}
		csvParser.close();
		reader.close();

		Account account = AccountDao.authenticate(applicationFactory.getSession(), "admin", "1234");
		if (account == null) {

			Role role = new Role();
			role.setLabel("ADMIN");
			RoleDao.saveRole(applicationFactory.getSession(), role);

			account = new Account();
			account.setLogin("admin");
			account.setPassword("1234");
			account.setListRoles(Arrays.asList(role));

			AccountDao.saveAccount(applicationFactory.getSession(), account);

			EmailService provider = new EmailService();
			provider.setName("sparkpost");
			provider.setCreated(new Date());
			provider.setParameters(new ArrayList<String>());
			for (String param : Sparkpost.listRequiredParameters)
				provider.getParameters().add(param);
			
			EmailServiceDao.saveEmailService(applicationFactory.getSession(), provider);
			
			EmailService provider1 = new EmailService();
			provider1.setName("gmail");
			provider1.setCreated(new Date());
			provider1.setParameters(new ArrayList<String>());
			for (String param : Sparkpost.listRequiredParameters)
				provider1.getParameters().add(param);

			EmailServiceDao.saveEmailService(applicationFactory.getSession(), provider1);

		}

	}

}
