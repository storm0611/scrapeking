package com.emailing.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.emailing.model.Event;
import com.emailing.model.Message;

public class EventDao implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final Logger logger = LoggerFactory.getLogger(EventDao.class);

	public static void saveEvent(Session session, Event event) {
		try {
			session.beginTransaction();
			session.save(event);
			session.getTransaction().commit();
		} catch (Exception ex) {
			logger.error("method saveEvent error : ", ex);
		}
	}

	public static boolean checkIfExist(Session session, String eventId) {
		try {

			String req = "SELECT * FROM EVENT WHERE EVENTID LIKE '" + eventId + "'";
			SQLQuery query = session.createSQLQuery(req);
			query.addEntity(Event.class);
			List<Event> listData = query.list();
			if (listData != null && listData.size() != 0)
				return true;
			else
				return false;

		} catch (Exception ex) {
			logger.error("method checkIfExist error : ", ex);
			return false;
		}
	}

	public static Optional<Event> getMessageValuableEvent(Session session, Message message) {
		try {

			String req = "select top 1 event .* from event where event.messageid=" + message.getMessageId()
					+ " and (event.type like 'open' or event.type like 'click' or event.type like 'delivered') order by CASE WHEN Type like 'open' THEN 0 WHEN Type like 'click' THEN 1 WHEN Type like 'delivered' THEN 2 end";
			SQLQuery query = session.createSQLQuery(req);
			query.addEntity(Event.class);
			List<Event> listData = query.list();
			if (listData != null && listData.size() != 0)
				return Optional.of(listData.get(0));
			else
				return Optional.empty();

		} catch (Exception ex) {
			logger.error("method getMessageValuableEvent error : ", ex);
			return Optional.empty();
		}
	}

}
