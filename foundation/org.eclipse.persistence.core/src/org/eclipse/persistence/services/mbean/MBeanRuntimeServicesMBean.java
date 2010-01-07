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
package org.eclipse.persistence.services.mbean;

import java.util.List;

/**
 * <p>
 * <b>Purpose</b>: Provide a dynamic interface into the EclipseLink Session.
 * <p>
 * <b>Description</b>: This class is meant to provide an interface for gaining access to configuration
 * of the EclipseLink Session during runtime.  It provides the basis for a JMX MBean
 *
 */
public interface MBeanRuntimeServicesMBean {

    /**
     *     This method is used to determine if messages should be logged by the session
     */
    public boolean getShouldLogMessages();

    /**
     *        This method is used to turn on Performance Profiling
     */
    public void setShouldProfilePerformance(boolean shouldProfile);

    /**
     *     This method will return if profiling is turned on or not
     */
    public boolean getShouldProfilePerformance();

    /**
     *     This method is used to turn on Profile logging when using the Performance Profiler
     */
    public void setShouldLogPerformanceProfiler(boolean shouldLogPerformanceProfiler);

    /**
     *     This method is used to determine if we should be logging when using the Performance Profiler
     * @return
     */
    public boolean getShouldLogPerformanceProfiler();

    /**
     *     Method used to set if statements should be cached.  Please note that Statements can not be cached when
     * using an external connection pool
     */
    public void setShouldCacheAllStatements(boolean shouldCacheAllStatements);

    /**
     *     Returns if statements should be cached or not
     */
    public boolean getShouldCacheAllStatements();

    /**
     *     Used to set the statement cache size.  This is only valid if using cached Statements
     */
    public void setStatementCacheSize(int size);

    /**
     *        Returns the statement cache size.  Only valid if statements are being cached
     */
    public int getStatementCacheSize();

    /**
     *     This method provides access for setting the sequence pre-allocation size
     */
    public void setSequencePreallocationSize(int size);

    /**
     *        Method returns the value of the Sequence Preallocation size
     */
    public int getSequencePreallocationSize();

    /**
     *     This method allows the client to set the pool size for a particular pool, based on the pool name
     */
    public void updatePoolSize(String poolName, int maxSize, int minSize);

    /**
     *     This method will return the available Connection pools within this Server Session
     */
    public List getAvailableConnectionPools();

    /**
     *     This method will retrieve the size of a particular connection pool
     */
    public List getSizeForPool(String poolName);


    /**
     *        This method provides client with access to add a new connection pool to a TopLink
     * ServerSession.  This method throws classNotFound Exception if any of the class names are misspelled.
     */
    public void addNewConnectionPool(String poolName, int maxSize, int minSize, String platform, String driverClassName, String url, String userName, String password) throws ClassNotFoundException;

    /**
     *        This method is used to reset connections from the session to the database.  Please
     * Note that this will not work with a SessionBroker at this time
     */
    public void resetAllConnections();

    /**
     *        This method is used to return those Class Names that have identity Maps in the Session.
     * Please note that SubClasses and aggregates will be missing from this list as they do not have
     * separate identity maps.
     */
    public List getClassesInSession();

    /**
     *        This method will return a collection of the objects in the Identity Map.
     * There is no particular order to these objects.
     */
    public List getObjectsInIdentityMap(String className) throws ClassNotFoundException;

    /**
     *        This method is used to return the number of objects in a particular Identity Map
     * @param className the fully qualified name of the class to get number of instances of.
     * @exception If ClassNotFoundException is thrown then the IdentityMap for that class name could not be found
     */
    public Integer getNumberOfObjectsInIdentityMap(String className) throws ClassNotFoundException;

    /**
    *        This method will return a collection of the objects in the Identity Map.
    * There is no particular order to these objects.  These objects are returned as a Map
    * which is how they are stored on the cache.  This method replaces getObjectsInIdentityMapSubCache(String className)
    * which returned a List.
    *
    */
    public List getObjectsInIdentityMapSubCacheAsMap(String className) throws ClassNotFoundException;

    /**
     *        This method is used to return the number of objects in a particular Identity Map's
     * subcache.  Only works for those identity Maps with a sub cache (IE Hard Cache Weak Identity Map)
     * If ClassNotFoundException is thrown then the IdenityMap for that class name could not be found
     */
    public Integer getNumberOfObjectsInIdentityMapSubCache(String className) throws ClassNotFoundException;
    
    /**
     * Return the DMS sensor weight
     * @return
     */
    public int getProfileWeight();

    /**
     * Set the DMS sensor weight
     */
    public void setProfileWeight(int size);

}