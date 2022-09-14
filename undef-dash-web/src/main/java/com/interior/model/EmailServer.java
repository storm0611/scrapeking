package com.interior.model;

import java.io.Serializable;
import java.util.LinkedHashMap;

public class EmailServer implements Serializable {
	private static final long serialVersionUID = 1L;

	private long emailServerId;
	private String label;
	private EmailService emailService;
	private LinkedHashMap<String, String> mapParameters;

	public EmailServer() {
		super();
		this.emailService = new EmailService();
		this.mapParameters = new LinkedHashMap<String, String>();
		// TODO Auto-generated constructor stub
	}

	public long getEmailServerId() {
		return emailServerId;
	}

	public void setEmailServerId(long emailServerId) {
		this.emailServerId = emailServerId;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public EmailService getEmailService() {
		return emailService;
	}

	public void setEmailService(EmailService emailService) {
		this.emailService = emailService;
	}

	public LinkedHashMap<String, String> getMapParameters() {
		return mapParameters;
	}

	public void setMapParameters(LinkedHashMap<String, String> mapParameters) {
		this.mapParameters = mapParameters;
	}

	@Override
	public String toString() {
		return "EmailServer [emailServerId=" + emailServerId + ", label=" + label + ", emailService=" + emailService
				+ ", mapParameters=" + mapParameters + "]";
	}

}
