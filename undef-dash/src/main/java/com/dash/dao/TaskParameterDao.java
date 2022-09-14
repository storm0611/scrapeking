package com.dash.dao;

import javax.transaction.Transactional;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dash.model.TaskParameter;

@Transactional
public class TaskParameterDao {

	private static final Logger logger = LoggerFactory.getLogger(TaskParameterDao.class);

	public static void saveTaskParameter(Session session, TaskParameter taskParameter) {
		try {
			session.beginTransaction();
			session.save(taskParameter);
			session.getTransaction().commit();
		} catch (Exception ex) {
			logger.error("method saveTaskParameter error : ", ex);
		}
	}

}
