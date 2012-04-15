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
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar2_1;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar2_2;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar2_3;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar2_4;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar2_0;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar2_1;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

/**
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuiteClasses({
	AllJPQLQueryHelperTests.AllDefaultJPQLQueryHelperTests.class,
	AllJPQLQueryHelperTests.AllDefaultJPQLQueryHelperTests2_1.class,
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

		private static JPQLQueryContext buildEclipseLinkJPQLQueryContext2_4() {
			return new EclipseLinkJPQLQueryContext(EclipseLinkJPQLGrammar2_4.instance());
		}

		private static JPQLQueryContext buildJPQLQueryContext2_0() {
			return new DefaultJPQLQueryContext(JPQLGrammar2_0.instance());
		}

		private static JPQLQueryContext buildJPQLQueryContext2_1() {
			return new DefaultJPQLQueryContext(JPQLGrammar2_1.instance());
		}

		@JPQLQueryHelperTestHelper
		static AbstractJPQLQueryHelper[] buildJPQLQueryHelpers() {
			return new AbstractJPQLQueryHelper[] {
				new DefaultJPQLQueryHelper(buildJPQLQueryContext2_0()),
				new DefaultJPQLQueryHelper(buildJPQLQueryContext2_1()),
				new EclipseLinkJPQLQueryHelper(buildEclipseLinkJPQLQueryContext2_4())
			};
		}
	}

	@SuiteClasses({
		DefaultJPQLQueryHelperTest2_1.class,
	})
	@RunWith(JPQLTestRunner.class)
	public static class AllDefaultJPQLQueryHelperTests2_1 {

		private AllDefaultJPQLQueryHelperTests2_1() {
			super();
		}

		private static JPQLQueryContext buildEclipseLinkJPQLQueryContext2_4() {
			return new EclipseLinkJPQLQueryContext(EclipseLinkJPQLGrammar2_4.instance());
		}

		private static JPQLQueryContext buildJPQLQueryContext2_1() {
			return new DefaultJPQLQueryContext(JPQLGrammar2_1.instance());
		}

		@JPQLQueryHelperTestHelper
		static AbstractJPQLQueryHelper[] buildJPQLQueryHelpers() {
			return new AbstractJPQLQueryHelper[] {
				new DefaultJPQLQueryHelper(buildJPQLQueryContext2_1()),
				new EclipseLinkJPQLQueryHelper(buildEclipseLinkJPQLQueryContext2_4())
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

		private static JPQLQueryContext buildJPQLQueryContext2_1() {
			return new EclipseLinkJPQLQueryContext(EclipseLinkJPQLGrammar2_1.instance());
		}

		private static JPQLQueryContext buildJPQLQueryContext2_2() {
			return new EclipseLinkJPQLQueryContext(EclipseLinkJPQLGrammar2_2.instance());
		}

		private static JPQLQueryContext buildJPQLQueryContext2_3() {
			return new EclipseLinkJPQLQueryContext(EclipseLinkJPQLGrammar2_3.instance());
		}

		private static JPQLQueryContext buildJPQLQueryContext2_4() {
			return new EclipseLinkJPQLQueryContext(EclipseLinkJPQLGrammar2_4.instance());
		}

		@JPQLQueryHelperTestHelper
		static AbstractJPQLQueryHelper[] buildJPQLQueryHelpers() {
			return new AbstractJPQLQueryHelper[] {
				new EclipseLinkJPQLQueryHelper(buildJPQLQueryContext2_1()),
				new EclipseLinkJPQLQueryHelper(buildJPQLQueryContext2_2()),
				new EclipseLinkJPQLQueryHelper(buildJPQLQueryContext2_3()),
				new EclipseLinkJPQLQueryHelper(buildJPQLQueryContext2_4())
			};
		}
	}
}