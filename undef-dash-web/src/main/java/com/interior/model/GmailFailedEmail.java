package com.interior.model;

import java.io.Serializable;
import java.util.Date;

public class GmailFailedEmail implements Serializable {
	private static final long serialVersionUID = 1L;

	private String subjectId;
	private String leadEmail;
	private String fromEmail;
	private String reason;
	private Date created;

	public GmailFailedEmail() {
		super();	
		// TODO Auto-generated constructor stub
	}
	
	public GmailFailedEmail(String subjectId, String leadEmail, String fromEmail, String reason, Date created) {
		super();
		this.subjectId = subjectId;
		this.leadEmail = leadEmail;
		this.fromEmail = fromEmail;
		this.reason = reason;
		this.created = created;
	}

	public String getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}

	public String getLeadEmail() {
		return leadEmail;
	}

	public void setLeadEmail(String leadEmail) {
		this.leadEmail = leadEmail;
	}

	public String getFromEmail() {
		return fromEmail;
	}

	public void setFromEmail(String fromEmail) {
		this.fromEmail = fromEmail;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	@Override
	public String toString() {
		return "GmailFailedEmail [subjectId=" + subjectId + ", leadEmail=" + leadEmail + ", fromEmail=" + fromEmail
				+ ", reason=" + reason + ", created=" + created + "]";
	}

}
