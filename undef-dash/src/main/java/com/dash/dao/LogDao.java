package com.dash.dao;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dash.model.Connection;
import com.dash.model.Log;

@Transactional
public class LogDao {

	private static final Logger logger = LoggerFactory.getLogger(LogDao.class);

	public static void saveLog(Session session, Log log) {
		try {
			session.beginTransaction();
			session.save(log);
			session.getTransaction().commit();
		} catch (Exception ex) {
			logger.error("method saveLog error : ", ex);
		}
	}

	public static void updateLog(Session session, Log log) {
		try {
			session.beginTransaction();
			session.update(log);
			session.getTransaction().commit();
		} catch (Exception ex) {
			logger.error("method updateLog error : ", ex);
		}
	}

	public static Optional<List<Log>> getConnectionListLogs(Session session, Connection connection) {
		try {

			String req = "SELECT * FROM LOG WHERE CONNECTIONID = " + connection.getConnectionId();
			SQLQuery query = session.createSQLQuery(req);
			query.addEntity(Log.class);
			List<Log> listLogs = query.list();

			if (listLogs == null) {
				return Optional.empty();
			} else {
				return Optional.of(listLogs);
			}

		} catch (Exception ex) {
			logger.error("method getConnectionListLogs error : ", ex);
			return Optional.empty();
		} finally {
		}
	}

}
