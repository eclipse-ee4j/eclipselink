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
package org.eclipse.persistence.utils.jpa.query;

import org.eclipse.persistence.utils.jpa.query.parser.AllOrAnyExpression;
import org.eclipse.persistence.utils.jpa.query.parser.AndExpression;
import org.eclipse.persistence.utils.jpa.query.parser.BetweenExpression;
import org.eclipse.persistence.utils.jpa.query.parser.CollectionValuedPathExpression;
import org.eclipse.persistence.utils.jpa.query.parser.ComparisonExpression;
import org.eclipse.persistence.utils.jpa.query.parser.EmptyCollectionComparisonExpression;
import org.eclipse.persistence.utils.jpa.query.parser.LikeExpression;
import org.eclipse.persistence.utils.jpa.query.parser.OrExpression;
import org.eclipse.persistence.utils.jpa.query.parser.SelectStatement;
import org.eclipse.persistence.utils.jpa.query.parser.SimpleSelectClause;
import org.eclipse.persistence.utils.jpa.query.parser.SimpleSelectStatement;
import org.eclipse.persistence.utils.jpa.query.spi.IQuery;

/**
 * The default implementation of {@link TypeVisitor}, which adds more visits that would not
 * necessarily be ok to add to the {@link TypeVisitor} because the subclass used to calculate
 * the result type of a select clause would not have those type of expressions.
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
final class DefaultTypeVisitor extends TypeVisitor
{
	/**
	 * Creates a new <code>DefaultTypeVisitor</code>.
	 *
	 * @param query The model object representing the JPA named query
	 */
	DefaultTypeVisitor(IQuery query)
	{
		super(query);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(AllOrAnyExpression expression)
	{
		expression.getExpression().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(AndExpression expression)
	{
		setResolver(buildClassTypeResolver(Boolean.class));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(BetweenExpression expression)
	{
		setResolver(buildClassTypeResolver(Boolean.class));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CollectionValuedPathExpression expression)
	{
		// Resolve the FROM clause so abstract schema names are resolvable
		TypeResolver visitor = buildDeclarationVisitor(expression);

		// Visit the collection-valued path expression so the resolver tree can be created
		PathDeclarationVisitor pathVisitor = new PathDeclarationVisitor(visitor);
		expression.accept(pathVisitor);

		setResolver(pathVisitor.resolver());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(ComparisonExpression expression)
	{
		setResolver(buildClassTypeResolver(Boolean.class));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(EmptyCollectionComparisonExpression expression)
	{
		setResolver(buildClassTypeResolver(Boolean.class));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(LikeExpression expression)
	{
		setResolver(buildClassTypeResolver(Boolean.class));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(OrExpression expression)
	{
		setResolver(buildClassTypeResolver(Boolean.class));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SelectStatement expression)
	{
		expression.getSelectClause().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SimpleSelectClause expression)
	{
		expression.getSelectExpression().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SimpleSelectStatement expression)
	{
		expression.getSelectClause().accept(this);
	}
}