package com.tscp.mvna.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PreparedQuery {
	protected static final Logger logger = LoggerFactory.getLogger(PreparedQuery.class);
	protected Query query;

	protected PreparedQuery(Query query, Object... args) throws HibernateException {
		this.query = query;

		String[] parameters = query.getNamedParameters();

		if (args.length != parameters.length)
			throw new HibernateException("Number of arguments does not match number of parameters");

		List<String> sortedParams = new ArrayList<String>();
		for (String p : parameters)
			sortedParams.add(p);
		Collections.sort(sortedParams);

		for (int i = 0; i < args.length; i++)
			query.setParameter(sortedParams.get(i), args[i]);
	}

	public List list() {
		return query.list();
	}

	public Object uniqueResult() {
		return query.uniqueResult();
	}

}