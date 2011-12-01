/*******************************************************************************
 * Copyright (c) 2006, 2011 Oracle. All rights reserved.
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
import org.eclipse.persistence.jpa.jpql.AbstractJPQLQueryHelper;
import org.eclipse.persistence.jpa.jpql.ExpressionTools;
import org.eclipse.persistence.jpa.jpql.JPQLQueryProblem;
import org.eclipse.persistence.jpa.jpql.JPQLQueryProblemMessages;
import org.eclipse.persistence.jpa.jpql.parser.Expression;
import org.eclipse.persistence.jpa.tests.jpql.parser.JPQLQueryStringFormatter;
import org.junit.Test;

/**
 * The unit-tests testing {@link AbstractGrammarValidator}.
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public abstract class AbstractGrammarValidatorTest extends AbstractValidatorTest {

	private JPQLQueryStringFormatter buildFormatter_01() {
		return new JPQLQueryStringFormatter() {
			public String format(String query) {
				return query.replace(",,", ", ,");
			}
		};
	}

	private JPQLQueryStringFormatter buildFormatter_1() throws Exception {
		return new JPQLQueryStringFormatter() {
			public String format(String query) {
				return query.replace("SELECT)", "SELECT )");
			}
		};
	}

	private JPQLQueryStringFormatter buildFormatter_2() throws Exception {
		return new JPQLQueryStringFormatter() {
			public String format(String query) {
				return query.replace("(DISTINCT)", "(DISTINCT )");
			}
		};
	}

	private JPQLQueryStringFormatter buildFormatter_4() throws Exception {
		return new JPQLQueryStringFormatter() {
			public String format(String query) {
				return query.replace(",)", ", )");
			}
		};
	}

	private JPQLQueryStringFormatter buildFormatter_5() throws Exception {
		return new JPQLQueryStringFormatter() {
			public String format(String query) {
				return query.replace(",)", ", )");
			}
		};
	}

	private JPQLQueryStringFormatter buildFormatter_6() throws Exception {
		return new JPQLQueryStringFormatter() {
			public String format(String query) {
				return query.replace(",)", ", )");
			}
		};
	}

	@Test
	public void test_AbsExpression_InvalidExpression() throws Exception {

		String query = "SELECT ABS(SELECT o FROM Order o) FROM Employee e";
		int startPosition = "SELECT ABS(".length();
		int endPosition   = "SELECT ABS(SELECT o FROM Order o".length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.AbsExpression_InvalidExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_AbsExpression_MissingExpression() throws Exception {

		String query = "SELECT ABS() FROM Employee e";
		int startPosition = "SELECT ABS(".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.AbsExpression_MissingExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_AbsExpression_MissingLeftParenthesis() throws Exception {

		String query = "SELECT ABS 4 + 5) FROM Employee e";
		int startPosition = "SELECT ABS".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.AbsExpression_MissingLeftParenthesis,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_AbsExpression_MissingRightParenthesis() throws Exception {

		String query = "SELECT ABS(e.age + 100 FROM Employee e";
		int startPosition = "SELECT ABS(e.age + 100".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.AbsExpression_MissingRightParenthesis,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_AbstractFromClause_IdentificationVariableDeclarationEndWithComma_1() throws Exception {

		String query = "SELECT e FROM Employee e JOIN e.managers m,";

		int startPosition = "SELECT e FROM Employee e JOIN e.managers m".length();
		int endPosition   = startPosition + 1;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.AbstractFromClause_IdentificationVariableDeclarationEndsWithComma,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_AbstractFromClause_IdentificationVariableDeclarationEndWithComma_2() throws Exception {

		String query = "SELECT e FROM Employee e WHERE EXISTS(SELECT o FROM Order o,)";

		int startPosition = "SELECT e FROM Employee e WHERE EXISTS(SELECT o FROM Order o".length();
		int endPosition   = startPosition + 1;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.AbstractFromClause_IdentificationVariableDeclarationEndsWithComma,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_AbstractFromClause_IdentificationVariableDeclarationIsMissingComma() throws Exception {

		String query = "SELECT e FROM Employee e Address a";

		int startPosition = "SELECT e FROM Employee e".length();
		int endPosition   = startPosition + 1;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.AbstractFromClause_IdentificationVariableDeclarationIsMissingComma,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_AbstractFromClause_MissingIdentificationVariableDeclaration_1() throws Exception {

		String query = "SELECT e FROM";

		int startPosition = query.length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.AbstractFromClause_MissingIdentificationVariableDeclaration,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_AbstractFromClause_MissingIdentificationVariableDeclaration_2() throws Exception {

		String query = "SELECT e FROM ";

		int startPosition = query.length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.AbstractFromClause_MissingIdentificationVariableDeclaration,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_AbstractFromClause_MissingIdentificationVariableDeclaration_3() throws Exception {

		String query = "SELECT e FROM WHERE e.name = 'JPQL'";

		int startPosition = "SELECT e FROM ".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.AbstractFromClause_MissingIdentificationVariableDeclaration,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_AbstractFromClause_MissingIdentificationVariableDeclaration_4() throws Exception {

		String query = "SELECT e FROM Employee e, Order o WHERE EXISTS(SELECT o FROM)";

		int startPosition = "SELECT e FROM Employee e, Order o WHERE EXISTS(SELECT o FROM".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.AbstractFromClause_MissingIdentificationVariableDeclaration,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_AbstractFromClause_MissingIdentificationVariableDeclaration_5() throws Exception {

		String query = "SELECT e FROM Employee e WHERE EXISTS(SELECT o FROM WHERE o.price > 2000)";

		int startPosition = "SELECT e FROM Employee e WHERE EXISTS(SELECT o FROM ".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.AbstractFromClause_MissingIdentificationVariableDeclaration,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_AbstractFromClause_WrongOrderOfIdentificationVariableDeclaration() throws Exception {

		String query = "SELECT e FROM Employee e JOIN a.people p, Address a";

		int startPosition = "SELECT e FROM Employee e JOIN ".length();
		int endPosition   = startPosition + 1;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.AbstractFromClause_WrongOrderOfIdentificationVariableDeclaration,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_AbstractPathExpression_CannotEndWithComma() throws Exception {

		String query = "SELECT e. FROM Employee e";

		int startPosition = "SELECT ".length();
		int endPosition   = "SELECT e.".length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.AbstractPathExpression_CannotEndWithComma,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_AbstractPathExpression_MissingIdentificationVariable() throws Exception {

		String query = "SELECT .name FROM Employee e";

		int startPosition = "SELECT ".length();
		int endPosition   = "SELECT .name".length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.AbstractPathExpression_MissingIdentificationVariable,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_AbstractSelectClause_SelectExpressionEndsWithComma_1() throws Exception {

		String query = "SELECT e, AVG(e.age), FROM Employee e";
		int startPosition = "SELECT e, AVG(e.age)".length();
		int endPosition   = startPosition + 1;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.AbstractSelectClause_SelectExpressionEndsWithComma,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_AbstractSelectClause_SelectExpressionIsMissingComma_1() throws Exception {

		String query = "SELECT e AVG(e.age) FROM Employee e";
		int startPosition = "SELECT e".length();
		int endPosition   = startPosition + 1;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.AbstractSelectClause_SelectExpressionIsMissingComma,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_AbstractSelectClause_SelectExpressionIsMissingComma_2() throws Exception {

		String query = "SELECT e, AVG(e.age) e.name FROM Employee e";
		int startPosition = "SELECT e, AVG(e.age)".length();
		int endPosition   = startPosition + 1;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.AbstractSelectClause_SelectExpressionIsMissingComma,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_AbstractSelectClause_SelectExpressionMissing_1() throws Exception {

		String query = "SELECT";
		int startPosition = query.length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			JPQLQueryProblemMessages.AbstractSelectClause_SelectExpressionMissing,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_AbstractSelectClause_SelectExpressionMissing_2() throws Exception {

		String query = "SELECT ";
		int startPosition = query.length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			JPQLQueryProblemMessages.AbstractSelectClause_SelectExpressionMissing,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_AbstractSelectClause_SelectExpressionMissing_3() throws Exception {

		String query = "SELECT FROM Employee e";
		int startPosition = "SELECT ".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.AbstractSelectClause_SelectExpressionMissing,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_AbstractSelectClause_SelectExpressionMissing_4() throws Exception {

		String query = "SELECT e FROM Employee e WHERE EXISTS(SELECT )";
		int startPosition = "SELECT e FROM Employee e WHERE EXISTS(SELECT ".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query, buildFormatter_1());

		testHasProblem(
			problems,
			JPQLQueryProblemMessages.AbstractSelectClause_SelectExpressionMissing,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_AbstractSelectClause_SelectExpressionMissing_5() throws Exception {

		String query = "SELECT e FROM Employee e WHERE EXISTS(SELECT FROM Order o)";
		int startPosition = "SELECT e FROM Employee e WHERE EXISTS(SELECT ".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.AbstractSelectClause_SelectExpressionMissing,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_AbstractSelectStatement_FromClauseMissing_1() throws Exception {

		String query = "SELECT e";
		int startPosition = query.length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.AbstractSelectStatement_FromClauseMissing,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_AbstractSelectStatement_FromClauseMissing_2() throws Exception {

		String query = "SELECT e WHERE e.name = 'JPQL'";
		int startPosition = "SELECT e ".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.AbstractSelectStatement_FromClauseMissing,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_AbstractSelectStatement_FromClauseMissing_3() throws Exception {

		String query = "SELECT e FROM Employee e WHERE ALL(SELECT E WHERE e.name = 'JPQL') < e";
		int startPosition = "SELECT e FROM Employee e WHERE ALL(SELECT E ".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.AbstractSelectStatement_FromClauseMissing,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_AllOrAnyExpression_InvalidExpression() throws Exception {

		String query = "SELECT e FROM Employee e WHERE SOME(AVG(e.name)) = TRUE";
		int startPosition = "SELECT e FROM Employee e WHERE SOME(".length();
		int endPosition   = "SELECT e FROM Employee e WHERE SOME(AVG(e.name)".length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.AllOrAnyExpression_InvalidExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_AllOrAnyExpression_MissingExpression() throws Exception {

		String query = "SELECT e FROM Employee e JOIN e.addresses a WHERE ALL() = TRUE";
		int startPosition = "SELECT e FROM Employee e JOIN e.addresses a WHERE ALL(".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.AllOrAnyExpression_MissingExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_AllOrAnyExpression_MissingLeftParenthesis() throws Exception {

		String query = "SELECT e FROM Employee e JOIN e.addresses a WHERE SOME SELECT p FROM Order p) = TRUE";
		int startPosition = "SELECT e FROM Employee e JOIN e.addresses a WHERE SOME".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.AllOrAnyExpression_MissingLeftParenthesis,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_AllOrAnyExpression_MissingRightParenthesis() throws Exception {

		String query = "SELECT e FROM Employee e JOIN e.addresses a WHERE SOME(SELECT p FROM Order p = TRUE";
		int startPosition = "SELECT e FROM Employee e JOIN e.addresses a WHERE SOME(SELECT p FROM Order p".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.AllOrAnyExpression_MissingRightParenthesis,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_AllOrAnyExpression_NotPartOfComparisonExpression() throws Exception {

		String query = "SELECT e FROM Employee e WHERE ALL(SELECT a FROM Address a)";

		int startPosition = "SELECT e FROM Employee e WHERE ".length();
		int endPosition   = query.length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			JPQLQueryProblemMessages.AllOrAnyExpression_NotPartOfComparisonExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_ArithmeticExpression_InvalidLeftExpression() throws Exception {

		String query = "SELECT e FROM Employee e WHERE LOWER(e.name) + LENGTH(e.name)";

		int startPosition = "SELECT e FROM Employee e WHERE ".length();
		int endPosition   = "SELECT e FROM Employee e WHERE LOWER(e.name)".length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			JPQLQueryProblemMessages.ArithmeticExpression_InvalidLeftExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_ArithmeticExpression_InvalidRightExpression() throws Exception {

		String query = "SELECT e FROM Employee e WHERE LENGTH(e.name) + LOWER(e.name)";

		int startPosition = "SELECT e FROM Employee e WHERE LENGTH(e.name) + ".length();
		int endPosition   = query.length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			JPQLQueryProblemMessages.ArithmeticExpression_InvalidRightExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_ArithmeticExpression_MissingRightExpression_1() throws Exception {

		String query = "SELECT e FROM Employee e WHERE LENGTH(e.name) +";

		int startPosition = query.length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			JPQLQueryProblemMessages.ArithmeticExpression_MissingRightExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_ArithmeticExpression_MissingRightExpression_2() throws Exception {

		String query = "SELECT e FROM Employee e WHERE LENGTH(e.name) + ";

		int startPosition = query.length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			JPQLQueryProblemMessages.ArithmeticExpression_MissingRightExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_ArithmeticFactor_MissingExpression_1() throws Exception {

		String query = "SELECT e FROM Employee e WHERE -";
		int startPosition = "SELECT e FROM Employee e WHERE -".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			JPQLQueryProblemMessages.ArithmeticFactor_MissingExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_ArithmeticFactor_MissingExpression_2() throws Exception {

		String query = "SELECT e FROM Employee e WHERE e.age + -";
		int startPosition = "SELECT e FROM Employee e WHERE e.age + -".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			JPQLQueryProblemMessages.ArithmeticFactor_MissingExpression,
			startPosition,
			endPosition
		);
	}

//	@Test
//	public void test_ArithmeticExpression_MissingLeftExpression()
//	{
//		String query = "SELECT e FROM Employee e WHERE + LENGTH(e.name)";
//
//		int startPosition = "SELECT e FROM Employee e WHERE ".length();
//		int endPosition   = startPosition;
//
//		List<QueryProblem> problems = validate(query);
//
//		testHasOnlyOneProblem
//		(
//			problems,
//			QueryProblemMessages.ArithmeticExpression_MissingLeftExpression,
//			startPosition,
//			endPosition
//		);
//	}

	@Test
	public void test_AvgFunction_InvalidExpression() throws Exception {

		String query = "SELECT AVG(e) FROM Employee e";
		int startPosition = "SELECT AVG(".length();
		int endPosition   = "SELECT AVG(e".length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.AvgFunction_InvalidExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_AvgFunction_MissingExpression_1() throws Exception {

		String query = "SELECT AVG() FROM Employee e";
		int startPosition = "SELECT AVG(".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.AvgFunction_MissingExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_AvgFunction_MissingExpression_2() throws Exception {

		String query = "SELECT AVG(DISTINCT) FROM Employee e";
		int startPosition = "SELECT AVG(DISTINCT".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.AvgFunction_MissingExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_AvgFunction_MissingExpression_3() throws Exception {

		String query = "SELECT AVG(DISTINCT ) FROM Employee e";
		int startPosition = "SELECT AVG(DISTINCT ".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query, buildFormatter_2());

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.AvgFunction_MissingExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_AvgFunction_MissingLeftParenthesis_1() throws Exception {

		String query = "SELECT AVG e.age) FROM Employee e";
		int startPosition = "SELECT AVG".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.AvgFunction_MissingLeftParenthesis,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_AvgFunction_MissingLeftParenthesis_2() throws Exception {

		String query = "SELECT AVG DISTINCT e.age) FROM Employee e";
		int startPosition = "SELECT AVG".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.AvgFunction_MissingLeftParenthesis,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_AvgFunction_MissingRightParenthesis() throws Exception {

		String query = "SELECT AVG(DISTINCT e.age FROM Employee e";

		int startPosition = "SELECT AVG(DISTINCT e.age".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.AvgFunction_MissingRightParenthesis,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_BetweenExpression_MissingAnd_1() throws Exception {

		String query = "SELECT e FROM Employee e WHERE e.age BETWEEN 20";

		int startPosition = query.length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.BetweenExpression_MissingAnd,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_BetweenExpression_MissingAnd_2() throws Exception {

		String query = "SELECT e FROM Employee e WHERE e.age BETWEEN 20 ";

		int startPosition = query.length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.BetweenExpression_MissingAnd,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_BetweenExpression_MissingAnd_3() throws Exception {

		String query = "SELECT e FROM Employee e WHERE e.age BETWEEN 20 40";

		int startPosition = "SELECT e FROM Employee e WHERE e.age BETWEEN 20 ".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.BetweenExpression_MissingAnd,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_BetweenExpression_MissingExpression() throws Exception {

		String query = "SELECT e FROM Employee e WHERE BETWEEN 20 AND 40";

		int startPosition = "SELECT e FROM Employee e WHERE ".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.BetweenExpression_MissingExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_BetweenExpression_MissingLowerBoundExpression() throws Exception {

		String query = "SELECT e FROM Employee e WHERE e.age BETWEEN AND 40";

		int startPosition = "SELECT e FROM Employee e WHERE e.age BETWEEN ".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.BetweenExpression_MissingLowerBoundExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_BetweenExpression_MissingUpperBoundExpression_1() throws Exception {

		String query = "SELECT e FROM Employee e WHERE e.age BETWEEN 20 AND";

		int startPosition = query.length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.BetweenExpression_MissingUpperBoundExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_BetweenExpression_MissingUpperBoundExpression_2() throws Exception {

		String query = "SELECT e FROM Employee e WHERE e.age BETWEEN 20 AND ";

		int startPosition = query.length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.BetweenExpression_MissingUpperBoundExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_CaseExpression_MissingElseExpression_1() throws Exception {

		String query = "UPDATE Employee e " +
		               "SET e.salary = " +
		               "    CASE WHEN e.rating = 1 THEN e.salary * 1.1 " +
		               "         WHEN e.rating = 2 THEN e.salary * 1.05 " +
		               "         ELSE " +
		               "    END";

		List<JPQLQueryProblem> problems = validate(query);

		query = query.replaceAll("\\s+", " ");

		int startPosition = query.length() - Expression.END.length();
		int endPosition   = startPosition;

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.CaseExpression_MissingElseExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_CaseExpression_MissingElseExpression_2() throws Exception {

		String query = "SELECT e.name," +
		               "       CASE TYPE(e) WHEN Exempt THEN 'Exempt'" +
		               "                    WHEN Contractor THEN 'Contractor'" +
		               "                    WHEN Intern THEN 'Intern'" +
		               "                    ELSE " +
		               "       END " +
		               "FROM Employee e";

		query = query.replaceAll("\\s+", " ");

		int startPosition = query.length() - "END FROM Employee e".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.CaseExpression_MissingElseExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_CaseExpression_MissingElseIdentifier_1() throws Exception {

		String query = "UPDATE Employee e " +
		               "SET e.salary = " +
		               "    CASE WHEN e.rating = 1 THEN e.salary * 1.1 " +
		               "         WHEN e.rating = 2 THEN e.salary * 1.05" +
		               "    END";

		query = query.replaceAll("\\s+", " ");

		int startPosition = query.length() - Expression.END.length();
		int endPosition = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.CaseExpression_MissingElseIdentifier,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_CaseExpression_MissingElseIdentifier_2() throws Exception {

		String query = "UPDATE Employee e " +
		               "SET e.salary = " +
		               "    CASE WHEN e.rating = 1 THEN e.salary * 1.1 " +
		               "         WHEN e.rating = 2 THEN e.salary * 1.05 " +
		               "    END";

		query = query.replaceAll("\\s+", " ");

		int startPosition = query.length() - Expression.END.length();
		int endPosition = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.CaseExpression_MissingElseIdentifier,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_CaseExpression_MissingEndIdentifier() throws Exception {

		String query = "UPDATE Employee e " +
		               "SET e.salary = " +
		               "    CASE WHEN e.rating = 1 THEN e.salary * 1.1 " +
		               "         WHEN e.rating = 2 THEN e.salary * 1.05 " +
		               "         ELSE e.salary * 1.01";

		query = query.replaceAll("\\s+", " ");

		int startPosition = query.length();
		int endPosition = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.CaseExpression_MissingEndIdentifier,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_CaseExpression_MissingWhenClause() throws Exception {

		String query = "UPDATE Employee e " +
		               "SET e.salary = " +
		               "    CASE TYPE(e)" +
		               "    ELSE 'NonExempt'" +
		               "    END";

		query = query.replaceAll("\\s+", " ");

		int startPosition = "UPDATE Employee e SET e.salary = CASE TYPE(e) ".length();
		int endPosition = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.CaseExpression_MissingWhenClause,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_CoalesceExpression_InvalidExpression() throws Exception {

		String query = "SELECT COALESCE(SELECT e) FROM Employee e";
		int startPosition = "SELECT COALESCE(".length();
		int endPosition   = "SELECT COALESCE(SELECT e".length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.CoalesceExpression_InvalidExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_CoalesceExpression_MissingExpression() throws Exception {

		String query = "SELECT COALESCE() FROM Employee e";
		int startPosition = "SELECT COALESCE(".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.CoalesceExpression_MissingExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_CoalesceExpression_MissingLeftParenthesis() throws Exception {

		String query = "SELECT COALESCE 4 + 5) FROM Employee e";
		int startPosition = "SELECT COALESCE".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.CoalesceExpression_MissingLeftParenthesis,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_CoalesceExpression_MissingRightParenthesis() throws Exception {

		String query = "SELECT COALESCE(e.age + 100 FROM Employee e";
		int startPosition = "SELECT COALESCE(e.age + 100".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.CoalesceExpression_MissingRightParenthesis,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_CollectionMemberDeclaration_MissingCollectionValuedPathExpression() throws Exception {

		String query = "SELECT e FROM Employee e, IN() AS m";

		int startPosition = "SELECT e FROM Employee e, IN(".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.CollectionMemberDeclaration_MissingCollectionValuedPathExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_CollectionMemberDeclaration_MissingIdentificationVariable_1() throws Exception {

		String query = "SELECT e FROM Employee e, IN(e.employees) AS";

		int startPosition = query.length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.CollectionMemberDeclaration_MissingIdentificationVariable,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_CollectionMemberDeclaration_MissingIdentificationVariable_2() throws Exception {

		String query = "SELECT e FROM Employee e, IN(e.employees) AS ";

		int startPosition = query.length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.CollectionMemberDeclaration_MissingIdentificationVariable,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_CollectionMemberDeclaration_MissingLeftParenthesis() throws Exception {

		String query = "SELECT e FROM Employee e, IN e.managers) AS m";

		int startPosition = "SELECT e FROM Employee e, IN".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.CollectionMemberDeclaration_MissingLeftParenthesis,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_CollectionMemberDeclaration_MissingRightParenthesis() throws Exception {

		String query = "SELECT e FROM Employee e, IN(e.employees AS m";

		int startPosition = "SELECT e FROM Employee e, IN(e.employees".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.CollectionMemberDeclaration_MissingRightParenthesis,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_CollectionMemberExpression_MissingCollectionValuedPathExpression() throws Exception {

		String query = "SELECT e, f FROM Employee e, e.employees f WHERE e.name MEMBER OF ";

		int startPosition = "SELECT e, f FROM Employee e, e.employees f WHERE e.name MEMBER OF ".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.CollectionMemberExpression_MissingCollectionValuedPathExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_CollectionMemberExpression_MissingEntityExpression() throws Exception {

		String query = "SELECT e, f FROM Employee e, e.employees f WHERE MEMBER f.offices";

		int startPosition = "SELECT e, f FROM Employee e, e.employees f WHERE ".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.CollectionMemberExpression_MissingEntityExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_ComparisonExpression_MissingLeftExpression() throws Exception {

		String query = "SELECT e FROM EMPLOYEE e WHERE > 20";

		int startPosition = "SELECT e FROM EMPLOYEE e WHERE ".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.ComparisonExpression_MissingLeftExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_ComparisonExpression_MissingLEftRightExpression() throws Exception {

		String query = "SELECT e FROM EMPLOYEE e WHERE >";

		int startPosition1 = "SELECT e FROM EMPLOYEE e WHERE ".length();
		int endPosition1   = startPosition1;

		int startPosition2 = "SELECT e FROM EMPLOYEE e WHERE >".length();
		int endPosition2   = startPosition2;

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblems(
			problems,
			new String [] { JPQLQueryProblemMessages.ComparisonExpression_MissingLeftExpression,
			                JPQLQueryProblemMessages.ComparisonExpression_MissingRightExpression },
			new int[] { startPosition1, startPosition2 },
			new int[] { endPosition1,   endPosition2   }
		);
	}

	@Test
	public void test_ComparisonExpression_MissingRightExpression() throws Exception {

		String query = "SELECT e FROM EMPLOYEE e WHERE e.age >";

		int startPosition = "SELECT e FROM EMPLOYEE e WHERE e.age >".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.ComparisonExpression_MissingRightExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_ConcatExpression_InvalidFirstExpression() throws Exception {

		String query = "SELECT e FROM Employee e WHERE CONCAT(FALSE, e.lastName)";

		int startPosition = "SELECT e FROM Employee e WHERE CONCAT(".length();
		int endPosition   = "SELECT e FROM Employee e WHERE CONCAT(FALSE".length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			JPQLQueryProblemMessages.ConcatExpression_InvalidExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_ConcatExpression_InvalidSecondExpression() throws Exception {

		String query = "SELECT e FROM Employee e WHERE CONCAT(e.firstName, TRUE)";

		int startPosition = "SELECT e FROM Employee e WHERE CONCAT(e.firstName, ".length();
		int endPosition   = query.length() - 1;

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			JPQLQueryProblemMessages.ConcatExpression_InvalidExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_ConcatExpression_MissingLeftParenthesis() throws Exception {

		String query = "SELECT e FROM Employee e WHERE CONCAT e.firstName, e.lastName)";

		int startPosition = "SELECT e FROM Employee e WHERE CONCAT".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			JPQLQueryProblemMessages.ConcatExpression_MissingLeftParenthesis,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_ConcatExpression_MissingRightParenthesis() throws Exception {

		String query = "SELECT e FROM Employee e WHERE CONCAT(e.firstName, e.lastName";

		int startPosition = query.length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			JPQLQueryProblemMessages.ConcatExpression_MissingRightParenthesis,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_ConstructorExpression_ConstructorItemEndsWithComma() throws Exception {

		String query = "SELECT NEW project1.Employee(e.name,) FROM Employee e";

		int startPosition = "SELECT NEW project1.Employee(e.name".length();
		int endPosition   = startPosition + 1;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.ConstructorExpression_ConstructorItemEndsWithComma,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_ConstructorExpression_ConstructorItemIsMissingComma() throws Exception {

		String query = "SELECT NEW project1.Employee(e.name e.age) FROM Employee e";

		int startPosition = "SELECT NEW project1.Employee(e.name".length();
		int endPosition   = startPosition + 1;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.ConstructorExpression_ConstructorItemIsMissingComma,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_ConstructorExpression_MissingConstructorItem() throws Exception {

		String query = "SELECT NEW project1.Employee() FROM Employee e";

		int startPosition = "SELECT NEW project1.Employee(".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.ConstructorExpression_MissingConstructorItem,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_ConstructorExpression_MissingConstructorName() throws Exception {

		String query = "SELECT NEW (e.name) FROM Employee e";

		int startPosition = "SELECT NEW ".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.ConstructorExpression_MissingConstructorName,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_ConstructorExpression_MissingLeftParenthesis() throws Exception {

		String query = "SELECT NEW project1.Employee e.name) FROM Employee e";

		int startPosition = "SELECT NEW project1.Employee".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.ConstructorExpression_MissingLeftParenthesis,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_ConstructorExpression_MissingRightParenthesis() throws Exception {

		String query = "SELECT NEW project1.Employee(e.name FROM Employee e";

		int startPosition = "SELECT NEW project1.Employee(e.name".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.ConstructorExpression_MissingRightParenthesis,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_CountFunction_InvalidExpression() throws Exception {

		String query = "SELECT COUNT(AVG(e.age)) FROM Employee e";
		int startPosition = "SELECT COUNT(".length();
		int endPosition   = "SELECT COUNT(AVG(e.age)".length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.CountFunction_InvalidExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_CountFunction_MissingExpression_1() throws Exception {

		String query = "SELECT COUNT() FROM Employee e";
		int startPosition = "SELECT COUNT(".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.CountFunction_MissingExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_CountFunction_MissingExpression_2() throws Exception {

		String query = "SELECT COUNT(DISTINCT) FROM Employee e";
		int startPosition = "SELECT COUNT(DISTINCT".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.CountFunction_MissingExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_CountFunction_MissingExpression_3() throws Exception {

		String query = "SELECT COUNT(DISTINCT ) FROM Employee e";
		int startPosition = "SELECT COUNT(DISTINCT ".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query, buildFormatter_2());

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.CountFunction_MissingExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_CountFunction_MissingLeftParenthesis_1() throws Exception {

		String query = "SELECT COUNT e.age) FROM Employee e";
		int startPosition = "SELECT COUNT".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.CountFunction_MissingLeftParenthesis,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_CountFunction_MissingLeftParenthesis_2() throws Exception {

		String query = "SELECT COUNT DISTINCT e.age) FROM Employee e";
		int startPosition = "SELECT COUNT".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.CountFunction_MissingLeftParenthesis,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_CountFunction_MissingRightParenthesis() throws Exception {

		String query = "SELECT COUNT(DISTINCT e.age FROM Employee e";
		int startPosition = "SELECT COUNT(DISTINCT e.age".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.CountFunction_MissingRightParenthesis,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_DateTime_JDBCEscapeFormat_InvalidSpecification() throws Exception {

		String query = "SELECT e FROM Employee e WHERE e.startDate < {jdbc '2008-12-31'}";

		int startPosition = "SELECT e FROM Employee e WHERE e.startDate < {".length();
		int endPosition   = "SELECT e FROM Employee e WHERE e.startDate < {jdbc".length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.DateTime_JDBCEscapeFormat_InvalidSpecification,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_DateTime_JDBCEscapeFormat_MissingCloseQuote() throws Exception {

		String query = "SELECT e FROM Employee e WHERE e.startDate < {d '2008-12-31}";

		int startPosition = "SELECT e FROM Employee e WHERE e.startDate < {d '2008-12-31".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.DateTime_JDBCEscapeFormat_MissingCloseQuote,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_DateTime_JDBCEscapeFormat_MissingOpenQuote() throws Exception {

		String query = "SELECT e FROM Employee e WHERE e.startDate < {d 2008-12-31'}";

		int startPosition = "SELECT e FROM Employee e WHERE e.startDate < {d ".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.DateTime_JDBCEscapeFormat_MissingOpenQuote,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_DateTime_JDBCEscapeFormat_MissingRightCurlyBrace() throws Exception {

		String query = "SELECT e FROM Employee e WHERE e.startDate < {d '2008-12-31'";

		int startPosition = query.length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.DateTime_JDBCEscapeFormat_MissingRightCurlyBrace,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_DeleteClause_FromMissing_1() throws Exception {

		String query = "DELETE";
		int startPosition = query.length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.DeleteClause_FromMissing,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_DeleteClause_FromMissing_2() throws Exception {

		String query = "DELETE ";
		int startPosition = query.length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.DeleteClause_FromMissing,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_DeleteClause_FromMissing_3() throws Exception {

		String query = "DELETE Employee e WHERE e.name = 'Pascal'";
		int startPosition = "DELETE ".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.DeleteClause_FromMissing,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_DeleteClause_MultipleRangeVariableDeclaration() throws Exception {

		String query = "DELETE FROM Employee e, Address a WHERE e.name = 'Pascal'";
		int startPosition = "DELETE FROM Employee e".length();
		int endPosition   = "DELETE FROM Employee e, Address a".length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.DeleteClause_MultipleRangeVariableDeclaration,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_DeleteClause_RangeVariableDeclarationMalformed() throws Exception {

		String query = "DELETE FROM Employee e a WHERE e.name = 'Pascal'";
		int startPosition = "DELETE FROM Employee e".length();
		int endPosition   = "DELETE FROM Employee e a".length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.DeleteClause_RangeVariableDeclarationMalformed,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_DeleteClause_RangeVariableDeclarationMalformed_FromMissing() throws Exception {

		String query = "DELETE Employee e a WHERE e.name = 'Pascal'";
		int startPosition1 = "DELETE ".length();
		int endPosition1   = startPosition1;
		int startPosition2 = "DELETE Employee e".length();
		int endPosition2   = "DELETE Employee e a".length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblems(
			problems,
			new String[] {
				JPQLQueryProblemMessages.DeleteClause_FromMissing,
				JPQLQueryProblemMessages.DeleteClause_RangeVariableDeclarationMalformed,
			},
			new int[] { startPosition1, startPosition2 },
			new int[] { endPosition1,   endPosition2   }
		);
	}

	@Test
	public void test_DeleteClause_RangeVariableDeclarationMissing_1() throws Exception {

		String query = "DELETE ";
		List<JPQLQueryProblem> problems = validate(query);

		testDoesNotHaveProblem(
			problems,
			JPQLQueryProblemMessages.DeleteClause_RangeVariableDeclarationMissing
		);
	}

	@Test
	public void test_DeleteClause_RangeVariableDeclarationMissing_2() throws Exception {

		String query = "DELETE FROM";
		int startPosition = query.length() + 1;
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.DeleteClause_RangeVariableDeclarationMissing,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_DeleteClause_RangeVariableDeclarationMissing_3() throws Exception {

		String query = "DELETE FROM ";
		int startPosition = query.length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.DeleteClause_RangeVariableDeclarationMissing,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_DeleteClause_RangeVariableDeclarationMissing_4() throws Exception {

		String query = "DELETE FROM WHERE e.name = 'Pascal'";
		int startPosition = "DELETE FROM ".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.DeleteClause_RangeVariableDeclarationMissing,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_EmptyCollectionComparisonExpression_MissingExpression() throws Exception {

		String query = "SELECT e FROM Employee e WHERE IS NOT EMPTY";

		int startPosition = "SELECT e FROM Employee e WHERE ".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.EmptyCollectionComparisonExpression_MissingExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_EntryExpression_InvalidExpression() throws Exception {

		String query = "SELECT ENTRY(e.name) FROM Employee e";
		int startPosition = "SELECT ENTRY(".length();
		int endPosition   = "SELECT ENTRY(e.name".length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.EntryExpression_InvalidExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_EntryExpression_MissingExpression() throws Exception {

		String query = "SELECT ENTRY() FROM Employee e JOIN e.addresses a";
		int startPosition = "SELECT ENTRY(".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.EntryExpression_MissingExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_EntryExpression_MissingLeftParenthesis() throws Exception {

		String query = "SELECT ENTRY a) FROM Employee e JOIN e.addresses a";
		int startPosition = "SELECT ENTRY".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.EntryExpression_MissingLeftParenthesis,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_EntryExpression_MissingRightParenthesis() throws Exception {

		String query = "SELECT ENTRY(a FROM Employee e JOIN e.addresses a";
		int startPosition = "SELECT ENTRY(a".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.EntryExpression_MissingRightParenthesis,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_ExistsExpression_InvalidExpression() throws Exception {

		String query = "SELECT e FROM Employee e WHERE EXISTS(e.name)";
		int startPosition = "SELECT e FROM Employee e WHERE EXISTS(".length();
		int endPosition   = "SELECT e FROM Employee e WHERE EXISTS(e.name".length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.ExistsExpression_InvalidExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_ExistsExpression_MissingExpression() throws Exception {

		String query = "SELECT e FROM Employee e JOIN e.addresses a WHERE EXISTS()";
		int startPosition = "SELECT e FROM Employee e JOIN e.addresses a WHERE EXISTS(".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.ExistsExpression_MissingExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_ExistsExpression_MissingLeftParenthesis() throws Exception {

		String query = "SELECT e FROM Employee e JOIN e.addresses a WHERE EXISTS SELECT p FROM Order p)";
		int startPosition = "SELECT e FROM Employee e JOIN e.addresses a WHERE EXISTS".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.ExistsExpression_MissingLeftParenthesis,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_ExistsExpression_MissingRightParenthesis() throws Exception {

		String query = "SELECT e FROM Employee e JOIN e.addresses a WHERE EXISTS(SELECT p FROM Order p";
		int startPosition = query.length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.ExistsExpression_MissingRightParenthesis,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_GroupByClause_GroupByItemEndsWithComma() throws Exception {

		String query = "SELECT e FROM Employee e GROUP BY e.name, e.age,";
		int startPosition = query.length() - 1;
		int endPosition   = startPosition + 1;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.GroupByClause_GroupByItemEndsWithComma,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_GroupByClause_GroupByItemIsMissingComma_1() throws Exception {

		String query = "SELECT e FROM Employee e GROUP BY e.name e.age";
		int startPosition = "SELECT e FROM Employee e GROUP BY e.name".length();
		int endPosition   = startPosition + 1;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.GroupByClause_GroupByItemIsMissingComma,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_GroupByClause_GroupByItemIsMissingComma_2() throws Exception {

		String query = "SELECT e FROM Employee e GROUP BY e.name, e.age e";
		int startPosition = "SELECT e FROM Employee e GROUP BY e.name, e.age".length();
		int endPosition   = startPosition + 1;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.GroupByClause_GroupByItemIsMissingComma,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_GroupByClause_GroupByItemMissing() throws Exception {

		String query = "SELECT e FROM Employee e GROUP BY";
		int startPosition = query.length();
		int endPosition   = query.length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.GroupByClause_GroupByItemMissing,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_HavingClause_ConditionalExpressionMissing_1() throws Exception {

		String query = "SELECT e FROM Employee e HAVING";
		int startPosition = "SELECT e FROM Employee e ".length();
		int endPosition   = query.length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.HavingClause_MissingConditionalExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_HavingClause_ConditionalExpressionMissing_2() throws Exception {

		String query = "SELECT e FROM Employee e HAVING ";
		int startPosition = "SELECT e FROM Employee e ".length();
		int endPosition   = query.length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.HavingClause_MissingConditionalExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_HavingClause_InvalidConditionalExpression() throws Exception {

		String query = "SELECT e FROM Employee e HAVING LENGTH(e.name)";
		int startPosition = "SELECT e FROM Employee e HAVING ".length();
		int endPosition   = query.length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.HavingClause_InvalidConditionalExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_IdentificationVariable_Invalid_Duplicate_1() throws Exception {

		String query = "SELECT e FROM Employee e, Address e";

		int startPosition1 = "SELECT e FROM Employee ".length();
		int endPosition1   = "SELECT e FROM Employee e".length();

		int startPosition2 = "SELECT e FROM Employee e, Address ".length();
		int endPosition2   = query.length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblems(
			problems,
			new String [] { JPQLQueryProblemMessages.IdentificationVariable_Invalid_Duplicate,
			                JPQLQueryProblemMessages.IdentificationVariable_Invalid_Duplicate },
			new int[] { startPosition1, startPosition2 },
			new int[] { endPosition1,   endPosition2   }
		);
	}

	@Test
	public void test_IdentificationVariable_Invalid_Duplicate_2() throws Exception {

		String query = "SELECT e FROM Employee e WHERE EXISTS(SELECT e.manager FROM Employee e)";

		List<JPQLQueryProblem> problems = validate(query);
		testHasNoProblems(problems);
	}

	@Test
	public void test_IdentificationVariable_Invalid_Duplicate_3() throws Exception {

		String query = "SELECT e FROM Employee e WHERE EXISTS(SELECT e.manager FROM Employee e, Address e)";

		int startPosition1 = "SELECT e FROM Employee e WHERE EXISTS(SELECT e.manager FROM Employee ".length();
		int endPosition1   = "SELECT e FROM Employee e WHERE EXISTS(SELECT e.manager FROM Employee e".length();

		int startPosition2 = "SELECT e FROM Employee e WHERE EXISTS(SELECT e.manager FROM Employee e, Address ".length();
		int endPosition2   = "SELECT e FROM Employee e WHERE EXISTS(SELECT e.manager FROM Employee e, Address e".length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblems(
			problems,
			new String [] { JPQLQueryProblemMessages.IdentificationVariable_Invalid_Duplicate,
			                JPQLQueryProblemMessages.IdentificationVariable_Invalid_Duplicate },
			new int[] { startPosition1, startPosition2 },
			new int[] { endPosition1,   endPosition2   }
		);
	}

	@Test
	public void test_IdentificationVariable_Invalid_Duplicate_4() throws Exception {

		String query = "SELECT COUNT(e) AS es FROM Employee e ORDER BY es";

		List<JPQLQueryProblem> problems = validate(query);
		testHasNoProblems(problems);
	}

	@Test
	public void test_IdentificationVariable_Invalid_Duplicate_5() throws Exception {

		String query = "SELECT COUNT(e) AS e FROM Employee e ORDER BY e";

		int startPosition1 = "SELECT COUNT(e) AS ".length();
		int endPosition1   = "SELECT COUNT(e) AS e".length();

		int startPosition2 = "SELECT COUNT(e) AS e FROM Employee ".length();
		int endPosition2   = "SELECT COUNT(e) AS e FROM Employee e".length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblems(
			problems,
			new String [] { JPQLQueryProblemMessages.IdentificationVariable_Invalid_Duplicate,
			                JPQLQueryProblemMessages.IdentificationVariable_Invalid_Duplicate },
			new int[] { startPosition1, startPosition2 },
			new int[] { endPosition1,   endPosition2   }
		);
	}

	@Test
	public void test_IdentificationVariable_Invalid_JavaIdentifier_1() throws Exception {

		String query = "SELECT e FROM Employee 2e";

		int startPosition = "SELECT e FROM Employee ".length();
		int endPosition   = query.length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			JPQLQueryProblemMessages.IdentificationVariable_Invalid_JavaIdentifier,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_IdentificationVariable_Invalid_JavaIdentifier_2() throws Exception {

		String query = "SELECT e!q FROM Employee e";

		int startPosition = "SELECT ".length();
		int endPosition   = "SELECT e!q".length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.IdentificationVariable_Invalid_JavaIdentifier,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_IdentificationVariable_Invalid_ReservedWord() throws Exception {

		String query = "SELECT e FROM Employee e, Adress AVG";

		int startPosition = "SELECT e FROM Employee e, Adress ".length();
		int endPosition   = query.length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.IdentificationVariable_Invalid_ReservedWord,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_IdentificationVariableDeclaration_MissingRangeVariableDeclaration1() throws Exception {

		String query = "SELECT e FROM JOIN e.managers m";

		int startPosition = "SELECT e FROM ".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			JPQLQueryProblemMessages.IdentificationVariableDeclaration_MissingRangeVariableDeclaration,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_IdentificationVariableDeclaration_MissingRangeVariableDeclaration2() throws Exception {

		String query = "SELECT e FROM Employee e JOIN e.managers m, JOIN e.addresses a, Address add";

		int startPosition = "SELECT e FROM Employee e JOIN e.managers m, ".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.IdentificationVariableDeclaration_MissingRangeVariableDeclaration,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_IndexExpression_InvalidExpression() throws Exception {

		String query = "SELECT INDEX(e.name) FROM Employee e";
		int startPosition = "SELECT INDEX(".length();
		int endPosition   = "SELECT INDEX(e.name".length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.IndexExpression_InvalidExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_IndexExpression_MissingExpression() throws Exception {

		String query = "SELECT INDEX() FROM Employee e";
		int startPosition = "SELECT INDEX(".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.IndexExpression_MissingExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_IndexExpression_MissingLeftParenthesis() throws Exception {

		String query = "SELECT INDEX e) FROM Employee e";
		int startPosition = "SELECT INDEX".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.IndexExpression_MissingLeftParenthesis,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_IndexExpression_MissingRightParenthesis() throws Exception {

		String query = "SELECT INDEX(e FROM Employee e";
		int startPosition = "SELECT INDEX(e".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.IndexExpression_MissingRightParenthesis,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_InExpression_InItemIsMissingComma() throws Exception {

		String query = "SELECT e FROM Employee e WHERE e.name IN(?1 ?2)";
		int startPosition = "SELECT e FROM Employee e WHERE e.name IN(?1".length();
		int endPosition   = startPosition + 1;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.InExpression_InItemIsMissingComma,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_InExpression_InItemsEndWithComma() throws Exception {

		String query = "SELECT e FROM Employee e WHERE e.name IN(?1, ?2,)";
		int startPosition = "SELECT e FROM Employee e WHERE e.name IN(?1, ?2".length();
		int endPosition   = startPosition + 1;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.InExpression_InItemEndsWithComma,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_InExpression_MalformedExpression() throws Exception {

		String query = "SELECT e FROM Employee e WHERE e IN(e.address.street)";
		int startPosition = "SELECT e FROM Employee e WHERE ".length();
		int endPosition   = "SELECT e FROM Employee e WHERE e".length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.InExpression_MalformedExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_InExpression_MissingExpression() throws Exception {

		String query = "SELECT e FROM Employee e WHERE IN(e.address.street)";
		int startPosition = "SELECT e FROM Employee e WHERE ".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.InExpression_MissingExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_InExpression_MissingInItems() throws Exception {

		String query = "SELECT e FROM Employee e WHERE e.name IN()";
		int startPosition = "SELECT e FROM Employee e WHERE e.name IN(".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.InExpression_MissingInItems,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_InExpression_MissingLeftParenthesis() throws Exception {

		String query = "SELECT e FROM Employee e WHERE e.name IN e.address.street)";
		int startPosition = "SELECT e FROM Employee e WHERE e.name IN".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.InExpression_MissingLeftParenthesis,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_InExpression_MissingRightParenthesis() throws Exception {

		String query = "SELECT e FROM Employee e WHERE e.name IN(e.address.street";
		int startPosition = query.length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.InExpression_MissingRightParenthesis,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_InputParameter_JavaIdentifier_1() throws Exception {

		String query = "SELECT e FROM Employee e WHERE :2AVG = 'Pascal'";

		int startPosition = "SELECT e FROM Employee e WHERE ".length();
		int endPosition   = "SELECT e FROM Employee e WHERE :2AVG".length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.InputParameter_JavaIdentifier,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_InputParameter_JavaIdentifier_2() throws Exception {

		String query = "SELECT e FROM Employee e WHERE :e!qb = 'Pascal'";

		int startPosition = "SELECT e FROM Employee e WHERE ".length();
		int endPosition   = "SELECT e FROM Employee e WHERE :e!qb".length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.InputParameter_JavaIdentifier,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_InputParameter_MissingParameter_1() throws Exception {

		String query = "SELECT e FROM Employee e WHERE ? = 'Pascal'";

		int startPosition = "SELECT e FROM Employee e WHERE ".length();
		int endPosition   = startPosition + 1;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.InputParameter_MissingParameter,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_InputParameter_MissingParameter_2() throws Exception {

		String query = "SELECT e FROM Employee e WHERE : = 'Pascal'";

		int startPosition = "SELECT e FROM Employee e WHERE ".length();
		int endPosition   = startPosition + 1;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.InputParameter_MissingParameter,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_InputParameter_Mixture() throws Exception {

		String query = "SELECT e FROM Employee e WHERE :name = 'Pascal' AND e.age < ?1";

		int startPosition1 = "SELECT e FROM Employee e WHERE ".length();
		int endPosition1   = "SELECT e FROM Employee e WHERE :name".length();

		int startPosition2 = "SELECT e FROM Employee e WHERE :name = 'Pascal' AND e.age < ".length();
		int endPosition2   = query.length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblems(
			problems,
			new String[] { JPQLQueryProblemMessages.InputParameter_SmallerThanOne,
			               JPQLQueryProblemMessages.InputParameter_SmallerThanOne },
			new int[] { startPosition1, startPosition2 },
			new int[] { endPosition1,   endPosition2 }
		);
	}

	@Test
	public void test_InputParameter_NotInteger_1() throws Exception {

		String query = "SELECT e FROM Employee e WHERE ?1a = 'Pascal'";

		int startPosition = "SELECT e FROM Employee e WHERE ".length();
		int endPosition   = "SELECT e FROM Employee e WHERE ?1a".length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.InputParameter_NotInteger,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_InputParameter_NotInteger_2() throws Exception {

		String query = "SELECT e FROM Employee e WHERE ?1.1 = 'Pascal'";

		int startPosition = "SELECT e FROM Employee e WHERE ".length();
		int endPosition   = "SELECT e FROM Employee e WHERE ?1.1".length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.InputParameter_NotInteger,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_InputParameter_SmallerThanOne_1() throws Exception {

		String query = "SELECT e FROM Employee e WHERE ?0 = 'Pascal'";

		int startPosition = "SELECT e FROM Employee e WHERE ".length();
		int endPosition   = "SELECT e FROM Employee e WHERE ?0".length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.InputParameter_SmallerThanOne,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_InputParameter_WrongClauseDeclaration_1() throws Exception {

		String query = "SELECT ?1 FROM Employee e";

		int startPosition = "SELECT ".length();
		int endPosition   = "SELECT ?1".length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.InputParameter_WrongClauseDeclaration,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_InputParameter_WrongClauseDeclaration_2() throws Exception {

		String query = "SELECT :name FROM Employee e";

		int startPosition = "SELECT ".length();
		int endPosition   = "SELECT :name".length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.InputParameter_WrongClauseDeclaration,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_Join_MissingIdentificationVariable_1() throws Exception {

		String query = "SELECT e FROM Employee e JOIN e.employees ";

		int startPosition = "SELECT e FROM Employee e JOIN e.employees ".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.Join_MissingIdentificationVariable,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_Join_MissingIdentificationVariable_2() throws Exception {

		String query = "SELECT e FROM Employee e JOIN e.employees AS";

		int startPosition = "SELECT e FROM Employee e JOIN e.employees AS".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.Join_MissingIdentificationVariable,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_Join_MissingIdentificationVariable_3() throws Exception {

		String query = "SELECT e FROM Employee e JOIN e.employees AS ";

		int startPosition = "SELECT e FROM Employee e JOIN e.employees AS ".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.Join_MissingIdentificationVariable,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_Join_MissingJoinAssociationPath_1() throws Exception {

		String query = "SELECT e FROM Employee e JOIN ";

		int startPosition = "SELECT e FROM Employee e JOIN ".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.Join_MissingJoinAssociationPath,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_Join_MissingJoinAssociationPath_2() throws Exception {

		String query = "SELECT e FROM Employee e JOIN AS p";

		int startPosition = "SELECT e FROM Employee e JOIN ".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.Join_MissingJoinAssociationPath,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_JoinFetch_MissingJoinAssociationPath_1() throws Exception {

		String query = "SELECT e FROM Employee e JOIN FETCH";

		int startPosition = "SELECT e FROM Employee e JOIN FETCH".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.JoinFetch_MissingJoinAssociationPath,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_JoinFetch_MissingJoinAssociationPath_2() throws Exception {

		String query = "SELECT e FROM Employee e JOIN FETCH ";

		int startPosition = "SELECT e FROM Employee e JOIN FETCH ".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.JoinFetch_MissingJoinAssociationPath,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_JoinFetch_WrongClauseDeclaration() throws Exception {

		String query = "SELECT e FROM Employee e WHERE EXISTS(SELECT o FROM Order o JOIN FETCH o.stores)";

		int startPosition = "SELECT e FROM Employee e WHERE EXISTS(SELECT o FROM Order o ".length();
		int endPosition   = query.length() - 1;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.JoinFetch_WrongClauseDeclaration,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_JPQLExpression_InvalidQuery_1() throws Exception {

		String query = ExpressionTools.EMPTY_STRING;
		int startPosition = 0;
		int endPosition   = 0;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.JPQLExpression_InvalidQuery,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_JPQLExpression_InvalidQuery_2() throws Exception {

		String query = "S";
		int startPosition = 0;
		int endPosition   = 1;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.JPQLExpression_InvalidQuery,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_JPQLExpression_InvalidQuery_3() throws Exception {

		String query = "JPA version 2.0";
		int startPosition = 0;
		int endPosition   = query.length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.JPQLExpression_InvalidQuery,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_JPQLExpression_InvalidQuery_4() throws Exception {

		String query = "SELECT e FROM Employee e";
		List<JPQLQueryProblem> problems = validate(query);
		testHasNoProblems(problems);
	}

	@Test
	public void test_JPQLExpression_InvalidQuery_5() throws Exception {

		String query = "UPDATE Employee e SET e.salary = e.salary * 1.20";
		List<JPQLQueryProblem> problems = validate(query);
		testHasNoProblems(problems);
	}

	@Test
	public void test_JPQLExpression_InvalidQuery_6() throws Exception {

		String query = "DELETE FROM Employee e WHERE e.name = 'Pascal'";
		List<JPQLQueryProblem> problems = validate(query);
		testHasNoProblems(problems);
	}

	@Test
	public void test_JPQLExpression_UnknownEnding_1() throws Exception {

		String query = "SELECT e FROM Employee e";
		List<JPQLQueryProblem> problems = validate(query);
		testHasNoProblems(problems);
	}

	@Test
	public void test_JPQLExpression_UnknownEnding_2() throws Exception {

		String query = "SELECT e FROM Employee e FROM";
		int startPosition = "SELECT e FROM Employee e ".length();
		int endPosition   = query.length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.JPQLExpression_UnknownEnding,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_KeyExpression_InvalidExpression() throws Exception {

		String query = "SELECT KEY(a.street) FROM Employee e JOIN e.addresses a";
		int startPosition = "SELECT KEY(".length();
		int endPosition   = "SELECT KEY(a.street".length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.KeyExpression_InvalidExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_KeyExpression_MissingExpression() throws Exception {

		String query = "SELECT KEY() FROM Employee e JOIN e.addresses a";
		int startPosition = "SELECT KEY(".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.KeyExpression_MissingExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_KeyExpression_MissingLeftParenthesis() throws Exception {

		String query = "SELECT KEY a) FROM Employee e JOIN e.addresses a";
		int startPosition = "SELECT KEY".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.KeyExpression_MissingLeftParenthesis,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_KeyExpression_MissingRightParenthesis() throws Exception {

		String query = "SELECT KEY(a FROM Employee e JOIN e.addresses a";
		int startPosition = "SELECT KEY(a".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.KeyExpression_MissingRightParenthesis,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_LengthExpression_InvalidExpression() throws Exception {

		String query = "SELECT e FROM Employee e WHERE LENGTH(2) = 2";
		int startPosition = "SELECT e FROM Employee e WHERE LENGTH(".length();
		int endPosition   = "SELECT e FROM Employee e WHERE LENGTH(2".length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.LengthExpression_InvalidExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_LengthExpression_MissingExpression() throws Exception {

		String query = "SELECT e FROM Employee e WHERE LENGTH() = 2";
		int startPosition = "SELECT e FROM Employee e WHERE LENGTH(".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.LengthExpression_MissingExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_LengthExpression_MissingLeftParenthesis() throws Exception {

		String query = "SELECT e FROM Employee e WHERE LENGTH e.name) = 2";
		int startPosition = "SELECT e FROM Employee e WHERE LENGTH".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.LengthExpression_MissingLeftParenthesis,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_LengthExpression_MissingRightParenthesis() throws Exception {

		String query = "SELECT e FROM Employee e WHERE LENGTH(e.name = 2";
		int startPosition = "SELECT e FROM Employee e WHERE LENGTH(e.name".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.LengthExpression_MissingRightParenthesis,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_LikeExpression_InvalidEscapeCharacter_1() throws Exception {

		String query = "SELECT e FROM Employee e WHERE e.name LIKE 'Pascal' ESCAPE 'C'";
		List<JPQLQueryProblem> problems = validate(query);

		testDoesNotHaveProblem(
			problems,
			JPQLQueryProblemMessages.LikeExpression_InvalidEscapeCharacter
		);
	}

	@Test
	public void test_LikeExpression_InvalidEscapeCharacter_2() throws Exception {

		String query = "SELECT e FROM Employee e WHERE e.name LIKE 'Pascal' ESCAPE :name";
		List<JPQLQueryProblem> problems = validate(query);

		testDoesNotHaveProblem(
			problems,
			JPQLQueryProblemMessages.LikeExpression_InvalidEscapeCharacter
		);
	}

	@Test
	public void test_LikeExpression_InvalidEscapeCharacter_3() throws Exception {

		String query = "SELECT e FROM Employee e WHERE e.name LIKE 'Pascal' ESCAPE 'CHAR'";
		int startPosition = "SELECT e FROM Employee e WHERE e.name LIKE 'Pascal' ESCAPE ".length();
		int endPosition   = query.length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			JPQLQueryProblemMessages.LikeExpression_InvalidEscapeCharacter,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_LikeExpression_InvalidEscapeCharacter_4() throws Exception {

		String query = "SELECT e FROM Employee e WHERE e.name LIKE 'Pascal' ESCAPE TRUE";
		int startPosition = "SELECT e FROM Employee e WHERE e.name LIKE 'Pascal' ESCAPE ".length();
		int endPosition   = query.length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			JPQLQueryProblemMessages.LikeExpression_InvalidEscapeCharacter,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_LikeExpression_MissingEscapeCharacter_1() throws Exception {

		String query = "SELECT e FROM Employee e WHERE e.name LIKE 'Pascal' ESCAPE";
		int startPosition = query.length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.LikeExpression_MissingEscapeCharacter,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_LikeExpression_MissingEscapeCharacter_2() throws Exception {

		String query = "SELECT e FROM Employee e WHERE e.name LIKE 'Pascal' ESCAPE ";
		int startPosition = query.length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.LikeExpression_MissingEscapeCharacter,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_LikeExpression_MissingPatternValue_1() throws Exception {

		String query = "SELECT e FROM Employee e WHERE e.name LIKE";
		int startPosition = query.length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.LikeExpression_MissingPatternValue,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_LikeExpression_MissingPatternValue_2() throws Exception {

		String query = "SELECT e FROM Employee e WHERE e.name LIKE ";
		int startPosition = query.length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.LikeExpression_MissingPatternValue,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_LikeExpression_MissingStringExpression() throws Exception {

		String query = "SELECT e FROM Employee e WHERE LIKE 'Pascal'";
		int startPosition = "SELECT e FROM Employee e WHERE ".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.LikeExpression_MissingStringExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_LocateExpression_InvalidFirstExpression() throws Exception {

		String query = "SELECT e FROM Employee e WHERE LOCATE(2, 'JPQL', 2)";
		int startPosition = "SELECT e FROM Employee e WHERE LOCATE(".length();
		int endPosition   = "SELECT e FROM Employee e WHERE LOCATE(2".length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			JPQLQueryProblemMessages.LocateExpression_InvalidFirstExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_LocateExpression_InvalidSecondExpression() throws Exception {

		String query = "SELECT e FROM Employee e WHERE LOCATE('JPQL', 2, 2)";

		int startPosition = "SELECT e FROM Employee e WHERE LOCATE('JPQL', ".length();
		int endPosition   = "SELECT e FROM Employee e WHERE LOCATE('JPQL', 2".length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			JPQLQueryProblemMessages.LocateExpression_InvalidSecondExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_LocateExpression_InvalidThirdExpression() throws Exception {

		String query = "SELECT e FROM Employee e WHERE LOCATE(e.name, 'JPQL', e)";
		int startPosition = "SELECT e FROM Employee e WHERE LOCATE(e.name, 'JPQL', ".length();
		int endPosition   = startPosition + 1;

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			JPQLQueryProblemMessages.LocateExpression_InvalidThirdExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_LocateExpression_MissingFirstComma_1() throws Exception {

		String query = "SELECT e FROM Employee e WHERE LOCATE(e.name";

		int startPosition = "SELECT e FROM Employee e WHERE LOCATE(e.name".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			JPQLQueryProblemMessages.LocateExpression_MissingFirstComma,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_LocateExpression_MissingFirstComma_2() throws Exception {

		String query = "SELECT e FROM Employee e WHERE LOCATE(e.name e.age, 2 + e.startDate)";
		int startPosition = "SELECT e FROM Employee e WHERE LOCATE(e.name".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			JPQLQueryProblemMessages.LocateExpression_MissingFirstComma,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_LocateExpression_MissingFirstExpression_1() throws Exception {

		String query = "SELECT e FROM Employee e WHERE LOCATE(";
		int startPosition = "SELECT e FROM Employee e WHERE LOCATE(".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			JPQLQueryProblemMessages.LocateExpression_MissingFirstExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_LocateExpression_MissingFirstExpression_2() throws Exception {

		String query = "SELECT e FROM Employee e WHERE LOCATE(, e.age)";
		int startPosition = "SELECT e FROM Employee e WHERE LOCATE(".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			JPQLQueryProblemMessages.LocateExpression_MissingFirstExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_LocateExpression_MissingFirstSecondExpression() throws Exception {

		String query = "SELECT e FROM Employee e WHERE LOCATE(, ";

		int startPosition1 = "SELECT e FROM Employee e WHERE LOCATE(".length();
		int endPosition1   = startPosition1;

		int startPosition2 = "SELECT e FROM Employee e WHERE LOCATE(, ".length();
		int endPosition2   = startPosition2;

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblems(
			problems,
			new String[] { JPQLQueryProblemMessages.LocateExpression_MissingFirstExpression,
			               JPQLQueryProblemMessages.LocateExpression_MissingSecondExpression },
			new int[] { startPosition1, startPosition2 },
			new int[] { endPosition1,   endPosition2 }
		);
	}

	@Test
	public void test_LocateExpression_MissingLeftParenthesis() throws Exception {

		String query = "SELECT e FROM Employee e WHERE LOCATE e.name, e.age)";
		int startPosition = "SELECT e FROM Employee e WHERE LOCATE".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			JPQLQueryProblemMessages.LocateExpression_MissingLeftParenthesis,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_LocateExpression_MissingRightParenthesis() throws Exception {

		String query = "SELECT e FROM Employee e WHERE LOCATE(e.name, e.age";
		int startPosition = query.length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			JPQLQueryProblemMessages.LocateExpression_MissingRightParenthesis,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_LocateExpression_MissingSecondComma() throws Exception {

		String query = "SELECT e FROM Employee e WHERE LOCATE(e.name, e.age 2 + e.startDate)";
		int startPosition = "SELECT e FROM Employee e WHERE LOCATE(e.name, e.age".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			JPQLQueryProblemMessages.LocateExpression_MissingSecondComma,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_LocateExpression_MissingSecondExpression_1() throws Exception {

		String query = "SELECT e FROM Employee e WHERE LOCATE(e.name,";
		int startPosition = "SELECT e FROM Employee e WHERE LOCATE(e.name,".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			JPQLQueryProblemMessages.LocateExpression_MissingSecondExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_LocateExpression_MissingSecondExpression_2() throws Exception {

		String query = "SELECT e FROM Employee e WHERE LOCATE(e.name, ";
		int startPosition = "SELECT e FROM Employee e WHERE LOCATE(e.name, ".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			JPQLQueryProblemMessages.LocateExpression_MissingSecondExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_LocateExpression_MissingThirdExpression_1() throws Exception {

		String query = "SELECT e FROM Employee e WHERE LOCATE(e.name, e.age,";
		int startPosition = query.length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			JPQLQueryProblemMessages.LocateExpression_MissingThirdExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_LocateExpression_MissingThirdExpression_2() throws Exception {

		String query = "SELECT e FROM Employee e WHERE LOCATE(e.name, e.age, ";
		int startPosition = query.length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			JPQLQueryProblemMessages.LocateExpression_MissingThirdExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_LocateExpression_MissingThirdExpression_3() throws Exception {

		String query = "SELECT e FROM Employee e WHERE LOCATE(e.name, e.age, )";
		int startPosition = query.length() - 1;
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query, buildFormatter_4());

		testHasProblem(
			problems,
			JPQLQueryProblemMessages.LocateExpression_MissingThirdExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_LogicalExpression_InvalidLeftExpression() throws Exception {

		String query = "SELECT e FROM Employee e WHERE LENGTH(e.name) AND e.name = 'JPQL'";

		int startPosition = "SELECT e FROM Employee e WHERE ".length();
		int endPosition   = "SELECT e FROM Employee e WHERE LENGTH(e.name)".length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.LogicalExpression_InvalidLeftExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_LogicalExpression_InvalidRightExpression() throws Exception {

		String query = "SELECT e FROM Employee e WHERE e.name = 'JPQL' AND LENGTH(e.name)";

		int startPosition = "SELECT e FROM Employee e WHERE e.name = 'JPQL' AND ".length();
		int endPosition   = query.length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.LogicalExpression_InvalidRightExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_LogicalExpression_MissingLeftExpression() throws Exception {

		String query = "SELECT e FROM Employee e WHERE AND e.name = 'JPQL'";

		int startPosition = "SELECT e FROM Employee e WHERE ".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.LogicalExpression_MissingLeftExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_LogicalExpression_MissingRightExpression_1() throws Exception {

		String query = "SELECT e FROM Employee e WHERE e.name = 'JPQL' AND";

		int startPosition = query.length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.LogicalExpression_MissingRightExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_LogicalExpression_MissingRightExpression_2() throws Exception {

		String query = "SELECT e FROM Employee e WHERE e.name = 'JPQL' AND ";

		int startPosition = query.length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.LogicalExpression_MissingRightExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_LowerExpression_InvalidExpression() throws Exception {

		String query = "SELECT e FROM Employee e WHERE LOWER(2) = 'PASCAL'";
		int startPosition = "SELECT e FROM Employee e WHERE LOWER(".length();
		int endPosition   = "SELECT e FROM Employee e WHERE LOWER(2".length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.LowerExpression_InvalidExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_LowerExpression_MissingExpression() throws Exception {

		String query = "SELECT e FROM Employee e WHERE LOWER() = 'PASCAL'";
		int startPosition = "SELECT e FROM Employee e WHERE LOWER(".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.LowerExpression_MissingExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_LowerExpression_MissingLeftParenthesis() throws Exception {

		String query = "SELECT e FROM Employee e WHERE LOWER e.name) = 'PASCAL'";
		int startPosition = "SELECT e FROM Employee e WHERE LOWER".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.LowerExpression_MissingLeftParenthesis,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_LowerExpression_MissingRightParenthesis() throws Exception {

		String query = "SELECT e FROM Employee e WHERE LOWER(e.name = 'PASCAL'";
		int startPosition = "SELECT e FROM Employee e WHERE LOWER(e.name".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.LowerExpression_MissingRightParenthesis,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_MaxFunction_InvalidExpression() throws Exception {

		String query = "SELECT MAX(e) FROM Employee e";
		int startPosition = "SELECT MAX(".length();
		int endPosition   = "SELECT MAX(e".length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.MaxFunction_InvalidExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_MaxFunction_MissingExpression_1() throws Exception {

		String query = "SELECT MAX() FROM Employee e";
		int startPosition = "SELECT MAX(".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.MaxFunction_MissingExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_MaxFunction_MissingExpression_2() throws Exception {

		String query = "SELECT MAX(DISTINCT) FROM Employee e";
		int startPosition = "SELECT MAX(DISTINCT".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.MaxFunction_MissingExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_MaxFunction_MissingExpression_3() throws Exception {

		String query = "SELECT MAX(DISTINCT ) FROM Employee e";
		int startPosition = "SELECT MAX(DISTINCT ".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query, buildFormatter_2());

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.MaxFunction_MissingExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_MaxFunction_MissingLeftParenthesis_1() throws Exception {

		String query = "SELECT MAX e.age) FROM Employee e";
		int startPosition = "SELECT MAX".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.MaxFunction_MissingLeftParenthesis,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_MaxFunction_MissingLeftParenthesis_2() throws Exception {

		String query = "SELECT MAX DISTINCT e.age) FROM Employee e";
		int startPosition = "SELECT MAX".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.MaxFunction_MissingLeftParenthesis,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_MaxFunction_MissingRightParenthesis() throws Exception {

		String query = "SELECT MAX(DISTINCT e.age FROM Employee e";
		int startPosition = "SELECT MAX(DISTINCT e.age".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.MaxFunction_MissingRightParenthesis,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_MinFunction_InvalidExpression() throws Exception {

		String query = "SELECT MIN(e) FROM Employee e";
		int startPosition = "SELECT MIN(".length();
		int endPosition   = "SELECT MIN(e".length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.MinFunction_InvalidExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_MinFunction_MissingExpression_1() throws Exception {

		String query = "SELECT MIN() FROM Employee e";
		int startPosition = "SELECT MIN(".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.MinFunction_MissingExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_MinFunction_MissingExpression_2() throws Exception {

		String query = "SELECT MIN(DISTINCT) FROM Employee e";

		int startPosition = "SELECT MIN(DISTINCT".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.MinFunction_MissingExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_MinFunction_MissingExpression_3() throws Exception {

		String query = "SELECT MIN(DISTINCT ) FROM Employee e";
		int startPosition = "SELECT MIN(DISTINCT ".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query, buildFormatter_2());

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.MinFunction_MissingExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_MinFunction_MissingLeftParenthesis_1() throws Exception {

		String query = "SELECT MIN e.age) FROM Employee e";
		int startPosition = "SELECT MIN".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.MinFunction_MissingLeftParenthesis,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_MinFunction_MissingLeftParenthesis_2() throws Exception {

		String query = "SELECT MIN DISTINCT e.age) FROM Employee e";
		int startPosition = "SELECT MIN".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.MinFunction_MissingLeftParenthesis,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_MinFunction_MissingRightParenthesis() throws Exception {

		String query = "SELECT MIN(DISTINCT e.age FROM Employee e";
		int startPosition = "SELECT MIN(DISTINCT e.age".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.MinFunction_MissingRightParenthesis,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_ModExpression_InvalidFirstExpression() throws Exception {

		String query = "SELECT e FROM Employee e WHERE MOD(TRUE, e.age)";

		int startPosition = "SELECT e FROM Employee e WHERE MOD(".length();
		int endPosition   = "SELECT e FROM Employee e WHERE MOD(TRUE".length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			JPQLQueryProblemMessages.ModExpression_InvalidFirstExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_ModExpression_InvalidSecondParenthesis() throws Exception {

		String query = "SELECT e FROM Employee e WHERE MOD(e.name, TRUE)";

		int startPosition = "SELECT e FROM Employee e WHERE MOD(e.name, ".length();
		int endPosition   = "SELECT e FROM Employee e WHERE MOD(e.name, TRUE".length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			JPQLQueryProblemMessages.ModExpression_InvalidSecondParenthesis,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_ModExpression_MissingComma_1() throws Exception {

		String query = "SELECT e FROM Employee e WHERE MOD(e.name";

		int startPosition = query.length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			JPQLQueryProblemMessages.ModExpression_MissingComma,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_ModExpression_MissingComma_2() throws Exception {

		String query = "SELECT e FROM Employee e WHERE MOD(e.name e.age)";

		int startPosition = "SELECT e FROM Employee e WHERE MOD(e.name".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			JPQLQueryProblemMessages.ModExpression_MissingComma,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_ModExpression_MissingFirstExpression() throws Exception {

		String query = "SELECT e FROM Employee e WHERE MOD(, e.age)";

		int startPosition = "SELECT e FROM Employee e WHERE MOD(".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			JPQLQueryProblemMessages.ModExpression_MissingFirstExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_ModExpression_MissingLeftParenthesis_1() throws Exception {

		String query = "SELECT e FROM Employee e WHERE MOD";

		int startPosition = query.length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			JPQLQueryProblemMessages.ModExpression_MissingLeftParenthesis,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_ModExpression_MissingLeftParenthesis_2() throws Exception {

		String query = "SELECT e FROM Employee e WHERE MOD e.name, e.age)";

		int startPosition = "SELECT e FROM Employee e WHERE MOD".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			JPQLQueryProblemMessages.ModExpression_MissingLeftParenthesis,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_ModExpression_MissingRightParenthesis() throws Exception {

		String query = "SELECT e FROM Employee e WHERE MOD(e.name, e.age";

		int startPosition = query.length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			JPQLQueryProblemMessages.ModExpression_MissingRightParenthesis,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_ModExpression_MissingSecondExpression_1() throws Exception {

		String query = "SELECT e FROM Employee e WHERE MOD(e.name,";

		int startPosition = query.length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			JPQLQueryProblemMessages.ModExpression_MissingSecondExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_ModExpression_MissingSecondExpression_2() throws Exception {

		String query = "SELECT e FROM Employee e WHERE MOD(e.name, ";

		int startPosition = query.length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			JPQLQueryProblemMessages.ModExpression_MissingSecondExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_ModExpression_MissingSecondExpression_3() throws Exception {

		String query = "SELECT e FROM Employee e WHERE MOD(e.name, )";

		int startPosition = query.length() - 1;
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query, buildFormatter_5());

		testHasProblem(
			problems,
			JPQLQueryProblemMessages.ModExpression_MissingSecondExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_NotExpression_MissingExpression_1() throws Exception {

		String query = "SELECT e FROM Employee e WHERE NOT";
		int startPosition = query.length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.NotExpression_MissingExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_NotExpression_MissingExpression_2() throws Exception {

		String query = "SELECT e FROM Employee e WHERE NOT ";
		int startPosition = query.length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.NotExpression_MissingExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_NullIfExpression_InvalidFirstExpression() throws Exception {

		String query = "SELECT NULLIF(e, 4 + e.age) FROM Employee e";

		int startPosition = "SELECT NULLIF(".length();
		int endPosition   = "SELECT NULLIF(e".length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.NullIfExpression_InvalidFirstExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_NullIfExpression_InvalidSecondExpression() throws Exception {

		String query = "SELECT NULLIF('JPQL', e) FROM Employee e";

		int startPosition = "SELECT NULLIF('JPQL', ".length();
		int endPosition   = "SELECT NULLIF('JPQL', e".length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.NullIfExpression_InvalidSecondExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_NullIfExpression_MissingComma() throws Exception {

		String query = "SELECT NULLIF('JPQL' 4 + e.age) FROM Employee e";

		int startPosition = "SELECT NULLIF('JPQL'".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.NullIfExpression_MissingComma,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_NullIfExpression_MissingFirstExpression_1() throws Exception {

		String query = "SELECT NULLIF( FROM Employee e";

		int startPosition = "SELECT NULLIF(".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.NullIfExpression_MissingFirstExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_NullIfExpression_MissingFirstExpression_2() throws Exception {

		String query = "SELECT NULLIF(, 4 + e.age) FROM Employee e";

		int startPosition = "SELECT NULLIF(".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.NullIfExpression_MissingFirstExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_NullIfExpression_MissingLeftParenthesis() throws Exception {

		String query = "SELECT NULLIF 'JPQL', 4 + e.age) FROM Employee e";

		int startPosition = "SELECT NULLIF".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.NullIfExpression_MissingLeftParenthesis,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_NullIfExpression_MissingRightParenthesis() throws Exception {

		String query = "SELECT NULLIF('JPQL', 4 + e.age FROM Employee e";

		int startPosition = "SELECT NULLIF('JPQL', 4 + e.age".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.NullIfExpression_MissingRightParenthesis,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_NullIfExpression_MissingSecondExpression_1() throws Exception {

		String query = "SELECT NULLIF('JPQL', FROM Employee e";

		int startPosition = "SELECT NULLIF('JPQL', ".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.NullIfExpression_MissingSecondExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_NullIfExpression_MissingSecondExpression_2() throws Exception {

		String query = "SELECT NULLIF('JPQL',) FROM Employee e";

		int startPosition = "SELECT NULLIF('JPQL',".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.NullIfExpression_MissingSecondExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_NullIfExpression_MissingSecondExpression_3() throws Exception {

		String query = "SELECT NULLIF('JPQL', ) FROM Employee e";

		int startPosition = "SELECT NULLIF('JPQL', ".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query, buildFormatter_6());

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.NullIfExpression_MissingSecondExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_NumericLiteral_Invalid_1() throws Exception {

		String query = "SELECT e FROM Employee e WHERE e.age > 1.2FF";

		int startPosition = "SELECT e FROM Employee e WHERE e.age > ".length();
		int endPosition   = query.length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.NumericLiteral_Invalid,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_NumericLiteral_Invalid_2() throws Exception {

		String query = "SELECT e FROM Employee e WHERE e.age > 2";
		List<JPQLQueryProblem> problems = validate(query);

		testDoesNotHaveProblem(
			problems,
			JPQLQueryProblemMessages.NumericLiteral_Invalid
		);
	}

	@Test
	public void test_NumericLiteral_Invalid_3() throws Exception {

		String query = "SELECT e FROM Employee e WHERE e.age > 2.2";
		List<JPQLQueryProblem> problems = validate(query);

		testDoesNotHaveProblem(
			problems,
			JPQLQueryProblemMessages.NumericLiteral_Invalid
		);
	}

	@Test
	public void test_NumericLiteral_Invalid_4() throws Exception {

		String query = "SELECT e FROM Employee e WHERE e.age > 2.2D";
		List<JPQLQueryProblem> problems = validate(query);

		testDoesNotHaveProblem(
			problems,
			JPQLQueryProblemMessages.NumericLiteral_Invalid
		);
	}

	@Test
	public void test_NumericLiteral_Invalid_5() throws Exception {

		String query = "SELECT e FROM Employee e WHERE e.age > 2.2E10";
		List<JPQLQueryProblem> problems = validate(query);

		testDoesNotHaveProblem(
			problems,
			JPQLQueryProblemMessages.NumericLiteral_Invalid
		);
	}

	@Test
	public void test_NumericLiteral_Invalid_6() throws Exception {

		String query = "SELECT e FROM Employee e WHERE e.age > 2.2E-10";
		List<JPQLQueryProblem> problems = validate(query);

		testDoesNotHaveProblem(
			problems,
			JPQLQueryProblemMessages.NumericLiteral_Invalid
		);
	}

	@Test
	public void test_NumericLiteral_Invalid_7() throws Exception {

		String query = "SELECT e FROM Employee e WHERE e.age > 0.34999999999999997779553950749686919152736663818359375";
		List<JPQLQueryProblem> problems = validate(query);

		testDoesNotHaveProblem(
			problems,
			JPQLQueryProblemMessages.NumericLiteral_Invalid
		);
	}

	@Test
	public void test_NumericLiteral_Invalid_8() throws Exception {

		String query = "SELECT e FROM Employee e WHERE e.age > 2.2F";
		List<JPQLQueryProblem> problems = validate(query);

		testDoesNotHaveProblem(
			problems,
			JPQLQueryProblemMessages.NumericLiteral_Invalid
		);
	}

	@Test
	public void test_NumericLiteral_Invalid_9() throws Exception {

		String query = "SELECT e FROM Employee e WHERE e.age > 2L";
		List<JPQLQueryProblem> problems = validate(query);

		testDoesNotHaveProblem(
			problems,
			JPQLQueryProblemMessages.NumericLiteral_Invalid
		);
	}

	@Test
	public void test_ObjectExpression_InvalidExpression() throws Exception {

		String query = "SELECT OBJECT(e.name) FROM Employee e";
		int startPosition = "SELECT OBJECT(".length();
		int endPosition   = "SELECT OBJECT(e.name".length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.ObjectExpression_InvalidExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_ObjectExpression_MissingExpression() throws Exception {

		String query = "SELECT OBJECT() FROM Employee e";
		int startPosition = "SELECT OBJECT(".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.ObjectExpression_MissingExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_ObjectExpression_MissingLeftParenthesis() throws Exception {

		String query = "SELECT OBJECT e) FROM Employee e";
		int startPosition = "SELECT OBJECT".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.ObjectExpression_MissingLeftParenthesis,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_ObjectExpression_MissingRightParenthesis() throws Exception {

		String query = "SELECT OBJECT(e FROM Employee e";
		int startPosition = "SELECT OBJECT(e".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.ObjectExpression_MissingRightParenthesis,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_OrderByClause_OrderByItemEndsWithComma() throws Exception {

		String query = "SELECT e FROM Employee e ORDER BY e.name, e.age,";
		int startPosition = query.length() - 1;
		int endPosition   = startPosition + 1;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.OrderByClause_OrderByItemEndsWithComma,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_OrderByClause_OrderByItemIsMissingComma_1() throws Exception {

		String query = "SELECT e FROM Employee e ORDER BY e.name e.age";
		int startPosition = "SELECT e FROM Employee e ORDER BY e.name".length();
		int endPosition   = startPosition + 1;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.OrderByClause_OrderByItemIsMissingComma,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_OrderByClause_OrderByItemIsMissingComma_2() throws Exception {

		String query = "SELECT e FROM Employee e ORDER BY e.name, e.age e";
		int startPosition = "SELECT e FROM Employee e ORDER BY e.name, e.age".length();
		int endPosition   = startPosition + 1;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.OrderByClause_OrderByItemIsMissingComma,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_OrderByClause_OrderByItemMissing() throws Exception {

		String query = "SELECT e FROM Employee e ORDER BY";
		int startPosition = query.length();
		int endPosition   = query.length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.OrderByClause_OrderByItemMissing,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_OrderByItem_InvalidPath() throws Exception {

		String query = "SELECT e FROM Employee e ORDER BY 2";
		int startPosition = "SELECT e FROM Employee e ORDER BY ".length();
		int endPosition   = query.length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.OrderByItem_InvalidPath,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_OrderByItem_MissingStateFieldPathExpression() throws Exception {

		String query = "SELECT e FROM Employee e ORDER BY ASC";
		int startPosition = "SELECT e FROM Employee e ORDER BY ".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.OrderByItem_MissingStateFieldPathExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_RangeVariableDeclaration_MissingAbstractSchemaName() throws Exception {

		String query = "SELECT e FROM AS e";
		int startPosition = "SELECT e FROM ".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.RangeVariableDeclaration_MissingAbstractSchemaName,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_RangeVariableDeclaration_MissingIdentificationVariable_1() throws Exception {

		String query = "SELECT o FROM Order o, Employee";
		int startPosition = "SELECT o FROM Order o, Employee".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.RangeVariableDeclaration_MissingIdentificationVariable,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_RangeVariableDeclaration_MissingIdentificationVariable_2() throws Exception {

		String query = "SELECT o FROM Order o, Employee ";
		int startPosition = "SELECT o FROM Order o, Employee ".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.RangeVariableDeclaration_MissingIdentificationVariable,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_RangeVariableDeclaration_MissingIdentificationVariable_3() throws Exception {

		String query = "SELECT o FROM Order o, Employee AS";
		int startPosition = "SELECT o FROM Order o, Employee AS".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.RangeVariableDeclaration_MissingIdentificationVariable,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_RangeVariableDeclaration_MissingIdentificationVariable_4() throws Exception {

		String query = "SELECT o FROM Order o, Employee AS ";
		int startPosition = "SELECT o FROM Order o, Employee AS ".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.RangeVariableDeclaration_MissingIdentificationVariable,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_ResultVariable_MissingResultVariable() throws Exception {

		String query = "SELECT AVG(e.age) AS FROM Employee e";
		int startPosition = "SELECT AVG(e.age) AS ".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.ResultVariable_MissingResultVariable,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_ResultVariable_MissingSelectExpression() throws Exception {

		String query = "SELECT AS e1 FROM Employee e";
		int startPosition = "SELECT ".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.ResultVariable_MissingSelectExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_SimpleSelectClause_NotSingleExpression() throws Exception {

		String query = "SELECT e FROM Employee e WHERE EXISTS(SELECT AVG(o.price), e FROM Order o)";
		int startPosition = "SELECT e FROM Employee e WHERE EXISTS(SELECT ".length();
		int endPosition   = "SELECT e FROM Employee e WHERE EXISTS(SELECT AVG(o.price), e".length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.SimpleSelectClause_NotSingleExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_SimpleSelectStatement_InvalidLocation_1() throws Exception {

		String query = "SELECT e FROM Employee e WHERE EXISTS(SELECT o.date FROM Order o)";
		List<JPQLQueryProblem> problems = validate(query);
		testHasNoProblems(problems);
	}

	@Test
	public void test_SimpleSelectStatement_InvalidLocation_2() throws Exception {

		String query = "SELECT e FROM Employee e HAVING ALL(SELECT o.name FROM Order o) = 'JPQL'";
		List<JPQLQueryProblem> problems = validate(query);
		testHasNoProblems(problems);
	}

	@Test
	public void test_SimpleSelectStatement_InvalidLocation_3() throws Exception {

		String query = "SELECT (SELECT e FROM Employee e) FROM Employee e";
		int startPosition = "SELECT (".length();
		int endPosition   = "SELECT (SELECT e FROM Employee e".length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.SimpleSelectStatement_InvalidLocation,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_SimpleSelectStatement_InvalidLocation_4() throws Exception {

		String query = "SELECT (SELECT e F) FROM Employee e";
		int startPosition = "SELECT (".length();
		int endPosition   = "SELECT (SELECT e F".length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.SimpleSelectStatement_InvalidLocation,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_SizeExpression_InvalidMissing() throws Exception {

		String query = "SELECT SIZE(e) FROM Employee e";
		int startPosition = "SELECT SIZE(".length();
		int endPosition   = "SELECT SIZE(e".length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.SizeExpression_InvalidExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_SizeExpression_MissingExpression() throws Exception {

		String query = "SELECT SIZE() FROM Employee e JOIN e.addresses a";
		int startPosition = "SELECT SIZE(".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.SizeExpression_MissingExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_SizeExpression_MissingLeftParenthesis() throws Exception {

		String query = "SELECT SIZE a) FROM Employee e JOIN e.addresses a";
		int startPosition = "SELECT SIZE".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.SizeExpression_MissingLeftParenthesis,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_SizeExpression_MissingRightParenthesis() throws Exception {

		String query = "SELECT SIZE(a.street FROM Employee e JOIN e.addresses a";
		int startPosition = "SELECT SIZE(a.street".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.SizeExpression_MissingRightParenthesis,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_SqrtExpression_InvalidExpression() throws Exception {

		String query = "SELECT SQRT(EXISTS()) FROM Employee e";
		int startPosition = "SELECT SQRT(".length();
		int endPosition   = "SELECT SQRT(EXISTS()".length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			JPQLQueryProblemMessages.SqrtExpression_InvalidExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_SqrtExpression_MissingExpression() throws Exception {

		String query = "SELECT SQRT() FROM Employee e";
		int startPosition = "SELECT SQRT(".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.SqrtExpression_MissingExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_SqrtExpression_MissingLeftParenthesis() throws Exception {

		String query = "SELECT SQRT 4 + 5) FROM Employee e";
		int startPosition = "SELECT SQRT".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.SqrtExpression_MissingLeftParenthesis,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_SqrtExpression_MissingRightParenthesis() throws Exception {

		String query = "SELECT SQRT(e.age + 100 FROM Employee e";
		int startPosition = "SELECT SQRT(e.age + 100".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.SqrtExpression_MissingRightParenthesis,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_SubExpression_MissingExpression() throws Exception {

		String query = "SELECT e FROM Employee e WHERE () = 2";

		int startPosition = "SELECT e FROM Employee e WHERE ".length();
		int endPosition   = startPosition + 2;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.SubExpression_MissingExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_SubExpression_MissingRightParenthesis() throws Exception {

		String query = "SELECT e FROM Employee e WHERE (2 + e.age";

		int startPosition = query.length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			JPQLQueryProblemMessages.SubExpression_MissingRightParenthesis,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_SubstringExpression_InvalidFirstExpression() throws Exception {

		String query = "SELECT e FROM Employee e WHERE SUBSTRING(e, 0, 1)";

		int startPosition = "SELECT e FROM Employee e WHERE SUBSTRING(".length();
		int endPosition   = startPosition + 1;

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			JPQLQueryProblemMessages.SubstringExpression_InvalidFirstExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_SubstringExpression_InvalidSecondExpression() throws Exception {

		String query = "SELECT e FROM Employee e WHERE SUBSTRING(e.name, e, 1)";

		int startPosition = "SELECT e FROM Employee e WHERE SUBSTRING(e.name, ".length();
		int endPosition   = startPosition + 1;

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			JPQLQueryProblemMessages.SubstringExpression_InvalidSecondExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_SubstringExpression_InvalidThirdExpression() throws Exception {

		String query = "SELECT e FROM Employee e WHERE SUBSTRING(e.name, 0, e)";

		int startPosition = "SELECT e FROM Employee e WHERE SUBSTRING(e.name, 0, ".length();
		int endPosition   = startPosition + 1;

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			JPQLQueryProblemMessages.SubstringExpression_InvalidThirdExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_SubstringExpression_MissingFirstComma() throws Exception {

		String query = "SELECT e FROM Employee e WHERE SUBSTRING(e.name 0, 1)";

		int startPosition = "SELECT e FROM Employee e WHERE SUBSTRING(e.name".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			JPQLQueryProblemMessages.SubstringExpression_MissingFirstComma,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_SubstringExpression_MissingFirstExpression() throws Exception {

		String query = "SELECT e FROM Employee e WHERE SUBSTRING(, 0, 1)";

		int startPosition = "SELECT e FROM Employee e WHERE SUBSTRING(".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			JPQLQueryProblemMessages.SubstringExpression_MissingFirstExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_SubstringExpression_MissingLeftParenthesis() throws Exception {

		String query = "SELECT e FROM Employee e WHERE SUBSTRING e.name, 0, 1)";

		int startPosition = "SELECT e FROM Employee e WHERE SUBSTRING".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			JPQLQueryProblemMessages.SubstringExpression_MissingLeftParenthesis,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_SubstringExpression_MissingRightParenthesis() throws Exception {

		String query = "SELECT e FROM Employee e WHERE SUBSTRING(e.name, 0, 1";

		int startPosition = query.length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			JPQLQueryProblemMessages.SubstringExpression_MissingRightParenthesis,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_SubstringExpression_MissingSecondComma() throws Exception {

		String query = "SELECT e FROM Employee e WHERE SUBSTRING(e.name, 0 1)";

		int startPosition = "SELECT e FROM Employee e WHERE SUBSTRING(e.name, 0".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasProblem(
			problems,
			JPQLQueryProblemMessages.SubstringExpression_MissingSecondComma,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_SubstringExpression_MissingSecondExpression() throws Exception {

		String query = "SELECT e FROM Employee e WHERE SUBSTRING(e.name, , 1)";

		int startPosition = "SELECT e FROM Employee e WHERE SUBSTRING(e.name, ".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query, buildFormatter_01());

		testHasProblem(
			problems,
			JPQLQueryProblemMessages.SubstringExpression_MissingSecondExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_SubstringExpression_MissingThirdExpression_1() throws Exception {

//		String query = "SELECT e FROM Employee e WHERE SUBSTRING(e.name, 0,";

//		int startPosition = query.length();
//		int endPosition   = startPosition;

		// TODO: Support platform and it should be done through an extension rather than
		// hard coding it in the unit-tests (like using IPlatform)
//		List<JPQLQueryProblem> problems = validate(query, IPlatform.JAVA);
//
//		testHasProblem(
//			problems,
//			JPQLQueryProblemMessages.SubstringExpression_MissingThirdExpression,
//			startPosition,
//			endPosition
//		);
	}

	@Test
	public void test_SubstringExpression_MissingThirdExpression_2() throws Exception {

//		String query = "SELECT e FROM Employee e WHERE SUBSTRING(e.name, 0, ";

//		int startPosition = query.length();
//		int endPosition   = startPosition;

		// TODO: Support platform and it should be done through an extension rather than
		// hard coding it in the unit-tests (like using IPlatform)
//		List<JPQLQueryProblem> problems = validate(query, IPlatform.JAVA);
//
//		testHasProblem(
//			problems,
//			JPQLQueryProblemMessages.SubstringExpression_MissingThirdExpression,
//			startPosition,
//			endPosition
//		);
	}

	@Test
	public void test_SubstringExpression_MissingThirdExpression_3() throws Exception {

//		String query = "SELECT e FROM Employee e WHERE SUBSTRING(e.name, 0, )";

//		int startPosition = query.length() - 1;
//		int endPosition   = startPosition;

		// TODO: Support platform and it should be done through an extension rather than
		// hard coding it in the unit-tests (like using IPlatform)
//		List<JPQLQueryProblem> problems = validate(query, IPlatform.JAVA, buildFormatter_7());
//
//		testHasProblem(
//			problems,
//			JPQLQueryProblemMessages.SubstringExpression_MissingThirdExpression,
//			startPosition,
//			endPosition
//		);
	}

	@Test
	public void test_SubstringExpression_MissingThirdExpression_4() throws Exception {

		String query = "SELECT e FROM Employee e WHERE SUBSTRING(e.name, 0)";

		List<JPQLQueryProblem> problems = validate(query);
		testDoesNotHaveProblem(problems, JPQLQueryProblemMessages.SubstringExpression_MissingThirdExpression);
	}

	@Test
	public void test_SumFunction_InvalidExpression() throws Exception {

		String query = "SELECT SUM(e) FROM Employee e";
		int startPosition = "SELECT SUM(".length();
		int endPosition   = "SELECT SUM(e".length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.SumFunction_InvalidExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_SumFunction_MissingExpression_1() throws Exception {

		String query = "SELECT SUM() FROM Employee e";
		int startPosition = "SELECT SUM(".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.SumFunction_MissingExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_SumFunction_MissingExpression_2() throws Exception {

		String query = "SELECT SUM(DISTINCT) FROM Employee e";
		int startPosition = "SELECT SUM(DISTINCT".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.SumFunction_MissingExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_SumFunction_MissingExpression_3() throws Exception {

		String query = "SELECT SUM(DISTINCT ) FROM Employee e";
		int startPosition = "SELECT SUM(DISTINCT ".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query, buildFormatter_2());

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.SumFunction_MissingExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_SumFunction_MissingLeftParenthesis_1() throws Exception {

		String query = "SELECT SUM e.age) FROM Employee e";
		int startPosition = "SELECT SUM".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.SumFunction_MissingLeftParenthesis,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_SumFunction_MissingLeftParenthesis_2() throws Exception {

		String query = "SELECT SUM DISTINCT e.age) FROM Employee e";
		int startPosition = "SELECT SUM".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.SumFunction_MissingLeftParenthesis,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_SumFunction_MissingRightParenthesis() throws Exception {

		String query = "SELECT SUM(DISTINCT e.age FROM Employee e";
		int startPosition = "SELECT SUM(DISTINCT e.age".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.SumFunction_MissingRightParenthesis,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_TrimExpression_InvalidExpression() throws Exception {

		String query = "SELECT e FROM Employee e WHERE TRIM(e) = 'JPQL'";
		int startPosition = "SELECT e FROM Employee e WHERE TRIM(".length();
		int endPosition   = startPosition + 1;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.TrimExpression_InvalidExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_TrimExpression_InvalidTrimCharacter() throws Exception {

		String query = "SELECT e FROM Employee e WHERE TRIM(e FROM ' JPQL ') = 'JPQL'";
		int startPosition = "SELECT e FROM Employee e WHERE TRIM(".length();
		int endPosition   = startPosition + 1;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.TrimExpression_InvalidTrimCharacter,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_TrimExpression_MissingExpression() throws Exception {

		String query = "SELECT e FROM Employee e WHERE TRIM() = 'JPQL'";
		int startPosition = "SELECT e FROM Employee e WHERE TRIM(".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.TrimExpression_MissingExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_TrimExpression_MissingLeftParenthesis() throws Exception {

		String query = "SELECT e FROM Employee e WHERE TRIM ' JPQL ') = 'JPQL'";
		int startPosition = "SELECT e FROM Employee e WHERE TRIM".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.TrimExpression_MissingLeftParenthesis,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_TrimExpression_MissingRightParenthesis() throws Exception {

		String query = "SELECT e FROM Employee e WHERE TRIM(' JPQL ' = 'JPQL'";
		int startPosition = "SELECT e FROM Employee e WHERE TRIM(' JPQL '".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.TrimExpression_MissingRightParenthesis,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_TrimExpression_NotSingleStringLiteral() throws Exception {

		String query = "SELECT e FROM Employee e WHERE TRIM('u2' FROM ' JPQL ') = 'JPQL'";
		int startPosition = "SELECT e FROM Employee e WHERE TRIM(".length();
		int endPosition   = "SELECT e FROM Employee e WHERE TRIM('u2'".length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.TrimExpression_NotSingleStringLiteral,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_TypeExpression_InvalidExpression() throws Exception {

		String query = "SELECT TYPE(e.name) FROM Employee e";
		int startPosition = "SELECT TYPE(".length();
		int endPosition   = "SELECT TYPE(e.name".length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.TypeExpression_InvalidExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_TypeExpression_MissingExpression() throws Exception {

		String query = "SELECT TYPE() FROM Employee e";
		int startPosition = "SELECT TYPE(".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.TypeExpression_MissingExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_TypeExpression_MissingLeftParenthesis() throws Exception {

		String query = "SELECT TYPE e) FROM Employee e";
		int startPosition = "SELECT TYPE".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.TypeExpression_MissingLeftParenthesis,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_TypeExpression_MissingRightParenthesis() throws Exception {

		String query = "SELECT TYPE(e FROM Employee e";
		int startPosition = "SELECT TYPE(e".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.TypeExpression_MissingRightParenthesis,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_UpdateClause_MissingRangeVariableDeclaration() throws Exception {

		String query = "UPDATE SET e.name = 'Pascal'";

		int startPosition = "UPDATE ".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.UpdateClause_MissingRangeVariableDeclaration,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_UpdateClause_MissingSet() throws Exception {

		String query = "UPDATE Employee e e.name = 'Pascal'";

		int startPosition = "UPDATE Employee e ".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.UpdateClause_MissingSet,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_UpdateClause_MissingUpdateItems_1() throws Exception {

		String query = "UPDATE Employee e SET";

		int startPosition = query.length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.UpdateClause_MissingUpdateItems,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_UpdateClause_MissingUpdateItems_2() throws Exception {

		String query = "UPDATE Employee e SET ";

		int startPosition = query.length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.UpdateClause_MissingUpdateItems,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_UpdateClause_UpdateItemEndsWithComma_1() throws Exception {

		String query = "UPDATE Employee e SET e.name = 'JPQL',";

		int startPosition = query.length() - 1;
		int endPosition   = startPosition + 1;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.UpdateClause_UpdateItemEndsWithComma,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_UpdateClause_UpdateItemEndsWithComma_2() throws Exception {

		String query = "UPDATE Employee e SET e.name = 'JPQL', ";

		int startPosition = "UPDATE Employee e SET e.name = 'JPQL'".length();
		int endPosition   = startPosition + 1;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.UpdateClause_UpdateItemEndsWithComma,
			startPosition,
			endPosition
		);
	}

//	@Test
//	public void test_UpdateItem_InvalidNewValue()
//	{
//		String query = "UPDATE Employee e SET e.name = LENGTH(e.age)";
//
//		int startPosition = "UPDATE Employee e SET e.name = ".length();
//		int endPosition   = query.length();
//
//		List<QueryProblem> problems = validate(query);
//
//		testHasOnlyOneProblem
//		(
//			problems,
//			QueryProblemMessages.UpdateItem_InvalidNewValue,
//			startPosition,
//			endPosition
//		);
//	}

	@Test
	public void test_UpdateClause_UpdateItemIsMissingComma() throws Exception {

		String query = "UPDATE Employee e SET e.name = 'JPQL' e.age = 20";

		int startPosition = "UPDATE Employee e SET e.name = 'JPQL'".length();
		int endPosition   = startPosition + 1;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.UpdateClause_UpdateItemIsMissingComma,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_UpdateItem_MissingEqualSign_1() throws Exception {

		String query = "UPDATE Employee e SET e.name";

		int startPosition = query.length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.UpdateItem_MissingEqualSign,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_UpdateItem_MissingEqualSign_2() throws Exception {

		String query = "UPDATE Employee e SET e.name ";

		int startPosition = query.length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.UpdateItem_MissingEqualSign,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_UpdateItem_MissingNewValue_1() throws Exception {

		String query = "UPDATE Employee e SET e.name =";

		int startPosition = query.length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.UpdateItem_MissingNewValue,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_UpdateItem_MissingNewValue_2() throws Exception {

		String query = "UPDATE Employee e SET e.name = ";

		int startPosition = query.length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.UpdateItem_MissingNewValue,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_UpdateItem_MissingStateFieldPathExpression() throws Exception {

		String query = "UPDATE Employee e SET = 'Pascal'";

		int startPosition = "UPDATE Employee e SET ".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.UpdateItem_MissingStateFieldPathExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_UpperExpression_InvalidExpression() throws Exception {

		String query = "SELECT e FROM Employee e WHERE UPPER(2) = 'PASCAL'";
		int startPosition = "SELECT e FROM Employee e WHERE UPPER(".length();
		int endPosition   = "SELECT e FROM Employee e WHERE UPPER(2".length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.UpperExpression_InvalidExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_UpperExpression_MissingExpression() throws Exception {

		String query = "SELECT e FROM Employee e WHERE UPPER() = 'PASCAL'";
		int startPosition = "SELECT e FROM Employee e WHERE UPPER(".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.UpperExpression_MissingExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_UpperExpression_MissingLeftParenthesis() throws Exception {

		String query = "SELECT e FROM Employee e WHERE UPPER e.name) = 'PASCAL'";
		int startPosition = "SELECT e FROM Employee e WHERE UPPER".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.UpperExpression_MissingLeftParenthesis,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_UpperExpression_MissingRightParenthesis() throws Exception {

		String query = "SELECT e FROM Employee e WHERE UPPER(e.name = 'PASCAL'";
		int startPosition = "SELECT e FROM Employee e WHERE UPPER(e.name".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.UpperExpression_MissingRightParenthesis,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_ValidQuery_01() throws Exception {

		String query = "UPDATE Product " +
		               "SET partNumber = CASE TYPE(project) WHEN com.titan.domain.EnumType.FIRST_NAME THEN '1' " +
		               "                                    WHEN com.titan.domain.EnumType.LAST_NAME  THEN '2' " +
		               "                                    ELSE '3' " +
		               "                 END";

		List<JPQLQueryProblem> problems = validate(query);
		testHasNoProblems(problems);
	}

	@Test
	public void test_ValueExpression_InvalidExpression() throws Exception {

		String query = "SELECT VALUE(e.name) FROM Employee e";
		int startPosition = "SELECT VALUE(".length();
		int endPosition   = "SELECT VALUE(e.name".length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.ValueExpression_InvalidExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_ValueExpression_MissingExpression() throws Exception {

		String query = "SELECT VALUE() FROM Employee e JOIN e.addresses a";
		int startPosition = "SELECT VALUE(".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.ValueExpression_MissingExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_ValueExpression_MissingLeftParenthesis() throws Exception {

		String query = "SELECT VALUE a) FROM Employee e JOIN e.addresses a";
		int startPosition = "SELECT VALUE".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.ValueExpression_MissingLeftParenthesis,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_ValueExpression_MissingRightParenthesis() throws Exception {

		String query = "SELECT VALUE(a FROM Employee e JOIN e.addresses a";
		int startPosition = "SELECT VALUE(a".length();
		int endPosition   = startPosition;

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.ValueExpression_MissingRightParenthesis,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_WhenClause_MissingThenExpression() throws Exception {

		String query = "UPDATE Employee e " +
		               "SET e.salary = " +
		               "    CASE WHEN e.rating = 1 THEN " +
		               "    ELSE e.salary * 1.01" +
		               "    END";

		List<JPQLQueryProblem> problems = validate(query);

		query = query.replaceAll("\\s+", " ");

		int startPosition = "UPDATE Employee e SET e.salary = CASE WHEN e.rating = 1 THEN ".length();
		int endPosition = startPosition;

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.WhenClause_MissingThenExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_WhenClause_MissingThenIdentifier() throws Exception {

		String query = "UPDATE Employee e " +
		               "SET e.salary = " +
		               "    CASE WHEN e.rating = 1 " +
		               "    ELSE e.salary * 1.01" +
		               "    END";

		List<JPQLQueryProblem> problems = validate(query);

		query = query.replaceAll("\\s+", " ");

		int startPosition = "UPDATE Employee e SET e.salary = CASE WHEN e.rating = 1 ".length();
		int endPosition = startPosition;

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.WhenClause_MissingThenIdentifier,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_WhenClause_MissingWhenExpression() throws Exception {

		String query = "UPDATE Employee e " +
		               "SET e.salary = " +
		               "    CASE WHEN" +
		               "    ELSE 'NonExempt'" +
		               "    END";

		List<JPQLQueryProblem> problems = validate(query);

		query = query.replaceAll("\\s+", " ");

		int startPosition = "UPDATE Employee e SET e.salary = CASE WHEN ".length();
		int endPosition = startPosition;

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.WhenClause_MissingWhenExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_WhereClause_ConditionalExpressionMissing_1() throws Exception {

		String query = "SELECT e FROM Employee e WHERE";
		int startPosition = "SELECT e FROM Employee e ".length();
		int endPosition   = query.length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.WhereClause_MissingConditionalExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_WhereClause_ConditionalExpressionMissing_2() throws Exception {

		String query = "SELECT e FROM Employee e WHERE ";
		int startPosition = "SELECT e FROM Employee e ".length();
		int endPosition   = query.length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.WhereClause_MissingConditionalExpression,
			startPosition,
			endPosition
		);
	}

	@Test
	public void test_WhereClause_HavingConditionalExpression() throws Exception {

		String query = "SELECT e FROM Employee e WHERE LENGTH(e.name)";
		int startPosition = "SELECT e FROM Employee e WHERE ".length();
		int endPosition   = query.length();

		List<JPQLQueryProblem> problems = validate(query);

		testHasOnlyOneProblem(
			problems,
			JPQLQueryProblemMessages.WhereClause_InvalidConditionalExpression,
			startPosition,
			endPosition
		);
	}

	@Override
	protected List<JPQLQueryProblem> validate(AbstractJPQLQueryHelper queryHelper) {
		return queryHelper.validateGrammar();
	}
}