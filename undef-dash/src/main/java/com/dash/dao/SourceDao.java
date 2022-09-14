package com.dash.dao;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dash.model.Source;

@Transactional
public class SourceDao {

	private static final Logger logger = LoggerFactory.getLogger(SourceDao.class);

	public static void saveSource(Session session, Source source) {
		try {
			session.beginTransaction();
			session.save(source);
			session.getTransaction().commit();
		} catch (Exception ex) {
			logger.error("method saveSource error : ", ex);
		}
	}

	public static void updateSource(Session session, Source source) {
		try {
			session.beginTransaction();
			session.update(source);
			session.getTransaction().commit();
		} catch (Exception ex) {
			logger.error("method updateSource error : ", ex);
		}
	}

	public static Optional<Source> getSourceById(Session session, long sourceId) {
		try {
			String req = "select * from source where source.deleted = false and  sourceId = " + sourceId;
			SQLQuery query = session.createSQLQuery(req);
			query.addEntity(Source.class);
			List<Source> listData = query.list();
			if (listData == null || listData.size() == 0)
				return Optional.empty();
			else {
				Hibernate.initialize(listData.get(0).getListInstances());
				return Optional.of(listData.get(0));
			}

		} catch (Exception ex) {
			logger.error("method getSourceById error : ", ex);
			return Optional.empty();
		} finally {
		}

	}

	public static Optional<Source> getSourceByIdAndAccount(Session session, long sourceId, int accountId) {
		try {
			String req = "select source .*  from source , account_source where  source.deleted = false and source.sourceid = account_source.sourceid and account_source.accountid="
					+ accountId + " and source.sourceId=" + sourceId;
			SQLQuery query = session.createSQLQuery(req);
			query.addEntity(Source.class);
			List<Source> listData = query.list();
			if (listData == null || listData.size() == 0)
				return Optional.empty();
			else {
				Hibernate.initialize(listData.get(0).getListInstances());
				return Optional.of(listData.get(0));
			}

		} catch (Exception ex) {
			logger.error("method getSourceByIdAndAccount error : ", ex);
			return Optional.empty();
		} finally {
		}
	}

	public static Optional<List<Source>> getListSourcesPerAccount(Session session, long accountId) {
		try {
			String req = "SELECT SOURCE .*  FROM SOURCE , ACCOUNT_SOURCE where source.deleted = false and  source.sourceid = account_source.sourceid and account_source.accountid="
					+ accountId;
			SQLQuery query = session.createSQLQuery(req);
			query.addEntity(Source.class);
			List<Source> listData = query.list();
			if (listData == null || listData.size() == 0)
				return Optional.empty();
			else {
				for (Source source : listData)
					Hibernate.initialize(source.getListInstances());
				return Optional.of(listData);
			}

		} catch (Exception ex) {
			logger.error("method getListSourcesPerAccount error : ", ex);
			return Optional.empty();
		} finally {
		}
	}

}
