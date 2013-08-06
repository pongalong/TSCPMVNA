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

			logger.debug("Loading object {} with ID {}", clazz.getSimpleName(), id);

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

			logger.debug("Saving object {}.", obj);

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

			logger.debug("Saving object {}.", obj);

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

			logger.debug("Updating object {}.", obj);

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

			logger.debug("Persisting object {}.", obj);

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

			Query query = session.getNamedQuery(namedQuery);
			String[] parameters = query.getNamedParameters();

			// TODO check number of parameters match
			logger.debug("Executing namedQuery {}", namedQuery);
			for (int i = 0; i < args.length; i++) {
				logger.debug("Parameter {} set to {}", parameters[i], args[args.length - i - 1]);
				query.setParameter(parameters[i], args[args.length - i - 1]);
			}

			List result = query.list();

			tx.commit();
			return result;
		} catch (HibernateException e) {
			logger.error("Error executing named query {}.", namedQuery, e);
			return null;
		} finally {
			closeSession(session);
		}

	}

}