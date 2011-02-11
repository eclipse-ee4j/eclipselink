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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import org.eclipse.persistence.utils.jpa.query.parser.AbstractExpressionVisitor;
import org.eclipse.persistence.utils.jpa.query.parser.CollectionExpression;
import org.eclipse.persistence.utils.jpa.query.parser.Expression;

/**
 * This visitor is used to visit each of the func items, which should be in
 * a {@link CollectionExpression}. If there is no {@link CollectionExpression},
 * then the expression is not complete and the return type will be Object.
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
final class FuncVisitor extends AbstractExpressionVisitor
{
	private Collection<TypeResolver> resolvers;
	private TypeVisitor typeVisitor;

	/**
	 * Creates a new <code>FuncVisitor</code>.
	 *
	 * @param typeVisitor
	 */
	FuncVisitor(TypeVisitor typeVisitor)
	{
		super();

		this.typeVisitor = typeVisitor;
		this.resolvers   = new ArrayList<TypeResolver>();
	}

	/**
	 * Returns
	 *
	 * @return
	 */
	Collection<TypeResolver> resolvers()
	{
		return resolvers;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CollectionExpression expression)
	{
		boolean firstChildIgnored = false;

		for (Iterator<Expression> children = expression.children(); children.hasNext(); )
		{
			Expression child = children.next();

			// Skip the function name
			if (!firstChildIgnored)
			{
				firstChildIgnored = true;
			}
			// Traverse the child in order to create the TypeResolver
			else
			{
				child.accept(typeVisitor);
				resolvers.add(typeVisitor.getResolver());
			}
		}
	}
}