/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.services;

import java.util.*;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.identitymaps.IdentityMap;
import org.eclipse.persistence.internal.identitymaps.HardCacheWeakIdentityMap;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.tools.profiler.*;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.server.*;
import org.eclipse.persistence.logging.SessionLog;

/**
 * <p>
 * <b>Purpose</b>: Provide a dynamic interface into the TopLink Session.
 * <p>
 * <b>Description</b>: This class is ment to provide a framework for gaining access to configuration
 * of the TopLink Session during runtime.  It will provide the basis for developement
 * of a JMX service and possibly other frameworks.
 *
 * @deprecated Will be replaced by a server-specific equivalent for org.eclipse.persistence.services.oc4j.Oc4jRuntimeServices
 * @see org.eclipse.persistence.services.oc4j.Oc4jRuntimeServices
 */
public class RuntimeServices {

    /** stores access to the session object that we are controlling */
    protected AbstractSession session;

    /** Stores state for the interface to report. */
    /**
     * PUBLIC:
     *  Default Constructor
     */
    public RuntimeServices() {
    }

    /**
     *  PUBLIC:
     *  Constructor
     *  @param session the session to be used with these RuntimeServices
     */
    public RuntimeServices(AbstractSession session) {
        this.session = session;
    }

    protected AbstractSession getSession() {
        return this.session;
    }

    /**
     * OBSOLETE:
     * Replaced by setLogLevel(int level);
     * @deprecated
     * @see #setLogLevel(int level)
     */
    public void setShouldLogMessages(boolean shouldLogMessages) {
        if (shouldLogMessages && (getSession().getLogLevel(null) > SessionLog.FINER)) {
            getSession().setLogLevel(SessionLog.FINER);
        } else if (!shouldLogMessages) {
            getSession().setLogLevel(SessionLog.OFF);
        }
    }

    /**
     * PUBLIC:
     * This method is used to determine if logging is turned on
     */
    public boolean getShouldLogMessages() {
        return getSession().shouldLogMessages();
    }

    /**
     * OBSOLETE:
     * Replaced by setLogLevel(int level);
     * @deprecated
     * @see #setLogLevel(int level)
     */
    public void setShouldLogDebug(boolean shouldLogDebug) {
        if (shouldLogDebug) {
            getSession().setLogLevel(SessionLog.FINEST);
        }
    }

    /**
     * OBSOLETE:
     * Replaced by getLogLevel(String category);
     * @deprecated
     * @see #getLogLevel(String category)
     */
    public boolean getShouldLogDebug() {
        if (getSession().getLogLevel(null) > SessionLog.FINEST) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * OBSOLETE:
     * Replaced by setLogLevel(int level);
     * @deprecated
     * @see #setLogLevel(int level)
     */
    public void setShouldLogExceptions(boolean shouldLogExceptions) {
        if (shouldLogExceptions && (getSession().getLogLevel(null) > SessionLog.WARNING)) {
            getSession().setLogLevel(SessionLog.WARNING);
        }
    }

    /**
     * OBSOLETE:
     * Replaced by getLogLevel(String category);
     * @deprecated
     * @see #getLogLevel(String category)
     */
    public boolean getShouldLogExceptions() {
        if (getSession().getLogLevel(null) > SessionLog.WARNING) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * OBSOLETE:
     * @deprecated Stack trace is logged for SEVERE all the time and at FINER level for WARNING or less
     */
    public void setShouldLogExceptionStackTrace(boolean shouldLogExceptionStackTrace) {
        //do nothing
    }

    /**
     * OBSOLETE:
     * @deprecated Stack trace is logged for SEVERE all the time and at FINER level for WARNING or less
     */
    public boolean getShouldLogExceptionStackTrace() {
        return getSession().getLogLevel(null) <= SessionLog.FINER;
    }

    /**
     * OBSOLETE:
     * @deprecated Date is always printed
     */
    public void setShouldPrintDate(boolean shouldPrintDate) {
        //do nothing
    }

    /**
     * OBSOLETE:
     * @deprecated Date is always printed
     */
    public boolean getShouldPrintDate() {
        return true;
    }

    /**
     * OBSOLETE:
     * @deprecated Session is always printed whenever available
     */
    public void setShouldPrintSession(boolean shouldPrintSession) {
        //do nothing
    }

    /**
     * OBSOLETE:
     * @deprecated Session is always printed whenever available
     */
    public boolean getShouldPrintSession() {
        return true;
    }

    /**
     * OBSOLETE:
     * @deprecated Thread is logged at FINE or less level
     */
    public void setShouldPrintThread(boolean shouldPrintThread) {
        //do nothing
    }

    /**
     * OBSOLETE:
     * @deprecated Thread is logged at FINE or less level
     */
    public boolean getShouldPrintThread() {
        return getSession().getLogLevel(null) <= SessionLog.FINE;
    }

    /**
     * OBSOLETE:
     * @deprecated Connection is always printed whenever available
     */
    public void setShouldPrintConnection(boolean shouldPrintConnection) {
        //do nothing
    }

    /**
     * OBSOLETE:
     * @deprecated Connection is always printed whenever available
     */
    public boolean getShouldPrintConnection() {
        return true;
    }

    /**
     * PUBLIC:
     *        This method is used to turn on Performance Profiling
     */
    public void setShouldProfilePerformance(boolean shouldProfile) {
        if (shouldProfile && (getSession().getProfiler() == null)) {
            getSession().setProfiler(new PerformanceProfiler());
        } else if (!shouldProfile) {
            getSession().setProfiler(null);
        }
    }

    /**
     * PUBLIC:
     *     This method will return if profgiling is turned on or not
     */
    public boolean getShouldProfilePerformance() {
        return (getSession().getProfiler() != null) && ClassConstants.PerformanceProfiler_Class.isAssignableFrom(getSession().getProfiler().getClass());
    }

    /**
     * PUBLIC:
     *     This method is used to turn on Profile logging when using th Performance Profiler
     */
    public void setShouldLogPerformanceProfiler(boolean shouldLogPerformanceProfiler) {
        if ((getSession().getProfiler() != null) && ClassConstants.PerformanceProfiler_Class.isAssignableFrom(getSession().getProfiler().getClass())) {
            ((PerformanceProfiler)getSession().getProfiler()).setShouldLogProfile(shouldLogPerformanceProfiler);
        }
    }

    /**
     * PUBLIC:
     *     Method indicates if Performace profile should be loged
     */
    public boolean getShouldLogPerformanceProfiler() {
        if ((getSession().getProfiler() != null) && ClassConstants.PerformanceProfiler_Class.isAssignableFrom(getSession().getProfiler().getClass())) {
            return ((PerformanceProfiler)getSession().getProfiler()).shouldLogProfile();
        }
        return false;
    }

    /**
     * PUBLIC:
     *     Method used to set if statements should be cached.  Please note that Statements can not be cached when
     * using an external connection pool
     */
    public void setShouldCacheAllStatements(boolean shouldCacheAllStatements) {
        if (!(getSession().getDatasourceLogin() instanceof DatabaseLogin)) {
            return;
        }
        ((DatabaseLogin)getSession().getDatasourceLogin()).setShouldCacheAllStatements(shouldCacheAllStatements);
    }

    /**
     * PUBLIC:
     *     Returns if statements should be cached or not
     */
    public boolean getShouldCacheAllStatements() {
        if (!(getSession().getDatasourceLogin() instanceof DatabaseLogin)) {
            return false;
        }
        return ((DatabaseLogin)getSession().getDatasourceLogin()).shouldCacheAllStatements();
    }

    /**
     * PUBLIC:
     *     Used to set the statement cache size.  This is only valid if using cached Statements
     */
    public void setStatementCacheSize(int size) {
        if (!(getSession().getDatasourceLogin() instanceof DatabaseLogin)) {
            return;
        }
        ((DatabaseLogin)getSession().getDatasourceLogin()).setStatementCacheSize(size);
    }

    /**
     * PUBLIC:
     *        Returns the statement cache size.  Only valid if statements are being cached
     */
    public int getStatementCacheSize() {
        if (!(getSession().getDatasourceLogin() instanceof DatabaseLogin)) {
            return 0;
        }
        return ((DatabaseLogin)getSession().getDatasourceLogin()).getStatementCacheSize();
    }

    /**
     * PUBLIC:
     * This method provide access for setting the sequence pre-allocation size
     */
    public void setSequencePreallocationSize(int size) {
        if (!(getSession().getDatasourceLogin() instanceof DatabaseLogin)) {
            return;
        }
        ((DatabaseLogin)getSession().getDatasourceLogin()).setSequencePreallocationSize(size);
    }

    /**
     * PUBLIC:
     *        Method returns the value of the Sequence Preallocation size
     */
    public int getSequencePreallocationSize() {
        if (!(getSession().getDatasourceLogin() instanceof DatabaseLogin)) {
            return 0;
        }
        return ((DatabaseLogin)getSession().getDatasourceLogin()).getSequencePreallocationSize();
    }

    /**
     * PUBLIC:
     *     This method allows the client to set the pool size for a particular pool, based on the pool name
     * @param poolName the name of the pool to be updated.
     * @param maxSize the new maximum number of connections
     * @param minSize the new minimum number of connections
     */
    public void updatePoolSize(String poolName, int maxSize, int minSize) {
        if (ClassConstants.ServerSession_Class.isAssignableFrom(getSession().getClass())) {
            ConnectionPool connectionPool = ((ServerSession)getSession()).getConnectionPool(poolName);
            if (connectionPool != null) {
                connectionPool.setMaxNumberOfConnections(maxSize);
                connectionPool.setMinNumberOfConnections(minSize);
            }
        }
    }

    /**
     * PUBLIC:
     *     This method will return the available Connection pools within this Server Session
     * @return java.util.List the available pools.
     */
    public List getAvailableConnectionPools() {
        Vector list = null;
        if (ClassConstants.ServerSession_Class.isAssignableFrom(getSession().getClass())) {
            Map pools = ((ServerSession)getSession()).getConnectionPools();
            list = new Vector(pools.size());
            Iterator poolNames = pools.keySet().iterator();
            while (poolNames.hasNext()) {
                list.add(poolNames.next());
            }
        } else {
            list = new Vector();
        }
        return list;
    }

    /**
     * PUBLIC:
     *     This method will retrieve the size of a particular connection pool
     * @param poolName the name of the pool to get the size for
     * @return java.util.List a list containing two values. The first value is the Maximun size of the pool.
     * The second value is the Minimum size of the pool.
     */
    public List getSizeForPool(String poolName) {
        Vector results = new Vector(2);
        if (ClassConstants.ServerSession_Class.isAssignableFrom(getSession().getClass())) {
            ConnectionPool connectionPool = ((ServerSession)getSession()).getConnectionPool(poolName);
            if (connectionPool != null) {
                results.add(new Integer(connectionPool.getMaxNumberOfConnections()));
                results.add(new Integer(connectionPool.getMinNumberOfConnections()));
            }
        }
        return results;
    }

    /**
     * OBSOLETE:
     * This method provides client with access to add a new connection pool to a TopLink
     * ServerSession.
     * @param poolName the name of the new pool
     * @param maxSize the maximum number of connections in the pool
     * @param minSize the minimum number of connections in the pool
     * @param platform the fully qualified name of the TopLink platform to use with this pool.
     * @param driverClassName the fully qualified name of the JDBC driver class
     * @param url the URL of the database to connect to
     * @param userName the user name to connect to the database with
     * @param password the password to connect to the database with
     * @param licencePath the path to the TopLink license file.
     * @exception ClassNotFoundException if any of the class names are mispelled.
     * @deprecated use addNewConnectionPool(String poolName, int maxSize, int minSize, String platform, String driverClassName, String url, String userName, String password) throws ClassNotFoundException instead.
     */
    public void addNewConnectionPool(String poolName, int maxSize, int minSize, String platform, String driverClassName, String url, String userName, String password, String licencePath) throws ClassNotFoundException {
        addNewConnectionPool(poolName, maxSize, minSize, platform, driverClassName, url, userName, password);
    }

    /**
     * PUBLIC:
     * This method provides client with access to add a new connection pool to a TopLink
     * ServerSession.
     * @param poolName the name of the new pool
     * @param maxSize the maximum number of connections in the pool
     * @param minSize the minimum number of connections in the pool
     * @param platform the fully qualified name of the TopLink platform to use with this pool.
     * @param driverClassName the fully qualified name of the JDBC driver class
     * @param url the URL of the database to connect to
     * @param userName the user name to connect to the database with
     * @param password the password to connect to the database with
     * @exception ClassNotFoundException if any of the class names are mispelled.
     */
    public void addNewConnectionPool(String poolName, int maxSize, int minSize, String platform, String driverClassName, String url, String userName, String password) throws ClassNotFoundException {
        if (ClassConstants.ServerSession_Class.isAssignableFrom(getSession().getClass())) {
            DatabaseLogin login = new DatabaseLogin();
            login.setPlatformClassName(platform);
            login.setDriverClassName(driverClassName);
            login.setConnectionString(url);
            login.setUserName(userName);
            login.setEncryptedPassword(password);
            ((ServerSession)getSession()).addConnectionPool(poolName, login, minSize, maxSize);
        }
    }

    /**
     * PUBLIC:
     * This method is used to reset connections from the session to the database.  Please
     * Note that this will not work with a SessionBroker at this time
     */
    public void resetAllConnections() {
        if (ClassConstants.ServerSession_Class.isAssignableFrom(getSession().getClass())) {
            Iterator enumtr = ((ServerSession)getSession()).getConnectionPools().values().iterator();
            while (enumtr.hasNext()) {
                ConnectionPool pool = (ConnectionPool)enumtr.next();
                pool.shutDown();
                pool.startUp();
            }
        } else if (ClassConstants.PublicInterfaceDatabaseSession_Class.isAssignableFrom(getSession().getClass())) {
            getSession().getAccessor().reestablishConnection(getSession());
        }
    }

    /**
     * PUBLIC:
     *        This method is used to return those Class Names that have identity Maps in the Session.
     * Please note that SubClasses and aggregates will be missing form this list as they do not have
     * separate identity maps.
     * @return java.util.List contains all of the classes which have identity maps in the current session.
     */
    public List getClassesInSession() {
        return getSession().getIdentityMapAccessorInstance().getIdentityMapManager().getClassesRegistered();
    }

    /**
     * PUBLIC:
     * This method will return a collection of the objects in the Identity Map.
     * There is no particular order to these objects.
     * @param className the fully qualified classname of the class to the instances of
     * @exception  thrown then the IdentityMap for that class name could not be found
     */
    public List getObjectsInIdentityMap(String className) throws ClassNotFoundException {
        Class classToChange = (Class)getSession().getDatasourcePlatform().getConversionManager().convertObject(className, ClassConstants.CLASS);
        IdentityMap map = getSession().getIdentityMapAccessorInstance().getIdentityMap(classToChange);

        Vector results = new Vector(map.getSize());
        Enumeration objects = map.keys();
        while (objects.hasMoreElements()) {
            results.add(((org.eclipse.persistence.internal.identitymaps.CacheKey)objects.nextElement()).getObject());
        }
        return results;
    }

    /**
     * PUBLIC:
     *        This method is used to return the number of objects in a particular Identity Map
     * @param className the fully qualified name of the class to get number of instances of.
     * @exception  thrown then the IdentityMap for that class name could not be found
     */
    public Integer getNumberOfObjectsInIdentityMap(String className) throws ClassNotFoundException {
        Class classToChange = (Class)getSession().getDatasourcePlatform().getConversionManager().convertObject(className, ClassConstants.CLASS);
        return new Integer(getSession().getIdentityMapAccessorInstance().getIdentityMap(classToChange).getSize());
    }

    /**
     * PUBLIC:
     * This method is used to return a collection of the objects in a particular Identity Map's
     * subcache.  Only works for those identity Maps with a sub cache (ie Hard Cache Weak Identity Map)
     * @deprecated
     * @see    getObjectsInIdentityMapSubCacheAsMap(String className)
     * @param className the fully qualified name of the class to get number of instances of.
     * @exception  thrown then the IdentityMap for that class name could not be found
     */
    public List getObjectsInIdentityMapSubCache(String className) throws ClassNotFoundException {
        return getObjectsInIdentityMapSubCacheAsMap(className);
    }

    /**
     * PUBLIC:
     * This method is used to return a Map of the objects in a particular Identity Map's
     * subcache.  Only works for those identity Maps with a sub cache (ie Hard Cache Weak Identity Map)
     * This method replaces getObjectsInIdentityMapSubCache(className) which returns a list instead
     * of a Map
     * @param className the fully qualified name of the class to get number of instances of.
     * @exception  thrown then the IdentityMap for that class name could not be found
     */
    public List getObjectsInIdentityMapSubCacheAsMap(String className) throws ClassNotFoundException {
        Class classToChange = (Class)getSession().getDatasourcePlatform().getConversionManager().convertObject(className, ClassConstants.CLASS);
        IdentityMap map = getSession().getIdentityMapAccessorInstance().getIdentityMap(classToChange);
        Vector results = new Vector(1);

        //CR3855
        List subCache = new ArrayList(0);
        if (ClassConstants.HardCacheWeakIdentityMap_Class.isAssignableFrom(map.getClass())) {
            subCache = ((HardCacheWeakIdentityMap)map).getReferenceCache();
        }
        return subCache;
    }

    /**
     * PUBLIC:
     * This method is used to return the number of objects in a particular Identity Map's
     * subcache.  Only works for those identity Maps with a sub cache (ie Hard Cache Weak Identity Map)
     * @param className the fully qualified name of the class to get number of instances of.
     * @exception  thrown then the IdentityMap for that class name could not be found
     */
    public Integer getNumberOfObjectsInIdentityMapSubCache(String className) throws ClassNotFoundException {
        //This needs to use the Session's active class loader (not implemented yet)
        Integer result = new Integer(0);
        Class classToChange = (Class)getSession().getDatasourcePlatform().getConversionManager().convertObject(className, ClassConstants.CLASS);
        IdentityMap map = getSession().getIdentityMapAccessorInstance().getIdentityMap(classToChange);
        if (map.getClass().isAssignableFrom(ClassConstants.HardCacheWeakIdentityMap_Class)) {
            List subCache = ((HardCacheWeakIdentityMap)map).getReferenceCache();
            result = new Integer(subCache.size());
        }
        return result;
    }

    /**
     * PUBLIC:
     * <p>
     * Return the log level
     * </p><p>
     *
     * @return the log level
     * </p><p>
     * @param category  the string representation of a TopLink category, e.g. "sql", "transaction" ...
     * </p>
     */
    public int getLogLevel(String category) {
        return getSession().getLogLevel(category);
    }

    /**
     * PUBLIC:
     * <p>
     * Set the log level
     * </p><p>
     *
     * @param level     the new log level
     * @param category  the string representation of a TopLink category.
     * </p>
     */
    public void setLogLevel(int level) {
        getSession().setLogLevel(level);
    }

    /**
     * PUBLIC:
     * <p>
     * Check if a message of the given level would actually be logged.
     * </p><p>
     *
     * @return true if the given message level will be logged
     * </p><p>
     * @param level  the log request level
     * @param category  the string representation of a TopLink category
     * </p>
     */
    public boolean shouldLog(int Level, String category) {
        return getSession().shouldLog(Level, category);
    }

    /**
     * PUBLIC:
     *    This method is used to change DMS sensor weight.
     */
    public void setProfileWeight(int weight) {
        if (getSession().isInProfile()) {
            getSession().getProfiler().setProfileWeight(weight);
        }
    }
}
