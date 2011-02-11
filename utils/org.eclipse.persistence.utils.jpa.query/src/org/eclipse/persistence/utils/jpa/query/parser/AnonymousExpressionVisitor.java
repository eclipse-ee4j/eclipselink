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
 * This visitor allows a subclass to simply override {@link #visit(Expression)} and perform the
 * same task for all visited {@link Expression expressions}.
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
public abstract class AnonymousExpressionVisitor implements ExpressionVisitor
{
	/**
	 * Creates a new <code>AnonymousExpressionVisitor</code>.
	 */
	protected AnonymousExpressionVisitor()
	{
		super();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(AbsExpression expression)
	{
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(AbstractSchemaName expression)
	{
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(AdditionExpression expression)
	{
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(AllOrAnyExpression expression)
	{
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(AndExpression expression)
	{
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ArithmeticFactor expression)
	{
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(AvgFunction expression)
	{
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(BadExpression expression)
	{
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(BetweenExpression expression)
	{
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CaseExpression expression)
	{
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CoalesceExpression expression)
	{
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CollectionExpression expression)
	{
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CollectionMemberDeclaration expression)
	{
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CollectionMemberExpression expression)
	{
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CollectionValuedPathExpression expression)
	{
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ComparisonExpression expression)
	{
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ConcatExpression expression)
	{
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ConstructorExpression expression)
	{
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CountFunction expression)
	{
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(DateTime expression)
	{
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(DeleteClause expression)
	{
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(DeleteStatement expression)
	{
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(DivisionExpression expression)
	{
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(EmptyCollectionComparisonExpression expression)
	{
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(EntityTypeLiteral expression)
	{
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(EntryExpression expression)
	{
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ExistsExpression expression)
	{
		visit((Expression) expression);
	}

	/**
	 * Blindly visit the given {@link Expression}.
	 *
	 * @param expression The {@link Expression} to visit
	 */
	protected void visit(Expression expression)
	{
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(FromClause expression)
	{
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(FuncExpression expression)
	{
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(GroupByClause expression)
	{
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(HavingClause expression)
	{
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(IdentificationVariable expression)
	{
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(IdentificationVariableDeclaration expression)
	{
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(IndexExpression expression)
	{
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(InExpression expression)
	{
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(InputParameter expression)
	{
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(Join expression)
	{
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(JoinFetch expression)
	{
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(JPQLExpression expression)
	{
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(KeyExpression expression)
	{
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(KeywordExpression expression)
	{
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(LengthExpression expression)
	{
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(LikeExpression expression)
	{
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(LocateExpression expression)
	{
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(LowerExpression expression)
	{
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(MaxFunction expression)
	{
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(MinFunction expression)
	{
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ModExpression expression)
	{
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(MultiplicationExpression expression)
	{
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(NotExpression expression)
	{
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(NullComparisonExpression expression)
	{
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(NullExpression expression)
	{
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(NullIfExpression expression)
	{
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(NumericLiteral expression)
	{
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ObjectExpression expression)
	{
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(OrderByClause expression)
	{
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(OrderByItem expression)
	{
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(OrExpression expression)
	{
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(RangeVariableDeclaration expression)
	{
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ResultVariable expression)
	{
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SelectClause expression)
	{
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SelectStatement expression)
	{
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SimpleFromClause expression)
	{
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SimpleSelectClause expression)
	{
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SimpleSelectStatement expression)
	{
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SizeExpression expression)
	{
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SqrtExpression expression)
	{
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(StateFieldPathExpression expression)
	{
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(StringLiteral expression)
	{
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SubExpression expression)
	{
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SubstractionExpression expression)
	{
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SubstringExpression expression)
	{
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SumFunction expression)
	{
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(TrimExpression expression)
	{
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(TypeExpression expression)
	{
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(UnknownExpression expression)
	{
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(UpdateClause expression)
	{
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(UpdateItem expression)
	{
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(UpdateStatement expression)
	{
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(UpperExpression expression)
	{
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ValueExpression expression)
	{
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(WhenClause expression)
	{
		visit((Expression) expression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(WhereClause expression)
	{
		visit((Expression) expression);
	}
}