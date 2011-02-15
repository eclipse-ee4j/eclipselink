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

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@SuiteClasses
({
	// Basic
	WordParserTest.class,
	JPQLExpressionTest.class,

	// JPA version 1.0
	SelectStatementTest.class,
	SelectClauseTest.class,
	ConstructorExpressionTest.class,
	StateFieldPathExpressionTest.class,
	ConditionalExpressionTest.class,
	UpdateItemTest.class,
	UpdateClauseTest.class,
	OrderByClauseTest.class,
	DeleteClauseTest.class,
	HavingClauseTest.class,
	WhereClauseTest.class,
	OrderByItemTest.class,
	RangeVariableDeclarationTest.class,
	IdentificationVariableDeclarationTest.class,
	CollectionMemberDeclarationTest.class,
	GroupByClauseTest.class,
	SimpleSelectStatementTest.class,
	ComparisonExpressionTest.class,
	ExistsExpressionTest.class,
	StringLiteralTest.class,
	LikeExpressionTest.class,
	AllOrAnyExpressionTest.class,
	NullComparisonExpressionTest.class,
	EmptyCollectionComparisonExpressionTest.class,
	CollectionMemberExpressionTest.class,
	InExpressionTest.class,
	BetweenExpressionTest.class,
	DateTimeTest.class,
	ConcatExpressionTest.class,
	AvgFunctionTest.class,
	MinFunctionTest.class,
	MaxFunctionTest.class,
	SumFunctionTest.class,
	CountFunctionTest.class,
	LowerExpressionTest.class,
	ObjectExpressionTest.class,
	UpperExpressionTest.class,
	LengthExpressionTest.class,
	LocateExpressionTest.class,
	AbsExpressionTest.class,
	SqrtExpressionTest.class,
	SizeExpressionTest.class,
	InputParameterTest.class,
	JoinTest.class,
	KeywordExpressionTest.class,
	SubstringExpressionTest.class,
	TrimExpressionTest.class,
	ModExpressionTest.class,
	NumericLiteralTest.class,
	NotExpressionTest.class,
	SubExpressionTest.class,
	AbstractSchemaNameTest.class,

	// JPA version 2.0
	TypeExpressionTest.class,
	NullIfExpressionTest.class,
	IndexExpressionTest.class,
	CoalesceExpressionTest.class,
	CaseExpressionTest.class,

	// EclipseLink's extension
	FuncExpressionTest.class,

	// Queries
	NonTolerantJPQLQueriesTest.class,
	TolerantJPQLQueriesTest.class,

	// Generic
	JPQLTests.class,
	BadExpressionTest.class
})
@RunWith(Suite.class)
public final class AllJPQLExpressionTests
{
	private AllJPQLExpressionTests()
	{
		super();
	}
}