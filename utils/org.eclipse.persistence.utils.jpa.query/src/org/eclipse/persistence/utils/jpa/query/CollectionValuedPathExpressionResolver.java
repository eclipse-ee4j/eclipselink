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
 * collection-valued path expression, which in some cases can be a state field path expression
 * (which happens when parsing a <b>JOIN FETCH</b> expression).
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 * @author John Bracken
 */
final class CollectionValuedPathExpressionResolver extends AbstractPathPathExpressionResolver {

	/**
	 * Creates a new <code>CollectionValuedPathExpressionResolver</code>.
	 *
	 * @param parent The parent resolver responsible for the parent path of the given path
	 * @param mapping The external form of the mapping with the given collection-field name
	 * @param collectionFieldName The property name, which is the last path of the collection-valued
	 * path expression
	 */
	CollectionValuedPathExpressionResolver(PathExpressionResolver parent,
	                                       IMapping mapping,
	                                       String collectionFieldName) {

		super(parent, mapping, collectionFieldName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CollectionValuedPathExpressionResolver clone() {
		return new CollectionValuedPathExpressionResolver(
			getParent().clone(),
			getMapping(),
			getPath()
		);
	}

	/**
	 * {@inheritDoc}
	 */
	public Expression getExpression(String variableName) {

		if (MappingTypeHelper.isCollectionMapping(getMapping())) {
			if (isNullAllowed()) {
				return getParentExpression().anyOfAllowingNone(variableName);
			}
			return getParentExpression().anyOf(variableName);
		}
		else {
			if (isNullAllowed()) {
				return getParentExpression().getAllowingNull(variableName);
			}
			return getParentExpression().get(variableName);
		}
	}
}