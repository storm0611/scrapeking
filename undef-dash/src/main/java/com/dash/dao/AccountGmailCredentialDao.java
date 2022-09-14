package com.dash.dao;

import java.util.Optional;

import javax.transaction.Transactional;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dash.model.AccountGmailCredential;
import com.dash.model.AccountSource;

@Transactional
public class AccountGmailCredentialDao {

	private static final Logger logger = LoggerFactory.getLogger(AccountGmailCredentialDao.class);

	public static void saveGmailCredential(Session session, AccountGmailCredential accountGmailCredential) {
		try {
			session.beginTransaction();
			session.save(accountGmailCredential);
			session.getTransaction().commit();
		} catch (Exception ex) {
			logger.error("method saveGmailCredential error : ", ex);
		}
	}

	public static Optional<AccountSource> getAccountGmailCredentialByAccountAndSource(Session session, int accountId,
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
			logger.error("method getAccountGmailCredentialByAccountAndSource error : ", ex);
			return Optional.empty();
		}
	}

}
