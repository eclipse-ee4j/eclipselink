/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package deprecated.services.mbean;

import java.util.List;

/**
 * <p>
 * <b>Purpose</b>: Provide a dynamic interface into the TopLink Session.
 * <p>
 * <b>Description</b>: This class is ment to provide an interface for gaining access to configuration
 * of the TopLink Session during runtime.  It will provides the basis for a JMX MBean
 *
 * @deprecated Will be replaced by a server-specific equivalent for deprecated.services.oc4j.Oc4jRuntimeServices
 * @see deprecated.services.oc4j.Oc4jRuntimeServices
 */
public interface MBeanRuntimeServicesMBean {

    /**
     * PUBLIC:
     *     This method is used to set if messages should be logged by the session
     */
    public void setShouldLogMessages(boolean shouldLogMessages);

    /**
     * PUBLIC:
     *     This method is used to determine if messages should be logged by the session
     */
    public boolean getShouldLogMessages();

    /**
     * PUBLIC:
     *     This method is used to set if debug messages should be logged
     */
    public void setShouldLogDebug(boolean shouldLogDebug);

    /**
     * PUBLIC:
     *     This method is used to determine if debug messages should be logged
     */
    public boolean getShouldLogDebug();

    /**
     * PUBLIC:
     *     This method is used to set if Exception messages should be logged
     */
    public void setShouldLogExceptions(boolean shouldLogExceptions);

    /**
     * PUBLIC:
     *     This method is used to determine if exception messages should be logged
     */
    public boolean getShouldLogExceptions();

    /**
     * PUBLIC:
     *     This method is used to set if Exception Stack Trace should be logged
     */
    public void setShouldLogExceptionStackTrace(boolean shouldLogExceptionStackTrace);

    /**
     * PUBLIC:
     *     This method is used to determine if exception Stack Trace should be logged
     */
    public boolean getShouldLogExceptionStackTrace();

    /**
     * PUBLIC:
     *     This method is used to set if Exception Stack Trace should be logged
     */
    public void setShouldPrintDate(boolean shouldPrintDate);

    /**
     * PUBLIC:
     *     This method is used to determine if exception Stack Trace should be logged
     */
    public boolean getShouldPrintDate();

    /**
     * PUBLIC:
     *     This method is used to set if Exception Stack Trace should be logged
     */
    public void setShouldPrintSession(boolean shouldPrintSession);

    /**
     * PUBLIC:
     *     This method is used to determine if exception Stack Trace should be logged
     */
    public boolean getShouldPrintSession();

    /**
     * PUBLIC:
     *     This method is used to set if Exception Stack Trace should be logged
     */
    public void setShouldPrintThread(boolean shouldPrintThread);

    /**
     * PUBLIC:
     *     This method is used to determine if exception Stack Trace should be logged
     */
    public boolean getShouldPrintThread();

    /**
     * PUBLIC:
     *     This method is used to set if Exception Stack Trace should be logged
     */
    public void setShouldPrintConnection(boolean shouldPrintConnection);

    /**
     * PUBLIC:
     *     This method is used to determine if exception Stack Trace should be logged
     */
    public boolean getShouldPrintConnection();

    /**
     * PUBLIC:
     *        This method is used to turn on Performance Profiling
     */
    public void setShouldProfilePerformance(boolean shouldProfile);

    /**
     * PUBLIC:
     *     This method will return if profgiling is turned on or not
     */
    public boolean getShouldProfilePerformance();

    /**
     * PUBLIC:
     *     This method is used to turn on Profile logging when using th Performance Profiler
     */
    public void setShouldLogPerformanceProfiler(boolean shouldLogPerformanceProfiler);

    public boolean getShouldLogPerformanceProfiler();

    /**
     * PUBLIC:
     *     Method used to set if statements should be cached.  Please note that Statements can not be cached when
     * using an external connection pool
     */
    public void setShouldCacheAllStatements(boolean shouldCacheAllStatements);

    /**
     * PUBLIC:
     *     Returns if statements should be cached or not
     */
    public boolean getShouldCacheAllStatements();

    /**
     * PUBLIC:
     *     Used to set the statement cache size.  This is only valid if using cached Statements
     */
    public void setStatementCacheSize(int size);

    /**
     * PUBLIC:
     *        Returns the statement cache size.  Only valid if statements are being cached
     */
    public int getStatementCacheSize();

    /**
     * PUBLIC:
     *     This method provide access for setting the sequence pre-allocation size
     */
    public void setSequencePreallocationSize(int size);

    /**
     * PUBLIC:
     *        Method returns the value of the Sequence Preallocation size
     */
    public int getSequencePreallocationSize();

    /**
     * PUBLIC:
     *     This method allows the client to set the pool size for a particular pool, based on the pool name
     */
    public void updatePoolSize(String poolName, int maxSize, int minSize);

    /**
     * PUBLIC:
     *     This method will return the available Connection pools within this Server Session
     */
    public List getAvailableConnectionPools();

    /**
     * PUBLIC:
     *     This method will retrieve the size of a particulat connection pool
     */
    public List getSizeForPool(String poolName);

    /**
     * OBSOLETE:
     *        This method provides client with access to add a new connection pool to a TopLink
     * ServerSession.  This method throws classNotFound Exception if any of the class names are mispelled.
     * @deprecated
     * @see addNewConnectionPool(String poolName, int maxSize, int minSize, String platform, String driverClassName, String url, String userName, String password) throws ClassNotFoundException
     */
    public void addNewConnectionPool(String poolName, int maxSize, int minSize, String platform, String driverClassName, String url, String userName, String password, String licencePath) throws ClassNotFoundException;

    /**
     * PUBLIC:
     *        This method provides client with access to add a new connection pool to a TopLink
     * ServerSession.  This method throws classNotFound Exception if any of the class names are mispelled.
     */
    public void addNewConnectionPool(String poolName, int maxSize, int minSize, String platform, String driverClassName, String url, String userName, String password) throws ClassNotFoundException;

    /**
     * PUBLIC:
     *        This method is used to reset connections from the session to the database.  Please
     * Note that this will not work with a SessionBroker at this time
     */
    public void resetAllConnections();

    /**
     * PUBLIC:
     *        This method is used to return those Class Names that have identity Maps in the Session.
     * Please note that SubClasses and aggregates will be missing form this list as they do not have
     * seperate identoty maps.
     */
    public List getClassesInSession();

    /**
     * PUBLIC:
     *        This method will return a collection of the objects in the Identity Map.
     * There is no particular order to these objects.
     */
    public List getObjectsInIdentityMap(String className) throws ClassNotFoundException;

    /**
     * PUBLIC:
     *        This method is used to return the number of objects in a particular Identity Map
     * If ClassNotFoundException is thrown then the IdenityMap for that class name could not be found
     */
    public Integer getNumberOfObjectsInIdentityMap(String className) throws ClassNotFoundException;

    /**
    * PUBLIC:
    *        This method will return a collection of the objects in the Identity Map.
    * There is no particular order to these objects.
    * @deprecated
    * @see getObjectsInIdentityMapSubCacheAsMap(String className)
    */
    public List getObjectsInIdentityMapSubCache(String className) throws ClassNotFoundException;

    /**
    * PUBLIC:
    *        This method will return a collection of the objects in the Identity Map.
    * There is no particular order to these objects.  These objects are returned as a Map
    * which is how they are stored on the cache.  This method replaces getObjectsInIdentityMapSubCache(String className)
    * which returned a List.
    *
    */
    public List getObjectsInIdentityMapSubCacheAsMap(String className) throws ClassNotFoundException;

    /**
     * PUBLIC:
     *        This method is used to return the number of objects in a particular Identity Map's
     * subcache.  Only works for those identity Maps with a sub cache (ie Hard Cache Weak Identity Map)
     * If ClassNotFoundException is thrown then the IdenityMap for that class name could not be found
     */
    public Integer getNumberOfObjectsInIdentityMapSubCache(String className) throws ClassNotFoundException;
}