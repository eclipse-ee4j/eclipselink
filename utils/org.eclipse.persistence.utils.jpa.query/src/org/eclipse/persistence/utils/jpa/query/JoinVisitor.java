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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.eclipse.persistence.utils.jpa.query.parser.AbstractExpressionVisitor;
import org.eclipse.persistence.utils.jpa.query.parser.CollectionValuedPathExpression;
import org.eclipse.persistence.utils.jpa.query.parser.Join;

/**
 * This visitor is used to retrieve the association path name from a {@link Join}
 * expression.
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
final class JoinVisitor extends AbstractExpressionVisitor
{
	/**
	 * The association path if it was discovered or an empty {@link Iterator}.
	 */
	private List<String> paths;

	/**
	 * Creates a new <code>JoinVisitor</code>.
	 */
	JoinVisitor()
	{
		super();
		this.paths = Collections.emptyList();
	}

	/**
	 * Returns the association path from {@link Join}.
	 *
	 * @return The association path if it was discovered or an empty {@link Iterator}
	 */
	List<String> paths()
	{
		return paths;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(CollectionValuedPathExpression expression)
	{
		paths = new ArrayList<String>();

		for (Iterator<String> iter = expression.paths(); iter.hasNext(); )
		{
			paths.add(iter.next());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void visit(Join expression)
	{
		expression.getJoinAssociationPath().accept(this);
	}
}