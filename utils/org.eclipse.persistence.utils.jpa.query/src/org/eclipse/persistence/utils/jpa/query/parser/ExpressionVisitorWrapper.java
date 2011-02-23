/*******************************************************************************
 * Copyright (c) 2006, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.utils.jpa.query.parser;

/**
 * This {@link ExpressionVisitor} wraps another {@link ExpressionVisitor} and delegates all its
 * calls to it (the delegate).
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public abstract class ExpressionVisitorWrapper implements ExpressionVisitor {

	/**
	 * The {@link ExpressionVisitor} that will have the calls delegated from this one.
	 */
	private final ExpressionVisitor delegate;

	/**
	 * Creates a new <code>ExpressionVisitorWrapper</code>.
	 */
	@SuppressWarnings("unused")
	private ExpressionVisitorWrapper() {
		this(null);
	}

	/**
	 * Creates a new <code>ExpressionVisitorWrapper</code>.
	 *
	 * @param delegate The {@link ExpressionVisitor} that will have the calls delegated from this one
	 */
	protected ExpressionVisitorWrapper(ExpressionVisitor delegate) {
		super();

		if (delegate == null) {
			throw new NullPointerException("The delegate ExpressionVisitor cannot be null");
		}

		this.delegate = delegate;
	}

	/**
	 * Returns the delegate {@link ExpressionVisitor} that is receiving all the calls from this one.
	 *
	 * @return The delegate {@link ExpressionVisitor}
	 */
	protected ExpressionVisitor getDelegate() {
		return delegate;
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(AbsExpression expression) {
		delegate.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(AbstractSchemaName expression) {
		delegate.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(AdditionExpression expression) {
		delegate.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(AllOrAnyExpression expression) {
		delegate.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(AndExpression expression) {
		delegate.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(ArithmeticFactor expression) {
		delegate.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(AvgFunction expression) {
		delegate.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(BadExpression expression) {
		delegate.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(BetweenExpression expression) {
		delegate.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(CaseExpression expression) {
		delegate.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(CoalesceExpression expression) {
		delegate.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(CollectionExpression expression) {
		delegate.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(CollectionMemberDeclaration expression) {
		delegate.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(CollectionMemberExpression expression) {
		delegate.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(CollectionValuedPathExpression expression) {
		delegate.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(ComparisonExpression expression) {
		delegate.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(ConcatExpression expression) {
		delegate.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(ConstructorExpression expression) {
		delegate.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(CountFunction expression) {
		delegate.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(DateTime expression) {
		delegate.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(DeleteClause expression) {
		delegate.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(DeleteStatement expression) {
		delegate.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(DivisionExpression expression) {
		delegate.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(EmptyCollectionComparisonExpression expression) {
		delegate.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(EntityTypeLiteral expression) {
		delegate.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(EntryExpression expression) {
		delegate.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(ExistsExpression expression) {
		delegate.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(FromClause expression) {
		delegate.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(FuncExpression expression) {
		delegate.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(GroupByClause expression) {
		delegate.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(HavingClause expression) {
		delegate.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(IdentificationVariable expression) {
		delegate.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(IdentificationVariableDeclaration expression) {
		delegate.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(IndexExpression expression) {
		delegate.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(InExpression expression) {
		delegate.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(InputParameter expression) {
		delegate.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(Join expression) {
		delegate.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(JoinFetch expression) {
		delegate.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(JPQLExpression expression) {
		delegate.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(KeyExpression expression) {
		delegate.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(KeywordExpression expression) {
		delegate.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(LengthExpression expression) {
		delegate.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(LikeExpression expression) {
		delegate.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(LocateExpression expression) {
		delegate.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(LowerExpression expression) {
		delegate.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(MaxFunction expression) {
		delegate.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(MinFunction expression) {
		delegate.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(ModExpression expression) {
		delegate.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(MultiplicationExpression expression) {
		delegate.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(NotExpression expression) {
		delegate.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(NullComparisonExpression expression) {
		delegate.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(NullExpression expression) {
		delegate.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(NullIfExpression expression) {
		delegate.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(NumericLiteral expression) {
		delegate.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(ObjectExpression expression) {
		delegate.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(OrderByClause expression) {
		delegate.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(OrderByItem expression) {
		delegate.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(OrExpression expression) {
		delegate.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(RangeVariableDeclaration expression) {
		delegate.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(ResultVariable expression) {
		delegate.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SelectClause expression) {
		delegate.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SelectStatement expression) {
		delegate.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SimpleFromClause expression) {
		delegate.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SimpleSelectClause expression) {
		delegate.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SimpleSelectStatement expression) {
		delegate.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SizeExpression expression) {
		delegate.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SqrtExpression expression) {
		delegate.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(StateFieldPathExpression expression) {
		delegate.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(StringLiteral expression) {
		delegate.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SubExpression expression) {
		delegate.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SubtractionExpression expression) {
		delegate.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SubstringExpression expression) {
		delegate.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(SumFunction expression) {
		delegate.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(TreatExpression expression) {
		delegate.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(TrimExpression expression) {
		delegate.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(TypeExpression expression) {
		delegate.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(UnknownExpression expression) {
		delegate.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(UpdateClause expression) {
		delegate.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(UpdateItem expression) {
		delegate.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(UpdateStatement expression) {
		delegate.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(UpperExpression expression) {
		delegate.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(ValueExpression expression) {
		delegate.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(WhenClause expression) {
		delegate.visit(expression);
	}

	/**
	 * {@inheritDoc}
	 */
	public void visit(WhereClause expression) {
		delegate.visit(expression);
	}
}