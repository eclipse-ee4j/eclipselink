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
package org.eclipse.persistence.tools.profiler;

import java.util.*;
import java.io.*;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.sessions.Record;
import org.eclipse.persistence.sessions.SessionProfiler;
import org.eclipse.persistence.internal.localization.*;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;

/**
 * <p><b>Purpose</b>: A tool used to provide high level performance profiling information.
 *
 * @since TopLink 1.0
 * @author James Sutherland
 */
public class PerformanceProfiler implements Serializable, Cloneable, SessionProfiler {
    protected Vector profiles;
    transient protected AbstractSession session;
    protected boolean shouldLogProfile;
    protected int nestLevel;
    protected long nestTime;
    protected long profileTime;
    protected Hashtable operationTimings;
    protected Hashtable operationStartTimes;
    protected Hashtable operationTimingsByThread;//facilitates concurrency
    protected Hashtable operationStartTimesByThread;//facilitates concurrency

    /**
     * PUBLIC:
     * Create a new profiler.
     * The profiler can be registered with a session to log performance information on queries.
     */
    public PerformanceProfiler() {
        this(true);
    }

    /**
     * OBSOLETE:
     * Create a new profiler.
     * The profiler can be registered with a session to log performance information on queries.
     * @deprecated replaced by PerformanceProfiler()
     */
    public PerformanceProfiler(org.eclipse.persistence.sessions.Session session) {
        this(session, true);
    }

    /**
     * OBSOLETE:
     * Create a new profiler.
     * The profiler can be registered with a session to log performance information on queries.
     * @deprecated replaced by PerformanceProfiler()
     */
    public PerformanceProfiler(org.eclipse.persistence.sessions.Session session, boolean shouldLogProfile) {
        super();
        this.profiles = new Vector();
        this.session = (AbstractSession)session;
        this.shouldLogProfile = shouldLogProfile;
        this.nestLevel = 0;
        this.operationTimingsByThread = new Hashtable();
        this.operationStartTimesByThread = new Hashtable();
    }

    /**
     * PUBLIC:
     * Create a new profiler.
     * The profiler can be registered with a session to log performance information on queries.
     */
    public PerformanceProfiler(boolean shouldLogProfile) {
        super();
        this.profiles = new Vector();
        this.shouldLogProfile = shouldLogProfile;
        this.nestLevel = 0;
        this.profileTime = 0;
        this.nestTime = 0;
        this.operationTimingsByThread = new Hashtable();
        this.operationStartTimesByThread = new Hashtable();
    }

    protected void addProfile(Profile profile) {
        getProfiles().addElement(profile);
    }

    /**
     * INTERNAL:
     * Return a summary profile reporting on the profiles contained.
     */
    public Profile buildProfileSummary() {
        Profile summary = new Profile();
        summary.setDomainClass(Void.class);
        summary.setQueryClass(Void.class);
        for (Enumeration enumtr = getProfiles().elements(); enumtr.hasMoreElements();) {
            Profile profile = (Profile)enumtr.nextElement();

            if ((summary.getShortestTime() == -1) || (profile.getTotalTime() < summary.getShortestTime())) {
                summary.setShortestTime(profile.getTotalTime());
            }
            if (profile.getTotalTime() > summary.getLongestTime()) {
                summary.setLongestTime(profile.getTotalTime());
            }
            summary.setTotalTime(summary.getTotalTime() + profile.getTotalTime());
            summary.setLocalTime(summary.getLocalTime() + profile.getLocalTime());
            summary.setProfileTime(summary.getProfileTime() + profile.getProfileTime());
            summary.setNumberOfInstancesEffected(summary.getNumberOfInstancesEffected() + profile.getNumberOfInstancesEffected());
            for (Enumeration operationNames = profile.getOperationTimings().keys();
                     operationNames.hasMoreElements();) {
                String name = (String)operationNames.nextElement();
                Long oldTime = (Long)summary.getOperationTimings().get(name);
                long profileTime = ((Long)profile.getOperationTimings().get(name)).longValue();
                long newTime;
                if (oldTime == null) {
                    newTime = profileTime;
                } else {
                    newTime = oldTime.longValue() + profileTime;
                }
                summary.getOperationTimings().put(name, Long.valueOf(newTime));
            }
        }

        return summary;
    }

    /**
     * INTERNAL:
     * Return a map of summary profiles reporting on the profile contained.
     */
    public Hashtable buildProfileSummaryByClass() {
        Hashtable summaries = new Hashtable();

        for (Enumeration enumtr = getProfiles().elements(); enumtr.hasMoreElements();) {
            Profile profile = (Profile)enumtr.nextElement();
            Class domainClass = profile.getDomainClass();
            if (domainClass == null) {
                domainClass = Void.class;
            }

            Profile summary = (Profile)summaries.get(domainClass);
            if (summary == null) {
                summary = new Profile();
                summary.setDomainClass(domainClass);
                summaries.put(domainClass, summary);
            }
            if ((summary.getShortestTime() == -1) || (profile.getTotalTime() < summary.getShortestTime())) {
                summary.setShortestTime(profile.getTotalTime());
            }
            if (profile.getTotalTime() > summary.getLongestTime()) {
                summary.setLongestTime(profile.getTotalTime());
            }
            summary.setTotalTime(summary.getTotalTime() + profile.getTotalTime());
            summary.setLocalTime(summary.getLocalTime() + profile.getLocalTime());
            summary.setProfileTime(summary.getProfileTime() + profile.getProfileTime());
            summary.setNumberOfInstancesEffected(summary.getNumberOfInstancesEffected() + profile.getNumberOfInstancesEffected());
            for (Enumeration operationNames = profile.getOperationTimings().keys();
                     operationNames.hasMoreElements();) {
                String name = (String)operationNames.nextElement();
                Long oldTime = (Long)summary.getOperationTimings().get(name);
                long profileTime = ((Long)profile.getOperationTimings().get(name)).longValue();
                long newTime;
                if (oldTime == null) {
                    newTime = profileTime;
                } else {
                    newTime = oldTime.longValue() + profileTime;
                }
                summary.getOperationTimings().put(name, Long.valueOf(newTime));
            }
        }

        return summaries;
    }

    /**
     * INTERNAL:
     * Return a map of summary profiles reporting on the profile contained.
     */
    public Hashtable buildProfileSummaryByQuery() {
        Hashtable summaries = new Hashtable();

        for (Enumeration enumtr = getProfiles().elements(); enumtr.hasMoreElements();) {
            Profile profile = (Profile)enumtr.nextElement();
            Class queryType = profile.getQueryClass();
            if (queryType == null) {
                queryType = Void.class;
            }

            Profile summary = (Profile)summaries.get(queryType);
            if (summary == null) {
                summary = new Profile();
                summary.setQueryClass(queryType);
                summaries.put(queryType, summary);
            }
            summary.setTotalTime(summary.getTotalTime() + profile.getTotalTime());
            summary.setLocalTime(summary.getLocalTime() + profile.getLocalTime());
            summary.setProfileTime(summary.getProfileTime() + profile.getProfileTime());
            summary.setNumberOfInstancesEffected(summary.getNumberOfInstancesEffected() + profile.getNumberOfInstancesEffected());
            for (Enumeration operationNames = profile.getOperationTimings().keys();
                     operationNames.hasMoreElements();) {
                String name = (String)operationNames.nextElement();
                Long oldTime = (Long)summary.getOperationTimings().get(name);
                long profileTime = ((Long)profile.getOperationTimings().get(name)).longValue();
                long newTime;
                if (oldTime == null) {
                    newTime = profileTime;
                } else {
                    newTime = oldTime.longValue() + profileTime;
                }
                summary.getOperationTimings().put(name, Long.valueOf(newTime));
            }
        }

        return summaries;
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException exception) {
            ;//Do nothing
        }

        return null;
    }

    /**
     * PUBLIC:
     * Set whether after each query execution the profile result should be logged.
     * By default this is false.
     */
    public void dontLogProfile() {
        setShouldLogProfile(false);
    }

    /**
     * INTERNAL:
     * End the operation timing.
     */
    public void endOperationProfile(String operationName) {
        long endTime = System.currentTimeMillis();
        Long startTime = ((Long)getOperationStartTimes().get(operationName));
        if (startTime == null) {
            return;
        }
        long time = endTime - startTime.longValue();

        if (getNestLevel() == 0) {
            // Log as a profile if not within query execution,
            // unless no time was recorded, in which case discard.
            if (time == 0) {
                return;
            }
            Profile profile = new Profile();
            profile.setTotalTime(time);
            profile.setLocalTime(time);
            profile.addTiming(operationName, time);
            addProfile(profile);
            if (shouldLogProfile()) {
                Writer writer = getSession().getLog();
                try {
                    profile.write(writer, this);
                    writer.write(Helper.cr());
                    writer.flush();
                } catch (IOException ioe) {
                }
            }
        }

        Long totalTime = (Long)getOperationTimings().get(operationName);
        if (totalTime == null) {
            getOperationTimings().put(operationName, Long.valueOf(time));
        } else {
            getOperationTimings().put(operationName, Long.valueOf(totalTime.longValue() + time));
        }
    }

    /**
     * INTERNAL:
     * End the operation timing.
     */
    public void endOperationProfile(String operationName, DatabaseQuery query, int weight) {
        endOperationProfile(operationName);
    }

    protected int getNestLevel() {
        return nestLevel;
    }

    protected long getNestTime() {
        return nestTime;
    }

    protected Hashtable getOperationStartTimes() {
        Integer threadId = Integer.valueOf(Thread.currentThread().hashCode());
        if (getOperationStartTimesByThread().get(threadId) == null) {
            getOperationStartTimesByThread().put(threadId, new Hashtable(10));
        }
        return (Hashtable)getOperationStartTimesByThread().get(threadId);
    }

    protected Hashtable getOperationStartTimesByThread() {
        return operationStartTimesByThread;
    }

    protected Hashtable getOperationTimings() {
        Integer threadId = Integer.valueOf(Thread.currentThread().hashCode());
        if (getOperationTimingsByThread().get(threadId) == null) {
            getOperationTimingsByThread().put(threadId, new Hashtable(10));
        }
        return (Hashtable)getOperationTimingsByThread().get(threadId);
    }

    protected Hashtable getOperationTimingsByThread() {
        return operationTimingsByThread;
    }

    /**
     * Return the profiles logged in this profiler.
     */
    public Vector getProfiles() {
        return profiles;
    }

    protected long getProfileTime() {
        return profileTime;
    }

    public AbstractSession getSession() {
        return session;
    }

    /**
     * PUBLIC:
     * Set whether after each query execution the profile result should be logged.
     * By default this is true.
     */
    public void logProfile() {
        setShouldLogProfile(true);
    }

    /**
     * PUBLIC:
     * Log a profile summary.
     */
    public void logProfileSummary() {
        Writer writer = getSession().getLog();
        try {
            writer.write(buildProfileSummary().toString());
            writer.write(Helper.cr());
        } catch (IOException ioe) {
        }
    }

    /**
     * PUBLIC:
     * Log a profile summary by class.
     */
    public void logProfileSummaryByClass() {
        Hashtable summaries = buildProfileSummaryByClass();

        for (Enumeration classes = summaries.keys(); classes.hasMoreElements();) {
            Class domainClass = (Class)classes.nextElement();
            Writer writer = getSession().getLog();
            try {
                writer.write(summaries.get(domainClass).toString());
                writer.write(Helper.cr());
            } catch (IOException ioe) {
            }
        }
    }

    /**
     * PUBLIC:
     * Log a profile summary by query.
     */
    public void logProfileSummaryByQuery() {
        Hashtable summaries = buildProfileSummaryByQuery();

        for (Enumeration classes = summaries.keys(); classes.hasMoreElements();) {
            Class queryType = (Class)classes.nextElement();
            Writer writer = getSession().getLog();
            try {
                writer.write(summaries.get(queryType).toString());
                writer.write(Helper.cr());
            } catch (IOException ioe) {
            }
        }
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
        long profileStartTime = System.currentTimeMillis();
        long nestedProfileStartTime = getProfileTime();
        Profile profile = new Profile();
        profile.setQueryClass(query.getClass());
        profile.setDomainClass(query.getReferenceClass());
        Writer writer = getSession().getLog();

        Object result = null;
        try {
            if (shouldLogProfile()) {
                writeNestingTabs(writer);
                writer.write(ToStringLocalization.buildMessage("begin_profile_of", (Object[])null) + "{" + query.toString() + Helper.cr());
                writer.flush();
            }

            setNestLevel(getNestLevel() + 1);
            long startNestTime = getNestTime();
            Hashtable timingsBeforeExecution = (Hashtable)getOperationTimings().clone();
            Hashtable startTimingsBeforeExecution = (Hashtable)getOperationStartTimes().clone();
            long startTime = System.currentTimeMillis();
            result = session.internalExecuteQuery(query, (AbstractRecord)row);
            long endTime = System.currentTimeMillis();
            setNestLevel(getNestLevel() - 1);

            for (Enumeration operationNames = getOperationTimings().keys();
                     operationNames.hasMoreElements();) {
                String name = (String)operationNames.nextElement();
                Long operationStartTime = (Long)timingsBeforeExecution.get(name);
                long operationEndTime = ((Long)getOperationTimings().get(name)).longValue();
                long operationTime;
                if (operationStartTime != null) {
                    operationTime = operationEndTime - operationStartTime.longValue();
                } else {
                    operationTime = operationEndTime;
                }
                profile.addTiming(name, operationTime);
            }

            profile.setTotalTime((endTime - startTime) - (getProfileTime() - nestedProfileStartTime));// Remove the profile time from the total time.;);
            profile.setLocalTime(profile.getTotalTime() - (getNestTime() - startNestTime));
            if (result instanceof Vector) {
                profile.setNumberOfInstancesEffected(((Vector)result).size());
            } else {
                profile.setNumberOfInstancesEffected(1);
            }

            addProfile(profile);
            if (shouldLogProfile()) {
                writeNestingTabs(writer);
                long profileEndTime = System.currentTimeMillis();
                long totalTimeIncludingProfiling = profileEndTime - profileStartTime;// Try to remove the profiling time from the total time.
                profile.setProfileTime(totalTimeIncludingProfiling - profile.getTotalTime());
                profile.write(writer, this);
                writer.write(Helper.cr());
                writeNestingTabs(writer);
                writer.write("}" + ToStringLocalization.buildMessage("end_profile", (Object[])null));
                writer.write(Helper.cr());
                writer.flush();
            }

            if (getNestLevel() == 0) {
                setNestTime(0);
                setProfileTime(0);
                setOperationTimings(new Hashtable(5));
                setOperationStartTimes(new Hashtable(5));
                long profileEndTime = System.currentTimeMillis();
                long totalTimeIncludingProfiling = profileEndTime - profileStartTime;// Try to remove the profiling time from the total time.
                profile.setProfileTime(totalTimeIncludingProfiling - profile.getTotalTime());
            } else {
                setNestTime(startNestTime + profile.getTotalTime());
                setOperationTimings(timingsBeforeExecution);
                setOperationStartTimes(startTimingsBeforeExecution);
                long profileEndTime = System.currentTimeMillis();
                long totalTimeIncludingProfiling = profileEndTime - profileStartTime;// Try to remove the profiling time from the total time.
                setProfileTime(getProfileTime() + (totalTimeIncludingProfiling - (endTime - startTime)));
                profile.setProfileTime(totalTimeIncludingProfiling - profile.getTotalTime());
                for (Enumeration timingsEnum = ((Hashtable)startTimingsBeforeExecution.clone()).keys();
                         timingsEnum.hasMoreElements();) {
                    String timingName = (String)timingsEnum.nextElement();
                    startTimingsBeforeExecution.put(timingName, Long.valueOf(((Number)startTimingsBeforeExecution.get(timingName)).longValue() + totalTimeIncludingProfiling));
                }
            }
        } catch (IOException ioe) {
        }

        return result;
    }

    protected void setNestLevel(int nestLevel) {
        this.nestLevel = nestLevel;
    }

    protected void setNestTime(long nestTime) {
        this.nestTime = nestTime;
    }

    protected void setOperationStartTimes(Hashtable operationStartTimes) {
        Integer threadId = Integer.valueOf(Thread.currentThread().hashCode());
        getOperationStartTimesByThread().put(threadId, operationStartTimes);
    }

    protected void setOperationStartTimesByThread(Hashtable operationStartTimesByThread) {
        this.operationStartTimesByThread = operationStartTimesByThread;
    }

    protected void setOperationTimings(Hashtable operationTimings) {
        Integer threadId = Integer.valueOf(Thread.currentThread().hashCode());
        getOperationTimingsByThread().put(threadId, operationTimings);
    }

    protected void setOperationTimingsByThread(Hashtable operationTimingsByThread) {
        this.operationTimingsByThread = operationTimingsByThread;
    }

    protected void setProfiles(Vector profiles) {
        this.profiles = profiles;
    }

    protected void setProfileTime(long profileTime) {
        this.profileTime = profileTime;
    }

    public void setSession(org.eclipse.persistence.sessions.Session session) {
        this.session = (AbstractSession)session;
    }

    /**
     * PUBLIC:
     * Set whether after each query execution the profile result should be logged.
     * By default this is true.
     */
    public void setShouldLogProfile(boolean shouldLogProfile) {
        this.shouldLogProfile = shouldLogProfile;
    }

    public boolean shouldLogProfile() {
        return shouldLogProfile;
    }

    /**
     * INTERNAL:
     * Start the operation timing.
     */
    public void startOperationProfile(String operationName) {
        getOperationStartTimes().put(operationName, Long.valueOf(System.currentTimeMillis()));
    }

    /**
     * INTERNAL:
     * Start the operation timing.
     */
    public void startOperationProfile(String operationName, DatabaseQuery query, int weight) {
        startOperationProfile(operationName);
    }

    protected void writeNestingTabs(Writer writer) {
        try {
            for (int index = 0; index < getNestLevel(); index++) {
                writer.write("\t");
            }
        } catch (IOException ioe) {
        }
    }

    //not implemented, for DMSPerformanceProfiler only
    public void update(String operationName, Object value) {
        ;
    }

    public void occurred(String operationName) {
    }

    public void setProfileWeight(int weight) {
    }

    public int getProfileWeight() {
        return -1;
    }

    public void initialize() {
    }
}
