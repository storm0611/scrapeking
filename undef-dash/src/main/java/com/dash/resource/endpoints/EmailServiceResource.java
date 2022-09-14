package com.dash.resource.endpoints;

import java.util.List;
import java.util.Optional;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dash.dao.EmailServiceDao;
import com.dash.model.EmailService;
import com.dash.resource.model.Response;
import com.dash.resource.model.Status;
import com.dash.undefdash.ApplicationFactory;

@RestController
@RequestMapping("/email-service")
public class EmailServiceResource {
	private static final Logger logger = LoggerFactory.getLogger(EmailServiceResource.class);

	@Autowired
	private ApplicationFactory appFactory;

	@GetMapping("/getListServices")
	public Response<EmailService> getListEmailServices() {
		Response<EmailService> response = new Response<EmailService>();
		try {

			Session session = appFactory.getSession();
			Optional<List<EmailService>> optListEmailServices = EmailServiceDao.getListEmailServices(session);

			response.setStatus(Status.SUCCESS);
			if (optListEmailServices.isPresent())
				response.setData(optListEmailServices.get());
			return response;

		} catch (Exception ex) {
			logger.error("method getListServices error : ", ex);
			response.setStatus(Status.ERROR);
			response.setMessage("internal server error : " + ex.getMessage());
			return response;
		}
	}

}
