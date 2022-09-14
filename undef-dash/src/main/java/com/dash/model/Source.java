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
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "SOURCE")
public class Source implements Serializable {
	private static final long serialVersionUID = 1L;

	private long sourceId;
	private String title;
	private String website;
	private Date created;
	private List<Instance> listInstances;
	@JsonIgnore
	private boolean deleted;
	@JsonIgnore
	private List<AccountSource> listAccountSource;

	public Source() {
		super();
		// TODO Auto-generated constructor stub
		this.deleted = false;
	}

	public Source(String title, String website, Date created) {
		super();
		this.title = title;
		this.website = website;
		this.created = created;
		deleted = false;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
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

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	@OneToMany(targetEntity = Instance.class, mappedBy = "source", cascade = { CascadeType.ALL })
	public List<Instance> getListInstances() {
		return listInstances;
	}

	public void setListInstances(List<Instance> listInstances) {
		this.listInstances = listInstances;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	@OneToMany(fetch = FetchType.EAGER, targetEntity = AccountSource.class, mappedBy = "source")
	public List<AccountSource> getListAccountSource() {
		return listAccountSource;
	}

	public void setListAccountSource(List<AccountSource> listAccountSource) {
		this.listAccountSource = listAccountSource;
	}

}
