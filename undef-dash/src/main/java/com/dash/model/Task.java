package com.dash.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "TASK")
public class Task implements Serializable {
	private static final long serialVersionUID = 1L;

	private String taskId;
	@JsonIgnore
	private Campaign campaign;
	private Instance instance;
	private Date created;
	private Date expireDate;
	private CronUnit cronUnit;
	private int cronValue;
	private boolean repeatForever;
	private List<TaskParameter> listTaskParameters;
	private Status status;

	@JsonIgnore
	private boolean deleted;

	public Task() {
		super();
		// TODO Auto-generated constructor stub
		this.deleted = false;
	}

	@Id
	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	@ManyToOne
	@JoinColumn(name = "campaignId")
	public Campaign getCampaign() {
		return campaign;
	}

	public void setCampaign(Campaign campaign) {
		this.campaign = campaign;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	@ManyToOne
	@JoinColumn(name = "instanceId")
	public Instance getInstance() {
		return instance;
	}

	public void setInstance(Instance instance) {
		this.instance = instance;
	}

	public Date getExpireDate() {
		return expireDate;
	}

	public void setExpireDate(Date expireDate) {
		this.expireDate = expireDate;
	}

	@Enumerated(EnumType.STRING)
	public CronUnit getCronUnit() {
		return cronUnit;
	}

	public void setCronUnit(CronUnit cronUnit) {
		this.cronUnit = cronUnit;
	}

	public int getCronValue() {
		return cronValue;
	}

	public void setCronValue(int cronValue) {
		this.cronValue = cronValue;
	}

	public boolean isRepeatForever() {
		return repeatForever;
	}

	public void setRepeatForever(boolean repeatForever) {
		this.repeatForever = repeatForever;
	}

	@OneToMany(targetEntity = TaskParameter.class, mappedBy = "task", cascade = { CascadeType.ALL })
	public List<TaskParameter> getListTaskParameters() {
		return listTaskParameters;
	}

	public void setListTaskParameters(List<TaskParameter> listTaskParameters) {
		this.listTaskParameters = listTaskParameters;
	}

	@Enumerated(EnumType.STRING)
	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

}
