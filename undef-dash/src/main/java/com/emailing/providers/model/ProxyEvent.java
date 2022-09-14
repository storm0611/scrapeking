package com.emailing.providers.model;

import java.io.Serializable;
import java.util.Date;

import com.emailing.model.EventType;

public class ProxyEvent implements Serializable {

	private static final long serialVersionUID = 1L;

	private int campaignId;
	private String subjectId;
	private String messageId;
	private String eventId;
	private EventType eventType;
	private Date eventDate;

	public ProxyEvent(int campaignId, String subjectId, String messageId, String eventId, Date eventDate) {
		super();
		this.campaignId = campaignId;
		this.subjectId = subjectId;
		this.messageId = messageId;
		this.eventId = eventId;
		this.eventDate = eventDate;
	}

	public ProxyEvent() {
		super();
		// TODO Auto-generated constructor stub
	}

	public int getCampaignId() {
		return campaignId;
	}

	public void setCampaignId(int campaignId) {
		this.campaignId = campaignId;
	}

	public String getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public EventType getEventType() {
		return eventType;
	}

	public void setEventType(EventType eventType) {
		this.eventType = eventType;
	}

	public Date getEventDate() {
		return eventDate;
	}

	public void setEventDate(Date eventDate) {
		this.eventDate = eventDate;
	}

}
