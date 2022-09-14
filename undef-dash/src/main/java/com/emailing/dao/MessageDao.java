package com.emailing.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.emailing.model.Message;
import com.emailing.model.Subject;

public class MessageDao {

	private static final Logger logger = LoggerFactory.getLogger(MessageDao.class);

	public static void saveMessage(Session session, Message message) {
		try {
			session.beginTransaction();
			session.save(message);
			session.getTransaction().commit();
		} catch (Exception ex) {
			logger.error("method saveMessage error : ", ex);
		}
	}

	public static void updateMessage(Session session, Message message) {
		try {
			session.beginTransaction();
			session.update(message);
			session.getTransaction().commit();
		} catch (Exception ex) {
			logger.error("method updateMessage error : ", ex);
		}
	}

	public static boolean checkIfExist(Session session, String messageId) {
		try {
			String req = "SELECT * FROM MESSAGE WHERE MESSAGEID LIKE '" + messageId + "'";

			SQLQuery query = session.createSQLQuery(req);
			query.addEntity(Message.class);
			ArrayList<Message> listMessages = (ArrayList<Message>) query.list();

			if (listMessages != null && listMessages.size() != 0)
				return true;
			else
				return false;

		} catch (Exception ex) {
			logger.error("methof checkIfExist error : ", ex);
			return false;
		}
	}

	public static Optional<Message> getLastMessage(Session session, Subject subject) {
		try {

			String req = "select top 1 message.* from message where message.subjectid = '" + subject.getSubjectId()
					+ "' order by date desc";
			SQLQuery query = session.createSQLQuery(req);
			query.addEntity(Message.class);
			List<Message> listData = query.list();
			if (listData != null && listData.size() != 0)
				return Optional.of(listData.get(0));
			else
				return Optional.empty();

		} catch (Exception ex) {
			logger.error("method getLastMessage error : ", ex);
			return Optional.empty();
		}
	}

	public static Optional<List<Message>> getSubjectListMessages(Session session, Subject subject) {
		try {
			String req = "SELECT * FROM MESSAGE WHERE SUBJECTID = '" + subject.getSubjectId()
					+ "' ORDER BY SENDINGDATE ASC";

			SQLQuery query = session.createSQLQuery(req);
			query.addEntity(Message.class);
			List<Message> listData = query.list();
			if (listData != null && listData.size() != 0)
				return Optional.of(listData);
			else
				return Optional.empty();

		} catch (Exception ex) {
			logger.error("method getSubjectListMessages error : ", ex);
			return Optional.empty();
		}
	}

	public static Optional<Message> getMessageById(Session session, String messageId) {
		try {
			String req = "SELECT * FROM MESSAGE WHERE MESSAGEID = '" + messageId + "'";

			SQLQuery query = session.createSQLQuery(req);
			query.addEntity(Message.class);
			List<Message> listMessages = query.list();
			if (listMessages != null && listMessages.size() != 0)
				return Optional.of(listMessages.get(0));
			else
				return Optional.empty();

		} catch (Exception ex) {
			logger.error("method getMessageById error : ", ex);
			return Optional.empty();
		}
	}

	public static Optional<Message> getMessageByUnsubscribeToken(Session session, String token) {
		try {
			String req = "select top 1 message.* from message where  message.unsubscribeToken = '" + token + "'";

			SQLQuery query = session.createSQLQuery(req);
			query.addEntity(Message.class);
			List<Message> listData = query.list();
			if (listData != null && listData.size() != 0)
				return Optional.of(listData.get(0));
			else
				return Optional.empty();

		} catch (Exception ex) {
			logger.error("method getMessageByUnsubscribeToken error : ", ex);
			return Optional.empty();
		}
	}

}
