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
package org.eclipse.persistence.jpa.jpql.parser;

/**
 * The interface is used to traverse the JPQL parsed tree. It follows the Visitor pattern.
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public interface ExpressionVisitor {

	/**
	 * Visits the {@link AbsExpression} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(AbsExpression expression);

	/**
	 * Visits the {@link AbstractSchemaName} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(AbstractSchemaName expression);

	/**
	 * Visits the {@link AdditionExpression} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(AdditionExpression expression);

	/**
	 * Visits the {@link AllOrAnyExpression} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(AllOrAnyExpression expression);

	/**
	 * Visits the {@link AndExpression} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(AndExpression expression);

	/**
	 * Visits the {@link ArithmeticFactor} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(ArithmeticFactor expression);

	/**
	 * Visits the {@link AvgFunction} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(AvgFunction expression);

	/**
	 * Visits the {@link BadExpression} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(BadExpression expression);

	/**
	 * Visits the {@link BetweenExpression} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(BetweenExpression expression);

	/**
	 * Visits the {@link CaseExpression} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(CaseExpression expression);

	/**
	 * Visits the {@link CoalesceExpression} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(CoalesceExpression expression);

	/**
	 * Visits the {@link CollectionExpression} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(CollectionExpression expression);

	/**
	 * Visits the {@link CollectionMemberDeclaration} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(CollectionMemberDeclaration expression);

	/**
	 * Visits the {@link CollectionMemberExpression} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(CollectionMemberExpression expression);

	/**
	 * Visits the {@link CollectionValuedPathExpression} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(CollectionValuedPathExpression expression);

	/**
	 * Visits the {@link ComparisonExpression} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(ComparisonExpression expression);

	/**
	 * Visits the {@link ConcatExpression} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(ConcatExpression expression);

	/**
	 * Visits the {@link ConstructorExpression} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(ConstructorExpression expression);

	/**
	 * Visits the {@link CountFunction} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(CountFunction expression);

	/**
	 * Visits the {@link DateTime} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(DateTime expression);

	/**
	 * Visits the {@link DeleteClause} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(DeleteClause expression);

	/**
	 * Visits the {@link DeleteStatement} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(DeleteStatement expression);

	/**
	 * Visits the {@link DivisionExpression} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(DivisionExpression expression);

	/**
	 * Visits the {@link EmptyCollectionComparisonExpression} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(EmptyCollectionComparisonExpression expression);

	/**
	 * Visits the {@link EntityTypeLiteral} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(EntityTypeLiteral expression);

	/**
	 * Visits the {@link EntryExpression} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(EntryExpression expression);

	/**
	 * Visits the {@link ExistsExpression} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(ExistsExpression expression);

        /**
         * Visits the {@link FunctionExpression} expression.
         *
         * @param expression The {@link Expression} to visit
         */
        void visit(FunctionExpression expression);

	/**
	 * Visits the {@link FromClause} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(FromClause expression);

	/**
	 * Visits the {@link GroupByClause} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(GroupByClause expression);

	/**
	 * Visits the {@link HavingClause} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(HavingClause expression);

        /**
         * Visits the {@link OnClause} expression.
         *
         * @param expression The {@link Expression} to visit
         */
        void visit(OnClause expression);

	/**
	 * Visits the {@link IdentificationVariable} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(IdentificationVariable expression);

	/**
	 * Visits the {@link IdentificationVariableDeclaration} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(IdentificationVariableDeclaration expression);

	/**
	 * Visits the {@link IndexExpression} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(IndexExpression expression);

	/**
	 * Visits the {@link InExpression} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(InExpression expression);

	/**
	 * Visits the {@link InputParameter} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(InputParameter expression);

	/**
	 * Visits the {@link Join} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(Join expression);

	/**
	 * Visits the {@link JPQLExpression} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(JPQLExpression expression);

	/**
	 * Visits the {@link KeyExpression} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(KeyExpression expression);

	/**
	 * Visits the {@link KeywordExpression} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(KeywordExpression expression);

	/**
	 * Visits the {@link LengthExpression} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(LengthExpression expression);

	/**
	 * Visits the {@link LikeExpression} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(LikeExpression expression);

	/**
	 * Visits the {@link LocateExpression} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(LocateExpression expression);

	/**
	 * Visits the {@link LowerExpression} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(LowerExpression expression);

	/**
	 * Visits the {@link MaxFunction} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(MaxFunction expression);

	/**
	 * Visits the {@link MinFunction} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(MinFunction expression);

	/**
	 * Visits the {@link ModExpression} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(ModExpression expression);

	/**
	 * Visits the {@link MultiplicationExpression} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(MultiplicationExpression expression);

	/**
	 * Visits the {@link NotExpression} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(NotExpression expression);

	/**
	 * Visits the {@link NullComparisonExpression} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(NullComparisonExpression expression);

	/**
	 * Visits the {@link NullExpression} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(NullExpression expression);

	/**
	 * Visits the {@link NullIfExpression} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(NullIfExpression expression);

	/**
	 * Visits the {@link NumericLiteral} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(NumericLiteral expression);

	/**
	 * Visits the {@link ObjectExpression} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(ObjectExpression expression);

	/**
	 * Visits the {@link OrderByClause} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(OrderByClause expression);

	/**
	 * Visits the {@link OrderByItem} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(OrderByItem expression);

	/**
	 * Visits the {@link OrExpression} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(OrExpression expression);

	/**
	 * Visits the {@link RangeVariableDeclaration} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(RangeVariableDeclaration expression);

	/**
	 * Visits the {@link ResultVariable} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(ResultVariable expression);

	/**
	 * Visits the {@link SelectClause} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(SelectClause expression);

	/**
	 * Visits the {@link SelectStatement} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(SelectStatement expression);

	/**
	 * Visits the {@link SimpleFromClause} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(SimpleFromClause expression);

	/**
	 * Visits the {@link SimpleSelectClause} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(SimpleSelectClause expression);

	/**
	 * Visits the {@link SimpleSelectStatement} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(SimpleSelectStatement expression);

	/**
	 * Visits the {@link SizeExpression} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(SizeExpression expression);

	/**
	 * Visits the {@link SqrtExpression} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(SqrtExpression expression);

	/**
	 * Visits the {@link StateFieldPathExpression} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(StateFieldPathExpression expression);

	/**
	 * Visits the {@link StringLiteral} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(StringLiteral expression);

	/**
	 * Visits the {@link SubExpression} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(SubExpression expression);

	/**
	 * Visits the {@link SubtractionExpression} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(SubtractionExpression expression);

	/**
	 * Visits the {@link SubstringExpression} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(SubstringExpression expression);

	/**
	 * Visits the {@link SumFunction} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(SumFunction expression);

	/**
	 * Visits the {@link TrimExpression} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(TrimExpression expression);

	/**
	 * Visits the {@link TypeExpression} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(TypeExpression expression);

	/**
	 * Visits the {@link UnknownExpression} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(UnknownExpression expression);

	/**
	 * Visits the {@link UpdateClause} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(UpdateClause expression);

	/**
	 * Visits the {@link UpdateItem} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(UpdateItem expression);

	/**
	 * Visits the {@link UpdateStatement} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(UpdateStatement expression);

	/**
	 * Visits the {@link UpperExpression} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(UpperExpression expression);

	/**
	 * Visits the {@link ValueExpression} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(ValueExpression expression);

	/**
	 * Visits the {@link WhenClause} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(WhenClause expression);

	/**
	 * Visits the {@link WhereClause} expression.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	void visit(WhereClause expression);
}