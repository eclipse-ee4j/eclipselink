/*******************************************************************************
 * Copyright (c) 2006, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available athttp://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle
 *
 ******************************************************************************/
package org.eclipse.persistence.utils.jpa.query.parser;

import org.junit.Test;

import static org.junit.Assert.*;

@SuppressWarnings("nls")
public final class SelectStatementTest extends AbstractJPQLTest
{
	@Override
	boolean isTolerant()
	{
		return true;
	}

	@Test
	public void testBuildExpression_01()
	{
		String query = "SELECT e FROM Employee e";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		assertEquals(query, selectStatement.toParsedText());
	}

	@Test
	public void testBuildExpression_02()
	{
		String query = "SELECT e " +
		               "FROM Employee e " +
		               "WHERE e.department.name = 'NA42' AND " +
		               "      e.address.state IN ('NY', 'CA')";

		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// SelectClause
		expression = selectStatement.getSelectClause();
		assertTrue(expression instanceof SelectClause);
		SelectClause selectClause = (SelectClause) expression;

		assertEquals("SELECT e", selectClause.toParsedText());

		// IdentificationVariable
		expression = selectClause.getSelectExpression();
		assertTrue(expression instanceof IdentificationVariable);
		IdentificationVariable identificationVariable = (IdentificationVariable) expression;

		assertEquals("e", identificationVariable.toParsedText());

		// FromClause
		expression = selectStatement.getFromClause();
		assertTrue(expression instanceof FromClause);
		FromClause fromClause = (FromClause) expression;

		assertEquals("FROM Employee e", fromClause.toParsedText());

		// IdentificationVariableDeclaration
		expression = fromClause.getDeclaration();
		assertTrue(expression instanceof IdentificationVariableDeclaration);
		IdentificationVariableDeclaration identificationVariableDeclaration = (IdentificationVariableDeclaration) expression;

		assertEquals("Employee e", identificationVariableDeclaration.toParsedText());

		// RangeVariableDeclaration
		expression = identificationVariableDeclaration.getRangeVariableDeclaration();
		assertTrue(expression instanceof RangeVariableDeclaration);
		RangeVariableDeclaration rangeVariableDeclaration = (RangeVariableDeclaration) expression;

		// AbstractSchemaName
		expression = rangeVariableDeclaration.getAbstractSchemaName();
		assertTrue(expression instanceof AbstractSchemaName);
		AbstractSchemaName abstractSchemaName = (AbstractSchemaName) expression;

		assertEquals("Employee", abstractSchemaName.toParsedText());

		// IdentificationVariable
		expression = rangeVariableDeclaration.getIdentificationVariable();
		assertTrue(expression instanceof IdentificationVariable);
		identificationVariable = (IdentificationVariable) expression;

		assertEquals("e", identificationVariable.toParsedText());

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		assertEquals("WHERE e.department.name = 'NA42' AND e.address.state IN('NY', 'CA')", whereClause.toParsedText());

		// AndExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof AndExpression);
		AndExpression andExpression = (AndExpression) expression;

		assertEquals(AndExpression.AND, andExpression.getText());
		assertEquals("e.department.name = 'NA42' AND e.address.state IN('NY', 'CA')", andExpression.toParsedText());

		// Left expression: ComparisonExpression
		expression = andExpression.getLeftExpression();
		assertTrue(expression instanceof ComparisonExpression);
		ComparisonExpression comparisonExpression = (ComparisonExpression) expression;

		// StateFieldPathExpression
		expression = comparisonExpression.getLeftExpression();
		assertTrue(expression instanceof StateFieldPathExpression);
		StateFieldPathExpression stateFieldPathExpression = (StateFieldPathExpression) expression;

		assertEquals("e.department.name", stateFieldPathExpression.toParsedText());

		// Right expression: InExpression
		expression = andExpression.getRightExpression();
		assertTrue(expression instanceof InExpression);
		InExpression inExpression = (InExpression) expression;

		assertEquals("e.address.state IN('NY', 'CA')", inExpression.toParsedText());

		// StateFieldPathExpression
		expression = inExpression.getStateFieldPathExpression();
		assertTrue(expression instanceof StateFieldPathExpression);
		stateFieldPathExpression = (StateFieldPathExpression) expression;

		assertEquals("e.address.state", stateFieldPathExpression.toParsedText());

		// CollectionExpression
		expression = inExpression.getInItems();
		assertTrue(expression instanceof CollectionExpression);
		CollectionExpression collectionExpression = (CollectionExpression) expression;

		assertEquals(2, collectionExpression.childrenSize());

		// 1. StringLiteral
		expression = collectionExpression.getChild(0);
		assertTrue(expression instanceof StringLiteral);
		StringLiteral stringLiteral = (StringLiteral) expression;

		assertEquals("'NY'", stringLiteral.toParsedText());

		// 2. StringLiteral
		expression = collectionExpression.getChild(1);
		assertTrue(expression instanceof StringLiteral);
		stringLiteral = (StringLiteral) expression;

		assertEquals("'CA'", stringLiteral.toParsedText());
	}

	@Test
	public void testBuildExpression_SelectDistinctFrom()
	{
		String query = "SELECT Distinct e FROM Employee e";
		String realQuery = JPQLQueries.formatQuery(query);
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		assertEquals(realQuery, selectStatement.toParsedText());

		assertTrue (selectStatement.hasFromClause());
		assertFalse(selectStatement.hasWhereClause());
		assertFalse(selectStatement.hasGroupByClause());
		assertFalse(selectStatement.hasHavingClause());
		assertFalse(selectStatement.hasOrderByClause());

		// SelectClause
		expression = selectStatement.getSelectClause();
		assertTrue(expression instanceof SelectClause);

		// FromClause
		expression = selectStatement.getFromClause();
		assertTrue(expression instanceof FromClause);
	}

	@Test
	public void testBuildExpression_SelectDistinctFromWhere()
	{
		String query = "SELECT DISTINCT e from Employee e WHERE e.name = 'Pascal'";
		String realQuery = JPQLQueries.formatQuery(query);
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		assertEquals(realQuery, selectStatement.toParsedText());

		assertTrue (selectStatement.hasFromClause());
		assertTrue (selectStatement.hasWhereClause());
		assertFalse(selectStatement.hasGroupByClause());
		assertFalse(selectStatement.hasHavingClause());
		assertFalse(selectStatement.hasOrderByClause());

		// SelectClause
		expression = selectStatement.getSelectClause();
		assertTrue(expression instanceof SelectClause);
	}

	@Test
	public void testBuildExpression_SelectFrom()
	{
		String query = "SELECT e From Employee e";
		String realQuery = JPQLQueries.formatQuery(query);
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		assertEquals(realQuery, selectStatement.toParsedText());

		assertTrue (selectStatement.hasFromClause());
		assertFalse(selectStatement.hasWhereClause());
		assertFalse(selectStatement.hasGroupByClause());
		assertFalse(selectStatement.hasHavingClause());
		assertFalse(selectStatement.hasOrderByClause());

		// SelectClause
		expression = selectStatement.getSelectClause();
		assertTrue(expression instanceof SelectClause);

		// FromClause
		expression = selectStatement.getFromClause();
		assertTrue(expression instanceof FromClause);
	}

	@Test
	public void testBuildExpression_SelectFromGroupByHavingOrderBy()
	{
		String query = "SELECT e "        +
		               "FROM Employee e " +
		               "GROUP BY e "      +
		               "HAVING SUM(e) "   +
		               "ORDER BY e";

		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		assertEquals(query, selectStatement.toParsedText());

		assertTrue (selectStatement.hasFromClause());
		assertFalse(selectStatement.hasWhereClause());
		assertTrue (selectStatement.hasGroupByClause());
		assertTrue (selectStatement.hasHavingClause());
		assertTrue (selectStatement.hasOrderByClause());

		// SelectClause
		expression = selectStatement.getSelectClause();
		assertTrue(expression instanceof SelectClause);

		// FromClause
		expression = selectStatement.getFromClause();
		assertTrue(expression instanceof FromClause);

		// GroupByClause
		expression = selectStatement.getGroupByClause();
		assertTrue(expression instanceof GroupByClause);

		// HavingClause
		expression = selectStatement.getHavingClause();
		assertTrue(expression instanceof HavingClause);

		// GroupByClause
		expression = selectStatement.getGroupByClause();
		assertTrue(expression instanceof GroupByClause);
	}

	@Test
	public void testBuildExpression_SelectFromWhere()
	{
		String query = "SELECT e FROM Employee e WHERE e.name = 'Pascal'";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		assertEquals(query, selectStatement.toParsedText());

		assertTrue (selectStatement.hasFromClause());
		assertTrue (selectStatement.hasWhereClause());
		assertFalse(selectStatement.hasGroupByClause());
		assertFalse(selectStatement.hasHavingClause());
		assertFalse(selectStatement.hasOrderByClause());

		// SelectClause
		expression = selectStatement.getSelectClause();
		assertTrue(expression instanceof SelectClause);

		// FromClause
		expression = selectStatement.getFromClause();
		assertTrue(expression instanceof FromClause);

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
	}

	@Test
	public void testBuildExpression_SelectFromWhereGroupBy()
	{
		String query = "SELECT e FROM Employee e WHERE e.name = 'Pascal' GROUP BY e";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		assertEquals(query, selectStatement.toParsedText());

		assertTrue (selectStatement.hasFromClause());
		assertTrue (selectStatement.hasWhereClause());
		assertTrue (selectStatement.hasGroupByClause());
		assertFalse(selectStatement.hasHavingClause());
		assertFalse(selectStatement.hasOrderByClause());

		// SelectClause
		expression = selectStatement.getSelectClause();
		assertTrue(expression instanceof SelectClause);

		// FromClause
		expression = selectStatement.getFromClause();
		assertTrue(expression instanceof FromClause);

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);

		// GroupByClause
		expression = selectStatement.getGroupByClause();
		assertTrue(expression instanceof GroupByClause);
	}

	@Test
	public void testBuildExpression_SelectFromWhereGroupByHaving()
	{
		String query = "SELECT e FROM Employee e WHERE e.name = 'Pascal' GROUP BY e HAVING SUM(e)";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		assertEquals(query, selectStatement.toParsedText());

		assertTrue (selectStatement.hasFromClause());
		assertTrue (selectStatement.hasWhereClause());
		assertTrue (selectStatement.hasGroupByClause());
		assertTrue (selectStatement.hasHavingClause());
		assertFalse(selectStatement.hasOrderByClause());

		// SelectClause
		expression = selectStatement.getSelectClause();
		assertTrue(expression instanceof SelectClause);

		// FromClause
		expression = selectStatement.getFromClause();
		assertTrue(expression instanceof FromClause);

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);

		// GroupByClause
		expression = selectStatement.getGroupByClause();
		assertTrue(expression instanceof GroupByClause);

		// HavingClause
		expression = selectStatement.getHavingClause();
		assertTrue(expression instanceof HavingClause);
	}

	@Test
	public void testBuildExpression_SelectFromWhereGroupByHavingOrderBy()
	{
		String query = "SELECT e FROM Employee e WHERE e.name = 'Pascal' GROUP BY e HAVING SUM(e) ORDER BY e";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		assertEquals(query, selectStatement.toParsedText());

		assertTrue (selectStatement.hasFromClause());
		assertTrue (selectStatement.hasWhereClause());
		assertTrue (selectStatement.hasGroupByClause());
		assertTrue (selectStatement.hasHavingClause());
		assertTrue(selectStatement.hasOrderByClause());

		// SelectClause
		expression = selectStatement.getSelectClause();
		assertTrue(expression instanceof SelectClause);

		// FromClause
		expression = selectStatement.getFromClause();
		assertTrue(expression instanceof FromClause);

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);

		// GroupByClause
		expression = selectStatement.getGroupByClause();
		assertTrue(expression instanceof GroupByClause);

		// HavingClause
		expression = selectStatement.getHavingClause();
		assertTrue(expression instanceof HavingClause);

		// GroupByClause
		expression = selectStatement.getGroupByClause();
		assertTrue(expression instanceof GroupByClause);
	}

	@Test
	public void testBuildExpression_SelectFromWhereGroupByOrderBy()
	{
		String query = "SELECT e FROM Employee e WHERE e.name = 'Pascal' GROUP BY e ORDER BY e";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		assertEquals(query, selectStatement.toParsedText());

		assertTrue (selectStatement.hasFromClause());
		assertTrue (selectStatement.hasWhereClause());
		assertTrue (selectStatement.hasGroupByClause());
		assertFalse(selectStatement.hasHavingClause());
		assertTrue (selectStatement.hasOrderByClause());

		// SelectClause
		expression = selectStatement.getSelectClause();
		assertTrue(expression instanceof SelectClause);

		// FromClause
		expression = selectStatement.getFromClause();
		assertTrue(expression instanceof FromClause);

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);

		// GroupByClause
		expression = selectStatement.getGroupByClause();
		assertTrue(expression instanceof GroupByClause);

		// GroupByClause
		expression = selectStatement.getGroupByClause();
		assertTrue(expression instanceof GroupByClause);
	}

	@Test
	public void testBuildExpression_SelectFromWhereHavingOrderBy()
	{
		String query = "SELECT e FROM Employee e WHERE e.name = 'Pascal' HAVING SUM(e) ORDER BY e";
		JPQLExpression jpqlExpression = JPQLTests.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		assertEquals(query, selectStatement.toParsedText());

		assertTrue (selectStatement.hasFromClause());
		assertTrue (selectStatement.hasWhereClause());
		assertFalse(selectStatement.hasGroupByClause());
		assertTrue (selectStatement.hasHavingClause());
		assertTrue (selectStatement.hasOrderByClause());

		// SelectClause
		expression = selectStatement.getSelectClause();
		assertTrue(expression instanceof SelectClause);

		// FromClause
		expression = selectStatement.getFromClause();
		assertTrue(expression instanceof FromClause);

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);

		// HavingClause
		expression = selectStatement.getHavingClause();
		assertTrue(expression instanceof HavingClause);
	}
}