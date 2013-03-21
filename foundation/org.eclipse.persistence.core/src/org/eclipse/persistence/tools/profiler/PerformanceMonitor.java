/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     James Sutherland (Oracle) - initial API and implementation
 ******************************************************************************/  
package org.eclipse.persistence.tools.profiler;

import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.io.*;

import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.sessions.Record;
import org.eclipse.persistence.sessions.SessionProfiler;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * <p><b>Purpose</b>: A tool used to provide performance monitoring information in a server environment.
 *
 * @since EclipseLink 2.2
 * @author James Sutherland
 */
public class PerformanceMonitor implements Serializable, Cloneable, SessionProfiler {
    protected static final String COUNTER = "Counter:";
    protected static final String TIMER = "Timer:";
    
    transient protected AbstractSession session;
    protected Map<String, Object> operationTimings;
    protected Map<Integer, Map<String, Long>> operationStartTimesByThread;//facilitates concurrency
    protected long lastDumpTime;
    protected long dumpTime;
    protected int profileWeight;

    /**
     * PUBLIC:
     * Create a new profiler.
     * The profiler can be registered with a session to log performance information on queries.
     */
    public PerformanceMonitor() {
        this.operationTimings = new ConcurrentHashMap();
        this.operationStartTimesByThread = new ConcurrentHashMap();
        this.lastDumpTime = System.currentTimeMillis();
        this.dumpTime = 60000; // 1 minute
        this.profileWeight = SessionProfiler.ALL;
    }

    /**
     * Return the number of milliseconds after which the monitor results should be logged.
     */
    public long getDumpTime() {
        return dumpTime;
    }

    /**
     * Set the number of milliseconds after which the monitor results should be logged.
     */
    public void setDumpTime(long dumpTime) {
        this.dumpTime = dumpTime;
    }
    
    public PerformanceMonitor clone() {
        try {
            return (PerformanceMonitor)super.clone();
        } catch (CloneNotSupportedException exception) {
            throw new InternalError();
        }
    }
    
    /**
     * Log the results after a set amount of time has passed.
     */
    public void checkDumpTime() {
        if ((System.currentTimeMillis() - this.lastDumpTime) > this.dumpTime) {
            dumpResults();
        }
    }
    
    /**
     * Log the results to the session's log (System.out).
     */
    public void dumpResults() {
        this.lastDumpTime = System.currentTimeMillis();
        StringWriter writer = new StringWriter();
        writer.write("\nPerformance Monitor:");
        writer.write(String.valueOf(this.lastDumpTime));
        writer.write("\nOperation\tValue (ns)\n");
        Set<String> operations = new TreeSet<String>(this.operationTimings.keySet());
        NumberFormat formater = NumberFormat.getInstance();
        for (String operation : operations) {
            Object value = this.operationTimings.get(operation);
            if (value == null) {
                value = Long.valueOf(0);
            }
            writer.write(operation);
            writer.write("\t");
            if (value instanceof Long) {
                writer.write(formater.format(value));
            } else {
                writer.write(value.toString());
            }
            writer.write("\n");
        }
        try {
            this.session.getLog().write(writer.toString());
            this.session.getLog().flush();
        } catch (IOException error) {
            // ignore
        }
    }

    /**
     * INTERNAL:
     * End the operation timing.
     */
    public void endOperationProfile(String operationName) {
        if (this.profileWeight < SessionProfiler.HEAVY) {
            return;
        }
        long endTime = System.nanoTime();
        Long startTime = getOperationStartTimes().get(operationName);
        if (startTime == null) {
            return;
        }
        long time = endTime - startTime.longValue();

        synchronized (this.operationTimings) {
            Long totalTime = (Long)this.operationTimings.get(operationName);
            if (totalTime == null) {
                this.operationTimings.put(operationName, Long.valueOf(time));
            } else {
                this.operationTimings.put(operationName, Long.valueOf(totalTime.longValue() + time));
            }
        }
    }

    /**
     * INTERNAL:
     * End the operation timing.
     */
    public void endOperationProfile(String operationName, DatabaseQuery query, int weight) {
        if (this.profileWeight < weight) {
            return;
        }
        endOperationProfile(operationName);
        if (query != null) {
            endOperationProfile(TIMER + query.getMonitorName() + ":" + operationName.substring(TIMER.length(), operationName.length()));
        }
    }

    protected Map<String, Long> getOperationStartTimes() {
        Integer threadId = Integer.valueOf(Thread.currentThread().hashCode());
        Map<String, Long> times = this.operationStartTimesByThread.get(threadId);
        if (times == null) {
            times = new Hashtable<String, Long>();
            this.operationStartTimesByThread.put(threadId, times);
        }
        return times;
    }

    protected Map<Integer, Map<String, Long>> getOperationStartTimesByThread() {
        return operationStartTimesByThread;
    }

    public Object getOperationTime(String operation) {
        return this.operationTimings.get(operation);
    }

    public Map<String, Object> getOperationTimings() {
        return operationTimings;
    }

    public AbstractSession getSession() {
        return session;
    }

    /**
     * INTERNAL:
     * Monitoring is done on the endOperation only.
     */
    public Object profileExecutionOfQuery(DatabaseQuery query, Record row, AbstractSession session) {
        if (this.profileWeight < SessionProfiler.HEAVY) {
            return session.internalExecuteQuery(query, (AbstractRecord)row);
        }
        startOperationProfile(TIMER + query.getMonitorName());
        startOperationProfile(TIMER + query.getClass().getSimpleName());
        occurred(COUNTER + query.getClass().getSimpleName(), session);
        occurred(COUNTER + query.getMonitorName(), session);
        try {
            return session.internalExecuteQuery(query, (AbstractRecord)row);
        } finally {
            endOperationProfile(TIMER + query.getMonitorName());
            endOperationProfile(TIMER + query.getClass().getSimpleName());
            checkDumpTime();
        }
    }

    public void setSession(org.eclipse.persistence.sessions.Session session) {
        this.session = (AbstractSession)session;
    }

    /**
     * INTERNAL:
     * Start the operation timing.
     */
    public void startOperationProfile(String operationName) {
        getOperationStartTimes().put(operationName, Long.valueOf(System.nanoTime()));
    }

    /**
     * INTERNAL:
     * Start the operation timing.
     */
    public void startOperationProfile(String operationName, DatabaseQuery query, int weight) {
        if (this.profileWeight < weight) {
            return;
        }
        startOperationProfile(operationName);
        if (query != null) {
            startOperationProfile(TIMER + query.getMonitorName() + ":" + operationName.substring(TIMER.length(), operationName.length()));
        }
    }

    public void update(String operationName, Object value) {
        this.operationTimings.put(operationName, value);
    }

    public void occurred(String operationName, AbstractSession session) {
        if (this.profileWeight < SessionProfiler.NORMAL) {
            return;
        }
        synchronized (this.operationTimings) {
            Long occurred = (Long)this.operationTimings.get(operationName);
            if (occurred == null) {
                this.operationTimings.put(operationName, Long.valueOf(1));
            } else {
                this.operationTimings.put(operationName, Long.valueOf(occurred.longValue() + 1));
            }
        }
    }

    public void occurred(String operationName, DatabaseQuery query, AbstractSession session) {
        if (this.profileWeight < SessionProfiler.NORMAL) {
            return;
        }
        occurred(operationName, session);
        occurred(COUNTER + query.getMonitorName() + ":" + operationName.substring(COUNTER.length(), operationName.length()), session);
    }

    /**
     * Set the level of profiling.
     * One of ALL, HEAVY, NORMAL, NONE.
     * The higher the level, the more operations are profiled.
     * @see SessiobProfiler
     */
    public void setProfileWeight(int profileWeight) {
        this.profileWeight = profileWeight;
    }

    /**
     * Return the level of profiling.
     * One of ALL, HEAVY, NORMAL, NONE.
     * @see SessiobProfiler
     */
    public int getProfileWeight() {
        return profileWeight;
    }

    public void initialize() {
    }
}
