/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle. All rights reserved.
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
package org.eclipse.persistence.jpa.tests.jpql.parser;

import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar2_4;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar;
import org.eclipse.persistence.jpa.tests.jpql.JPQLTestRunner;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

/**
 * This test suite runs {@link EclipseLinkJPQLParserTests2_4} using JPQL grammars written for JPA
 * 2.1 and for EclipseLink 2.4.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuiteClasses({

	EclipseLinkJPQLParserTests2_4.class,

	CastExpressionTest.class,
	ExtractExpressionTest.class,
	ColumnExpressionTest.class,
	FunctionExpressionTest.class,
	OperatorExpressionTest.class,
	OrderByItemTest.class,
	RegexpExpressionTest.class,
	SQLExpressionTest.class,
	TableVariableDeclarationTest.class,
	UnionClauseTest.class
})
@RunWith(JPQLTestRunner.class)
public final class AllEclipseLinkJPQLParserTests2_4 {

	private AllEclipseLinkJPQLParserTests2_4() {
		super();
	}

	@JPQLGrammarTestHelper
	static JPQLGrammar[] buildJPQLGrammars() {
		return new JPQLGrammar[] {
			EclipseLinkJPQLGrammar2_4.instance()
		};
	}
}