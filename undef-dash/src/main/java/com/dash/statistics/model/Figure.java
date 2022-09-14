package com.dash.statistics.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "Figure")
public class Figure {

	@JsonIgnore
	private long figureId;
	private String key;
	private String value;
	@JsonIgnore
	private Pull pull;

	public Figure() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Figure(String key, String value, Pull pull) {
		super();
		this.key = key;
		this.value = value;
		this.pull = pull;
	}

	@Id
	@GeneratedValue
	public long getFigureId() {
		return figureId;
	}

	public void setFigureId(long figureId) {
		this.figureId = figureId;
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

	@ManyToOne
	@JoinColumn(name = "pullId")
	@OnDelete(action = OnDeleteAction.CASCADE)
	public Pull getPull() {
		return pull;
	}

	public void setPull(Pull pull) {
		this.pull = pull;
	}

}
