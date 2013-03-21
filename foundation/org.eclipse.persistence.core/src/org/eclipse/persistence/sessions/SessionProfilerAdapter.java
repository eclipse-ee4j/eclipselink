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
package org.eclipse.persistence.sessions;

import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.*;

/**
 * Adapter class for SessionProfiler interface.
 * This class should be subclasses for profilers to avoid backward compatiblity issues with future releases
 * when additional API is added to the interface.
 *
 * @author James Sutherland
 */
public abstract class SessionProfilerAdapter implements SessionProfiler {

    /**
     * INTERNAL:
     * End the operation timing.
     */
    public void endOperationProfile(String operationName) {
        
    }

    /**
     * INTERNAL:
     * End the operation timing.
     */
    public void endOperationProfile(String operationName, DatabaseQuery query, int weight) {
        
    }

    /**
     * INTERNAL:
     * Finish a profile operation if profiling.
     * This assumes the start operation proceeds on the stack.
     * The session must be passed to allow units of work etc. to share their parents profiler.
     *
     * @return the execution result of the query.
     */
    public Object profileExecutionOfQuery(DatabaseQuery query, Record row, AbstractSession session) {
        return session.internalExecuteQuery(query, (AbstractRecord)row);
    }

    /**
     * INTERNAL:
     * Set the session.
     */
    public void setSession(Session session) {
        
    }

    /**
     * INTERNAL:
     * Start the operation timing.
     */
    public void startOperationProfile(String operationName) {
        
    }

    /**
     * INTERNAL:
     * Start the operation timing.
     */
    public void startOperationProfile(String operationName, DatabaseQuery query, int weight) {
        
    }

    /**
     * INTERNAL:
     * Update the value of the State sensor.(DMS)
     */
    public void update(String operationName, Object value) {
        
    }

    /**
     * INTERNAL:
     * Increase DMS Event sensor occurrence.(DMS)
     */
    public void occurred(String operationName, AbstractSession session) {
        
    }
    
    /**
     * INTERNAL:
     * Increase DMS Event sensor occurrence.(DMS)
     */
    public void occurred(String operationName, DatabaseQuery query, AbstractSession session) {
        
    }

    /**
     * INTERNAL:
     * Set DMS sensor weight(DMS)
     */
    public void setProfileWeight(int weight) {
        
    }

    /**
     * INTERNAL:
     * Return DMS sensor weight(DMS)
     */
    public int getProfileWeight() {
        return SessionProfiler.HEAVY;
    }

    /**
     * INTERNAL:
     * Initialize EclipseLink noun tree(DMS)
     */
    public void initialize() {
        
    }
}
