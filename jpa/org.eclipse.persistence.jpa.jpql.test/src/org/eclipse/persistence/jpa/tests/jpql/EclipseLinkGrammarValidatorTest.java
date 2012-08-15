/*******************************************************************************
 * Copyright (c) 2006, 2012 Oracle and/or its affiliates. All rights reserved.
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
import org.eclipse.persistence.jpa.jpql.EclipseLinkGrammarValidator;
import org.eclipse.persistence.jpa.jpql.EclipseLinkVersion;
import org.eclipse.persistence.jpa.jpql.JPQLQueryProblem;
import org.junit.Test;

import static org.eclipse.persistence.jpa.jpql.JPQLQueryProblemMessages.*;

/**
 * The unit-test class used for testing a JPQL query grammatically when the JPA version is 1.0 and
 * 2.0 and EclipseLink is the persistence provider. The EclipseLink version supported is 2.0, 2.1,
 * 2.2 and 2.3.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public class EclipseLinkGrammarValidatorTest extends AbstractGrammarValidatorTest {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected AbstractGrammarValidator buildValidator() {
		return new EclipseLinkGrammarValidator(jpqlGrammar);
	}

	protected boolean equals(EclipseLinkVersion version) {
		EclipseLinkVersion currentVersion = EclipseLinkVersion.value(jpqlGrammar.getProviderVersion());
		return currentVersion == version;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean isJoinFetchIdentifiable() {
		return isNewerThanOrEqual(EclipseLinkVersion.VERSION_2_4);
	}

	protected boolean isNewerThanOrEqual(EclipseLinkVersion version) {
		EclipseLinkVersion currentVersion = EclipseLinkVersion.value(jpqlGrammar.getProviderVersion());
		return currentVersion.isNewerThanOrEqual(version);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean isSubqueryAllowedAnywhere() {
		return isNewerThanOrEqual(EclipseLinkVersion.VERSION_2_4);
	}

	@Test
	public final void test_AggregateFunction_WrongClause_5() throws Exception {

		String query = "SELECT e FROM Employee e GROUP BY AVG(e.age)";
		List<JPQLQueryProblem> problems = validate(query);

		if (equals(EclipseLinkVersion.VERSION_2_0)) {
			int startPosition = "SELECT e FROM Employee e GROUP BY ".length();
			int endPosition   = query.length();
			testHasOnlyOneProblem(problems, BadExpression_InvalidExpression, startPosition, endPosition);
		}
		else {
			testHasNoProblems(problems);
		}
	}

	@Test
	public final void test_GroupByClause_GroupByItemIsMissingComma_3() throws Exception {

		String query = "SELECT e FROM Employee e GROUP BY AVG(e.age) e.name";
		int startPosition = "SELECT e FROM Employee e GROUP BY AVG(e.age)".length();
		int endPosition   = "SELECT e FROM Employee e GROUP BY AVG(e.age) ".length();

		List<JPQLQueryProblem> problems = validate(query);

		if (equals(EclipseLinkVersion.VERSION_2_0)) {

			int startPosition1 = "SELECT e FROM Employee e GROUP BY ".length();
			int endPosition1   = "SELECT e FROM Employee e GROUP BY AVG(e.age)".length();

			testHasProblem(
				problems,
				BadExpression_InvalidExpression,
				startPosition1,
				endPosition1
			);

			testHasProblem(
				problems,
				GroupByClause_GroupByItemIsMissingComma,
				startPosition,
				endPosition
			);
		}
		else {
			testHasOnlyOneProblem(
				problems,
				GroupByClause_GroupByItemIsMissingComma,
				startPosition,
				endPosition
			);
		}
	}

	@Test
	public final void test_OrderByClause_GroupByItemIsMissingComma_3() throws Exception {

		String query = "SELECT e FROM Employee e ORDER BY LENGTH(e.age) e.name";
		int startPosition = "SELECT e FROM Employee e ORDER BY LENGTH(e.age)".length();
		int endPosition   = "SELECT e FROM Employee e ORDER BY LENGTH(e.age) ".length();

		List<JPQLQueryProblem> problems = validate(query);

		if (equals(EclipseLinkVersion.VERSION_2_0)) {

			int startPosition1 = "SELECT e FROM Employee e ORDER BY ".length();
			int endPosition1   = "SELECT e FROM Employee e ORDER BY LENGTH(e.age)".length();

			testHasProblem(
				problems,
				BadExpression_InvalidExpression,
				startPosition1,
				endPosition1
			);

			testHasProblem(
				problems,
				OrderByClause_OrderByItemIsMissingComma,
				startPosition,
				endPosition
			);
		}
		else {
			testHasOnlyOneProblem(
				problems,
				OrderByClause_OrderByItemIsMissingComma,
				startPosition,
				endPosition
			);
		}
	}
}