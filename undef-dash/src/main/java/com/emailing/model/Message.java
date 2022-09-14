package com.emailing.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "message")
public class Message implements Serializable {
	private static final long serialVersionUID = 1L;

	private String messageId;
	private Date date;
	private int rank;
	private Subject subject;
	private String unsubscribeToken;
	private List<Event> listEvents;

	public Message() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Message(String messageId, Date date, int rank, Subject subject) {
		super();
		this.messageId = messageId;
		this.date = date;
		this.rank = rank;
		this.subject = subject;
	}

	public Date getDate() {
		return date;
	}

	@OneToMany(targetEntity = Event.class, mappedBy = "message", cascade = { CascadeType.ALL })
	public List<Event> getListEvents() {
		return listEvents;
	}

	@Id
	public String getMessageId() {
		return messageId;
	}

	public int getRank() {
		return rank;
	}

	public String getUnsubscribeToken() {
		return unsubscribeToken;
	}

	public void setUnsubscribeToken(String unsubscribeToken) {
		this.unsubscribeToken = unsubscribeToken;
	}

	@ManyToOne
	@JoinColumn(name = "subjectId")
	@OnDelete(action = OnDeleteAction.CASCADE)
	public Subject getSubject() {
		return subject;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public void setListEvents(List<Event> listEvents) {
		this.listEvents = listEvents;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public void setSubject(Subject subject) {
		this.subject = subject;
	}

}
