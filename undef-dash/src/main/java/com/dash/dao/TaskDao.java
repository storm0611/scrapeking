package com.dash.dao;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dash.model.Task;

@Transactional
public class TaskDao {

	private static final Logger logger = LoggerFactory.getLogger(TaskDao.class);

	public static void saveTask(Session session, Task task) {
		try {
			session.beginTransaction();
			session.save(task);
			session.getTransaction().commit();
		} catch (Exception ex) {
			logger.error("method saveTask error : ", ex);
		}
	}

	public static void updateTask(Session session, Task task) {
		try {
			session.beginTransaction();
			session.update(task);
			session.getTransaction().commit();
		} catch (Exception ex) {
			logger.error("method updateTask error : ", ex);
		}
	}

	public static Optional<List<Task>> getListTasksPerInstance(Session session, long instanceId) {
		try {

			String req = "select * from task where deleted = false and  instanceid = " + instanceId;
			SQLQuery query = session.createSQLQuery(req);
			query.addEntity(Task.class);

			List<Task> listData = query.list();
			if (listData == null || listData.size() == 0)
				return Optional.empty();
			else
				return Optional.of(listData);

		} catch (Exception ex) {
			logger.error("method getListTasksPerInstance error : ", ex);
			return Optional.empty();
		} finally {
		}
	}

	public static Optional<List<Task>> getListTasksPerCampaign(Session session, long campaignId) {
		try {

			String req = "select * from task where deleted = false and campaignId = " + campaignId;
			SQLQuery query = session.createSQLQuery(req);
			query.addEntity(Task.class);

			List<Task> listData = query.list();
			return Optional.ofNullable(listData);

		} catch (Exception ex) {
			logger.error("method getListTasksPerCampaign error : ", ex);
			return Optional.empty();
		} finally {
		}

	}

	public static Optional<Task> getTaskByIdAndAccount(Session session, String taskId, long accountId) {
		try {

			String req = "select task.* from task , campaign where task.deleted=false and task.campaignId = campaign.campaignId and campaign.accountid ="
					+ accountId + " and task.taskId = '" + taskId + "'";

			SQLQuery query = session.createSQLQuery(req);
			query.addEntity(Task.class);

			List<Task> listData = query.list();
			if (listData == null || listData.size() == 0)
				return Optional.empty();
			else
				return Optional.of(listData.get(0));

		} catch (Exception ex) {
			logger.error("method getTaskByIdAndAccount error : ", ex);
			return Optional.empty();
		} finally {
		}
	}

	public static Optional<Task> getTaskById(Session session, String taskId) {
		try {

			String req = "select * from task where task.deleted = false and task.taskid = '" + taskId + "'";
			SQLQuery query = session.createSQLQuery(req);
			query.addEntity(Task.class);

			List<Task> listData = query.list();
			if (listData == null || listData.size() == 0)
				return Optional.empty();
			else
				return Optional.of(listData.get(0));

		} catch (Exception ex) {
			logger.error("method getTaskById error : ", ex);
			return Optional.empty();
		} finally {
		}
	}

}
