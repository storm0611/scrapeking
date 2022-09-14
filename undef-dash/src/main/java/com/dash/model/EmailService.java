package com.dash.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "EmailService")
public class EmailService implements Serializable {
	private static final long serialVersionUID = 1L;

	private int emailServiceId;
	private String name;
	private Date created;
	private List<String> parameters = new ArrayList<String>();

	public EmailService() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getEmailServiceId() {
		return emailServiceId;
	}

	public void setEmailServiceId(int emailServiceId) {
		this.emailServiceId = emailServiceId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	@ElementCollection
	public List<String> getParameters() {
		return parameters;
	}

	public void setParameters(List<String> parameters) {
		this.parameters = parameters;
	}

}
