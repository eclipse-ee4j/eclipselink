/*
 * Copyright (c) 2006, 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation
//     06/02/2023: Radek Felcman
//       - Issue 1885: Implement new JPQLGrammar for upcoming Jakarta Persistence 3.2
package org.eclipse.persistence.jpa.jpql.tools.model.query;

/**
 * The abstract definition of {@link StateObjectVisitor}, which implements all the methods but does
 * nothing. It can be subclassed so that only the required methods are overridden.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
public abstract class AbstractStateObjectVisitor implements StateObjectVisitor {

    @Override
    public void visit(AbsExpressionStateObject stateObject) {
    }

    @Override
    public void visit(AbstractSchemaNameStateObject stateObject) {
    }

    @Override
    public void visit(AdditionExpressionStateObject stateObject) {
    }

    @Override
    public void visit(AllOrAnyExpressionStateObject stateObject) {
    }

    @Override
    public void visit(AndExpressionStateObject stateObject) {
    }

    @Override
    public void visit(ArithmeticFactorStateObject stateObject) {
    }

    @Override
    public void visit(AvgFunctionStateObject stateObject) {
    }

    @Override
    public void visit(BadExpressionStateObject stateObject) {
    }

    @Override
    public void visit(BetweenExpressionStateObject stateObject) {
    }

    @Override
    public void visit(CaseExpressionStateObject stateObject) {
    }

    @Override
    public void visit(CoalesceExpressionStateObject stateObject) {
    }

    @Override
    public void visit(CollectionMemberDeclarationStateObject stateObject) {
    }

    @Override
    public void visit(CollectionMemberExpressionStateObject stateObject) {
    }

    @Override
    public void visit(CollectionValuedPathExpressionStateObject stateObject) {
    }

    @Override
    public void visit(ComparisonExpressionStateObject stateObject) {
    }

    @Override
    public void visit(ConcatExpressionStateObject stateObject) {
    }

    @Override
    public void visit(ConcatPipesExpressionStateObject stateObject) {
    }

    @Override
    public void visit(ConstructorExpressionStateObject stateObject) {
    }

    @Override
    public void visit(CountFunctionStateObject stateObject) {
    }

    @Override
    public void visit(DateTimeStateObject stateObject) {
    }

    @Override
    public void visit(DeleteClauseStateObject stateObject) {
    }

    @Override
    public void visit(DeleteStatementStateObject stateObject) {
    }

    @Override
    public void visit(DerivedPathIdentificationVariableDeclarationStateObject stateObject) {
    }

    @Override
    public void visit(DerivedPathVariableDeclarationStateObject stateObject) {
    }

    @Override
    public void visit(DivisionExpressionStateObject stateObject) {
    }

    @Override
    public void visit(EmptyCollectionComparisonExpressionStateObject stateObject) {
    }

    @Override
    public void visit(EntityTypeLiteralStateObject stateObject) {
    }

    @Override
    public void visit(EntryExpressionStateObject stateObject) {
    }

    @Override
    public void visit(EnumTypeStateObject stateObject) {
    }

    @Override
    public void visit(ExistsExpressionStateObject stateObject) {
    }

    @Override
    public void visit(FromClauseStateObject stateObject) {
    }

    @Override
    public void visit(FunctionExpressionStateObject stateObject) {
    }

    @Override
    public void visit(GroupByClauseStateObject stateObject) {
    }

    @Override
    public void visit(HavingClauseStateObject stateObject) {
    }

    @Override
    public void visit(IdentificationVariableDeclarationStateObject stateObject) {
    }

    @Override
    public void visit(IdentificationVariableStateObject stateObject) {
    }

    @Override
    public void visit(IndexExpressionStateObject stateObject) {
    }

    @Override
    public void visit(InExpressionStateObject stateObject) {
    }

    @Override
    public void visit(InputParameterStateObject stateObject) {
    }

    @Override
    public void visit(JoinStateObject stateObject) {
    }

    @Override
    public void visit(JPQLQueryStateObject stateObject) {
    }

    @Override
    public void visit(KeyExpressionStateObject stateObject) {
    }

    @Override
    public void visit(KeywordExpressionStateObject stateObject) {
    }

    @Override
    public void visit(LengthExpressionStateObject stateObject) {
    }

    @Override
    public void visit(LikeExpressionStateObject stateObject) {
    }

    @Override
    public void visit(LocateExpressionStateObject stateObject) {
    }

    @Override
    public void visit(LowerExpressionStateObject stateObject) {
    }

    @Override
    public void visit(MaxFunctionStateObject stateObject) {
    }

    @Override
    public void visit(MinFunctionStateObject stateObject) {
    }

    @Override
    public void visit(ModExpressionStateObject stateObject) {
    }

    @Override
    public void visit(MultiplicationExpressionStateObject stateObject) {
    }

    @Override
    public void visit(NotExpressionStateObject stateObject) {
    }

    @Override
    public void visit(NullComparisonExpressionStateObject stateObject) {
    }

    @Override
    public void visit(NullIfExpressionStateObject stateObject) {
    }

    @Override
    public void visit(NumericLiteralStateObject stateObject) {
    }

    @Override
    public void visit(ObjectExpressionStateObject stateObject) {
    }

    @Override
    public void visit(OrderByClauseStateObject stateObject) {
    }

    @Override
    public void visit(OrderByItemStateObject stateObject) {
    }

    @Override
    public void visit(OrExpressionStateObject stateObject) {
    }

    @Override
    public void visit(RangeVariableDeclarationStateObject stateObject) {
    }

    @Override
    public void visit(ReplaceExpressionStateObject stateObject) {
    }

    @Override
    public void visit(ResultVariableStateObject stateObject) {
    }

    @Override
    public void visit(SelectClauseStateObject stateObject) {
    }

    @Override
    public void visit(SelectStatementStateObject stateObject) {
    }

    @Override
    public void visit(SimpleFromClauseStateObject stateObject) {
    }

    @Override
    public void visit(SimpleSelectClauseStateObject stateObject) {
    }

    @Override
    public void visit(SimpleSelectStatementStateObject stateObject) {
    }

    @Override
    public void visit(SizeExpressionStateObject stateObject) {
    }

    @Override
    public void visit(SqrtExpressionStateObject stateObject) {
    }

    @Override
    public void visit(StateFieldPathExpressionStateObject stateObject) {
    }

    @Override
    public void visit(StringLiteralStateObject stateObject) {
    }

    @Override
    public void visit(SubExpressionStateObject stateObject) {
    }

    @Override
    public void visit(SubstringExpressionStateObject stateObject) {
    }

    @Override
    public void visit(SubtractionExpressionStateObject stateObject) {
    }

    @Override
    public void visit(SumFunctionStateObject stateObject) {
    }

    @Override
    public void visit(TreatExpressionStateObject stateObject) {
    }

    @Override
    public void visit(TrimExpressionStateObject stateObject) {
    }

    @Override
    public void visit(TypeExpressionStateObject stateObject) {
    }

    @Override
    public void visit(UnknownExpressionStateObject stateObject) {
    }

    @Override
    public void visit(UpdateClauseStateObject stateObject) {
    }

    @Override
    public void visit(UpdateItemStateObject stateObject) {
    }

    @Override
    public void visit(UpdateStatementStateObject stateObject) {
    }

    @Override
    public void visit(UpperExpressionStateObject stateObject) {
    }

    @Override
    public void visit(ValueExpressionStateObject stateObject) {
    }

    @Override
    public void visit(WhenClauseStateObject stateObject) {
    }

    @Override
    public void visit(WhereClauseStateObject stateObject) {
    }
}
