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

/**
 * This {@link ExpressionVisitor} traverses up the hierarchy. It is up to the
 * subclass to complete the behavior.
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
public abstract class AbstractTraverseParentVisitor implements ExpressionVisitor
{
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(AbsExpression expression)
	{
		expression.getParent().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(AbstractSchemaName expression)
	{
		expression.getParent().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(AdditionExpression expression)
	{
		expression.getParent().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(AllOrAnyExpression expression)
	{
		expression.getParent().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(AndExpression expression)
	{
		expression.getParent().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ArithmeticFactor expression)
	{
		expression.getParent().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(AvgFunction expression)
	{
		expression.getParent().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(BadExpression expression)
	{
		expression.getParent().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(BetweenExpression expression)
	{
		expression.getParent().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CaseExpression expression)
	{
		expression.getParent().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CoalesceExpression expression)
	{
		expression.getParent().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CollectionExpression expression)
	{
		expression.getParent().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CollectionMemberDeclaration expression)
	{
		expression.getParent().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CollectionMemberExpression expression)
	{
		expression.getParent().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CollectionValuedPathExpression expression)
	{
		expression.getParent().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ComparisonExpression expression)
	{
		expression.getParent().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ConcatExpression expression)
	{
		expression.getParent().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ConstructorExpression expression)
	{
		expression.getParent().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CountFunction expression)
	{
		expression.getParent().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(DateTime expression)
	{
		expression.getParent().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(DeleteClause expression)
	{
		expression.getParent().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(DeleteStatement expression)
	{
		expression.getParent().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(DivisionExpression expression)
	{
		expression.getParent().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(EmptyCollectionComparisonExpression expression)
	{
		expression.getParent().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(EntityTypeLiteral expression)
	{
		expression.getParent().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(EntryExpression expression)
	{
		expression.getParent().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ExistsExpression expression)
	{
		expression.getParent().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(FromClause expression)
	{
		expression.getParent().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(FuncExpression expression)
	{
		expression.getParent().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(GroupByClause expression)
	{
		expression.getParent().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(HavingClause expression)
	{
		expression.getParent().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(IdentificationVariable expression)
	{
		expression.getParent().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(IdentificationVariableDeclaration expression)
	{
		expression.getParent().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(IndexExpression expression)
	{
		expression.getParent().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(InExpression expression)
	{
		expression.getParent().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(InputParameter expression)
	{
		expression.getParent().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(Join expression)
	{
		expression.getParent().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(JoinFetch expression)
	{
		expression.getParent().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(JPQLExpression expression)
	{
		// Nothing to traverse, this is the root of the parsed tree
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(KeyExpression expression)
	{
		expression.getParent().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(KeywordExpression expression)
	{
		expression.getParent().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(LengthExpression expression)
	{
		expression.getParent().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(LikeExpression expression)
	{
		expression.getParent().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(LocateExpression expression)
	{
		expression.getParent().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(LowerExpression expression)
	{
		expression.getParent().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(MaxFunction expression)
	{
		expression.getParent().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(MinFunction expression)
	{
		expression.getParent().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ModExpression expression)
	{
		expression.getParent().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(MultiplicationExpression expression)
	{
		expression.getParent().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(NotExpression expression)
	{
		expression.getParent().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(NullComparisonExpression expression)
	{
		expression.getParent().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(NullExpression expression)
	{
		expression.getParent().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(NullIfExpression expression)
	{
		expression.getParent().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(NumericLiteral expression)
	{
		expression.getParent().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ObjectExpression expression)
	{
		expression.getParent().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(OrderByClause expression)
	{
		expression.getParent().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(OrderByItem expression)
	{
		expression.getParent().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(OrExpression expression)
	{
		expression.getParent().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(RangeVariableDeclaration expression)
	{
		expression.getParent().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ResultVariable expression)
	{
		expression.getParent().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SelectClause expression)
	{
		expression.getParent().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SelectStatement expression)
	{
		expression.getParent().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SimpleFromClause expression)
	{
		expression.getParent().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SimpleSelectClause expression)
	{
		expression.getParent().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SimpleSelectStatement expression)
	{
		expression.getParent().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SizeExpression expression)
	{
		expression.getParent().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SqrtExpression expression)
	{
		expression.getParent().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(StateFieldPathExpression expression)
	{
		expression.getParent().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(StringLiteral expression)
	{
		expression.getParent().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SubExpression expression)
	{
		expression.getParent().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SubstractionExpression expression)
	{
		expression.getParent().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SubstringExpression expression)
	{
		expression.getParent().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SumFunction expression)
	{
		expression.getParent().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(TrimExpression expression)
	{
		expression.getParent().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(TypeExpression expression)
	{
		expression.getParent().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(UnknownExpression expression)
	{
		expression.getParent().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(UpdateClause expression)
	{
		expression.getParent().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(UpdateItem expression)
	{
		expression.getParent().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(UpdateStatement expression)
	{
		expression.getParent().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(UpperExpression expression)
	{
		expression.getParent().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ValueExpression expression)
	{
		expression.getParent().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(WhenClause expression)
	{
		expression.getParent().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(WhereClause expression)
	{
		expression.getParent().accept(this);
	}
}