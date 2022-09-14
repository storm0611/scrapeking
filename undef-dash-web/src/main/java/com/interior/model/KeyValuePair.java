package com.interior.model;

import java.io.Serializable;

public class KeyValuePair implements Serializable {
	private static final long serialVersionUID = 1L;

	private int id;
	private String key;
	private String value;

	public KeyValuePair() {
		super();
		// TODO Auto-generated constructor stub
	}

	public KeyValuePair(String key, String value) {
		super();
		this.key = key;
		this.value = value;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
