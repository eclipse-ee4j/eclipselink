/*******************************************************************************
 * Copyright (c) 2006, 2012 Oracle. All rights reserved.
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
package org.eclipse.persistence.jpa.jpql.model.query;

/**
 * The visitor is used to traverse the {@link StateObject} hierarchy that represents a JPQL query.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public interface StateObjectVisitor {

	/**
	 * Visits the given {@link AbsExpressionStateObject}.
	 *
	 * @param stateObject The {@link AbsExpressionStateObject} to visit
	 */
	void visit(AbsExpressionStateObject stateObject);

	/**
	 * Visits the given {@link AbstractSchemaNameStateObject}.
	 *
	 * @param stateObject The {@link AbstractSchemaNameStateObject} to visit
	 */
	void visit(AbstractSchemaNameStateObject stateObject);

	/**
	 * Visits the given {@link AdditionExpressionStateObject}.
	 *
	 * @param stateObject The {@link AdditionExpressionStateObject} to visit
	 */
	void visit(AdditionExpressionStateObject stateObject);

	/**
	 * Visits the given {@link AllOrAnyExpressionStateObject}.
	 *
	 * @param stateObject The {@link AllOrAnyExpressionStateObject} to visit
	 */
	void visit(AllOrAnyExpressionStateObject stateObject);

	/**
	 * Visits the given {@link AndExpressionStateObject}.
	 *
	 * @param stateObject The {@link AndExpressionStateObject} to visit
	 */
	void visit(AndExpressionStateObject stateObject);

	/**
	 * Visits the given {@link ArithmeticFactorStateObject}.
	 *
	 * @param stateObject The {@link ArithmeticFactorStateObject} to visit
	 */
	void visit(ArithmeticFactorStateObject stateObject);

	/**
	 * Visits the given {@link AvgFunctionStateObject}.
	 *
	 * @param stateObject The {@link AvgFunctionStateObject} to visit
	 */
	void visit(AvgFunctionStateObject stateObject);

	/**
	 * Visits the given {@link BadExpressionStateObject}.
	 *
	 * @param stateObject The {@link BadExpressionStateObject} to visit
	 */
	void visit(BadExpressionStateObject stateObject);

	/**
	 * Visits the given {@link BetweenExpressionStateObject}.
	 *
	 * @param stateObject The {@link BetweenExpressionStateObject} to visit
	 */
	void visit(BetweenExpressionStateObject stateObject);

	/**
	 * Visits the given {@link CaseExpressionStateObject}.
	 *
	 * @param stateObject The {@link CaseExpressionStateObject} to visit
	 */
	void visit(CaseExpressionStateObject stateObject);

	/**
	 * Visits the given {@link CoalesceExpressionStateObject}.
	 *
	 * @param stateObject The {@link CoalesceExpressionStateObject} to visit
	 */
	void visit(CoalesceExpressionStateObject stateObject);

	/**
	 * Visits the given {@link CollectionMemberVariableDeclarationStateObject}.
	 *
	 * @param stateObject The {@link CollectionMemberVariableDeclarationStateObject} to visit
	 */
	void visit(CollectionMemberDeclarationStateObject stateObject);

	/**
	 * Visits the given {@link CollectionMemberExpressionStateObject}.
	 *
	 * @param stateObject The {@link CollectionMemberExpressionStateObject} to visit
	 */
	void visit(CollectionMemberExpressionStateObject stateObject);

	/**
	 * Visits the given {@link CollectionValuedPathExpressionStateObject}.
	 *
	 * @param stateObject The {@link CollectionValuedPathExpressionStateObject} to visit
	 */
	void visit(CollectionValuedPathExpressionStateObject stateObject);

	/**
	 * Visits the given {@link ComparisonExpressionStateObject}.
	 *
	 * @param stateObject The {@link ComparisonExpressionStateObject} to visit
	 */
	void visit(ComparisonExpressionStateObject stateObject);

	/**
	 * Visits the given {@link ConcatExpressionStateObject}.
	 *
	 * @param stateObject The {@link ConcatExpressionStateObject} to visit
	 */
	void visit(ConcatExpressionStateObject stateObject);

	/**
	 * Visits the given {@link ConstructorExpressionStateObject}.
	 *
	 * @param stateObject The {@link ConstructorExpressionStateObject} to visit
	 */
	void visit(ConstructorExpressionStateObject stateObject);

	/**
	 * Visits the given {@link CountFunctionStateObject}.
	 *
	 * @param stateObject The {@link CountFunctionStateObject} to visit
	 */
	void visit(CountFunctionStateObject stateObject);

	/**
	 * Visits the given {@link DateTimeStateObject}.
	 *
	 * @param stateObject The {@link DateTimeStateObject} to visit
	 */
	void visit(DateTimeStateObject stateObject);

	/**
	 * Visits the given {@link DeleteClauseStateObject}.
	 *
	 * @param stateObject The {@link DeleteClauseStateObject} to visit
	 */
	void visit(DeleteClauseStateObject stateObject);

	/**
	 * Visits the given {@link DeleteStatementStateObject}.
	 *
	 * @param stateObject The {@link DeleteStatementStateObject} to visit
	 */
	void visit(DeleteStatementStateObject stateObject);

	/**
	 * Visits the given {@link DerivedPathDeclarationStateObject}.
	 *
	 * @param stateObject The {@link DerivedPathDeclarationStateObject} to visit
	 */
	void visit(DerivedPathVariableDeclarationStateObject stateObject);

	/**
	 * Visits the given {@link DerivedPathIdentificationVariableDeclarationStateObject}.
	 *
	 * @param stateObject The {@link DerivedPathIdentificationVariableDeclarationStateObject} to visit
	 */
	void visit(DerivedPathIdentificationVariableDeclarationStateObject stateObject);

	/**
	 * Visits the given {@link DivisionExpressionStateObject}.
	 *
	 * @param stateObject The {@link DivisionExpressionStateObject} to visit
	 */
	void visit(DivisionExpressionStateObject stateObject);

	/**
	 * Visits the given {@link EmptyCollectionComparisonExpressionStateObject}.
	 *
	 * @param stateObject The {@link EmptyCollectionComparisonExpressionStateObject} to visit
	 */
	void visit(EmptyCollectionComparisonExpressionStateObject stateObject);

	/**
	 * Visits the given {@link EntityTypeLiteralStateObject}.
	 *
	 * @param stateObject The {@link EntityTypeLiteralStateObject} to visit
	 */
	void visit(EntityTypeLiteralStateObject stateObject);

	/**
	 * Visits the given {@link EntryExpressionStateObject}.
	 *
	 * @param stateObject The {@link EntryExpressionStateObject} to visit
	 */
	void visit(EntryExpressionStateObject stateObject);

	/**
	 * Visits the given {@link EnumTypeStateObject}.
	 *
	 * @param stateObject The {@link EnumTypeStateObject} to visit
	 */
	void visit(EnumTypeStateObject stateObject);

	/**
	 * Visits the given {@link ExistsExpressionStateObject}.
	 *
	 * @param stateObject The {@link ExistsExpressionStateObject} to visit
	 */
	void visit(ExistsExpressionStateObject stateObject);

	/**
	 * Visits the given {@link FromClauseStateObject}.
	 *
	 * @param stateObject The {@link FromClauseStateObject} to visit
	 */
	void visit(FromClauseStateObject stateObject);

	/**
	 * Visits the given {@link GroupByClauseStateObject}.
	 *
	 * @param stateObject The {@link GroupByClauseStateObject} to visit
	 */
	void visit(GroupByClauseStateObject stateObject);

	/**
	 * Visits the given {@link HavingClauseStateObject}.
	 *
	 * @param stateObject The {@link HavingClauseStateObject} to visit
	 */
	void visit(HavingClauseStateObject stateObject);

	/**
	 * Visits the given {@link IdentificationVariableDeclarationStateObject}.
	 *
	 * @param stateObject The {@link IdentificationVariableDeclarationStateObject} to visit
	 */
	void visit(IdentificationVariableDeclarationStateObject stateObject);

	/**
	 * Visits the given {@link IdentificationVariableStateObject}.
	 *
	 * @param stateObject The {@link IdentificationVariableStateObject} to visit
	 */
	void visit(IdentificationVariableStateObject stateObject);

	/**
	 * Visits the given {@link IndexExpressionStateObject}.
	 *
	 * @param stateObject The {@link IndexExpressionStateObject} to visit
	 */
	void visit(IndexExpressionStateObject stateObject);

	/**
	 * Visits the given {@link InExpressionStateObject}.
	 *
	 * @param stateObject The {@link InExpressionStateObject} to visit
	 */
	void visit(InExpressionStateObject stateObject);

	/**
	 * Visits the given {@link InputParameterStateObject}.
	 *
	 * @param stateObject The {@link InputParameterStateObject} to visit
	 */
	void visit(InputParameterStateObject stateObject);

	/**
	 * Visits the given {@link JoinStateObject}.
	 *
	 * @param stateObject The {@link JPQLQueryStateObject} to visit
	 */
	void visit(JoinStateObject stateObject);

	/**
	 * Visits the given {@link JPQLQueryStateObject}.
	 *
	 * @param stateObject The {@link JPQLQueryStateObject} to visit
	 */
	void visit(JPQLQueryStateObject stateObject);

	/**
	 * Visits the given {@link KeyExpressionStateObject}.
	 *
	 * @param stateObject The {@link KeyExpressionStateObject} to visit
	 */
	void visit(KeyExpressionStateObject stateObject);

	/**
	 * Visits the given {@link KeywordExpressionStateObject}.
	 *
	 * @param stateObject The {@link KeywordExpressionStateObject} to visit
	 */
	void visit(KeywordExpressionStateObject stateObject);

	/**
	 * Visits the given {@link LengthExpressionStateObject}.
	 *
	 * @param stateObject The {@link LengthExpressionStateObject} to visit
	 */
	void visit(LengthExpressionStateObject stateObject);

	/**
	 * Visits the given {@link LikeExpressionStateObject}.
	 *
	 * @param stateObject The {@link LikeExpressionStateObject} to visit
	 */
	void visit(LikeExpressionStateObject stateObject);

	/**
	 * Visits the given {@link LocateExpressionStateObject}.
	 *
	 * @param stateObject The {@link LocateExpressionStateObject} to visit
	 */
	void visit(LocateExpressionStateObject stateObject);

	/**
	 * Visits the given {@link LowerExpressionStateObject}.
	 *
	 * @param stateObject The {@link LowerExpressionStateObject} to visit
	 */
	void visit(LowerExpressionStateObject stateObject);

	/**
	 * Visits the given {@link MaxFunctionStateObject}.
	 *
	 * @param stateObject The {@link MaxFunctionStateObject} to visit
	 */
	void visit(MaxFunctionStateObject stateObject);

	/**
	 * Visits the given {@link WhereClauseStateObject}.
	 *
	 * @param stateObject The {@link WhereClauseStateObject} to visit
	 */
	void visit(MinFunctionStateObject stateObject);

	/**
	 * Visits the given {@link ModExpressionStateObject}.
	 *
	 * @param stateObject The {@link ModExpressionStateObject} to visit
	 */
	void visit(ModExpressionStateObject stateObject);

	/**
	 * Visits the given {@link MultiplicationExpressionStateObject}.
	 *
	 * @param stateObject The {@link MultiplicationExpressionStateObject} to visit
	 */
	void visit(MultiplicationExpressionStateObject stateObject);

	/**
	 * Visits the given {@link NotExpressionStateObject}.
	 *
	 * @param stateObject The {@link NotExpressionStateObject} to visit
	 */
	void visit(NotExpressionStateObject stateObject);

	/**
	 * Visits the given {@link NullComparisonExpressionStateObject}.
	 *
	 * @param stateObject The {@link NullComparisonExpressionStateObject} to visit
	 */
	void visit(NullComparisonExpressionStateObject stateObject);

	/**
	 * Visits the given {@link NullIfExpressionStateObject}.
	 *
	 * @param stateObject The {@link NullIfExpressionStateObject} to visit
	 */
	void visit(NullIfExpressionStateObject stateObject);

	/**
	 * Visits the given {@link NumericLiteralStateObject}.
	 *
	 * @param stateObject The {@link NumericLiteralStateObject} to visit
	 */
	void visit(NumericLiteralStateObject stateObject);

	/**
	 * Visits the given {@link ObjectExpressionStateObject}.
	 *
	 * @param stateObject The {@link ObjectExpressionStateObject} to visit
	 */
	void visit(ObjectExpressionStateObject stateObject);

	/**
	 * Visits the given {@link OrderByClauseStateObject}.
	 *
	 * @param stateObject The {@link OrderByClauseStateObject} to visit
	 */
	void visit(OrderByClauseStateObject stateObject);

	/**
	 * Visits the given {@link OrderByItemStateObject}.
	 *
	 * @param stateObject The {@link OrderByItemStateObject} to visit
	 */
	void visit(OrderByItemStateObject stateObject);

	/**
	 * Visits the given {@link OrExpressionStateObject}.
	 *
	 * @param stateObject The {@link OrExpressionStateObject} to visit
	 */
	void visit(OrExpressionStateObject stateObject);

	/**
	 * Visits the given {@link RangeVariableDeclarationStateObject}.
	 *
	 * @param stateObject The {@link RangeVariableDeclarationStateObject} to visit
	 */
	void visit(RangeVariableDeclarationStateObject stateObject);

	/**
	 * Visits the given {@link ResultVariableStateObject}.
	 *
	 * @param stateObject The {@link ResultVariableStateObject} to visit
	 */
	void visit(ResultVariableStateObject stateObject);

	/**
	 * Visits the given {@link SelectClauseStateObject}.
	 *
	 * @param stateObject The {@link SelectClauseStateObject} to visit
	 */
	void visit(SelectClauseStateObject stateObject);

	/**
	 * Visits the given {@link SelectStatementStateObject}.
	 *
	 * @param stateObject The {@link SelectStatementStateObject} to visit
	 */
	void visit(SelectStatementStateObject stateObject);

	/**
	 * Visits the given {@link SimpleFromClauseStateObject}.
	 *
	 * @param stateObject The {@link SimpleFromClauseStateObject} to visit
	 */
	void visit(SimpleFromClauseStateObject stateObject);

	/**
	 * Visits the given {@link SimpleSelectClauseStateObject}.
	 *
	 * @param stateObject The {@link SimpleSelectClauseStateObject} to visit
	 */
	void visit(SimpleSelectClauseStateObject stateObject);

	/**
	 * Visits the given {@link SimpleSelectStatementStateObject}.
	 *
	 * @param stateObject The {@link SimpleSelectStatementStateObject} to visit
	 */
	void visit(SimpleSelectStatementStateObject stateObject);

	/**
	 * Visits the given {@link SizeExpressionStateObject}.
	 *
	 * @param stateObject The {@link SizeExpressionStateObject} to visit
	 */
	void visit(SizeExpressionStateObject stateObject);

	/**
	 * Visits the given {@link SqrtExpressionStateObject}.
	 *
	 * @param stateObject The {@link SqrtExpressionStateObject} to visit
	 */
	void visit(SqrtExpressionStateObject stateObject);

	/**
	 * Visits the given {@link StateFieldPathExpressionStateObject}.
	 *
	 * @param stateObject The {@link StateFieldPathExpressionStateObject} to visit
	 */
	void visit(StateFieldPathExpressionStateObject stateObject);

	/**
	 * Visits the given {@link StringLiteralStateObject}.
	 *
	 * @param stateObject The {@link StringLiteralStateObject} to visit
	 */
	void visit(StringLiteralStateObject stateObject);

	/**
	 * Visits the given {@link SubExpressionStateObject}.
	 *
	 * @param stateObject The {@link SubExpressionStateObject} to visit
	 */
	void visit(SubExpressionStateObject stateObject);

	/**
	 * Visits the given {@link SubstringExpressionStateObject}.
	 *
	 * @param stateObject The {@link SubstringExpressionStateObject} to visit
	 */
	void visit(SubstringExpressionStateObject stateObject);

	/**
	 * Visits the given {@link SubtractionExpressionStateObject}.
	 *
	 * @param stateObject The {@link SubtractionExpressionStateObject} to visit
	 */
	void visit(SubtractionExpressionStateObject stateObject);

	/**
	 * Visits the given {@link SumFunctionStateObject}.
	 *
	 * @param stateObject The {@link SumFunctionStateObject} to visit
	 */
	void visit(SumFunctionStateObject stateObject);

	/**
	 * Visits the given {@link TrimExpressionStateObject}.
	 *
	 * @param stateObject The {@link TrimExpressionStateObject} to visit
	 */
	void visit(TrimExpressionStateObject stateObject);

	/**
	 * Visits the given {@link TypeExpressionStateObject}.
	 *
	 * @param stateObject The {@link TypeExpressionStateObject} to visit
	 */
	void visit(TypeExpressionStateObject stateObject);

	/**
	 * Visits the given {@link UnknownExpressionStateObject}.
	 *
	 * @param stateObject The {@link UnknownExpressionStateObject} to visit
	 */
	void visit(UnknownExpressionStateObject stateObject);

	/**
	 * Visits the given {@link UpdateClauseStateObject}.
	 *
	 * @param stateObject The {@link UpdateClauseStateObject} to visit
	 */
	void visit(UpdateClauseStateObject stateObject);

	/**
	 * Visits the given {@link UpdateItemStateObject}.
	 *
	 * @param stateObject The {@link UpdateItemStateObject} to visit
	 */
	void visit(UpdateItemStateObject stateObject);

	/**
	 * Visits the given {@link UpdateStatementStateObject}.
	 *
	 * @param stateObject The {@link UpdateStatementStateObject} to visit
	 */
	void visit(UpdateStatementStateObject stateObject);

	/**
	 * Visits the given {@link UpperExpressionStateObject}.
	 *
	 * @param stateObject The {@link UpperExpressionStateObject} to visit
	 */
	void visit(UpperExpressionStateObject stateObject);

	/**
	 * Visits the given {@link ValueExpressionStateObject}.
	 *
	 * @param stateObject The {@link ValueExpressionStateObject} to visit
	 */
	void visit(ValueExpressionStateObject stateObject);

	/**
	 * Visits the given {@link WhenClauseStateObject}.
	 *
	 * @param stateObject The {@link WhenClauseStateObject} to visit
	 */
	void visit(WhenClauseStateObject stateObject);

	/**
	 * Visits the given {@link WhereClauseStateObject}.
	 *
	 * @param stateObject The {@link WhereClauseStateObject} to visit
	 */
	void visit(WhereClauseStateObject stateObject);
}