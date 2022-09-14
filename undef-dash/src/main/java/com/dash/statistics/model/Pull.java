package com.dash.statistics.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.dash.model.Campaign;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "PULL")
public class Pull implements Serializable {
	private static final long serialVersionUID = 1L;
	@JsonIgnore
	private long pullId;
	private Date date;
	private List<Figure> listFigures;
	@JsonIgnore
	private Campaign campaign;

	public Pull() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Id
	@GeneratedValue
	public long getPullId() {
		return pullId;
	}

	public void setPullId(long pullId) {
		this.pullId = pullId;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@OneToMany(fetch = FetchType.EAGER, targetEntity = Figure.class, mappedBy = "pull")
	public List<Figure> getListFigures() {
		return listFigures;
	}

	public void setListFigures(List<Figure> listFigures) {
		this.listFigures = listFigures;
	}

	@ManyToOne
	@JoinColumn(name = "campaignId")
	@OnDelete(action = OnDeleteAction.CASCADE)
	public Campaign getCampaign() {
		return campaign;
	}

	public void setCampaign(Campaign campaign) {
		this.campaign = campaign;
	}

}
