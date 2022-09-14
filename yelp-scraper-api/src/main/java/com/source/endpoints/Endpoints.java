package com.source.endpoints;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.source.scraper.Yelp;
import com.source.utils.BrowserConfig;
import com.source.yelpscraperapi.MyQuartz;

@RestController
@RequestMapping("/yelp")
public class Endpoints {

	private static final Logger logger = LoggerFactory.getLogger(Endpoints.class);

	@GetMapping("/ping")
	public ResponseEntity<String> ping() {
		// TODO Auto-generated method stub
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("its up \n");
		strBuilder.append(BrowserConfig.listProxies.toString() + "\n");
		return ResponseEntity.status(HttpStatus.OK).body(strBuilder.toString());
	}

	@GetMapping("/parameters")
	public ResponseEntity<List<String>> getParameters(HttpSession httpSession) {
		return ResponseEntity.status(HttpStatus.OK).body(Yelp.referentialParameters);
	}

	// return 200 created
	// return 409 already exist/scheduled
	// return 500 internal server error
	@PostMapping("/register")
	public ResponseEntity<String> register(@RequestParam("taskId") String taskId,
			@RequestParam(name = "expireDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'hh:mm:ss") Date expireDate,
			@RequestParam(name = "cronUnit", required = false, defaultValue = "HOURS") String cronUnit,
			@RequestParam(name = "cronValue", required = false, defaultValue = "10") int cronValue,
			@RequestParam("repeatForever") boolean repeatForever,
			@RequestParam("parameters") Map<String, String> parameters, HttpSession httpSession) {
		try {

			Optional<JobKey> optJobKey = MyQuartz.findJobByName(String.valueOf(taskId));
			if (optJobKey.isPresent()) {
				return ResponseEntity.status(HttpStatus.CONFLICT).body("apiTaskId " + taskId + " already exist");
			} else {

				// construct the task
				JobDetail jobDetail = JobBuilder.newJob(Yelp.class).withIdentity(String.valueOf(taskId)).build();
				JobDataMap jobDataMap = jobDetail.getJobDataMap();

				jobDataMap.put("taskId", taskId);
				jobDataMap.put("parameters", parameters);

				logger.info("SCHEDULE_TASK : " + taskId);

				SimpleScheduleBuilder schedulerBuilder = SimpleScheduleBuilder.simpleSchedule();

				TriggerBuilder<SimpleTrigger> triggerBuilder = TriggerBuilder.newTrigger()
						.withSchedule(schedulerBuilder);

				if (repeatForever) {
					if (cronUnit.equals("HOURS"))
						schedulerBuilder.withIntervalInHours(cronValue);
					else if (cronUnit.equals("MINUTES"))
						schedulerBuilder.withIntervalInMinutes(cronValue);
					else if (cronUnit.equals("SECONDS"))
						schedulerBuilder.withIntervalInSeconds(cronValue);

					schedulerBuilder.repeatForever();
				}
				if (expireDate != null)
					triggerBuilder.endAt(expireDate);

				triggerBuilder.endAt(expireDate);
				triggerBuilder.startNow();

				// Schedule the job
				Scheduler scheduler = MyQuartz.getScheduler();
				scheduler.scheduleJob(jobDetail, triggerBuilder.build());
				scheduler.start();

				return ResponseEntity.status(HttpStatus.OK).body("task scheduled successfully");
			}

		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
		}
	}

	@PostMapping("/unregister")
	public ResponseEntity<String> unregister(@RequestParam("taskId") String taskId) {
		// TODO Auto-generated method stub
		try {

			logger.info("UNSCHEDULE_TASK : " + taskId);

			Optional<JobKey> optJobKey = MyQuartz.findJobByName(String.valueOf(taskId));
			if (optJobKey.isPresent() == false) {
				return ResponseEntity.status(HttpStatus.OK).body("task not found");
			} else {

				Scheduler scheduler = MyQuartz.getScheduler();
				scheduler.pauseJob(optJobKey.get());
				scheduler.interrupt(optJobKey.get());
				boolean deleteResp = scheduler.deleteJob(optJobKey.get());

				logger.info("RESPONSE : " + deleteResp);
				if (deleteResp) {
					return ResponseEntity.status(HttpStatus.OK).body("unregistered successfully");
				} else {
					return ResponseEntity.status(HttpStatus.OK).body("something went wrong task not unregistered");
				}

			}

		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("method unregister error : " + ex.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
		}

	}

	@GetMapping("/list-registered-tasks")
	public ResponseEntity<List<String>> listRegisteredTasks(HttpSession httpSession) {
		try {

			List<String> listTasks = new ArrayList<String>();
			Scheduler scheduler = MyQuartz.getScheduler();
			for (String groupName : scheduler.getJobGroupNames()) {
				for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
					listTasks.add(jobKey.getName());
				}
			}
			return ResponseEntity.status(HttpStatus.OK).body(listTasks);

		} catch (Exception ex) {
//			logger.error("method listRegisteredTasks error : " + ex.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

}
