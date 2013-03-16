/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jpa.tests.jpql.tools;

import org.eclipse.persistence.jpa.jpql.parser.DefaultEclipseLinkJPQLGrammar;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar2_0;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar2_1;
import org.eclipse.persistence.jpa.jpql.tools.AbstractJPQLQueryHelper;
import org.eclipse.persistence.jpa.jpql.tools.DefaultJPQLQueryContext;
import org.eclipse.persistence.jpa.jpql.tools.DefaultJPQLQueryHelper;
import org.eclipse.persistence.jpa.jpql.tools.EclipseLinkJPQLQueryContext;
import org.eclipse.persistence.jpa.jpql.tools.EclipseLinkJPQLQueryHelper;
import org.eclipse.persistence.jpa.jpql.tools.JPQLQueryContext;
import org.eclipse.persistence.jpa.tests.jpql.JPQLQueryHelperTestHelper;
import org.eclipse.persistence.jpa.tests.jpql.JPQLTestRunner;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

/**
 * This class defines the JPQL content assist unit-tests.
 *
 * @version 2.5
 * @since 2.4
 * @author Pascal Filion
 */
public final class AllContentAssistTests {

	private AllContentAssistTests() {
		super();
	}

	@SuiteClasses({
		DefaultContentAssistTest.class,
		DefaultContentAssistExtensionTest.class,
		DefaultDefaultContentAssistExtensionTest.class
	})
	@RunWith(JPQLTestRunner.class)
	public static class AllDefaultContentAssistTests {

		private AllDefaultContentAssistTests() {
			super();
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
				new DefaultJPQLQueryHelper(buildJPQLQueryContext2_1())
			};
		}
	}

	@SuiteClasses({
		EclipseLinkContentAssistTest2_4.class,
		EclipseLinkContentAssistTest2_5.class,
		EclipseLinkContentAssistExtensionTest.class,
		EclipseLinkEclipseLinkContentAssistExtensionTest.class
	})
	@RunWith(JPQLTestRunner.class)
	public static class AllEclipseLinkContentAssistTests {

		private AllEclipseLinkContentAssistTests() {
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