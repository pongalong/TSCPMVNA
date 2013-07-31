package com.tscp.mvna.dao.hibernate;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HibernateUtil {
	protected static final Logger logger = LoggerFactory.getLogger(HibernateUtil.class);
	protected static final SessionFactory sessionFactory = buildSessionFactory();

	private static final SessionFactory buildSessionFactory() {
		try {
			return new Configuration().configure("hibernate.tscpmvna.cfg.xml").buildSessionFactory();
		} catch (Throwable ex) {
			System.err.println("Initial SessionFactory creation failed..." + ex);
			throw new ExceptionInInitializerError(ex);
		}
	}

	public static final SessionFactory getSessionFactory() {
		return sessionFactory;
	}

}