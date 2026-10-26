/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation. All rights reserved.
 * Copyright (c) 2012, 2026 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2022 IBM Corporation. All rights reserved.
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

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.FlushModeType;
import jakarta.persistence.LockModeType;
import jakarta.persistence.LockTimeoutException;
import jakarta.persistence.NoResultException;
import jakarta.persistence.NonUniqueResultException;
import jakarta.persistence.Parameter;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.PessimisticLockException;
import jakarta.persistence.Query;
import jakarta.persistence.QueryFlushMode;
import jakarta.persistence.QueryTimeoutException;
import jakarta.persistence.TemporalType;
import jakarta.persistence.TransactionRequiredException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.internal.core.helper.CoreClassConstants;
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
import org.eclipse.persistence.queries.ResultSetMappingQuery;
import org.eclipse.persistence.queries.SQLResultSetMapping;
import org.eclipse.persistence.queries.SQLCall;
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

import static jakarta.persistence.QueryFlushMode.DEFAULT;
import static jakarta.persistence.QueryFlushMode.FLUSH;
import static jakarta.persistence.QueryFlushMode.NO_FLUSH;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.eclipse.persistence.internal.helper.CollectionUtils.isEmpty;
import static org.eclipse.persistence.internal.localization.ExceptionLocalization.buildMessage;

/**
 * Concrete JPA query class. The JPA query wraps a DatabaseQuery which is
 * executed.
 */
public class QueryImpl {

    public static final int UNDEFINED = -1;

    /**
     * Wrapped native query. The query may be {@link #isShared}
     */
    protected DatabaseQuery databaseQuery;

    protected EntityManagerImpl entityManager;
    protected final QueryExecutionContext executionContext;
    protected String queryName;

    protected Map<String, Object> parameterValues = new HashMap<>();
    protected Map<String, Class<?>> parameterTypes = new HashMap<>();
    protected Map<String, AttributeConverter<Object, Object>> parameterConverters = new HashMap<>();
    protected Map<String, Parameter<?>> parameters;

    protected int firstResultIndex = UNDEFINED;
    protected int maxResults = UNDEFINED;

    protected LockModeType lockMode;

    /**
     * Stores if the wrapped query is shared, and requires cloning before being
     * changed.
     */
    protected boolean isShared;


    /**
     * Create an EJBQueryImpl with a DatabaseQuery.
     */
    public QueryImpl(DatabaseQuery query, EntityManagerImpl entityManager) {
        this(entityManager);
        this.databaseQuery = query;
    }

    /**
     * Base constructor for EJBQueryImpl. Initializes basic variables.
     */
    protected QueryImpl(EntityManagerImpl entityManager) {
        this(new EntityManagerQueryContext(entityManager));
    }

    QueryImpl(DatabaseQuery query, QueryExecutionContext executionContext) {
        this(executionContext);
        this.databaseQuery = query;
    }

    QueryImpl(QueryExecutionContext executionContext) {
        this.executionContext = executionContext;
        if (executionContext instanceof EntityManagerQueryContext context) {
            this.entityManager = context.getEntityManager();
        }
        this.isShared = true;
    }

    /**
     * This method should be called to close any left over open connection to
     * the database (if there is one).
     */
    public void close() {
        releaseResources();
    }

    /** Internal disposal, also usable after the owner has been closed. */
    public void releaseResources() {
        // Ordinary list results do not retain JDBC resources.
        executionContext.unregisterQuery(this);
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
        prepareNamedResultSetMappingLocks();
        List<Object> parameterValues = processParameters();
        if (Boolean.TRUE.equals(getDatabaseQueryInternal().getProperty(ResultSetMappingTranslator.MAPPING_LOCK_PROPERTY))) {
            executionContext.requireTransaction();
        }
        if (executionContext.isEntityAgent()) {
            final LockModeType executionLockMode = lockMode;
            DatabaseQuery executable = getDatabaseQueryInternal();
            if (executable.isReportQuery() || executable instanceof ResultSetMappingQuery) {
                // Owner-specific materialization state belongs only to this execution,
                // never to a query later registered as shared named metadata.
                executable = (DatabaseQuery) executable.clone();
                executable.setProperty(ResultSetMappingTranslator.EXECUTION_CONTEXT_PROPERTY, executionContext);
            }
            if (executable.isReportQuery()) {
                executable.setProperty(
                        org.eclipse.persistence.queries.ReportQueryResult.CONSTRUCTOR_ARGUMENT_PREPARER,
                        (java.util.function.UnaryOperator<Object>) value -> {
                            Object prepared = executionContext.prepareResult(value);
                            if (prepared != null && executionContext.getSession().getDescriptor(prepared) != null
                                    && executionLockMode != null && executionLockMode != LockModeType.NONE) {
                                executionContext.lockResult(prepared, executionLockMode);
                            }
                            return prepared;
                        });
            }
            return executionContext.executeQuery(executable, parameterValues, executionLockMode);
        }
        // TODO: the following performFlush() call is a "temporary" workaround for
        // bug 4752493:
        // CTS: INMEMORY QUERYING IN EJBQUERY BROKEN DUE TO CHANGE TO USE
        // REPORTQUERY.
        // Ideally we should only flush in case the selectionExpression can't be
        // conformed in memory.
        //
        // There are two alternative ways to implement that:
        //
        // 1. Try running the query with conformInUOW flag first - if it fails
        // with
        // QueryException.cannotConformExpression then flush and run the query
        // again -
        // now without conforming.
        //
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
            this.executionContext.requireTransaction();

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
                if (this.executionContext.hasActiveTransaction()) {
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

            // Materialization owners are execution state, not reusable named metadata.
            if (query instanceof ResultSetMappingQuery) {
                query = (DatabaseQuery) query.clone();
                query.setProperty(ResultSetMappingTranslator.EXECUTION_CONTEXT_PROPERTY, executionContext);
            }
            Object result = session.executeQuery(query, parameterValues);
            if (query instanceof ObjectLevelReadQuery objectQuery && objectQuery.getLoadGroup() != null) {
                // A primary-key cache hit may return before the core query's load phase.
                // Apply this query's graph to the managed result in the same session.
                ((AbstractSession) session).load(result, objectQuery.getLoadGroup(), objectQuery.getDescriptor(), false);
            }
            return result;
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

    /** Retain locks declared by named SQL mappings on both handler implementations. */
    private void prepareNamedResultSetMappingLocks() {
        DatabaseQuery query = getDatabaseQueryInternal();
        if (!(query instanceof ResultSetMappingQuery mapped)
                || Boolean.TRUE.equals(query.getProperty(ResultSetMappingTranslator.MAPPING_LOCK_PROPERTY))) {
            return;
        }
        List<SQLResultSetMapping> mappings = new ArrayList<>();
        if (mapped.getSQLResultSetMappingNames().isEmpty()) {
            mappings.addAll(mapped.getSQLResultSetMappings());
        } else {
            for (String name : mapped.getSQLResultSetMappingNames()) {
                mappings.add(executionContext.getSession().getProject().getSQLResultSetMapping(name));
            }
        }
        if (mappings.stream().noneMatch(ResultSetMappingTranslator::mappingRequiresTransaction)) {
            return;
        }
        executionContext.requireTransaction();
        cloneSharedQuery();
        List<SQLResultSetMapping> translated = new ArrayList<>(mappings.size());
        for (SQLResultSetMapping mapping : mappings) {
            translated.add(ResultSetMappingTranslator.mappingRequiresTransaction(mapping)
                    ? ResultSetMappingTranslator.toEclipseLinkMapping(
                            ResultSetMappingTranslator.toJakartaMapping(mapping, executionContext.getSession()), executionContext)
                    : mapping);
        }
        ((ResultSetMappingQuery) getDatabaseQueryInternal()).setSQLResultSetMappings(translated);
        getDatabaseQueryInternal().setProperty(ResultSetMappingTranslator.MAPPING_LOCK_PROPERTY, Boolean.TRUE);
    }

    /**
     * Execute an update or delete statement.
     *
     * @return the number of entities updated or deleted
     */
    public int executeUpdate() {
        // bug51411440: need to throw IllegalStateException if query
        // executed on closed em
        this.executionContext.verifyOpenWithSetRollbackOnly();
        try {
            setAsSQLModifyQuery();
            // bug:4294241, only allow modify queries - UpdateAllQuery preferred
            if (!(getDatabaseQueryInternal() instanceof ModifyQuery)) {
                throw new IllegalStateException(ExceptionLocalization.buildMessage("incorrect_query_for_execute_update"));
            }

            // need to throw TransactionRequiredException if there is no active
            // transaction
            executionContext.requireTransaction();

            // fix for bug:4288845, did not add the parameters to the query
            List<Object> parameterValues = processParameters();
            if (isFlushModeAUTO()) {
                performPreQueryFlush();
            }
            Integer changedRows = (Integer) executionContext.executeQuery(databaseQuery, parameterValues, null);
            return changedRows;
        } catch (PersistenceException | IllegalStateException exception) {
            setRollbackOnly();
            throw exception;
        } catch (RuntimeException exception) {
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
        executionContext.verifyOpen();
        cloneSharedQuery();
        return getDatabaseQueryInternal();
    }

    /**
     * INTERNAL: Return the cached database query for this EJBQueryImpl. If the
     * query is a named query and it has not yet been looked up, the query will
     * be looked up and stored as the cached query.
     */
    public DatabaseQuery getDatabaseQueryInternal() {
        executionContext.verifyOpen();
        if ((this.queryName != null) && (this.databaseQuery == null)) {
            // Always ask for the query from the active session. Table per
            // tenant multitenant entity queries may be isolated per EM meaning
            // those queries will not have been initialized (and made available)
            // from their parent session.
            this.databaseQuery = this.executionContext.getSession().getQuery(this.queryName);
            // need error checking and appropriate exception for non-existing query
            if (this.databaseQuery != null) {
                if (!this.databaseQuery.isPrepared()) {
                    // prepare the query before cloning, this ensures we do not
                    // have to continually prepare on each usage
                    try {
                        this.databaseQuery.checkPrepare(this.executionContext.getSession(), new DatabaseRecord());
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
        executionContext.verifyOpen();
        if (entityManager == null) {
            throw new IllegalStateException("This query belongs to an EntityAgent");
        }
        return entityManager;
    }

    /**
     * Return the internal map of parameters.
     */
    protected Map<String, Parameter<?>> getInternalParameters() {
        if (parameters == null) {
            parameters = new HashMap<>();
            DatabaseQuery query = getDatabaseQueryInternal(); // Retrieve named query
            Object criteriaParameters = query.getProperty(ParameterExpressionImpl.QUERY_PARAMETERS_PROPERTY);
            if (criteriaParameters instanceof Map<?, ?> metadata) {
                metadata.forEach((name, parameter) -> parameters.put((String) name, (Parameter<?>) parameter));
                return parameters;
            }

            int count = 0;
            if (!isEmpty(query.getArguments())) {
                boolean checkParameterType = query.getArgumentParameterTypes().size() == query.getArguments().size();
                for (String argName : query.getArguments()) {
                    Parameter<?> param = null;
                    ParameterType type = null;
                    if (checkParameterType){
                        type = query.getArgumentParameterTypes().get(count);
                    }

                    if (type == ParameterType.POSITIONAL){
                        Integer position = Integer.parseInt(argName);
                        param = new ParameterExpressionImpl<>(null, query.getArgumentTypes().get(count), position);
                    } else {
                        param = new ParameterExpressionImpl<>(null, query.getArgumentTypes().get(count), argName);
                    }

                    parameters.put(argName, param);
                    ++count;
                }
            }

        }

        return parameters;
    }

    /** Validate the complete positional bind before changing any existing argument values. */
    protected void validatePositionalArgumentCount(int actualCount) {
        DatabaseQuery query = getDatabaseQueryInternal();
        Set<String> positions = new HashSet<>();
        if (query.getEJBQLString() == null && query.getDatasourceCall() instanceof SQLCall sqlCall) {
            SQLCall translated = (SQLCall) sqlCall.clone();
            translated.setQuery(query);
            if (translated.getParameters().isEmpty()) {
                translated.translateCustomQuery();
            }
            for (Object value : translated.getParameters()) {
                if (value instanceof DatabaseField field && field.getName().matches("[1-9][0-9]*")) {
                    positions.add(field.getName());
                }
            }
        } else {
            for (Parameter<?> parameter : getInternalParameters().values()) {
                if (parameter.getPosition() != null) {
                    positions.add(parameter.getPosition().toString());
                }
            }
        }
        if (positions.size() != actualCount) {
            throw new IllegalArgumentException("Expected " + positions.size()
                    + " positional arguments, but received " + actualCount);
        }
    }

    /**
     * Get the current lock mode for the query.
     *
     * @return lock mode
     * @throws IllegalStateException
     *             if not a Java Persistence query language SELECT query
     */
    public LockModeType getLockMode() {
        executionContext.verifyOpen();

        if (!getDatabaseQueryInternal().isObjectLevelReadQuery()) {
            throw new IllegalStateException(ExceptionLocalization.buildMessage("invalid_lock_query", null));
        }

        return this.lockMode == null ? LockModeType.NONE : this.lockMode;
    }

    /**
     * Execute the query and return the query results as a List.
     *
     * @return a list of the results
     */
    public List<?> getResultList() {
        // bug51411440: need to throw IllegalStateException if query
        // executed on closed em
        this.executionContext.verifyOpenWithSetRollbackOnly();
        try {
            setAsSQLReadQuery();
            propagateResultProperties();
            // bug:4297903, check container policy class and throw exception if
            // its not the right type
            DatabaseQuery query = getDatabaseQueryInternal();
            if (query.isReadAllQuery()) {
                Class<?> containerClass = ((ReadAllQuery) query).getContainerPolicy().getContainerClass();
                if (!Helper.classImplementsInterface(containerClass, CoreClassConstants.List_Class)) {
                    throw QueryException.invalidContainerClass(containerClass, CoreClassConstants.List_Class);
                }
            } else if (query.isReadObjectQuery()) {
                List<Object> resultList = new ArrayList<>();
                Object result = executeReadQuery();
                if (result != null) {
                    resultList.add(result);
                }
                return resultList;
            } else if (!query.isReadQuery()) {
                throw new IllegalStateException(ExceptionLocalization.buildMessage("incorrect_query_for_get_result_list"));
            }
            return (List) executeReadQuery();
        } catch (LockTimeoutException | QueryTimeoutException exception) {
            throw exception;
        } catch (PersistenceException | IllegalStateException exception) {
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
     * @return the result, or {@code null} if there is no result
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
    public Object getSingleResultOrNull() {
        return getSingleResult(false);
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
        return getSingleResult(true);
    }

    private Object getSingleResult(boolean failOnEmpty) {
        boolean rollbackOnException = true;
        // bug51411440: need to throw IllegalStateException if query
        // executed on closed em
        this.executionContext.verifyOpenWithSetRollbackOnly();
        try {
            setAsSQLReadQuery();
            propagateResultProperties();
            // This API is used to return non-List results, so no other validation is done.
            // It could be Cursor or other Collection or Map type.
            if (!(getDatabaseQueryInternal().isReadQuery())) {
                throw new IllegalStateException(ExceptionLocalization.buildMessage("incorrect_query_for_get_single_result"));
            }
            Object result = executeReadQuery();
            if (result instanceof List<?> results) {
                if (results.isEmpty()) {
                    if (failOnEmpty) {
                        rollbackOnException = false;
                        throwNoResultException(ExceptionLocalization.buildMessage("no_entities_retrieved_for_get_single_result",
                                                                                  null));
                    } else {
                        return null;
                    }
                } else if (results.size() > 1) {
                    rollbackOnException = false;
                    throwNonUniqueResultException(ExceptionLocalization.buildMessage("too_many_results_for_get_single_result", null));
                }
                return results.get(0);
            } else {
                if (failOnEmpty && result == null) {
                    rollbackOnException = false;
                    throwNoResultException(ExceptionLocalization.buildMessage("no_entities_retrieved_for_get_single_result", null));
                }
                return result;
            }
        } catch (LockTimeoutException | QueryTimeoutException exception) {
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

        List<String> arguments = query.getArguments();
        if (arguments.isEmpty()) {
            // This occurs for native queries, as the query does not know of its arguments.
            // This may have issues, it is better if the query set its arguments
            // when parsing the SQL.

            arguments = new ArrayList<>(parameterValues.keySet());
            query.setArguments(arguments);
        }

        // Now create parameterValues in the same order as the argument list
        int size = arguments.size();

        List<Object> processedParameterValues = new ArrayList<>(size);

        for (int index = 0; index < size; index++) {
            String parameterName = arguments.get(index);
            Object parameterValue = parameterValues.get(parameterName);

            if (parameterConverters.containsKey(parameterName)) {
                parameterValue = parameterConverters.get(parameterName).convertToDatabaseColumn(parameterValue);
            }

            if (parameterValue != null || parameterValues.containsKey(parameterName)) {

                Class<?> parameterType = parameterTypes.get(parameterName);
                boolean expressionArgument = query.getQueryMechanism().isExpressionQueryMechanism();
                if (parameterValue == null && parameterType != null
                        && (!expressionArgument || parameterConverters.containsKey(parameterName))) {
                    DatabaseField field = new DatabaseField(parameterName);
                    field.setType(parameterType);
                    parameterValue = field;
                }

                if (expressionArgument && parameterConverters.containsKey(parameterName)) {
                    parameterValue = new org.eclipse.persistence.internal.expressions.ParameterExpression.ConvertedValue(parameterValue);
                }
                processedParameterValues.add(parameterValue);
            } else if (query.hasNullableArguments() && query.getNullableArguments().contains(new DatabaseField(parameterName))) {
                processedParameterValues.add(null);
            } else {
                // Error: missing actual parameter value
                throw new IllegalStateException(ExceptionLocalization.buildMessage("missing_parameter_value", new Object[] { parameterName }));
            }
        }

        return processedParameterValues;
    }

    /**
     * Replace the cached query with the given query.
     */
    public void setDatabaseQuery(DatabaseQuery query) {
        executionContext.verifyOpen();
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
            executionContext.verifyOpen();
            setFirstResultInternal(startPosition);
            return this;
        } catch (RuntimeException e) {
            setRollbackOnly();
            throw e;
        }
    }

    /**
     * @see jakarta.persistence.Query#getFirstResult()
     * @since Java Persistence API 2.0
     */
    public int getFirstResult() {
        executionContext.verifyOpenWithSetRollbackOnly();
        if (this.firstResultIndex == UNDEFINED) {
            return 0;
        }
        return this.firstResultIndex;
    }

    public QueryFlushMode getQueryFlushMode() {
        try {
            executionContext.verifyOpen();
            return flushOnExecuteToQueryFlushMode(getDatabaseQueryInternal().getFlushOnExecute());
        } catch (RuntimeException e) {
            setRollbackOnly();
            throw e;
        }
    }

    public QueryImpl setQueryFlushMode(QueryFlushMode queryFlushMode) {
        try {
            executionContext.verifyOpen();
            cloneSharedQuery();
            getDatabaseQueryInternal().setFlushOnExecute(queryFlushModeToFlushOnExecute(queryFlushMode));
            return this;
        } catch (RuntimeException e) {
            setRollbackOnly();
            throw e;
        }
    }

    /**
     * @see Query#getFlushMode()
     * @since Java Persistence 2.0
     */
    @Deprecated(since = "6.0")
    public FlushModeType getFlushMode() {
        try {
            executionContext.verifyOpen();
            Boolean flushOnExecute = getDatabaseQueryInternal().getFlushOnExecute();
            if (flushOnExecute == null) {
                return executionContext.getFlushMode();
            }
            return flushOnExecute ? FlushModeType.AUTO : FlushModeType.COMMIT;
        } catch (RuntimeException e) {
            setRollbackOnly();
            throw e;
        }
    }

    @Deprecated(since = "6.0")
    public QueryImpl setFlushMode(FlushModeType flushMode) {
        return setQueryFlushMode(flushModeTypeToQueryFlushMode(flushMode));
    }

    public static Boolean queryFlushModeToFlushOnExecute(QueryFlushMode queryFlushMode) {
        return switch (queryFlushMode) {
            case null -> null;
            case DEFAULT -> null;
            case FLUSH -> TRUE;
            case NO_FLUSH -> FALSE;
        };
    }

    public static QueryFlushMode flushOnExecuteToQueryFlushMode(Boolean flushOnExecute) {
        if (flushOnExecute == null) {
            return DEFAULT;
        }

        return flushOnExecute ? FLUSH : NO_FLUSH;
    }

    public static QueryFlushMode flushModeTypeToQueryFlushMode(FlushModeType flushModeType) {
        return switch (flushModeType) {
            case null -> DEFAULT;
            case AUTO -> FLUSH;
            case COMMIT, EXPLICIT -> NO_FLUSH;
        };
    }

    /**
     * Set the position of the first result to retrieve.
     *
     * @param startPosition
     *            position of the first result, numbered from 0.
     */
    protected void setFirstResultInternal(int startPosition) {
        if (startPosition < 0) {
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("negative_start_position", null));
        }
        // bug 362804
        firstResultIndex = startPosition;
    }

    /**
     * Define the query arguments based on the procedure call.
     */
    protected static void applyArguments(StoredProcedureCall call, DatabaseQuery query) {
        if (call instanceof PLSQLStoredProcedureCall plsqlCall) {
            for (int index = 0; index < plsqlCall.getArguments().size(); index++) {
                PLSQLargument argument = plsqlCall.getArguments().get(index);
                org.eclipse.persistence.internal.databaseaccess.DatasourceCall.ParameterType type = argument.direction;
                if ((type == org.eclipse.persistence.internal.databaseaccess.DatasourceCall.ParameterType.IN)
                        || (type == org.eclipse.persistence.internal.databaseaccess.DatasourceCall.ParameterType.INOUT)) {
                    if (call.hasOptionalArguments()) {
                        query.addArgument(argument.name, Object.class, call.getOptionalArguments().contains(new DatabaseField(argument.name)));
                    } else {
                        query.addArgument(argument.name);
                    }
                }
            }
        } else {
            for (int index = 0; index < call.getParameters().size(); index++) {
                org.eclipse.persistence.internal.databaseaccess.DatasourceCall.ParameterType type = call.getParameterTypes().get(index);
                if ((type == org.eclipse.persistence.internal.databaseaccess.DatasourceCall.ParameterType.IN)
                        || (type == org.eclipse.persistence.internal.databaseaccess.DatasourceCall.ParameterType.INOUT)) {
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
     * Return the identifier of this parameter. This will be the name if it is set, else it will be the position
     */
    public static String getParameterId(Parameter param) {
        Integer id = param.getPosition();
        if (id == null) {
            return String.valueOf(((ParameterExpressionImpl<?>) param).getInternalName());
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
        executionContext.verifyOpenWithSetRollbackOnly();
        if (param == null) {
            return false;
        }

        return parameterValues.containsKey(getParameterId(param));
    }

    /**
     * Spec. 3.5.2: "QueryFlushMode.DEFAULT is set on the Query object, or if the flush
     * mode setting for the persistence context is DEFAULT (the default) and a
     * flush mode setting has not been specified for the Query object, the
     * persistence provider is responsible for ensuring that all updates to the
     * state of all entities in the persistence context which could potentially
     * affect the result of the query are visible to the processing of the
     * query."
     */
    protected boolean isFlushModeAUTO() {
        if (executionContext.isEntityAgent()) {
            return false;
        }
        if (getDatabaseQueryInternal().getFlushOnExecute() != null) {
            return getDatabaseQueryInternal().getFlushOnExecute();
        }

        return executionContext.getFlushMode() == FlushModeType.AUTO;
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
        ClassLoader loader = executionContext.getSession().getLoader();
        DatabaseQuery hintQuery = QueryHintsHandler.apply(hintName, value, getDatabaseQueryInternal(), loader, (AbstractSession) getActiveSession());
        if (hintQuery != null) {
            setDatabaseQuery(hintQuery);
        }
    }

    /**
     * Set the lock mode type to be used for the query execution.
     *
     * @throws IllegalStateException
     *             if not a Java Persistence query language SELECT query
     */
    public QueryImpl setLockMode(LockModeType lockMode) {
        try {
            executionContext.verifyOpen();

            if (!getDatabaseQueryInternal().isObjectLevelReadQuery()) {
                throw new IllegalStateException(ExceptionLocalization.buildMessage("invalid_lock_query", null));
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
        ConversionManager conversionManager = executionContext.getSession().getDatasourcePlatform().getConversionManager();
        if (type == TemporalType.TIME) {
            return conversionManager.convertObject(value, ClassConstants.TIME);
        }

        if (type == TemporalType.TIMESTAMP) {
            return conversionManager.convertObject(value, ClassConstants.TIMESTAMP);
        }

        if (type == TemporalType.DATE) {
            return conversionManager.convertObject(value, ClassConstants.SQLDATE);
        }

        return value;
    }

    /**
     * Set the maximum number of results to retrieve.
     *
     * @return the same query instance
     */
    public QueryImpl setMaxResults(int maxResult) {
        try {
            executionContext.verifyOpen();
            setMaxResultsInternal(maxResult);
            return this;
        } catch (RuntimeException e) {
            setRollbackOnly();
            throw e;
        }
    }

    /**
     * @see jakarta.persistence.Query#getMaxResults()
     * @since Java Persistence API 2.0
     */
    public int getMaxResults() {
        executionContext.verifyOpenWithSetRollbackOnly();
        if (maxResults == UNDEFINED) {
            return Integer.MAX_VALUE;
        }

        return maxResults;
    }

    /**
     * @see jakarta.persistence.Query#getMaxResults()
     * @since Java Persistence API 2.0
     */
    public int getMaxResultsInternal() {
        executionContext.verifyOpen();
        return maxResults;
    }

    /**
     * Set the maximum number of results to retrieve.
     *
     */
    public void setMaxResultsInternal(int maxResult) {
        if (maxResult < 0) {
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("negative_max_result", null));
        }

        if (maxResult == Integer.MAX_VALUE) {
            maxResults = UNDEFINED;
        } else {
            maxResults = maxResult;
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
     */
    protected void setParameterInternal(int position, Object value) {
        setParameterInternal(String.valueOf(position), value, true);
    }

    protected void setParameterInternal(String name, Object value, boolean isIndex) {
        setParameterInternal(name, value, null, isIndex);
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
    protected void setParameterInternal(String name, Object value, Class<?> typex, boolean isIndex) {
        if (name == null || name.isEmpty() || (isIndex && Integer.parseInt(name) < 1)) {
            throw new IllegalArgumentException("Invalid query parameter identifier");
        }
        DatabaseQuery query = getDatabaseQueryInternal();

        if (query.getQueryMechanism().isJPQLCallQueryMechanism()) { // only non native queries
            final List<String> queryArguments = query.getArguments();
            int index = queryArguments.indexOf(name);
            if (index == -1) {
                if (isIndex) {
                    throw new IllegalArgumentException(ExceptionLocalization.buildMessage("ejb30-wrong-argument-index", name, query.getEJBQLString()));
                }

                throw new IllegalArgumentException(ExceptionLocalization.buildMessage("ejb30-wrong-argument-name", name, query.getEJBQLString()));
            }

            do {
                Class<?> type = query.getArgumentTypes().get(index);
                if (!isValidActualParameter(value, type)) {
                    throw new IllegalArgumentException(ExceptionLocalization.buildMessage("ejb30-incorrect-parameter-type", name, value.getClass(), query.getArgumentTypes().get(index), query.getEJBQLString()));
                }

                int pos = queryArguments.subList(index + 1, queryArguments.size()).indexOf(name);
                index = pos < 0 ? -1 : (pos + index + 1);
            } while (index > - 1);
        } else {
            // native queries start a 1 not 0.
            if (isIndex && name.equals("0")) {
                throw new IllegalArgumentException(buildMessage("ejb30-wrong-argument-index", new Object[] { name, query.getSQLString() }));
            }
        }

        parameterValues.put(name, value);
        if (typex != null) {
            parameterTypes.put(name, typex);
        } else {
            parameterTypes.remove(name);
        }
    }

    protected boolean isValidActualParameter(Object value, Class<?> parameterType) {
        if (value == null) {
            return true;
        }

        return BasicTypeHelperImpl.getInstance().isAssignableFrom(parameterType, value.getClass());
    }

    protected Session getActiveSession() {
        return executionContext.getExecutionSession(getDatabaseQueryInternal());
    }

    protected void performPreQueryFlush() {
        if (executionContext.shouldFlush()) {
            executionContext.flush();
        }
    }

    protected void setRollbackOnly() {
        // Agent statement/optimistic failures may be caught and recovered from.
        // Its execution boundary decides whether the physical transaction failed.
        if (!executionContext.isEntityAgent()) {
            executionContext.setRollbackOnly();
        }
    }

    protected void throwNoResultException(String message) {
        throw new NoResultException(message);
    }

    protected void throwNonUniqueResultException(String message) {
        throw new NonUniqueResultException(message);
    }

    /**
     * @see Query#getHints()
     * @since Java Persistence 2.0
     */
    public Map<String, Object> getHints() {
        executionContext.verifyOpenWithSetRollbackOnly();
        Map<String, Object> hints = (Map<String, Object>) getDatabaseQueryInternal().getProperty(QueryHintsHandler.QUERY_HINT_PROPERTY);
        return hints == null ? new HashMap<>() : new HashMap<>(hints);
    }

    /**
     * @see Query#getParameter(String, Class)
     * @since Java Persistence 2.0
     */
    public <T> Parameter<T> getParameter(String name, Class<T> type) {
        //don't rollback transaction on error
        executionContext.verifyOpen();
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
        executionContext.verifyOpen();
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
        executionContext.verifyOpen();
        Parameter<?> param = getInternalParameters().get(name);
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
        executionContext.verifyOpen();
        Parameter<?> param = getInternalParameters().get(String.valueOf(position));
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
        executionContext.verifyOpen();
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
     * @return parameter value
     * @throws IllegalStateException
     *             if the parameter has not been been bound
     */
    public Object getParameterValue(String name) {
        executionContext.verifyOpen();//don't rollback transaction
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
     * @return parameter value
     * @throws IllegalStateException
     *             if the parameter has not been been bound
     */
    public Object getParameterValue(int position) {
        executionContext.verifyOpen();//don't rollback transaction
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
     * @since Jakarta Persistence 2.0
     */
    public Set<Parameter<?>> getParameters() {
        executionContext.verifyOpen();//don't rollback transaction
        return new HashSet<>(getInternalParameters().values());
    }

    /**
     * @since Jakarta Persistence 2.0
     */
    public Set<String> getSupportedHints() {
        executionContext.verifyOpen();
        return QueryHintsHandler.getSupportedHints();
    }

    /**
     * Unwrap the query into the Jakarta Persistence implementation classes/interfaces or the
     * underlying native EclipseLink query.
     *
     * @see Query#unwrap(Class)
     * @since Jakarta Persistence 2.0
     */
    public <T> T unwrap(Class<T> cls) {
        executionContext.verifyOpen();
        if (cls == null) {
            throw new IllegalArgumentException("Unwrap type must not be null");
        }
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
        return getClass().getSimpleName() + "(" + this.databaseQuery + ")";
    }
}

