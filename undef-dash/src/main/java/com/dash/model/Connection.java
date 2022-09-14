package com.dash.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
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

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "CONNECTION")
public class Connection implements Serializable {
	private static final long serialVersionUID = 1L;
	@JsonIgnore
	private long connectionId;
	@JsonIgnore
	private Account account;
	private String ip;
	private Date timestamp;
	@JsonIgnore
	private List<Log> listLogs;

	public Connection() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Connection(Account account, String ip, Date timestamp) {
		super();
		this.account = account;
		this.ip = ip;
		this.timestamp = timestamp;
	}

	public Connection(long connectionId, Account account, String ip, Date timestamp, List<Log> listLogs) {
		super();
		this.connectionId = connectionId;
		this.account = account;
		this.ip = ip;
		this.timestamp = timestamp;
		this.listLogs = listLogs;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "accountId")
	public Account getAccount() {
		return account;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public long getConnectionId() {
		return connectionId;
	}

	public String getIp() {
		return ip;
	}

	@OneToMany(targetEntity = Log.class, mappedBy = "connection", cascade = { CascadeType.ALL })
	@OnDelete(action = OnDeleteAction.CASCADE)
	public List<Log> getListLogs() {
		return listLogs;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public void setConnectionId(long connectionId) {
		this.connectionId = connectionId;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setListLogs(List<Log> listLogs) {
		this.listLogs = listLogs;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

}
