/*******************************************************************************
 * Copyright (c) 2006, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.jpa.tests.jpql;

import org.eclipse.persistence.jpa.tests.jpql.model.AllStateObjectTests;
import org.eclipse.persistence.jpa.tests.jpql.parser.AllJPQLParserTests;
import org.eclipse.persistence.jpa.tests.jpql.parser.AllUtilityTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

/**
 * The root test suite that includes the entire functionality provided by Hermes.
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
@SuiteClasses({
	AllUtilityTests.class,
	AllJPQLParserTests.class,
	AllGrammarValidatorTests.class,
	AllHermesTests.BatchTestSuite.class
})
@RunWith(JPQLTestRunner.class)
public final class AllHermesTests {

	private AllHermesTests() {
		super();
	}

	@SuiteClasses({
		AllSemanticValidatorTests.class,
		AllContentAssistTests.class,
		AllStateObjectTests.class,
		AllRefactoringToolTests.class
	})
	@RunWith(JPQLTestRunner.class)
	public static final class BatchTestSuite {
		@JPQLQueryTestHelperTestHelper
		static JPQLQueryTestHelper buildQueryTestHelper() {
			return new JavaJPQLQueryTestHelper();
		}
	}

	// TODO
//	ORMEntityJPQLQueryHelperTest.class,
//	ORMJPQLQueryHelperTest.class,
//	PersistenceUnitEntityJPQLQueryHelperTest.class,
}