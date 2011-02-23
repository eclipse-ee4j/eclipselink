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
package org.eclipse.persistence.utils.jpa.query.parser;

import org.junit.Test;

import static org.junit.Assert.*;

@SuppressWarnings("nls")
public final class SelectClauseTest extends AbstractJPQLTest {
	private JPQLQueryStringFormatter buildQueryStringFormatter_CountDistinct_WithSpaces() {
		return new JPQLQueryStringFormatter() {
			public String format(String query) {
				query = query.replaceAll("\\(\\s", "(");
				query = query.replaceAll("\\s\\)", ")");
				return query;
			}
		};
	}

	@Test
	public void testBuildExpression_Avg() {
		String query = "SELECT AVG(mag.price) FROM Magazine mag";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// SelectClause
		expression = selectStatement.getSelectClause();
		assertTrue(expression instanceof SelectClause);
		SelectClause selectClause = (SelectClause) expression;

		// AvgFunction
		expression = selectClause.getSelectExpression();
		assertTrue(expression instanceof AvgFunction);
		AvgFunction avgFunction = (AvgFunction) expression;

		// StateFieldPathExpression
		expression = avgFunction.getExpression();
		assertTrue(expression instanceof StateFieldPathExpression);
		StateFieldPathExpression stateFieldPathExpression = (StateFieldPathExpression) expression;
		assertEquals("mag.price", stateFieldPathExpression.getText());
	}

	@Test
	public void testBuildExpression_Collection_1() {
		String query = "SELECT e.firstName, e.lastName, e.address FROM Employee e";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// SelectClause
		expression = selectStatement.getSelectClause();
		assertTrue(expression instanceof SelectClause);
		SelectClause selectClause = (SelectClause) expression;

		expression = selectClause.getSelectExpression();
		assertTrue(expression instanceof CollectionExpression);
		CollectionExpression collectionExpression = (CollectionExpression) expression;

		assertEquals(3, collectionExpression.childrenSize());

		// First select expression
		expression = collectionExpression.getChild(0);
		assertTrue(expression instanceof StateFieldPathExpression);
		StateFieldPathExpression stateFieldPathExpression = (StateFieldPathExpression) expression;

		assertEquals("e.firstName", stateFieldPathExpression.getText());

		// Second select expression
		expression = collectionExpression.getChild(1);
		assertTrue(expression instanceof StateFieldPathExpression);
		stateFieldPathExpression = (StateFieldPathExpression) expression;

		assertEquals("e.lastName", stateFieldPathExpression.getText());

		// Third select expression
		expression = collectionExpression.getChild(2);
		assertTrue(expression instanceof StateFieldPathExpression);
		stateFieldPathExpression = (StateFieldPathExpression) expression;

		assertEquals("e.address", stateFieldPathExpression.getText());
	}

	@Test
	public void testBuildExpression_Collection_2() {
		String query = "SELECT OBJECT(e), COUNT(DISTINCT e) FROM Employee e";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// SelectClause
		expression = selectStatement.getSelectClause();
		assertTrue(expression instanceof SelectClause);
		SelectClause selectClause = (SelectClause) expression;

		// ObjectExpression
		expression = selectClause.getSelectExpression();
		assertTrue(expression instanceof CollectionExpression);
		CollectionExpression collectionExpression = (CollectionExpression) expression;

		// CollectionExpression
		assertEquals(2, collectionExpression.childrenSize());
		assertTrue(collectionExpression.getChild(0) instanceof ObjectExpression);
		assertTrue(collectionExpression.getChild(1) instanceof CountFunction);

		CountFunction countFunction = (CountFunction) collectionExpression.getChild(1);
		assertTrue(countFunction.hasDistinct());
	}

	@Test
	public void testBuildExpression_CountDistinct() {
		String query = "SELECT COUNT(DISTINCT mag.price) FROM Magazine mag";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// SelectClause
		expression = selectStatement.getSelectClause();
		assertTrue(expression instanceof SelectClause);
		SelectClause selectClause = (SelectClause) expression;

		// CountFunction
		expression = selectClause.getSelectExpression();
		assertTrue(expression instanceof CountFunction);
		CountFunction countFunction = (CountFunction) expression;

		// StateFieldPathExpression
		expression = countFunction.getExpression();
		assertTrue(expression instanceof StateFieldPathExpression);
		StateFieldPathExpression stateFieldPathExpression = (StateFieldPathExpression) expression;
		assertEquals("mag.price", stateFieldPathExpression.getText());
	}

	@Test
	public void testBuildExpression_CountDistinct_WithSpaces() {
		String query = "SELECT COUNT  ( DISTINCT mag.price  ) FROM Magazine mag";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query, buildQueryStringFormatter_CountDistinct_WithSpaces());

		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// SelectClause
		expression = selectStatement.getSelectClause();
		assertTrue(expression instanceof SelectClause);
		SelectClause selectClause = (SelectClause) expression;

		// CountFunction
		expression = selectClause.getSelectExpression();
		assertTrue(expression instanceof CountFunction);
		CountFunction countFunction = (CountFunction) expression;

		// StateFieldPathExpression
		expression = countFunction.getExpression();
		assertTrue(expression instanceof StateFieldPathExpression);
		StateFieldPathExpression stateFieldPathExpression = (StateFieldPathExpression) expression;
		assertEquals("mag.price", stateFieldPathExpression.getText());
	}

	@Test
	public void testBuildExpression_Disctint() {
		String query = "SELECT DISTINCT e FROM Employee e";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		expression = selectStatement.getSelectClause();
		assertTrue(expression instanceof SelectClause);

		SelectClause selectExpression = (SelectClause) expression;
		assertTrue(selectExpression.hasDistinct());
	}

	@Test
	public void testBuildExpression_MultipleSelectExpressions() {
		String query = "SELECT AVG(e), e, COUNT(e.name), NEW test(e), OBJECT(e), SUM(e.age) FROM Employee e";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// SelectClause
		expression = selectStatement.getSelectClause();
		assertTrue(expression instanceof SelectClause);
		SelectClause selectClause = (SelectClause) expression;

		// AggregateExpression
		expression = selectClause.getSelectExpression();
		assertTrue(expression instanceof CollectionExpression);
		CollectionExpression collectionExpression = (CollectionExpression) expression;

		assertEquals(6, collectionExpression.childrenSize());
		assertTrue(collectionExpression.getChild(0) instanceof AvgFunction);
		assertTrue(collectionExpression.getChild(1) instanceof IdentificationVariable);
		assertTrue(collectionExpression.getChild(2) instanceof CountFunction);
		assertTrue(collectionExpression.getChild(3) instanceof ConstructorExpression);
		assertTrue(collectionExpression.getChild(4) instanceof ObjectExpression);
		assertTrue(collectionExpression.getChild(5) instanceof SumFunction);

		// Item 1 - e
		expression = collectionExpression.getChild(1);
		assertTrue(expression instanceof IdentificationVariable);

		// Item 2 - COUNT(e.name)
		CountFunction countFunction = (CountFunction) collectionExpression.getChild(2);
		expression = countFunction.getExpression();
		assertTrue(expression instanceof StateFieldPathExpression);

		// Item 3 - NEW test(e)
		ConstructorExpression constructorExpression = (ConstructorExpression) collectionExpression.getChild(3);
		expression = constructorExpression.getConstructorItems();
		assertTrue(expression instanceof IdentificationVariable);

		// Item 4 - OBJECT(e)
		ObjectExpression objectExpression = (ObjectExpression) collectionExpression.getChild(4);
		expression = objectExpression.getExpression();
		assertTrue(expression instanceof IdentificationVariable);

		// Item 5 - SUM(e.age)
		SumFunction sumFunction = (SumFunction) collectionExpression.getChild(5);
		assertTrue(sumFunction.getExpression() instanceof StateFieldPathExpression);
	}

	@Test
	public void testBuildExpression_Object() {
		String query = "SELECT OBJECT(e) FROM Employee e";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// SelectClause
		expression = selectStatement.getSelectClause();
		assertTrue(expression instanceof SelectClause);
		SelectClause selectClause = (SelectClause) expression;

		// ObjectExpression
		expression = selectClause.getSelectExpression();
		assertTrue(expression instanceof ObjectExpression);
		ObjectExpression objectExpression = (ObjectExpression) expression;

		// IdentificationVariable
		expression = objectExpression.getExpression();
		assertTrue(expression instanceof IdentificationVariable);
		IdentificationVariable identificationVariable = (IdentificationVariable) expression;
		assertEquals("e", identificationVariable.getText());
	}

	@Test
	public void testBuildExpression_ResultVariable_1() {
		String query = "SELECT e AS emp FROM Employee e ORDER BY emp";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// SelectClause
		expression = selectStatement.getSelectClause();
		assertTrue(expression instanceof SelectClause);
		SelectClause selectClause = (SelectClause) expression;

		// ResultVariable
		expression = selectClause.getSelectExpression();
		assertTrue(expression instanceof ResultVariable);
		ResultVariable resultVariable = (ResultVariable) expression;

		// IdentificationVariable
		expression = resultVariable.getSelectExpression();
		assertTrue(expression instanceof IdentificationVariable);
		IdentificationVariable identificationVariable = (IdentificationVariable) expression;

		assertEquals("e", identificationVariable.toParsedText());

		// ResultVariable.IdentificationVariable
		expression = resultVariable.getResultVariable();
		assertTrue(expression instanceof IdentificationVariable);
		identificationVariable = (IdentificationVariable) expression;

		assertEquals("emp", identificationVariable.toParsedText());
	}

	@Test
	public void testBuildExpression_ResultVariable_2() {
		String query = "SELECT e.name, AVG(e.age) AS age FROM Employee e ORDER BY age";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// SelectClause
		expression = selectStatement.getSelectClause();
		assertTrue(expression instanceof SelectClause);
		SelectClause selectClause = (SelectClause) expression;

		// CollectionExpression
		expression = selectClause.getSelectExpression();
		assertTrue(expression instanceof CollectionExpression);
		CollectionExpression collectionExpression = (CollectionExpression) expression;

		assertEquals(2, collectionExpression.childrenSize());

		// ResultVariable
		expression = collectionExpression.getChild(1);
		assertTrue(expression instanceof ResultVariable);
		ResultVariable resultVariable = (ResultVariable) expression;

		// AvgFunction
		expression = resultVariable.getSelectExpression();
		assertTrue(expression instanceof AvgFunction);
		AvgFunction avgFunction = (AvgFunction) expression;

		assertEquals("AVG(e.age)", avgFunction.toParsedText());

		// ResultVariable.IdentificationVariable
		expression = resultVariable.getResultVariable();
		assertTrue(expression instanceof IdentificationVariable);
		IdentificationVariable identificationVariable = (IdentificationVariable) expression;

		assertEquals("age", identificationVariable.toParsedText());
	}

	@Test
	public void testBuildExpression_SelectDistinctFrom() {
		String query = "SELECT DISTINCT e FROM Employee e";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);

		SelectStatement selectStatement = (SelectStatement) expression;
		assertEquals(query, selectStatement.toParsedText());

		assertTrue (selectStatement.hasFromClause());
		assertFalse(selectStatement.hasWhereClause());
		assertFalse(selectStatement.hasGroupByClause());
		assertFalse(selectStatement.hasHavingClause());
		assertFalse(selectStatement.hasOrderByClause());

		// SelectClause
		expression = selectStatement.getSelectClause();
		assertTrue(expression instanceof SelectClause);

		SelectClause selectClause = (SelectClause) expression;
		assertTrue(selectClause.hasDistinct());

		// FromClause
		expression = selectStatement.getFromClause();
		assertTrue(expression instanceof FromClause);
	}

	@Test
	public void testBuildExpression_SelectFrom() {
		String query = "SELECT e FROM Employee e";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		assertEquals(query, selectStatement.toParsedText());

		assertTrue (selectStatement.hasFromClause());
		assertFalse(selectStatement.hasWhereClause());
		assertFalse(selectStatement.hasGroupByClause());
		assertFalse(selectStatement.hasHavingClause());
		assertFalse(selectStatement.hasOrderByClause());

		// SelectClause
		expression = selectStatement.getSelectClause();
		assertTrue(expression instanceof SelectClause);
		SelectClause selectClause = (SelectClause) expression;

		assertFalse(selectClause.hasDistinct());

		expression = selectClause.getSelectExpression();
		assertTrue(expression instanceof IdentificationVariable);
		IdentificationVariable identificationVariable = (IdentificationVariable) expression;

		assertEquals("e", identificationVariable.getText());

		// FromClause
		expression = selectStatement.getFromClause();
		assertTrue(expression instanceof FromClause);
	}
}