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

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@SuiteClasses
({
	// Basic
	ExpressionToolsTest.class,
	JPQLExpressionTest.class,
	WordParserTest.class,

	// JPA 1.0
	AbsExpressionTest.class,
	AbstractSchemaNameTest.class,
	AllOrAnyExpressionTest.class,
	AvgFunctionTest.class,
	BetweenExpressionTest.class,
	CollectionMemberDeclarationTest.class,
	CollectionMemberExpressionTest.class,
	ComparisonExpressionTest.class,
	ConcatExpressionTest.class,
	ConditionalExpressionTest.class,
	ConstructorExpressionTest.class,
	CountFunctionTest.class,
	DateTimeTest.class,
	DeleteClauseTest.class,
	EmptyCollectionComparisonExpressionTest.class,
	ExistsExpressionTest.class,
	GroupByClauseTest.class,
	HavingClauseTest.class,
	IdentificationVariableDeclarationTest.class,
	InExpressionTest.class,
	InputParameterTest.class,
	JoinTest.class,
	KeywordExpressionTest.class,
	LengthExpressionTest.class,
	LikeExpressionTest.class,
	LocateExpressionTest.class,
	LowerExpressionTest.class,
	MaxFunctionTest.class,
	MinFunctionTest.class,
	ModExpressionTest.class,
	NotExpressionTest.class,
	NullComparisonExpressionTest.class,
	NumericLiteralTest.class,
	ObjectExpressionTest.class,
	OrderByClauseTest.class,
	OrderByItemTest.class,
	RangeVariableDeclarationTest.class,
	ResultVariableTest.class,
	SelectClauseTest.class,
	SelectStatementTest.class,
	SimpleSelectStatementTest.class,
	SizeExpressionTest.class,
	SqrtExpressionTest.class,
	StateFieldPathExpressionTest.class,
	StringLiteralTest.class,
	SubExpressionTest.class,
	SubstringExpressionTest.class,
	SumFunctionTest.class,
	TrimExpressionTest.class,
	UpdateClauseTest.class,
	UpdateItemTest.class,
	UpperExpressionTest.class,
	WhereClauseTest.class,

	// JPA 2.0
	CaseExpressionTest.class,
	CoalesceExpressionTest.class,
	IndexExpressionTest.class,
	NullIfExpressionTest.class,
	TypeExpressionTest.class,

	// EclipseLink's extension
	FuncExpressionTest.class,
	TreatExpressionTest.class,

	// JPQL Queries
	JPQLQueriesTest.class,

	// Test invalid JPQL queries
	BadExpressionTest.class
})
@RunWith(Suite.class)
public final class AllParserTests {

	private AllParserTests() {
		super();
	}
}