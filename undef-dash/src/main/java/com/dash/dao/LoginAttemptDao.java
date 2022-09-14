package com.dash.dao;

import javax.transaction.Transactional;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dash.model.LoginAttempt;

@Transactional
public class LoginAttemptDao {

	private static final Logger logger = LoggerFactory.getLogger(LoginAttemptDao.class);

	public static void saveLoginAttempt(Session session, LoginAttempt loginAttempt) {
		try {
			session.beginTransaction();
			session.save(loginAttempt);
			session.getTransaction().commit();
		} catch (Exception ex) {
			logger.error("method saveLoginAttempt error : ", ex);
		}
	}

}
