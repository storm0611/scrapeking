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
@Table(name = "CampaignGmailCredential")
public class CampaignGmailCredential implements Serializable {
	private static final long serialVersionUID = 1L;

	private long campaignGmailCredentialId;
	@JsonIgnore
	private Campaign campaign;
	private GmailCredential gmailCredential;
	private Date created;
	
	public CampaignGmailCredential() {
		super();
		// TODO Auto-generated constructor stub
	}

	public CampaignGmailCredential(Campaign campaign, GmailCredential gmailCredential, Date created) {
		super();
		this.campaign = campaign;
		this.gmailCredential = gmailCredential;
		this.created = created;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public long getCampaignGmailCredentialId() {
		return campaignGmailCredentialId;
	}

	public void setCampaignGmailCredentialId(long campaignGmailCredentialId) {
		this.campaignGmailCredentialId = campaignGmailCredentialId;
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
	@JoinColumn(name = "gmailCredentialId")
	public GmailCredential getGmailCredential() {
		return gmailCredential;
	}

	public void setGmailCredential(GmailCredential gmailCredential) {
		this.gmailCredential = gmailCredential;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

}
