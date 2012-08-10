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
package org.eclipse.persistence.jpa.tests.jpql.parser;

import org.junit.Test;

/**
 * Tests parsing hierarchical query clause.
 *
 * @see org.eclipse.persistence.jpa.jpql.parser.HierarchicalQueryClause HierarchicalQueryClause
 *
 * @version 2.5
 * @since 2.5
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class HierarchicalQueryClauseTest extends JPQLParserTest {

	@Test
	public void test_BuildExpression_01() throws Exception {

		String jpqlQuery = "SELECT e FROM Employee e START WITH CONNECT BY";

		StartWithClauseTester startWithClause = startWith(nullExpression());
		startWithClause.hasSpaceAfterIdentifier = true;

		ConnectByClauseTester connectByClause = connectBy(nullExpression());
		connectByClause.hasSpaceAfterConnectBy = false;
		connectByClause.hasSpaceAfterNocycle = false;

		HierarchicalQueryClauseTester hierarchicalQuery = hierarchicalQueryClause(
			startWithClause,
			connectByClause
		);

		hierarchicalQuery.hasSpaceAfterStartWithClause = false;

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e", hierarchicalQuery)
		);

		testInvalidQuery(jpqlQuery, selectStatement);
	}
}