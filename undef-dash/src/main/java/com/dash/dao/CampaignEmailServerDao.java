package com.dash.dao;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dash.model.CampaignEmailServer;

@Transactional
public class CampaignEmailServerDao {

	private static final Logger logger = LoggerFactory.getLogger(CampaignEmailServerDao.class);

	public static void saveCampaignEmailServer(Session session, CampaignEmailServer campaignEmailServer) {
		try {
			session.beginTransaction();
			session.save(campaignEmailServer);
			session.getTransaction().commit();
		} catch (Exception ex) {
			logger.error("method saveCampaignEmailServer error : ", ex);
		}
	}

	public static void deleteCampaignEmailServer(Session session, CampaignEmailServer campaignEmailServer) {
		try {
			session.beginTransaction();
			session.delete(campaignEmailServer);
			session.getTransaction().commit();
		} catch (Exception ex) {
			logger.error("method deleteCampaignEmailServer error : ", ex);
		}
	}

	public static Optional<CampaignEmailServer> getCampaignEmailServerByIds(Session session, long campaignId,
			long emailServerId) {
		try {
			String req = "SELECT  * FROM CampaingEmailServer where campaignId= " + campaignId + " and emailServerId="
					+ emailServerId;

			SQLQuery query = session.createSQLQuery(req);
			query.addEntity(CampaignEmailServer.class);
			List<CampaignEmailServer> listData = query.list();
			if (listData == null || listData.size() == 0)
				return Optional.empty();
			else
				return Optional.of(listData.get(0));

		} catch (Exception ex) {
			logger.error("method getCampaignEmailServerByIds error : ", ex);
			return Optional.empty();
		} finally {
		}
	}

}
