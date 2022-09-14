package com.campaign.record.dao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.campaign.record.model.Record;

public class RecordDao {

	private static final Logger logger = LoggerFactory.getLogger(RecordDao.class);

	public static void saveRecord(Session session, Record record) {
		try {
			session.beginTransaction();
			session.save(record);
			session.getTransaction().commit();

		} catch (org.hibernate.exception.ConstraintViolationException e1) {
			logger.error("skipped already exist");
		} catch (Exception e2) {
			logger.error("method saveRecord error : ", e2);
		} 
	}

	public static Optional<List<Record>> getListRecordsBySchema(Session session, String schema) {
		try {
			String req = "select * from record where upper(schema) LIKE'" + schema.toUpperCase() + "' ";
			SQLQuery query = session.createSQLQuery(req);
			query.addEntity(Record.class);
			List<Record> listData = query.list();
			return Optional.ofNullable(listData);
		} catch (Exception ex) {
			logger.error("method getListRecordsBySchema error : ", ex);
			return Optional.empty();
		} finally {
		}
	}

	public static Optional<List<Record>> getListRecordsWithPagination(Session session, Date insertDate, int page) {
		try {

			String req = "select * from record where insertDate  > '"
					+ new SimpleDateFormat("yyyy-MM-dd").format(insertDate) + "'";
			SQLQuery query = session.createSQLQuery(req);
			query.addEntity(Record.class);

			query.setFirstResult((page - 1) * 10);
			query.setMaxResults(10);

			List<Record> listData = query.list();
			return Optional.ofNullable(listData);

		} catch (Exception ex) {
			logger.error("method getListRecordsWithPagination error : ", ex);
			return Optional.empty();
		} finally {
		}
	}

	public static Optional<List<Record>> getListRecordsWithNOPagination(Session session, Date insertDate) {
		try {

			String req = "select * from record where insertDate > '"
					+ new SimpleDateFormat("yyyy-MM-dd").format(insertDate) + "'";
			SQLQuery query = session.createSQLQuery(req);
			query.addEntity(Record.class);

			List<Record> listData = query.list();
			return Optional.ofNullable(listData);

		} catch (Exception ex) {
			logger.error("method getListRecordsWithNOPagination error : ", ex);
			return Optional.empty();
		} finally {
		}
	}

}
