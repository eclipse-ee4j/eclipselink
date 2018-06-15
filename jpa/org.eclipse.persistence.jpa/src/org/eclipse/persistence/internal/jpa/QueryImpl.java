/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
//     Zoltan NAGY & tware - updated support for MaxRows
//     11/01/2010-2.2 Guy Pelletier
//       - 322916: getParameter on Query throws NPE
//     11/09/2010-2.1 Michael O'Brien
//       - 329089: PERF: EJBQueryImpl.setParamenterInternal() move indexOf check inside non-native block
//     02/08/2012-2.4 Guy Pelletier
//       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
//     06/20/2012-2.5 Guy Pelletier
//       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
//     08/24/2012-2.5 Guy Pelletier
//       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
//     11/05/2012-2.5 Guy Pelletier
//       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
//     08/11/2012-2.5 Guy Pelletier
//       - 393867: Named queries do not work when using EM level Table Per Tenant Multitenancy.
package org.eclipse.persistence.internal.jpa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.LockTimeoutException;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Parameter;
import javax.persistence.PersistenceException;
import javax.persistence.PessimisticLockException;
import javax.persistence.Query;
import javax.persistence.QueryTimeoutException;
import javax.persistence.TemporalType;
import javax.persistence.TransactionRequiredException;

import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.internal.helper.BasicTypeHelperImpl;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.helper.ConversionManager;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.jpa.querydef.ParameterExpressionImpl;
import org.eclipse.persistence.internal.localization.ExceptionLocalization;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.jpa.JpaEntityManager;
import org.eclipse.persistence.platform.database.oracle.plsql.PLSQLStoredProcedureCall;
import org.eclipse.persistence.platform.database.oracle.plsql.PLSQLargument;
import org.eclipse.persistence.queries.Call;
import org.eclipse.persistence.queries.DataModifyQuery;
import org.eclipse.persistence.queries.DataReadQuery;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.DatabaseQuery.ParameterType;
import org.eclipse.persistence.queries.ModifyQuery;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReadQuery;
import org.eclipse.persistence.queries.StoredProcedureCall;
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.eclipse.persistence.sessions.Session;

/**
 * Concrete JPA query class. The JPA query wraps a DatabaseQuery which is
 * executed.
 */
public class QueryImpl {

    public static final int UNDEFINED = -1;

    /**
     * Wrapped native query. The query may be {@link #isShared}
     */
    protected DatabaseQuery databaseQuery = null;

    protected EntityManagerImpl entityManager = null;
    protected String queryName = null;
    protected Map<String, Object> parameterValues = null;
    protected Map<String, Parameter<?>> parameters;
    protected int firstResultIndex = UNDEFINED;
    protected int maxResults = UNDEFINED;

    protected LockModeType lockMode = null;

    /**
     * Stores if the wrapped query is shared, and requires cloning before being
     * changed.
     */
    protected boolean isShared;

    /**
     * Base constructor for EJBQueryImpl. Initializes basic variables.
     */
    protected QueryImpl(EntityManagerImpl entityManager) {
        this.parameterValues = new HashMap<String, Object>();
        this.entityManager = entityManager;
        this.isShared = true;
    }

    /**
     * Create an EJBQueryImpl with a DatabaseQuery.
     */
    public QueryImpl(DatabaseQuery query, EntityManagerImpl entityManager) {
        this(entityManager);
        this.databaseQuery = query;
    }

    /**
     * This method should be called to close any left over open connection to
     * the database (if there is one).
     */
    public void close() {
        // Currently nothing to do at this level. Connections are not left open.
    }

    /**
     * INTERNAL:
     * Change the internal query to data modify query.
     */
    protected void setAsDataModifyQuery() {
        DataModifyQuery query = new DataModifyQuery();
        query.setIsUserDefined(this.databaseQuery.isUserDefined());
        // By default, do not batch user native queries, as row count must be returned.
        query.setIsBatchExecutionSupported(false);
        query.copyFromQuery(this.databaseQuery);
        // Need to clone call, in case was executed as read.
        query.setDatasourceCall((Call) this.databaseQuery.getDatasourceCall().clone());
        this.databaseQuery = query;
    }

    /**
     * Internal method to change the wrapped query to a DataModifyQuery if
     * necessary. When created, the query is created as a DataReadQuery as it is
     * unknown if it is a SELECT or UPDATE. Note that this prevents the original
     * named query from every being prepared.
     */
    protected void setAsSQLModifyQuery() {
        if (getDatabaseQueryInternal().isDataReadQuery()) {
            setAsDataModifyQuery();
        }
    }

    /**
     * Internal method to change the wrapped query to a DataReadQuery if
     * necessary. This should never occur, but could possibly if the same query
     * was executed as executeUpdate() then as getResultList(). Note that the
     * initial conversion to modify would loose any read settings that had been
     * set.
     */
    protected void setAsSQLReadQuery() {
        if (getDatabaseQueryInternal().isDataModifyQuery()) {
            DataReadQuery query = new DataReadQuery();
            query.setResultType(DataReadQuery.AUTO);
            query.setIsUserDefined(databaseQuery.isUserDefined());
            query.copyFromQuery(this.databaseQuery);
            this.databaseQuery = query;
        }
    }

    /**
     * Execute a ReadQuery by assigning the stored parameter values and running
     * it in the database
     *
     * @return the results of the query execution
     */
    protected Object executeReadQuery() {
        List parameterValues = processParameters();
        // TODO: the following performFlush() call is a temporary workaround for
        // bug 4752493:
        // CTS: INMEMORY QUERYING IN EJBQUERY BROKEN DUE TO CHANGE TO USE
        // REPORTQUERY.
        // Ideally we should only flush in case the selectionExpression can't be
        // conformed in memory.
        // There are two alternative ways to implement that:
        // 1. Try running the query with conformInUOW flag first - if it fails
        // with
        // QueryException.cannotConformExpression then flush and run the query
        // again -
        // now without conforming.
        // 2. Implement a new isComformable method on Expression which would
        // determine whether the expression
        // could be conformed in memory, flush only in case it returns false.
        // Note that doesConform method currently implemented on Expression
        // requires object(s) to be confirmed as parameter(s).
        // The new isComformable method should not take any objects as
        // parameters,
        // it should return false if there could be such an object that
        // passed to doesConform causes it to throw
        // QueryException.cannotConformExpression -
        // and true otherwise.
        boolean shouldResetConformResultsInUnitOfWork = false;
        DatabaseQuery query = getDatabaseQueryInternal();
        boolean isObjectLevelReadQuery = query.isObjectLevelReadQuery();
        if (isFlushModeAUTO() && (!isObjectLevelReadQuery || !((ObjectLevelReadQuery)query).isReadOnly())) {
            performPreQueryFlush();
            if (isObjectLevelReadQuery) {
                if (((ObjectLevelReadQuery)query).shouldConformResultsInUnitOfWork()) {
                    cloneSharedQuery();
                    query = getDatabaseQueryInternal();
                    ((ObjectLevelReadQuery)query).setCacheUsage(ObjectLevelReadQuery.UseDescriptorSetting);
                    shouldResetConformResultsInUnitOfWork = true;
                }
            }
        }

        // Set a pessimistic locking on the query if specified.
        if (this.lockMode != null && !this.lockMode.equals(LockModeType.NONE)) {
            // We need to throw TransactionRequiredException if there is no
            // active transaction
            this.entityManager.checkForTransaction(true);

            // The lock mode setters and getters validate the query type
            // so should be safe to make the casting.
            cloneSharedQuery();
            query = getDatabaseQueryInternal();

            // Set the lock mode (the session is passed in to do some validation
            // checks)
            // If the return value from the set returns true, it indicates that
            // we were unable to set the lock mode.
            if (((ObjectLevelReadQuery)query).setLockModeType(lockMode.name(), (AbstractSession) getActiveSession())) {
                throw new PersistenceException(ExceptionLocalization.buildMessage("ejb30-wrong-lock_called_without_version_locking-index", null));
            }
        }

        Session session = getActiveSession();
        try {
            // in case it's a user-defined query
            if (query.isUserDefined()) {
                // and there is an active transaction
                if (this.entityManager.checkForTransaction(false) != null) {
                    // verify whether uow has begun early transaction
                    if (session.isUnitOfWork() && !((UnitOfWorkImpl)session).wasTransactionBegunPrematurely()) {
                        // uow begins early transaction in case it hasn't
                        // already begun.
                        // TODO: This is not good, it means that no SQL queries
                        // can ever use the cache,
                        // using isUserDefined to mean an SQL query is also
                        // wrong.
                        ((UnitOfWorkImpl)session).beginEarlyTransaction();
                    }
                }
            }

            // Execute the query and return the result.
            return session.executeQuery(query, parameterValues);
        } catch (DatabaseException e) {
            throw getDetailedException(e);
        } catch (RuntimeException e) {
            setRollbackOnly();
            throw e;
        } finally {
            this.lockMode = null;

            if (shouldResetConformResultsInUnitOfWork) {
                ((ObjectLevelReadQuery)query).conformResultsInUnitOfWork();
            }
        }
    }

    /**
     * Execute an update or delete statement.
     *
     * @return the number of entities updated or deleted
     */
    public int executeUpdate() {
        // bug51411440: need to throw IllegalStateException if query
        // executed on closed em
        this.entityManager.verifyOpenWithSetRollbackOnly();
        try {
            setAsSQLModifyQuery();
            // bug:4294241, only allow modify queries - UpdateAllQuery preferred
            if (!(getDatabaseQueryInternal() instanceof ModifyQuery)) {
                throw new IllegalStateException(ExceptionLocalization.buildMessage("incorrect_query_for_execute_update"));
            }

            // need to throw TransactionRequiredException if there is no active
            // transaction
            entityManager.checkForTransaction(true);

            // fix for bug:4288845, did not add the parameters to the query
            List parameterValues = processParameters();
            if (isFlushModeAUTO()) {
                performPreQueryFlush();
            }
            Integer changedRows = (Integer) getActiveSession().executeQuery(databaseQuery, parameterValues);
            return changedRows.intValue();
        } catch (PersistenceException exception) {
            setRollbackOnly();
            throw exception;
        } catch (IllegalStateException exception) {
            setRollbackOnly();
            throw exception;
        }catch (RuntimeException exception) {
            setRollbackOnly();
            throw new PersistenceException(exception);
        }
    }

    /**
     * Return the wrapped {@link DatabaseQuery} ensuring that if it
     * {@link #isShared} it is cloned before returning to prevent corruption of
     * the query cache.
     *
     * @see #getDatabaseQueryInternal()
     */
    public DatabaseQuery getDatabaseQuery() {
        cloneSharedQuery();
        return getDatabaseQueryInternal();
    }

    /**
     * INTERNAL: Return the cached database query for this EJBQueryImpl. If the
     * query is a named query and it has not yet been looked up, the query will
     * be looked up and stored as the cached query.
     */
    public DatabaseQuery getDatabaseQueryInternal() {
        if ((this.queryName != null) && (this.databaseQuery == null)) {
            // Always ask for the query from the active session. Table per
            // tenant multitenant entity queries may be isolated per EM meaning
            // those queries will not have been initialized (and made available)
            // from their parent session.
            this.databaseQuery = this.entityManager.getActiveSessionIfExists().getQuery(this.queryName);
            // need error checking and appropriate exception for non-existing query
            if (this.databaseQuery != null) {
                if (!this.databaseQuery.isPrepared()) {
                    // prepare the query before cloning, this ensures we do not
                    // have to continually prepare on each usage
                    try {
                        this.databaseQuery.checkPrepare(this.entityManager.getActiveSessionIfExists(), new DatabaseRecord());
                    } catch(RuntimeException re){
                        throw new IllegalArgumentException(re);
                    }
                }
                if (this.databaseQuery.isObjectLevelReadQuery() && ((ObjectLevelReadQuery)this.databaseQuery).getLockModeType() != null){
                    this.lockMode = LockModeType.valueOf(((ObjectLevelReadQuery)this.databaseQuery).getLockModeType());
                }
                if (this.databaseQuery.isReadQuery()){
                    this.maxResults = ((ReadQuery)this.databaseQuery).getInternalMax();
                    // Bug 501272
                    // Do not reset a Query's uninitialized first result index, unless the parameter is greater than 0 (default for ReadQuery).
                    int queryFirstResult = ((ReadQuery)this.databaseQuery).getFirstResult();
                    if ((this.firstResultIndex != UNDEFINED) || (this.firstResultIndex == UNDEFINED && queryFirstResult > 0)) {
                        this.firstResultIndex = queryFirstResult;
                    }
                }
            } else {
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage("unable_to_find_named_query", new Object[] { this.queryName }));
            }

        }

        return this.databaseQuery;
    }

    /**
     * Given a DatabaseException, this method will determine if we should
     * throw a different more specific exception like a lock timeout exception.
     */
    protected RuntimeException getDetailedException(DatabaseException e) {
        // If we catch a database exception as a result of executing a
        // pessimistic locking query we need to ask the platform which
        // JPA 2.0 locking exception we should throw. It will be either
        // be a PessimisticLockException or a LockTimeoutException (if
        // the query was executed using a wait timeout value)
        if (this.lockMode != null && this.lockMode.name().contains(ObjectLevelReadQuery.PESSIMISTIC_)) {
            // ask the platform if it is a lock timeout
            if (getActiveSession().getPlatform().isLockTimeoutException(e)) {
                return new LockTimeoutException(e);
            } else {
                return new PessimisticLockException(e);
            }
        } else {
            setRollbackOnly();
            return new PersistenceException(e);
        }
    }

    /**
     * Return the entityManager this query is tied to.
     */
    public JpaEntityManager getEntityManager() {
        return entityManager;
    }

    /**
     * Return the internal map of parameters.
     */
    protected Map<String, Parameter<?>> getInternalParameters() {
        if (this.parameters == null) {
            this.parameters = new HashMap<String, Parameter<?>>();
            DatabaseQuery query = getDatabaseQueryInternal(); // Retrieve named
                                                              // query
            int count = 0;
            if (query.getArguments() != null && !query.getArguments().isEmpty()) {
                boolean checkParameterType = query.getArgumentParameterTypes().size() == query.getArguments().size();
                for (String argName : query.getArguments()) {
                    Parameter<?> param = null;
                    ParameterType type = null;
                    if (checkParameterType){
                        type = query.getArgumentParameterTypes().get(count);
                    }
                    if (type == ParameterType.POSITIONAL){
                        Integer position = Integer.parseInt(argName);
                        param = new ParameterExpressionImpl(null, query.getArgumentTypes().get(count), position);
                    } else {
                        param = new ParameterExpressionImpl(null, query.getArgumentTypes().get(count), argName);
                    }
                    this.parameters.put(argName, param);
                    ++count;
                }
            }

        }

        return this.parameters;
    }

    /**
     * Get the current lock mode for the query.
     *
     * @return lock mode
     * @throws IllegalStateException
     *             if not a Java Persistence query language SELECT query
     */
    public LockModeType getLockMode() {
        entityManager.verifyOpen();

        if (!getDatabaseQueryInternal().isObjectLevelReadQuery()) {
            throw new IllegalStateException(ExceptionLocalization.buildMessage("invalid_lock_query", (Object[]) null));
        }

        return this.lockMode;
    }

    /**
     * Execute the query and return the query results as a List.
     *
     * @return a list of the results
     */
    public List getResultList() {
        // bug51411440: need to throw IllegalStateException if query
        // executed on closed em
        this.entityManager.verifyOpenWithSetRollbackOnly();
        try {
            setAsSQLReadQuery();
            propagateResultProperties();
            // bug:4297903, check container policy class and throw exception if
            // its not the right type
            DatabaseQuery query = getDatabaseQueryInternal();
            if (query.isReadAllQuery()) {
                Class containerClass = ((ReadAllQuery) query).getContainerPolicy().getContainerClass();
                if (!Helper.classImplementsInterface(containerClass, ClassConstants.List_Class)) {
                    throw QueryException.invalidContainerClass(containerClass, ClassConstants.List_Class);
                }
            } else if (query.isReadObjectQuery()) {
                List resultList = new ArrayList();
                Object result = executeReadQuery();
                if (result != null) {
                    resultList.add(result);
                }
                return resultList;
            } else if (!query.isReadQuery()) {
                throw new IllegalStateException(ExceptionLocalization.buildMessage("incorrect_query_for_get_result_list"));
            }
            return (List) executeReadQuery();
        } catch (LockTimeoutException exception) {
            throw exception;
        } catch (PersistenceException exception) {
            setRollbackOnly();
            throw exception;
        } catch (IllegalStateException exception) {
            setRollbackOnly();
            throw exception;
        } catch (RuntimeException exception) {
            setRollbackOnly();
            throw new PersistenceException(exception);
        }
    }

    /**
     * Execute a SELECT query that returns a single untyped result.
     *
     * @return the result
     * @throws NoResultException if there is no result
     * @throws NonUniqueResultException if more than one result
     * @throws IllegalStateException if called for a Java Persistence query
     *         language UPDATE or DELETE statement
     * @throws QueryTimeoutException if the query execution exceeds the query
     *         timeout value set and only the statement is rolled back
     * @throws TransactionRequiredException if a lock mode other than NONE has
     *         been been set and there is no transaction or the persistence
     *         context has not been joined to the transaction
     * @throws PessimisticLockException if pessimistic locking fails and the
     *         transaction is rolled back
     * @throws LockTimeoutException if pessimistic locking fails and only the
     *         statement is rolled back
     * @throws PersistenceException if the query execution exceeds the query
     *         timeout value set and the transaction is rolled back
     */
    public Object getSingleResult() {
        boolean rollbackOnException = true;
        // bug51411440: need to throw IllegalStateException if query
        // executed on closed em
        this.entityManager.verifyOpenWithSetRollbackOnly();
        try {
            setAsSQLReadQuery();
            propagateResultProperties();
            // This API is used to return non-List results, so no other validation is done.
            // It could be Cursor or other Collection or Map type.
            if (!(getDatabaseQueryInternal().isReadQuery())) {
                throw new IllegalStateException(ExceptionLocalization.buildMessage("incorrect_query_for_get_single_result"));
            }
            Object result = executeReadQuery();
            if (result instanceof List) {
                List results = (List) result;
                if (results.isEmpty()) {
                    rollbackOnException = false;
                    throwNoResultException(ExceptionLocalization.buildMessage("no_entities_retrieved_for_get_single_result", (Object[]) null));
                } else if (results.size() > 1) {
                    rollbackOnException = false;
                    throwNonUniqueResultException(ExceptionLocalization.buildMessage("too_many_results_for_get_single_result", (Object[]) null));
                }
                return results.get(0);
            } else {
                if (result == null) {
                    rollbackOnException = false;
                    throwNoResultException(ExceptionLocalization.buildMessage("no_entities_retrieved_for_get_single_result", (Object[]) null));
                }
                return result;
            }
        } catch (LockTimeoutException exception) {
            throw exception;
        } catch (PersistenceException exception) {
            if (rollbackOnException) {
                setRollbackOnly();
            }
            throw exception;
        } catch (IllegalStateException exception) {
            setRollbackOnly();
            throw exception;
        } catch (RuntimeException exception) {
            setRollbackOnly();
            throw new PersistenceException(exception);
        }
    }

    /**
     * Internal method to add the parameters values to the query prior to
     * execution. Returns a list of parameter values in the order the parameters
     * are defined for the databaseQuery.
     */
    protected List<Object> processParameters() {
        DatabaseQuery query = getDatabaseQueryInternal();
        List arguments = query.getArguments();
        if (arguments.isEmpty()) {
            // This occurs for native queries, as the query does not know of its arguments.
            // This may have issues, it is better if the query set its arguments
            // when parsing the SQL.

            arguments = new ArrayList<String>(this.parameterValues.keySet());
            query.setArguments(arguments);
        }
        // now create parameterValues in the same order as the argument list
        int size = arguments.size();
        List<Object> parameterValues = new ArrayList<Object>(size);
        for (int index = 0; index < size; index++) {
            String name = (String) arguments.get(index);
            Object parameter = this.parameterValues.get(name);
            if ((parameter != null) || this.parameterValues.containsKey(name)) {
                parameterValues.add(parameter);
            } else if (query.hasNullableArguments() && query.getNullableArguments().contains(new DatabaseField(name))) {
                parameterValues.add(null);
            } else {
                // Error: missing actual parameter value
                throw new IllegalStateException(ExceptionLocalization.buildMessage("missing_parameter_value", new Object[] { name }));
            }
        }
        return parameterValues;
    }

    /**
     * Replace the cached query with the given query.
     */
    public void setDatabaseQuery(DatabaseQuery query) {
        databaseQuery = query;
    }

    /**
     * Set the position of the first result to retrieve.
     *
     * @param startPosition
     *            position of the first result, numbered from 0
     * @return the same query instance
     */
    public QueryImpl setFirstResult(int startPosition) {
        try {
            entityManager.verifyOpen();
            setFirstResultInternal(startPosition);
            return this;
        } catch (RuntimeException e) {
            setRollbackOnly();
            throw e;
        }
    }

    /**
     * @see javax.persistence.Query#getFirstResult()
     * @since Java Persistence API 2.0
     */
    public int getFirstResult() {
        entityManager.verifyOpenWithSetRollbackOnly();
        if (this.firstResultIndex == UNDEFINED) {
            return 0;
        }
        return this.firstResultIndex;
    }

    /**
     * Set the flush mode type to be used for the query execution.
     *
     * @param flushMode
     */
    public QueryImpl setFlushMode(FlushModeType flushMode) {
        try {
            entityManager.verifyOpen();
            if (flushMode == null) {
                getDatabaseQueryInternal().setFlushOnExecute(null);
            } else {
                cloneSharedQuery();
                getDatabaseQueryInternal().setFlushOnExecute(flushMode == FlushModeType.AUTO);
            }
            return this;
        } catch (RuntimeException e) {
            setRollbackOnly();
            throw e;
        }
    }

    /**
     * Set the position of the first result to retrieve.
     *
     * @param startPosition
     *            position of the first result, numbered from 0.
     */
    protected void setFirstResultInternal(int startPosition) {
        if (startPosition < 0) {
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("negative_start_position", (Object[]) null));
        }
        // bug 362804
        firstResultIndex = startPosition;
    }

    /**
     * Define the query arguments based on the procedure call.
     */
    protected static void applyArguments(StoredProcedureCall call, DatabaseQuery query) {
        if (call instanceof PLSQLStoredProcedureCall) {
            PLSQLStoredProcedureCall plsqlCall = (PLSQLStoredProcedureCall)call;
            for (int index = 0; index < plsqlCall.getArguments().size(); index++) {
                PLSQLargument argument = plsqlCall.getArguments().get(index);
                int type = argument.direction;
                if ((type == StoredProcedureCall.IN) || (type == StoredProcedureCall.INOUT)) {
                    if (call.hasOptionalArguments()) {
                        query.addArgument(argument.name, Object.class, call.getOptionalArguments().contains(new DatabaseField(argument.name)));
                    } else {
                        query.addArgument(argument.name);
                    }
                }
            }
        } else {
            for (int index = 0; index < call.getParameters().size(); index++) {
                int type = call.getParameterTypes().get(index);
                if ((type == StoredProcedureCall.IN) || (type == StoredProcedureCall.INOUT)) {
                    Object value = call.getParameters().get(index);
                    DatabaseField parameter = null;
                    if (value instanceof Object[]) {
                        parameter = (DatabaseField) ((Object[])value)[0];
                    } else {
                        parameter = (DatabaseField)call.getParameters().get(index);
                    }
                    if (call.hasOptionalArguments()) {
                        query.addArgument(parameter.getName(), Object.class, call.getOptionalArguments().contains(parameter));
                    } else {
                        query.addArgument(parameter.getName());
                    }
                }
            }
        }
    }

    /**
     * Set implementation-specific hints.
     *
     * @param hints
     *            a list of hints to be applied to the query
     * @param query
     *            the query to apply the hints to
     */
    protected static DatabaseQuery applyHints(Map<String, Object> hints, DatabaseQuery query, ClassLoader classLoader, AbstractSession session) {
        return QueryHintsHandler.apply(hints, query, classLoader, session);
    }


    /**
     * Return the identifier of this parameter.  This will be the name if it is set, else it will be the position
     * @param param
     * @return
     */
    public static String getParameterId(Parameter param){
        Integer id= param.getPosition();
        if (id == null ){
            return String.valueOf(((ParameterExpressionImpl)param).getInternalName());
        }
        return String.valueOf(id);
    }

    /**
     * Return a boolean indicating whether a value has been bound to the
     * parameter.
     *
     * @param param
     *            parameter object
     * @return boolean indicating whether parameter has been bound
     */
    public boolean isBound(Parameter<?> param) {
        entityManager.verifyOpenWithSetRollbackOnly();
        if (param == null)
            return false;
        return this.parameterValues.containsKey(getParameterId(param));
    }

    /**
     * Spec. 3.5.2: "FlushMode.AUTO is set on the Query object, or if the flush
     * mode setting for the persistence context is AUTO (the default) and a
     * flush mode setting has not been specified for the Query object, the
     * persistence provider is responsible for ensuring that all updates to the
     * state of all entities in the persistence context which could potentially
     * affect the result of the query are visible to the processing of the
     * query."
     */
    protected boolean isFlushModeAUTO() {
        if (getDatabaseQueryInternal().getFlushOnExecute() != null) {
            return getDatabaseQueryInternal().getFlushOnExecute().booleanValue();
        } else {
            return entityManager.isFlushModeAUTO();
        }
    }

    /**
     * Set an implementation-specific hint. If the hint name is not recognized,
     * it is silently ignored.
     *
     * @throws IllegalArgumentException
     *             if the second argument is not valid for the implementation.
     */
    protected void setHintInternal(String hintName, Object value) {
        cloneSharedQuery();
        ClassLoader loader = getEntityManager().getAbstractSession().getLoader();
        DatabaseQuery hintQuery = QueryHintsHandler.apply(hintName, value, getDatabaseQueryInternal(), loader, (AbstractSession) getActiveSession());
        if (hintQuery != null) {
            setDatabaseQuery(hintQuery);
        }
    }

    /**
     * Set the lock mode type to be used for the query execution.
     *
     * @param lockMode
     * @throws IllegalStateException
     *             if not a Java Persistence query language SELECT query
     */
    public QueryImpl setLockMode(LockModeType lockMode) {
        try {
            entityManager.verifyOpen();

            if (!getDatabaseQueryInternal().isObjectLevelReadQuery()) {
                throw new IllegalStateException(ExceptionLocalization.buildMessage("invalid_lock_query", (Object[]) null));
            }

            this.lockMode = lockMode;
            return this;
        } catch (RuntimeException e) {
            setRollbackOnly();
            throw e;
        }
    }

    /**
     * If the query was from the jpql parse cache it must be cloned before being
     * modified.
     */
    protected void cloneSharedQuery() {
        DatabaseQuery query = getDatabaseQueryInternal();
        if (this.isShared) {
            // Clone to allow setting of hints or other properties without
            // corrupting original query.
            query = (DatabaseQuery) databaseQuery.clone();
            setDatabaseQuery(query);
            this.isShared = false;
        }
    }

    /**
     * Convert the given object to the class represented by the given temporal
     * type.
     *
     * @return an object representing the given TemporalType.
     */
    protected Object convertTemporalType(Object value, TemporalType type) {
        ConversionManager conversionManager = ((org.eclipse.persistence.internal.sessions.AbstractSession) getEntityManager().getActiveSession()).getDatasourcePlatform().getConversionManager();
        if (type == TemporalType.TIME) {
            return conversionManager.convertObject(value, ClassConstants.TIME);
        } else if (type == TemporalType.TIMESTAMP) {
            return conversionManager.convertObject(value, ClassConstants.TIMESTAMP);
        } else if (type == TemporalType.DATE) {
            return conversionManager.convertObject(value, ClassConstants.SQLDATE);
        }
        return value;
    }

    /**
     * Set the maximum number of results to retrieve.
     *
     * @param maxResult
     * @return the same query instance
     */
    public QueryImpl setMaxResults(int maxResult) {
        try {
            entityManager.verifyOpen();
            setMaxResultsInternal(maxResult);
            return this;
        } catch (RuntimeException e) {
            setRollbackOnly();
            throw e;
        }
    }

    /**
     * @see javax.persistence.Query#getMaxResults()
     * @since Java Persistence API 2.0
     */
    public int getMaxResults() {
        entityManager.verifyOpenWithSetRollbackOnly();
        if (this.maxResults == UNDEFINED) {
            return Integer.MAX_VALUE;
        }
        return this.maxResults;
    }

    /**
     * @see javax.persistence.Query#getMaxResults()
     * @since Java Persistence API 2.0
     */
    public int getMaxResultsInternal() {
        return this.maxResults;
    }

    /**
     * Set the maximum number of results to retrieve.
     *
     * @param maxResult
     */
    public void setMaxResultsInternal(int maxResult) {
        if (maxResult < 0) {
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("negative_max_result", (Object[]) null));
        }
        if (maxResult == Integer.MAX_VALUE) {
            this.maxResults = UNDEFINED;
        } else {
            this.maxResults = maxResult;
        }
    }

    /**
     * Configure the firstResult, maxRows and lock mode in the EclipseLink
     * ReadQuery.
     */
    protected void propagateResultProperties() {
        DatabaseQuery databaseQuery = getDatabaseQueryInternal();
        if (databaseQuery.isReadQuery()) {
            ReadQuery readQuery = (ReadQuery) databaseQuery;
            if (maxResults >= 0) {
                cloneSharedQuery();
                readQuery = (ReadQuery) getDatabaseQueryInternal();
                int maxRows = maxResults + ((firstResultIndex >= 0) ? firstResultIndex : 0);
                readQuery.setMaxRows(maxRows);
            }
            if (firstResultIndex > UNDEFINED) {
                cloneSharedQuery();
                readQuery = (ReadQuery) getDatabaseQueryInternal();
                readQuery.setFirstResult(firstResultIndex);
            }
        }
    }

    /**
     * Bind an argument to a positional parameter.
     *
     * @param position
     * @param value
     */
    protected void setParameterInternal(int position, Object value) {
        setParameterInternal(String.valueOf(position), value, true);
    }

    /**
     * Bind an argument to a named or indexed parameter.
     *
     * @param name
     *            the parameter name
     * @param value
     *            to bind
     * @param isIndex
     *            defines if index or named
     */
    protected void setParameterInternal(String name, Object value, boolean isIndex) {
        DatabaseQuery query = getDatabaseQueryInternal();
        if (query.getQueryMechanism().isJPQLCallQueryMechanism()) { // only non native queries
            int index = query.getArguments().indexOf(name);
            if (index == -1) {
                if (isIndex) {
                    throw new IllegalArgumentException(ExceptionLocalization.buildMessage("ejb30-wrong-argument-index", new Object[] { name, query.getEJBQLString() }));
                } else {
                    throw new IllegalArgumentException(ExceptionLocalization.buildMessage("ejb30-wrong-argument-name", new Object[] { name, query.getEJBQLString() }));
                }
            }
            Class type = query.getArgumentTypes().get(index);
            if (!isValidActualParameter(value, type)) {
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage("ejb30-incorrect-parameter-type", new Object[] { name, value.getClass(), query.getArgumentTypes().get(index), query.getEJBQLString() }));
            }
        } else {
            // native queries start a 1 not 0.
            if (isIndex && name.equals("0")) {
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage("ejb30-wrong-argument-index", new Object[] { name, query.getSQLString() }));
            }
        }
        this.parameterValues.put(name, value);
    }

    protected boolean isValidActualParameter(Object value, Class parameterType) {
        if (value == null) {
            return true;
        } else {
            return BasicTypeHelperImpl.getInstance().isAssignableFrom(parameterType, value.getClass());
        }
    }

    protected Session getActiveSession() {
        DatabaseQuery query = getDatabaseQueryInternal();
        // PERF: If read-only query, avoid creating unit of work and JTA transaction.
        if (query.isObjectLevelReadQuery() && ((ObjectLevelReadQuery) query).isReadOnly()) {
            return this.entityManager.getReadOnlySession();
        }
        return this.entityManager.getActiveSession();
    }

    protected void performPreQueryFlush() {
        if (this.entityManager.shouldFlushBeforeQuery()) {
            this.entityManager.flush();
        }
    }

    protected void setRollbackOnly() {
        entityManager.setRollbackOnly();
    }

    protected void throwNoResultException(String message) {
        throw new NoResultException(message);
    }

    protected void throwNonUniqueResultException(String message) {
        throw new NonUniqueResultException(message);
    }

    /**
     * @see Query#getFlushMode()
     * @since Java Persistence 2.0
     */
    public FlushModeType getFlushMode() {
        try {
            entityManager.verifyOpen();
            Boolean flushOnExecute = getDatabaseQueryInternal().getFlushOnExecute();
            if ((flushOnExecute == null) || flushOnExecute)
                return FlushModeType.AUTO;
            return FlushModeType.COMMIT;
        } catch (RuntimeException e) {
            setRollbackOnly();
            throw e;
        }
    }

    /**
     * @see Query#getHints()
     * @since Java Persistence 2.0
     */
    public Map<String, Object> getHints() {
        entityManager.verifyOpenWithSetRollbackOnly();
        return (Map<String, Object>) getDatabaseQueryInternal().getProperty(QueryHintsHandler.QUERY_HINT_PROPERTY);
    }

    /**
     * @see Query#getParameter(String, Class)
     * @since Java Persistence 2.0
     */
    public <T> Parameter<T> getParameter(String name, Class<T> type) {
        //don't rollback transaction on error
        entityManager.verifyOpen();
        Parameter param = getInternalParameters().get(name);
        if (param == null) {
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("NO_PARAMETER_WITH_NAME", new Object[] { name, this.databaseQuery }));
        } else if (param.getParameterType() != null && type != null && !type.isAssignableFrom(param.getParameterType())){
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("INCORRECT_PARAMETER_TYPE", new Object[] { name, type }));
        }
        return param;
    }

    /**
     * @see Query#getParameter(int, Class)
     * @since Java Persistence 2.0
     */
    public <T> Parameter<T> getParameter(int position, Class<T> type) {
        //don't rollback transaction on error
        entityManager.verifyOpen();
        Parameter param = getInternalParameters().get(String.valueOf(position));
        if (param == null) {
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("NO_PARAMETER_WITH_INDEX", new Object[] { position, this.databaseQuery }));
        } else if (param.getParameterType() != null && type != null && !type.isAssignableFrom(param.getParameterType())){
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("INCORRECT_PARAMETER_TYPE", new Object[] { position, type }));
        }
        return param;
    }

    /**
     * @see Query#getParameter(String, Class)
     * @since Java Persistence 2.0
     */
    public Parameter<?> getParameter(String name) {
        //don't rollback transaction on error
        entityManager.verifyOpen();
        Parameter param = getInternalParameters().get(name);
        if (param == null) {
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("NO_PARAMETER_WITH_NAME", new Object[] { name, this.databaseQuery }));
        }
        return param;
    }

    /**
     * @see Query#getParameter(int, Class)
     * @since Java Persistence 2.0
     */
    public Parameter<?> getParameter(int position) {
        //don't rollback transaction on error
        entityManager.verifyOpen();
        Parameter param = getInternalParameters().get(String.valueOf(position));
        if (param == null) {
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("NO_PARAMETER_WITH_INDEX", new Object[] { position, this.databaseQuery }));
        }
        return param;
    }

    /**
     * @see Query#getParameterValue(Parameter)
     * @since Java Persistence 2.0
     */
    public <T> T getParameterValue(Parameter<T> param) {
        if (param == null)
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("PARAMETER_NILL_NOT_FOUND"));

        ParameterExpressionImpl<T> parameter = (ParameterExpressionImpl<T>) this.getInternalParameters().get(getParameterId(param));
        if (parameter == null || !parameter.getParameterType().equals(param.getParameterType())) {
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("NO_PARAMETER_WITH_NAME", new Object[] { param.toString(), this.databaseQuery }));
        }

        return (T) this.getParameterValue(getParameterId(param));
    }

    /**
     * Return the value bound to the named parameter.
     *
     * @param name
     * @return parameter value
     * @throws IllegalStateException
     *             if the parameter has not been been bound
     */
    public Object getParameterValue(String name) {
        entityManager.verifyOpen();//don't rollback transaction
        if (!getInternalParameters().containsKey(name)) {
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("NO_PARAMETER_WITH_NAME", new Object[] { name, this.databaseQuery }));
        }
        if (!this.parameterValues.containsKey(name)) { // must check for
            // key. get() would
            // return negative
            // for value == null.
            throw new IllegalStateException(ExceptionLocalization.buildMessage("NO_VALUE_BOUND", new Object[] { name }));
        }
        return this.parameterValues.get(name);
    }

    /**
     * Return the value bound to the positional parameter.
     *
     * @param position
     * @return parameter value
     * @throws IllegalStateException
     *             if the parameter has not been been bound
     */
    public Object getParameterValue(int position) {
        entityManager.verifyOpen();//don't rollback transaction
        String param = String.valueOf(position);

        if (!getInternalParameters().containsKey(param)) {
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("position_param_not_found", new Object[] { position }));
        }

        if (!this.parameterValues.containsKey(param)) {
            throw new IllegalStateException(ExceptionLocalization.buildMessage("position_bound_param_not_found", new Object[] { position }));
        }

        return this.parameterValues.get(param);
    }

    /**
     * @see Query#getParameters()
     * @since Java Persistence 2.0
     */
    public Set<Parameter<?>> getParameters() {
        entityManager.verifyOpen();//don't rollback transaction
        return new HashSet(getInternalParameters().values());
    }

    /**
     * @since Java Persistence 2.0
     */
    public Set<String> getSupportedHints() {
        return QueryHintsHandler.getSupportedHints();
    }

    /**
     * Unwrap the query into the JPA implementation classes/interfaces or the
     * underlying native EclipseLink query.
     *
     * @see Query#unwrap(Class)
     * @since Java Persistence 2.0
     */
    public <T> T unwrap(Class<T> cls) {
        if (cls.isAssignableFrom(this.getClass())) {
            // unwraps any proxy to Query, JPAQuery or EJBQueryImpl
            return (T) this;
        }
        if (cls.isAssignableFrom(getDatabaseQueryInternal().getClass())) {
            return (T) getDatabaseQueryInternal();
        }

        throw new PersistenceException(ExceptionLocalization.buildMessage("unable_to_unwrap_jpa", new String[]{Query.class.getName(), cls.getName()}));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + String.valueOf(this.databaseQuery) + ")";
    }
}

