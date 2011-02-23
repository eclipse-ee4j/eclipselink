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
package org.eclipse.persistence.utils.jpa.query.parser;

import org.junit.Test;

@SuppressWarnings("nls")
public final class FuncExpressionTest extends AbstractJPQLTest {

	@Test
	public void testBuildExpression_01() {
		String query = "SELECT FUNC('NVL', e.firstName, 'NoFirstName'), func('NVL', e.lastName, 'NoLastName') FROM Employee e";

		ExpressionTester selectStatement = selectStatement(
			select(
				func("'NVL'", path("e.firstName"), string("'NoFirstName'")),
				func("'NVL'", path("e.lastName"), string("'NoLastName'"))
			),
			from("Employee", "e")
		);

		testQuery(query, selectStatement);
	}

	@Test
	public void testBuildExpression_02() {
		String query = "SELECT a " +
		               "FROM Asset a, Geography selectedGeography " +
                     "WHERE selectedGeography.id = :id AND " +
                     "      a.id IN (:id_list) AND " +
                     "      FUNC('ST_Intersects', a.geometry, selectedGeography.geometry) = 'TRUE'";

		ExpressionTester selectStatement = selectStatement(
			select(variable("a")),
			from("Asset", "a", "Geography", "selectedGeography"),
			where(
					path("selectedGeography.id").equal(inputParameter(":id"))
				.and(
					path("a.id").in(inputParameter(":id_list"))
				).and(
						func("'ST_Intersects'", path("a.geometry"), path("selectedGeography.geometry"))
					.equal(
						string("'TRUE'"))
				)
			)
		);

		testQuery(query, selectStatement);
	}
}