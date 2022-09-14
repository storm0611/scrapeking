package com.dash.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "EmailTemplate")
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
	@JsonIgnore
	private Date created;
	@JsonIgnore
	private Account account;
	private boolean deleted;

	public EmailTemplate() {
		super();
		// TODO Auto-generated constructor stub
		this.deleted = false;
	}

	public EmailTemplate(String label, String subject, String content, String content2, String content3, String content4, String content5, String content6, Account account, Date created) {
		super();
		this.label = label;
		this.subject = subject;
		this.content = content;
		this.content2 = content2;
		this.content3 = content3;
		this.content4 = content4;
		this.content5 = content5;
		this.content6 = content6;
		this.created = created;
		this.account = account;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public long getEmailTemplateId() {
		return emailTemplateId;
	}

	public void setEmailTemplateId(long emailTemplateId) {
		this.emailTemplateId = emailTemplateId;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	@Type(type = "text")
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	@ManyToOne
	@JoinColumn(name = "accountId")
	@OnDelete(action = OnDeleteAction.CASCADE)
	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	@Type(type = "text")
	public String getContent2() {
		return content2;
	}

	public void setContent2(String content2) {
		this.content2 = content2;
	}

	@Type(type = "text")
	public String getContent3() {
		return content3;
	}

	public void setContent3(String content3) {
		this.content3 = content3;
	}

	@Type(type = "text")
	public String getContent4() {
		return content4;
	}

	public void setContent4(String content4) {
		this.content4 = content4;
	}

	@Type(type = "text")
	public String getContent5() {
		return content5;
	}

	public void setContent5(String content5) {
		this.content5 = content5;
	}

	@Type(type = "text")
	public String getContent6() {
		return content6;
	}

	public void setContent6(String content6) {
		this.content6 = content6;
	}

}
