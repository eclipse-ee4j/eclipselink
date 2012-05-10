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

import org.eclipse.persistence.jpa.jpql.parser.CaseExpressionBNF;
import org.eclipse.persistence.jpa.jpql.parser.SelectClauseBNF;
import org.junit.Test;

@SuppressWarnings("nls")
public final class JPQLExpressionTest2_0 extends JPQLParserTest {

	@Test
	public void testJPQLFragment_1() {

		String jpqlFragment = "CASE TYPE(e) WHEN Exempt THEN 'Exempt'" +
		                      "             ELSE 'ELSE'" +
		                      "             END";

		ExpressionTester caseExpression = case_(
			type("e"),
			new ExpressionTester[] { when(entity("Exempt"), string("'Exempt'")) },
			string("'ELSE'")
		);

		testQuery(jpqlFragment, caseExpression, CaseExpressionBNF.ID);
	}

	@Test
	public void testJPQLFragment_2() {
		String jpqlFragment = "CASE TYPE(e) WHEN Exempt THEN 'Exempt' ELSE 'ELSE' END";
		ExpressionTester jpqlExpression = jpqlExpression(nullExpression(), unknown(jpqlFragment));
		testQuery(jpqlFragment, jpqlExpression, SelectClauseBNF.ID);
	}
}