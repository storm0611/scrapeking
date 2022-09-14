package com.dash.dao;

import java.util.Optional;

import javax.transaction.Transactional;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dash.model.AccountSource;

@Transactional
public class AccountSourceDao {

	private static final Logger logger = LoggerFactory.getLogger(AccountSourceDao.class);

	public static void saveAccountSource(Session session, AccountSource accountSource) {
		try {
			session.beginTransaction();
			session.save(accountSource);
			session.getTransaction().commit();
		} catch (Exception ex) {
			logger.error("method saveAccountSource error : ", ex);
		}
	}

	public static Optional<AccountSource> getAccountSourceByAccountAndSource(Session session, int accountId,
			long sourceId) {
		try {

			String req = "select * from ACCOUNT_SOURCE  where accountid=" + accountId + " and sourceid=" + sourceId;
			SQLQuery query = session.createSQLQuery(req);
			query.addEntity(AccountSource.class);

			AccountSource ac = (AccountSource) query.uniqueResult();
			if (ac == null)
				return Optional.empty();
			else
				return Optional.of(ac);

		} catch (Exception ex) {
			logger.error("method getAccountSourceByAccountAndSource error : ", ex);
			return Optional.empty();
		} finally {
		}
	}

}
