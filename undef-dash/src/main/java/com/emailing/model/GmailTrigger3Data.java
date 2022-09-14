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
@Table(name = "GmailTrigger3Data")
public class GmailTrigger3Data implements Serializable {
	private static final long serialVersionUID = 1L;

	private long trigger3Id;
	
	private String messageId;
	
	private String subjectId;
	
	private Date date;
	
	private String sentFrom;
	
	private String sentTo;
	
	private long emailTemplateId;
	
	private int contentNumber;
	
	private String unsubscribeToken;
	
	private boolean sent3;
	
	private String failedReason;
	
	public GmailTrigger3Data() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public GmailTrigger3Data(long trigger3Id, String messageId, String subjectId, Date date, String sentFrom,
			String sentTo, long emailTemplateId, int contentNumber,
			String failedReason) {
		super();
		this.trigger3Id = trigger3Id;
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
	public long getTrigger3Id() {
		return trigger3Id;
	}

	public void setTrigger3Id(long trigger3Id) {
		this.trigger3Id = trigger3Id;
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
	public boolean isSent3() {
		return sent3;
	}

	public void setSent3(boolean sent3) {
		this.sent3 = sent3;
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
		return "GmailTrigger3Data [trigger3Id=" + trigger3Id + ", messageId=" + messageId + ", subjectId=" + subjectId
				+ ", date=" + date + ", sentFrom=" + sentFrom + ", sentTo=" + sentTo + ", emailTemplateId="
				+ emailTemplateId + ", contentNumber=" + contentNumber + ", unsubscribeToken=" + unsubscribeToken
				+ ", sent3=" + sent3 + ", failedReason=" + failedReason + "]";
	}

}
