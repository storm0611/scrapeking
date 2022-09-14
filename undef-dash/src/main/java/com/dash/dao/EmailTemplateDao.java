package com.dash.dao;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dash.model.EmailTemplate;

@Transactional
public class EmailTemplateDao {

	private static final Logger logger = LoggerFactory.getLogger(EmailTemplateDao.class);

	public static void saveEmailTemplate(Session session, EmailTemplate emailTemplate) {
		try {
			session.beginTransaction();
			session.save(emailTemplate);
			session.getTransaction().commit();
		} catch (Exception ex) {
			logger.error("method saveEmailTemplate error : ", ex);
		}
	}

	public static void deleteEmailTemplate(Session session, EmailTemplate emailTemplate) {
		try {
			session.beginTransaction();
			session.delete(emailTemplate);
			session.getTransaction().commit();
		} catch (Exception ex) {
			logger.error("method deleteEmailTemplate error : ", ex);
		}
	}

	public static void updateEmailTemplate(Session session, EmailTemplate emailTemplate) {
		try {
			session.beginTransaction();
			session.update(emailTemplate);
			session.getTransaction().commit();
		} catch (Exception ex) {
			logger.error("method updateEmailTemplate error : ", ex);
		}
	}

	public static Optional<List<EmailTemplate>> getListEmailTemplateByAccount(Session session, int accountId) {
		
		try {

			String req = "select * from emailTemplate where deleted= false and  accountid= " + accountId;
			SQLQuery query = session.createSQLQuery(req);
			query.addEntity(EmailTemplate.class);
			List<EmailTemplate> listData = query.list();
			if (listData == null || listData.size() == 0)
				return Optional.empty();
			else
				return Optional.of(listData);

		} catch (Exception ex) {
			logger.error("method getListEmailTemplateByAccount error : ", ex);
			return Optional.empty();
		} finally {
			
		}
	}

	public static Optional<EmailTemplate> getEmailTemplateByIdAndAccount(Session session, long emailTemplateId,
			long accountId) {
		
		try {
			String req = "select * from emailtemplate where  deleted=false and emailTemplateId=" + emailTemplateId
					+ " and accountId = " + accountId;
			SQLQuery query = session.createSQLQuery(req);
			query.addEntity(EmailTemplate.class);
			List<EmailTemplate> listData = query.list();
			if (listData == null || listData.size() == 0)
				return Optional.empty();
			else
				return Optional.of(listData.get(0));

		} catch (Exception ex) {
			logger.error("method getEmailTemplateByIdAndAccount error : ", ex);
			return Optional.empty();
		} finally {
			
		}
	}

}
