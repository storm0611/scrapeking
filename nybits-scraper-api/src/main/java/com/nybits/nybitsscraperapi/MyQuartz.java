package com.nybits.nybitsscraperapi;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyQuartz implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final Logger logger = LoggerFactory.getLogger(MyQuartz.class);

	private static Scheduler scheduler;

	public static Scheduler getScheduler() {
		if (scheduler == null)
			try {
				scheduler = new StdSchedulerFactory().getScheduler();
			} catch (SchedulerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return scheduler;
	}

	public static Optional<JobKey> findJobByName(String jobName) {
		try {

			// Check all jobs if not found
			for (String groupName : scheduler.getJobGroupNames()) {
				for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
					if (Objects.equals(jobName, jobKey.getName())) {
						return Optional.of(jobKey);
					}
				}
			}
			return Optional.empty();

		} catch (Exception ex) {
			logger.error("method findJobByName error : " + ex.getMessage());
			return Optional.empty();
		}
	}

}
