package com.dash.scheduledtasks;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.dash.dao.InstanceDao;
import com.dash.dao.InstanceHistoryDao;
import com.dash.dao.TaskDao;
import com.dash.model.Instance;
import com.dash.model.InstanceHistory;
import com.dash.model.Task;
import com.dash.undefdash.ApplicationFactory;
import com.source.utils.SourceUtils;

@Component
public class InstancesAvailabilityCheckTask {

	private static final Logger logger = LoggerFactory.getLogger(InstancesAvailabilityCheckTask.class);
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	@Autowired
	private ApplicationFactory appFactory;

	// BY DEFAULT CONCURENT EXECUTION IS DISABLED
	// make sure to check later if task is expired
	@Scheduled(fixedRate = 1000 * 120, initialDelay = 1000 * 50)
	public void verifyInstancesAvailability() {
		try {

			logger.info("verifying instance(s) availability at :  {}", dateFormat.format(new Date()));
			Session session = appFactory.getSession();
			// get list of all instances
			Optional<List<Instance>> optListInstances = InstanceDao.getListAllInstances(session);
			if (optListInstances.isPresent()) {
				for (Instance instance : optListInstances.get()) {
					try {
						// check if instance status is different to current status saved on dash
						// database
						boolean currentStatus = SourceUtils.instanceStatus(instance);
						if (currentStatus != instance.isOnline()) {
							instance.setOnline(currentStatus);
							InstanceDao.updateInstance(session, instance);
							InstanceHistoryDao.saveInstanceHistory(session,
									new InstanceHistory(new Date(), currentStatus, instance));
						}

						// now sync tasks in between
						if (instance.isOnline()) {
							Optional<List<Task>> optListTasks = TaskDao.getListTasksPerInstance(session,
									instance.getInstanceId());

							if (optListTasks.isPresent() && optListTasks.get().size() != 0) {
								Optional<List<String>> optListIds = SourceUtils.listRegisteredTasks(instance);
								if (optListIds.isPresent()) {
									for (Task task : optListTasks.get()) {
										if (optListIds.get().contains(task.getTaskId()) == false
												&& task.getStatus().equals(com.dash.model.Status.active)
												&& task.isRepeatForever()) {
											// need to reschdule task
											SourceUtils.registerTask(task);
										}
									}
								}
							}
						}

					} catch (Exception ex) {
						logger.warn("issue on : ", ex);
					}
				}
			}

			System.gc();
		} catch (Exception ex) {
			logger.error("method verifyInstancesAvailability error : ", ex);
		}
	}

}
