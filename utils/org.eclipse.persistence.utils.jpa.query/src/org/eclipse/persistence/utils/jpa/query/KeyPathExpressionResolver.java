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
 * The {@link PathExpressionResolver} is responsible to retrieve the {@link Expression} for the
 * key of a {@link java.util.Map Map}.
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 * @author John Bracken
 */
final class KeyPathExpressionResolver extends AbstractPathExpressionResolver {

	/**
	 * Creates a new <code>KeyPathExpressionResolver</code>.
	 *
	 * @param parent The parent resolver responsible for the parent path of the given path
	 */
	KeyPathExpressionResolver(PathExpressionResolver parent) {
		super(parent);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public KeyPathExpressionResolver clone() {
		return new KeyPathExpressionResolver(getParent().clone());
	}

	/**
	 * {@inheritDoc}
	 */
	public Expression getExpression() {
		return new MapEntryExpression(getParentExpression());
	}

	/**
	 * {@inheritDoc}
	 */
	public Expression getExpression(String variableName) {
		return getParent().getExpression(variableName);
	}
}