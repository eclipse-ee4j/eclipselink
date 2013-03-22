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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.queries;

import org.eclipse.persistence.internal.databaseaccess.*;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.sessions.remote.*;

/**
 * <p><b>Purpose</b>:
 * Used to support cursored streams in a read query.
 * <p>
 * <p><b>Responsibilities</b>:
 * Execute the cursored read and build the stream.
 *
 * @author James Sutherland
 * @since TOPLink/Java 1.2
 */
public class CursoredStreamPolicy extends CursorPolicy {
    protected int initialReadSize;
    protected ValueReadQuery sizeQuery;

    /**
     * default constructor
     */
    public CursoredStreamPolicy() {
        super();
    }

    /**
     * set the initial read size to match the page size
     */
    public CursoredStreamPolicy(ReadQuery query, int pageSize) {
        super(query, pageSize);
        setInitialReadSize(pageSize);
    }

    public CursoredStreamPolicy(ReadQuery query, int initialReadSize, int pageSize) {
        this(query, pageSize);
        setInitialReadSize(initialReadSize);
    }

    public CursoredStreamPolicy(ReadQuery query, int initialReadSize, int pageSize, ValueReadQuery sizeQuery) {
        this(query, initialReadSize, pageSize);
        setSizeQuery(sizeQuery);
    }

    /**
     * INTERNAL:
     * Execute the cursored select and build the stream.
     */
    public Object execute() {
        DatabaseCall call = getQuery().getQueryMechanism().cursorSelectAllRows();

        // Create cursored stream		
        CursoredStream stream = new CursoredStream(call, this);

        return stream;
    }

    /**
     * Specifies the number of elements to be read initially into a cursored stream.
     */
    public int getInitialReadSize() {
        return initialReadSize;
    }

    /**
     * Return the query used to read the size.
     * This is required for SQL read queries.
     */
    public ValueReadQuery getSizeQuery() {
        return sizeQuery;
    }

    /**
     * INTERNAL:
     * Return if a custom size query is defined.
     */
    public boolean hasSizeQuery() {
        return sizeQuery != null;
    }

    public boolean isCursoredStreamPolicy() {
        return true;
    }

    /**
     * INTERNAL:
     * Prepare and validate.
     */
    public void prepare(DatabaseQuery query, AbstractSession session) throws QueryException {
        super.prepare(query, session);
    }

    /**
     * INTERNAL:
     * Execute the cursored select and build the stream.
     */
    public Object remoteExecute() {
        return ((DistributedSession)getQuery().getSession()).cursorSelectObjects(this);
    }

    /**
     * Specifies the number of elements to be read initially into a cursored stream
     */
    public void setInitialReadSize(int initialReadSize) {
        this.initialReadSize = initialReadSize;
    }

    /**
     * Set the query used to read the size.
     * This is required for SQL read queries.
     */
    public void setSizeQuery(ValueReadQuery sizeQuery) {
        this.sizeQuery = sizeQuery;
    }
}
