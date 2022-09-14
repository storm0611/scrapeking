package com.interior.managedbean;

import java.io.Serializable;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;

import com.interior.model.Account;

public class SessionManagedBean implements Serializable {

	private static final long serialVersionUID = 1L;
	final static Logger logger = Logger.getLogger(SessionManagedBean.class);

	private Account account;

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	@PostConstruct
	public void init() {
		try {

		} catch (Exception ex) {
			logger.error("method init error : ", ex);
		}
	}

}
