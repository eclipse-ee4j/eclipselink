/*
 * Copyright (c) 1998, 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.sessions;

import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.*;

/**
 * <p><b>Purpose</b>: This interface defines the link between the Session and the PerformanceProfiler tool.
 * It is provide to decouple the session from tools and to allow other profilers to register with the session.
 *
 * @author James Sutherland
 */
public interface SessionProfiler {
    //dms sensor weight constants
    int NONE = 0;
    int NORMAL = 5;
    int HEAVY = 10;
    int ALL = Integer.MAX_VALUE;

    // Sensors display name
    String SessionName = "Info:SessionName";
    String LoginTime = "Info:LoginTime";
    String RcmStatus = "Info:CacheCoordinationStatus";
    String CacheSize = "Info:CacheSize";//TODO

    String ClientSessionCreated = "Counter:ClientSessionCreates";
    String ClientSessionReleased = "Counter:ClientSessionReleases";
    String UowCreated = "Counter:UnitOfWorkCreates";
    String UowReleased = "Counter:UnitOfWorkReleases";
    String UowCommits = "Counter:UnitOfWorkCommits";
    String UowRollbacks = "Counter:UnitOfWorkRollbacks";
    String OptimisticLockException = "Counter:OptimisticLocks";
    String RcmReceived = "Counter:MessagesReceived";
    String RcmSent = "Counter:MessagesSent";
    String RemoteChangeSet = "Counter:RemoteChangeSets";
    String Connects = "Counter:ConnectCalls";
    String Disconnects = "Counter:DisconnectCalls";
    String CacheHits = "Counter:CacheHits";
    String CacheMisses = "Counter:CacheMisses";
    String ChangeSetsProcessed = "Counter:ChangesProcessed";
    String ChangeSetsNotProcessed = "Counter:ChangesNotProcessed";

    String DescriptorEvent = "Timer:DescriptorEvents";
    String SessionEvent = "Timer:SessionEvents";
    String QueryPreparation = "Timer:QueryPreparation";
    String SqlGeneration = "Timer:SqlGeneration";
    String SqlPrepare = "Timer:SqlPrepare";
    String StatementExecute = "Timer:StatementExecute";
    String RowFetch = "Timer:RowFetch";
    String ObjectBuilding = "Timer:ObjectBuilding";
    String Register = "Timer:Register";
    String Merge = "Timer:Merge";
    String DistributedMerge = "Timer:DistributedMerge";
    String AssignSequence = "Timer:Sequencing";
    String Caching = "Timer:Caching";
    String CacheCoordinationSerialize = "Timer:CacheCoordinationSerialize";
    String CacheCoordination = "Timer:CacheCoordination";
    String ConnectionManagement = "Timer:ConnectionManagement";
    String Logging = "Timer:Logging";
    String JtsBeforeCompletion = "Timer:TXBeforeCompletion";
    String JtsAfterCompletion = "Timer:TXAfterCompletion";
    String Transaction = "Timer:Transactions";
    String UowCommit = "Timer:UnitOfWorkCommit";
    String ConnectionPing = "Timer:ConnectionPing";
    String Remote = "Timer:Remote";
    String RemoteLazy = "Timer:RemoteLazy";
    String RemoteMetadata = "Timer:RemoteMetadata";

    /**
     * INTERNAL:
     * End the operation timing.
     */
    void endOperationProfile(String operationName);

    /**
     * INTERNAL:
     * End the operation timing.
     */
    void endOperationProfile(String operationName, DatabaseQuery query, int weight);

    /**
     * INTERNAL:
     * Finish a profile operation if profiling.
     * This assumes the start operation proceeds on the stack.
     * The session must be passed to allow units of work etc. to share their parents profiler.
     *
     * @return the execution result of the query.
     */
    Object profileExecutionOfQuery(DatabaseQuery query, Record row, AbstractSession session);

    /**
     * INTERNAL:
     * Set the session.
     */
    void setSession(Session session);

    /**
     * INTERNAL:
     * Start the operation timing.
     */
    void startOperationProfile(String operationName);

    /**
     * INTERNAL:
     * Start the operation timing.
     */
    void startOperationProfile(String operationName, DatabaseQuery query, int weight);

    /**
     * INTERNAL:
     * Update the value of the State sensor.(DMS)
     */
    void update(String operationName, Object value);

    /**
     * INTERNAL:
     * Increase DMS Event sensor occurrence.(DMS)
     */
    void occurred(String operationName, AbstractSession session);

    /**
     * INTERNAL:
     * Increase DMS Event sensor occurrence.(DMS)
     */
    void occurred(String operationName, DatabaseQuery query, AbstractSession session);

    /**
     * INTERNAL:
     * Set DMS sensor weight(DMS)
     */
    void setProfileWeight(int weight);

    /**
     * INTERNAL:
     * Return DMS sensor weight(DMS)
     */
    int getProfileWeight();

    /**
     * INTERNAL:
     * Initialize EclipseLink noun tree(DMS)
     */
    void initialize();
}
