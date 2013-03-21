/*******************************************************************************
 * Copyright (c) 2006, 2013 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.internal.jpa.jpql;

import org.eclipse.persistence.jpa.jpql.AbstractEclipseLinkParameterTypeVisitor;

/**
 * This visitor's responsibility is to find the type of an input parameter.
 *
 * @version 2.5
 * @since 2.3
 * @author Pascal Filion
 */
final class ParameterTypeVisitor extends AbstractEclipseLinkParameterTypeVisitor {

	/**
	 * The context used to query information about the query.
	 */
	private final JPQLQueryContext queryContext;

	/**
	 * Creates a new <code>ParameterTypeVisitor</code>.
	 *
	 * @param queryContext The context used to query information about the application metadata and
	 * cached information
	 */
	ParameterTypeVisitor(JPQLQueryContext queryContext) {
		super();
		this.queryContext = queryContext;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Class<?> getType() {

		// The type should be ignored, use the special constant
		if (ignoreType) {
			return Object.class;
		}

		// The type name was set
		if (typeName != null) {
			return queryContext.getType(typeName);
		}

		// The calculation couldn't find an expression with a type
		if (expression == null) {
			if (type == null) {
				type = Object.class;
			}
			return type;
		}

		return queryContext.getType(expression);
	}
}