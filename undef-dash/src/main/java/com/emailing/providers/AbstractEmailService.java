package com.emailing.providers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dash.model.Campaign;
import com.dash.model.CampaignGmailCredential;
import com.dash.model.Email;
import com.dash.model.EmailServer;
import com.dash.undefdash.ApplicationFactory;
import com.emailing.model.Subject;
import com.emailing.model.GmailTrigger1Data;
import com.emailing.model.GmailTrigger2Data;
import com.emailing.model.GmailTrigger3Data;
import com.emailing.providers.model.ProxyEvent;
import com.emailing.providers.model.ProxyMessage;

@Component
public abstract class AbstractEmailService {

	@Autowired
	private ApplicationFactory appFactory;

	public String nameTag = "\\{name\\}";
	public String unsubscribeTag = "\\{unsubscribe\\}";
	public String serverUrlTag = "\\{serverUrl\\}";
	public String analyticUrlTag = "\\{analyticUrl\\}";

	public abstract Optional<ProxyMessage> sendEmail(EmailServer emailServer, Campaign campaign, Subject subject,
			Email email);

	public abstract Optional<List<ProxyEvent>> fetchServerEvents(EmailServer emailServer);

	public ApplicationFactory getAppFactory() {
		return appFactory;
	}

	public void setAppFactory(ApplicationFactory appFactory) {
		this.appFactory = appFactory;
	}

	public abstract Optional<GmailTrigger1Data> sendEmailFromGmailServer(CampaignGmailCredential campaignGmailCredential, Campaign campaign,
			Subject subject, Email email, String content, int sentEmailIndex);

	public abstract Optional<GmailTrigger2Data> sendEmailFromGmailServerForTrigger2(
			CampaignGmailCredential campaignGmailCredential, Campaign campaign, Subject subject, Email email,
			String content, int sentEmailIndex);

	public abstract Optional<GmailTrigger3Data> sendEmailFromGmailServerForTrigger3(
			CampaignGmailCredential campaignGmailCredential, Campaign campaign, Subject subject, Email email,
			String content, int sentEmailIndex);

}
