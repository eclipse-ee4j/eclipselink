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
    public static final int NONE = 0;
    public static final int NORMAL = 5;
    public static final int HEAVY = 10;
    public static final int ALL = Integer.MAX_VALUE;

    // Sensors display name
    public static final String SessionName = "Info:SessionName";
    public static final String LoginTime = "Info:LoginTime";
    public static final String RcmStatus = "Info:CacheCoordinationStatus";
    public static final String CacheSize = "Info:CacheSize";//TODO
    
    public static final String ClientSessionCreated = "Counter:ClientSessionCreates";
    public static final String ClientSessionReleased = "Counter:ClientSessionReleases";
    public static final String UowCreated = "Counter:UnitOfWorkCreates";
    public static final String UowReleased = "Counter:UnitOfWorkReleases";
    public static final String UowCommits = "Counter:UnitOfWorkCommits";
    public static final String UowRollbacks = "Counter:UnitOfWorkRollbacks";
    public static final String OptimisticLockException = "Counter:OptimisticLocks";
    public static final String RcmReceived = "Counter:MessagesReceived";
    public static final String RcmSent = "Counter:MessagesSent";
    public static final String RemoteChangeSet = "Counter:RemoteChangeSets";
    public static final String Connects = "Counter:ConnectCalls";
    public static final String Disconnects = "Counter:DisconnectCalls";
    public static final String CacheHits = "Counter:CacheHits";
    public static final String CacheMisses = "Counter:CacheMisses";
    public static final String ChangeSetsProcessed = "Counter:ChangesProcessed";
    public static final String ChangeSetsNotProcessed = "Counter:ChangesNotProcessed";
    
    public static final String DescriptorEvent = "Timer:DescriptorEvents";
    public static final String SessionEvent = "Timer:SessionEvents";
    public static final String QueryPreparation = "Timer:QueryPreparation";
    public static final String SqlGeneration = "Timer:SqlGeneration";
    public static final String SqlPrepare = "Timer:SqlPrepare";
    public static final String StatementExecute = "Timer:StatementExecute";
    public static final String RowFetch = "Timer:RowFetch";
    public static final String ObjectBuilding = "Timer:ObjectBuilding";
    public static final String Register = "Timer:Register";
    public static final String Merge = "Timer:Merge";
    public static final String DistributedMerge = "Timer:DistributedMerge";
    public static final String AssignSequence = "Timer:Sequencing";
    public static final String Caching = "Timer:Caching";
    public static final String ConnectionManagement = "Timer:ConnectionManagement";
    public static final String Logging = "Timer:Logging";
    public static final String JtsBeforeCompletion = "Timer:TXBeforeCompletion";
    public static final String JtsAfterCompletion = "Timer:TXAfterCompletion";
    public static final String Transaction = "Timer:Transactions";
    public static final String UowCommit = "Timer:UnitOfWorkCommit";
    public static final String ConnectionPing = "Timer:ConnectionPing";
    public static final String Remote = "Timer:Remote";
    public static final String RemoteLazy = "Timer:RemoteLazy";
    public static final String RemoteMetadata = "Timer:RemoteMetadata";

    /**
     * INTERNAL:
     * End the operation timing.
     */
    public void endOperationProfile(String operationName);

    /**
     * INTERNAL:
     * End the operation timing.
     */
    public void endOperationProfile(String operationName, DatabaseQuery query, int weight);

    /**
     * INTERNAL:
     * Finish a profile operation if profiling.
     * This assumes the start operation proceeds on the stack.
     * The session must be passed to allow units of work etc. to share their parents profiler.
     *
     * @return the execution result of the query.
     */
    public Object profileExecutionOfQuery(DatabaseQuery query, Record row, AbstractSession session);

    /**
     * INTERNAL:
     * Set the session.
     */
    public void setSession(Session session);

    /**
     * INTERNAL:
     * Start the operation timing.
     */
    public void startOperationProfile(String operationName);

    /**
     * INTERNAL:
     * Start the operation timing.
     */
    public void startOperationProfile(String operationName, DatabaseQuery query, int weight);

    /**
     * INTERNAL:
     * Update the value of the State sensor.(DMS)
     */
    public void update(String operationName, Object value);

    /**
     * INTERNAL:
     * Increase DMS Event sensor occurrence.(DMS)
     */
    public void occurred(String operationName, AbstractSession session);
    
    /**
     * INTERNAL:
     * Increase DMS Event sensor occurrence.(DMS)
     */
    public void occurred(String operationName, DatabaseQuery query, AbstractSession session);

    /**
     * INTERNAL:
     * Set DMS sensor weight(DMS)
     */
    public void setProfileWeight(int weight);

    /**
     * INTERNAL:
     * Return DMS sensor weight(DMS)
     */
    public int getProfileWeight();

    /**
     * INTERNAL:
     * Initialize EclipseLink noun tree(DMS)
     */
    public void initialize();
}
