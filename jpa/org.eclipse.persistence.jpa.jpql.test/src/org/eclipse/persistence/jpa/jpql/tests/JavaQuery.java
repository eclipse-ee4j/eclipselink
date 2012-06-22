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
package org.eclipse.persistence.jpa.jpql.tests;

import org.eclipse.persistence.jpa.jpql.spi.IManagedTypeProvider;
import org.eclipse.persistence.jpa.jpql.spi.IQuery;

/**
 * The concrete implementation of {@link IQuery} that is wrapping the design-time representation
 * of a JPQL query.
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
final class JavaQuery implements IQuery {

	/**
	 *
	 */
	private String jpqlQuery;

	/**
	 *
	 */
	private IManagedTypeProvider provider;

	/**
	 * Creates a new <code>JavaQuery</code>.
	 *
	 * @param provider
	 * @param jpqlQuery
	 */
	JavaQuery(IManagedTypeProvider provider, String jpqlQuery) {
		super();
		this.provider  = provider;
		this.jpqlQuery = jpqlQuery;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getExpression() {
		return jpqlQuery;
	}

	/**
	 * {@inheritDoc}
	 */
	public IManagedTypeProvider getProvider() {
		return provider;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString());
		sb.append(", query=");
		sb.append(getExpression());
		return sb.toString();
	}
}