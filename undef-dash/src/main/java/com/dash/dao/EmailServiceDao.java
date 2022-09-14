package com.dash.dao;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dash.model.EmailService;

@Transactional
public class EmailServiceDao {

	private static final Logger logger = LoggerFactory.getLogger(EmailServiceDao.class);

	public static void saveEmailService(Session session, EmailService emailService) {
		try {
			session.beginTransaction();
			session.save(emailService);
			session.getTransaction().commit();
		} catch (Exception ex) {
			logger.error("method saveEmailService error : ", ex);
		}
	}

	public static Optional<EmailService> getEmailServiceByName(Session session, String name) {
		try {

			String req = "SELECT  * FROM emailService where upper(name) like '" + name.toUpperCase() + "'";
			SQLQuery query = session.createSQLQuery(req);
			query.addEntity(EmailService.class);
			List<EmailService> listData = query.list();
			if (listData == null || listData.size() == 0)
				return Optional.empty();
			else
				return Optional.of(listData.get(0));

		} catch (Exception ex) {
			logger.error("method getEmailServiceByName error : ", ex);
			return Optional.empty();
		} finally {
		}
	}

	public static Optional<EmailService> getEmailServiceById(Session session, long id) {

		try {

			String req = "SELECT  * FROM emailService where emailServiceId= " + id;
			SQLQuery query = session.createSQLQuery(req);
			query.addEntity(EmailService.class);
			List<EmailService> listData = query.list();
			if (listData == null || listData.size() == 0)
				return Optional.empty();
			else
				return Optional.of(listData.get(0));

		} catch (Exception ex) {
			logger.error("method getEmailServiceById error : ", ex);
			return Optional.empty();
		} finally {

		}
	}

	public static Optional<List<EmailService>> getListEmailServices(Session session) {

		try {
			String req = "SELECT  * FROM emailservice ";
			SQLQuery query = session.createSQLQuery(req);
			query.addEntity(EmailService.class);
			List<EmailService> listData = query.list();
			if (listData == null || listData.size() == 0)
				return Optional.empty();
			else
				return Optional.of(listData);

		} catch (Exception ex) {
			logger.error("method getListEmailServices error : ", ex);
		} finally {

		}

		return Optional.empty();
	}

}
