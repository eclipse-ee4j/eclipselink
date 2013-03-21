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
 *     10/15/2010-2.2 Guy Pelletier 
 *       - 322008: Improve usability of additional criteria applied to queries at the session/EM
 *     06/30/2011-2.3.1 Guy Pelletier 
 *       - 341940: Add disable/enable allowing native queries
 *     09/09/2011-2.3.1 Guy Pelletier 
 *       - 356197: Add new VPD type to MultitenantType 
 *     09/14/2011-2.3.1 Guy Pelletier 
 *       - 357533: Allow DDL queries to execute even when Multitenant entities are part of the PU
 *     05/14/2012-2.4 Guy Pelletier  
 *       - 376603: Provide for table per tenant support for multitenant applications
 *     08/11/2012-2.5 Guy Pelletier  
 *       - 393867: Named queries do not work when using EM level Table Per Tenant Multitenancy.
 *     11/29/2012-2.5 Guy Pelletier 
 *       - 395406: Fix nightly static weave test errors
 ******************************************************************************/
package org.eclipse.persistence.internal.sessions;

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.util.*;
import java.io.*;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.config.ReferenceMode;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.DescriptorQueryManager;
import org.eclipse.persistence.descriptors.TablePerMultitenantPolicy;
import org.eclipse.persistence.descriptors.invalidation.CacheInvalidationPolicy;
import org.eclipse.persistence.indirection.ValueHolderInterface;
import org.eclipse.persistence.descriptors.partitioning.PartitioningPolicy;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.internal.helper.linkedlist.ExposedNodeLinkedList;
import org.eclipse.persistence.internal.indirection.DatabaseValueHolder;
import org.eclipse.persistence.internal.indirection.ProtectedValueHolder;
import org.eclipse.persistence.internal.indirection.ProxyIndirectionPolicy;
import org.eclipse.persistence.platform.database.DatabasePlatform;
import org.eclipse.persistence.platform.server.ServerPlatform;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.history.*;
import org.eclipse.persistence.internal.identitymaps.*;
import org.eclipse.persistence.internal.localization.ExceptionLocalization;
import org.eclipse.persistence.internal.history.*;
import org.eclipse.persistence.internal.core.sessions.CoreAbstractSession;
import org.eclipse.persistence.internal.databaseaccess.Accessor;
import org.eclipse.persistence.internal.databaseaccess.Platform;
import org.eclipse.persistence.internal.descriptors.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.sessions.CopyGroup;
import org.eclipse.persistence.sessions.ObjectCopyingPolicy;
import org.eclipse.persistence.sessions.SessionProfiler;
import org.eclipse.persistence.sessions.SessionEventManager;
import org.eclipse.persistence.sessions.ExternalTransactionController;
import org.eclipse.persistence.sessions.Login;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.logging.SessionLogEntry;
import org.eclipse.persistence.logging.DefaultSessionLog;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;
import org.eclipse.persistence.mappings.foundation.AbstractTransformationMapping;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.internal.queries.JoinedAttributeManager;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedClassForName;
import org.eclipse.persistence.internal.security.PrivilegedNewInstanceFromClass;
import org.eclipse.persistence.internal.sequencing.Sequencing;
import org.eclipse.persistence.internal.sessions.cdi.DisabledEntityListenerInjectionManager;
import org.eclipse.persistence.internal.sessions.cdi.EntityListenerInjectionManager;
import org.eclipse.persistence.sessions.coordination.CommandProcessor;
import org.eclipse.persistence.sessions.coordination.CommandManager;
import org.eclipse.persistence.sessions.coordination.Command;
import org.eclipse.persistence.sessions.coordination.MetadataRefreshListener;
import org.eclipse.persistence.sessions.serializers.Serializer;

/**
 * Implementation of org.eclipse.persistence.sessions.Session
 * The public interface should be used.
 * @see org.eclipse.persistence.sessions.Session
 *
 * <p>
 * <b>Purpose</b>: Define the interface and common protocol of an EclipseLink compliant session.
 * <p>
 * <b>Description</b>: The session is the primary interface into EclipseLink,
 * the application should do all of its reading and writing of objects through the session.
 * The session also manages transactions and units of work.  Normally the session
 * is passed and used by the application controller objects.  Controller objects normally
 * sit behind the GUI and perform the business processes required for the application,
 * they should perform all explicit database access and database access should be avoided from
 * the domain object model.  Do not use a globally accessible session instance, doing so does
 * not allow for multiple sessions.  Multiple sessions may required when performing things like
 * data migration or multiple database access, as well the unit of work feature requires the usage
 * of multiple session instances.  Although session is abstract, any users of its subclasses
 * should only cast the variables to Session to allow usage of any of its subclasses.
 * <p>
 * <b>Responsibilities</b>:
 *    <ul>
 *    <li> Connecting/disconnecting.
 *    <li> Reading and writing objects.
 *    <li> Transaction and unit of work support.
 *    <li> Identity maps and caching.
 *    </ul>
 * @see DatabaseSessionImpl
 */
public abstract class AbstractSession extends CoreAbstractSession<ClassDescriptor, Login, Platform, Project, SessionEventManager> implements org.eclipse.persistence.sessions.Session, CommandProcessor, java.io.Serializable, java.lang.Cloneable {
    /** ExceptionHandler handles database exceptions. */
    transient protected ExceptionHandler exceptionHandler;

    /** IntegrityChecker catch all the descriptor Exceptions.  */
    transient protected IntegrityChecker integrityChecker;

    /** The project stores configuration information, such as the descriptors and login. */
    transient protected Project project;

    /** Ensure mutual exclusion of the session's transaction state across multiple threads.*/
    transient protected ConcurrencyManager transactionMutex;

    /** Manages the live object cache.*/
    protected IdentityMapAccessor identityMapAccessor;

    /** If Transactions were externally started */
    protected boolean wasJTSTransactionInternallyStarted;

    /** The connection to the data store. */
    transient protected Collection<Accessor> accessors;

    /** Allow the datasource platform to be cached. */
    transient protected Platform platform;

    /** Stores predefine reusable queries.*/
    transient protected Map<String, List<DatabaseQuery>> queries;
    
    /**
     * Stores predefined reusable AttributeGroups.
     */
    protected Map<String, AttributeGroup> attributeGroups;

    /** Stores predefined not yet parsed JPQL queries.*/
    protected boolean jpaQueriesProcessed = false;

    /** Resolves referential integrity on commits. */
    transient protected CommitManager commitManager;

    /** Tool that log performance information. */
    transient protected SessionProfiler profiler;

    /** Support being owned by a session broker. */
    transient protected AbstractSession broker;

    /** Used to identify a session when using the session broker. */
    protected String name;

    /** Keep track of active units of work. */
    transient protected int numberOfActiveUnitsOfWork;

    /**
     * This collection will be used to store those objects that are currently locked
     * for the clone process. It should be populated with an EclipseLinkIdentityHashMap
     */
    protected Map objectsLockedForClone;
    
    /** Destination for logged messages and SQL. */
    transient protected SessionLog sessionLog;

    /** When logging the name of the session is typed: class name + system hashcode. */
    transient protected String logSessionString;

    /** Stores the event listeners for this session. */
    transient protected SessionEventManager eventManager;

    /** Allow for user defined properties. */
    protected Map<Object, Object> properties;

    /** Delegate that handles synchronizing a UnitOfWork with an external transaction. */
    transient protected ExternalTransactionController externalTransactionController;

    /** Last descriptor accessed, use to optimize descriptor lookup. */
    transient protected ClassDescriptor lastDescriptorAccessed;
    
    /** PERF: cache descriptors from project. */
    transient protected Map<Class, ClassDescriptor> descriptors;
    
    /** PERF: cache table per tenant descriptors needing to be initialized per EM */
    transient protected List<ClassDescriptor> tablePerTenantDescriptors;
    
    /** PERF: cache table per tenant queries needing to be initialized per EM */
    transient protected List<DatabaseQuery> tablePerTenantQueries;

    // bug 3078039: move EJBQL alias > descriptor map from Session to Project (MWN)

    /** Used to determine If a session is in a Broker or not */
    protected boolean isInBroker;

    /**
     * Used to connect this session to EclipseLink cluster for distributed command
     */
    transient protected CommandManager commandManager;

    /**
     * PERF: Cache the write-lock check to avoid cost of checking in every register/clone.
     */
    protected boolean shouldCheckWriteLock;

    /**
     * Determined whether changes should be propagated to an EclipseLink cluster
     */
    protected boolean shouldPropagateChanges;

    /** Used to determine If a session is in a profile or not */
    protected boolean isInProfile;

    /** PERF: Quick check if logging is OFF entirely. */
    protected boolean isLoggingOff;

    /** PERF: Allow for finalizers to be enabled, currently enables client-session finalize. */
    protected boolean isFinalizersEnabled = false;

    /** List of active command threads. */
    transient protected ExposedNodeLinkedList activeCommandThreads;

    /**
     * Indicates whether the session is synchronized.
     * In case external transaction controller is used isSynchronized==true means
     * the session's jta connection will be freed during external transaction callback.
     */
    protected boolean isSynchronized;

    /**
     *  Stores the default reference mode that a UnitOfWork will use when referencing
     *  managed objects.
     *  @see org.eclipse.persistence.config.ReferenceMode
     */
    protected ReferenceMode defaultReferenceMode = null;

    /**
     * Default pessimistic lock timeout value.
     */
    protected Integer pessimisticLockTimeoutDefault;

    protected int queryTimeoutDefault;
    
    /** Allow a session to enable concurrent processing. */
    protected boolean isConcurrent = false;
    
    /**
     * This map will hold onto class to static metamodel class references from JPA.
     */
    protected Map<String, String> staticMetamodelClasses;
    
    /** temporarily holds a list of events that must be fired after the current operation completes. 
     *  Initialy created for postClone events.
     */
    protected List<DescriptorEvent> deferredEvents;
    
    /** records that the UOW is executing deferred events.  Events could cause operations to occur that may attempt to restart the event execution.  This must be avoided*/
    protected boolean isExecutingEvents = false;

    /** Allow queries to be targeted at specific connection pools. */
    protected PartitioningPolicy partitioningPolicy;

    /** the MetadataRefreshListener is used with RCM to force a refresh of the metadata used within EntityManagerFactoryWrappers */
    protected MetadataRefreshListener metadatalistener;

    /** Stores the set of multitenant context properties this session requires **/
    protected Set<String> multitenantContextProperties;

    /** Store the query builder used to parse JPQL. */
    transient protected JPAQueryBuilder queryBuilder;

    /** Set the Serializer to use by default for serialization. */
    transient protected Serializer serializer;
    
    /** Allow CDI injection of entity listeners **/
    transient protected EntityListenerInjectionManager entityListenerInjectionManager;
    
    /** Indicates whether ObjectLevelReadQuery should by default use ResultSet Access optimization. 
     * If not set then parent's flag is used, is none set then ObjectLevelReadQuery.isResultSetAccessOptimizedQueryDefault is used.
     * If the optimization specified by the session is ignored if incompatible with other query settings. 
     */
    protected Boolean shouldOptimizeResultSetAccess; 
    
    /**
     * INTERNAL:
     * Create and return a new session.
     * This should only be called if the database login information is not know at the time of creation.
     * Normally it is better to call the constructor that takes the login information as an argument
     * so that the session can initialize itself to the platform information given in the login.
     */
    protected AbstractSession() {
        this.name = "";
        initializeIdentityMapAccessor();
        // PERF - move to lazy init (3286091)
        this.numberOfActiveUnitsOfWork = 0;
        this.isInBroker = false;
        this.isSynchronized = false;
    }

    /**
     * INTERNAL:
     * Create a blank session, used for proxy session.
     */
    protected AbstractSession(int nothing) {
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
    public AbstractSession(Login login) {
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
    public AbstractSession(org.eclipse.persistence.sessions.Project project) {
        this();
        this.project = project;
        if (project.getDatasourceLogin() == null) {
            throw ValidationException.projectLoginIsNull(this);
        }
        // add the Project's queries as session queries
        for (DatabaseQuery query : project.getQueries()) {
            addQuery(query.getName(), query);
        }
    }

    /**
     * Return the Serializer to use by default for serialization.
     */
    public Serializer getSerializer() {
        if ((this.serializer == null) && (getParent() != null)) {
            return getParent().getSerializer();
        }
        return serializer;
    }

    /**
     * Set the Serializer to use by default for serialization.
     */
    public void setSerializer(Serializer serializer) {
        this.serializer = serializer;
    }
    
    /**
     * INTERNAL
     * Return the query builder used to parser JPQL.
     */
    public JPAQueryBuilder getQueryBuilder() {
        if (this.queryBuilder == null) {
            AbstractSession parent = getParent();
            if (parent != null) {
                this.queryBuilder = parent.getQueryBuilder();
            } else {
                this.queryBuilder = buildDefaultQueryBuilder();
            }
        }
        return this.queryBuilder;
    }
    
    /**
     * INTERNAL
     * Set the query builder used to parser JPQL.
     */
    public void setQueryBuilder(JPAQueryBuilder queryBuilder) {
        this.queryBuilder = queryBuilder;
    }
    
    /**
     * INTERNAL
     * Build the JPQL builder based on session properties.
     */
    protected JPAQueryBuilder buildDefaultQueryBuilder() {
        String queryBuilderClassName = (String)getProperty(PersistenceUnitProperties.JPQL_PARSER);
        if (queryBuilderClassName == null) {
            queryBuilderClassName = "org.eclipse.persistence.internal.jpa.jpql.HermesParser";
            //queryBuilderClassName = "org.eclipse.persistence.queries.ANTLRQueryBuilder";
        }
        String validation = (String)getProperty(PersistenceUnitProperties.JPQL_VALIDATION);
        JPAQueryBuilder builder = null;
        try {
            Class parserClass = null;
            // use class.forName() to avoid loading parser classes for JAXB
            // Use Class.forName not thread class loader to avoid class loader issues.
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()){
                try {
                    parserClass = (Class)AccessController.doPrivileged(new PrivilegedClassForName(queryBuilderClassName));
                } catch (PrivilegedActionException exception) {
                }
            } else {
                parserClass = PrivilegedAccessHelper.getClassForName(queryBuilderClassName);
            }
            if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
                try {
                    builder = (JPAQueryBuilder)AccessController.doPrivileged(new PrivilegedNewInstanceFromClass(parserClass));
                } catch (PrivilegedActionException exception) {
                }
            } else {
                builder = (JPAQueryBuilder)PrivilegedAccessHelper.newInstanceFromClass(parserClass);
            }
        } catch (Exception e) {
            throw new IllegalStateException("Could not load the JPQL parser class." /* TODO: Localize string */, e);
        }
        if (validation != null) {
            builder.setValidationLevel(validation);
        }
        return builder;
    }
    
    /**
     * INTERNAL:
     * PERF: Used for quick check if logging is OFF entirely.
     */
    public boolean isLoggingOff() {
        return isLoggingOff;
    }

    /**
     * INTERNAL:
     * Called by a sessions queries to obtain individual query ids.
     * CR #2698903
     */
    public long getNextQueryId() {
        return QueryCounter.getCount();
    }

    /**
     * INTERNAL:
     * Return a unit of work for this session not registered with the JTS transaction.
     */
    public UnitOfWorkImpl acquireNonSynchronizedUnitOfWork() {
    	return acquireNonSynchronizedUnitOfWork(null);
    }

    /**
     * INTERNAL:
     * Return a unit of work for this session not registered with the JTS transaction.
     */
    public UnitOfWorkImpl acquireNonSynchronizedUnitOfWork(ReferenceMode referenceMode) {
        setNumberOfActiveUnitsOfWork(getNumberOfActiveUnitsOfWork() + 1);
        UnitOfWorkImpl unitOfWork = new UnitOfWorkImpl(this, referenceMode);
        if (shouldLog(SessionLog.FINER, SessionLog.TRANSACTION)) {
            log(SessionLog.FINER, SessionLog.TRANSACTION, "acquire_unit_of_work_with_argument", String.valueOf(System.identityHashCode(unitOfWork)));
        }
        return unitOfWork;
    }

    /**
     * INTERNAL:
     * Constructs a HistoricalSession given a valid AsOfClause.
     */
    public org.eclipse.persistence.sessions.Session acquireHistoricalSession(AsOfClause clause) throws ValidationException {
        if ((clause == null) || (clause.getValue() == null)) {
            throw ValidationException.cannotAcquireHistoricalSession();
        }
        if (!getProject().hasGenericHistorySupport() && !hasBroker() && ((getPlatform() == null) || !getPlatform().isOracle())) {
            throw ValidationException.historicalSessionOnlySupportedOnOracle();
        }
        return new HistoricalSession(this, clause);
    }

    /**
     * PUBLIC:
     * Return a unit of work for this session.
     * The unit of work is an object level transaction that allows
     * a group of changes to be applied as a unit.
     *
     * @see UnitOfWorkImpl
     */
    public UnitOfWorkImpl acquireUnitOfWork() {
        UnitOfWorkImpl unitOfWork = acquireNonSynchronizedUnitOfWork(getDefaultReferenceMode());
        unitOfWork.registerWithTransactionIfRequired();

        return unitOfWork;
    }

    /**
     * PUBLIC:
     * Return a repeatable write unit of work for this session.
     * A repeatable write unit of work allows multiple writeChanges (flushes).
     *
     * @see RepeatableWriteUnitOfWork
     */
    public RepeatableWriteUnitOfWork acquireRepeatableWriteUnitOfWork(ReferenceMode referenceMode) {
        return new RepeatableWriteUnitOfWork(this, referenceMode);
    }

    /**
     * PUBLIC:
     * Return a unit of work for this session.
     * The unit of work is an object level transaction that allows
     * a group of changes to be applied as a unit.
     *
     * @see UnitOfWorkImpl
     * @param referenceMode The reference type the UOW should use internally when
     * referencing Working clones.  Setting this to WEAK means the UOW will use
     * weak references to reference clones that support active object change
     * tracking and hard references for deferred change tracked objects.
     * Setting to FORCE_WEAK means that all objects will be referenced by weak
     * references and if the application no longer references the clone the
     * clone may be garbage collected.  If the clone
     * has uncommitted changes then those changes will be lost.
     */
    public UnitOfWorkImpl acquireUnitOfWork(ReferenceMode referenceMode) {
        UnitOfWorkImpl unitOfWork = acquireNonSynchronizedUnitOfWork(referenceMode);
        unitOfWork.registerWithTransactionIfRequired();

        return unitOfWork;
    }
   /**
     * PUBLIC:
     * Add an alias for the descriptor
     */
    public void addAlias(String alias, ClassDescriptor descriptor) {
        project.addAlias(alias, descriptor);
    }
    
    /**
     * INTERNAL:
     * Return all pre-defined not yet parsed EJBQL queries.
     */
    public void addJPAQuery(DatabaseQuery query) {
        getProject().addJPAQuery(query);
    }
    
    /**
     * INTERNAL:
     * Return all pre-defined not yet parsed EJBQL multitenant queries.
     */
    public void addJPATablePerTenantQuery(DatabaseQuery query) {
        getProject().addJPATablePerTenantQuery(query);
    }
    
    /**
     * PUBLIC:
     * Return a set of multitenant context properties this session
     */
    public void addMultitenantContextProperty(String contextProperty) {
        getMultitenantContextProperties().add(contextProperty);
    }
    
    /**
     * INTERNAL:
     * Add the query to the session queries.
     */
    protected synchronized void addQuery(DatabaseQuery query, boolean nameMustBeUnique) {
        Vector queriesByName = (Vector)getQueries().get(query.getName());
        if (queriesByName == null) {
            // lazily create Vector in Hashtable.
            queriesByName = org.eclipse.persistence.internal.helper.NonSynchronizedVector.newInstance();
            getQueries().put(query.getName(), queriesByName);
        }

        if (nameMustBeUnique){ // JPA addNamedQuery
            if (queriesByName.size() <= 1){
                queriesByName.clear();
            }else{
                throw new IllegalStateException(ExceptionLocalization.buildMessage("argument_keyed_named_query_with_JPA", new Object[]{query.getName()}));
            }
        }else{
            // Check that we do not already have a query that matched it
            for (Iterator enumtr = queriesByName.iterator(); enumtr.hasNext();) {
                DatabaseQuery existingQuery = (DatabaseQuery)enumtr.next();
                if (Helper.areTypesAssignable(query.getArgumentTypes(), existingQuery.getArgumentTypes())) {
                    throw ValidationException.existingQueryTypeConflict(query, existingQuery);
                }
            }
        }
        queriesByName.add(query);
    }
    
    /**
     * PUBLIC:
     * Add the query to the session queries with the given name.
     * This allows for common queries to be pre-defined, reused and executed by name.
     */
    public void addQuery(String name, DatabaseQuery query) {
        query.setName(name);
        addQuery(query, false);
    }
    
    /**
     * PUBLIC:
     * Add the query to the session queries with the given name.
     * This allows for common queries to be pre-defined, reused and executed by name.
     */
    public void addQuery(String name, DatabaseQuery query, boolean replace) {
        query.setName(name);
        addQuery(query, replace);
    }
    
    /**
     * INTERNAL:
     * Add a metamodel class to model class reference.
     */
    public void addStaticMetamodelClass(String modelClassName, String metamodelClassName) {
        if (staticMetamodelClasses == null) {
            staticMetamodelClasses = new HashMap<String, String>();
        }
        
        staticMetamodelClasses.put(modelClassName, metamodelClassName);
    }

    /**
     * INTERNAL:
     * Add a descriptor that is uses a table per tenant multitenant policy.
     */
    protected void addTablePerTenantDescriptor(ClassDescriptor descriptor) {
        getTablePerTenantDescriptors().add(descriptor);
    }
    
    /**
     * INTERNAL:
     * Add a query that queries a table per tenant entity
     */
    protected void addTablePerTenantQuery(DatabaseQuery query) {
        getTablePerTenantQueries().add(query);
    }
    
    /**
     * INTERNAL:
     * Called by beginTransaction() to start a transaction.
     * This starts a real database transaction.
     * Allows retry if the connection is dead.
     */
    protected void basicBeginTransaction() throws DatabaseException {
        Collection<Accessor> accessors = getAccessors();
        if (accessors == null) {
            return;
    }
        Accessor failedAccessor = null;
        try {
            for (Accessor accessor : accessors) {
                failedAccessor = accessor;
                basicBeginTransaction(accessor);
            }
        } catch (RuntimeException exception) {
            // If begin failed, rollback ones already started.
            for (Accessor accessor : accessors) {
                if (accessor == failedAccessor) {
                    break;
                }
                try {
                    accessor.rollbackTransaction(this);
                } catch (RuntimeException ignore) { }
            }            
            throw exception;
        }
    }

    /**
     * INTERNAL:
     * Called by beginTransaction() to start a transaction.
     * This starts a real database transaction.
     * Allows retry if the connection is dead.
     */
    protected void basicBeginTransaction(Accessor accessor) throws DatabaseException {
        try {
            accessor.beginTransaction(this);
        } catch (DatabaseException databaseException) {
            // Retry if the failure was communication based?  (i.e. timeout, database down, can no longer ping)
            if ((!getDatasourceLogin().shouldUseExternalTransactionController()) && databaseException.isCommunicationFailure()) {
                DatabaseException exceptionToThrow = databaseException;
                Object[] args = new Object[1];
                args[0] = databaseException;
                log(SessionLog.INFO, "communication_failure_attempting_begintransaction_retry", args, null);
                // Attempt to reconnect connection.
                exceptionToThrow = retryTransaction(accessor, databaseException, 0, this);
                if (exceptionToThrow == null) {
                    // Retry was a success.
                    return;
                }
                handleException(exceptionToThrow);
            } else {
                handleException(databaseException);
            }
        } catch (RuntimeException exception) {
            handleException(exception);
        }
    }

    /**
     * INTERNAL:
     * A begin transaction failed.
     * Re-connect and retry the begin transaction.
     */
    public DatabaseException retryTransaction(Accessor accessor, DatabaseException databaseException, int retryCount, AbstractSession executionSession) {
        DatabaseException exceptionToThrow = databaseException;
        // Attempt to reconnect connection.
        int count = getLogin().getQueryRetryAttemptCount();
        while (retryCount < count) {
            try {
                // if database session then re-establish
                // connection
                // else the session will just get a new
                // connection from the pool
                accessor.reestablishConnection(this);
                accessor.beginTransaction(this);
                return null;
            } catch (DatabaseException newException) {
                // Need to use last exception, as may not be a communication exception.
                exceptionToThrow = newException;
                // failed to get connection because of
                // database error.
                ++retryCount;
                try {
                    // Give the failover time to recover.
                    Thread.currentThread().sleep(getLogin().getDelayBetweenConnectionAttempts());
                    Object[] args = new Object[1];
                    args[0] = databaseException;
                    log(SessionLog.INFO, "communication_failure_attempting_begintransaction_retry", args, null);
                } catch (InterruptedException intEx) {
                    break;
                }
            }
        }
        return exceptionToThrow;
    }
    
    /**
     * INTERNAL:
     * Called in the end of beforeCompletion of external transaction synchronization listener.
     * Close the managed sql connection corresponding to the external transaction,
     * if applicable releases accessor.
     */
    public void releaseJTSConnection() {
    }

    /**
     * INTERNAL:
     * Called by commitTransaction() to commit a transaction.
     * This commits the active transaction.
     */
    protected void basicCommitTransaction() throws DatabaseException {
        Collection<Accessor> accessors = getAccessors();
        if (accessors == null) {
            return;
        }
        try {
            for (Accessor accessor : accessors) {
                accessor.commitTransaction(this);
            }
        } catch (RuntimeException exception) {
            // Leave transaction uncommitted as rollback should be called.
            handleException(exception);
        }
    }

    /**
     * INTERNAL:
     * Called by rollbackTransaction() to rollback a transaction.
     * This rolls back the active transaction.
     */
    protected void basicRollbackTransaction() throws DatabaseException {
        Collection<Accessor> accessors = getAccessors();
        if (accessors == null) {
            return;
        }
        RuntimeException exception = null;
        for (Accessor accessor : accessors) {
        try {
                accessor.rollbackTransaction(this);
            } catch (RuntimeException failure) {
                exception = failure;
            }
        }
        if (exception != null) {
            handleException(exception);
        }
    }

    /**
     * INTERNAL:
     * Attempts to begin an external transaction.
     * Returns true only in one case -
     * external transaction has been internally started during this method call:
     * wasJTSTransactionInternallyStarted()==false in the beginning of this method and
     * wasJTSTransactionInternallyStarted()==true in the end of this method.
     */
    public boolean beginExternalTransaction() {
        boolean externalTransactionHasBegun = false;
        if (hasExternalTransactionController() && !wasJTSTransactionInternallyStarted()) {
            try {
                getExternalTransactionController().beginTransaction(this);
            } catch (RuntimeException exception) {
                handleException(exception);
            }
            if (wasJTSTransactionInternallyStarted()) {
                externalTransactionHasBegun = true;
                log(SessionLog.FINER, SessionLog.TRANSACTION, "external_transaction_has_begun_internally");
            }
        }
        return externalTransactionHasBegun;
    }

    /**
     * PUBLIC:
     * Begin a transaction on the database.
     * This allows a group of database modification to be committed or rolled back as a unit.
     * All writes/deletes will be sent to the database be will not be visible to other users until commit.
     * Although databases do not allow nested transaction,
     * EclipseLink supports nesting through only committing to the database on the outer commit.
     *
     * @exception DatabaseException if the database connection is lost or the begin is rejected.
     * @exception ConcurrencyException if this session's transaction is acquired by another thread and a timeout occurs.
     *
     * @see #isInTransaction()
     */
    public void beginTransaction() throws DatabaseException, ConcurrencyException {
        ConcurrencyManager mutex = getTransactionMutex();
        // If there is no db transaction in progress
        // beginExternalTransaction() starts an external transaction -
        // provided externalTransactionController is used, and there is
        // no active external transaction - so we have to start one internally.
        if (!mutex.isAcquired()) {
            beginExternalTransaction();
        }
        // For unit of work and client session multi threading is allowed as they are a context,
        // this is required for JTS/RMI/CORBA/EJB stuff where the server thread can be different across calls.
        if (isClientSession()) {
            mutex.setActiveThread(Thread.currentThread());
        }

        // Ensure mutual exclusion and call subclass specific begin.
        mutex.acquire();
        if (!mutex.isNested()) {
            if (this.eventManager != null) {
                this.eventManager.preBeginTransaction();
            }
            basicBeginTransaction();
            if (this.eventManager != null) {
                this.eventManager.postBeginTransaction();
            }
        }
    }


    /**
     * Check to see if the descriptor of a superclass can be used to describe this class
     *
     * @param Class
     * @return ClassDescriptor
     */
    protected ClassDescriptor checkHierarchyForDescriptor(Class theClass){
        return getDescriptor(theClass.getSuperclass());
    }

    /**
     * allow the entity listener injection manager to clean itself up.
     */
    public void cleanUpEntityListenerInjectionManager(){
        if (entityListenerInjectionManager != null){
            entityListenerInjectionManager.cleanUp(this);
        }
    }
    
    /**
     * PUBLIC:
     * clear the integrityChecker. IntegrityChecker holds all the ClassDescriptor Exceptions.
     */
    public void clearIntegrityChecker() {
        setIntegrityChecker(null);
    }

    /**
     * INTERNAL:
     * Clear the the lastDescriptorAccessed cache.
     */
    public void clearLastDescriptorAccessed() {
        this.lastDescriptorAccessed = null;
    }

    /**
     * INTERNAL:
     * Clear the the descriptors cache.
     */
    public void clearDescriptors() {
        this.descriptors = null;
    }

    /**
     * PUBLIC:
     * Clear the profiler, this will end the current profile operation.
     */
    public void clearProfile() {
        setProfiler(null);
    }

    /**
     * INTERNAL:
     * Clones the descriptor
     */
    public Object clone() {
        // An alternative to this process should be found
        try {
            return super.clone();
        } catch (Exception exception) {
            return null;
        }
    }

    /**
     * INTERNAL:
     * Attempts to commit the running internally started external transaction.
     * Returns true only in one case -
     * external transaction has been internally committed during this method call:
     * wasJTSTransactionInternallyStarted()==true in the beginning of this method and
     * wasJTSTransactionInternallyStarted()==false in the end of this method.
     */
    public boolean commitExternalTransaction() {
        boolean externalTransactionHasCommitted = false;
        if (hasExternalTransactionController() && wasJTSTransactionInternallyStarted()) {
            try {
                getExternalTransactionController().commitTransaction(this);
            } catch (RuntimeException exception) {
                handleException(exception);
            }
            if (!wasJTSTransactionInternallyStarted()) {
                externalTransactionHasCommitted = true;
                log(SessionLog.FINER, SessionLog.TRANSACTION, "external_transaction_has_committed_internally");
            }
        }
        return externalTransactionHasCommitted;
    }

    /**
     * PUBLIC:
     * Commit the active database transaction.
     * This allows a group of database modification to be committed or rolled back as a unit.
     * All writes/deletes will be sent to the database be will not be visible to other users until commit.
     * Although databases do not allow nested transaction,
     * EclipseLink supports nesting through only committing to the database on the outer commit.
     *
     * @exception DatabaseException most databases validate changes as they are done,
     * normally errors do not occur on commit unless the disk fails or the connection is lost.
     * @exception ConcurrencyException if this session is not within a transaction.
     */
    public void commitTransaction() throws DatabaseException, ConcurrencyException {
        ConcurrencyManager mutex = getTransactionMutex();
        // Release mutex and call subclass specific commit.
        if (!mutex.isNested()) {
            if (this.eventManager != null) {
                this.eventManager.preCommitTransaction();
            }
            basicCommitTransaction();
            if (this.eventManager != null) {
                this.eventManager.postCommitTransaction();
            }
        }

        // This MUST not be in a try catch or finally as if the commit failed the transaction is still open.
        mutex.release();

        // If there is no db transaction in progress
        // if there is an active external transaction
        // which was started internally - it should be committed internally, too.
        if (!mutex.isAcquired()) {
            commitExternalTransaction();
        }
    }

    /**
     * INTERNAL:
     * Return if the two object match completely.
     * This checks the objects attributes and their private parts.
     */
    public boolean compareObjects(Object firstObject, Object secondObject) {
        if ((firstObject == null) && (secondObject == null)) {
            return true;
        }

        if ((firstObject == null) || (secondObject == null)) {
            return false;
        }

        if (!(firstObject.getClass().equals(secondObject.getClass()))) {
            return false;
        }

        ObjectBuilder builder = getDescriptor(firstObject.getClass()).getObjectBuilder();

        return builder.compareObjects(builder.unwrapObject(firstObject, this), builder.unwrapObject(secondObject, this), this);
    }

    /**
     * TESTING:
     * Return true if the object do not match.
     * This checks the objects attributes and their private parts.
     */
    public boolean compareObjectsDontMatch(Object firstObject, Object secondObject) {
        return !this.compareObjects(firstObject, secondObject);
    }

    /**
     * PUBLIC:
     * Return true if the pre-defined query is defined on the session.
     */
    public boolean containsQuery(String queryName) {
        return getQueries().containsKey(queryName);
    }

    /**
     * PUBLIC:
     * Return a complete copy of the object or of collection of objects.
     * In case of collection all members should be either entities of the same type
     * or have a common inheritance hierarchy mapped root class.
     * This can be used to obtain a scratch copy of an object,
     * or for templatizing an existing object into another new object.
     * The object and all of its privately owned parts will be copied.
     *
     * @see #copy(Object, AttributeGroup)
     */
    public Object copy(Object originalObjectOrObjects) {
        return copy(originalObjectOrObjects, new CopyGroup());
    }

    /**
     * PUBLIC:
     * Return a complete copy of the object or of collection of objects.
     * In case of collection all members should be either entities of the same type
     * or have a common inheritance hierarchy mapped root class.
     * This can be used to obtain a scratch copy of an object,
     * or for templatizing an existing object into another new object.
     * If there are no attributes in the group 
     * then the object and all of its privately owned parts will be copied.
     * Otherwise only the attributes included into the group will be copied.
     */
    public Object copy(Object originalObjectOrObjects, AttributeGroup group) {
        if (originalObjectOrObjects == null) {
            return null;
        }

        CopyGroup copyGroup = group.toCopyGroup();
        copyGroup.setSession(this);
        if(originalObjectOrObjects instanceof Collection) {
            // it's a collection - make sure all elements use the same instance of CopyGroup.
            Collection originalCollection = (Collection)originalObjectOrObjects;
            Collection copies;
            if(originalCollection instanceof List) {
                copies = new ArrayList();
            } else {
                copies = new HashSet();
            }
            Iterator it = originalCollection.iterator();
            while(it.hasNext()) {
                copies.add(copyInternal(it.next(), copyGroup));
            }
            return copies;
        }

        // it's not a collection
        return copyInternal(originalObjectOrObjects, copyGroup);
    }

    /**
     * INTERNAL:
     */
    public Object copyInternal(Object originalObject, CopyGroup copyGroup) {
        if (originalObject == null) {
            return null;
        }

        ClassDescriptor descriptor = getDescriptor(originalObject);
        if (descriptor == null) {
            return originalObject;
        }

        return descriptor.getObjectBuilder().copyObject(originalObject, copyGroup);
    }

    /**
     * PUBLIC:
     * Return a complete copy of the object.
     * This can be used to obtain a scratch copy of an object,
     * or for templatizing an existing object into another new object.
     * The object and all of its privately owned parts will be copied, the object's primary key will be reset to null.
     *
     * @see #copyObject(Object, ObjectCopyingPolicy)
     * @deprecated since EclipseLink 2.1, replaced by copy(Object)
     * @see #copy(Object)
     */
    public Object copyObject(Object original) {
        CopyGroup copyGroup = new CopyGroup();
        copyGroup.setShouldResetPrimaryKey(true);
        return copy(original, copyGroup);
    }

    /**
     * PUBLIC:
     * Return a complete copy of the object.
     * This can be used to obtain a scratch copy of an object,
     * or for templatizing an existing object into another new object.
     * The object copying policy allow for the depth, and reseting of the primary key to null, to be specified.
     * @deprecated since EclipseLink 2.1, replaced by copy(Object, AttributeGroup)
     * @see #copy(Object, AttributeGroup)
     */
    public Object copyObject(Object original, ObjectCopyingPolicy policy) {
        return copy(original, policy);
    }

    /**
     * INTERNAL:
     * Copy the read only classes from the unit of work
     *
     * Added Nov 8, 2000 JED for Patch 2.5.1.8
     * Ref: Prs 24502
     */
    public Vector copyReadOnlyClasses() {
        return getDefaultReadOnlyClasses();
    }

    public DatabaseValueHolder createCloneQueryValueHolder(ValueHolderInterface attributeValue, Object clone, AbstractRecord row, ForeignReferenceMapping mapping) {
        return new ProtectedValueHolder(attributeValue, mapping, this);
    }

    public DatabaseValueHolder createCloneTransformationValueHolder(ValueHolderInterface attributeValue, Object original, Object clone, AbstractTransformationMapping mapping) {
        return new ProtectedValueHolder(attributeValue, mapping, this);
    }

    public EntityListenerInjectionManager createEntityListenerInjectionManager(){
        try{
            return (EntityListenerInjectionManager)Class.forName(EntityListenerInjectionManager.DEFAULT_CDI_INJECTION_MANAGER, true, getLoader()).newInstance();
        } catch (Exception e){
            logThrowable(SessionLog.FINEST, SessionLog.JPA, e);
        }
        return new DisabledEntityListenerInjectionManager();
    }
    
    /**
     * INTERNAL:
     * This method is similar to getAndCloneCacheKeyFromParent.  It purpose is to get protected cache data from the shared cache and
     * build/return a protected instance.
     */
    public Object createProtectedInstanceFromCachedData(Object cached, Integer refreshCascade, ClassDescriptor descriptor){
        CacheKey localCacheKey = getIdentityMapAccessorInstance().getCacheKeyForObject(cached);
        if (localCacheKey != null && localCacheKey.getObject() != null){
            return localCacheKey.getObject();
        }
        boolean identityMapLocked = this.shouldCheckWriteLock && getParent().getIdentityMapAccessorInstance().acquireWriteLock();
        boolean rootOfCloneRecursion = false;
        CacheKey cacheKey = getParent().getIdentityMapAccessorInstance().getCacheKeyForObject(cached);
        try{
            Object key = null;
            Object lockValue = null;
            long readTime = 0; 
            if (cacheKey != null){
                if (identityMapLocked) {
                    checkAndRefreshInvalidObject(cached, cacheKey, descriptor);
                } else {
                    // Check if we have locked all required objects already.
                    if (this.objectsLockedForClone == null) {
                        // PERF: If a simple object just acquire a simple read-lock.
                        if (descriptor.shouldAcquireCascadedLocks()) {
                            this.objectsLockedForClone = getParent().getIdentityMapAccessorInstance().getWriteLockManager().acquireLocksForClone(cached, descriptor, cacheKey, this);
                        } else {
                            checkAndRefreshInvalidObject(cached, cacheKey, descriptor);
                            cacheKey.acquireReadLock();
                        }
                        rootOfCloneRecursion = true;
                    }
                }
                key = cacheKey.getKey();
                lockValue = cacheKey.getWriteLockValue();
                readTime = cacheKey.getReadTime();
            }
            if (descriptor.hasInheritance()){
                descriptor = this.getClassDescriptor(cached.getClass());
            }
            ObjectBuilder builder = descriptor.getObjectBuilder();
            Object workingClone = builder.instantiateWorkingCopyClone(cached, this);
            // PERF: Cache the primary key if implements PersistenceEntity.
            builder.populateAttributesForClone(cached, cacheKey,  workingClone, refreshCascade, this);
            getIdentityMapAccessorInstance().putInIdentityMap(workingClone, key, lockValue, readTime, descriptor);
            return workingClone;
        }finally{
            if (rootOfCloneRecursion){
                if (this.objectsLockedForClone == null && cacheKey != null) {
                    cacheKey.releaseReadLock();
                } else {                        
                    for (Iterator iterator = this.objectsLockedForClone.values().iterator(); iterator.hasNext();) {
                        ((CacheKey)iterator.next()).releaseReadLock();
                    }
                    this.objectsLockedForClone = null;
                }
                executeDeferredEvents();
            }
        }
    }

        /**
         * INTERNAL:
         * Check if the object is invalid and refresh it.
         * This is used to ensure that no invalid objects are registered.
         */
        public void checkAndRefreshInvalidObject(Object object, CacheKey cacheKey, ClassDescriptor descriptor) {
            if (isConsideredInvalid(object, cacheKey, descriptor)) {
                ReadObjectQuery query = new ReadObjectQuery();
                query.setReferenceClass(object.getClass());
                query.setSelectionId(cacheKey.getKey());
                query.refreshIdentityMapResult();
                query.setIsExecutionClone(true);
                this.executeQuery(query);
            }
        }

        /**
         * INTERNAL:
         * Check if the object is invalid and *should* be refreshed.
         * This is used to ensure that no invalid objects are cloned.
         */
        public boolean isConsideredInvalid(Object object, CacheKey cacheKey, ClassDescriptor descriptor) {
            if (cacheKey.getObject() != null) {
                CacheInvalidationPolicy cachePolicy = descriptor.getCacheInvalidationPolicy();
                // BUG#6671556 refresh invalid objects when accessed in the unit of work.
                return (cachePolicy.shouldRefreshInvalidObjectsOnClone() && cachePolicy.isInvalidated(cacheKey));
            }
            return false;
        }

    /**
     * INTERNAL:
     * Add an event to the deferred list.  Events will be fired after the operation completes
     */
    public void deferEvent(DescriptorEvent event){
        if (this.deferredEvents == null){
            this.deferredEvents = new ArrayList<DescriptorEvent>();
        }
        this.deferredEvents.add(event);
    }

    /**
     * PUBLIC:
     * delete all of the objects and all of their privately owned parts in the database.
     * The allows for a group of objects to be deleted as a unit.
     * The objects will be deleted through a single transactions.
     *
     * @exception DatabaseException if an error occurs on the database,
     * these include constraint violations, security violations and general database errors.
     * @exception OptimisticLockException if the object's descriptor is using optimistic locking and
     * the object has been updated or deleted by another user since it was last read.
     */
    public void deleteAllObjects(Collection domainObjects) throws DatabaseException, OptimisticLockException {
        for (Iterator objectsEnum = domainObjects.iterator(); objectsEnum.hasNext();) {
            deleteObject(objectsEnum.next());
        }
    }

    /**
     * PUBLIC:
     * delete all of the objects and all of their privately owned parts in the database.
     * The allows for a group of objects to be deleted as a unit.
     * The objects will be deleted through a single transactions.
     *
     * @exception DatabaseException if an error occurs on the database,
     * these include constraint violations, security violations and general database errors.
     * @exception OptimisticLockException if the object's descriptor is using optimistic locking and
     * the object has been updated or deleted by another user since it was last read.
     */
    @Deprecated
    public void deleteAllObjects(Vector domainObjects) throws DatabaseException, OptimisticLockException {
        for (Enumeration objectsEnum = domainObjects.elements(); objectsEnum.hasMoreElements();) {
            deleteObject(objectsEnum.nextElement());
        }
    }

    /**
     * PUBLIC:
     * Delete the object and all of its privately owned parts from the database.
     * The delete operation can be customized through using a delete query.
     *
     * @exception DatabaseException if an error occurs on the database,
     * these include constraint violations, security violations and general database errors.
     * An database error is not raised if the object is already deleted or no rows are effected.
     * @exception OptimisticLockException if the object's descriptor is using optimistic locking and
     * the object has been updated or deleted by another user since it was last read.
     *
     * @see DeleteObjectQuery
     */
    public Object deleteObject(Object domainObject) throws DatabaseException, OptimisticLockException {
        DeleteObjectQuery query = new DeleteObjectQuery();
        query.setObject(domainObject);
        query.setIsExecutionClone(true);
        return executeQuery(query);
    }

    /**
     * PUBLIC:
     * Return if the object exists on the database or not.
     * This always checks existence on the database.
     */
    public boolean doesObjectExist(Object object) throws DatabaseException {
        DoesExistQuery query = new DoesExistQuery();
        query.setObject(object);
        query.checkDatabaseForDoesExist();
        query.setIsExecutionClone(true);
        return ((Boolean)executeQuery(query)).booleanValue();
    }

    /**
     * PUBLIC:
     * Turn off logging
     */
    public void dontLogMessages() {
        setLogLevel(SessionLog.OFF);
    }

    /**
     * INTERNAL:
     * End the operation timing.
     */
    public void endOperationProfile(String operationName) {
        if (this.isInProfile) {
            getProfiler().endOperationProfile(operationName);
        }
    }

    /**
     * INTERNAL:
     * End the operation timing.
     */
    public void endOperationProfile(String operationName, DatabaseQuery query, int weight) {
        if (this.isInProfile) {
            getProfiler().endOperationProfile(operationName, query, weight);
        }
    }

    /**
     * INTERNAL:
     * Updates the value of SessionProfiler state
     */
    public void updateProfile(String operationName, Object value) {
        if (this.isInProfile) {
            getProfiler().update(operationName, value);
        }
    }

    /**
     * INTERNAL:
     * Set the table per tenant. This should be called per client session after 
     * the start of a transaction. From JPA this method is called on the entity 
     * manager by setting the multitenant table per tenant property.
     */
    public void updateTablePerTenantDescriptors(String property, Object value) {
        // When all the table per tenant descriptors are set, we should initialize them.
        boolean shouldInitializeDescriptors = true;
        
        for (ClassDescriptor descriptor : getTablePerTenantDescriptors()) {
            TablePerMultitenantPolicy policy = (TablePerMultitenantPolicy) descriptor.getMultitenantPolicy();
            
            if ((! policy.hasContextTenant()) && policy.usesContextProperty(property)) {
                policy.setContextTenant((String) value);
            }
            
            shouldInitializeDescriptors = shouldInitializeDescriptors && policy.hasContextTenant();
        }
        
        if (shouldInitializeDescriptors) {
            // Now that the correct tables are set on all table per tenant 
            // descriptors, we can go through the initialization phases safely.
            try {
                // First initialize basic properties (things that do not depend on anything else)
                for (ClassDescriptor descriptor : tablePerTenantDescriptors) {
                    descriptor.preInitialize(this);
                }

                // Second initialize basic mappings
                for (ClassDescriptor descriptor : tablePerTenantDescriptors) {
                    descriptor.initialize(this);
                }

                // Third initialize child dependencies
                for (ClassDescriptor descriptor : tablePerTenantDescriptors) {
                    descriptor.postInitialize(this);
                }

                if (getIntegrityChecker().hasErrors()) {
                    handleSevere(new IntegrityException(getIntegrityChecker()));
                }
            } finally {
                clearIntegrityChecker();
            }

            getCommitManager().initializeCommitOrder();
            
            // If we have table per tenant queries, initialize and add them now
            // once all the descriptors have been initialized.
            if (hasTablePerTenantQueries()) {
                for (DatabaseQuery query : getTablePerTenantQueries()) {
                    processJPAQuery(query);
                }
            }
        }
    }
    
    /**
     * INTERNAL:
     * Updates the count of SessionProfiler event
     */
    public void incrementProfile(String operationName) {
        if (this.isInProfile) {
            getProfiler().occurred(operationName, this);
        }
    }

    /**
     * INTERNAL:
     * Updates the count of SessionProfiler event
     */
    public void incrementProfile(String operationName, DatabaseQuery query) {
        if (this.isInProfile) {
            getProfiler().occurred(operationName, query, this);
        }
    }

    /**
     * INTERNAL:
     * Causes any deferred events to be fired.  Called after operation completes
     */
    public void executeDeferredEvents(){
        if (!this.isExecutingEvents && this.deferredEvents != null) {
            this.isExecutingEvents = true;
            try {
                for (int i = 0; i < this.deferredEvents.size(); ++i) { 
                    // the size is checked every time here because the list may grow
                    DescriptorEvent event = this.deferredEvents.get(i);
                    event.getDescriptor().getEventManager().executeEvent(event);
                }
                this.deferredEvents.clear();
            } finally {
                this.isExecutingEvents = false;
            }
        }
    }
    

    /**
     * INTERNAL:
     * Overridden by subclasses that do more than just execute the call.
     * Executes the call directly on this session and does not check which
     * session it should have executed on.
     */
    public Object executeCall(Call call, AbstractRecord translationRow, DatabaseQuery query) throws DatabaseException {
        if (query.getAccessors() == null) {
            query.setAccessors(getAccessors());
        }
        try {
            return basicExecuteCall(call, translationRow, query);
        } finally {
            if (call.isFinished()) {
                query.setAccessors(null);
            }
        }
    }
    
    /**
     * INTERNAL:
     * Release (if required) connection after call.
     * @param query
     * @return
     */
    public void releaseConnectionAfterCall(DatabaseQuery query) {
    }

    /**
     * PUBLIC:
     * Execute the call on the database.
     * The row count is returned.
     * The call can be a stored procedure call, SQL call or other type of call.
     * <p>Example:
     * <p>session.executeNonSelectingCall(new SQLCall("Delete from Employee");
     *
     * @see #executeSelectingCall(Call)
     */
    public int executeNonSelectingCall(Call call) throws DatabaseException {
        DataModifyQuery query = new DataModifyQuery();
        query.setIsExecutionClone(true);
        query.setCall(call);
        Integer value = (Integer)executeQuery(query);
        if (value == null) {
            return 0;
        } else {
            return value.intValue();
        }
    }
    
    /**
     * PUBLIC:
     * Execute the sql on the database.
     * <p>Example:
     * <p>session.executeNonSelectingSQL("Delete from Employee");
     * @see #executeNonSelectingCall(Call)
	 * Warning: Allowing an unverified SQL string to be passed into this
	 * method makes your application vulnerable to SQL injection attacks.
     */
    public void executeNonSelectingSQL(String sqlString) throws DatabaseException {
        executeNonSelectingCall(new SQLCall(sqlString));
    }

    /**
     * PUBLIC:
     * Execute the pre-defined query by name and return the result.
     * Queries can be pre-defined and named to allow for their reuse.
     *
     * @see #addQuery(String, DatabaseQuery)
     */
    public Object executeQuery(String queryName) throws DatabaseException {
        DatabaseQuery query = getQuery(queryName);

        if (query == null) {
            throw QueryException.queryNotDefined(queryName);
        }

        return executeQuery(query);
    }

    /**
     * PUBLIC:
     * Execute the pre-defined query by name and return the result.
     * Queries can be pre-defined and named to allow for their reuse.
     * The class is the descriptor in which the query was pre-defined.
     *
     * @see DescriptorQueryManager#addQuery(String, DatabaseQuery)
     */
    public Object executeQuery(String queryName, Class domainClass) throws DatabaseException {
        ClassDescriptor descriptor = getDescriptor(domainClass);

        if (descriptor == null) {
            throw QueryException.descriptorIsMissingForNamedQuery(domainClass, queryName);
        }

        DatabaseQuery query = descriptor.getQueryManager().getQuery(queryName);

        if (query == null) {
            throw QueryException.queryNotDefined(queryName, domainClass);
        }

        return executeQuery(query);
    }

    /**
     * PUBLIC:
     * Execute the pre-defined query by name and return the result.
     * Queries can be pre-defined and named to allow for their reuse.
     * The class is the descriptor in which the query was pre-defined.
     *
     * @see DescriptorQueryManager#addQuery(String, DatabaseQuery)
     */
    public Object executeQuery(String queryName, Class domainClass, Object arg1) throws DatabaseException {
        Vector argumentValues = new Vector();
        argumentValues.addElement(arg1);
        return executeQuery(queryName, domainClass, argumentValues);
    }

    /**
     * PUBLIC:
     * Execute the pre-defined query by name and return the result.
     * Queries can be pre-defined and named to allow for their reuse.
     * The class is the descriptor in which the query was pre-defined.
     *
     * @see DescriptorQueryManager#addQuery(String, DatabaseQuery)
     */
    public Object executeQuery(String queryName, Class domainClass, Object arg1, Object arg2) throws DatabaseException {
        Vector argumentValues = new Vector();
        argumentValues.addElement(arg1);
        argumentValues.addElement(arg2);
        return executeQuery(queryName, domainClass, argumentValues);
    }

    /**
     * PUBLIC:
     * Execute the pre-defined query by name and return the result.
     * Queries can be pre-defined and named to allow for their reuse.
     * The class is the descriptor in which the query was pre-defined.
     *
     * @see DescriptorQueryManager#addQuery(String, DatabaseQuery)
     */
    public Object executeQuery(String queryName, Class domainClass, Object arg1, Object arg2, Object arg3) throws DatabaseException {
        Vector argumentValues = new Vector();
        argumentValues.addElement(arg1);
        argumentValues.addElement(arg2);
        argumentValues.addElement(arg3);
        return executeQuery(queryName, domainClass, argumentValues);
    }

    /**
     * PUBLIC:
     * Execute the pre-defined query by name and return the result.
     * Queries can be pre-defined and named to allow for their reuse.
     * The class is the descriptor in which the query was pre-defined.
     *
     * @see DescriptorQueryManager#addQuery(String, DatabaseQuery)
     */
    public Object executeQuery(String queryName, Class domainClass, List argumentValues) throws DatabaseException {
        if (argumentValues instanceof Vector) {
            return executeQuery(queryName, domainClass, (Vector)argumentValues);
        } else {
            return executeQuery(queryName, domainClass, new Vector(argumentValues));
        }
    }

    /**
     * PUBLIC:
     * Execute the pre-defined query by name and return the result.
     * Queries can be pre-defined and named to allow for their reuse.
     * The class is the descriptor in which the query was pre-defined.
     *
     * @see DescriptorQueryManager#addQuery(String, DatabaseQuery)
     */
    public Object executeQuery(String queryName, Class domainClass, Vector argumentValues) throws DatabaseException {
        ClassDescriptor descriptor = getDescriptor(domainClass);

        if (descriptor == null) {
            throw QueryException.descriptorIsMissingForNamedQuery(domainClass, queryName);
        }

        DatabaseQuery query = descriptor.getQueryManager().getQuery(queryName, argumentValues);

        if (query == null) {
            throw QueryException.queryNotDefined(queryName, domainClass);
        }

        return executeQuery(query, argumentValues);
    }

    /**
     * PUBLIC:
     * Execute the pre-defined query by name and return the result.
     * Queries can be pre-defined and named to allow for their reuse.
     *
     * @see #addQuery(String, DatabaseQuery)
     */
    public Object executeQuery(String queryName, Object arg1) throws DatabaseException {
        Vector argumentValues = new Vector();
        argumentValues.addElement(arg1);
        return executeQuery(queryName, argumentValues);
    }

    /**
     * PUBLIC:
     * Execute the pre-defined query by name and return the result.
     * Queries can be pre-defined and named to allow for their reuse.
     *
     * @see #addQuery(String, DatabaseQuery)
     */
    public Object executeQuery(String queryName, Object arg1, Object arg2) throws DatabaseException {
        Vector argumentValues = new Vector();
        argumentValues.addElement(arg1);
        argumentValues.addElement(arg2);
        return executeQuery(queryName, argumentValues);
    }

    /**
     * PUBLIC:
     * Execute the pre-defined query by name and return the result.
     * Queries can be pre-defined and named to allow for their reuse.
     *
     * @see #addQuery(String, DatabaseQuery)
     */
    public Object executeQuery(String queryName, Object arg1, Object arg2, Object arg3) throws DatabaseException {
        Vector argumentValues = new Vector();
        argumentValues.addElement(arg1);
        argumentValues.addElement(arg2);
        argumentValues.addElement(arg3);
        return executeQuery(queryName, argumentValues);
    }

    /**
     * PUBLIC:
     * Execute the pre-defined query by name and return the result.
     * Queries can be pre-defined and named to allow for their reuse.
     *
     * @see #addQuery(String, DatabaseQuery)
     */
    public Object executeQuery(String queryName, List argumentValues) throws DatabaseException {
        if (argumentValues instanceof Vector) {
            return executeQuery(queryName, (Vector)argumentValues);
        } else {
            return executeQuery(queryName, new Vector(argumentValues));
        }
    }

    /**
     * PUBLIC:
     * Execute the pre-defined query by name and return the result.
     * Queries can be pre-defined and named to allow for their reuse.
     *
     * @see #addQuery(String, DatabaseQuery)
     */
    public Object executeQuery(String queryName, Vector argumentValues) throws DatabaseException {
        DatabaseQuery query = getQuery(queryName, argumentValues);

        if (query == null) {
            throw QueryException.queryNotDefined(queryName);
        }

        return executeQuery(query, argumentValues);
    }

    /**
     * PUBLIC:
     * Execute the database query.
     * A query is a database operation such as reading or writing.
     * The query allows for the operation to be customized for such things as,
     * performance, depth, caching, etc.
     *
     * @see DatabaseQuery
     */
    public Object executeQuery(DatabaseQuery query) throws DatabaseException {
        return executeQuery(query, EmptyRecord.getEmptyRecord());
    }

    /**
     * PUBLIC:
     * Return the results from executing the database query.
     * The query arguments are passed in as a List of argument values in the same order as the query arguments.
     */
    public Object executeQuery(DatabaseQuery query, List argumentValues) throws DatabaseException {
        if (query == null) {
            throw QueryException.queryNotDefined();
        }
        AbstractRecord row = query.rowFromArguments(argumentValues, this);

        return executeQuery(query, row);
    }

    /**
     * INTERNAL:
     * Return the results from executing the database query.
     * the arguments should be a database row with raw data values.
     */
    public Object executeQuery(DatabaseQuery query, AbstractRecord row) throws DatabaseException {
        if (hasBroker()) {
            if (!((query.isDataModifyQuery() || query.isDataReadQuery()) && (query.getSessionName() == null))) {
                return getBroker().executeQuery(query, row);
            }
        }

        if (query == null) {
            throw QueryException.queryNotDefined();
        }
        
        // Check for disabled native queries.
        if (query.isUserDefinedSQLCall() && query.isSQLCallQuery() && ! query.isJPQLCallQuery()) {
            if (! query.shouldAllowNativeSQLQuery(getProject().allowNativeSQLQueries())) {
                // If the session/project says no to SQL queries and the database 
                // query doesn't ask to bypass this decision then throw an exception.
                throw QueryException.nativeSQLQueriesAreDisabled(query);
            }
        }

        //CR#2272
        log(SessionLog.FINEST, SessionLog.QUERY, "execute_query", query);

        //Make a call to the internal method with a retry count of 0.  This will
        //initiate a retry call stack if required and supported.  The separation between the
        //calling stack and the target method is made because the target method may call itself
        //recursively.
        return this.executeQuery(query, row, 0);
    }

    /**
     * INTERNAL:
     * Return the results from executing the database query.
     * the arguments should be a database row with raw data values.
     */
    public Object executeQuery(DatabaseQuery query, AbstractRecord row, int retryCount) throws DatabaseException {

        try {
            if (this.eventManager != null) {
                this.eventManager.preExecuteQuery(query);
            }
            Object result;
            if (isInProfile()) {
                result = getProfiler().profileExecutionOfQuery(query, row, this);
            } else {
                result = internalExecuteQuery(query, row);
            }
            if (this.eventManager != null) {
                this.eventManager.postExecuteQuery(query, result);
            }
            return result;
        } catch (RuntimeException exception) {
            if (exception instanceof QueryException) {
                QueryException queryException = (QueryException)exception;
                if (queryException.getQuery() == null) {
                    queryException.setQuery(query);
                }
                if (queryException.getQueryArgumentsRecord() == null) {
                    queryException.setQueryArguments(row);
                }
                if (queryException.getSession() == null) {
                    queryException.setSession(this);
                }
            } else if (exception instanceof DatabaseException) {
                DatabaseException databaseException = (DatabaseException)exception;
                if (databaseException.getQuery() == null) {
                    databaseException.setQuery(query);
                }
                if (databaseException.getQueryArgumentsRecord() == null) {
                    databaseException.setQueryArguments(row);
                }
                if (databaseException.getSession() == null) {
                    databaseException.setSession(this);
                }
                //if this query is a read query outside of a transaction then we may be able to retry the query
                if (!isInTransaction() && query.isReadQuery()) {
                    //was the failure communication based?  (ie timeout)
                    if (databaseException.isCommunicationFailure()) {
                        Object[] args = new Object[1];
                        args[0] = databaseException;
                        log(SessionLog.INFO, "communication_failure_attempting_query_retry", args, null);
                        Object result = retryQuery(query, row, databaseException, retryCount, this);
                        if (result instanceof DatabaseException) {
                            exception = (DatabaseException)result;
                        } else {
                            return result;
                        }
                    }
                }
            }
            return handleException(exception);
        }
    }

    /**
     * INTERNAL:
     * A query execution failed due to an invalid query.
     * Re-connect and retry the query.
     */
    public Object retryQuery(DatabaseQuery query, AbstractRecord row, DatabaseException databaseException, int retryCount, AbstractSession executionSession) {
        DatabaseException exception = databaseException;
        //retry
        if (retryCount <= getLogin().getQueryRetryAttemptCount()) {
            try {
                // attempt to reconnect for a certain number of times.
                // servers may take some time to recover.
                ++retryCount;
                try {
                    //passing the retry count will prevent a runaway retry where
                    // we can acquire connections but are unable to execute any queries
                    if (retryCount > 1) {
                        // We are retrying more than once lets wait to give connection time to restart.
                        //Give the failover time to recover.
                        Thread.currentThread().sleep(getLogin().getDelayBetweenConnectionAttempts());
                    }
                    return executionSession.executeQuery(query, row, retryCount);
                } catch (DatabaseException newException){
                    //replace original exception with last exception thrown
                    //this exception could be a data based exception as opposed
                    //to a connection exception that needs to go back to the customer.
                    exception = newException;
                }
            } catch (InterruptedException ex) {
                //Ignore interrupted exception.
            }
        }
        return exception;
    }

    /**
     * PUBLIC:
     * Execute the call on the database and return the result.
     * The call must return a value, if no value is return executeNonSelectCall must be used.
     * The call can be a stored procedure call, SQL call or other type of call.
     * A vector of database rows is returned, database row implements Java 2 Map which should be used to access the data.
     * <p>Example:
     * <p>session.executeSelectingCall(new SQLCall("Select * from Employee");
     *
     * @see #executeNonSelectingCall(Call)
     */
    public Vector executeSelectingCall(Call call) throws DatabaseException {
        DataReadQuery query = new DataReadQuery();
        query.setCall(call);
        query.setIsExecutionClone(true);
        return (Vector)executeQuery(query);
    }

    /**
     * PUBLIC:
     * Execute the sql on the database and return the result.
     * It must return a value, if no value is return executeNonSelectingSQL must be used.
     * A vector of database rows is returned, database row implements Java 2 Map which should be used to access the data.
 	 * Warning: Allowing an unverified SQL string to be passed into this
	 * method makes your application vulnerable to SQL injection attacks.
     * <p>Example:
     * <p>session.executeSelectingCall("Select * from Employee");
     *
     * @see #executeSelectingCall(Call)
     */
    public Vector executeSQL(String sqlString) throws DatabaseException {
        return executeSelectingCall(new SQLCall(sqlString));
    }

    /**
     * INTERNAL:
     * This should normally not be used, most sessions do not have a single accessor.
     * ServerSession has a set of connection pools.
     * ClientSession only has an accessor during a transaction.
     * SessionBroker has multiple accessors.
     * getAccessors() should be used to support partitioning.
     * To maintain backward compatibility, and to support certain cases that required a default accessor,
     * this returns the first accessor.
     */
    public Accessor getAccessor() {
        Collection<Accessor> accessors = getAccessors();
        if ((accessors == null) || accessors.isEmpty()) {
            return null;
        }
        if (accessors instanceof List) {
            return ((List<Accessor>)accessors).get(0);
        }
        return accessors.iterator().next();
    }

    /**
     * INTERNAL:
     * This should normally not be used, most sessions do not have specific accessors.
     * ServerSession has a set of connection pools.
     * ClientSession only has an accessor during a transaction.
     * SessionBroker has multiple accessors.
     * getAccessors() is used to support partitioning.
     * If the accessor is null, this lazy initializes one for backwardcompatibility with DatabaseSession.
     */
    public Collection<Accessor> getAccessors() {
        if ((this.accessors == null) && (this.project != null) && (this.project.getDatasourceLogin() != null)) {
            synchronized (this) {
                if ((this.accessors == null) && (this.project != null) && (this.project.getDatasourceLogin() != null)) {
                    // PERF: lazy init, not always required.
                    List<Accessor> newAccessors = new ArrayList(1);
                    newAccessors.add(this.project.getDatasourceLogin().buildAccessor());
                    this.accessors = newAccessors;
                }
            }
        }
        return this.accessors;
    }
    
    /**
     * INTERNAL:
     * Return the connections to use for the query execution.
     */
    public Collection<Accessor> getAccessors(Call call, AbstractRecord translationRow, DatabaseQuery query) {
        // Check for partitioning.
        Collection<Accessor> accessors = null;
        if (query.getPartitioningPolicy() != null) {
            accessors = query.getPartitioningPolicy().getConnectionsForQuery(this, query, translationRow);
            if (accessors != null) {
                return accessors;
            }
        }
        ClassDescriptor descriptor = query.getDescriptor();
        if ((descriptor != null) && (descriptor.getPartitioningPolicy() != null)) {
            accessors = descriptor.getPartitioningPolicy().getConnectionsForQuery(this, query, translationRow);
            if (accessors != null) {
                return accessors;
            }
        }
        if (this.partitioningPolicy != null) {
            accessors = this.partitioningPolicy.getConnectionsForQuery(this, query, translationRow);
            if (accessors != null) {
                return accessors;
            }
        }
        return accessors;
    }

    /**
     * INTERNAL:
     * Execute the call on each accessors and merge the results.
     */
    public Object basicExecuteCall(Call call, AbstractRecord translationRow, DatabaseQuery query) throws DatabaseException {
        Object result = null;
        if (query.getAccessors().size() == 1) {
            result = query.getAccessor().executeCall(call, translationRow, this);
        } else {
            RuntimeException exception = null;
            // Replication or partitioning may require execution on multiple connections.
            for (Accessor accessor : query.getAccessors()) {
                Object object = null;
                try {
                    object = accessor.executeCall(call, translationRow, this);
                } catch (RuntimeException failed) {
                    // Catch any exceptions to allow execution on each connections.
                    // This is used to have DDL run on every database even if one db fails because table already exists.
                    if (exception == null) {
                        exception = failed;
                    }
                }
                if (call.isOneRowReturned()) {
                    // If one row is desired, then break on first hit.
                    if (object != null) {
                        result = object;
                        break;
                    }
                } else if (call.isNothingReturned()) {
                    // If no return ensure row count is consistent, 0 if any 0, otherwise first number.
                    if (result == null) {
                        result = object;
                    } else {
                        if (object instanceof Integer) {
                            if (((Integer)result).intValue() != 0) {
                                if (((Integer)object).intValue() != 0) {
                                    result = object;
                                }
                            }
                        }
                    }
                } else {
                    // Either a set of rows (union), or cursor (return).
                    if (result == null) {
                        result = object;
                    } else {
                        if (object instanceof List) {
                            ((List)result).addAll((List)object);
                        } else {
                            break; // Not sure what to do, so break (if a cursor, don't only want to open one cursor.
                        }
                    }                        
                }
            }
            if (exception != null) {
                throw exception;
            }
        }
        return result;
    }
    
    /**
     * INTERNAL:
     */
    public ExposedNodeLinkedList getActiveCommandThreads() {
        if (activeCommandThreads == null) {
            activeCommandThreads = new ExposedNodeLinkedList();
        }

        return activeCommandThreads;
    }

    /**
     * PUBLIC:
     * Return the active session for the current active external (JTS) transaction.
     * This should only be used with JTS and will return the session if no external transaction exists.
     */
    public org.eclipse.persistence.sessions.Session getActiveSession() {
        org.eclipse.persistence.sessions.Session activeSession = getActiveUnitOfWork();
        if (activeSession == null) {
            activeSession = this;
        }

        return activeSession;
    }

    /**
     * PUBLIC:
     * Return the active unit of work for the current active external (JTS) transaction.
     * This should only be used with JTS and will return null if no external transaction exists.
     */
    public org.eclipse.persistence.sessions.UnitOfWork getActiveUnitOfWork() {
        if (hasExternalTransactionController()) {
            return getExternalTransactionController().getActiveUnitOfWork();
        }

        /* Steven Vo:  CR# 2517
           Get from the server session since the external transaction controller could be
           null out from the client session by TL WebLogic 5.1 to provide non-jts transaction
           operations
          */
        if (isClientSession()) {
            return ((org.eclipse.persistence.sessions.server.ClientSession)this).getParent().getActiveUnitOfWork();
        }

        return null;
    }

    /**
     * INTERNAL:
     * Returns the alias descriptors Map.
     */
    public Map getAliasDescriptors() {
        return project.getAliasDescriptors();
    }

    /**
     * ADVANCED:
     * Answers the past time this session is as of.  Indicates whether or not this
     * is a special historical session where all objects are read relative to a
     * particular point in time.
     * @return An immutable object representation of the past time.
     * <code>null</code> if no clause set, or this a regular session.
     * @see #acquireHistoricalSession(org.eclipse.persistence.history.AsOfClause)
     */
    public AsOfClause getAsOfClause() {
        return null;
    }

    /**
     * INTERNAL:
     * Allow the session to be used from a session broker.
     */
    public AbstractSession getBroker() {
        return broker;
    }

    /**
     * INTERNAL:
     * The session that this query is executed against when not in transaction.
     * The session containing the shared identity map.
     * <p>
     * In most cases this is the root ServerSession or DatabaseSession.
     * <p>
     * In cases where objects are not to be cached in the global identity map
     * an alternate session may be returned:
     * <ul>
     * <li>A ClientSession if in transaction
     * <li>An isolated ClientSession or HistoricalSession
     * <li>A registered session of a root SessionBroker
     * </ul>
     */
    public AbstractSession getRootSession(DatabaseQuery query) {
        ClassDescriptor descriptor = null;
        if (query != null){
            descriptor = query.getDescriptor();
        }
        return getParentIdentityMapSession(descriptor, true, true);
    }

    /**
     * INTERNAL:
     * Gets the parent session.
     */
    public AbstractSession getParent() {
        return null;
    }

    /**
     * INTERNAL:
     * Gets the next link in the chain of sessions followed by a query's check
     * early return, the chain of sessions with identity maps all the way up to
     * the root session.
     */
    public AbstractSession getParentIdentityMapSession(DatabaseQuery query) {
        ClassDescriptor descriptor = null;
        if (query != null){
            descriptor = query.getDescriptor();
        }
        return getParentIdentityMapSession(descriptor, false, false);
    }

    /**
     * INTERNAL:
     * Gets the next link in the chain of sessions followed by a query's check
     * early return, the chain of sessions with identity maps all the way up to
     * the root session.
     * <p>
     * Used for session broker which delegates to registered sessions, or UnitOfWork
     * which checks parent identity map also.
     * @param canReturnSelf true when method calls itself.  If the path
     * starting at <code>this</code> is acceptable.  Sometimes true if want to
     * move to the first valid session, i.e. executing on ClientSession when really
     * should be on ServerSession.
     * @param terminalOnly return the session we will execute the call on, not
     * the next step towards it.
     * @return this if there is no next link in the chain
     */
    public AbstractSession getParentIdentityMapSession(DatabaseQuery query, boolean canReturnSelf, boolean terminalOnly) {
        ClassDescriptor descriptor = null;
        if (query != null){
            descriptor = query.getDescriptor();
        }
        return getParentIdentityMapSession(descriptor, canReturnSelf, terminalOnly);
    }

    /**
     * INTERNAL:
     * Returns the appropriate IdentityMap session for this descriptor.  Sessions can be 
     * chained and each session can have its own Cache/IdentityMap.  Entities can be stored
     * at different levels based on Cache Isolation.  This method will return the correct Session
     * for a particular Entity class based on the Isolation Level and the attributes provided.
     * <p>
     * @param canReturnSelf true when method calls itself.  If the path
     * starting at <code>this</code> is acceptable.  Sometimes true if want to
     * move to the first valid session, i.e. executing on ClientSession when really
     * should be on ServerSession.
     * @param terminalOnly return the last session in the chain where the Enitity is stored.
     * @return Session with the required IdentityMap
     */
    public AbstractSession getParentIdentityMapSession(ClassDescriptor descriptor, boolean canReturnSelf, boolean terminalOnly) {
        return this;
    }

    /**
     * INTERNAL:
     * Returns the default pessimistic lock timeout value.
     */
    public Integer getPessimisticLockTimeoutDefault() {
        return pessimisticLockTimeoutDefault;
    }

    /**
     * PUBLIC:
     * Return the default query timeout for this session.
     * This timeout will apply to any queries that do not have a timeout set,
     * and that do not have a default timeout defined in their descriptor.
     */
    public int getQueryTimeoutDefault() {
        return queryTimeoutDefault;
    }

    public EntityListenerInjectionManager getEntityListenerInjectionManager() {
        if (entityListenerInjectionManager == null){
            entityListenerInjectionManager = createEntityListenerInjectionManager();
        }
        return entityListenerInjectionManager;
    }

    /**
     * INTERNAL:
     * Gets the session which this query will be executed on.
     * Generally will be called immediately before the call is translated,
     * which is immediately before session.executeCall.
     * <p>
     * Since the execution session also knows the correct datasource platform
     * to execute on, it is often used in the mappings where the platform is
     * needed for type conversion, or where calls are translated.
     * <p>
     * Is also the session with the accessor.  Will return a ClientSession if
     * it is in transaction and has a write connection.
     * @return a session with a live accessor
     * @param query may store session name or reference class for brokers case
     */
    public AbstractSession getExecutionSession(DatabaseQuery query) {
        return this;
    }

    /**
     * INTERNAL:
     * Return if the commit manager has been set.
     */
    public boolean hasCommitManager() {
        return commitManager != null;
    }

    /**
     * INTERNAL:
     * The commit manager is used to resolve referential integrity on commits of multiple objects.
     * All brokered sessions share the same commit manager.
     */
    public CommitManager getCommitManager() {
        if (hasBroker()) {
            return getBroker().getCommitManager();
        }

        // PERF: lazy init, not always required, not required for client sessions
        if (commitManager == null) {
            commitManager = new CommitManager(this);
        }
        return commitManager;
    }

    /**
     * INTERNAL:
     * Returns the set of read-only classes that gets assigned to each newly created UnitOfWork.
     *
     * @see org.eclipse.persistence.sessions.Project#setDefaultReadOnlyClasses(Vector)
     */
    public Vector getDefaultReadOnlyClasses() {
        //Bug#3911318  All brokered sessions share the same DefaultReadOnlyClasses.
        if (hasBroker()) {
            return getBroker().getDefaultReadOnlyClasses();
        }
        return getProject().getDefaultReadOnlyClasses();
    }

    /**
     * ADVANCED:
     * Return the descriptor specified for the class.
     * If the class does not have a descriptor but implements an interface that is also implemented
     * by one of the classes stored in the map, that descriptor will be stored under the
     * new class. If a descriptor does not exist for the Class parameter, null is returned.
     * If the passed Class parameter is null, then null will be returned.
     */
    public ClassDescriptor getClassDescriptor(Class theClass) {
        if (theClass == null) {
            return null;
        }
        return getDescriptor(theClass);
    }

    /**
     * ADVANCED:
     * Return the descriptor specified for the object's class.
     * If a descriptor does not exist for the Object parameter, null is returned.
     * If the passed Object parameter is null, then null will be returned.
     */
    public ClassDescriptor getClassDescriptor(Object domainObject) {
        if (domainObject == null) {
            return null;
        }
        return getDescriptor(domainObject);
    }

    /**
     * PUBLIC:
     * Return the descriptor for  the alias.
     * UnitOfWork delegates this to the parent
     */
    public ClassDescriptor getClassDescriptorForAlias(String alias) {
        return project.getDescriptorForAlias(alias);
    }

    /**
     * ADVANCED:
     * Return the descriptor specified for the class.
     * If the class does not have a descriptor but implements an interface that is also implemented
     * by one of the classes stored in the map, that descriptor will be stored under the
     * new class. If the passed Class is null, null will be returned.
     */
    public ClassDescriptor getDescriptor(Class theClass) {
        if (theClass == null) {
            return null;
        }

        // Optimize descriptor lookup through caching the last one accessed.
        ClassDescriptor descriptor = this.lastDescriptorAccessed;
        if ((descriptor != null) && (descriptor.getJavaClass() == theClass)) {
            return descriptor;
        }
        
        if (this.descriptors != null) {
            descriptor = this.descriptors.get(theClass);
        } else {
            descriptor = this.project.getDescriptors().get(theClass);
        }

        if (descriptor == null) {
            if (hasBroker()) {
                // Also check the broker
                descriptor = getBroker().getDescriptor(theClass);
            }
            if (descriptor == null) {
                // Allow for an event listener to lazy register the descriptor for a class.
                if (this.eventManager != null) {
                    this.eventManager.missingDescriptor(theClass);
                }
                descriptor = getDescriptors().get(theClass);
    
                if (descriptor == null) {
                    // This allows for the correct descriptor to be found if the class implements an interface,
                    // or extends a class that a descriptor is registered for.
                    // This is used by EJB to find the descriptor for a stub and remote to unwrap it,
                    // and by inheritance to allow for subclasses that have no additional state to not require a descriptor.
                    if (!theClass.isInterface()) {
                        Class[] interfaces = theClass.getInterfaces();
                        for (int index = 0; index < interfaces.length; ++index) {
                            Class interfaceClass = interfaces[index];
                            descriptor = getDescriptor(interfaceClass);
                            if (descriptor != null) {
                                getDescriptors().put(interfaceClass, descriptor);
                                break;
                            }
                        }
                        if (descriptor == null ) {
                            descriptor = checkHierarchyForDescriptor(theClass);
                        }
                    }
                }
            }
        }

        // Cache for optimization.
        this.lastDescriptorAccessed = descriptor;

        return descriptor;
    }

    /**
     * ADVANCED:
     * Return the descriptor specified for the object's class.
     * If the passed Object is null, null will be returned.
     */
    public ClassDescriptor getDescriptor(Object domainObject) {
        if (domainObject == null) {
            return null;
        }
        //Bug#3947714  Check and trigger the proxy here
        if (this.project.hasProxyIndirection()) {
            return getDescriptor(ProxyIndirectionPolicy.getValueFromProxy(domainObject).getClass());
        }
        return getDescriptor(domainObject.getClass());
    }

    /**
     * PUBLIC:
     * Return the descriptor for  the alias
     */
    public ClassDescriptor getDescriptorForAlias(String alias) {
        // If we have a descriptors list return our sessions descriptor and
        // not that of the project since we may be dealing with a multitenant
        // descriptor which will have been initialized locally on the session.
        // The project descriptor will be not initialized.
        ClassDescriptor desc = project.getDescriptorForAlias(alias);
        if (desc != null && this.descriptors != null) {
            return this.descriptors.get(desc.getJavaClass());
        } else {
            return desc;
        }
    }
    
    /**
     * ADVANCED:
     * Return all registered descriptors.
     */
    @Override
    public Map<Class, ClassDescriptor> getDescriptors() {
        return this.project.getDescriptors();
    }

    /**
     * INTERNAL:
     * Return if an event manager has been set.
     */
    public boolean hasEventManager() {
        return eventManager != null;
    }

    /**
     * PUBLIC:
     * Return the event manager.
     * The event manager can be used to register for various session events.
     */
    public SessionEventManager getEventManager() {
        if (eventManager == null) {
            synchronized (this) {
                if (eventManager == null) {
                    // PERF: lazy init.
                    eventManager = new SessionEventManager(this);
                }
            }
        }
        return eventManager;
    }

    /**
     * INTERNAL:
     * Return a string which represents my ExceptionHandler's class
     * Added for F2104: Properties.xml
     * - gn
     */
    public String getExceptionHandlerClass() {
        String className = null;
        try {
            className = getExceptionHandler().getClass().getName();
        } catch (Exception exception) {
            return null;
        }
        return className;
    }

    /**
     * PUBLIC:
     * Return the ExceptionHandler.Exception handler can catch errors that occur on queries or during database access.
     */
    public ExceptionHandler getExceptionHandler() {
        return exceptionHandler;
    }

    /**
     * PUBLIC:
     * Used for JTS integration.  If your application requires to have JTS control transactions instead of EclipseLink an
     * external transaction controller must be specified.
     * EclipseLink provides JTS controllers for several JTS implementations including JTS 1.0, Weblogic 5.1 and WebSphere 3.0.
     *
     * @see org.eclipse.persistence.transaction.JTATransactionController
     */
    public ExternalTransactionController getExternalTransactionController() {
        return externalTransactionController;
    }

    /**
     * PUBLIC:
     * The IdentityMapAccessor is the preferred way of accessing IdentityMap funcitons
     * This will return an object which implements an interface which exposes all public
     * IdentityMap functions.
     */
    public org.eclipse.persistence.sessions.IdentityMapAccessor getIdentityMapAccessor() {
        return identityMapAccessor;
    }

    /**
     * INTERNAL:
     * Return the internally available IdentityMapAccessor instance.
     */
    public org.eclipse.persistence.internal.sessions.IdentityMapAccessor getIdentityMapAccessorInstance() {
        return identityMapAccessor;
    }

    /**
     * PUBLIC:
     * Returns the integrityChecker.IntegrityChecker holds all the ClassDescriptor Exceptions.
     */
    public IntegrityChecker getIntegrityChecker() {
        // BUG# 2700595 - Lazily create an IntegrityChecker if one has not already been created.
        if (integrityChecker == null) {
            integrityChecker = new IntegrityChecker();
        }

        return integrityChecker;
    }
    
    /**
     * ADVANCED:
     * Return all pre-defined not yet parsed JPQL queries.
     */
    public List<DatabaseQuery> getJPAQueries() {
        return getProject().getJPAQueries();
    }
    
    /**
     * ADVANCED:
     * Return all pre-defined not yet parsed JPQL queries.
     */
    public List<DatabaseQuery> getJPATablePerTenantQueries() {
        return getProject().getJPATablePerTenantQueries();
    }
    
    /**
     * PUBLIC:
     * Return the writer to which an accessor writes logged messages and SQL.
     * If not set, this reference defaults to a writer on System.out.
     * To enable logging, logMessages must be turned on.
     *
     * @see #logMessages()
     */
    public Writer getLog() {
        return getSessionLog().getWriter();
    }

    /**
     * INTERNAL:
     * Return the name of the session: class name + system hashcode.
     * <p>
     * This should be the implementation of toString(), and also the
     * value should be calculated in the constructor for it is used all the
     * time.  However everything is lazily initialized now and the value is
     * transient for the system hashcode could vary?
     */
    public String getLogSessionString() {
        if (logSessionString == null) {
            StringWriter writer = new StringWriter();
            writer.write(getSessionTypeString());
            writer.write("(");
            writer.write(String.valueOf(System.identityHashCode(this)));
            writer.write(")");
            logSessionString = writer.toString();
        }
        return logSessionString;
    }

    /**
     * INTERNAL:
     * Returns the type of session, its class.
     * <p>
     * Override to hide from the user when they are using an internal subclass
     * of a known class.
     * <p>
     * A user does not need to know that their UnitOfWork is a
     * non-deferred UnitOfWork, or that their ClientSession is an
     * IsolatedClientSession.
     */
    public String getSessionTypeString() {
        return Helper.getShortClassName(getClass());
    }

    /**
     * INTERNAL:
     * Return the static metamodel class associated with the given model class 
     * if available. Callers must handle null.
     */
    public String getStaticMetamodelClass(String modelClassName) {
        if (staticMetamodelClasses != null) {
            return staticMetamodelClasses.get(modelClassName);
        }
        
        return null;
    }
    
    /**
     * OBSOLETE:
     * Return the login, the login holds any database connection information given.
     * This has been replaced by getDatasourceLogin to make use of the Login interface
     * to support non-relational datasources,
     * if DatabaseLogin API is required it will need to be cast.
     */
    public DatabaseLogin getLogin() {
        try {
            return (DatabaseLogin)getDatasourceLogin();
        } catch (ClassCastException wrongType) {
            throw ValidationException.notSupportedForDatasource();
        }
    }

    /**
     * PUBLIC:
     * Return the login, the login holds any database connection information given.
     * This return the Login interface and may need to be cast to the datasource specific implementation.
     */
    public Login getDatasourceLogin() {
        if (this.project == null) {
            return null;
        }
        return this.project.getDatasourceLogin();
    }
    
    /**
     * INTERNAL:
     * Return the mapped superclass descriptor if one exists for the given
     * class name. Must check any broker as well.
     */
    public ClassDescriptor getMappedSuperclass(String className) {
        ClassDescriptor desc = getProject().getMappedSuperclass(className);
        
        if (desc == null && hasBroker()) {
            getBroker().getMappedSuperclass(className);
        }

        return desc;
    }
    
    /**
     * PUBLIC:
     * Return a set of multitenant context properties this session
     */
    public Set<String> getMultitenantContextProperties() {
        if (this.multitenantContextProperties == null) {
            this.multitenantContextProperties = new HashSet<String>();
        }

        return this.multitenantContextProperties;
    }
    
    /**
     * PUBLIC:
     * Return the name of the session.
     * This is used with the session broker, or to give the session a more meaningful name.
     */
    public String getName() {
        return name;
    }

    /**
     * ADVANCED:
     * Return the sequnce number from the database
     */
    public Number getNextSequenceNumberValue(Class domainClass) {
        return (Number)getSequencing().getNextValue(domainClass);
    }

    /**
     * INTERNAL:
     * Return the number of units of work connected.
     */
    public int getNumberOfActiveUnitsOfWork() {
        return numberOfActiveUnitsOfWork;
    }

    /**
     * INTERNAL:
     * For use within the merge process this method will get an object from the shared 
     * cache using a readlock.  If a readlock is unavailable then the merge manager will be 
     * transitioned to deferred locks and a deferred lock will be used.
     */
    protected CacheKey getCacheKeyFromTargetSessionForMerge(Object implementation, ObjectBuilder builder, ClassDescriptor descriptor, MergeManager mergeManager){
      Object original = null;
       Object primaryKey = builder.extractPrimaryKeyFromObject(implementation, this, true);
       if (primaryKey == null) {
           return null;
       }
       CacheKey cacheKey = null;
       if (mergeManager == null){
           cacheKey = getIdentityMapAccessorInstance().getCacheKeyForObject(primaryKey, implementation.getClass(), descriptor, true);
           if (cacheKey != null){
               cacheKey.checkReadLock();
           }
           return cacheKey;
       }
       
       cacheKey = getIdentityMapAccessorInstance().getCacheKeyForObject(primaryKey, implementation.getClass(), descriptor, true);
       if (cacheKey != null) {
           if (cacheKey.acquireReadLockNoWait()) {
               original = cacheKey.getObject();
               cacheKey.releaseReadLock();
           } else {
               if (!mergeManager.isTransitionedToDeferredLocks()) {
                   getIdentityMapAccessorInstance().getWriteLockManager().transitionToDeferredLocks(mergeManager);
               }
               cacheKey.acquireDeferredLock();
               original = cacheKey.getObject();
               if (original == null) {
                   synchronized (cacheKey) {
                       if (cacheKey.isAcquired()) {
                           try {
                               cacheKey.wait();
                           } catch (InterruptedException e) {
                               //ignore and return
                           }
                       }
                       original = cacheKey.getObject();
                   }
               }
               cacheKey.releaseDeferredLock();
           }
       }
       return cacheKey;
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
            platform = getDatasourceLogin().getPlatform();
        }
        return (DatabasePlatform)platform;
    }


    /**
     * INTERNAL:
     * Return the class loader for the session's application.
     * This loader should be able to load any application or EclipseLink class.
     */
    public ClassLoader getLoader() {
        return getDatasourcePlatform().getConversionManager().getLoader();
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
            platform = getDatasourceLogin().getDatasourcePlatform();
        }
        return platform;
    }

    /**
     * INTERNAL:
     * Marked internal as this is not customer API but helper methods for
     * accessing the server platform from within EclipseLink's other sessions types
     * (ie not DatabaseSession)
     */
    public ServerPlatform getServerPlatform() {
        return null;
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
            platform = getDatasourcePlatform();
        }
        return platform;
    }

    /**
     * PUBLIC:
     * Return the profiler.
     * The profiler is a tool that can be used to determine performance bottlenecks.
     * The profiler can be queries to print summaries and configure for logging purposes.
     */
    public SessionProfiler getProfiler() {
        return profiler;
    }

    /**
     * PUBLIC:
     * Return the project, the project holds configuartion information including the descriptors.
     */
    public org.eclipse.persistence.sessions.Project getProject() {
        return project;
    }

    /**
     * ADVANCED:
     * Allow for user defined properties.
     */
    public Map getProperties() {
        if (properties == null) {
            properties = new HashMap(5);
        }
        return properties;
    }

    /**
     * INTERNAL:
     * Allow to check for user defined properties.
     */
    public boolean hasProperties() {
        return ((properties != null) && !properties.isEmpty());
    }
    
    /**
     * INTERNAL:
     * Return list of table per tenant multitenant descriptors.
     */
    public boolean hasTablePerTenantDescriptors() {
        return (tablePerTenantDescriptors != null && ! tablePerTenantDescriptors.isEmpty());
    }
    
    /**
     * INTERNAL:
     * Return a list of table per tenant multitenant queries.
     */
    public boolean hasTablePerTenantQueries() {
        return (tablePerTenantQueries != null && ! tablePerTenantQueries.isEmpty());
    }

    /**
     * ADVANCED:
     * Returns the user defined property.
     */
    public Object getProperty(String name) {
        if(this.properties==null){
            return null;
        }
        return getProperties().get(name);
    }

    /**
     * ADVANCED:
     * Return all pre-defined queries.
     */
    public Map<String, List<DatabaseQuery>> getQueries() {
        // PERF: lazy init, not normally required.
        if (queries == null) {
            queries = new HashMap(5);
        }
        return queries;
    }
    
    /**
     * ADVANCED:
     * Return an attribute group of a particular name.
     */
    
    /**
     * ADVANCED
     * Return all predefined attribute groups
     */
    public Map<String, AttributeGroup> getAttributeGroups(){
        if (this.attributeGroups == null){
            this.attributeGroups = new HashMap<String, AttributeGroup>(5);
        }
        return this.attributeGroups;
    }

    /**
     * INTERNAL:
     * Return the pre-defined queries in this session.
     * A single vector containing all the queries is returned.
     *
     * @see #getQueries()
     */
    public List<DatabaseQuery> getAllQueries() {
        Vector allQueries = new Vector();
        for (Iterator vectors = getQueries().values().iterator(); vectors.hasNext();) {
            allQueries.addAll((Vector)vectors.next());
        }
        return allQueries;
    }

    /**
     * PUBLIC:
     * Return the query from the session pre-defined queries with the given name.
     * This allows for common queries to be pre-defined, reused and executed by name.
     */
    public DatabaseQuery getQuery(String name) {
        return getQuery(name, null);
    }

    /**
     * PUBLIC:
     * Return the query from the session pre-defined queries with the given name and argument types.
     * This allows for common queries to be pre-defined, reused and executed by name.
     * This method should be used if the Session has multiple queries with the same name but
     * different arguments.
     *
     * @see #getQuery(String)
     */
    public DatabaseQuery getQuery(String name, List arguments) {
        if (arguments instanceof Vector) {
            return getQuery(name, (Vector)arguments);
        } else {
            return getQuery(name, new Vector(arguments));
        }
    }

    /**
     * PUBLIC:
     * Return the query from the session pre-defined queries with the given name and argument types.
     * This allows for common queries to be pre-defined, reused and executed by name.
     * This method should be used if the Session has multiple queries with the same name but
     * different arguments.
     *
     * @see #getQuery(String)
     */
    public DatabaseQuery getQuery(String name, Vector arguments) {
        return getQuery(name, arguments, true);
    }
    
    /**
     * INTERNAL:
     * Return the query from the session pre-defined queries with the given name and argument types.
     * This allows for common queries to be pre-defined, reused and executed by name.
     * This method should be used if the Session has multiple queries with the same name but
     * different arguments. 
     * 
     * @parameter shouldSearchParent indicates whether parent should be searched if query not found.
     * @see #getQuery(String, arguments)
     */    
    public DatabaseQuery getQuery(String name, Vector arguments, boolean shouldSearchParent) {
        Vector queries = (Vector)getQueries().get(name);
        if ((queries != null) && !queries.isEmpty()) {
            // Short circuit the simple, most common case of only one query.
            if (queries.size() == 1) {
                return (DatabaseQuery)queries.firstElement();
            }
    
            // CR#3754; Predrag; mar 19/2002;
            // We allow multiple named queries with the same name but
            // different argument set; we can have only one query with
            // no arguments; Vector queries is not sorted;
            // When asked for the query with no parameters the
            // old version did return the first query - wrong:
            // return (DatabaseQuery) queries.firstElement();
            int argumentTypesSize = 0;
            if (arguments != null) {
                argumentTypesSize = arguments.size();
            }
            Vector argumentTypes = new Vector(argumentTypesSize);
            for (int i = 0; i < argumentTypesSize; i++) {
                argumentTypes.addElement(arguments.elementAt(i).getClass());
            }
            for (Enumeration queriesEnum = queries.elements(); queriesEnum.hasMoreElements();) {
                DatabaseQuery query = (DatabaseQuery)queriesEnum.nextElement();
                if (Helper.areTypesAssignable(argumentTypes, query.getArgumentTypes())) {
                    return query;
                }
            }
        }
        if(shouldSearchParent) {
            AbstractSession parent = getParent();
            if(parent != null) {
                return parent.getQuery(name, arguments, true);
            }
        }
        return null;
    }

    /**
     * Returns an AttributeGroup by name
     */
    
    /**
     * INTERNAL:
     * Return the Sequencing object used by the session.
     */
    public Sequencing getSequencing() {
        return null;
    }

    /**
     * INTERNAL:
     * Return the session to be used for the class.
     * Used for compatibility with the session broker.
     */
    public AbstractSession getSessionForClass(Class domainClass) {
        if (hasBroker()) {
            return getBroker().getSessionForClass(domainClass);
        }
        return this;
    }

    /**
     * INTERNAL:
     * Return the session by name.
     * Used for compatibility with the session broker.
     */
    public AbstractSession getSessionForName(String name) throws ValidationException {
        if (hasBroker()) {
            return getBroker().getSessionForName(name);
        }
        return this;
    }
    
    /**
     * PUBLIC:
     * Return the session log to which an accessor logs messages and SQL.
     * If not set, this will default to a session log on a writer on System.out.
     * To enable logging, logMessages must be turned on.
     *
     * @see #logMessages()
     */
    public SessionLog getSessionLog() {
        if (sessionLog == null) {
            setSessionLog(new DefaultSessionLog());
        }
        return sessionLog;
    }

    /**
     * INTERNAL:
     * Return list of table per tenant multitenant descriptors.
     */
    public List<ClassDescriptor> getTablePerTenantDescriptors() {
        if (tablePerTenantDescriptors == null) {
            tablePerTenantDescriptors = new ArrayList<ClassDescriptor>();
        }
        
        return tablePerTenantDescriptors;
    }
    
    /**
     * INTERNAL:
     * Return list of table per tenant multitenant descriptors.
     */
    public List<DatabaseQuery> getTablePerTenantQueries() {
        if (tablePerTenantQueries == null) {
            tablePerTenantQueries = new ArrayList<DatabaseQuery>();
        }
        
        return tablePerTenantQueries;
    }
    
    /**
     * INTERNAL:
     * The transaction mutex ensure mutual exclusion on transaction across multiple threads.
     */
    public ConcurrencyManager getTransactionMutex() {
        // PERF: not always required, defer.
        if (transactionMutex == null) {
            synchronized (this) {
                if (transactionMutex == null) {
                    transactionMutex = new ConcurrencyManager();
                }
            }
        }
        return transactionMutex;
    }

    /**
     * PUBLIC:
     * Allow any WARNING level exceptions that occur within EclipseLink to be logged and handled by the exception handler.
     */
    public Object handleException(RuntimeException exception) throws RuntimeException {
        if ((exception instanceof EclipseLinkException)) {
            EclipseLinkException eclipseLinkException = (EclipseLinkException)exception;
            if (eclipseLinkException.getSession() == null) {
                eclipseLinkException.setSession(this);
            }

            //Bug#3559280  Avoid logging an exception twice
            if (!eclipseLinkException.hasBeenLogged()) {
                logThrowable(SessionLog.WARNING, null, exception);
                eclipseLinkException.setHasBeenLogged(true);
            }
        } else {
            logThrowable(SessionLog.WARNING, null, exception);
        }
        if (hasExceptionHandler()) {
            if (this.broker != null && this.broker.hasExceptionHandler()) {
                try {
                    return getExceptionHandler().handleException(exception);
                } catch (RuntimeException ex) {
                    // handle the original exception
                    return this.broker.getExceptionHandler().handleException(exception);
                }
            } else {
                return getExceptionHandler().handleException(exception);
            }
        } else {
            if (this.broker != null && this.broker.hasExceptionHandler()) {
                return this.broker.getExceptionHandler().handleException(exception);
            } else {
                throw exception;
            }
        }
    }

    /**
     * INTERNAL:
     * Allow the session to be used from a session broker.
     */
    public boolean hasBroker() {
        return broker != null;
    }


    /**
     * ADVANCED:
     * Return true if a descriptor exists for the given class.
     */
    public boolean hasDescriptor(Class theClass) {
        if (theClass == null) {
            return false;
        }

        return getDescriptors().get(theClass) != null;
    }

    /**
     * PUBLIC:
     * Return if an exception handler is present.
     */
    public boolean hasExceptionHandler() {
        if (exceptionHandler == null) {
            return false;
        }
        return true;
    }

    /**
     * PUBLIC:
     * Used for JTA integration.  If your application requires to have JTA control transactions instead of EclipseLink an
     * external transaction controler must be specified.  EclipseLink provides JTA controlers for JTA 1.0 and application
     * servers.
     * @see org.eclipse.persistence.transaction.JTATransactionController
     */
    public boolean hasExternalTransactionController() {
        return externalTransactionController != null;
    }


    /**
     * INTERNAL:
     * Set up the IdentityMapManager.  This method allows subclasses of Session to override
     * the default IdentityMapManager functionality.
     */
    public void initializeIdentityMapAccessor() {
        this.identityMapAccessor = new org.eclipse.persistence.internal.sessions.IdentityMapAccessor(this, new IdentityMapManager(this));
    }

    /**
     * PUBLIC:
     * Insert the object and all of its privately owned parts into the database.
     * Insert should only be used if the application knows that the object is new,
     * otherwise writeObject should be used.
     * The insert operation can be customized through using an insert query.
     *
     * @exception DatabaseException if an error occurs on the database,
     * these include constraint violations, security violations and general database errors.
     *
     * @see InsertObjectQuery
     * @see #writeObject(Object)
     */
    public Object insertObject(Object domainObject) throws DatabaseException {
        InsertObjectQuery query = new InsertObjectQuery();
        query.setObject(domainObject);
        query.setIsExecutionClone(true);
        return executeQuery(query);
    }

    /**
     * INTERNAL:
     * Return the results from exeucting the database query.
     * The arguments should be a database row with raw data values.
     * This method is provided to allow subclasses to change the default querying behavior.
     * All querying goes through this method.
     */
    public Object internalExecuteQuery(DatabaseQuery query, AbstractRecord databaseRow) throws DatabaseException {
        return query.execute(this, databaseRow);
    }

    /**
     * INTERNAL:
     * Returns true if the session is a session Broker.
     */
    public boolean isBroker() {
        return false;
    }

    /**
     * INTERNAL:
     * Returns true if the session is in a session Broker.
     */
    public boolean isInBroker() {
        return isInBroker;
    }

    /**
     * PUBLIC:
     * Return if the class is defined as read-only.
     */
    public boolean isClassReadOnly(Class theClass) {
        ClassDescriptor descriptor = getDescriptor(theClass);
        return isClassReadOnly(theClass, descriptor);
    }

    /**
     * INTERNAL:
     * Return if the class is defined as read-only.
     * PERF: Pass descriptor to avoid re-lookup.
     */
    public boolean isClassReadOnly(Class theClass, ClassDescriptor descriptor) {
        if ((descriptor != null) && descriptor.shouldBeReadOnly()) {
            return true;
        }
        if (theClass != null) {
            return getDefaultReadOnlyClasses().contains(theClass);
        }
        return false;
    }

    /**
     * PUBLIC:
     * Return if this session is a client session.
     */
    public boolean isClientSession() {
        return false;
    }

    /**
     * PUBLIC:
     * Return if this session is an isolated client session.
     */
    public boolean isIsolatedClientSession() {
        return false;
    }

    /**
     * PUBLIC:
     * Return if this session is an exclusive isolated client session.
     */
    public boolean isExclusiveIsolatedClientSession() {
        return false;
    }

    /**
     * PUBLIC:
     * Return if this session is connected to the database.
     */
    public boolean isConnected() {
        if (getAccessor() == null) {
            return false;
        }

        return getAccessor().isConnected();
    }

    /**
     * PUBLIC:
     * Return if this session is a database session.
     */
    public boolean isDatabaseSession() {
        return false;
    }

    /**
     * PUBLIC:
     * Return if this session is a distributed session.
     */
    public boolean isDistributedSession() {
        return false;
    }

    /**
     * PUBLIC:
     * Return if a profiler is being used.
     */
    public boolean isInProfile() {
        return isInProfile;
    }

    /**
     * PUBLIC:
     * Allow for user deactive a profiler
     */
    public void setIsInProfile(boolean inProfile) {
        this.isInProfile = inProfile;
    }

    /**
     * INTERNAL:
     * Set if this session is contained in a SessionBroker.
     */
    public void setIsInBroker(boolean isInBroker) {
        this.isInBroker = isInBroker;
    }

    /**
     * PUBLIC:
     * Return if this session's decendants should use finalizers.
     * The allows certain finalizers such as in ClientSession to be enabled.
     * These are disable by default for performance reasons.
     */
    public boolean isFinalizersEnabled() {
        return isFinalizersEnabled;
    }

    /**
     * INTERNAL:
     * Register a finalizer to release this session.
     */
    public void registerFinalizer() {
        // Ensure the finalizer is referenced until the session is gc'd.
        setProperty("finalizer", new SessionFinalizer(this));
    }

    /**
     * INTERNAL:
     * Return if this session is a historical session.
     */
    public boolean isHistoricalSession() {
        return false;
    }

    /**
     * PUBLIC:
     * Set if this session's decendants should use finalizers.
     * The allows certain finalizers such as in ClientSession to be enabled.
     * These are disable by default for performance reasons.
     */
    public void setIsFinalizersEnabled(boolean isFinalizersEnabled) {
        this.isFinalizersEnabled = isFinalizersEnabled;
    }

    /**
     * PUBLIC:
     * Return if the session is currently in the progress of a database transaction.
     * Because nested transactions are allowed check if the transaction mutex has been aquired.
     */
    public boolean isInTransaction() {
        return this.transactionMutex != null && this.transactionMutex.isAcquired();
    }
    
    /**
     * INTERNAL:
     * used to see if JPA Queries have been processed during initialization
     */
    public boolean isJPAQueriesProcessed(){
        return this.jpaQueriesProcessed;
    }
    
    /**
     * PUBLIC:
     * Returns true if Protected Entities should be built within this session
     */
    public boolean isProtectedSession(){
        return true;
    }

    /**
     * PUBLIC:
     * Return if this session is remote.
     */
    public boolean isRemoteSession() {
        return false;
    }

    /**
     * PUBLIC:
     * Return if this session is a unit of work.
     */
    public boolean isRemoteUnitOfWork() {
        return false;
    }

    /**
     * PUBLIC:
     * Return if this session is a server session.
     */
    public boolean isServerSession() {
        return false;
    }

    /**
     * PUBLIC:
     * Return if this session is a session broker.
     */
    public boolean isSessionBroker() {
        return false;
    }

    /**
     * INTERNAL:
     * Return if this session is synchronized.
     */
    public boolean isSynchronized() {
        return isSynchronized;
    }

    /**
     * PUBLIC:
     * Return if this session is a unit of work.
     */
    public boolean isUnitOfWork() {
        return false;
    }

    /**
     * ADVANCED:
     * Extract and return the primary key from the object.
     */
    public Object getId(Object domainObject) throws ValidationException {
        ClassDescriptor descriptor = getDescriptor(domainObject);
        return keyFromObject(domainObject, descriptor);
    }

    /**
     * ADVANCED:
     * Extract and return the primary key from the object.
     * @deprecated since EclipseLink 2.1, replaced by getId(Object)
     * @see #getId(Object)
     */
    @Deprecated
    public Vector keyFromObject(Object domainObject) throws ValidationException {
        ClassDescriptor descriptor = getDescriptor(domainObject);
        return (Vector)keyFromObject(domainObject, descriptor);
    }

    /**
     * ADVANCED:
     * Extract and return the primary key from the object.
     */
    public Object keyFromObject(Object domainObject, ClassDescriptor descriptor) throws ValidationException {
        if (descriptor == null) {
            throw ValidationException.missingDescriptor(domainObject.getClass().getName());
        }
        Object implemention = descriptor.getObjectBuilder().unwrapObject(domainObject, this);
        if (implemention == null) {
            return null;
        }
        return descriptor.getObjectBuilder().extractPrimaryKeyFromObject(implemention, this);
    }

    /**
     * PUBLIC:
     * Log the log entry.
     */
    public void log(SessionLogEntry entry) {
        if (this.isLoggingOff) {
            return;
        }
        if (shouldLog(entry.getLevel(), entry.getNameSpace())) {
            if (entry.getSession() == null) {// Used for proxy session.
                entry.setSession(this);
            }
            getSessionLog().log(entry);
        }
    }

    /**
     * Log a untranslated message to the EclipseLink log at FINER level.
     */
    public void logMessage(String message) {
        if (this.isLoggingOff) {
            return;
        }
        log(SessionLog.FINER, message, (Object[])null, null, false);
    }

    /**
     * INTERNAL:
     * A call back to do session specific preparation of a query.
     * <p>
     * The call back occurs soon before we clone the query for execution,
     * meaning that if this method needs to clone the query then the caller will
     * determine that it doesn't need to clone the query itself.
     */
    public DatabaseQuery prepareDatabaseQuery(DatabaseQuery query) {
        if (!isUnitOfWork() && query.isObjectLevelReadQuery()) {
            return ((ObjectLevelReadQuery)query).prepareOutsideUnitOfWork(this);
        } else {
            return query;
        }
    }


    /**
     * PUBLIC:
     * Read all of the instances of the class from the database.
     * This operation can be customized through using a ReadAllQuery,
     * or through also passing in a selection criteria.
     *
     * @see ReadAllQuery
     * @see #readAllObjects(Class, Expression)
     */
    public Vector readAllObjects(Class domainClass) throws DatabaseException {
        ReadAllQuery query = new ReadAllQuery();
        query.setIsExecutionClone(true);
        query.setReferenceClass(domainClass);
        return (Vector)executeQuery(query);
    }

    /**
     * PUBLIC:
     * Read all of the instances of the class from the database return through execution the SQL string.
     * The SQL string must be a valid SQL select statement or selecting stored procedure call.
     * This operation can be customized through using a ReadAllQuery.
 	 * Warning: Allowing an unverified SQL string to be passed into this
	 * method makes your application vulnerable to SQL injection attacks.
     *
     * @see ReadAllQuery
     */
    public Vector readAllObjects(Class domainClass, String sqlString) throws DatabaseException {
        ReadAllQuery query = new ReadAllQuery();
        query.setReferenceClass(domainClass);
        query.setSQLString(sqlString);
        query.setIsExecutionClone(true);
        return (Vector)executeQuery(query);
    }

    /**
     * PUBLIC:
     * Read all the instances of the class from the database returned through execution the Call string.
     * The Call can be an SQLCall or JPQLCall.
     *
     * example: session.readAllObjects(Employee.class, new SQLCall("SELECT * FROM EMPLOYEE"));
     * @see Call
     */
    public Vector readAllObjects(Class referenceClass, Call aCall) throws DatabaseException {
        ReadAllQuery raq = new ReadAllQuery();
        raq.setReferenceClass(referenceClass);
        raq.setCall(aCall);
        raq.setIsExecutionClone(true);
        return (Vector)executeQuery(raq);
    }

    /**
     * PUBLIC:
     * Read all of the instances of the class from the database matching the given expression.
     * This operation can be customized through using a ReadAllQuery.
     *
     * @see ReadAllQuery
     */
    public Vector readAllObjects(Class domainClass, Expression expression) throws DatabaseException {
        ReadAllQuery query = new ReadAllQuery();
        query.setReferenceClass(domainClass);
        query.setSelectionCriteria(expression);
        query.setIsExecutionClone(true);
        return (Vector)executeQuery(query);
    }

    /**
     * PUBLIC:
     * Read the first instance of the class from the database.
     * This operation can be customized through using a ReadObjectQuery,
     * or through also passing in a selection criteria.
     *
     * @see ReadObjectQuery
     * @see #readAllObjects(Class, Expression)
     */
    public Object readObject(Class domainClass) throws DatabaseException {
        ReadObjectQuery query = new ReadObjectQuery();
        query.setReferenceClass(domainClass);
        query.setIsExecutionClone(true);
        return executeQuery(query);
    }

    /**
     * PUBLIC:
     * Read the first instance of the class from the database return through execution the SQL string.
     * The SQL string must be a valid SQL select statement or selecting stored procedure call.
     * This operation can be customized through using a ReadObjectQuery.
 	 * Warning: Allowing an unverified SQL string to be passed into this
	 * method makes your application vulnerable to SQL injection attacks.
     *
     * @see ReadObjectQuery
     */
    public Object readObject(Class domainClass, String sqlString) throws DatabaseException {
        ReadObjectQuery query = new ReadObjectQuery();
        query.setReferenceClass(domainClass);
        query.setSQLString(sqlString);
        query.setIsExecutionClone(true);
        return executeQuery(query);
    }

    /**
     * PUBLIC:
     * Read the first instance of the class from the database returned through execution the Call string.
     * The Call can be an SQLCall or JPQLCall.
     *
     * example: session.readObject(Employee.class, new SQLCall("SELECT * FROM EMPLOYEE"));
     * @see SQLCall
     * @see JPQLCall
     */
    public Object readObject(Class domainClass, Call aCall) throws DatabaseException {
        ReadObjectQuery query = new ReadObjectQuery();
        query.setReferenceClass(domainClass);
        query.setCall(aCall);
        query.setIsExecutionClone(true);
        return executeQuery(query);
    }

    /**
     * PUBLIC:
     * Read the first instance of the class from the database matching the given expression.
     * This operation can be customized through using a ReadObjectQuery.
     *
     * @see ReadObjectQuery
     */
    public Object readObject(Class domainClass, Expression expression) throws DatabaseException {
        ReadObjectQuery query = new ReadObjectQuery();
        query.setReferenceClass(domainClass);
        query.setSelectionCriteria(expression);
        query.setIsExecutionClone(true);
        return executeQuery(query);
    }

    /**
     * PUBLIC:
     * Use the example object to consruct a read object query by the objects primary key.
     * This will read the object from the database with the same primary key as the object
     * or null if no object is found.
     */
    public Object readObject(Object object) throws DatabaseException {
        ReadObjectQuery query = new ReadObjectQuery();
        query.setSelectionObject(object);
        query.setIsExecutionClone(true);
        return executeQuery(query);
    }

    /**
     * PUBLIC:
     * Refresh the attributes of the object and of all of its private parts from the database.
     * The object will be pessimisticly locked on the database for the duration of the transaction.
     * If the object is already locked this method will wait until the lock is released.
     * A no wait option is available through setting the lock mode.
     * @see #refreshAndLockObject(Object, lockMode)
     */
    public Object refreshAndLockObject(Object object) throws DatabaseException {
        return refreshAndLockObject(object, ObjectBuildingQuery.LOCK);
    }

    /**
     * PUBLIC:
     * Refresh the attributes of the object and of all of its private parts from the database.
     * The object will be pessimisticly locked on the database for the duration of the transaction.
     * <p>Lock Modes: ObjectBuildingQuery.NO_LOCK, LOCK, LOCK_NOWAIT
     */
    public Object refreshAndLockObject(Object object, short lockMode) throws DatabaseException {
        ReadObjectQuery query = new ReadObjectQuery();
        query.setSelectionObject(object);
        query.refreshIdentityMapResult();
        query.cascadePrivateParts();
        query.setLockMode(lockMode);
        query.setIsExecutionClone(true);
        return executeQuery(query);
    }

    /**
     * PUBLIC:
     * Refresh the attributes of the object and of all of its private parts from the database.
     * This can be used to ensure the object is up to date with the database.
     * Caution should be used when using this to make sure the application has no un commited
     * changes to the object.
     */
    public Object refreshObject(Object object) throws DatabaseException {
        return refreshAndLockObject(object, ObjectBuildingQuery.NO_LOCK);
    }

    /**
     * PUBLIC:
     * Release the session.
     * This does nothing by default, but allows for other sessions such as the ClientSession to do something.
     */
    public void release() {
    }

    /**
     * INTERNAL:
     * Release the unit of work, if lazy release the connection.
     */
    public void releaseUnitOfWork(UnitOfWorkImpl unitOfWork) {
        // Nothing is required by default, allow subclasses to do cleanup.
        setNumberOfActiveUnitsOfWork(getNumberOfActiveUnitsOfWork() - 1);
    }


    /**
     * PUBLIC:
     * Remove the user defined property.
     */
    public void removeProperty(String property) {
        getProperties().remove(property);
    }

    /**
     * PUBLIC:
     * Remove all queries with the given queryName regardless of the argument types.
     *
     * @see #removeQuery(String, Vector)
     */
    public void removeQuery(String queryName) {
        getQueries().remove(queryName);
    }

    /**
     * PUBLIC:
     * Remove the specific query with the given queryName and argumentTypes.
     */
    public void removeQuery(String queryName, Vector argumentTypes) {
        Vector queries = (Vector)getQueries().get(queryName);
        if (queries == null) {
            return;
        } else {
            DatabaseQuery query = null;
            for (Enumeration enumtr = queries.elements(); enumtr.hasMoreElements();) {
                query = (DatabaseQuery)enumtr.nextElement();
                if (Helper.areTypesAssignable(argumentTypes, query.getArgumentTypes())) {
                    break;
                }
            }
            if (query != null) {
                queries.remove(query);
            }
        }
    }

    /**
     * PROTECTED:
     * Attempts to rollback the running internally started external transaction.
     * Returns true only in one case -
     * extenal transaction has been internally rolled back during this method call:
     * wasJTSTransactionInternallyStarted()==true in the beginning of this method and
     * wasJTSTransactionInternallyStarted()==false in the end of this method.
     */
    protected boolean rollbackExternalTransaction() {
        boolean externalTransactionHasRolledBack = false;
        if (hasExternalTransactionController() && wasJTSTransactionInternallyStarted()) {
            try {
                getExternalTransactionController().rollbackTransaction(this);
            } catch (RuntimeException exception) {
                handleException(exception);
            }
            if (!wasJTSTransactionInternallyStarted()) {
                externalTransactionHasRolledBack = true;
                log(SessionLog.FINER, SessionLog.TRANSACTION, "external_transaction_has_rolled_back_internally");
            }
        }
        return externalTransactionHasRolledBack;
    }

    /**
     * PUBLIC:
     * Rollback the active database transaction.
     * This allows a group of database modification to be commited or rolledback as a unit.
     * All writes/deletes will be sent to the database be will not be visible to other users until commit.
     * Although databases do not allow nested transaction,
     * EclipseLink supports nesting through only committing to the database on the outer commit.
     *
     * @exception DatabaseException if the database connection is lost or the rollback fails.
     * @exception ConcurrencyException if this session is not within a transaction.
     */
    public void rollbackTransaction() throws DatabaseException, ConcurrencyException {
        ConcurrencyManager mutex = getTransactionMutex();
        // Ensure release of mutex and call subclass specific release.
        try {
            if (!mutex.isNested()) {
                if (this.eventManager != null) {
                    this.eventManager.preRollbackTransaction();
                }
                basicRollbackTransaction();
                if (this.eventManager != null) {
                    this.eventManager.postRollbackTransaction();
                }
            }
        } finally {
            mutex.release();

            // If there is no db transaction in progress
            // if there is an active external transaction
            // which was started internally - it should be rolled back internally, too.
            if (!mutex.isAcquired()) {
                rollbackExternalTransaction();
            }
        }
    }

    /**
     * INTERNAL:
     * Set the accessor.
     */
    public void setAccessor(Accessor accessor) {
        this.accessors = new ArrayList(1);
        this.accessors.add(accessor);
    }

    /**
     * INTERNAL:
     * Allow the session to be used from a session broker.
     */
    public void setBroker(AbstractSession broker) {
        this.broker = broker;
    }


    /**
     * INTERNAL:
     * The commit manager is used to resolve referncial integrity on commits of multiple objects.
     */
    public void setCommitManager(CommitManager commitManager) {
        this.commitManager = commitManager;
    }

    public void setEntityListenerInjectionManager(
            EntityListenerInjectionManager entityListenerInjectionManager) {
        this.entityListenerInjectionManager = entityListenerInjectionManager;
    }
    
    /**
     * INTERNAL:
     * Set the event manager.
     * The event manager can be used to register for various session events.
     */
    public void setEventManager(SessionEventManager eventManager) {
        this.eventManager = eventManager;
        if (eventManager != null) {
            this.eventManager.setSession(this);
        }
    }

    /**
     * PUBLIC:
     * Set the exceptionHandler.
     * Exception handler can catch errors that occur on queries or during database access.
     */
    public void setExceptionHandler(ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    /**
     * Used for JTS integration internally by ServerPlatform.
     */
    public void setExternalTransactionController(ExternalTransactionController externalTransactionController) {
        this.externalTransactionController = externalTransactionController;
        if (externalTransactionController == null) {
            return;
        }
        if (!hasBroker()) {
            externalTransactionController.setSession(this);
        }
    }

    /**
     * PUBLIC:
     * set the integrityChecker. IntegrityChecker holds all the ClassDescriptor Exceptions.
     */
    public void setIntegrityChecker(IntegrityChecker integrityChecker) {
        this.integrityChecker = integrityChecker;
    }
    
    /**
     * INTERNAL:
     * used to set if JPA Queries have been processed during initialization
     */
    public void setJPAQueriesProcessed(boolean jpaQueriesProcessed){
        this.jpaQueriesProcessed = jpaQueriesProcessed;
    }

    /**
     * PUBLIC:
     * Set the writer to which an accessor writes logged messages and SQL.
     * If not set, this reference defaults to a writer on System.out.
     * To enable logging logMessages() is used.
     *
     * @see #logMessages()
     */
    public void setLog(Writer log) {
        getSessionLog().setWriter(log);
    }

    /**
     * PUBLIC:
     * Set the login.
     */
    public void setLogin(DatabaseLogin login) {
        setDatasourceLogin(login);
    }

    /**
     * PUBLIC:
     * Set the login.
     */
    public void setLogin(Login login) {
        setDatasourceLogin(login);
    }

    /**
     * PUBLIC:
     * Set the login.
     */
    public void setDatasourceLogin(Login login) {
        getProject().setDatasourceLogin(login);
        this.platform = null;
    }

    /**
     * PUBLIC:
     * Set the name of the session.
     * This is used with the session broker.
     */
    public void setName(String name) {
        this.name = name;
    }

    protected void setNumberOfActiveUnitsOfWork(int numberOfActiveUnitsOfWork) {
        this.numberOfActiveUnitsOfWork = numberOfActiveUnitsOfWork;
    }

    /**
     * PUBLIC:
     * Set the default pessimistic lock timeout value. This value will be used
     * to set the WAIT clause of a SQL SELECT FOR UPDATE statement. It defines
     * how long EcliseLink should wait for a lock on the database row before
     * aborting.
     */
    public void setPessimisticLockTimeoutDefault(Integer pessimisticLockTimeoutDefault) {
        this.pessimisticLockTimeoutDefault = pessimisticLockTimeoutDefault;
    }

    /**
     * PUBLIC:
     * Set the default query timeout for this session.
     * This timeout will apply to any queries that do not have a timeout set,
     * and that do not have a default timeout defined in their descriptor.
     */
    public void setQueryTimeoutDefault(int queryTimeoutDefault) {
        this.queryTimeoutDefault = queryTimeoutDefault;
    }

    /**
     * PUBLIC:
     * Set the profiler for the session.
     * This allows for performance operations to be profiled.
     */
    public void setProfiler(SessionProfiler profiler) {
        this.profiler = profiler;
        if (profiler != null) {
            profiler.setSession(this);
            setIsInProfile(getProfiler().getProfileWeight() != SessionProfiler.NONE);
            // Clear cached flag that bypasses the profiler check.
            getIdentityMapAccessorInstance().getIdentityMapManager().checkIsCacheAccessPreCheckRequired();
        } else {
            setIsInProfile(false);
        }
    }

    /**
     * INTERNAL:
     * Set the project, the project holds configuration information including the descriptors.
     */
    public void setProject(org.eclipse.persistence.sessions.Project project) {
        this.project = project;
    }

    /**
     * INTERNAL:
     * Set the user defined properties by shallow copying the propertiesMap.
     * @param propertiesMap
     */
    public void setProperties(Map<Object, Object> propertiesMap) {
        if (null == propertiesMap) {
            // Keep current behavior and set properties map to null
            properties = propertiesMap;
        } else {
            /*
             * Bug# 219097 Clone as (HashMap) possible immutable maps to avoid
             * an UnsupportedOperationException on a later put() We do not know
             * the key:value type values at design time. putAll() is not
             * synchronized. We clone all maps whether immutable or not.
             */
            properties = new HashMap();
            // Shallow copy all internal key:value pairs - a null propertiesMap will throw a NPE
            properties.putAll(propertiesMap);
        }
    }

    /**
	 * PUBLIC: Allow for user defined properties.
	 */
    public void setProperty(String propertyName, Object propertyValue) {
        getProperties().put(propertyName, propertyValue);
    }

    /**
     * INTERNAL:
     * Set the named queries.
     */
    public void setQueries(Map<String, List<DatabaseQuery>> queries) {
        this.queries = queries;
    }

    /**
     * PUBLIC:
     * Set the session log to which an accessor logs messages and SQL.
     * If not set, this will default to a session log on a writer on System.out.
     * To enable logging, log level can not be OFF.
     * Also set a backpointer to this session in SessionLog.
     *
     * @see #logMessage(String)
     */
    public void setSessionLog(SessionLog sessionLog) {
        this.isLoggingOff = false;
        this.sessionLog = sessionLog;
        if ((sessionLog != null) && (sessionLog.getSession() == null)) {
            sessionLog.setSession(this);
        }
    }

    /**
     * INTERNAL:
     * Set isSynchronized flag to indicate that this session is synchronized.
     * This method should only be called by setSynchronized methods of derived classes.
     */
    public void setSynchronized(boolean synched) {
        isSynchronized = synched;
    }

    protected void setTransactionMutex(ConcurrencyManager transactionMutex) {
        this.transactionMutex = transactionMutex;
    }

    /**
     * INTERNAL:
     * Return if a JTS transaction was started by the session.
     * The session will start a JTS transaction if a unit of work or transaction is begun without a JTS transaction present.
     */
    public void setWasJTSTransactionInternallyStarted(boolean wasJTSTransactionInternallyStarted) {
        this.wasJTSTransactionInternallyStarted = wasJTSTransactionInternallyStarted;
    }

    /**
     * PUBLIC:
     * Return if logging is enabled (false if log level is OFF)
     */
    public boolean shouldLogMessages() {
        if (this.isLoggingOff) {
            return false;
        }
        if (getLogLevel(null) == SessionLog.OFF) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * INTERNAL:
     * Start the operation timing.
     */
    public void startOperationProfile(String operationName) {
        if (this.isInProfile) {
            getProfiler().startOperationProfile(operationName);
        }
    }

    /**
     * INTERNAL:
     * Start the operation timing.
     */
    public void startOperationProfile(String operationName, DatabaseQuery query, int weight) {
        if (this.isInProfile) {
            getProfiler().startOperationProfile(operationName, query, weight);
        }
    }

    /**
     * Print the connection status with the session.
     */
    public String toString() {
        StringWriter writer = new StringWriter();
        writer.write(getSessionTypeString() + "(" + Helper.cr() + "\t" + getAccessor() + Helper.cr() + "\t" + getDatasourcePlatform() + ")");
        return writer.toString();
    }

    /**
     * INTERNAL:
     * Unwrap the object if required.
     * This is used for the wrapper policy support and EJB.
     */
    public Object unwrapObject(Object proxy) {
        return getDescriptor(proxy).getObjectBuilder().unwrapObject(proxy, this);
    }

    /**
     * PUBLIC:
     * Update the object and all of its privately owned parts in the database.
     * Update should only be used if the application knows that the object is new,
     * otherwise writeObject should be used.
     * The update operation can be customized through using an update query.
     *
     * @exception DatabaseException if an error occurs on the database,
     * these include constraint violations, security violations and general database errors.
     * @exception OptimisticLockException if the object's descriptor is using optimistic locking and
     * the object has been updated or deleted by another user since it was last read.
     *
     * @see UpdateObjectQuery
     * @see #writeObject(Object)
     */
    public Object updateObject(Object domainObject) throws DatabaseException, OptimisticLockException {
        UpdateObjectQuery query = new UpdateObjectQuery();
        query.setObject(domainObject);
        query.setIsExecutionClone(true);
        return executeQuery(query);
    }

    /**
     * ADVANCED:
     * This can be used to help debugging an object identity problem.
     * An object identity problem is when an object in the cache references an object not in the cache.
     * This method will validate that all cached objects are in a correct state.
     */
    public void validateCache() {
        getIdentityMapAccessorInstance().validateCache();
    }

    /**
     * INTERNAL:
     * This method will be used to update the query with any settings required
     * For this session.  It can also be used to validate execution.
     */
    public void validateQuery(DatabaseQuery query) {
        // a no-op for this class
    }

    /**
     * TESTING:
     * This is used by testing code to ensure that a deletion was successful.
     */
    public boolean verifyDelete(Object domainObject) {
        ObjectBuilder builder = getDescriptor(domainObject).getObjectBuilder();
        Object implementation = builder.unwrapObject(domainObject, this);

        return builder.verifyDelete(implementation, this);
    }

    /**
     * INTERNAL:
     * Return if a JTS transaction was started by the session.
     * The session will start a JTS transaction if a unit of work or transaction is begun without a JTS transaction present.
     */
    public boolean wasJTSTransactionInternallyStarted() {
        return wasJTSTransactionInternallyStarted;
    }

    /**
     * INTERNAL:
     * Wrap the object if required.
     * This is used for the wrapper policy support and EJB.
     */
    public Object wrapObject(Object implementation) {
        return getDescriptor(implementation).getObjectBuilder().wrapObject(implementation, this);
    }

    /**
     * INTERNAL:
     * Write all of the objects and all of their privately owned parts in the database.
     * The allows for a group of new objects to be commited as a unit.
     * The objects will be commited through a single transactions and any
     * foreign keys/circular references between the objects will be resolved.
     */
    protected void writeAllObjectsWithChangeSet(UnitOfWorkChangeSet uowChangeSet) throws DatabaseException, OptimisticLockException {
        getCommitManager().commitAllObjectsWithChangeSet(uowChangeSet);
    }

    /**
     * PUBLIC:
     * Write the object and all of its privately owned parts in the database.
     * Write will determine if an insert or an update should be done,
     * it may go to the database to determine this (by default will check the identity map).
     * The write operation can be customized through using an write query.
     *
     * @exception DatabaseException if an error occurs on the database,
     * these include constraint violations, security violations and general database errors.
     * @exception OptimisticLockException if the object's descriptor is using optimistic locking and
     * the object has been updated or deleted by another user since it was last read.
     *
     * @see WriteObjectQuery
     * @see #insertObject(Object)
     * @see #updateObject(Object)
     */
    public Object writeObject(Object domainObject) throws DatabaseException, OptimisticLockException {
        WriteObjectQuery query = new WriteObjectQuery();
        query.setObject(domainObject);
        query.setIsExecutionClone(true);
        return executeQuery(query);
    }

    /**
     * INTERNAL:
     * This method notifies the accessor that a particular sets of writes has
     * completed.  This notification can be used for such thing as flushing the
     * batch mechanism
     */
    public void writesCompleted() {
        if (getAccessors() == null) {
            return;
        }
        for (Accessor accessor : getAccessors()) {
            accessor.writesCompleted(this);
    }
    }

    /**
      * INTERNAL:
      * RemoteCommandManager method. This is a required method in order
      * to implement the CommandProcessor interface.
      * Process the remote command from the RCM. The command may have come from
      * any remote session or application. Since this is a EclipseLink session we can
      * always assume that the object that we receive here will be a Command object.
      */
    public void processCommand(Object command) {
        ((Command)command).executeWithSession(this);
    }

    /**
     * INTERNAL:
     * Process the JPA named queries into EclipseLink Session queries. This 
     * method is called after descriptor initialization.
     */
    protected void processJPAQueries() {
        if (! jpaQueriesProcessed) {
            // Process the JPA queries that do not query table per tenant entities.
            for (DatabaseQuery jpaQuery : getJPAQueries()) {
                processJPAQuery(jpaQuery);
            }
            
            // Process the JPA queries that query table per tenant entities. At
            // the EMF level, these queries will be initialized and added right
            // away. At the EM level we must defer their initialization to each
            // individual client session.
            for (DatabaseQuery jpaQuery : getJPATablePerTenantQueries()) {
                boolean processQuery = true;
                
                for (ClassDescriptor descriptor : jpaQuery.getDescriptors()) {
                    // If the descriptor is not fully initialized then we can
                    // not initialize the query and must isolate it to be 
                    // initialized and stored per client session (EM).
                    if (! descriptor.isFullyInitialized()) {
                        processQuery = false;
                        break;
                    }
                }
                
                if (processQuery) {
                    processJPAQuery(jpaQuery);
                } else {
                    addTablePerTenantQuery(jpaQuery);
                }
            }
            
            jpaQueriesProcessed = true;
        }
    }
    
    /**
     * INTERNAL:
     * Process the JPA named query into an EclipseLink Session query. This 
     * method is called after descriptor initialization.
     */
    protected void processJPAQuery(DatabaseQuery jpaQuery) {
        // This is a hack, to allow the core Session to initialize JPA queries, without have a dependency on JPA.
        // They need to be initialized after login, as the database platform must be known.
        jpaQuery.prepareInternal(this);
        DatabaseQuery databaseQuery = (DatabaseQuery) jpaQuery.getProperty("databasequery");
        databaseQuery = (databaseQuery == null)? jpaQuery : databaseQuery;
        addQuery(databaseQuery, false); // this should be true but for backward compatibility it is set to false.
    }
    
    /**
     * PUBLIC:
     * Return the CommandManager that allows this session to act as a
     * CommandProcessor and receive or propagate commands from/to the
     * EclipseLink cluster.
     *
     * @see CommandManager
     * @return The CommandManager instance that controls the remote command
     * service for this session
     */
    public CommandManager getCommandManager() {
        return commandManager;
    }

    /**
     * ADVANCED:
     * Set the CommandManager that allows this session to act as a
     * CommandProcessor and receive or propagate commands from/to the
     * EclipseLink cluster.
     *
     * @see CommandManager
     * @param mgr The CommandManager instance to control the remote command
     * service for this session
     */
    public void setCommandManager(CommandManager mgr) {
        commandManager = mgr;
    }

    /**
     * PUBLIC:
     * Return whether changes should be propagated to other sessions or applications
     * in a EclipseLink cluster through the Remote Command Manager mechanism. In order for
     * this to occur the CommandManager must be set.
     *
     * @see #setCommandManager(CommandManager)
     * @return True if propagation is set to occur, false if not
     */
    public boolean shouldPropagateChanges() {
        return shouldPropagateChanges;
    }

    /**
     * ADVANCED:
     * Set whether changes should be propagated to other sessions or applications
     * in a EclipseLink cluster through the Remote Command Manager mechanism. In order for
     * this to occur the CommandManager must be set.
     *
     * @see #setCommandManager(CommandManager)
     * @param choice If true (and the CommandManager is set) then propagation will occur
     */
    public void setShouldPropagateChanges(boolean choice) {
        shouldPropagateChanges = choice;
    }

    /**
     * INTERNAL:
     * RemoteCommandManager method. This is a required method in order
     * to implement the CommandProcessor interface.
     * Return true if a message at the specified log level would be logged given the
     * log level setting of this session. This can be used by the CommandManager to
     * know whether it should even bother to create the localized strings and call
     * the logMessage method, or if it would only find that the message would not be
     * logged because the session level does not permit logging. The log level passed
     * in will be one of the constants LOG_ERROR, LOG_WARNING, LOG_INFO, and LOG_DEBUG
     * defined in the CommandProcessor interface.
     */
    public boolean shouldLogMessages(int logLevel) {
        if (this.isLoggingOff) {
            return false;
        }
        if (LOG_ERROR == logLevel) {
            return getLogLevel(SessionLog.PROPAGATION) <= SessionLog.SEVERE;
        }
        if (LOG_WARNING == logLevel) {
            return getLogLevel(SessionLog.PROPAGATION) <= SessionLog.WARNING;
        }
        if (LOG_INFO == logLevel) {
            return getLogLevel(SessionLog.PROPAGATION) <= SessionLog.FINER;
        }
        if (LOG_DEBUG == logLevel) {
            return getLogLevel(SessionLog.PROPAGATION) <= SessionLog.FINEST;
        }
        return false;
    }

    /**
     * INTERNAL:
     * RemoteCommandManager method. This is a required method in order
     * to implement the CommandProcessor interface.
     * Log the specified message string at the specified level if it should be logged
     * given the log level setting in this session. The log level passed in will be one
     * of the constants LOG_ERROR, LOG_WARNING, LOG_INFO, and LOG_DEBUG defined in the
     * CommandProcessor interface.
     */
    public void logMessage(int logLevel, String message) {
        if (this.isLoggingOff) {
            return;
        }
        if (shouldLogMessages(logLevel)) {
            int level;
            switch (logLevel) {
            case CommandProcessor.LOG_ERROR:
                level = SessionLog.SEVERE;
                break;
            case CommandProcessor.LOG_WARNING:
                level = SessionLog.WARNING;
                break;
            case CommandProcessor.LOG_INFO:
                level = SessionLog.FINER;
                break;
            case CommandProcessor.LOG_DEBUG:
                level = SessionLog.FINEST;
                break;
            default:
                level = SessionLog.ALL;
                ;
            }
            log(level, message, null, null, false);
        }
    }

    /**
     * PUBLIC:
     * <p>
     * Return the log level
     * </p><p>
     *
     * @return the log level
     * </p><p>
     * @param category  the string representation of a EclipseLink category, e.g. "sql", "transaction" ...
     * </p>
     */
    public int getLogLevel(String category) {
        return getSessionLog().getLevel(category);
    }

    /**
     * PUBLIC:
     * <p>
     * Return the log level
     * </p><p>
     * @return the log level
     * </p>
     */
    public int getLogLevel() {
        return getSessionLog().getLevel();
    }

    /**
     * PUBLIC:
     * <p>
     * Set the log level
     * </p><p>
     *
     * @param level     the new log level
     * </p>
     */
    public void setLogLevel(int level) {
        this.isLoggingOff = false;
        getSessionLog().setLevel(level);
    }

    /**
     * PUBLIC:
     * Return true if SQL logging should log visible bind parameters. If the 
     * shouldDisplayData is not set, check the session log level and return 
     * true for a level greater than CONFIG.
     */
    public boolean shouldDisplayData() {
        return getSessionLog().shouldDisplayData();
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
     * @param category  the string representation of a EclipseLink category
     * </p>
     */
    public boolean shouldLog(int Level, String category) {
        if (this.isLoggingOff) {
            return false;
        }
        return getSessionLog().shouldLog(Level, category);
    }

    /**
     * PUBLIC:
     * <p>
     * Log a message with level and category that needs to be translated.
     * </p><p>
     *
     * @param level  the log request level value
     * </p><p>
     * @param message  the string message
     * </p><p>
     * @param category  the string representation of a EclipseLink category.
     * </p>
     */
    public void log(int level, String category, String message) {
        if (this.isLoggingOff) {
            return;
        }
        if (!shouldLog(level, category)) {
            return;
        }
        log(level, category, message, (Object[])null);
    }

    /**
     * PUBLIC:
     * <p>
     * Log a message with level, category and a parameter that needs to be translated.
     * </p><p>
     *
     * @param level  the log request level value
     * </p><p>
     * @param message  the string message
     * </p><p>
     * @param category  the string representation of a EclipseLink category.
     * </p><p>
     * @param param  a parameter of the message
     * </p>
     */
    public void log(int level, String category, String message, Object param) {
        if (this.isLoggingOff) {
            return;
        }
        if (!shouldLog(level, category)) {
            return;
        }
        log(level, category, message, new Object[] { param });
    }

    /**
     * PUBLIC:
     * <p>
     * Log a message with level, category and two parameters that needs to be translated.
     * </p><p>
     *
     * @param level  the log request level value
     * </p><p>
     * @param message  the string message
     * </p><p>
     * @param category  the string representation of a EclipseLink category.
     * </p><p>
     * @param param1  a parameter of the message
     * </p><p>
     * @param param2  second parameter of the message
     * </p>
     */
    public void log(int level, String category, String message, Object param1, Object param2) {
        if (this.isLoggingOff) {
            return;
        }
        if (!shouldLog(level, category)) {
            return;
        }
        log(level, category, message, new Object[] { param1, param2 });
    }

    /**
     * PUBLIC:
     * <p>
     * Log a message with level, category and three parameters that needs to be translated.
     * </p><p>
     *
     * @param level  the log request level value
     * </p><p>
     * @param message  the string message
     * </p><p>
     * @param category  the string representation of a EclipseLink category.
     * </p><p>
     * @param param1  a parameter of the message
     * </p><p>
     * @param param2  second parameter of the message
     * </p><p>
     * @param param3  third parameter of the message
     * </p>
     */
    public void log(int level, String category, String message, Object param1, Object param2, Object param3) {
        if (this.isLoggingOff) {
            return;
        }
        if (!shouldLog(level, category)) {
            return;
        }
        log(level, category, message, new Object[] { param1, param2, param3 });
    }

    /**
     * PUBLIC:
     * <p>
     * Log a message with level, category and an array of parameters that needs to be translated.
     * </p><p>
     *
     * @param level  the log request level value
     * </p><p>
     * @param message  the string message
     * </p><p>
     * @param category  the string representation of a EclipseLink category.
     * </p><p>
     * @param params  array of parameters to the message
     * </p>
     */
    public void log(int level, String category, String message, Object[] params) {
        if (this.isLoggingOff) {
            return;
        }
        log(level, category, message, params, null);
    }

    /**
     * PUBLIC:
     * <p>
     * Log a message with level, category, parameters and accessor that needs to be translated.
     * </p><p>
     *
     * @param level  the log request level value
     * </p><p>
     * @param message  the string message
     * </p><p>
     * @param params  array of parameters to the message
     * </p><p>
     * @param accessor  the connection that generated the log entry
     * </p><p>
     * @param category  the string representation of a EclipseLink category.
     * </p>
     */
    public void log(int level, String category, String message, Object[] params, Accessor accessor) {
        if (this.isLoggingOff) {
            return;
        }
        log(level, category, message, params, accessor, true);
    }

    /**
     * PUBLIC:
     * <p>
     * Log a message with level, category, parameters and accessor.  shouldTranslate determines if the message needs to be translated.
     * </p><p>
     *
     * @param level  the log request level value
     * </p><p>
     * @param message  the string message
     * </p><p>
     * @param params  array of parameters to the message
     * </p><p>
     * @param accessor  the connection that generated the log entry
     * </p><p>
     * @param category  the string representation of a EclipseLink category.
     * </p><p>
     * @param shouldTranslate  true if the message needs to be translated.
     * </p>
     */
    public void log(int level, String category, String message, Object[] params, Accessor accessor, boolean shouldTranslate) {
        if (this.isLoggingOff) {
            return;
        }
        if (shouldLog(level, category)) {
            startOperationProfile(SessionProfiler.Logging);
            log(new SessionLogEntry(level, category, this, message, params, accessor, shouldTranslate));
            endOperationProfile(SessionProfiler.Logging);
        }
    }

    /**
     * PUBLIC:
     * <p>
     * Log a message with level, parameters and accessor that needs to be translated.
     * </p><p>
     *
     * @param level  the log request level value
     * </p><p>
     * @param message  the string message
     * </p><p>
     * @param params  array of parameters to the message
     * </p><p>
     * @param accessor  the connection that generated the log entry
     * </p>
     */
    public void log(int level, String message, Object[] params, Accessor accessor) {
        if (this.isLoggingOff) {
            return;
        }
        log(level, message, params, accessor, true);
    }

    /**
     * PUBLIC:
     * <p>
     * Log a message with level, parameters and accessor.  shouldTranslate determines if the message needs to be translated.
     * </p><p>
     *
     * @param level  the log request level value
     * </p><p>
     * @param message  the string message
     * </p><p>
     * @param params  array of parameters to the message
     * </p><p>
     * @param accessor  the connection that generated the log entry
     * </p><p>
     * @param shouldTranslate  true if the message needs to be translated.
     * </p>
     */
    public void log(int level, String message, Object[] params, Accessor accessor, boolean shouldTranslate) {
        if (this.isLoggingOff) {
            return;
        }
        if (shouldLog(level, null)) {
            startOperationProfile(SessionProfiler.Logging);
            log(new SessionLogEntry(level, this, message, params, accessor, shouldTranslate));
            endOperationProfile(SessionProfiler.Logging);
        }
    }

    /**
     * PUBLIC:
     * <p>
     * Log a throwable with level and category.
     * </p><p>
     *
     * @param level  the log request level value
     * </p><p>
     * @param category  the string representation of a EclipseLink category.
     * </p><p>
     * @param throwable  a Throwable
     * </p>
     */
    public void logThrowable(int level, String category, Throwable throwable) {
        if (this.isLoggingOff) {
            return;
        }
        // Must not create the log if not logging as is a performance issue.
        if (shouldLog(level, category)) {
            startOperationProfile(SessionProfiler.Logging);
            log(new SessionLogEntry(this, level, category, throwable));
            endOperationProfile(SessionProfiler.Logging);
        }
    }

    /**
     * PUBLIC:
     * <p>
     * This method is called when a severe level message needs to be logged.
     * The message will be translated
     * </p><p>
     *
     * @param message  the message key
     * </p>
     */
    public void severe(String message, String category) {
        if (this.isLoggingOff) {
            return;
        }
        log(SessionLog.SEVERE, category, message);
    }

    /**
     * PUBLIC:
     * <p>
     * This method is called when a warning level message needs to be logged.
     * The message will be translated
     * </p><p>
     *
     * @param message  the message key
     * </p>
     */
    public void warning(String message, String category) {
        if (this.isLoggingOff) {
            return;
        }
        log(SessionLog.WARNING, category, message);
    }

    /**
     * PUBLIC:
     * <p>
     * This method is called when a info level message needs to be logged.
     * The message will be translated
     * </p><p>
     *
     * @param message  the message key
     * </p>
     */
    public void info(String message, String category) {
        if (this.isLoggingOff) {
            return;
        }
        log(SessionLog.INFO, category, message);
    }

    /**
     * PUBLIC:
     * <p>
     * This method is called when a config level message needs to be logged.
     * The message will be translated
     * </p><p>
     *
     * @param message  the message key
     * </p>
     */
    public void config(String message, String category) {
        if (this.isLoggingOff) {
            return;
        }
        log(SessionLog.CONFIG, category, message);
    }

    /**
     * PUBLIC:
     * <p>
     * This method is called when a fine level message needs to be logged.
     * The message will be translated
     * </p><p>
     *
     * @param message  the message key
     * </p>
     */
    public void fine(String message, String category) {
        if (this.isLoggingOff) {
            return;
        }
        log(SessionLog.FINE, category, message);
    }

    /**
     * PUBLIC:
     * <p>
     * This method is called when a finer level message needs to be logged.
     * The message will be translated
     * </p><p>
     *
     * @param message  the message key
     * </p>
     */
    public void finer(String message, String category) {
        if (this.isLoggingOff) {
            return;
        }
        log(SessionLog.FINER, category, message);
    }

    /**
     * PUBLIC:
     * <p>
     * This method is called when a finest level message needs to be logged.
     * The message will be translated
     * </p><p>
     *
     * @param message  the message key
     * </p>
     */
    public void finest(String message, String category) {
        if (this.isLoggingOff) {
            return;
        }
        log(SessionLog.FINEST, category, message);
    }

    /**
     * PUBLIC:
     * Allow any SEVERE level exceptions that occur within EclipseLink to be logged and handled by the exception handler.
     */
    public Object handleSevere(RuntimeException exception) throws RuntimeException {
        logThrowable(SessionLog.SEVERE, null, exception);
        if (hasExceptionHandler()) {
            return getExceptionHandler().handleException(exception);
        } else {
            throw exception;
        }
    }

    /**
      * INTERNAL:
      */
    public void releaseReadConnection(Accessor connection) {
        //bug 4668234 -- used to only release connections on server sessions but should always release
        //do nothing -- overidden in UnitOfWork,ClientSession and ServerSession
    }

    /**
     * INTERNAL:
     * Copies descriptors cached on the Project.
     * Used after Project.descriptors has been reset by addDescriptor(s) when the session is connected.
     */
   public void copyDescriptorsFromProject() {
       this.descriptors = getDescriptors();
   }

    /**
     * INTERNAL:
     * This method will be used to copy all EclipseLink named queries defined in descriptors into the session.
     * @param allowSameQueryNameDiffArgsCopyToSession  if the value is true, it allow
     * multiple queries of the same name but different arguments to be copied to the session.
     */
    public void copyDescriptorNamedQueries(boolean allowSameQueryNameDiffArgsCopyToSession) {
        for (ClassDescriptor descriptor : getProject().getOrderedDescriptors()) {
            Map queries  = descriptor.getQueryManager().getQueries();
            if ((queries != null) && (queries.size() > 0)) {
                for (Iterator keyValueItr = queries.entrySet().iterator(); keyValueItr.hasNext();){
                    Map.Entry entry = (Map.Entry) keyValueItr.next();
                    Vector thisQueries = (Vector)entry.getValue();
                    if ((thisQueries != null) && (thisQueries.size() > 0)){
                        for( Iterator thisQueriesItr=thisQueries.iterator();thisQueriesItr.hasNext();){
                            DatabaseQuery queryToBeAdded = (DatabaseQuery)thisQueriesItr.next();
                            if (allowSameQueryNameDiffArgsCopyToSession){
                                addQuery(queryToBeAdded, false);
                            } else {
                                if (getQuery(queryToBeAdded.getName()) == null){
                                    addQuery(queryToBeAdded, false);
                                } else {
                                    log(SessionLog.WARNING, SessionLog.PROPERTIES, "descriptor_named_query_cannot_be_added", new Object[]{queryToBeAdded,queryToBeAdded.getName(),queryToBeAdded.getArgumentTypes()});
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * INTERNAL:
     * This method rises appropriate for the session event(s)
     * right after connection is acquired.
     */
    public void postAcquireConnection(Accessor accessor) {
        if (getProject().hasVPDIdentifier(this)) {
            if (getPlatform().supportsVPD()) {
                DatabaseQuery query = getPlatform().getVPDSetIdentifierQuery(getProject().getVPDIdentifier());
                List argValues = new ArrayList();
                query.addArgument(getProject().getVPDIdentifier());
                argValues.add(getProperty(getProject().getVPDIdentifier()));
                executeQuery(query, argValues);
            } else {
                throw ValidationException.vpdNotSupported(getPlatform().getClass().getName());
            }
        }
        
        if (this.eventManager != null) { 
            this.eventManager.postAcquireConnection(accessor);
        }
    }

    /**
     * INTERNAL:
     * This method rises appropriate for the session event(s)
     * right before the connection is released.
     */
    public void preReleaseConnection(Accessor accessor) {
        if (getProject().hasVPDIdentifier(this)) {
            if (getPlatform().supportsVPD()) {
                DatabaseQuery query = getPlatform().getVPDClearIdentifierQuery(getProject().getVPDIdentifier());
                List argValues = new ArrayList();
                query.addArgument(getProject().getVPDIdentifier());
                argValues.add(getProperty(getProject().getVPDIdentifier()));
                executeQuery(query, argValues);
            } else {
                throw ValidationException.vpdNotSupported(getPlatform().getClass().getName());
            }
        }
        
        if (this.eventManager != null) { 
            this.eventManager.preReleaseConnection(accessor);
        }
    }

    /**
     * INTERNAL:
     * Execute the call on the database. Calling this method will bypass a 
     * global setting to disallow native SQL queries. (set by default when
     * one Entity is marked as multitenant)
     * 
     * The row count is returned. 
     * 
     * The call can be a stored procedure call, SQL call or other type of call.
     * 
     * <p>Example:
     * <p>session.executeNonSelectingCall(new SQLCall("Delete from Employee"), true);
     *
     * @see #priviledgedExecuteSelectingCall(Call)
     */
    public int priviledgedExecuteNonSelectingCall(Call call) throws DatabaseException {
        DataModifyQuery query = new DataModifyQuery();
        query.setAllowNativeSQLQuery(true);
        query.setIsExecutionClone(true);
        query.setCall(call);
        Integer value = (Integer)executeQuery(query);
        if (value == null) {
            return 0;
        } else {
            return value.intValue();
        }
    }

    /**
     * INTERNAL:
     * Execute the call on the database and return the result. Calling this
     * method will bypass a global setting to disallow native SQL queries. (set 
     * by default when one Entity is marked as multitenant)
     *  
     * The call must return a value, if no value is return executeNonSelectCall 
     * must be used.
     * 
     * The call can be a stored procedure call, SQL call or other type of call.
     * 
     * A vector of database rows is returned, database row implements Java 2 Map 
     * which should be used to access the data.
     * 
     * <p>Example:
     * <p>session.executeSelectingCall(new SQLCall("Select * from Employee");
     *
     * @see #priviledgedExecuteNonSelectingCall(Call)
     */
    public Vector priviledgedExecuteSelectingCall(Call call) throws DatabaseException {
        DataReadQuery query = new DataReadQuery();
        query.setAllowNativeSQLQuery(true);
        query.setCall(call);
        query.setIsExecutionClone(true);
        return (Vector)executeQuery(query);
    }
    
    /**
     * INTERNAL:
     * This method is called in case externalConnectionPooling is used.
     * If returns true, accessor used by the session keeps its
     * connection open until released by the session.
     */
    public boolean isExclusiveConnectionRequired() {
        return false;
    }

    /**
     *  Stores the default Session wide reference mode that a UnitOfWork will use when referencing
     *  managed objects.
     *  @see org.eclipse.persistence.config.ReferenceMode
     */
    public ReferenceMode getDefaultReferenceMode() {
        return defaultReferenceMode;
    }

    /**
     *  Stores the default Session wide reference mode that a UnitOfWork will use when referencing
     *  managed objects.
     *  @see org.eclipse.persistence.config.ReferenceMode
     */
   public void setDefaultReferenceMode(ReferenceMode defaultReferenceMode) {
        this.defaultReferenceMode = defaultReferenceMode;
    }

   /**
    * This method will load the passed object or collection of objects using the passed AttributeGroup.
    * In case of collection all members should be either objects of the same mapped type
    * or have a common inheritance hierarchy mapped root class.
    * The AttributeGroup should correspond to the object type. 
    * 
    * @param objectOrCollection
    */
   public void load(Object objectOrCollection, AttributeGroup group) {
       if (objectOrCollection == null || group == null) {
           return;
       }       
       if (objectOrCollection instanceof Collection) {
           Iterator iterator = ((Collection)objectOrCollection).iterator();
           while (iterator.hasNext()) {
               load(iterator.next(), group);
           }
       } else {
           ClassDescriptor concreteDescriptor = getDescriptor(objectOrCollection);
           AttributeGroup concreteGroup = group.findGroup(concreteDescriptor);
           concreteDescriptor.getObjectBuilder().load(objectOrCollection, concreteGroup, this);
       }
   }

   public CacheKey retrieveCacheKey(Object primaryKey, ClassDescriptor concreteDescriptor, JoinedAttributeManager joinManager, ObjectBuildingQuery query){
       
       CacheKey cacheKey;
       //lock the object in the IM     
       // PERF: Only use deferred locking if required.
       // CR#3876308 If joining is used, deferred locks are still required.
       if (query.requiresDeferredLocks()) {
           cacheKey = this.getIdentityMapAccessorInstance().acquireDeferredLock(primaryKey, concreteDescriptor.getJavaClass(), concreteDescriptor, query.isCacheCheckComplete());

           if (cacheKey.getActiveThread() != Thread.currentThread()) {
               int counter = 0;
               while ((cacheKey.getObject() == null) && (counter < 1000)) {
                   //must release lock here to prevent acquiring multiple deferred locks but only
                   //releasing one at the end of the build object call.
                   //bug 5156075
                   cacheKey.releaseDeferredLock();
                   //sleep and try again if we are not the owner of the lock for CR 2317
                   // prevents us from modifying a cache key that another thread has locked.
                   try {
                       Thread.sleep(10);
                   } catch (InterruptedException exception) {
                   }
                   cacheKey = this.getIdentityMapAccessorInstance().acquireDeferredLock(primaryKey, concreteDescriptor.getJavaClass(), concreteDescriptor, query.isCacheCheckComplete());
                   if (cacheKey.getActiveThread() == Thread.currentThread()) {
                       break;
                   }
                   counter++;
               }
               if (counter == 1000) {
                   throw ConcurrencyException.maxTriesLockOnBuildObjectExceded(cacheKey.getActiveThread(), Thread.currentThread());
               }
           }
       } else {
           cacheKey = this.getIdentityMapAccessorInstance().acquireLock(primaryKey, concreteDescriptor.getJavaClass(), concreteDescriptor, query.isCacheCheckComplete());
       }
       return  cacheKey;
   }

   
   /**
    * PUBLIC:
    * Return the session's partitioning policy.
    */
   public PartitioningPolicy getPartitioningPolicy() {
       return partitioningPolicy;
   }
   
   /**
    * PUBLIC:
    * Set the session's partitioning policy.
    * A PartitioningPolicy is used to partition, load-balance or replicate data across multiple difference databases
    * or across a database cluster such as Oracle RAC.
    * Partitioning can provide improved scalability by allowing multiple database machines to service requests.
    */
   public void setPartitioningPolicy(PartitioningPolicy partitioningPolicy) {
       this.partitioningPolicy = partitioningPolicy;
   }
   
   /**
    * INTERNAL:
    * This currently only used by JPA with RCM to force a refresh of the metadata used within EntityManagerFactoryWrappers
    */
   public MetadataRefreshListener getRefreshMetadataListener() {
       return metadatalistener;
   }
   
   public void setRefreshMetadataListener(MetadataRefreshListener metadatalistener) {
       this.metadatalistener = metadatalistener;
   }
   
   /**
    * ADVANCED:
    * Return if the session enables concurrent processing.
    * Concurrent processing allow certain processing to be done on seperate threads.
    * This can result in improved performance.
    * This will use the session's server platform's thread pool.
    */
   public boolean isConcurrent() {
       return this.isConcurrent;
   }

   /**
    * ADVANCED:
    * Set if the session enables concurrent processing.
    * Concurrent processing allow certain processing to be done on seperate threads.
    * This can result in improved performance.
    * This will use the session's server platform's thread pool.
    */
   public void setIsConcurrent(boolean isConcurrent) {
       this.isConcurrent = isConcurrent;
   }
   
   /**
    * ADVANCED:
    * Set to indicate whether ObjectLevelReadQuery should by default use ResultSet Access optimization. 
    * If not set then parent's flag is used, is none set then ObjectLevelReadQuery.isResultSetAccessOptimizedQueryDefault is used.
    * If the optimization specified by the session is ignored if incompatible with other query settings. 
    */
   public void setShouldOptimizeResultSetAccess(boolean shouldOptimizeResultSetAccess) {
       this.shouldOptimizeResultSetAccess = shouldOptimizeResultSetAccess;
   }
   
   /**
    * ADVANCED:
    * Indicates whether ObjectLevelReadQuery should by default use ResultSet Access optimization. 
    * If not set then parent's flag is used, is none set then ObjectLevelReadQuery.isResultSetAccessOptimizedQueryDefault is used.
    * If the optimization specified by the session is ignored if incompatible with other query settings. 
    */
   public boolean shouldOptimizeResultSetAccess() {
       if (this.shouldOptimizeResultSetAccess != null) {
           return this.shouldOptimizeResultSetAccess.booleanValue();
       } else {
           if (getParent() != null) {
               return getParent().shouldOptimizeResultSetAccess();
           } else {
               return ObjectLevelReadQuery.isResultSetAccessOptimizedQueryDefault;
           }
       }
   }   
}
