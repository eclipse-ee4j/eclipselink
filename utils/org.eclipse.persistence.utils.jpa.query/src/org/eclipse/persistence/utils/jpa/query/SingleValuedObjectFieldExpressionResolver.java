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
 * This {@link PathExpressionResolver} is responsible to create the {@link Expression} for a
 * single valued-object field, which is the association field name in a one-to-one or many-to-one
 * relationship
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 * @author John Bracken
 */
final class SingleValuedObjectFieldExpressionResolver extends AbstractPathPathExpressionResolver {

	/**
	 * Creates a new <code>SingleValuedObjectFieldExpressionResolver</code>.
	 *
	 * @param parent The parent resolver responsible for the parent path of the given path
	 * @param associationFieldName The association field name in a one-to-one or many-to-one
	 * relationship
	 */
	SingleValuedObjectFieldExpressionResolver(PathExpressionResolver parent,
	                                          String associationFieldName) {

		super(parent, null, associationFieldName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SingleValuedObjectFieldExpressionResolver clone() {
		return new SingleValuedObjectFieldExpressionResolver(getParent().clone(), getPath());
	}

	/**
	 * {@inheritDoc}
	 */
	public Expression getExpression(String variableName) {
		return getParentExpression().get(variableName);
	}
}