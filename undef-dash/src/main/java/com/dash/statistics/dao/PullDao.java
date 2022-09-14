package com.dash.statistics.dao;

import java.util.List;
import java.util.Optional;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dash.model.Campaign;
import com.dash.model.EmailServer;
import com.dash.statistics.model.Pull;

public class PullDao {
	private static final Logger logger = LoggerFactory.getLogger(PullDao.class);

	public static void savePull(Session session, Pull pull) {
		try {
			session.beginTransaction();
			session.save(pull);
			session.getTransaction().commit();
		} catch (Exception ex) {
			logger.error("method savePull error : ", ex);
		}
	}

	public static Optional<Pull> getLastPullPerCampaign(Session session, Campaign campaign) {
		Transaction tx = session.beginTransaction();
		try {

			String req = "select top 1 * from pull where campaignid = " + campaign.getCampaignId()
					+ " order by date desc ";
			SQLQuery query = session.createSQLQuery(req);
			query.addEntity(Pull.class);
			List<Pull> listData = query.list();
			if (listData == null || listData.size() == 0)
				return Optional.empty();
			else
				return Optional.of(listData.get(0));

		} catch (Exception ex) {
			logger.error("method getLastPullPerCampaign error : ", ex);
			return Optional.empty();
		} finally {
			tx.commit();
		}
	}

}
