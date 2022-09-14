package com.interior.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Source implements Serializable {
	private static final long serialVersionUID = 1L;

	private long sourceId;
	private String title;
	private String website;
	private Date created;
	private List<Instance> listInstance;

	public Source() {
		super();
		// TODO Auto-generated constructor stub
		listInstance = new ArrayList<Instance>();
	}

	public Source(long sourceId, String title) {
		this.sourceId = sourceId;
		this.title = title;
	}

	public Source(long sourceId, String title, String website, Date created) {
		super();
		this.sourceId = sourceId;
		this.title = title;
		this.website = website;
		this.created = created;

	}

	public List<Instance> getListInstance() {
		return listInstance;
	}

	public void setListInstance(List<Instance> listInstance) {
		this.listInstance = listInstance;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public long getSourceId() {
		return sourceId;
	}

	public void setSourceId(long sourceId) {
		this.sourceId = sourceId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	@Override
	public String toString() {
		return "Source [sourceId=" + sourceId + ", title=" + title + ", website=" + website + ", created=" + created
				+ ", listInstance=" + listInstance + "]";
	}

}
