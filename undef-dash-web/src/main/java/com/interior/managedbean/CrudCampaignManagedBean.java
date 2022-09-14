package com.interior.managedbean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.annotation.ManagedProperty;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FlowEvent;

import com.interior.api.dao.CampaignDao;
import com.interior.api.dao.EmailServerDao;
import com.interior.api.dao.EmailTemplateDao;
import com.interior.api.dao.GmailCredentialDao;
import com.interior.api.model.Response;
import com.interior.api.model.Status;
import com.interior.model.Campaign;
import com.interior.model.EmailServer;
import com.interior.model.EmailTemplate;
import com.interior.model.GmailCredential;

@Named
@ViewScoped
public class CrudCampaignManagedBean implements Serializable {

	private static final long serialVersionUID = 1L;

	final static Logger logger = Logger.getLogger(CrudCampaignManagedBean.class);

	public static final String DASHBOARD_REDIRECT = "dashboard.xhtml";

	@Inject
	@ManagedProperty("#{applicationManagedBean}")
	private ApplicationManagedBean applicationManagedBean;

	@Inject
	@ManagedProperty("#{sessionManagedBean}")
	private SessionManagedBean sessionManagedBean;

	private String operation;

	private Campaign targetCampaign;
	private List<EmailTemplate> listEmailTemplate;
	private List<EmailServer> listEmailServer;
	private List<GmailCredential> gmailCredentials;
	private List<GmailCredential> selectedCredentials;
	
	public List<GmailCredential> getSelectedCredentials() {
		return selectedCredentials;
	}

	public void setSelectedCredentials(List<GmailCredential> selectedCredentials) {
		this.selectedCredentials = selectedCredentials;
	}

	public Campaign getTargetCampaign() {
		return targetCampaign;
	}

	public void setTargetCampaign(Campaign targetCampaign) {
		this.targetCampaign = targetCampaign;
	}

	public List<EmailTemplate> getListEmailTemplate() {
		return listEmailTemplate;
	}

	public void setListEmailTemplate(List<EmailTemplate> listEmailTemplate) {
		this.listEmailTemplate = listEmailTemplate;
	}

	public List<EmailServer> getListEmailServer() {
		return listEmailServer;
	}

	public void setListEmailServer(List<EmailServer> listEmailServer) {
		this.listEmailServer = listEmailServer;
	}

	public ApplicationManagedBean getApplicationManagedBean() {
		return applicationManagedBean;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
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
	
	public List<GmailCredential> getGmailCredentials() {
		return gmailCredentials;
	}

	public void setGmailCredentials(List<GmailCredential> gmailCredentials) {
		this.gmailCredentials = gmailCredentials;
	}

	@PostConstruct
	public void init() {
		try {

			// load gmail credentials
			Response<GmailCredential> respListGmailCredential = GmailCredentialDao.getListGmailCredentials();
			Status responseStatus = respListGmailCredential.getStatus();
			if (responseStatus.equals(Status.SUCCESS)) {
				this.gmailCredentials = respListGmailCredential.getData();
			}
			
			// load email servers
			Response<EmailServer> respListEmailServer = EmailServerDao.getListEmailServers();
			if (respListEmailServer.getStatus().equals(Status.SUCCESS))
				this.listEmailServer = respListEmailServer.getData();

			this.listEmailTemplate = new ArrayList<EmailTemplate>();
			Response<EmailTemplate> respListEmailTemplate = EmailTemplateDao.getListEmailTemplate();
			if (respListEmailTemplate.getStatus().equals(Status.SUCCESS))
				this.listEmailTemplate = respListEmailTemplate.getData();

			Flash flash = FacesContext.getCurrentInstance().getExternalContext().getFlash();
			if (flash.get("operation") == null) {

				this.operation = "create";

				this.targetCampaign = new Campaign();
				this.targetCampaign.setEmailingDays(new ArrayList<String>());

				this.targetCampaign.getEmailingDays().add("Monday");
				this.targetCampaign.getEmailingDays().add("Tuesday");
				this.targetCampaign.getEmailingDays().add("Wednesday");
				this.targetCampaign.getEmailingDays().add("Thursday");

				this.targetCampaign.getEmailingDays().add("Friday");
				this.targetCampaign.getEmailingDays().add("Saturday");
				this.targetCampaign.getEmailingDays().add("Sunday");

				this.targetCampaign.setEnableEmailing(true);
				this.targetCampaign.setListEmailServer(new ArrayList<EmailServer>());

			} else if (flash.get("selectedCampaign") != null) {

				this.operation = "edit";

				this.targetCampaign = (Campaign) flash.get("selectedCampaign");

				ArrayList<EmailServer> flist = new ArrayList<EmailServer>();
				for (EmailServer emailServer : this.targetCampaign.getListEmailServer()) {
					for (EmailServer original : this.listEmailServer) {
						if (original.getEmailServerId() == emailServer.getEmailServerId()) {
							flist.add(original);
						}
					}
				}
				this.targetCampaign.setListEmailServer(flist);

				for (EmailTemplate emailTemplate : this.listEmailTemplate) {
					if (this.targetCampaign.getFirstAttemptEmail().getEmailTemplateId() == emailTemplate
							.getEmailTemplateId()) {
						this.targetCampaign.setFirstAttemptEmail(emailTemplate);

					} else if (this.targetCampaign.getFollowupEmail().getEmailTemplateId() == emailTemplate
							.getEmailTemplateId()) {
						this.targetCampaign.setFollowupEmail(emailTemplate);

					} else if (this.targetCampaign.getSecondAttemptEmail().getEmailTemplateId() == emailTemplate
							.getEmailTemplateId()) {
						this.targetCampaign.setSecondAttemptEmail(emailTemplate);
					}
				}

			} else {
				FacesContext.getCurrentInstance().getExternalContext().redirect(DASHBOARD_REDIRECT);
			}

		} catch (Exception ex) {
			logger.error("method init error : ", ex);
		}
	}

	public void btnSubmitCampaign() {
		try {

			if (this.targetCampaign.isEnableEmailing()) {

				if (this.targetCampaign.getFirstAttemptEmail() == null && this.targetCampaign.getFollowupEmail() == null
						&& this.targetCampaign.getSecondAttemptEmail() == null) {

					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
							"please make sure to choose an email template", ""));
					return;
				}

				if ((this.targetCampaign.getFollowupEmail() != null
						&& this.targetCampaign.getFirstAttemptEmail() == null)
						|| (this.targetCampaign.getSecondAttemptEmail() != null
								&& this.targetCampaign.getFollowupEmail() == null)) {

					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_WARN, "select email template in correct order", ""));

					return;
				}
				
				if (this.selectedCredentials != null) {
					this.targetCampaign.setSelectedCredentials(this.selectedCredentials);
		        }
			}

			if (operation.equals("create")) {

				Response<Campaign> respAddCampaign = CampaignDao.crudCampaign(this.targetCampaign, "create");
				if (respAddCampaign.getStatus().equals(Status.SUCCESS)) {
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
							this.targetCampaign.getName() + " was successfully added", ""));
					FacesContext.getCurrentInstance().getExternalContext().redirect(DASHBOARD_REDIRECT);
				} else {
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"failed to save new campaign : " + respAddCampaign.getMessage(), ""));
				}

			} else {

				Response<Campaign> respAddCampaign = CampaignDao.crudCampaign(this.targetCampaign, "update");
				if (respAddCampaign.getStatus().equals(Status.SUCCESS)) {
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
							this.targetCampaign.getName() + " was successfully edited", ""));
					FacesContext.getCurrentInstance().getExternalContext().redirect(DASHBOARD_REDIRECT);
				} else {
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
							"failed to save edit campaign : " + respAddCampaign.getMessage(), ""));
				}

			}

		} catch (Exception ex) {
			logger.error("method btnSubmitCampaign error : ", ex);
		}
	}

	public String onFlowProcess(FlowEvent event) {
		return event.getNewStep();
	}
	
	public void checkboxChangeListener(AjaxBehaviorEvent event){
		UIComponent component=(UIComponent)event.getSource();
		System.out.println(this.listEmailServer);
		Map<String, Object> attrs = component.getAttributes(); 
		Object attributeValue = component.getAttributes().get("targetCampaign");
//	       for (EmailServer post : this.listEmailServer) {
//	            if (post.isChosen() && "No".equals(post.getDeclined())) {
//	                anythingChosen = true;
//	                break;
//	            }
//	        }
//		  String attributeName=(String)component.findComponent("targetCampaign").getAttributes().get("value");
//		  Object attributeValue=component.findComponent("value").getAttributes().get("value");
//		  component.findComponent("fu").getAttributes().put(attributeName,attributeValue);
//	     RenderBean rb = (RenderBean) FacesContext.getCurrentInstance()
//	            .getExternalContext().getSessionMap().get("renderBean");
//	        if(e.getNewValue().toString().equals("gmail")) {
//	        	System.out.println("gmail");
////	        	FacesContext.getCurrentInstance().getExternalContext().
//	        	RequestContext.getCurrentInstance().execute("PF('yourdialogid').show()");
	        	PrimeFaces.current().executeScript("PF('dlg-edit-details').show()");
//	        }
	}
	
    public void saveSelectedCredentials() {
    	System.out.println(this.selectedCredentials);
//        this.products.removeAll(this.selectedProducts);
//        this.selectedProducts = null;
//        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Products Removed"));
//        PrimeFaces.current().ajax().update("form:messages", "form:dt-products");
//        PrimeFaces.current().executeScript("PF('dtProducts').clearFilters()");
    }
    
    public String getCheckedCredential() {
        if (this.selectedCredentials != null) {
            int size = this.selectedCredentials.size();
            return size > 1 ? size + " products selected" : "1 product selected";
        }
        return "Delete";
    }

}
