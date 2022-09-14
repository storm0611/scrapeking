package com.interior.managedbean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
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
import org.primefaces.PrimeFaces;

import com.google.gson.JsonObject;
import com.interior.api.dao.SourceDao;
import com.interior.api.model.Response;
import com.interior.api.model.Status;
import com.interior.model.Source;
import com.interior.ui.utils.Notification;

@Named
@ViewScoped
public class SourceManagedBean implements Serializable {

	private static final long serialVersionUID = 1L;
	final static Logger logger = Logger.getLogger(SourceManagedBean.class);
	public static final String MANAGE_SOURCE_PAGE_REDIRECT = "mn-source.xhtml";

	@Inject
	@ManagedProperty("#{applicationManagedBean}")
	private ApplicationManagedBean applicationManagedBean;

	@Inject
	@ManagedProperty("#{sessionManagedBean}")
	private SessionManagedBean sessionManagedBean;

	private List<Source> listSources;
	private Source newSource;
	private Source selectedSource;

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

	public Source getNewSource() {
		return newSource;
	}

	public void setNewSource(Source newSource) {
		this.newSource = newSource;
	}

	public Source getSelectedSource() {
		return selectedSource;
	}

	public void setSelectedSource(Source selectedSource) {
		this.selectedSource = selectedSource;
	}

	@PostConstruct
	public void init() {
		try {

			this.newSource = new Source();
			this.listSources = new ArrayList<Source>();

			Response<Source> respListSources = SourceDao.getListSources();
			Status responseStatus = respListSources.getStatus();
			if (responseStatus.equals(Status.SUCCESS)) {
				this.listSources = respListSources.getData();
			}

		} catch (Exception ex) {
			logger.error("method init error : ", ex);
		}
	}

	public void btnAddSource() {
		try {

			Response<Source> respAddSource = SourceDao.addSource(this.newSource);
			if (respAddSource.getStatus().equals(Status.SUCCESS)) {

				this.listSources.add(this.newSource);
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
						this.newSource.getTitle() + " was successfully added", ""));
				this.newSource = new Source();
			} else {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"failed to save new source : " + respAddSource.getMessage(), ""));
			}

		} catch (Exception ex) {
			logger.error("method btnAddSource error : ", ex);
		}
	}

	public void manageSource() {
		try {

			Flash flash = FacesContext.getCurrentInstance().getExternalContext().getFlash();
			flash.put("selectedSource", this.selectedSource);
			FacesContext.getCurrentInstance().getExternalContext().redirect(MANAGE_SOURCE_PAGE_REDIRECT);

		} catch (Exception ex) {
			logger.error("method manageSource error : ", ex);
		}
	}

	public void deleteSource() {
		try {

			Response<Source> respDeleteSource = SourceDao.deleteSource(this.selectedSource);
			Status responseStatus = respDeleteSource.getStatus();

			if (responseStatus.equals(Status.SUCCESS)) {
				listSources.remove(this.selectedSource);
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
						this.selectedSource.getTitle() + " was successfully deleted", ""));
			} else {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"failed to delete source : " + respDeleteSource.getMessage(), ""));
			}

		} catch (Exception ex) {
			logger.error("method deleteSources error : ", ex);
		}
	}

}
