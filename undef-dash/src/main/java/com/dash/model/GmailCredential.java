package com.dash.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "GMAIL_CREDENTIAL")
public class GmailCredential implements Serializable {
	private static final long serialVersionUID = 1L;

	private long gmailCredentialId;
	private String username;
	private String password;
	private long perDay;
	private String analyticsId;
	private Date created;
	@JsonIgnore
	private boolean deleted;
	@JsonIgnore
	private List<AccountGmailCredential> listAccountGmailCredential;
	@JsonIgnore
	private Account account;
	private int unReadMessageCount;
	
	//By default its null, when it will be zero than stop sending email
	private Long perDayPendingCount;
	
	private String proxyIp;
	
	private Long proxyPort;

	public GmailCredential() {
		super();
		// TODO Auto-generated constructor stub
		this.deleted = false;
	}

	public GmailCredential(String username, String password, Date created, long perDay, String analyticsId, Account account, int unReadMessageCount, String proxyIp, Long proxyPort) {
		super();
		this.username = username;
		this.password = password;
		this.created = created;
		this.perDay = perDay;
		this.analyticsId = analyticsId;
		this.account = account;
		this.unReadMessageCount = unReadMessageCount;
		this.proxyIp = proxyIp;
		this.proxyPort = proxyPort;
		deleted = false;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public long getGmailCredentialId() {
		return gmailCredentialId;
	}

	public void setGmailCredentialId(long gmailCredentialId) {
		this.gmailCredentialId = gmailCredentialId;
	}
	
	public Date getCreated() {
		return created;
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

	public long getPerDay() {
		return perDay;
	}

	public void setPerDay(long perDay) {
		this.perDay = perDay;
	}

	public String getAnalyticsId() {
		return analyticsId;
	}

	public void setAnalyticsId(String analyticsId) {
		this.analyticsId = analyticsId;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	@OneToMany(fetch = FetchType.EAGER, targetEntity = AccountGmailCredential.class, mappedBy = "gmailCredential")
	public List<AccountGmailCredential> getListAccountGmailCredential() {
		return listAccountGmailCredential;
	}

	public void setListAccountGmailCredential(List<AccountGmailCredential> listAccountGmailCredential) {
		this.listAccountGmailCredential = listAccountGmailCredential;
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

	public int getUnReadMessageCount() {
		return unReadMessageCount;
	}

	public void setUnReadMessageCount(int unReadMessageCount) {
		this.unReadMessageCount = unReadMessageCount;
	}

	@Transient
	public Long getPerDayPendingCount() {
		return perDayPendingCount;
	}

	public void setPerDayPendingCount(Long perDayPendingCount) {
		this.perDayPendingCount = perDayPendingCount;
	}

	public String getProxyIp() {
		return proxyIp;
	}

	public void setProxyIp(String proxyIp) {
		this.proxyIp = proxyIp;
	}

	public Long getProxyPort() {
		return proxyPort;
	}

	public void setProxyPort(Long proxyPort) {
		this.proxyPort = proxyPort;
	}
	
}
