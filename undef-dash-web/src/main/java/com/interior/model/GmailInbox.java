package com.interior.model;

import java.io.Serializable;
import java.util.Date;

public class GmailInbox implements Serializable {
	private static final long serialVersionUID = 1L;

	private String threadId;
	private String subject;
	private String from;
	private String to;
	private String title;
	private String text;
	private Date receiveDate;
	private Long credentialId;
	private boolean unread;

	public GmailInbox() {
		super();	
		// TODO Auto-generated constructor stub
	}
	
	public GmailInbox(String threadId, String subject, String from, String to, String title, String text,
			Date receiveDate, Long credentialId) {
		super();
		this.threadId = threadId;
		this.subject = subject;
		this.from = from;
		this.to = to;
		this.title = title;
		this.text = text;
		this.receiveDate = receiveDate;
		this.credentialId = credentialId;
	}

	public String getThreadId() {
		return threadId;
	}

	public void setThreadId(String threadId) {
		this.threadId = threadId;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Date getReceiveDate() {
		return receiveDate;
	}

	public void setReceiveDate(Date receiveDate) {
		this.receiveDate = receiveDate;
	}

	public Long getCredentialId() {
		return credentialId;
	}

	public void setCredentialId(Long credentialId) {
		this.credentialId = credentialId;
	}

	public boolean isUnread() {
		return unread;
	}

	public void setUnread(boolean unread) {
		this.unread = unread;
	}

	@Override
	public String toString() {
		return "GmailInbox [threadId=" + threadId + ", subject=" + subject + ", from=" + from + ", to=" + to
				+ ", title=" + title + ", text=" + text + ", receiveDate=" + receiveDate + ", credentialId="
				+ credentialId + "]";
	}

}
