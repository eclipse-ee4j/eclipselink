/*
 * Copyright (c) 1998, 2019 Oracle and/or its affiliates. All rights reserved.
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
//     09/03/2015 - Will Dazey
//       - 456067 : Added support for defining query timeout units
//     05/26/2016-2.7 Tomas Kraus
//       - 494610: Session Properties map should be Map<String, Object>
package org.eclipse.persistence.sessions;

import java.io.Writer;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import org.eclipse.persistence.config.ReferenceMode;
import org.eclipse.persistence.core.sessions.CoreSession;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.partitioning.PartitioningPolicy;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.exceptions.ExceptionHandler;
import org.eclipse.persistence.exceptions.IntegrityChecker;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.internal.databaseaccess.Platform;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.logging.SessionLogEntry;
import org.eclipse.persistence.platform.database.DatabasePlatform;
import org.eclipse.persistence.platform.server.ServerPlatform;
import org.eclipse.persistence.queries.AttributeGroup;
import org.eclipse.persistence.queries.Call;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.JPQLCall;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.queries.SQLCall;
import org.eclipse.persistence.sessions.serializers.Serializer;

/**
 * <p>
 * <b>Purpose</b>: Define the EclipseLink session public interface.
 * <p>
 * <b>Description</b>: This interface is meant to clarify the public protocol into EclipseLink.
 * It also allows for non-subclasses of Session to conform to the EclipseLink API.
 * It should be used as the applications main interface into the EclipseLink API to
 * ensure compatibility between all EclipseLink sessions.
 * <p>
 * <b>Responsibilities</b>:
 * <ul>
 * <li> Define the API for all reading, units of work.
 * </ul>
 * @see UnitOfWork
 * @see DatabaseSession
 * @see org.eclipse.persistence.internal.sessions.AbstractSession
 * @see org.eclipse.persistence.internal.sessions.DatabaseSessionImpl
 * @see org.eclipse.persistence.sessions.server.ServerSession
 * @see org.eclipse.persistence.sessions.server.ClientSession
 */
public interface Session extends CoreSession<ClassDescriptor, Login, Platform, Project, SessionEventManager> {

    /**
     * ADVANCED:
     * Returns a light weight read-only session where all
     * objects are automatically read as of the specified past time.
     * <p>Use this Session to take advantage of Oracle 9 Release 2 Flashback or
     * EclipseLink general history support and still be able to cache query results.
     * <p>A special historical session is required as all objects read may
     * be of different versions than those stored in the global session cache.
     * Hence also known as IsolationSession, as all reads bypass the global
     * cache.
     * <p>An AsOfClause at the Session level will override any clauses set at the
     * query or expression levels.
     * <p>
     * Example: Using a historical session to read past versions of objects.
     *
     * <blockquote><pre>
     *  AsOfClause pastTime = new AsOfClause(System.currentTimeMillis() - 24*60*60*1000);
     *     Session historicalSession = session.acquireSessionAsOf(pastTime);
     *      Employee pastEmployee = (Employee)historicalSession.readObject(Employee.class);
     *      Address pastAddress = pastEmployee.getAddress();
     *      Vector pastProjects = pastEmployee.getProjects();
     *  historicalSession.release();
     * </pre></blockquote>
     * <p>
     * Example: Using the above past employee to recover objects.
     *
     * <blockquote><pre>
     *     UnitOfWork uow = baseSession.acquireUnitOfWork();
     *      Employee presentClone = (Employee)uow.readObject(pastEmployee);
     *      uow.deepMergeClone(pastEmployee);
     *  uow.commit();
     * </pre></blockquote>
     * <p>
     * By definition all data as of a past time is frozen.  So this session is
     * also ideal for read consistent queries and read only transactions, as all
     * queries will be against a consistent and immutable snap shot of the data.
     * @param pastTime Represents a valid snap shot time.
     * @throws ValidationException if <code>this</code>
     * not a ClientSession, plain Session, or SessionBroker.
     * @see org.eclipse.persistence.history.AsOfClause
     * @see org.eclipse.persistence.expressions.Expression#asOf(org.eclipse.persistence.history.AsOfClause)
     * @see org.eclipse.persistence.queries.ObjectLevelReadQuery#setAsOfClause(org.eclipse.persistence.history.AsOfClause)
     * @see org.eclipse.persistence.history.HistoryPolicy
     */
    Session acquireHistoricalSession(org.eclipse.persistence.history.AsOfClause pastTime);

    /**
     * PUBLIC:
     * Return a unit of work for this session.
     * The unit of work is an object level transaction that allows
     * a group of changes to be applied as a unit.
     * The return value should be used as the org.eclipse.persistence.sessions.UnitOfWork interface
     *
     * @see UnitOfWork
     */
    UnitOfWork acquireUnitOfWork();

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
    UnitOfWork acquireUnitOfWork(ReferenceMode referenceMode);
    /**
     * PUBLIC:
     * Add the query to the session queries with the given name.
     * This allows for common queries to be pre-defined, reused and executed by name.
     */
    void addQuery(String name, DatabaseQuery query);

    /**
     * ADVANCED:
     * Add a pre-defined not yet parsed JPQL String/query to the session to be parsed
     * after descriptors are initialized.
     */
    void addJPAQuery(DatabaseQuery query);

    /**
     * PUBLIC:
     * clear the integrityChecker, the integrityChecker holds all the ClassDescriptor Exceptions.
     */
    void clearIntegrityChecker();

    /**
     * PUBLIC:
     * Clear the profiler, this will end the current profile operation.
     */
    void clearProfile();


    /**
     * PUBLIC:
     * Return true if the pre-defined query is defined on the session.
     */
    boolean containsQuery(String queryName);

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
    Object copy(Object originalObjectOrObjects);

    /**
     * PUBLIC:
     * Return a complete copy of the object or collection of objects.
     * In case of collection all members should be either entities of the same type
     * or have a common inheritance hierarchy mapped root class.
     * This can be used to obtain a scratch copy of an object,
     * or for templatizing an existing object into another new object.
     * If there are no attributes in the group
     * then the object and all of its privately owned parts will be copied.
     * Otherwise only the attributes included into the group will be copied.
     */
    Object copy(Object originalObjectOrObjects, AttributeGroup group);

    /**
     * PUBLIC:
     * Return if the object exists on the database or not.
     * This always checks existence on the database.
     */
    boolean doesObjectExist(Object object) throws DatabaseException;

    /**
     * PUBLIC:
     * Turn off logging
     */
    void dontLogMessages();

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
    int executeNonSelectingCall(Call call);

    /**
     * PUBLIC:
     * Execute the non-selecting (update/DML) SQL string.
     * Warning: Allowing an unverified SQL string to be passed into this
     * method makes your application vulnerable to SQL injection attacks.
     */
    void executeNonSelectingSQL(String sqlString);

    /**
     * PUBLIC:
     * Execute the pre-defined query by name and return the result.
     * Queries can be pre-defined and named to allow for their reuse.
     *
     * @see #addQuery(String, DatabaseQuery)
     */
    Object executeQuery(String queryName);

    /**
     * PUBLIC:
     * Execute the pre-defined query by name and return the result.
     * Queries can be pre-defined and named to allow for their reuse.
     * The class is the descriptor in which the query was pre-defined.
     *
     * @see org.eclipse.persistence.descriptors.DescriptorQueryManager#addQuery(String, DatabaseQuery)
     */
    Object executeQuery(String queryName, Class domainClass);

    /**
     * PUBLIC:
     * Execute the pre-defined query by name and return the result.
     * Queries can be pre-defined and named to allow for their reuse.
     * The class is the descriptor in which the query was pre-defined.
     *
     * @see org.eclipse.persistence.descriptors.DescriptorQueryManager#addQuery(String, DatabaseQuery)
     */
    Object executeQuery(String queryName, Class domainClass, Object arg1);

    /**
     * PUBLIC:
     * Execute the pre-defined query by name and return the result.
     * Queries can be pre-defined and named to allow for their reuse.
     * The class is the descriptor in which the query was pre-defined.
     *
     * @see org.eclipse.persistence.descriptors.DescriptorQueryManager#addQuery(String, DatabaseQuery)
     */
    Object executeQuery(String queryName, Class domainClass, Object arg1, Object arg2);

    /**
     * PUBLIC:
     * Execute the pre-defined query by name and return the result.
     * Queries can be pre-defined and named to allow for their reuse.
     * The class is the descriptor in which the query was pre-defined.
     *
     * @see org.eclipse.persistence.descriptors.DescriptorQueryManager#addQuery(String, DatabaseQuery)
     */
    Object executeQuery(String queryName, Class domainClass, Object arg1, Object arg2, Object arg3);

    /**
     * PUBLIC:
     * Execute the pre-defined query by name and return the result.
     * Queries can be pre-defined and named to allow for their reuse.
     * The class is the descriptor in which the query was pre-defined.
     *
     * @see org.eclipse.persistence.descriptors.DescriptorQueryManager#addQuery(String, DatabaseQuery)
     */
    Object executeQuery(String queryName, Class domainClass, List argumentValues);

    /**
     * PUBLIC:
     * Execute the pre-defined query by name and return the result.
     * Queries can be pre-defined and named to allow for their reuse.
     *
     * @see #addQuery(String, DatabaseQuery)
     */
    Object executeQuery(String queryName, Object arg1);

    /**
     * PUBLIC:
     * Execute the pre-defined query by name and return the result.
     * Queries can be pre-defined and named to allow for their reuse.
     *
     * @see #addQuery(String, DatabaseQuery)
     */
    Object executeQuery(String queryName, Object arg1, Object arg2);

    /**
     * PUBLIC:
     * Execute the pre-defined query by name and return the result.
     * Queries can be pre-defined and named to allow for their reuse.
     *
     * @see #addQuery(String, DatabaseQuery)
     */
    Object executeQuery(String queryName, Object arg1, Object arg2, Object arg3);

    /**
     * PUBLIC:
     * Execute the pre-defined query by name and return the result.
     * Queries can be pre-defined and named to allow for their reuse.
     *
     * @see #addQuery(String, DatabaseQuery)
     */
    Object executeQuery(String queryName, List argumentValues);

    /**
     * PUBLIC:
     * Execute the database query.
     * A query is a database operation such as reading or writing.
     * The query allows for the operation to be customized for such things as,
     * performance, depth, caching, etc.
     *
     * @see DatabaseQuery
     */
    Object executeQuery(DatabaseQuery query) throws EclipseLinkException;

    /**
     * PUBLIC:
     * Return the results from executing the database query.
     * the arguments are passed in as a vector
     */
    Object executeQuery(DatabaseQuery query, List argumentValues);

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
    Vector executeSelectingCall(Call call);

    /**
     * PUBLIC:
     * Execute the selecting SQL string.
     * A Vector of DatabaseRecords are returned.
     * Warning: Allowing an unverified SQL string to be passed into this
     * method makes your application vulnerable to SQL injection attacks.
     */
    Vector executeSQL(String sqlString);

    /**
     * PUBLIC:
     * Return the active session for the current active external (JTS) transaction.
     * This should only be used with JTS and will return the session if no external transaction exists.
     */
    Session getActiveSession();

    /**
     * PUBLIC:
     * Return the active unit of work for the current active external (JTS) transaction.
     * This should only be used with JTS and will return null if no external transaction exists.
     */
    UnitOfWork getActiveUnitOfWork();

    /**
     * ADVANCED:
     * Return the descriptor specified for the class.
     * If the class does not have a descriptor but implements an interface that is also implemented
     * by one of the classes stored in the map, that descriptor will be stored under the
     * new class.
     */
    ClassDescriptor getClassDescriptor(Class theClass);

    /**
     * ADVANCED:
     * Return the descriptor specified for the object's class.
     */
    ClassDescriptor getClassDescriptor(Object domainObject);

    /**
     * PUBLIC:
     * Return the descriptor for the alias.
     */
    ClassDescriptor getClassDescriptorForAlias(String alias);


    /**
     * ADVANCED:
     * Answers the past time this session is as of.  Indicates whether or not this
     * is a special historical session where all objects are read relative to a
     * particular point in time.
     * @return An immutable object representation of the past time.
     * <code>null</code> if no clause set, or this a regular session.
     * @see #acquireHistoricalSession(org.eclipse.persistence.history.AsOfClause)
     */
    org.eclipse.persistence.history.AsOfClause getAsOfClause();

    /**
     *  Stores the default Session wide reference mode that a UnitOfWork will use when referencing
     *  managed objects.
     *  @see org.eclipse.persistence.config.ReferenceMode
     */
    ReferenceMode getDefaultReferenceMode();

    /**
     * ADVANCED:
     * Return the descriptor specified for the class.
     * If the class does not have a descriptor but implements an interface that is also implemented
     * by one of the classes stored in the map, that descriptor will be stored under the
     * new class.
     */
    @Override ClassDescriptor getDescriptor(Class theClass);

    /**
     * ADVANCED:
     * Return the descriptor specified for the object's class.
     */
    @Override ClassDescriptor getDescriptor(Object domainObject);

    /**
     * PUBLIC:
     * Return the descriptor for  the alias.
     * UnitOfWork delegates this to the parent
     */
    ClassDescriptor getDescriptorForAlias(String alias);

    /**
     * ADVANCED:
     * Return all registered descriptors.
     */
    @Override Map<Class, ClassDescriptor> getDescriptors();

    /**
     * ADVANCED:
     * Return all pre-defined not yet parsed EJBQL queries.
     */
    List<DatabaseQuery> getJPAQueries();

    /**
     * PUBLIC:
     * Return the event manager.
     * The event manager can be used to register for various session events.
     */
    @Override SessionEventManager getEventManager();

    /**
     * PUBLIC:
     * Return the ExceptionHandler.Exception handler can catch errors that occur on queries or during database access.
     */
    ExceptionHandler getExceptionHandler();

    /**
     * PUBLIC:
     * Used for JTS integration.  If your application requires to have JTS control transactions instead of EclipseLink an
     * external transaction controller must be specified.  EclipseLink provides JTS controllers for JTS 1.0 and Weblogic's JTS.
     * @see org.eclipse.persistence.transaction.JTATransactionController
     */
    ExternalTransactionController getExternalTransactionController();


    /**
     * PUBLIC:
     * The IdentityMapAccessor is the preferred way of accessing IdentityMap functions
     * This will return an object which implements an interface which exposes all public
     * IdentityMap functions.
     */
    IdentityMapAccessor getIdentityMapAccessor();

    /**
     * PUBLIC:
     * Returns the integrityChecker,the integrityChecker holds all the ClassDescriptor Exceptions.
     */
    IntegrityChecker getIntegrityChecker();

    /**
     * PUBLIC:
     * Return the writer to which an accessor writes logged messages and SQL.
     * If not set, this reference defaults to a writer on System.out.
     * To enable logging logMessages must be turned on.
     *
     * @see #logMessage
     */
    Writer getLog();

    /**
     * PUBLIC:
     * Return the database platform currently connected to.
     * The platform is used for database specific behavior.
     * NOTE: this must only be used for relational specific usage,
     * it will fail for non-relational datasources.
     */
    DatabasePlatform getPlatform();

    /**
     * PUBLIC:
     * Return the database platform currently connected to.
     * The platform is used for database specific behavior.
     */
    @Override Platform getDatasourcePlatform();

    /**
     * PUBLIC:
     * Return the login, the login holds any database connection information given.
     * NOTE: this must only be used for relational specific usage,
     * it will fail for non-relational datasources.
     */
    DatabaseLogin getLogin();

    /**
     * PUBLIC:
     * Return the login, the login holds any database connection information given.
     * This return the Login interface and may need to be cast to the datasource specific implementation.
     */
    @Override Login getDatasourceLogin();

    /**
     * PUBLIC:
     * Return the name of the session.
     * This is used with the session broker, or to give the session a more meaningful name.
     */
    String getName();

    /**
     * ADVANCED:
     * Return the sequence number from the database.
     */
    Number getNextSequenceNumberValue(Class domainClass);

    /**
     * PUBLIC:
     * Return the profiler.
     * The profiler is a tool that can be used to determine performance bottlenecks.
     * The profiler can be queries to print summaries and configure for logging purposes.
     */
    SessionProfiler getProfiler();

    /**
     * PUBLIC:
     * Return the project.
     * The project includes the login and descriptor and other configuration information.
     */
    @Override org.eclipse.persistence.sessions.Project getProject();

    /**
     * ADVANCED:
     * Allow for user defined properties.
     */
    Map<String, Object> getProperties();

    /**
     * ADVANCED:
     * Returns the user defined property.
     */
    Object getProperty(String name);

    /**
     * ADVANCED:
     * Return all pre-defined queries.
     */
    Map<String, List<DatabaseQuery>> getQueries();

    /**
     * PUBLIC:
     * Return the query from the session pre-defined queries with the given name.
     * This allows for common queries to be pre-defined, reused and executed by name.
     */
    DatabaseQuery getQuery(String name);

    /**
     * PUBLIC:
     * Return the query from the session pre-defined queries with the given name.
     * This allows for common queries to be pre-defined, reused and executed by name.
     */
    DatabaseQuery getQuery(String name, List arguments);

    /**
     * PUBLIC:
     * Return the server platform currently used.
     * The server platform is used for application server specific behavior.
     */
    ServerPlatform getServerPlatform();

    /**
     * PUBLIC:
     * Return the session log to which an accessor logs messages and SQL.
     * If not set, this will default to a session log on a writer on System.out.
     * To enable logging, logMessages must be turned on.
     *
     * @see #logMessage
     */
    SessionLog getSessionLog();


    /**
     * PUBLIC:
     * Allow any WARNING level exceptions that occur within EclipseLink to be logged and handled by the exception handler.
     */
    Object handleException(RuntimeException exception) throws RuntimeException;


    /**
     * ADVANCED:
     * Return true if a descriptor exists for the given class.
     */
    boolean hasDescriptor(Class theClass);

    /**
     * PUBLIC:
     * Return if an exception handler is present.
     */
    boolean hasExceptionHandler();

    /**
     * PUBLIC:
     * Used for JTS integration.  If your application requires to have JTS control transactions instead of EclipseLink an
     * external transaction controller must be specified.  EclipseLink provides JTS controllers for JTS 1.0 and Weblogic's JTS.
     * @see org.eclipse.persistence.transaction.JTATransactionController
     */
    boolean hasExternalTransactionController();


    /**
     * PUBLIC:
     * Return if this session is a client session.
     */
    boolean isClientSession();

    /**
     * PUBLIC:
     * Return if this session is connected to the database.
     */
    boolean isConnected();

    /**
     * PUBLIC:
     * Return if this session is a database session.
     */
    boolean isDatabaseSession();

    /**
     * PUBLIC:
     * Return if this session is a distributed session.
     */
    boolean isDistributedSession();

    /**
     * PUBLIC:
     * Return if a profiler is being used.
     */
    boolean isInProfile();

    /**
     * PUBLIC:
     * Return if this session is a remote session.
     */
    boolean isRemoteSession();

    /**
     * PUBLIC:
     * Return if this session is a server session.
     */
    boolean isServerSession();

    /**
     * PUBLIC:
     * Return if this session is a session broker.
     */
    boolean isSessionBroker();

    /**
     * PUBLIC:
     * Return if this session is a unit of work.
     */
    boolean isUnitOfWork();

    /**
     * PUBLIC:
     * Return if this session is a remote unit of work.
     */
    boolean isRemoteUnitOfWork();

    /**
     * ADVANCED:
     * Extract and return the Id from the object.
     */
    Object getId(Object domainObject) throws ValidationException;

    /**
     * PUBLIC:
     * Log the log entry.
     */
    void log(SessionLogEntry entry);

    /**
     * Log a untranslated message to the EclipseLink log at FINER level.
     */
    void logMessage(String message);


    /**
     * PUBLIC:
     * Read all of the instances of the class from the database.
     * This operation can be customized through using a ReadAllQuery,
     * or through also passing in a selection criteria.
     *
     * @see ReadAllQuery
     * @see #readAllObjects(Class, Expression)
     */
    Vector readAllObjects(Class domainClass) throws DatabaseException;


    /**
     * PUBLIC:
     * Read all the instances of the class from the database returned through execution the Call string.
     * The Call can be an SQLCall or JPQLCall.
     *
     * example: session.readAllObjects(Employee.class, new SQLCall("SELECT * FROM EMPLOYEE"));
     * @see SQLCall
     * @see JPQLCall
     */
    Vector readAllObjects(Class domainClass, Call aCall) throws DatabaseException;

    /**
     * PUBLIC:
     * Read all of the instances of the class from the database matching the given expression.
     * This operation can be customized through using a ReadAllQuery.
     *
     * @see ReadAllQuery
     */
    Vector readAllObjects(Class domainClass, Expression selectionCriteria) throws DatabaseException;

    /**
     * PUBLIC:
     * Read the first instance of the class from the database.
     * This operation can be customized through using a ReadObjectQuery,
     * or through also passing in a selection criteria.
     * By default, this method executes a query without selection criteria and
     * consequently it will always result in a database access even if an instance
     * of the specified Class exists in the cache. Executing a query with
     * selection criteria allows you to avoid a database access if the selected
     * instance is in the cache.
     * Because of this, you may wish to consider a readObject method that takes selection criteria, such as: {@link #readObject(Class, Call)}, {@link #readObject(Class, Expression)}, or {@link #readObject(Object)}.
     * @see ReadObjectQuery
     * @see #readAllObjects(Class, Expression)
     */
    Object readObject(Class domainClass) throws DatabaseException;

    /**
     * PUBLIC:
     * Read the first instance of the class from the database returned through execution the Call string.
     * The Call can be an SQLCall or JPQLCall.
     *
     * example: session.readObject(Employee.class, new SQLCall("SELECT * FROM EMPLOYEE"));
     * @see SQLCall
     * @see JPQLCall
     */
    Object readObject(Class domainClass, Call aCall) throws DatabaseException;

    /**
     * PUBLIC:
     * Read the first instance of the class from the database matching the given expression.
     * This operation can be customized through using a ReadObjectQuery.
     *
     * @see ReadObjectQuery
     */
    Object readObject(Class domainClass, Expression selectionCriteria) throws DatabaseException;

    /**
     * PUBLIC:
     * Use the example object to construct a read object query by the objects primary key.
     * This will read the object from the database with the same primary key as the object
     * or null if no object is found.
     */
    Object readObject(Object object) throws DatabaseException;

    /**
     * PUBLIC:
     * Refresh the attributes of the object and of all of its private parts from the database.
     * This can be used to ensure the object is up to date with the database.
     * Caution should be used when using this to make sure the application has no uncommitted
     * changes to the object.
     */
    Object refreshObject(Object object);

    /**
     * PUBLIC:
     * Release the session.
     * This does nothing by default, but allows for other sessions such as the ClientSession to do something.
     */
    void release();


    /**
     * PUBLIC:
     * Remove the user defined property.
     */
    void removeProperty(String property);

    /**
     * PUBLIC:
     * Remove the query name from the set of pre-defined queries
     */
    void removeQuery(String queryName);


    /**
     *  Stores the default Session wide reference mode that a UnitOfWork will use when referencing
     *  managed objects.
     *  @see org.eclipse.persistence.config.ReferenceMode
     */
   void setDefaultReferenceMode(ReferenceMode defaultReferenceMode);

   /**
     * PUBLIC:
     * Set the exceptionHandler.
     * Exception handler can catch errors that occur on queries or during database access.
     */
    void setExceptionHandler(ExceptionHandler exceptionHandler);

    /**
     * OBSOLETE:
     * Previously used for JTS integration.
     *
     * If your application requires to have JTS control transactions a
     * ServerPlatform must be specified before login, either via your sessions.xml or in code.
     *
     * A subclass of ServerPlatformBase should handle your requirements.
     *
     * If not, we suggest creating your own subclass of ServerPlatformBase to specify the
     * external transaction controller class.
     *
     * @see org.eclipse.persistence.platform.server.CustomServerPlatform
     */
    //@deprecated was removed from this method as there is no viable alternative bug 5637867 was filed to
    // have this resolved.
    void setExternalTransactionController(ExternalTransactionController externalTransactionController);

    /**
     * PUBLIC:
     * Set the integrityChecker, the integrityChecker holds all the ClassDescriptor Exceptions.
     */
    void setIntegrityChecker(IntegrityChecker integrityChecker);

    /**
     * PUBLIC:
     * Set the writer to which an accessor writes logged messages and SQL.
     * If not set, this reference defaults to a writer on System.out.
     * To enable logging logMessages() is used.
     *
     * @see #logMessage
     */
    void setLog(Writer log);

    /**
     * PUBLIC:
     * Set the name of the session.
     * This is used with the session broker, or to give the session a more meaningful name.
     */
    void setName(String name);

    /**
     * PUBLIC:
     * Set the profiler for the session.
     * This allows for performance operations to be profiled.
     */
    void setProfiler(SessionProfiler profiler);

    /**
     * PUBLIC:
     * Allow for user defined properties.
     */
    void setProperty(String propertyName, Object propertyValue);

    /**
     * PUBLIC:
     * Set the session log to which an accessor logs messages and SQL.
     * If not set, this will default to a session log on a writer on System.out.
     * To enable logging, logMessages must be turned on.
     *
     * @see #logMessage
     */
    void setSessionLog(SessionLog sessionLog);

    /**
     * PUBLIC:
     * Return if logging is enabled (false if log level is OFF)
     */
    boolean shouldLogMessages();


    /**
     * ADVANCED:
     * This can be used to help debugging an object identity problem.
     * An object identity problem is when an object in the cache references an object not in the cache.
     * This method will validate that all cached objects are in a correct state.
     */
    void validateCache();

    /**
     * PUBLIC:
     * Return the log level.
     * <br>Possible values for log level and category are listed in SessionLog.
     * @see org.eclipse.persistence.logging.SessionLog
     */
    int getLogLevel(String category);

    /**
     * PUBLIC:
     * Return the log level.
     * <br>Possible values for log level are listed in SessionLog.
     * @see org.eclipse.persistence.logging.SessionLog
     */
    int getLogLevel();

    /**
     * PUBLIC:
     * Set the log level.
     * <br>Possible values for log level are listed in SessionLog.
     * @see org.eclipse.persistence.logging.SessionLog
     */
    @Override void setLogLevel(int level);

    /**
     * PUBLIC:
     * Check if a message of the given level would actually be logged.
     * <br>Possible values for log level and category are listed in SessionLog.
     * @see org.eclipse.persistence.logging.SessionLog
     */
    boolean shouldLog(int Level, String category);

    /**
     * PUBLIC:
     * Allow any SEVERE level exceptions that occur within EclipseLink to be logged and handled by the exception handler.
     */
    Object handleSevere(RuntimeException exception) throws RuntimeException;

    /**
     * PUBLIC:
     * Return if this session's descendants should use finalizers.
     * The allows certain finalizers such as in ClientSession to be enabled.
     * These are disable by default for performance reasons.
     */
    boolean isFinalizersEnabled();

    /**
     * PUBLIC:
     * Set if this session's descendants should use finalizers.
     * The allows certain finalizers such as in ClientSession to be enabled.
     * These are disable by default for performance reasons.
     */
    void setIsFinalizersEnabled(boolean isFinalizersEnabled);

    /**
     * PUBLIC:
     * Set the default query timeout for this session.
     * This timeout will apply to any queries that do not have a timeout set,
     * and that do not have a default timeout defined in their descriptor.
     */
    void setQueryTimeoutDefault(int queryTimeoutDefault);

    /**
     * PUBLIC:
     * Set the default query timeout units for this session.
     * This timeout unit will apply to any queries that do not have a unit value set,
     * and that do not have a default timeout unit defined in their descriptor.
     */
    void setQueryTimeoutUnitDefault(TimeUnit queryTimeoutDefault);

    /**
     * PUBLIC:
     * Return the session's partitioning policy.
     */
    PartitioningPolicy getPartitioningPolicy();

    /**
     * PUBLIC:
     * Set the session's partitioning policy.
     * A PartitioningPolicy is used to partition, load-balance or replicate data across multiple difference databases
     * or across a database cluster such as Oracle RAC.
     * Partitioning can provide improved scalability by allowing multiple database machines to service requests.
     */
    void setPartitioningPolicy(PartitioningPolicy partitioningPolicy);

    /**
     * Return the Serializer to use by default for serialization.
     */
    Serializer getSerializer();

    /**
     * Set the Serializer to use by default for serialization.
     */
    void setSerializer(Serializer serializer);
}
