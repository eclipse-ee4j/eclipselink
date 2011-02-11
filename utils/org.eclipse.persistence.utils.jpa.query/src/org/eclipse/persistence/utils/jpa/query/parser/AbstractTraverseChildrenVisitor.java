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

import java.util.Iterator;

/**
 * This {@link ExpressionVisitor} traverses the entire hierarchy of the JPQL
 * parsed tree by going down into each of the children of any given
 * {@link Expression}. It is up to the subclass to complete the behavior.
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
public abstract class AbstractTraverseChildrenVisitor implements ExpressionVisitor
{
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(AbsExpression expression)
	{
		visitExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(AbstractSchemaName expression)
	{
		visitExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(AdditionExpression expression)
	{
		visitExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(AllOrAnyExpression expression)
	{
		visitExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(AndExpression expression)
	{
		visitExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ArithmeticFactor expression)
	{
		visitExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(AvgFunction expression)
	{
		visitExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(BadExpression expression)
	{
		visitExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(BetweenExpression expression)
	{
		visitExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CaseExpression expression)
	{
		visitExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CoalesceExpression expression)
	{
		visitExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CollectionExpression expression)
	{
		visitExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CollectionMemberDeclaration expression)
	{
		visitExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CollectionMemberExpression expression)
	{
		visitExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CollectionValuedPathExpression expression)
	{
		visitExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ComparisonExpression expression)
	{
		visitExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ConcatExpression expression)
	{
		visitExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ConstructorExpression expression)
	{
		visitExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CountFunction expression)
	{
		visitExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(DateTime expression)
	{
		visitExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(DeleteClause expression)
	{
		visitExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(DeleteStatement expression)
	{
		visitExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(DivisionExpression expression)
	{
		visitExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(EmptyCollectionComparisonExpression expression)
	{
		visitExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(EntityTypeLiteral expression)
	{
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(EntryExpression expression)
	{
		visitExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ExistsExpression expression)
	{
		visitExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(FromClause expression)
	{
		visitExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(FuncExpression expression)
	{
		visitExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(GroupByClause expression)
	{
		visitExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(HavingClause expression)
	{
		visitExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(IdentificationVariable expression)
	{
		visitExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(IdentificationVariableDeclaration expression)
	{
		visitExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(IndexExpression expression)
	{
		visitExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(InExpression expression)
	{
		visitExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(InputParameter expression)
	{
		visitExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(Join expression)
	{
		visitExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(JoinFetch expression)
	{
		visitExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(JPQLExpression expression)
	{
		visitExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(KeyExpression expression)
	{
		visitExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(KeywordExpression expression)
	{
		visitExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(LengthExpression expression)
	{
		visitExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(LikeExpression expression)
	{
		visitExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(LocateExpression expression)
	{
		visitExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(LowerExpression expression)
	{
		visitExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(MaxFunction expression)
	{
		visitExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(MinFunction expression)
	{
		visitExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ModExpression expression)
	{
		visitExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(MultiplicationExpression expression)
	{
		visitExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(NotExpression expression)
	{
		visitExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(NullComparisonExpression expression)
	{
		visitExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(NullExpression expression)
	{
		visitExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(NullIfExpression expression)
	{
		visitExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(NumericLiteral expression)
	{
		visitExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ObjectExpression expression)
	{
		visitExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(OrderByClause expression)
	{
		visitExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(OrderByItem expression)
	{
		visitExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(OrExpression expression)
	{
		visitExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(RangeVariableDeclaration expression)
	{
		visitExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ResultVariable expression)
	{
		visitExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SelectClause expression)
	{
		visitExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SelectStatement expression)
	{
		visitExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SimpleFromClause expression)
	{
		visitExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SimpleSelectClause expression)
	{
		visitExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SimpleSelectStatement expression)
	{
		visitExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SizeExpression expression)
	{
		visitExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SqrtExpression expression)
	{
		visitExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(StateFieldPathExpression expression)
	{
		visitExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(StringLiteral expression)
	{
		visitExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SubExpression expression)
	{
		visitExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SubstractionExpression expression)
	{
		visitExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SubstringExpression expression)
	{
		visitExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SumFunction expression)
	{
		visitExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(TrimExpression expression)
	{
		visitExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(TypeExpression expression)
	{
		visitExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(UnknownExpression expression)
	{
		visitExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(UpdateClause expression)
	{
		visitExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(UpdateItem expression)
	{
		visitExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(UpdateStatement expression)
	{
		visitExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(UpperExpression expression)
	{
		visitExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ValueExpression expression)
	{
		visitExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(WhenClause expression)
	{
		visitExpression(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(WhereClause expression)
	{
		visitExpression(expression);
	}

	/**
	 * Visits the children of the given {@link Expression}.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	protected final void visitExpression(Expression expression)
	{
		for (Iterator<Expression> iter = expression.children(); iter.hasNext(); )
		{
			iter.next().accept(this);
		}
	}
}