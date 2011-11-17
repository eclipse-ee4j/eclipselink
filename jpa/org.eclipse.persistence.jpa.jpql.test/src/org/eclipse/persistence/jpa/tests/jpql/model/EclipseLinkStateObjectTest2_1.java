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

import org.junit.Test;

import static org.eclipse.persistence.jpa.tests.jpql.JPQLQueries.*;

@SuppressWarnings("nls")
public final class EclipseLinkStateObjectTest2_1 extends EclipseLinkJPQLStateObjectTest {

	@Test
	public void test_Query_224() throws Exception {

		// SELECT FUNC('NVL', e.firstName, 'NoFirstName'),
		//        func('NVL', e.lastName,  'NoLastName')
		// FROM Employee e

		StateObjectTester selectStatement = selectStatement(
			select(
				func("NVL", path("e.firstName"), string("'NoFirstName'")),
				func("NVL", path("e.lastName"),  string("'NoLastName'"))
			),
			from("Employee", "e")
		);

		testQuery(query_224(), selectStatement);
	}

	@Test
	public void test_Query_225() throws Exception {

		// SELECT a
		// FROM Asset a, Geography selectedGeography
      // WHERE selectedGeography.id = :id AND
      //       a.id IN (:id_list) AND
      //       FUNC('ST_Intersects', a.geometry, selectedGeography.geometry) = 'TRUE'

		StateObjectTester selectStatement = selectStatement(
			select(variable("a")),
			from("Asset", "a", "Geography", "selectedGeography"),
			where(
					path("selectedGeography.id").equal(inputParameter(":id"))
				.and(
					path("a.id").in(inputParameter(":id_list"))
				).and(
						func("ST_Intersects", path("a.geometry"), path("selectedGeography.geometry"))
					.equal(
						string("'TRUE'"))
				)
			)
		);

		testQuery(query_225(), selectStatement);
	}

	@Test
	public void test_Query_226() throws Exception {

		// Select e
		// From Employee e Join TREAT(e.projects AS LargeProject) lp
		// Where lp.budget = :value

		StateObjectTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e", join(treatAs("e.projects", "LargeProject"), "lp")),
			where(path("lp.budget").equal(inputParameter(":value")))
		);

		testQuery(query_226(), selectStatement);
	}

	@Test
	public void test_Query_227() throws Exception {

		// Select e
		// From Employee e Join TREAT(e.projects LargeProject) lp

		StateObjectTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e", join(treat("e.projects", "LargeProject"), "lp"))
		);

		testQuery(query_227(), selectStatement);
	}
}