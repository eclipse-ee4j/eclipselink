/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle
 *
 ******************************************************************************/
package org.eclipse.persistence.queries;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * This interface defines the entry point for implementing a JPQL query parsing
 * system in EclipseLink.
 * <p>
 * By default, EclipseLink uses {@link ANTLRQueryBuilder} for parsing a query
 * and converting it into a {@link DatabaseQuery}.
 * <p>
 * Third-parties can implement this interface and provide a different JPQL
 * parsing system if required. The {@link JPAQueryBuilder} implementing class
 * can be registered via a persistence unit property and the implementation
 * class must have a public, zero-arg constructor.
 * 
 * @version 2.4
 * @since 2.2
 * @author John Bracken
 * @author Pascal Filion
 */
public interface JPAQueryBuilder {

    /**
     * Allow the parser validation level to be set.
     * 
     * @param level
     *            The validation levels are defined in ParserValidationType
     */
    void setValidationLevel(String level);

    /**
     * Creates a fully initialized {@link DatabaseQuery} by parsing the given
     * JPQL query.
     * 
     * @param jpqlQuery
     *            A non-<code>null</code> string representation of the query to
     *            parse and to convert into a {@link DatabaseQuery}
     * @param query
     *            The query to populate with the derived JPQL query
     * @param session
     *            The EclipseLink {@link AbstractSession} that this query will
     *            execute against
     * @return The fully initialized {@link DatabaseQuery}
     */
    DatabaseQuery buildQuery(CharSequence jpqlQuery, AbstractSession session);

    /**
     * Creates a new {@link Expression} that represents the given selection
     * criteria.
     * 
     * @param entityName
     *            The name of the entity for which a criteria is created
     * @param selectionCriteria
     *            The string representation of a conditional expression to parse
     * @param session
     *            The EclipseLink {@link AbstractSession} that this query will
     *            execute against
     * @return The fully initialized {@link Expression}
     */
   Expression buildSelectionCriteria(String entityName,
                                     String selectionCriteria,
                                     AbstractSession session);

    /**
     * Populates the given {@link DatabaseQuery} by parsing the given JPQL
     * query.
     * 
     * @param jpqlQuery
     *            A non-<code>null</code> string representation of the query to
     *            parse and to convert into a {@link DatabaseQuery}
     * @param query
     *            The query to populate with the derived JPQL query
     * @param session
     *            The EclipseLink {@link AbstractSession} that this query will
     *            execute against
     */
    void populateQuery(CharSequence jpqlQuery, DatabaseQuery query, AbstractSession session);
}