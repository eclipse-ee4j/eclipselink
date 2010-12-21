/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.jpa;

import java.util.Collection;

import org.eclipse.persistence.queries.Cursor;
import org.eclipse.persistence.queries.DatabaseQuery;

/**
 * PUBLIC:
 * EclipseLInk specific JPA query interface.  Provides the functionality defined in
 * javax.persistence.Query and adds access to the underlying database query for EclipseLink specific
 * functionality.
 */
public interface JpaQuery<X> extends javax.persistence.TypedQuery<X> {

    /**
     * PUBLIC:
     * Return the cached database query for this query.  If the query is
     * a named query and it has not yet been looked up, the query will be looked up
     * and stored as the cached query.
     */
    public DatabaseQuery getDatabaseQuery();

    /**
     * PUBLIC:
     * return the EntityManager for this query
     */
    public JpaEntityManager getEntityManager();

    /**
     * PUBLIC:
     * Non-standard method to return results of a ReadQuery that has a containerPolicy
     * that returns objects as a collection rather than a List
     * @return Collection of results
     */
    public Collection getResultCollection();
    
    /**
     * PUBLIC:
     * Non-standard method to return results of a ReadQuery that uses a Cursor.
     * @return Cursor on results, either a CursoredStream, or ScrollableCursor
     */
    public Cursor getResultCursor();
    
    /**
     * PUBLIC:
     * Replace the cached query with the given query.
     */
    public void setDatabaseQuery(DatabaseQuery query);

}
