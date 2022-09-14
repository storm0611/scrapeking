package com.emailing.dao;

import java.util.List;
import java.util.Optional;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.emailing.model.Subject;

public class SubjectDao {

	private static final Logger logger = LoggerFactory.getLogger(SubjectDao.class);

	public static void saveSubject(Session session, Subject subject) {
		try {
			session.beginTransaction();
			session.save(subject);
			session.getTransaction().commit();
		} catch (Exception ex) {
			logger.error("method saveSubject error : ", ex);
		}
	}

	public static void updateSubject(Session session, Subject subject) {
		try {
			session.beginTransaction();
			session.update(subject);
			session.getTransaction().commit();
		} catch (Exception ex) {
			logger.error("method updateSubject error : ", ex);
		}
	}

	public static Optional<Subject> getSubjectById(Session session, String subjectId) {
		try {
			String req = "select * from subject where subjectid = '" + subjectId + "'";
			SQLQuery query = session.createSQLQuery(req);
			query.addEntity(Subject.class);
			List<Subject> listData = query.list();
			if (listData != null && listData.size() != 0)
				return Optional.of(listData.get(0));
			else
				return Optional.empty();
		} catch (Exception ex) {
			logger.error("method getSubjectById error : ", ex);
			return Optional.empty();
		}
	}

	public static Optional<List<Subject>> getTopXSubjectsPerCampaign(Session session, int maxCount) {
		try {
			String req = "select top 200 * from subject where subject.blacklistedword != true and 0 = (select count(message.*) from message where message.subjectid=subject.subjectid)";
			SQLQuery query = session.createSQLQuery(req);
			query.addEntity(Subject.class);
			List<Subject> listData = query.list();
			return Optional.ofNullable(listData);
		} catch (Exception ex) {
			logger.error("method getTopXSubjectsPerCampaign error : ", ex);
			return Optional.empty();
		}
	}

	public static Optional<List<Subject>> getTop1000ActiveSubject(Session session) {
		try {
			String req = "select top 1000 subject.* from subject where  (select  top 1 message.subjectid  from message where message.subjectid = subject.subjectid and message.rank = 3 ) is null  and  (select  count (message.subjectid ) from message where message.subjectid = subject.subjectid  )> 0 and subjectid not in  (select message.subjectid from message where message.subjectid = subject.subjectid  and message.messageid in (select event.messageid from event where event.messageid =message.messageid  and ( event.type like 'unsubscribe'  or event.type like 'bounce' or event.type like 'close' ) ) )";
			SQLQuery query = session.createSQLQuery(req);
			query.addEntity(Subject.class);
			List<Subject> listData = query.list();
			return Optional.ofNullable(listData);

		} catch (Exception ex) {
			logger.error("method getTop1000ActiveSubject error : ", ex);
			return Optional.empty();
		}
	}

	public static List<Subject> getAllSubjects(Session session) {
		try {
			String req = "select * from Subject";
			SQLQuery query = session.createSQLQuery(req);
			query.addEntity(Subject.class);
			List<Subject> listData = query.list();
			return listData;
		} catch (Exception ex) {
			logger.error("method getAllSubjects error : ", ex);
			return null;
		}
	}

	public static Optional<List<Object[]>> getListSubjectWithStatus(Session session) {
		try {
			String req = "select subject .* , 'new' as status from subject where subject.blacklistedword != true and 0 = (select count(message.*) from message where message.subjectid=subject.subjectid) "
					+ "union " + "SELECT SUBJECT.* ,   event.type as status   FROM SUBJECT , message , event "
					+ "where subject.subjectid = message.subjectId and event.messageid  "
					+ "and message.messageid = (select top 1 message.messageid from message where message.subjectid =SUBJECT.SUBJECTid  order by date desc) "
					+ "and event.eventid = (select top 1 event .eventid from event where event.messageid=message.messageid order by event.date desc) "
					+ "union  " + "select SUBJECT.*,'blacklisted' as status from subject where blacklistedword = true ";

			SQLQuery query = session.createSQLQuery(req);
			List<Object[]> listObject = query.list();
			return Optional.ofNullable(listObject);

		} catch (Exception ex) {
			logger.error("method getListSubjectWithStatus error : ", ex);
			return Optional.empty();
		}
	}

	public static List<Subject> getSubjectsPerCampaignAndPerDayLimit(Session session, long perDayTotalCount) {
		try {
			Query query = session.createQuery("from Subject s where s.blacklistedWord != true and s.sent1=null and (s.unsubscribe=null OR s.unsubscribe=false)");
			
			query.setMaxResults((int) perDayTotalCount);
			List<Subject> subjectList = (List<Subject>) query.list();  
			return subjectList;
			
		} catch (Exception ex) {
			logger.error("method getSubjectsPerCampaignAndPerDayLimit error : ", ex);
			return null;
		}
	}

	public static Subject getBySubjectId(Session session, String subjectId) {
		try {
			Query query = session.createQuery("from Subject s where s.subjectId=:subjectId");
			query.setParameter("subjectId", subjectId);
			List<Subject> subjects = (List<Subject>)query.list();
			if(subjects.isEmpty())
				return null;
			return subjects.get(0);

		} catch (Exception ex) {
			logger.error("method getBySubjectId error : ", ex);
			return null;
		}
	}

	public static Subject getSubjectByUnsubscribeToken(Session session, String token) {
		try {
			Query query = session.createQuery("from Subject s where s.unsubscribeToken=:token");
			query.setParameter("token", token);
			List<Subject> subjects = (List<Subject>)query.list();
			if(subjects.isEmpty())
				return null;
			return subjects.get(0);

		} catch (Exception ex) {
			logger.error("method getSubjectByUnsubscribeToken error : ", ex);
			return null;
		}
	}

	public static List<Subject> getSubjectsForTrigger2(Session session, long perDayTotalCount) {
		try {
			Query query = session.createQuery("from Subject s where s.blacklistedWord != true and s.sent1=true and (s.unsubscribe=null OR s.unsubscribe=false) and s.sent2=null and s.sent1Read=true and (s.sent1Reply=null OR s.sent1Reply=false)");
			
			query.setMaxResults((int) perDayTotalCount);
			List<Subject> subjectList = (List<Subject>) query.list();  
			return subjectList;
			
		} catch (Exception ex) {
			logger.error("method getSubjectsForTrigger2 error : ", ex);
			return null;
		}
	}

	public static List<Subject> getSubjectsForTrigger3(Session session, long perDayTotalCount) {
		try {
			Query query = session.createQuery("from Subject s where s.blacklistedWord != true and s.sent2=true and (s.unsubscribe=null OR s.unsubscribe=false) and s.sent3=null and s.sent1Read=true and (s.sent2Reply=null OR s.sent2Reply=false)");
			
			query.setMaxResults((int) perDayTotalCount);
			List<Subject> subjectList = (List<Subject>) query.list();  
			return subjectList;
			
		} catch (Exception ex) {
			logger.error("method getSubjectsForTrigger3 error : ", ex);
			return null;
		}
	}

	public static long getTrigger1SendSuccessEmailCount(Session session) {
		try {
			Query query = session.createQuery("select count(s.subjectId) from Subject s where s.sent1=true");
			
			List result = query.list();
			return result.isEmpty() ? 0 : Long.parseLong(result.get(0).toString());
			
		} catch (Exception ex) {
			logger.error("method getTrigger1SendSuccessEmailCount error : ", ex);
			return 0;
		}
	}
	
	public static long getTrigger2SendSuccessEmailCount(Session session) {
		try {
			Query query = session.createQuery("select count(s.subjectId) from Subject s where s.sent2=true");
			
			List result = query.list();
			return result.isEmpty() ? 0 : Long.parseLong(result.get(0).toString());
			
		} catch (Exception ex) {
			logger.error("method getTrigger2SendSuccessEmailCount error : ", ex);
			return 0;
		}
	}
	
	public static long getTrigger3SendSuccessEmailCount(Session session) {
		try {
			Query query = session.createQuery("select count(s.subjectId) from Subject s where s.sent3=true");
			
			List result = query.list();
			return result.isEmpty() ? 0 : Long.parseLong(result.get(0).toString());
			
		} catch (Exception ex) {
			logger.error("method getTrigger3SendSuccessEmailCount error : ", ex);
			return 0;
		}
	}

	public static long getTrigger1FailEmailCount(Session session) {
		try {
			Query query = session.createQuery("select count(s.subjectId) from Subject s where s.sent1=false");
			
			List result = query.list();
			return result.isEmpty() ? 0 : Long.parseLong(result.get(0).toString());
			
		} catch (Exception ex) {
			logger.error("method getTrigger1FailEmailCount error : ", ex);
			return 0;
		}
	}
	
	public static long getTrigger2FailEmailCount(Session session) {
		try {
			Query query = session.createQuery("select count(s.subjectId) from Subject s where s.sent2=false");
			
			List result = query.list();
			return result.isEmpty() ? 0 : Long.parseLong(result.get(0).toString());
			
		} catch (Exception ex) {
			logger.error("method getTrigger2FailEmailCount error : ", ex);
			return 0;
		}
	}
	
	public static long getTrigger3FailEmailCount(Session session) {
		try {
			Query query = session.createQuery("select count(s.subjectId) from Subject s where s.sent3=false");
			
			List result = query.list();
			return result.isEmpty() ? 0 : Long.parseLong(result.get(0).toString());
			
		} catch (Exception ex) {
			logger.error("method getTrigger3FailEmailCount error : ", ex);
			return 0;
		}
	}

	public static long getUnsubscribeEmailCount(Session session) {
		try {
			Query query = session.createQuery("select count(s.subjectId) from Subject s where s.unsubscribe=true");
			
			List result = query.list();
			return result.isEmpty() ? 0 : Long.parseLong(result.get(0).toString());
			
		} catch (Exception ex) {
			logger.error("method getUnsubscribeEmailCount error : ", ex);
			return 0;
		}
	}

}
