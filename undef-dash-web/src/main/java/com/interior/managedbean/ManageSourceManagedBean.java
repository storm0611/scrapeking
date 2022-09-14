package com.interior.managedbean;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.faces.annotation.ManagedProperty;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.validator.routines.UrlValidator;
import org.apache.log4j.Logger;

import com.interior.api.dao.InstanceDao;
import com.interior.api.model.Response;
import com.interior.api.model.Status;
import com.interior.model.Instance;
import com.interior.model.Source;
import com.interior.ui.utils.Notification;

@Named
@ViewScoped
public class ManageSourceManagedBean implements Serializable {

	private static final long serialVersionUID = 1L;

	final static Logger logger = Logger.getLogger(ManageSourceManagedBean.class);
	public static final String SOURCES_PAGE_REDIRECT = "source.xhtml";

	@Inject
	@ManagedProperty("#{applicationManagedBean}")
	private ApplicationManagedBean applicationManagedBean;

	@Inject
	@ManagedProperty("#{sessionManagedBean}")
	private SessionManagedBean sessionManagedBean;

	private Source source;
	private Instance newInstance;
	private Instance selectedInstance;

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

	public Source getSource() {
		return source;
	}

	public void setSource(Source source) {
		this.source = source;
	}

	public Instance getNewInstance() {
		return newInstance;
	}

	public void setNewInstance(Instance newInstance) {
		this.newInstance = newInstance;
	}

	public Instance getSelectedInstance() {
		return selectedInstance;
	}

	public void setSelectedInstance(Instance selectedInstance) {
		this.selectedInstance = selectedInstance;
	}

	@PostConstruct
	public void init() {
		try {

			newInstance = new Instance();
			// get source selected from flash scope
			Flash flash = FacesContext.getCurrentInstance().getExternalContext().getFlash();
			if (flash.get("selectedSource") != null) {
				source = (Source) flash.get("selectedSource");
			} else {
				FacesContext.getCurrentInstance().getExternalContext().redirect(SOURCES_PAGE_REDIRECT);
			}

		} catch (Exception ex) {
			logger.error("method init error : ", ex);
		}
	}

	public void addInstance() {
		try {

			if (new UrlValidator().isValid(newInstance.getUrl()) == false) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"Input Error", "the URL you provided is incorrect"));
			} else {

				newInstance.setSource(source);
				Response<Instance> respAdd = InstanceDao.addInstance(newInstance);
				Status responseStatus = respAdd.getStatus();

				if (responseStatus.equals(Status.SUCCESS)) {
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_INFO, "success", "Instance saved successfully"));
					newInstance.setInstanceId(respAdd.getData().get(0).getInstanceId());
					source.getListInstance().add(newInstance);
					// clean inputs
					newInstance = new Instance();
				} else {
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", respAdd.getMessage()));
				}
			}

		} catch (Exception ex) {
			logger.error("method addInstance error : ", ex);
		}
	}

	public void deleteInstance(String deleteDialogConfirmValue) {
		try {
			if (deleteDialogConfirmValue != null && deleteDialogConfirmValue.toLowerCase().equals("delete")) {
				Response<Instance> respRemoveInstance = InstanceDao.deleteInstance(this.selectedInstance);
				Status responseStatus = respRemoveInstance.getStatus();

				if (responseStatus.equals(Status.SUCCESS)) {
					Notification.notifySuccess(responseStatus + " " + respRemoveInstance.getMessage());
					source.getListInstance().remove(this.selectedInstance);
				} else {
					Notification.notifyError(respRemoveInstance.getMessage());
				}

			}
		} catch (Exception ex) {
			logger.error("method deleteInstance error : ", ex);
		}
	}

	public void updatListInstancesStatus() {
		try {
			for (Instance instance : source.getListInstance())
				InstanceDao.updateInstanceStatus(instance);
		} catch (Exception ex) {
			logger.error("method updatListInstancesStatus error : ", ex);
		}
	}

}
