package com.dash.dao;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dash.model.Instance;
import com.dash.model.Source;

@Transactional
public class InstanceDao {

	private static final Logger logger = LoggerFactory.getLogger(InstanceDao.class);

	public static void saveInstance(Session session, Instance instance) {
		try {
			session.beginTransaction();
			session.save(instance);
			session.getTransaction().commit();
		} catch (Exception ex) {
			logger.error("method saveInstance error : ", ex);
		}
	}

	public static void updateInstance(Session session, Instance instance) {
		try {
			session.beginTransaction();
			session.update(instance);
			session.getTransaction().commit();
		} catch (Exception ex) {
			logger.error("method updateInstance error : ", ex);
		}
	}

	public static Optional<List<Instance>> getListInstancesBySource(Session session, Source source) {
		try {

			String req = "select * from instance where deleted = false and sourceid = " + source.getSourceId();
			SQLQuery query = session.createSQLQuery(req);
			query.addEntity(Instance.class);
			List<Instance> listData = query.list();
			if (listData == null || listData.size() == 0)
				return Optional.empty();
			else
				return Optional.of(listData);

		} catch (Exception ex) {
			logger.error("method getListInstancesBySource error : ", ex);
			return Optional.empty();
		} finally {
		}
	}

	public static Optional<List<Instance>> getListAllInstances(Session session) {
		try {

			String req = "select * from instance where deleted = false";
			SQLQuery query = session.createSQLQuery(req);
			query.addEntity(Instance.class);
			List<Instance> listData = query.list();
			if (listData == null || listData.size() == 0)
				return Optional.empty();
			else
				return Optional.of(listData);

		} catch (Exception ex) {
			logger.error("method getListAllInstances error : ", ex);
			return Optional.empty();
		} finally {
		}
	}

	public static Optional<Instance> getInstanceById(Session session, long instanceId) {
		try {
			String req = "SELECT INSTANCE.* FROM INSTANCE where deleted=false and  instanceId =" + instanceId;
			SQLQuery query = session.createSQLQuery(req);
			query.addEntity(Instance.class);
			List<Instance> listData = query.list();
			if (listData == null || listData.size() == 0)
				return Optional.empty();
			else
				return Optional.of(listData.get(0));
		} catch (Exception ex) {
			logger.error("method getInstanceById error : ", ex);
			return Optional.empty();
		} finally {
		}
	}

	public static Optional<Instance> getInstanceByIdAndAccount(Session session, long instanceId, long accountId) {
		try {
			String req = "select instance.* from instance , source , account_source where instance.sourceid = source.sourceid and source.sourceid= account_source.sourceid and account_source.accountid="
					+ accountId + " and instance.instanceid =" + instanceId;
			SQLQuery query = session.createSQLQuery(req);
			query.addEntity(Instance.class);
			List<Instance> listData = query.list();
			if (listData == null || listData.size() == 0)
				return Optional.empty();
			else
				return Optional.of(listData.get(0));
		} catch (Exception ex) {
			logger.error("method getInstanceByIdAndAccount error : ", ex);
			return Optional.empty();
		} finally {
		}
	}

}
