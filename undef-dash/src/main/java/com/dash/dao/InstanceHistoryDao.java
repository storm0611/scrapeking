package com.dash.dao;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dash.model.InstanceHistory;

@Transactional
public class InstanceHistoryDao {

	private static final Logger logger = LoggerFactory.getLogger(InstanceHistoryDao.class);

	public static void saveInstanceHistory(Session session, InstanceHistory instanceHistory) {
		try {
			session.beginTransaction();
			session.save(instanceHistory);
			session.getTransaction().commit();
		} catch (Exception ex) {
			logger.error("method saveInstanceHistory error : ", ex);
		}
	}

	public static Optional<List<InstanceHistory>> getInstanceListHistory(Session session, long instanceId) {
		try {

			String req = "select * from instancehistory where instanceid = " + instanceId;
			SQLQuery query = session.createSQLQuery(req);
			query.addEntity(InstanceHistory.class);
			List<InstanceHistory> listData = query.list();
			if (listData == null || listData.size() == 0)
				return Optional.empty();
			else
				return Optional.of(listData);

		} catch (Exception ex) {
			logger.error("method getInstanceListHistory error : ", ex);
			return Optional.empty();
		} finally {
		}
	}

}
