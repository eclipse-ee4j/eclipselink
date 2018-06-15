/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation
//
package org.eclipse.persistence.jpa.tests.jpql.tools;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.JPQLQueryDeclaration.Type;
import org.eclipse.persistence.jpa.jpql.parser.AbstractTraverseChildrenVisitor;
import org.eclipse.persistence.jpa.jpql.parser.Expression;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar;
import org.eclipse.persistence.jpa.jpql.parser.Join;
import org.eclipse.persistence.jpa.jpql.parser.SimpleSelectStatement;
import org.eclipse.persistence.jpa.jpql.tools.JPQLQueryContext;
import org.eclipse.persistence.jpa.jpql.tools.resolver.Declaration;
import org.eclipse.persistence.jpa.jpql.tools.spi.IQuery;
import org.eclipse.persistence.jpa.jpql.utility.CollectionTools;
import org.eclipse.persistence.jpa.tests.jpql.JPQLCoreTest;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLGrammarTestHelper;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.DeleteClauseTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.ExpressionTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.JoinTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.RangeVariableDeclarationTester;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTest.UpdateClauseTester;
import org.eclipse.persistence.jpa.tests.jpql.tools.spi.java.JavaQuery;
import org.junit.Test;
import static org.eclipse.persistence.jpa.tests.jpql.parser.JPQLParserTester.*;
import static org.junit.Assert.*;

/**
 * Unit-tests for {@link Declaration}.
 *
 * @version 2.5
 * @since 2.5
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public abstract class DeclarationTest extends JPQLCoreTest {

    @JPQLGrammarTestHelper
    protected JPQLGrammar jpqlGrammar;
    private JavaQuery virtualQuery;

    protected final IQuery buildQuery(String jpqlQuery) {
        virtualQuery.setExpression(jpqlQuery);
        return virtualQuery;
    }

    protected abstract JPQLQueryContext buildQueryContext();

    protected final CollectionDeclarationTester collectionDeclaration(String collectionPath,
                                                                      String variableName) {

        CollectionDeclarationTester declaration = new CollectionDeclarationTester();
        declaration.baseExpression = collectionPath(collectionPath);
        declaration.rootPath       = collectionPath;

        if (variableName == null) {
            declaration.declarationExpression = fromIn(declaration.baseExpression, nullExpression());
            declaration.variableName          = ExpressionTools.EMPTY_STRING;
        }
        else {
            declaration.declarationExpression = fromIn(declaration.baseExpression, variable(variableName));
            declaration.variableName          = variableName.toUpperCase().intern();
        }

        return declaration;
    }

    protected final CollectionDeclarationTester derivedCollectionDeclaration(String collectionPath) {

        CollectionDeclarationTester declaration = new CollectionDeclarationTester();
        declaration.baseExpression        = collectionPath(collectionPath);
        declaration.rootPath              = collectionPath;
        declaration.declarationExpression = subFromIn(declaration.baseExpression);
        declaration.variableName          = ExpressionTools.EMPTY_STRING;

        return declaration;
    }

    protected final DerivedDeclarationTester derivedRangeDeclaration(String derivedPath,
                                                                     String variableName,
                                                                     JoinTester... joins) {

        DerivedDeclarationTester declaration = new DerivedDeclarationTester();

        if (variableName == null) {
            declaration.baseExpression = rangeVariableDeclaration(collectionPath(derivedPath));
            declaration.variableName   = ExpressionTools.EMPTY_STRING;
        }
        else {

            RangeVariableDeclarationTester rangeVariableDeclaration = rangeVariableDeclaration(
                collectionPath(derivedPath),
                variable(variableName)
            );

            if (variableName.length() == 0) {
                rangeVariableDeclaration.hasSpaceAfterAbstractSchemaName = false;
            }

            declaration.baseExpression = rangeVariableDeclaration;
            declaration.variableName   = variableName.toUpperCase().intern();
        }

        declaration.declarationExpression = identificationVariableDeclaration(declaration.baseExpression, joins);
        declaration.rootPath              = derivedPath;
        declaration.joins                 = CollectionTools.list(joins);

        return declaration;
    }

    protected final RangeDeclarationTester rangeDeclaration(String entityName,
                                                            String variableName) {

        return rangeDeclaration(entityName, variableName, null, new JoinTester[0]);
    }

    protected final RangeDeclarationTester rangeDeclaration(String entityName,
                                                            String variableName,
                                                            ExpressionTester declarationExpression) {

        return rangeDeclaration(entityName, variableName, declarationExpression, new JoinTester[0]);
    }

    protected final RangeDeclarationTester rangeDeclaration(String entityName,
                                                            String variableName,
                                                            ExpressionTester declarationExpression,
                                                            JoinTester... joins) {

        RangeDeclarationTester declaration = new RangeDeclarationTester();

        if (variableName == null) {
            declaration.baseExpression = rangeVariableDeclaration(entityName);
            declaration.variableName   = ExpressionTools.EMPTY_STRING;
        }
        else if (variableName == ExpressionTools.EMPTY_STRING) {
            RangeVariableDeclarationTester rangeVariableDeclaration = rangeVariableDeclaration(entityName);
            rangeVariableDeclaration.hasSpaceAfterAbstractSchemaName = true;
            declaration.baseExpression = rangeVariableDeclaration;
            declaration.variableName   = ExpressionTools.EMPTY_STRING;
        }
        else {
            declaration.baseExpression = rangeVariableDeclaration(entityName, variableName);
            declaration.variableName   = variableName.toUpperCase().intern();
        }

        if (declarationExpression != null) {
            declaration.declarationExpression = declarationExpression;
        }
        else {
            declaration.declarationExpression = identificationVariableDeclaration(declaration.baseExpression, joins);
        }

        declaration.rootPath = entityName;

        if (joins.length == 0) {
            declaration.joins = Collections.emptyList();
        }
        else {
            declaration.joins = CollectionTools.list(joins);
        }

        return declaration;
    }

    protected final RangeDeclarationTester rangeDeclarationWithJoins(String entityName,
                                                                     String variableName,
                                                                     JoinTester... joins) {

        return rangeDeclaration(entityName, variableName, null, joins);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setUpClass() throws Exception {
        super.setUpClass();
        virtualQuery = new JavaQuery(getPersistenceUnit(), null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        virtualQuery.setExpression(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void tearDownClass() throws Exception {
        virtualQuery = null;
        super.tearDownClass();
    }

    private void test_Declaration_01(boolean tolerant) throws Exception {

        String jpqlQuery = "select e from Employee e";

        testDeclarations(
            jpqlQuery,
            tolerant,
            rangeDeclaration("Employee", "e")
        );
    }

    @Test
    public void test_Declaration_01_1() throws Exception {
        test_Declaration_01(false);
    }

    @Test
    public void test_Declaration_01_2() throws Exception {
        test_Declaration_01(true);
    }

    private void test_Declaration_02(boolean tolerant) throws Exception {

        String jpqlQuery = "select e from Employee e, Address a";

        testDeclarations(
            jpqlQuery,
            tolerant,
            rangeDeclaration("Employee", "e"),
            rangeDeclaration("Address", "a")
        );
    }

    @Test
    public void test_Declaration_02_1() throws Exception {
        test_Declaration_02(false);
    }

    @Test
    public void test_Declaration_02_2() throws Exception {
        test_Declaration_02(true);
    }

    private void test_Declaration_03(boolean tolerant) throws Exception {

        String jpqlQuery = "select e from Employee e join e.manager m, Address a";

        testDeclarations(
            jpqlQuery,
            tolerant,
            rangeDeclarationWithJoins("Employee", "e", join("e.manager", "m")),
            rangeDeclaration("Address", "a")
        );
    }

    @Test
    public void test_Declaration_03_1() throws Exception {
        test_Declaration_03(false);
    }

    @Test
    public void test_Declaration_03_2() throws Exception {
        test_Declaration_03(true);
    }

    private void test_Declaration_04(boolean tolerant) throws Exception {

        String jpqlQuery = "select e from Employee e, in(e.phoneNumbers) p";

        testDeclarations(
            jpqlQuery,
            tolerant,
            rangeDeclaration("Employee", "e"),
            collectionDeclaration("e.phoneNumbers", "p")
        );
    }

    @Test
    public void test_Declaration_04_1() throws Exception {
        test_Declaration_04(false);
    }

    @Test
    public void test_Declaration_04_2() throws Exception {
        test_Declaration_04(true);
    }

    @Test
    public void test_Declaration_05() throws Exception {

        String jpqlQuery = "select e from Employee";

        testDeclarations(
            jpqlQuery,
            true,
            rangeDeclaration("Employee", null)
        );
    }

    private void test_Declaration_06(boolean tolerant) throws Exception {

        String jpqlQuery = "select e from Employee e where e.address = (select a from Address a)";

        JPQLQueryContext queryContext = buildQueryContext();
        queryContext.setTolerant(tolerant);
        queryContext.setQuery(buildQuery(jpqlQuery));

        final Map<Expression, DeclarationTester[]> testers = new HashMap<Expression, DeclarationTester[]>();
        testers.put(queryContext.getJPQLExpression(), new DeclarationTester[] { rangeDeclaration("Employee", "e") });

        queryContext.getJPQLExpression().accept(new AbstractTraverseChildrenVisitor() {
            @Override
            public void visit(SimpleSelectStatement expression) {
                testers.put(expression, new DeclarationTester[] { rangeDeclaration("Address", "a") });
            }
        });

        assertFalse(testers.isEmpty());
        testDeclarations(queryContext, testers);
    }

    @Test
    public void test_Declaration_06_1() throws Exception {
        test_Declaration_06(false);
    }

    @Test
    public void test_Declaration_06_2() throws Exception {
        test_Declaration_06(true);
    }

    private void test_Declaration_07(boolean tolerant) throws Exception {

        String jpqlQuery = "select e from Employee e where e.address = (select d from e.dealers d)";

        JPQLQueryContext queryContext = buildQueryContext();
        queryContext.setTolerant(tolerant);
        queryContext.setQuery(buildQuery(jpqlQuery));

        final Map<Expression, DeclarationTester[]> testers = new HashMap<Expression, DeclarationTester[]>();
        testers.put(queryContext.getJPQLExpression(), new DeclarationTester[] { rangeDeclaration("Employee", "e") });

        queryContext.getJPQLExpression().accept(new AbstractTraverseChildrenVisitor() {
            @Override
            public void visit(SimpleSelectStatement expression) {
                testers.put(expression, new DeclarationTester[] { derivedRangeDeclaration("e.dealers", "d") });
            }
        });

        assertEquals(2, testers.size());
        testDeclarations(queryContext, testers);
    }

    @Test
    public void test_Declaration_07_1() throws Exception {
        test_Declaration_07(false);
    }

    @Test
    public void test_Declaration_07_2() throws Exception {
        test_Declaration_07(true);
    }

    private void test_Declaration_08(boolean tolerant) throws Exception {

        String jpqlQuery = "select e from Employee e where e.address = (select d from Buyer b, IN e.dealers)";

        JPQLQueryContext queryContext = buildQueryContext();
        queryContext.setTolerant(tolerant);
        queryContext.setQuery(buildQuery(jpqlQuery));

        final Map<Expression, DeclarationTester[]> testers = new HashMap<Expression, DeclarationTester[]>();
        testers.put(queryContext.getJPQLExpression(), new DeclarationTester[] { rangeDeclaration("Employee", "e") });

        queryContext.getJPQLExpression().accept(new AbstractTraverseChildrenVisitor() {
            @Override
            public void visit(SimpleSelectStatement expression) {
                testers.put(expression, new DeclarationTester[] {
                    rangeDeclaration("Buyer", "b"),
                    derivedCollectionDeclaration("e.dealers"),
                });
            }
        });

        assertEquals(2, testers.size());
        testDeclarations(queryContext, testers);
    }

    @Test
    public void test_Declaration_08_1() throws Exception {
        test_Declaration_08(false);
    }

    @Test
    public void test_Declaration_08_2() throws Exception {
        test_Declaration_08(true);
    }

    private void test_Declaration_09(final boolean tolerant) throws Exception {

        String jpqlQuery = "select e from Employee e where e.address = (select d from e.dealers)";

        JPQLQueryContext queryContext = buildQueryContext();
        queryContext.setTolerant(tolerant);
        queryContext.setQuery(buildQuery(jpqlQuery));

        final Map<Expression, DeclarationTester[]> testers = new HashMap<Expression, DeclarationTester[]>();
        testers.put(queryContext.getJPQLExpression(), new DeclarationTester[] { rangeDeclaration("Employee", "e") });

        queryContext.getJPQLExpression().accept(new AbstractTraverseChildrenVisitor() {
            @Override
            public void visit(SimpleSelectStatement expression) {
                testers.put(expression, new DeclarationTester[] {
                    derivedRangeDeclaration("e.dealers", tolerant ? null : ExpressionTools.EMPTY_STRING)
                });
            }
        });

        assertEquals(2, testers.size());
        testDeclarations(queryContext, testers);
    }

    @Test
    public void test_Declaration_09_1() throws Exception {
        test_Declaration_09(false);
    }

    @Test
    public void test_Declaration_09_2() throws Exception {
        test_Declaration_09(true);
    }

    private void test_Declaration_10(boolean tolerant) throws Exception {
        String jpqlQuery = "select e from";
        testDeclarations(jpqlQuery, tolerant);
    }

    @Test
    public void test_Declaration_10_1() throws Exception {
        test_Declaration_10(false);
    }

    @Test
    public void test_Declaration_10_2() throws Exception {
        test_Declaration_10(true);
    }

    @Test
    public void test_Declaration_11() throws Exception {

        String jpqlQuery = "select e from Employee e, where e.name = 'JPQL'";

        testDeclarations(
            jpqlQuery,
            true,
            rangeDeclaration("Employee", "e")
        );
    }

    @Test
    public void test_Declaration_12() throws Exception {

        String jpqlQuery = "select e from Employee e, a where e.name = 'JPQL'";

        testDeclarations(
            jpqlQuery,
            true,
            rangeDeclaration("Employee", "e"),
            rangeDeclaration("a", ExpressionTools.EMPTY_STRING)
        );
    }

    private void test_Declaration_13(boolean tolerant) throws Exception {

        String jpqlQuery = "update Employee e set e.name = 'JPQL'";

        UpdateClauseTester updateClause = update(
            "Employee",
            "e",
            set("e.name", string("'JPQL'"))
        );

        testDeclarations(
            jpqlQuery,
            tolerant,
            rangeDeclaration("Employee", "e", updateClause)
        );
    }

    @Test
    public void test_Declaration_13_1() throws Exception {
        test_Declaration_13(false);
    }

    @Test
    public void test_Declaration_13_2() throws Exception {
        test_Declaration_13(true);
    }

    @Test
    public void test_Declaration_14() throws Exception {

        String jpqlQuery = "update set e.name = 'JPQL'";

        UpdateClauseTester updateClause = update(
            nullExpression(),
            set("e.name", string("'JPQL'"))
        );

        testDeclarations(
            jpqlQuery,
            true,
            unknownDeclaration(updateClause)
        );
    }

    @Test
    public void test_Declaration_15() throws Exception {

        String jpqlQuery = "delete from where e.name = 'JPQL'";
        DeleteClauseTester deleteClause = delete(nullExpression());

        testDeclarations(
            jpqlQuery,
            true,
            unknownDeclaration(deleteClause)
        );
    }

    protected final void testDeclarations(JPQLQueryContext queryContext,
                                          Map<Expression, DeclarationTester[]> declarationTesters) {

        for (Map.Entry<Expression, DeclarationTester[]> entry : declarationTesters.entrySet()) {

            Expression expression = entry.getKey();
            DeclarationTester[] testers = entry.getValue();

            if (expression.getRoot() != expression) {
                queryContext.newSubqueryContext(expression);
            }

            // Retrieve the list of Declarations
            List<Declaration> declarations = queryContext.getDeclarations();
            assertNotNull(declarations);
            assertEquals(testers.length, declarations.size());

            // Test each one of them
            for (int index = testers.length; --index >= 0; ) {
                Declaration declaration = declarations.get(index);
                DeclarationTester tester = testers[index];
                tester.test(declaration);
            }

            if (expression.getRoot() != expression) {
                queryContext.disposeSubqueryContext();
            }
        }
    }

    protected final void testDeclarations(String jpqlQuery,
                                          boolean tolerant,
                                          DeclarationTester... declarationTesters) {

        // Build the JPQLQueryContext, which will parse the JPQL query and create the list of Declarations
        JPQLQueryContext queryContext = buildQueryContext();
        queryContext.setTolerant(tolerant);
        queryContext.setQuery(buildQuery(jpqlQuery));

        // Retrieve the list of Declarations
        List<Declaration> declarations = queryContext.getDeclarations();
        assertNotNull(declarations);
        assertEquals(declarationTesters.length, declarations.size());

        // Test each one of them
        for (int index = declarationTesters.length; --index >= 0; ) {
            Declaration declaration = declarations.get(index);
            DeclarationTester tester = declarationTesters[index];
            tester.test(declaration);
        }
    }

    protected final UnknownDeclarationTester unknownDeclaration(ExpressionTester declarationExpression) {

        UnknownDeclarationTester declaration = new UnknownDeclarationTester();
        declaration.baseExpression         = nullExpression();
        declaration.declarationExpression  = declarationExpression;
        declaration.rootPath               = ExpressionTools.EMPTY_STRING;
        declaration.variableName           = ExpressionTools.EMPTY_STRING;

        return declaration;
    }

    protected static abstract class AbstractRangeDeclarationTester extends DeclarationTester {

        protected List<JoinTester> joins;

        @Override
        protected void test(Declaration declaration) {
            super.test(declaration);

            List<Join> declarationJoins = declaration.getJoins();
            assertEquals(!joins.isEmpty(), declaration.hasJoins());
            assertEquals(joins.size(),     declarationJoins.size());

            for (int index = joins.size(); --index >= 0; ) {
                JoinTester expectedJoin = joins.get(index);
                Join resultJoin = declarationJoins.get(index);
                expectedJoin.test(resultJoin);
            }
        }
    }

    protected class CollectionDeclarationTester extends DeclarationTester {
        @Override
        protected Type getType() {
            return Type.COLLECTION;
        }
    }

    protected abstract static class DeclarationTester {

        protected ExpressionTester baseExpression;
        protected ExpressionTester declarationExpression;
        protected String rootPath;
        protected String variableName;

        protected abstract Type getType();

        protected void test(Declaration declaration) {

            baseExpression.test(declaration.getBaseExpression());
            declarationExpression.test(declaration.getDeclarationExpression());

            assertSame  (getType(),    declaration.getType());
            assertSame  (variableName, declaration.getVariableName());
            assertEquals(rootPath,     declaration.getRootPath());
        }
    }

    protected static class DerivedDeclarationTester extends AbstractRangeDeclarationTester {
        @Override
        protected Type getType() {
            return Type.DERIVED;
        }
    }

    protected static class RangeDeclarationTester extends AbstractRangeDeclarationTester {
        @Override
        protected Type getType() {
            return Type.RANGE;
        }
    }

    protected static class UnknownDeclarationTester extends DeclarationTester {
        @Override
        protected Type getType() {
            return Type.UNKNOWN;
        }
    }
}
