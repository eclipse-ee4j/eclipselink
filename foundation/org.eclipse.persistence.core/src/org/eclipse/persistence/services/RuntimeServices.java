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
 *     @author  mobrien
 *     @since   EclipseLink 1.0 enh# 235168
 ******************************************************************************/  
package org.eclipse.persistence.services;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.eclipse.persistence.internal.databaseaccess.DatasourcePlatform;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.internal.identitymaps.HardCacheWeakIdentityMap;
import org.eclipse.persistence.internal.identitymaps.IdentityMap;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.server.ConnectionPool;
import org.eclipse.persistence.sessions.server.ServerSession;
import org.eclipse.persistence.tools.profiler.PerformanceProfiler;


/**
 * <p>
 * <b>Purpose</b>: Provide a dynamic interface into the EclipseLink Session.
 * <p>
 * <b>Description</b>: This class is meant to provide a framework for gaining access to configuration
 * of the EclipseLink Session during runtime.  It will provide the basis for development
 * of a JMX service and possibly other frameworks.
 *
 */
public class RuntimeServices {

    /** stores access to the session object that we are controlling */
    protected Session session;

    /**
     *  Default Constructor
     */
    public RuntimeServices() {
    }

    /**
     *  Constructor
     *  @param session the session to be used with these RuntimeServices
     */
    public RuntimeServices(Session session) {
        this.session = session;
    }

    /**
     * INTERNAL:
     */
    protected AbstractSession getSession() {
        return (AbstractSession)this.session;
    }

    /**
     * This method is used to determine if logging is turned on
     */
    public boolean getShouldLogMessages() {
        return getSession().shouldLogMessages();
    }

    /**
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
     *     This method will return if profiling is turned on or not
     */
    public boolean getShouldProfilePerformance() {
        return (getSession().getProfiler() != null) && ClassConstants.PerformanceProfiler_Class.isAssignableFrom(getSession().getProfiler().getClass());
    }

    /**
     *     This method is used to turn on Profile logging when using the Performance Profiler
     */
    public void setShouldLogPerformanceProfiler(boolean shouldLogPerformanceProfiler) {
        if ((getSession().getProfiler() != null) && ClassConstants.PerformanceProfiler_Class.isAssignableFrom(getSession().getProfiler().getClass())) {
            ((PerformanceProfiler)getSession().getProfiler()).setShouldLogProfile(shouldLogPerformanceProfiler);
        }
    }

    /**
     *     Method indicates if Performance profile should be logged
     */
    public boolean getShouldLogPerformanceProfiler() {
        if ((getSession().getProfiler() != null) && ClassConstants.PerformanceProfiler_Class.isAssignableFrom(getSession().getProfiler().getClass())) {
            return ((PerformanceProfiler)getSession().getProfiler()).shouldLogProfile();
        }
        return false;
    }

    /**
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
     *     Returns if statements should be cached or not
     */
    public boolean getShouldCacheAllStatements() {
        if (!(getSession().getDatasourceLogin() instanceof DatabaseLogin)) {
            return false;
        }
        return ((DatabaseLogin)getSession().getDatasourceLogin()).shouldCacheAllStatements();
    }

    /**
     *     Used to set the statement cache size.  This is only valid if using cached Statements
     */
    public void setStatementCacheSize(int size) {
        if (!(getSession().getDatasourceLogin() instanceof DatabaseLogin)) {
            return;
        }
        ((DatabaseLogin)getSession().getDatasourceLogin()).setStatementCacheSize(size);
    }

    /**
     *        Returns the statement cache size.  Only valid if statements are being cached
     */
    public int getStatementCacheSize() {
        if (!(getSession().getDatasourceLogin() instanceof DatabaseLogin)) {
            return 0;
        }
        return ((DatabaseLogin)getSession().getDatasourceLogin()).getStatementCacheSize();
    }

    /**
     * This method provide access for setting the sequence pre-allocation size
     */
    public void setSequencePreallocationSize(int size) {
        if (!(getSession().getDatasourceLogin() instanceof DatabaseLogin)) {
            return;
        }
        ((DatasourcePlatform)getSession().getDatasourcePlatform()).setSequencePreallocationSize(size);
    }

    /**
     *        Method returns the value of the Sequence Preallocation size
     */
    public int getSequencePreallocationSize() {
        if (!(getSession().getDatasourceLogin() instanceof DatabaseLogin)) {
            return 0;
        }
        return ((DatasourcePlatform)getSession().getDatasourcePlatform()).getSequencePreallocationSize();
    }

    /**
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
     * This method provides client with access to add a new connection pool to a EclipseLink
     * ServerSession.
     * @param poolName the name of the new pool
     * @param maxSize the maximum number of connections in the pool
     * @param minSize the minimum number of connections in the pool
     * @param platform the fully qualified name of the EclipseLink platform to use with this pool.
     * @param driverClassName the fully qualified name of the JDBC driver class
     * @param url the URL of the database to connect to
     * @param userName the user name to connect to the database with
     * @param password the password to connect to the database with
     * @exception ClassNotFoundException if any of the class names are misspelled.
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
     *        This method is used to return those Class Names that have identity Maps in the Session.
     * Please note that SubClasses and aggregates will be missing form this list as they do not have
     * separate identity maps.
     * @return java.util.List contains all of the classes which have identity maps in the current session.
     */
    public List getClassesInSession() {
        return getSession().getIdentityMapAccessorInstance().getIdentityMapManager().getClassesRegistered();
    }

    /**
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
            results.add(((CacheKey)objects.nextElement()).getObject());
        }
        return results;
    }

    /**
     *        This method is used to return the number of objects in a particular Identity Map
     * @param className the fully qualified name of the class to get number of instances of.
     * @exception  thrown then the IdentityMap for that class name could not be found
     */
    public Integer getNumberOfObjectsInIdentityMap(String className) throws ClassNotFoundException {
        Class classToChange = (Class)getSession().getDatasourcePlatform().getConversionManager().convertObject(className, ClassConstants.CLASS);
        return new Integer(getSession().getIdentityMapAccessorInstance().getIdentityMap(classToChange).getSize());
    }

    /**
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

        //CR3855
        List subCache = new ArrayList(0);
        if (ClassConstants.HardCacheWeakIdentityMap_Class.isAssignableFrom(map.getClass())) {
            subCache = ((HardCacheWeakIdentityMap)map).getReferenceCache();
        }
        return subCache;
    }

    /**
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
     * <p>
     * Return the log level
     * </p><p>
     *
     * @return the log level
     * </p><p>
     * @param category  the string representation of an EclipseLink category, e.g. "sql", "transaction" ...
     * </p>
     */
    public int getLogLevel(String category) {
        return getSession().getLogLevel(category);
    }

    /**
     * <p>
     * Set the log level
     * </p><p>
     *
     * @param level     the new log level
     * </p>
     */
    public void setLogLevel(int level) {
        getSession().setLogLevel(level);
    }

    /**
     * <p>
     * Check if a message of the given level would actually be logged.
     * </p><p>
     *
     * @return true if the given message level will be logged
     * </p><p>
     * @param level  the log request level
     * @param category  the string representation of an EclipseLink category
     * </p>
     */
    public boolean shouldLog(int Level, String category) {
        return getSession().shouldLog(Level, category);
    }

    /**
     * Return the DMS sensor weight
     * @return
     */
    public int getProfileWeight() {
        if (getSession().isInProfile()) {
            return getSession().getProfiler().getProfileWeight();
        } else {
            return 0;
        }
    }
    
    /**
     *    This method is used to change DMS sensor weight.
     */
    public void setProfileWeight(int weight) {
        if (getSession().isInProfile()) {
            getSession().getProfiler().setProfileWeight(weight);
        }
    }
}
