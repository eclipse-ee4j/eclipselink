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
 *     stardif - ClientSession broker ServerSession and change propagation additions
 ******************************************************************************/  
package org.eclipse.persistence.sessions.broker;

import java.util.*;
import java.io.Writer;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.partitioning.PartitioningPolicy;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.history.*;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.sequencing.Sequence;
import org.eclipse.persistence.sessions.ExternalTransactionController;
import org.eclipse.persistence.sessions.SessionProfiler;
import org.eclipse.persistence.internal.databaseaccess.*;
import org.eclipse.persistence.internal.identitymaps.*;
import org.eclipse.persistence.sessions.server.*;
import org.eclipse.persistence.internal.sequencing.SequencingHome;
import org.eclipse.persistence.internal.sequencing.Sequencing;
import org.eclipse.persistence.internal.sequencing.SequencingFactory;
import org.eclipse.persistence.internal.sessions.*;

/**
 * <p>
 * <b>Purpose</b>: Provide a single view to a TopLink session that transparently accesses multple databases.
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li> Broker queries to the appropriate child sessions.
 * <li> Use a simplified two-stage commit policy on commit of transaction.
 * <li> Support brokered units of work.
 * </ul>
 */
public class SessionBroker extends DatabaseSessionImpl {
    protected SessionBroker parent;
    protected Map<Class, String> sessionNamesByClass;
    protected Map<String, AbstractSession> sessionsByName;
    protected Sequencing sequencing;
    protected boolean shouldUseDescriptorAliases;

    /**
     * PUBLIC:
     * Create and return a session broker.
     * Because a session broker has multiple sessions it does not used a login.
     */
    public SessionBroker() {
        super(new org.eclipse.persistence.sessions.DatabaseLogin());

        this.sessionsByName = new HashMap<String, AbstractSession>();
        this.sessionNamesByClass = new HashMap<Class, String>();
    }

    /**
     * INTERNAL:
     * Create and return a session broker.
     * Used internally to set the Names by Class from the parent.  Reduces garbage.
     */
    protected SessionBroker(Map sessionNames) {
        super(new org.eclipse.persistence.sessions.DatabaseLogin());

        this.sessionsByName = new HashMap<String, AbstractSession>();
        this.sessionNamesByClass = sessionNames;
    }

    /**
     * PUBLIC:
     * Return a session broker that behaves as a client session broker.  An
     * acquire session broker is done under the covers on each session inside
     * the session broker, and a new broker is returned.
     *
     * NOTE: when finished with the client broker, it should be released.
     */
    public SessionBroker acquireClientSessionBroker() {
        return acquireClientSessionBroker(null, null);
    }
    
    /**
     * PUBLIC:
     * Return a session broker that behaves as a client session broker.  An
     * acquire session broker is done under the covers on each session inside
     * the session broker, and a new broker is returned.
     *
     * NOTE: when finished with the client broker, it should be released.
     * @param connectionPolicies maps session name to connectionPolicy to be used for this session;
     * @param mapOfProperties maps session name to properties to be used for this session. 
     */
    public SessionBroker acquireClientSessionBroker(Map<String, ConnectionPolicy> connectionPolicies, Map mapOfProperties) {
        log(SessionLog.FINER, SessionLog.CONNECTION, "acquire_client_session_broker");
        SessionBroker clientBroker = copySessionBroker();
        clientBroker.parent = this;
        clientBroker.getIdentityMapAccessorInstance().setIdentityMapManager(getIdentityMapAccessorInstance().getIdentityMapManager());
        clientBroker.commitManager = getCommitManager();
        clientBroker.commandManager = getCommandManager();
        clientBroker.externalTransactionController = getExternalTransactionController();
        clientBroker.setServerPlatform(getServerPlatform());
        String sessionName;
        AbstractSession session;
        Iterator names = this.getSessionsByName().keySet().iterator();
        while (names.hasNext()) {
            sessionName = (String)names.next();
            session = getSessionForName(sessionName);
            if (session.isServerSession()) {
                ServerSession serverSession = (ServerSession)session;
                if (serverSession.getProject().hasIsolatedCacheClassWithoutUOWIsolation()) {
                    throw ValidationException.isolatedDataNotSupportedInSessionBroker(sessionName);
                }
                ConnectionPolicy connectionPolicy = null;
                if (connectionPolicies != null) {
                    connectionPolicy = connectionPolicies.get(sessionName);
                }
                if (connectionPolicy == null) {
                    connectionPolicy = serverSession.getDefaultConnectionPolicy();
                }
                Map properties = null;
                if (mapOfProperties != null) {
                    properties = (Map)mapOfProperties.get(sessionName);
                }
                clientBroker.registerSession(sessionName, serverSession.acquireClientSession(connectionPolicy, properties));
            } else {
                throw ValidationException.cannotAcquireClientSessionFromSession();
            }
        }

        clientBroker.initializeSequencing();
        return clientBroker;
    }

    /**
     * INTERNAL:
     * Acquires a special historical session for reading objects as of a past time.
     */
    public org.eclipse.persistence.sessions.Session acquireHistoricalSession(AsOfClause clause) throws ValidationException {
        if (isServerSessionBroker()) {
            throw ValidationException.cannotAcquireHistoricalSession();
        }

        // ? logMessage("acquire_client_session_broker", (Object[])null);
        SessionBroker historicalBroker = copySessionBroker();
        historicalBroker.parent = this;
        String sessionName;
        AbstractSession session;
        Iterator names = this.getSessionsByName().keySet().iterator();
        while (names.hasNext()) {
            sessionName = (String)names.next();
            session = getSessionForName(sessionName);
            historicalBroker.registerSession(sessionName, (AbstractSession)session.acquireHistoricalSession(clause));
        }
        return historicalBroker;
    }

    /**
     * INTERNAL:
     * Called in the end of beforeCompletion of external transaction sychronization listener.
     * Close the managed sql connection corresponding to the external transaction,
     * if applicable releases accessor.
     */
    public void releaseJTSConnection() {
        RuntimeException exception = null;
        for (Iterator sessionEnum = getSessionsByName().values().iterator();
                 sessionEnum.hasNext();) {
            AbstractSession session = (AbstractSession)sessionEnum.next();
            try {
                session.releaseJTSConnection();
            } catch (RuntimeException ex) {
                if (exception == null) {
                    exception = ex;
                }
            }
        }
        if (exception != null) {
            throw exception;
        }
    }

    /**
     * PUBLIC:
     * Return a unit of work for this session broker.
     * Acquire a client session broker if is a server session broker.
     *
     * @see UnitOfWorkImpl
     */
    public UnitOfWorkImpl acquireUnitOfWork() {
        if (isServerSessionBroker()) {
            return acquireClientSessionBroker().acquireUnitOfWork();
        } else {
            return super.acquireUnitOfWork();
        }
    }

    /**
     * PUBLIC:
     * You cannot add a descriptor to a session broker, you must add it to its session.
     */
    public void addDescriptor(ClassDescriptor descriptor) {
        throw ValidationException.cannotAddDescriptorsToSessionBroker();
    }

    /**
     * PUBLIC:
     * You cannot add descriptors to a session broker, you must add them to its session.
     */
    public void addDescriptors(Vector descriptors) throws ValidationException {
        throw ValidationException.cannotAddDescriptorsToSessionBroker();
    }

    /**
     * PUBLIC:
     * You cannot add a project to a session broker, you must add it to its session.
     */
    public void addDescriptors(org.eclipse.persistence.sessions.Project project) throws ValidationException {
        throw ValidationException.cannotAddDescriptorsToSessionBroker();
    }

    /**
     * PUBLIC:
     * You cannot add a sequence to a session broker, you must add it to its session.
     */
    public void addSequence(Sequence sequence) {
        throw ValidationException.cannotAddSequencesToSessionBroker();
    }

    /**
     * INTERNAL:
     * Begin the transaction on all child sessions.
     */
    protected void basicBeginTransaction() throws DatabaseException {
        for (Iterator sessionEnum = getSessionsByName().values().iterator();
                 sessionEnum.hasNext();) {
            AbstractSession session = (AbstractSession)sessionEnum.next();
            session.beginTransaction();
        }
    }

    /**
     * INTERNAL:
     * Commit the transaction on all child sessions.
     * This assumes that the commit of the transaction will not fail because all of the
     * modification has already taken place and no errors would have occurred.
     */
    protected void basicCommitTransaction() throws DatabaseException {
        // Do one last check it make sure that all sessions are still connected.
        for (Iterator sessionEnum = getSessionsByName().values().iterator();
                 sessionEnum.hasNext();) {
            AbstractSession session = (AbstractSession)sessionEnum.next();
            if (!session.isConnected()) {
                throw DatabaseException.databaseAccessorNotConnected();
            }
        }
        for (Iterator sessionEnum = getSessionsByName().values().iterator();
                 sessionEnum.hasNext();) {
            AbstractSession session = (AbstractSession)sessionEnum.next();
            session.commitTransaction();
        }
    }

    /**
     * INTERNAL:
     * Rollback the transaction on all child sessions.
     */
    protected void basicRollbackTransaction() throws DatabaseException {
        DatabaseException globalException = null;
        for (Iterator sessionEnum = getSessionsByName().values().iterator();
                 sessionEnum.hasNext();) {
            AbstractSession session = (AbstractSession)sessionEnum.next();
            try {
                session.rollbackTransaction();
            } catch (DatabaseException exception) {
                globalException = exception;
            }
        }
        if (globalException != null) {
            throw globalException;
        }
    }
    
    /**
     * PUBLIC:
     * Return true if the pre-defined query is defined on the session.
     */
    public boolean containsQuery(String queryName) {
        boolean containsQuery = getQueries().containsKey(queryName);
        if (isClientSessionBroker() && (containsQuery == false)) {
            String sessionName = null;
            AbstractSession ssession = null;

            Iterator names = this.getSessionsByName().keySet().iterator();
            while (names.hasNext()) {
                sessionName = (String)names.next();
                ssession = getSessionForName(sessionName);
                if (ssession instanceof org.eclipse.persistence.sessions.server.ClientSession) {
                    if (((ClientSession)ssession).getParent().getBroker().containsQuery(queryName)) {
                        return true;
                    }
                }
            }
        }

        return containsQuery;

    }

    /**
     * INTERNAL:
     * Return a copy (not using clone) of a session broker.
     */
    protected SessionBroker copySessionBroker() {
        SessionBroker broker = new SessionBroker(getSessionNamesByClass());

        broker.accessors = getAccessors();
        broker.name = getName();
        broker.isLoggingOff = isLoggingOff();
        broker.sessionLog = getSessionLog();
        broker.profiler = getProfiler();
        broker.isInProfile = isInProfile();
        broker.project = project;
        if (hasEventManager()) {
            broker.eventManager = getEventManager().clone(broker);
        }
        broker.exceptionHandler = getExceptionHandler();
        broker.descriptors = getDescriptors();
        broker.shouldPropagateChanges = shouldPropagateChanges;
	
        return broker;
    }

    /**
     * INTERNAL:
     * Return the low-level database accessors.
     * The database accessor is used for direct database access.
     * The right accessor for this broker will be returned.
     */
    public Collection<Accessor> getAccessors(Call call, AbstractRecord translationRow, DatabaseQuery query) {
        if (query.getSessionName() != null) {
            return getSessionForName(query.getSessionName()).getAccessors(call, translationRow, query);
        }
        if (query.getReferenceClass() == null) {
            // CR#... this occurs if a data query is used without a session-name, needs to throw an error
            throw QueryException.unnamedQueryOnSessionBroker(null);
        }

        //session may be a broker too.  Eventually a non session broker will be found
        return getSessionForClass(query.getReferenceClass()).getAccessors(call, translationRow, query);
    }

    /**
     * ADVANCED:
     * Answers the past time this session is as of.  Only meaningfull
     * for special historical sessions.
     * @return An immutable object representation of the past time.
     * <code>null</code> if no clause set, or this a regular session.
     * @see org.eclipse.persistence.expressions.AsOfClause
     * @see #acquireSessionAsOf(java.lang.Number)
     * @see #acquireSessionAsOf(java.util.Date)
     * @see #hasAsOfClause
     */
    public AsOfClause getAsOfClause() {
        for (Iterator enumtr = getSessionsByName().values().iterator(); enumtr.hasNext();) {
            return ((AbstractSession)enumtr.next()).getAsOfClause();
        }
        return null;
    }    
    
    /**
     * INTERNAL:
     * Gets the parent SessionBroker.
     */
    public SessionBroker getParent() {
        return parent;
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
        AbstractSession sessionByQuery = getSessionForQuery(query);

        // Always forward to a registered session.
        return sessionByQuery.getExecutionSession(query);
    }

    /**
     * INTERNAL:
     * Return the platform for a particular class.
     */
    public Platform getPlatform(Class domainClass) {
        if (domainClass == null) {
            return super.getDatasourcePlatform();
        }
        //else
        return getSessionForClass(domainClass).getDatasourcePlatform();
    }

    /**
     * PUBLIC:
     * Return the query from the session pre-defined queries with the given name and argument types.
     * This allows for common queries to be pre-defined, reused and executed by name.
     * This method should be used if the Session has multiple queries with the same name but
     * different arguments.
     * 
     * The search order is: 
     *    for ClientSessionBroker:
     *      the broker; 
     *      it's member ClientSessions (but not their parent ServerSessions);
     *      the parent SessionBroker.
     *      
     *    for ServerSession or DatabaseSession SessionBroker:
     *      the broker;
     *      it's member ServerSessions (or DatabaseSessions). 
     */
    //Bug#3551263  Override getQuery(String name, Vector arguments) in Session search through 
    //the server session broker as well
    public DatabaseQuery getQuery(String name, Vector arguments, boolean shouldSearchParent) {
        // First search the broker
        DatabaseQuery query = super.getQuery(name, arguments, shouldSearchParent);
        if(query != null) {
            return query;
        }
        Iterator<AbstractSession> it = this.sessionsByName.values().iterator();
        while(it.hasNext()) {
            // Note that ClientSession's parent ServerSessions should not be searched at this time
            query = it.next().getQuery(name, arguments, false);
            if(query != null) {
                return query;
            }
        }
        if(shouldSearchParent) {
            AbstractSession parent = getParent();
            if(parent != null) {
                // Search SessionBroker and it's member ServerSessions
                return parent.getQuery(name, arguments, true);
            }
        }
        return null;
    }

    /**
     * INTERNAL:
     * Return the session to be used for the class.
     */
    public AbstractSession getSessionForClass(Class domainClass) throws ValidationException {
        if (domainClass == null) {
            // CR2114; we don't have a session name. Return us.
            return this;
        }
        String sessionName = getSessionNamesByClass().get(domainClass);
        if (sessionName == null) {
            throw ValidationException.noSessionRegisteredForClass(domainClass);
        }
        return getSessionsByName().get(sessionName);
    }

    /**
     * INTERNAL:
     * Return the session by name.
     */
    public AbstractSession getSessionForName(String name) throws ValidationException {
        AbstractSession sessionByName = getSessionsByName().get(name);
        if (sessionByName == null) {
            throw ValidationException.noSessionRegisteredForName(name);
        }

        return sessionByName;
    }

    /**
     * INTERNAL:
     * Answers the session to be used for the given query.
     */
    protected AbstractSession getSessionForQuery(DatabaseQuery query) {
        if (query.hasSessionName()) {
            return getSessionForName(query.getSessionName());
        }

        Class queryClass;
        if (query.getDescriptor() != null) {
            queryClass = query.getDescriptor().getJavaClass();
        } else {
            queryClass = query.getReferenceClass();
            if (queryClass == null) {
                throw QueryException.unnamedQueryOnSessionBroker(query);
            }
        }
        return getSessionForClass(queryClass);
    }

    /**
     * INTERNAL:
     * Return sessions indexed by class, each class can only have one default session.
     */
    protected Map<Class, String> getSessionNamesByClass() {
        return sessionNamesByClass;
    }

    /**
     * INTERNAL:
     * Return sessions indexed by name.
     */
    public Map<String, AbstractSession> getSessionsByName() {
        return sessionsByName;
    }

    /**
     * INTERNAL:
     * Allow each descriptor to initialize any dependencies on this session.
     * This is done in two passes to allow the inheritance to be resolved first.
     * Normally the descriptors are added before login, then initialized on login.
     * Should not be called on client SessoionBroker
     */
    public void initializeDescriptors() {
        // ClientSession initializes sequencing during construction,
        // however DatabaseSession and ServerSession normally call initializeSequencing()
        // in initializeDescriptors method. 
        // Because initializeDescriptors() is not called for a session-member of a SessionBroker,
        // initializeSequencing() for the sessions should be called here.
        for (Iterator enumtr = getSessionsByName().values().iterator(); enumtr.hasNext();) {
            DatabaseSessionImpl databaseSession = (DatabaseSessionImpl)enumtr.next();                
            String sessionName = databaseSession.getName();

            // Initialize partitioning policies.
            for (PartitioningPolicy policy : databaseSession.getProject().getPartitioningPolicies().values()) {
                policy.initialize(this);
            }
            
            // Copy jpa queries from member sessions into SessionBroker before descriptors' initialization.
            if (!databaseSession.isJPAQueriesProcessed()) {
                for(DatabaseQuery query: databaseSession.getJPAQueries()) {
                    query.setSessionName(sessionName);
                    getJPAQueries().add(query);
                }
                //prevent them from being re-added in the case of a logout/login
                databaseSession.setJPAQueriesProcessed(true);
            }

            // assign session name to each query
            Iterator<List<DatabaseQuery>> it = databaseSession.getQueries().values().iterator();
            while(it.hasNext()) {
                List<DatabaseQuery> queryList = it.next();
                for(int i=0; i < queryList.size(); i++) {
                    queryList.get(i).setSessionName(sessionName);
                }
            }
            
            databaseSession.initializeSequencing();
        }
        if(hasExternalTransactionController()) {
            getExternalTransactionController().initializeSequencingListeners();
        }

        super.initializeDescriptors();
        // Must reset project options to session broker project, as initialization occurs
        // with local projects.
        for (Iterator enumtr = getSessionsByName().values().iterator(); enumtr.hasNext();) {
            DatabaseSessionImpl databaseSession = (DatabaseSessionImpl)enumtr.next();
            if (databaseSession.getProject().hasGenericHistorySupport()) {
                getProject().setHasGenericHistorySupport(true);
            }
            if (databaseSession.getProject().hasIsolatedClasses()) {
                getProject().setHasIsolatedClasses(true);
            }
            if (databaseSession.getProject().hasMappingsPostCalculateChangesOnDeleted()) {
                getProject().setHasMappingsPostCalculateChangesOnDeleted(true);
            }
            if (databaseSession.getProject().hasNonIsolatedUOWClasses()) {
                getProject().setHasNonIsolatedUOWClasses(true);
            }
            if (databaseSession.getProject().hasProxyIndirection()) {
                getProject().setHasProxyIndirection(true);
            }
            getProject().getDefaultReadOnlyClasses().addAll(databaseSession.getProject().getDefaultReadOnlyClasses());
        }
        
        // ServerSessionBroker doesn't need sequencing.
        // Sequencing should be obtained either from the ClientSessionBroker or directly
        // from the ClientSession.
        if (isServerSessionBroker()) {
            sequencing = null;
        }
    }

    /**
     * INTERNAL:
     * Set up the IdentityMapManager.  This method allows subclasses of Session to override
     * the default IdentityMapManager functionality.
     */
    public void initializeIdentityMapAccessor() {
        this.identityMapAccessor = new SessionBrokerIdentityMapAccessor(this, new IdentityMapManager(this));
    }

    /**
     * INTERNAL:
     * Return the results from exeucting the database query.
     * the arguments should be a database row with raw data values.
     * Find the correct child session to broker the query to,
     * and return the result of the session executing the query.
     */
    public Object internalExecuteQuery(DatabaseQuery query, AbstractRecord row) throws DatabaseException, QueryException {
        AbstractSession sessionByQuery = getSessionForQuery(query);

        // Note, this disables local profilers.
        return sessionByQuery.internalExecuteQuery(query, row);
    }

    /**
     * INTERNAL:
     * Returns true if the session is a session Broker.
     */
    public boolean isBroker() {
        return true;
    }

    /**
     * PUBLIC:
     * Return if this session is a client session broker.
     */
    public boolean isClientSessionBroker() {
        for (Iterator sessionEnum = getSessionsByName().values().iterator();
                 sessionEnum.hasNext();) {
            AbstractSession session = (AbstractSession)sessionEnum.next();
            if (session.isClientSession()) {
                return true;
            }
        }

        return false;
    }

    /**
     * PUBLIC:
     * Return if all sessions are still connected to the database.
     */
    public boolean isConnected() {
        if ((getSessionsByName() == null) || (getSessionsByName().isEmpty())) {
            return false;
        }

        for (Iterator sessionEnum = getSessionsByName().values().iterator();
                 sessionEnum.hasNext();) {
            AbstractSession session = (AbstractSession)sessionEnum.next();
            if (!session.isConnected()) {
                return false;
            }
        }

        return true;
    }

    /**
     * PUBLIC:
     * Return if this session is a server session broker.
     */
    public boolean isServerSessionBroker() {
        for (Iterator sessionEnum = getSessionsByName().values().iterator();
                 sessionEnum.hasNext();) {
            AbstractSession session = (AbstractSession)sessionEnum.next();
            if (session.isServerSession()) {
                return true;
            }
        }

        return false;
    }

    /**
     * INTERNAL:
     * Return if this session is a session broker.
     */
    public boolean isSessionBroker() {
        return true;
    }

    /**
     * PUBLIC:
     * Connect to the database using the predefined login.
     * This connects all of the child sessions and expects that they are in a valid state to be connected.
     */
    public void login() throws DatabaseException {
        preConnectDatasource();
        // Connection all sessions and initialize
        for (Iterator sessionEnum = getSessionsByName().values().iterator();
                 sessionEnum.hasNext();) {
            DatabaseSessionImpl session = (DatabaseSessionImpl)sessionEnum.next();
            if (!session.isConnected()) {
                session.login();
            }
        }
        postConnectDatasource();
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
        // Connection all sessions and initialize
        for (Iterator sessionEnum = getSessionsByName().values().iterator();
                 sessionEnum.hasNext();) {
            DatabaseSessionImpl session = (DatabaseSessionImpl)sessionEnum.next();
            if (!session.isConnected()) {
                if(session.getDatasourcePlatform().getClass().getName().equals("org.eclipse.persistence.platform.database.DatabasePlatform")) {
                    session.loginAndDetectDatasource();
                } else {
                    session.login();
                }
            }
        }
        postConnectDatasource();
    }

    /**
     * PUBLIC:
     * Connect to the database using the predefined login.
     * This connects all of the child sessions and expects that they are in a valid state to be connected.
     */
    public void login(String userName, String password) throws DatabaseException {
        preConnectDatasource();
        // Connection all sessions and initialize
        for (Iterator sessionEnum = getSessionsByName().values().iterator();
                 sessionEnum.hasNext();) {
            DatabaseSessionImpl session = (DatabaseSessionImpl)sessionEnum.next();
            session.getDatasourceLogin().setUserName(userName);
            session.getDatasourceLogin().setPassword(password);
            if (!session.isConnected()) {
                session.login();
            }
        }
        postConnectDatasource();
    }

    /**
     * PUBLIC:
     * Disconnect from all databases.
     *
     * @exception EclipseLinkException if a transaction is active, you must rollback any active transaction before logout.
     * @exception DatabaseException the database will also raise an error if their is an active transaction,
     * or a general error occurs.
     */
    public void logout() throws DatabaseException {
        if(!isLoggedIn) {
            return;
        }
        if (this.eventManager != null) {
            this.eventManager.preLogout(this);
        }
        if (!isClientSessionBroker()) {
            for (Iterator sessionEnum = getSessionsByName().values().iterator();
                     sessionEnum.hasNext();) {
                DatabaseSessionImpl session = (DatabaseSessionImpl)sessionEnum.next();
                session.logout();
            }
        }
        this.sequencing = null;
        this.isLoggedIn = false;
        if (this.eventManager != null) {
            this.eventManager.postLogout(this);
        }
    }

    /**
     * INTERNAL:
     * Rise postLogin events for member sessions and the SessionBroker.
     */
    public void postLogin() {
        for (Iterator sessionEnum = getSessionsByName().values().iterator();
            sessionEnum.hasNext();) {
           DatabaseSessionImpl session = (DatabaseSessionImpl)sessionEnum.next();
           session.postLogin();
        }
        super.postLogin();
    }
    
    /**
     * PUBLIC:
     * Register the session under its name.
     * All of the session's descriptors must have already been registered.
     * DatabaseSession/ServerSession should not be connected and descriptors should not be initialized.
     */
    public void registerSession(String name, AbstractSession session) {
        session.setIsInBroker(true);
        getSessionsByName().put(name, session);
        session.setBroker(this);
        session.setName(name);
        if (session.isDatabaseSession()) {
            if (hasEventManager()) {
                // add broker's listeners to member sessions.
                // each time a listener added to broker it is added to member sessions, too
                // (see SessionEventManager#addListener) therefore at all times
                // each member session has all broker's listeners.
                session.getEventManager().getListeners().addAll(getEventManager().getListeners());
            }
    
            // The keys/classes must also be used as some descriptors may be under multiple classes.
            Iterator descriptors = session.getDescriptors().values().iterator();
            Iterator classes = session.getDescriptors().keySet().iterator();
            while (descriptors.hasNext()) {
                ClassDescriptor descriptor = (ClassDescriptor)descriptors.next();
                Class descriptorClass = (Class)classes.next();
                getSessionNamesByClass().put(descriptorClass, name);
                if(this.shouldUseDescriptorAliases) {
                    String alias = descriptor.getAlias();
                    if(alias != null && alias.length() > 0) {
                        ClassDescriptor anotherDescriptor = getDescriptorForAlias(alias);
                        if(anotherDescriptor != null) {
                            if(anotherDescriptor.getJavaClass() != descriptor.getJavaClass()) {
                                throw ValidationException.sharedDescriptorAlias(alias, descriptor.getJavaClass().getName(), anotherDescriptor.getJavaClass().getName());
                            }
                        }
                        addAlias(alias, descriptor);
                    }
                }
                getDescriptors().put(descriptorClass, descriptor);
            }
        }
    }

    /**
     * PUBLIC:
     * Register the session under its name.
     * All of the session's descriptors must have already been registered.
     * DatabaseSession/ServerSession should not be connected and descriptors should not be initialized.
     */
    public void registerSession(String name, org.eclipse.persistence.sessions.Session session) {
        registerSession(name, (AbstractSession)session);
    }

    /**
     * PUBLIC:
     * Release the session.
     * This does nothing by default, but allows for other sessions such as the ClientSession to do something.
     */
    public void release() {
        if (isClientSessionBroker()) {
            log(SessionLog.FINER, SessionLog.CONNECTION, "releasing_client_session_broker");
        }
        RuntimeException exception = null;
        AbstractSession session;
        for (Iterator enumtr = getSessionsByName().values().iterator(); enumtr.hasNext();) {
            session = (AbstractSession)enumtr.next();
            try {
                session.release();
            } catch (RuntimeException ex) {
                if (exception == null) {
                    exception = ex;
                }
            }
        }
        super.release();
        if (exception != null) {
            throw exception;
        }
    }

    /**
     * INTERNAL:
     * A query execution failed due to an invalid query.
     * Re-connect and retry the query.
     */
    @Override
    public Object retryQuery(DatabaseQuery query, AbstractRecord row, DatabaseException databaseException, int retryCount, AbstractSession executionSession) {
        // If not in a transaction and has a write connection, must release it if invalid.
        if (isClientSessionBroker()) {
            for (Iterator it = getSessionsByName().values().iterator(); it.hasNext();) {
                ClientSession clientSession = (ClientSession)it.next();
                clientSession.getParent().releaseInvalidClientSession(clientSession);
            }
        }
        return super.retryQuery(query, row, databaseException, retryCount, executionSession);
    }

    /**
     * INTERNAL:
     * Used for JTS integration internally by ServerPlatform.
     */
    public void setExternalTransactionController(ExternalTransactionController externalTransactionController) {
        super.setExternalTransactionController(externalTransactionController);
        for (AbstractSession session : getSessionsByName().values()) {
            DatabaseSessionImpl dbSession = (DatabaseSessionImpl)session;
            dbSession.setExternalTransactionController(externalTransactionController);
        }
    }

    /**
     * PUBLIC:
     * set the integrityChecker. IntegrityChecker holds all the ClassDescriptor Exceptions.
     */
    public void setIntegrityChecker(IntegrityChecker integrityChecker) {
        super.setIntegrityChecker(integrityChecker);

        for (Iterator sessionEnum = getSessionsByName().values().iterator();
                 sessionEnum.hasNext();) {
            AbstractSession session = (AbstractSession)sessionEnum.next();
            session.setIntegrityChecker(integrityChecker);
        }
    }

    /**
     * PUBLIC:
     * Set the session log.
     *
     * @see #logMessages()
     */
    public void setSessionLog(SessionLog log) {
        super.setSessionLog(log);

        for (Iterator sessionEnum = getSessionsByName().values().iterator();
                 sessionEnum.hasNext();) {
            AbstractSession session = (AbstractSession)sessionEnum.next();
            session.setSessionLog(log);
        }
    }

    /**
     * PUBLIC:
     * Set the message log.
     *
     * @see #logMessages()
     */
    public void setLog(Writer log) {
        super.setLog(log);

        for (Iterator sessionEnum = getSessionsByName().values().iterator();
                 sessionEnum.hasNext();) {
            AbstractSession session = (AbstractSession)sessionEnum.next();
            session.setLog(log);
        }
    }

    /**
     * PUBLIC:
     * Set the profiler for the session.
     * This allows for performance operations to be profiled.
     */
    public void setProfiler(SessionProfiler profiler) {
        super.setProfiler(profiler);

        for (Iterator sessionEnum = getSessionsByName().values().iterator();
                 sessionEnum.hasNext();) {
            AbstractSession session = (AbstractSession)sessionEnum.next();
            session.setProfiler(profiler);
        }
    }

    /**
     * INTERNAL:
     * Set sessions indexed by class, each class can only have one default session.
     */
    protected void setSessionNameByClass(HashMap sessionNameByClass) {
        this.sessionNamesByClass = sessionNameByClass;
    }

    /**
     * INTERNAL:
     * Set sessions indexed by name.
     */
    public void setSessionsByName(Map sessionsByName) {
        this.sessionsByName = sessionsByName;
    }

    /**
     * INTERNAL:
     * Set isSynchronized flag to indicate that members of session broker are synchronized.
     * This method should only be called by setSynchronized method of UnitOfWork
     * obtained from either DatabaseSession Broker or ClientSession Broker.
     */
    public void setSynchronized(boolean synched) {
        if(!isServerSessionBroker()) {
            super.setSynchronized(synched);
            Iterator itSessions = getSessionsByName().values().iterator();
            while(itSessions.hasNext()) {
                ((AbstractSession)itSessions.next()).setSynchronized(synched);
            }
        }
    }

    /**
     * INTERNAL:
     * This method notifies the accessor that a particular sets of writes has
     * completed.  This notification can be used for such thing as flushing the
     * batch mechanism
     */
    public void writesCompleted() {
        for (Iterator sessionEnum = getSessionsByName().values().iterator();
                 sessionEnum.hasNext();) {
            AbstractSession session = (AbstractSession)sessionEnum.next();
            if (!session.isConnected()) {
                throw DatabaseException.databaseAccessorNotConnected();
            }
        }
        for (Iterator sessionEnum = getSessionsByName().values().iterator();
                 sessionEnum.hasNext();) {
            AbstractSession session = (AbstractSession)sessionEnum.next();
            session.writesCompleted();
        }
    }

    /**
     * ADVANCED:
     * Creates sequencing object for the session broker.
     * Typically there is no need for the user to call this method -
     * it is called by login() and acquireClientSessionBroker.
     */
    public void initializeSequencing() {
        sequencing = SequencingFactory.createSequencing(this);
    }

    /**
     * PROTECTED:
     * Session broker doesn't have SequencingHome.
     */
    protected SequencingHome getSequencingHome() {
        return null;
    }

    /**
     * PUBLIC:
     * Return the Sequencing object used by the session.
     */
    public Sequencing getSequencing() {
        return sequencing;
    }

    /**
     * INTERNAL:
     * Returns a number of member sessions that require sequencing callback.
     * Always returns 0 if sequencing is not connected.
     */
    public int howManySequencingCallbacks() {
        if(this.isClientSessionBroker()) {
            return getParent().howManySequencingCallbacks();
        } else {
            int nCallbacks = 0;
            Iterator itSessions = getSessionsByName().values().iterator();
            while(itSessions.hasNext()) {
                if(((DatabaseSessionImpl)itSessions.next()).isSequencingCallbackRequired()) {
                    nCallbacks++;
                }
            }
            return nCallbacks;
        }
    }

    /**
     * INTERNAL:
     * Indicates whether SequencingCallback is required.
     * Always returns false if sequencing is not connected.
     */
    public boolean isSequencingCallbackRequired() {
        return howManySequencingCallbacks() > 0;
    }
    
    /**
     * PUBLIC:
     * Indicates whether descriptors should use aliasDescriptors map.
     * If aliasDescriptors is used then descriptors' aliases should be unique.
     */
    public boolean shouldUseDescriptorAliases() {
        return this.shouldUseDescriptorAliases;
    }

    /**
     * PUBLIC:
     * Indicates whether descriptors should use aliasDescriptors map.
     * If aliasDescriptors is used then descriptors' aliases should be unique.
     */
    public void setShouldUseDescriptorAliases(boolean shouldUseDescriptorAliases) {
        this.shouldUseDescriptorAliases = shouldUseDescriptorAliases;
    }
}
