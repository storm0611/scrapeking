package com.interior.managedbean;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.annotation.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.primefaces.event.FlowEvent;

import com.interior.api.dao.SubjectDao;
import com.interior.api.model.Response;
import com.interior.api.model.Status;
import com.interior.model.Campaign;
import com.interior.model.GmailFailedEmail;

@Named
@ViewScoped
public class GmailFailedEmailManagedBean implements Serializable {

	private static final long serialVersionUID = 1L;

	final static Logger logger = Logger.getLogger(GmailFailedEmailManagedBean.class);

	public static final String DASHBOARD_REDIRECT = "dashboard.xhtml";

	@Inject
	@ManagedProperty("#{applicationManagedBean}")
	private ApplicationManagedBean applicationManagedBean;

	@Inject
	@ManagedProperty("#{sessionManagedBean}")
	private SessionManagedBean sessionManagedBean;

	private Campaign targetCampaign;
	private List<GmailFailedEmail> gmailFailedEmails;
	
	public Campaign getTargetCampaign() {
		return targetCampaign;
	}

	public void setTargetCampaign(Campaign targetCampaign) {
		this.targetCampaign = targetCampaign;
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

	public List<GmailFailedEmail> getGmailFailedEmails() {
		return gmailFailedEmails;
	}

	public void setGmailFailedEmails(List<GmailFailedEmail> gmailFailedEmails) {
		this.gmailFailedEmails = gmailFailedEmails;
	}

	@PostConstruct
	public void init() {
		try {

			Flash flash = FacesContext.getCurrentInstance().getExternalContext().getFlash();
			if (flash.get("selectedCampaign") != null) {

				this.targetCampaign = (Campaign) flash.get("selectedCampaign");

				// load campaign subjects
				Response<GmailFailedEmail> response = SubjectDao.getCampaignFailedEmails(this.targetCampaign.getCampaignId());
				
				Status status = response.getStatus();
				if (status.equals(Status.SUCCESS)) 
					this.gmailFailedEmails = response.getData();
			} else {
				FacesContext.getCurrentInstance().getExternalContext().redirect(DASHBOARD_REDIRECT);
			}

		} catch (Exception ex) {
			logger.error("method init error : ", ex);
		}
	}

	public String onFlowProcess(FlowEvent event) {
		return event.getNewStep();
	}

}
