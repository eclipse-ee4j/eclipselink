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
 *     stardif - ClientSession broker ServerSession and change propagation additions
 ******************************************************************************/  
package org.eclipse.persistence.sessions.broker;

import java.util.*;
import java.io.Writer;
import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.history.*;
import org.eclipse.persistence.logging.SessionLog;
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
     * NOTE: when finished with the client broker, it should be releases.  See
     * releaseClientSessionBroker.
     */
    public SessionBroker acquireClientSessionBroker() {
        log(SessionLog.FINER, SessionLog.CONNECTION, "acquire_client_session_broker");
        SessionBroker clientBroker = copySessionBroker();
        clientBroker.parent = this;
        clientBroker.getIdentityMapAccessorInstance().setIdentityMapManager(getIdentityMapAccessorInstance().getIdentityMapManager());
        clientBroker.commitManager = getCommitManager();
        clientBroker.commandManager = getCommandManager();
        clientBroker.externalTransactionController = getExternalTransactionController();
        clientBroker.setServerPlatform(getServerPlatform());
        String sessionName;
        AbstractSession serverSession;
        Iterator names = this.getSessionsByName().keySet().iterator();
        while (names.hasNext()) {
            sessionName = (String)names.next();
            serverSession = getSessionForName(sessionName);
            if (serverSession instanceof org.eclipse.persistence.sessions.server.ServerSession) {
                if (serverSession.getProject().hasIsolatedClasses()) {
                    throw ValidationException.isolatedDataNotSupportedInSessionBroker(sessionName);
                }
                clientBroker.internalRegisterSession(sessionName, ((org.eclipse.persistence.sessions.server.ServerSession)serverSession).acquireClientSession());
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
        String sessionName;
        AbstractSession session;
        Iterator names = this.getSessionsByName().keySet().iterator();
        while (names.hasNext()) {
            sessionName = (String)names.next();
            session = getSessionForName(sessionName);
            historicalBroker.registerSession(sessionName, session.acquireHistoricalSession(clause));
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
        for (Iterator sessionEnum = getSessionsByName().values().iterator();
                 sessionEnum.hasNext();) {
            AbstractSession session = (AbstractSession)sessionEnum.next();
            session.releaseJTSConnection();
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

        broker.accessor = getAccessor();
        broker.name = getName();
        broker.sessionLog = getSessionLog();
        broker.project = project;
        if (hasEventManager()) {
            broker.eventManager = getEventManager().clone(broker);
        }
        broker.shouldPropagateChanges = shouldPropagateChanges;
	
        return broker;
    }

    /**
     * INTERNAL:
     * Return the lowlevel database accessor.
     * The database accessor is used for direct database access.
     * The right accessor for this
     * broker will be returned.
     */
    public Accessor getAccessor(Class domainClass) {
        if (domainClass == null) {
            // CR#... this occurs if a data query is used without a session-name, needs to throw an error
            throw QueryException.unnamedQueryOnSessionBroker(null);
        }

        //session may be a broker too.  Eventually a non session broker wil be found
        return getSessionForClass(domainClass).getAccessor(domainClass);
    }

    /**
     * INTERNAL:
     * Return the lowlevel database accessor.
     * The database accesor is used for direct database access.
     * The right accessor for this
     * broker will be returned.
     */
    public Accessor getAccessor(String sessionName) {
        //session may be a broker too.  Eventually a non session broker wil be found
        return getSessionForName(sessionName).getAccessor(sessionName);
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
        if (query == null) {
            return this;
        }
        return getSessionForQuery(query).getParentIdentityMapSession(query, canReturnSelf, terminalOnly);
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
     * Return the query from the session pre-defined queries with the given name.
     * This allows for common queries to be pre-defined, reused and executed by name.
     */
    public DatabaseQuery getQuery(String name) {
        //Bug#3473441 Should always call super first to ensure any change in Session will be reflected here.
        DatabaseQuery query = super.getQuery(name);
        if (isClientSessionBroker() && (query == null)) {
            String sessionName = null;
            AbstractSession ssession = null;
            Iterator names = this.getSessionsByName().keySet().iterator();
            while (names.hasNext()) {
                sessionName = (String)names.next();
                ssession = getSessionForName(sessionName);
                if (ssession instanceof org.eclipse.persistence.sessions.server.ClientSession) {
                    query = ((ClientSession)ssession).getParent().getBroker().getQuery(name);
                    if (query != null) {
                        return query;
                    }
                }
            }
        }

        return query;
    }

    /**
     * PUBLIC:
     * Return the query from the session pre-defined queries with the given name and argument types.
     * This allows for common queries to be pre-defined, reused and executed by name.
     * This method should be used if the Session has multiple queries with the same name but
     * different arguments.
     */

    //Bug#3551263  Override getQuery(String name, Vector arguments) in Session search through 
    //the server session broker as well
    public DatabaseQuery getQuery(String name, Vector arguments) {
        DatabaseQuery query = super.getQuery(name, arguments);
        if (isClientSessionBroker() && (query == null)) {
            String sessionName = null;
            AbstractSession ssession = null;
            Iterator names = this.getSessionsByName().keySet().iterator();
            while (names.hasNext()) {
                sessionName = (String)names.next();
                ssession = getSessionForName(sessionName);
                if (ssession instanceof org.eclipse.persistence.sessions.server.ClientSession) {
                    query = ((ClientSession)ssession).getParent().getBroker().getQuery(name, arguments);
                    if (query != null) {
                        return query;
                    }
                }
            }
        }

        return query;
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
     */
    public void initializeDescriptors() {
        // ClientSession initializes sequencing during construction,
        // however DatabaseSession and ServerSession normally call initializeSequencing()
        // in initializeDescriptors method. 
        // Because initializeDescriptors() is not called for a session-member of a SessionBroker,
        // initializeSequencing() for the sessions should be called here.
        if (!isClientSessionBroker()) {
            for (Iterator enumtr = getSessionsByName().values().iterator(); enumtr.hasNext();) {
                DatabaseSessionImpl databaseSession = (DatabaseSessionImpl)enumtr.next();
                databaseSession.initializeSequencing();
            }
            if(hasExternalTransactionController()) {
                getExternalTransactionController().initializeSequencingListeners();
            }

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
            if (databaseSession.getProject().hasNonIsolatedUOWClasses()) {
                getProject().setHasNonIsolatedUOWClasses(true);
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
        //Bug#3440544 Check if logged in already to stop the attempt to login more than once
        if (isLoggedIn) {
            throw ValidationException.alreadyLoggedIn(this.getName());
        } else {
            if (this.eventManager != null) {
                this.eventManager.preLogin(this);
            }
            // Bug 3848021 - ensure the external transaction controller is initialized
            if (!isConnected()) {
                getServerPlatform().initializeExternalTransactionController();
            }

            // Connection all sessions and initialize
            for (Iterator sessionEnum = getSessionsByName().values().iterator();
                     sessionEnum.hasNext();) {
                DatabaseSessionImpl session = (DatabaseSessionImpl)sessionEnum.next();
                if (session.hasEventManager()) {
                    session.getEventManager().preLogin(session);
                }
                if (!session.isConnected()) {
                    session.connect();
                }
            }
            initializeDescriptors();
            if (getCommandManager() != null) {
                getCommandManager().initialize();
            }
            isLoggedIn = true;
        }
    }

    /**
     * PUBLIC:
     * Connect to the database using the predefined login.
     * This connects all of the child sessions and expects that they are in a valid state to be connected.
     */
    public void login(String userName, String password) throws DatabaseException {
        //Bug#3440544 Check if logged in already to stop the attempt to login more than once
        if (isLoggedIn) {
            throw ValidationException.alreadyLoggedIn(this.getName());
        } else {
            if (this.eventManager != null) {
                this.eventManager.preLogin(this);
            }
            // Bug 3848021 - ensure the external transaction controller is initialized
            if (!isConnected()) {
                getServerPlatform().initializeExternalTransactionController();
            }

            // Connection all sessions and initialize
            for (Iterator sessionEnum = getSessionsByName().values().iterator();
                     sessionEnum.hasNext();) {
                DatabaseSessionImpl session = (DatabaseSessionImpl)sessionEnum.next();
                if (session.hasEventManager()) {
                    session.getEventManager().preLogin(session);
                }
                session.getDatasourceLogin().setUserName(userName);
                session.getDatasourceLogin().setPassword(password);

                if (!session.isConnected()) {
                    session.connect();
                }
            }
            initializeDescriptors();
            this.isLoggedIn = true;
        }
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
        for (Iterator sessionEnum = getSessionsByName().values().iterator();
                 sessionEnum.hasNext();) {
            DatabaseSessionImpl session = (DatabaseSessionImpl)sessionEnum.next();
            session.logout();
        }
        if (!isClientSessionBroker()) {
            if(hasExternalTransactionController()) {
                getExternalTransactionController().clearSequencingListeners();
            }
        }
        sequencing = null;
        isLoggedIn = false;
    }

    /**
     * PUBLIC:
     * Register the session under its name.
     * All of the session's descriptors must have already been registered.
     * The session should not be connected and descriptors should not be initialized.
     */
    public void registerSession(String name, AbstractSession session) {
        session.setIsInBroker(true);
        getSessionsByName().put(name, session);
        session.setBroker(this);
        session.setName(name);

        // The keys/classes must also be used as some descriptors may be under multiple classes.
        Iterator descriptors = session.getDescriptors().values().iterator();
        Iterator classes = session.getDescriptors().keySet().iterator();
        while (descriptors.hasNext()) {
            ClassDescriptor descriptor = (ClassDescriptor)descriptors.next();
            Class descriptorClass = (Class)classes.next();
            getSessionNamesByClass().put(descriptorClass, name);
            getDescriptors().put(descriptorClass, descriptor);
        }
    }

    /**
     * PUBLIC:
     * Register the session under its name.
     * All of the session's descriptors must have already been registered.
     * The session should not be connected and descriptors should not be initialized.
     */
    public void registerSession(String name, org.eclipse.persistence.sessions.Session session) {
        registerSession(name, (AbstractSession)session);
    }

    /**
     * INTERNAL:
     * Register the session under its name.
     * All of the session's descriptors must have already been registered.
     * The session should not be connected and descriptors should not be initialized.
     * Used for client session broker
     */
 
    public void internalRegisterSession(String name, AbstractSession session) {
        //Bug#3911318  Removed session brokers and sessions point to the same descriptors.  
        //They don't need to be reassigned.  Also the code to add read only classes has been removed
        //because client broker and server broker point to the same project.
        session.setIsInBroker(true);
        getSessionsByName().put(name, session);
        session.setBroker(this);
        session.setName(name);

 /*       // The keys/classes must also be used as some descriptors may be under multiple classes.
        Iterator classes = session.getDescriptors().keySet().iterator();
        while (classes.hasNext()) {
            Class descriptorClass = (Class)classes.next();
            getSessionNamesByClass().put(descriptorClass, session);
        }
  */  }

    /**
     * PUBLIC:
     * Release the session.
     * This does nothing by default, but allows for other sessions such as the ClientSession to do something.
     */
    public void release() {
        if (isClientSessionBroker()) {
            log(SessionLog.FINER, SessionLog.CONNECTION, "releasing_client_session_broker");
        }
        AbstractSession session;
        for (Iterator enumtr = getSessionsByName().values().iterator(); enumtr.hasNext();) {
            session = (AbstractSession)enumtr.next();
            session.release();
        }
        super.release();
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
}
