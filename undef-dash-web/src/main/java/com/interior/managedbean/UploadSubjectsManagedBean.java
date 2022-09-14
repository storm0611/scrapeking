package com.interior.managedbean;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
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

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;

import com.interior.api.dao.SubjectDao;
import com.interior.api.model.Response;
import com.interior.api.model.Status;
import com.interior.model.Campaign;
import com.interior.model.Record;
import com.interior.model.Subject;

@Named
@ViewScoped
public class UploadSubjectsManagedBean implements Serializable {

	private static final long serialVersionUID = 1L;
	final static Logger logger = Logger.getLogger(UploadSubjectsManagedBean.class);

	public static final String DASHBOARD_REDIRECT = "dashboard.xhtml";

	private Campaign campaign;
	private String fileName;
	private List<Subject> listSubjects;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public List<Subject> getListSubjects() {
		return listSubjects;
	}

	public void setListSubjects(List<Subject> listSubjects) {
		this.listSubjects = listSubjects;
	}

	public Campaign getCampaign() {
		return campaign;
	}

	public void setCampaign(Campaign campaign) {
		this.campaign = campaign;
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

			Flash flash = FacesContext.getCurrentInstance().getExternalContext().getFlash();
			if (flash.get("selectedCampaign") != null) {
				this.campaign = (Campaign) flash.get("selectedCampaign");
				this.listSubjects = new ArrayList<Subject>();
			} else {
				FacesContext.getCurrentInstance().getExternalContext().redirect(DASHBOARD_REDIRECT);
			}

		} catch (Exception ex) {
			logger.error("method init error : ", ex);
		}
	}

	public void btnSubmitSubjects() {
		try {

			if (this.listSubjects.size() <= 0) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
						"nothing to persist , make sure you upload a file first", ""));
			} else {

				Response<Subject> resp = SubjectDao.uploadListSubjects(this.campaign, this.listSubjects);
				if (resp.getStatus().equals(Status.SUCCESS)) {
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_INFO, " File was successfully added", ""));
					this.listSubjects = new ArrayList<Subject>();
				} else {
					FacesContext.getCurrentInstance().addMessage(null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed : " + resp.getMessage(), ""));
				}
			}
		} catch (Exception ex) {
			logger.error("method btnSubmitSubjects error : ", ex);
		}
	}

	public void upload(FileUploadEvent event) {
		try {
			InputStream is = event.getFile().getInputStream();
			if (is != null) {

				this.fileName = event.getFile().getFileName();
				Reader reader = new InputStreamReader(is);
				CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withDelimiter(';'));

				boolean header = true;
				for (CSVRecord csvRecord : csvParser) {

					String name = StringUtils.normalizeSpace(csvRecord.get(0)).trim();
					String email = StringUtils.normalizeSpace(csvRecord.get(1)).trim();

					System.out.println("email : " + email);
					if (header == false) {
						Subject newSubject = new Subject();
						newSubject.setName(name);
						newSubject.setEmail(email);
						this.listSubjects.add(newSubject);
					}

					header = false;
				}
				csvParser.close();
				reader.close();
				PrimeFaces.current().ajax().update("form-2");
			}

		} catch (Exception ex) {
			logger.error("method upload error : ", ex);
		}
	}

}
