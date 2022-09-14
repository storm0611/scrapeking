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

import org.apache.log4j.Logger;

import com.interior.api.dao.EmailTemplateDao;
import com.interior.api.model.Response;
import com.interior.api.model.Status;
import com.interior.model.EmailTemplate;

@Named
@ViewScoped
public class EmailTemplateManagedBean implements Serializable {

	private static final long serialVersionUID = 1L;
	final static Logger logger = Logger.getLogger(EmailTemplateManagedBean.class);

	public static final String LIST_EMAIL_TEMPLATE = "list-email-template.xhtml";

	private final String regexName = "<strong style='background-color:yellow'>JOHN DOE</strong>";
	private final String regexSource = "<strong style='background-color:yellow'>WEBSITE</strong>";

	private EmailTemplate targetEmailTemplate;

	private String operation;
	private String labelHolder;
	private String subjectHolder;
	private String contentHolder;

	@Inject
	@ManagedProperty("#{sessionManagedBean}")
	private SessionManagedBean sessionManagedBean;

	@Inject
	@ManagedProperty("#{applicationManagedBean}")
	private ApplicationManagedBean applicationManagedBean;

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getLabelHolder() {
		return labelHolder;
	}

	public void setLabelHolder(String labelHolder) {
		this.labelHolder = labelHolder;
	}

	public String getSubjectHolder() {
		return subjectHolder;
	}

	public void setSubjectHolder(String subjectHolder) {
		this.subjectHolder = subjectHolder;
	}

	public String getContentHolder() {
		return contentHolder;
	}

	public void setContentHolder(String contentHolder) {
		this.contentHolder = contentHolder;
	}

	public EmailTemplate getTargetEmailTemplate() {
		return targetEmailTemplate;
	}

	public void setTargetEmailTemplate(EmailTemplate targetEmailTemplate) {
		this.targetEmailTemplate = targetEmailTemplate;
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

	@PostConstruct
	public void init() {
		try {

			this.operation = "add";
			this.targetEmailTemplate = new EmailTemplate();

			Flash flash = FacesContext.getCurrentInstance().getExternalContext().getFlash();
			if (flash.get("selectedEmailTemplate") != null) {
				this.targetEmailTemplate = (EmailTemplate) flash.get("selectedEmailTemplate");
				this.operation = "edit";
			}

		} catch (Exception ex) {
			logger.error("method init error : ", ex);
		}
	}

	public void btnSubmitEmailTemplate() {
		try {

			Response<EmailTemplate> respActionEmailTemplate = null;

			if (operation.equalsIgnoreCase("add"))
				respActionEmailTemplate = EmailTemplateDao.addEmailTemplate(this.targetEmailTemplate);
			else
				respActionEmailTemplate = EmailTemplateDao.editEmailTemplate(this.targetEmailTemplate);

			Status responseStatus = respActionEmailTemplate.getStatus();
			if (responseStatus.equals(Status.SUCCESS)) {
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, "operation was successfully performed", ""));
				this.targetEmailTemplate = new EmailTemplate();
			} else {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
						"failed to perform operation : " + respActionEmailTemplate.getMessage(), ""));
			}

		} catch (Exception ex) {
			logger.error("method btnSubmitEmailTemplate error : ", ex);
		}
	}

	public void btnPreview() {
		
		this.subjectHolder = "";
		this.contentHolder = "";
		
		String subject = this.targetEmailTemplate.getSubject();
		String content = this.targetEmailTemplate.getContent();
		
		this.subjectHolder = subject.replaceAll("\\{name\\}", this.regexName).replaceAll("\\{source\\}",
				this.regexSource);
		this.contentHolder = content.replaceAll("\\{name\\}", this.regexName).replaceAll("\\{source\\}",
				this.regexSource);
		
	}
	
	public void btnPreview2() {
		
		this.subjectHolder = "";
		this.contentHolder = "";
		
		String subject = this.targetEmailTemplate.getSubject();
		String content = this.targetEmailTemplate.getContent2();
		
		this.subjectHolder = subject.replaceAll("\\{name\\}", this.regexName).replaceAll("\\{source\\}",
				this.regexSource);
		this.contentHolder = content.replaceAll("\\{name\\}", this.regexName).replaceAll("\\{source\\}",
				this.regexSource);
		
	}
	
	public void btnPreview3() {
		
		this.subjectHolder = "";
		this.contentHolder = "";
		
		String subject = this.targetEmailTemplate.getSubject();
		String content = this.targetEmailTemplate.getContent3();
		
		this.subjectHolder = subject.replaceAll("\\{name\\}", this.regexName).replaceAll("\\{source\\}",
				this.regexSource);
		this.contentHolder = content.replaceAll("\\{name\\}", this.regexName).replaceAll("\\{source\\}",
				this.regexSource);
		
	}
	
	public void btnPreview4() {
		
		this.subjectHolder = "";
		this.contentHolder = "";
		
		String subject = this.targetEmailTemplate.getSubject();
		String content = this.targetEmailTemplate.getContent4();
		
		this.subjectHolder = subject.replaceAll("\\{name\\}", this.regexName).replaceAll("\\{source\\}",
				this.regexSource);
		this.contentHolder = content.replaceAll("\\{name\\}", this.regexName).replaceAll("\\{source\\}",
				this.regexSource);
		
	}
	
	public void btnPreview5() {
		
		this.subjectHolder = "";
		this.contentHolder = "";
		
		String subject = this.targetEmailTemplate.getSubject();
		String content = this.targetEmailTemplate.getContent5();
		
		this.subjectHolder = subject.replaceAll("\\{name\\}", this.regexName).replaceAll("\\{source\\}",
				this.regexSource);
		this.contentHolder = content.replaceAll("\\{name\\}", this.regexName).replaceAll("\\{source\\}",
				this.regexSource);
		
	}
	
	public void btnPreview6() {

		this.subjectHolder = "";
		this.contentHolder = "";

		String subject = this.targetEmailTemplate.getSubject();
		String content = this.targetEmailTemplate.getContent6();

		this.subjectHolder = subject.replaceAll("\\{name\\}", this.regexName).replaceAll("\\{source\\}",
				this.regexSource);
		this.contentHolder = content.replaceAll("\\{name\\}", this.regexName).replaceAll("\\{source\\}",
				this.regexSource);

	}

}
