package com.interior.managedbean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.annotation.ManagedProperty;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.interior.api.dao.CampaignDao;
import com.interior.api.dao.SubjectDao;
import com.interior.api.dao.TaskDao;
import com.interior.api.model.Response;
import com.interior.api.model.Status;
import com.interior.model.Campaign;
import com.interior.model.Subject;
import com.interior.model.Task;

@Named
@ViewScoped
public class ManageCampaignManagedBean implements Serializable {

	final static Logger logger = Logger.getLogger(SessionManagedBean.class);

	private static final long serialVersionUID = 1L;

	public static final String DASHBOARD_REDIRECT = "dashboard.xhtml";
	public static final String CRUD_CAMPAIGN_REDIRECT = "crud-campaign.xhtml";
	public static final String TASK_REDIRECT = "task.xhtml";
	public static final String LIST_LEADS_REDIRECT = "list-campaign-leads.xhtml";
	public static final String UPLOAD_SUBJECTS_REDIRECT = "upload-subjects.xhtml";
	public static final String FAILED_EMAILS_REDIRECT = "failed-emails.xhtml";
	public static final String EMAIL_WISE_REDIRECT = "emailwise-count.xhtml";

	private Task selectedTask;
	private List<Task> listTasks;

	private Campaign campaign;
	
	private long trigge1SuccessEmailCount;
	
	private long trigge2SuccessEmailCount;
	
	private long trigge3SuccessEmailCount;
	
	private long trigge1FailEmailCount;
	
	private long trigge2FailEmailCount;
	
	private long trigge3FailEmailCount;
	
	private long unsubscribeCount;

	public long getTrigge1SuccessEmailCount() {
		return trigge1SuccessEmailCount;
	}

	public void setTrigge1SuccessEmailCount(long trigge1SuccessEmailCount) {
		this.trigge1SuccessEmailCount = trigge1SuccessEmailCount;
	}

	public long getTrigge2SuccessEmailCount() {
		return trigge2SuccessEmailCount;
	}

	public void setTrigge2SuccessEmailCount(long trigge2SuccessEmailCount) {
		this.trigge2SuccessEmailCount = trigge2SuccessEmailCount;
	}

	public long getTrigge3SuccessEmailCount() {
		return trigge3SuccessEmailCount;
	}

	public void setTrigge3SuccessEmailCount(long trigge3SuccessEmailCount) {
		this.trigge3SuccessEmailCount = trigge3SuccessEmailCount;
	}

	public long getTrigge1FailEmailCount() {
		return trigge1FailEmailCount;
	}

	public void setTrigge1FailEmailCount(long trigge1FailEmailCount) {
		this.trigge1FailEmailCount = trigge1FailEmailCount;
	}

	public long getTrigge2FailEmailCount() {
		return trigge2FailEmailCount;
	}

	public void setTrigge2FailEmailCount(long trigge2FailEmailCount) {
		this.trigge2FailEmailCount = trigge2FailEmailCount;
	}

	public long getTrigge3FailEmailCount() {
		return trigge3FailEmailCount;
	}

	public void setTrigge3FailEmailCount(long trigge3FailEmailCount) {
		this.trigge3FailEmailCount = trigge3FailEmailCount;
	}

	public long getUnsubscribeCount() {
		return unsubscribeCount;
	}

	public void setUnsubscribeCount(long unsubscribeCount) {
		this.unsubscribeCount = unsubscribeCount;
	}

	public Campaign getCampaign() {
		return campaign;
	}

	public void setCampaign(Campaign campaign) {
		this.campaign = campaign;
	}

	public Task getSelectedTask() {
		return selectedTask;
	}

	public void setSelectedTask(Task selectedTask) {
		this.selectedTask = selectedTask;
	}

	@Inject
	@ManagedProperty("#{applicationManagedBean}")
	private ApplicationManagedBean applicationManagedBean;

	@Inject
	@ManagedProperty("#{sessionManagedBean}")
	private SessionManagedBean sessionManagedBean;

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

	public List<Task> getListTasks() {
		return listTasks;
	}

	public void setListTasks(List<Task> listTasks) {
		this.listTasks = listTasks;
	}

	@PostConstruct
	public void init() {
		try {

			Flash flash = FacesContext.getCurrentInstance().getExternalContext().getFlash();
			if (flash.get("selectedCampaign") != null) {
				this.campaign = (Campaign) flash.get("selectedCampaign");

				Response<Task> resp = TaskDao.getCampaignListTasks(this.campaign);
				Status responseStatus = resp.getStatus();
				if (responseStatus.equals(Status.SUCCESS)) {
					this.listTasks = resp.getData();
				}
				
				// load campaign subjects
				Response<Subject> respListSubject = SubjectDao.getCampaignSubjects(this.campaign.getCampaignId());
				
				Status status = respListSubject.getStatus();
				if (status.equals(Status.SUCCESS) && !StringUtils.isEmpty(respListSubject.getMessage())) {
					Map<String, Long> retMap = new Gson().fromJson(
							respListSubject.getMessage(), new TypeToken<HashMap<String, Long>>() {}.getType()
					);
					this.trigge1SuccessEmailCount = retMap.get("trigge1SuccessEmailCount");
					this.trigge2SuccessEmailCount = retMap.get("trigge2SuccessEmailCount");
					this.trigge3SuccessEmailCount = retMap.get("trigge3SuccessEmailCount");
					
					this.trigge1FailEmailCount = retMap.get("trigge1FailEmailCount");
					this.trigge2FailEmailCount = retMap.get("trigge2FailEmailCount");
					this.trigge3FailEmailCount = retMap.get("trigge3FailEmailCount");
					
					this.unsubscribeCount = retMap.get("unsubscribeCount");
				}

			} else {
				FacesContext.getCurrentInstance().getExternalContext().redirect(DASHBOARD_REDIRECT);
			}

		} catch (Exception ex) {
			logger.error("method init error : ", ex);
		}
	}

	public void btnUploadRecordsFile() {
		try {

			Flash flash = FacesContext.getCurrentInstance().getExternalContext().getFlash();
			flash.put("selectedCampaign", this.campaign);
			FacesContext.getCurrentInstance().getExternalContext().redirect(UPLOAD_SUBJECTS_REDIRECT);

		} catch (Exception ex) {
			logger.error("method btnUploadRecordsFile error : ", ex);
		}
	}

	public void btnDeleteCampaign() {
		try {

			Response<Campaign> respDeleteCampaign = CampaignDao.deleteCampaign(this.campaign);
			Status responseStatus = respDeleteCampaign.getStatus();
			if (responseStatus.equals(Status.SUCCESS)) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, "campaign was successfully deleted", ""));

				FacesContext.getCurrentInstance().getExternalContext().redirect(DASHBOARD_REDIRECT);

			} else {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"failed to delete " + this.campaign.getName() + " : " + respDeleteCampaign.getMessage(), ""));
			}

		} catch (Exception ex) {
			logger.error("method btnEditCampaign error : ", ex);
		}

	}

	public void btnEditCampaign() {
		try {

			Flash flash = FacesContext.getCurrentInstance().getExternalContext().getFlash();
			flash.put("operation", "edit");
			flash.put("selectedCampaign", this.campaign);
			FacesContext.getCurrentInstance().getExternalContext().redirect(CRUD_CAMPAIGN_REDIRECT);

		} catch (Exception ex) {
			logger.error("method btnEditCampaign error : ", ex);
		}
	}

	public void btnScheduleNewTask() {
		try {

			Flash flash = FacesContext.getCurrentInstance().getExternalContext().getFlash();
			flash.put("operation", "create");
			flash.put("selectedCampaign", this.campaign);
			FacesContext.getCurrentInstance().getExternalContext().redirect(TASK_REDIRECT);

		} catch (Exception ex) {
			logger.error("method btnScheduleNewTask error : ", ex);
		}
	}

	public void btnEditTask() {
		try {

			Flash flash = FacesContext.getCurrentInstance().getExternalContext().getFlash();
			flash.put("operation", "edit");
			flash.put("selectedTask", this.selectedTask);
			FacesContext.getCurrentInstance().getExternalContext().redirect(TASK_REDIRECT);

		} catch (Exception ex) {
			logger.error("method btnScheduleNewTask error : ", ex);
		}
	}

	public void btnDeleteTask(String deleteDialogConfirmValue) {
		try {

			if (deleteDialogConfirmValue != null && deleteDialogConfirmValue.toLowerCase().equals("delete")) {

				Response<Task> respDeleteTask = TaskDao.deleteTask(this.selectedTask);
				Status responseStatus = respDeleteTask.getStatus();
				if (responseStatus.equals(Status.SUCCESS)) {
					this.listTasks.remove(this.selectedTask);
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_INFO, "task was successfully deleted", ""));

				} else {
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"failed to delete " + this.selectedTask.getTaskId() + " : " + respDeleteTask.getMessage(),
							""));
				}
			}
		} catch (Exception ex) {
			logger.error("method btnDeleteTask error : ", ex);
		}
	}

	public void btnViewCollectedLeads() {
		try {

			Flash flash = FacesContext.getCurrentInstance().getExternalContext().getFlash();
			flash.put("selectedCampaign", this.campaign);
			FacesContext.getCurrentInstance().getExternalContext().redirect(LIST_LEADS_REDIRECT);

		} catch (Exception ex) {
			logger.error("method btnViewCollectedLeads error : ", ex);
		}
	}

	public void btnStartStopTask() {
		try {

			String operation = "";
			if (this.selectedTask.getStatus().equals(com.interior.model.Status.active))
				operation = "stop";
			else
				operation = "start";

			Response<Task> respUpdateTask = TaskDao.startStopTask(this.selectedTask, operation);
			Status responseStatus = respUpdateTask.getStatus();
			if (responseStatus.equals(Status.SUCCESS)) {

				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
						"task was successfully " + operation.toUpperCase(), ""));

				for (Task task : listTasks) {
					if (task.getTaskId() == this.selectedTask.getTaskId()) {
						if (operation.equals("stop")) {
							task.setStatus(com.interior.model.Status.inactive);
						} else {
							task.setStatus(com.interior.model.Status.active);
						}
					}
				}

			} else {
				FacesContext.getCurrentInstance()
						.addMessage(null,
								new FacesMessage(
										FacesMessage.SEVERITY_ERROR, "failed to " + operation.toUpperCase() + " "
												+ this.selectedTask.getTaskId() + " : " + respUpdateTask.getMessage(),
										""));
			}

		} catch (Exception ex) {
			logger.error("method btnStartStopTask error : ", ex);
		}
	}
	
	public void btnFailedEmails() {
		try {
			
			Flash flash = FacesContext.getCurrentInstance().getExternalContext().getFlash();
			flash.put("selectedCampaign", this.campaign);
			FacesContext.getCurrentInstance().getExternalContext().redirect(FAILED_EMAILS_REDIRECT);
			
		} catch (Exception ex) {
			logger.error("method btnFailedEmails error : ", ex);
		}
	}
	
	public void btnEmailWiseStatus() {
		try {

			Flash flash = FacesContext.getCurrentInstance().getExternalContext().getFlash();
			flash.put("selectedCampaign", this.campaign);
			FacesContext.getCurrentInstance().getExternalContext().redirect(EMAIL_WISE_REDIRECT);

		} catch (Exception ex) {
			logger.error("method btnEmailWiseStatus error : ", ex);
		}
	}
}
