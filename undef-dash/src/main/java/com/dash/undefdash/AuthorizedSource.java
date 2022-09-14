package com.dash.undefdash;

import java.io.Serializable;

public class AuthorizedSource implements Serializable {
	private static final long serialVersionUID = 1L;

	private String application;
	private String apiKey;
	private boolean forTest;

	public AuthorizedSource() {
		super();
		// TODO Auto-generated constructor stub
	}

	public AuthorizedSource(String application, String apiKey, boolean forTest) {
		super();

		this.application = application;
		this.apiKey = apiKey;
		this.forTest = forTest;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public boolean isForTest() {
		return forTest;
	}

	public void setForTest(boolean forTest) {
		this.forTest = forTest;
	}

}
