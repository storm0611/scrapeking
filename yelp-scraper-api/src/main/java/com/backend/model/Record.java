package com.backend.model;

import java.io.Serializable;

public class Record implements Serializable {
	private static final long serialVersionUID = 1L;

	private String name;
	private String email;
	private String phone;
	private String fullAddress;

	public Record() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Record(String name, String email, String phone, String fullAddress) {
		super();
		this.name = name;
		this.email = email;
		this.phone = phone;
		this.fullAddress = fullAddress;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getFullAddress() {
		return fullAddress;
	}

	public void setFullAddress(String fullAddress) {
		this.fullAddress = fullAddress;
	}

}
