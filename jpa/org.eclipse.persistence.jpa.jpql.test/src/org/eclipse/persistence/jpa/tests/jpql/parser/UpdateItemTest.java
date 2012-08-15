/*******************************************************************************
 * Copyright (c) 2006, 2012 Oracle and/or its affiliates. All rights reserved.
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

@SuppressWarnings("nls")
public final class UpdateItemTest extends JPQLParserTest {

	private JPQLQueryStringFormatter buildQueryFormatter_1() {
		return new JPQLQueryStringFormatter() {
			public String format(String query) {
				return query.replace("AVG", "avg");
			}
		};
	}

	@Test
	public void test_BuildExpression_02() {

		String jpqlQuery = "UPDATE Employee e SET e.name = 'Pascal', e.manager.salary = 100000";

		UpdateStatementTester updateStatement = updateStatement(
			update(
				"Employee", "e",
				set("e.name", string("'Pascal'")),
				set("e.manager.salary", numeric(100000))
			)
		);

		testQuery(jpqlQuery, updateStatement);
	}

	@Test
	public void test_BuildExpression_03() {

		String jpqlQuery = "UPDATE Employee SET AVG(2) = 'Pascal'";

		UpdateStatementTester updateStatement = updateStatement(
			update(
				"Employee",
				set(bad(avg(numeric(2))), string("'Pascal'"))
			)
		);

		testInvalidQuery(jpqlQuery, updateStatement);
	}

	@Test
	public void test_BuildExpression_04() {

		String jpqlQuery = "UPDATE Employee SET avg = 'Pascal'";

		UpdateStatementTester updateStatement = updateStatement(
			update(
				"Employee",
				set("{employee}.avg", string("'Pascal'"))
			)
		);

		testInvalidQuery(jpqlQuery, updateStatement, buildQueryFormatter_1());
	}

	@Test
	public void testBuildExpression_01() {

		String jpqlQuery = "UPDATE Employee e SET e.name = 'Pascal'";

		UpdateStatementTester updateStatement = updateStatement(
			update(
				"Employee", "e",
				set("e.name", string("'Pascal'"))
			)
		);

		testQuery(jpqlQuery, updateStatement);
	}
}