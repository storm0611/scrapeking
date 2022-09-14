package com.interior.model;

import java.io.Serializable;
import java.util.Date;

public class Account implements Serializable {
	private static final long serialVersionUID = 1L;

	// add new fields whenever its necessary / no need to store password
	private int accountId;
	private String username;
	private String token;

	public Account() {
		// TODO Auto-generated constructor stub
	}

	public Account(String username, String token) {
		super();
		this.username = username;
		this.token = token;
	}

	public int getAccountId() {
		return accountId;
	}

	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

}
