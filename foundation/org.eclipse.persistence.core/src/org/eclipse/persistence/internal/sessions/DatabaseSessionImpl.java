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
package org.eclipse.persistence.internal.sessions;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.internal.helper.DBPlatformHelper;
import org.eclipse.persistence.internal.databaseaccess.Platform;
import org.eclipse.persistence.internal.sequencing.Sequencing;
import org.eclipse.persistence.internal.sequencing.SequencingHome;
import org.eclipse.persistence.internal.sequencing.SequencingFactory;
import org.eclipse.persistence.sequencing.SequencingControl;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.sessions.Login;
import org.eclipse.persistence.sessions.DatasourceLogin;
import org.eclipse.persistence.sessions.SessionProfiler;
import org.eclipse.persistence.platform.database.DatabasePlatform;
import org.eclipse.persistence.platform.database.OraclePlatform;
import org.eclipse.persistence.platform.server.ServerPlatform;
import org.eclipse.persistence.platform.server.NoServerPlatform;
import org.eclipse.persistence.platform.server.ServerPlatformBase;

/**
 * Implementation of org.eclipse.persistence.sessions.DatabaseSession
 * The public interface should be used.
 * @see org.eclipse.persistence.sessions.DatabaseSession
 *
 * <p>
 * <b>Purpose</b>: Define the implementation for a single user/single connection EclipseLink session.
 * <p>
 * <b>Description</b>: The session is the primary interface into EclipseLink,
 * the application should do all of its reading and writing of objects through the session.
 * The session also manages transactions and units of work.  The database session is intended
 * for usage in two-tier client-server applications.  Although it could be used in a server
 * situation, it is limited to only having a single database connection and only allows
 * a single open database transaction.
 * <p>
 * <b>Responsibilities</b>:
 *    <ul>
 *    <li> Connecting/disconnecting.
 *    <li> Reading and writing objects.
 *    <li> Transaction and unit of work support.
 *    <li> Identity maps and caching.
 *    </ul>
 */
public class DatabaseSessionImpl extends AbstractSession implements org.eclipse.persistence.sessions.DatabaseSession {

    /**
     * INTERNAL:
     * sequencingHome for this session.
     */
    private SequencingHome sequencingHome;

    /**
     * Used to store the server platform that handles server-specific functionality for Oc4j, WLS,  etc.
     */
    private ServerPlatform serverPlatform;

    /**
     * INTERNAL:
     * connectedTime indicates the exact time this session was logged in.
     */
    private long connectedTime;

    /**
     * INTERNAL
     * Indicate if this session is logged in.
     */

    //Bug#3440544 Used to stop the attempt to login more than once. 
    protected volatile boolean isLoggedIn;

    /**
     * INTERNAL:
     * Set the SequencingHome object used by the session.
     */
    protected void setSequencingHome(SequencingHome sequencingHome) {
        this.sequencingHome = sequencingHome;
    }

    /**
     * INTERNAL:
     * Return  SequencingHome which used to obtain all sequence-related
     * interfaces for DatabaseSession
     */
    protected SequencingHome getSequencingHome() {
        if (sequencingHome == null) {
            setSequencingHome(SequencingFactory.createSequencingHome(this));
        }
        return sequencingHome;
    }

    /**
     * INTERNAL:
     * Return if the session was logged in.
     * This may slight differ to isConnected which asks the JDBC Connection if it is connected.
     */
    public boolean isLoggedIn() {
        return isLoggedIn;
    }
    
    /**
     * PUBLIC:
     * Return  SequencingControl which used for sequencing setup and
     * customization including management of sequencing preallocation.
     */
    public SequencingControl getSequencingControl() {
        return getSequencingHome().getSequencingControl();
    }

    /**
     * PUBLIC:
     * Return the Sequencing object used by the session.
     */
    public Sequencing getSequencing() {
        return getSequencingHome().getSequencing();
    }

    /**
     * INTERNAL:
     * Indicates whether SequencingCallback is required.
     * Always returns false if sequencing is not connected.
     */
    public boolean isSequencingCallbackRequired() {
        return getSequencingHome().isSequencingCallbackRequired();
    }

    /**
     * INTERNAL:
     * Creates sequencing object
     */
    public void initializeSequencing() {
        getSequencingHome().onDisconnect();
        getSequencingHome().onConnect();
    }

    /**
     * INTERNAL:
     * Called in the end of beforeCompletion of external transaction synchronization listener.
     * Close the managed sql connection corresponding to the external transaction.
     */
    public void releaseJTSConnection() {
        getAccessor().closeJTSConnection();
    }

    /**
     * INTERNAL:
     * Create and return a new default database session.
     * Used for EJB SessionManager to instantiate a database session
     */
    public DatabaseSessionImpl() {
        super();
        this.setServerPlatform(new NoServerPlatform(this));
    }

    /**
     * PUBLIC:
     * Create and return a new session.
     * By giving the login information on creation this allows the session to initialize itself
     * to the platform given in the login. This constructor does not return a connected session.
     * To connect the session to the database login() must be sent to it. The login(userName, password)
     * method may also be used to connect the session, this allows for the user name and password
     * to be given at login but for the other database information to be provided when the session is created.
     */
    public DatabaseSessionImpl(Login login) {
        this(new org.eclipse.persistence.sessions.Project(login));
    }

    /**
     * PUBLIC:
     * Create and return a new session.
     * This constructor does not return a connected session.
     * To connect the session to the database login() must be sent to it. The login(userName, password)
     * method may also be used to connect the session, this allows for the user name and password
     * to be given at login but for the other database information to be provided when the session is created.
     */
    public DatabaseSessionImpl(org.eclipse.persistence.sessions.Project project) {
        super(project);
        this.setServerPlatform(new NoServerPlatform(this));
    }

    /**
     * PUBLIC:
     * Add the descriptor to the session.
     * All persistent classes must have a descriptor registered for them with the session.
     * It is best to add the descriptors before login, if added after login the order in which
     * descriptors are added is dependent on inheritance and references unless the addDescriptors
     * method is used.
     *
     * @see #addDescriptors(Vector)
     * @see #addDescriptors(org.eclipse.persistence.sessions.Project)
     */
    public void addDescriptor(ClassDescriptor descriptor) {
        // Reset cached data, as may be invalid later on.
        this.lastDescriptorAccessed = null;

        getProject().addDescriptor(descriptor, this);
    }

    /**
     * PUBLIC:
     * Add the descriptors to the session.
     * All persistent classes must have a descriptor registered for them with the session.
     * This method allows for a batch of descriptors to be added at once so that EclipseLink
     * can resolve the dependencies between the descriptors and perform initialization optimally.
     */
    public void addDescriptors(Collection descriptors) {
        // Reset cached data, as may be invalid later on.
        this.lastDescriptorAccessed = null;

        getProject().addDescriptors(descriptors, this);
    }

    /**
     * PUBLIC:
     * Add the descriptors to the session from the Project.
     * This can be used to combine the descriptors from multiple projects into a single session.
     * This can be called after the session has been connected as long as there are no external dependencies.
     */
    public void addDescriptors(org.eclipse.persistence.sessions.Project project) {
        // Reset cached data, as may be invalid later on.
        this.lastDescriptorAccessed = null;

        getProject().addDescriptors(project, this);
    }

    /**
     * INTERNAL:
     * Connect the session only.
     */
    public void connect() throws DatabaseException {
        getAccessor().connect(getDatasourceLogin(), this);
    }

    /**
     * INTERNAL:
     * Disconnect the accessor only.
     */
    public void disconnect() throws DatabaseException {
        getSequencingHome().onDisconnect();
        getAccessor().disconnect(this);
    }

    /**
     * PUBLIC:
     * Answer the server platform to handle server specific behavior for WLS, Oc4j, etc.
     *
     * If the user wants a different external transaction controller class or
     * to provide some different behavior than the provided ServerPlatform(s), we recommend
     * subclassing org.eclipse.persistence.platform.server.ServerPlatformBase (or a subclass),
     * and overriding:
     *
     * ServerPlatformBase.getExternalTransactionControllerClass()
     * ServerPlatformBase.registerMBean()
     * ServerPlatformBase.unregisterMBean()
     *
     * for the desired behavior.
     *
     * @see org.eclipse.persistence.platform.server.ServerPlatformBase
     */
    public ServerPlatform getServerPlatform() {
        return serverPlatform;
    }

    /**
     * PUBLIC:
     * Set the server platform to handle server specific behavior for WLS, Oc4j, etc
     *
     * This is not permitted after the session is logged in.
     *
     * If the user wants a different external transaction controller class or
     * to provide some different behavior than the provided ServerPlatform(s), we recommend
     * subclassing org.eclipse.persistence.platform.server.ServerPlatformBase (or a subclass),
     * and overriding:
     *
     * ServerPlatformBase.getExternalTransactionControllerClass()
     * ServerPlatformBase.registerMBean()
     * ServerPlatformBase.unregisterMBean()
     *
     * for the desired behavior.
     *
     * @see org.eclipse.persistence.platform.server.ServerPlatformBase
     */
    public void setServerPlatform(ServerPlatform newServerPlatform) {
        if (this.isLoggedIn) {
            throw ValidationException.serverPlatformIsReadOnlyAfterLogin(newServerPlatform.getClass().getName());
        }
        this.serverPlatform = newServerPlatform;
    }

    /**
     * INTERNAL:
     * Logout in case still connected.
     */
    protected void finalize() throws DatabaseException {
        if (isConnected()) {
            logout();
        }
    }

    /**
     * INTERNAL:
     * Return the database platform currently connected to.
     * The platform is used for database specific behavior.
     * NOTE: this must only be used for relational specific usage,
     * it will fail for non-relational datasources.
     */
    public DatabasePlatform getPlatform() {
        // PERF: Cache the platform.
        if (platform == null) {
            if(isLoggedIn) {
                platform = getDatasourceLogin().getPlatform();
            } else {
                return getDatasourceLogin().getPlatform();
            }
        }
        return (DatabasePlatform)platform;
    }

    /**
     * INTERNAL:
     * Return the database platform currently connected to.
     * The platform is used for database specific behavior.
     */
    public Platform getDatasourcePlatform() {
        // PERF: Cache the platform.
        if (platform == null) {
            if(isLoggedIn) {
                platform = getDatasourceLogin().getDatasourcePlatform();
            } else {
                return getDatasourceLogin().getDatasourcePlatform();
            }
        }
        return platform;
    }

    /**
     * INTERNAL:
     * Return the database platform currently connected to
     * for specified class.
     * The platform is used for database specific behavior.
     */
    public Platform getPlatform(Class domainClass) {
        // PERF: Cache the platform.
        if (platform == null) {
            if(isLoggedIn) {
                platform = getDatasourceLogin().getDatasourcePlatform();
            } else {
                return getDatasourceLogin().getDatasourcePlatform();
            }
        }
        return platform;
    }

    /**
     * INTERNAL:
     * A descriptor may have been added after the session is logged in.
     * In this case the descriptor must be allowed to initialize any dependencies on this session.
     * Normally the descriptors are added before login, then initialized on login.
     */
    public void initializeDescriptorIfSessionAlive(ClassDescriptor descriptor) {
        if (isConnected() && (descriptor.requiresInitialization())) {
            try {
                try {
                    initializeSequencing();
                    descriptor.preInitialize(this);
                    descriptor.initialize(this);
                    descriptor.postInitialize(this);
                    getCommitManager().initializeCommitOrder();
                } catch (RuntimeException exception) {
                    getIntegrityChecker().handleError(exception);
                }

                if (getIntegrityChecker().hasErrors()) {
                    //CR#4011
                    handleException(new IntegrityException(getIntegrityChecker()));
                }
            } finally {
                clearIntegrityChecker();
            }
        }
    }

    /**
     * INTERNAL:
     * Allow each descriptor to initialize any dependencies on this session.
     * This is done in two passes to allow the inheritance to be resolved first.
     * Normally the descriptors are added before login, then initialized on login.
     */
    public void initializeDescriptors() {
        // Must clone to avoid modification of the map while enumerating.
        initializeDescriptors((Map)((HashMap)getDescriptors()).clone());
        // Process JPA named queries and add as session queries,
        // this must be done after descriptor init as requires to parse the JPQL.
        processJPAQueries();
    }

    /**
     * INTERNAL:
     * Allow each descriptor to initialize any dependencies on this session.
     * This is done in two passes to allow the inheritance to be resolved first.
     * Normally the descriptors are added before login, then initialized on login.
     * The descriptors session must be used, not the broker.
     */
    public void initializeDescriptors(Map descriptors) {
        initializeSequencing();
        try {
            // First initialize basic properties (things that do not depend on anything else)
            Iterator iterator = descriptors.values().iterator();
            while (iterator.hasNext()) {
                ClassDescriptor descriptor = (ClassDescriptor)iterator.next();
                try {
                    AbstractSession session = getSessionForClass(descriptor.getJavaClass());
                    if (descriptor.requiresInitialization()) {
                        descriptor.preInitialize(session);
                    }

                    //check if inheritance is involved in aggregate relationship, and let the parent know the child descriptor
                    if ((descriptor.isAggregateDescriptor() || descriptor.isAggregateCollectionDescriptor()) && descriptor.isChildDescriptor()) {
                        descriptor.initializeAggregateInheritancePolicy(session);
                    }
                } catch (RuntimeException exception) {
                    exception.printStackTrace();
                    getIntegrityChecker().handleError(exception);
                }
            }

            // Second initialize basic mappings
            iterator = descriptors.values().iterator();
            while (iterator.hasNext()) {
                ClassDescriptor descriptor = (ClassDescriptor)iterator.next();
                try {
                    AbstractSession session = getSessionForClass(descriptor.getJavaClass());
                    if (descriptor.requiresInitialization()) {
                        descriptor.initialize(session);
                    }
                } catch (RuntimeException exception) {
                    getIntegrityChecker().handleError(exception);
                }
            }

            // Third initialize child dependencies
            iterator = descriptors.values().iterator();
            while (iterator.hasNext()) {
                ClassDescriptor descriptor = (ClassDescriptor)iterator.next();
                try {
                    AbstractSession session = getSessionForClass(descriptor.getJavaClass());
                    if (descriptor.requiresInitialization()) {
                        descriptor.postInitialize(session);
                    }
                } catch (RuntimeException exception) {
                    getIntegrityChecker().handleError(exception);
                }
            }

            try {
                getCommitManager().initializeCommitOrder();
            } catch (RuntimeException exception) {
                getIntegrityChecker().handleError(exception);
            }

            if (getIntegrityChecker().hasErrors()) {
                //CR#4011
                handleSevere(new IntegrityException(getIntegrityChecker()));
            }
        } finally {
            clearIntegrityChecker();
        }
    }

    /**
     * INTERNAL:
     * Allow each descriptor to initialize any dependencies on this session.
     * This is done in two passes to allow the inheritance to be resolved first.
     * Normally the descriptors are added before login, then initialized on login.
     * The descriptors session must be used, not the broker.
     */
    public void initializeDescriptors(Collection descriptors) {
        initializeSequencing();
        try {
            // First initialize basic properties (things that do not depend on anything else)
            for (Iterator descriptorEnum = descriptors.iterator(); descriptorEnum.hasNext();) {
                try {
                    ClassDescriptor descriptor = (ClassDescriptor)descriptorEnum.next();
                    AbstractSession session = getSessionForClass(descriptor.getJavaClass());
                    if (descriptor.requiresInitialization()) {
                        descriptor.preInitialize(session);
                    }

                    //check if inheritance is involved in aggregate relationship, and let the parent know the child descriptor
                    if (descriptor.isAggregateDescriptor() && descriptor.isChildDescriptor()) {
                        descriptor.initializeAggregateInheritancePolicy(session);
                    }
                } catch (RuntimeException exception) {
                    getIntegrityChecker().handleError(exception);
                }
            }

            // Second basic initialize mappings
            for (Iterator descriptorEnum = descriptors.iterator(); descriptorEnum.hasNext();) {
                try {
                    ClassDescriptor descriptor = (ClassDescriptor)descriptorEnum.next();
                    AbstractSession session = getSessionForClass(descriptor.getJavaClass());
                    if (descriptor.requiresInitialization()) {
                        descriptor.initialize(session);
                    }
                } catch (RuntimeException exception) {
                    getIntegrityChecker().handleError(exception);
                }
            }

            // Third initialize child dependencies
            for (Iterator descriptorEnum = descriptors.iterator(); descriptorEnum.hasNext();) {
                try {
                    ClassDescriptor descriptor = (ClassDescriptor)descriptorEnum.next();
                    AbstractSession session = getSessionForClass(descriptor.getJavaClass());
                    if (descriptor.requiresInitialization()) {
                        descriptor.postInitialize(session);
                    }
                } catch (RuntimeException exception) {
                    getIntegrityChecker().handleError(exception);
                }
            }

            try {
                getCommitManager().initializeCommitOrder();
            } catch (RuntimeException exception) {
                getIntegrityChecker().handleError(exception);
            }

            if (getIntegrityChecker().hasErrors()) {
                //CR#4011
                handleException(new IntegrityException(getIntegrityChecker()));
            }
        } finally {
            clearIntegrityChecker();
        }
    }

    /**
     * INTERNAL:
     * Return if this session is a database session.
     */
    public boolean isDatabaseSession() {
        return true;
    }

    /**
     * INTERNAL:
     * Return the login for the read connection.  Used by the platform autodetect feature
     */
    protected Login getReadLogin(){
        return getDatasourceLogin();
    }

    /**
     * PUBLIC:
     * Connect to the database using the predefined login.
     * During connection, attempt to auto detect the required database platform.
     * This method can be used in systems where for ease of use developers have
     * EclipseLink autodetect the platform.
     * To be safe, however, the platform should be configured directly.
     * The login must have been assigned when or after creating the session.
     *
     */
    public void loginAndDetectDatasource() throws DatabaseException {
        preConnectDatasource();
        Connection conn = null;
        try{
            conn = (Connection)getReadLogin().connectToDatasource(null,this);
            // null out the cached platform because the platform on the login will be changed by the following line of code
            this.platform = null;
            String platformName = null;
            try {
                platformName = DBPlatformHelper.getDBPlatform(conn.getMetaData().getDatabaseProductName(), getSessionLog());
                getLogin().setPlatformClassName(platformName);
            } catch (EclipseLinkException classNotFound) {
                if (platformName.indexOf("Oracle") != -1) {
                    // If we are running against Oracle, it is possible that we are running in an environment where
                    // the OracleXPlatform class can not be loaded. Try using OraclePlatform class before giving up
                    getLogin().setPlatformClassName(OraclePlatform.class.getName());
                } else {
                    throw classNotFound;
                }
            }
        }catch (SQLException ex){
            DatabaseException dbEx =  DatabaseException.errorRetrieveDbMetadataThroughJDBCConnection();
            // Typically exception would occur if user did not provide correct connection
            // parameters. The root cause of exception should be propagated up
            dbEx.initCause(ex);
            throw dbEx;
        }finally{
            if (conn != null){
                try{
                    conn.close();
                }catch (SQLException ex){
                    DatabaseException dbEx =  DatabaseException.errorRetrieveDbMetadataThroughJDBCConnection();
                    // Typically exception would occur if user did not provide correct connection
                    // parameters. The root cause of exception should be propagated up
                    dbEx.initCause(ex);
                    throw dbEx;
                }
            }
        }
        connect();
        postConnectDatasource();
    }

    /**
     * PUBLIC:
     * Connect to the database using the predefined login.
     * The login must have been assigned when or after creating the session.
     *
     * @see #login(Login)
     */
    public void login() throws DatabaseException {
        preConnectDatasource();
        connect();
        postConnectDatasource();
    }
    /**
     * INTERNAL:
     * This method includes all of the code that is issued before the datasource
     * is connected to.
     */
    protected void preConnectDatasource(){
        //Bug#3440544 Check if logged in already to stop the attempt to login more than once
        if (isLoggedIn) {
            throw ValidationException.alreadyLoggedIn(this.getName());
        }
        this.platform = null;
        if (isInProfile()) {
            getProfiler().initialize();
        }
        updateProfile(SessionProfiler.LoginTime, new Date(System.currentTimeMillis()));

        // Login and initialize
        if (this.eventManager != null) {
            this.eventManager.preLogin(this);
        }
        //setup the external transaction controller
        getServerPlatform().initializeExternalTransactionController();
        log(SessionLog.INFO, null, "topLink_version", DatasourceLogin.getVersion());
        if (getServerPlatform().getServerNameAndVersion() != null && 
                !getServerPlatform().getServerNameAndVersion().equals(ServerPlatformBase.DEFAULT_SERVER_NAME_AND_VERSION)) {
            log(SessionLog.INFO, null, "application_server_name_and_version", getServerPlatform().getServerNameAndVersion());
        }
        this.isLoggingOff = (getLogLevel() == SessionLog.OFF);
    }

    /**
     * INTERNAL:
     * This method includes all of the code that is issued after the datasource
     * is connected to.
     */
    protected void postConnectDatasource(){
        initializeDescriptors();
        //added to process ejbQL query strings
        if (getCommandManager() != null) {
            getCommandManager().initialize();
        }
        log(SessionLog.INFO, null, "login_successful", this.getName());
        if (this.eventManager != null) {
            this.eventManager.postLogin(this);
        }

        initializeConnectedTime();
        this.isLoggedIn = true;
        this.platform = null;
        
        //register the MBean
        getServerPlatform().registerMBean();
        this.descriptors = getDescriptors();
        // EclipseLink 23869 - Initialize plaformOperators eagerly to avoid concurrency issues.
        getDatasourcePlatform().initialize();
    }

    /**
     * PUBLIC:
     * Connect to the database using the given user name and password.
     * The additional login information must have been preset in the session's login attribute.
     * This is the login that should be used if each user has their own id,
     * but all users share the same database configuration.
     */
    public void login(String userName, String password) throws DatabaseException {
        getDatasourceLogin().setUserName(userName);
        getDatasourceLogin().setPassword(password);
        login();
    }

    /**
     * PUBLIC:
     * Connect to the database using the given login.
     * The login may also the preset and the login() protocol called.
     * This is the login should only be used if each user has their own database configuration.
     */
    public void login(Login login) throws DatabaseException {
        setLogin(login);
        login();
    }

    /**
     * PUBLIC:
     * Disconnect from the database.
     *
     * @exception EclipseLinkException if a transaction is active, you must rollback any active transaction before logout.
     * @exception DatabaseException the database will also raise an error if their is an active transaction,
     * or a general error occurs.
     */
    public void logout() throws DatabaseException {
        // Reset cached data, as may be invalid later on.
        this.lastDescriptorAccessed = null;

        if (isInTransaction()) {
            throw DatabaseException.logoutWhileTransactionInProgress();
        }
        
        if (getAccessor() == null) {
            return;
        }
        
        // We're logging out so turn off change propagation.
        this.setShouldPropagateChanges(false);
        
        if (getCommandManager() != null) {
            getCommandManager().shutdown();
        }

        // Unregister the JMX MBean before logout to avoid a javax.naming.NameNotFoundException
        getServerPlatform().unregisterMBean();
        
        disconnect();
        getIdentityMapAccessor().initializeIdentityMaps();
        isLoggedIn = false;
        log(SessionLog.INFO, null, "logout_successful", this.getName());
	   
    }

    /**
     * PUBLIC:
     * Initialize the time that this session got connected. This can help determine how long a session has been
     * connected.
     */
    public void initializeConnectedTime() {
        connectedTime = System.currentTimeMillis();
    }

    /**
     * PUBLIC:
     * Answer the time that this session got connected. This can help determine how long a session has been
     * connected.
     */
    public long getConnectedTime() {
        return connectedTime;
    }

    /**
     * PUBLIC:
     * Write all of the objects and all of their privately owned parts in the database.
     * The objects will be committed through a single transaction.
     *
     * @exception DatabaseException if an error occurs on the database,
     * these include constraint violations, security violations and general database errors.
     * @exception OptimisticLockException if the object's descriptor is using optimistic locking and
     * the object has been updated or deleted by another user since it was last read.
     */
    public void writeAllObjects(Collection domainObjects) throws DatabaseException, OptimisticLockException {
        for (Iterator objectsEnum = domainObjects.iterator(); objectsEnum.hasNext();) {
            writeObject(objectsEnum.next());
        }
    }

    /**
     * PUBLIC:
     * Write all of the objects and all of their privately owned parts in the database.
     * The objects will be committed through a single transaction.
     *
     * @exception DatabaseException if an error occurs on the database,
     * these include constraint violations, security violations and general database errors.
     * @exception OptimisticLockException if the object's descriptor is using optimistic locking and
     * the object has been updated or deleted by another user since it was last read.
     */
    public void writeAllObjects(Vector domainObjects) throws DatabaseException, OptimisticLockException {
        for (Enumeration objectsEnum = domainObjects.elements(); objectsEnum.hasMoreElements();) {
            writeObject(objectsEnum.nextElement());
        }
    }
}
