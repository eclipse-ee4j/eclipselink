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
 * This {@link PathExpressionResolver} resolving a path and casting it as another entity type.
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
final class TreatExpressionResolver extends AbstractPathExpressionResolver {

	/**
	 * The type used to cast the parent's {@link Expression}.
	 */
	private final Class<?> entityType;

	/**
	 * Creates a new <code>TreatExpressionResolver</code>.
	 *
	 * @param parent The parent resolver of this one
	 * @param entityType The type used to cast the parent's {@link Expression}
	 */
	TreatExpressionResolver(PathExpressionResolver parent, Class<?> entityType) {
		super(parent);
		this.entityType = entityType;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TreatExpressionResolver clone() {
		return new TreatExpressionResolver(getParent().clone(), entityType);
	}

	/**
	 * {@inheritDoc}
	 */
	public Expression getExpression() {
		return getParentExpression().as(entityType);
	}

	/**
	 * {@inheritDoc}
	 */
	public Expression getExpression(String variableName) {
		return getParentExpression().get(variableName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setNullAllowed(boolean nullAllowed) {

		super.setNullAllowed(nullAllowed);

		// Make sure the parent PathExpressionResolver is updated because that's where it actually
		// matters if null is allowed or not (to use anyOf() or anyOfAllowingNull())
		getParent().setNullAllowed(nullAllowed);
	}
}