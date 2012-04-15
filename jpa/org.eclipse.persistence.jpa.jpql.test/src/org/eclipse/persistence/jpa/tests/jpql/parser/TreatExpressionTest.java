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
package org.eclipse.persistence.jpa.tests.jpql.parser;

import org.eclipse.persistence.jpa.tests.jpql.UniqueSignature;
import org.junit.Test;

import static org.eclipse.persistence.jpa.tests.jpql.JPQLQueries2_1.*;

@UniqueSignature
@SuppressWarnings("nls")
public final class TreatExpressionTest extends JPQLParserTest {

	@Test
	public void testBuildExpression_01() throws Exception {

		// Select e
		// From Employee e Join TREAT(e.projects AS LargeProject) lp
		// Where lp.budget = :value

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e", join(treatAs("e.projects", "LargeProject"), "lp")),
			where(path("lp.budget").equal(inputParameter(":value")))
		);

		testQuery(query_001(), selectStatement);
	}

	@Test
	public void testBuildExpression_02() throws Exception {

		// Select e
		// From Employee e Join TREAT(e.projects LargeProject) lp

		ExpressionTester selectStatement = selectStatement(
			select(variable("e")),
			from("Employee", "e", join(treat("e.projects", "LargeProject"), "lp"))
		);

		testQuery(query_002(), selectStatement);
	}
}