/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jpa.tests.jpql;

import java.util.LinkedList;
import java.util.List;
import org.eclipse.persistence.jpa.jpql.AbstractValidator;
import org.eclipse.persistence.jpa.jpql.EclipseLinkGrammarValidator;
import org.eclipse.persistence.jpa.jpql.JPQLQueryProblem;
import org.eclipse.persistence.jpa.jpql.parser.DefaultEclipseLinkJPQLGrammar;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar2_4;
import org.eclipse.persistence.jpa.jpql.parser.JPQLExpression;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar1_0;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar2_0;
import org.eclipse.persistence.jpa.jpql.parser.JPQLGrammar2_1;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.eclipse.persistence.jpa.jpql.JPQLQueryProblemMessages.*;

/**
 * This unit-test tests validating the JPQL query based on the JPA platform. Any expressions that
 * is defined in a more recent version than the one used for validation will list the expression
 * as not supported.
 *
 * @version 2.5
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
@RunWith(JPQLTestRunner.class)
public class ValidateJPQLVersionTest extends AbstractValidatorTest {

	@Override
	protected AbstractValidator buildValidator() {
		return null;
	}

	@Test
	public void test_CaseExpression_1() {

		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name = " +
		                   "CASE WHEN e.age < 12  THEN org.eclipse.persistence.Person.CHILD " +
		                   "     WHEN e.age < 18  THEN org.eclipse.persistence.TEENAGER " +
		                   "     WHEN e.age >= 65 THEN org.eclipse.persistence.SENIOR " +
		                   "     ELSE org.eclipse.persistence.ADULT " +
		                   "END";

 		int startPosition = "SELECT e FROM Employee e WHERE e.name = ".length();
 		int endPosition   = jpqlQuery.replaceAll("\\s+", " ") .length();

 		testHasOnlyOneProblem(
 			jpqlQuery,
 			JPQLGrammar2_0.instance(),
 			JPQLGrammar1_0.instance(),
 			CaseExpression_InvalidJPAVersion,
 			startPosition,
 			endPosition
 		);
	}

	@Test
	public void test_CaseExpression_2() {

		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name = " +
		                   "CASE WHEN e.age < 12  THEN org.eclipse.persistence.Person.CHILD " +
		                   "     WHEN e.age < 18  THEN org.eclipse.persistence.TEENAGER " +
		                   "     WHEN e.age >= 65 THEN org.eclipse.persistence.SENIOR " +
		                   "     ELSE org.eclipse.persistence.ADULT " +
		                   "END";

		int startPosition = "SELECT e FROM Employee e WHERE e.name = ".length();
		int endPosition   = jpqlQuery.replaceAll("\\s+", " ") .length();

 		testHasOnlyOneProblem(
 			jpqlQuery,
 			JPQLGrammar2_1.instance(),
 			JPQLGrammar1_0.instance(),
 			CaseExpression_InvalidJPAVersion,
 			startPosition,
 			endPosition
 		);
	}

	@Test
	public void test_CaseExpression_3() {

		String jpqlQuery = "SELECT e FROM Employee e WHERE e.name = " +
		                   "CASE WHEN e.age < 12  THEN org.eclipse.persistence.Person.CHILD " +
		                   "     WHEN e.age < 18  THEN org.eclipse.persistence.TEENAGER " +
		                   "     WHEN e.age >= 65 THEN org.eclipse.persistence.SENIOR " +
		                   "     ELSE org.eclipse.persistence.ADULT " +
		                   "END";

		int startPosition = "SELECT e FROM Employee e WHERE e.name = ".length();
		int endPosition   = jpqlQuery.replaceAll("\\s+", " ") .length();

 		testHasOnlyOneProblem(
 			jpqlQuery,
 			DefaultEclipseLinkJPQLGrammar.instance(),
 			JPQLGrammar1_0.instance(),
 			CaseExpression_InvalidJPAVersion,
 			startPosition,
 			endPosition
 		);
	}

	@Test
	public void test_CastExpression_1() {

		String jpqlQuery  = "Select e from Employee e where 2 = cast(e.firstName as char(3))";
 		int startPosition = "Select e from Employee e where 2 = ".length();
 		int endPosition   = jpqlQuery.length();

 		testHasOnlyOneProblem(
 			jpqlQuery,
 			DefaultEclipseLinkJPQLGrammar.instance(),
 			JPQLGrammar1_0.instance(),
 			CastExpression_InvalidJPAVersion,
 			startPosition,
 			endPosition
 		);
	}

	@Test
	public void test_CastExpression_2() {

		String jpqlQuery  = "Select e from Employee e where 2 = cast(e.firstName as char(3))";
 		int startPosition = "Select e from Employee e where 2 = ".length();
 		int endPosition   = jpqlQuery.length();

 		testHasOnlyOneProblem(
 			jpqlQuery,
 			DefaultEclipseLinkJPQLGrammar.instance(),
 			JPQLGrammar2_0.instance(),
 			CastExpression_InvalidJPAVersion,
 			startPosition,
 			endPosition
 		);
	}

	@Test
	public void test_CastExpression_3() {

		String jpqlQuery  = "Select e from Employee e where 2 = cast(e.firstName as char(3))";
 		int startPosition = "Select e from Employee e where 2 = ".length();
 		int endPosition   = jpqlQuery.length();

 		testHasOnlyOneProblem(
 			jpqlQuery,
 			DefaultEclipseLinkJPQLGrammar.instance(),
 			JPQLGrammar2_1.instance(),
 			CastExpression_InvalidJPAVersion,
 			startPosition,
 			endPosition
 		);
	}

	@Test
	public void test_CoalesceExpression_1() {

		String jpqlQuery  = "SELECT e FROM Employee e WHERE COALESCE(e.age, 20) > 20";
 		int startPosition = "SELECT e FROM Employee e WHERE ".length();
 		int endPosition   = "SELECT e FROM Employee e WHERE COALESCE(e.age, 20)".length();

 		testHasOnlyOneProblem(
 			jpqlQuery,
 			JPQLGrammar2_0.instance(),
 			JPQLGrammar1_0.instance(),
 			CoalesceExpression_InvalidJPAVersion,
 			startPosition,
 			endPosition
 		);
	}

	@Test
	public void test_CoalesceExpression_2() {

		String jpqlQuery  = "SELECT e FROM Employee e WHERE COALESCE(e.age, 20) > 20";
 		int startPosition = "SELECT e FROM Employee e WHERE ".length();
 		int endPosition   = "SELECT e FROM Employee e WHERE COALESCE(e.age, 20)".length();

 		testHasOnlyOneProblem(
 			jpqlQuery,
 			JPQLGrammar2_1.instance(),
 			JPQLGrammar1_0.instance(),
 			CoalesceExpression_InvalidJPAVersion,
 			startPosition,
 			endPosition
 		);
	}

	@Test
	public void test_CoalesceExpression_3() {

		String jpqlQuery  = "SELECT e FROM Employee e WHERE COALESCE(e.age, 20) > 20";
 		int startPosition = "SELECT e FROM Employee e WHERE ".length();
 		int endPosition   = "SELECT e FROM Employee e WHERE COALESCE(e.age, 20)".length();

 		testHasOnlyOneProblem(
 			jpqlQuery,
 			DefaultEclipseLinkJPQLGrammar.instance(),
 			JPQLGrammar1_0.instance(),
 			CoalesceExpression_InvalidJPAVersion,
 			startPosition,
 			endPosition
 		);
	}

	@Test
	public void test_EntityTypeLiteral_1() {

		String jpqlQuery  = "SELECT e FROM Employee e WHERE e.name IN (Exempt, 'JPQL')";
 		int startPosition = "SELECT e FROM Employee e WHERE e.name IN(".length();
 		int endPosition   = "SELECT e FROM Employee e WHERE e.name IN(Exempt".length();

 		testHasOnlyOneProblem(
 			jpqlQuery,
 			DefaultEclipseLinkJPQLGrammar.instance(),
 			JPQLGrammar1_0.instance(),
 			EntityTypeLiteral_InvalidJPAVersion,
 			startPosition,
 			endPosition
 		);
	}

	@Test
	public void test_EntityTypeLiteral_2() {

		String jpqlQuery  = "SELECT e FROM Employee e WHERE e.name IN (Exempt, 'JPQL')";
 		int startPosition = "SELECT e FROM Employee e WHERE e.name IN(".length();
 		int endPosition   = "SELECT e FROM Employee e WHERE e.name IN(Exempt".length();

 		testHasOnlyOneProblem(
 			jpqlQuery,
 			JPQLGrammar2_1.instance(),
 			JPQLGrammar1_0.instance(),
 			EntityTypeLiteral_InvalidJPAVersion,
 			startPosition,
 			endPosition
 		);
	}

	@Test
	public void test_EntityTypeLiteral_3() {

		String jpqlQuery  = "SELECT e FROM Employee e WHERE e.name IN (Exempt, 'JPQL')";
 		int startPosition = "SELECT e FROM Employee e WHERE e.name IN(".length();
 		int endPosition   = "SELECT e FROM Employee e WHERE e.name IN(Exempt".length();

 		testHasOnlyOneProblem(
 			jpqlQuery,
 			JPQLGrammar2_0.instance(),
 			JPQLGrammar1_0.instance(),
 			EntityTypeLiteral_InvalidJPAVersion,
 			startPosition,
 			endPosition
 		);
	}

	@Test
	public void test_ExtractExpression_1() {

		String jpqlQuery  = "Select e from Employee e where 2 = extract(year from e.period.startDate)";
 		int startPosition = "Select e from Employee e where 2 = ".length();
 		int endPosition   = jpqlQuery.length();

 		testHasOnlyOneProblem(
 			jpqlQuery,
 			DefaultEclipseLinkJPQLGrammar.instance(),
 			JPQLGrammar1_0.instance(),
 			ExtractExpression_InvalidJPAVersion,
 			startPosition,
 			endPosition
 		);
	}

	@Test
	public void test_ExtractExpression_2() {

		String jpqlQuery  = "Select e from Employee e where 2 = extract(year from e.period.startDate)";
 		int startPosition = "Select e from Employee e where 2 = ".length();
 		int endPosition   = jpqlQuery.length();

 		testHasOnlyOneProblem(
 			jpqlQuery,
 			DefaultEclipseLinkJPQLGrammar.instance(),
 			JPQLGrammar2_0.instance(),
 			ExtractExpression_InvalidJPAVersion,
 			startPosition,
 			endPosition
 		);
	}

	@Test
	public void test_ExtractExpression_3() {

		String jpqlQuery  = "Select e from Employee e where 2 = extract(year from e.period.startDate)";
 		int startPosition = "Select e from Employee e where 2 = ".length();
 		int endPosition   = jpqlQuery.length();

 		testHasOnlyOneProblem(
 			jpqlQuery,
 			DefaultEclipseLinkJPQLGrammar.instance(),
 			JPQLGrammar2_1.instance(),
 			ExtractExpression_InvalidJPAVersion,
 			startPosition,
 			endPosition
 		);
	}

	@Test
	public void test_FromSubquery_1() {

		String jpqlQuery  = "Select e.firstName, avg(e3.count) from Employee e, (Select count(e2), e2.firstName from Employee e2 group by e2.firstName) e3 where e.firstName = e3.firstName";
 		int startPosition = "Select e.firstName, avg(e3.count) from Employee e, ".length();
 		int endPosition   = "Select e.firstName, avg(e3.count) from Employee e, (Select count(e2), e2.firstName from Employee e2 group by e2.firstName)".length();

 		testHasOnlyOneProblem(
 			jpqlQuery,
 			DefaultEclipseLinkJPQLGrammar.instance(),
 			JPQLGrammar1_0.instance(),
 			RangeVariableDeclaration_InvalidRootObject,
 			startPosition,
 			endPosition
 		);
	}

	@Test
	public void test_FromSubquery_2() {

		String jpqlQuery  = "Select e.firstName, avg(e3.count) from Employee e, (Select count(e2), e2.firstName from Employee e2 group by e2.firstName) e3 where e.firstName = e3.firstName";
 		int startPosition = "Select e.firstName, avg(e3.count) from Employee e, ".length();
 		int endPosition   = "Select e.firstName, avg(e3.count) from Employee e, (Select count(e2), e2.firstName from Employee e2 group by e2.firstName)".length();

 		testHasOnlyOneProblem(
 			jpqlQuery,
 			DefaultEclipseLinkJPQLGrammar.instance(),
 			JPQLGrammar2_0.instance(),
 			RangeVariableDeclaration_InvalidRootObject,
 			startPosition,
 			endPosition
 		);
	}

	@Test
	public void test_FromSubquery_3() {

		String jpqlQuery  = "Select e.firstName, avg(e3.count) from Employee e, (Select count(e2), e2.firstName from Employee e2 group by e2.firstName) e3 where e.firstName = e3.firstName";
 		int startPosition = "Select e.firstName, avg(e3.count) from Employee e, ".length();
 		int endPosition   = "Select e.firstName, avg(e3.count) from Employee e, (Select count(e2), e2.firstName from Employee e2 group by e2.firstName)".length();

 		testHasOnlyOneProblem(
 			jpqlQuery,
 			DefaultEclipseLinkJPQLGrammar.instance(),
 			JPQLGrammar2_1.instance(),
 			RangeVariableDeclaration_InvalidRootObject,
 			startPosition,
 			endPosition
 		);
	}

	@Test
	public void test_FunctionExpression_1() {

		String jpqlQuery  = "SELECT e FROM Employee e where FUNC('SQL', ename) = 2";
 		int startPosition = "SELECT e FROM Employee e where ".length();
 		int endPosition   = "SELECT e FROM Employee e where FUNC('SQL', ename)".length();

 		testHasOnlyOneProblem(
 			jpqlQuery,
 			DefaultEclipseLinkJPQLGrammar.instance(),
 			JPQLGrammar1_0.instance(),
 			FunctionExpression_InvalidJPAVersion,
 			startPosition,
 			endPosition
 		);
	}

	@Test
	public void test_FunctionExpression_2() {

		String jpqlQuery  = "SELECT e FROM Employee e where FUNC('SQL', ename) = 2";
 		int startPosition = "SELECT e FROM Employee e where ".length();
 		int endPosition   = "SELECT e FROM Employee e where FUNC('SQL', ename)".length();

 		testHasOnlyOneProblem(
 			jpqlQuery,
 			DefaultEclipseLinkJPQLGrammar.instance(),
 			JPQLGrammar2_0.instance(),
 			FunctionExpression_InvalidJPAVersion,
 			startPosition,
 			endPosition
 		);
	}

	@Test
	public void test_FunctionExpression_3() {

		String jpqlQuery = "SELECT e FROM Employee e where FUNCTION('SQL', ename) = 2";

 		testHasNoProblems(
 			jpqlQuery,
 			DefaultEclipseLinkJPQLGrammar.instance(),
 			JPQLGrammar2_1.instance()
 		);
	}

	@Test
	public void test_FunctionExpression_4() {

		String jpqlQuery  = "SELECT e FROM Employee e WHERE FUNCTION('SQL', ename) = 2";
 		int startPosition = "SELECT e FROM Employee e WHERE ".length();
 		int endPosition   = "SELECT e FROM Employee e WHERE FUNCTION('SQL', ename)".length();

 		testHasOnlyOneProblem(
 			jpqlQuery,
 			JPQLGrammar2_1.instance(),
 			JPQLGrammar1_0.instance(),
 			FunctionExpression_InvalidJPAVersion,
 			startPosition,
 			endPosition
 		);
	}

	@Test
	public void test_FunctionExpression_5() {

		String jpqlQuery  = "SELECT e FROM Employee e WHERE FUNCTION('SQL', ename) = 2";
 		int startPosition = "SELECT e FROM Employee e WHERE ".length();
 		int endPosition   = "SELECT e FROM Employee e WHERE FUNCTION('SQL', ename)".length();

 		testHasOnlyOneProblem(
 			jpqlQuery,
 			JPQLGrammar2_1.instance(),
 			JPQLGrammar2_0.instance(),
 			FunctionExpression_InvalidJPAVersion,
 			startPosition,
 			endPosition
 		);
	}

	@Test
	public void test_FunctionExpression_6() {

		String jpqlQuery = "SELECT FUNCTION('SQL', ename) FROM Employee e";

 		testHasNoProblems(
 			jpqlQuery,
 			JPQLGrammar2_1.instance(),
 			DefaultEclipseLinkJPQLGrammar.instance()
 		);
	}

	@Test
	public void test_FunctionExpression_7() {

		String jpqlQuery = "SELECT FUNCTION('SQL', ename) FROM Employee e";

 		testHasNoProblems(
 			jpqlQuery,
 			JPQLGrammar2_1.instance(),
 			JPQLGrammar2_1.instance()
 		);
	}

	@Test
	public void test_FunctionExpression_8() {

		String jpqlQuery = "SELECT FUNC('SQL', ename) FROM Employee e";

 		testHasNoProblems(
 			jpqlQuery,
 			DefaultEclipseLinkJPQLGrammar.instance(),
 			DefaultEclipseLinkJPQLGrammar.instance()
 		);
	}

	@Test
	public void test_IndexExpression_1() {

		String jpqlQuery  = "SELECT e FROM EXPERT_CONSUMER e JOIN e.designations d where INDEX(d) = 1";
		int startPosition = "SELECT e FROM EXPERT_CONSUMER e JOIN e.designations d where ".length();
		int endPosition   = "SELECT e FROM EXPERT_CONSUMER e JOIN e.designations d where INDEX(d)".length();

		testHasOnlyOneProblem(
			jpqlQuery,
			DefaultEclipseLinkJPQLGrammar.instance(),
			JPQLGrammar1_0.instance(),
			IndexExpression_InvalidJPAVersion,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_IndexExpression_2() {

		String jpqlQuery  = "SELECT e FROM EXPERT_CONSUMER e JOIN e.designations d where INDEX(d) = 1";
		int startPosition = "SELECT e FROM EXPERT_CONSUMER e JOIN e.designations d where ".length();
		int endPosition   = "SELECT e FROM EXPERT_CONSUMER e JOIN e.designations d where INDEX(d)".length();

		testHasOnlyOneProblem(
			jpqlQuery,
			JPQLGrammar2_1.instance(),
			JPQLGrammar1_0.instance(),
			IndexExpression_InvalidJPAVersion,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_IndexExpression_3() {

		String jpqlQuery  = "SELECT e FROM EXPERT_CONSUMER e JOIN e.designations d where INDEX(d) = 1";
		int startPosition = "SELECT e FROM EXPERT_CONSUMER e JOIN e.designations d where ".length();
		int endPosition   = "SELECT e FROM EXPERT_CONSUMER e JOIN e.designations d where INDEX(d)".length();

		testHasOnlyOneProblem(
			jpqlQuery,
			JPQLGrammar2_0.instance(),
			JPQLGrammar1_0.instance(),
			IndexExpression_InvalidJPAVersion,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_JoinAbstractSchemaName_1() {

		String jpqlQuery  = "Select e, e2 from Employee e left join Employee e2 on e.address = e2.address";
 		int startPosition = "Select e, e2 from Employee e left join ".length();
 		int endPosition   = "Select e, e2 from Employee e left join Employee".length();

 		testHasOnlyOneProblem(
 			jpqlQuery,
 			DefaultEclipseLinkJPQLGrammar.instance(),
 			JPQLGrammar1_0.instance(),
 			Join_InvalidJoinAssociationPath,
 			startPosition,
 			endPosition
 		);
	}

	@Test
	public void test_JoinAbstractSchemaName_2() {

		String jpqlQuery  = "Select e, e2 from Employee e left join Employee e2 on e.address = e2.address";
 		int startPosition = "Select e, e2 from Employee e left join ".length();
 		int endPosition   = "Select e, e2 from Employee e left join Employee".length();

 		testHasOnlyOneProblem(
 			jpqlQuery,
 			DefaultEclipseLinkJPQLGrammar.instance(),
 			JPQLGrammar2_0.instance(),
 			Join_InvalidJoinAssociationPath,
 			startPosition,
 			endPosition
 		);
	}

	@Test
	public void test_JoinAbstractSchemaName_3() {

		String jpqlQuery  = "Select e, e2 from Employee e left join Employee e2 on e.address = e2.address";
 		int startPosition = "Select e, e2 from Employee e left join ".length();
 		int endPosition   = "Select e, e2 from Employee e left join Employee".length();

 		testHasOnlyOneProblem(
 			jpqlQuery,
 			DefaultEclipseLinkJPQLGrammar.instance(),
 			JPQLGrammar2_1.instance(),
 			Join_InvalidJoinAssociationPath,
 			startPosition,
 			endPosition
 		);
	}

	@Test
	public void test_KeyExpression_1() {

		String jpqlQuery  = "SELECT KEY(d).number FROM Employee e JOIN e.depts e";
 		int startPosition = "SELECT ".length();
 		int endPosition   = "SELECT KEY(d)".length();

 		testHasOnlyOneProblem(
 			jpqlQuery,
 			DefaultEclipseLinkJPQLGrammar.instance(),
 			JPQLGrammar1_0.instance(),
 			KeyExpression_InvalidJPAVersion,
 			startPosition,
 			endPosition
 		);
	}

	@Test
	public void test_KeyExpression_2() {

		String jpqlQuery  = "SELECT KEY(d).number FROM Employee e JOIN e.depts e";
 		int startPosition = "SELECT ".length();
 		int endPosition   = "SELECT KEY(d)".length();

 		testHasOnlyOneProblem(
 			jpqlQuery,
 			JPQLGrammar2_1.instance(),
 			JPQLGrammar1_0.instance(),
 			KeyExpression_InvalidJPAVersion,
 			startPosition,
 			endPosition
 		);
	}

	@Test
	public void test_KeyExpression_3() {

		String jpqlQuery  = "SELECT KEY(d).number FROM Employee e JOIN e.depts e";
 		int startPosition = "SELECT ".length();
 		int endPosition   = "SELECT KEY(d)".length();

 		testHasOnlyOneProblem(
 			jpqlQuery,
 			JPQLGrammar2_0.instance(),
 			JPQLGrammar1_0.instance(),
 			KeyExpression_InvalidJPAVersion,
 			startPosition,
 			endPosition
 		);
	}

	@Test
	public void test_NullIfExpression_1() {

		String jpqlQuery  = "SELECT e FROM Employee e WHERE NULLIF(e.firstName, 'Bob') IS NULL";
 		int startPosition = "SELECT e FROM Employee e WHERE ".length();
 		int endPosition   = "SELECT e FROM Employee e WHERE NULLIF(e.firstName, 'Bob')".length();

 		testHasOnlyOneProblem(
 			jpqlQuery,
 			DefaultEclipseLinkJPQLGrammar.instance(),
 			JPQLGrammar1_0.instance(),
 			NullIfExpression_InvalidJPAVersion,
 			startPosition,
 			endPosition
 		);
	}

	@Test
	public void test_NullIfExpression_2() {

		String jpqlQuery  = "SELECT e FROM Employee e WHERE NULLIF(e.firstName, 'Bob') IS NULL";
 		int startPosition = "SELECT e FROM Employee e WHERE ".length();
 		int endPosition   = "SELECT e FROM Employee e WHERE NULLIF(e.firstName, 'Bob')".length();

 		testHasOnlyOneProblem(
 			jpqlQuery,
 			JPQLGrammar2_1.instance(),
 			JPQLGrammar1_0.instance(),
 			NullIfExpression_InvalidJPAVersion,
 			startPosition,
 			endPosition
 		);
	}

	@Test
	public void test_NullIfExpression_3() {

		String jpqlQuery  = "SELECT e FROM Employee e WHERE NULLIF(e.firstName, 'Bob') IS NULL";
 		int startPosition = "SELECT e FROM Employee e WHERE ".length();
 		int endPosition   = "SELECT e FROM Employee e WHERE NULLIF(e.firstName, 'Bob')".length();

 		testHasOnlyOneProblem(
 			jpqlQuery,
 			JPQLGrammar2_0.instance(),
 			JPQLGrammar1_0.instance(),
 			NullIfExpression_InvalidJPAVersion,
 			startPosition,
 			endPosition
 		);
	}

	@Test
	public void test_RegexpExpression_1() {

		String jpqlQuery  = "select e.firstName from Employee e where e.firstName regexp '^B.*'";
 		int startPosition = "select e.firstName from Employee e where ".length();
 		int endPosition   = jpqlQuery.length();

 		testHasProblem(
 			jpqlQuery,
 			DefaultEclipseLinkJPQLGrammar.instance(),
 			JPQLGrammar1_0.instance(),
 			RegexpExpression_InvalidJPAVersion,
 			startPosition,
 			endPosition
 		);
	}

	@Test
	public void test_RegexpExpression_2() {

		String jpqlQuery  = "select e.firstName from Employee e where e.firstName regexp '^B.*'";
 		int startPosition = "select e.firstName from Employee e where ".length();
 		int endPosition   = jpqlQuery.length();

 		testHasProblem(
 			jpqlQuery,
 			DefaultEclipseLinkJPQLGrammar.instance(),
 			JPQLGrammar2_0.instance(),
 			RegexpExpression_InvalidJPAVersion,
 			startPosition,
 			endPosition
 		);
	}

	@Test
	public void test_RegexpExpression_3() {

		String jpqlQuery  = "select e.firstName from Employee e where e.firstName regexp '^B.*'";
 		int startPosition = "select e.firstName from Employee e where ".length();
 		int endPosition   = jpqlQuery.length();

 		testHasProblem(
 			jpqlQuery,
 			DefaultEclipseLinkJPQLGrammar.instance(),
 			JPQLGrammar2_1.instance(),
 			RegexpExpression_InvalidJPAVersion,
 			startPosition,
 			endPosition
 		);
	}

	@Test
	public void test_ResultVariable_1() {

		String jpqlQuery  = "SELECT AVG(e.age) AS a FROM Employee e";
 		int startPosition = "SELECT AVG(e.age) ".length();
 		int endPosition   = "SELECT AVG(e.age) AS a".length();

 		testHasOnlyOneProblem(
 			jpqlQuery,
 			DefaultEclipseLinkJPQLGrammar.instance(),
 			JPQLGrammar1_0.instance(),
 			ResultVariable_InvalidJPAVersion,
 			startPosition,
 			endPosition
 		);
	}

	@Test
	public void test_ResultVariable_2() {

		String jpqlQuery  = "SELECT AVG(e.age) AS a FROM Employee e";
 		int startPosition = "SELECT AVG(e.age) ".length();
 		int endPosition   = "SELECT AVG(e.age) AS a".length();

 		testHasOnlyOneProblem(
 			jpqlQuery,
 			JPQLGrammar2_1.instance(),
 			JPQLGrammar1_0.instance(),
 			ResultVariable_InvalidJPAVersion,
 			startPosition,
 			endPosition
 		);
	}

	@Test
	public void test_ResultVariable_3() {

		String jpqlQuery  = "SELECT AVG(e.age) AS a FROM Employee e";
 		int startPosition = "SELECT AVG(e.age) ".length();
 		int endPosition   = "SELECT AVG(e.age) AS a".length();

 		testHasOnlyOneProblem(
 			jpqlQuery,
 			JPQLGrammar2_0.instance(),
 			JPQLGrammar1_0.instance(),
 			ResultVariable_InvalidJPAVersion,
 			startPosition,
 			endPosition
 		);
	}

	@Test
	public void test_SQLFunction_1() {

		String jpqlQuery  = "Select e from Employee e order by SQL('? NULLS LAST', e.id)";
 		int startPosition = "Select e from Employee e order by ".length();
 		int endPosition   = jpqlQuery.length();

 		testHasOnlyOneProblem(
 			jpqlQuery,
 			DefaultEclipseLinkJPQLGrammar.instance(),
 			JPQLGrammar1_0.instance(),
 			FunctionExpression_InvalidJPAVersion,
 			startPosition,
 			endPosition
 		);
	}

	@Test
	public void test_SQLFunction_2() {

		String jpqlQuery  = "Select e from Employee e order by SQL('? NULLS LAST', e.id)";
 		int startPosition = "Select e from Employee e order by ".length();
 		int endPosition   = jpqlQuery.length();

 		testHasOnlyOneProblem(
 			jpqlQuery,
 			EclipseLinkJPQLGrammar2_4.instance(),
 			JPQLGrammar2_0.instance(),
 			FunctionExpression_InvalidJPAVersion,
 			startPosition,
 			endPosition
 		);
	}

	@Test
	public void test_SQLFunction_3() {

		String jpqlQuery  = "Select e from Employee e order by SQL('? NULLS LAST', e.id)";
 		int startPosition = "Select e from Employee e order by ".length();
 		int endPosition   = jpqlQuery.length();

 		testHasOnlyOneProblem(
 			jpqlQuery,
 			DefaultEclipseLinkJPQLGrammar.instance(),
 			JPQLGrammar2_1.instance(),
 			FunctionExpression_InvalidJPAVersion,
 			startPosition,
 			endPosition
 		);
	}

	@Test
	public void test_TableVariableDeclaration_1() {

		String jpqlQuery  = "select e from Employee e, table('EMP') EMP where e.empId = EMP.EMP_ID";
 		int startPosition = "select e from Employee e, ".length();
 		int endPosition   = "select e from Employee e, table('EMP') EMP".length();

 		testHasOnlyOneProblem(
 			jpqlQuery,
 			DefaultEclipseLinkJPQLGrammar.instance(),
 			JPQLGrammar1_0.instance(),
 			TableVariableDeclaration_InvalidJPAVersion,
 			startPosition,
 			endPosition
 		);
	}

	@Test
	public void test_TableVariableDeclaration_2() {

		String jpqlQuery  = "select e from Employee e, table('EMP') EMP where e.empId = EMP.EMP_ID";
		int startPosition = "select e from Employee e, ".length();
		int endPosition   = "select e from Employee e, table('EMP') EMP".length();

		testHasOnlyOneProblem(
			jpqlQuery,
			DefaultEclipseLinkJPQLGrammar.instance(),
			JPQLGrammar2_0.instance(),
			TableVariableDeclaration_InvalidJPAVersion,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_TableVariableDeclaration_3() {

		String jpqlQuery  = "select e from Employee e, table('EMP') EMP where e.empId = EMP.EMP_ID";
		int startPosition = "select e from Employee e, ".length();
		int endPosition   = "select e from Employee e, table('EMP') EMP".length();

		testHasOnlyOneProblem(
			jpqlQuery,
			DefaultEclipseLinkJPQLGrammar.instance(),
			JPQLGrammar2_1.instance(),
			TableVariableDeclaration_InvalidJPAVersion,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_TypeExpression_1() {

		String jpqlQuery  = "SELECT e FROM Employee e WHERE TYPE(e) <> e.name";
 		int startPosition = "SELECT e FROM Employee e WHERE ".length();
 		int endPosition   = "SELECT e FROM Employee e WHERE TYPE(e)".length();

 		testHasOnlyOneProblem(
 			jpqlQuery,
 			DefaultEclipseLinkJPQLGrammar.instance(),
 			JPQLGrammar1_0.instance(),
 			TypeExpression_InvalidJPAVersion,
 			startPosition,
 			endPosition
 		);
	}

	@Test
	public void test_TypeExpression_2() {

		String jpqlQuery  = "SELECT e FROM Employee e WHERE TYPE(e) <> e.name";
 		int startPosition = "SELECT e FROM Employee e WHERE ".length();
 		int endPosition   = "SELECT e FROM Employee e WHERE TYPE(e)".length();

 		testHasOnlyOneProblem(
 			jpqlQuery,
 			JPQLGrammar2_1.instance(),
 			JPQLGrammar1_0.instance(),
 			TypeExpression_InvalidJPAVersion,
 			startPosition,
 			endPosition
 		);
	}

	@Test
	public void test_TypeExpression_3() {

		String jpqlQuery  = "SELECT e FROM Employee e WHERE TYPE(e) <> e.name";
 		int startPosition = "SELECT e FROM Employee e WHERE ".length();
 		int endPosition   = "SELECT e FROM Employee e WHERE TYPE(e)".length();

 		testHasOnlyOneProblem(
 			jpqlQuery,
 			JPQLGrammar2_0.instance(),
 			JPQLGrammar1_0.instance(),
 			TypeExpression_InvalidJPAVersion,
 			startPosition,
 			endPosition
 		);
	}

	@Test
	public void test_UnionClause_1() {

		String jpqlQuery  = "select e from Employee e intersect all select p from Product p where p.id <> 2";
 		int startPosition = "select e from Employee e ".length();
 		int endPosition   = jpqlQuery.length();

 		testHasOnlyOneProblem(
 			jpqlQuery,
 			DefaultEclipseLinkJPQLGrammar.instance(),
 			JPQLGrammar1_0.instance(),
 			UnionClause_InvalidJPAVersion,
 			startPosition,
 			endPosition
 		);
	}

	@Test
	public void test_UnionClause_2() {

		String jpqlQuery  = "select e from Employee e intersect all select p from Product p where p.id <> 2";
 		int startPosition = "select e from Employee e ".length();
 		int endPosition   = jpqlQuery.length();

 		testHasOnlyOneProblem(
 			jpqlQuery,
 			DefaultEclipseLinkJPQLGrammar.instance(),
 			JPQLGrammar2_0.instance(),
 			UnionClause_InvalidJPAVersion,
 			startPosition,
 			endPosition
 		);
	}

	@Test
	public void test_UnionClause_3() {

		String jpqlQuery  = "select e from Employee e intersect all select p from Product p where p.id <> 2";
 		int startPosition = "select e from Employee e ".length();
 		int endPosition   = jpqlQuery.length();

 		testHasOnlyOneProblem(
 			jpqlQuery,
 			DefaultEclipseLinkJPQLGrammar.instance(),
 			JPQLGrammar2_1.instance(),
 			UnionClause_InvalidJPAVersion,
 			startPosition,
 			endPosition
 		);
	}

	@Test
	public void test_ValueExpression_1() {

		String jpqlQuery  = "SELECT VALUE(m).name FROM Employee e JOIN e.managers m";
 		int startPosition = "SELECT ".length();
 		int endPosition   = "SELECT VALUE(m)".length();

 		testHasOnlyOneProblem(
 			jpqlQuery,
 			DefaultEclipseLinkJPQLGrammar.instance(),
 			JPQLGrammar1_0.instance(),
 			ValueExpression_InvalidJPAVersion,
 			startPosition,
 			endPosition
 		);
	}

	@Test
	public void test_ValueExpression_2() {

		String jpqlQuery  = "SELECT VALUE(m).name FROM Employee e JOIN e.managers m";
 		int startPosition = "SELECT ".length();
 		int endPosition   = "SELECT VALUE(m)".length();

 		testHasOnlyOneProblem(
 			jpqlQuery,
 			JPQLGrammar2_1.instance(),
 			JPQLGrammar1_0.instance(),
 			ValueExpression_InvalidJPAVersion,
 			startPosition,
 			endPosition
 		);
	}

	@Test
	public void test_ValueExpression_3() {

		String jpqlQuery  = "SELECT VALUE(m).name FROM Employee e JOIN e.managers m";
		int startPosition = "SELECT ".length();
		int endPosition   = "SELECT VALUE(m)".length();

		testHasOnlyOneProblem(
			jpqlQuery,
			JPQLGrammar2_0.instance(),
			JPQLGrammar1_0.instance(),
			ValueExpression_InvalidJPAVersion,
			startPosition,
			endPosition
		);
	}

	protected final void testHasNoProblems(String jpqlQuery,
	                                       JPQLGrammar parserGrammar,
	                                       JPQLGrammar validationGrammar) {

 		List<JPQLQueryProblem> problems = validate(jpqlQuery, parserGrammar, validationGrammar);
 		testHasNoProblems(problems);
	}

	protected final void testHasOnlyOneProblem(String jpqlQuery,
	                                           JPQLGrammar parserGrammar,
	                                           JPQLGrammar validationGrammar,
	                                           String problemKey,
	                                           int startPosition,
	                                           int endPosition) {

 		List<JPQLQueryProblem> problems = validate(jpqlQuery, parserGrammar, validationGrammar);
 		testHasOnlyOneProblem(problems, problemKey, startPosition, endPosition);
	}

	protected final void testHasProblem(String jpqlQuery,
	                                    JPQLGrammar parserGrammar,
	                                    JPQLGrammar validationGrammar,
	                                    String problemKey,
	                                    int startPosition,
	                                    int endPosition) {

 		List<JPQLQueryProblem> problems = validate(jpqlQuery, parserGrammar, validationGrammar);
 		testHasProblem(problems, problemKey, startPosition, endPosition);
	}

	protected List<JPQLQueryProblem> validate(String jpqlQuery,
	                                          JPQLGrammar parserGrammar,
	                                          JPQLGrammar validationGrammar) {

		JPQLExpression jpqlExpression = new JPQLExpression(jpqlQuery, parserGrammar);
 		List<JPQLQueryProblem> problems = new LinkedList<JPQLQueryProblem>();

 		EclipseLinkGrammarValidator grammarValidator = new EclipseLinkGrammarValidator(validationGrammar);
 		grammarValidator.setProblems(problems);
 		jpqlExpression.accept(grammarValidator);

 		return problems;
	}
}