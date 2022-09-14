package com.dash.scheduledtasks;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.dash.dao.CampaignDao;
import com.dash.dao.TaskDao;
import com.dash.model.Campaign;
import com.dash.model.Task;
import com.dash.statistics.dao.FigureDao;
import com.dash.statistics.dao.PullDao;
import com.dash.statistics.dao.StatisticsDao;
import com.dash.statistics.model.Figure;
import com.dash.statistics.model.Pull;
import com.dash.undefdash.ApplicationFactory;
import com.emailing.utils.EmailingDBUtils;

@Component
public class StatisticsTask {

	private static final Logger logger = LoggerFactory.getLogger(StatisticsTask.class);
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	@Autowired
	private ApplicationFactory appFactory;

	@Scheduled(fixedRate = 1800000, initialDelay = 40000)
	public void start() {
		try {
			logger.info("StatisticsTask at :  {}", dateFormat.format(new Date()));
			Session dashSession = appFactory.getSession();

			Optional<List<Campaign>> optListCampaign = CampaignDao.getListCampaignsFromAllAccounts(dashSession);
			if (optListCampaign.isPresent()) {
				for (Campaign campaign : optListCampaign.get()) {
					Optional<Session> optSession = EmailingDBUtils.getSession(campaign);
					if (optSession.isPresent() == false) {
						logger.warn("cannot connect to campaign database : " + campaign.getCampaignId() + " : "
								+ campaign.getName());
					} else {

						Session session = optSession.get();

						Pull pull = new Pull();
						pull.setDate(new Date());
						pull.setCampaign(campaign);
						pull.setListFigures(new ArrayList<Figure>());
						PullDao.savePull(dashSession, pull);

						Integer subjectCount = StatisticsDao.getSubjectsCount(session);
						Figure figureSC = new Figure("subject_count", String.valueOf(subjectCount), pull);
						FigureDao.saveFigure(dashSession, figureSC);

						// unsubscribe count

						Integer taskCount = 0;
						Optional<List<Task>> optListTask = TaskDao.getListTasksPerCampaign(dashSession,
								campaign.getCampaignId());
						if (optListTask.isPresent())
							taskCount = optListTask.get().size();

						Figure figureTC = new Figure("task_count", String.valueOf(taskCount), pull);
						FigureDao.saveFigure(dashSession, figureTC);

					}
				}
			}

			System.gc();
		} catch (Exception ex) {
			logger.error("method start error : ", ex);
		}
	}

}
