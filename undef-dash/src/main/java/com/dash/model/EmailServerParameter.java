package com.dash.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "EMAILSERVERPARAMETER")
public class EmailServerParameter implements Serializable {
	private static final long serialVersionUID = 1L;

	@JsonIgnore
	private long parameterId;
	private String key;
	private String value;
	@JsonIgnore
	private EmailServer emailServer;

	public EmailServerParameter() {
		super();
		// TODO Auto-generated constructor stub
	}

	public EmailServerParameter(String key, String value, EmailServer emailServer) {
		super();
		this.key = key;
		this.value = value;
		this.emailServer = emailServer;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public long getParameterId() {
		return parameterId;
	}

	public void setParameterId(long parameterId) {
		this.parameterId = parameterId;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@ManyToOne
	@JoinColumn(name = "emailServerId")
	@OnDelete(action = OnDeleteAction.CASCADE)
	public EmailServer getEmailServer() {
		return emailServer;
	}

	public void setEmailServer(EmailServer emailServer) {
		this.emailServer = emailServer;
	}

}
