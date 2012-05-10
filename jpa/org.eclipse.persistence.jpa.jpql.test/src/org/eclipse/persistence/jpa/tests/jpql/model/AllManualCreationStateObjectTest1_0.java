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
package org.eclipse.persistence.jpa.tests.jpql.model;

import org.eclipse.persistence.jpa.jpql.model.EclipseLinkJPQLQueryBuilder;
import org.eclipse.persistence.jpa.jpql.model.IJPQLQueryBuilder;
import org.eclipse.persistence.jpa.jpql.model.JPQLQueryBuilder1_0;
import org.eclipse.persistence.jpa.jpql.model.JPQLQueryBuilder2_0;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar1;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar2_0;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar2_1;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar2_2;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar2_3;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar2_4;
import org.eclipse.persistence.jpa.tests.jpql.JPQLTestRunner;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

/**
 * The test suite containing the unit-tests testing the manual creation of the {@link StateObject}
 * representation of a JPQL query defined in JPA version 1.0.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuiteClasses({
	ManualCreationStateObjectTest1_0.class
})
@RunWith(JPQLTestRunner.class)
public final class AllManualCreationStateObjectTest1_0 {

	private AllManualCreationStateObjectTest1_0() {
		super();
	}

	@IJPQLQueryBuilderTestHelper
	static IJPQLQueryBuilder[] buildJPQLQueryBuilders() {
		return new IJPQLQueryBuilder[] {
			new JPQLQueryBuilder1_0(),
			new JPQLQueryBuilder2_0(),
			new EclipseLinkJPQLQueryBuilder(EclipseLinkJPQLGrammar1.instance()),
			new EclipseLinkJPQLQueryBuilder(EclipseLinkJPQLGrammar2_0.instance()),
			new EclipseLinkJPQLQueryBuilder(EclipseLinkJPQLGrammar2_1.instance()),
			new EclipseLinkJPQLQueryBuilder(EclipseLinkJPQLGrammar2_2.instance()),
			new EclipseLinkJPQLQueryBuilder(EclipseLinkJPQLGrammar2_3.instance()),
			new EclipseLinkJPQLQueryBuilder(EclipseLinkJPQLGrammar2_4.instance())
		};
	}
}