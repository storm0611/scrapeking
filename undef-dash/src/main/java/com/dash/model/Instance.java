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

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "INSTANCE")
public class Instance implements Serializable {
	private static final long serialVersionUID = 1L;

	private long instanceId;
	private Date created;
	private String url;
	private boolean online;
	@JsonIgnore
	private Source source;
	@JsonIgnore
	private boolean deleted;

	public Instance() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Instance(Date created, String url, Source source) {
		super();
		this.created = created;
		this.url = url;
		this.source = source;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public long getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(long instanceId) {
		this.instanceId = instanceId;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public boolean isOnline() {
		return online;
	}

	public void setOnline(boolean online) {
		this.online = online;
	}

	@ManyToOne
	@JoinColumn(name = "sourceId")
	@OnDelete(action = OnDeleteAction.CASCADE)
	public Source getSource() {
		return source;
	}

	public void setSource(Source source) {
		this.source = source;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

}
