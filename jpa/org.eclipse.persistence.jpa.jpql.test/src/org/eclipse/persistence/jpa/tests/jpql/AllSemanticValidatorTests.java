/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar2_0;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar2_1;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar2_2;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar2_3;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar2_4;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar2_5;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar2_0;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar2_1;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLGrammarTestHelper;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

/**
 * @version 2.5
 * @since 2.4
 * @author Pascal Filion
 */
@SuiteClasses({
	// Unit-Test testing validating a JPQL query that was written following the JPA 2.0 spec
	AllSemanticValidatorTests.AllDefaultSemanticValidatorTest2_0.class,
	// Unit-Test testing validating a JPQL query that was written following the JPA 2.1 spec
	AllSemanticValidatorTests.AllDefaultSemanticValidatorTest2_1.class,
	// Unit-Test testing validating a JPQL query that was written following EclipseLink 2.0, 2.1, 2.2, 2.3
	AllSemanticValidatorTests.AllEclipseLinkSemanticValidatorTest.class,
	// Unit-Test testing validating a JPQL query that was written following EclipseLink 2.4
	AllSemanticValidatorTests.AllEclipseLinkSemanticValidatorTest2_4.class,
	// Unit-Test testing validating a JPQL query that was written following EclipseLink 2.5
	AllSemanticValidatorTests.AllEclipseLinkSemanticValidatorTest2_5.class
})
@RunWith(JPQLTestRunner.class)
public final class AllSemanticValidatorTests {

	private AllSemanticValidatorTests() {
		super();
	}

	/**
	 * This test suite tests JPQL queries written following the grammar defined in the JPA 2.0 spec
	 * and makes sure the various JPQL grammars that support it parses them correctly.
	 */
	@SuiteClasses({
		DefaultSemanticValidatorTest2_0.class,
	})
	@RunWith(JPQLTestRunner.class)
	public static class AllDefaultSemanticValidatorTest2_0 {

		private AllDefaultSemanticValidatorTest2_0() {
			super();
		}

		@JPQLGrammarTestHelper
		static JPQLGrammar[] buildJPQLGrammars() {
			return new JPQLGrammar[] {
				JPQLGrammar2_0.instance(),
				JPQLGrammar2_1.instance(),
				EclipseLinkJPQLGrammar2_4.instance(),
				EclipseLinkJPQLGrammar2_5.instance()
			};
		}
	}

	/**
	 * This test suite tests JPQL queries written following the grammar defined in the JPA 2.1 spec
	 * and makes sure the various JPQL grammars that support it parses them correctly.
	 */
	@SuiteClasses({
		DefaultSemanticValidatorTest2_1.class,
	})
	@RunWith(JPQLTestRunner.class)
	public static class AllDefaultSemanticValidatorTest2_1 {

		private AllDefaultSemanticValidatorTest2_1() {
			super();
		}

		@JPQLGrammarTestHelper
		static JPQLGrammar[] buildJPQLGrammars() {
			return new JPQLGrammar[] {
				JPQLGrammar2_1.instance(),
				EclipseLinkJPQLGrammar2_4.instance(),
				EclipseLinkJPQLGrammar2_5.instance()
			};
		}
	}

	/**
	 * This test suite tests JPQL queries written following the grammar defined in the JPA 2.0 spec
	 * with the extension provided by EclipseLink 2.0, 2.1, 2.2 and 2.3 and makes sure the various
	 * JPQL grammars that support it parses them correctly.
	 */
	@SuiteClasses({
		EclipseLinkSemanticValidatorTest.class,
	})
	@RunWith(JPQLTestRunner.class)
	public static class AllEclipseLinkSemanticValidatorTest {

		private AllEclipseLinkSemanticValidatorTest() {
			super();
		}

		@JPQLGrammarTestHelper
		static JPQLGrammar[] buildJPQLGrammars() {
			return new JPQLGrammar[] {
				EclipseLinkJPQLGrammar2_0.instance(),
				EclipseLinkJPQLGrammar2_1.instance(),
				EclipseLinkJPQLGrammar2_2.instance(),
				EclipseLinkJPQLGrammar2_3.instance(),
				EclipseLinkJPQLGrammar2_4.instance(),
				EclipseLinkJPQLGrammar2_5.instance()
			};
		}
	}

	/**
	 * This test suite tests JPQL queries written following the grammar defined in the JPA 2.1 spec
	 * with the extension provided by EclipseLink 2.4 and makes sure the various JPQL grammars that
	 * support it parses them correctly.
	 */
	@SuiteClasses({
		EclipseLinkSemanticValidatorTest2_4.class,
	})
	@RunWith(JPQLTestRunner.class)
	public static class AllEclipseLinkSemanticValidatorTest2_4 {

		private AllEclipseLinkSemanticValidatorTest2_4() {
			super();
		}

		@JPQLGrammarTestHelper
		static JPQLGrammar[] buildJPQLGrammars() {
			return new JPQLGrammar[] {
				EclipseLinkJPQLGrammar2_4.instance(),
				EclipseLinkJPQLGrammar2_5.instance(),
			};
		}
	}

	/**
	 * This test suite tests JPQL queries written following the grammar defined in the JPA 2.1 spec
	 * with the extension provided by EclipseLink 2.5 and makes sure the various JPQL grammars that
	 * support it parses them correctly.
	 */
	@SuiteClasses({
		EclipseLinkSemanticValidatorTest2_5.class,
	})
	@RunWith(JPQLTestRunner.class)
	public static class AllEclipseLinkSemanticValidatorTest2_5 {

		private AllEclipseLinkSemanticValidatorTest2_5() {
			super();
		}

		@JPQLGrammarTestHelper
		static JPQLGrammar[] buildJPQLGrammars() {
			return new JPQLGrammar[] {
				EclipseLinkJPQLGrammar2_5.instance(),
			};
		}
	}
}