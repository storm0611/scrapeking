package com.emailing.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "GmailTrigger1Data")
public class GmailTrigger1Data implements Serializable {
	private static final long serialVersionUID = 1L;

	private long trigger1Id;
	
	private String messageId;
	
	private String subjectId;
	
	private Date date;
	
	private String sentFrom;
	
	private String sentTo;
	
	private long emailTemplateId;
	
	private int contentNumber;
	
	private String unsubscribeToken;
	
	private boolean sent1;
	
	private String failedReason;
	
	public GmailTrigger1Data() {
		super();
		// TODO Auto-generated constructor stub
	}

	public GmailTrigger1Data(long trigger1Id, String messageId, String subjectId, Date date, String sentFrom,
			String sentTo, long emailTemplateId, int contentNumber,
			String failedReason) {
		super();
		this.trigger1Id = trigger1Id;
		this.messageId = messageId;
		this.subjectId = subjectId;
		this.date = date;
		this.sentFrom = sentFrom;
		this.sentTo = sentTo;
		this.emailTemplateId = emailTemplateId;
		this.contentNumber = contentNumber;
		this.failedReason = failedReason;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public long getTrigger1Id() {
		return trigger1Id;
	}

	public void setTrigger1Id(long trigger1Id) {
		this.trigger1Id = trigger1Id;
	}
	
	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public String getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getSentFrom() {
		return sentFrom;
	}

	public void setSentFrom(String sentFrom) {
		this.sentFrom = sentFrom;
	}

	public String getSentTo() {
		return sentTo;
	}

	public void setSentTo(String sentTo) {
		this.sentTo = sentTo;
	}

	public long getEmailTemplateId() {
		return emailTemplateId;
	}

	public void setEmailTemplateId(long emailTemplateId) {
		this.emailTemplateId = emailTemplateId;
	}

	public int getContentNumber() {
		return contentNumber;
	}

	public void setContentNumber(int contentNumber) {
		this.contentNumber = contentNumber;
	}
	
	@Transient
	public boolean isSent1() {
		return sent1;
	}

	public void setSent1(boolean sent1) {
		this.sent1 = sent1;
	}

	@Transient
	public String getUnsubscribeToken() {
		return unsubscribeToken;
	}

	public void setUnsubscribeToken(String unsubscribeToken) {
		this.unsubscribeToken = unsubscribeToken;
	}

	public String getFailedReason() {
		return failedReason;
	}

	public void setFailedReason(String failedReason) {
		this.failedReason = failedReason;
	}

	@Override
	public String toString() {
		return "GmailTrigger1Data [trigger1Id=" + trigger1Id + ", messageId=" + messageId + ", subjectId=" + subjectId
				+ ", date=" + date + ", sentFrom=" + sentFrom + ", sentTo=" + sentTo + ", emailTemplateId="
				+ emailTemplateId + ", contentNumber=" + contentNumber + ", unsubscribeToken=" + unsubscribeToken
				+ ", sent1=" + sent1 + ", failedReason=" + failedReason + "]";
	}

}
