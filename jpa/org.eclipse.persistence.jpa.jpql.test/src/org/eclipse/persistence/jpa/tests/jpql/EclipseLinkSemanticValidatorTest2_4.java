/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
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

import java.util.List;
import org.eclipse.persistence.jpa.jpql.AbstractSemanticValidator;
import org.eclipse.persistence.jpa.jpql.EclipseLinkJPQLQueryContext;
import org.eclipse.persistence.jpa.jpql.EclipseLinkSemanticValidator;
import org.eclipse.persistence.jpa.jpql.JPQLQueryContext;
import org.eclipse.persistence.jpa.jpql.JPQLQueryProblem;
import org.junit.Test;

import static org.eclipse.persistence.jpa.jpql.JPQLQueryProblemMessages.*;

/**
 * The unit-test class used for testing a JPQL query semantically when the JPA version is 1.0 and 2.0
 * and EclipseLink is the persistence provider. The EclipseLink version supported is 2.4 only.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public class EclipseLinkSemanticValidatorTest2_4 extends AbstractSemanticValidatorTest {

	@Override
	protected JPQLQueryContext buildQueryContext() {
		return new EclipseLinkJPQLQueryContext(jpqlGrammar);
	}

	@Override
	protected AbstractSemanticValidator buildValidator() {
		return new EclipseLinkSemanticValidator(buildSemanticValidatorHelper());
	}

	@Override
	protected boolean isPathExpressionToCollectionMappingAllowed() {
		return true;
	}

	@Test
	public final void test_AbstractFromClause_InvalidFirstIdentificationVariableDeclaration_07() throws Exception {

		String jpqlQuery = "select e from (select e2 from Employee e2) e";
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
	public final void test_AbstractFromClause_InvalidFirstIdentificationVariableDeclaration_08() throws Exception {

		String jpqlQuery = "select e3 from Employee e, (select e2 from Employee e2) e3, IN(e.phoneNumbers) p";
		List<JPQLQueryProblem> problems = validate(jpqlQuery);
		testHasNoProblems(problems);
	}

	@Test
	public final void test_AbstractFromClause_InvalidFirstIdentificationVariableDeclaration_09() throws Exception {

		String jpqlQuery = "select EMP from TABLE('EMPLOYEE') EMP";
		int startPosition = "select EMP from ".length();
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
	public final void test_AbstractFromClause_InvalidFirstIdentificationVariableDeclaration_10() throws Exception {

		String jpqlQuery = "select e from TABLE('EMPLOYEE') EMP, Employee e";
		int startPosition = "select e from ".length();
		int endPosition   = "select e from TABLE('EMPLOYEE') EMP".length();

		List<JPQLQueryProblem> problems = validate(jpqlQuery);

		testHasOnlyOneProblem(
			problems,
			AbstractFromClause_InvalidFirstIdentificationVariableDeclaration,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_CollectionValuedPathExpression_NotResolvable() throws Exception {

		String query = "SELECT a FROM jpql.query.Address a";
		List<JPQLQueryProblem> problems = validate(query);
		testHasNoProblems(problems);
	}

	@Test
	public final void test_StateFieldPathExpression_NotResolvable_4() throws Exception {

		String jpqlQuery  = "select e from Employee e where e.empId in (select table('employee').id from Employee e)";
		int startPosition = "select e from Employee e where e.empId in(select ".length();
		int endPosition   = "select e from Employee e where e.empId in(select table('employee').id".length();

		List<JPQLQueryProblem> problems = validate(jpqlQuery);

		testHasOnlyOneProblem(
			problems,
			StateFieldPathExpression_NotResolvable,
			startPosition,
			endPosition
		);
	}
}