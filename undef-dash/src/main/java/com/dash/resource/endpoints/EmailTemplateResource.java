package com.dash.resource.endpoints;

import java.util.ArrayList;
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

import com.dash.dao.EmailTemplateDao;
import com.dash.model.Account;
import com.dash.model.EmailTemplate;
import com.dash.resource.model.Response;
import com.dash.resource.model.Status;
import com.dash.undefdash.ApplicationFactory;

@RestController
@RequestMapping("/email-template")
public class EmailTemplateResource {

	private static final Logger logger = LoggerFactory.getLogger(EmailTemplateResource.class);

	@Autowired
	private ApplicationFactory appFactory;

	@GetMapping("/getListEmailTemplates")
	public Response<EmailTemplate> getListEmailTemplates() {
		Response<EmailTemplate> response = new Response<EmailTemplate>();
		try {
			Session session = appFactory.getSession();
			Account account = (Account) session.load(Account.class, 1);

			response.setData(new ArrayList<EmailTemplate>());
			Optional<List<EmailTemplate>> optListEmailTemplate = EmailTemplateDao.getListEmailTemplateByAccount(session,
					account.getAccountId());
			response.setStatus(Status.SUCCESS);
			if (optListEmailTemplate.isPresent())
				response.setData(optListEmailTemplate.get());

			return response;

		} catch (Exception ex) {
			logger.error("method getListEmailTemplates error : ", ex);
			response.setStatus(Status.ERROR);
			response.setMessage("internal server error : " + ex.getMessage());
			return response;
		}
	}

	@PostMapping("/addEmailTemplate")
	public Response<EmailTemplate> addEmailTemplate(@RequestParam("label") String label,
			@RequestParam("subject") String subject, @RequestParam("content") String content,
			@RequestParam("content2") String content2, @RequestParam("content3") String content3,
			@RequestParam("content4") String content4, @RequestParam("content5") String content5,
			@RequestParam("content6") String content6) {
		Response<EmailTemplate> response = new Response<EmailTemplate>();
		try {

			Session session = appFactory.getSession();
			Account account = (Account) session.load(Account.class, 1);

			EmailTemplate emailTemplate = new EmailTemplate(label, subject, content, content2, content3, content4, content5, content6, account, new Date());
			EmailTemplateDao.saveEmailTemplate(session, emailTemplate);

			response.setStatus(Status.SUCCESS);
			response.setData(Arrays.asList(emailTemplate));
			return response;

		} catch (Exception ex) {
			logger.error("method addEmailTemplate error : ", ex);
			response.setStatus(Status.ERROR);
			response.setMessage("internal server error : " + ex.getMessage());
			return response;
		}
	}

	@PostMapping("/editEmailTemplate")
	public Response<String> editEmailTemplate(@RequestParam("emailTemplateId") long emailTemplateId,
			@RequestParam("label") String label, @RequestParam("subject") String subject,
			@RequestParam("content") String content, @RequestParam("content2") String content2, @RequestParam("content3") String content3,
			@RequestParam("content4") String content4, @RequestParam("content5") String content5,
			@RequestParam("content6") String content6) {
		Response<String> response = new Response<String>();
		try {

			Session session = appFactory.getSession();
			Account account = (Account) session.load(Account.class, 1);

			Optional<EmailTemplate> optEmailTemplate = EmailTemplateDao.getEmailTemplateByIdAndAccount(session,
					emailTemplateId, account.getAccountId());

			if (optEmailTemplate.isPresent() == false) {
				response.setStatus(Status.ERROR);
				response.setMessage("email template not found");
				return response;
			} else {
				EmailTemplate emailTemplate = optEmailTemplate.get();

				emailTemplate.setLabel(label);
				emailTemplate.setSubject(subject);
				emailTemplate.setContent(content);
				emailTemplate.setContent2(content2);
				emailTemplate.setContent3(content3);
				emailTemplate.setContent4(content4);
				emailTemplate.setContent5(content5);
				emailTemplate.setContent6(content6);
				EmailTemplateDao.updateEmailTemplate(session, emailTemplate);

				response.setStatus(Status.SUCCESS);
				response.setData(Arrays.asList("SUCCESS"));
				return response;
			}

		} catch (Exception ex) {
			logger.error("method editEmailTemplate error : ", ex);
			response.setStatus(Status.ERROR);
			response.setMessage("internal server error : " + ex.getMessage());
			return response;
		}
	}

	@PostMapping("/deleteEmailTemplate")
	public Response<String> deleteEmailTemplate(@RequestParam("emailTemplateId") long emailTemplateId) {
		Response<String> response = new Response<String>();
		try {

			Session session = appFactory.getSession();
			Account account = (Account) session.load(Account.class, 1);

			Optional<EmailTemplate> optEmailTemplate = EmailTemplateDao.getEmailTemplateByIdAndAccount(session,
					emailTemplateId, account.getAccountId());
			if (optEmailTemplate.isPresent() == false) {
				response.setStatus(Status.ERROR);
				response.setMessage("emailTemplate not found");
				return response;

			} else {

				EmailTemplate emailTemplate = optEmailTemplate.get();
				emailTemplate.setDeleted(true);
				EmailTemplateDao.updateEmailTemplate(session, emailTemplate);

				response.setStatus(Status.SUCCESS);
				response.setMessage("deleted successfully");
				return response;

			}

		} catch (Exception ex) {
			logger.error("method deleteEmailTemplate error : ", ex);
			response.setStatus(Status.ERROR);
			response.setMessage("internal server error : " + ex.getMessage());
			return response;
		}
	}

}
