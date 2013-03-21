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

import java.util.Properties;

import org.eclipse.persistence.core.sessions.CoreLogin;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.internal.databaseaccess.Accessor;
import org.eclipse.persistence.internal.databaseaccess.Platform;
import org.eclipse.persistence.platform.database.DatabasePlatform;

/**
 * <p>
 * <b>Purpose</b>: Define the information required to connect to an EclipseLink session.
 * <p>
 * <b>Description</b>: This interface represents a generic concept of a login to be used
 * when connecting to a data-store.  It is independent of JDBC so that the EclipseLink
 * session interface can be used for JCA, XML, non-relational or three-tiered frameworks.
 * <p>
 * @see DatabaseLogin
 */
public interface Login extends CoreLogin<Platform> {

    /**
     * PUBLIC:
     * All logins must take a user name and password.
     */
    String getPassword();

    /**
     * PUBLIC:
     * All logins must take a user name and password.
     */
    String getUserName();

    /**
     * PUBLIC:
     * This value defaults to false when not on a DatabaseLogin as the functionality has not been implemented
     * for other datasource type.  On an SQL Exception EclipseLink will ping the database to determine
     * if the connection used can continue to be used for queries.  This should have no impact on applications
     * unless the user is using pessimistic locking queries with 'no wait' or are using a query timeout feature.
     * If that is the case and the application is experiencing a performance impact from the health check then
     * this feature can be turned off. Turning this feature off will prevent EclipseLink from being able to
     * retry queries in the case of database failure. 
     */
    public boolean isConnectionHealthValidatedOnError();

    /**
     * PUBLIC:
     * All logins must take a user name and password.
     */
    void setPassword(String password);

    /**
     * PUBLIC:
     * All logins must take a user name and password.
     */
    void setUserName(String userName);

    /**
     * PUBLIC:
     * Return whether EclipseLink uses some externally managed connection pooling.
     */
    boolean shouldUseExternalConnectionPooling();

    /**
     * PUBLIC:
     * Return whether EclipseLink uses some externally managed transaction service such as JTS.
     */
    boolean shouldUseExternalTransactionController();

    /**
     * INTERNAL:
     * Return the database platform specific information.
     * This allows EclipseLink to configure certain advanced features for the database desired.
     * The platform also allows configuration of sequence information.
     * NOTE: this must only be used for relational specific usage and will not work for
     * non-relational datasources.
     */
    DatabasePlatform getPlatform();

    /**
     * PUBLIC:
     * Return the datasource platform specific information.
     * This allows EclipseLink to configure certain advanced features for the datasource desired.
     * The platform also allows configuration of sequence information.
     */
    Platform getDatasourcePlatform();

    /**
     * INTERNAL:
     * Set the database platform specific information.
     * This allows EclipseLink to configure certain advanced features for the database desired.
     * The platform also allows configuration of sequence information.
     */
    void setPlatform(Platform platform);

    /**
     * PUBLIC:
     * Set the database platform specific information.
     * This allows EclipseLink to configure certain advanced features for the database desired.
     * The platform also allows configuration of sequence information.
     */
    void setDatasourcePlatform(Platform platform);

    /**
     * INTERNAL:
     * Connect to the datasource, and return the driver level connection object.
     */
    Object connectToDatasource(Accessor accessor, Session session) throws DatabaseException;

    /**
     * INTERNAL:
     * Build the correct datasource Accessor for this login instance.
     */
    Accessor buildAccessor();

    /**
     * INTERNAL:
     * Clone the login.
     */
    Login clone();

    /**
     * PUBLIC:
     * Return the qualifier for the all of the tables.
     */
    public String getTableQualifier();

    /**
     * INTERNAL:
     * Used for cache isolation.
     */
    public boolean shouldAllowConcurrentReadWrite();

    /**
     * INTERNAL:
     * Used for Cache Isolation.  Causes EclipseLink to lock at the class level on
     * cache updates.
     */
    public boolean shouldSynchronizeWrites();
    
    /**
     * INTERNAL:
     * Used for Cache Isolation.  Causes EclipseLink to lock at the object level on
     * cache updates and cache access.
     */
    public boolean shouldSynchronizeObjectLevelReadWrite();
    
    /**
     * INTERNAL:
     * Used for Cache Isolation.  Causes EclipseLink to lock at the object level on
     * cache updates and cache access, based on database transaction.
     */
    public boolean shouldSynchronizeObjectLevelReadWriteDatabase();
    
    /**
     * INTERNAL:
     * Used for cache isolation.
     */
    public boolean shouldSynchronizedReadOnWrite();
    
    /**
     * PUBLIC:
     * The properties are additional, driver-specific, connection information
     * to be passed to the driver.<p>
     * NOTE: Do not set the password directly by getting the properties and
     * setting the "password" property directly. Use the method DatabaseLogin.setPassword(String).
     */
    public Object getProperty(String name);
    
    /**
     * PUBLIC:
     * The properties are additional, driver-specific, connection information
     * to be passed to the JDBC driver.
     */
    public void setProperties(Properties properties);

    /**
     * PUBLIC:
     * Some JDBC drivers require additional, driver-specific, properties.
     * Add the specified property to those to be passed to the JDBC driver.
     */
    public void setProperty(String propertyName, Object propertyValue);
}
