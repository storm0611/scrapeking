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
@Table(name = "ACCOUNT_SOURCE")
public class AccountSource implements Serializable {
	private static final long serialVersionUID = 1L;

	private long accountSourceId;
	private Account account;
	private Source source;

	private Date created;

	public AccountSource() {
		super();
		// TODO Auto-generated constructor stub
	}

	public AccountSource(Account account, Source source, Date created) {
		super();
		this.account = account;
		this.source = source;
		this.created = created;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public long getAccountSourceId() {
		return accountSourceId;
	}

	public void setAccountSourceId(long accountSourceId) {
		this.accountSourceId = accountSourceId;
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
	@JoinColumn(name = "sourceId")
	public Source getSource() {
		return source;
	}

	public void setSource(Source source) {
		this.source = source;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

}
