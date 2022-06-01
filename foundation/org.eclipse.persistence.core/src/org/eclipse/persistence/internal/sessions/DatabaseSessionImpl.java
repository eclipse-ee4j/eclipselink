/*
 * Copyright (c) 1998, 2020 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2015, 2020 IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
//     14/05/2012-2.4 Guy Pelletier
//       - 376603: Provide for table per tenant support for multitenant applications
//     22/05/2012-2.4 Guy Pelletier
//       - 380008: Multitenant persistence units with a dedicated emf should force tenant property specification up front.
//     31/05/2012-2.4 Guy Pelletier
//       - 381196: Multitenant persistence units with a dedicated emf should allow for DDL generation.
//     12/24/2012-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support
//     01/11/2013-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support
//     03/19/2015 - Rick Curtis
//       - 462586 : Add national character support for z/OS.
//     04/14/2015 - Will Dazey
//       - 464641 : Fixed platform matching returning CNF.
//     09/03/2015 - Will Dazey
//       - 456067 : Added support for defining query timeout units
//     06/26/2018 - Will Dazey
//       - 532160 : Add support for non-extension OracleXPlatform classes
package org.eclipse.persistence.internal.sessions;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.config.PropertiesUtils;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.partitioning.PartitioningPolicy;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.exceptions.IntegrityException;
import org.eclipse.persistence.exceptions.OptimisticLockException;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.databaseaccess.Platform;
import org.eclipse.persistence.internal.helper.DBPlatformHelper;
import org.eclipse.persistence.internal.sequencing.Sequencing;
import org.eclipse.persistence.internal.sequencing.SequencingFactory;
import org.eclipse.persistence.internal.sequencing.SequencingHome;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.platform.database.DatabasePlatform;
import org.eclipse.persistence.platform.database.OraclePlatform;
import org.eclipse.persistence.platform.database.events.DatabaseEventListener;
import org.eclipse.persistence.platform.server.NoServerPlatform;
import org.eclipse.persistence.platform.server.ServerPlatform;
import org.eclipse.persistence.platform.server.ServerPlatformBase;
import org.eclipse.persistence.queries.AttributeGroup;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.QueryResultsCachePolicy;
import org.eclipse.persistence.queries.ReadQuery;
import org.eclipse.persistence.sequencing.Sequence;
import org.eclipse.persistence.sequencing.SequencingControl;
import org.eclipse.persistence.sessions.DatasourceLogin;
import org.eclipse.persistence.sessions.Login;
import org.eclipse.persistence.sessions.SessionProfiler;
import org.eclipse.persistence.tools.tuning.SessionTuner;

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
     * Database event listener, this allows database events to invalidate the cache.
     */
    protected DatabaseEventListener databaseEventListener;

    /**
     * INTERNAL:
     * sequencingHome for this session.
     */
    protected SequencingHome sequencingHome;

    /**
     * Used to store the server platform that handles server-specific functionality for Oc4j, WLS,  etc.
     */
    protected ServerPlatform serverPlatform;

    /**
     * Stores the tuner used to tune the configuration of this session.
     */
    protected SessionTuner tuner;

    /**
     * INTERNAL:
     * connectedTime indicates the exact time this session was logged in.
     */
    protected long connectedTime;

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
     * Return the database event listener, this allows database events to invalidate the cache.
     */
    @Override
    public DatabaseEventListener getDatabaseEventListener() {
        return databaseEventListener;
    }

    /**
     * PUBLIC:
     * Set the database event listener, this allows database events to invalidate the cache.
     */
    @Override
    public void setDatabaseEventListener(DatabaseEventListener databaseEventListener) {
        this.databaseEventListener = databaseEventListener;
    }

    /**
     * INTERNAL:
     * Issue any pre connect and post connect without an actual connection to
     * the database. Descriptors are initialized in postConnectDatasource and
     * are used in DDL generation. This will look to set the schema platform
     * via the JPA 2.1 properties or through a detection on the connection
     * before DDL generation.
     */
    public void setDatasourceAndInitialize() throws DatabaseException {
        preConnectDatasource();
        setOrDetectDatasource(false);
        postConnectDatasource();
    }

    /**
     * INTERNAL:
     * Will set the platform from specified schema generation properties or
     * by detecting it through the connection (if one is available).
     * Any connection that is open for detection is closed before this method
     * returns.
     *
     * @param throwException - set to true if the caller cares to throw exceptions, false to swallow them.
     */
    protected void setOrDetectDatasource(boolean throwException) {
        String vendorName = null;
        String minorVersion = null;
        String majorVersion = null;
        String driverName = null;

        // Try to set the platform from JPA 2.1 schema properties first before
        // attempting a detection.
        if (getProperties().containsKey(PersistenceUnitProperties.SCHEMA_DATABASE_PRODUCT_NAME)) {
            vendorName = (String) getProperties().get(PersistenceUnitProperties.SCHEMA_DATABASE_PRODUCT_NAME);
            minorVersion = (String) getProperties().get(PersistenceUnitProperties.SCHEMA_DATABASE_MINOR_VERSION);
            majorVersion = (String) getProperties().get(PersistenceUnitProperties.SCHEMA_DATABASE_MAJOR_VERSION);
        } else {
            Connection conn = null;
            try {
                conn = (Connection) getReadLogin().connectToDatasource(null, this);
                DatabaseMetaData dmd = conn.getMetaData();
                vendorName = dmd.getDatabaseProductName();
                minorVersion = dmd.getDatabaseProductVersion();
                majorVersion = Integer.toString(dmd.getDatabaseMajorVersion());
                driverName = conn.getMetaData().getDriverName();
            } catch (SQLException ex) {
                if (throwException) {
                    DatabaseException dbEx = DatabaseException.errorRetrieveDbMetadataThroughJDBCConnection();
                    // Typically exception would occur if user did not provide
                    // correct connection
                    // parameters. The root cause of exception should be
                    // propagated up
                    dbEx.initCause(ex);
                    throw dbEx;
                }
            } finally {
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (SQLException ex) {
                        if (throwException) {
                            DatabaseException dbEx = DatabaseException.errorRetrieveDbMetadataThroughJDBCConnection();
                            // Typically exception would occur if user did not
                            // provide correct connection
                            // parameters. The root cause of exception should be
                            // propagated up
                            dbEx.initCause(ex);
                            throw dbEx;
                        }
                    }
                }
            }
        }

        String platformName = null;
        try {
            // null out the cached platform because the platform on the login
            // will be changed by the following line of code
            this.platform = null;
            platformName = DBPlatformHelper.getDBPlatform(vendorName, minorVersion, majorVersion, getSessionLog());
            getLogin().setPlatformClassName(platformName);
        } catch (EclipseLinkException classNotFound) {
            if (platformName != null && platformName.indexOf("Oracle") != -1) {
                try {
                    // If we are running against Oracle, it is possible that we are
                    // running in an environment where the extension OracleXPlatform classes can 
                    // not be loaded. Try using the core OracleXPlatform classes
                    platformName = DBPlatformHelper.getDBPlatform("core."+ vendorName, minorVersion, majorVersion, getSessionLog());
                    getLogin().setPlatformClassName(platformName);
                } catch (EclipseLinkException oracleClassNotFound) {
                    // If we still cannot classload a matching OracleXPlatform class, 
                    // fallback on the base OraclePlatform class
                    getLogin().setPlatformClassName(OraclePlatform.class.getName());
                }
            } else {
                throw classNotFound;
            }
        }
        if (driverName != null) {
            getLogin().getPlatform().setDriverName(driverName);
        }
    }

    /**
     * PUBLIC:
     * Return  SequencingControl which used for sequencing setup and
     * customization including management of sequencing preallocation.
     */
    @Override
    public SequencingControl getSequencingControl() {
        return getSequencingHome().getSequencingControl();
    }

    /**
     * PUBLIC:
     * Return the Sequencing object used by the session.
     */
    @Override
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
     * If sequencing is connected then initializes sequences referenced by the passed descriptors,
     * otherwise connects sequencing.
     */
    public void addDescriptorsToSequencing(Collection descriptors) {
        getSequencingHome().onAddDescriptors(descriptors);
    }

    /**
     * INTERNAL:
     * Called in the end of beforeCompletion of external transaction synchronization listener.
     * Close the managed sql connection corresponding to the external transaction.
     */
    @Override
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
        this.shouldOptimizeResultSetAccess = ObjectLevelReadQuery.isResultSetAccessOptimizedQueryDefault;
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
        this.shouldOptimizeResultSetAccess = ObjectLevelReadQuery.isResultSetAccessOptimizedQueryDefault;
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
    @Override
    public void addDescriptor(ClassDescriptor descriptor) {
        // Reset cached data, as may be invalid later on.
        this.lastDescriptorAccessed = null;
        // Bug# 429760: Add descriptor to the session when session Map exists and is not the same as in the project.
        if (this.descriptors != null && this.descriptors != getProject().getDescriptors()) {
            this.descriptors.put(descriptor.getJavaClass(), descriptor);
        }
        getProject().addDescriptor(descriptor, this);
    }

    /**
     * PUBLIC:
     * Add the descriptors to the session.
     * All persistent classes must have a descriptor registered for them with the session.
     * This method allows for a batch of descriptors to be added at once so that EclipseLink
     * can resolve the dependencies between the descriptors and perform initialization optimally.
     * @param descriptors The descriptors to be added to the session and project.
     */
    @Override
    public void addDescriptors(final Collection descriptors) {
        // Reset cached data, as may be invalid later on.
        this.lastDescriptorAccessed = null;
        // Bug# 429760: Add descriptors to the session when session Map exists and is not the same as in the project.
        if (this.descriptors != null && this.descriptors != getProject().getDescriptors()) {
            for (ClassDescriptor descriptor : (Collection<ClassDescriptor>) descriptors) {
                this.descriptors.put(descriptor.getJavaClass(), descriptor);
            }
        }
        getProject().addDescriptors(descriptors, this);
    }

    /**
     * PUBLIC:
     * Add the descriptors to the session from the Project.
     * This can be used to combine the descriptors from multiple projects into a single session.
     * This can be called after the session has been connected as long as there are no external dependencies.
     */
    @Override
    public void addDescriptors(org.eclipse.persistence.sessions.Project project) {
        // Reset cached data, as may be invalid later on.
        this.lastDescriptorAccessed = null;

        getProject().addDescriptors(project, this);
    }

    /**
     * PUBLIC:
     * Add the sequence to the session.
     * Allows to add a new sequence to the session even if the session is connected.
     * If the session is connected then the sequence is added only
     * if there is no sequence with the same name already in use.
     * Call this method before addDescriptor(s) if need to add new descriptor
     * with a new non-default sequence to connected session.
     *
     * @see #addSequences(Collection)
     */
    @Override
    public void addSequence(Sequence sequence) {
        getProject().getLogin().getDatasourcePlatform().addSequence(sequence, this.getSequencingHome().isConnected());
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
    @Override
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
        if (isConnected() && (descriptor.requiresInitialization(this))) {
            try {
                try {
                    Collection descriptorsToAdd = new ArrayList(1);
                    descriptorsToAdd.add(descriptor);
                    addDescriptorsToSequencing(descriptorsToAdd);
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
        initializeDescriptors((Map)((HashMap)getDescriptors()).clone(), true);
        // Initialize serializer
        if (this.serializer != null) {
            this.serializer.initialize(null, null, this);
        }
        // Initialize partitioning policies.
        for (PartitioningPolicy policy : getProject().getPartitioningPolicies().values()) {
            policy.initialize(this);
        }

        if (getProject().getMultitenantPolicy() != null) {
            getProject().getMultitenantPolicy().initialize(this);
        }

        // Process JPA named queries and add as session queries,
        // this must be done after descriptor init as requires to parse the JPQL.
        processJPAQueries();

        // Configure default query cache for all named queries.
        QueryResultsCachePolicy defaultQueryCachePolicy = getProject().getDefaultQueryResultsCachePolicy();
        if (defaultQueryCachePolicy != null) {
            for (List<DatabaseQuery> queries : getQueries().values()) {
                for (DatabaseQuery query : queries) {
                    if (query.isReadQuery() && (query.getDescriptor() != null) && !query.getDescriptor().getCachePolicy().isIsolated()) {
                        ReadQuery readQuery = (ReadQuery)query;
                        if (!readQuery.shouldCacheQueryResults()) {
                            readQuery.setQueryResultsCachePolicy(defaultQueryCachePolicy.clone());
                        }
                    }
                }
            }
        }
        for (AttributeGroup group : getProject().getAttributeGroups().values()){
            getAttributeGroups().put(group.getName(), group);
            this.getDescriptor(group.getType()).addAttributeGroup(group);
        }
    }

    /**
     * INTERNAL:
     * Allow each descriptor to initialize any dependencies on this session.
     * This is done in two passes to allow the inheritance to be resolved first.
     * Normally the descriptors are added before login, then initialized on login.
     * The descriptors session must be used, not the broker.
     * Sequencing is (re)initialized: disconnected (if has been already connected), then connected.
     */
    public void initializeDescriptors(Map descriptors) {
        initializeDescriptors(descriptors.values(), false);
    }
    public void initializeDescriptors(Collection descriptors) {
        initializeDescriptors(descriptors, false);
    }

    /**
     * INTERNAL:
     * Allow each descriptor to initialize any dependencies on this session.
     * This is done in two passes to allow the inheritance to be resolved first.
     * Normally the descriptors are added before login, then initialized on login.
     * The descriptors session must be used, not the broker.
     * If shouldInitializeSequencing parameter is true then sequencing is (re)initialized:
     * disconnected (if has been connected), then connected.
     * If shouldInitializeSequencing parameter is false then
     *   if sequencing has been already connected, then it stays connected:
     *     only the new sequences used by the passed descriptors are initialized;
     *   otherwise, if sequencing has NOT been connected then it is connected
     *     (just like in shouldInitializeSequencing==true case);
     *   disconnected (if has been connected), then connected.
     */
    public void initializeDescriptors(Map descriptors, boolean shouldInitializeSequencing) {
        initializeDescriptors(descriptors.values(), shouldInitializeSequencing);
    }
    public void initializeDescriptors(Collection descriptors, boolean shouldInitializeSequencing) {
        if (shouldInitializeSequencing) {
            initializeSequencing();
        } else {
            addDescriptorsToSequencing(descriptors);
        }

        try {
            // First initialize basic properties (things that do not depend on anything else)
            Iterator iterator = descriptors.iterator();
            while (iterator.hasNext()) {
                ClassDescriptor descriptor = (ClassDescriptor)iterator.next();
                try {
                    AbstractSession session = getSessionForClass(descriptor.getJavaClass());
                    if (descriptor.requiresInitialization(session)) {
                        descriptor.preInitialize(session);
                    } else if (descriptor.hasTablePerMultitenantPolicy()) {
                        // If the descriptor doesn't require initialization and
                        // has a table per tenant policy then add to the list
                        // to be cloned and initialized per client session.
                        addTablePerTenantDescriptor(descriptor);
                    }

                    //check if inheritance is involved in aggregate relationship, and let the parent know the child descriptor
                    if (descriptor.isDescriptorTypeAggregate() && descriptor.isChildDescriptor()) {
                        descriptor.initializeAggregateInheritancePolicy(session);
                    }
                } catch (RuntimeException exception) {
                    getIntegrityChecker().handleError(exception);
                }
            }

            // Second initialize basic mappings
            iterator = descriptors.iterator();
            while (iterator.hasNext()) {
                ClassDescriptor descriptor = (ClassDescriptor)iterator.next();
                try {
                    AbstractSession session = getSessionForClass(descriptor.getJavaClass());
                    if (descriptor.requiresInitialization(session)) {
                        descriptor.initialize(session);
                    }
                } catch (RuntimeException exception) {
                    getIntegrityChecker().handleError(exception);
                }
            }

            // Third initialize child dependencies
            iterator = descriptors.iterator();
            while (iterator.hasNext()) {
                ClassDescriptor descriptor = (ClassDescriptor)iterator.next();
                try {
                    AbstractSession session = getSessionForClass(descriptor.getJavaClass());
                    if (descriptor.requiresInitialization(session)) {
                        descriptor.postInitialize(session);
                    }
                } catch (RuntimeException exception) {
                    getIntegrityChecker().handleError(exception);
                }
            }

            if (getIntegrityChecker().hasErrors()) {
                //CR#4011
                handleSevere(new IntegrityException(getIntegrityChecker()));
            }
        } finally {
            clearIntegrityChecker();
        }

        getCommitManager().initializeCommitOrder();
    }

    /**
     * INTERNAL:
     * Return if this session is a database session.
     */
    @Override
    public boolean isDatabaseSession() {
        return true;
    }

    /**
     * PUBLIC:
     * Returns true if Protected Entities should be built within this session
     */
    @Override
    public boolean isProtectedSession(){
        return false;
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
        setOrDetectDatasource(true);
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
    @Override
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
        updateProfile(SessionProfiler.SessionName, getName());

        // Login and initialize
        if (this.eventManager != null) {
            this.eventManager.preLogin(this);
        }
        if (!hasBroker()) {
            //setup the external transaction controller
            getServerPlatform().initializeExternalTransactionController();
            log(SessionLog.INFO, null, "topLink_version", DatasourceLogin.getVersion());
            if (getServerPlatform().getServerNameAndVersion() != null &&
                    !getServerPlatform().getServerNameAndVersion().equals(ServerPlatformBase.DEFAULT_SERVER_NAME_AND_VERSION)) {
                log(SessionLog.INFO, null, "application_server_name_and_version", getServerPlatform().getServerNameAndVersion());
            }
        }
        this.isLoggingOff = (getLogLevel() == SessionLog.OFF);
    }

    /**
     * INTERNAL:
     * This method includes all of the code that is issued after the datasource
     * is connected to.
     */
    protected void postConnectDatasource(){
        // Initialize the Platform properties now that the datasource is connected and the Platform should be initialized
        if((getDatasourcePlatform() instanceof DatabasePlatform)) {
            final Platform platform = getDatasourcePlatform();
            String platformValues = (String) getProperties().get(PersistenceUnitProperties.TARGET_DATABASE_PROPERTIES);
            PropertiesUtils.set(platform, PersistenceUnitProperties.TARGET_DATABASE_PROPERTIES, platformValues);
        }

        if (!hasBroker()) {
            initializeDescriptors();

            //added to process ejbQL query strings
            if (getCommandManager() != null) {
                getCommandManager().initialize();
            }
        }

        // Once the descriptors are initialized we can check if there are
        // multitenant entities and if this session (emf) is shared or not. If
        // not shared, all multitenant properties must be available and set by
        // the user at this point for us to validate (meaning they must be set
        // in a persitence.xml or passed into the create EMF call).
        if (getProperties().containsKey(PersistenceUnitProperties.MULTITENANT_SHARED_EMF)) {
            String value = (String) getProperties().get(PersistenceUnitProperties.MULTITENANT_SHARED_EMF);
            if (!Boolean.valueOf(value)) {
                for (String property : getMultitenantContextProperties()) {
                    if (! getProperties().containsKey(property)) {
                        throw ValidationException.multitenantContextPropertyForNonSharedEMFNotSpecified(property);
                    }
                }

                // Once the properties are validated we can allow ddl generation to happen (if needed).
                project.setAllowTablePerMultitenantDDLGeneration(true);
            }
        }

        log(SessionLog.FINE, SessionLog.CONNECTION, "login_successful", this.getName());
        // postLogin event should not be risen before descriptors have been initialized
        if (!hasBroker()) {
            postLogin();
        }

        initializeConnectedTime();
        this.isLoggedIn = true;
        this.platform = null;

        if (!hasBroker()) {
            //register the MBean
            getServerPlatform().registerMBean();
        }
        this.descriptors = getDescriptors();
        if (!isBroker()) {
            // EclipseLink 23869 - Initialize plaformOperators eagerly to avoid concurrency issues.
            getDatasourcePlatform().initialize();
            getIdentityMapAccessorInstance().getIdentityMapManager().checkIsCacheAccessPreCheckRequired();
        }
        if (this.databaseEventListener != null) {
            this.databaseEventListener.register(this);
        }
        if ((getDatasourcePlatform() instanceof DatabasePlatform) && getPlatform().getBatchWritingMechanism() != null) {
            getPlatform().getBatchWritingMechanism().initialize(this);
        }

    }

    /**
     * INTERNAL:
     * Rise postLogin event.
     */
    public void postLogin() {
        if (this.eventManager != null) {
            this.eventManager.postLogin(this);
        }
    }

    /**
     * PUBLIC:
     * Connect to the database using the given user name and password.
     * The additional login information must have been preset in the session's login attribute.
     * This is the login that should be used if each user has their own id,
     * but all users share the same database configuration.
     */
    @Override
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
    @Override
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
    @Override
    public void logout() throws DatabaseException {
        if (this.eventManager != null) {
            this.eventManager.preLogout(this);
        }

        cleanUpInjectionManager();

        // Reset cached data, as may be invalid later on.
        this.lastDescriptorAccessed = null;

        if (isInTransaction()) {
            throw DatabaseException.logoutWhileTransactionInProgress();
        }

        if (getAccessor() == null) {
            return;
        }

        if (this.databaseEventListener != null) {
            this.databaseEventListener.remove(this);
        }

        // We're logging out so turn off change propagation.
        setShouldPropagateChanges(false);

        if (!hasBroker()) {
            if (getCommandManager() != null) {
                getCommandManager().shutdown();
            }

            // Unregister the JMX MBean before logout to avoid a javax.naming.NameNotFoundException
            getServerPlatform().shutdown();
        }

        disconnect();
        getIdentityMapAccessor().initializeIdentityMaps();
        this.isLoggedIn = false;
        if (this.eventManager != null) {
            this.eventManager.postLogout(this);
        }
        log(SessionLog.FINE, SessionLog.CONNECTION, "logout_successful", this.getName());

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
    @Override
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

    /**
     * INTERNAL:
     * A query execution failed due to an invalid query.
     * Re-connect and retry the query.
     */
    @Override
    public Object retryQuery(DatabaseQuery query, AbstractRecord row, DatabaseException databaseException, int retryCount, AbstractSession executionSession) {
        if (getClass() != DatabaseSessionImpl.class) {
            return super.retryQuery(query, row, databaseException, retryCount, executionSession);
        }
        //attempt to reconnect connection:
        final int count = getLogin().getQueryRetryAttemptCount();
        while (retryCount < count) {
            try {
                // if database session then re-establish connection
                // else the session will just get a new
                // connection from the pool
                ++retryCount;
                databaseException.getAccessor().reestablishConnection(this);
                break;
            } catch (DatabaseException ex) {
                // failed to get connection because of
                // database error.
                try {
                    // Give the failover time to recover.
                    Thread.currentThread().sleep(getLogin().getDelayBetweenConnectionAttempts());
                    Object[] args = new Object[1];
                    args[0] = ex;
                    log(SessionLog.INFO, SessionLog.QUERY, "communication_failure_attempting_query_retry", args, null);
                } catch (InterruptedException intEx) {
                    break;
                }
            }
        }
        return executionSession.executeQuery(query, row, retryCount);
    }

    /**
     * Return the tuner used to tune the configuration of this session.
     */
    public SessionTuner getTuner() {
        return tuner;
    }

    /**
     * Set the tuner used to tune the configuration of this session.
     */
    public void setTuner(SessionTuner tuner) {
        this.tuner = tuner;
    }
}
