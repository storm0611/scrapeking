package com.interior.model;

import java.io.Serializable;

public class CampaignEmailsCount implements Serializable {
	private static final long serialVersionUID = 1L;

	private String fromEmail;
	private long successCount;
	private long failCount;

	public CampaignEmailsCount() {
		super();	
		// TODO Auto-generated constructor stub
	}

	public CampaignEmailsCount(String fromEmail, long successCount, long failCount) {
		super();
		this.fromEmail = fromEmail;
		this.successCount = successCount;
		this.failCount = failCount;
	}

	public String getFromEmail() {
		return fromEmail;
	}

	public void setFromEmail(String fromEmail) {
		this.fromEmail = fromEmail;
	}

	public long getSuccessCount() {
		return successCount;
	}

	public void setSuccessCount(long successCount) {
		this.successCount = successCount;
	}

	public long getFailCount() {
		return failCount;
	}

	public void setFailCount(long failCount) {
		this.failCount = failCount;
	}

	@Override
	public String toString() {
		return "CampaignEmailsCount [fromEmail=" + fromEmail + ", successCount=" + successCount + ", failCount="
				+ failCount + "]";
	}

}
