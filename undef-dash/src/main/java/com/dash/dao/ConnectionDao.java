package com.dash.dao;

import java.util.Optional;

import javax.transaction.Transactional;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dash.model.Account;
import com.dash.model.Connection;
import com.dash.undefdash.ApplicationFactory;

@Transactional
public class ConnectionDao {

	private static final Logger logger = LoggerFactory.getLogger(ApplicationFactory.class);

	public static void saveConnection(Session session, Connection connection) {
		try {
			session.beginTransaction();
			session.save(connection);
			session.getTransaction().commit();
		} catch (Exception ex) {
			logger.error("method saveConnection error : ", ex);
		}
	}

	public static void updateConnection(Session session, Connection connection) {
		try {
			session.beginTransaction();
			session.update(connection);
			session.getTransaction().commit();
		} catch (Exception ex) {
			logger.error("method updateConnection error : ", ex);
		}
	}

	public static Optional<Connection> getPastConnection(Session session, Account account) {
		try {

			String req = "SELECT TOP 1 * FROM CONNECTION WHERE ACCOUNTID = " + account.getAccountId()
					+ " order by connectionid desc";
			SQLQuery query = session.createSQLQuery(req);
			query.addEntity(Connection.class);

			Connection connection = (Connection) query.uniqueResult();
			if (connection == null)
				return Optional.empty();
			else
				return Optional.of(connection);

		} catch (Exception ex) {
			logger.error("method getPastConnection error : ", ex);
			return Optional.empty();
		}  finally {
		}
	}

}
