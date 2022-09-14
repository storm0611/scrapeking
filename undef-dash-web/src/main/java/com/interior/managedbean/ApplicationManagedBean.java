package com.interior.managedbean;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.annotation.FacesConfig;
import javax.faces.annotation.FacesConfig.Version;
import javax.inject.Named;

import org.apache.log4j.Logger;

@FacesConfig(version = Version.JSF_2_3)
@Named
@ApplicationScoped
public class ApplicationManagedBean {

	final static Logger logger = Logger.getLogger(ApplicationManagedBean.class);

	@PostConstruct
	public void init() { 
		System.out.println("just testing ");
		System.out.println("second push");
	}

}
