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

import java.util.List;
import org.eclipse.persistence.jpa.jpql.AbstractGrammarValidator;
import org.eclipse.persistence.jpa.jpql.DefaultGrammarValidator;
import org.eclipse.persistence.jpa.jpql.JPQLQueryProblem;
import org.eclipse.persistence.jpa.jpql.JPQLQueryProblemMessages;
import org.eclipse.persistence.jpa.jpql.spi.JPAVersion;
import org.junit.Test;

/**
 * The unit-test class used for testing a JPQL query grammatically when the JPA version is 2.1.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public class DefaultGrammarValidatorTest2_1 extends AbstractGrammarValidatorTest {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected AbstractGrammarValidator buildValidator() {
		return new DefaultGrammarValidator(jpqlGrammar);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean isJoinFetchIdentifiable() {
		return false;
	}

	@Override
	protected boolean isSubqueryAllowedAnywhere() {
		return jpqlGrammar.getJPAVersion().isNewerThanOrEqual(JPAVersion.VERSION_2_1);
	}

	@Test
	public final void test_FunctionExpression_MissingFunctionName_1() throws Exception {

		String query = "SELECT FUNCTION() FROM Employee e";
		int startPosition = "SELECT FUNCTION(".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.FunctionExpression_MissingFunctionName,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_FunctionExpression_MissingFunctionName_2() throws Exception {

		String query = "SELECT FUNCTION('sql') FROM Employee e";
		List<JPQLQueryProblem> problems = validate(query);

		testDoesNotHaveProblem(
			problems,
			JPQLQueryProblemMessages.FunctionExpression_MissingFunctionName
		);
	}

	@Test
	public final void test_FunctionExpression_MissingRightParenthesis_1() throws Exception {

		String query = "SELECT FUNCTION('getName', 'String' FROM Employee e";
		int startPosition = "SELECT FUNCTION('getName', 'String'".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.FunctionExpression_MissingRightParenthesis,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_FunctionExpression_MissingRightParenthesis_2() throws Exception {

		String query = "SELECT FUNCTION('getName', 'String') FROM Employee e";
		List<JPQLQueryProblem> problems = validate(query);

		testDoesNotHaveProblem(
			problems,
			JPQLQueryProblemMessages.FunctionExpression_MissingRightParenthesis
		);
	}

	@Test
	public final void test_OnClause_InvalidConditionalExpression_1() throws Exception {

		String query = "select e from Employee e join e.address a on a.id > 2";
		List<JPQLQueryProblem> problems = validate(query);

		testDoesNotHaveProblem(
			problems,
			JPQLQueryProblemMessages.OnClause_InvalidConditionalExpression
		);
	}

	@Test
	public final void test_OnClause_InvalidConditionalExpression_2() throws Exception {

		String query = "select e from Employee e join e.address a on a.id";
		int startPosition = "select e from Employee e join e.address a on ".length();
		int endPosition   = query.length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.OnClause_InvalidConditionalExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_OnClause_MissingConditionalExpression_1() throws Exception {

		String query = "select e from Employee e join e.address a on";
		int startPosition = "select e from Employee e join e.address a on".length();
		int endPosition   = query.length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.OnClause_MissingConditionalExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_OnClause_MissingConditionalExpression_2() throws Exception {

		String query = "select e from Employee e join e.address a on where e.id > 2";
		int startPosition = "select e from Employee e join e.address a on ".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.OnClause_MissingConditionalExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_OnClause_MissingConditionalExpression_3() throws Exception {

		String query = "select e from Employee e join e.address a on e.id > 2";
		List<JPQLQueryProblem> problems = validate(query);

		testDoesNotHaveProblem(
			problems,
			JPQLQueryProblemMessages.OnClause_MissingConditionalExpression
		);
	}
}