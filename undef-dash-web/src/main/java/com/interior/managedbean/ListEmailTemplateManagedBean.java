package com.interior.managedbean;

import java.io.Serializable;
import java.util.ArrayList;
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

import com.interior.api.dao.EmailTemplateDao;
import com.interior.api.model.Response;
import com.interior.api.model.Status;
import com.interior.model.EmailTemplate;

@Named
@ViewScoped
public class ListEmailTemplateManagedBean implements Serializable {

	private static final long serialVersionUID = 1L;
	final static Logger logger = Logger.getLogger(ListEmailTemplateManagedBean.class);

	public static final String EDIT_EMAIl_TEMPLATE = "email-template.xhtml";

	private EmailTemplate selectedEmailTemplate;
	private List<EmailTemplate> listEmailTemplate;

	public EmailTemplate getSelectedEmailTemplate() {
		return selectedEmailTemplate;
	}

	public void setSelectedEmailTemplate(EmailTemplate selectedEmailTemplate) {
		this.selectedEmailTemplate = selectedEmailTemplate;
	}

	public List<EmailTemplate> getListEmailTemplate() {
		return listEmailTemplate;
	}

	public void setListEmailTemplate(List<EmailTemplate> listEmailTemplate) {
		this.listEmailTemplate = listEmailTemplate;
	}

	@Inject
	@ManagedProperty("#{sessionManagedBean}")
	private SessionManagedBean sessionManagedBean;

	@Inject
	@ManagedProperty("#{applicationManagedBean}")
	private ApplicationManagedBean applicationManagedBean;

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

	@PostConstruct
	public void init() {
		try {

			this.listEmailTemplate = new ArrayList<EmailTemplate>();
			Response<EmailTemplate> respListEmailTemplate = EmailTemplateDao.getListEmailTemplate();
			if (respListEmailTemplate.getStatus().equals(com.interior.api.model.Status.SUCCESS)) {
				this.listEmailTemplate = respListEmailTemplate.getData();
			}

		} catch (Exception ex) {
			logger.error("method init error : ", ex);
		}
	}

	public void btnEditEmailTemplate() {
		try {

			Flash flash = FacesContext.getCurrentInstance().getExternalContext().getFlash();
			flash.put("selectedEmailTemplate", this.selectedEmailTemplate);
			FacesContext.getCurrentInstance().getExternalContext().redirect(EDIT_EMAIl_TEMPLATE);

		} catch (Exception ex) {
			logger.error("method btnEditEmailTemplate error : ", ex);
		}
	}

	public void deleteEmailTemplate() {
		try {

			Response<EmailTemplate> respDeleteEmailTemplate = EmailTemplateDao
					.deleteEmailTemplate(this.selectedEmailTemplate);
			Status responseStatus = respDeleteEmailTemplate.getStatus();

			if (responseStatus.equals(Status.SUCCESS)) {
				this.listEmailTemplate.remove(this.selectedEmailTemplate);
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
						this.selectedEmailTemplate.getLabel() + " was successfully deleted", ""));
			} else {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"failed to delete email template : " + respDeleteEmailTemplate.getMessage(), ""));
			}

		} catch (Exception ex) {
			logger.error("method deleteEmailTemplate error : ", ex);
		}
	}

}
