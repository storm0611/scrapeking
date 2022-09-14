package com.dash.dao;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dash.model.CampaignEmailServer;
import com.dash.model.CampaignGmailCredential;

@Transactional
public class CampaignGmailCredentialDao {

	private static final Logger logger = LoggerFactory.getLogger(CampaignGmailCredentialDao.class);

	public static void saveGmailCredentialServer(Session session, CampaignGmailCredential campaignGmailCredential) {
		try {
			session.beginTransaction();
			session.save(campaignGmailCredential);
			session.getTransaction().commit();
		} catch (Exception ex) {
			logger.error("method saveGmailCredentialServer error : ", ex);
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

	public static Optional<CampaignGmailCredential> getCampaignGmailCredentialByIds(Session session, long campaignId,
			int emailServerId) {
		try {
			String req = "SELECT  * FROM CampaignGmailCredential where campaignId= " + campaignId + " and emailServerId="
					+ emailServerId;
			
			SQLQuery query = session.createSQLQuery(req);
			query.addEntity(CampaignGmailCredential.class);
			List<CampaignGmailCredential> listData = query.list();
			if (listData == null || listData.size() == 0)
				return Optional.empty();
			else
				return Optional.of(listData.get(0));
			
		} catch (Exception ex) {
			logger.error("method getCampaignGmailCredentialByIds error : ", ex);
			return Optional.empty();
		}
	}
	
	public static Optional<List<CampaignGmailCredential>> getCampaignGmailCredentialsByCampaignId(Session session, long campaignId) {
		try {
			String req = "SELECT  * FROM CampaignGmailCredential where campaignId= " + campaignId;

			SQLQuery query = session.createSQLQuery(req);
			query.addEntity(CampaignGmailCredential.class);
			List<CampaignGmailCredential> listData = query.list();
			if (listData == null || listData.size() == 0)
				return Optional.empty();
			else
				return Optional.of(listData);

		} catch (Exception ex) {
			logger.error("method getCampaignGmailCredentialsByCampaignId error : ", ex);
			return Optional.empty();
		}
	}

	public static long getPerDayTotalCountByCampaign(Session session, long campaignId) {
		try {
			String sumHql = "SELECT SUM(cgc.gmailCredential.perDay) FROM CampaignGmailCredential cgc where campaignId= " + campaignId;
			Query sumQuery = session.createQuery(sumHql);
			long perDayTotalCount = (long) sumQuery.list().get(0);
			return perDayTotalCount;
			
		} catch (Exception ex) {
			logger.error("method getPerDayTotalCountByCampaign error : ", ex);
			return 0;
		}
	}

}
