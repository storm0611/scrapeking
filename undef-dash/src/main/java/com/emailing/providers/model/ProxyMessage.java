package com.emailing.providers.model;

import java.io.Serializable;

public class ProxyMessage implements Serializable {
	private static final long serialVersionUID = 1L;

	private String messageId;
	private String unsubscribeToken;

	public ProxyMessage() {
		super();
		// TODO Auto-generated constructor stub
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public String getUnsubscribeToken() {
		return unsubscribeToken;
	}

	public void setUnsubscribeToken(String unsubscribeToken) {
		this.unsubscribeToken = unsubscribeToken;
	}

}
