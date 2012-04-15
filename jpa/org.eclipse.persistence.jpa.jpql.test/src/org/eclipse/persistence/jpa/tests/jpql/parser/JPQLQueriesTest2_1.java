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

import static org.eclipse.persistence.jpa.tests.jpql.JPQLQueries2_1.*;

/**
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class JPQLQueriesTest2_1 extends JPQLParserTest {

	@Test
	public void test_Query_001() throws Exception {

		// Select e
		// From Employee e Join TREAT(e.projects AS LargeProject) lp
		// Where lp.budget = :value

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e", joinTreatAs("e.projects", "LargeProject", "lp")),
			where(path("lp.budget").equal(inputParameter(":value")))
		);

		testQuery(query_001(), selectStatement);
	}

	@Test
	public void test_Query_002() throws Exception {

		// Select e
		// From Employee e Join TREAT(e.projects LargeProject) lp

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e", joinTreat("e.projects", "LargeProject", "lp"))
		);

		testQuery(query_002(), selectStatement);
	}

	@Test
	public void test_Query_003() throws Exception {

		// SELECT TREAT(TREAT(p.project LargeProject).parent AS LargeProject).endDate
      // FROM Product p

		ExpressionTester selectStatement = selectStatement(
			select(
				path(
					treatAs(
						path(treat("p.project", "LargeProject"), "parent"),
						entity("LargeProject")
					),
					"endDate"
				)
			),
			from("Product", "p")
		);

		testQuery(query_003(), selectStatement);
	}
}