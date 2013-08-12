package com.tscp.mvna.dao.hibernate;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class HibernateUtil {
	protected static final Logger logger = LoggerFactory.getLogger(HibernateUtil.class);
	protected static final Configuration configuration = buildConfiguration();
	protected static final ServiceRegistry serviceRegistry = buildServiceRegistry();
	protected static final SessionFactory sessionFactory = buildSessionFactory();

	private static final Configuration buildConfiguration() {
		try {
			return new Configuration().configure("hibernate.tscpmvna.cfg.xml");
		} catch (HibernateException e) {
			logger.error("Initial Hibernate Configuration creation failed", e);
			throw new ExceptionInInitializerError(e);
		}
	}

	private static final ServiceRegistry buildServiceRegistry() {
		return new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();
	}

	private static final SessionFactory buildSessionFactory() {
		try {
			return configuration.buildSessionFactory(serviceRegistry);
		} catch (Throwable e) {
			logger.error("Initial SessionFactory creation failed", e);
			throw new ExceptionInInitializerError(e);
		}
	}

	public static final SessionFactory getSessionFactory() {
		return sessionFactory;
	}

}