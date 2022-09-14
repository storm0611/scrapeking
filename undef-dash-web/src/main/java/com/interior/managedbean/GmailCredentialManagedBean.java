package com.interior.managedbean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.annotation.ManagedProperty;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;

import com.interior.api.dao.GmailCredentialDao;
import com.interior.api.model.Response;
import com.interior.api.model.Status;
import com.interior.model.GmailCredential;

@Named
@ViewScoped
public class GmailCredentialManagedBean implements Serializable {

	private static final long serialVersionUID = 1L;
	final static Logger logger = Logger.getLogger(GmailCredentialManagedBean.class);
	public static final String GMAIL_PAGE_REDIRECT = "gmail.xhtml";

	@Inject
	@ManagedProperty("#{applicationManagedBean}")
	private ApplicationManagedBean applicationManagedBean;

	@Inject
	@ManagedProperty("#{sessionManagedBean}")
	private SessionManagedBean sessionManagedBean;

	private List<GmailCredential> listGmailCredentials;
	private GmailCredential newGmailCredential;
	private GmailCredential selectedGmailCredential;

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

	public List<GmailCredential> getListGmailCredentials() {
		return listGmailCredentials;
	}

	public void setListGmailCredentials(List<GmailCredential> listGmailCredentials) {
		this.listGmailCredentials = listGmailCredentials;
	}

	public GmailCredential getNewGmailCredential() {
		return newGmailCredential;
	}

	public void setNewGmailCredential(GmailCredential newGmailCredential) {
		this.newGmailCredential = newGmailCredential;
	}

	public GmailCredential getSelectedGmailCredential() {
		return selectedGmailCredential;
	}

	public void setSelectedGmailCredential(GmailCredential selectedGmailCredential) {
		this.selectedGmailCredential = selectedGmailCredential;
	}

	@PostConstruct
	public void init() {
		try {

			this.newGmailCredential = new GmailCredential();
			this.listGmailCredentials = new ArrayList<GmailCredential>();

			Response<GmailCredential> respListSources = GmailCredentialDao.getListGmailCredentials();
			Status responseStatus = respListSources.getStatus();
			if (responseStatus.equals(Status.SUCCESS)) {
				this.listGmailCredentials = respListSources.getData();
			}

		} catch (Exception ex) {
			logger.error("method init error : ", ex);
		}
	}

	public void btnAddGmailCredential() {
		try {
			
			Response<GmailCredential> respAddSource = GmailCredentialDao.addGmailCredential(this.newGmailCredential);
			if (respAddSource.getStatus().equals(Status.SUCCESS)) {
				
				this.listGmailCredentials.add(this.newGmailCredential);
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
						this.newGmailCredential.getUsername() + " was successfully added", ""));
				this.newGmailCredential = new GmailCredential();
			} else {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"failed to save new gmail credential : " + respAddSource.getMessage(), ""));
			}
			
		} catch (Exception ex) {
			logger.error("method btnAddSource error : ", ex);
		}
	}
	
	public void btnEditGmailCredential(GmailCredential selectedGmailCredential) {
		try {

			Response<GmailCredential> respAddSource = GmailCredentialDao.editGmailCredential(selectedGmailCredential);
			if (respAddSource.getStatus().equals(Status.SUCCESS)) {

				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
						selectedGmailCredential.getUsername() + " was successfully updated", ""));
			} else {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"failed to update gmail credential : " + respAddSource.getMessage(), ""));
			}

		} catch (Exception ex) {
			logger.error("method btnEditGmailCredential error : ", ex);
		}
	}

	public void deleteGmailCredential() {
		try {

			Response<GmailCredential> respDeleteSource = GmailCredentialDao.deleteGmailCredential(this.selectedGmailCredential);
			Status responseStatus = respDeleteSource.getStatus();

			if (responseStatus.equals(Status.SUCCESS)) {
				listGmailCredentials.remove(this.selectedGmailCredential);
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
						this.selectedGmailCredential.getUsername() + " was successfully deleted", ""));
			} else {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"failed to delete source : " + respDeleteSource.getMessage(), ""));
			}

		} catch (Exception ex) {
			logger.error("method deleteSources error : ", ex);
		}
	}

}
