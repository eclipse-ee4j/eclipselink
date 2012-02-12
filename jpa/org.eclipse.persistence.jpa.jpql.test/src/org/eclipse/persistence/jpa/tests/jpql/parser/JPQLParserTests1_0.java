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
package org.eclipse.persistence.jpa.tests.jpql.parser;

import org.junit.runners.Suite.SuiteClasses;

/**
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
@SuiteClasses
({
	// Test the parser with hundreds of JPQL queries
	JPQLQueriesTest1_0.class,

	// Individual unit-tests
	AbsExpressionTest.class,
	AbstractSchemaNameTest.class,
	AllOrAnyExpressionTest.class,
	ArithmeticExpressionTest.class,
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
	JPQLExpressionTest1_0.class,
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
	RangeVariableDeclarationTest.class,
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
})
public final class JPQLParserTests1_0 {

	private JPQLParserTests1_0() {
		super();
	}
}