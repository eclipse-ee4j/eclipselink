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
import org.eclipse.persistence.utils.jpa.query.spi.IMapping;

/**
 * This {@link PathExpressionResolver} is responsible to create the {@link Expression} for a
 * state field path.
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 * @author John Bracken
 */
final class StateFieldPathExpressionResolver extends AbstractPathPathExpressionResolver {

	/**
	 * Creates a new <code>StateFieldPathExpressionResolver</code>.
	 *
	 * @param parent The parent resolver responsible for the parent path of the given path
	 * @param mapping The external form of the mapping with the given property name
	 * @param propertyName The property name, which is the last path of the state field path expression
	 */
	StateFieldPathExpressionResolver(PathExpressionResolver parent,
	                                 IMapping mapping,
	                                 String propertyName) {

		super(parent, mapping, propertyName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public StateFieldPathExpressionResolver clone() {
		return new StateFieldPathExpressionResolver(getParent().clone(), getMapping(), getPath());
	}

	/**
	 * {@inheritDoc}
	 */
	public Expression getExpression(String variableName) {
		if (isNullAllowed() && MappingTypeHelper.isRelationshipMapping(getMapping())) {
			return getParentExpression().getAllowingNull(variableName);
		}
		return getParentExpression().get(variableName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isNullAllowed() {
		return super.isNullAllowed() || getParent().isNullAllowed();
	}
}