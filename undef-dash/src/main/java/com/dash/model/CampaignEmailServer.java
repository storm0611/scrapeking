package com.dash.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "CampaingEmailServer")
public class CampaignEmailServer implements Serializable {
	private static final long serialVersionUID = 1L;

	private long campaignEmailServerId;
	@JsonIgnore
	private Campaign campaign;
	private EmailServer emailServer;
	private Date created;

	public CampaignEmailServer() {
		super();
		// TODO Auto-generated constructor stub
	}

	public CampaignEmailServer(Campaign campaign, EmailServer emailServer, Date created) {
		super();
		this.campaign = campaign;
		this.emailServer = emailServer;
		this.created = created;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public long getCampaignEmailServerId() {
		return campaignEmailServerId;
	}

	public void setCampaignEmailServerId(long campaignEmailServerId) {
		this.campaignEmailServerId = campaignEmailServerId;
	}

	@ManyToOne
	@JoinColumn(name = "campaignId")
	public Campaign getCampaign() {
		return campaign;
	}

	public void setCampaign(Campaign campaign) {
		this.campaign = campaign;
	}

	@ManyToOne
	@JoinColumn(name = "emailServerId")
	public EmailServer getEmailServer() {
		return emailServer;
	}

	public void setEmailServer(EmailServer emailServer) {
		this.emailServer = emailServer;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

}
