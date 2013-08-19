package com.tscp.mvna.tester;

import java.util.ArrayList;
import java.util.Collection;

public class Sandbox {

	public static void main(
			String[] args) {
		System.out.println("Hello world!\n");
		try {

			Collection blah = new ArrayList();
			Collection numbers = new ArrayList();
			numbers.add(1);
			numbers.add(2);

			blah.addAll(null);

			blah.addAll(numbers);

		} catch (Exception e) {
			System.err.println(e.getMessage());
		} finally {
			System.out.println("\nGoodbye world!");
		}
	}

	static class Parent {

		public void inheritedMethod() {
			overriddenMethod();
		}

		public void overriddenMethod() {
			System.out.println("parent method called");
		}
	}

	static class Child extends Parent {
		@Override
		public void overriddenMethod() {
			System.out.println("child method called");
		}
	}

}