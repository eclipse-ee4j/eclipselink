/*******************************************************************************
 * Copyright (c) 2012 Oracle. All rights reserved.
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
import org.eclipse.persistence.jpa.jpql.DefaultJPQLQueryContext;
import org.eclipse.persistence.jpa.jpql.DefaultJPQLQueryHelper;
import org.eclipse.persistence.jpa.jpql.EclipseLinkJPQLQueryContext;
import org.eclipse.persistence.jpa.jpql.EclipseLinkJPQLQueryHelper;
import org.eclipse.persistence.jpa.jpql.JPQLQueryContext;
import org.eclipse.persistence.jpa.jpql.parser.DefaultEclipseLinkJPQLGrammar;
import org.eclipse.persistence.jpa.jpql.parser.DefaultJPQLGrammar;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

/**
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuiteClasses({
	AllJPQLQueryHelperTests.AllDefaultJPQLQueryHelperTests.class,
	AllJPQLQueryHelperTests.AllEclipseLinkJPQLQueryHelperTests.class,
//	ORMEntityJPQLQueryHelperTest.class,
//	ORMJPQLQueryHelperTest.class
})
@RunWith(JPQLTestRunner.class)
public final class AllJPQLQueryHelperTests {

	private AllJPQLQueryHelperTests() {
		super();
	}

	@SuiteClasses({
		DefaultJPQLQueryHelperTest.class,
	})
	@RunWith(JPQLTestRunner.class)
	public static class AllDefaultJPQLQueryHelperTests {

		private AllDefaultJPQLQueryHelperTests() {
			super();
		}

		private static JPQLQueryContext buildJPQLQueryContext() {
			return new DefaultJPQLQueryContext(DefaultJPQLGrammar.instance());
		}

		@JPQLQueryHelperTestHelper
		static AbstractJPQLQueryHelper[] buildJPQLQueryHelpers() {
			return new AbstractJPQLQueryHelper[] {
				new DefaultJPQLQueryHelper(buildJPQLQueryContext())
			};
		}
	}

	@SuiteClasses({
		EclipseLinkJPQLQueryHelperTest.class,
	})
	@RunWith(JPQLTestRunner.class)
	public static class AllEclipseLinkJPQLQueryHelperTests {

		private AllEclipseLinkJPQLQueryHelperTests() {
			super();
		}

		private static JPQLQueryContext buildJPQLQueryContext() {
			return new EclipseLinkJPQLQueryContext(DefaultEclipseLinkJPQLGrammar.instance());
		}

		@JPQLQueryHelperTestHelper
		static AbstractJPQLQueryHelper[] buildJPQLQueryHelpers() {
			return new AbstractJPQLQueryHelper[] {
				new EclipseLinkJPQLQueryHelper(buildJPQLQueryContext())
			};
		}
	}
}