package com.dash.dao;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dash.model.EmailServer;
import com.dash.undefdash.ApplicationFactory;

@Transactional
public class EmailServerDao {

	private static final Logger logger = LoggerFactory.getLogger(ApplicationFactory.class);

	public static void saveEmailServer(Session session, EmailServer emailServer) {
		try {
			session.beginTransaction();
			session.save(emailServer);
			session.getTransaction().commit();
		} catch (Exception ex) {
			logger.error("method saveEmailServer error : ", ex);
		}
	}

	public static void updateEmailServer(Session session, EmailServer emailServer) {
		try {
			session.beginTransaction();
			session.update(emailServer);
			session.getTransaction().commit();
		} catch (Exception ex) {
			logger.error("method updateEmailServer error : ", ex);
		}
	}

	public static Optional<List<EmailServer>> getListAllEmailServers(Session session) {
		try {

			String req = "SELECT  * FROM EMAILSERVER where deleted =false";
			SQLQuery query = session.createSQLQuery(req);
			query.addEntity(EmailServer.class);
			List<EmailServer> listData = query.list();
			if (listData == null || listData.size() == 0)
				return Optional.empty();
			else
				return Optional.of(listData);

		} catch (Exception ex) {
			logger.error("method getListAllEmailServers error : ", ex);
			return Optional.empty();
		} finally {
		}
	}

	public static Optional<EmailServer> getEmailServerById(Session session, long id) {
		try {

			String req = "SELECT  * FROM emailserver WHERE emailserverid= " + id + " and  deleted=false";
			SQLQuery query = session.createSQLQuery(req);
			query.addEntity(EmailServer.class);

			List<EmailServer> listData = query.list();
			if (listData == null || listData.size() == 0)
				return Optional.empty();
			else
				return Optional.of(listData.get(0));

		} catch (Exception ex) {
			logger.error("method getEmailServerById error : ", ex);
			return Optional.empty();
		} finally {
		}
	}

	public static Optional<List<EmailServer>> getListEmailServersByAccount(Session session, int accountId) {
		try {

			String req = "SELECT  * FROM EMAILSERVER where accountid =" + accountId + " and deleted =false";
			SQLQuery query = session.createSQLQuery(req);
			query.addEntity(EmailServer.class);
			List<EmailServer> listData = query.list();
			if (listData == null || listData.size() == 0)
				return Optional.empty();
			else
				return Optional.of(listData);

		} catch (Exception ex) {
			logger.error("method getListEmailServers error : ", ex);
			return Optional.empty();
		} finally {
		}
	}

}
