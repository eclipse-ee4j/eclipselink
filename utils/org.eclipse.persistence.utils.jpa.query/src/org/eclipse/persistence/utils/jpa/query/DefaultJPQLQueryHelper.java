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

import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.utils.jpa.query.spi.IQuery;

/**
 * The EclipseLink implementation of {@link AbstractJPQLQueryHelper}.
 *
 * @version 2.3
 * @since 2.3
 * @author Pascal Filion
 */
final class DefaultJPQLQueryHelper extends AbstractJPQLQueryHelper<String> {

	/**
	 * The EclipseLink {@link AbstractSession} this query will use.
	 */
	private final AbstractSession session;

	/**
	 * Creates a new <code>DefaultJPQLQueryHelper</code>.
	 *
	 * @param session The EclipseLink {@link AbstractSession} this query will use
	 * @param query The string representation of the query
	 */
	DefaultJPQLQueryHelper(AbstractSession session, String query) {
		super(query);
		this.session = session;
	}

	private JavaManagedTypeProvider buildProvider() {
		return new JavaManagedTypeProvider(session);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IQuery buildQuery(String query) {
		return new JavaQuery(buildProvider(), query);
	}
}