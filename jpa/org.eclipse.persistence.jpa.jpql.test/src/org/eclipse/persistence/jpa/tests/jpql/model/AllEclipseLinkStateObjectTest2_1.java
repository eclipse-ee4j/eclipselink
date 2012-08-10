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

import org.eclipse.persistence.jpa.jpql.model.EclipseLinkActualJPQLQueryFormatter;
import org.eclipse.persistence.jpa.jpql.model.EclipseLinkJPQLQueryBuilder;
import org.eclipse.persistence.jpa.jpql.model.EclipseLinkJPQLQueryFormatter;
import org.eclipse.persistence.jpa.jpql.model.IJPQLQueryBuilder;
import org.eclipse.persistence.jpa.jpql.model.IJPQLQueryFormatter;
import org.eclipse.persistence.jpa.jpql.model.IJPQLQueryFormatter.IdentifierStyle;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar2_1;
import org.eclipse.persistence.jpa.tests.jpql.JPQLTestRunner;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

/**
 * The test suite containing the unit-tests testing the {@link StateObject} API that was extended
 * with EclipseLink additional functionality.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuiteClasses({
	EclipseLinkStateObjectTest2_4.class,
	EclipseLinkStateObjectTest2_5.class
})
@RunWith(JPQLTestRunner.class)
public final class AllEclipseLinkStateObjectTest2_1 {

	private AllEclipseLinkStateObjectTest2_1() {
		super();
	}

	@IJPQLQueryBuilderTestHelper
	static IJPQLQueryBuilder buildJPQLQueryBuilder() {
		return new EclipseLinkJPQLQueryBuilder(EclipseLinkJPQLGrammar2_1.instance());
	}

	@IJPQLQueryFormatterTestHelper
	static IJPQLQueryFormatter[] buildJPQLQUeryFormatters() {
		return new IJPQLQueryFormatter[] {

			new EclipseLinkJPQLQueryFormatter(IdentifierStyle.CAPITALIZE_EACH_WORD),
			new EclipseLinkJPQLQueryFormatter(IdentifierStyle.LOWERCASE),
			new EclipseLinkJPQLQueryFormatter(IdentifierStyle.UPPERCASE),

			new EclipseLinkActualJPQLQueryFormatter(true, IdentifierStyle.CAPITALIZE_EACH_WORD),
			new EclipseLinkActualJPQLQueryFormatter(true, IdentifierStyle.LOWERCASE),
			new EclipseLinkActualJPQLQueryFormatter(true, IdentifierStyle.UPPERCASE),

			new EclipseLinkActualJPQLQueryFormatter(false, IdentifierStyle.CAPITALIZE_EACH_WORD),
			new EclipseLinkActualJPQLQueryFormatter(false, IdentifierStyle.LOWERCASE),
			new EclipseLinkActualJPQLQueryFormatter(false, IdentifierStyle.UPPERCASE)
		};
	}
}