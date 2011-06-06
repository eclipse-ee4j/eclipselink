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
package org.eclipse.persistence.internal.jpa.jpql;

import org.eclipse.persistence.jpa.jpql.spi.IQuery;

/**
 * The concrete implementation of {@link IQuery} that is wrapping the runtime representation of a
 * named query.
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
@SuppressWarnings("nls")
final class JavaQuery implements IQuery {

	/**
	 *  The provider of managed types.
	 */
	private final JavaManagedTypeProvider provider;

	/**
	 * The string representation of the Java Persistence query.
	 */
	private final String query;

	/**
	 * Creates a new <code>JavaQuery</code>.
	 *
	 * @param provider The provider of managed types
	 * @param query The string representation of the Java Persistence query
	 */
	JavaQuery(JavaManagedTypeProvider provider, String query) {
		super();
		this.provider = provider;
		this.query    = query;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getExpression() {
		return query;
	}

	/**
	 * {@inheritDoc}
	 */
	public JavaManagedTypeProvider getProvider() {
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
		sb.append(query);
		return sb.toString();
	}
}