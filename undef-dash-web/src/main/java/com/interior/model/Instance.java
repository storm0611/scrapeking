package com.interior.model;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashMap;

public class Instance implements Serializable {
	private static final long serialVersionUID = 1L;

	private long instanceId;
	private Date created;
	private String url;
	private boolean online;
	private Source source;

	public Instance() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Instance(long instanceId, Date created, String url, boolean online, Source source) {
		super();
		this.instanceId = instanceId;
		this.created = created;
		this.url = url;
		this.online = online;
		this.source = source;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public long getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(long instanceId) {
		this.instanceId = instanceId;
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

	public Source getSource() {
		return source;
	}

	public void setSource(Source source) {
		this.source = source;
	}

	@Override
	public String toString() {
		return "Instance [instanceId=" + instanceId + ", created=" + created + ", url=" + url + ", online=" + online
				+ "]";
	}

}
