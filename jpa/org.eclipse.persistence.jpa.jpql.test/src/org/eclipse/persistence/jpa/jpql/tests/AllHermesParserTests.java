/*******************************************************************************
 * Copyright (c) 2006, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jpa.jpql.tests;

import org.eclipse.persistence.jpa.tests.internal.jpql.parser.AllParserTests;

import junit.extensions.TestSetup;
import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.junit.runner.RunWith;
import org.junit.runners.AllTests;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * The test suite for the Hermes parser, which includes the parser tests and the content assist
 * tests. The other tests have to be defined by the implementors (grammar and semantical validation,
 * query result type and input parameter type tests) because they require an implementation of the
 * SPI, which also helps to test the SPI implementation.
 *
 * @version 2.3.0
 * @since 2.3.0
 * @author Pascal Filion
 */
@SuiteClasses
({
	AllParserTests.class,
	AllHermesParserTests.BatchTestSuite.class
})
@RunWith(Suite.class)
@SuppressWarnings("nls")
public final class AllHermesParserTests {

	private AllHermesParserTests() {
		super();
	}

	@RunWith(AllTests.class)
	public static final class BatchTestSuite {

		private static Test buildAllTests() {

			TestSuite suite = new TestSuite("Hermes Parser Batch Tests");

			// JPQLQueryHelper tests
			suite.addTest(new JUnit4TestAdapter(ORMEntityJPQLQueryHelperTest.class));
			suite.addTest(new JUnit4TestAdapter(ORMJPQLQueryHelperTest.class));
			suite.addTest(new JUnit4TestAdapter(PersistenceUnitEntityJPQLQueryHelperTest.class));

			// Validation
			suite.addTest(new JUnit4TestAdapter(GrammarValidatorTest.class));
			suite.addTest(new JUnit4TestAdapter(SemanticValidatorTest.class));

			// Content Assist
			suite.addTest(new JUnit4TestAdapter(ContentAssistTest.class));

			return buildBatchTestSuite(suite);
		}

		private static TestSetup buildBatchTestSuite(Test test) {
			return new TestSetup(test) {
				@Override
				protected void setUp() throws Exception {
					AbstractJPQLQueryTest.setUpClass();
				}
				@Override
				protected void tearDown() throws Exception {
					AbstractJPQLQueryTest.tearDownClass();
				}
			};
		}

		public static Test suite() {
			AbstractJPQLQueryTest.setJPQLQueryTestHelper(new JavaJPQLQueryTestHelper());
			return buildAllTests();
		}
	}
}