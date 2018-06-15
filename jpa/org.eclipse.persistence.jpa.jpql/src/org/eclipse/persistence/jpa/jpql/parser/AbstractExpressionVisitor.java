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
package org.eclipse.persistence.jpa.jpql.parser;

/**
 * The abstract definition of {@link ExpressionVisitor}, which implements all the methods but does
 * nothing. It can be subclassed so that only the required methods are overridden.
 * <p>
 * Provisional API: This interface is part of an interim API that is still under development and
 * expected to change significantly before reaching stability. It is available at this early stage
 * to solicit feedback from pioneering adopters on the understanding that any code that uses this
 * API will almost certainly be broken (repeatedly) as the API evolves.
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public abstract class AbstractExpressionVisitor implements ExpressionVisitor {

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(AbsExpression expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(AbstractSchemaName expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(AdditionExpression expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(AllOrAnyExpression expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(AndExpression expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(ArithmeticFactor expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(AvgFunction expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(BadExpression expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(BetweenExpression expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(CaseExpression expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(CoalesceExpression expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(CollectionExpression expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(CollectionMemberDeclaration expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(CollectionMemberExpression expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(CollectionValuedPathExpression expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(ComparisonExpression expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(ConcatExpression expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(ConstructorExpression expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(CountFunction expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(DateTime expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(DeleteClause expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(DeleteStatement expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(DivisionExpression expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(EmptyCollectionComparisonExpression expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(EntityTypeLiteral expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(EntryExpression expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(ExistsExpression expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(FromClause expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(FunctionExpression expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(GroupByClause expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(HavingClause expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(IdentificationVariable expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(IdentificationVariableDeclaration expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(IndexExpression expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(InExpression expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(InputParameter expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(Join expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(JPQLExpression expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(KeyExpression expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(KeywordExpression expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(LengthExpression expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(LikeExpression expression) {
    }

        /**
         * {@inheritDoc}
         */
        @Override
        public void visit(LocateExpression expression) {
        }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(LowerExpression expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(MaxFunction expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(MinFunction expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(ModExpression expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(MultiplicationExpression expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(NotExpression expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(NullComparisonExpression expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(NullExpression expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(NullIfExpression expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(NumericLiteral expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(ObjectExpression expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(OnClause expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(OrderByClause expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(OrderByItem expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(OrExpression expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(RangeVariableDeclaration expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(ResultVariable expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(SelectClause expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(SelectStatement expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(SimpleFromClause expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(SimpleSelectClause expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(SimpleSelectStatement expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(SizeExpression expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(SqrtExpression expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(StateFieldPathExpression expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(StringLiteral expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(SubExpression expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(SubstringExpression expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(SubtractionExpression expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(SumFunction expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(TreatExpression expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(TrimExpression expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(TypeExpression expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(UnknownExpression expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(UpdateClause expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(UpdateItem expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(UpdateStatement expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(UpperExpression expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(ValueExpression expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(WhenClause expression) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(WhereClause expression) {
    }
}
