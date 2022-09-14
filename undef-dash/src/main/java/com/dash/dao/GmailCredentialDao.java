package com.dash.dao;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dash.model.GmailCredential;

@Transactional
public class GmailCredentialDao {

	private static final Logger logger = LoggerFactory.getLogger(GmailCredentialDao.class);

	public static void saveGmailCredential(Session session, GmailCredential gmailCredential) {
		try {
			session.beginTransaction();
			session.save(gmailCredential);
			session.getTransaction().commit();
		} catch (Exception ex) {
			logger.error("method saveGmailCredential error : ", ex);
		}
	}

	public static void updateGmailCredential(Session session, GmailCredential gmailCredential) {
		try {
			session.beginTransaction();
			session.update(gmailCredential);
			session.getTransaction().commit();
		} catch (Exception ex) {
			logger.error("method updateGmailCredential error : ", ex);
		}
	}

	public static Optional<GmailCredential> getGmailCredentialById(Session session, long gmailCredentialId) {
		
		try {
			String req = "select * from GMAIL_CREDENTIAL where gmail_credential.deleted = false and gmailcredentialid = " + gmailCredentialId;
			SQLQuery query = session.createSQLQuery(req);
			query.addEntity(GmailCredential.class);
			List<GmailCredential> listData = query.list();
			if (listData == null || listData.size() == 0)
				return Optional.empty();
			else {
				return Optional.of(listData.get(0));
			}

		} catch (Exception ex) {
			logger.error("method getGmailCredentialById error : ", ex);
			return Optional.empty();
		} finally {
			
		}

	}

	public static Optional<List<GmailCredential>> getListGmailCredentialsPerAccount(Session session, long accountId) {
		
		try {
			String req = "SELECT GMAIL_CREDENTIAL .*  FROM GMAIL_CREDENTIAL , ACCOUNT_GMAIL_CREDENTIAL where gmail_credential.deleted = false and  gmail_credential.gmailcredentialid = account_gmail_credential.gmailcredentialid and account_gmail_credential.accountid="
					+ accountId;
			SQLQuery query = session.createSQLQuery(req);
			query.addEntity(GmailCredential.class);
			List<GmailCredential> listData = query.list();
			if (listData == null || listData.size() == 0)
				return Optional.empty();
			else {
				return Optional.of(listData);
			}

		} catch (Exception ex) {
			logger.error("method getListGmailCredentialsPerAccount error : ", ex);
			return Optional.empty();
		} finally {
			
		}
	}

}
