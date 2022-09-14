package com.dash.dao;

import javax.transaction.Transactional;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dash.model.Role;

@Transactional
public class RoleDao {

	private static final Logger logger = LoggerFactory.getLogger(RoleDao.class);

	public static void saveRole(Session session, Role role) {
		try {
			session.beginTransaction();
			session.save(role);
			session.getTransaction().commit();
		} catch (Exception ex) {
			logger.error("method saveRole error : ", ex);
		}
	}

}
