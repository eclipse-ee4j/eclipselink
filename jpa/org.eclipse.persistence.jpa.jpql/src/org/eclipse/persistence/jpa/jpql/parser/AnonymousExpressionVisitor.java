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
 * This visitor allows a subclass to simply override {@link #visit(Expression)} and perform the
 * same task for all visited {@link Expression expressions}.
 *
 * @version 2.4
 * @since 2.3
 * @author Pascal Filion
 */
public abstract class AnonymousExpressionVisitor implements ExpressionVisitor {

	/**
	 * {@inheritDoc}
	 */
	public void visit(AbsExpression expression) {
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(AbstractSchemaName expression) {
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(AdditionExpression expression) {
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(AllOrAnyExpression expression) {
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(AndExpression expression) {
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(ArithmeticFactor expression) {
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(AvgFunction expression) {
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(BadExpression expression) {
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(BetweenExpression expression) {
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(CaseExpression expression) {
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(CoalesceExpression expression) {
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(CollectionExpression expression) {
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(CollectionMemberDeclaration expression) {
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(CollectionMemberExpression expression) {
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(CollectionValuedPathExpression expression) {
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(ComparisonExpression expression) {
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(ConcatExpression expression) {
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(ConstructorExpression expression) {
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(CountFunction expression) {
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(DateTime expression) {
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(DeleteClause expression) {
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(DeleteStatement expression) {
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(DivisionExpression expression) {
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(EmptyCollectionComparisonExpression expression) {
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(EntityTypeLiteral expression) {
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(EntryExpression expression) {
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(ExistsExpression expression) {
		visit((Expression) expression);
	}

	/**
	 * Blindly visit the given {@link Expression}.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	protected void visit(Expression expression) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(FromClause expression) {
		visit((Expression) expression);
	}

        /**
         * {@inheritDoc}
         */
        public void visit(FunctionExpression expression) {
            visit((Expression) expression);
        }

	/**
	 * {@inheritDoc}
	 */
	public void visit(GroupByClause expression) {
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(HavingClause expression) {
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(IdentificationVariable expression) {
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(IdentificationVariableDeclaration expression) {
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(IndexExpression expression) {
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(InExpression expression) {
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(InputParameter expression) {
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(Join expression) {
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(JPQLExpression expression) {
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(KeyExpression expression) {
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(KeywordExpression expression) {
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(LengthExpression expression) {
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(LikeExpression expression) {
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(LocateExpression expression) {
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(LowerExpression expression) {
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(MaxFunction expression) {
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(MinFunction expression) {
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(ModExpression expression) {
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(MultiplicationExpression expression) {
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(NotExpression expression) {
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(NullComparisonExpression expression) {
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(NullExpression expression) {
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(NullIfExpression expression) {
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(NumericLiteral expression) {
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(ObjectExpression expression) {
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(OrderByClause expression) {
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(OrderByItem expression) {
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(OrExpression expression) {
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(RangeVariableDeclaration expression) {
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(ResultVariable expression) {
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SelectClause expression) {
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SelectStatement expression) {
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SimpleFromClause expression) {
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SimpleSelectClause expression) {
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SimpleSelectStatement expression) {
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SizeExpression expression) {
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SqrtExpression expression) {
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(StateFieldPathExpression expression) {
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(StringLiteral expression) {
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SubExpression expression) {
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SubtractionExpression expression) {
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SubstringExpression expression) {
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SumFunction expression) {
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(TrimExpression expression) {
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(TypeExpression expression) {
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(UnknownExpression expression) {
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(UpdateClause expression) {
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(UpdateItem expression) {
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(UpdateStatement expression) {
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(UpperExpression expression) {
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(ValueExpression expression) {
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(WhenClause expression) {
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(WhereClause expression) {
		visit((Expression) expression);
	}

        /**
         * {@inheritDoc}
         */
        public void visit(OnClause expression) {
                visit((Expression) expression);
        }
}