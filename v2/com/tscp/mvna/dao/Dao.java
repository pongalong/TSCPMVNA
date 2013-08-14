package com.tscp.mvna.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
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

		try {
			session = getSession();
			Transaction tx = session.beginTransaction();

			logger.trace("Loading object {} with ID {}", clazz.getSimpleName(), id);

			Object result = session.get(clazz, id);
			tx.commit();
			return result;
		} catch (HibernateException e) {
			logger.error("Error loading object {} with ID {}.", clazz.getSimpleName(), id, e);
			return null;
		} finally {
			closeSession(session);
		}
	}

	public static void save(
			Object obj) throws HibernateException {

		Session session = null;

		try {
			session = getSession();
			Transaction tx = session.beginTransaction();

			logger.trace("Saving object {}.", obj);

			session.save(obj);
			tx.commit();
		} catch (HibernateException e) {
			logger.error("Error saving object {}.", obj, e);
			throw e;
		} finally {
			closeSession(session);
		}
	}

	public static void saveOrUpdate(
			Object obj) throws HibernateException {

		Session session = null;

		try {
			session = getSession();
			Transaction tx = session.beginTransaction();

			logger.trace("Saving or updating object {}.", obj);

			session.saveOrUpdate(obj);
			tx.commit();
		} catch (HibernateException e) {
			logger.error("Error saving object {}.", obj, e);
			throw e;
		} finally {
			closeSession(session);
		}
	}

	public static void update(
			Object obj) throws HibernateException {

		Session session = null;

		try {
			session = getSession();
			Transaction tx = session.beginTransaction();

			logger.trace("Updating object {}.", obj);

			session.update(obj);
			tx.commit();
		} catch (HibernateException e) {
			logger.error("Error updating object {}.", obj, e);
			throw e;
		} finally {
			closeSession(session);
		}
	}

	public static void persist(
			Object obj) {

		Session session = null;

		try {
			session = getSession();
			Transaction tx = session.beginTransaction();

			logger.trace("Persisting object {}.", obj);

			session.persist(obj);
			tx.commit();
		} catch (HibernateException e) {
			logger.error("Error persisting object {}.", obj, e);
		} finally {
			closeSession(session);
		}
	}

	@SuppressWarnings("rawtypes")
	public static List executeNamedQuery(
			String namedQuery, Object... args) {

		Session session = null;

		try {
			session = getSession();
			Transaction tx = session.beginTransaction();

			profiler.start(namedQuery);

			Query query = session.getNamedQuery(namedQuery);
			String[] parameters = query.getNamedParameters();

			if (args.length != parameters.length)
				throw new HibernateException("Number of arguments does not match number of parameters");

			List<String> sortedParameters = new ArrayList<String>();
			for (String p : parameters)
				sortedParameters.add(p);
			Collections.sort(sortedParameters);

			// TODO check number of parameters match
			logger.trace("Executing namedQuery {}", namedQuery);
			for (int i = 0; i < args.length; i++) {
				// logger.debug("... Parameter {}={}", sortedParameters.get(i), args[i]);
				query.setParameter(sortedParameters.get(i), args[i]);
			}

			List result = query.list();

			tx.commit();
			return result;
		} catch (HibernateException e) {
			logger.error("Error executing named query {}: {} : {}", namedQuery, e.getMessage(), e.getCause());
			e.printStackTrace();
			return null;
		} finally {
			closeSession(session);
			profiler.stop();
		}

	}

	/* **************************************************
	 * Profiler Methods
	 */

	protected static Profiler profiler = new Profiler();

	public static Profiler getProfiler() {
		return profiler;
	}

}