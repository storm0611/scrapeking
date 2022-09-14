package com.emailing.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "event")
public class Event implements Serializable {
	private static final long serialVersionUID = 1L;

	private String eventId;
	private EventType type;
	private Date date;
	private Message message;

	public Event() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Event(String eventId, EventType type, Date date, Message message) {
		super();
		this.eventId = eventId;
		this.type = type;
		this.date = date;
		this.message = message;
	}

	public Date getDate() {
		return date;
	}

	@Id
	public String getEventId() {
		return eventId;
	}

	@ManyToOne
	@JoinColumn(name = "messageId")
	@OnDelete(action = OnDeleteAction.CASCADE)
	public Message getMessage() {
		return message;
	}

	@Enumerated(EnumType.STRING)
	public EventType getType() {
		return type;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public void setMessage(Message message) {
		this.message = message;
	}

	public void setType(EventType type) {
		this.type = type;
	}

}
