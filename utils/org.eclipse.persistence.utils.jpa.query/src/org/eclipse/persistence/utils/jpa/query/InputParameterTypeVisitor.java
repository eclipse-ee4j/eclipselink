/*******************************************************************************
 * Copyright (c) 2006, 2011 Oracle. All rights reserved.
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

import org.eclipse.persistence.utils.jpa.query.parser.DeleteStatement;
import org.eclipse.persistence.utils.jpa.query.parser.InputParameter;
import org.eclipse.persistence.utils.jpa.query.parser.LikeExpression;
import org.eclipse.persistence.utils.jpa.query.parser.SubstringExpression;
import org.eclipse.persistence.utils.jpa.query.parser.UpdateStatement;
import org.eclipse.persistence.utils.jpa.query.spi.IQuery;

/**
 * This resolver tries to guess the type if an input parameter by attempting to
 * find the closest type.
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
final class InputParameterTypeVisitor extends TypeVisitor
{
	/**
	 * The input parameter for which its type needs to be calculated.
	 */
	private final InputParameter inputParameter;

	/**
	 * Creates a new <code>InternalTypeVisitor</code>.
	 *
	 * @param query The model object representing the JPA named query
	 * @param inputParameter The input parameter for which its type needs to be
	 * calculated
	 */
	InputParameterTypeVisitor(IQuery query, InputParameter inputParameter)
	{
		super(query);
		this.inputParameter = inputParameter;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(DeleteStatement expression)
	{
		expression.getDeleteClause().accept(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(LikeExpression expression)
	{
		setResolver(buildClassTypeResolver(String.class));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(SubstringExpression expression)
	{
		// The first or second arithmetic expression is always an integer
		if (expression.getSecondExpression().isAncestor(inputParameter) ||
		    expression.getThirdExpression() .isAncestor(inputParameter))
		{
			setResolver(buildClassTypeResolver(Integer.class));
		}
		// The string primary is always a string
		else if (expression.getFirstExpression().isAncestor(inputParameter))
		{
			setResolver(buildClassTypeResolver(String.class));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(UpdateStatement expression)
	{
		expression.getUpdateClause().accept(this);
	}
}