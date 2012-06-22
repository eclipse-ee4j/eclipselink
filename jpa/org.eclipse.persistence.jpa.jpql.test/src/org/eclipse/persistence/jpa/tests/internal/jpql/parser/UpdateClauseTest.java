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
package org.eclipse.persistence.jpa.tests.internal.jpql.parser;

import org.eclipse.persistence.jpa.internal.jpql.parser.CollectionExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.Expression;
import org.eclipse.persistence.jpa.internal.jpql.parser.JPQLExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.NullExpression;
import org.eclipse.persistence.jpa.internal.jpql.parser.RangeVariableDeclaration;
import org.eclipse.persistence.jpa.internal.jpql.parser.UpdateClause;
import org.eclipse.persistence.jpa.internal.jpql.parser.UpdateItem;
import org.eclipse.persistence.jpa.internal.jpql.parser.UpdateStatement;

import org.junit.Test;

import static org.junit.Assert.*;

@SuppressWarnings("nls")
public final class UpdateClauseTest extends AbstractJPQLTest {
	@Test
	public void testBuildExpression_01() {
		String query = "UPDATE ";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// UpdateStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof UpdateStatement);
		UpdateStatement updateStatement = (UpdateStatement) expression;

		// UpdateClause
		expression = updateStatement.getUpdateClause();
		assertTrue(expression instanceof UpdateClause);
		UpdateClause updateClause = (UpdateClause) expression;

		assertTrue (updateClause.hasSpaceAfterUpdate());
		assertFalse(updateClause.hasSpaceAfterRangeVariableDeclaration());
		assertFalse(updateClause.hasSet());
		assertFalse(updateClause.hasSpaceAfterSet());
		assertFalse(updateClause.hasUpdateItems());

		// RangeVariableDeclaration
		expression = updateClause.getRangeVariableDeclaration();
		assertTrue(expression instanceof NullExpression);

		// UpdateItem
		expression = updateClause.getUpdateItems();
		assertTrue(expression instanceof NullExpression);
	}

	@Test
	public void testBuildExpression_02() {
		String query = "UPDATE SET";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// UpdateStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof UpdateStatement);
		UpdateStatement updateStatement = (UpdateStatement) expression;

		// UpdateClause
		expression = updateStatement.getUpdateClause();
		assertTrue(expression instanceof UpdateClause);
		UpdateClause updateClause = (UpdateClause) expression;

		assertTrue (updateClause.hasSpaceAfterUpdate());
		assertFalse(updateClause.hasSpaceAfterRangeVariableDeclaration());
		assertTrue (updateClause.hasSet());
		assertFalse(updateClause.hasSpaceAfterSet());
		assertFalse(updateClause.hasUpdateItems());

		// RangeVariableDeclaration
		expression = updateClause.getRangeVariableDeclaration();
		assertTrue(expression instanceof NullExpression);

		// UpdateItem
		expression = updateClause.getUpdateItems();
		assertTrue(expression instanceof NullExpression);
	}

	@Test
	public void testBuildExpression_03() {
		String query = "UPDATE SET ";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// UpdateStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof UpdateStatement);
		UpdateStatement updateStatement = (UpdateStatement) expression;

		// UpdateClause
		expression = updateStatement.getUpdateClause();
		assertTrue(expression instanceof UpdateClause);
		UpdateClause updateClause = (UpdateClause) expression;

		assertTrue (updateClause.hasSpaceAfterUpdate());
		assertFalse(updateClause.hasSpaceAfterRangeVariableDeclaration());
		assertTrue (updateClause.hasSet());
		assertTrue (updateClause.hasSpaceAfterSet());
		assertFalse(updateClause.hasUpdateItems());

		// RangeVariableDeclaration
		expression = updateClause.getRangeVariableDeclaration();
		assertTrue(expression instanceof NullExpression);

		// UpdateItem
		expression = updateClause.getUpdateItems();
		assertTrue(expression instanceof NullExpression);
	}

	@Test
	public void testBuildExpression_04() {
		String query = "UPDATE Employee";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// UpdateStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof UpdateStatement);
		UpdateStatement updateStatement = (UpdateStatement) expression;

		// UpdateClause
		expression = updateStatement.getUpdateClause();
		assertTrue(expression instanceof UpdateClause);
		UpdateClause updateClause = (UpdateClause) expression;

		assertTrue (updateClause.hasSpaceAfterUpdate());
		assertFalse(updateClause.hasSpaceAfterRangeVariableDeclaration());
		assertFalse(updateClause.hasSet());
		assertFalse(updateClause.hasSpaceAfterSet());
		assertFalse(updateClause.hasUpdateItems());

		// RangeVariableDeclaration
		expression = updateClause.getRangeVariableDeclaration();
		assertTrue(expression instanceof RangeVariableDeclaration);
		RangeVariableDeclaration rangeVariableDeclaration = (RangeVariableDeclaration) expression;

		assertEquals("Employee", rangeVariableDeclaration.toParsedText());

		// UpdateItem
		expression = updateClause.getUpdateItems();
		assertTrue(expression instanceof NullExpression);
	}

	@Test
	public void testBuildExpression_05() {
		String query = "UPDATE Employee ";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// UpdateStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof UpdateStatement);
		UpdateStatement updateStatement = (UpdateStatement) expression;

		// UpdateClause
		expression = updateStatement.getUpdateClause();
		assertTrue(expression instanceof UpdateClause);
		UpdateClause updateClause = (UpdateClause) expression;

		assertTrue (updateClause.hasSpaceAfterUpdate());
		assertFalse(updateClause.hasSpaceAfterRangeVariableDeclaration());
		assertFalse(updateClause.hasSet());
		assertFalse(updateClause.hasSpaceAfterSet());
		assertFalse(updateClause.hasUpdateItems());

		// RangeVariableDeclaration
		expression = updateClause.getRangeVariableDeclaration();
		assertTrue(expression instanceof RangeVariableDeclaration);
		RangeVariableDeclaration rangeVariableDeclaration = (RangeVariableDeclaration) expression;

		assertEquals("Employee ", rangeVariableDeclaration.toParsedText());
		assertTrue  (rangeVariableDeclaration.hasSpaceAfterAbstractSchemaName());

		// UpdateItem
		expression = updateClause.getUpdateItems();
		assertTrue(expression instanceof NullExpression);
	}

	@Test
	public void testBuildExpression_06() {
		String query = "UPDATE Employee AS";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// UpdateStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof UpdateStatement);
		UpdateStatement updateStatement = (UpdateStatement) expression;

		// UpdateClause
		expression = updateStatement.getUpdateClause();
		assertTrue(expression instanceof UpdateClause);
		UpdateClause updateClause = (UpdateClause) expression;

		assertTrue (updateClause.hasSpaceAfterUpdate());
		assertFalse(updateClause.hasSpaceAfterRangeVariableDeclaration());
		assertFalse(updateClause.hasSet());
		assertFalse(updateClause.hasSpaceAfterSet());
		assertFalse(updateClause.hasUpdateItems());

		// RangeVariableDeclaration
		expression = updateClause.getRangeVariableDeclaration();
		assertTrue(expression instanceof RangeVariableDeclaration);
		RangeVariableDeclaration rangeVariableDeclaration = (RangeVariableDeclaration) expression;

		assertEquals("Employee AS", rangeVariableDeclaration.toParsedText());

		// UpdateItem
		expression = updateClause.getUpdateItems();
		assertTrue(expression instanceof NullExpression);
	}

	@Test
	public void testBuildExpression_07() {
		String query = "UPDATE Employee AS e";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// UpdateStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof UpdateStatement);
		UpdateStatement updateStatement = (UpdateStatement) expression;

		// UpdateClause
		expression = updateStatement.getUpdateClause();
		assertTrue(expression instanceof UpdateClause);
		UpdateClause updateClause = (UpdateClause) expression;

		assertTrue (updateClause.hasSpaceAfterUpdate());
		assertFalse(updateClause.hasSpaceAfterRangeVariableDeclaration());
		assertFalse(updateClause.hasSet());
		assertFalse(updateClause.hasSpaceAfterSet());
		assertFalse(updateClause.hasUpdateItems());

		// RangeVariableDeclaration
		expression = updateClause.getRangeVariableDeclaration();
		assertTrue(expression instanceof RangeVariableDeclaration);
		RangeVariableDeclaration rangeVariableDeclaration = (RangeVariableDeclaration) expression;

		assertEquals("Employee AS e", rangeVariableDeclaration.toParsedText());

		// UpdateItem
		expression = updateClause.getUpdateItems();
		assertTrue(expression instanceof NullExpression);
	}

	@Test
	public void testBuildExpression_08() {
		String query = "UPDATE Employee AS SET";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// UpdateStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof UpdateStatement);
		UpdateStatement updateStatement = (UpdateStatement) expression;

		// UpdateClause
		expression = updateStatement.getUpdateClause();
		assertTrue(expression instanceof UpdateClause);
		UpdateClause updateClause = (UpdateClause) expression;

		assertTrue (updateClause.hasSpaceAfterUpdate());
		assertFalse(updateClause.hasSpaceAfterRangeVariableDeclaration());
		assertTrue (updateClause.hasSet());
		assertFalse(updateClause.hasSpaceAfterSet());
		assertFalse(updateClause.hasUpdateItems());

		// RangeVariableDeclaration
		expression = updateClause.getRangeVariableDeclaration();
		assertTrue(expression instanceof RangeVariableDeclaration);
		RangeVariableDeclaration rangeVariableDeclaration = (RangeVariableDeclaration) expression;

		assertEquals("Employee AS ", rangeVariableDeclaration.toParsedText());
		assertTrue  (rangeVariableDeclaration.hasSpaceAfterAbstractSchemaName());

		// UpdateItem
		expression = updateClause.getUpdateItems();
		assertTrue(expression instanceof NullExpression);
	}

	@Test
	public void testBuildExpression_09() {
		String query = "UPDATE Employee AS e SET";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// UpdateStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof UpdateStatement);
		UpdateStatement updateStatement = (UpdateStatement) expression;

		// UpdateClause
		expression = updateStatement.getUpdateClause();
		assertTrue(expression instanceof UpdateClause);
		UpdateClause updateClause = (UpdateClause) expression;

		assertTrue (updateClause.hasSpaceAfterUpdate());
		assertTrue (updateClause.hasSpaceAfterRangeVariableDeclaration());
		assertTrue (updateClause.hasSet());
		assertFalse(updateClause.hasSpaceAfterSet());
		assertFalse(updateClause.hasUpdateItems());

		// RangeVariableDeclaration
		expression = updateClause.getRangeVariableDeclaration();
		assertTrue(expression instanceof RangeVariableDeclaration);
		RangeVariableDeclaration rangeVariableDeclaration = (RangeVariableDeclaration) expression;

		assertEquals("Employee AS e", rangeVariableDeclaration.toParsedText());

		// UpdateItem
		expression = updateClause.getUpdateItems();
		assertTrue(expression instanceof NullExpression);
	}

	@Test
	public void testBuildExpression_10() {
		String query = "UPDATE Employee AS SET ";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// UpdateStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof UpdateStatement);
		UpdateStatement updateStatement = (UpdateStatement) expression;

		// UpdateClause
		expression = updateStatement.getUpdateClause();
		assertTrue(expression instanceof UpdateClause);
		UpdateClause updateClause = (UpdateClause) expression;

		assertTrue (updateClause.hasSpaceAfterUpdate());
		assertFalse(updateClause.hasSpaceAfterRangeVariableDeclaration());
		assertTrue (updateClause.hasSet());
		assertTrue (updateClause.hasSpaceAfterSet());
		assertFalse(updateClause.hasUpdateItems());

		// RangeVariableDeclaration
		expression = updateClause.getRangeVariableDeclaration();
		assertTrue(expression instanceof RangeVariableDeclaration);
		RangeVariableDeclaration rangeVariableDeclaration = (RangeVariableDeclaration) expression;

		assertEquals("Employee AS ", rangeVariableDeclaration.toParsedText());
		assertTrue  (rangeVariableDeclaration.hasSpaceAfterAbstractSchemaName());

		// UpdateItem
		expression = updateClause.getUpdateItems();
		assertTrue(expression instanceof NullExpression);
	}

	@Test
	public void testBuildExpression_11() {
		String query = "UPDATE Employee AS e SET e.name = 'Pascal'";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// UpdateStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof UpdateStatement);
		UpdateStatement updateStatement = (UpdateStatement) expression;

		// UpdateClause
		expression = updateStatement.getUpdateClause();
		assertTrue(expression instanceof UpdateClause);
		UpdateClause updateClause = (UpdateClause) expression;

		assertTrue(updateClause.hasSpaceAfterUpdate());
		assertTrue(updateClause.hasSpaceAfterRangeVariableDeclaration());
		assertTrue(updateClause.hasSet());
		assertTrue(updateClause.hasSpaceAfterSet());
		assertTrue(updateClause.hasUpdateItems());

		// RangeVariableDeclaration
		expression = updateClause.getRangeVariableDeclaration();
		assertTrue(expression instanceof RangeVariableDeclaration);
		RangeVariableDeclaration rangeVariableDeclaration = (RangeVariableDeclaration) expression;

		assertEquals("Employee AS e", rangeVariableDeclaration.toParsedText());

		// UpdateItem
		expression = updateClause.getUpdateItems();
		assertTrue(expression instanceof UpdateItem);
		UpdateItem updateItem = (UpdateItem) expression;

		assertEquals("e.name = 'Pascal'", updateItem.toParsedText());
	}

	@Test
	public void testBuildExpression_12() {
		String query = "UPDATE Employee AS e SET e.name = 'Pascal',";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// UpdateStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof UpdateStatement);
		UpdateStatement updateStatement = (UpdateStatement) expression;

		// UpdateClause
		expression = updateStatement.getUpdateClause();
		assertTrue(expression instanceof UpdateClause);
		UpdateClause updateClause = (UpdateClause) expression;

		assertTrue(updateClause.hasSpaceAfterUpdate());
		assertTrue(updateClause.hasSpaceAfterRangeVariableDeclaration());
		assertTrue(updateClause.hasSet());
		assertTrue(updateClause.hasSpaceAfterSet());
		assertTrue(updateClause.hasUpdateItems());

		// RangeVariableDeclaration
		expression = updateClause.getRangeVariableDeclaration();
		assertTrue(expression instanceof RangeVariableDeclaration);
		RangeVariableDeclaration rangeVariableDeclaration = (RangeVariableDeclaration) expression;

		assertEquals("Employee AS e", rangeVariableDeclaration.toParsedText());

		// CollectionExpression
		expression = updateClause.getUpdateItems();
		assertTrue(expression instanceof CollectionExpression);
		CollectionExpression collectionExpression = (CollectionExpression) expression;

		assertEquals(2, collectionExpression.childrenSize());

		// (1) UpdateItem
		expression = collectionExpression.getChild(0);
		assertTrue(expression instanceof UpdateItem);
		UpdateItem updateItem = (UpdateItem) expression;

		assertEquals("e.name = 'Pascal'", updateItem.toParsedText());
	}

	@Test
	public void testBuildExpression_13() {
		String query = "UPDATE  SET";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// UpdateStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof UpdateStatement);
		UpdateStatement updateStatement = (UpdateStatement) expression;

		// UpdateClause
		expression = updateStatement.getUpdateClause();
		assertTrue(expression instanceof UpdateClause);
		UpdateClause updateClause = (UpdateClause) expression;

		assertTrue (updateClause.hasSpaceAfterUpdate());
		assertFalse(updateClause.hasSpaceAfterRangeVariableDeclaration());
		assertTrue (updateClause.hasSet());
		assertFalse(updateClause.hasSpaceAfterSet());
		assertFalse(updateClause.hasUpdateItems());

		// RangeVariableDeclaration
		expression = updateClause.getRangeVariableDeclaration();
		assertTrue(expression instanceof NullExpression);

		// UpdateItem
		expression = updateClause.getUpdateItems();
		assertTrue(expression instanceof NullExpression);
	}

	@Test
	public void testBuildExpression_14() {
		String query = "UPDATE SET e.name = 'Pascal'";
		JPQLExpression jpqlExpression = JPQLQueryBuilder.buildQuery(query);

		// UpdateStatement
		Expression expression = jpqlExpression.getQueryStatement();
		assertTrue(expression instanceof UpdateStatement);
		UpdateStatement updateStatement = (UpdateStatement) expression;

		// UpdateClause
		expression = updateStatement.getUpdateClause();
		assertTrue(expression instanceof UpdateClause);
		UpdateClause updateClause = (UpdateClause) expression;

		assertTrue (updateClause.hasSpaceAfterUpdate());
		assertFalse(updateClause.hasSpaceAfterRangeVariableDeclaration());
		assertTrue (updateClause.hasSet());
		assertTrue (updateClause.hasSpaceAfterSet());
		assertTrue (updateClause.hasUpdateItems());

		// RangeVariableDeclaration
		expression = updateClause.getRangeVariableDeclaration();
		assertTrue(expression instanceof NullExpression);

		// UpdateItem
		expression = updateClause.getUpdateItems();
		assertTrue(expression instanceof UpdateItem);
		UpdateItem updateItem = (UpdateItem) expression;

		assertEquals("e.name = 'Pascal'", updateItem.toParsedText());
	}

//	@Test
//	public void testManualCreation_01()
//	{
//		testManualCreation_01(true);
//	}
//
//	private void testManualCreation_01(boolean hasAS)
//	{
//		JPQLExpression expression = new JPQLExpression(IJPAVersion.VERSION_2_0);
//
//		UpdateStatement updateStatement = expression.addUpdateStatement();
//		assertNotNull("UpdateStatement should not be null", updateStatement);
//
//		UpdateClause updateClause = updateStatement.getUpdateClause();
//		assertNotNull("UpdateClause should not be null", updateClause);
//
//		String abstractSchemaName = "Employee";
//		String identificationVariable = "e";
//
//		RangeVariableDeclaration declaration = updateClause.addAbstractSchemaName
//		(
//			abstractSchemaName,
//			identificationVariable,
//			hasAS
//		);
//
//		assertNotNull("The abstract schema name should not be null", declaration);
//
//		assertSame
//		(
//			"The schema name declaration should be the one that was manually created",
//			declaration,
//			updateClause.getRangeVariableDeclaration()
//		);
//
//		assertEquals(hasAS,                  declaration.hasAs());
//		assertEquals(abstractSchemaName,     declaration.getRangeVariableDeclaration().toParsedText());
//		assertEquals(identificationVariable, declaration.getIdentificationVariable().toParsedText());
//	}
//
//	@Test
//	public void testManualCreation_02()
//	{
//		testManualCreation_01(false);
//	}
//
//	private void testManualCreation_02(String schemaNameDeclaration)
//	{
//		JPQLExpression expression = new JPQLExpression(IJPAVersion.VERSION_2_0);
//
//		UpdateStatement updateStatement = expression.addUpdateStatement();
//		assertNotNull("UpdateStatement should not be null", updateStatement);
//
//		UpdateClause updateClause = updateStatement.getUpdateClause();
//		assertNotNull("UpdateClause should not be null", updateClause);
//
//		RangeVariableDeclaration declaration = updateClause.addAbstractSchemaName(schemaNameDeclaration);
//
//		assertNotNull("The abstract schema name should not be null", declaration);
//
//		assertSame
//		(
//			"The schema name declaration should be the one that was manually created",
//			declaration,
//			updateClause.getRangeVariableDeclaration()
//		);
//
//		assertEquals(schemaNameDeclaration, declaration.toParsedText());
//	}
//
//	@Test
//	public void testManualCreation_03()
//	{
//		JPQLExpression expression = new JPQLExpression(IJPAVersion.VERSION_2_0);
//
//		UpdateStatement updateStatement = expression.addUpdateStatement();
//		assertNotNull("UpdateStatement should not be null", updateStatement);
//
//		UpdateClause updateClause = updateStatement.getUpdateClause();
//		assertNotNull("UpdateClause should not be null", updateClause);
//
//		String schemaNameDeclaration = "Employee AS e";
//
//		RangeVariableDeclaration declaration = updateClause.addAbstractSchemaName
//		(
//			schemaNameDeclaration
//		);
//
//		assertNotNull("The abstract schema name should not be null", declaration);
//
//		assertSame
//		(
//			"The schema name declaration should be the one that was manually created",
//			declaration,
//			updateClause.getRangeVariableDeclaration()
//		);
//
//		assertEquals(schemaNameDeclaration, declaration.toParsedText());
//	}
//
//	private void testManualCreation_03(String stateFieldPath, String value)
//	{
//		JPQLExpression expression = new JPQLExpression(IJPAVersion.VERSION_2_0);
//
//		UpdateStatement updateStatement = expression.addUpdateStatement();
//		assertNotNull("UpdateStatement should not be null", updateStatement);
//
//		UpdateClause updateClause = updateStatement.getUpdateClause();
//		assertNotNull("UpdateClause should not be null", updateClause);
//
//		UpdateItem updateItem = updateClause.addUpdateItem(stateFieldPath, value);
//
//		assertNotNull("The UpdateItem should not be null", updateItem);
//
//		assertSame
//		(
//			"The UpdateItem should be the one that was manually created",
//			updateItem,
//			updateClause.getUpdateItems()
//		);
//
//		assertEquals
//		(
//			stateFieldPath + " = " + value,
//			updateItem.toParsedText()
//		);
//	}
//
//	@Test
//	public void testManualCreation_04()
//	{
//		testManualCreation_02("Employee AS e");
//	}
//
//	private void testManualCreation_04(String... updateItems)
//	{
//		JPQLExpression expression = new JPQLExpression(IJPAVersion.VERSION_2_0);
//
//		UpdateStatement updateStatement = expression.addUpdateStatement();
//		assertNotNull("UpdateStatement should not be null", updateStatement);
//
//		UpdateClause updateClause = updateStatement.getUpdateClause();
//		assertNotNull("UpdateClause should not be null", updateClause);
//
//		Expression updateItemsExpression = updateClause.addUpdateItems(updateItems);
//
//		assertNotNull
//		(
//			"The UpdateItems should not be null",
//			updateItemsExpression
//		);
//
//		assertSame
//		(
//			"The UpdateItems should be the one that was manually created",
//			updateItemsExpression,
//			updateClause.getUpdateItems()
//		);
//
//		assertTrue(updateItemsExpression instanceof CollectionExpression);
//	}
//
//	@Test
//	public void testManualCreation_05()
//	{
//		testManualCreation_02("Employee e");
//	}
//
//	@Test
//	public void testManualCreation_06()
//	{
//		testManualCreation_03("e.name", "'Pascal'");
//	}
//
//	@Test
//	public void testManualCreation_07()
//	{
//		testManualCreation_04("e.name = 'Pascal'", "e.hired = TRUE");
//	}
}