package com.dash.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "CAMPAIGN")
public class Campaign implements Serializable {
	private static final long serialVersionUID = 1L;

	private long campaignId;
	@JsonIgnore
	private Account account;
	private String name;
	private String description;
	private Date created;
	private Status status;
	private boolean enableEmailing;
	@JsonIgnore
	private String emailingDBConnectionURL;
	private String recordsDBConnectionURL;
	@JsonIgnore
	private String username;
	@JsonIgnore
	private String password;
	@JsonIgnore
	private List<Task> listTasks;
	private List<CampaignEmailServer> listCampaignEmailServer;
	private List<Email> listEmail;
	private Date expireDate;
	private int maxSendingPerDay;
	private String emailingDays;
	private String blacklistedWords;
	private int timeIntervalUntilNextEmail;

	@JsonIgnore
	private boolean deleted;

	public Campaign() {
		super();
		this.deleted = false;
	}

	public Campaign(long campaignId) {
		super();
		this.campaignId = campaignId;
	}

	public Campaign(String name, String description, Date created, Account account, boolean enableEmailing,
			Status status) {
		super();
		this.name = name;
		this.description = description;
		this.created = created;
		this.account = account;
		this.enableEmailing = enableEmailing;
		this.deleted = false;
		this.status = status;
		this.listCampaignEmailServer = new ArrayList<CampaignEmailServer>();
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public long getCampaignId() {
		return campaignId;
	}

	public void setCampaignId(long campaignId) {
		this.campaignId = campaignId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
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

	@Enumerated(EnumType.STRING)
	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public boolean isEnableEmailing() {
		return enableEmailing;
	}

	public void setEnableEmailing(boolean enableEmailing) {
		this.enableEmailing = enableEmailing;
	}

	public String getEmailingDBConnectionURL() {
		return emailingDBConnectionURL;
	}

	public void setEmailingDBConnectionURL(String emailingDBConnectionURL) {
		this.emailingDBConnectionURL = emailingDBConnectionURL;
	}

	public String getRecordsDBConnectionURL() {
		return recordsDBConnectionURL;
	}

	public void setRecordsDBConnectionURL(String recordsDBConnectionURL) {
		this.recordsDBConnectionURL = recordsDBConnectionURL;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getMaxSendingPerDay() {
		return maxSendingPerDay;
	}

	public void setMaxSendingPerDay(int maxSendingPerDay) {
		this.maxSendingPerDay = maxSendingPerDay;
	}

	@OneToMany(targetEntity = Task.class, mappedBy = "campaign", cascade = { CascadeType.ALL })
	@OnDelete(action = OnDeleteAction.CASCADE)
	public List<Task> getListTasks() {
		return listTasks;
	}

	public void setListTasks(List<Task> listTasks) {
		this.listTasks = listTasks;
	}

	@OneToMany(targetEntity = Email.class, mappedBy = "campaign", cascade = { CascadeType.ALL })
	@OnDelete(action = OnDeleteAction.CASCADE)
	public List<Email> getListEmail() {
		return listEmail;
	}

	public void setListEmail(List<Email> listEmail) {
		this.listEmail = listEmail;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	@OneToMany(fetch = FetchType.EAGER, targetEntity = CampaignEmailServer.class, mappedBy = "campaign")
	public List<CampaignEmailServer> getListCampaignEmailServer() {
		return listCampaignEmailServer;
	}

	public void setListCampaignEmailServer(List<CampaignEmailServer> listCampaignEmailServer) {
		this.listCampaignEmailServer = listCampaignEmailServer;
	}

	public Date getExpireDate() {
		return expireDate;
	}

	public void setExpireDate(Date expireDate) {
		this.expireDate = expireDate;
	}

	public String getEmailingDays() {
		return emailingDays;
	}

	public void setEmailingDays(String emailingDays) {
		this.emailingDays = emailingDays;
	}

	public int getTimeIntervalUntilNextEmail() {
		return timeIntervalUntilNextEmail;
	}

	public void setTimeIntervalUntilNextEmail(int timeIntervalUntilNextEmail) {
		this.timeIntervalUntilNextEmail = timeIntervalUntilNextEmail;
	}

	@Type(type = "text")
	public String getBlacklistedWords() {
		return blacklistedWords;
	}

	public void setBlacklistedWords(String blacklistedWords) {
		this.blacklistedWords = blacklistedWords;
	}

}
