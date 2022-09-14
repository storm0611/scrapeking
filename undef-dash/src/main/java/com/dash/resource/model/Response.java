package com.dash.resource.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Response<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	private Date timestamp;
	private Status status;
	private String message;
	private List<T> data;

	public Response() {
		super();
		timestamp = new Date();
		// TODO Auto-generated constructor stub
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<T> getData() {
		return data;
	}

	public void setData(List<T> data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "Response [timestamp=" + timestamp + ", status=" + status + ", message=" + message + ", data=" + data
				+ "]";
	}
	

}
