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
//     04/21/2022: Tomas Kraus
//       - Issue 1474: Update JPQL Grammar for Jakarta Persistence 2.2, 3.0 and 3.1
//       - Issue 317: Implement LOCAL DATE, LOCAL TIME and LOCAL DATETIME.
//     06/02/2023: Radek Felcman
//       - Issue 1885: Implement new JPQLGrammar for upcoming Jakarta Persistence 3.2
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
 * @author Pascal Filion
 */
public abstract class AbstractExpressionVisitor implements ExpressionVisitor {

    /**
     * Default constructor.
     */
    protected AbstractExpressionVisitor() {
    }

    @Override
    public void visit(AbsExpression expression) {
    }

    @Override
    public void visit(AbstractSchemaName expression) {
    }

    @Override
    public void visit(AdditionExpression expression) {
    }

    @Override
    public void visit(AllOrAnyExpression expression) {
    }

    @Override
    public void visit(AndExpression expression) {
    }

    @Override
    public void visit(ArithmeticFactor expression) {
    }

    @Override
    public void visit(AvgFunction expression) {
    }

    @Override
    public void visit(BadExpression expression) {
    }

    @Override
    public void visit(BetweenExpression expression) {
    }

    @Override
    public void visit(CaseExpression expression) {
    }

    @Override
    public void visit(CoalesceExpression expression) {
    }

    @Override
    public void visit(CollectionExpression expression) {
    }

    @Override
    public void visit(CollectionMemberDeclaration expression) {
    }

    @Override
    public void visit(CollectionMemberExpression expression) {
    }

    @Override
    public void visit(CollectionValuedPathExpression expression) {
    }

    @Override
    public void visit(ComparisonExpression expression) {
    }

    @Override
    public void visit(ConcatExpression expression) {
    }

    @Override
    public void visit(ConcatPipesExpression expression) {
    }

    @Override
    public void visit(ConstructorExpression expression) {
    }

    @Override
    public void visit(CountFunction expression) {
    }

    @Override
    public void visit(DateTime expression) {
    }

    @Override
    public void visit(DeleteClause expression) {
    }

    @Override
    public void visit(DeleteStatement expression) {
    }

    @Override
    public void visit(DivisionExpression expression) {
    }

    @Override
    public void visit(EmptyCollectionComparisonExpression expression) {
    }

    @Override
    public void visit(EntityTypeLiteral expression) {
    }

    @Override
    public void visit(EntryExpression expression) {
    }

    @Override
    public void visit(ExistsExpression expression) {
    }

    @Override
    public void visit(FromClause expression) {
    }

    @Override
    public void visit(FunctionExpression expression) {
    }

    @Override
    public void visit(GroupByClause expression) {
    }

    @Override
    public void visit(HavingClause expression) {
    }

    @Override
    public void visit(IdentificationVariable expression) {
    }

    @Override
    public void visit(IdentificationVariableDeclaration expression) {
    }

    @Override
    public void visit(IndexExpression expression) {
    }

    @Override
    public void visit(InExpression expression) {
    }

    @Override
    public void visit(InputParameter expression) {
    }

    @Override
    public void visit(Join expression) {
    }

    @Override
    public void visit(JPQLExpression expression) {
    }

    @Override
    public void visit(KeyExpression expression) {
    }

    @Override
    public void visit(KeywordExpression expression) {
    }

    @Override
    public void visit(LengthExpression expression) {
    }

    @Override
    public void visit(LocalExpression expression) {
    }

    @Override
    public void visit(LocalDateTime expression) {
    }

    @Override
    public void visit(LikeExpression expression) {
    }

    @Override
    public void visit(LocateExpression expression) {
    }

    @Override
    public void visit(LowerExpression expression) {
    }

    @Override
    public void visit(MathDoubleExpression.Power expression) {
    }

    @Override
    public void visit(MathDoubleExpression.Round expression) {
    }

    @Override
    public void visit(MathSingleExpression.Ceiling expression) {
    }

    @Override
    public void visit(MathSingleExpression.Exp expression) {
    }

    @Override
    public void visit(MathSingleExpression.Floor expression) {
    }

    @Override
    public void visit(MathSingleExpression.Ln expression) {
    }

    @Override
    public void visit(MathSingleExpression.Sign expression) {
    }

    @Override
    public void visit(MaxFunction expression) {
    }

    @Override
    public void visit(MinFunction expression) {
    }

    @Override
    public void visit(ModExpression expression) {
    }

    @Override
    public void visit(MultiplicationExpression expression) {
    }

    @Override
    public void visit(NotExpression expression) {
    }

    @Override
    public void visit(NullComparisonExpression expression) {
    }

    @Override
    public void visit(NullExpression expression) {
    }

    @Override
    public void visit(NullIfExpression expression) {
    }

    @Override
    public void visit(NumericLiteral expression) {
    }

    @Override
    public void visit(ObjectExpression expression) {
    }

    @Override
    public void visit(OnClause expression) {
    }

    @Override
    public void visit(OrderByClause expression) {
    }

    @Override
    public void visit(OrderByItem expression) {
    }

    @Override
    public void visit(OrExpression expression) {
    }

    @Override
    public void visit(RangeVariableDeclaration expression) {
    }

    @Override
    public void visit(ReplaceExpression expression) {
    }

    @Override
    public void visit(ResultVariable expression) {
    }

    @Override
    public void visit(SelectClause expression) {
    }

    @Override
    public void visit(SelectStatement expression) {
    }

    @Override
    public void visit(SimpleFromClause expression) {
    }

    @Override
    public void visit(SimpleSelectClause expression) {
    }

    @Override
    public void visit(SimpleSelectStatement expression) {
    }

    @Override
    public void visit(SizeExpression expression) {
    }

    @Override
    public void visit(SqrtExpression expression) {
    }

    @Override
    public void visit(StateFieldPathExpression expression) {
    }

    @Override
    public void visit(StringLiteral expression) {
    }

    @Override
    public void visit(SubExpression expression) {
    }

    @Override
    public void visit(SubstringExpression expression) {
    }

    @Override
    public void visit(SubtractionExpression expression) {
    }

    @Override
    public void visit(SumFunction expression) {
    }

    @Override
    public void visit(TreatExpression expression) {
    }

    @Override
    public void visit(TrimExpression expression) {
    }

    @Override
    public void visit(TypeExpression expression) {
    }

    @Override
    public void visit(UnknownExpression expression) {
    }

    @Override
    public void visit(UpdateClause expression) {
    }

    @Override
    public void visit(UpdateItem expression) {
    }

    @Override
    public void visit(UpdateStatement expression) {
    }

    @Override
    public void visit(UpperExpression expression) {
    }

    @Override
    public void visit(ValueExpression expression) {
    }

    @Override
    public void visit(WhenClause expression) {
    }

    @Override
    public void visit(WhereClause expression) {
    }
}
