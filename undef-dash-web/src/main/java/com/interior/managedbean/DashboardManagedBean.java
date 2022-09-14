package com.interior.managedbean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.annotation.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;

import com.interior.api.dao.CampaignDao;
import com.interior.api.dao.PullDao;
import com.interior.api.dao.SourceDao;
import com.interior.api.dao.TaskDao;
import com.interior.api.model.Response;
import com.interior.api.model.Status;
import com.interior.model.Campaign;
import com.interior.model.Source;
import com.interior.model.Task;

@Named
@ViewScoped
public class DashboardManagedBean implements Serializable {
	private static final long serialVersionUID = 1L;

	final static Logger logger = Logger.getLogger(DashboardManagedBean.class);

	public static final String MANAGE_CAMPAIGN_REDIRECT = "mn-campaign.xhtml";
	public static final String LIST_LEADS_REDIRECT = "list-campaign-leads.xhtml";

	@Inject
	@ManagedProperty("#{applicationManagedBean}")
	private ApplicationManagedBean applicationManagedBean;

	@Inject
	@ManagedProperty("#{sessionManagedBean}")
	private SessionManagedBean sessionManagedBean;

	private Date lastPullDate;
	private int totalLeadsCount;
	private int taskCount;

	private List<Source> listSources;
	private List<Campaign> listCampaigns;
	private Campaign selectedCampaign;

	public List<Source> getListSources() {
		return listSources;
	}

	public void setListSources(List<Source> listSources) {
		this.listSources = listSources;
	}

	public ApplicationManagedBean getApplicationManagedBean() {
		return applicationManagedBean;
	}

	public void setApplicationManagedBean(ApplicationManagedBean applicationManagedBean) {
		this.applicationManagedBean = applicationManagedBean;
	}

	public SessionManagedBean getSessionManagedBean() {
		return sessionManagedBean;
	}

	public void setSessionManagedBean(SessionManagedBean sessionManagedBean) {
		this.sessionManagedBean = sessionManagedBean;
	}

	public Date getLastPullDate() {
		return lastPullDate;
	}

	public void setLastPullDate(Date lastPullDate) {
		this.lastPullDate = lastPullDate;
	}

	public int getTotalLeadsCount() {
		return totalLeadsCount;
	}

	public void setTotalLeadsCount(int totalLeadsCount) {
		this.totalLeadsCount = totalLeadsCount;
	}

	public int getTaskCount() {
		return taskCount;
	}

	public void setTaskCount(int taskCount) {
		this.taskCount = taskCount;
	}

	public List<Campaign> getListCampaigns() {
		return listCampaigns;
	}

	public void setListCampaigns(List<Campaign> listCampaigns) {
		this.listCampaigns = listCampaigns;
	}

	public Campaign getSelectedCampaign() {
		return selectedCampaign;
	}

	public void setSelectedCampaign(Campaign selectedCampaign) {
		this.selectedCampaign = selectedCampaign;
	}

	@PostConstruct
	public void init() {
		try {

			this.listCampaigns = new ArrayList<Campaign>();
			Response<Campaign> respListCampaigns = CampaignDao.getListCampaigns();

			this.listSources = new ArrayList<Source>();

			Response<Source> respListSources = SourceDao.getListSources();
			if (respListSources.getStatus().equals(Status.SUCCESS))
				this.listSources = respListSources.getData();

			if (respListCampaigns.getStatus().equals(Status.SUCCESS)) {
				this.listCampaigns = respListCampaigns.getData();
				for (Campaign campaign : listCampaigns) {

					PullDao.getLastPull(campaign);
					if (campaign.getPull().getMapFigures().containsKey("subject_count"))
						this.totalLeadsCount = totalLeadsCount
								+ Integer.valueOf(campaign.getPull().getMapFigures().get("subject_count"));

					this.lastPullDate = campaign.getPull().getDate();

					Response<Task> respListTasks = TaskDao.getCampaignListTasks(campaign);
					Status responseStatus = respListTasks.getStatus();
					if (responseStatus.equals(Status.SUCCESS)) {
						campaign.setListTasks(respListTasks.getData());
						this.taskCount = this.taskCount + campaign.getListTasks().size();
					}
				}
			}

		} catch (Exception ex) {
			logger.error("method init error : ", ex);
		}
	}

	public void btnViewCampaign() {
		try {
			Flash flash = FacesContext.getCurrentInstance().getExternalContext().getFlash();
			flash.put("selectedCampaign", this.selectedCampaign);
			FacesContext.getCurrentInstance().getExternalContext().redirect(MANAGE_CAMPAIGN_REDIRECT);
		} catch (Exception ex) {
			logger.error("method btnViewCampaign error : ", ex);
		}
	}

	public void btnViewLeads() {
		try {
			Flash flash = FacesContext.getCurrentInstance().getExternalContext().getFlash();
			flash.put("selectedCampaign", this.selectedCampaign);
			FacesContext.getCurrentInstance().getExternalContext().redirect(LIST_LEADS_REDIRECT);
		} catch (Exception ex) {
			logger.error("method btnViewLeads error : ", ex);
		}
	}

}
