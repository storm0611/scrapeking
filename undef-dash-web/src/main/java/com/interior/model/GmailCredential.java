package com.interior.model;

import java.io.Serializable;
import java.util.Date;

public class GmailCredential implements Serializable {
	private static final long serialVersionUID = 1L;

	private long gmailCredentialId;
	private String username;
	private String password;
	private long perDay;
	private String analyticsId;
	private Date created;
	private int unReadMessageCount;
	private String proxyIp;
	
	private Long proxyPort;

	public GmailCredential() {
		super();	
		// TODO Auto-generated constructor stub
	}

	public GmailCredential(long gmailCredentialId, String username, String password, long perDay, String analyticsId,
			Date created, int unReadMessageCount, String proxyIp, Long proxyPort) {
		super();
		this.gmailCredentialId = gmailCredentialId;
		this.username = username;
		this.password = password;
		this.perDay = perDay;
		this.analyticsId = analyticsId;
		this.created = created;
		this.unReadMessageCount = unReadMessageCount;
		this.proxyIp = proxyIp;
		this.proxyPort = proxyPort;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public long getGmailCredentialId() {
		return gmailCredentialId;
	}

	public void setGmailCredentialId(long gmailCredentialId) {
		this.gmailCredentialId = gmailCredentialId;
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

	public long getPerDay() {
		return perDay;
	}

	public void setPerDay(long perDay) {
		this.perDay = perDay;
	}

	public String getAnalyticsId() {
		return analyticsId;
	}

	public void setAnalyticsId(String analyticsId) {
		this.analyticsId = analyticsId;
	}

	public int getUnReadMessageCount() {
		return unReadMessageCount;
	}

	public void setUnReadMessageCount(int unReadMessageCount) {
		this.unReadMessageCount = unReadMessageCount;
	}

	public String getProxyIp() {
		return proxyIp;
	}

	public void setProxyIp(String proxyIp) {
		this.proxyIp = proxyIp;
	}

	public Long getProxyPort() {
		return proxyPort;
	}

	public void setProxyPort(Long proxyPort) {
		this.proxyPort = proxyPort;
	}

	@Override
	public String toString() {
		return "GmailCredential [gmailCredentialId=" + gmailCredentialId + ", username=" + username + ", password="
				+ password + ", perDay=" + perDay + ", analyticsId=" + analyticsId + ", created=" + created
				+ ", unReadMessageCount=" + unReadMessageCount + ", proxyIp=" + proxyIp + ", proxyPort=" + proxyPort
				+ "]";
	}

}
