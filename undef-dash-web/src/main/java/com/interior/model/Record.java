package com.interior.model;

import java.io.Serializable;
import java.util.Date;

public class Record implements Serializable {
	private static final long serialVersionUID = 1L;

	private String recordId;
	private String schema;
	private String data;
	private Date insertDate;
	private String source;

	public Record() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Record(String schema, String data) {
		super();
		this.schema = schema;
		this.data = data;
	}

	public String getRecordId() {
		return recordId;
	}

	public void setRecordId(String recordId) {
		this.recordId = recordId;
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public Date getInsertDate() {
		return insertDate;
	}

	public void setInsertDate(Date insertDate) {
		this.insertDate = insertDate;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	@Override
	public String toString() {
		return "Record [recordId=" + recordId + ", schema=" + schema + ", data=" + data + ", insertDate=" + insertDate
				+ ", source=" + source + "]";
	}

}
