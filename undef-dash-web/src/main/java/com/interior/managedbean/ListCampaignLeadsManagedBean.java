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
import org.primefaces.model.LazyDataModel;

import com.interior.api.dao.RecordDao;
import com.interior.api.model.Response;
import com.interior.model.Campaign;
import com.interior.model.Record;

@Named
@ViewScoped
public class ListCampaignLeadsManagedBean implements Serializable {

	private static final long serialVersionUID = 1L;
	final static Logger logger = Logger.getLogger(ListCampaignLeadsManagedBean.class);

	@Inject
	@ManagedProperty("#{sessionManagedBean}")
	private SessionManagedBean sessionManagedBean;

	@Inject
	@ManagedProperty("#{applicationManagedBean}")
	private ApplicationManagedBean applicationManagedBean;

	private Campaign campaign;
	private LazyDataModel<Record> lazyModel;

	public Campaign getCampaign() {
		return campaign;
	}

	public void setCampaign(Campaign campaign) {
		this.campaign = campaign;
	}

	public LazyDataModel<Record> getLazyModel() {
		return lazyModel;
	}

	public void setLazyModel(LazyDataModel<Record> lazyModel) {
		this.lazyModel = lazyModel;
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

			// get First page

			Flash flash = FacesContext.getCurrentInstance().getExternalContext().getFlash();
			if (flash.get("selectedCampaign") != null) {

				this.campaign = (Campaign) flash.get("selectedCampaign");
				Response<Record> respListObjects = RecordDao.getListCampaignSubjects(this.campaign);
				if (respListObjects.getStatus().equals(com.interior.api.model.Status.SUCCESS)) {
					System.out.println("respListObjects.getData().size() : " + respListObjects.getData().size());
					lazyModel = new LazyRecordDataModel((List<Record>) respListObjects.getData());
		
				}

			} else {
				// redirect
			}
		} catch (Exception ex) {
			logger.error("method init error : ", ex);
		}
	}

}
