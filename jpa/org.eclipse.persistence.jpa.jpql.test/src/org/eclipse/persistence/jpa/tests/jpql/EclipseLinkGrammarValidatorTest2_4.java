/*******************************************************************************
 * Copyright (c) 2012 Oracle. All rights reserved.
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
import org.eclipse.persistence.jpa.jpql.JPQLQueryProblem;
import org.eclipse.persistence.jpa.jpql.parser.EclipseLinkJPQLGrammar2_4;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLQueryStringFormatter;
import org.junit.Test;

import static org.eclipse.persistence.jpa.jpql.JPQLQueryProblemMessages.*;

/**
 * The unit-test class used for testing a JPQL query grammatically when the JPA version is 2.1 and
 * EclipseLink is the persistence provider. The EclipseLink version supported is only 2.4.
 *
 * @see EclipseLinkGrammarValidator
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public class EclipseLinkGrammarValidatorTest2_4 extends AbstractGrammarValidatorTest {

	private JPQLQueryStringFormatter buildStringFormatter_1() {
		return new JPQLQueryStringFormatter() {
			public String format(String jpqlQuery) {
				return jpqlQuery.replace(",)", ", )");
			}
		};
	}

	private JPQLQueryStringFormatter buildStringFormatter_2() {
		return new JPQLQueryStringFormatter() {
			public String format(String query) {
				return query.replace("firstName)", "firstName )");
			}
		};
	}

	private JPQLQueryStringFormatter buildStringFormatter_3() {
		return new JPQLQueryStringFormatter() {
			public String format(String query) {
				return query.replace(")", " )");
			}
		};
	}

	private JPQLQueryStringFormatter buildStringFormatter_4() {
		return new JPQLQueryStringFormatter() {
			public String format(String query) {
				return query.replace(",", ", ");
			}
		};
	}

	private JPQLQueryStringFormatter buildStringFormatter_5() {
		return new JPQLQueryStringFormatter() {
			public String format(String query) {
				return query.replace("(", "( ");
			}
		};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected AbstractGrammarValidator buildValidator() {
		return new EclipseLinkGrammarValidator(jpqlGrammar);
	}

	protected boolean isEclipseLink2_4() {
		return jpqlGrammar.getProviderVersion() == EclipseLinkJPQLGrammar2_4.VERSION;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean isJoinFetchIdentifiable() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean isSubqueryAllowedAnywhere() {
		return isEclipseLink2_4();
	}

	@Test
	public final void test_CastExpression_InvalidExpression_1() throws Exception {

		String jpqlQuery = "Select cast(e.firstName as char) from Employee e";
		List<JPQLQueryProblem> problems = validate(jpqlQuery);
		testDoesNotHaveProblem(problems, CastExpression_InvalidExpression);
	}

	@Test
	public final void test_CastExpression_MissingDatabaseType_1() throws Exception {

		String jpqlQuery = "Select cast(e.firstName char) from Employee e";
		List<JPQLQueryProblem> problems = validate(jpqlQuery);
		testDoesNotHaveProblem(problems, CastExpression_MissingDatabaseType);
	}

	@Test
	public final void test_CastExpression_MissingDatabaseType_2() throws Exception {

		String jpqlQuery = "Select cast(e.firstName as char) from Employee e";
		List<JPQLQueryProblem> problems = validate(jpqlQuery);
		testDoesNotHaveProblem(problems, CastExpression_MissingDatabaseType);
	}

	@Test
	public final void test_CastExpression_MissingDatabaseType_3() throws Exception {

		String jpqlQuery = "Select cast(e.firstName) from Employee e";
		int startPosition = "Select cast(e.firstName".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(jpqlQuery);

		testHasOnlyOneProblem(
			problems,
			CastExpression_MissingDatabaseType,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_CastExpression_MissingDatabaseType_4() throws Exception {

		String jpqlQuery = "Select cast(e.firstName ) from Employee e";
		int startPosition = "Select cast(e.firstName ".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(jpqlQuery, buildStringFormatter_2());

		testHasOnlyOneProblem(
			problems,
			CastExpression_MissingDatabaseType,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_CastExpression_MissingDatabaseType_5() throws Exception {

		String jpqlQuery = "Select cast(e.firstName as) from Employee e";
		int startPosition = "Select cast(e.firstName as".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(jpqlQuery);

		testHasOnlyOneProblem(
			problems,
			CastExpression_MissingDatabaseType,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_CastExpression_MissingDatabaseType_6() throws Exception {

		String jpqlQuery = "Select cast(e.firstName as ) from Employee e";
		int startPosition = "Select cast(e.firstName as ".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(jpqlQuery, buildStringFormatter_3());

		testHasOnlyOneProblem(
			problems,
			CastExpression_MissingDatabaseType,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_CastExpression_MissingDatabaseType_7() throws Exception {

		String jpqlQuery = "Select cast(e.firstName from Employee e";
		int startPosition = "Select cast(e.firstName ".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(jpqlQuery);

		testHasProblem(
			problems,
			CastExpression_MissingDatabaseType,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_CastExpression_MissingDatabaseType_8() throws Exception {

		String jpqlQuery = "Select cast(e.firstName as from Employee e";
		int startPosition = "Select cast(e.firstName as ".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(jpqlQuery);

		testHasProblem(
			problems,
			CastExpression_MissingDatabaseType,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_CastExpression_MissingExpression_1() throws Exception {

		String jpqlQuery = "Select cast(e.firstName as char) from Employee e";
		List<JPQLQueryProblem> problems = validate(jpqlQuery);
		testDoesNotHaveProblem(problems, CastExpression_MissingExpression);
	}

	@Test
	public final void test_CastExpression_MissingExpression_2() throws Exception {

		String jpqlQuery = "Select cast(as char) from Employee e";
		int startPosition = "Select cast(".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(jpqlQuery);

		testHasOnlyOneProblem(
			problems,
			CastExpression_MissingExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_CastExpression_MissingLeftParenthesis_1() throws Exception {

		String jpqlQuery = "Select cast(e.firstName as char) from Employee e";
		List<JPQLQueryProblem> problems = validate(jpqlQuery);
		testDoesNotHaveProblem(problems, CastExpression_MissingLeftParenthesis);
	}

	@Test
	public final void test_CastExpression_MissingLeftParenthesis_2() throws Exception {

		String jpqlQuery = "Select cast from Employee e";
		int startPosition = "Select cast".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(jpqlQuery);

		testHasOnlyOneProblem(
			problems,
			CastExpression_MissingLeftParenthesis,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_CastExpression_MissingLeftParenthesis_3() throws Exception {

		String jpqlQuery = "Select cast e.firstName as char) from Employee e";
		int startPosition = "Select cast".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(jpqlQuery);

		testHasOnlyOneProblem(
			problems,
			CastExpression_MissingLeftParenthesis,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_CastExpression_MissingLeftParenthesis_4() throws Exception {

		String jpqlQuery = "Select cast as from Employee e";
		int startPosition = "Select cast".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(jpqlQuery);

		testHasProblem(
			problems,
			CastExpression_MissingLeftParenthesis,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_CastExpression_MissingRightParenthesis_1() throws Exception {

		String jpqlQuery = "Select cast(e.firstName char) from Employee e";
		List<JPQLQueryProblem> problems = validate(jpqlQuery);
		testDoesNotHaveProblem(problems, CastExpression_MissingRightParenthesis);
	}

	@Test
	public final void test_CastExpression_MissingRightParenthesis_2() throws Exception {

		String jpqlQuery = "Select cast(e.firstName as char) from Employee e";
		List<JPQLQueryProblem> problems = validate(jpqlQuery);
		testDoesNotHaveProblem(problems, CastExpression_MissingRightParenthesis);
	}

	@Test
	public final void test_CastExpression_MissingRightParenthesis_3() throws Exception {

		String jpqlQuery  = "Select cast(e.firstName as char(2) from Employee e";
		int startPosition = "Select cast(e.firstName as char(2)".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(jpqlQuery);

		testHasOnlyOneProblem(
			problems,
			CastExpression_MissingRightParenthesis,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_CastExpression_MissingRightParenthesis_4() throws Exception {

		String jpqlQuery = "Select cast(e.firstName as char from Employee e";
		int startPosition = "Select cast(e.firstName as char".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(jpqlQuery);

		testHasOnlyOneProblem(
			problems,
			CastExpression_MissingRightParenthesis,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_DatabaseType_InvalidFirstExpression_1() throws Exception {

		String jpqlQuery = "Select cast(e.firstName char) from Employee e";
		List<JPQLQueryProblem> problems = validate(jpqlQuery);
		testDoesNotHaveProblem(problems, DatabaseType_InvalidFirstExpression);
	}

	@Test
	public final void test_DatabaseType_InvalidFirstExpression_2() throws Exception {

		String jpqlQuery = "Select cast(e.firstName char(2)) from Employee e";
		List<JPQLQueryProblem> problems = validate(jpqlQuery);
		testDoesNotHaveProblem(problems, DatabaseType_InvalidFirstExpression);
	}

	@Test
	public final void test_DatabaseType_InvalidFirstExpression_3() throws Exception {

		String jpqlQuery = "Select cast(e.firstName char(2, 2)) from Employee e";
		List<JPQLQueryProblem> problems = validate(jpqlQuery);
		testDoesNotHaveProblem(problems, DatabaseType_InvalidFirstExpression);
	}

	@Test
	public final void test_DatabaseType_InvalidFirstExpression_4() throws Exception {

		String jpqlQuery  = "Select cast(e.firstName char(avg(e.age), 2)) from Employee e";
		int startPosition = "Select cast(e.firstName char(".length();
		int endPosition   = "Select cast(e.firstName char(avg(e.age)".length();

		List<JPQLQueryProblem> problems = validate(jpqlQuery);

		testHasOnlyOneProblem(
			problems,
			DatabaseType_InvalidFirstExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_DatabaseType_InvalidSecondExpression_1() throws Exception {

		String jpqlQuery = "Select cast(e.firstName char) from Employee e";
		List<JPQLQueryProblem> problems = validate(jpqlQuery);
		testDoesNotHaveProblem(problems, DatabaseType_InvalidSecondExpression);
	}

	@Test
	public final void test_DatabaseType_InvalidSecondExpression_2() throws Exception {

		String jpqlQuery = "Select cast(e.firstName char(2)) from Employee e";
		List<JPQLQueryProblem> problems = validate(jpqlQuery);
		testDoesNotHaveProblem(problems, DatabaseType_InvalidSecondExpression);
	}

	@Test
	public final void test_DatabaseType_InvalidSecondExpression_3() throws Exception {

		String jpqlQuery = "Select cast(e.firstName char(2, 2)) from Employee e";
		List<JPQLQueryProblem> problems = validate(jpqlQuery);
		testDoesNotHaveProblem(problems, DatabaseType_InvalidSecondExpression);
	}

	@Test
	public final void test_DatabaseType_InvalidSecondExpression_4() throws Exception {

		String jpqlQuery = "Select cast(e.firstName char(2, avg(e.age))) from Employee e";
		int startPosition = "Select cast(e.firstName char(2, ".length();
		int endPosition   = "Select cast(e.firstName char(2, avg(e.age)".length();

		List<JPQLQueryProblem> problems = validate(jpqlQuery);

		testHasOnlyOneProblem(
			problems,
			DatabaseType_InvalidSecondExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_DatabaseType_MissingComma_1() throws Exception {

		String jpqlQuery = "Select cast(e.firstName char(2, 2)) from Employee e";
		List<JPQLQueryProblem> problems = validate(jpqlQuery);
		testDoesNotHaveProblem(problems, DatabaseType_MissingComma);
	}

	@Test
	public final void test_DatabaseType_MissingComma_2() throws Exception {

		String jpqlQuery = "Select cast(e.firstName char(2)) from Employee e";
		List<JPQLQueryProblem> problems = validate(jpqlQuery);
		testDoesNotHaveProblem(problems, DatabaseType_MissingComma);
	}

	@Test
	public final void test_DatabaseType_MissingComma_3() throws Exception {

		String jpqlQuery = "Select cast(e.firstName char(2 2)) from Employee e";
		int startPosition = "Select cast(e.firstName char(2".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(jpqlQuery);

		testHasOnlyOneProblem(
			problems,
			DatabaseType_MissingComma,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_DatabaseType_MissingFirstExpression_1() throws Exception {

		String jpqlQuery = "Select cast(e.firstName char) from Employee e";
		List<JPQLQueryProblem> problems = validate(jpqlQuery);
		testDoesNotHaveProblem(problems, DatabaseType_MissingFirstExpression);
	}

	@Test
	public final void test_DatabaseType_MissingFirstExpression_2() throws Exception {

		String jpqlQuery = "Select cast(e.firstName char(2)) from Employee e";
		List<JPQLQueryProblem> problems = validate(jpqlQuery);
		testDoesNotHaveProblem(problems, DatabaseType_MissingFirstExpression);
	}

	@Test
	public final void test_DatabaseType_MissingFirstExpression_3() throws Exception {

		String jpqlQuery = "Select cast(e.firstName char(2,)) from Employee e";
		List<JPQLQueryProblem> problems = validate(jpqlQuery);
		testDoesNotHaveProblem(problems, DatabaseType_MissingFirstExpression);
	}

	@Test
	public final void test_DatabaseType_MissingFirstExpression_4() throws Exception {

		String jpqlQuery = "Select cast(e.firstName char(, 2)) from Employee e";
		int startPosition = "Select cast(e.firstName char(".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(jpqlQuery);

		testHasOnlyOneProblem(
			problems,
			DatabaseType_MissingFirstExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_DatabaseType_MissingFirstExpression_5() throws Exception {

		String jpqlQuery = "Select cast(e.firstName char(,)) from Employee e";
		int startPosition = "Select cast(e.firstName char(".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(jpqlQuery);

		testHasProblem(
			problems,
			DatabaseType_MissingFirstExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_DatabaseType_MissingLeftParenthesis_1() throws Exception {

		String jpqlQuery = "Select cast(e.firstName char) from Employee e";
		List<JPQLQueryProblem> problems = validate(jpqlQuery);
		testDoesNotHaveProblem(problems, DatabaseType_MissingLeftParenthesis);
	}

	@Test
	public final void test_DatabaseType_MissingLeftParenthesis_2() throws Exception {

		String jpqlQuery = "Select cast(e.firstName char()) from Employee e";
		List<JPQLQueryProblem> problems = validate(jpqlQuery);
		testDoesNotHaveProblem(problems, DatabaseType_MissingLeftParenthesis);
	}

	@Test
	public final void test_DatabaseType_MissingLeftParenthesis_3() throws Exception {

		String jpqlQuery = "Select cast(e.firstName char(3)) from Employee e";
		List<JPQLQueryProblem> problems = validate(jpqlQuery);
		testDoesNotHaveProblem(problems, DatabaseType_MissingLeftParenthesis);
	}

	@Test
	public final void test_DatabaseType_MissingLeftParenthesis_4() throws Exception {

		String jpqlQuery = "Select cast(e.firstName char(3,)) from Employee e";
		List<JPQLQueryProblem> problems = validate(jpqlQuery);
		testDoesNotHaveProblem(problems, DatabaseType_MissingLeftParenthesis);
	}

	@Test
	public final void test_DatabaseType_MissingLeftParenthesis_5() throws Exception {

		String jpqlQuery = "Select cast(e.firstName char(3, )) from Employee e";
		List<JPQLQueryProblem> problems = validate(jpqlQuery, buildStringFormatter_4());
		testDoesNotHaveProblem(problems, DatabaseType_MissingLeftParenthesis);
	}

	@Test
	public final void test_DatabaseType_MissingLeftParenthesis_6() throws Exception {

		String jpqlQuery = "Select cast(e.firstName char(3, 3)) from Employee e";
		List<JPQLQueryProblem> problems = validate(jpqlQuery);
		testDoesNotHaveProblem(problems, DatabaseType_MissingLeftParenthesis);
	}

	@Test
	public final void test_DatabaseType_MissingLeftParenthesis_7() throws Exception {

		String jpqlQuery = "Select cast(e.firstName char 3)) from Employee e";
		int startPosition = "Select cast(e.firstName char".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(jpqlQuery);

		testHasOnlyOneProblem(
			problems,
			DatabaseType_MissingLeftParenthesis,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_DatabaseType_MissingLeftParenthesis_8() throws Exception {

		String jpqlQuery = "Select cast(e.firstName char 3, 3)) from Employee e";
		int startPosition = "Select cast(e.firstName char".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(jpqlQuery);

		testHasOnlyOneProblem(
			problems,
			DatabaseType_MissingLeftParenthesis,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_DatabaseType_MissingRightParenthesis_1() throws Exception {

		String jpqlQuery = "Select cast(e.firstName char) from Employee e";
		List<JPQLQueryProblem> problems = validate(jpqlQuery);
		testDoesNotHaveProblem(problems, DatabaseType_MissingRightParenthesis);
	}

	@Test
	public final void test_DatabaseType_MissingRightParenthesis_2() throws Exception {

		String jpqlQuery = "Select cast(e.firstName char()) from Employee e";
		List<JPQLQueryProblem> problems = validate(jpqlQuery);
		testDoesNotHaveProblem(problems, DatabaseType_MissingRightParenthesis);
	}

	@Test
	public final void test_DatabaseType_MissingRightParenthesis_3() throws Exception {

		String jpqlQuery  = "Select cast(e.firstName char(2 from Employee e";
		int startPosition = "Select cast(e.firstName char(2".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(jpqlQuery);

		testHasProblem(
			problems,
			DatabaseType_MissingRightParenthesis,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_DatabaseType_MissingRightParenthesis_4() throws Exception {

		String jpqlQuery = "Select cast(e.firstName char(2, from Employee e";
		int startPosition = "Select cast(e.firstName char(2, ".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(jpqlQuery);

		testHasProblem(
			problems,
			DatabaseType_MissingRightParenthesis,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_DatabaseType_MissingRightParenthesis_5() throws Exception {

		String jpqlQuery = "Select cast(e.firstName char(2, 2 from Employee e";
		int startPosition = "Select cast(e.firstName char(2, 2".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(jpqlQuery);

		testHasProblem(
			problems,
			DatabaseType_MissingRightParenthesis,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_DatabaseType_MissingSecondExpression_1() throws Exception {

		String jpqlQuery = "Select cast(e.firstName char) from Employee e";
		List<JPQLQueryProblem> problems = validate(jpqlQuery);
		testDoesNotHaveProblem(problems, DatabaseType_MissingFirstExpression);
	}

	@Test
	public final void test_DatabaseType_MissingSecondExpression_2() throws Exception {

		String jpqlQuery = "Select cast(e.firstName char(2, 2)) from Employee e";
		List<JPQLQueryProblem> problems = validate(jpqlQuery);
		testDoesNotHaveProblem(problems, DatabaseType_MissingFirstExpression);
	}

	@Test
	public final void test_DatabaseType_MissingSecondExpression_3() throws Exception {

		String jpqlQuery = "Select cast(e.firstName char(, 2)) from Employee e";
		int startPosition = "Select cast(e.firstName char(".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(jpqlQuery);

		testHasOnlyOneProblem(
			problems,
			DatabaseType_MissingFirstExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_DatabaseType_MissingSecondExpression_4() throws Exception {

		String jpqlQuery = "Select cast(e.firstName char(2,)) from Employee e";
		int startPosition = "Select cast(e.firstName char(2,".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(jpqlQuery);

		testHasOnlyOneProblem(
			problems,
			DatabaseType_MissingSecondExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_DatabaseType_MissingSecondExpression_5() throws Exception {

		String jpqlQuery = "Select cast(e.firstName char(2, )) from Employee e";
		int startPosition = "Select cast(e.firstName char(2, ".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(jpqlQuery, buildStringFormatter_4());

		testHasOnlyOneProblem(
			problems,
			DatabaseType_MissingSecondExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_DatabaseType_MissingSecondExpression_6() throws Exception {

		String jpqlQuery = "Select cast(e.firstName char(,)) from Employee e";
		int startPosition = "Select cast(e.firstName char(,".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(jpqlQuery);

		testHasProblem(
			problems,
			DatabaseType_MissingSecondExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_ExtractExpression_InvalidExpression_1() throws Exception {

		String jpqlQuery = "Select extract(DAY_MICROSECOND from e.hiringDate) from Employee e";
		List<JPQLQueryProblem> problems = validate(jpqlQuery);
		testDoesNotHaveProblem(problems, ExtractExpression_MissingExpression);
	}

	@Test
	public final void test_ExtractExpression_MissingDatePart_1() throws Exception {

		String jpqlQuery = "Select extract(DAY_MICROSECOND from e.hiringDate) from Employee e";
		List<JPQLQueryProblem> problems = validate(jpqlQuery);
		testDoesNotHaveProblem(problems, ExtractExpression_MissingDatePart);
	}

	@Test
	public final void test_ExtractExpression_MissingDatePart_2() throws Exception {

		String jpqlQuery = "Select extract(from e.hiringDate) from Employee e";
		int startPosition = "Select extract(".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(jpqlQuery);

		testHasOnlyOneProblem(
			problems,
			ExtractExpression_MissingDatePart,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_ExtractExpression_MissingDatePart_3() throws Exception {

		String jpqlQuery = "Select extract(e.hiringDate) from Employee e";
		int startPosition = "Select extract(".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(jpqlQuery);

		testHasOnlyOneProblem(
			problems,
			ExtractExpression_MissingDatePart,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_ExtractExpression_MissingDatePart_4() throws Exception {

		String jpqlQuery = "Select extract( e.hiringDate) from Employee e";
		int startPosition = "Select extract(".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(jpqlQuery, buildStringFormatter_5());

		testHasOnlyOneProblem(
			problems,
			ExtractExpression_MissingDatePart,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_ExtractExpression_MissingDatePart_5() throws Exception {

		String jpqlQuery = "Select extract( from e.hiringDate) from Employee e";
		int startPosition = "Select extract(".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(jpqlQuery);

		testHasOnlyOneProblem(
			problems,
			ExtractExpression_MissingDatePart,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_ExtractExpression_MissingExpression_1() throws Exception {

		String jpqlQuery = "Select extract(DAY_MICROSECOND e.hiringDate) from Employee e";
		List<JPQLQueryProblem> problems = validate(jpqlQuery);
		testDoesNotHaveProblem(problems, ExtractExpression_MissingExpression);
	}

	@Test
	public final void test_ExtractExpression_MissingExpression_2() throws Exception {

		String jpqlQuery = "Select extract(DAY_MICROSECOND from e.hiringDate) from Employee e";
		List<JPQLQueryProblem> problems = validate(jpqlQuery);
		testDoesNotHaveProblem(problems, ExtractExpression_MissingExpression);
	}

	@Test
	public final void test_ExtractExpression_MissingExpression_3() throws Exception {

		String jpqlQuery = "Select extract(DAY_MICROSECOND) from Employee e";
		int startPosition = "Select extract(DAY_MICROSECOND".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(jpqlQuery);

		testHasOnlyOneProblem(
			problems,
			ExtractExpression_MissingExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_ExtractExpression_MissingExpression_4() throws Exception {

		String jpqlQuery = "Select extract(DAY_MICROSECOND ) from Employee e";
		int startPosition = "Select extract(DAY_MICROSECOND ".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(jpqlQuery, buildStringFormatter_3());

		testHasOnlyOneProblem(
			problems,
			ExtractExpression_MissingExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_ExtractExpression_MissingExpression_5() throws Exception {

		String jpqlQuery = "Select extract(DAY_MICROSECOND from) from Employee e";
		int startPosition = "Select extract(DAY_MICROSECOND from".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(jpqlQuery);

		testHasOnlyOneProblem(
			problems,
			ExtractExpression_MissingExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_ExtractExpression_MissingExpression_6() throws Exception {

		String jpqlQuery = "Select extract(DAY_MICROSECOND from ) from Employee e";
		int startPosition = "Select extract(DAY_MICROSECOND from ".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(jpqlQuery, buildStringFormatter_3());

		testHasOnlyOneProblem(
			problems,
			ExtractExpression_MissingExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_ExtractExpression_MissingExpression_7() throws Exception {

		String jpqlQuery = "Select extract(DAY_MICROSECOND ) from Employee e";
		int startPosition = "Select extract(DAY_MICROSECOND ".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(jpqlQuery, buildStringFormatter_3());

		testHasProblem(
			problems,
			ExtractExpression_MissingExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_ExtractExpression_MissingExpression_8() throws Exception {

		String jpqlQuery = "Select extract(DAY_MICROSECOND from from Employee e";
		int startPosition = "Select extract(DAY_MICROSECOND from ".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(jpqlQuery);

		testHasProblem(
			problems,
			ExtractExpression_MissingExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_ExtractExpression_MissingLeftParenthesis_1() throws Exception {

		String jpqlQuery = "Select extract(DAY_MICROSECOND from e.hiringDate) from Employee e";
		List<JPQLQueryProblem> problems = validate(jpqlQuery);
		testDoesNotHaveProblem(problems, ExtractExpression_MissingLeftParenthesis);
	}

	@Test
	public final void test_ExtractExpression_MissingLeftParenthesis_2() throws Exception {

		String jpqlQuery  = "Select e from Employee e where extract";
		int startPosition = "Select e from Employee e where extract".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(jpqlQuery);

		testHasProblem(
			problems,
			ExtractExpression_MissingLeftParenthesis,
			startPosition,
			endPosition
		);

		testDoesNotHaveProblem(problems, ExtractExpression_MissingDatePart);
		testDoesNotHaveProblem(problems, ExtractExpression_MissingExpression);
		testDoesNotHaveProblem(problems, ExtractExpression_MissingRightParenthesis);
	}

	@Test
	public final void test_ExtractExpression_MissingLeftParenthesis_3() throws Exception {

		String jpqlQuery = "Select extract DAY_MICROSECOND from e.hiringDate) from Employee e";
		int startPosition = "Select extract".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(jpqlQuery);

		testHasOnlyOneProblem(
			problems,
			ExtractExpression_MissingLeftParenthesis,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_ExtractExpression_MissingLeftParenthesis_4() throws Exception {

		String jpqlQuery = "Select extract from from Employee e";
		int startPosition = "Select extract".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(jpqlQuery);

		testHasProblem(
			problems,
			ExtractExpression_MissingLeftParenthesis,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_ExtractExpression_MissingRightParenthesis_1() throws Exception {

		String jpqlQuery = "Select extract(DAY_MICROSECOND e.hiringDate) from Employee e";
		List<JPQLQueryProblem> problems = validate(jpqlQuery);
		testDoesNotHaveProblem(problems, ExtractExpression_MissingRightParenthesis);
	}

	@Test
	public final void test_ExtractExpression_MissingRightParenthesis_2() throws Exception {

		String jpqlQuery = "Select extract(DAY_MICROSECOND from e.hiringDate) from Employee e";
		List<JPQLQueryProblem> problems = validate(jpqlQuery);
		testDoesNotHaveProblem(problems, ExtractExpression_MissingRightParenthesis);
	}

	@Test
	public final void test_ExtractExpression_MissingRightParenthesis_3() throws Exception {

		String jpqlQuery  = "Select extract(DAY_MICROSECOND from e.hiringDate from Employee e";
		int startPosition = "Select extract(DAY_MICROSECOND from e.hiringDate".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(jpqlQuery);

		testHasOnlyOneProblem(
			problems,
			ExtractExpression_MissingRightParenthesis,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_ExtractExpression_MissingRightParenthesis_4() throws Exception {

		String jpqlQuery = "Select extract(DAY_MICROSECOND e.hiringDate from Employee e";
		int startPosition = "Select extract(DAY_MICROSECOND e.hiringDate".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(jpqlQuery);

		testHasOnlyOneProblem(
			problems,
			ExtractExpression_MissingRightParenthesis,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_FunctionExpression_MissingFunctionName_1() throws Exception {

		String jpqlQuery = "SELECT FUNCTION() FROM Employee e";
		int startPosition = "SELECT FUNCTION(".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(jpqlQuery);

		testHasOnlyOneProblem(
			problems,
			FunctionExpression_MissingFunctionName,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_FunctionExpression_MissingFunctionName_2() throws Exception {

		String jpqlQuery = "SELECT FUNCTION('sql') FROM Employee e";
		List<JPQLQueryProblem> problems = validate(jpqlQuery);
		testDoesNotHaveProblem(problems, FunctionExpression_MissingFunctionName);
	}

	@Test
	public final void test_FunctionExpression_MissingLeftParenthesis_1() throws Exception {

		String jpqlQuery = "SELECT FUNCTION 'getName', 'String') FROM Employee e";
		int startPosition = "SELECT FUNCTION".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(jpqlQuery);

		testHasOnlyOneProblem(
			problems,
			FunctionExpression_MissingLeftParenthesis,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_FunctionExpression_MissingLeftParenthesis_2() throws Exception {

		String jpqlQuery = "SELECT FUNCTION('getName', 'String') FROM Employee e";
		List<JPQLQueryProblem> problems = validate(jpqlQuery);
		testDoesNotHaveProblem(problems, FunctionExpression_MissingLeftParenthesis);
	}

	@Test
	public final void test_FunctionExpression_MissingOneExpression_1() throws Exception {

		String jpqlQuery = "select e from Employee e where column('city', e.address) = 'Ottawa'";
		List<JPQLQueryProblem> problems = validate(jpqlQuery);

		testDoesNotHaveProblem(
			problems,
			FunctionExpression_MissingOneExpression
		);
	}

	@Test
	public final void test_FunctionExpression_MissingOneExpression_2() throws Exception {

		String jpqlQuery = "select e from Employee e where column('city') = 'Ottawa'";
		int startPosition = "select e from Employee e where column('city'".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(jpqlQuery);

		testHasOnlyOneProblem(
			problems,
			FunctionExpression_MissingOneExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_FunctionExpression_MissingOneExpression_3() throws Exception {

		String jpqlQuery = "select e from Employee e where column('city',) = 'Ottawa'";
		int startPosition = "select e from Employee e where column('city',".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(jpqlQuery);

		testHasOnlyOneProblem(
			problems,
			FunctionExpression_MissingOneExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_FunctionExpression_MissingOneExpression_4() throws Exception {

		String jpqlQuery = "select e from Employee e where column('city', ) = 'Ottawa'";
		int startPosition = "select e from Employee e where column('city', ".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(jpqlQuery, buildStringFormatter_1());

		testHasOnlyOneProblem(
			problems,
			FunctionExpression_MissingOneExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_FunctionExpression_MissingRightParenthesis_1() throws Exception {

		String jpqlQuery = "SELECT FUNCTION('getName', 'String' FROM Employee e";
		int startPosition = "SELECT FUNCTION('getName', 'String'".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(jpqlQuery);

		testHasOnlyOneProblem(
			problems,
			FunctionExpression_MissingRightParenthesis,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_FunctionExpression_MissingRightParenthesis_2() throws Exception {

		String jpqlQuery = "SELECT FUNCTION('getName', 'String') FROM Employee e";
		List<JPQLQueryProblem> problems = validate(jpqlQuery);
		testDoesNotHaveProblem(problems, FunctionExpression_MissingRightParenthesis);
	}

	@Test
	public final void test_FunctionExpression_MoreThanOneExpression_1() throws Exception {

		String jpqlQuery = "SELECT COLUMN('city', e.name, e.id) FROM Employee e";
		int startPosition = "SELECT COLUMN('city', ".length();
		int endPosition   = "SELECT COLUMN('city', e.name, e.id".length();

		List<JPQLQueryProblem> problems = validate(jpqlQuery);

		testHasOnlyOneProblem(
			problems,
			FunctionExpression_MoreThanOneExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_OnClause_InvalidConditionalExpression_1() throws Exception {

		String jpqlQuery = "select e from Employee e join e.address a on a.id > 2";
		List<JPQLQueryProblem> problems = validate(jpqlQuery);
		testDoesNotHaveProblem(problems, OnClause_InvalidConditionalExpression);
	}

	@Test
	public final void test_OnClause_InvalidConditionalExpression_2() throws Exception {

		String jpqlQuery = "select e from Employee e join e.address a on a.id";
		int startPosition = "select e from Employee e join e.address a on ".length();
		int endPosition   = jpqlQuery.length();

		List<JPQLQueryProblem> problems = validate(jpqlQuery);

		testHasOnlyOneProblem(
			problems,
			OnClause_InvalidConditionalExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_OnClause_MissingConditionalExpression_1() throws Exception {

		String jpqlQuery = "select e from Employee e join e.address a on";
		int startPosition = "select e from Employee e join e.address a on".length();
		int endPosition   = jpqlQuery.length();

		List<JPQLQueryProblem> problems = validate(jpqlQuery);

		testHasOnlyOneProblem(
			problems,
			OnClause_MissingConditionalExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_OnClause_MissingConditionalExpression_2() throws Exception {

		String jpqlQuery = "select e from Employee e join e.address a on where e.id > 2";
		int startPosition = "select e from Employee e join e.address a on ".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(jpqlQuery);

		testHasOnlyOneProblem(
			problems,
			OnClause_MissingConditionalExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_OnClause_MissingConditionalExpression_3() throws Exception {

		String jpqlQuery = "select e from Employee e join e.address a on e.id > 2";
		List<JPQLQueryProblem> problems = validate(jpqlQuery);
		testDoesNotHaveProblem(problems, OnClause_MissingConditionalExpression);
	}

	@Test
	public final void test_RegexpExpression_InvalidPatternValue_1() throws Exception {

		String jpqlQuery = "Select e from Employee e where e.firstName regexp :regexp'";
		List<JPQLQueryProblem> problems = validate(jpqlQuery);
		testHasNoProblems(problems);
	}

	@Test
	public final void test_RegexpExpression_InvalidPatternValue_2() throws Exception {

		String jpqlQuery = "Select e from Employee e where e.firstName regexp '^B.*'";
		List<JPQLQueryProblem> problems = validate(jpqlQuery);
		testHasNoProblems(problems);
	}

	@Test
	public final void test_RegexpExpression_InvalidPatternValue_3() throws Exception {

		// TODO
	}

	@Test
	public final void test_RegexpExpression_InvalidStringExpression_2() throws Exception {

		String jpqlQuery  = "Select e from Employee e where LENGTH(e.name) regexp '^B.*'";
		int startPosition = "Select e from Employee e where ".length();
		int endPosition   = "Select e from Employee e where LENGTH(e.name)".length();

		List<JPQLQueryProblem> problems = validate(jpqlQuery);

		testHasOnlyOneProblem(
			problems,
			RegexpExpression_InvalidStringExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_RegexpExpression_MissingPatternValue_1() throws Exception {

		String jpqlQuery = "Select e from Employee e where e.firstName regexp '^B.*'";
		List<JPQLQueryProblem> problems = validate(jpqlQuery);
		testHasNoProblems(problems);
	}

	@Test
	public final void test_RegexpExpression_MissingPatternValue_2() throws Exception {

		String jpqlQuery  = "Select e from Employee e where e.name regexp";
		int startPosition = "Select e from Employee e where e.name regexp".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(jpqlQuery);

		testHasOnlyOneProblem(
			problems,
			RegexpExpression_MissingPatternValue,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_RegexpExpression_MissingPatternValue_3() throws Exception {

		String jpqlQuery  = "Select e from Employee e where e.name regexp group by e.name";
		int startPosition = "Select e from Employee e where e.name regexp ".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(jpqlQuery);

		testHasOnlyOneProblem(
			problems,
			RegexpExpression_MissingPatternValue,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_RegexpExpression_MissingStringExpression_1() throws Exception {

		String jpqlQuery = "Select e from Employee e where e.firstName regexp '^B.*'";
		List<JPQLQueryProblem> problems = validate(jpqlQuery);
		testHasNoProblems(problems);
	}

	@Test
	public final void test_RegexpExpression_MissingStringExpression_2() throws Exception {

		String jpqlQuery  = "Select e from Employee e where regexp '^B.*'";
		int startPosition = "Select e from Employee e where ".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(jpqlQuery);

		testHasOnlyOneProblem(
			problems,
			RegexpExpression_MissingStringExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_UnionClause_MissingExpression_1() throws Exception {

		String jpqlQuery = "select e from Employee e intersect all select p from Product p where p.id <> 2";
		List<JPQLQueryProblem> problems = validate(jpqlQuery);
		testHasNoProblems(problems);
	}

	@Test
	public final void test_UnionClause_MissingExpression_2() throws Exception {

		String jpqlQuery = "select e from Employee e intersect";
		int startPosition = "select e from Employee e intersect".length();
		int endPosition   = jpqlQuery.length();

		List<JPQLQueryProblem> problems = validate(jpqlQuery);

		testHasOnlyOneProblem(
			problems,
			UnionClause_MissingExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_UnionClause_MissingExpression_3() throws Exception {

		String jpqlQuery = "select e from Employee e intersect ";
		int startPosition = "select e from Employee e intersect ".length();
		int endPosition   = jpqlQuery.length();

		List<JPQLQueryProblem> problems = validate(jpqlQuery);

		testHasOnlyOneProblem(
			problems,
			UnionClause_MissingExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_UnionClause_MissingExpression_4() throws Exception {

		String jpqlQuery = "select e from Employee e intersect all";
		int startPosition = "select e from Employee e intersect all".length();
		int endPosition   = jpqlQuery.length();

		List<JPQLQueryProblem> problems = validate(jpqlQuery);

		testHasOnlyOneProblem(
			problems,
			UnionClause_MissingExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public final void test_UnionClause_MissingExpression_5() throws Exception {

		String jpqlQuery = "select e from Employee e intersect all ";
		int startPosition = "select e from Employee e intersect all ".length();
		int endPosition   = jpqlQuery.length();

		List<JPQLQueryProblem> problems = validate(jpqlQuery);

		testHasOnlyOneProblem(
			problems,
			UnionClause_MissingExpression,
			startPosition,
			endPosition
		);
	}
}