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
@Table(name = "Email")
public class Email implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonIgnore
	private int emailId;
	@JsonIgnore
	private Campaign campaign;
	private EmailTemplate emailTemplate;
	private int rank;

	public Email() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Email(int rank, Campaign campaign, EmailTemplate emailTemplate) {
		super();
		this.rank = rank;
		this.campaign = campaign;
		this.emailTemplate = emailTemplate;

	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getEmailId() {
		return emailId;
	}

	public void setEmailId(int emailId) {
		this.emailId = emailId;
	}

	@ManyToOne
	@JoinColumn(name = "campaignId")
	@OnDelete(action = OnDeleteAction.CASCADE)
	public Campaign getCampaign() {
		return campaign;
	}

	public void setCampaign(Campaign campaign) {
		this.campaign = campaign;
	}

	@ManyToOne
	@JoinColumn(name = "emailTemplateId")
	@OnDelete(action = OnDeleteAction.CASCADE)
	public EmailTemplate getEmailTemplate() {
		return emailTemplate;
	}

	public void setEmailTemplate(EmailTemplate emailTemplate) {
		this.emailTemplate = emailTemplate;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

}
