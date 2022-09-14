package com.interior.managedbean;

import java.io.IOException;
import java.io.Serializable;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.annotation.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.primefaces.PrimeFaces;

import com.interior.api.dao.AccountDao;
import com.interior.api.model.Response;
import com.interior.api.model.Status;
import com.interior.model.Account;
import com.interior.ui.utils.Notification;

@Named
@RequestScoped
public class AuthenticationManagedBean implements Serializable {

	private static final long serialVersionUID = 1L;
	final static Logger logger = Logger.getLogger(AuthenticationManagedBean.class);

	public static final String HOME_PAGE_REDIRECT = "secured/dashboard.xhtml";
	public static final String LOGIN_PAGE_REDIRECT = "login.xhtml";

	@Inject
	@ManagedProperty("#{applicationManagedBean}")
	private ApplicationManagedBean applicationManagedBean;

	@Inject
	@ManagedProperty("#{sessionManagedBean}")
	private SessionManagedBean sessionManagedBean;

	private String username;
	private String password;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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

		} catch (Exception ex) {
			logger.error("method init error : ", ex);
		}
	}

	public void login() {
		try {
			System.out.println("authentication for : " + username);
			logger.info("authentication for : " + username);

			if (this.username != null && this.username.trim().isEmpty() == false && this.password != null
					&& this.password.trim().isEmpty() == false) {

				Response<Account> respAccount = AccountDao.authentication(this.username, this.password);

				if (respAccount.getStatus().equals(Status.SUCCESS)) {
					if (respAccount.getData() == null) {
						Notification.notifyError(respAccount.getMessage());
						FacesContext.getCurrentInstance().getExternalContext().redirect(LOGIN_PAGE_REDIRECT);
						// add login attempt later
					} else {
						FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("account",
								respAccount.getData().get(0));
						FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
						FacesContext.getCurrentInstance().getExternalContext().redirect(HOME_PAGE_REDIRECT);
					}

				} else {
					// internal error
					Notification.notifyError(respAccount.getMessage());
					FacesContext.getCurrentInstance().getExternalContext().redirect(LOGIN_PAGE_REDIRECT);
				}

			}

		} catch (Exception ex) {
			logger.error("method login error : ", ex);
		}

	}

	public void logout() {
		try {
			FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
			FacesContext.getCurrentInstance().getExternalContext().redirect(LOGIN_PAGE_REDIRECT);
		} catch (IOException ex) {
			logger.error("method logout error : ", ex);
		}
	}

}
