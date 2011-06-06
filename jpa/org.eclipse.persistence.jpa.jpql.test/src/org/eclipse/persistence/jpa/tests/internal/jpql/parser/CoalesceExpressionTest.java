/*******************************************************************************
 * Copyright (c) 2006, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.jpa.tests.internal.jpql.parser;

import org.junit.Test;

@SuppressWarnings("nls")
public final class CoalesceExpressionTest extends AbstractJPQLTest {

	@Test
	public void testBuildExpression_01() {
		String query = "SELECT COALESCE(e.age, e.name) FROM Employee e";

		ExpressionTester selectStatement = selectStatement(
			select(coalesce(path("e.age"), path("e.name"))),
			from("Employee", "e")
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_02() {
		String query = "SELECT e FROM Employee e WHERE COALESCE(e.age, 20) + 20";

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e"),
			where(
					coalesce(path("e.age"), numeric(20))
				.add(
					numeric(20)
				)
			)
		);

		testQuery(query, selectStatement);
	}
}