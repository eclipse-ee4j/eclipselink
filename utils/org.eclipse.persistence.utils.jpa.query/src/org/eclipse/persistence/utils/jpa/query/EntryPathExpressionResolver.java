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
package org.eclipse.persistence.utils.jpa.query;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.internal.expressions.MapEntryExpression;

/**
 * The {@link PathExpressionResolver} is responsible to retrieve the {@link Expression} for an
 * a {@link java.util.Map.Entry Map.Entry}.
 *
 * @version 2.3
 * @since 1.3
 * @author Pascal Filion
 */
final class EntryPathExpressionResolver extends AbstractPathExpressionResolver {

	/**
	 * Creates a new <code>EntryPathExpressionResolver</code>.
	 *
	 * @param parent The parent of this one
	 */
	EntryPathExpressionResolver(PathExpressionResolver parent) {
		super(parent);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AbstractPathExpressionResolver clone() {
		return new EntryPathExpressionResolver(getParent().clone());
	}

	/**
	 * {@inheritDoc}
	 */
	public Expression getExpression() {
		MapEntryExpression expression = new MapEntryExpression(getParentExpression());
		expression.returnMapEntry();
		return expression;
	}

	/**
	 * {@inheritDoc}
	 */
	public Expression getExpression(String variableName) {
		return getParent().getExpression(variableName);
	}
}