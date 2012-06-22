/*******************************************************************************
 * Copyright (c) 2006, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.jpa.internal.jpql;

import org.eclipse.persistence.jpa.jpql.spi.IManagedTypeProvider;
import org.eclipse.persistence.jpa.jpql.spi.IQuery;

/**
 * This implementation of an {@link IQuery} simply holds onto the string representation of the JPQL
 * query.
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
public final class VirtualQuery implements IQuery {

	/**
	 * A non-<code>null</code> string representation of the JPQL query
	 */
	private String jpqlQuery;

	/**
	 * The provider of managed types.
	 */
	private final IManagedTypeProvider provider;

	/**
	 * Creates a new <code>VirtualQuery</code>.
	 *
	 * @param provider The provider of managed types
	 * @param query A non-<code>null</code> string representation of the JPQL query
	 */
	public VirtualQuery(IManagedTypeProvider provider, String jpqlQuery) {
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
	 * Sets the string representation of the JPQL query.
	 *
	 * @param jpqlQuery A non-<code>null</code> string representation of the JPQL query
	 */
	public void setExpression(String jpqlQuery) {
		this.jpqlQuery = jpqlQuery;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString());
		sb.append(", query=");
		sb.append(jpqlQuery);
		return sb.toString();
	}
}