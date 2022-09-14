package com.emailing.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "subject", indexes = { @Index(columnList = "email", name = "subject_email_idx") })
public class Subject implements Serializable {
	private static final long serialVersionUID = 1L;

	private String subjectId;
	private String source;
	private String name;
	private String phone;
	private String email;
	private Date insertDate;
	@JsonIgnore
	private List<String> metadata;

	@JsonIgnore
	private List<Message> listMessages;

	private boolean blacklistedWord;
	
	private String unsubscribeToken;
	
	private Boolean unsubscribe;
	
	private Boolean sent1;
	
	private Boolean sent2;
	
	private Boolean sent3;
	
	private Boolean sent1Read;
	
	private Boolean sent2Read;
	
	private Boolean sent3Read;
	
	private Boolean sent1Reply;
	
	private Boolean sent2Reply;
	
	private Boolean sent3Reply;

	public Subject() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Id
	public String getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(String subjectId) {
		this.subjectId = subjectId;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getInsertDate() {
		return insertDate;
	}

	public void setInsertDate(Date insertDate) {
		this.insertDate = insertDate;
	}

	@ElementCollection
	public List<String> getMetadata() {
		return metadata;
	}

	public void setMetadata(List<String> metadata) {
		this.metadata = metadata;
	}

	@OneToMany(targetEntity = Message.class, mappedBy = "subject", cascade = { CascadeType.ALL })
	@OnDelete(action = OnDeleteAction.CASCADE)
	public List<Message> getListMessages() {
		return listMessages;
	}

	public void setListMessages(List<Message> listMessages) {
		this.listMessages = listMessages;
	}

	public boolean isBlacklistedWord() {
		return blacklistedWord;
	}

	public void setBlacklistedWord(boolean blacklistedWord) {
		this.blacklistedWord = blacklistedWord;
	}
	
	public String getUnsubscribeToken() {
		return unsubscribeToken;
	}

	public void setUnsubscribeToken(String unsubscribeToken) {
		this.unsubscribeToken = unsubscribeToken;
	}

	public Boolean getUnsubscribe() {
		return unsubscribe;
	}

	public void setUnsubscribe(Boolean unsubscribe) {
		this.unsubscribe = unsubscribe;
	}

	public Boolean getSent1() {
		return sent1;
	}

	public void setSent1(Boolean sent1) {
		this.sent1 = sent1;
	}

	public Boolean getSent2() {
		return sent2;
	}

	public void setSent2(Boolean sent2) {
		this.sent2 = sent2;
	}

	public Boolean getSent3() {
		return sent3;
	}

	public void setSent3(Boolean sent3) {
		this.sent3 = sent3;
	}

	public Boolean getSent1Read() {
		return sent1Read;
	}

	public void setSent1Read(Boolean sent1Read) {
		this.sent1Read = sent1Read;
	}

	public Boolean getSent2Read() {
		return sent2Read;
	}

	public void setSent2Read(Boolean sent2Read) {
		this.sent2Read = sent2Read;
	}

	public Boolean getSent3Read() {
		return sent3Read;
	}

	public void setSent3Read(Boolean sent3Read) {
		this.sent3Read = sent3Read;
	}

	public Boolean getSent1Reply() {
		return sent1Reply;
	}

	public void setSent1Reply(Boolean sent1Reply) {
		this.sent1Reply = sent1Reply;
	}

	public Boolean getSent2Reply() {
		return sent2Reply;
	}

	public void setSent2Reply(Boolean sent2Reply) {
		this.sent2Reply = sent2Reply;
	}

	public Boolean getSent3Reply() {
		return sent3Reply;
	}

	public void setSent3Reply(Boolean sent3Reply) {
		this.sent3Reply = sent3Reply;
	}

	@Override
	public String toString() {
		return "Subject [subjectId=" + subjectId + ", source=" + source + ", name=" + name + ", phone=" + phone
				+ ", email=" + email + ", insertDate=" + insertDate + ", metadata=" + metadata + ", listMessages="
				+ listMessages + ", blacklistedWord=" + blacklistedWord + ", unsubscribeToken=" + unsubscribeToken
				+ ", unsubscribe=" + unsubscribe + ", sent1=" + sent1 + ", sent2=" + sent2 + ", sent3=" + sent3
				+ ", sent1Read=" + sent1Read + ", sent2Read=" + sent2Read + ", sent3Read=" + sent3Read + ", sent1Reply="
				+ sent1Reply + ", sent2Reply=" + sent2Reply + ", sent3Reply=" + sent3Reply + "]";
	}

}
