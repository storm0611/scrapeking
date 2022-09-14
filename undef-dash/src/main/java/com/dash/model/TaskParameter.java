package com.dash.model;

import java.io.Serializable;

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
@Table(name = "TaskParameter")
public class TaskParameter implements Serializable {
	private static final long serialVersionUID = 1L;

	@JsonIgnore
	private long taskParameterId;
	private String key;
	private String value;
	@JsonIgnore
	private Task task;

	public TaskParameter() {
		super();
		// TODO Auto-generated constructor stub
	}

	public TaskParameter(String key, String value, Task task) {
		super();
		this.key = key;
		this.value = value;
		this.task = task;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public long getTaskParameterId() {
		return taskParameterId;
	}

	public void setTaskParameterId(long taskParameterId) {
		this.taskParameterId = taskParameterId;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@Type(type = "text")
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@ManyToOne
	@JoinColumn(name = "taskId")
	@OnDelete(action = OnDeleteAction.CASCADE)
	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

}
