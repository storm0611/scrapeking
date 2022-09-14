package com.dash.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "LOGINATTEMPT")
public class LoginAttempt implements Serializable {
	private static final long serialVersionUID = 1L;

	private long loginAttemptId;
	private String username;
	private String password;
	private String ip;
	private Date timestamp;

	public LoginAttempt() {
		super();
		// TODO Auto-generated constructor stub
	}

	public LoginAttempt(String username, String password, String ip, Date timestamp) {
		super();
		this.username = username;
		this.password = password;
		this.ip = ip;
		this.timestamp = timestamp;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public long getLoginAttemptId() {
		return loginAttemptId;
	}

	public void setLoginAttemptId(long loginAttemptId) {
		this.loginAttemptId = loginAttemptId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

}
