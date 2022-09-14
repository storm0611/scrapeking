package com.source.cache;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

@Entity
@Table(name = "ENTRY", indexes = { @Index(columnList = "url", name = "entry_url_idx") })
public class Entry {

	private String entryId;
	private String url;
	private Date insertDate;

	public Entry() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Entry(String url, Date insertDate) {
		super();
		this.url = url;
		this.insertDate = insertDate;
	}

	@Id
	@GeneratedValue
	public String getEntryId() {
		return entryId;
	}

	public void setEntryId(String entryId) {
		this.entryId = entryId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Date getInsertDate() {
		return insertDate;
	}

	public void setInsertDate(Date insertDate) {
		this.insertDate = insertDate;
	}

}
