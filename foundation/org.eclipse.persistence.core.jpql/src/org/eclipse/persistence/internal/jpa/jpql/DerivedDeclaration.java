/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
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

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.mappings.DatabaseMapping;

/**
 * This <code>DerivedDeclaration</code> represents an identification variable declaration that was
 * declared in the <code><b>FROM</b></code> clause of a <code><b>SELECT</b></code> subquery. The
 * "root" object is not an entity name but a derived path expression.
 *
 * @see IdentificationVariableDeclaration
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
final class DerivedDeclaration extends AbstractRangeDeclaration {

	/**
	 * The identification variable of the derived path expression.
	 */
	private String derivedIdentificationVariableName;

	/**
	 * Creates a new <code>DerivedDeclaration</code>.
	 *
	 * @param queryContext The context used to query information about the application metadata and
	 * cached information
	 */
	DerivedDeclaration(JPQLQueryContext queryContext) {
		super(queryContext);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	Expression buildExpression() {
		return queryContext.buildExpression(getBaseExpression().getAbstractSchemaName());
	}

	/**
	 * Returns the identification variable used in the derived path expression that is defined in the
	 * superquery.
	 *
	 * @return The identification variable starting the derived path expression
	 */
	String getDerivedIdentificationVariableName() {
		if (derivedIdentificationVariableName == null) {
			int index = rootPath.indexOf('.');
			derivedIdentificationVariableName = rootPath.substring(0, index).toUpperCase().intern();
		}
		return derivedIdentificationVariableName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	boolean isDerived() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	boolean isRange() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	ClassDescriptor resolveDescriptor() {
		return getMapping().getReferenceDescriptor();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	DatabaseMapping resolveMapping() {
		return queryContext.resolveMapping(getBaseExpression().getAbstractSchemaName());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<?> resolveType() {
		return getMapping().getReferenceDescriptor().getJavaClass();
	}
}