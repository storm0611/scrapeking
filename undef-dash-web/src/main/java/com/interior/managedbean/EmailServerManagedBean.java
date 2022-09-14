package com.interior.managedbean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.annotation.ManagedProperty;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.primefaces.event.RowEditEvent;

import com.interior.api.dao.EmailServerDao;
import com.interior.api.dao.EmailServiceDao;
import com.interior.api.model.Response;
import com.interior.api.model.Status;
import com.interior.model.EmailServer;
import com.interior.model.EmailService;
import com.interior.ui.utils.Notification;

@Named
@ViewScoped
public class EmailServerManagedBean implements Serializable {

	private static final long serialVersionUID = 1L;
	final static Logger logger = Logger.getLogger(EmailServerManagedBean.class);

	@Inject
	@ManagedProperty("#{applicationManagedBean}")
	private ApplicationManagedBean applicationManagedBean;

	@Inject
	@ManagedProperty("#{sessionManagedBean}")
	private SessionManagedBean sessionManagedBean;

	private EmailServer newEmailServer;
	private List<EmailServer> listEmailServers;
	private EmailServer selectedEmailServer;
	// immutable
	private List<EmailService> referenceListEmailService;

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

	public EmailServer getNewEmailServer() {
		return newEmailServer;
	}

	public void setNewEmailServer(EmailServer newEmailServer) {
		this.newEmailServer = newEmailServer;
	}

	public List<EmailServer> getListEmailServers() {
		return listEmailServers;
	}

	public void setListEmailServers(List<EmailServer> listEmailServers) {
		this.listEmailServers = listEmailServers;
	}

	public EmailServer getSelectedEmailServer() {
		return selectedEmailServer;
	}

	public void setSelectedEmailServer(EmailServer selectedEmailServer) {
		this.selectedEmailServer = selectedEmailServer;
	}

	public List<EmailService> getReferenceListEmailService() {
		return referenceListEmailService;
	}

	public void setReferenceListEmailService(List<EmailService> referenceListEmailService) {
		this.referenceListEmailService = referenceListEmailService;
	}

	@PostConstruct
	public void init() {
		try {

			//// initialize empty object
			this.newEmailServer = new EmailServer();

			this.referenceListEmailService = new ArrayList<EmailService>();
			Response<EmailService> respListEmailServices = EmailServiceDao.getListEmailServices();
			if (respListEmailServices.getStatus().equals(com.interior.api.model.Status.SUCCESS)) {
				referenceListEmailService = respListEmailServices.getData();
				newEmailServer.setEmailService(referenceListEmailService.get(0));
			}

			this.listEmailServers = new ArrayList<EmailServer>();
			Response<EmailServer> respListEmailServer = EmailServerDao.getListEmailServers();
			Status status = respListEmailServer.getStatus();
			if (status.equals(Status.SUCCESS))
				this.listEmailServers = respListEmailServer.getData();

		} catch (Exception ex) {
			logger.error("method init error : ", ex);
		}
	}

	public void addEmailServer() {
		try {

			Response<EmailServer> respAddEmailServer = EmailServerDao.addEmailServer(this.newEmailServer);
			Status responseStatus = respAddEmailServer.getStatus();
			if (responseStatus.equals(Status.SUCCESS)) {
				this.listEmailServers.add(respAddEmailServer.getData().get(0));

				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
						this.newEmailServer.getLabel() + " was successfully added", ""));

				this.newEmailServer = new EmailServer();
				newEmailServer.setEmailService(referenceListEmailService.get(0));
			} else {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"failed to save new email server : " + respAddEmailServer.getMessage(), ""));
			}

		} catch (Exception ex) {
			logger.error("method addEmailServer error : ", ex);
		}
	}

	public void cleanForm() {
		try {
			this.newEmailServer = new EmailServer();
			this.newEmailServer.setEmailService(referenceListEmailService.get(0));
		} catch (Exception ex) {
			logger.error("method cleanForm error : ", ex);
		}
	}

	public void deleteEmailServer(String deleteDialogConfirmValue) {
		try {
			if (deleteDialogConfirmValue != null && deleteDialogConfirmValue.toLowerCase().equals("delete")) {

				Response<EmailServer> respDeleteEmailServer = EmailServerDao
						.deleteEmailServer(this.selectedEmailServer);
				Status responseStatus = respDeleteEmailServer.getStatus();
				if (responseStatus.equals(Status.SUCCESS)) {

					this.listEmailServers.remove(this.selectedEmailServer);
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
							this.selectedEmailServer.getLabel() + " was successfully deleted", ""));

				} else {
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, "failed to delete "
									+ this.selectedEmailServer.getLabel() + " : " + respDeleteEmailServer.getMessage(),
									""));
				}
			}
		} catch (Exception ex) {
			logger.error("method deleteEmailServer error : ", ex);
		}
	}

	public void onEmailServerParameterRowEdit(RowEditEvent<Map.Entry<String, String>> event) {
		try {
			Response<EmailServer> respEditParameter = EmailServerDao.editEmailServer(this.selectedEmailServer);
			Status responseStatus = respEditParameter.getStatus();
			if (responseStatus.equals(Status.SUCCESS)) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, "parameter was successfully updated", ""));
			} else {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"failed to edit parameter : " + respEditParameter.getMessage(), ""));
			}
		} catch (Exception ex) {
			logger.error("method onEmailServerParameterRowEdit error : ", ex);
		}
	}
}
