/*
 * Copyright (c) 2006, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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
