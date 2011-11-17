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

import org.eclipse.persistence.jpa.jpql.model.EclipseLinkJPQLQueryBuilder;
import org.eclipse.persistence.jpa.jpql.model.IJPQLQueryBuilder;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar2_1;
import org.eclipse.persistence.jpa.tests.jpql.model.IJPQLQueryBuilderTestHelper;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

/**
 * This test suite containing the unit-tests testing the refactoring functionality with EclipseLink
 * additional support.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuiteClasses({
	EclipseLinkRefactoringToolTest2_1.class
})
@RunWith(JPQLTestRunner.class)
public final class AllEclipseLinkRefactoringToolTest2_1 {

	private AllEclipseLinkRefactoringToolTest2_1() {
		super();
	}

	@IJPQLQueryBuilderTestHelper
	static IJPQLQueryBuilder buildJPQLQueryBuilder() {
		return new EclipseLinkJPQLQueryBuilder(EclipseLinkJPQLGrammar2_1.instance());
	}
}