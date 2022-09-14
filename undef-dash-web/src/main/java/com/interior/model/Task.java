package com.interior.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

public class Task implements Serializable {
	private static final long serialVersionUID = 1L;

	private String taskId;

	private Campaign campaign;
	private Source source;
	private Instance instance;
	private Date created;
	private Date expireDate;
	private String cronUnit;
	private int cronValue;
	private boolean repeatForever;
	private Status status;
	private Map<String, String> parameters;

	public Task() {
		super();
		this.cronUnit = "SECONDS";
		this.repeatForever = true;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public Campaign getCampaign() {
		return campaign;
	}

	public void setCampaign(Campaign campaign) {
		this.campaign = campaign;
	}

	public Source getSource() {
		return source;
	}

	public void setSource(Source source) {
		this.source = source;
	}

	public Instance getInstance() {
		return instance;
	}

	public void setInstance(Instance instance) {
		this.instance = instance;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getExpireDate() {
		return expireDate;
	}

	public void setExpireDate(Date expireDate) {
		this.expireDate = expireDate;
	}

	public String getCronUnit() {
		return cronUnit;
	}

	public void setCronUnit(String cronUnit) {
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

	public Map<String, String> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "Task [taskId=" + taskId + ", campaign=" + campaign.getName() + " instance=" + instance.getUrl()
				+ ", created=" + created + ", expireDate=" + expireDate + ", cronUnit=" + cronUnit + ", cronValue="
				+ cronValue + ", repeatForever=" + repeatForever + ", parameters=" + parameters + "]";
	}

}
