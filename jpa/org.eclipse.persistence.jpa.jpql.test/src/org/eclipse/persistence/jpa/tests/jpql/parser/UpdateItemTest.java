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

	@Test
	public void testBuildExpression_01() {

		String query = "UPDATE Employee e SET e.name = 'Pascal'";

		UpdateStatementTester updateStatement = updateStatement(
			update(
				"Employee", "e",
				set("e.name", string("'Pascal'"))
			)
		);

		testQuery(query, updateStatement);
	}

	@Test
	public void testBuildExpression_02() {

		String query = "UPDATE Employee e SET e.name = 'Pascal', e.manager.salary = 100000";

		UpdateStatementTester updateStatement = updateStatement(
			update(
				"Employee", "e",
				set("e.name", string("'Pascal'")),
				set("e.manager.salary", numeric(100000))
			)
		);

		testQuery(query, updateStatement);
	}
}