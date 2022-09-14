package com.dash.resource.endpoints;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dash.dao.CampaignDao;
import com.dash.model.Account;
import com.dash.model.Campaign;
import com.dash.resource.model.Response;
import com.dash.resource.model.Status;
import com.dash.statistics.dao.PullDao;
import com.dash.statistics.model.Pull;
import com.dash.undefdash.ApplicationFactory;

@RestController
@RequestMapping("/statistics")
public class StatisticsResource {
	private static final Logger logger = LoggerFactory.getLogger(StatisticsResource.class);

	@Autowired
	private ApplicationFactory appFactory;

	@PostMapping("/get-last-pull")
	public Response<Pull> getLastPull(@RequestParam("campaignId") long campaignId) {
		Response<Pull> response = new Response<Pull>();
		try {

			Session session = appFactory.getSession();
			Optional<Campaign> optCampaign = CampaignDao.getCampaignbyIdAndAccount(session, campaignId,1);
			if (optCampaign.isPresent() == false) {
				response.setStatus(Status.ERROR);
				response.setMessage("campaign not found");
				return response;
			} else {

				response.setStatus(Status.SUCCESS);
				response.setData(new ArrayList<Pull>());

				Campaign campaign = optCampaign.get();
				Optional<Pull> optPull = PullDao.getLastPullPerCampaign(session, campaign);
				if (optPull.isPresent()) {
					response.setData(Arrays.asList(optPull.get()));
				}
			}

			return response;

		} catch (Exception ex) {
			logger.error("method getLastPull error : ", ex);
			response.setStatus(Status.ERROR);
			response.setMessage("internal server error : " + ex.getMessage());
			return response;
		}
	}

}
