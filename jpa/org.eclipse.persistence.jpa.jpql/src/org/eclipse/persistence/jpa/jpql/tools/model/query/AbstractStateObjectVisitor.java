/*
 * Copyright (c) 2006, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation
//
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(AbsExpressionStateObject stateObject) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(AbstractSchemaNameStateObject stateObject) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(AdditionExpressionStateObject stateObject) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(AllOrAnyExpressionStateObject stateObject) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(AndExpressionStateObject stateObject) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(ArithmeticFactorStateObject stateObject) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(AvgFunctionStateObject stateObject) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(BadExpressionStateObject stateObject) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(BetweenExpressionStateObject stateObject) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(CaseExpressionStateObject stateObject) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(CoalesceExpressionStateObject stateObject) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(CollectionMemberDeclarationStateObject stateObject) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(CollectionMemberExpressionStateObject stateObject) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(CollectionValuedPathExpressionStateObject stateObject) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(ComparisonExpressionStateObject stateObject) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(ConcatExpressionStateObject stateObject) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(ConstructorExpressionStateObject stateObject) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(CountFunctionStateObject stateObject) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(DateTimeStateObject stateObject) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(DeleteClauseStateObject stateObject) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(DeleteStatementStateObject stateObject) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(DerivedPathIdentificationVariableDeclarationStateObject stateObject) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(DerivedPathVariableDeclarationStateObject stateObject) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(DivisionExpressionStateObject stateObject) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(EmptyCollectionComparisonExpressionStateObject stateObject) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(EntityTypeLiteralStateObject stateObject) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(EntryExpressionStateObject stateObject) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(EnumTypeStateObject stateObject) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(ExistsExpressionStateObject stateObject) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(FromClauseStateObject stateObject) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(FunctionExpressionStateObject stateObject) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(GroupByClauseStateObject stateObject) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(HavingClauseStateObject stateObject) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(IdentificationVariableDeclarationStateObject stateObject) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(IdentificationVariableStateObject stateObject) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(IndexExpressionStateObject stateObject) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(InExpressionStateObject stateObject) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(InputParameterStateObject stateObject) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(JoinStateObject stateObject) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(JPQLQueryStateObject stateObject) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(KeyExpressionStateObject stateObject) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(KeywordExpressionStateObject stateObject) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(LengthExpressionStateObject stateObject) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(LikeExpressionStateObject stateObject) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(LocateExpressionStateObject stateObject) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(LowerExpressionStateObject stateObject) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(MaxFunctionStateObject stateObject) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(MinFunctionStateObject stateObject) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(ModExpressionStateObject stateObject) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(MultiplicationExpressionStateObject stateObject) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(NotExpressionStateObject stateObject) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(NullComparisonExpressionStateObject stateObject) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(NullIfExpressionStateObject stateObject) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(NumericLiteralStateObject stateObject) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(ObjectExpressionStateObject stateObject) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(OrderByClauseStateObject stateObject) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(OrderByItemStateObject stateObject) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(OrExpressionStateObject stateObject) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(RangeVariableDeclarationStateObject stateObject) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(ResultVariableStateObject stateObject) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(SelectClauseStateObject stateObject) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(SelectStatementStateObject stateObject) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(SimpleFromClauseStateObject stateObject) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(SimpleSelectClauseStateObject stateObject) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(SimpleSelectStatementStateObject stateObject) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(SizeExpressionStateObject stateObject) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(SqrtExpressionStateObject stateObject) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(StateFieldPathExpressionStateObject stateObject) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(StringLiteralStateObject stateObject) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(SubExpressionStateObject stateObject) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(SubstringExpressionStateObject stateObject) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(SubtractionExpressionStateObject stateObject) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(SumFunctionStateObject stateObject) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(TreatExpressionStateObject stateObject) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(TrimExpressionStateObject stateObject) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(TypeExpressionStateObject stateObject) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(UnknownExpressionStateObject stateObject) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(UpdateClauseStateObject stateObject) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(UpdateItemStateObject stateObject) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(UpdateStatementStateObject stateObject) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(UpperExpressionStateObject stateObject) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(ValueExpressionStateObject stateObject) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(WhenClauseStateObject stateObject) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(WhereClauseStateObject stateObject) {
    }
}
