/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
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
import org.eclipse.persistence.jpa.jpql.JPQLQueryProblem;
import org.eclipse.persistence.jpa.jpql.JPQLQueryProblemMessages;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar2_0;
import org.junit.Test;

/**
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public class EclipseLinkGrammarValidatorTest extends AbstractGrammarValidatorTest {

	@Test
	public void test_FuncExpression_MissingFunctionName() throws Exception {

		// Ignore
		if (getGrammar() == EclipseLinkJPQLGrammar2_0.instance()) {
			return;
		}

		String query = "SELECT FUNC() FROM Employee e";
		int startPosition = "SELECT FUNC(".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.FuncExpression_MissingFunctionName,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_FuncExpression_MissingLeftParenthesis() throws Exception {

		// Ignore
		if (getGrammar() == EclipseLinkJPQLGrammar2_0.instance()) {
			return;
		}

		String query = "SELECT FUNC 'getName', 'String') FROM Employee e";
		int startPosition = "SELECT FUNC".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.FuncExpression_MissingLeftParenthesis,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_FuncExpression_MissingRightParenthesis() throws Exception {

		// Ignore
		if (getGrammar() == EclipseLinkJPQLGrammar2_0.instance()) {
			return;
		}

		String query = "SELECT FUNC('getName', 'String' FROM Employee e";
		int startPosition = "SELECT FUNC('getName', 'String'".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.FuncExpression_MissingRightParenthesis,
			startPosition,
			endPosition
		);
	}

	@Test
	@Override
	public void test_OrderByItem_InvalidPath() throws Exception {

		String query = "SELECT e FROM Employee e ORDER BY e.name 2";

		List<JPQLQueryProblem> problems = validate(query);

		testDoesNotHaveProblem(
			problems,
			JPQLQueryProblemMessages.OrderByItem_InvalidPath
		);
	}
}