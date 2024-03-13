/*
 * Copyright (c) 2024 Oracle and/or its affiliates. All rights reserved.
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

import org.eclipse.persistence.jpa.jpql.parser.FromClause;
import org.eclipse.persistence.jpa.jpql.parser.IdentificationVariableDeclaration;
import org.eclipse.persistence.jpa.jpql.parser.IdentificationVariable;
import org.eclipse.persistence.jpa.jpql.parser.JPQLExpression;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar3_2;
import org.eclipse.persistence.jpa.jpql.parser.JPQLStatementBNF;
import org.eclipse.persistence.jpa.jpql.parser.RangeVariableDeclaration;
import org.eclipse.persistence.jpa.jpql.parser.SelectStatement;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public final class JPQLExpressionTestJakartaData extends JPQLParserTest {

    @Test
    public void testNoAlias() {
        testQuery("SELECT o FROM NoAliasEntity", "o");
    }

    @Test
    public void testNoAliasOBJECT() {
        testQuery("SELECT OBJECT(o) FROM NoAliasEntity", "o");
    }

    @Test
    public void testNoAliasCOUNT() {
        testQuery("SELECT COUNT(o) FROM NoAliasEntity", "o");
    }

    @Test
    public void testNoAliasCASTCOUNT() {
        testQuery("SELECT CAST(COUNT(o) AS SMALLINT) FROM NoAliasEntity", "o");
    }

    @Test
    public void testNoAliasCASTCASTCOUNT() {
        testQuery("SELECT CAST(CAST(COUNT(o) AS SMALLINT) AS BIGINT) FROM NoAliasEntity", "o");
    }

    private void testQuery(String actualQuery, String expectedAlias) {
        JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(actualQuery, JPQLGrammar3_2.instance(), JPQLStatementBNF.ID, true, "None");
        SelectStatement selectStatement = (SelectStatement)jpqlExpression.getQueryStatement();
        FromClause fromClause = (FromClause)selectStatement.getFromClause();
        IdentificationVariableDeclaration identificationVariableDeclaration = (IdentificationVariableDeclaration)fromClause.getDeclaration();
        RangeVariableDeclaration rangeVariableDeclaration = (RangeVariableDeclaration)identificationVariableDeclaration.getRangeVariableDeclaration();
        IdentificationVariable identificationVariable = (IdentificationVariable)rangeVariableDeclaration.getIdentificationVariable();
        String alias = identificationVariable.getText();

        assertEquals(expectedAlias, alias);
    }
}
