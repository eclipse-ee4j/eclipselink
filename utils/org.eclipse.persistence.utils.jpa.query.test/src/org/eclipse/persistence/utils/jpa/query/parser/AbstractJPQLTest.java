/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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

import java.util.Arrays;
import org.eclipse.persistence.utils.jpa.query.parser.JPQLTests.QueryStringFormatter;
import org.eclipse.persistence.utils.jpa.query.parser.OrderByItem.Ordering;
import org.eclipse.persistence.utils.jpa.query.parser.TrimExpression.Specification;

import static org.eclipse.persistence.utils.jpa.query.parser.JPQLTests.*;
import static org.junit.Assert.*;

@SuppressWarnings("nls")
abstract class AbstractJPQLTest
{
	static AbsExpressionTester abs(ExpressionTester simpleArithmeticExpression)
	{
		return new AbsExpressionTester(simpleArithmeticExpression);
	}

	static ExpressionTester abstractSchemaName(String abstractSchemaName)
	{
		return new AbstractSchemaNameTester(abstractSchemaName);
	}

	static ExpressionTester add(ExpressionTester leftExpression,
	                            ExpressionTester rightExpression)
	{
		return new AdditionExpressionTester(leftExpression, rightExpression);
	}

	static ExpressionTester all(ExpressionTester subquery)
	{
		return new AllOrAnyExpressionTester(Expression.ALL, subquery);
	}

	static ExpressionTester and(ExpressionTester leftExpression,
	                            ExpressionTester rightExpression)
	{
		return new AndExpressionTester(leftExpression, rightExpression);
	}

	static ExpressionTester any(ExpressionTester subquery)
	{
		return new AllOrAnyExpressionTester(Expression.ANY, subquery);
	}

	static ExpressionTester anyExpression(ExpressionTester subquery)
	{
		return new AllOrAnyExpressionTester(Expression.ANY, subquery);
	}

	static ExpressionTester avg(String statefieldPathExpression)
	{
		return new AvgFunctionTester
		(
			path(statefieldPathExpression),
			false
		);
	}

	static ExpressionTester avgDistinct(String statefieldPathExpression)
	{
		return new AvgFunctionTester
		(
			path(statefieldPathExpression),
			true
		);
	}

	static ExpressionTester between(ExpressionTester expression,
	                               ExpressionTester lowerBoundExpression,
	                               ExpressionTester upperBoundExpression)
	{
		return new BetweenExpressionTester
		(
			expression,
			false,
			lowerBoundExpression,
			upperBoundExpression
		);
	}

	static ExpressionTester collection(ExpressionTester... expressions)
	{
		Boolean[] spaces = new Boolean[expressions.length];
		Boolean[] commas = new Boolean[expressions.length];

		Arrays.fill(spaces, 0, expressions.length - 1, Boolean.TRUE);
		Arrays.fill(commas, 0, expressions.length - 1, Boolean.TRUE);

		spaces[expressions.length - 1] = Boolean.FALSE;
		commas[expressions.length - 1] = Boolean.FALSE;

		return collection(expressions, spaces, commas);
	}

	static ExpressionTester collection(ExpressionTester[] expressions,
	                                   Boolean[] spaces,
	                                   Boolean[] commas)
	{
		return new CollectionExpressionTester(expressions, spaces, commas);
	}

	static ExpressionTester collectionValuedPath(String collectionValuedPathExpression)
	{
		return new CollectionValuedPathExpressionTester(collectionValuedPathExpression);
	}

	private static ExpressionTester comparison(ExpressionTester leftExpression,
	                                           String comparator,
	                                           ExpressionTester rightExpression)
	{
		return new ComparisonExpressionTester
		(
			comparator,
			leftExpression,
			rightExpression
		);
	}

	static ExpressionTester concat(ExpressionTester stringPrimary1,
	                              ExpressionTester stringPrimary2)
	{
		return new ConcatExpressionTester(stringPrimary1, stringPrimary2);
	}

	static ExpressionTester count(ExpressionTester statefieldPathExpression)
	{
		return new CountFunctionTester(statefieldPathExpression, false);
	}

	static ExpressionTester count(String statefieldPathExpression)
	{
		return count(path(statefieldPathExpression));
	}

	static ExpressionTester countDistinct(ExpressionTester statefieldPathExpression)
	{
		return new CountFunctionTester(statefieldPathExpression, true);
	}

	static ExpressionTester CURRENT_DATE()
	{
		return new DateTimeTester(Expression.CURRENT_DATE);
	}

	static ExpressionTester CURRENT_TIME()
	{
		return new DateTimeTester(Expression.CURRENT_TIME);
	}

	static ExpressionTester CURRENT_TIMESTAMP()
	{
		return new DateTimeTester(Expression.CURRENT_TIMESTAMP);
	}

	static ExpressionTester dateTime(String jdbcEscapeFormat)
	{
		return new DateTimeTester(jdbcEscapeFormat);
	}

	static ExpressionTester delete(String abstractSchemaName,
	                              String identificationVariable)
	{
		return new DeleteClauseTester
		(
			rangeVariableDeclaration(abstractSchemaName, identificationVariable)
		);
	}

	static ExpressionTester deleteStatement(ExpressionTester updateClause)
	{
		return deleteStatement(updateClause, nullExpression());
	}

	static ExpressionTester deleteStatement(ExpressionTester updateClause,
	                                       ExpressionTester whereClause)
	{
		return new DeleteStatementTester(updateClause, whereClause);
	}

	static ExpressionTester deleteStatement(String abstractSchemaName,
	                                       String identificationVariable)
	{
		return deleteStatement
		(
			delete(abstractSchemaName, identificationVariable)
		);
	}

	static ExpressionTester deleteStatement(String abstractSchemaName,
	                                              String identificationVariable,
	                                              ExpressionTester whereClause)
	{
		return deleteStatement
		(
			delete(abstractSchemaName, identificationVariable),
			whereClause
		);
	}

	static ExpressionTester different(ExpressionTester leftExpression,
	                                        ExpressionTester rightExpression)
	{
		return comparison
		(
			leftExpression,
			Expression.DIFFERENT,
			rightExpression
		);
	}

	static ExpressionTester division(ExpressionTester leftExpression,
	                                ExpressionTester rightExpression)
	{
		return new DivisionExpressionTester(leftExpression, rightExpression);
	}

	static ExpressionTester entity(String entity)
	{
		return new EntityTypeLiteralTester(entity);
	}

	static ExpressionTester equal(ExpressionTester leftExpression,
	                             ExpressionTester rightExpression)
	{
		return comparison
		(
			leftExpression,
			Expression.EQUAL,
			rightExpression
		);
	}

	static ExpressionTester exists(ExpressionTester subquery)
	{
		return new ExistsExpressionTester(subquery, false);
	}

	static ExpressionTester FALSE()
	{
		return new KeywordExpressionTester(Expression.FALSE);
	}

	static ExpressionTester from(ExpressionTester declaration)
	{
		return new FromClauseTester(declaration);
	}

	static ExpressionTester from(ExpressionTester... declarations)
	{
		return new FromClauseTester(collection(declarations));
	}

	/**
	 * Example: from("Employee", "e", "Product", "p")
	 */
	static ExpressionTester from(String... declarations)
	{
		ExpressionTester[] identificationVariableDeclarations = new ExpressionTester[declarations.length / 2];

		for (int index = 0, count = declarations.length; index + 1 < count; index += 2)
		{
			identificationVariableDeclarations[index / 2] = identificationVariableDeclaration
			(
				declarations[index],
				declarations[index + 1]
			);
		}

		return from(identificationVariableDeclarations);
	}

	/**
	 * Example: from("Employee", "e")
	 */
	static ExpressionTester from(String abstractSchemaName,
	                             String identificationVariable)
	{
		return from(fromEntity(abstractSchemaName, identificationVariable));
	}

	static ExpressionTester from(String abstractSchemaName,
	                             String identificationVariable,
	                             ExpressionTester... joins)
	{
		return from
		(
			identificationVariableDeclaration
			(
				abstractSchemaName,
				identificationVariable,
				joins
			)
		);
	}

	static ExpressionTester from(String abstractSchemaName,
	                             String identificationVariable,
	                             ExpressionTester joins)
	{
		return from
		(
			fromEntity
			(
				abstractSchemaName,
				identificationVariable,
				joins
			)
		);
	}

	static ExpressionTester fromAs(String abstractSchemaName,
	                              String identificationVariable)
	{
		return from
		(
			identificationVariableDeclarationAs
			(
				abstractSchemaName,
				identificationVariable
			)
		);
	}

	/**
	 * Example: from("e.employees", "e")
	 */
	static ExpressionTester fromCollection(String collectionValuedPathExpression,
	                                       String identificationVariable)
	{
		return identificationVariableDeclaration
		(
			rangeVariableDeclaration
			(
				collectionValuedPath(collectionValuedPathExpression),
				variable(identificationVariable)
			),
			nullExpression()
		);
	}

	static ExpressionTester fromEntity(String abstractSchemaName,
	                                   String identificationVariable)
	{
		return identificationVariableDeclaration
		(
			abstractSchemaName,
			identificationVariable
		);
	}

	static ExpressionTester fromEntity(String abstractSchemaName,
	                                   String identificationVariable,
	                                   ExpressionTester... joins)
	{
		return identificationVariableDeclaration
		(
			abstractSchemaName,
			identificationVariable,
			joins
		);
	}

	static ExpressionTester fromEntity(String abstractSchemaName,
	                                   String identificationVariable,
	                                   ExpressionTester join)
	{
		return identificationVariableDeclaration
		(
			abstractSchemaName,
			identificationVariable,
			join
		);
	}

	static ExpressionTester fromEntityAs(String abstractSchemaName,
	                                     String identificationVariable)
	{
		return identificationVariableDeclarationAs
		(
			abstractSchemaName,
			identificationVariable
		);
	}

	static ExpressionTester fromEntityAs(String abstractSchemaName,
	                                     String identificationVariable,
	                                     ExpressionTester... joins)
	{
		return identificationVariableDeclarationAs
		(
			abstractSchemaName,
			identificationVariable,
			joins
		);
	}

	static ExpressionTester fromEntityAs(String abstractSchemaName,
	                                     String identificationVariable,
	                                     ExpressionTester join)
	{
		return identificationVariableDeclarationAs
		(
			abstractSchemaName,
			identificationVariable,
			join
		);
	}

	static ExpressionTester fromIn(String collectionValuedPathExpression,
	                               String identificationVariable)
	{
		return new CollectionMemberDeclarationTester
		(
			collectionValuedPath(collectionValuedPathExpression),
			false,
			variable(identificationVariable)
		);
	}

	static ExpressionTester fromInAs(String collectionValuedPathExpression,
	                                 String identificationVariable)
	{
		return new CollectionMemberDeclarationTester
		(
			collectionValuedPath(collectionValuedPathExpression),
			true,
			variable(identificationVariable)
		);
	}

	static ExpressionTester func(ExpressionTester... funcItems)
	{
		return new FuncExpressionTester(collection(funcItems));
	}

	static ExpressionTester func(ExpressionTester funcItem)
	{
		return new FuncExpressionTester(funcItem);
	}

	static ExpressionTester greaterThan(ExpressionTester leftExpression,
	                                   ExpressionTester rightExpression)
	{
		return comparison
		(
			leftExpression,
			Expression.GREATER_THAN,
			rightExpression
		);
	}

	static ExpressionTester greaterThanOrEqual(ExpressionTester leftExpression,
	                                          ExpressionTester rightExpression)
	{
		return comparison
		(
			leftExpression,
			Expression.GREATER_THAN_OR_EQUAL,
			rightExpression
		);
	}

	static ExpressionTester groupBy(ExpressionTester groupByItem)
	{
		return new GroupByClauseTester(groupByItem);
	}

	static ExpressionTester groupBy(ExpressionTester... groupByItems)
	{
		return new GroupByClauseTester
		(
			collection(groupByItems)
		);
	}

	static ExpressionTester having(ExpressionTester havingItem)
	{
		return new HavingClauseTester(havingItem);
	}

	static ExpressionTester identificationVariableDeclaration(ExpressionTester rangeVariableDeclaration)
	{
		return new IdentificationVariableDeclarationTester
		(
			rangeVariableDeclaration,
			nullExpression()
		);
	}

	static ExpressionTester identificationVariableDeclaration(ExpressionTester rangeVariableDeclaration,
	                                                          ExpressionTester... joins)
	{
		return new IdentificationVariableDeclarationTester
		(
			rangeVariableDeclaration,
			collection(joins)
		);
	}

	static ExpressionTester identificationVariableDeclaration(ExpressionTester rangeVariableDeclaration,
	                                                          ExpressionTester joins)
	{
		return new IdentificationVariableDeclarationTester
		(
			rangeVariableDeclaration,
			joins
		);
	}

	static ExpressionTester identificationVariableDeclaration(String abstractSchemaName,
	                                                          String identificationVariable)
	{
		return identificationVariableDeclaration
		(
			rangeVariableDeclaration(abstractSchemaName, identificationVariable),
			nullExpression()
		);
	}

	static ExpressionTester identificationVariableDeclaration(String abstractSchemaName,
	                                                          String identificationVariable,
	                                                          ExpressionTester... joins)
	{
		return new IdentificationVariableDeclarationTester
		(
			rangeVariableDeclaration(abstractSchemaName, identificationVariable),
			spacedCollection(joins)
		);
	}

	static ExpressionTester identificationVariableDeclaration(String abstractSchemaName,
	                                                          String identificationVariable,
	                                                          ExpressionTester join)
	{
		return new IdentificationVariableDeclarationTester
		(
			rangeVariableDeclaration(abstractSchemaName, identificationVariable),
			join
		);
	}

	static ExpressionTester identificationVariableDeclarationAs(String abstractSchemaName,
	                                                            String identificationVariable)
	{
		return new IdentificationVariableDeclarationTester
		(
			rangeVariableDeclarationAs(abstractSchemaName, identificationVariable),
			nullExpression()
		);
	}

	static ExpressionTester identificationVariableDeclarationAs(String abstractSchemaName,
	                                                            String identificationVariable,
	                                                            ExpressionTester join)
	{
		return new IdentificationVariableDeclarationTester
		(
			rangeVariableDeclarationAs(abstractSchemaName, identificationVariable),
			join
		);
	}

	static ExpressionTester identificationVariableDeclarationAs(String abstractSchemaName,
	                                                            String identificationVariable,
	                                                            ExpressionTester... joins)
	{
		return new IdentificationVariableDeclarationTester
		(
			rangeVariableDeclarationAs(abstractSchemaName, identificationVariable),
			spacedCollection(joins)
		);
	}

	static InExpressionTester in(ExpressionTester stateFieldPathExpression,
	                             ExpressionTester... inItems)
	{
		return new InExpressionTester
		(
			stateFieldPathExpression,
			false,
			collection(inItems)
		);
	}

	static InExpressionTester in(ExpressionTester stateFieldPathExpression,
	                             ExpressionTester inItems)
	{
		return new InExpressionTester(stateFieldPathExpression, false, inItems);
	}

	static InExpressionTester in(String stateFieldPathExpression,
	                             ExpressionTester... inItems)
	{
		return in
		(
			path(stateFieldPathExpression),
			inItems
		);
	}

	static InExpressionTester in(String stateFieldPathExpression,
	                             ExpressionTester inItem)
	{
		return in
		(
			path(stateFieldPathExpression),
			inItem
		);
	}

	static ExpressionTester index(String identificationVariable)
	{
		return new IndexExpressionTester
		(
			variable(identificationVariable)
		);
	}

	static ExpressionTester innerJoin(String collectionValuedPathExpression,
	                                        String identificationVariable)
	{
		return join
		(
			Join.Type.INNER_JOIN,
			collectionValuedPathExpression,
			identificationVariable
		);
	}

	static ExpressionTester innerJoinFetch(String collectionValuedPathExpression)
	{
		return joinFetch
		(
			JoinFetch.Type.INNER_JOIN_FETCH,
			collectionValuedPathExpression
		);
	}

	static ExpressionTester inputParameter(String inputParameter)
	{
		return new InputParameterTester(inputParameter);
	}

	static ExpressionTester isEmpty(String collectionValuedPathExpression)
	{
		return new EmptyExpressionTester
		(
			collectionValuedPath(collectionValuedPathExpression),
			false
		);
	}

	static ExpressionTester isNotEmpty(String collectionValuedPathExpression)
	{
		return new EmptyExpressionTester
		(
			collectionValuedPath(collectionValuedPathExpression),
			true
		);
	}

	static ExpressionTester isNotNull(ExpressionTester expression)
	{
		return new NullComparisonExpressionTester(expression, true);
	}

	static ExpressionTester isNull(ExpressionTester expression)
	{
		return new NullComparisonExpressionTester(expression, false);
	}

	private static ExpressionTester join(Join.Type joinType,
	                                     String collectionValuedPathExpression,
	                                     String identificationVariable)
	{
		return new JoinTester
		(
			joinType,
			collectionValuedPath(collectionValuedPathExpression),
			false,
			variable(identificationVariable)
		);
	}

	static ExpressionTester join(String collectionValuedPathExpression,
	                            String identificationVariable)
	{
		return join
		(
			Join.Type.JOIN,
			collectionValuedPathExpression,
			identificationVariable
		);
	}

	static ExpressionTester joinAs(String collectionValuedPathExpression,
	                              String identificationVariable)
	{
		return new JoinTester
		(
			Join.Type.JOIN,
			collectionValuedPath(collectionValuedPathExpression),
			true,
			variable(identificationVariable)
		);
	}

	private static ExpressionTester joinFetch(JoinFetch.Type joinType,
	                                          String collectionValuedPathExpression)
	{
		return new JoinFetchTester
		(
			joinType,
			collectionValuedPath(collectionValuedPathExpression)
		);
	}

	static ExpressionTester joinFetch(String collectionValuedPathExpression)
	{
		return joinFetch
		(
			JoinFetch.Type.JOIN_FETCH,
			collectionValuedPathExpression
		);
	}

	static ExpressionTester jpqlExpression(ExpressionTester queryStatement)
	{
		return new JPQLExpressionTester(queryStatement);
	}

	static ExpressionTester leftJoin(String collectionValuedPathExpression,
	                                String identificationVariable)
	{
		return join
		(
			Join.Type.LEFT_JOIN,
			collectionValuedPathExpression,
			identificationVariable
		);
	}

	static ExpressionTester leftJoinFetch(String collectionValuedPathExpression)
	{
		return joinFetch
		(
			JoinFetch.Type.LEFT_JOIN_FETCH,
			collectionValuedPathExpression
		);
	}

	static ExpressionTester leftOuterJoin(String collectionValuedPathExpression,
	                                     String identificationVariable)
	{
		return join
		(
			Join.Type.LEFT_OUTER_JOIN,
			collectionValuedPathExpression,
			identificationVariable
		);
	}

	static ExpressionTester leftOuterJoinFetch(String collectionValuedPathExpression)
	{
		return joinFetch
		(
			JoinFetch.Type.LEFT_OUTER_JOIN_FETCH,
			collectionValuedPathExpression
		);
	}

	static ExpressionTester length(ExpressionTester stringPrimary)
	{
		return new LengthExpressionTester(stringPrimary);
	}

	static ExpressionTester like(ExpressionTester stringExpression,
	                            ExpressionTester patternValue)
	{
		return like(stringExpression, patternValue, nullExpression());
	}

	static ExpressionTester like(ExpressionTester stringExpression,
	                            ExpressionTester patternValue,
	                            char escapeCharacter)
	{
		return like
		(
			stringExpression,
			patternValue,
			string("'" + escapeCharacter + "'")
		);
	}

	static ExpressionTester like(ExpressionTester stringExpression,
	                            ExpressionTester patternValue,
	                            ExpressionTester escapeCharacter)
	{
		return new LikeExpressionTester
		(
			stringExpression,
			false,
			patternValue,
			escapeCharacter
		);
	}

	static ExpressionTester locate(ExpressionTester firstExpression,
	                              ExpressionTester secondExpression)
	{
		return locate(firstExpression, secondExpression, nullExpression());
	}

	static ExpressionTester locate(ExpressionTester firstExpression,
	                              ExpressionTester secondExpression,
	                              ExpressionTester thirdExpression)
	{
		return new LocateExpressionTester
		(
			firstExpression,
			secondExpression,
			thirdExpression
		);
	}

	static ExpressionTester lower(ExpressionTester stringPrimary)
	{
		return new LowerExpressionTester(stringPrimary);
	}

	static ExpressionTester lowerThan(ExpressionTester leftExpression,
	                                 ExpressionTester rightExpression)
	{
		return comparison
		(
			leftExpression,
			Expression.LOWER_THAN,
			rightExpression
		);
	}

	static ExpressionTester lowerThanOrEqual(ExpressionTester leftExpression,
	                                        ExpressionTester rightExpression)
	{
		return comparison
		(
			leftExpression,
			Expression.LOWER_THAN_OR_EQUAL,
			rightExpression
		);
	}

	static ExpressionTester max(String statefieldPathExpression)
	{
		return new MaxFunctionTester
		(
			path(statefieldPathExpression),
			false
		);
	}

	static ExpressionTester maxDistinct(String statefieldPathExpression)
	{
		return new MaxFunctionTester
		(
			path(statefieldPathExpression),
			true
		);
	}

	static ExpressionTester member(ExpressionTester entityExpression,
	                              ExpressionTester collectionValuedPathExpression)
	{
		return new CollectionMemberExpressionTester
		(
			entityExpression,
			false,
			false,
			collectionValuedPathExpression
		);
	}

	static ExpressionTester member(ExpressionTester entityExpression,
	                              String collectionValuedPathExpression)
	{
		return member
		(
			entityExpression,
			collectionValuedPath(collectionValuedPathExpression)
		);
	}

	static ExpressionTester member(String identificationVariable,
	                              String collectionValuedPathExpression)
	{
		return member
		(
			variable(identificationVariable),
			collectionValuedPathExpression
		);
	}

	static ExpressionTester memberOf(ExpressionTester entityExpression,
	                                ExpressionTester collectionValuedPathExpression)
	{
		return new CollectionMemberExpressionTester
		(
			entityExpression,
			false,
			true,
			collectionValuedPathExpression
		);
	}

	static ExpressionTester memberOf(ExpressionTester entityExpression,
	                                String collectionValuedPathExpression)
	{
		return memberOf
		(
			entityExpression,
			collectionValuedPath(collectionValuedPathExpression)
		);
	}

	static ExpressionTester memberOf(String identificationVariable,
	                                String collectionValuedPathExpression)
	{
		return memberOf
		(
			variable(identificationVariable),
			collectionValuedPathExpression
		);
	}

	static ExpressionTester min(String statefieldPathExpression)
	{
		return new MinFunctionTester
		(
			path(statefieldPathExpression),
			false
		);
	}

	static ExpressionTester minDistinct(String statefieldPathExpression)
	{
		return new MinFunctionTester
		(
			path(statefieldPathExpression),
			true
		);
	}

	static ExpressionTester mod(ExpressionTester simpleArithmeticExpression1,
	                           ExpressionTester simpleArithmeticExpression2)
	{
		return new ModExpressionTester
		(
			simpleArithmeticExpression1,
			simpleArithmeticExpression2
		);
	}

	static ExpressionTester multiplication(ExpressionTester leftExpression,
	                                      ExpressionTester rightExpression)
	{
		return new MultiplicationExpressionTester(leftExpression, rightExpression);
	}

	static ExpressionTester new_(String className,
	                            ExpressionTester constructorItem)
	{
		return new ConstructorExpressionTester(className, constructorItem);
	}

	static ExpressionTester new_(String className,
	                            ExpressionTester... constructorItems)
	{
		return new ConstructorExpressionTester
		(
			className,
			collection(constructorItems)
		);
	}

	static ExpressionTester not(ExpressionTester expression)
	{
		return new NotExpressionTester(expression);
	}

	static ExpressionTester notBetween(ExpressionTester expression,
	                                  ExpressionTester lowerBoundExpression,
	                                  ExpressionTester upperBoundExpression)
	{
		return new BetweenExpressionTester
		(
			expression,
			true,
			lowerBoundExpression,
			upperBoundExpression
		);
	}

	static ExpressionTester notExists(ExpressionTester subquery)
	{
		return new ExistsExpressionTester(subquery, true);
	}

	static ExpressionTester notIn(ExpressionTester stateFieldPathExpression,
	                             ExpressionTester inItems)
	{
		return new InExpressionTester(stateFieldPathExpression, true, inItems);
	}

	static ExpressionTester notIn(ExpressionTester stateFieldPathExpression,
	                             ExpressionTester... inItems)
	{
		return new InExpressionTester
		(
			stateFieldPathExpression,
			true,
			collection(inItems)
		);
	}

	static ExpressionTester notIn(String stateFieldPathExpression,
	                             ExpressionTester... inItems)
	{
		return notIn
		(
			path(stateFieldPathExpression),
			collection(inItems)
		);
	}

	static ExpressionTester notIn(String stateFieldPathExpression,
	                             ExpressionTester inItem)
	{
		return notIn
		(
			path(stateFieldPathExpression),
			inItem
		);
	}

	static ExpressionTester notLike(ExpressionTester stringExpression,
	                               ExpressionTester patternValue)
	{
		return notLike
		(
			stringExpression,
			patternValue,
			nullExpression()
		);
	}

	static ExpressionTester notLike(ExpressionTester stringExpression,
	                               ExpressionTester patternValue,
	                               ExpressionTester escapeCharacter)
	{
		return new LikeExpressionTester
		(
			stringExpression,
			true,
			patternValue,
			escapeCharacter
		);
	}

	static ExpressionTester notMember(ExpressionTester entityExpression,
	                                 ExpressionTester collectionValuedPathExpression)
	{
		return new CollectionMemberExpressionTester
		(
			entityExpression,
			true,
			false,
			collectionValuedPathExpression
		);
	}

	static ExpressionTester notMember(ExpressionTester entityExpression,
	                                 String collectionValuedPathExpression)
	{
		return notMember
		(
			entityExpression,
			collectionValuedPathExpression
		);
	}

	static ExpressionTester notMember(String identificationVariable,
	                                 String collectionValuedPathExpression)
	{
		return notMember
		(
			variable(identificationVariable),
			collectionValuedPathExpression
		);
	}

	static ExpressionTester notMemberOf(ExpressionTester entityExpression,
	                                   ExpressionTester collectionValuedPathExpression)
	{
		return new CollectionMemberExpressionTester
		(
			entityExpression,
			true,
			true,
			collectionValuedPathExpression
		);
	}

	static ExpressionTester NULL()
	{
		return new KeywordExpressionTester(Expression.NULL);
	}

	static ExpressionTester nullExpression()
	{
		return new NullExpressionTester();
	}

	static NullIfExpressionTester nullIf(ExpressionTester expression1,
	                                     ExpressionTester expression2)
	{
		return new NullIfExpressionTester(expression1, expression2);
	}

	static ExpressionTester numeric(double number)
	{
		return numeric(String.valueOf(number));
	}

	static ExpressionTester numeric(long number)
	{
		return numeric(String.valueOf(number));
	}

	static ExpressionTester numeric(String value)
	{
		return new NumericLiteralTester(value);
	}

	static ExpressionTester object(String identificationVariable)
	{
		return new ObjectTester(variable(identificationVariable));
	}

	static ExpressionTester or(ExpressionTester leftExpression,
	                          ExpressionTester rightExpression)
	{
		return new OrExpressionTester(leftExpression, rightExpression);
	}

	static ExpressionTester orderBy(ExpressionTester orderByItem)
	{
		return new OrderByClauseTester(orderByItem);
	}

	static ExpressionTester orderBy(ExpressionTester... orderByItems)
	{
		return new OrderByClauseTester(collection(orderByItems));
	}

	static ExpressionTester orderBy(String stateFieldPathExpression)
	{
		return new OrderByClauseTester(orderByItem(stateFieldPathExpression));
	}

	static ExpressionTester orderByItem(ExpressionTester orderByItem)
	{
		return orderByItem(orderByItem, Ordering.DEFAULT);
	}

	private static ExpressionTester orderByItem(ExpressionTester orderByItem,
	                                            Ordering ordering)
	{
		return new OrderByItemTester(orderByItem, ordering);
	}

	static ExpressionTester orderByItem(String stateFieldPathExpression)
	{
		return orderByItem(path(stateFieldPathExpression));
	}

	static ExpressionTester orderByItemAsc(ExpressionTester orderByItem)
	{
		return orderByItem(orderByItem, Ordering.ASC);
	}

	static ExpressionTester orderByItemAsc(String stateFieldPathExpression)
	{
		return orderByItemAsc(path(stateFieldPathExpression));
	}

	static ExpressionTester orderByItemDesc(ExpressionTester orderByItem)
	{
		return orderByItem(orderByItem, Ordering.DESC);
	}

	static ExpressionTester orderByItemDesc(String stateFieldPathExpression)
	{
		return orderByItemDesc(path(stateFieldPathExpression));
	}

	static ExpressionTester path(String stateFieldPathExpression)
	{
		return new StateFieldPathExpressionTester(stateFieldPathExpression);
	}

	private static ExpressionTester rangeVariableDeclaration(ExpressionTester abstractSchemaName,
	                                                         boolean hasAs,
	                                                         ExpressionTester identificationVariable)
	{
		return new RangeVariableDeclarationTester
		(
			abstractSchemaName,
			hasAs,
			identificationVariable
		);
	}

	static ExpressionTester rangeVariableDeclaration(ExpressionTester abstractSchemaName,
	                                                ExpressionTester identificationVariable)
	{
		return rangeVariableDeclaration
		(
			abstractSchemaName,
			false,
			identificationVariable
		);
	}

	static ExpressionTester rangeVariableDeclaration(String abstractSchemaName,
	                                                 String identificationVariable)
	{
		if (identificationVariable != null)
		{
			return rangeVariableDeclaration
			(
				abstractSchemaName(abstractSchemaName),
				false,
				variable(identificationVariable)
			);
		}
		else
		{
			return rangeVariableDeclaration
			(
				abstractSchemaName(abstractSchemaName),
				false,
				nullExpression()
			);
		}
	}

	static ExpressionTester rangeVariableDeclarationAs(String abstractSchemaName,
	                                                  String identificationVariable)
	{
		return rangeVariableDeclaration
		(
			abstractSchemaName(abstractSchemaName),
			true,
			variable(identificationVariable)
		);
	}

	static ExpressionTester select(ExpressionTester selectExpression)
	{
		return select(selectExpression, false);
	}

	static ExpressionTester select(ExpressionTester... selectExpressions)
	{
		return new SelectClauseTester
		(
			collection(selectExpressions),
			false
		);
	}

	private static ExpressionTester select(ExpressionTester selectExpression,
	                                       boolean hasDistinct)
	{
		return new SelectClauseTester(selectExpression, hasDistinct);
	}

	static ExpressionTester selectDistinct(ExpressionTester... selectExpressions)
	{
		return new SelectClauseTester
		(
			collection(selectExpressions),
			true
		);
	}

	static ExpressionTester selectDistinct(ExpressionTester selectExpression)
	{
		return new SelectClauseTester(selectExpression, true);
	}

	static ExpressionTester selectDisting(ExpressionTester selectExpression)
	{
		return select(selectExpression, true);
	}

	static ExpressionTester selectItem(ExpressionTester selectExpression,
	                                   String resultVariable)
	{
		return new ResultVariableTester
		(
			selectExpression,
			false,
			variable(resultVariable)
		);
	}

	static ExpressionTester selectItemAs(ExpressionTester selectExpression,
	                                     String resultVariable)
	{
		return new ResultVariableTester
		(
			selectExpression,
			true,
			variable(resultVariable)
		);
	}

	static ExpressionTester selectStatement(ExpressionTester selectClause,
	                                       ExpressionTester fromClause)
	{
		return selectStatement(selectClause, fromClause, nullExpression());
	}

	static ExpressionTester selectStatement(ExpressionTester selectClause,
	                                        ExpressionTester fromClause,
	                                        ExpressionTester whereClause)
	{
		return selectStatement
		(
			selectClause,
			fromClause,
			whereClause,
			nullExpression(),
			nullExpression(),
			nullExpression()
		);
	}

	static ExpressionTester selectStatement(ExpressionTester selectClause,
	                                        ExpressionTester fromClause,
	                                        ExpressionTester whereClause,
	                                        ExpressionTester groupByClause,
	                                        ExpressionTester havingClause,
	                                        ExpressionTester orderByClause)
	{
		return new SelectStatementTester
		(
			selectClause,
			fromClause,
			whereClause,
			groupByClause,
			havingClause,
			orderByClause
		);
	}

	static ExpressionTester set(ExpressionTester stateFieldPathExpression,
	                           ExpressionTester newValue)
	{
		return new UpdateItemTester(stateFieldPathExpression, newValue);
	}

	static ExpressionTester set(String stateFieldPathExpression,
	                           ExpressionTester newValue)
	{
		return set
		(
			path(stateFieldPathExpression),
			newValue
		);
	}

	static ExpressionTester size(String collectionValuedPathExpression)
	{
		return new SizeExpressionTester
		(
			collectionValuedPath(collectionValuedPathExpression)
		);
	}

	static ExpressionTester some(ExpressionTester subquery)
	{
		return new AllOrAnyExpressionTester(Expression.SOME, subquery);
	}

	static ExpressionTester spacedCollection(ExpressionTester... expressions)
	{
		Boolean[] spaces = new Boolean[expressions.length];
		Boolean[] commas = new Boolean[expressions.length];

		Arrays.fill(spaces, 0, expressions.length - 1, Boolean.TRUE);
		Arrays.fill(commas, 0, expressions.length, Boolean.FALSE);

		spaces[expressions.length - 1] = Boolean.FALSE;

		return collection(expressions, spaces, commas);
	}

	static ExpressionTester sqrt(ExpressionTester simpleArithmeticExpression)
	{
		return new SqrtExpressionTester(simpleArithmeticExpression);
	}

	static ExpressionTester string(String literal)
	{
		return new StringLiteralTester(literal);
	}

	static ExpressionTester subExpression(ExpressionTester expression)
	{
		return new SubExpressionTester(expression);
	}

	static ExpressionTester subFrom(ExpressionTester declaration)
	{
		return new SimpleFromClauseTester(declaration);
	}

	static ExpressionTester subFrom(ExpressionTester... declarations)
	{
		return new SimpleFromClauseTester(collection(declarations));
	}

	static ExpressionTester subFrom(String abstractSchemaName,
	                                String identificationVariable)
	{
		return subFrom
		(
			identificationVariableDeclaration
			(
				abstractSchemaName,
				identificationVariable
			)
		);
	}

	static ExpressionTester subFrom(String abstractSchemaName,
	                                String identificationVariable,
	                                ExpressionTester... joins)
	{
		return subFrom
		(
			identificationVariableDeclaration
			(
				abstractSchemaName,
				identificationVariable,
				joins
			)
		);
	}

	static ExpressionTester subFrom(String abstractSchemaName,
	                               String identificationVariable,
	                               ExpressionTester joins)
	{
		return subFrom
		(
			identificationVariableDeclaration
			(
				abstractSchemaName,
				identificationVariable,
				joins
			)
		);
	}

	static ExpressionTester subquery(ExpressionTester selectClause,
	                                ExpressionTester fromClause)
	{
		return subquery
		(
			selectClause,
			fromClause,
			nullExpression()
		);
	}

	static ExpressionTester subquery(ExpressionTester selectClause,
	                                ExpressionTester fromClause,
	                                ExpressionTester whereClause)
	{
		return subSelectStatement
		(
			selectClause,
			fromClause,
			whereClause
		);
	}

	static ExpressionTester subSelect(ExpressionTester selectExpression)
	{
		return subSelect(selectExpression, false);
	}

	static ExpressionTester subSelect(ExpressionTester... selectExpressions)
	{
		return new SimpleSelectClauseTester
		(
			collection(selectExpressions),
			false
		);
	}

	private static ExpressionTester subSelect(ExpressionTester selectExpression,
	                                          boolean hasDistinct)
	{
		return new SimpleSelectClauseTester(selectExpression, hasDistinct);
	}

	static ExpressionTester subSelectDistinct(ExpressionTester selectExpression)
	{
		return subSelect(selectExpression, true);
	}

	static ExpressionTester subSelectDistinct(ExpressionTester... selectExpressions)
	{
		return new SimpleSelectClauseTester
		(
			collection(selectExpressions),
			true
		);
	}

	static ExpressionTester subSelectStatement(ExpressionTester selectClause,
	                                          ExpressionTester fromClause,
	                                          ExpressionTester whereClause)
	{
		return subSelectStatement
		(
			selectClause,
			fromClause,
			whereClause,
			nullExpression(),
			nullExpression()
		);
	}

	static ExpressionTester subSelectStatement(ExpressionTester selectClause,
	                                          ExpressionTester fromClause,
	                                          ExpressionTester whereClause,
	                                          ExpressionTester groupByClause,
	                                          ExpressionTester havingClause)
	{
		return new SimpleSelectStatementTester
		(
			selectClause,
			fromClause,
			whereClause,
			groupByClause,
			havingClause
		);
	}

	static ExpressionTester substract(ExpressionTester leftExpression,
	                                 ExpressionTester rightExpression)
	{
		return new SubstractionExpressionTester(leftExpression, rightExpression);
	}

	static ExpressionTester substring(ExpressionTester firstExpression,
	                                 ExpressionTester secondExpression)
	{
		return substring(firstExpression, secondExpression, nullExpression());
	}

	static ExpressionTester substring(ExpressionTester firstExpression,
		                              ExpressionTester secondExpression,
		                              ExpressionTester thirdExpression)
	{
		return new SubstringExpressionTester
		(
			firstExpression,
			secondExpression,
			thirdExpression
		);
	}

	static ExpressionTester sum(String statefieldPathExpression)
	{
		return new SumFunctionTester
		(
			path(statefieldPathExpression),
			false
		);
	}

	static ExpressionTester sumDistinct(String statefieldPathExpression)
	{
		return new SumFunctionTester
		(
			path(statefieldPathExpression),
			true
		);
	}

	static ExpressionTester trim(ExpressionTester stringPrimary)
	{
		return new TrimExpressionTester
		(
			Specification.DEFAULT,
			stringPrimary,
			nullExpression(),
			false
		);
	}

	static ExpressionTester trimBothFrom(ExpressionTester stringPrimary)
	{
		return new TrimExpressionTester
		(
			Specification.BOTH,
			stringPrimary,
			nullExpression(),
			true
		);
	}

	static ExpressionTester trimFrom(ExpressionTester stringPrimary)
	{
		return new TrimExpressionTester
		(
			Specification.DEFAULT,
			stringPrimary,
			nullExpression(),
			true
		);
	}

	static ExpressionTester trimLeading(ExpressionTester stringPrimary)
	{
		return new TrimExpressionTester
		(
			Specification.LEADING,
			stringPrimary,
			nullExpression(),
			false
		);
	}

	static ExpressionTester trimLeadingFrom(ExpressionTester stringPrimary)
	{
		return new TrimExpressionTester
		(
			Specification.LEADING,
			stringPrimary,
			nullExpression(),
			true
		);
	}

	static ExpressionTester trimTrailing(ExpressionTester stringPrimary)
	{
		return new TrimExpressionTester
		(
			Specification.TRAILING,
			stringPrimary,
			nullExpression(),
			false
		);
	}

	static ExpressionTester trimTrailingFrom(ExpressionTester stringPrimary)
	{
		return new TrimExpressionTester
		(
			Specification.TRAILING,
			stringPrimary,
			nullExpression(),
			true
		);
	}

	static ExpressionTester TRUE()
	{
		return new KeywordExpressionTester(Expression.TRUE);
	}

	static ExpressionTester type(String identificationVariable)
	{
		return new TypeExpressionTester
		(
			variable(identificationVariable)
		);
	}

	static ExpressionTester update(String abstractSchemaName,
	                              ExpressionTester updateItem)
	{
		return new UpdateClauseTester
		(
			rangeVariableDeclaration(abstractSchemaName, null),
			updateItem
		);
	}

	static ExpressionTester update(String abstractSchemaName,
	                              String identificationVariable,
	                              ExpressionTester updateItem)
	{
		return new UpdateClauseTester
		(
			rangeVariableDeclaration(abstractSchemaName, identificationVariable),
			updateItem
		);
	}

	static ExpressionTester update(String abstractSchemaName,
	                              String identificationVariable,
	                              ExpressionTester... updateItems)
	{
		return new UpdateClauseTester
		(
			rangeVariableDeclaration(abstractSchemaName, identificationVariable),
			collection(updateItems)
		);
	}

	static ExpressionTester updateStatement(ExpressionTester updateClause)
	{
		return updateStatement(updateClause, nullExpression());
	}

	static ExpressionTester updateStatement(ExpressionTester updateClause,
	                                        ExpressionTester whereClause)
	{
		return new UpdateStatementTester
		(
			updateClause,
			whereClause
		);
	}

	static ExpressionTester upper(ExpressionTester stringPrimary)
	{
		return new UpperExpressionTester(stringPrimary);
	}

	static ExpressionTester variable(String identificationVariable)
	{
		return new IdentificationVariableTester(identificationVariable);
	}

	static ExpressionTester where(ExpressionTester conditionalExpression)
	{
		return new WhereClauseTester(conditionalExpression);
	}

	final ExpressionTester case_(ExpressionTester... caseOperands)
	{
		return new CaseExpressionTester
		(
			nullExpression(),
			spacedCollection(Arrays.copyOfRange(caseOperands, 0, caseOperands.length - 1)),
			caseOperands[caseOperands.length - 1]
		);
	}

	final ExpressionTester case_(ExpressionTester caseOperand,
	                             ExpressionTester[] whenClauses,
	                             ExpressionTester elseExpression)
	{
		return new CaseExpressionTester
		(
			caseOperand,
			spacedCollection(whenClauses),
			elseExpression
		);
	}

	final ExpressionTester case_(ExpressionTester[] whenClauses,
	                             ExpressionTester elseExpression)
	{
		return case_(nullExpression(), whenClauses, elseExpression);
	}

	abstract boolean isTolerant();

	final void testQuery(String query, ExpressionTester queryStatement)
	{
		JPQLExpression jpqlExpression = buildQuery(query, isTolerant());

		ExpressionTester jpqlExpressionTester = jpqlExpression(queryStatement);
		jpqlExpressionTester.test(jpqlExpression);
	}

	final void testQuery(String query,
	                     ExpressionTester queryStatement,
	                     QueryStringFormatter formatter)
	{
		JPQLExpression jpqlExpression = buildQuery(query, formatter);

		ExpressionTester jpqlExpressionTester = jpqlExpression(queryStatement);
		jpqlExpressionTester.test(jpqlExpression);
	}

	final ExpressionTester when(ExpressionTester conditionalExpression,
	                            ExpressionTester thenExpression)
	{
		return new WhenClauseTester(conditionalExpression, thenExpression);
	}

	static final class AbsExpressionTester extends AbstractSingleEncapsulatedExpressionTester
	{
		AbsExpressionTester(ExpressionTester simpleArithmeticExpression)
		{
			super(simpleArithmeticExpression);
		}

		@Override
		Class<? extends AbstractSingleEncapsulatedExpression> expressionType()
		{
			return AbsExpression.class;
		}

		@Override
		String identifier()
		{
			return Expression.ABS;
		}
	}

	static abstract class AbstractConditionalClauseTester extends AbstractExpressionTester
	{
		private ExpressionTester conditionalExpression;
		public boolean hasSpaceAfterIdentifier;

		AbstractConditionalClauseTester(ExpressionTester conditionalExpression)
		{
			super();

			this.conditionalExpression   = conditionalExpression;
			this.hasSpaceAfterIdentifier = true;
		}

		abstract Class<? extends AbstractConditionalClause> expressionType();

		abstract String identifier();
		@Override
		public void test(Expression expression)
		{
			assertInstance(expression, expressionType());

			AbstractConditionalClause conditionalClause = (AbstractConditionalClause) expression;
			assertEquals(toString(), conditionalClause.toParsedText());
			assertTrue(conditionalClause.hasConditionalExpression());
			assertEquals(hasSpaceAfterIdentifier, conditionalClause.hasSpaceAfterIdentifier());

			conditionalExpression.test(conditionalClause.getConditionalExpression());
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			sb.append(identifier());

			if (hasSpaceAfterIdentifier)
			{
				sb.append(" ");
			}

			sb.append(conditionalExpression);
			return sb.toString();
		}
	}

	static abstract class AbstractDoubleEncapsulatedExpressionTester extends AbstractEncapsulatedExpressionTester
	{
		private ExpressionTester firstExpression;
		public boolean hasComma;
		public boolean hasSpaceAfterComma;
		private ExpressionTester secondExpression;

		AbstractDoubleEncapsulatedExpressionTester(ExpressionTester firstExpression,
		                                           ExpressionTester secondExpression)
		{
			super();

			this.hasComma = true;
			this.hasSpaceAfterComma = true;
			this.firstExpression = firstExpression;
			this.secondExpression = secondExpression;
		}

		@Override
		abstract Class<? extends AbstractDoubleEncapsulatedExpression> expressionType();

		@Override
		boolean hasEncapsulatedExpression()
		{
			return !firstExpression.isNull() || hasComma || !secondExpression.isNull();
		}

		@Override
		public void test(Expression expression)
		{
			super.test(expression);

			AbstractDoubleEncapsulatedExpression abstractDoubleEncapsulatedExpression = (AbstractDoubleEncapsulatedExpression) expression;
			assertEquals(!firstExpression.isNull(),  abstractDoubleEncapsulatedExpression.hasFirstExpression());
			assertEquals(!secondExpression.isNull(), abstractDoubleEncapsulatedExpression.hasSecondExpression());
			assertEquals(hasComma,           abstractDoubleEncapsulatedExpression.hasComma());
			assertEquals(hasSpaceAfterComma, abstractDoubleEncapsulatedExpression.hasSpaceAfterComma());

			firstExpression .test(abstractDoubleEncapsulatedExpression.getFirstExpression());
			secondExpression.test(abstractDoubleEncapsulatedExpression.getSecondExpression());
		}

		@Override
		void toStringEncapsulatedExpression(StringBuilder sb)
		{
			if (!firstExpression.isNull())
			{
				sb.append(firstExpression.toString());
			}

			if (hasComma)
			{
				sb.append(",");
			}

			if (hasSpaceAfterComma)
			{
				sb.append(" ");
			}

			if (!secondExpression.isNull())
			{
				sb.append(secondExpression.toString());
			}
		}
	}

	static abstract class AbstractEncapsulatedExpressionTester extends AbstractExpressionTester
	{
		public boolean hasLeftParenthesis;
		public boolean hasRightParenthesis;

		AbstractEncapsulatedExpressionTester()
		{
			super();

			this.hasLeftParenthesis  = true;
			this.hasRightParenthesis = true;
		}

		abstract Class<? extends AbstractEncapsulatedExpression> expressionType();

		abstract boolean hasEncapsulatedExpression();

		abstract String identifier();

		@Override
		public void test(Expression expression)
		{
			assertInstance(expression, expressionType());

			AbstractEncapsulatedExpression abstractEncapsulatedExpression = (AbstractEncapsulatedExpression) expression;
			assertEquals(toString(), abstractEncapsulatedExpression.toParsedText());
			assertEquals(hasLeftParenthesis, abstractEncapsulatedExpression.hasLeftParenthesis());
			assertEquals(hasRightParenthesis, abstractEncapsulatedExpression.hasRightParenthesis());
			assertEquals(hasEncapsulatedExpression(), abstractEncapsulatedExpression.hasEncapsulatedExpression());
		}

		@Override
		public final String toString()
		{
			StringBuilder sb = new StringBuilder();
			sb.append(identifier());

			if (hasLeftParenthesis)
			{
				sb.append("(");
			}

			toStringEncapsulatedExpression(sb);

			if (hasRightParenthesis)
			{
				sb.append(")");
			}
			return sb.toString();
		}

		abstract void toStringEncapsulatedExpression(StringBuilder sb);
	}

	static abstract class AbstractExpressionTester implements ExpressionTester
	{
		@Override
		public final ExpressionTester add(ExpressionTester expression)
		{
			return AbstractJPQLTest.add(this, expression);
		}

		@Override
		public final ExpressionTester and(ExpressionTester expression)
		{
			return AbstractJPQLTest.and(this, expression);
		}

		final void assertInstance(Expression expression,
		                          Class<? extends Expression> expressionType)
		{
			Class<? extends Expression> expressionClass = expression.getClass();

			if (expressionClass != expressionType &&
			   !expressionType.isAssignableFrom(expressionClass))
			{
				fail(String.format
				(
					"Expecting %s but was %s for %s",
					expressionType.getSimpleName(),
					expressionClass.getSimpleName(),
					expression.toParsedText()
				));
			}
		}

		@Override
		public final ExpressionTester between(ExpressionTester lowerBoundExpression,
		                                      ExpressionTester upperBoundExpression)
		{
			return AbstractJPQLTest.between
			(
				this,
				lowerBoundExpression,
				upperBoundExpression
			);
		}

		@Override
		public final ExpressionTester different(ExpressionTester expression)
		{
			return AbstractJPQLTest.different(this, expression);
		}

		@Override
		public final ExpressionTester division(ExpressionTester expression)
		{
			return AbstractJPQLTest.division(this, expression);
		}

		@Override
		public final ExpressionTester equal(ExpressionTester expression)
		{
			return AbstractJPQLTest.equal(this, expression);
		}

		@Override
		public final ExpressionTester greaterThan(ExpressionTester expression)
		{
			return AbstractJPQLTest.greaterThan(this, expression);
		}

		@Override
		public final ExpressionTester greaterThanOrEqual(ExpressionTester expression)
		{
			return AbstractJPQLTest.greaterThanOrEqual(this, expression);
		}

		@Override
		public final ExpressionTester in(ExpressionTester... inItems)
		{
			if (inItems.length == 1)
			{
				return AbstractJPQLTest.in(this, inItems[0]);
			}

			return AbstractJPQLTest.in(this, inItems);
		}

		@Override
		public boolean isNull()
		{
			return false;
		}

		@Override
		public final ExpressionTester like(ExpressionTester patternValue)
		{
			return AbstractJPQLTest.like(this, patternValue);
		}

		@Override
		public final ExpressionTester like(ExpressionTester patternValue,
		                                   char escapeCharacter)
		{
			return AbstractJPQLTest.like(this, patternValue, escapeCharacter);
		}

		@Override
		public final ExpressionTester lowerThan(ExpressionTester expression)
		{
			return AbstractJPQLTest.lowerThan(this, expression);
		}

		@Override
		public final ExpressionTester lowerThanOrEqual(ExpressionTester expression)
		{
			return AbstractJPQLTest.lowerThanOrEqual(this, expression);
		}

		@Override
		public final ExpressionTester member(ExpressionTester collectionValuedPathExpression)
		{
			return AbstractJPQLTest.member(this, collectionValuedPathExpression);
		}

		@Override
		public final ExpressionTester memberOf(ExpressionTester collectionValuedPathExpression)
		{
			return AbstractJPQLTest.memberOf(this, collectionValuedPathExpression);
		}

		@Override
		public final ExpressionTester multiplication(ExpressionTester expression)
		{
			return AbstractJPQLTest.multiplication(this, expression);
		}

		@Override
		public final ExpressionTester notBetween(ExpressionTester lowerBoundExpression,
		                                         ExpressionTester upperBoundExpression)
		{
			return AbstractJPQLTest.notBetween
			(
				this,
				lowerBoundExpression,
				upperBoundExpression
			);
		}

		@Override
		public final ExpressionTester notIn(ExpressionTester... inItems)
		{
			if (inItems.length == 1)
			{
				return AbstractJPQLTest.notIn(this, inItems[0]);
			}

			return AbstractJPQLTest.notIn(this, inItems);
		}

		@Override
		public ExpressionTester notLike(ExpressionTester expression)
		{
			return AbstractJPQLTest.notLike(this, expression);
		}

		@Override
		public final ExpressionTester notMember(ExpressionTester collectionValuedPathExpression)
		{
			return AbstractJPQLTest.notMember(this, collectionValuedPathExpression);
		}

		@Override
		public final ExpressionTester notMemberOf(ExpressionTester collectionValuedPathExpression)
		{
			return AbstractJPQLTest.notMemberOf
			(
				this,
				collectionValuedPathExpression
			);
		}

		@Override
		public final ExpressionTester or(ExpressionTester expression)
		{
			return AbstractJPQLTest.or(this, expression);
		}

		@Override
		public final ExpressionTester substract(ExpressionTester expression)
		{
			return AbstractJPQLTest.substract(this, expression);
		}
	}

	static abstract class AbstractFromClauseTester extends AbstractExpressionTester
	{
		private ExpressionTester declaration;

		AbstractFromClauseTester(ExpressionTester declaration)
		{
			super();
			this.declaration = declaration;
		}

		@Override
		public void test(Expression expression)
		{
			assertInstance(expression, AbstractFromClause.class);

			AbstractFromClause fromClause = (AbstractFromClause) expression;
			assertEquals(toString(), fromClause.toParsedText());
			assertTrue  (fromClause.hasDeclaration());
			assertTrue  (fromClause.hasSpaceAfterFrom());

			declaration.test(fromClause.getDeclaration());
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			sb.append(Expression.FROM);
			sb.append(" ");
			sb.append(declaration);
			return sb.toString();
		}
	}

	static abstract class AbstractPathExpressionTester extends AbstractExpressionTester
	{
		private String value;

		AbstractPathExpressionTester(String value)
		{
			super();
			this.value = value;
		}

		@Override
		public void test(Expression expression)
		{
			assertInstance(expression, AbstractPathExpression.class);

			AbstractPathExpression abstractPathExpression = (AbstractPathExpression) expression;
			assertEquals(value, abstractPathExpression.toParsedText());
			assertTrue  (abstractPathExpression.hasIdentificationVariable());
			assertFalse (abstractPathExpression.endsWithDot());
			assertFalse (abstractPathExpression.startsWithDot());
		}

		@Override
		public String toString()
		{
			return value;
		}
	}

	static final class AbstractSchemaNameTester extends AbstractExpressionTester
	{
		private String abstractSchemaName;

		AbstractSchemaNameTester(String abstractSchemaName)
		{
			super();
			this.abstractSchemaName = abstractSchemaName;
		}

		@Override
		public void test(Expression expression)
		{
			assertInstance(expression, AbstractSchemaName.class);

			AbstractSchemaName abstractSchemaName = (AbstractSchemaName) expression;
			assertEquals(toString(), abstractSchemaName.toParsedText());
		}

		@Override
		public String toString()
		{
			return abstractSchemaName;
		}
	}

	static abstract class AbstractSelectClauseTester extends AbstractExpressionTester
	{
		private boolean hasDistinct;
		private ExpressionTester selectExpressions;

		AbstractSelectClauseTester(ExpressionTester selectExpressions,
		                           boolean hasDistinct)
		{
			super();

			this.hasDistinct       = hasDistinct;
			this.selectExpressions = selectExpressions;
		}

		@Override
		public void test(Expression expression)
		{
			assertInstance(expression, AbstractSelectClause.class);

			AbstractSelectClause selectClause = (AbstractSelectClause) expression;
			assertEquals(toString(),  selectClause.toParsedText());
			assertTrue  (selectClause.hasSelectExpression());
			assertEquals(hasDistinct, selectClause.hasDistinct());
			assertEquals(hasDistinct, selectClause.hasSpaceAfterDistinct());
			assertTrue  (selectClause.hasSpaceAfterSelect());

			selectExpressions.test(selectClause.getSelectExpression());
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			sb.append(Expression.SELECT);
			sb.append(" ");

			if (hasDistinct)
			{
				sb.append(Expression.DISTINCT);
				sb.append(" ");
			}

			sb.append(selectExpressions);
			return sb.toString();
		}
	}

	static abstract class AbstractSelectStatementTester extends AbstractExpressionTester
	{
		private ExpressionTester fromClause;
		private ExpressionTester groupByClause;
		private ExpressionTester havingClause;
		private ExpressionTester selectClause;
		private ExpressionTester whereClause;

		AbstractSelectStatementTester(ExpressionTester selectClause,
		                              ExpressionTester fromClause,
		                              ExpressionTester whereClause,
		                              ExpressionTester groupByClause,
		                              ExpressionTester havingClause)
		{
			super();

			this.selectClause  = selectClause;
			this.fromClause    = fromClause;
			this.whereClause   = whereClause;
			this.groupByClause = groupByClause;
			this.havingClause  = havingClause;
		}

		@Override
		public void test(Expression expression)
		{
			assertInstance(expression, AbstractSelectStatement.class);

			AbstractSelectStatement selectStatement = (AbstractSelectStatement) expression;
			assertEquals(toString(), selectStatement.toParsedText());

			assertEquals(!fromClause   .isNull(), selectStatement.hasFromClause());
			assertEquals(!whereClause  .isNull(), selectStatement.hasWhereClause());
			assertEquals(!groupByClause.isNull(), selectStatement.hasGroupByClause());
			assertEquals(!havingClause .isNull(), selectStatement.hasHavingClause());

//			assertEquals(!whereClause.isNull() || !groupByClause.isNull() || !havingClause.isNull(), selectStatement.hasSpaceAfterFrom());
//			assertEquals(!havingClause.isNull() || !groupByClause.isNull(), selectStatement.hasSpaceAfterGroupBy());
			assertTrue  (selectStatement.hasSpaceAfterSelect());
//			assertFalse (selectStatement.hasSpaceAfterWhere());

			selectClause .test(selectStatement.getSelectClause());
			fromClause   .test(selectStatement.getFromClause());
			whereClause  .test(selectStatement.getWhereClause());
			groupByClause.test(selectStatement.getGroupByClause());
			havingClause .test(selectStatement.getHavingClause());
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			sb.append(selectClause);
			sb.append(" ");
			sb.append(fromClause);

			if (!whereClause.isNull())
			{
				sb.append(" ");
				sb.append(whereClause);
			}

			if (!groupByClause.isNull())
			{
				sb.append(" ");
				sb.append(groupByClause);
			}

			if (!havingClause.isNull())
			{
				sb.append(" ");
				sb.append(havingClause);
			}

			return sb.toString();
		}
	}

	static abstract class AbstractSingleEncapsulatedExpressionTester extends AbstractEncapsulatedExpressionTester
	{
		private ExpressionTester expression;

		AbstractSingleEncapsulatedExpressionTester(ExpressionTester expression)
		{
			super();
			this.expression = expression;
		}

		@Override
		abstract Class<? extends AbstractSingleEncapsulatedExpression> expressionType();

		@Override
		final boolean hasEncapsulatedExpression()
		{
			return !expression.isNull();
		}

		@Override
		void toStringEncapsulatedExpression(StringBuilder sb)
		{
			sb.append(expression.toString());
		}
	}

	static final class AdditionExpressionTester extends CompoundExpressionTester
	{
		AdditionExpressionTester(ExpressionTester leftExpression,
		                         ExpressionTester rightExpression)
		{
			super(leftExpression, rightExpression);
		}

		@Override
		Class<? extends CompoundExpression> expressionType()
		{
			return AdditionExpression.class;
		}

		@Override
		String identifier()
		{
			return Expression.PLUS;
		}
	}

	static abstract class AggregateFunctionTester extends AbstractExpressionTester
	{
		private boolean hasDistinct;
		private ExpressionTester stateFieldPathExpression;

		AggregateFunctionTester(ExpressionTester stateFieldPathExpression,
		                        boolean hasDistinct)
		{
			super();

			this.hasDistinct = hasDistinct;
			this.stateFieldPathExpression = stateFieldPathExpression;
		}

		abstract String identifier();

		@Override
		public void test(Expression expression)
		{
			assertInstance(expression, AggregateFunction.class);

			AggregateFunction abstractFunction = (AggregateFunction) expression;
			assertEquals(toString(), abstractFunction.toParsedText());
			assertEquals(hasDistinct, abstractFunction.hasDistinct());
			assertTrue  (abstractFunction.hasLeftParenthesis());
			assertTrue  (abstractFunction.hasRightParenthesis());
			assertTrue  (abstractFunction.hasExpression());

			stateFieldPathExpression.test(abstractFunction.getExpression());
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			sb.append(identifier());
			sb.append("(");

			if (hasDistinct)
			{
				sb.append(Expression.DISTINCT);
				sb.append(" ");
			}

			sb.append(stateFieldPathExpression);
			sb.append(")");
			return sb.toString();
		}
	}

	static final class AllOrAnyExpressionTester extends AbstractExpressionTester
	{
		private String identifier;
		private ExpressionTester subquery;

		AllOrAnyExpressionTester(String identifier, ExpressionTester subquery)
		{
			super();
			this.identifier = identifier;
			this.subquery = subquery;
		}

		@Override
		public void test(Expression expression)
		{
			assertInstance(expression, AllOrAnyExpression.class);

			AllOrAnyExpression allOrAnyExpression = (AllOrAnyExpression) expression;
			assertTrue(allOrAnyExpression.hasLeftParenthesis());
			assertTrue(allOrAnyExpression.hasRightParenthesis());
			assertTrue(allOrAnyExpression.hasExpression());

			subquery.test(allOrAnyExpression.getExpression());
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			sb.append(identifier);
			sb.append("(");
			sb.append(subquery);
			sb.append(")");
			return sb.toString();
		}
	}

	static final class AndExpressionTester extends LogicalExpressionTester
	{
		AndExpressionTester(ExpressionTester leftExpression,
		                    ExpressionTester rightExpression)
		{
			super(leftExpression, rightExpression);
		}

		@Override
		Class<? extends CompoundExpression> expressionType()
		{
			return AndExpression.class;
		}

		@Override
		String identifier()
		{
			return Expression.AND;
		}
	}

	static final class AvgFunctionTester extends AggregateFunctionTester
	{
		AvgFunctionTester(ExpressionTester stateFieldPathExpression,
		                  boolean hasDistinct)
		{
			super(stateFieldPathExpression, hasDistinct);
		}

		@Override
		String identifier()
		{
			return Expression.AVG;
		}

		@Override
		public void test(Expression expression)
		{
			super.test(expression);
			assertInstance(expression, AvgFunction.class);
		}
	}

	static final class BetweenExpressionTester extends AbstractExpressionTester
	{
		private ExpressionTester expression;
		private boolean hasNot;
		private ExpressionTester lowerBoundExpression;
		private ExpressionTester upperBoundExpression;

		BetweenExpressionTester(ExpressionTester expression,
		                        boolean hasNot,
		                        ExpressionTester lowerBoundExpression,
		                        ExpressionTester upperBoundExpression)
		{
			super();

			this.hasNot = hasNot;
			this.expression = expression;
			this.lowerBoundExpression = lowerBoundExpression;
			this.upperBoundExpression = upperBoundExpression;
		}

		@Override
		public void test(Expression expression)
		{
			assertInstance(expression, BetweenExpression.class);

			BetweenExpression betweenExpression = (BetweenExpression) expression;
			assertEquals(toString(), betweenExpression.toParsedText());
			assertTrue  (betweenExpression.hasAnd());
			assertTrue  (betweenExpression.hasExpression());
			assertTrue  (betweenExpression.hasLowerBoundExpression());
			assertEquals(hasNot, betweenExpression.hasNot());
			assertTrue  (betweenExpression.hasSpaceAfterAnd());
			assertTrue  (betweenExpression.hasSpaceAfterBetween());
			assertTrue  (betweenExpression.hasSpaceAfterLowerBound());
			assertTrue  (betweenExpression.hasUpperBoundExpression());

			this.expression.test(betweenExpression.getExpression());
			lowerBoundExpression.test(betweenExpression.getLowerBoundExpression());
			upperBoundExpression.test(betweenExpression.getUpperBoundExpression());
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			sb.append(expression);
			sb.append(" ");

			if (hasNot)
			{
				sb.append("NOT ");
			}

			sb.append(Expression.BETWEEN);
			sb.append(" ");
			sb.append(lowerBoundExpression);
			sb.append(" ");
			sb.append(Expression.AND);
			sb.append(" ");
			sb.append(upperBoundExpression);
			return sb.toString();
		}
	}

	static final class CaseExpressionTester extends AbstractExpressionTester
	{
		private ExpressionTester caseOperand;
		private ExpressionTester elseExpression;
		private ExpressionTester whenClauses;

		CaseExpressionTester(ExpressionTester caseOperand,
		                     ExpressionTester whenClauses,
		                     ExpressionTester elseExpression)
		{
			super();

			this.whenClauses = whenClauses;
			this.caseOperand = caseOperand;
			this.elseExpression = elseExpression;
		}

		@Override
		public void test(Expression expression)
		{
			assertInstance(expression, CaseExpression.class);

			CaseExpression caseExpression = (CaseExpression) expression;
			assertEquals(toString(), caseExpression.toParsedText());
			assertEquals(!caseOperand.isNull(), caseExpression.hasCaseOperand());
			assertTrue  (caseExpression.hasElse());
			assertTrue  (caseExpression.hasEnd());
			assertTrue  (caseExpression.hasSpaceAfterCase());
			assertTrue  (caseExpression.hasSpaceAfterElse());
			assertTrue  (caseExpression.hasSpaceAfterElseExpression());
			assertTrue  (caseExpression.hasSpaceAfterWhenClauses());
			assertTrue  (caseExpression.hasWhenClauses());

			caseOperand.test(caseExpression.getCaseOperand());
			whenClauses.test(caseExpression.getWhenClauses());
			elseExpression.test(caseExpression.getElseExpression());
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			sb.append(Expression.CASE);
			sb.append(" ");

			if (!caseOperand.isNull())
			{
				sb.append(caseOperand);
				sb.append(" ");
			}

			sb.append(whenClauses);
			sb.append(" ");
			sb.append(Expression.ELSE);
			sb.append(" ");
			sb.append(elseExpression);
			sb.append(" ");
			sb.append(Expression.END);
			return sb.toString();
		}
	}

	static final class CollectionExpressionTester extends AbstractExpressionTester
	{
		private Boolean[] commas;
		private ExpressionTester[] expressionTesters;
		private Boolean[] spaces;

		CollectionExpressionTester(ExpressionTester[] expressionTesters,
		                           Boolean[] spaces,
		                           Boolean[] commas)
		{
			super();

			this.expressionTesters = expressionTesters;
			this.spaces = spaces;
			this.commas = commas;
		}

		@Override
		public void test(Expression expression)
		{
			assertInstance(expression, CollectionExpression.class);

			CollectionExpression collectionExpression = (CollectionExpression) expression;
			assertEquals(toString(), collectionExpression.toParsedText());
			assertEquals(spaces.length, collectionExpression.spacesSize());
			assertEquals(commas.length, collectionExpression.commasSize());
			assertEquals(expressionTesters.length, collectionExpression.childrenSize());

			// Expressions
			for (int index = expressionTesters.length; --index >= 0; )
			{
				expressionTesters[index].test(collectionExpression.getChild(index));
			}

			// Spaces
			for (int index = 0, count = spaces.length; index < count; index++)
			{
				assertEquals
				(
					"The flag for a space at " + index + " does not match",
					spaces[index],
					collectionExpression.hasSpace(index)
				);
			}

			// Commas
			for (int index = 0, count = commas.length; index < count; index++)
			{
				assertEquals
				(
					"The flag for a comma at " + index + " does not match",
					commas[index],
					collectionExpression.hasComma(index)
				);
			}
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();

			for (int index = 0, count = expressionTesters.length; index < count; index++)
			{
				sb.append(expressionTesters[index]);

				if (commas[index])
				{
					sb.append(",");
				}

				if (spaces[index] || (commas[index] && (index + 1 < count)))
				{
					sb.append(" ");
				}
			}

			return sb.toString();
		}
	}

	static final class CollectionMemberDeclarationTester extends AbstractExpressionTester
	{
		private ExpressionTester collectionValuedPath;
		private boolean hasAs;
		private ExpressionTester identificationVariable;

		CollectionMemberDeclarationTester(ExpressionTester collectionValuedPath,
		                                  boolean hasAs,
		                                  ExpressionTester identificationVariable)
		{
			super();

			this.hasAs = hasAs;
			this.collectionValuedPath   = collectionValuedPath;
			this.identificationVariable = identificationVariable;
		}

		@Override
		public void test(Expression expression)
		{
			assertInstance(expression, CollectionMemberDeclaration.class);

			CollectionMemberDeclaration collectionMemberDeclaration = (CollectionMemberDeclaration) expression;
			assertEquals(toString(), collectionMemberDeclaration.toParsedText());
			assertEquals(hasAs, collectionMemberDeclaration.hasAs());
			assertTrue  (collectionMemberDeclaration.hasCollectionValuedPathExpression());
			assertTrue  (collectionMemberDeclaration.hasIdentificationVariable());
			assertTrue  (collectionMemberDeclaration.hasLeftParenthesis());
			assertTrue  (collectionMemberDeclaration.hasRightParenthesis());
			assertEquals(hasAs, collectionMemberDeclaration.hasSpaceAfterAs());
			assertFalse (collectionMemberDeclaration.hasSpaceAfterIn());
			assertTrue  (collectionMemberDeclaration.hasSpaceAfterRightParenthesis());

			collectionValuedPath.test(collectionMemberDeclaration.getCollectionValuedPathExpression());
			identificationVariable.test(collectionMemberDeclaration.getIdentificationVariable());
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			sb.append(Expression.IN);
			sb.append("(");
			sb.append(collectionValuedPath);
			sb.append(hasAs ? ") AS " : ") ");
			sb.append(identificationVariable);
			return sb.toString();
		}
	}

	static final class CollectionMemberExpressionTester extends AbstractExpressionTester
	{
		private ExpressionTester collectionValuedPathExpression;
		private ExpressionTester entityExpression;
		private boolean hasNot;
		private boolean hasOf;

		CollectionMemberExpressionTester(ExpressionTester entityExpression,
		                                 boolean hasNot,
		                                 boolean hasOf,
		                                 ExpressionTester collectionValuedPathExpression)
		{
			super();

			this.hasNot = hasNot;
			this.hasOf = hasOf;
			this.entityExpression = entityExpression;
			this.collectionValuedPathExpression = collectionValuedPathExpression;
		}

		@Override
		public void test(Expression expression)
		{
			assertInstance(expression, CollectionMemberExpression.class);

			CollectionMemberExpression collectionMemberExpression = (CollectionMemberExpression) expression;
			assertEquals(toString(), collectionMemberExpression.toParsedText());
			assertTrue  (collectionMemberExpression.hasCollectionValuedPathExpression());
			assertTrue  (collectionMemberExpression.hasEntityExpression());
			assertTrue  (collectionMemberExpression.hasSpaceAfterMember());
			assertEquals(hasNot, collectionMemberExpression.hasNot());
			assertEquals(hasOf,  collectionMemberExpression.hasSpaceAfterOf());

			entityExpression.test(collectionMemberExpression.getEntityExpression());
			collectionValuedPathExpression.test(collectionMemberExpression.getCollectionValuedPathExpression());
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			sb.append(entityExpression);
			sb.append(hasNot && hasOf ? " NOT MEMBER OF " : hasOf ? " MEMBER OF " : hasNot ? " NOT MEMBER " : " MEMBER ");
			sb.append(collectionValuedPathExpression);
			return sb.toString();
		}
	}

	static final class CollectionValuedPathExpressionTester extends AbstractPathExpressionTester
	{
		CollectionValuedPathExpressionTester(String value)
		{
			super(value);
		}

		@Override
		public void test(Expression expression)
		{
			super.test(expression);
			assertInstance(expression, CollectionValuedPathExpression.class);
		}
	}

	static final class ComparisonExpressionTester extends AbstractExpressionTester
	{
		private String comparator;
		private ExpressionTester leftExpression;
		private ExpressionTester rightExpression;

		ComparisonExpressionTester(String comparator,
		                           ExpressionTester leftExpression,
		                           ExpressionTester rightExpression)
		{
			super();

			this.comparator  = comparator;
			this.leftExpression  = leftExpression;
			this.rightExpression = rightExpression;
		}

		@Override
		public void test(Expression expression)
		{
			assertInstance(expression, ComparisonExpression.class);

			ComparisonExpression comparisonExpression = (ComparisonExpression) expression;
			assertEquals(toString(), comparisonExpression.toParsedText());
			assertTrue  (comparisonExpression.hasLeftExpression());
			assertTrue  (comparisonExpression.hasRightExpression());
			assertTrue  (comparisonExpression.hasSpaceAfterIdentifier());

			leftExpression .test(comparisonExpression.getLeftExpression());
			rightExpression.test(comparisonExpression.getRightExpression());
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			sb.append(leftExpression);
			sb.append(" ");
			sb.append(comparator);
			sb.append(" ");
			sb.append(rightExpression);
			return sb.toString();
		}
	}

	static abstract class CompoundExpressionTester extends AbstractExpressionTester
	{
		public boolean hasSpaceAfterIdentifier;
		private ExpressionTester leftExpression;
		private ExpressionTester rightExpression;

		CompoundExpressionTester(ExpressionTester leftExpression,
		                         ExpressionTester rightExpression)
		{
			super();

			this.leftExpression  = leftExpression;
			this.rightExpression = rightExpression;
			this.hasSpaceAfterIdentifier = true;
		}

		abstract Class<? extends CompoundExpression> expressionType();

		abstract String identifier();

		@Override
		public void test(Expression expression)
		{
			assertInstance(expression, expressionType());

			CompoundExpression compoundExpression = (CompoundExpression) expression;
			assertEquals(toString(), compoundExpression.toParsedText());
			assertEquals(!leftExpression.isNull(), compoundExpression.hasLeftExpression());
			assertEquals(!rightExpression.isNull(), compoundExpression.hasRightExpression());
			assertEquals(hasSpaceAfterIdentifier, compoundExpression.hasSpaceAfterIdentifier());

			leftExpression.test(compoundExpression.getLeftExpression());
			rightExpression.test(compoundExpression.getRightExpression());
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			sb.append(leftExpression);

			if (!leftExpression.isNull())
			{
				sb.append(" ");
			}

			sb.append(identifier());

			if (hasSpaceAfterIdentifier)
			{
				sb.append(" ");
			}

			sb.append(rightExpression);
			return sb.toString();
		}
	}

	static final class ConcatExpressionTester extends AbstractDoubleEncapsulatedExpressionTester
	{
		ConcatExpressionTester(ExpressionTester firstExpression,
		                       ExpressionTester secondExpression)
		{
			super(firstExpression, secondExpression);
		}

		@Override
		Class<? extends AbstractDoubleEncapsulatedExpression> expressionType()
		{
			return ConcatExpression.class;
		}

		@Override
		String identifier()
		{
			return Expression.CONCAT;
		}
	}

	static final class ConstructorExpressionTester extends AbstractExpressionTester
	{
		private String className;
		private ExpressionTester constructorItems;

		ConstructorExpressionTester(String className,
		                            ExpressionTester constructorItems)
		{
			super();

			this.className = className;
			this.constructorItems = constructorItems;
		}

		@Override
		public void test(Expression expression)
		{
			assertInstance(expression, ConstructorExpression.class);

			ConstructorExpression constructorExpression = (ConstructorExpression) expression;
			assertEquals(toString(), constructorExpression.toParsedText());
			assertTrue  (constructorExpression.hasConstructorItems());
			assertTrue  (constructorExpression.hasLeftParenthesis());
			assertTrue  (constructorExpression.hasRightParenthesis());

			constructorItems.test(constructorExpression.getConstructorItems());
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			sb.append(Expression.NEW);
			sb.append(" ");
			sb.append(className);
			sb.append("(");
			sb.append(constructorItems);
			sb.append(")");
			return sb.toString();
		}
	}

	static final class CountFunctionTester extends AggregateFunctionTester
	{
		CountFunctionTester(ExpressionTester stateFieldPathExpression,
		                    boolean hasDistinct)
		{
			super(stateFieldPathExpression, hasDistinct);
		}

		@Override
		String identifier()
		{
			return Expression.COUNT;
		}

		@Override
		public void test(Expression expression)
		{
			super.test(expression);
			assertInstance(expression, CountFunction.class);
		}
	}

	static final class DateTimeTester extends AbstractExpressionTester
	{
		private String dateTime;

		DateTimeTester(String dateTime)
		{
			super();
			this.dateTime = dateTime;
		}

		@Override
		public void test(Expression expression)
		{
			assertInstance(expression, DateTime.class);

			DateTime dateTime = (DateTime) expression;
			assertEquals(toString(), dateTime.toParsedText());
		}

		@Override
		public String toString()
		{
			return dateTime;
		}
	}

	static final class DeleteClauseTester extends AbstractExpressionTester
	{
		private ExpressionTester rangeVariableDeclaration;

		DeleteClauseTester(ExpressionTester rangeVariableDeclaration)
		{
			super();
			this.rangeVariableDeclaration = rangeVariableDeclaration;
		}

		@Override
		public void test(Expression expression)
		{
			assertInstance(expression, DeleteClause.class);

			DeleteClause deleteClause = (DeleteClause) expression;
			assertEquals(toString(), deleteClause.toParsedText());
			assertTrue(deleteClause.hasFrom());
			assertTrue(deleteClause.hasRangeVariableDeclaration());
			assertTrue(deleteClause.hasSpaceAfterDelete());
			assertTrue(deleteClause.hasSpaceAfterFrom());

			rangeVariableDeclaration.test(deleteClause.getRangeVariableDeclaration());
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			sb.append(Expression.DELETE_FROM);
			sb.append(" ");
			sb.append(rangeVariableDeclaration);
			return sb.toString();
		}
	}

	static final class DeleteStatementTester extends AbstractExpressionTester
	{
		private ExpressionTester deleteClause;
		private ExpressionTester whereClause;

		DeleteStatementTester(ExpressionTester deleteClause,
		                      ExpressionTester whereClause)
		{
			super();

			this.deleteClause = deleteClause;
			this.whereClause = whereClause;
		}

		@Override
		public void test(Expression expression)
		{
			assertInstance(expression, DeleteStatement.class);

			DeleteStatement deleteStatement = (DeleteStatement) expression;
			assertEquals(toString(), deleteStatement.toParsedText());
			assertTrue(deleteStatement.hasSpaceAfterDeleteClause());
			assertTrue(deleteStatement.hasWhereClause());

			deleteClause.test(deleteStatement.getDeleteClause());
			whereClause.test(deleteStatement.getWhereClause());
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			sb.append(deleteClause);

			if (!whereClause.isNull())
			{
				sb.append(" ");
				sb.append(whereClause);
			}

			return sb.toString();
		}
	}

	static final class DivisionExpressionTester extends CompoundExpressionTester
	{
		DivisionExpressionTester(ExpressionTester leftExpression,
		                         ExpressionTester rightExpression)
		{
			super(leftExpression, rightExpression);
		}

		@Override
		Class<? extends CompoundExpression> expressionType()
		{
			return DivisionExpression.class;
		}

		@Override
		String identifier()
		{
			return Expression.DIVISION;
		}
	}

	static final class EmptyExpressionTester extends AbstractExpressionTester
	{
		private ExpressionTester collectionValuedPathExpression;
		private boolean hasNot;

		EmptyExpressionTester(ExpressionTester collectionValuedPathExpression,
		                      boolean hasNot)
		{
			super();

			this.hasNot = hasNot;
			this.collectionValuedPathExpression = collectionValuedPathExpression;
		}

		@Override
		public void test(Expression expression)
		{
			assertInstance(expression, EmptyCollectionComparisonExpression.class);

			EmptyCollectionComparisonExpression emptyCollection = (EmptyCollectionComparisonExpression) expression;
			assertEquals(toString(), emptyCollection.toParsedText());
			assertTrue  (emptyCollection.hasExpression());
			assertEquals(hasNot, emptyCollection.hasNot());
			assertTrue  (emptyCollection.hasSpaceAfterIs());

			collectionValuedPathExpression.test(emptyCollection.getExpression());
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			sb.append(collectionValuedPathExpression);
			sb.append(hasNot ? " IS NOT EMPTY" : " IS EMPTY");
			return sb.toString();
		}
	}

	static final class EntityTypeLiteralTester extends AbstractExpressionTester
	{
		private String entityType;

		EntityTypeLiteralTester(String entityType)
		{
			super();
			this.entityType = entityType;
		}

		@Override
		public void test(Expression expression)
		{
			assertInstance(expression, EntityTypeLiteral.class);

			EntityTypeLiteral entityTypeLiteral = (EntityTypeLiteral) expression;
			assertEquals(toString(), entityTypeLiteral.toParsedText());
		}

		@Override
		public String toString()
		{
			return entityType;
		}
	}

	static final class ExistsExpressionTester extends AbstractExpressionTester
	{
		private boolean hasNot;
		private ExpressionTester subquery;

		ExistsExpressionTester(ExpressionTester subquery, boolean hasNot)
		{
			super();
			this.hasNot = hasNot;
			this.subquery = subquery;
		}

		@Override
		public void test(Expression expression)
		{
			assertInstance(expression, ExistsExpression.class);

			ExistsExpression existsExpression = (ExistsExpression) expression;
			assertEquals(toString(), expression.toParsedText());
			assertTrue  (existsExpression.hasLeftParenthesis());
			assertTrue  (existsExpression.hasRightParenthesis());
			assertTrue  (existsExpression.hasExpression());
			assertEquals(hasNot, existsExpression.hasNot());

			subquery.test(existsExpression.getExpression());
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			sb.append(hasNot ? Expression.NOT_EXISTS : Expression.EXISTS);
			sb.append("(");
			sb.append(subquery);
			sb.append(")");
			return sb.toString();
		}
	}

	static interface ExpressionTester
	{
		ExpressionTester add(ExpressionTester expression);
		ExpressionTester and(ExpressionTester expression);
		ExpressionTester between(ExpressionTester lowerBoundExpression, ExpressionTester upperBoundExpression);
		ExpressionTester different(ExpressionTester expression);
		ExpressionTester division(ExpressionTester expression);
		ExpressionTester equal(ExpressionTester expression);
		ExpressionTester greaterThan(ExpressionTester expression);
		ExpressionTester greaterThanOrEqual(ExpressionTester expression);
		ExpressionTester in(ExpressionTester... inItems);
		boolean isNull();
		ExpressionTester like(ExpressionTester patternValue);
		ExpressionTester like(ExpressionTester patternValue, char escapeCharacter);
		ExpressionTester lowerThan(ExpressionTester expression);
		ExpressionTester lowerThanOrEqual(ExpressionTester expression);
		ExpressionTester member(ExpressionTester collectionValuedPathExpression);
		ExpressionTester memberOf(ExpressionTester collectionValuedPathExpression);
		ExpressionTester multiplication(ExpressionTester expression);
		ExpressionTester notBetween(ExpressionTester lowerBoundExpression, ExpressionTester upperBoundExpression);
		ExpressionTester notIn(ExpressionTester... inItems);
		ExpressionTester notLike(ExpressionTester expression);
		ExpressionTester notMember(ExpressionTester collectionValuedPathExpression);
		ExpressionTester notMemberOf(ExpressionTester collectionValuedPathExpression);
		ExpressionTester or(ExpressionTester expression);
		ExpressionTester substract(ExpressionTester expression);
		void test(Expression expression);
	}

	static final class FromClauseTester extends AbstractFromClauseTester
	{
		FromClauseTester(ExpressionTester declarations)
		{
			super(declarations);
		}

		@Override
		public void test(Expression expression)
		{
			super.test(expression);
			assertInstance(expression, FromClause.class);
		}
	}

	private static final class FuncExpressionTester extends AbstractSingleEncapsulatedExpressionTester
	{
		FuncExpressionTester(ExpressionTester funcItems)
		{
			super(funcItems);
		}

		@Override
		Class<? extends AbstractSingleEncapsulatedExpression> expressionType()
		{
			return FuncExpression.class;
		}

		@Override
		String identifier()
		{
			return Expression.FUNC;
		}
	}

	static final class GroupByClauseTester extends AbstractExpressionTester
	{
		private ExpressionTester groupByItems;

		GroupByClauseTester(ExpressionTester groupByItems)
		{
			super();
			this.groupByItems = groupByItems;
		}

		@Override
		public void test(Expression expression)
		{
			assertInstance(expression, GroupByClause.class);

			GroupByClause groupByClause = (GroupByClause) expression;
			assertEquals(toString(), groupByClause.toParsedText());
			assertTrue  (groupByClause.hasGroupByItems());
			assertTrue  (groupByClause.hasSpaceAfterGroupBy());

			groupByItems.test(groupByClause.getGroupByItems());
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			sb.append(Expression.GROUP_BY);
			sb.append(" ");
			sb.append(groupByItems);
			return sb.toString();
		}
	}

	static final class HavingClauseTester extends AbstractConditionalClauseTester
	{
		HavingClauseTester(ExpressionTester conditionalExpression)
		{
			super(conditionalExpression);
		}

		@Override
		Class<? extends AbstractConditionalClause> expressionType()
		{
			return HavingClause.class;
		}

		@Override
		String identifier()
		{
			return Expression.HAVING;
		}
	}

	static final class IdentificationVariableDeclarationTester extends AbstractExpressionTester
	{
		private ExpressionTester joins;
		private ExpressionTester rangeVariableDeclaration;

		IdentificationVariableDeclarationTester(ExpressionTester rangeVariableDeclaration,
		                                        ExpressionTester joins)
		{
			super();

			this.rangeVariableDeclaration = rangeVariableDeclaration;
			this.joins = joins;
		}

		@Override
		public void test(Expression expression)
		{
			assertInstance(expression, IdentificationVariableDeclaration.class);

			IdentificationVariableDeclaration identificationVariableDeclaration = (IdentificationVariableDeclaration) expression;
			assertEquals(toString(), identificationVariableDeclaration.toParsedText());
			assertTrue  (identificationVariableDeclaration.hasRangeVariableDeclaration());

			assertEquals(!joins.isNull(), identificationVariableDeclaration.hasSpace());
			assertEquals(!joins.isNull(), identificationVariableDeclaration.hasJoins());

			rangeVariableDeclaration.test(identificationVariableDeclaration.getRangeVariableDeclaration());
			joins.test(identificationVariableDeclaration.getJoins());
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			sb.append(rangeVariableDeclaration);

			if (!joins.isNull())
			{
				sb.append(" ");
				sb.append(joins);
			}

			return sb.toString();
		}
	}

	static final class IdentificationVariableTester extends AbstractExpressionTester
	{
		private String identificationVariable;

		IdentificationVariableTester(String identificationVariable)
		{
			super();
			this.identificationVariable = identificationVariable;
		}

		@Override
		public void test(Expression expression)
		{
			assertInstance(expression, IdentificationVariable.class);

			IdentificationVariable identificationVariable = (IdentificationVariable) expression;
			assertEquals(toString(), identificationVariable.toParsedText());
		}

		@Override
		public String toString()
		{
			return identificationVariable;
		}
	}

	static final class IndexExpressionTester extends AbstractSingleEncapsulatedExpressionTester
	{
		IndexExpressionTester(ExpressionTester identificationVariable)
		{
			super(identificationVariable);
		}

		@Override
		Class<? extends AbstractSingleEncapsulatedExpression> expressionType()
		{
			return IndexExpression.class;
		}

		@Override
		String identifier()
		{
			return Expression.INDEX;
		}
	}

	static final class InExpressionTester extends AbstractExpressionTester
	{
		boolean hasLeftParenthesis;
		private boolean hasNot;
		boolean hasRightParenthesis;
		private ExpressionTester inItems;
		private ExpressionTester stateFieldPathExpression;

		InExpressionTester(ExpressionTester stateFieldPathExpression,
		                   boolean hasNot,
		                   ExpressionTester inItems)
		{
			super();

			this.stateFieldPathExpression = stateFieldPathExpression;
			this.hasLeftParenthesis = true;
			this.hasRightParenthesis = true;
			this.hasNot = hasNot;
			this.inItems = inItems;
		}

		@Override
		public void test(Expression expression)
		{
			assertInstance(expression, InExpression.class);

			InExpression inExpression = (InExpression) expression;
			assertEquals(toString(), inExpression.toParsedText());
			assertEquals(hasLeftParenthesis, inExpression.hasLeftParenthesis());
			assertEquals(hasRightParenthesis, inExpression.hasRightParenthesis());
			assertEquals(hasNot, inExpression.hasNot());
			assertTrue  (inExpression.hasInItems());
			assertTrue  (inExpression.hasStateFieldPathExpression());

			stateFieldPathExpression.test(inExpression.getStateFieldPathExpression());
			inItems.test(inExpression.getInItems());
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			sb.append(stateFieldPathExpression);
			sb.append(hasNot ? " NOT IN" : " IN");
			sb.append(hasLeftParenthesis ? "(" : " ");
			sb.append(inItems);

			if (hasRightParenthesis)
			{
				sb.append(")");
			}

			return sb.toString();
		}
	}

	static final class InputParameterTester extends AbstractExpressionTester
	{
		private String inputParameter;

		InputParameterTester(String inputParameter)
		{
			super();
			this.inputParameter = inputParameter;
		}

		@Override
		public void test(Expression expression)
		{
			assertInstance(expression, InputParameter.class);

			InputParameter inputParameter = (InputParameter) expression;
			assertEquals(toString(), inputParameter.toParsedText());
			assertEquals(this.inputParameter.charAt(0) == '?', inputParameter.isPositional());
			assertEquals(this.inputParameter.charAt(0) == ':', inputParameter.isNamed());
		}

		@Override
		public String toString()
		{
			return inputParameter;
		}
	}

	static final class JoinFetchTester extends AbstractExpressionTester
	{
		private ExpressionTester collectionValuedPathExpression;
		private JoinFetch.Type joinType;

		JoinFetchTester(JoinFetch.Type joinType,
		                ExpressionTester collectionValuedPathExpression)
		{
			super();

			this.joinType = joinType;
			this.collectionValuedPathExpression = collectionValuedPathExpression;
		}

		@Override
		public void test(Expression expression)
		{
			assertInstance(expression, JoinFetch.class);

			JoinFetch join = (JoinFetch) expression;
			assertEquals(toString(), join.toParsedText());
			assertEquals(joinType, join.getIdentifier());
			assertTrue  (join.hasJoinAssociationPath());
			assertTrue  (join.hasSpaceAfterFetch());

			collectionValuedPathExpression.test(join.getJoinAssociationPath());
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			sb.append(joinType);
			sb.append(" ");
			sb.append(collectionValuedPathExpression);
			return sb.toString();
		}
	}

	static final class JoinTester extends AbstractExpressionTester
	{
		private ExpressionTester collectionValuedPathExpression;
		private boolean hasAs;
		private ExpressionTester identificationVariable;
		private Join.Type joinType;

		JoinTester(Join.Type joinType,
		           ExpressionTester collectionValuedPathExpression,
		           boolean hasAs,
		           ExpressionTester identificationVariable)
		{
			super();

			this.collectionValuedPathExpression = collectionValuedPathExpression;
			this.identificationVariable = identificationVariable;
			this.joinType = joinType;
			this.hasAs = hasAs;
		}

		@Override
		public void test(Expression expression)
		{
			assertInstance(expression, Join.class);

			Join join = (Join) expression;
			assertEquals(toString(), join.toParsedText());
			assertEquals(joinType, join.getIdentifier());

			assertEquals(hasAs, join.hasAs());
			assertTrue  (join.hasIdentificationVariable());
			assertTrue  (join.hasJoinAssociationPath());

			assertEquals(hasAs, join.hasSpaceAfterAs());
			assertTrue  (join.hasSpaceAfterJoin());
			assertTrue  (join.hasSpaceAfterJoinAssociation());

			collectionValuedPathExpression.test(join.getJoinAssociationPath());
			identificationVariable.test(join.getIdentificationVariable());
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			sb.append(joinType);
			sb.append(" ");
			sb.append(collectionValuedPathExpression);
			sb.append(hasAs ? " AS " : " ");
			sb.append(identificationVariable);
			return sb.toString();
		}
	}

	static final class JPQLExpressionTester extends AbstractExpressionTester
	{
		private ExpressionTester queryStatement;

		JPQLExpressionTester(ExpressionTester queryStatement)
		{
			super();
			this.queryStatement = queryStatement;
		}

		@Override
		public void test(Expression expression)
		{
			JPQLExpression jpqlExpression = (JPQLExpression) expression;
			assertEquals(toString(), jpqlExpression.toParsedText());
			assertTrue  (jpqlExpression.hasQueryStatement());
			assertFalse (jpqlExpression.hasUnknownEndingStatement());

			queryStatement.test(jpqlExpression.getQueryStatement());
		}

		@Override
		public String toString()
		{
			return queryStatement.toString();
		}
	}

	static final class KeywordExpressionTester extends AbstractExpressionTester
	{
		private String keyword;

		KeywordExpressionTester(String keyword)
		{
			super();
			this.keyword = keyword;
		}

		@Override
		public void test(Expression expression)
		{
			assertInstance(expression, KeywordExpression.class);

			KeywordExpression keywordExpression = (KeywordExpression) expression;
			assertEquals(toString(), keywordExpression.toParsedText());
		}

		@Override
		public String toString()
		{
			return keyword;
		}
	}

	static final class LengthExpressionTester extends AbstractSingleEncapsulatedExpressionTester
	{
		LengthExpressionTester(ExpressionTester expression)
		{
			super(expression);
		}

		@Override
		Class<? extends AbstractSingleEncapsulatedExpression> expressionType()
		{
			return LengthExpression.class;
		}

		@Override
		String identifier()
		{
			return Expression.LENGTH;
		}
	}

	static final class LikeExpressionTester extends AbstractExpressionTester
	{
		private ExpressionTester escapeCharacter;
		private boolean hasNot;
		private ExpressionTester patternValue;
		private ExpressionTester stringExpression;

		LikeExpressionTester(ExpressionTester stringExpression,
		                     boolean hasNot,
		                     ExpressionTester patternValue,
		                     ExpressionTester escapeCharacter)
		{
			super();

			this.hasNot = hasNot;
			this.stringExpression = stringExpression;
			this.patternValue = patternValue;
			this.escapeCharacter = escapeCharacter;
		}

		@Override
		public void test(Expression expression)
		{
			assertInstance(expression, LikeExpression.class);

			LikeExpression likeExpression = (LikeExpression) expression;
			assertEquals(toString(), likeExpression.toParsedText());
			assertEquals(hasNot, likeExpression.hasNot());
			assertEquals(!escapeCharacter.isNull(), likeExpression.hasEscape());
			assertEquals(!escapeCharacter.isNull(), likeExpression.hasEscapeCharacter());
			assertEquals(!escapeCharacter.isNull(), likeExpression.hasSpaceAfterEscape());
			assertEquals(!escapeCharacter.isNull(), likeExpression.hasSpaceAfterPatternValue());
			assertTrue  (likeExpression.hasPatternValue());
			assertTrue  (likeExpression.hasSpaceAfterLike());

			stringExpression.test(likeExpression.getStringExpression());
			patternValue.test(likeExpression.getPatternValue());
			escapeCharacter.test(likeExpression.getEscapeCharacter());
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			sb.append(stringExpression);
			sb.append(hasNot ? " NOT LIKE " : " LIKE ");
			sb.append(patternValue);

			if (!escapeCharacter.isNull())
			{
				sb.append(" ");
				sb.append(Expression.ESCAPE);
				sb.append(" ");
				sb.append(escapeCharacter);
			}

			return sb.toString();
		}
	}

	static final class LocateExpressionTester extends AbstractExpressionTester
	{
		private ExpressionTester firstExpression;
		private ExpressionTester secondExpression;
		private ExpressionTester thirdExpression;

		LocateExpressionTester(ExpressionTester firstExpression,
		                       ExpressionTester secondExpression,
		                       ExpressionTester thirdExpression)
		{
			super();

			this.firstExpression  = firstExpression;
			this.secondExpression = secondExpression;
			this.thirdExpression  = thirdExpression;
		}

		@Override
		public void test(Expression expression)
		{
			assertInstance(expression, LocateExpression.class);

			LocateExpression locateExpression = (LocateExpression) expression;
			assertEquals(toString(), locateExpression.toParsedText());
			assertEquals(!thirdExpression.isNull(), locateExpression.hasThirdExpression());
			assertEquals(!thirdExpression.isNull(), locateExpression.hasSecondComma());
			assertEquals(!thirdExpression.isNull(), locateExpression.hasSpaceAfterSecondComma());
			assertTrue  (locateExpression.hasFirstComma());
			assertTrue  (locateExpression.hasFirstExpression());
			assertTrue  (locateExpression.hasLeftParenthesis());
			assertTrue  (locateExpression.hasRightParenthesis());
			assertTrue  (locateExpression.hasSecondExpression());
			assertTrue  (locateExpression.hasSpaceAfterFirstComma());

			firstExpression.test(locateExpression.getFirstExpression());
			secondExpression.test(locateExpression.getSecondExpression());
			thirdExpression.test(locateExpression.getThirdExpression());
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			sb.append(Expression.LOCATE);
			sb.append("(");
			sb.append(firstExpression);
			sb.append(", ");
			sb.append(secondExpression);

			if (!thirdExpression.isNull())
			{
				sb.append(", ");
				sb.append(thirdExpression);
			}

			sb.append(")");
			return sb.toString();
		}
	}

	static abstract class LogicalExpressionTester extends CompoundExpressionTester
	{
		LogicalExpressionTester(ExpressionTester leftExpression,
		                        ExpressionTester rightExpression)
		{
			super(leftExpression, rightExpression);
		}

		@Override
		public void test(Expression expression)
		{
			super.test(expression);
			assertInstance(expression, LogicalExpression.class);
		}
	}

	static final class LowerExpressionTester extends AbstractSingleEncapsulatedExpressionTester
	{
		LowerExpressionTester(ExpressionTester expression)
		{
			super(expression);
		}

		@Override
		Class<? extends AbstractSingleEncapsulatedExpression> expressionType()
		{
			return LowerExpression.class;
		}

		@Override
		String identifier()
		{
			return Expression.LOWER;
		}
	}

	static final class MaxFunctionTester extends AggregateFunctionTester
	{
		MaxFunctionTester(ExpressionTester stateFieldPathExpression,
		                  boolean hasDistinct)
		{
			super(stateFieldPathExpression, hasDistinct);
		}

		@Override
		String identifier()
		{
			return Expression.MAX;
		}

		@Override
		public void test(Expression expression)
		{
			super.test(expression);
			assertInstance(expression, MaxFunction.class);
		}
	}

	static final class MinFunctionTester extends AggregateFunctionTester
	{
		MinFunctionTester(ExpressionTester stateFieldPathExpression,
		                  boolean hasDistinct)
		{
			super(stateFieldPathExpression, hasDistinct);
		}

		@Override
		String identifier()
		{
			return Expression.MIN;
		}

		@Override
		public void test(Expression expression)
		{
			super.test(expression);
			assertInstance(expression, MinFunction.class);
		}
	}

	static final class ModExpressionTester extends AbstractDoubleEncapsulatedExpressionTester
	{
		ModExpressionTester(ExpressionTester firstExpression,
		                    ExpressionTester secondExpression)
		{
			super(firstExpression, secondExpression);
		}

		@Override
		Class<? extends AbstractDoubleEncapsulatedExpression> expressionType()
		{
			return ModExpression.class;
		}

		@Override
		String identifier()
		{
			return Expression.MOD;
		}
	}

	static final class MultiplicationExpressionTester extends CompoundExpressionTester
	{
		MultiplicationExpressionTester(ExpressionTester leftExpression,
		                               ExpressionTester rightExpression)
		{
			super(leftExpression, rightExpression);
		}

		@Override
		Class<? extends CompoundExpression> expressionType()
		{
			return MultiplicationExpression.class;
		}

		@Override
		String identifier()
		{
			return Expression.MULTIPLICATION;
		}
	}

	static final class NotExpressionTester extends AbstractExpressionTester
	{
		private ExpressionTester expression;

		NotExpressionTester(ExpressionTester expression)
		{
			super();
			this.expression = expression;
		}

		@Override
		public void test(Expression expression)
		{
			assertInstance(expression, NotExpression.class);

			NotExpression notExpression = (NotExpression) expression;
			assertEquals(toString(), notExpression.toParsedText());
			assertEquals(!this.expression.isNull(), notExpression.hasExpression());

			this.expression.test(notExpression.getExpression());
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			sb.append(Expression.NOT);
			sb.append(" ");
			sb.append(expression);
			return sb.toString();
		}
	}

	static final class NullComparisonExpressionTester extends AbstractExpressionTester
	{
		private ExpressionTester expression;
		private boolean hasNot;

		NullComparisonExpressionTester(ExpressionTester expression,
		                               boolean hasNot)
		{
			super();

			this.hasNot = hasNot;
			this.expression = expression;
		}

		@Override
		public void test(Expression expression)
		{
			assertInstance(expression, NullComparisonExpression.class);

			NullComparisonExpression nullComparisonExpression = (NullComparisonExpression) expression;
			assertEquals(toString(), nullComparisonExpression.toParsedText());
			assertEquals(hasNot, nullComparisonExpression.hasNot());
			assertEquals(!this.expression.isNull(), nullComparisonExpression.hasExpression());

			this.expression.test(nullComparisonExpression.getExpression());
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			sb.append(expression);

			if (expression.isNull())
			{
				sb.append(hasNot ? "IS NOT NULL" : "IS NULL");
			}
			else
			{
				sb.append(hasNot ? " IS NOT NULL" : " IS NULL");
			}

			return sb.toString();
		}
	}

	static final class NullExpressionTester extends AbstractExpressionTester
	{
		@Override
		public boolean isNull()
		{
			return true;
		}

		@Override
		public void test(Expression expression)
		{
			assertInstance(expression, NullExpression.class);
		}

		@Override
		public String toString()
		{
			return AbstractExpression.EMPTY_STRING;
		}
	}

	static final class NullIfExpressionTester extends AbstractDoubleEncapsulatedExpressionTester
	{
		NullIfExpressionTester(ExpressionTester firstExpression,
                             ExpressionTester secondExpression)
      {
			super(firstExpression, secondExpression);
      }

		@Override
		Class<? extends AbstractDoubleEncapsulatedExpression> expressionType()
		{
			return NullIfExpression.class;
		}

		@Override
		String identifier()
		{
			return Expression.NULLIF;
		}
	}

	static final class NumericLiteralTester extends AbstractExpressionTester
	{
		private String number;

		NumericLiteralTester(String number)
		{
			super();
			this.number = number;
		}

		@Override
		public void test(Expression expression)
		{
			assertInstance(expression, NumericLiteral.class);

			assertEquals(toString(), expression.toParsedText());
		}

		@Override
		public String toString()
		{
			return number;
		}
	}

	static final class ObjectTester extends AbstractExpressionTester
	{
		private ExpressionTester identificationVariable;

		ObjectTester(ExpressionTester identificationVariable)
		{
			super();
			this.identificationVariable = identificationVariable;
		}

		@Override
		public void test(Expression expression)
		{
			assertInstance(expression, ObjectExpression.class);

			ObjectExpression objectExpression = (ObjectExpression) expression;
			assertEquals(toString(), objectExpression.toParsedText());
			assertTrue  (objectExpression.hasExpression());
			assertTrue  (objectExpression.hasLeftParenthesis());
			assertTrue  (objectExpression.hasRightParenthesis());

			identificationVariable.test(objectExpression.getExpression());
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			sb.append(Expression.OBJECT);
			sb.append("(");
			sb.append(identificationVariable);
			sb.append(")");
			return sb.toString();
		}
	}

	static final class OrderByClauseTester extends AbstractExpressionTester
	{
		private ExpressionTester orderByItems;

		OrderByClauseTester(ExpressionTester orderByItems)
		{
			super();
			this.orderByItems = orderByItems;
		}

		@Override
		public void test(Expression expression)
		{
			assertInstance(expression, OrderByClause.class);

			OrderByClause orderByClause = (OrderByClause) expression;
			assertEquals(toString(), orderByClause.toParsedText());
			assertTrue  (orderByClause.hasOrderByItems());
			assertTrue  (orderByClause.hasSpaceAfterOrderBy());

			orderByItems.test(orderByClause.getOrderByItems());
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			sb.append(Expression.ORDER_BY);
			sb.append(" ");
			sb.append(orderByItems);
			return sb.toString();
		}
	}

	static final class OrderByItemTester extends AbstractExpressionTester
	{
		private ExpressionTester orderByItem;
		private Ordering ordering;

		OrderByItemTester(ExpressionTester orderByItem, Ordering ordering)
		{
			super();

			this.ordering = ordering;
			this.orderByItem = orderByItem;
		}

		@Override
		public void test(Expression expression)
		{
			assertInstance(expression, OrderByItem.class);

			OrderByItem orderByItem = (OrderByItem) expression;
			assertEquals(toString(), orderByItem.toParsedText());
			assertEquals(!this.orderByItem.isNull(), orderByItem.hasStateFieldPathExpression());
			assertEquals(!this.orderByItem.isNull() && ordering != Ordering.DEFAULT, orderByItem.hasSpaceAfterStateFieldPathExpression());
			assertSame  (ordering, orderByItem.getOrdering());

			this.orderByItem.test(orderByItem.getStateFieldPathExpression());
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			sb.append(orderByItem);

			if (!orderByItem.isNull() && (ordering != Ordering.DEFAULT))
			{
				sb.append(" ");
			}

			if (ordering != Ordering.DEFAULT)
			{
				sb.append(ordering.name());
			}

			return sb.toString();
		}
	}

	static final class OrExpressionTester extends LogicalExpressionTester
	{
		OrExpressionTester(ExpressionTester leftExpression,
		                   ExpressionTester rightExpression)
		{
			super(leftExpression, rightExpression);
		}

		@Override
		Class<? extends CompoundExpression> expressionType()
		{
			return OrExpression.class;
		}

		@Override
		String identifier()
		{
			return Expression.OR;
		}
	}

	static final class RangeVariableDeclarationTester extends AbstractExpressionTester
	{
		private ExpressionTester abstractSchemaName;
		private boolean hasAs;
		private ExpressionTester identificationVariable;

		RangeVariableDeclarationTester(ExpressionTester abstractSchemaName,
		                               boolean hasAs,
		                               ExpressionTester identificationVariable)
		{
			super();

			this.hasAs = hasAs;
			this.abstractSchemaName = abstractSchemaName;
			this.identificationVariable = identificationVariable;
		}

		@Override
		public void test(Expression expression)
		{
			assertInstance(expression, RangeVariableDeclaration.class);

			RangeVariableDeclaration rangeVariableDeclaration = (RangeVariableDeclaration) expression;
			assertEquals(toString(), rangeVariableDeclaration.toParsedText());
			assertEquals(hasAs, rangeVariableDeclaration.hasAs());
			assertEquals(hasAs, rangeVariableDeclaration.hasSpaceAfterAs());
			assertEquals(!identificationVariable.isNull(), rangeVariableDeclaration.hasIdentificationVariable());
			assertTrue  (rangeVariableDeclaration.hasSpaceAfterAbstractSchemaName());

			abstractSchemaName.test(rangeVariableDeclaration.getAbstractSchemaName());
			identificationVariable.test(rangeVariableDeclaration.getIdentificationVariable());
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			sb.append(abstractSchemaName);

			if (hasAs)
			{
				sb.append(" AS");
			}

			sb.append(" ");

			if (!identificationVariable.isNull())
			{
				sb.append(identificationVariable);
			}

			return sb.toString();
		}
	}

	private static final class ResultVariableTester extends AbstractExpressionTester
	{
		private boolean hasAs;
		private ExpressionTester resultVariable;
		private ExpressionTester selectExpression;

		ResultVariableTester(ExpressionTester selectExpression,
		                     boolean hasAs,
		                     ExpressionTester resultVariable)
		{
			super();

			this.hasAs = hasAs;
			this.selectExpression = selectExpression;
			this.resultVariable = resultVariable;
		}

		@Override
		public void test(Expression expression)
		{
			assertInstance(expression, ResultVariable.class);

			ResultVariable resultVariable = (ResultVariable) expression;
			assertEquals(toString(), resultVariable.toParsedText());
			assertEquals(hasAs, resultVariable.hasAs());
			assertEquals(hasAs, resultVariable.hasSpaceAfterAs());
			assertTrue(resultVariable.hasResultVariable());
			assertTrue(resultVariable.hasSelectExpression());

			this.selectExpression.test(resultVariable.getSelectExpression());
			this.resultVariable  .test(resultVariable.getResultVariable());
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			sb.append(selectExpression);
			sb.append(" ");

			if (hasAs)
			{
				sb.append("AS ");
			}

			sb.append(resultVariable);
			return sb.toString();
		}
	}

	static final class SelectClauseTester extends AbstractSelectClauseTester
	{
		SelectClauseTester(ExpressionTester selectExpressions,
		                   boolean hasDistinct)
		{
			super(selectExpressions, hasDistinct);
		}

		@Override
		public void test(Expression expression)
		{
			super.test(expression);
			assertInstance(expression, SelectClause.class);
		}
	}

	static final class SelectStatementTester extends AbstractSelectStatementTester
	{
		private ExpressionTester orderByClause;

		SelectStatementTester(ExpressionTester selectClause,
		                      ExpressionTester fromClause,
		                      ExpressionTester whereClause,
		                      ExpressionTester groupByClause,
		                      ExpressionTester havingClause,
		                      ExpressionTester orderByClause)
		{
			super(selectClause, fromClause, whereClause, groupByClause, havingClause);
			this.orderByClause = orderByClause;
		}

		@Override
		public boolean isNull()
		{
			return false;
		}

		@Override
		public void test(Expression expression)
		{
			super.test(expression);
			assertInstance(expression, SelectStatement.class);

			SelectStatement selectStatement = (SelectStatement) expression;
			assertEquals(!orderByClause.isNull(), selectStatement.hasOrderByClause());
			assertFalse (selectStatement.hasSpaceBeforeGroupBy());

			orderByClause.test(selectStatement.getOrderByClause());
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			sb.append(super.toString());

			if (!orderByClause.isNull())
			{
				sb.append(" ");
				sb.append(orderByClause);
			}

			return sb.toString();
		}
	}

	static final class SimpleFromClauseTester extends AbstractFromClauseTester
	{
		SimpleFromClauseTester(ExpressionTester declaration)
		{
			super(declaration);
		}

		@Override
		public void test(Expression expression)
		{
			super.test(expression);
			assertInstance(expression, SimpleFromClause.class);
		}
	}

	static final class SimpleSelectClauseTester extends AbstractSelectClauseTester
	{
		SimpleSelectClauseTester(ExpressionTester selectExpressions,
		                         boolean hasDistinct)
		{
			super(selectExpressions, hasDistinct);
		}

		@Override
		public void test(Expression expression)
		{
			super.test(expression);
			assertInstance(expression, SimpleSelectClause.class);
		}
	}

	static final class SimpleSelectStatementTester extends AbstractSelectStatementTester
	{
		SimpleSelectStatementTester(ExpressionTester selectClause,
		                            ExpressionTester fromClause,
		                            ExpressionTester whereClause,
		                            ExpressionTester groupByClause,
		                            ExpressionTester havingClause)
		{
			super(selectClause, fromClause, whereClause, groupByClause, havingClause);
		}

		@Override
		public boolean isNull()
		{
			return false;
		}

		@Override
		public void test(Expression expression)
		{
			super.test(expression);
			assertInstance(expression, SimpleSelectStatement.class);
		}
	}

	static final class SizeExpressionTester extends AbstractExpressionTester
	{
		private ExpressionTester collectionValuedPathExpression;

		SizeExpressionTester(ExpressionTester collectionValuedPathExpression)
		{
			super();
			this.collectionValuedPathExpression = collectionValuedPathExpression;
		}

		@Override
		public void test(Expression expression)
		{
			assertInstance(expression, SizeExpression.class);

			SizeExpression sizeExpression = (SizeExpression) expression;
			assertTrue(sizeExpression.hasExpression());
			assertTrue(sizeExpression.hasLeftParenthesis());
			assertTrue(sizeExpression.hasRightParenthesis());

			collectionValuedPathExpression.test(sizeExpression.getExpression());
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			sb.append(Expression.SIZE);
			sb.append("(");
			sb.append(collectionValuedPathExpression);
			sb.append(")");
			return sb.toString();
		}
	}

	static final class SqrtExpressionTester extends AbstractSingleEncapsulatedExpressionTester
	{
		SqrtExpressionTester(ExpressionTester expression)
		{
			super(expression);
		}

		@Override
		Class<? extends AbstractSingleEncapsulatedExpression> expressionType()
		{
			return SqrtExpression.class;
		}

		@Override
		String identifier()
		{
			return Expression.SQRT;
		}
	}

	static final class StateFieldPathExpressionTester extends AbstractPathExpressionTester
	{
		StateFieldPathExpressionTester(String value)
		{
			super(value);
		}

		@Override
		public void test(Expression expression)
		{
			super.test(expression);
			assertInstance(expression, StateFieldPathExpression.class);
		}
	}

	static final class StringLiteralTester extends AbstractExpressionTester
	{
		private String literal;

		StringLiteralTester(String literal)
		{
			super();
			this.literal = literal;
		}

		@Override
		public void test(Expression expression)
		{
			assertInstance(expression, StringLiteral.class);

			StringLiteral stringLiteral = (StringLiteral) expression;
			assertEquals(toString(), stringLiteral.toString());
			assertTrue(stringLiteral.hasCloseQuote());
		}

		@Override
		public String toString()
		{
			return literal;
		}
	}

	static final class SubExpressionTester extends AbstractExpressionTester
	{
		private ExpressionTester expression;

		SubExpressionTester(ExpressionTester expression)
		{
			super();
			this.expression = expression;
		}

		@Override
		public void test(Expression expression)
		{
			assertInstance(expression, SubExpression.class);

			SubExpression subExpression = (SubExpression) expression;
			assertEquals(toString(), subExpression.toParsedText());
			assertTrue(subExpression.hasExpression());
			assertTrue(subExpression.hasRightParenthesis());

			this.expression.test(subExpression.getExpression());
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			sb.append("(");
			sb.append(expression);
			sb.append(")");
			return sb.toString();
		}
	}

	static final class SubstractionExpressionTester extends CompoundExpressionTester
	{
		SubstractionExpressionTester(ExpressionTester leftExpression,
		                             ExpressionTester rightExpression)
		{
			super(leftExpression, rightExpression);
		}

		@Override
		Class<? extends CompoundExpression> expressionType()
		{
			return SubstractionExpression.class;
		}

		@Override
		String identifier()
		{
			return Expression.MINUS;
		}
	}

	static final class SubstringExpressionTester extends AbstractExpressionTester
	{
		private ExpressionTester firstArithmeticExpression;
		private ExpressionTester firstExpression;
		private ExpressionTester secondArithmeticExpression;

		SubstringExpressionTester(ExpressionTester firstExpression,
		                          ExpressionTester firstArithmeticExpression,
		                          ExpressionTester secondArithmeticExpression)
		{
			super();

			this.firstExpression = firstExpression;
			this.firstArithmeticExpression = firstArithmeticExpression;
			this.secondArithmeticExpression = secondArithmeticExpression;
		}

		@Override
		public void test(Expression expression)
		{
			assertInstance(expression, SubstringExpression.class);

			SubstringExpression substringExpression = (SubstringExpression) expression;
			assertEquals(toString(), substringExpression.toParsedText());
			assertTrue  (substringExpression.hasSecondExpression());
			assertTrue  (substringExpression.hasFirstComma());
			assertTrue  (substringExpression.hasFirstExpression());
			assertTrue  (substringExpression.hasLeftParenthesis());
			assertTrue  (substringExpression.hasRightParenthesis());
			assertTrue  (substringExpression.hasThirdExpression());
			assertTrue  (substringExpression.hasSecondComma());
			assertTrue  (substringExpression.hasSpaceAfterFirstComma());
			assertTrue  (substringExpression.hasSpaceAfterSecondComma());

			firstExpression.test(substringExpression.getFirstExpression());
			firstArithmeticExpression.test(substringExpression.getSecondExpression());
			secondArithmeticExpression.test(substringExpression.getThirdExpression());
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			sb.append(Expression.SUBSTRING);
			sb.append("(");
			sb.append(firstExpression);
			sb.append(", ");
			sb.append(firstArithmeticExpression);
			sb.append(", ");
			sb.append(secondArithmeticExpression);
			sb.append(")");
			return sb.toString();
		}
	}

	static final class SumFunctionTester extends AggregateFunctionTester
	{
		SumFunctionTester(ExpressionTester stateFieldPathExpression,
		                  boolean hasDistinct)
		{
			super(stateFieldPathExpression, hasDistinct);
		}

		@Override
		String identifier()
		{
			return Expression.SUM;
		}

		@Override
		public void test(Expression expression)
		{
			super.test(expression);
			assertInstance(expression, SumFunction.class);
		}
	}

	static final class TrimExpressionTester extends AbstractExpressionTester
	{
		private boolean hasFrom;
		private Specification specification;
		private ExpressionTester stringPrimary;
		private ExpressionTester trimCharacter;

		TrimExpressionTester(Specification specification,
		                     ExpressionTester stringPrimary,
		                     ExpressionTester trimCharacter,
		                     boolean hasFrom)
		{
			super();

			this.specification = specification;
			this.stringPrimary = stringPrimary;
			this.trimCharacter = trimCharacter;
			this.hasFrom = hasFrom;
		}

		@Override
		public void test(Expression expression)
		{
			assertInstance(expression, TrimExpression.class);

			TrimExpression trimExpression = (TrimExpression) expression;
			assertEquals(toString(), trimExpression.toParsedText());
			assertEquals(hasFrom, trimExpression.hasFrom());
			assertEquals(hasFrom, trimExpression.hasSpaceAfterFrom());
			assertTrue  (trimExpression.hasLeftParenthesis());
			assertTrue  (trimExpression.hasRightParenthesis());
			assertEquals(!trimCharacter.isNull(), trimExpression.hasSpaceAfterTrimCharacter());
			assertEquals(!trimCharacter.isNull(), trimExpression.hasTrimCharacter());
			assertEquals(specification != Specification.DEFAULT, trimExpression.hasSpecification());
			assertEquals(specification != Specification.DEFAULT, trimExpression.hasSpaceAfterSpecification());
			assertTrue  (trimExpression.hasExpression());

			stringPrimary.test(trimExpression.getExpression());
			trimCharacter.test(trimExpression.getTrimCharacter());
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			sb.append(Expression.TRIM);
			sb.append("(");

			if (specification != Specification.DEFAULT)
			{
				sb.append(specification);
				sb.append(" ");
			}

			if (!trimCharacter.isNull())
			{
				sb.append(" ");
				sb.append(trimCharacter);
			}

			if (hasFrom)
			{
				sb.append(Expression.FROM);
				sb.append(" ");
			}

			sb.append(stringPrimary);
			sb.append(")");
			return sb.toString();
		}
	}

	static final class TypeExpressionTester extends AbstractSingleEncapsulatedExpressionTester
	{
		TypeExpressionTester(ExpressionTester identificationVariable)
		{
			super(identificationVariable);
		}

		@Override
		Class<? extends AbstractSingleEncapsulatedExpression> expressionType()
		{
			return TypeExpression.class;
		}

		@Override
		String identifier()
		{
			return Expression.TYPE;
		}
	}

	static final class UpdateClauseTester extends AbstractExpressionTester
	{
		private ExpressionTester rangeVariableDeclaration;
		private ExpressionTester updateItems;

		UpdateClauseTester(ExpressionTester rangeVariableDeclaration,
		                   ExpressionTester updateItems)
		{
			super();

			this.rangeVariableDeclaration = rangeVariableDeclaration;
			this.updateItems = updateItems;
		}

		@Override
		public void test(Expression expression)
		{
			assertInstance(expression, UpdateClause.class);

			UpdateClause updateClause = (UpdateClause) expression;
			assertEquals(toString(), updateClause.toParsedText());
			assertTrue(updateClause.hasRangeVariableDeclaration());
			assertTrue(updateClause.hasSet());

			if (updateClause.hasRangeVariableDeclaration())
			{
				RangeVariableDeclaration variableDeclaration = (RangeVariableDeclaration) updateClause.getRangeVariableDeclaration();

				if (!variableDeclaration.hasIdentificationVariable())
				{
					assertFalse(updateClause.hasSpaceAfterRangeVariableDeclaration());
				}
				else
				{
					assertTrue(updateClause.hasSpaceAfterRangeVariableDeclaration());
				}
			}
			else
			{
				assertTrue(updateClause.hasSpaceAfterRangeVariableDeclaration());
			}

			assertTrue(updateClause.hasSpaceAfterSet());
			assertTrue(updateClause.hasSpaceAfterUpdate());
			assertTrue(updateClause.hasUpdateItems());

			rangeVariableDeclaration.test(updateClause.getRangeVariableDeclaration());
			updateItems.test(updateClause.getUpdateItems());
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			sb.append(Expression.UPDATE);
			sb.append(" ");
			sb.append(rangeVariableDeclaration);

			if (rangeVariableDeclaration.toString().endsWith(" "))
			{
				sb.append("SET ");
			}
			else
			{
				sb.append(" SET ");
			}

			sb.append(updateItems);
			return sb.toString();
		}
	}

	static final class UpdateItemTester extends AbstractExpressionTester
	{
		private ExpressionTester newValue;
		private ExpressionTester stateFieldPathExpression;

		UpdateItemTester(ExpressionTester stateFieldPathExpression,
		                 ExpressionTester newValue)
		{
			super();

			this.stateFieldPathExpression = stateFieldPathExpression;
			this.newValue = newValue;
		}

		@Override
		public void test(Expression expression)
		{
			assertInstance(expression, UpdateItem.class);

			UpdateItem updateItem = (UpdateItem) expression;
			assertEquals(toString(), updateItem.toParsedText());
			assertTrue(updateItem.hasEqualSign());
			assertTrue(updateItem.hasNewValue());
			assertTrue(updateItem.hasSpaceAfterEqualSign());
			assertTrue(updateItem.hasSpaceAfterStateFieldPathExpression());
			assertTrue(updateItem.hasStateFieldPathExpression());

			stateFieldPathExpression.test(updateItem.getStateFieldPathExpression());
			newValue.test(updateItem.getNewValue());
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			sb.append(stateFieldPathExpression);
			sb.append(" = ");
			sb.append(newValue);
			return sb.toString();
		}
	}

	static final class UpdateStatementTester extends AbstractExpressionTester
	{
		public boolean hasSpaceAfterUpdateClause;
		private ExpressionTester updateClause;
		private ExpressionTester whereClause;

		UpdateStatementTester(ExpressionTester updateClause,
                            ExpressionTester whereClause)
		{
			super();

			this.updateClause = updateClause;
			this.whereClause  = whereClause;
			this.hasSpaceAfterUpdateClause = !whereClause.isNull();
		}

		@Override
		public void test(Expression expression)
		{
			assertInstance(expression, UpdateStatement.class);

			UpdateStatement updateStatement = (UpdateStatement) expression;
			assertEquals(toString(), updateStatement.toParsedText());
			assertEquals(hasSpaceAfterUpdateClause, updateStatement.hasSpaceAfterUpdateClause());
			assertEquals(!whereClause.isNull(), updateStatement.hasWhereClause());

			updateClause.test(updateStatement.getUpdateClause());
			whereClause .test(updateStatement.getWhereClause());
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			sb.append(updateClause);

			if (hasSpaceAfterUpdateClause)
			{
				sb.append(" ");
			}

			sb.append(whereClause);
			return sb.toString();
		}
	}

	static final class UpperExpressionTester extends AbstractSingleEncapsulatedExpressionTester
	{
		UpperExpressionTester(ExpressionTester expression)
		{
			super(expression);
		}

		@Override
		Class<? extends AbstractSingleEncapsulatedExpression> expressionType()
		{
			return UpperExpression.class;
		}

		@Override
		String identifier()
		{
			return Expression.UPPER;
		}
	}

	static final class WhenClauseTester extends AbstractExpressionTester
	{
		private ExpressionTester conditionalExpression;
		private ExpressionTester thenExpression;

		WhenClauseTester(ExpressionTester conditionalExpression,
		                 ExpressionTester thenExpression)
		{
			super();

			this.conditionalExpression = conditionalExpression;
			this.thenExpression = thenExpression;
		}

		@Override
		public void test(Expression expression)
		{
			assertInstance(expression, WhenClause.class);

			WhenClause whenClause = (WhenClause) expression;
			assertEquals(toString(), whenClause.toParsedText());
			assertTrue  (whenClause.hasWhenExpression());
			assertTrue  (whenClause.hasSpaceAfterWhenExpression());
			assertTrue  (whenClause.hasSpaceAfterThen());
			assertTrue  (whenClause.hasSpaceAfterWhen());
			assertTrue  (whenClause.hasThen());
			assertTrue  (whenClause.hasThenExpression());

			conditionalExpression.test(whenClause.getWhenExpression());
			thenExpression.test(whenClause.getThenExpression());
		}

		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			sb.append(Expression.WHEN);
			sb.append(" ");
			sb.append(conditionalExpression);
			sb.append(" ");
			sb.append(Expression.THEN);
			sb.append(" ");
			sb.append(thenExpression);
			return sb.toString();
		}
	}

	static final class WhereClauseTester extends AbstractConditionalClauseTester
	{
		WhereClauseTester(ExpressionTester conditionalExpression)
		{
			super(conditionalExpression);
		}

		@Override
		Class<? extends AbstractConditionalClause> expressionType()
		{
			return WhereClause.class;
		}

		@Override
		String identifier()
		{
			return Expression.WHERE;
		}
	}
}