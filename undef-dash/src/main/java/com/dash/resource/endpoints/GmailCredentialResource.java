package com.dash.resource.endpoints;

import java.util.Arrays;
import java.util.Date;
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

import com.dash.dao.AccountGmailCredentialDao;
import com.dash.dao.GmailCredentialDao;
import com.dash.model.Account;
import com.dash.model.AccountGmailCredential;
import com.dash.model.GmailCredential;
import com.dash.resource.model.Response;
import com.dash.resource.model.Status;
import com.dash.undefdash.ApplicationFactory;

@RestController
@RequestMapping("/gmailCredential")
public class GmailCredentialResource {
	
	private static final Logger logger = LoggerFactory.getLogger(GmailCredentialResource.class);

	@Autowired
	private ApplicationFactory appFactory;

	@GetMapping("/getListGmailCredentials")
	public Response<GmailCredential> getListGmailCredentials() {
		Response<GmailCredential> response = new Response<GmailCredential>();
		try {

			Session session = appFactory.getSession();
			Account account = (Account) session.load(Account.class, 1);

			Optional<List<GmailCredential>> optListGmailCredentials = GmailCredentialDao.getListGmailCredentialsPerAccount(session, account.getAccountId());
			response.setStatus(Status.SUCCESS);

			if (optListGmailCredentials.isPresent()) {
				response.setData(optListGmailCredentials.get());
			}
			return response;

		} catch (Exception ex) {
			logger.error("method getListGmailCredentials error : ", ex);
			response.setStatus(Status.ERROR);
			response.setMessage("internal server error : " + ex.getMessage());
			return response;
		}
	}

	@PostMapping("/addGmailCredential")
	public Response<GmailCredential> addGmailCredential(@RequestParam("username") String username, @RequestParam("password") String password, @RequestParam("perDay") String perDay, @RequestParam("analyticsId") String analyticsId, @RequestParam("proxyIp") String proxyIp, @RequestParam("proxyPort") String proxyPort) {
		Response<GmailCredential> response = new Response<GmailCredential>();
		try {

			Session session = appFactory.getSession();
			Account account = (Account) session.load(Account.class, 1);

			GmailCredential gmailCredential = new GmailCredential(username, password, new Date(), Long.valueOf(perDay), analyticsId, account, 0, proxyIp, Long.valueOf(proxyPort));
			GmailCredentialDao.saveGmailCredential(session, gmailCredential);

			// assign this gmailCredential to current account
			AccountGmailCredentialDao.saveGmailCredential(session, new AccountGmailCredential(
					(Account) session.load(Account.class, (int) account.getAccountId()), gmailCredential, new Date()));
			response.setStatus(Status.SUCCESS);
			response.setData(Arrays.asList(gmailCredential));
			response.setMessage("gmail credential added");
			return response;

		} catch (Exception ex) {
			logger.error("method addGmailCredential error : ", ex);
			response.setStatus(Status.ERROR);
			response.setMessage("internal server error : " + ex.getMessage());
			return response;
		}
	}

	@PostMapping("/updateGmailCredential")
	public Response<GmailCredential> editGmailCredential(@RequestParam("gmailCredentialId") String gmailCredentialId, @RequestParam("username") String username, @RequestParam("password") String password, @RequestParam("perDay") String perDay, @RequestParam("analyticsId") String analyticsId, @RequestParam("proxyIp") String proxyIp, @RequestParam("proxyPort") String proxyPort) {
		Response<GmailCredential> response = new Response<GmailCredential>();
		try {

			Session session = appFactory.getSession();

			Optional<GmailCredential> optGmailCredential = GmailCredentialDao.getGmailCredentialById(session, Long.valueOf(gmailCredentialId));
			if (optGmailCredential.isPresent() == false) {
				response.setStatus(Status.ERROR);
				response.setMessage("gmail credential not found");
				return response;
			} else {
				GmailCredential gmailCredential = optGmailCredential.get();
				gmailCredential.setAnalyticsId(analyticsId);
				gmailCredential.setPassword(password);
				gmailCredential.setPerDay(Long.valueOf(perDay));
				gmailCredential.setUsername(username);
				gmailCredential.setProxyIp(proxyIp);
				gmailCredential.setProxyPort(Long.valueOf(proxyPort));
				GmailCredentialDao.updateGmailCredential(session, gmailCredential);

				response.setStatus(Status.SUCCESS);
				response.setData(Arrays.asList(gmailCredential));
				response.setMessage("gmail credential updated");
				return response;
			}

		} catch (Exception ex) {
			logger.error("method editGmailCredential error : ", ex);
			response.setStatus(Status.ERROR);
			response.setMessage("internal server error : " + ex.getMessage());
			return response;
		}
	}

	@GetMapping("/deleteGmailCredential")
	public Response<String> deleteGmailCredential(@RequestParam("gmailCredentialId") long gmailCredentialId) {
		Response<String> response = new Response<String>();
		try {

			Session session = appFactory.getSession();

			Optional<GmailCredential> optGmailCredential = GmailCredentialDao.getGmailCredentialById(session, gmailCredentialId);
			if (optGmailCredential.isPresent() == false) {
				response.setStatus(Status.ERROR);
				response.setMessage("gmail credential not found");
				return response;
			} else {

				GmailCredential gmailCredential = optGmailCredential.get();
				// now mark the source as deleted
				gmailCredential.setDeleted(true);
				GmailCredentialDao.updateGmailCredential(session, gmailCredential);
				response.setStatus(Status.SUCCESS);
				response.setMessage("deleted successfully");
				return response;

			}

		} catch (Exception ex) {
			logger.error("method deleteGmailCredential error : ", ex);
			response.setStatus(Status.ERROR);
			response.setMessage("internal server error : " + ex.getMessage());
			return response;
		}
	}

}
