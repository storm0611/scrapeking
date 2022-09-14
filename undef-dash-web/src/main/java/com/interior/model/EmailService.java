package com.interior.model;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EmailService implements Serializable {

	private static final long serialVersionUID = 1L;

	private long emailServiceId;
	private String name;
	private List<String> listParameters;

	public EmailService() {
		super();
		listParameters = new ArrayList<String>();
		// TODO Auto-generated constructor stub
	}

	public long getEmailServiceId() {
		return emailServiceId;
	}

	public void setEmailServiceId(long emailServiceId) {
		this.emailServiceId = emailServiceId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getListParameters() {
		return listParameters;
	}

	public void setListParameters(List<String> listParameters) {
		this.listParameters = listParameters;
	}

	@Override
	public String toString() {
		return "EmailService [emailServiceId=" + emailServiceId + ", name=" + name + ", listParameters="
				+ Arrays.toString(listParameters.toArray()) + "]";
	}

}
