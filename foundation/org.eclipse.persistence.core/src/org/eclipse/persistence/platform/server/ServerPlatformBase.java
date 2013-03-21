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
 *     06/30/2010-2.1.1 Michael O'Brien 
 *       - 316513: Enable JMX MBean functionality for JBoss, Glassfish and WebSphere in addition to WebLogic
 *       Move JMX MBean generic registration code up from specific platforms
 *       add new isRuntimeServicesEnabledDefault()
 *       see <link>http://wiki.eclipse.org/EclipseLink/DesignDocs/316513</link>        
 ******************************************************************************/  
package org.eclipse.persistence.platform.server;

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.persistence.spi.PersistenceUnitInfo;

import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.ExternalTransactionController;
import org.eclipse.persistence.internal.helper.JPAClassLoaderHolder;
import org.eclipse.persistence.internal.localization.ToStringLocalization;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedNewInstanceFromClass;
import org.eclipse.persistence.internal.databaseaccess.Accessor;

/**
 * PUBLIC:
 * Implementation of org.eclipse.persistence.platform.server.ServerPlatform
 * <p>
 * This is the abstract superclass of all platforms for all servers. Each DatabaseSession
 * contains an instance of the receiver, to help the DatabaseSession determine:
 * <p><ul>
 * <li> Which external transaction controller to use
 * <li> Whether or not to enable JTA (external transaction control)
 * <li> How to register/unregister for runtime services (JMX/MBean)
 * <li> Whether or not to enable runtime services
 * <li> How to launch container Threads
 * </ul><p>
 * Subclasses already exist to provide configurations for Oc4J, WebLogic, JBoss, NetWeaver, GlassFish and WebSphere.
 * <p>
 * If the user wants a different external transaction controller class or
 * to provide some different behavior than the provided ServerPlatform(s), we recommend
 * subclassing org.eclipse.persistence.platform.server.ServerPlatformBase (or a subclass),
 * and overriding:
 * <ul>
 * <li>ServerPlatformBase.getExternalTransactionControllerClass()
 * <li>ServerPlatformBase.registerMBean()
 * <li>ServerPlatformBase.unregisterMBean()
 * </ul>
 * for the desired behavior.
 *
 * @see org.eclipse.persistence.platform.server.ServerPlatform
 */
public abstract class ServerPlatformBase implements ServerPlatform {

    // Secondary override properties can be set to disable MBean registration
    /** This System property "eclipselink.register.dev.mbean" when set to true will enable registration/unregistration of the DevelopmentServices MBean */
    public static final String JMX_REGISTER_DEV_MBEAN_PROPERTY = "eclipselink.register.dev.mbean";
    /** This System property "eclipselink.register.run.mbean" when set to true will enable registration/unregistration of the RuntimeServices MBean */    
    public static final String JMX_REGISTER_RUN_MBEAN_PROPERTY = "eclipselink.register.run.mbean";
    /**
     * INTERNAL:
     * Answer "unknown" as a default for platforms that do not implement getModuleName()
     */
    public static final String DEFAULT_SERVER_NAME_AND_VERSION = ToStringLocalization.buildMessage("unknown");
        
    /**
     * INTERNAL:
     * isRuntimeServicesEnabled: Determines if the JMX Runtime Services will be deployed at runtime
     */
    private boolean isRuntimeServicesEnabled;

    // Only a "false" value will disable MBean registration for both beans
    protected boolean shouldRegisterDevelopmentBean = true;
    protected boolean shouldRegisterRuntimeBean = true;
    
    
    /**
     * externalTransactionControllerClass: This is a user-specifiable class defining the class
     * of external transaction controller to be set into the DatabaseSession
     */
    protected Class externalTransactionControllerClass;
	

    /**
     * INTERNAL:
     * isJTAEnabled: Determines if the external transaction controller will be populated into the DatabaseSession
     * at runtime
     */
    private boolean isJTAEnabled;

    
    /**
     * INTERNAL:
     * isCMP: true if the container created the server platform, because we're configured
     * for CMP.
     */
    private boolean isCMP;

    /**
     * INTERNAL:
     * databaseSession: The instance of DatabaseSession that I am helping.
     */
    private DatabaseSession databaseSession;
    
    /**
     * INTERNAL:
     * Server name and version.
     */
    protected String serverNameAndVersion;
    
    /**
     * Allow the thread pool size to be configured.
     */
    protected int threadPoolSize = 32;

    /**
     * Allow pooling of threads for asynchronous processing in RCM and other areas.
     */
    protected volatile ExecutorService threadPool;

    /**
     * INTERNAL:
     * Default Constructor: Initialize so that runtime services and JTA are enabled. Set the DatabaseSession that I
     * will be helping.
     */
    public ServerPlatformBase(DatabaseSession newDatabaseSession) {
        this.isJTAEnabled = true;
        // Default JMX support to false for all sub-platforms except those that override this flag
        this.isRuntimeServicesEnabled = isRuntimeServicesEnabledDefault();
        this.databaseSession = newDatabaseSession;
        this.setIsCMP(false);
        // Enable users to disable or enable (default) MBean registration
        String shouldRegisterRuntimeBeanProperty = System.getProperty(JMX_REGISTER_RUN_MBEAN_PROPERTY);
        if(null != shouldRegisterRuntimeBeanProperty) {
            if(shouldRegisterRuntimeBeanProperty.toLowerCase().indexOf("false") > -1) {
                shouldRegisterRuntimeBean = false;
            }
            if(shouldRegisterRuntimeBeanProperty.toLowerCase().indexOf("true") > -1) {
                shouldRegisterRuntimeBean = true;
            }
        }
        String shouldRegisterDevelopmentBeanProperty = System.getProperty(JMX_REGISTER_DEV_MBEAN_PROPERTY);
        if(null != shouldRegisterDevelopmentBeanProperty) {
            if(shouldRegisterDevelopmentBeanProperty.toLowerCase().indexOf("false") > -1) {
                shouldRegisterDevelopmentBean = false;
            }
            if(shouldRegisterDevelopmentBeanProperty.toLowerCase().indexOf("true") > -1) {
                shouldRegisterDevelopmentBean = true;
            }
        }        
    }
    
    /**
     * INTERNAL: configureProfiler(): set default performance profiler used in this server.
     */
    public void configureProfiler(org.eclipse.persistence.sessions.Session session) {
        return;
    }
    
    /**
     * INTERNAL: getDatabaseSession(): Answer the instance of DatabaseSession the receiver is helping.
     *
     * @return DatabaseSession databaseSession
     */
    public DatabaseSession getDatabaseSession() {
        return this.databaseSession;
    }

    /**
     * PUBLIC: getServerNameAndVersion(): Talk to the relevant server class library, and get the server name
     * and version
     * 
     * @return String serverNameAndVersion
     */
    public String getServerNameAndVersion() {
        if(this.serverNameAndVersion == null) {
            this.initializeServerNameAndVersion();
        }
        return this.serverNameAndVersion;
    }

    /**
     * INTERNAL: initializeServerNameAndVersion(): Talk to the relevant server class library, and get the server name
     * and version
     *
     * Default is "unknown"
     */
    protected void initializeServerNameAndVersion() {
        this.serverNameAndVersion = DEFAULT_SERVER_NAME_AND_VERSION;
    }

    /**
     * INTERNAL: getModuleName(): Answer the name of the module (jar name) that my session
       * is associated with.
       * Answer "unknown" if there is no module name available.
       *
       * Default behavior is to return "unknown".
     *
     * @return String moduleName
     */
    public String getModuleName() {
        return DEFAULT_SERVER_NAME_AND_VERSION;
    }

    /**
     * INTERNAL: getExternalTransactionControllerClass(): Answer the class of external transaction controller to use
     * For this server platform. This is read-only.
     *
       * If the user wants a different external transaction controller class than the provided ServerPlatform(s),
       * we recommend subclassing org.eclipse.persistence.platform.server.ServerPlatformBase (or a subclass),
       * and overriding:
       *
       * ServerPlatformBase.getExternalTransactionControllerClass()
       *
       * for the desired behavior.
     *
     * @return Class externalTransactionControllerClass
     *
     * @see org.eclipse.persistence.transaction.JTATransactionController
     * @see #isJTAEnabled()
     * @see #disableJTA()
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
    public void setExternalTransactionControllerClass(Class newClass) {
        this.externalTransactionControllerClass = newClass;
    }
    
    /**
     * INTERNAL: initializeExternalTransactionController(): Populate the DatabaseSession's
     * external transaction controller with an instance of my transaction controller class.
     *
     * To change the external transaction controller class, we recommend creating a subclass of
     * ServerPlatformBase, and overriding getExternalTransactionControllerClass().
     */
    public void initializeExternalTransactionController() {
        this.ensureNotLoggedIn();

        //BUG 3975114: Even if JTA is disabled, override if we're in CMP
        //JTA must never be disable during CMP (WLS/Oc4j)
        if (!isJTAEnabled() && !isCMP()) {
            return;
        }
        //BUG 3975114: display a warning if JTA is disabled and we're in CMP
        if (!isJTAEnabled() && isCMP()) {
            getDatabaseSession().getSessionLog().log(SessionLog.WARNING, SessionLog.EJB, "jta_cannot_be_disabled_in_cmp", null, true);
        }

        //check if the transaction controller class is overridden by a preLogin or equivalent,
        //or if the transaction controller was already defined, in which case they should have written 
        //a subclass. Show a warning
        try {
            if (getDatabaseSession().getExternalTransactionController() != null) {
                this.externalTransactionControllerNotNullWarning();
                return;
            }
            ExternalTransactionController controller = null;
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                try {
                    controller = (ExternalTransactionController)AccessController.doPrivileged(new PrivilegedNewInstanceFromClass(this.getExternalTransactionControllerClass()));
                } catch (PrivilegedActionException exception) {
                    Exception throwableException = exception.getException();
                    if (throwableException instanceof InstantiationException) {
                        throw ValidationException.cannotCreateExternalTransactionController(getExternalTransactionControllerClass().getName());
                    } else {
                        throw ValidationException.cannotCreateExternalTransactionController(getExternalTransactionControllerClass().getName());
                    }
                }
            } else {
                controller = (ExternalTransactionController)PrivilegedAccessHelper.newInstanceFromClass(this.getExternalTransactionControllerClass());
            }
            getDatabaseSession().setExternalTransactionController(controller);
        } catch (InstantiationException instantiationException) {
            throw ValidationException.cannotCreateExternalTransactionController(getExternalTransactionControllerClass().getName());
        } catch (IllegalAccessException illegalAccessException) {
            throw ValidationException.cannotCreateExternalTransactionController(getExternalTransactionControllerClass().getName());
        }
    }

    /**
     * INTERNAL: externalTransactionControllerNotNullWarning():
     * When the external transaction controller is being initialized, we warn the developer
     * if they have already defined the external transaction controller in some way other
     * than subclassing ServerPlatformBase.
     *
     * @see #getExternalTransactionControllerClass()
     */
    protected void externalTransactionControllerNotNullWarning() {
        ((DatabaseSessionImpl)getDatabaseSession()).warning("External_transaction_controller_not_defined_by_server_platform", SessionLog.EJB);
    }

    /**
     * INTERNAL: isJTAEnabled(): Answer true if the DatabaseSession's external transaction controller class will
     * be populated with my transaction controller class at runtime. If the transaction controller class is
     * overridden in the DatabaseSession, my transaction controller class will be ignored.
     *
     * Answer true if TopLink will be configured to register for callbacks for beforeCompletion and afterCompletion.
     *
     * @return boolean isJTAEnabled
     * @see #getExternalTransactionControllerClass()
     * @see #disableJTA()
     */
    public boolean isJTAEnabled() {
        return this.isJTAEnabled;
    }

    
    /**
     * INTERNAL: 
     * isRuntimeServicesEnabledDefault(): Answer true if the JMX/MBean providing runtime services for
     * the receiver's DatabaseSession will be deployed at runtime.
     * Provide the default value for {@link #isRuntimeServicesEnabled()} for a
     * ServerPlatform. By default this is <code>false</code> but some platforms
     * can choose to have MBeans deployed by default.
     */
    public boolean isRuntimeServicesEnabledDefault() {
        return false;
    }
    
    /**
     * INTERNAL: 
     * isRuntimeServicesEnabled(): Answer true if the JMX/MBean providing runtime services for
     * the receiver's DatabaseSession will be deployed at runtime.
     *
     * @return boolean isRuntimeServicesEnabled
     * @see #disableRuntimeServices()
     */
    public boolean isRuntimeServicesEnabled() {
        return this.isRuntimeServicesEnabled;
    }
    
    /**
     * INTERNAL: disableRuntimeServices(): Configure the receiver such that no JMX/MBean will be registered
     * to provide runtime services for my DatabaseSession at runtime.
     *
     * @return void
     * @see #isRuntimeServicesEnabled()
     */
    public void disableRuntimeServices() {
        this.ensureNotLoggedIn();
        this.isRuntimeServicesEnabled = false;
    }

    /**
     * INTERNAL: 
     * enableRuntimeServices(): Configure the receiver such that JMX/MBeans will be registered
     * to provide runtime services for my DatabaseSession at runtime.
     *
     * @return void
     * @see #isRuntimeServicesEnabled()
     * @since EclipseLink 2.2.0
     */
    public void enableRuntimeServices() {
        this.ensureNotLoggedIn();
        this.isRuntimeServicesEnabled = true;
    }
    
    
    /**
     * INTERNAL: disableJTA(): Configure the receiver such that my external transaction controller class will
     * be ignored, and will NOT be used to populate DatabaseSession's external transaction controller class
     * at runtime.
       *
       * TopLink will NOT be configured to register for callbacks for beforeCompletion and afterCompletion.
     *
     * @return void
     * @see #getExternalTransactionControllerClass()
     * @see #isJTAEnabled()
     */
    public void disableJTA() {
        this.ensureNotLoggedIn();
        this.isJTAEnabled = false;
    }

    /**
     * INTERNAL:  This method is used to unwrap the connection wrapped by
     * the application server.  TopLink needs this unwrapped connection for certain
     * database vendor specific support. (i.e. TIMESTAMPTZ,NCHAR,XMLTYPE)
     * 
     * Be default we will use the connection's metadata to try to get the connection
     */
    public java.sql.Connection unwrapConnection(java.sql.Connection connection){
        try {
            return connection.getMetaData().getConnection();
        } catch (java.sql.SQLException e){
            ((DatabaseSessionImpl)getDatabaseSession()).log(SessionLog.WARNING, SessionLog.CONNECTION, "cannot_unwrap_connection", e);
            return connection;            
        }
    }  

    /**
     * INTERNAL: launchContainerRunnable(Runnable runnable): Use the container library to
     * start the provided Runnable.
     *
     * Default behavior is to use Thread(runnable).start()
     *
     * @param Runnable runnable: the instance of runnable to be "started"
     * @return void
     */
    public void launchContainerRunnable(Runnable runnable) {
        if (getThreadPool() == null) {
            Thread thread = new Thread(runnable);
            thread.start();
        } else {
            getThreadPool().execute(runnable);
        }
    }

    /**
     * INTERNAL: Make sure that the DatabaseSession has not logged in yet.
     * Throw a ValidationException if we have.
     *
     */
    protected void ensureNotLoggedIn() {
        //RCM: Allow for a null database session
        if (getDatabaseSession() == null) {
            return;
        }
    }

    /**
     * INTERNAL: getServerLog(): Return the ServerLog for this platform
     *
     * Return the default ServerLog in the base
     *
     * @return org.eclipse.persistence.logging.SessionLog
     */
    public org.eclipse.persistence.logging.SessionLog getServerLog() {
        return new ServerLog();
    }
    
    /**
     * Return the thread pool size.
     */
    public int getThreadPoolSize() {
        return threadPoolSize;
    }
    
    /**
     * Set the thread pool size.
     */
    public void setThreadPoolSize(int threadPoolSize) {
        this.threadPoolSize = threadPoolSize;
    }

    /**
     * INTERNAL: Return the thread pool, initializing if required.
     */
    public ExecutorService getThreadPool() {
        if ((threadPool == null) && (this.threadPoolSize > 0)) {
            synchronized (this) {
                if (threadPool == null) {
                    threadPool = Executors.newFixedThreadPool(getThreadPoolSize());
                }
            }
        }
        return threadPool;
    }

    /**
     * INTERNAL: Set the thread pool to use.
     */
    public void setThreadPool(ExecutorService threadPool) {
        this.threadPool = threadPool;
    }
    
    /**
     * INTERNAL: isCMP(): Answer true if we're in the context of CMP (i.e. the container created me)
     *
     * @return boolean 
     */
    public boolean isCMP() {
        return isCMP;
    }

    /**
     * INTERNAL: setIsCMP(boolean): Define whether or not we're in the context of CMP (i.e. the container created me)
     *
     * @return void 
     */
    public void setIsCMP(boolean isThisCMP) {
        isCMP = isThisCMP;
    }

    /**
     * INTERNAL: shouldUseDriverManager(): Indicates whether DriverManager should be used while connecting DefaultConnector.
     *
     * @return boolean 
     */
    public boolean shouldUseDriverManager() {
        return true;
    }

    /**
     * INTERNAL:
     * A call to this method will perform a platform based check on the connection and exception
     * error code to determine if the connection is still valid or if a communication error has occurred.
     * If a communication error has occurred then the query may be retried.
     * If this platform is unable to determine if the error was communication based it will return
     * false forcing the error to be thrown to the user.
     */    
    public boolean wasFailureCommunicationBased(SQLException exception, Accessor connection, AbstractSession sessionForProfile){
        return getDatabaseSession().getPlatform().wasFailureCommunicationBased(exception, connection.getConnection(), sessionForProfile);
    }

    public JPAClassLoaderHolder getNewTempClassLoader(PersistenceUnitInfo puInfo) {
        return new JPAClassLoaderHolder(puInfo.getNewTempClassLoader(), true);
    }

    /**
     * INTERNAL:
     * Clears statement cache of the wrapper connection.
     * Required by Oracle proxy authentication: currently connection statement cache
     * becomes invalid on switching to/from proxy session.
     * This method is called by OracleJDBC_10_1_0_2ProxyConnectionCustomizer  
     * before opening proxy session and before closing it.
     */
    public void clearStatementCache(java.sql.Connection connection) {   
    }
    
    /**
     * INTERNAL: registerMBean(): Create and deploy the JMX MBean to provide runtime services for my
     * databaseSession.
     *
     * Default is to do nothing.
     *
     * @return void
     * @see #isRuntimeServicesEnabled()
     * @see #disableRuntimeServices()
     * @see #unregisterMBean()
     */
    public void registerMBean() {
        if (!this.isRuntimeServicesEnabled()) {
            return;
        }
        this.serverSpecificRegisterMBean();
    }

    /**
     * INTERNAL: unregisterMBean(): Unregister the JMX MBean that was providing runtime services for my
     * databaseSession.
     *
     * @return void
     * @see #isRuntimeServicesEnabled()
     * @see #disableRuntimeServices()
     * @see #registerMBean()
     */
    public void unregisterMBean() {
        if (!this.isRuntimeServicesEnabled()) {
            return;
        }
        this.serverSpecificUnregisterMBean();
    }
    
    /**
     * INTERNAL: perform any require shutdown tasks.
     */
    public void shutdown() {
        unregisterMBean();
        if (this.threadPool != null) {
            this.threadPool.shutdownNow();
            this.threadPool = null;
        }
    }

    
    /**
     * INTERNAL: serverSpecificUnregisterMBean(): Server specific implementation of the
     * unregistration of the JMX MBean from its server.
     *
     * Default is to do nothing. This should be subclassed if required.
     *
     * @return void
     * @see #isRuntimeServicesEnabled()
     * @see #disableRuntimeServices()
     */
    public void serverSpecificUnregisterMBean() { }

    /**
     * INTERNAL: serverSpecificRegisterMBean(): Server specific implementation of the
     * creation and deployment of the JMX MBean to provide runtime services for my
     * databaseSession.
     *
     * Default is to do nothing. This should be subclassed if required.
     *
     * @return void
     * @see #isRuntimeServicesEnabled()
     * @see #disableRuntimeServices()
     * @see #registerMBean()
     */
    public void serverSpecificRegisterMBean() { }       
}
