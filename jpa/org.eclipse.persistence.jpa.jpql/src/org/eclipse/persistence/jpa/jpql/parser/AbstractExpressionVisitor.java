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
	public void visit(AbsExpression expression) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(AbstractSchemaName expression) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(AdditionExpression expression) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(AllOrAnyExpression expression) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(AndExpression expression) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(ArithmeticFactor expression) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(AvgFunction expression) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(BadExpression expression) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(BetweenExpression expression) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(CaseExpression expression) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(CoalesceExpression expression) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(CollectionExpression expression) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(CollectionMemberDeclaration expression) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(CollectionMemberExpression expression) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(CollectionValuedPathExpression expression) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(ComparisonExpression expression) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(ConcatExpression expression) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(ConstructorExpression expression) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(CountFunction expression) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(DateTime expression) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(DeleteClause expression) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(DeleteStatement expression) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(DivisionExpression expression) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(EmptyCollectionComparisonExpression expression) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(EntityTypeLiteral expression) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(EntryExpression expression) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(ExistsExpression expression) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(FromClause expression) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(FunctionExpression expression) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(GroupByClause expression) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(HavingClause expression) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(IdentificationVariable expression) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(IdentificationVariableDeclaration expression) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(IndexExpression expression) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(InExpression expression) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(InputParameter expression) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(Join expression) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(JPQLExpression expression) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(KeyExpression expression) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(KeywordExpression expression) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(LengthExpression expression) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(LikeExpression expression) {
	}

        /**
		 * {@inheritDoc}
		 */
		public void visit(LocateExpression expression) {
		}

	/**
	 * {@inheritDoc}
	 */
	public void visit(LowerExpression expression) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(MaxFunction expression) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(MinFunction expression) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(ModExpression expression) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(MultiplicationExpression expression) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(NotExpression expression) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(NullComparisonExpression expression) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(NullExpression expression) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(NullIfExpression expression) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(NumericLiteral expression) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(ObjectExpression expression) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(OnClause expression) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(OrderByClause expression) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(OrderByItem expression) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(OrExpression expression) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(RangeVariableDeclaration expression) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(ResultVariable expression) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SelectClause expression) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SelectStatement expression) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SimpleFromClause expression) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SimpleSelectClause expression) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SimpleSelectStatement expression) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SizeExpression expression) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SqrtExpression expression) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(StateFieldPathExpression expression) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(StringLiteral expression) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SubExpression expression) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SubstringExpression expression) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SubtractionExpression expression) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SumFunction expression) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(TreatExpression expression) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(TrimExpression expression) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(TypeExpression expression) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(UnknownExpression expression) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(UpdateClause expression) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(UpdateItem expression) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(UpdateStatement expression) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(UpperExpression expression) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(ValueExpression expression) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(WhenClause expression) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(WhereClause expression) {
	}
}