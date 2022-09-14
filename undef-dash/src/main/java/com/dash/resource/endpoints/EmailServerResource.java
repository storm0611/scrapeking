package com.dash.resource.endpoints;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dash.dao.EmailServerDao;
import com.dash.dao.EmailServerParameterDao;
import com.dash.dao.EmailServiceDao;
import com.dash.model.Account;
import com.dash.model.EmailServer;
import com.dash.model.EmailServerParameter;
import com.dash.model.EmailService;
import com.dash.resource.model.Response;
import com.dash.resource.model.Status;
import com.dash.undefdash.ApplicationFactory;

@RestController
@RequestMapping("/email-server")
public class EmailServerResource {
	private static final Logger logger = LoggerFactory.getLogger(EmailServerResource.class);

	@Autowired
	private ApplicationFactory appFactory;

	@GetMapping("/getListEmailServers")
	public Response<EmailServer> getListEmailServer() {
		Response<EmailServer> response = new Response<EmailServer>();
		try {

			Session session = appFactory.getSession();
			Account account = (Account) session.load(Account.class, 1);

			response.setData(new ArrayList<EmailServer>());
			Optional<List<EmailServer>> optListEmailServers = EmailServerDao.getListEmailServersByAccount(session,
					account.getAccountId());
			response.setStatus(Status.SUCCESS);
			if (optListEmailServers.isPresent())
				response.setData(optListEmailServers.get());

			return response;

		} catch (Exception ex) {
			logger.error("method getListEmailServers error : ", ex);
			response.setStatus(Status.ERROR);
			response.setMessage("internal server error : " + ex.getMessage());
			return response;
		}
	}

	@PostMapping("/addEmailServer")
	public Response<EmailServer> addEmailServer(@RequestParam("emailServiceId") long emailServiceId,
			@RequestParam("label") String label, @RequestParam("parameters") List<String> parameters) {
		Response<EmailServer> response = new Response<EmailServer>();
		try {

			Session session = appFactory.getSession();
			Account account = (Account) session.load(Account.class, 1);

			Optional<EmailService> optEmailService = EmailServiceDao.getEmailServiceById(session, emailServiceId);
			if (optEmailService.isPresent() == false) {
				response.setStatus(Status.ERROR);
				response.setMessage("email service not found");
				return response;
			}

			EmailServer newServer = new EmailServer(label, optEmailService.get(), account);
			newServer.setListServerParameters(new ArrayList<EmailServerParameter>());
			EmailServerDao.saveEmailServer(session, newServer);

			for (String entry : parameters) {
				EmailServerParameter newParam = new EmailServerParameter(entry.split("=")[0], entry.split("=")[1],
						newServer);
				EmailServerParameterDao.saveEmailServerParameter(session, newParam);
				newServer.getListServerParameters().add(newParam);
			}

			response.setStatus(Status.SUCCESS);
			response.setData(Arrays.asList(newServer));
			return response;

		} catch (Exception ex) {
			logger.error("method addEmailServer error : ", ex);
			response.setStatus(Status.ERROR);
			response.setMessage("internal server error : " + ex.getMessage());
			return response;
		}
	}

	@PostMapping("/editEmailServer")
	public Response<EmailServer> editEmailServer(@RequestParam("emailServerId") long emailServerId,
			@RequestParam("label") String label, @RequestParam("parameters") List<String> parameters) {
		Response<EmailServer> response = new Response<EmailServer>();
		try {

			Session session = appFactory.getSession();
			Account account = (Account) session.load(Account.class, 1);

			Optional<EmailServer> optEmailServer = EmailServerDao.getEmailServerById(session, emailServerId);

			if (optEmailServer.isPresent() == false) {
				response.setStatus(Status.ERROR);
				response.setMessage("email server not found");
				return response;
			} else {

				EmailServer emailServer = optEmailServer.get();
				emailServer.setLabel(label);
				EmailServerDao.updateEmailServer(session, emailServer);

				for (String entry : parameters) {
					String key = entry.split("=")[0];
					Optional<EmailServerParameter> optEP = EmailServerParameterDao
							.getEmailServerParameterByKeyAndEmailServer(session, key, emailServer.getEmailServerId());
					if (optEP.isPresent()) {
						EmailServerParameter parameter = optEP.get();
						parameter.setValue(entry.split("=")[1]);
						EmailServerParameterDao.updateEmailServerParameter(session, parameter);
					}
				}
			}

			response.setStatus(Status.SUCCESS);
			response.setMessage("completed");
			return response;

		} catch (Exception ex) {
			logger.error("method editEmailServer error : ", ex);
			response.setStatus(Status.ERROR);
			response.setMessage("internal server error : " + ex.getMessage());
			return response;
		}
	}

	@PostMapping("/deleteEmailServer")
	public Response<String> deleteEmailServer(@RequestParam("emailServerId") long emailServerId) {
		// TODO Auto-generated method stub
		Response<String> response = new Response<String>();
		try {

			Session session = appFactory.getSession();
			Account account = (Account) session.load(Account.class, 1);

			Optional<EmailServer> optEmailServer = EmailServerDao.getEmailServerById(session, emailServerId);
			if (optEmailServer.isPresent() == false) {
				response.setStatus(Status.ERROR);
				response.setMessage("email server not found");
				return response;
			} else {
				EmailServer emailServer = optEmailServer.get();
				emailServer.setDeleted(true);
				EmailServerDao.updateEmailServer(session, emailServer);
				response.setStatus(Status.SUCCESS);
				response.setMessage("deleted successfully");
				return response;
			}

		} catch (Exception ex) {
			logger.error("method deleteEmailServer error : ", ex);
			response.setStatus(Status.ERROR);
			response.setMessage("internal server error : " + ex.getMessage());
			return response;
		}

	}

}
