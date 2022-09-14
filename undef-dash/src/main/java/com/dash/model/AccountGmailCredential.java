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

@Entity
@Table(name = "ACCOUNT_GMAIL_CREDENTIAL")
public class AccountGmailCredential implements Serializable {
	private static final long serialVersionUID = 1L;

	private long accountGmailCredentialId;
	private Account account;
	private GmailCredential gmailCredential;

	private Date created;

	public AccountGmailCredential() {
		super();
		// TODO Auto-generated constructor stub
	}

	public AccountGmailCredential(Account account, GmailCredential gmailCredential, Date created) {
		super();
		this.account = account;
		this.gmailCredential = gmailCredential;
		this.created = created;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public long getAccountGmailCredentialId() {
		return accountGmailCredentialId;
	}

	public void setAccountGmailCredentialId(long accountGmailCredentialId) {
		this.accountGmailCredentialId = accountGmailCredentialId;
	}
	
	@ManyToOne
	@JoinColumn(name = "accountId")
	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
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
