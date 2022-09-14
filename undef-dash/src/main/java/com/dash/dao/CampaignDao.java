package com.dash.dao;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dash.model.Campaign;

@Transactional
public class CampaignDao {

	private static final Logger logger = LoggerFactory.getLogger(CampaignDao.class);

	public static void saveCampaign(Session session, Campaign campaign) {
		try {
			session.beginTransaction();
			session.save(campaign);
			session.getTransaction().commit();
		} catch (Exception ex) {
			logger.error("method saveCampaign error : ", ex);
		}
	}

	public static void updateCampaign(Session session, Campaign campaign) {
		try {
			session.beginTransaction();
			session.update(campaign);
			session.getTransaction().commit();
		} catch (Exception ex) {
			logger.error("method updateCampaign error : ", ex);
		}
	}

	public static Optional<Campaign> getCampaignById(Session session, long campaignId) {
		try {
			String req = "SELECT  * FROM CAMPAIGN where deleted = false and campaignId= " + campaignId;
			SQLQuery query = session.createSQLQuery(req);
			query.addEntity(Campaign.class);
			List<Campaign> listData = query.list();
			if (listData == null || listData.size() == 0)
				return Optional.empty();
			else
				return Optional.of(listData.get(0));

		} catch (Exception ex) {
			logger.error("method getCampaignById error : ", ex);
			return Optional.empty();
		} finally {
		}
	}

	public static Optional<Campaign> getCampaignbyIdAndAccount(Session session, long campaignId, int accountId) {
		try {
			String req = "SELECT  * FROM CAMPAIGN where deleted = false and campaignId= " + campaignId
					+ " and accountid = " + accountId;

			SQLQuery query = session.createSQLQuery(req);
			query.addEntity(Campaign.class);
			List<Campaign> listData = query.list();
			if (listData == null || listData.size() == 0)
				return Optional.empty();
			else
				return Optional.of(listData.get(0));

		} catch (Exception ex) {
			logger.error("method getCampaignbyIdAndAccount error : ", ex);
			return Optional.empty();
		} finally {
		}
	}

	public static Optional<List<Campaign>> getListCampaignsPerAccount(Session session, long accountId) {
		try {
			String req = "SELECT  * FROM Campaign where deleted = false and accountid= " + accountId
					+ " order by campaignId desc";
			SQLQuery query = session.createSQLQuery(req);
			query.addEntity(Campaign.class);
			List<Campaign> listData = query.list();
			if (listData == null || listData.size() == 0)
				return Optional.empty();
			else
				return Optional.of(listData);

		} catch (Exception ex) {
			logger.error("method getListCampaignsPerAccount error : ", ex);
			return Optional.empty();
		} finally {
		}
	}

	public static Optional<List<Campaign>> getListCampaignsFromAllAccounts(Session session) {
		try {
			String req = "select  * from campaign where deleted = false";
			SQLQuery query = session.createSQLQuery(req);
			query.addEntity(Campaign.class);

			List<Campaign> listData = query.list();
			return Optional.ofNullable(listData);

		} catch (Exception ex) {
			logger.error("method getListCampaignsFromAllAccounts error : ", ex);
			return Optional.empty();
		} finally {
		}
	}

}
