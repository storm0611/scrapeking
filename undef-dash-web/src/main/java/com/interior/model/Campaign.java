package com.interior.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Campaign implements Serializable {

	private static final long serialVersionUID = 1L;

	private long campaignId;
	private String name;
	private String description;
	private boolean enableEmailing;
	private List<EmailServer> listEmailServer;
	private int maxSendingPerDay;
	private int timeIntervalUntilNextEmail;

	private EmailTemplate firstAttemptEmail;
	private EmailTemplate followupEmail;
	private EmailTemplate secondAttemptEmail;

	private String blacklistedWords;
	private List<String> emailingDays;
	private Date created;
	private Status status;
	private Pull pull;

	private List<Task> listTasks;
	
	private List<GmailCredential> selectedCredentials;

	public Campaign() {
		super();
		this.description = "";

//		this.firstAttemptEmail = new EmailTemplate();
//		this.followupEmail = new EmailTemplate();
//		this.secondAttemptEmail = new EmailTemplate();

		this.pull = new Pull();
		this.listTasks = new ArrayList<Task>();
		this.description = "";
	}

	public List<Task> getListTasks() {
		return listTasks;
	}

	public void setListTasks(List<Task> listTasks) {
		this.listTasks = listTasks;
	}

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

	public String getBlacklistedWords() {
		return blacklistedWords;
	}

	public void setBlacklistedWords(String blacklistedWords) {
		this.blacklistedWords = blacklistedWords;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isEnableEmailing() {
		return enableEmailing;
	}

	public void setEnableEmailing(boolean enableEmailing) {
		this.enableEmailing = enableEmailing;
	}

	public List<EmailServer> getListEmailServer() {
		return listEmailServer;
	}

	public void setListEmailServer(List<EmailServer> listEmailServer) {
		this.listEmailServer = listEmailServer;
	}

	public int getMaxSendingPerDay() {
		return maxSendingPerDay;
	}

	public void setMaxSendingPerDay(int maxSendingPerDay) {
		this.maxSendingPerDay = maxSendingPerDay;
	}

	public int getTimeIntervalUntilNextEmail() {
		return timeIntervalUntilNextEmail;
	}

	public void setTimeIntervalUntilNextEmail(int timeIntervalUntilNextEmail) {
		this.timeIntervalUntilNextEmail = timeIntervalUntilNextEmail;
	}

	public EmailTemplate getFirstAttemptEmail() {
		return firstAttemptEmail;
	}

	public void setFirstAttemptEmail(EmailTemplate firstAttemptEmail) {
		this.firstAttemptEmail = firstAttemptEmail;
	}

	public EmailTemplate getFollowupEmail() {
		return followupEmail;
	}

	public void setFollowupEmail(EmailTemplate followupEmail) {
		this.followupEmail = followupEmail;
	}

	public EmailTemplate getSecondAttemptEmail() {
		return secondAttemptEmail;
	}

	public void setSecondAttemptEmail(EmailTemplate secondAttemptEmail) {
		this.secondAttemptEmail = secondAttemptEmail;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Pull getPull() {
		return pull;
	}

	public void setPull(Pull pull) {
		this.pull = pull;
	}

	public List<String> getEmailingDays() {
		return emailingDays;
	}

	public void setEmailingDays(List<String> emailingDays) {
		this.emailingDays = emailingDays;
	}

	public List<GmailCredential> getSelectedCredentials() {
		return selectedCredentials;
	}

	public void setSelectedCredentials(List<GmailCredential> selectedCredentials) {
		this.selectedCredentials = selectedCredentials;
	}

	@Override
	public String toString() {
		return "Campaign [campaignId=" + campaignId + ", name=" + name + ", description=" + description
				+ ", listEmailServer=" + listEmailServer + "]";
	}

}
