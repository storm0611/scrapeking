package com.dash.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "EMAILSERVER")
public class EmailServer implements Serializable {
	private static final long serialVersionUID = 1L;

	private int emailServerId;
	private String label;
	@JsonIgnore
	private Date created;
	private EmailService emailService;
	private Status status;
	private List<EmailServerParameter> listServerParameters;
	private boolean deleted;
	@JsonIgnore
	private Account account;

	public EmailServer() {
		super();
		this.created = new Date();
		this.status = Status.active;
		this.listServerParameters = new ArrayList<EmailServerParameter>();
		this.deleted = false;

	}

	public EmailServer(String label, EmailService emailService, Account account) {
		super();
		this.label = label;
		this.emailService = emailService;
		this.account = account;
		this.deleted = false;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getEmailServerId() {
		return emailServerId;
	}

	public void setEmailServerId(int emailServerId) {
		this.emailServerId = emailServerId;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	@ManyToOne
	@JoinColumn(name = "emailServiceId")
	@OnDelete(action = OnDeleteAction.CASCADE)
	public EmailService getEmailService() {
		return emailService;
	}

	public void setEmailService(EmailService emailService) {
		this.emailService = emailService;
	}

	@Enumerated(EnumType.STRING)
	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	@OneToMany(targetEntity = EmailServerParameter.class, mappedBy = "emailServer", cascade = { CascadeType.ALL })
	public List<EmailServerParameter> getListServerParameters() {
		return listServerParameters;
	}

	public void setListServerParameters(List<EmailServerParameter> listServerParameters) {
		this.listServerParameters = listServerParameters;
	}

	@ManyToOne
	@JoinColumn(name = "accountId")
	@OnDelete(action = OnDeleteAction.CASCADE)
	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	@Override
	public String toString() {
		return "EmailServer [emailServerId=" + emailServerId + ", label=" + label + ", created=" + created
				+ ", emailService=" + emailService + "]";
	}

}
