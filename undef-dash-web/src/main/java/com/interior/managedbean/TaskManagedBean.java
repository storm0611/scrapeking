package com.interior.managedbean;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.annotation.ManagedProperty;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;

import com.interior.api.dao.InstanceDao;
import com.interior.api.dao.SourceDao;
import com.interior.api.dao.TaskDao;
import com.interior.api.model.Response;
import com.interior.api.model.Status;
import com.interior.model.Campaign;
import com.interior.model.Source;
import com.interior.model.Task;

@Named
@ViewScoped
public class TaskManagedBean implements Serializable {

	private static final long serialVersionUID = 1L;

	final static Logger logger = Logger.getLogger(TaskManagedBean.class);

	public static final String MANAGE_CAMPAIGN_REDIRECT = "mn-campaign.xhtml";
	public static final String DASHBOARD_REDIRECT = "dashboard.xhtml";

	private String operation;

	private boolean save = false;

	private Task task;
	private List<Source> listSources;

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
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

	public List<Source> getListSources() {
		return listSources;
	}

	public void setListSources(List<Source> listSources) {
		this.listSources = listSources;
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	public boolean isSave() {
		return save;
	}

	public void setSave(boolean save) {
		this.save = save;
	}

	@Inject
	@ManagedProperty("#{applicationManagedBean}")
	private ApplicationManagedBean applicationManagedBean;

	@Inject
	@ManagedProperty("#{sessionManagedBean}")
	private SessionManagedBean sessionManagedBean;

	@PostConstruct
	public void init() {
		try {

			this.operation = "create";
			Flash flash = FacesContext.getCurrentInstance().getExternalContext().getFlash();

			Campaign selectedCampaign = (Campaign) flash.get("selectedCampaign");
			Task selectedTask = (Task) flash.get("selectedTask");

			if (selectedCampaign == null && selectedTask == null) {

				flash.put("selectedCampaign", this.task.getCampaign());
				FacesContext.getCurrentInstance().getExternalContext().redirect(MANAGE_CAMPAIGN_REDIRECT);

			} else {

				Response<Source> respListSources = SourceDao.getListSources();
				if (respListSources.getStatus().equals(Status.SUCCESS))
					this.listSources = respListSources.getData();

				if (selectedTask != null) {

					this.operation = "edit";

					this.task = new Task();

					this.task.setCampaign(selectedTask.getCampaign());
					this.task.setTaskId(selectedTask.getTaskId());
					this.task.setCreated(selectedTask.getCreated());
					this.task.setRepeatForever(selectedTask.isRepeatForever());
					this.task.setCronUnit(selectedTask.getCronUnit());
					this.task.setCronValue(selectedTask.getCronValue());
					this.task.setExpireDate(selectedTask.getExpireDate());
					this.task.setParameters(selectedTask.getParameters());

//					this.task.setSource(selectedTask.getInstance().getSource());
//					this.task.getSource().getListInstance().add(selectedTask.getInstance()) ; 
//					this.task.setInstance(selectedTask.getInstance());

				} else {

					this.operation = "create";

					this.task = new Task();
					this.task.setRepeatForever(true);
					this.task.setCampaign(selectedCampaign);
					this.task.setCronUnit("HOURS");
					this.task.setCronValue(10);
					this.task.setParameters(new LinkedHashMap<String, String>());

				}

			}

		} catch (Exception ex) {
			logger.error("method init error : ", ex);
		}
	}

	public void onInstanceChange() {
		try {

			this.task.getParameters().clear();
			Response<String> respListInstanceParameters = InstanceDao.getInstanceAPIParameters(this.task.getInstance());
			if (respListInstanceParameters.getStatus().equals(Status.SUCCESS)) {
				if (respListInstanceParameters.getData() != null) {
					for (String key : respListInstanceParameters.getData())
						this.task.getParameters().put(key, "");
				}
				save = true;
			} else {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
						"API offline cannot load parameters for : " + this.task.getInstance().getUrl(), ""));
				save = false;
			}

		} catch (Exception ex) {
			logger.error("method onInstanceChange error : ", ex);
		}
	}

	public void submitTask() {
		try {

			if (this.operation.equalsIgnoreCase("create")) {

				Response<Task> respCreateTask = TaskDao.createTask(task);
				if (respCreateTask.getStatus().equals(Status.SUCCESS)) {
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
							task.getTaskId() + " was successufly created", ""));

					Flash flash = FacesContext.getCurrentInstance().getExternalContext().getFlash();
					flash.put("selectedCampaign", this.task.getCampaign());
					FacesContext.getCurrentInstance().getExternalContext().redirect(MANAGE_CAMPAIGN_REDIRECT);

				} else {
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"failed to schedule new task : " + respCreateTask.getMessage(), ""));
				}

			} else if (this.operation.equalsIgnoreCase("edit")) {
				Response<Task> respEditTask = TaskDao.editTask(task);

				if (respEditTask.getStatus().equals(Status.SUCCESS)) {
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
							task.getTaskId() + " was successufly edited", ""));

					Flash flash = FacesContext.getCurrentInstance().getExternalContext().getFlash();
					flash.put("selectedCampaign", this.task.getCampaign());
					FacesContext.getCurrentInstance().getExternalContext().redirect(MANAGE_CAMPAIGN_REDIRECT);

				} else {
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"failed to schedule edit task : " + respEditTask.getMessage(), ""));
				}
			}

		} catch (Exception ex) {
			logger.error("method createTask error : ", ex);
		}
	}

}
