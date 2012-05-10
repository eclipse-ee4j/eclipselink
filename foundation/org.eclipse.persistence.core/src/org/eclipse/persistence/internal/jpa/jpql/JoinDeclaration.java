/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
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

import java.util.Collections;
import java.util.List;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.jpa.jpql.parser.Join;
import org.eclipse.persistence.mappings.DatabaseMapping;

/**
 * A <code>JoinDeclaration</code> represents a single <code><b>JOIN</b></code> expression.
 *
 * @version 2.4
 * @since 2.4
 * @author Pascal Filion
 */
final class JoinDeclaration extends Declaration {

	/**
	 * Creates a new <code>JoinDeclaration</code>.
	 *
	 * @param queryContext The context used to query information about the application metadata and
	 * cached information
	 */
	JoinDeclaration(JPQLQueryContext queryContext) {
		super(queryContext);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	Expression buildQueryExpression() {
		return queryContext.buildExpression(baseExpression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Join getBaseExpression() {
		return (Join) super.getBaseExpression();
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Join> getJoins() {
		return Collections.emptyList();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean hasJoins() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isCollection() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isDerived() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isRange() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	ClassDescriptor resolveDescriptor() {
		return queryContext.resolveDescriptor(getBaseExpression().getJoinAssociationPath());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	DatabaseMapping resolveMapping() {
		return queryContext.resolveMapping(getBaseExpression().getJoinAssociationPath());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return baseExpression.toParsedText();
	}
}