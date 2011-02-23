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

/**
 * The abstract definition of a {@link PathExpressionResolver}.
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
abstract class AbstractPathExpressionResolver implements PathExpressionResolver {

	/**
	 * This is only applicable to 1:1 relationships, and allows the target of the relationship to be
	 * <code>null</code> if there is no corresponding relationship in the database.
	 */
	private boolean nullAllowed;

	/**
	 * The parent resolver of this one.
	 */
	private final PathExpressionResolver parent;

	/**
	 * Creates a new <code>AbstractPathExpressionResolver</code>.
	 *
	 * @param parent The parent resolver of this one
	 */
	AbstractPathExpressionResolver(PathExpressionResolver parent) {
		super();
		this.parent = parent;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public abstract AbstractPathExpressionResolver clone();

	/**
	 * Returns the parent resolver of this one
	 *
	 * @return The parent {@link PathExpressionResolver}
	 */
	final PathExpressionResolver getParent() {
		return parent;
	}

	/**
	 * Returns the {@link Expression} from the parent path.
	 *
	 * @return The {@link Expression} from the parent path
	 */
	final Expression getParentExpression() {
		return parent.getExpression();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isNullAllowed() {
		return nullAllowed;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isNullAllowed(String variableName) {
		return getParent().isNullAllowed(variableName);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setNullAllowed(boolean nullAllowed) {
		this.nullAllowed = nullAllowed;
	}
}