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
package org.eclipse.persistence.platform.server;

import java.sql.SQLException;

import javax.persistence.spi.PersistenceUnitInfo;

import org.eclipse.persistence.internal.databaseaccess.Accessor;
import org.eclipse.persistence.internal.helper.JPAClassLoaderHolder;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.DatabaseSession;

/**
 * PUBLIC:
 *
 * This is the interface describing the behavior for ServerPlatformBase, and any other
 * class that wants to provide behavior for a server.
 *
 * This interface provides the behavior for
 *
 * - Which external transaction controller to use
 * - Whether or not to enable JTA (external transaction control)
 * - How to register/unregister for runtime services (JMX/MBean)
 * - Whether or not to enable runtime services
 * - How to launch container Threads
 *
 * Any subclasses of ServerPlatformBase created by the user must implement this interface.
 *
 * public API:
 *
 *  String getServerNameAndVersion()
 *
 * @see ServerPlatformBase
 */
public interface ServerPlatform {

    /**
     * INTERNAL: getDatabaseSession(): Answer the instance of DatabaseSession the receiver is helping.
     *
     * @return DatabaseSession
     */
    public abstract DatabaseSession getDatabaseSession();

    /**
     * PUBLIC: getServerNameAndVersion(): Talk to the relevant server class library, and get the server name
     * and version
     *
     * @return String serverNameAndVersion
     */
    public abstract String getServerNameAndVersion();

    /**
     * INTERNAL: getModuleName(): Answer the name of the module (jar name) that my session
       * is associated with.
       * Answer "unknown" if there is no module name available.
     *
     * @return String moduleName
     */
    public abstract String getModuleName();

    /**
     * INTERNAL: getExternalTransactionControllerClass(): Answer the class of external transaction controller to use
     * For this server platform. This is read-only.
     *
     * If the subclasses of the ServerPlatformBase do not provide the Class desired, then
     * a new subclass should be created to return the desired class.
     *
     * @return Class externalTransactionControllerClass
     *
     * @see org.eclipse.persistence.transaction.JTATransactionController
     * @see #isJTAEnabled()
     * @see #disableJTA()
     * @see #initializeExternalTransactionController()
     */
    public abstract Class getExternalTransactionControllerClass();

    /**
     * INTERNAL: setExternalTransactionControllerClass(Class newClass): Set the class of external
     * transaction controller to use in the DatabaseSession.
     * This is defined by the user via the sessions.xml.
     *
     * @see org.eclipse.persistence.transaction.JTATransactionController
     * @see #isJTAEnabled()
     * @see #disableJTA()
     * @see #initializeExternalTransactionController()
     */
    public void setExternalTransactionControllerClass(Class newClass);
    
    /**
     * INTERNAL: initializeExternalTransactionController(): Populate the DatabaseSession's
     * external transaction controller with an instance of my transaction controller class.
     *
     * To change the external transaction controller class, we recommend creating a subclass of
       * ServerPlatformBase, and overriding getExternalTransactionControllerClass()
       *
       * @see ServerPlatformBase
     *
     * @return void
     *
     */
    public abstract void initializeExternalTransactionController();

    /**
     * INTERNAL: isJTAEnabled(): Answer true if the DatabaseSession's external transaction controller class will
     * be populated with my transaction controller class at runtime. If the transaction controller class is
     * overridden in the DatabaseSession, my transaction controller class will be ignored.
     *
     * @return boolean isJTAEnabled
     * @see #getExternalTransactionControllerClass()
     * @see #disableJTA()
     */
    public abstract boolean isJTAEnabled();

    /**
     * INTERNAL: disableJTA(): Configure the receiver such that my external transaction controller class will
     * be ignored, and will NOT be used to populate DatabaseSession's external transaction controller class
     * at runtime.
     *
     * @return void
     * @see #getExternalTransactionControllerClass()
     * @see #isJTAEnabled()
     */
    public abstract void disableJTA();

    /**
     * INTERNAL: isRuntimeServicesEnabled(): Answer true if the JMX/MBean providing runtime services for
     * the receiver's DatabaseSession will be deployed at runtime.
     *
     * @return boolean isRuntimeServicesEnabled
     * @see #disableRuntimeServices()
     */
    public abstract boolean isRuntimeServicesEnabled();

    /**
     * INTERNAL: disableRuntimeServices(): Configure the receiver such that no JMX/MBean will be registered
     * to provide runtime services for my DatabaseSession at runtime.
     *
     * @return void
     * @see #isRuntimeServicesEnabled()
     */
    public abstract void disableRuntimeServices();

    /**
     * INTERNAL: registerMBean(): Create and deploy the JMX MBean to provide runtime services for my
     * databaseSession.
     *
     * @return void
     * @see #isRuntimeServicesEnabled()
     * @see #disableRuntimeServices()
     * @see #unregisterMBean()
     */
    public abstract void registerMBean();

    /**
     * INTERNAL: unregisterMBean(): Unregister the JMX MBean that was providing runtime services for my
     * databaseSession.
     *
     * @return void
     * @see #isRuntimeServicesEnabled()
     * @see #disableRuntimeServices()
     * @see #registerMBean()
     */
    public abstract void unregisterMBean();

    /**
     * INTERNAL:  This method is used to unwrap the oracle connection wrapped by
     * the application server.  TopLink needs this unwrapped connection for certain
     * database vendor specific support.
     * This is added as a workaround for bug 4460996
     */
    public java.sql.Connection unwrapConnection(java.sql.Connection connection);
    
    /**
     * INTERNAL: launchContainerRunnable(Runnable runnable): Use the container library to
     * start the provided Runnable.
     *
     * Default behavior is to use Thread(runnable).start()
     *
     * @param Runnable runnable: the instance of runnable to be "started"
     * @return void
     */
    public void launchContainerRunnable(Runnable runnable);

    /**
     * INTERNAL: getServerLog(): Return the ServerLog for this platform
     *
     * Return the default ServerLog in the base
     *
     * @return org.eclipse.persistence.logging.SessionLog
     */
    public org.eclipse.persistence.logging.SessionLog getServerLog();

    /**
     * INTERNAL: shouldUseDriverManager(): Indicates whether DriverManager should be used while connecting DefaultConnector.
     *
     * @return boolean 
     */
    public boolean shouldUseDriverManager();

    /**
     * INTERNAL:
     * A call to this method will perform a platform based check on the connection and exception
     * error code to determine if the connection is still valid or if a communication error has occurred.
     * In the case of the server platform the connection pool itself may be tested.
     * If a communication error has occurred then the query may be retried.
     * If this platform is unable to determine if the error was communication based it will return
     * false forcing the error to be thrown to the user.
     */
    
    public boolean wasFailureCommunicationBased(SQLException exception, Accessor connection, AbstractSession sessionForProfile);
    
    /**
     * INTERNAL:
     * JIRA EJBTHREE-572 requires that we use the real classLoader in place of the getNewTempClassLoader().
     * The override code should stay in place until the UCL3 loader does not throw a NPE on loadClass()
     * 
     * @param puInfo - the persistence unit info
     * @return ClassLoaderHolder - a composite object containing the classLoader and the flag
     *     that is true if the classLoader returned is temporary
     *     
     *  @see org.eclipse.persistence.internal.helper.ClassLoaderHolder
     */
    public JPAClassLoaderHolder getNewTempClassLoader(PersistenceUnitInfo puInfo);
    
    /**
     * INTERNAL:
     * Clears statement cache of the wrapper connection.
     * Required by Oracle proxy authentication: currently connection statement cache
     * becomes invalid on switching to/from proxy session.
     * This method is called by OracleJDBC_10_1_0_2ProxyConnectionCustomizer  
     * before opening proxy session and before closing it.
     */
    public void clearStatementCache(java.sql.Connection connection);
}
