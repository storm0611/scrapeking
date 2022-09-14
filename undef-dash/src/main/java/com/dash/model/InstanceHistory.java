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
@Table(name = "INSTANCEHISTORY")
public class InstanceHistory implements Serializable {
	private static final long serialVersionUID = 1L;

	private long instanceHistoryId;
	private Date timestamp;
	private boolean online;
	@JsonIgnore
	private Instance instance;

	public InstanceHistory() {
		super();
		// TODO Auto-generated constructor stub
	}

	public InstanceHistory(Date timestamp, boolean online, Instance instance) {
		super();
		this.timestamp = timestamp;
		this.online = online;
		this.instance = instance;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public long getInstanceHistoryId() {
		return instanceHistoryId;
	}

	public void setInstanceHistoryId(long instanceHistoryId) {
		this.instanceHistoryId = instanceHistoryId;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public boolean isOnline() {
		return online;
	}

	public void setOnline(boolean online) {
		this.online = online;
	}

	@ManyToOne
	@JoinColumn(name = "instanceId")
	@OnDelete(action = OnDeleteAction.CASCADE)
	public Instance getInstance() {
		return instance;
	}

	public void setInstance(Instance instance) {
		this.instance = instance;
	}

}
