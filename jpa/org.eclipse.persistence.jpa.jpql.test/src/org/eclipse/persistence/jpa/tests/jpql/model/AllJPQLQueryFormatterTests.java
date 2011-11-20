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
package org.eclipse.persistence.jpa.tests.jpql.model;

import org.eclipse.persistence.jpa.jpql.model.EclipseLinkJPQLQueryBuilder;
import org.eclipse.persistence.jpa.jpql.model.IJPQLQueryBuilder;
import org.eclipse.persistence.jpa.jpql.model.JPQLQueryBuilder2_0;
import org.eclipse.persistence.jpa.jpql.parser.DefaultEclipseLinkJPQLGrammar;
import org.eclipse.persistence.jpa.tests.jpql.JPQLTestRunner;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

/**
 * The test suite containing the unit-tests testing the {@link org.eclipse.persistence.jpa.jpql.model.
 * IJPQLQueryFormatter IJPQLQueryFormatter}.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuiteClasses({
	DefaultActualJPQLQueryFormatterTest.class
})
@RunWith(JPQLTestRunner.class)
public final class AllJPQLQueryFormatterTests {

	private AllJPQLQueryFormatterTests() {
		super();
	}

	@IJPQLQueryBuilderTestHelper
	static IJPQLQueryBuilder[] buildJPQLQueryBuilders() {
		return new IJPQLQueryBuilder[] {
			new JPQLQueryBuilder2_0(),
			new EclipseLinkJPQLQueryBuilder(DefaultEclipseLinkJPQLGrammar.instance()),
		};
	}
}