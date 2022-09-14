package com.interior.model;

import java.io.Serializable;

public class EmailTemplate implements Serializable {
	private static final long serialVersionUID = 1L;

	private long emailTemplateId;
	private String label;
	private String subject;
	private String content;
	private String content2;
	private String content3;
	private String content4;
	private String content5;
	private String content6;

	public EmailTemplate() {
		super();
		this.label = "";
		this.subject = "";
		this.content = "";
		this.content2 = "";
		this.content3 = "";
		this.content4 = "";
		this.content5 = "";
		this.content6 = "";
	}

	public long getEmailTemplateId() {
		return emailTemplateId;
	}

	public void setEmailTemplateId(long emailTemplateId) {
		this.emailTemplateId = emailTemplateId;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getContent2() {
		return content2;
	}

	public void setContent2(String content2) {
		this.content2 = content2;
	}

	public String getContent3() {
		return content3;
	}

	public void setContent3(String content3) {
		this.content3 = content3;
	}

	public String getContent4() {
		return content4;
	}

	public void setContent4(String content4) {
		this.content4 = content4;
	}

	public String getContent5() {
		return content5;
	}

	public void setContent5(String content5) {
		this.content5 = content5;
	}

	public String getContent6() {
		return content6;
	}

	public void setContent6(String content6) {
		this.content6 = content6;
	}

	@Override
	public String toString() {
		return "EmailTemplate [emailTemplateId=" + emailTemplateId + ", label=" + label + ", subject=" + subject
				+ ", content=" + content + ", content2=" + content2 + ", content3=" + content3 + ", content=4" + content4 + ", content5=" + content5 + ", content=6" + content6 + "]";
	}

}
