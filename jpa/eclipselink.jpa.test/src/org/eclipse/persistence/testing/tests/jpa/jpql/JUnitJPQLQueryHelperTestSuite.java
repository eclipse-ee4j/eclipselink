/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation
 *
 ******************************************************************************/
package org.eclipse.persistence.testing.tests.jpa.jpql;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.internal.jpa.JPAQuery;
import org.eclipse.persistence.internal.jpa.jpql.ConstructorQueryMappings;
import org.eclipse.persistence.internal.jpa.jpql.JPQLQueryHelper;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.advanced.Address;
import org.eclipse.persistence.testing.models.jpa.advanced.Employee.SalaryRate;

/**
 * Tests for {@link JPQLQueryHelper}.
 *
 * @version 2.5
 * @since 2.5
 * @author Pascal Filion
 */
public class JUnitJPQLQueryHelperTestSuite extends JUnitTestCase {

	public JUnitJPQLQueryHelperTestSuite() {
		super();
	}

	public JUnitJPQLQueryHelperTestSuite(String name) {
		super(name);
	}

	public static Test suite() {
		TestSuite suite = new TestSuite();
		suite.setName("JUnitJPQLQueryHelperTestSuite");
		suite.addTest(new JUnitJPQLQueryHelperTestSuite("test_getConstructorQueryMappings_01"));
		suite.addTest(new JUnitJPQLQueryHelperTestSuite("test_getConstructorQueryMappings_02"));
		return suite;
	}

	private int size(Iterable<DatabaseMapping> mappings) {

		int count = 0;

		for (DatabaseMapping mapping : mappings) {
			count++;
		}

		return count;
	}

	@Override
	public void tearDown() {
		super.tearDown();
		clearCache();
	}

	public void test_getConstructorQueryMappings_01() {

		String jpqlQuery = "SELECT e FROM Employee e";
		JPAQuery query = new JPAQuery(jpqlQuery);
		query.setSession(getDatabaseSession());

		JPQLQueryHelper helper = new JPQLQueryHelper();
		ConstructorQueryMappings constructorQuery = helper.getConstructorQueryMappings(query.getSession(), query);

		assertNotNull("ConstructorQueryMappings should not be null", constructorQuery);
		assertSame   ("The query was not cached correctly",          query, constructorQuery.getQuery());
		assertFalse  ("The query is not a constructor query",        constructorQuery.isConstructorQuery());
		assertNull   ("The class name should be null",               constructorQuery.getClassName());
		assertNotNull("The list of mappings cannot be null",         constructorQuery.mappings());
		assertFalse  ("The list of mappings should be empty",        constructorQuery.mappings().iterator().hasNext());
	}

	public void test_getConstructorQueryMappings_02() {

		String jpqlQuery = "SELECT NEW org.eclipse.persistence.testing.tests.jpa.jpql.JUnitJPQLQueryHelperTestSuite.MyConstructorClass(" +
		                   "   e.lastName, e.id, e.address, e.payScale" +
		                   ") " +
		                   "FROM Employee e";

		JPAQuery query = new JPAQuery(jpqlQuery);
		query.setSession(getDatabaseSession());

		JPQLQueryHelper helper = new JPQLQueryHelper();
		ConstructorQueryMappings constructorQuery = helper.getConstructorQueryMappings(query.getSession(), query);

		assertNotNull("ConstructorQueryMappings should not be null", constructorQuery);
		assertSame   ("The query was not cached correctly",          query, constructorQuery.getQuery());
		assertTrue   ("The query is a constructor query",            constructorQuery.isConstructorQuery());

		String expectedClassName = MyConstructorClass.class.getName().replace('$', '.');
		assertEquals ("The class name should be null", expectedClassName, constructorQuery.getClassName());

		Iterable<DatabaseMapping> mappings = constructorQuery.mappings();

		assertNotNull("The list of mappings cannot be null",  mappings);
		assertTrue   ("The list of mappings should be empty", mappings.iterator().hasNext());
		assertEquals ("The count of mappings should be 4", 4, size(mappings));

		int index = 0;

		for (DatabaseMapping mapping : mappings) {
			switch (index++) {
				case 0: {
					assertEquals("lastName", mapping.getAttributeName());
					break;
				}
				case 1: {
					assertEquals("id", mapping.getAttributeName());
					break;
				}
				case 2: {
					assertEquals("address", mapping.getAttributeName());
					break;
				}
				case 3: {
					assertEquals("payScale", mapping.getAttributeName());
					break;
				}
			}
		}
	}

	private static final class MyConstructorClass {

		private MyConstructorClass(String name, Integer id, Address address, SalaryRate payScale) {
			super();
		}
	}
}