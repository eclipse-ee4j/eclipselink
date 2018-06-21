/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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
import org.eclipse.persistence.jpa.jpql.tools.DefaultJPQLQueryContext;
import org.eclipse.persistence.jpa.jpql.tools.DefaultSemanticValidator;
import org.eclipse.persistence.jpa.jpql.tools.JPQLQueryContext;
import org.junit.Ignore;
import org.junit.Test;
import static org.eclipse.persistence.jpa.jpql.JPQLQueryProblemMessages.*;

/**
 * The unit-test class used for testing a JPQL jpqlQuery semantically when the JPA version is 1.0 and 2.0.
 *
 * @version 2.5.1
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class DefaultSemanticValidatorTest2_0 extends AbstractSemanticValidatorTest {

    /**
     * {@inheritDoc}
     */
    @Override
    protected JPQLQueryContext buildQueryContext() {
        return new DefaultJPQLQueryContext(jpqlGrammar);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected AbstractSemanticValidator buildValidator() {
        return new DefaultSemanticValidator(buildSemanticValidatorHelper());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isComparisonTypeChecked() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isPathExpressionToCollectionMappingAllowed() {
        return false;
    }

    @Test
    public final void test_AbsExpression_InvalidNumericExpression_1() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE ABS(e.name)";
        int startPosition = "SELECT e FROM Employee e WHERE ABS(".length();
        int endPosition   = "SELECT e FROM Employee e WHERE ABS(e.name".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            AbsExpression_InvalidNumericExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_AbsExpression_InvalidNumericExpression_2() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE ABS(e.empId)";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, AbsExpression_InvalidNumericExpression);
    }

    @Test
    public final void test_AdditionExpression_LeftExpression_WrongType_1() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE e.name + e.empId";
        int startPosition = "SELECT e FROM Employee e WHERE ".length();
        int endPosition   = "SELECT e FROM Employee e WHERE e.name".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            AdditionExpression_LeftExpression_WrongType,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_AdditionExpression_LeftExpression_WrongType_2() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE SQRT(e.empId) + e.empId";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, AdditionExpression_LeftExpression_WrongType);
    }

    @Test
    public final void test_AdditionExpression_RightExpression_WrongType_1() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE e.empId + e.name";
        int startPosition = "SELECT e FROM Employee e WHERE e.empId + ".length();
        int endPosition   = "SELECT e FROM Employee e WHERE e.empId + e.name".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            AdditionExpression_RightExpression_WrongType,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_AdditionExpression_RightExpression_WrongType_2() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE e.empId + SQRT(e.empId)";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, AdditionExpression_RightExpression_WrongType);
    }

    @Test
    public final void test_AvgFunction_InvalidNumericExpression_1() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE AVG(e.name)";
        int startPosition = "SELECT e FROM Employee e WHERE AVG(".length();
        int endPosition   = "SELECT e FROM Employee e WHERE AVG(e.name".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            AvgFunction_InvalidNumericExpression,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_AvgFunction_InvalidNumericExpression_2() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE AVG(e.empId)";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, AvgFunction_InvalidNumericExpression);
    }

    @Test
    public final void test_BetweenExpression_WrongType_1() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE e.name BETWEEN 'A' AND 2";
        int startPosition = "SELECT e FROM Employee e WHERE ".length();
        int endPosition   = "SELECT e FROM Employee e WHERE e.name BETWEEN 'A' AND 2".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            BetweenExpression_WrongType,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_BetweenExpression_WrongType_2() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE e.empId BETWEEN 'A' AND 2";
        int startPosition = "SELECT e FROM Employee e WHERE ".length();
        int endPosition   = "SELECT e FROM Employee e WHERE e.empId BETWEEN 'A' AND 2".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            BetweenExpression_WrongType,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_BetweenExpression_WrongType_3() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE e.empId BETWEEN 'A' AND 'Z'";
        int startPosition = "SELECT e FROM Employee e WHERE ".length();
        int endPosition   = "SELECT e FROM Employee e WHERE e.empId BETWEEN 'A' AND 'Z'".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            BetweenExpression_WrongType,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_BetweenExpression_WrongType_4() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE e.name BETWEEN 'A' AND :name";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, BetweenExpression_WrongType);
    }

    @Test
    public final void test_CollectionMemberExpression_Embeddable_1() throws Exception {

        String jpqlQuery = "SELECT a FROM Address a, Customer c WHERE c MEMBER OF a.customerList";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, CollectionMemberExpression_Embeddable);
    }

    @Test
    public final void test_CollectionMemberExpression_Embeddable_2() throws Exception {

        String jpqlQuery  = "SELECT a FROM Address a, Customer c WHERE a.zip MEMBER OF a.customerList";
        int startPosition = "SELECT a FROM Address a, Customer c WHERE ".length();
        int endPosition   = "SELECT a FROM Address a, Customer c WHERE a.zip".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            CollectionMemberExpression_Embeddable,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_ComparisonExpression_WrongComparisonType_1() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE NULLIF(e.working, TRUE) < LENGTH(e.name) + 2";
        int startPosition = "SELECT e FROM Employee e WHERE ".length();
        int endPosition   = "SELECT e FROM Employee e WHERE NULLIF(e.working, TRUE) < LENGTH(e.name) + 2".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            ComparisonExpression_WrongComparisonType,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_ComparisonExpression_WrongComparisonType_2() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE COALESCE(e.working, :name) < LENGTH(e.name)";
        int startPosition = "SELECT e FROM Employee e WHERE ".length();
        int endPosition   = "SELECT e FROM Employee e WHERE COALESCE(e.working, :name) < LENGTH(e.name)".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            ComparisonExpression_WrongComparisonType,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_ComparisonExpression_WrongComparisonType_3() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE ALL(SELECT p.id FROM Project p) < e.salary";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, ComparisonExpression_WrongComparisonType);
    }

    @Test
    public final void test_ComparisonExpression_WrongComparisonType_4() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE ALL(SELECT p.id FROM Project p) < e.name";
        int startPosition = "SELECT e FROM Employee e WHERE ".length();
        int endPosition   = "SELECT e FROM Employee e WHERE ALL(SELECT p.id FROM Project p) < e.name".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            ComparisonExpression_WrongComparisonType,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_ComparisonExpression_WrongComparisonType_5() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE ALL(SELECT p.id FROM Project p) < (e.name)";
        int startPosition = "SELECT e FROM Employee e WHERE ".length();
        int endPosition   = "SELECT e FROM Employee e WHERE ALL(SELECT p.id FROM Project p) < (e.name)".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            ComparisonExpression_WrongComparisonType,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_ComparisonExpression_WrongComparisonType_6() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE ALL(SELECT p.id FROM Project p) < (e.salary)";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, ComparisonExpression_WrongComparisonType);
    }

    @Test
    public final void test_ComparisonExpression_WrongComparisonType_7() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE ALL < e.name";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, ComparisonExpression_WrongComparisonType);
    }

    @Test
    public final void test_ConcatExpression_Expression_WrongType_1() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE CONCAT('JPQL', 'jpqlQuery') = 'JPQL jpqlQuery'";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, ConcatExpression_Expression_WrongType);
    }

    @Test
    public final void test_ConcatExpression_Expression_WrongType_2() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE CONCAT(e.name, 'jpqlQuery') = 'JPQL jpqlQuery'";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, ConcatExpression_Expression_WrongType);
    }

    @Test
    public final void test_ConcatExpression_Expression_WrongType_3() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE CONCAT(e.empId, 'jpqlQuery') = 'JPQL jpqlQuery'";
        int startPosition = "SELECT e FROM Employee e WHERE CONCAT(".length();
        int endPosition   = "SELECT e FROM Employee e WHERE CONCAT(e.empId".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            ConcatExpression_Expression_WrongType,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_ConcatExpression_Expression_WrongType_4() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE CONCAT('JPQL', e.name) = 'JPQL jpqlQuery'";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, ConcatExpression_Expression_WrongType);
    }

    @Test
    public final void test_ConcatExpression_Expression_WrongType_5() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE CONCAT('JPQL', e.empId) = 'JPQL jpqlQuery'";
        int startPosition = "SELECT e FROM Employee e WHERE CONCAT('JPQL', ".length();
        int endPosition   = "SELECT e FROM Employee e WHERE CONCAT('JPQL', e.empId".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            ConcatExpression_Expression_WrongType,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_ConstructorExpression_UndefinedConstructor_1() throws Exception {

        String jpqlQuery  = "SELECT NEW java.lang.StringBuilder(e.dept) FROM Employee e";
        int startPosition = "SELECT NEW ".length();
        int endPosition   = "SELECT NEW java.lang.StringBuilder".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            ConstructorExpression_UndefinedConstructor,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_ConstructorExpression_UndefinedConstructor_2() throws Exception {

        String jpqlQuery = "SELECT NEW java.lang.StringBuilder(e.name) FROM Employee e";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, ConstructorExpression_UndefinedConstructor);
    }

    @Test
    public final void test_ConstructorExpression_UndefinedConstructor_3() throws Exception {

        String jpqlQuery  = "SELECT NEW java.lang.ProcessBuilder(e.name, e.name, e.name) FROM Employee e";
        int startPosition = "SELECT NEW ".length();
        int endPosition   = "SELECT NEW java.lang.ProcessBuilder".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        // Note: Unless there is a way to discover vararg, we'll assume it's not possible to match
        testHasProblem(
            problems,
            ConstructorExpression_UndefinedConstructor,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_ConstructorExpression_UnknownType_1() throws Exception {

        String jpqlQuery  = "SELECT NEW Address(e.name) FROM Employee e";
        int startPosition = "SELECT NEW ".length();
        int endPosition   = "SELECT NEW Address".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            ConstructorExpression_UnknownType,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_ConstructorExpression_UnknownType_2() throws Exception {

        String jpqlQuery  = "SELECT NEW String(e.name) FROM Employee e";
        int startPosition = "SELECT NEW ".length();
        int endPosition   = "SELECT NEW String".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            ConstructorExpression_UnknownType,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_ConstructorExpression_UnknownType_3() throws Exception {

        String jpqlQuery = "SELECT NEW java.lang.StringBuilder(e.empId) FROM Employee e";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, ConstructorExpression_UnknownType);
    }

    @Test
    public final void test_CountFunction_DistinctEmbeddable() throws Exception {

        String jpqlQuery  = "SELECT COUNT(DISTINCT a.zip) FROM Address a";
        int startPosition = "SELECT COUNT(".length();
        int endPosition   = "SELECT COUNT(DISTINCT a.zip".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            CountFunction_DistinctEmbeddable,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_DivisionExpression_LeftExpression_WrongType_1() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE e.name / e.empId";
        int startPosition = "SELECT e FROM Employee e WHERE ".length();
        int endPosition   = "SELECT e FROM Employee e WHERE e.name".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            DivisionExpression_LeftExpression_WrongType,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_DivisionExpression_LeftExpression_WrongType_2() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE SQRT(e.empId) / e.empId";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, DivisionExpression_LeftExpression_WrongType);
    }

    @Test
    public final void test_DivisionExpression_RightExpression_WrongType_1() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE e.empId / e.name";
        int startPosition = "SELECT e FROM Employee e WHERE e.empId / ".length();
        int endPosition   = "SELECT e FROM Employee e WHERE e.empId / e.name".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            DivisionExpression_RightExpression_WrongType,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_DivisionExpression_RightExpression_WrongType_2() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE e.empId / SQRT(e.empId)";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, DivisionExpression_RightExpression_WrongType);
    }

    @Test
    public final void test_EncapsulatedIdentificationVariableExpression_NotMapValued_1() throws Exception {

        String jpqlQuery = "SELECT ENTRY(add) FROM Alias a JOIN a.addresses add";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, EncapsulatedIdentificationVariableExpression_NotMapValued);
    }

    @Test
    public final void test_EncapsulatedIdentificationVariableExpression_NotMapValued_2() throws Exception {

        String jpqlQuery  = "SELECT ENTRY(c) FROM Address a JOIN a.customerList c";
        int startPosition = "SELECT ENTRY(".length();
        int endPosition   = "SELECT ENTRY(c".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            EncapsulatedIdentificationVariableExpression_NotMapValued,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_IdentificationVariable_EntityName() throws Exception {

        String jpqlQuery   = "SELECT Employee FROM Address Employee";

        int startPosition1 = "SELECT ".length();
        int endPosition1   = "SELECT Employee".length();

        int startPosition2 = "SELECT Employee FROM Address ".length();
        int endPosition2   = "SELECT Employee FROM Address Employee".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblems(
            problems,
            new String[] { IdentificationVariable_EntityName, IdentificationVariable_EntityName },
            new int[]    { startPosition1,                    startPosition2 },
            new int[]    { endPosition1,                      endPosition2 }
        );
    }

    @Test
    public final void test_IdentificationVariableDeclaration_JoinsEndWithComma_2() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e JOIN e.managerEmployee m";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, IdentificationVariableDeclaration_JoinsEndWithComma);
    }

    @Test
    @Ignore("The parser does not support parsing multiple joins separated by commas")
    public final void test_IdentificationVariableDeclaration_JoinsEndWithComma_3() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e JOIN e.managerEmployee m,";
        int startPosition = "SELECT e FROM Employee e JOIN e.managerEmployee m".length();
        int endPosition   = jpqlQuery.length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            IdentificationVariableDeclaration_JoinsEndWithComma,
            startPosition,
            endPosition
        );
    }

    @Test
    @Ignore("The parser does not support parsing multiple joins separated by commas")
    public final void test_IdentificationVariableDeclaration_JoinsEndWithComma_4() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e JOIN e.managerEmployee p JOIN e.managerEmployee m,";
        int startPosition = "SELECT e FROM Employee e JOIN e.managerEmployee p JOIN e.managerEmployee m".length();
        int endPosition   = jpqlQuery.length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            IdentificationVariableDeclaration_JoinsEndWithComma,
            startPosition,
            endPosition
        );
    }

    @Test
    @Ignore("The parser does not support parsing multiple joins separated by commas")
    public final void test_IdentificationVariableDeclaration_JoinsHaveComma_1() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e JOIN e.managerEmployee p, JOIN e.managerEmployee m";
        int startPosition = "SELECT e FROM Employee e JOIN e.managerEmployee p".length();
        int endPosition   = "SELECT e FROM Employee e JOIN e.managerEmployee p,".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            IdentificationVariableDeclaration_JoinsHaveComma,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_IdentificationVariableDeclaration_JoinsHaveComma_2() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e JOIN e.managerEmployee p JOIN e.managerEmployee m";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, IdentificationVariableDeclaration_JoinsHaveComma);
    }

    @Test
    public final void test_InExpression_InItem_WrongType_1() throws Exception {
//        String jpqlQuery = "SELECT e FROM Employee e WHERE e.name IN(e, ‘JPQL’)
//        List<QueryProblem> problems = validate(jpqlQuery);
//
//        int startPosition = "SELECT e FROM Employee e WHERE e.name IN(".length();
//        int endPosition   = "SELECT e FROM Employee e WHERE e.name IN(e, ‘JPQL’".length();
//
//        testHasProblem(
//            problems,
//            QueryProblemMessages.InExpression_InItem_WrongType,
//            startPosition,
//            endPosition
//        );
    }

    @Test
    public final void test_InExpression_InItem_WrongType_2() throws Exception {
//        String jpqlQuery = "SELECT e FROM Employee e WHERE e.name IN(e.name, :name, ‘JPQL’)
//        List<QueryProblem> problems = validate(jpqlQuery);
//
//        testDoesNotHaveProblem(
//            problems,
//            QueryProblemMessages.InExpression_InItem_WrongType
//        );
    }

    @Test
    public final void test_InExpression_WrongType_1() throws Exception {
//        String jpqlQuery = "SELECT e FROM Employee e WHERE e.name IN(e.empId)
//        List<QueryProblem> problems = validate(jpqlQuery);
//
//        int startPosition = "SELECT e FROM Employee e WHERE ".length();
//        int endPosition   = "SELECT e FROM Employee e WHERE e.name IN(e.empId)".length();
//
//        testHasProblem(
//            problems,
//            QueryProblemMessages.InExpression_WrongType,
//            startPosition,
//            endPosition
//        );
    }

    @Test
    public final void test_InExpression_WrongType_2() throws Exception {
//        String jpqlQuery = "SELECT e FROM Employee e WHERE e.name IN('JPQL', 'Java')
//        List<QueryProblem> problems = validate(jpqlQuery);
//
//        testDoesNotHaveProblem(
//            problems,
//            QueryProblemMessages.InExpression_WrongType
//        );
    }

    @Test
    public final void test_InExpression_WrongType_3() throws Exception {
//        //SELECT e FROM Employee e WHERE e.name IN(?1, ?2, ?3)
//        List<QueryProblem> problems = validate(jpqlQuery);
//
//        testDoesNotHaveProblem(
//            problems,
//            QueryProblemMessages.InExpression_WrongType
//        );
    }

    @Test
    public final void test_LengthExpression_WrongType_1() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE LENGTH(e.empId) = 2";
        int startPosition = "SELECT e FROM Employee e WHERE LENGTH(".length();
        int endPosition   = "SELECT e FROM Employee e WHERE LENGTH(e.empId".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            LengthExpression_WrongType,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_LengthExpression_WrongType_2() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE LENGTH(e.name) = 2";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, LengthExpression_WrongType);
    }

    @Test
    public final void test_LocateExpression_FirstExpression_WrongType_1() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE LOCATE(e.empId, e.name) = 0";
        int startPosition = "SELECT e FROM Employee e WHERE LOCATE(".length();
        int endPosition   = "SELECT e FROM Employee e WHERE LOCATE(e.empId".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            LocateExpression_FirstExpression_WrongType,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_LocateExpression_FirstExpression_WrongType_2() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE LOCATE(e.name, e.name) = 0";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, LocateExpression_FirstExpression_WrongType);
    }

    @Test
    public final void test_LocateExpression_FirstExpression_WrongType_3() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE LOCATE(, e.name) = 0";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, LocateExpression_FirstExpression_WrongType);
    }

    @Test
    public final void test_LocateExpression_SecondExpression_WrongType_1() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE LOCATE(e.name, e.empId) = 0";
        int startPosition = "SELECT e FROM Employee e WHERE LOCATE(e.name, ".length();
        int endPosition   = "SELECT e FROM Employee e WHERE LOCATE(e.name, e.empId".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            LocateExpression_SecondExpression_WrongType,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_LocateExpression_SecondExpression_WrongType_2() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE LOCATE(e.name, e.name) = 0";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, LocateExpression_SecondExpression_WrongType);
    }

    @Test
    public final void test_LocateExpression_SecondExpression_WrongType_3() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE LOCATE(e.name,) = 0";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, LocateExpression_SecondExpression_WrongType);
    }

    @Test
    public final void test_LocateExpression_ThirdExpression_WrongType_1() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE LOCATE(e.name, e.name, e.name) = 0";
        int startPosition = "SELECT e FROM Employee e WHERE LOCATE(e.name, e.name, ".length();
        int endPosition   = "SELECT e FROM Employee e WHERE LOCATE(e.name, e.name, e.name".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            LocateExpression_ThirdExpression_WrongType,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_LocateExpression_ThirdExpression_WrongType_2() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE LOCATE(e.name, e.name, 2) = 0";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, LocateExpression_ThirdExpression_WrongType);
    }

    @Test
    public final void test_LowerExpression_WrongType_1() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE LOWER('JPQL') = 'jpql'";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, LowerExpression_WrongType);
    }

    @Test
    public final void test_LowerExpression_WrongType_2() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE LOWER(e.name) = 'jpql'";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, LowerExpression_WrongType);
    }

    @Test
    public final void test_LowerExpression_WrongType_3() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE LOWER(e.empId) = 'jpql'";
        int startPosition = "SELECT e FROM Employee e WHERE LOWER(".length();
        int endPosition   = "SELECT e FROM Employee e WHERE LOWER(e.empId".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            LowerExpression_WrongType,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_ModExpression_FirstExpression_WrongType_1() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE MOD(e.name, 3) = 1";
        int startPosition = "SELECT e FROM Employee e WHERE MOD(".length();
        int endPosition   = "SELECT e FROM Employee e WHERE MOD(e.name".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            ModExpression_FirstExpression_WrongType,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_ModExpression_FirstExpression_WrongType_2() throws Exception {

        String jpqlQuery = "SELECT d FROM Dept d WHERE MOD(d.floorNumber, 3) = 1";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, ModExpression_FirstExpression_WrongType);
    }

    @Test
    public final void test_ModExpression_FirstExpression_WrongType_3() throws Exception {

        String jpqlQuery = "SELECT d FROM Dept d, Product p WHERE MOD(p.quantity + d.floorNumber, 3) = 1";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, ModExpression_FirstExpression_WrongType);
    }

    @Test
    public final void test_ModExpression_SecondExpression_WrongType_1() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE MOD(3, e.name) = 1";
        int startPosition = "SELECT e FROM Employee e WHERE MOD(3, ".length();
        int endPosition   = "SELECT e FROM Employee e WHERE MOD(3, e.name".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            ModExpression_SecondExpression_WrongType,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_ModExpression_SecondExpression_WrongType_2() throws Exception {

        String jpqlQuery = "SELECT d FROM Dept d WHERE MOD(3, d.floorNumber) = 1";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, ModExpression_SecondExpression_WrongType);
    }

    @Test
    public final void test_ModExpression_SecondExpression_WrongType_3() throws Exception {

        String jpqlQuery = "SELECT d FROM Dept d, Product p WHERE MOD(3, p.quantity + d.floorNumber) = 1";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, ModExpression_SecondExpression_WrongType);
    }

    @Test
    public final void test_MultiplicationExpression_LeftExpression_WrongType_1() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE e.name * e.empId";
        int startPosition = "SELECT e FROM Employee e WHERE ".length();
        int endPosition   = "SELECT e FROM Employee e WHERE e.name".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            MultiplicationExpression_LeftExpression_WrongType,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_MultiplicationExpression_LeftExpression_WrongType_2() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE SQRT(e.empId) * e.empId";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, MultiplicationExpression_LeftExpression_WrongType);
    }

    @Test
    public final void test_MultiplicationExpression_RightExpression_WrongType_1() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE e.empId * e.name";
        int startPosition = "SELECT e FROM Employee e WHERE e.empId * ".length();
        int endPosition   = "SELECT e FROM Employee e WHERE e.empId * e.name".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            MultiplicationExpression_RightExpression_WrongType,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_MultiplicationExpression_RightExpression_WrongType_2() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE e.empId * SQRT(e.empId)";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, MultiplicationExpression_RightExpression_WrongType);
    }

    @Test
    public final void test_NotExpression_WrongType() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE NOT e.name";
        int startPosition = "SELECT e FROM Employee e WHERE NOT ".length();
        int endPosition   = "SELECT e FROM Employee e WHERE NOT e.name".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            NotExpression_WrongType,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_NullComparisonExpression_InvalidType_1() throws Exception {

        String jpqlQuery = "SELECT a FROM Address a WHERE a.customerList IS NOT NULL";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, NullComparisonExpression_InvalidType);
    }

    @Test
    public final void test_NullComparisonExpression_InvalidType_2() throws Exception {

        String jpqlQuery  = "SELECT a FROM Address a WHERE a.zip IS NOT NULL";
        int startPosition = "SELECT a FROM Address a WHERE ".length();
        int endPosition   = "SELECT a FROM Address a WHERE a.zip".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            NullComparisonExpression_InvalidType,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_PathExpression_NotRelationshipMapping_1() throws Exception {

        String jpqlQuery  = "UPDATE Employee WHERE ALL(SELECT e FROM name e)";
        int startPosition = "UPDATE Employee WHERE ALL(SELECT e FROM ".length();
        int endPosition   = "UPDATE Employee WHERE ALL(SELECT e FROM name".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            PathExpression_NotRelationshipMapping,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_PathExpression_NotRelationshipMapping_2() throws Exception {

        String jpqlQuery = "UPDATE Employee WHERE ALL(SELECT manager FROM managerEmployee manager)";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, PathExpression_NotRelationshipMapping);
    }

    @Test
    public final void test_PathExpression_NotRelationshipMapping_3() throws Exception {

        String jpqlQuery = "UPDATE Employee WHERE ALL(SELECT phone FROM phoneNumbers phone)";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, PathExpression_NotRelationshipMapping);
    }

    @Test
    public final void test_SqrtExpression_WrongType_1() throws Exception {

        String jpqlQuery  = "SELECT a FROM Address a WHERE SQRT(a.state) = 2";
        int startPosition = "SELECT a FROM Address a WHERE SQRT(".length();
        int endPosition   = "SELECT a FROM Address a WHERE SQRT(a.state".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            SqrtExpression_WrongType,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_SqrtExpression_WrongType_2() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE SQRT(e.salary) = 2";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, SqrtExpression_WrongType);
    }

    @Test
    public final void test_SqrtExpression_WrongType_3() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE SQRT(NULLIF(e.name, :name)) = 2";
        int startPosition = "SELECT e FROM Employee e WHERE SQRT(".length();
        int endPosition   = "SELECT e FROM Employee e WHERE SQRT(NULLIF(e.name, :name)".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            SqrtExpression_WrongType,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_SqrtExpression_WrongType_4() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE SQRT(NULLIF(e.salary, :salary)) = 2";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, SqrtExpression_WrongType);
    }

    @Test
    public final void test_SubstringExpression_FirstExpression_WrongType_1() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE SUBSTRING(e.empId, 1, 2) = 'P'";
        int startPosition = "SELECT e FROM Employee e WHERE SUBSTRING(".length();
        int endPosition   = "SELECT e FROM Employee e WHERE SUBSTRING(e.empId".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            SubstringExpression_FirstExpression_WrongType,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_SubstringExpression_FirstExpression_WrongType_2() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE SUBSTRING(e.name, 1, 2) = 'P'";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, SubstringExpression_FirstExpression_WrongType);
    }

    @Test
    public final void test_SubstringExpression_SecondExpression_WrongType_1() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE SUBSTRING(e.name, 2 + e.name, 2) = 'P'";
        int startPosition = "SELECT e FROM Employee e WHERE SUBSTRING(e.name, ".length();
        int endPosition   = "SELECT e FROM Employee e WHERE SUBSTRING(e.name, 2 + e.name".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            SubstringExpression_SecondExpression_WrongType,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_SubstringExpression_SecondExpression_WrongType_2() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE SUBSTRING(e.name, 1, 2) = 'P'";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, SubstringExpression_SecondExpression_WrongType);
    }

    @Test
    public final void test_SubstringExpression_ThirdExpression_WrongType_1() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE SUBSTRING(e.name, 2, 2 + e.name) = 'P'";
        int startPosition = "SELECT e FROM Employee e WHERE SUBSTRING(e.name, 2, ".length();
        int endPosition   = "SELECT e FROM Employee e WHERE SUBSTRING(e.name, 2, 2 + e.name".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            SubstringExpression_ThirdExpression_WrongType,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_SubstringExpression_ThirdExpression_WrongType_2() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE SUBSTRING(e.name, 1, 2) = 'P'";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, SubstringExpression_ThirdExpression_WrongType);
    }

    @Test
    public final void test_SubtractionExpression_LeftExpression_WrongType_1() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE e.name - e.empId";
        int startPosition = "SELECT e FROM Employee e WHERE ".length();
        int endPosition   = "SELECT e FROM Employee e WHERE e.name".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            SubtractionExpression_LeftExpression_WrongType,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_SubtractionExpression_LeftExpression_WrongType_2() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE SQRT(e.empId) - e.empId";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, SubtractionExpression_LeftExpression_WrongType);
    }

    @Test
    public final void test_SubtractionExpression_RightExpression_WrongType_1() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE e.empId - e.name";
        int startPosition = "SELECT e FROM Employee e WHERE e.empId - ".length();
        int endPosition   = "SELECT e FROM Employee e WHERE e.empId - e.name".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            SubtractionExpression_RightExpression_WrongType,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_SubtractionExpression_RightExpression_WrongType_2() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE e.empId - SQRT(e.empId)";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, SubtractionExpression_RightExpression_WrongType);
    }

    @Test
    public final void test_SumFunction_WrongType_1() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE SUM(e.name)";
        int startPosition = "SELECT e FROM Employee e WHERE SUM(".length();
        int endPosition   = "SELECT e FROM Employee e WHERE SUM(e.name".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            SumFunction_WrongType,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_SumFunction_WrongType_2() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE SUM(e.empId)";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, SumFunction_WrongType);
    }

    @Test
    public final void test_UpdateItem_NotAssignable_1() throws Exception {

        String jpqlQuery = "UPDATE Employee AS e SET e.name = 'JPQL'";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, UpdateItem_NotAssignable);
    }

    @Test
    public final void test_UpdateItem_NotAssignable_2() throws Exception {

        String jpqlQuery  = "UPDATE Employee AS e SET e.empId = SUBSTRING('JPQL')";
        int startPosition = "UPDATE Employee AS e SET ".length();
        int endPosition   = "UPDATE Employee AS e SET e.empId = SUBSTRING('JPQL')".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            UpdateItem_NotAssignable,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_UpdateItem_NotResolvable_1() throws Exception {

        String jpqlQuery = "UPDATE Employee e SET e.name = 'JPQL'";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, UpdateItem_NotResolvable);
    }

    @Test
    public final void test_UpdateItem_NotResolvable_2() throws Exception {

        String jpqlQuery = "UPDATE Employee AS e SET e.name = 'JPQL'";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, UpdateItem_NotResolvable);
    }

    @Test
    public final void test_UpdateItem_NotResolvable_3() throws Exception {

        String jpqlQuery = "UPDATE Employee SET name = 'JPQL'";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, UpdateItem_NotResolvable);
    }

    @Test
    public final void test_UpdateItem_NotResolvable_4() throws Exception {

        String jpqlQuery  = "UPDATE Employee e SET name = 'JPQL'";
        int startPosition = "UPDATE Employee e SET ".length();
        int endPosition   = "UPDATE Employee e SET name".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            UpdateItem_NotResolvable,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_UpdateItem_NotResolvable_5() throws Exception {

        String jpqlQuery  = "UPDATE Employee SET e.name = 'JPQL'";
        int startPosition = "UPDATE Employee SET ".length();
        int endPosition   = "UPDATE Employee SET e.name".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            UpdateItem_NotResolvable,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_UpdateItem_NotResolvable_6() throws Exception {

        String jpqlQuery  = "UPDATE Employee AS e SET e.customerList = 'JPQL'";
        int startPosition = "UPDATE Employee AS e SET ".length();
        int endPosition   = "UPDATE Employee AS e SET e.customerList".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            UpdateItem_NotResolvable,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_UpdateItem_NotResolvable_7() throws Exception {

        String jpqlQuery  = "UPDATE Employee e SET e.embeddedAddress.city = 'JPQL'";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testHasNoProblems(problems);
    }

    @Test
    public final void test_UpdateItem_NotResolvable_8() throws Exception {

        String jpqlQuery  = "UPDATE Employee e SET e.embeddedAddress.postalcode = 'JPQL'";
        int startPosition = "UPDATE Employee e SET ".length();
        int endPosition   = "UPDATE Employee e SET e.embeddedAddress.postalcode".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            UpdateItem_NotResolvable,
            startPosition,
            endPosition
        );
    }

    @Test
    public final void test_UpperExpression_WrongType_1() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE UPPER('jpql') = 'JPQL'";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, UpperExpression_WrongType);
    }

    @Test
    public final void test_UpperExpression_WrongType_2() throws Exception {

        String jpqlQuery = "SELECT e FROM Employee e WHERE UPPER(e.name) = 'JPQL'";
        List<JPQLQueryProblem> problems = validate(jpqlQuery);
        testDoesNotHaveProblem(problems, UpperExpression_WrongType);
    }

    @Test
    public final void test_UpperExpression_WrongType_3() throws Exception {

        String jpqlQuery  = "SELECT e FROM Employee e WHERE UPPER(e.empId) = 'JPQL'";
        int startPosition = "SELECT e FROM Employee e WHERE UPPER(".length();
        int endPosition   = "SELECT e FROM Employee e WHERE UPPER(e.empId".length();

        List<JPQLQueryProblem> problems = validate(jpqlQuery);

        testHasProblem(
            problems,
            UpperExpression_WrongType,
            startPosition,
            endPosition
        );
    }
}
