package com.dash.statistics.dao;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dash.statistics.model.Figure;

public class FigureDao {
	private static final Logger logger = LoggerFactory.getLogger(FigureDao.class);

	public static void saveFigure(Session session, Figure figure) {
		try {
			session.beginTransaction();
			session.save(figure);
			session.getTransaction().commit();
		} catch (Exception ex) {
			logger.error("method saveFigure error : ", ex);
		}
	}
}
