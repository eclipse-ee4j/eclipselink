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
import org.eclipse.persistence.expressions.ExpressionBuilder;
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
	Expression buildQueryExpression() {

		// Retrieve the superquery identification variable from the derived path
		int index = rootPath.indexOf('.');
		String superqueryVariableName = rootPath.substring(0, index).toUpperCase().intern();

		// Create the local ExpressionBuilder for the super identification variable
		Expression expression = queryContext.getParent().findQueryExpressionImp(superqueryVariableName);
		expression = new ExpressionBuilder(expression.getBuilder().getQueryClass());

		// Cache the info
		queryContext.addUsedIdentificationVariable(superqueryVariableName);
		queryContext.addQueryExpression(superqueryVariableName, expression);

		// Now create the base Expression for the actual derived path
		return queryContext.buildExpression(baseExpression);
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
		return queryContext.resolveDescriptor(getBaseExpression().getAbstractSchemaName());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	DatabaseMapping resolveMapping() {
		return queryContext.resolveMapping(getBaseExpression().getAbstractSchemaName());
	}
}