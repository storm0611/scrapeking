package com.dash.dao;

import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dash.model.Account;

@Transactional
public class AccountDao {

	private static final Logger logger = LoggerFactory.getLogger(AccountDao.class);

	public static void saveAccount(Session session, Account account) {
		try {
			session.beginTransaction();
			session.save(account);
			session.getTransaction().commit();
		} catch (Exception ex) {
			logger.error("method saveAccount error : ", ex);
		}
	}

	public static void updateAccount(Session session, Account account) {
		try {
			session.beginTransaction();
			session.update(account);
			session.getTransaction().commit();
		} catch (Exception ex) {
			logger.error("method updateAccount error : ", ex);
		}
	}

	public static boolean isAdmin(Session session, Account account) {
		try {
			String req = "select account_role.* from account_role , role where account_role.accountid = "
					+ account.getAccountId()
					+ " and account_role.roleid = role.roleId and upper(role.label) like 'ADMIN'";

			SQLQuery query = session.createSQLQuery(req);
			List<Object> listData = query.list();
			if (listData != null && listData.size() > 0)
				return true;
			else
				return false;

		} catch (Exception ex) {
			logger.error("method isAdmin error : ", ex);
			return false;
		} finally {
		}
	}

	public static Account authenticate(Session session, String login, String password) {
		try {
			
			String queryStr = "FROM Account ACC WHERE ACC.login = :login AND ACC.password = :password ";
			Account account = (Account) session.createQuery(queryStr).setString("login", login)
					.setString("password", password).uniqueResult();
			if (account == null)
				return null;
			else
				return account;

		} catch (Exception ex) {
			logger.error("method authenticate error : ", ex);
			return null;
		} finally {
		}
	}

}
