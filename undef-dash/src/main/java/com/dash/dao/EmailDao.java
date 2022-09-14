package com.dash.dao;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dash.model.Email;

@Transactional
public class EmailDao {
	private static final Logger logger = LoggerFactory.getLogger(EmailDao.class);

	public static void saveEmail(Session session, Email email) {
		try {
			session.beginTransaction();
			session.save(email);
			session.getTransaction().commit();
		} catch (Exception ex) {
			logger.error("method saveEmail error : ", ex);
		}
	}

	public static void deleteEmail(Session session, Email email) {
		try {
			session.beginTransaction();
			session.delete(email);
			session.getTransaction().commit();
		} catch (Exception ex) {
			logger.error("method deleteEmail error : ", ex);
		}
	}

	public static Optional<List<Email>> getListEmailByCampaign(Session session, long campaignId) {
		try {

			String req = "select * from email where campaignId = " + campaignId;
			SQLQuery query = session.createSQLQuery(req);
			query.addEntity(Email.class);
			List<Email> listData = query.list();

			if (listData == null || listData.size() == 0)
				return Optional.empty();
			else
				return Optional.of(listData);

		} catch (Exception ex) {
			logger.error("method getListEmailByCampaign error : ", ex);
			return Optional.empty();
		} finally {
		}
	}

	public static Optional<Email> getEmailByRankAndCampaign(Session session, long campaignId, int rank) {
		try {

			String req = "select * from email where campaignId = " + campaignId + " and rank = " + rank;
			SQLQuery query = session.createSQLQuery(req);
			query.addEntity(Email.class);
			List<Email> listData = query.list();

			if (listData == null || listData.size() == 0)
				return Optional.empty();
			else
				return Optional.of(listData.get(0));

		} catch (Exception ex) {
			logger.error("method getEmailByRankAndCampaign error : ", ex);
			return Optional.empty();
		} finally {
		}
	}

}
