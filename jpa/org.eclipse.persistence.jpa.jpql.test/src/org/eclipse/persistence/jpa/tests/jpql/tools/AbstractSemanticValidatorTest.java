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
package org.eclipse.persistence.jpa.tests.jpql.tools;

import java.util.List;
import org.eclipse.persistence.jpa.jpql.AbstractSemanticValidator;
import org.eclipse.persistence.jpa.jpql.JPQLQueryProblem;
import org.eclipse.persistence.jpa.jpql.SemanticValidatorHelper;
import org.eclipse.persistence.jpa.jpql.parser.JPQLExpression;
import org.eclipse.persistence.jpa.jpql.tools.GenericSemanticValidatorHelper;
import org.eclipse.persistence.jpa.jpql.tools.JPQLQueryContext;
import org.eclipse.persistence.jpa.jpql.tools.spi.IQuery;
import org.eclipse.persistence.jpa.tests.jpql.AbstractValidatorTest;
import org.eclipse.persistence.jpa.tests.jpql.tools.spi.java.JavaQuery;
import org.junit.Ignore;
import org.junit.Test;
import static org.eclipse.persistence.jpa.jpql.JPQLQueryProblemMessages.*;

/**
 * The base definition of a test class used for testing a JPQL jpqlQuery semantically.
 *
 * @see AbstractSemanticValidator
 *
 * @version 2.5.2
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public abstract class AbstractSemanticValidatorTest extends AbstractValidatorTest {

    /**
     * The instance of {@link JPQLQueryContext} that is used when running the unit-tests defined by
     * the subclass. The instance is shared between each the unit-test.
     */
    private JPQLQueryContext queryContext;

    /**
     * Creates a new external form for the given JPQL jpqlQuery.
     *
     * @param jpqlQuery The JPQL jpqlQuery to wrap with a {@link IQuery}
     * @return The external form for the JPQL jpqlQuery
     * @throws Exception If a problem was encountered while access the application metadata
     */
    protected IQuery buildQuery(String jpqlQuery) throws Exception {
        return new JavaQuery(getPersistenceUnit(), jpqlQuery);
    }

    /**
     * Creates a new {@link JPQLQueryContext}.
     *
     * @return A instance of {@link JPQLQueryContext} that is used to cache information about the
     * JPQL jpqlQuery being manipulated
     */
    protected abstract JPQLQueryContext buildQueryContext();

    /**
     * Creates a new {@link SemanticValidatorHelper}, which is used by {@link AbstractSemanticValidator}
     * in order to access the JPA metadata. The unit-tests uses {@link GenericSemanticValidatorHelper},
     * which simply accesses Hermes SPI and delegates the calls to {@link JPQLQueryContext}.
     *
     * @return A new instance of {@link GenericSemanticValidatorHelper}
     */
    protected SemanticValidatorHelper buildSemanticValidatorHelper() {
        return new GenericSemanticValidatorHelper(queryContext);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected abstract AbstractSemanticValidator buildValidator();

    protected final JPQLQueryContext getQueryContext() {
        return queryContext;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected AbstractSemanticValidator getValidator() {
        return (AbstractSemanticValidator) super.getValidator();
    }

    protected abstract boolean isComparisonTypeChecked();

    protected abstract boolean isPathExpressionToCollectionMappingAllowed();

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setUpClass() throws Exception {
        queryContext = buildQueryContext();
        super.setUpClass();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void tearDown() throws Exception {
        queryContext.dispose();
        super.tearDown();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void tearDownClass() throws Exception {
        queryContext = null;
        super.tearDownClass();
    }

    @Test
    public final void test_AbstractFromClause_InvalidFirstIdentificationVariableDeclaration_01() throws Exception {

        String jpqlQuery = "select e from Employee e";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasNoProblems(problems);
    }

    @Test
    public final void test_AbstractFromClause_InvalidFirstIdentificationVariableDeclaration_02() throws Exception {

        String jpqlQuery = "select e from Employee e where e.managerEmployee IN (select e2 from Employee e2)";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasNoProblems(problems);
    }

    @Test
    public final void test_AbstractFromClause_InvalidFirstIdentificationVariableDeclaration_03() throws Exception {

        String jpqlQuery = "select e from Employee e where e.address IN (select a from e.address a)";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasNoProblems(problems);
    }

    @Test
    public final void test_AbstractFromClause_InvalidFirstIdentificationVariableDeclaration_04() throws Exception {

        String jpqlQuery = "select e from Employee e where EXISTS(select p from in e.phoneNumbers p)";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasNoProblems(problems);
    }

    @Test
    public final void test_AbstractFromClause_InvalidFirstIdentificationVariableDeclaration_05() throws Exception {

        String jpqlQuery  = "select e from in(e.address.street) as e";
        int startPosition = "select e from ".length();
        int endPosition   = jpqlQuery.length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            AbstractFromClause_InvalidFirstIdentificationVariableDeclaration,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_AbstractFromClause_InvalidFirstIdentificationVariableDeclaration_06() throws Exception {

        String jpqlQuery  = "select e from e.address.street as e";
        int startPosition = "select e from ".length();
        int endPosition   = jpqlQuery.length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            AbstractFromClause_InvalidFirstIdentificationVariableDeclaration,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_AbstractFromClause_WrongOrderOfIdentificationVariableDeclaration() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e JOIN a.people p, Address a";
        int startPosition = "SELECT e FROM Employee e JOIN ".length();
        int endPosition   = startPosition + 1;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            AbstractFromClause_WrongOrderOfIdentificationVariableDeclaration,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_AbstractSchemaName_Invalid_1() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE ABS(e.name)";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, AbstractSchemaName_Invalid);
    }

    @Test
    public final void test_AbstractSchemaName_Invalid_2() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee2 e WHERE ABS(e.name)";
        int startPosition = "SELECT e FROM ".length();
        int endPosition   = "SELECT e FROM Employee2".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            AbstractSchemaName_Invalid,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_AbstractSchemaName_Invalid_3() throws Exception {

        String jpqlQuery  = "select e from Employee e where e.empId in (select e.empId from xx x)";
        int startPosition = "select e from Employee e where e.empId in(select e.empId from ".length();
        int endPosition   = "select e from Employee e where e.empId in(select e.empId from xx".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            AbstractSchemaName_Invalid,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_AbstractSchemaName_Invalid_4() throws Exception {

        String jpqlQuery   = "delete aaa aaa a";

        int startPosition1 = "delete ".length();
        int endPosition1   = "delete aaa".length();

        int startPosition2 = "delete aaa aaa ".length();
        int endPosition2   = "delete aaa aaa a".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyTheseProblems(
            problems,
            new String[] { AbstractSchemaName_Invalid, AbstractSchemaName_Invalid },
            new int[] { startPosition1, startPosition2 },
            new int[] { endPosition1,   endPosition2 }
        );
    }

    @Test
    public final void test_AbstractSchemaName_Invalid_5() throws Exception {

        String jpqlQuery = "update Employee set name = 'JPQL' " +
                           "where (select a from address a where a.city = 'Cary') is not null";

        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasNoProblems(problems);
    }

    @Test
    public final void test_CollectionValuedPathExpression_NotCollectionType_01() throws Exception {

        String jpqlQuery = "SELECT a FROM Address a JOIN a.customerList c";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, CollectionValuedPathExpression_NotCollectionType);
    }

    @Test
    public final void test_CollectionValuedPathExpression_NotCollectionType_02() throws Exception {

        String jpqlQuery  = "SELECT a FROM Address a JOIN a.state c";
        int startPosition = "SELECT a FROM Address a JOIN ".length();
        int endPosition   = "SELECT a FROM Address a JOIN a.state".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            CollectionValuedPathExpression_NotCollectionType,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_CollectionValuedPathExpression_NotCollectionType_03() throws Exception {

        String jpqlQuery  = "SELECT a FROM Address a WHERE a.state IS NOT EMPTY";
        int startPosition = "SELECT a FROM Address a WHERE ".length();
        int endPosition   = "SELECT a FROM Address a WHERE a.state".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            CollectionValuedPathExpression_NotCollectionType,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_CollectionValuedPathExpression_NotCollectionType_04() throws Exception {

        String jpqlQuery = "SELECT a FROM Address a WHERE a.customerList IS NOT EMPTY";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, CollectionValuedPathExpression_NotCollectionType);
    }

    @Test
    public final void test_CollectionValuedPathExpression_NotCollectionType_05() throws Exception {

        String jpqlQuery  = "SELECT a FROM Address a, IN(a.zip) c";
        int startPosition = "SELECT a FROM Address a, IN(".length();
        int endPosition   = "SELECT a FROM Address a, IN(a.zip".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            CollectionValuedPathExpression_NotCollectionType,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_CollectionValuedPathExpression_NotCollectionType_06() throws Exception {

        String jpqlQuery = "SELECT a FROM Address a IN(a.customerList) c";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, CollectionValuedPathExpression_NotCollectionType);
    }

    @Test
    public final void test_CollectionValuedPathExpression_NotCollectionType_07() throws Exception {

        String jpqlQuery = "SELECT a FROM Address a, Customer c WHERE c MEMBER OF a.customerList";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, CollectionValuedPathExpression_NotCollectionType);
    }

    @Test
    public final void test_CollectionValuedPathExpression_NotCollectionType_08() throws Exception {

        String jpqlQuery  = "SELECT a FROM Address a, Customer c WHERE c MEMBER OF a.state";
        int startPosition = "SELECT a FROM Address a, Customer c WHERE c MEMBER OF ".length();
        int endPosition   = "SELECT a FROM Address a, Customer c WHERE c MEMBER OF a.state".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            CollectionValuedPathExpression_NotCollectionType,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_CollectionValuedPathExpression_NotCollectionType_09() throws Exception {

        String jpqlQuery = "SELECT a FROM Address a WHERE SIZE(a.customerList) = 2";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, CollectionValuedPathExpression_NotCollectionType);
    }

    @Test
    public final void test_CollectionValuedPathExpression_NotCollectionType_10() throws Exception {

        String jpqlQuery  = "SELECT a FROM Address a WHERE SIZE(a.zip) = 2";
        int startPosition = "SELECT a FROM Address a WHERE SIZE(".length();
        int endPosition   = "SELECT a FROM Address a WHERE SIZE(a.zip".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            CollectionValuedPathExpression_NotCollectionType,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_CollectionValuedPathExpression_NotResolvable_1() throws Exception {

        String jpqlQuery = "SELECT a FROM Address a JOIN a.customerList c";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, CollectionValuedPathExpression_NotResolvable);
    }

    @Test
    public final void test_CollectionValuedPathExpression_NotResolvable_2() throws Exception {

        String jpqlQuery  = "SELECT a FROM Address a JOIN a.wrong c";
        int startPosition = "SELECT a FROM Address a JOIN ".length();
        int endPosition   = "SELECT a FROM Address a JOIN a.wrong".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            CollectionValuedPathExpression_NotResolvable,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_ComparisonExpression_AssociationField_01() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE e.address < 2";
        int startPosition = "SELECT e FROM Employee e WHERE ".length();
        int endPosition   = "SELECT e FROM Employee e WHERE e.address".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasOnlyOneProblem(problems, ComparisonExpression_AssociationField, startPosition, endPosition);
    }

    @Test
    public final void test_ComparisonExpression_AssociationField_02() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE e.address > 2";
        int startPosition = "SELECT e FROM Employee e WHERE ".length();
        int endPosition   = "SELECT e FROM Employee e WHERE e.address".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasOnlyOneProblem(problems, ComparisonExpression_AssociationField, startPosition, endPosition);
    }

    @Test
    public final void test_ComparisonExpression_AssociationField_03() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE e.address <= 2";
        int startPosition = "SELECT e FROM Employee e WHERE ".length();
        int endPosition   = "SELECT e FROM Employee e WHERE e.address".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasOnlyOneProblem(problems, ComparisonExpression_AssociationField, startPosition, endPosition);
    }

    @Test
    public final void test_ComparisonExpression_AssociationField_04() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE e.address >= 2";
        int startPosition = "SELECT e FROM Employee e WHERE ".length();
        int endPosition   = "SELECT e FROM Employee e WHERE e.address".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasOnlyOneProblem(problems, ComparisonExpression_AssociationField, startPosition, endPosition);
    }

    @Test
    public final void test_ComparisonExpression_AssociationField_05() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE 2 < e.address";
        int startPosition = "SELECT e FROM Employee e WHERE 2 < ".length();
        int endPosition   = "SELECT e FROM Employee e WHERE 2 < e.address".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasOnlyOneProblem(problems, ComparisonExpression_AssociationField, startPosition, endPosition);
    }

    @Test
    public final void test_ComparisonExpression_AssociationField_06() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE 2 > e.address";
        int startPosition = "SELECT e FROM Employee e WHERE 2 > ".length();
        int endPosition   = "SELECT e FROM Employee e WHERE 2 > e.address".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasOnlyOneProblem(problems, ComparisonExpression_AssociationField, startPosition, endPosition);
    }

    @Test
    public final void test_ComparisonExpression_AssociationField_07() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE 2 <= e.address";
        int startPosition = "SELECT e FROM Employee e WHERE 2 <= ".length();
        int endPosition   = "SELECT e FROM Employee e WHERE 2 <= e.address".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasOnlyOneProblem(problems, ComparisonExpression_AssociationField, startPosition, endPosition);
    }

    @Test
    public final void test_ComparisonExpression_AssociationField_08() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE 2 >= e.address";
        int startPosition = "SELECT e FROM Employee e WHERE 2 >= ".length();
        int endPosition   = "SELECT e FROM Employee e WHERE 2 >= e.address".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasOnlyOneProblem(problems, ComparisonExpression_AssociationField, startPosition, endPosition);
    }

    @Test
    public final void test_ComparisonExpression_AssociationField_09() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e, Address a WHERE a = e.address";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasNoProblems(problems);
    }

    @Test
    public final void test_ComparisonExpression_AssociationField_10() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e, Address a WHERE a <> e.address";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasNoProblems(problems);
    }

    @Test
    public final void test_ComparisonExpression_AssociationField_11() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE e.embeddedAddress > 2";
        int startPosition = "SELECT e FROM Employee e WHERE ".length();
        int endPosition   = "SELECT e FROM Employee e WHERE e.embeddedAddress".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            ComparisonExpression_AssociationField,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_ComparisonExpression_AssociationField_12() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE e.managerEmployee > 2";
        int startPosition = "SELECT e FROM Employee e WHERE ".length();
        int endPosition   = "SELECT e FROM Employee e WHERE e.managerEmployee".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            ComparisonExpression_AssociationField,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_ComparisonExpression_BasicField_01() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE e.roomNumber < 2";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasNoProblems(problems);
    }

    @Test
    public final void test_ComparisonExpression_BasicField_02() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE e.roomNumber > 2";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasNoProblems(problems);
    }

    @Test
    public final void test_ComparisonExpression_BasicField_03() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE e.roomNumber <= 2";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasNoProblems(problems);
    }

    @Test
    public final void test_ComparisonExpression_BasicField_04() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE e.roomNumber >= 2";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasNoProblems(problems);
    }

    @Test
    public final void test_ComparisonExpression_BasicField_05() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE 2 < e.roomNumber";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasNoProblems(problems);
    }

    @Test
    public final void test_ComparisonExpression_BasicField_06() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE 2 > e.roomNumber";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasNoProblems(problems);
    }

    @Test
    public final void test_ComparisonExpression_BasicField_07() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE 2 <= e.roomNumber";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasNoProblems(problems);
    }

    @Test
    public final void test_ComparisonExpression_BasicField_08() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE 2 >= e.roomNumber";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasNoProblems(problems);
    }

    @Test
    public final void test_ComparisonExpression_BasicField_09() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e, Address a WHERE a = e.salary";
        int startPosition = "SELECT e FROM Employee e, Address a WHERE a = ".length();
        int endPosition   = "SELECT e FROM Employee e, Address a WHERE a = e.salary".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasOnlyOneProblem(problems, ComparisonExpression_BasicField, startPosition, endPosition);
    }

    @Test
    public final void test_ComparisonExpression_BasicField_10() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e, Address a WHERE a <> e.salary";
        int startPosition = "SELECT e FROM Employee e, Address a WHERE a <> ".length();
        int endPosition   = "SELECT e FROM Employee e, Address a WHERE a <> e.salary".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasOnlyOneProblem(problems, ComparisonExpression_BasicField, startPosition, endPosition);
    }

    @Test
    public final void test_ComparisonExpression_BasicField_11() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e, Address a WHERE e.salary = a";
        int startPosition = "SELECT e FROM Employee e, Address a WHERE ".length();
        int endPosition   = "SELECT e FROM Employee e, Address a WHERE e.salary".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasOnlyOneProblem(problems, ComparisonExpression_BasicField, startPosition, endPosition);
    }

    @Test
    public final void test_ComparisonExpression_BasicField_12() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e, Address a WHERE e.salary <> a";
        int startPosition = "SELECT e FROM Employee e, Address a WHERE ".length();
        int endPosition   = "SELECT e FROM Employee e, Address a WHERE e.salary".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasOnlyOneProblem(problems, ComparisonExpression_BasicField, startPosition, endPosition);
    }

    @Test
    public final void test_ComparisonExpression_IdentificationVariable_01() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE e > 2";
        int startPosition = "SELECT e FROM Employee e WHERE ".length();
        int endPosition   = "SELECT e FROM Employee e WHERE e".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            ComparisonExpression_IdentificationVariable,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_ComparisonExpression_IdentificationVariable_02() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE e < 2";
        int startPosition = "SELECT e FROM Employee e WHERE ".length();
        int endPosition   = "SELECT e FROM Employee e WHERE e".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            ComparisonExpression_IdentificationVariable,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_ComparisonExpression_IdentificationVariable_03() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE e >= 2";
        int startPosition = "SELECT e FROM Employee e WHERE ".length();
        int endPosition   = "SELECT e FROM Employee e WHERE e".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            ComparisonExpression_IdentificationVariable,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_ComparisonExpression_IdentificationVariable_04() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE e <= 2";
        int startPosition = "SELECT e FROM Employee e WHERE ".length();
        int endPosition   = "SELECT e FROM Employee e WHERE e".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            ComparisonExpression_IdentificationVariable,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_ComparisonExpression_IdentificationVariable_05() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE 2 < e";
        int startPosition = "SELECT e FROM Employee e WHERE 2 < ".length();
        int endPosition   = "SELECT e FROM Employee e WHERE 2 < e".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            ComparisonExpression_IdentificationVariable,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_ComparisonExpression_IdentificationVariable_06() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE 2 > e";
        int startPosition = "SELECT e FROM Employee e WHERE 2 > ".length();
        int endPosition   = "SELECT e FROM Employee e WHERE 2 > e".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            ComparisonExpression_IdentificationVariable,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_ComparisonExpression_IdentificationVariable_07() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE 2 <= e";
        int startPosition = "SELECT e FROM Employee e WHERE 2 <= ".length();
        int endPosition   = "SELECT e FROM Employee e WHERE 2 <= e".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            ComparisonExpression_IdentificationVariable,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_ComparisonExpression_IdentificationVariable_08() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE 2 >= e";
        int startPosition = "SELECT e FROM Employee e WHERE 2 >= ".length();
        int endPosition   = "SELECT e FROM Employee e WHERE 2 >= e".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            ComparisonExpression_IdentificationVariable,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_ComparisonExpression_IdentificationVariable_09() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE e = e";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasNoProblems(problems);
    }

    @Test
    public final void test_ComparisonExpression_IdentificationVariable_10() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE e <> e";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasNoProblems(problems);
    }

    @Test
    public final void test_ComparisonExpression_IdentificationVariable_11() throws Exception {

        String jpqlQuery = "SELECT LENGTH(e.name) AS n FROM Employee e WHERE n > 2";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasNoProblems(problems);
    }

    @Test
    public final void test_ComparisonExpression_IdentificationVariable_12() throws Exception {

        String jpqlQuery = "SELECT LENGTH(e.name) AS n FROM Employee e WHERE 2 < n";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasNoProblems(problems);
    }

    @Test
    public final void test_ComparisonExpression_IdentificationVariable_13() throws Exception {

        String jpqlQuery = "SELECT LENGTH(e.name) AS n FROM Employee e WHERE n = 2";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasNoProblems(problems);
    }

    @Test
    public final void test_ComparisonExpression_IdentificationVariable_14() throws Exception {

        String jpqlQuery = "SELECT LENGTH(e.name) AS n FROM Employee e WHERE 2 = n";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasNoProblems(problems);
    }

    @Test
    public final void test_ComparisonExpression_WrongComparisonType_01() throws Exception {

        String jpqlQuery  = "SELECT e FROM Address e WHERE e.id = :key AND e.street = (SELECT MAX(c2.street) FROM Address AS c2 WHERE c2.id = :key)";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasNoProblems(problems);
    }

    @Test
    public final void test_ComparisonExpression_WrongComparisonType_02() throws Exception {

        String jpqlQuery  = "SELECT p FROM Product p WHERE p.id = :key AND p.releaseDate = (SELECT MAX(p2.shelfLife.soldDate) FROM Product AS p2 WHERE p2.id = :key)";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasNoProblems(problems);
    }

    @Test
    public final void test_ComparisonExpression_WrongComparisonType_03() throws Exception {

        String jpqlQuery  = "SELECT e FROM Address e WHERE e.id = :key AND e.id = (SELECT MAX(c2.street) FROM Address AS c2 WHERE c2.id = :key)";
        int startPosition = "SELECT e FROM Address e WHERE e.id = :key AND ".length();
        int endPosition   = jpqlQuery.length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        if (isComparisonTypeChecked()) {
            testHasOnlyOneProblem(
                problems,
                ComparisonExpression_WrongComparisonType,
                startPosition,
                endPosition
            );
        }
        else {
            testHasNoProblems(problems);
        }
    }

    @Test
    public final void test_IdentificationVariable_Invalid_Duplicate_1() throws Exception {

        String jpqlQuery   = "SELECT e FROM Employee e, Address e";

        int startPosition1 = "SELECT e FROM Employee ".length();
        int endPosition1   = "SELECT e FROM Employee e".length();

        int startPosition2 = "SELECT e FROM Employee e, Address ".length();
        int endPosition2   = jpqlQuery.length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblems(
            problems,
            new String [] { IdentificationVariable_Invalid_Duplicate,
                            IdentificationVariable_Invalid_Duplicate },
            new int[] { startPosition1, startPosition2 },
            new int[] { endPosition1,   endPosition2   }
        );
    }

    @Test
    public final void test_IdentificationVariable_Invalid_Duplicate_2() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE EXISTS(SELECT e.manager FROM Employee e)";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasNoProblems(problems);
    }

    @Test
    public final void test_IdentificationVariable_Invalid_Duplicate_3() throws Exception {

        String jpqlQuery   = "SELECT e FROM Employee e WHERE EXISTS(SELECT e.manager FROM Employee e, Address e)";

        int startPosition1 = "SELECT e FROM Employee e WHERE EXISTS(SELECT e.manager FROM Employee ".length();
        int endPosition1   = "SELECT e FROM Employee e WHERE EXISTS(SELECT e.manager FROM Employee e".length();

        int startPosition2 = "SELECT e FROM Employee e WHERE EXISTS(SELECT e.manager FROM Employee e, Address ".length();
        int endPosition2   = "SELECT e FROM Employee e WHERE EXISTS(SELECT e.manager FROM Employee e, Address e".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblems(
            problems,
            new String [] { IdentificationVariable_Invalid_Duplicate,
                            IdentificationVariable_Invalid_Duplicate },
            new int[] { startPosition1, startPosition2 },
            new int[] { endPosition1,   endPosition2   }
        );
    }

    @Test
    public final void test_IdentificationVariable_Invalid_Duplicate_4() throws Exception {

        String jpqlQuery = "SELECT COUNT(e) AS es FROM Employee e ORDER BY es";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasNoProblems(problems);
    }

    @Test
    public final void test_IdentificationVariable_Invalid_Duplicate_5() throws Exception {

        String jpqlQuery   = "SELECT COUNT(e) AS e FROM Employee e ORDER BY e";

        int startPosition1 = "SELECT COUNT(e) AS ".length();
        int endPosition1   = "SELECT COUNT(e) AS e".length();

        int startPosition2 = "SELECT COUNT(e) AS e FROM Employee ".length();
        int endPosition2   = "SELECT COUNT(e) AS e FROM Employee e".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblems(
            problems,
            new String [] { IdentificationVariable_Invalid_Duplicate,
                            IdentificationVariable_Invalid_Duplicate },
            new int[] { startPosition1, startPosition2 },
            new int[] { endPosition1,   endPosition2   }
        );
    }

    @Test
    public final void test_IdentificationVariable_Invalid_NotDeclared_1() throws Exception {

        String jpqlQuery  = "SELECT a FROM Employee e";
        int startPosition = "SELECT ".length();
        int endPosition   = "SELECT a".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            IdentificationVariable_Invalid_NotDeclared,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_IdentificationVariable_Invalid_NotDeclared_2() throws Exception {

        String jpqlQuery  = "SELECT a.name FROM Employee e";
        int startPosition = "SELECT ".length();
        int endPosition   = "SELECT a".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            IdentificationVariable_Invalid_NotDeclared,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_IdentificationVariable_Invalid_NotDeclared_3() throws Exception {

        String jpqlQuery = "select t from TestEntity t where t.id = (select max(tt.id) from TestEntity tt)";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, IdentificationVariable_Invalid_NotDeclared);
    }

    @Test
    public final void test_IdentificationVariable_Invalid_NotDeclared_4() throws Exception {

        String jpqlQuery = "select t from TestEntity t where t.id = (select max(t.id) from TestEntity tt)";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, IdentificationVariable_Invalid_NotDeclared);
    }

    @Test
    public final void test_IdentificationVariable_Invalid_NotDeclared_5() throws Exception {

        String jpqlQuery  = "select t from TestEntity t where t.id = (select max(e.id) from TestEntity tt)";
        int startPosition = "select t from TestEntity t where t.id = (select max(".length();
        int endPosition   = startPosition + 1;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            IdentificationVariable_Invalid_NotDeclared,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_IdentificationVariable_Invalid_NotDeclared_6() throws Exception {

        String jpqlQuery  = "select e from Employee e where e.empId in (select xx from Employee e)";
        int startPosition = "select e from Employee e where e.empId in(select ".length();
        int endPosition   = "select e from Employee e where e.empId in(select xx".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            IdentificationVariable_Invalid_NotDeclared,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_IdentificationVariable_Valid_1() throws Exception {

        String jpqlQuery = "SELECT MAX(e.roomNumber) mois, " +
                           "       SUBSTRING(e.department) annee, " +
                           "       e.address.city categ, " +
                           "       SUM(e.salary) " +
                           "FROM Employee e " +
                           "GROUP BY annee, mois, categ " +
                           "ORDER BY annee ASC, mois ASC, categ ASC";

        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasNoProblems(problems);
    }

    @Test
    public final void test_IndexExpression_WrongVariable_1() throws Exception {

        String jpqlQuery = "SELECT c FROM Customer c JOIN c.aliases t WHERE c.holder.name = 'John Doe' AND INDEX(t) BETWEEN 0 AND 9";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, IndexExpression_WrongVariable);
    }

    @Test
    public final void test_IndexExpression_WrongVariable_2() throws Exception {

        String jpqlQuery = "SELECT c FROM Customer c, IN(c.aliases) t WHERE c.holder.name = 'John Doe' AND INDEX(t) BETWEEN 0 AND 9";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, IndexExpression_WrongVariable);
    }

    @Test
    public final void test_IndexExpression_WrongVariable_3() throws Exception {

        String jpqlQuery  = "SELECT c FROM Customer c JOIN c.aliases t WHERE c.holder.name = 'John Doe' AND INDEX(c) BETWEEN 0 AND 9";
        int startPosition = "SELECT c FROM Customer c JOIN a.aliases t WHERE c.holder.name = 'John Doe' AND INDEX(".length();
        int endPosition   = startPosition + 1;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            IndexExpression_WrongVariable,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_IndexExpression_WrongVariable_4() throws Exception {

        String jpqlQuery  = "SELECT c FROM Customer c, IN(c.aliases) t WHERE c.holder.name = 'John Doe' AND INDEX(c) BETWEEN 0 AND 9";
        int startPosition = "SELECT c FROM Customer c, IN(a.aliases) t WHERE c.holder.name = 'John Doe' AND INDEX(".length();
        int endPosition   = startPosition + 1;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            IndexExpression_WrongVariable,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_IndexExpression_WrongVariable_5() throws Exception {

        String jpqlQuery  = "SELECT c FROM Customer c, IN(c.aliases) t WHERE EXISTS(SELECT e FROM Employee e WHERE c.holder.name = 'John Doe' AND INDEX(c) BETWEEN 0 AND 9)";
        int startPosition = "SELECT c FROM Customer c, IN(a.aliases) t WHERE EXISTS(SELECT e FROM Employee e WHERE c.holder.name = 'John Doe' AND INDEX(".length();
        int endPosition   = startPosition + 1;

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            IndexExpression_WrongVariable,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_InExpression_InvalidExpression_1() throws Exception {

        String jpqlQuery = "SELECT prod FROM Product prod WHERE TYPE(prod.project) IN(LargeProject, SmallProject)";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasNoProblems(problems);
    }

    @Test
    @Ignore
    // The semantic validator does not check the type of the left expression with the items
    public final void test_InExpression_InvalidExpression_2() throws Exception {

        String jpqlQuery = "SELECT prod FROM Product prod WHERE prod.project IN(2, 3)";

        int startPosition1 = "SELECT prod FROM Product prod WHERE prod.project IN(".length();
        int endPosition1   = "SELECT prod FROM Product prod WHERE prod.project IN(2".length();

        int startPosition2 = "SELECT prod FROM Product prod WHERE prod.project IN(2, ".length();
        int endPosition2   = "SELECT prod FROM Product prod WHERE prod.project IN(2, 3".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyTheseProblems(
            problems,
            new String[] { InExpression_InvalidExpression, InExpression_InvalidExpression },
            new int[] { startPosition1, startPosition2 },
            new int[] { endPosition1,   endPosition2 }
        );
    }

    @Test
    public final void test_InExpression_Valid_01() throws Exception {

        String jpqlQuery = "SELECT k FROM Project k WHERE TYPE(k) IN (LargeProject, SmallProject)";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasNoProblems(problems);
    }

    @Test
    public final void test_InvalidQuery_01() throws Exception {

        String jpqlQuery   = "select e from Employee e where e.empId in (select x.id from xx x)";

        int startPosition1 = "select e from Employee e where e.empId in(select ".length();
        int endPosition1   = "select e from Employee e where e.empId in(select x.id".length();

        int startPosition2 = "select e from Employee e where e.empId in(select x.id from ".length();
        int endPosition2   = "select e from Employee e where e.empId in(select x.id from xx".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblems(
            problems,
            new String[] { StateFieldPathExpression_NotResolvable, AbstractSchemaName_Invalid },
            new int[] { startPosition1, startPosition2 },
            new int[] { endPosition1,   endPosition2 }
        );
    }

    @Test
    public final void test_InvalidQuery_02() throws Exception {

        String jpqlQuery   = "select e from Employee e where e.empId in (select e.empId from emp e)";

        int startPosition1 = "select e from Employee e where e.empId in(select ".length();
        int endPosition1   = "select e from Employee e where e.empId in(select e.empId".length();

        int startPosition2 = "select e from Employee e where e.empId in(select e.empId from ".length();
        int endPosition2   = "select e from Employee e where e.empId in(select e.empId from emp".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblems(
            problems,
            new String[] { StateFieldPathExpression_NotResolvable, AbstractSchemaName_Invalid },
            new int[] { startPosition1, startPosition2 },
            new int[] { endPosition1,   endPosition2 }
        );
    }

    @Test
    public final void test_InvalidQuery_03() throws Exception {

        String jpqlQuery  = "SELECT i FROM Product i WHERE i.enumType=:category ORDER BY i.id\"";
        int startPosition = "SELECT i FROM Product i WHERE i.enumType = :category ORDER BY ".length();
        int endPosition   = "SELECT i FROM Product i WHERE i.enumType = :category ORDER BY i.id\"".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            StateFieldPathExpression_NotResolvable,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_StateFieldPathExpression_AssociationField_01() throws Exception {

        String jpqlQuery  = "SELECT MIN(e.managerEmployee) FROM Employee e";
        int startPosition = "SELECT MIN(".length();
        int endPosition   = "SELECT MIN(e.managerEmployee".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            StateFieldPathExpression_AssociationField,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_StateFieldPathExpression_AssociationField_02() throws Exception {

        String jpqlQuery = "SELECT e.managerEmployee FROM Employee e";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, StateFieldPathExpression_AssociationField);
    }

    @Test
    public final void test_StateFieldPathExpression_AssociationField_03() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE UPPER(e.address) = 'JPQL'";
        int startPosition = "SELECT e FROM Employee e WHERE UPPER(".length();
        int endPosition   = "SELECT e FROM Employee e WHERE UPPER(e.address".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            StateFieldPathExpression_AssociationField,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_StateFieldPathExpression_AssociationField_04() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE LOWER(e.address) = 'JPQL'";
        int startPosition = "SELECT e FROM Employee e WHERE LOWER(".length();
        int endPosition   = "SELECT e FROM Employee e WHERE LOWER(e.address".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            StateFieldPathExpression_AssociationField,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_StateFieldPathExpression_AssociationField_05() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE LENGTH(e.address) = 'JPQL'";
        int startPosition = "SELECT e FROM Employee e WHERE LENGTH(".length();
        int endPosition   = "SELECT e FROM Employee e WHERE LENGTH(e.address".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            StateFieldPathExpression_AssociationField,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_StateFieldPathExpression_AssociationField_06() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE e.address + 2";
        int startPosition = "SELECT e FROM Employee e WHERE ".length();
        int endPosition   = "SELECT e FROM Employee e WHERE e.address".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            StateFieldPathExpression_AssociationField,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_StateFieldPathExpression_AssociationField_07() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE MOD(e.address, 2) > 2";
        int startPosition = "SELECT e FROM Employee e WHERE MOD(".length();
        int endPosition   = "SELECT e FROM Employee e WHERE MOD(e.address".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            StateFieldPathExpression_AssociationField,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_StateFieldPathExpression_AssociationField_08() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE TRIM(e.address, 'JPQL') = 'JPQL'";
        int startPosition = "SELECT e FROM Employee e WHERE TRIM(".length();
        int endPosition   = "SELECT e FROM Employee e WHERE TRIM(e.address".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            StateFieldPathExpression_AssociationField,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_StateFieldPathExpression_AssociationField_09() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE TRIM(BOTH '_' FROM e.address) = 'JPQL'";
        int startPosition = "SELECT e FROM Employee e WHERE TRIM(BOTH '_' FROM ".length();
        int endPosition   = "SELECT e FROM Employee e WHERE TRIM(BOTH '_' FROM e.address".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            StateFieldPathExpression_AssociationField,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_StateFieldPathExpression_AssociationField_10() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE TRIM(e.department, 'JPQL') = 'JPQL'";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, StateFieldPathExpression_AssociationField);
    }

    @Test
    public final void test_StateFieldPathExpression_AssociationField_11() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE LENGTH(e.department) = 2";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, StateFieldPathExpression_AssociationField);
    }

    @Test
    public final void test_StateFieldPathExpression_AssociationField_12() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE LOWER(e.department) = 2";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, StateFieldPathExpression_AssociationField);
    }

    @Test
    public final void test_StateFieldPathExpression_AssociationField_13() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE LOWER(UPPER(e.address)) = 2";
        int startPosition = "SELECT e FROM Employee e WHERE LOWER(UPPER(".length();
        int endPosition   = "SELECT e FROM Employee e WHERE LOWER(UPPER(e.address".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            StateFieldPathExpression_AssociationField,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_StateFieldPathExpression_AssociationField_14() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE e.name > 2";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, StateFieldPathExpression_AssociationField);
    }

    @Test
    public final void test_StateFieldPathExpression_AssociationField_19() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE e <> e";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasNoProblems(problems);
    }

    @Test
    public final void test_StateFieldPathExpression_AssociationField_20() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE e = e";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasNoProblems(problems);
    }

    @Test
    @Ignore
    public final void test_StateFieldPathExpression_BasicField_01() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE e.name IS NOT NULL";
        int startPosition = "SELECT e FROM Employee e WHERE ".length();
        int endPosition   = "SELECT e FROM Employee e WHERE e.name".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            StateFieldPathExpression_BasicField,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_StateFieldPathExpression_BasicField_02() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE e.address IS NOT NULL";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, StateFieldPathExpression_BasicField);
    }

    @Test
    public final void test_StateFieldPathExpression_BasicField_03() throws Exception {

        String jpqlQuery = "SELECT e.name FROM Employee e ORDER BY e.name";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, StateFieldPathExpression_BasicField);
    }

    @Test
    public final void test_StateFieldPathExpression_BasicField_04() throws Exception {

        String jpqlQuery = "SELECT e.address FROM Employee e JOIN e.address";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, StateFieldPathExpression_BasicField);
    }

    @Test
    public final void test_StateFieldPathExpression_BasicField_06() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE e.managerEmployee IS NOT NULL";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, StateFieldPathExpression_BasicField);
    }

    @Test
    public final void test_StateFieldPathExpression_BasicField_07() throws Exception {

        String jpqlQuery = "SELECT p FROM Product p WHERE TYPE(p) IN (SmallProject, LargeProject)";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasNoProblems(problems);
    }

    @Test
    public final void test_StateFieldPathExpression_BasicField_08() throws Exception {

        String jpqlQuery = "SELECT p FROM Product p WHERE TYPE(p.id) IN (SmallProject, LargeProject)";
        int startPosition = "SELECT p FROM Product p WHERE TYPE(".length();
        int endPosition   = "SELECT p FROM Product p WHERE TYPE(p.id".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasOnlyOneProblem(problems, StateFieldPathExpression_BasicField, startPosition, endPosition);
    }

    @Test
    public final void test_StateFieldPathExpression_BasicField_09() throws Exception {

        String jpqlQuery = "SELECT p FROM Product p WHERE TYPE(p.project) IN (SmallProject, LargeProject)";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasNoProblems(problems);
    }

    @Test
    public final void test_StateFieldPathExpression_BasicField_10() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE TYPE(e.embeddedAddress) IN (Address)";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasNoProblems(problems);
    }

    @Test
    public final void test_StateFieldPathExpression_CollectionType_1() throws Exception {

        String jpqlQuery = "SELECT a.customerList FROM Address a";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        if (isPathExpressionToCollectionMappingAllowed()) {
            testHasNoProblems(problems);
        }
        else {
            int startPosition = "SELECT ".length();
            int endPosition   = "SELECT a.customerList".length();

            testHasProblem(
                problems,
                StateFieldPathExpression_CollectionType,
                startPosition,
                endPosition
            );
        }
    }

    @Test
    public final void test_StateFieldPathExpression_CollectionType_2() throws Exception {

        String jpqlQuery = "SELECT COUNT(a.customerList) FROM Address a";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        if (isPathExpressionToCollectionMappingAllowed()) {
            testHasNoProblems(problems);
        }
        else {
            int startPosition = "SELECT COUNT(".length();
            int endPosition   = "SELECT COUNT(a.customerList".length();

            testHasProblem(
                problems,
                StateFieldPathExpression_CollectionType,
                startPosition,
                endPosition
            );
        }
    }

    @Test
    public final void test_StateFieldPathExpression_CollectionType_3() throws Exception {

        String jpqlQuery = "SELECT c FROM Customer c, Address a WHERE c NOT IN (a.customerList)";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        if (isPathExpressionToCollectionMappingAllowed()) {
            testHasNoProblems(problems);
        }
        else {
            int startPosition = "SELECT c FROM Customer c, Address a WHERE c NOT IN(".length();
            int endPosition   = "SELECT c FROM Customer c, Address a WHERE c NOT IN(a.customerList".length();

            testHasProblem(
                problems,
                StateFieldPathExpression_CollectionType,
                startPosition,
                endPosition
            );
        }
    }

    @Test
    public final void test_StateFieldPathExpression_InvalidEnumConstant_1() throws Exception {

        String jpqlQuery = "SELECT p FROM Product p WHERE p.enumType = jpql.jpqlQuery.EnumType.FIRST_NAME";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, StateFieldPathExpression_InvalidEnumConstant);
    }

    @Test
    public final void test_StateFieldPathExpression_InvalidEnumConstant_2() throws Exception {

        String jpqlQuery  = "SELECT p FROM Product p WHERE p.enumType = jpql.query.EnumType.INVALID";
        int startPosition = "SELECT p FROM Product p WHERE p.enumType = jpql.query.EnumType.".length();
        int endPosition   = jpqlQuery.length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasOnlyOneProblem(
            problems,
            StateFieldPathExpression_InvalidEnumConstant,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_StateFieldPathExpression_NoMapping_1() throws Exception {

        String jpqlQuery  = "SELECT c.title FROM Customer c";
        int startPosition = "SELECT ".length();
        int endPosition   = "SELECT c.title".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            StateFieldPathExpression_NoMapping,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_StateFieldPathExpression_NoMapping_2() throws Exception {

        String jpqlQuery = "SELECT c.lastName FROM Customer c";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, StateFieldPathExpression_NoMapping);
    }

    @Test
    public final void test_StateFieldPathExpression_NotResolvable_1() throws Exception {

        String jpqlQuery  = "SELECT e.name.wrong FROM Employee e";
        int startPosition = "SELECT ".length();
        int endPosition   = "SELECT e.name.wrong".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            StateFieldPathExpression_NotResolvable,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_StateFieldPathExpression_NotResolvable_2() throws Exception {

        String jpqlQuery = "UPDATE Employee WHERE ALL(SELECT e FROM managerEmployee e)";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, StateFieldPathExpression_NotResolvable);
    }

    @Test
    public final void test_StateFieldPathExpression_NotResolvable_3() throws Exception {

        String jpqlQuery  = "UPDATE Employee WHERE ALL(SELECT e FROM phone e)";
        int startPosition = "UPDATE Employee WHERE ALL(SELECT e FROM ".length();
        int endPosition   = "UPDATE Employee WHERE ALL(SELECT e FROM phone".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            StateFieldPathExpression_NotResolvable,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_UpdateItem_RelationshipPathExpression_1() throws Exception {

        String jpqlQuery = "UPDATE Employee e SET e.name = 'JPQL'";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, UpdateItem_RelationshipPathExpression);
    }

    @Test
    public final void test_UpdateItem_RelationshipPathExpression_2() throws Exception {

        String jpqlQuery = "UPDATE Employee e SET e.managerEmployee = NULL";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, UpdateItem_RelationshipPathExpression);
    }

    @Test
    public final void test_UpdateItem_RelationshipPathExpression_3() throws Exception {

        String jpqlQuery  = "UPDATE Employee e SET e.phoneNumbers.area = NULL";
        int startPosition = "UPDATE Employee e SET ".length();
        int endPosition   = "UPDATE Employee e SET e.phoneNumbers.area".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            UpdateItem_RelationshipPathExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_UpdateItem_RelationshipPathExpression_4() throws Exception {

        String jpqlQuery  = "UPDATE Employee SET phoneNumbers.area = NULL";
        int startPosition = "UPDATE Employee SET ".length();
        int endPosition   = "UPDATE Employee SET phoneNumbers.area".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            UpdateItem_RelationshipPathExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_UpdateItem_RelationshipPathExpression_5() throws Exception {

        String jpqlQuery  = "UPDATE Employee e SET e.department.invalid = TRUE";
        int startPosition = "UPDATE Employee e SET ".length();
        int endPosition   = "UPDATE Employee e SET e.department.invalid".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            UpdateItem_RelationshipPathExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_UpdateItem_RelationshipPathExpression_6() throws Exception {

        String jpqlQuery  = "UPDATE Employee SET department.invalid = TRUE";
        int startPosition = "UPDATE Employee SET ".length();
        int endPosition   = "UPDATE Employee SET department.invalid".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            UpdateItem_RelationshipPathExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_UpdateItem_RelationshipPathExpression_7() throws Exception {

        String jpqlQuery = "UPDATE Employee e SET e.embeddedAddress.city = 'Cary'";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, UpdateItem_RelationshipPathExpression);
    }

    @Test
    public final void test_UpdateItem_RelationshipPathExpression_8() throws Exception {

        String jpqlQuery = "UPDATE Employee SET embeddedAddress.city = 'Cary'";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, UpdateItem_RelationshipPathExpression);
    }

    @Test
    public final void test_ValidQuery_01() throws Exception {

        String jpqlQuery = "select d from Employee d where UPPER(d.name) IN (:keywords) AND UPPER(d.manager) IN (:keywords) order by d.name,d.manager";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasNoProblems(problems);
    }

    @Test
    public final void test_ValidQuery_02() throws Exception {

        String jpqlQuery = "SELECT p FROM Product p WHERE TYPE(p.project) <> SmallProject";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasNoProblems(problems);
    }

    @Test
    public final void test_ValidQuery_03() throws Exception {

        String jpqlQuery = "SELECT i FROM Customer i WHERE i.home=:category ORDER BY i.id";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasNoProblems(problems);
    }

    @Test
    public final void test_ValidQuery_04() throws Exception {

        String jpqlQuery = "select p from Alias p join p.ids m where key(m)=:language and value(m)=:name";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasNoProblems(problems);
    }

    @Test
    public final void test_ValidQuery_05() throws Exception {

        String jpqlQuery = "SELECT r FROM Employee r OUTER JOIN r.phoneNumbers c WHERE c.phoneNumber = :major AND c.area = :name AND r.working = true";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasNoProblems(problems);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<JPQLQueryProblem> validate(String jpqlQuery, JPQLExpression jpqlExpression) throws Exception {

        // Update JPQLQueryContext, set JPQLExpression first since it was already parsed
        queryContext.setJPQLExpression(jpqlExpression);
        queryContext.setQuery(buildQuery(jpqlQuery));

        // Now validate semantically the JPQL jpqlQuery
        return super.validate(jpqlQuery, jpqlExpression);
    }
}
