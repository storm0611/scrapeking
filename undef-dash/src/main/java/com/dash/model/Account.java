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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "ACCOUNT")
public class Account implements Serializable {
	private static final long serialVersionUID = 1L;

	@JsonIgnore
	private int accountId;
	private String login;
	private String token;
	@JsonIgnore
	private String password;
	@JsonIgnore
	private Status status;
	@JsonIgnore
	private Date created;
	private List<Connection> listConnections;
	@JsonIgnore
	private List<Role> listRoles;
	@JsonIgnore
	private List<AccountSource> listAccountSource;

	@JsonIgnore
	private List<EmailServer> listEmailServers;

	public Account() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getAccountId() {
		return accountId;
	}

	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	@OneToMany(fetch = FetchType.EAGER, targetEntity = Connection.class, mappedBy = "account")
	public List<Connection> getListConnections() {
		return listConnections;
	}

	public void setListConnections(List<Connection> listConnections) {
		this.listConnections = listConnections;
	}

	@ManyToMany(cascade = CascadeType.PERSIST)
	@JoinTable(name = "account_role", joinColumns = { @JoinColumn(name = "accountId") }, inverseJoinColumns = {
			@JoinColumn(name = "roleId") })
	public List<Role> getListRoles() {
		return listRoles;
	}

	public void setListRoles(List<Role> listRoles) {
		this.listRoles = listRoles;
	}

	@OneToMany(fetch = FetchType.EAGER, targetEntity = EmailServer.class, mappedBy = "account")
	public List<EmailServer> getListEmailServers() {
		return listEmailServers;
	}

	public void setListEmailServers(List<EmailServer> listEmailServers) {
		this.listEmailServers = listEmailServers;
	}

	@OneToMany(fetch = FetchType.EAGER, targetEntity = AccountSource.class, mappedBy = "account")
	public List<AccountSource> getListAccountSource() {
		return listAccountSource;
	}

	public void setListAccountSource(List<AccountSource> listAccountSource) {
		this.listAccountSource = listAccountSource;
	}

}
