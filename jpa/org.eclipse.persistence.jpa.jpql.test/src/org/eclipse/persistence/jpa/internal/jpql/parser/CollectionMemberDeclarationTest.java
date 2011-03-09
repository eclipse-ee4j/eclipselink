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
package org.eclipse.persistence.jpa.internal.jpql.parser;

import org.junit.Test;

import static org.junit.Assert.*;

@SuppressWarnings("nls")
public final class CollectionMemberDeclarationTest extends AbstractJPQLTest {
	private JPQLQueryStringFormatter buildFormatter() {
		return new JPQLQueryStringFormatter() {
			public String format(String query) {
				query = query.replaceAll("\\(\\s", "(");
				query = query.replaceAll("\\s\\)", ")");
				return query;
			}
		};
	}

	private JPQLQueryStringFormatter buildQueryFormatter_09() {
		return new JPQLQueryStringFormatter() {
			public String format(String query) {
				return query.replace("IN(AS", "IN( AS");
			}
		};
	}

	@Test
	public void testBuildExpression_01() {
		String query = " SELECT e  FROM  IN ( e.address.street  )  AS emp ";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query, buildFormatter());

		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// FromClause
		expression = selectStatement.getFromClause();
		assertTrue(expression instanceof FromClause);
		FromClause fromClause = (FromClause) expression;

		// CollectionMemberDeclaration
		expression = fromClause.getDeclaration();
		assertTrue(expression instanceof CollectionMemberDeclaration);
		CollectionMemberDeclaration collectionMemberDeclaration = (CollectionMemberDeclaration) expression;

		assertSame(CollectionMemberDeclaration.IN, collectionMemberDeclaration.getText());
		assertTrue(collectionMemberDeclaration.hasLeftParenthesis());
		assertTrue(collectionMemberDeclaration.hasRightParenthesis());
		assertTrue(collectionMemberDeclaration.hasAs());
		assertTrue(collectionMemberDeclaration.hasSpaceAfterAs());
		assertTrue(collectionMemberDeclaration.hasSpaceAfterRightParenthesis());

		// CollectionValuedPathExpression
		expression = collectionMemberDeclaration.getCollectionValuedPathExpression();
		assertTrue(expression instanceof CollectionValuedPathExpression);
		CollectionValuedPathExpression collectionValuedPathExpression = (CollectionValuedPathExpression) expression;

		assertEquals(3, collectionValuedPathExpression.pathSize());
		assertEquals("e.address.street", collectionValuedPathExpression.toParsedText());

		// IdentificationVariable
		expression = collectionMemberDeclaration.getIdentificationVariable();
		assertTrue(expression instanceof IdentificationVariable);
		IdentificationVariable identificationVariable = (IdentificationVariable) expression;

		assertEquals("emp", identificationVariable.toParsedText());
	}

	@Test
	public void testBuildExpression_02() {
		String query = "SELECT e FROM IN(e.address.street) emp";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// FromClause
		expression = selectStatement.getFromClause();
		assertTrue(expression instanceof FromClause);
		FromClause fromClause = (FromClause) expression;

		// CollectionMemberDeclaration
		expression = fromClause.getDeclaration();
		assertTrue(expression instanceof CollectionMemberDeclaration);
		CollectionMemberDeclaration collectionMemberDeclaration = (CollectionMemberDeclaration) expression;

		assertSame (CollectionMemberDeclaration.IN, collectionMemberDeclaration.getText());
		assertTrue (collectionMemberDeclaration.hasLeftParenthesis());
		assertTrue (collectionMemberDeclaration.hasRightParenthesis());
		assertFalse(collectionMemberDeclaration.hasAs());
		assertFalse(collectionMemberDeclaration.hasSpaceAfterAs());
		assertTrue (collectionMemberDeclaration.hasSpaceAfterRightParenthesis());

		// CollectionValuedPathExpression
		expression = collectionMemberDeclaration.getCollectionValuedPathExpression();
		assertTrue(expression instanceof CollectionValuedPathExpression);
		CollectionValuedPathExpression collectionValuedPathExpression = (CollectionValuedPathExpression) expression;

		assertEquals(3, collectionValuedPathExpression.pathSize());
		assertEquals("e.address.street", collectionValuedPathExpression.toParsedText());

		// IdentificationVariable
		expression = collectionMemberDeclaration.getIdentificationVariable();
		assertTrue(expression instanceof IdentificationVariable);
		IdentificationVariable identificationVariable = (IdentificationVariable) expression;

		assertEquals("emp", identificationVariable.toParsedText());
	}

	@Test
	public void testBuildExpression_03() {
		String query = "SELECT e FROM IN(e.address.street) emp, IN(e.name) AS name";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// FromClause
		expression = selectStatement.getFromClause();
		assertTrue(expression instanceof FromClause);
		FromClause fromClause = (FromClause) expression;

		expression = fromClause.getDeclaration();
		assertTrue(expression instanceof CollectionExpression);
		CollectionExpression collectionExpression = (CollectionExpression) expression;

		assertEquals(2, collectionExpression.childrenSize());

		// 1. CollectionMemberDeclaration
		expression = collectionExpression.getChild(0);
		assertTrue(expression instanceof CollectionMemberDeclaration);
		CollectionMemberDeclaration collectionMemberDeclaration = (CollectionMemberDeclaration) expression;

		assertEquals("IN(e.address.street) emp", collectionMemberDeclaration.toParsedText());
		assertSame  (CollectionMemberDeclaration.IN, collectionMemberDeclaration.getText());
		assertTrue  (collectionMemberDeclaration.hasLeftParenthesis());
		assertTrue  (collectionMemberDeclaration.hasRightParenthesis());
		assertFalse (collectionMemberDeclaration.hasAs());
		assertFalse (collectionMemberDeclaration.hasSpaceAfterAs());
		assertTrue  (collectionMemberDeclaration.hasSpaceAfterRightParenthesis());

		// CollectionValuedPathExpression
		expression = collectionMemberDeclaration.getCollectionValuedPathExpression();
		assertTrue(expression instanceof CollectionValuedPathExpression);
		CollectionValuedPathExpression collectionValuedPathExpression = (CollectionValuedPathExpression) expression;

		assertEquals(3, collectionValuedPathExpression.pathSize());
		assertEquals("e.address.street", collectionValuedPathExpression.toParsedText());

		// IdentificationVariable
		expression = collectionMemberDeclaration.getIdentificationVariable();
		assertTrue(expression instanceof IdentificationVariable);
		IdentificationVariable identificationVariable = (IdentificationVariable) expression;

		assertEquals("emp", identificationVariable.toParsedText());

		// 2. CollectionMemberDeclaration
		expression = collectionExpression.getChild(1);
		assertTrue(expression instanceof CollectionMemberDeclaration);
		collectionMemberDeclaration = (CollectionMemberDeclaration) expression;

		assertEquals("IN(e.name) AS name", collectionMemberDeclaration.toParsedText());
		assertSame  (CollectionMemberDeclaration.IN, collectionMemberDeclaration.getText());
		assertTrue  (collectionMemberDeclaration.hasLeftParenthesis());
		assertTrue  (collectionMemberDeclaration.hasRightParenthesis());
		assertTrue  (collectionMemberDeclaration.hasAs());
		assertTrue  (collectionMemberDeclaration.hasSpaceAfterAs());
		assertTrue  (collectionMemberDeclaration.hasSpaceAfterRightParenthesis());

		// StateFieldPathExpression
		expression = collectionMemberDeclaration.getCollectionValuedPathExpression();
		assertTrue(expression instanceof CollectionValuedPathExpression);
		collectionValuedPathExpression = (CollectionValuedPathExpression) expression;

		assertEquals(2, collectionValuedPathExpression.pathSize());
		assertEquals("e.name", collectionValuedPathExpression.toParsedText());

		// IdentificationVariable
		expression = collectionMemberDeclaration.getIdentificationVariable();
		assertTrue(expression instanceof IdentificationVariable);
		identificationVariable = (IdentificationVariable) expression;

		assertEquals("name", identificationVariable.toParsedText());
	}

	@Test
	public void testBuildExpression_04() {
		String query = "SELECT e FROM IN";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// FromClause
		expression = selectStatement.getFromClause();
		assertTrue(expression instanceof FromClause);
		FromClause fromClause = (FromClause) expression;

		// CollectionMemberDeclaration
		expression = fromClause.getDeclaration();
		assertTrue(expression instanceof CollectionMemberDeclaration);
		CollectionMemberDeclaration collectionMemberDeclaration = (CollectionMemberDeclaration) expression;

		assertFalse(collectionMemberDeclaration.hasLeftParenthesis());
		assertFalse(collectionMemberDeclaration.hasRightParenthesis());
		assertFalse(collectionMemberDeclaration.hasAs());
		assertFalse(collectionMemberDeclaration.hasSpaceAfterAs());
		assertFalse(collectionMemberDeclaration.hasSpaceAfterRightParenthesis());

		// CollectionValuePathExpression
		expression = collectionMemberDeclaration.getCollectionValuedPathExpression();
		assertTrue(expression instanceof NullExpression);

		// IdentificationVariable
		expression = collectionMemberDeclaration.getIdentificationVariable();
		assertTrue(expression instanceof NullExpression);
	}

	@Test
	public void testBuildExpression_05() {
		String query = "SELECT e FROM IN(";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// FromClause
		expression = selectStatement.getFromClause();
		assertTrue(expression instanceof FromClause);
		FromClause fromClause = (FromClause) expression;

		// CollectionMemberDeclaration
		expression = fromClause.getDeclaration();
		assertTrue(expression instanceof CollectionMemberDeclaration);
		CollectionMemberDeclaration collectionMemberDeclaration = (CollectionMemberDeclaration) expression;

		assertTrue (collectionMemberDeclaration.hasLeftParenthesis());
		assertFalse(collectionMemberDeclaration.hasRightParenthesis());
		assertFalse(collectionMemberDeclaration.hasAs());
		assertFalse(collectionMemberDeclaration.hasSpaceAfterAs());
		assertFalse(collectionMemberDeclaration.hasSpaceAfterRightParenthesis());

		// CollectionValuePathExpression
		expression = collectionMemberDeclaration.getCollectionValuedPathExpression();
		assertTrue(expression instanceof NullExpression);

		// IdentificationVariable
		expression = collectionMemberDeclaration.getIdentificationVariable();
		assertTrue(expression instanceof NullExpression);
	}

	@Test
	public void testBuildExpression_06() {
		String query = "SELECT e FROM IN()";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// FromClause
		expression = selectStatement.getFromClause();
		assertTrue(expression instanceof FromClause);
		FromClause fromClause = (FromClause) expression;

		// CollectionMemberDeclaration
		expression = fromClause.getDeclaration();
		assertTrue(expression instanceof CollectionMemberDeclaration);
		CollectionMemberDeclaration collectionMemberDeclaration = (CollectionMemberDeclaration) expression;

		assertTrue (collectionMemberDeclaration.hasLeftParenthesis());
		assertTrue (collectionMemberDeclaration.hasRightParenthesis());
		assertFalse(collectionMemberDeclaration.hasAs());
		assertFalse(collectionMemberDeclaration.hasSpaceAfterAs());
		assertFalse(collectionMemberDeclaration.hasSpaceAfterRightParenthesis());

		// CollectionValuePathExpression
		expression = collectionMemberDeclaration.getCollectionValuedPathExpression();
		assertTrue(expression instanceof NullExpression);

		// IdentificationVariable
		expression = collectionMemberDeclaration.getIdentificationVariable();
		assertTrue(expression instanceof NullExpression);
	}

	@Test
	public void testBuildExpression_07() {
		String query = "SELECT e FROM IN() AS";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// FromClause
		expression = selectStatement.getFromClause();
		assertTrue(expression instanceof FromClause);
		FromClause fromClause = (FromClause) expression;

		// CollectionMemberDeclaration
		expression = fromClause.getDeclaration();
		assertTrue(expression instanceof CollectionMemberDeclaration);
		CollectionMemberDeclaration collectionMemberDeclaration = (CollectionMemberDeclaration) expression;

		assertTrue (collectionMemberDeclaration.hasLeftParenthesis());
		assertTrue (collectionMemberDeclaration.hasRightParenthesis());
		assertTrue (collectionMemberDeclaration.hasAs());
		assertFalse(collectionMemberDeclaration.hasSpaceAfterAs());
		assertTrue (collectionMemberDeclaration.hasSpaceAfterRightParenthesis());

		// CollectionValuePathExpression
		expression = collectionMemberDeclaration.getCollectionValuedPathExpression();
		assertTrue(expression instanceof NullExpression);

		// IdentificationVariable
		expression = collectionMemberDeclaration.getIdentificationVariable();
		assertTrue(expression instanceof NullExpression);
	}

	@Test
	public void testBuildExpression_08() {
		String query = "SELECT e FROM IN AS";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// FromClause
		expression = selectStatement.getFromClause();
		assertTrue(expression instanceof FromClause);
		FromClause fromClause = (FromClause) expression;

		// CollectionMemberDeclaration
		expression = fromClause.getDeclaration();
		assertTrue(expression instanceof CollectionMemberDeclaration);
		CollectionMemberDeclaration collectionMemberDeclaration = (CollectionMemberDeclaration) expression;

		assertFalse(collectionMemberDeclaration.hasLeftParenthesis());
		assertFalse(collectionMemberDeclaration.hasRightParenthesis());
		assertTrue (collectionMemberDeclaration.hasAs());
		assertFalse(collectionMemberDeclaration.hasSpaceAfterAs());
		assertTrue (collectionMemberDeclaration.hasSpaceAfterIn());
		assertFalse(collectionMemberDeclaration.hasSpaceAfterRightParenthesis());

		// CollectionValuePathExpression
		expression = collectionMemberDeclaration.getCollectionValuedPathExpression();
		assertTrue(expression instanceof NullExpression);

		// IdentificationVariable
		expression = collectionMemberDeclaration.getIdentificationVariable();
		assertTrue(expression instanceof NullExpression);
	}

	@Test
	public void testBuildExpression_09() {
		String query = "SELECT e FROM IN( AS";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query, buildQueryFormatter_09());

		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// FromClause
		expression = selectStatement.getFromClause();
		assertTrue(expression instanceof FromClause);
		FromClause fromClause = (FromClause) expression;

		// CollectionMemberDeclaration
		expression = fromClause.getDeclaration();
		assertTrue(expression instanceof CollectionMemberDeclaration);
		CollectionMemberDeclaration collectionMemberDeclaration = (CollectionMemberDeclaration) expression;

		assertTrue (collectionMemberDeclaration.hasLeftParenthesis());
		assertFalse(collectionMemberDeclaration.hasRightParenthesis());
		assertTrue (collectionMemberDeclaration.hasAs());
		assertFalse(collectionMemberDeclaration.hasSpaceAfterAs());
		assertTrue (collectionMemberDeclaration.hasSpaceAfterRightParenthesis());

		// CollectionValuePathExpression
		expression = collectionMemberDeclaration.getCollectionValuedPathExpression();
		assertTrue(expression instanceof NullExpression);

		// IdentificationVariable
		expression = collectionMemberDeclaration.getIdentificationVariable();
		assertTrue(expression instanceof NullExpression);
	}

	@Test
	public void testBuildExpression_10() {
		String query = "SELECT e FROM IN) AS";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// FromClause
		expression = selectStatement.getFromClause();
		assertTrue(expression instanceof FromClause);
		FromClause fromClause = (FromClause) expression;

		// CollectionMemberDeclaration
		expression = fromClause.getDeclaration();
		assertTrue(expression instanceof CollectionMemberDeclaration);
		CollectionMemberDeclaration collectionMemberDeclaration = (CollectionMemberDeclaration) expression;

		assertFalse(collectionMemberDeclaration.hasLeftParenthesis());
		assertTrue (collectionMemberDeclaration.hasRightParenthesis());
		assertTrue (collectionMemberDeclaration.hasAs());
		assertFalse(collectionMemberDeclaration.hasSpaceAfterAs());
		assertTrue (collectionMemberDeclaration.hasSpaceAfterRightParenthesis());

		// CollectionValuePathExpression
		expression = collectionMemberDeclaration.getCollectionValuedPathExpression();
		assertTrue(expression instanceof NullExpression);

		// IdentificationVariable
		expression = collectionMemberDeclaration.getIdentificationVariable();
		assertTrue(expression instanceof NullExpression);
	}

	@Test
	public void testBuildExpression_11() {
		String query = "SELECT e FROM IN AS e.employees";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// FromClause
		expression = selectStatement.getFromClause();
		assertTrue(expression instanceof FromClause);
		FromClause fromClause = (FromClause) expression;

		// CollectionMemberDeclaration
		expression = fromClause.getDeclaration();
		assertTrue(expression instanceof CollectionMemberDeclaration);
		CollectionMemberDeclaration collectionMemberDeclaration = (CollectionMemberDeclaration) expression;

		assertFalse(collectionMemberDeclaration.hasLeftParenthesis());
		assertFalse(collectionMemberDeclaration.hasRightParenthesis());
		assertTrue (collectionMemberDeclaration.hasAs());
		assertTrue (collectionMemberDeclaration.hasSpaceAfterAs());
		assertTrue (collectionMemberDeclaration.hasSpaceAfterIn());
		assertFalse(collectionMemberDeclaration.hasSpaceAfterRightParenthesis());

		// CollectionValuePathExpression
		expression = collectionMemberDeclaration.getCollectionValuedPathExpression();
		assertTrue(expression instanceof NullExpression);

		// IdentificationVariable
		expression = collectionMemberDeclaration.getIdentificationVariable();
		assertTrue(expression instanceof IdentificationVariable);
		IdentificationVariable identificationVariable = (IdentificationVariable) expression;

		assertEquals("e.employees", identificationVariable.toParsedText());
	}

	@Test
	public void testBuildExpression_12() {
		String query = "SELECT e FROM IN() AS ";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// FromClause
		expression = selectStatement.getFromClause();
		assertTrue(expression instanceof FromClause);
		FromClause fromClause = (FromClause) expression;

		// CollectionMemberDeclaration
		expression = fromClause.getDeclaration();
		assertTrue(expression instanceof CollectionMemberDeclaration);
		CollectionMemberDeclaration collectionMemberDeclaration = (CollectionMemberDeclaration) expression;

		assertTrue(collectionMemberDeclaration.hasLeftParenthesis());
		assertTrue(collectionMemberDeclaration.hasRightParenthesis());
		assertTrue(collectionMemberDeclaration.hasAs());
		assertTrue(collectionMemberDeclaration.hasSpaceAfterAs());
		assertTrue(collectionMemberDeclaration.hasSpaceAfterRightParenthesis());

		// CollectionValuePathExpression
		expression = collectionMemberDeclaration.getCollectionValuedPathExpression();
		assertTrue(expression instanceof NullExpression);

		// IdentificationVariable
		expression = collectionMemberDeclaration.getIdentificationVariable();
		assertTrue(expression instanceof NullExpression);
	}

	@Test
	public void testBuildExpression_14() {
		String query = "SELECT a FROM Address a WHERE EXISTS (SELECT e FROM Employee e, IN a.customerList)";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// SelectStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof SelectStatement);
		SelectStatement selectStatement = (SelectStatement) expression;

		// WhereClause
		expression = selectStatement.getWhereClause();
		assertTrue(expression instanceof WhereClause);
		WhereClause whereClause = (WhereClause) expression;

		// ExistsExpression
		expression = whereClause.getConditionalExpression();
		assertTrue(expression instanceof ExistsExpression);
		ExistsExpression existsExpression = (ExistsExpression) expression;

		// SimpleSelectStatement
		expression = existsExpression.getExpression();
		assertTrue(expression instanceof SimpleSelectStatement);
		SimpleSelectStatement simpleSelectStatement = (SimpleSelectStatement) expression;

		// SimpleFromClause
		expression = simpleSelectStatement.getFromClause();
		assertTrue(expression instanceof SimpleFromClause);
		SimpleFromClause simpleFromClause = (SimpleFromClause) expression;

		// CollectionExpression
		expression = simpleFromClause.getDeclaration();
		assertTrue(expression instanceof CollectionExpression);
		CollectionExpression collectionExpression = (CollectionExpression) expression;

		assertEquals(2, collectionExpression.childrenSize());

		// CollectionMemberDeclaration
		expression = collectionExpression.getChild(1);
		assertTrue(expression instanceof CollectionMemberDeclaration);
		CollectionMemberDeclaration collectionMemberDeclaration = (CollectionMemberDeclaration) expression;

		assertTrue(collectionMemberDeclaration.getIdentificationVariable() instanceof NullExpression);

		// CollectionValuedPathExpression
		expression = collectionMemberDeclaration.getCollectionValuedPathExpression();
		assertTrue(expression instanceof CollectionValuedPathExpression);
		CollectionValuedPathExpression collectionValuedPathExpression = (CollectionValuedPathExpression) expression;

		assertEquals("a.customerList", collectionValuedPathExpression.toParsedText());
	}
}