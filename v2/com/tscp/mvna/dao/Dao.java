package com.tscp.mvna.dao;

import java.io.Serializable;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tscp.mvna.dao.hibernate.HibernateUtil;
import com.tscp.util.profiler.Profiler;

public class Dao {
	private static final Logger logger = LoggerFactory.getLogger(Dao.class);
	protected static int maxOpenSessionCount;
	protected static int openSessionCount;

	public static int getOpenSessionCount() {
		return openSessionCount;
	}

	public static int getMaxOpenSessionCount() {
		return maxOpenSessionCount;
	}

	private static void closeSession(
			Session session) {

		if (session != null && session.isOpen())
			session.close();
		openSessionCount--;
	}

	protected static Session getSession() {
		try {
			Session session = HibernateUtil.getSessionFactory().openSession();
			openSessionCount++;

			if (openSessionCount > maxOpenSessionCount)
				maxOpenSessionCount = openSessionCount;

			return session;
		} catch (HibernateException e) {
			logger.error("Error opening Hibernate Session", e);
			throw e;
		}
	}

	public static Object get(
			@SuppressWarnings("rawtypes") Class clazz, Serializable id) {

		Session session = null;
		Transaction tx = null;

		try {
			session = getSession();
			tx = session.beginTransaction();

			logger.trace("Loading object {} with ID {}", clazz.getSimpleName(), id);

			Object result = session.get(clazz, id);
			tx.commit();
			return result;
		} catch (HibernateException e) {
			logger.error("Error loading object {} with ID {}.", clazz.getSimpleName(), id, e);
			tx.rollback();
			return null;
		} finally {
			closeSession(session);
		}
	}

	public static void save(
			Object obj) throws HibernateException {

		Session session = null;
		Transaction tx = null;

		try {
			session = getSession();
			tx = session.beginTransaction();

			logger.trace("Saving object {}.", obj);

			session.save(obj);
			tx.commit();
		} catch (HibernateException e) {
			logger.error("Error saving object {}.", obj, e);
			tx.rollback();
			throw e;
		} finally {
			closeSession(session);
		}
	}

	public static void saveOrUpdate(
			Object obj) throws HibernateException {

		Session session = null;
		Transaction tx = null;

		try {
			session = getSession();
			tx = session.beginTransaction();

			logger.trace("Saving or updating object {}.", obj);

			session.saveOrUpdate(obj);
			tx.commit();
		} catch (HibernateException e) {
			logger.error("Error saving object {}.", obj, e);
			tx.rollback();
			throw e;
		} finally {
			closeSession(session);
		}
	}

	public static void update(
			Object obj) throws HibernateException {

		Session session = null;
		Transaction tx = null;

		try {
			session = getSession();
			tx = session.beginTransaction();

			logger.trace("Updating object {}.", obj);

			session.update(obj);
			tx.commit();
		} catch (HibernateException e) {
			logger.error("Error updating object {}.", obj, e);
			tx.rollback();
			throw e;
		} finally {
			closeSession(session);
		}
	}

	public static void persist(
			Object obj) {

		Session session = null;
		Transaction tx = null;

		try {
			session = getSession();
			tx = session.beginTransaction();

			logger.trace("Persisting object {}.", obj);

			session.persist(obj);
			tx.commit();
		} catch (HibernateException e) {
			logger.error("Error persisting object {}.", obj, e);
			tx.rollback();
		} finally {
			closeSession(session);
		}
	}

	public static List fetch(
			String hqlString, Object... args) {

		Session session = null;
		Transaction tx = null;
		List result = null;

		try {
			session = getSession();
			tx = session.beginTransaction();

			profiler.start(hqlString);
			Query query = session.createQuery(hqlString);
			for (int i = 0; i < args.length; i++) {
				query.setParameter(i, args[i]);
				logger.debug("set param {} to {}", i, args[i]);
			}

			logger.debug("Executing HQL {}", query.getQueryString());

			tx.commit();
			result = query.list();
		} catch (HibernateException e) {
			logger.error("Error executing {}", hqlString, e);
			tx.rollback();
		} finally {
			closeSession(session);
			profiler.stop();
		}

		return result;
	}

	@SuppressWarnings("rawtypes")
	public static List list(
			String queryName, Object... args) {

		Session session = getSession();
		Transaction tx = session.beginTransaction();

		PreparedQuery preparedQuery = new PreparedQuery(session.getNamedQuery(queryName), args);
		List result = null;

		try {
			profiler.start(queryName);
			result = preparedQuery.list();
			tx.commit();
		} catch (HibernateException e) {
			logger.error("Error executing named query {}: {} : {}", queryName, e.getMessage(), e.getCause());
			tx.rollback();
		} finally {
			closeSession(session);
			profiler.stop();
		}

		return result;
	}

	public static Object uniqueResultScalar(
			String queryName, Object... args) {

		Session session = getSession();
		Transaction tx = session.beginTransaction();

		PreparedQuery preparedQuery = new PreparedQuery(session.getNamedQuery(queryName), args);
		Object result = null;

		try {
			profiler.start(queryName);
			result = preparedQuery.uniqueResult();
			tx.commit();
		} catch (HibernateException e) {
			logger.error("Error executing named query {}: {} : {}", queryName, e.getMessage(), e.getCause());
			tx.rollback();
		} finally {
			closeSession(session);
			profiler.stop();
		}

		return result;
	}

	public static Object[] listScalar(
			String queryName, Object... args) {

		Session session = getSession();
		Transaction tx = session.beginTransaction();

		PreparedQuery preparedQuery = new PreparedQuery(session.getNamedQuery(queryName), args);
		Object[] result = null;

		try {
			profiler.start(queryName);
			result = (Object[]) preparedQuery.uniqueResult();
			tx.commit();
		} catch (HibernateException e) {
			logger.error("Error executing named query {}: {} : {}", queryName, e.getMessage(), e.getCause());
			tx.rollback();
		} finally {
			closeSession(session);
			profiler.stop();
		}

		return result;
	}

	/* **************************************************
	 * Profiler Methods
	 */

	protected static Profiler profiler = new Profiler();

	public static Profiler getProfiler() {
		return profiler;
	}

}