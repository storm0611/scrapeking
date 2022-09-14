package com.dash.statistics.dao;

import java.math.BigInteger;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StatisticsDao {
	private static final Logger logger = LoggerFactory.getLogger(StatisticsDao.class);

	public static Integer getSubjectsCount(Session session) {
		Transaction tx = session.beginTransaction();
		try {
			String req = "select count(*) from subject";
			SQLQuery query = session.createSQLQuery(req);
			BigInteger count = (BigInteger) query.uniqueResult();
			if (count != null)
				return count.intValue();
			else
				return 1;
		} catch (Exception ex) {
			logger.error("method getSubjectsCount error : ", ex);
			return -1;
		} finally {
			tx.commit();
		}
	}

}
