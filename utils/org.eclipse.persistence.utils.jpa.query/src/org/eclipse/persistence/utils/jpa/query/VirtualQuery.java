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

import org.eclipse.persistence.utils.jpa.query.spi.IManagedTypeProvider;
import org.eclipse.persistence.utils.jpa.query.spi.IQuery;

/**
 * This implementation of an {@link IQuery} simply holds onto the string representation of the JPQL.
 *
 * @version 11.2.0
 * @since 11.2.0
 * @author Pascal Filion
 */
public final class VirtualQuery implements IQuery {

	/**
	 * The provider of managed types.
	 */
	private final IManagedTypeProvider provider;

	/**
	 * A non-<code>null</code> string representation of the JPQL query
	 */
	private final String query;

	/**
	 * Creates a new <code>VirtualQuery</code>.
	 *
	 * @param provider The provider of managed types
	 * @param query A non-<code>null</code> string representation of the JPQL query
	 */
	public VirtualQuery(IManagedTypeProvider provider, String query) {
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
	public IManagedTypeProvider getProvider() {
		return provider;
	}
}