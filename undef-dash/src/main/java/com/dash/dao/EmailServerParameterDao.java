package com.dash.dao;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dash.model.EmailServerParameter;
import com.dash.undefdash.ApplicationFactory;

@Transactional
public class EmailServerParameterDao {

	private static final Logger logger = LoggerFactory.getLogger(ApplicationFactory.class);

	public static void saveEmailServerParameter(Session session, EmailServerParameter emailServerParameter) {
		try {
			session.beginTransaction();
			session.save(emailServerParameter);
			session.getTransaction().commit();
		} catch (Exception ex) {
			logger.error("method saveEmailServerParameter error : ", ex);
		}
	}

	public static void updateEmailServerParameter(Session session, EmailServerParameter emailServerParameter) {
		try {
			session.beginTransaction();
			session.update(emailServerParameter);
			session.getTransaction().commit();
		} catch (Exception ex) {
			logger.error("method updateEmailServerParameter error : ", ex);
		}
	}

	public static Optional<EmailServerParameter> getEmailServerParameterByKeyAndEmailServer(Session session, String key,
			int emailServerId) {
		try {

			String req = "SELECT  * FROM emailServerParameter WHERE key like '" + key + "'  and emailServerId="
					+ emailServerId;
			SQLQuery query = session.createSQLQuery(req);
			query.addEntity(EmailServerParameter.class);

			List<EmailServerParameter> listData = query.list();
			if (listData == null || listData.size() == 0)
				return Optional.empty();
			else
				return Optional.of(listData.get(0));

		} catch (Exception ex) {
			logger.error("method getEmailServerParameterByKeyAndEmailServer error : ", ex);
			return Optional.empty();
		} finally {
		}
	}

}
