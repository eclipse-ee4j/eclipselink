/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
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

import org.eclipse.persistence.jpa.jpql.AbstractJPQLQueryHelper;
import org.eclipse.persistence.jpa.jpql.DefaultJPQLQueryHelper;
import org.eclipse.persistence.jpa.jpql.EclipseLinkJPQLQueryHelper;
import org.eclipse.persistence.jpa.jpql.parser.DefaultEclipseLinkJPQLGrammar;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar2_0;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

/**
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuiteClasses({
	AllSemanticValidatorTests.AllDefaultSemanticValidatorTest.class,
	AllSemanticValidatorTests.AllEclipseLinkSemanticValidatorTest.class
})
@RunWith(JPQLTestRunner.class)
public final class AllSemanticValidatorTests {

	private AllSemanticValidatorTests() {
		super();
	}

	@SuiteClasses({
		DefaultSemanticValidatorTest.class,
	})
	@RunWith(JPQLTestRunner.class)
	public static class AllDefaultSemanticValidatorTest {

		private AllDefaultSemanticValidatorTest() {
			super();
		}

		@JPQLQueryHelperTestHelper
		static AbstractJPQLQueryHelper[] buildJPQLQueryHelpers() {
			return new AbstractJPQLQueryHelper[] {
				new DefaultJPQLQueryHelper(JPQLGrammar2_0.instance())
			};
		}
	}

	@SuiteClasses({
		EclipseLinkSemanticValidatorTest.class,
	})
	@RunWith(JPQLTestRunner.class)
	public static class AllEclipseLinkSemanticValidatorTest {

		private AllEclipseLinkSemanticValidatorTest() {
			super();
		}

		@JPQLQueryHelperTestHelper
		static AbstractJPQLQueryHelper[] buildJPQLQueryHelpers() {
			return new AbstractJPQLQueryHelper[] {
				new EclipseLinkJPQLQueryHelper(DefaultEclipseLinkJPQLGrammar.instance())
			};
		}
	}
}