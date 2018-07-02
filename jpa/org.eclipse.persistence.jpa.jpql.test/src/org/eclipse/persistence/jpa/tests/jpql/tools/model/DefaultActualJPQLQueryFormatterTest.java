/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jpa.tests.jpql.tools.model;

import java.io.IOException;
import org.eclipse.persistence.jpa.jpql.tools.model.AbstractActualJPQLQueryFormatter;
import org.eclipse.persistence.jpa.jpql.tools.model.DefaultActualJPQLQueryFormatter;
import org.eclipse.persistence.jpa.jpql.tools.model.query.AbsExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.tools.model.query.AbstractStateObject;
import org.eclipse.persistence.jpa.jpql.tools.model.query.JPQLQueryStateObject;
import org.eclipse.persistence.jpa.jpql.tools.model.query.SelectStatementStateObject;
import org.eclipse.persistence.jpa.jpql.tools.model.query.StateFieldPathExpressionStateObject;
import org.eclipse.persistence.jpa.jpql.tools.model.query.StateObject;
import org.eclipse.persistence.jpa.jpql.tools.model.query.StateObjectVisitor;
import org.eclipse.persistence.jpa.tests.jpql.UniqueSignature;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * This unit-tests tests {@link DefaultActualJPQLQueryFormatter}.
 *
 * @version 2.5
 * @since 2.4
 * @author Pascal Filion
 */
@UniqueSignature
@SuppressWarnings("nls")
public final class DefaultActualJPQLQueryFormatterTest extends AbstractStateObjectTest {

    /**
     * Exact match, using info from Expression, which is valid.
     */
    @Test
    public void testAbsExpression_ExactMatch1() throws Exception {

        String jpqlQuery = "SeLeCt AbS(e.age) FrOm Employee e";
        StateObject stateObject = buildStateObject(jpqlQuery, true);

        DefaultActualJPQLQueryFormatter formatter = new DefaultActualJPQLQueryFormatter(true);
        assertEquals("SeLeCt AbS(e.age) FrOm Employee e", formatter.toString(stateObject));
    }

    /**
     * Exact match, using info from Expression, which is invalid.
     */
    @Test
    public void testAbsExpression_ExactMatch2() throws Exception {

        String jpqlQuery = "SELECT aBs FROM Employee e";
        JPQLQueryStateObject stateObject = buildStateObject(jpqlQuery, true);

        DefaultActualJPQLQueryFormatter formatter = new DefaultActualJPQLQueryFormatter(true);
        assertEquals("SELECT aBs FROM Employee e", formatter.toString(stateObject));
    }

    /**
     * Exact match, using info from Expression, which is invalid.
     */
    @Test
    public void testAbsExpression_ExactMatch3() throws Exception {

        String jpqlQuery = "SELECT aBs(e.age FROM Employee e";
        JPQLQueryStateObject stateObject = buildStateObject(jpqlQuery, true);

        DefaultActualJPQLQueryFormatter formatter = new DefaultActualJPQLQueryFormatter(true);
        assertEquals("SELECT aBs(e.age FROM Employee e", formatter.toString(stateObject));
    }

    /**
     * Exact match, using info from Expression, which is valid.
     */
    @Test
    public void testAbsExpression_ExactMatch4() throws Exception {

        String jpqlQuery = "SELECT abS(e.age) FROM Employee e";
        JPQLQueryStateObject stateObject = buildStateObject(jpqlQuery, true);

        DefaultActualJPQLQueryFormatter formatter = new DefaultActualJPQLQueryFormatter(true);
        assertEquals("SELECT abS(e.age) FROM Employee e", formatter.toString(stateObject));
    }

    /**
     * Exact match, no Expression available.
     */
    @Test
    public void testAbsExpression_ExactMatch5() throws Exception {

        String jpqlQuery = "SELECT FROM Employee e";

        JPQLQueryStateObject stateObject = buildStateObject(jpqlQuery, true);
        SelectStatementStateObject select = (SelectStatementStateObject) stateObject.getQueryStatement();
        select.addSelectItem(new AbsExpressionStateObject(select, new StateFieldPathExpressionStateObject(select, "e.age")));

        DefaultActualJPQLQueryFormatter formatter = new DefaultActualJPQLQueryFormatter(true);
        assertEquals("SELECT ABS(e.age) FROM Employee e", formatter.toString(stateObject));
    }

    /**
     * No exact match, should not use info from Expression, which is invalid.
     */
    @Test
    public void testAbsExpression_NoExactMatch1() throws Exception {

        String jpqlQuery = "SeLeCt AbS FrOm Employee e";
        StateObject stateObject = buildStateObject(jpqlQuery, true);

        DefaultActualJPQLQueryFormatter formatter = new DefaultActualJPQLQueryFormatter(false);
        assertEquals("SELECT ABS() FROM Employee e", formatter.toString(stateObject));
    }

    /**
     * No exact match, should not use info from Expression, which is invalid.
     */
    @Test
    public void testAbsExpression_NoExactMatch2() throws Exception {

        String jpqlQuery = "SeLeCt AbS( FrOm Employee e";
        StateObject stateObject = buildStateObject(jpqlQuery, true);

        DefaultActualJPQLQueryFormatter formatter = new DefaultActualJPQLQueryFormatter(false);
        assertEquals("SELECT ABS() FROM Employee e", formatter.toString(stateObject));
    }

    /**
     * No exact match, no Expression available.
     */
    @Test
    public void testAbsExpression_NoExactMatch3() throws Exception {

        String jpqlQuery = "SELECT FROM Employee e";

        JPQLQueryStateObject stateObject = buildStateObject(jpqlQuery, true);
        SelectStatementStateObject select = (SelectStatementStateObject) stateObject.getQueryStatement();
        select.addSelectItem(new AbsExpressionStateObject(select, new StateFieldPathExpressionStateObject(select, "e.age")));

        DefaultActualJPQLQueryFormatter formatter = new DefaultActualJPQLQueryFormatter(false);
        assertEquals("SELECT ABS(e.age) FROM Employee e", formatter.toString(stateObject));
    }

    /**
     * Test decorated StateObject.
     */
    @Test
    public void testDecorated() throws Exception {

        String jpqlQuery = "Select e From Employee e";

        JPQLQueryStateObject stateObject = buildStateObject(jpqlQuery, true);
        SelectStatementStateObject select = (SelectStatementStateObject) stateObject.getQueryStatement();
        select.decorate(new DecoratedSelectStatementStateObject(select));

        DecoratedActualJPQLQueryFormatter formatter = new DecoratedActualJPQLQueryFormatter(false);
        assertEquals("Decorated SELECT e FROM Employee e", formatter.toString(stateObject));
    }

    /**
     * Exact match, Expression available, which is invalid.
     */
    @Test
    public void testHavingClause_ExactMatch1() throws Exception {

        String jpqlQuery = "SELECT e fRom Employee e hAvInG";

        JPQLQueryStateObject stateObject = buildStateObject(jpqlQuery, true);

        DefaultActualJPQLQueryFormatter formatter = new DefaultActualJPQLQueryFormatter(true);
        assertEquals("SELECT e fRom Employee e hAvInG", formatter.toString(stateObject));
    }

    /**
     * Exact match, Expression available, which is invalid.
     */
    @Test
    public void testHavingClause_ExactMatch2() throws Exception {

        String jpqlQuery = "SELECT e fRom Employee e hAvInG ";

        JPQLQueryStateObject stateObject = buildStateObject(jpqlQuery, true);

        DefaultActualJPQLQueryFormatter formatter = new DefaultActualJPQLQueryFormatter(true);
        assertEquals("SELECT e fRom Employee e hAvInG ", formatter.toString(stateObject));
    }

    /**
     * Exact match, Expression available, which invalid.
     */
    @Test
    public void testHavingClause_ExactMatch3() throws Exception {

        String jpqlQuery = "SELECT e fRom Employee e hAvInG e.name = 'JPQL'";

        JPQLQueryStateObject stateObject = buildStateObject(jpqlQuery, true);

        DefaultActualJPQLQueryFormatter formatter = new DefaultActualJPQLQueryFormatter(true);
        assertEquals("SELECT e fRom Employee e hAvInG e.name = 'JPQL'", formatter.toString(stateObject));
    }

    /**
     * Exact match, no Expression available.
     */
    @Test
    public void testHavingClause_ExactMatch4() throws Exception {

        String jpqlQuery = "SELECT e fRom Employee e";

        JPQLQueryStateObject stateObject = buildStateObject(jpqlQuery, true);
        SelectStatementStateObject select = (SelectStatementStateObject) stateObject.getQueryStatement();
        select.addHavingClause();

        DefaultActualJPQLQueryFormatter formatter = new DefaultActualJPQLQueryFormatter(true);
        assertEquals("SELECT e fRom Employee e HAVING", formatter.toString(stateObject));
    }

    /**
     * Exact match, no Expression available.
     */
    @Test
    public void testHavingClause_ExactMatch5() throws Exception {

        String jpqlQuery = "SELECT e fRom Employee e";

        JPQLQueryStateObject stateObject = buildStateObject(jpqlQuery, true);
        SelectStatementStateObject select = (SelectStatementStateObject) stateObject.getQueryStatement();
        select.addHavingClause("e.name = 'JPQL'");

        DefaultActualJPQLQueryFormatter formatter = new DefaultActualJPQLQueryFormatter(true);
        assertEquals("SELECT e fRom Employee e HAVING e.name = 'JPQL'", formatter.toString(stateObject));
    }

    /**
     * No exact match.
     */
    @Test
    public void testHavingClause_NoExactMatch1() throws Exception {

        String jpqlQuery = "sELect e fRom Employee e HavinG e.name = 'JPQL'";

        JPQLQueryStateObject stateObject = buildStateObject(jpqlQuery, true);

        DefaultActualJPQLQueryFormatter formatter = new DefaultActualJPQLQueryFormatter(false);
        assertEquals("SELECT e FROM Employee e HAVING e.name = 'JPQL'", formatter.toString(stateObject));
    }

    /**
     * No exact match.
     */
    @Test
    public void testHavingClause_NoExactMatch2() throws Exception {

        String jpqlQuery = "sELect e fRom Employee e HavinG";

        JPQLQueryStateObject stateObject = buildStateObject(jpqlQuery, true);

        DefaultActualJPQLQueryFormatter formatter = new DefaultActualJPQLQueryFormatter(false);
        assertEquals("SELECT e FROM Employee e HAVING", formatter.toString(stateObject));
    }

    /**
     * No exact match.
     */
    @Test
    public void testHavingClause_NoExactMatch3() throws Exception {

        String jpqlQuery = "sELect e fRom Employee e HavinG ";

        JPQLQueryStateObject stateObject = buildStateObject(jpqlQuery, true);

        DefaultActualJPQLQueryFormatter formatter = new DefaultActualJPQLQueryFormatter(false);
        assertEquals("SELECT e FROM Employee e HAVING", formatter.toString(stateObject));
    }

    /**
     * No exact match.
     */
    @Test
    public void testHavingClause_NoExactMatch4() throws Exception {

        String jpqlQuery = "sELect e fRom Employee e";

        JPQLQueryStateObject stateObject = buildStateObject(jpqlQuery, true);
        SelectStatementStateObject select = (SelectStatementStateObject) stateObject.getQueryStatement();
        select.addHavingClause("e.name = 'JPQL'");

        DefaultActualJPQLQueryFormatter formatter = new DefaultActualJPQLQueryFormatter(false);
        assertEquals("SELECT e FROM Employee e HAVING e.name = 'JPQL'", formatter.toString(stateObject));
    }

    /**
     * Exact match, Expression available, which is invalid.
     */
    @Test
    public void testWhereClause_ExactMatch1() throws Exception {

        String jpqlQuery = "SELECT e fRom Employee e wHeRe";

        JPQLQueryStateObject stateObject = buildStateObject(jpqlQuery, true);

        DefaultActualJPQLQueryFormatter formatter = new DefaultActualJPQLQueryFormatter(true);
        assertEquals("SELECT e fRom Employee e wHeRe", formatter.toString(stateObject));
    }

    /**
     * Exact match, Expression available, which is invalid.
     */
    @Test
    public void testWhereClause_ExactMatch2() throws Exception {

        String jpqlQuery = "SELECT e fRom Employee e wHeRe ";

        JPQLQueryStateObject stateObject = buildStateObject(jpqlQuery, true);

        DefaultActualJPQLQueryFormatter formatter = new DefaultActualJPQLQueryFormatter(true);
        assertEquals("SELECT e fRom Employee e wHeRe ", formatter.toString(stateObject));
    }

    /**
     * Exact match, Expression available, which invalid.
     */
    @Test
    public void testWhereClause_ExactMatch3() throws Exception {

        String jpqlQuery = "SELECT e fRom Employee e wHeRe e.name = 'JPQL'";

        JPQLQueryStateObject stateObject = buildStateObject(jpqlQuery, true);

        DefaultActualJPQLQueryFormatter formatter = new DefaultActualJPQLQueryFormatter(true);
        assertEquals("SELECT e fRom Employee e wHeRe e.name = 'JPQL'", formatter.toString(stateObject));
    }

    /**
     * Exact match, no Expression available.
     */
    @Test
    public void testWhereClause_ExactMatch4() throws Exception {

        String jpqlQuery = "SELECT e fRom Employee e";

        JPQLQueryStateObject stateObject = buildStateObject(jpqlQuery, true);
        SelectStatementStateObject select = (SelectStatementStateObject) stateObject.getQueryStatement();
        select.addWhereClause();

        DefaultActualJPQLQueryFormatter formatter = new DefaultActualJPQLQueryFormatter(true);
        assertEquals("SELECT e fRom Employee e WHERE", formatter.toString(stateObject));
    }

    /**
     * Exact match, no Expression available.
     */
    @Test
    public void testWhereClause_ExactMatch5() throws Exception {

        String jpqlQuery = "SELECT e fRom Employee e";

        JPQLQueryStateObject stateObject = buildStateObject(jpqlQuery, true);
        SelectStatementStateObject select = (SelectStatementStateObject) stateObject.getQueryStatement();
        select.addWhereClause("e.name = 'JPQL'");

        DefaultActualJPQLQueryFormatter formatter = new DefaultActualJPQLQueryFormatter(true);
        assertEquals("SELECT e fRom Employee e WHERE e.name = 'JPQL'", formatter.toString(stateObject));
    }

    /**
     * No exact match.
     */
    @Test
    public void testWhereClause_NoExactMatch1() throws Exception {

        String jpqlQuery = "sELect e fRom Employee e WheRe e.name = 'JPQL'";

        JPQLQueryStateObject stateObject = buildStateObject(jpqlQuery, true);

        DefaultActualJPQLQueryFormatter formatter = new DefaultActualJPQLQueryFormatter(false);
        assertEquals("SELECT e FROM Employee e WHERE e.name = 'JPQL'", formatter.toString(stateObject));
    }

    /**
     * No exact match.
     */
    @Test
    public void testWhereClause_NoExactMatch2() throws Exception {

        String jpqlQuery = "sELect e fRom Employee e WheRe";

        JPQLQueryStateObject stateObject = buildStateObject(jpqlQuery, true);

        DefaultActualJPQLQueryFormatter formatter = new DefaultActualJPQLQueryFormatter(false);
        assertEquals("SELECT e FROM Employee e WHERE", formatter.toString(stateObject));
    }

    /**
     * No exact match.
     */
    @Test
    public void testWhereClause_NoExactMatch3() throws Exception {

        String jpqlQuery = "sELect e fRom Employee e WheRe ";

        JPQLQueryStateObject stateObject = buildStateObject(jpqlQuery, true);

        DefaultActualJPQLQueryFormatter formatter = new DefaultActualJPQLQueryFormatter(false);
        assertEquals("SELECT e FROM Employee e WHERE", formatter.toString(stateObject));
    }

    /**
     * No exact match.
     */
    @Test
    public void testWhereClause_NoExactMatch4() throws Exception {

        String jpqlQuery = "sELect e fRom Employee e";

        JPQLQueryStateObject stateObject = buildStateObject(jpqlQuery, true);
        SelectStatementStateObject select = (SelectStatementStateObject) stateObject.getQueryStatement();
        select.addWhereClause("e.name = 'JPQL'");

        DefaultActualJPQLQueryFormatter formatter = new DefaultActualJPQLQueryFormatter(false);
        assertEquals("SELECT e FROM Employee e WHERE e.name = 'JPQL'", formatter.toString(stateObject));
    }

    private static class DecoratedActualJPQLQueryFormatter extends AbstractActualJPQLQueryFormatter {

        DecoratedActualJPQLQueryFormatter(boolean exactMatch) {
            super(exactMatch);
        }

        @SuppressWarnings("unused")
        public void visit(DecoratedSelectStatementStateObject stateObject) {
            writer.append("Decorated ");
            stateObject.getParent().accept(this);
        }
    }

    private static class DecoratedSelectStatementStateObject extends AbstractStateObject {

        DecoratedSelectStatementStateObject(SelectStatementStateObject parent) {
            super(parent);
        }

        public void accept(StateObjectVisitor visitor) {
            acceptUnknownVisitor(visitor);
        }

        @Override
        public SelectStatementStateObject getParent() {
            return (SelectStatementStateObject) super.getParent();
        }

        @Override
        protected void toTextInternal(Appendable writer) throws IOException {
            writer.append("Decorated ");
            getParent().toText(writer);
        }
    }
}
