package com.tscp.mvna;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public abstract class Integrity<T> {
	protected T target;
	protected List<Problem> problemObjects;

	public Integrity(T target) {
		this.target = target;
	}

	protected void addProblem(
			Object obj, String description) {
		addProblem(new Problem(obj, description));
	}

	protected void addProblem(
			Problem problem) {
		if (problemObjects == null)
			problemObjects = new ArrayList<Problem>();
		problemObjects.add(problem);
	}

	@XmlAttribute
	public boolean isSane() {
		return problemObjects == null || problemObjects.isEmpty();
	}

	@XmlElement
	public List<Problem> getProblemObjects() {
		return problemObjects;
	}

	public abstract void check();

}