/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
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
    @Override
    public void endOperationProfile(String operationName) {

    }

    /**
     * INTERNAL:
     * End the operation timing.
     */
    @Override
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
    @Override
    public Object profileExecutionOfQuery(DatabaseQuery query, Record row, AbstractSession session) {
        return session.internalExecuteQuery(query, (AbstractRecord)row);
    }

    /**
     * INTERNAL:
     * Set the session.
     */
    @Override
    public void setSession(Session session) {

    }

    /**
     * INTERNAL:
     * Start the operation timing.
     */
    @Override
    public void startOperationProfile(String operationName) {

    }

    /**
     * INTERNAL:
     * Start the operation timing.
     */
    @Override
    public void startOperationProfile(String operationName, DatabaseQuery query, int weight) {

    }

    /**
     * INTERNAL:
     * Update the value of the State sensor.(DMS)
     */
    @Override
    public void update(String operationName, Object value) {

    }

    /**
     * INTERNAL:
     * Increase DMS Event sensor occurrence.(DMS)
     */
    @Override
    public void occurred(String operationName, AbstractSession session) {

    }

    /**
     * INTERNAL:
     * Increase DMS Event sensor occurrence.(DMS)
     */
    @Override
    public void occurred(String operationName, DatabaseQuery query, AbstractSession session) {

    }

    /**
     * INTERNAL:
     * Set DMS sensor weight(DMS)
     */
    @Override
    public void setProfileWeight(int weight) {

    }

    /**
     * INTERNAL:
     * Return DMS sensor weight(DMS)
     */
    @Override
    public int getProfileWeight() {
        return SessionProfiler.HEAVY;
    }

    /**
     * INTERNAL:
     * Initialize EclipseLink noun tree(DMS)
     */
    @Override
    public void initialize() {

    }
}
