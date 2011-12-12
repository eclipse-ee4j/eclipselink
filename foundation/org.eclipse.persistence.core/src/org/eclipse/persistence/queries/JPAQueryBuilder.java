/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available athttp://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle
 *
 ******************************************************************************/
package org.eclipse.persistence.queries;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.DatabaseQuery;

/**
 * This interface defines the entry point for implementing a JPA Query parsing system in EclipseLink.
 * <p>
 * By default, EclipseLink uses {@link ANTLRQueryBuilder} for parsing a query and
 * converting it into a {@link DatabaseQuery}.
 * <p>
 * Third-parties can implement this interface and provide a different JPQL parsing system if required.
 * The {@link JPAQueryBuilder} implementing class can be registered via a persistence unit property and
 * the implementation class must have a public, zero-arg constructor.
 *
 * @see PersistenceUnitProperties#JPQL_QUERY_BUILDER
 * @see PersistenceUnitProperties#JPQL_QUERY_VALIDATION
 *
 * @version 2.2
 * @since 2.2
 * @author John Bracken
 * @author Pascal Filion
 */
public interface JPAQueryBuilder {

	/**
	 * Creates a fully initialized {@link DatabaseQuery} by parsing the given Java Persistence query.
	 *
	 * @param jpqlQuery A non-<code>null</code> string representation of the query to parse and to
	 * convert into a {@link DatabaseQuery}
	 * @param query The query to populate with the derived jpql criteria
	 * @param session The EclipseLink {@link AbstractSession} that this query will execute against
	 * @return The fully initialized {@link DatabaseQuery}
	 */
        // TODO - must set class loader
	DatabaseQuery buildQuery(String jpqlQuery, AbstractSession session);

	/**
	 * Populates an existing {@link DatabaseQuery} by parsing the given Java Persistence query.
	 *
	 * @param jpqlQuery A non-<code>null</code> string representation of the query to parse and to
	 * convert into a {@link DatabaseQuery}
	 * @param query The query to populate with the derived jpql criteria
	 * @param session The EclipseLink {@link AbstractSession} that this query will execute against
	 */
        // TODO - must set class loader
	void populateQuery(String jpqlQuery, DatabaseQuery query, AbstractSession session);
}
