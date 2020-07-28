/*
 * Copyright (c) 2006, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.jpa.tests.jpql.parser;

import org.eclipse.persistence.jpa.jpql.parser.CaseExpressionBNF;
import org.eclipse.persistence.jpa.jpql.parser.SelectClauseBNF;
import org.junit.Test;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.*;

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
