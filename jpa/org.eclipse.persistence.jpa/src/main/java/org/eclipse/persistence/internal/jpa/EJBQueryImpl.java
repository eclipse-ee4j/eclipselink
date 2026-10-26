/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation. All rights reserved.
 * Copyright (c) 1998, 2026 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2025 IBM Corporation. All rights reserved.
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
//     08/11/2012-2.5 Guy Pelletier
//       - 393867: Named queries do not work when using EM level Table Per Tenant Multitenancy.
//     08/23/2023: Tomas Kraus
//       - New Jakarta Persistence 3.2 Features
package org.eclipse.persistence.internal.jpa;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.CacheRetrieveMode;
import jakarta.persistence.CacheStoreMode;
import jakarta.persistence.EntityGraph;
import jakarta.persistence.FlushModeType;
import jakarta.persistence.LockModeType;
import jakarta.persistence.LockTimeoutException;
import jakarta.persistence.Parameter;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.PessimisticLockScope;
import jakarta.persistence.QueryFlushMode;
import jakarta.persistence.Statement;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Timeout;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.metamodel.Type;
import jakarta.persistence.sql.ResultSetMapping;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionOperator;
import org.eclipse.persistence.internal.expressions.FunctionExpression;
import org.eclipse.persistence.internal.databaseaccess.DatasourcePlatform;
import org.eclipse.persistence.internal.databaseaccess.DatabaseCall;
import org.eclipse.persistence.internal.helper.GenericTypes;
import org.eclipse.persistence.internal.jpa.querydef.ParameterExpressionImpl;
import org.eclipse.persistence.internal.expressions.SQLSelectStatement;
import org.eclipse.persistence.internal.queries.ReportItem;
import org.eclipse.persistence.mappings.converters.ConverterClass;
import org.eclipse.persistence.internal.localization.ExceptionLocalization;
import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.internal.queries.JPQLCallQueryMechanism;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.internal.queries.ExpressionQueryMechanism;
import org.eclipse.persistence.jpa.JpaQuery;
import org.eclipse.persistence.queries.Cursor;
import org.eclipse.persistence.queries.DataReadQuery;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.JPAQueryBuilder;
import org.eclipse.persistence.queries.ModifyQuery;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.queries.ReadQuery;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.queries.ReportQueryResult;
import org.eclipse.persistence.queries.ResultSetMappingQuery;
import org.eclipse.persistence.queries.SQLResultSetMapping;
import org.eclipse.persistence.sessions.DatabaseRecord;

import static org.eclipse.persistence.config.QueryHints.CACHE_RETRIEVE_MODE;
import static org.eclipse.persistence.config.QueryHints.CACHE_STORE_MODE;
import static org.eclipse.persistence.config.QueryHints.QUERY_TIMEOUT;
import static org.eclipse.persistence.config.QueryHints.QUERY_TIMEOUT_UNIT;
import static org.eclipse.persistence.config.QueryHints.PESSIMISTIC_LOCK_SCOPE;
import static org.eclipse.persistence.internal.core.helper.CoreClassConstants.Collection_Class;
import static org.eclipse.persistence.internal.helper.Helper.classImplementsInterface;
import static org.eclipse.persistence.queries.ObjectLevelReadQuery.DONT_USE_DISTINCT;

/**
 * Concrete JPA query class. The JPA query wraps a DatabaseQuery which is
 * executed.
 */
public class EJBQueryImpl<X> extends QueryImpl implements JpaQuery<X> {

    private final Map<Class<?>, Option> options = new LinkedHashMap<>();
    private final List<Cursor> cursors = new ArrayList<>();

    /**
     * Base constructor for EJBQueryImpl. Initializes basic variables.
     */
    protected EJBQueryImpl(EntityManagerImpl entityManager) {
        super(entityManager);
    }

    /**
     * Create an EJBQueryImpl with a DatabaseQuery.
     */
    public EJBQueryImpl(DatabaseQuery query, EntityManagerImpl entityManager) {
        super(query, entityManager);
        // Inherit applicable hints from EntityManager
        inheritEntityManagerHints();
    }

    /**
     * Build an EJBQueryImpl based on the given jpql string.
     */
    public EJBQueryImpl(String jpql, EntityManagerImpl entityManager) {
        this(jpql, entityManager, false);
    }

    /**
     * Build an EJBQueryImpl based on the given jpql string and result class.
     */
    public EJBQueryImpl(String jpql, EntityManagerImpl entityManager, Class<?> resultClass) {
        this(jpql, entityManager, false, resultClass);
    }

    /**
     * Create an EJBQueryImpl with either a query name or an jpql string.
     *
     * @param queryDescription a Java Persistence query string
     * @param entityManager EntityManager instance
     * @param isNamedQuery
     *            determines whether to treat the queryDescription as jpql or a
     *            query name.
     */
    public EJBQueryImpl(String queryDescription, EntityManagerImpl entityManager, boolean isNamedQuery) {
        this(queryDescription, entityManager, isNamedQuery, null);
    }

    /**
     * Create an EJBQueryImpl with either a query name or an jpql string.
     *
     * @param queryDescription a Java Persistence query string
     * @param entityManager EntityManager instance
     * @param isNamedQuery
     *            determines whether to treat the queryDescription as jpql or a
     *            query name.
     * @param resultClass the type of the query result
     */
    public EJBQueryImpl(String queryDescription, EntityManagerImpl entityManager, boolean isNamedQuery, Class<?> resultClass) {
        super(entityManager);

        if (isNamedQuery) {
            this.queryName = queryDescription;
        } else {
            if (databaseQuery == null) {
                AbstractSession session = executionContext.getSession();
                databaseQuery = buildEJBQLDatabaseQuery(null, queryDescription, executionContext.getSession(), null, null, session.getDatasourcePlatform().getConversionManager().getLoader(), resultClass);
            }
        }

        // Inherit applicable hints from EntityManager
        inheritEntityManagerHints();
    }

    EJBQueryImpl(DatabaseQuery query, QueryExecutionContext context) {
        super(query, context);
        inheritEntityManagerHints();
    }

    EJBQueryImpl(String jpql, QueryExecutionContext context) {
        this(jpql, context, false, null);
    }

    EJBQueryImpl(String jpql, QueryExecutionContext context, Class<?> resultClass) {
        this(jpql, context, false, resultClass);
    }

    EJBQueryImpl(String queryDescription, QueryExecutionContext context, boolean isNamedQuery) {
        this(queryDescription, context, isNamedQuery, null);
    }

    EJBQueryImpl(String queryDescription, QueryExecutionContext context, boolean isNamedQuery, Class<?> resultClass) {
        super(context);
        context.verifyOpen();
        if (queryDescription == null) {
            throw new IllegalArgumentException("Query text or name must not be null");
        }
        if (isNamedQuery) {
            queryName = queryDescription;
        } else {
            AbstractSession session = context.getSession();
            databaseQuery = buildEJBQLDatabaseQuery(null, queryDescription, session, null, null,
                    session.getDatasourcePlatform().getConversionManager().getLoader(), resultClass);
        }
        inheritEntityManagerHints();
        if (isNamedQuery && resultClass != null) {
            ofType(resultClass);
        }
    }

    /**
     * Build a DatabaseQuery from an jpql string.
     *
     * @param session
     *            the session to get the descriptors for this query for.
     * @return a DatabaseQuery representing the given jpql.
     */
    public static DatabaseQuery buildEJBQLDatabaseQuery(String jpql, AbstractSession session) {
        return buildEJBQLDatabaseQuery(null, jpql, session, null, null, session.getDatasourcePlatform().getConversionManager().getLoader());
    }

    /**
     * Build a DatabaseQuery from an JPQL string.
     *
     * @param jpqlQuery
     *            the JPQL string.
     * @param session
     *            the session to get the descriptors for this query for.
     * @param hints
     *            a list of hints to be applied to the query.
     * @return a DatabaseQuery representing the given jpql.
     */
    public static DatabaseQuery buildEJBQLDatabaseQuery(String queryName, String jpqlQuery, AbstractSession session, Enum lockMode, Map<String, Object> hints, ClassLoader classLoader) {
        return buildEJBQLDatabaseQuery(queryName, jpqlQuery, session, lockMode, hints, classLoader, null);
    }

    /**
     * Build a DatabaseQuery from an JPQL string.
     *
     * @param jpqlQuery
     *            the JPQL string.
     * @param session
     *            the session to get the descriptors for this query for.
     * @param hints
     *            a list of hints to be applied to the query.
     * @param resultClass
     *            the type of the query result
     * @return a DatabaseQuery representing the given jpql.
     */
    public static DatabaseQuery buildEJBQLDatabaseQuery(String queryName, String jpqlQuery, AbstractSession session, Enum lockMode, Map<String, Object> hints, ClassLoader classLoader, Class<?> resultClass) {
        // PERF: Check if the JPQL has already been parsed.
        // Only allow queries with default properties to be parse cached.
        boolean isCacheable = (queryName == null) && (hints == null);
        DatabaseQuery databaseQuery = null;
        if (isCacheable) {
            databaseQuery = session.getProject().getJPQLParseCache().get(jpqlQuery);
        }

        if ((databaseQuery == null) || (!databaseQuery.isPrepared())) {
            JPAQueryBuilder queryBuilder = session.getQueryBuilder();
            databaseQuery = queryBuilder.buildQuery(jpqlQuery, session);

            // If the query uses fetch joins, need to use JPA default of not
            // filtering duplicates.
            if (databaseQuery.isReadAllQuery()) {
                ReadAllQuery readAllQuery = (ReadAllQuery) databaseQuery;
                if (readAllQuery.hasJoining() && (readAllQuery.getDistinctState() == DONT_USE_DISTINCT)) {
                    readAllQuery.setShouldFilterDuplicates(false);
                }
                if (databaseQuery.isReportQuery()) {
                    ReportQuery reportQuery = (ReportQuery) databaseQuery;
                    reportQuery.setResultClass(resultClass);
                    Class<?> pkClass = reportQuery.getDescriptor().getCMPPolicy().getPKClass();
                    if (pkClass != null && pkClass == reportQuery.getResultClass() && reportQuery.hasIDFunctionSelectItemOnly()) {
                        reportQuery.setReturnPKClassInstance();
                    }
                }
            } else if (databaseQuery.isModifyQuery()) {
                // By default, do not batch modify queries, as row count must be returned.
                ((ModifyQuery)databaseQuery).setIsBatchExecutionSupported(false);
            }

            ((JPQLCallQueryMechanism) databaseQuery.getQueryMechanism()).getJPQLCall().setIsParsed(true);

            // Apply the lock mode.
            if (lockMode != null && !lockMode.name().equals(ObjectLevelReadQuery.NONE)) {
                if (databaseQuery.isObjectLevelReadQuery()) {
                    // If setting the lock mode returns true, we were unable to
                    // set the lock mode, throw an exception.
                    if (((ObjectLevelReadQuery) databaseQuery).setLockModeType(lockMode.name(), session)) {
                        throw new PersistenceException(ExceptionLocalization.buildMessage("ejb30-wrong-lock_called_without_version_locking-index", null));
                    }
                } else {
                    throw new IllegalArgumentException(ExceptionLocalization.buildMessage("invalid_lock_query", null));
                }
            }

            // Apply any query hints.
            databaseQuery = applyHints(hints, databaseQuery, classLoader, session);

            // If a primary key query, switch to read-object to allow cache hit.
            if (databaseQuery.isReadAllQuery() && !databaseQuery.isReportQuery() && ((ReadAllQuery)databaseQuery).shouldCheckCache()) {
                ReadAllQuery readQuery = (ReadAllQuery)databaseQuery;
                if ((readQuery.getContainerPolicy().getContainerClass() == ContainerPolicy.getDefaultContainerClass())
                        && (!readQuery.hasHierarchicalExpressions())) {
                    databaseQuery.checkDescriptor(session);
                    Expression selectionCriteria = databaseQuery.getSelectionCriteria();
                    if ((selectionCriteria != null)
                            && (databaseQuery.getDescriptor().getObjectBuilder().isPrimaryKeyExpression(true, selectionCriteria, session)
                            || (databaseQuery.getDescriptor().getCachePolicy().isIndexableExpression(selectionCriteria, databaseQuery.getDescriptor(), session)))) {
                        ReadObjectQuery newQuery = new ReadObjectQuery();
                        newQuery.copyFromQuery(databaseQuery);
                        databaseQuery = newQuery;
                    }
                }
            }

            if (isCacheable) {
                // Prepare query as hint may cause cloning (but not un-prepare
                // as in read-only).
                databaseQuery.checkPrepare(session, new DatabaseRecord());
                session.getProject().getJPQLParseCache().put(jpqlQuery, databaseQuery);
            }
        }

        return databaseQuery;
    }

    /**
     * Build a ReadAllQuery from a class and sql string.
     */
    public static DatabaseQuery buildSQLDatabaseQuery(Class<?> resultClass, String sqlString, ClassLoader classLoader, AbstractSession session) {
        return buildSQLDatabaseQuery(resultClass, sqlString, null, classLoader, session);
    }

    /**
     * Build a ReadAllQuery for class and sql string.
     *
     * @param hints
     *            a list of hints to be applied to the query.
     */
    public static DatabaseQuery buildSQLDatabaseQuery(Class<?> resultClass, String sqlString, Map<String, Object> hints, ClassLoader classLoader, AbstractSession session) {
        DatabaseQuery query;

        // A result class naming an entity is read as an entity result, with the columns of the
        // result set matched against the entity's mapped columns. Any other result class - a basic
        // type, or a class constructed from the columns - describes rows that are not entities at
        // all, and reading them as one would go looking for a descriptor that cannot exist. Those
        // are read through their result class instead; see
        // ResultSetMappingQuery#buildResultSetMappingForResultClass.
        if (session.getDescriptor(resultClass) != null) {
            query = new ReadAllQuery(resultClass);
        }
        else {
            ResultSetMappingQuery resultSetMappingQuery = new ResultSetMappingQuery();
            resultSetMappingQuery.setResultClass(resultClass);
            query = resultSetMappingQuery;
        }

        query.setCall(((DatasourcePlatform)session.getPlatform(resultClass)).buildNativeCall(sqlString));
        query.setIsUserDefined(true);

        // apply any query hints
        return applyHints(hints, query, classLoader, session);
    }

    /**
     * Build a DataReadQuery from a sql string.
     */
    public static DatabaseQuery buildSQLDatabaseQuery(String sqlString, ClassLoader classLoader, AbstractSession session) {
        return buildSQLDatabaseQuery(sqlString, new HashMap<>(), classLoader, session);
    }

    /**
     * Build a DataReadQuery from a sql string.
     */
    public static DatabaseQuery buildSQLDatabaseQuery(String sqlString, Map<String, Object> hints, ClassLoader classLoader, AbstractSession session) {
        DataReadQuery query = new DataReadQuery();
        query.setResultType(DataReadQuery.AUTO);
        query.setSQLString(sqlString);
        query.setIsUserDefined(true);

        // apply any query hints
        return applyHints(hints, query, classLoader, session);
    }

    /**
     * Build a ResultSetMappingQuery from a sql result set mapping name and sql
     * string.
     */
    public static DatabaseQuery buildSQLDatabaseQuery(String sqlResultSetMappingName, String sqlString, ClassLoader classLoader, AbstractSession session) {
        return buildSQLDatabaseQuery(sqlResultSetMappingName, sqlString, null, classLoader, session);
    }

    /**
     * Build a ResultSetMappingQuery from a sql result set mapping name and sql
     * string.
     *
     * @param hints
     *            a list of hints to be applied to the query.
     */
    public static DatabaseQuery buildSQLDatabaseQuery(String sqlResultSetMappingName, String sqlString, Map<String, Object> hints, ClassLoader classLoader, AbstractSession session) {
        ResultSetMappingQuery query = new ResultSetMappingQuery();
        query.setSQLResultSetMappingName(sqlResultSetMappingName);
        query.setCall(((DatasourcePlatform)session.getDatasourcePlatform()).buildNativeCall(sqlString));
        query.setIsUserDefined(true);

        // apply any query hints
        return applyHints(hints, query, classLoader, session);
    }

    /**
     * Build a ResultSetMappingQuery from a sql result set mapping name and sql
     * string.
     *
     * @param hints
     *            a list of hints to be applied to the query.
     */
    public static DatabaseQuery buildSQLDatabaseQuery(SQLResultSetMapping sqlResultSetMapping, String sqlString, Map<String, Object> hints, ClassLoader classLoader, AbstractSession session) {
        ResultSetMappingQuery query = new ResultSetMappingQuery();
        query.addSQLResultSetMapping(sqlResultSetMapping);
        query.setCall(((DatasourcePlatform)session.getDatasourcePlatform()).buildNativeCall(sqlString));
        query.setIsUserDefined(true);

        // apply any query hints
        return applyHints(hints, query, classLoader, session);
    }

    /**
     * Set an implementation-specific hint. If the hint name is not recognized,
     * it is silently ignored.
     *
     * @return the same query instance
     * @throws IllegalArgumentException
     *             if the second argument is not valid for the implementation
     */
    @Override
    public EJBQueryImpl<X> setHint(String hintName, Object value) {
        try {
            executionContext.verifyOpen();
            setHintInternal(hintName, value);
            return this;
        } catch (RuntimeException e) {
            setRollbackOnly();
            throw e;
        }
    }

    /**
     * Set the lock mode type to be used for the query execution.
     *
     * @throws IllegalStateException
     *             if not a Java Persistence query language SELECT query
     */
    @Override
    public EJBQueryImpl<X> setLockMode(LockModeType lockMode) {
        if (lockMode == null) {
            executionContext.verifyOpen();
            throw new IllegalArgumentException("Lock mode must not be null");
        }
        super.setLockMode(lockMode);
        recordOption(lockMode);
        return this;
    }


    // Based on EntityManagerImpl#getQueryHints(Object,OperationType)
    @Override
    public CacheRetrieveMode getCacheRetrieveMode() {
        return FindOptionUtils.getCacheRetrieveMode(executionContext.getSession(), getDatabaseQuery().getProperties());
    }

    @Override
    public EJBQueryImpl<X> setCacheRetrieveMode(CacheRetrieveMode cacheRetrieveMode) {
        executionContext.verifyOpen();
        if (cacheRetrieveMode == null) {
            throw new IllegalArgumentException("Cache retrieve mode must not be null");
        }
        FindOptionUtils.setCacheRetrieveMode(getDatabaseQuery().getProperties(), cacheRetrieveMode);
        setHint(CACHE_RETRIEVE_MODE, cacheRetrieveMode);
        recordOption(cacheRetrieveMode);
        return this;
    }

    @Override
    public CacheStoreMode getCacheStoreMode() {
        return FindOptionUtils.getCacheStoreMode(executionContext.getSession(), getDatabaseQuery().getProperties());
    }

    @Override
    public EJBQueryImpl<X> setCacheStoreMode(CacheStoreMode cacheStoreMode) {
        executionContext.verifyOpen();
        if (cacheStoreMode == null) {
            throw new IllegalArgumentException("Cache store mode must not be null");
        }
        FindOptionUtils.setCacheStoreMode(getDatabaseQuery().getProperties(), cacheStoreMode);
        setHint(CACHE_STORE_MODE, cacheStoreMode);
        recordOption(cacheStoreMode);
        return this;
    }

    @Override
    public Integer getTimeout() {
        executionContext.verifyOpen();
        if (getDatabaseQuery().getProperty(QUERY_TIMEOUT) == null) {
            return null;
        }
        return FindOptionUtils.getTimeout(executionContext.getSession(), getDatabaseQuery().getProperties());
    }

    @Override
    public EJBQueryImpl<X> setTimeout(Integer timeout) {
        executionContext.verifyOpen();
        if (timeout != null && timeout < 0) {
            throw new IllegalArgumentException("Query timeout must not be negative");
        }
        FindOptionUtils.setTimeout(getDatabaseQuery().getProperties(), timeout);
        setHint(QUERY_TIMEOUT_UNIT, java.util.concurrent.TimeUnit.MILLISECONDS);
        setHint(QUERY_TIMEOUT, timeout == null ? 0 : timeout);
        if (timeout == null) {
            getDatabaseQuery().getProperties().remove(QUERY_TIMEOUT);
            options.remove(Timeout.class);
        } else {
            recordOption(Timeout.milliseconds(timeout));
        }
        return this;
    }

    /**
     * Inherit applicable query hints from the EntityManager.
     * EntityManager-level settings to newly created queries.
     */
    protected void inheritEntityManagerHints() {
        if (executionContext.getProperties() == null) {
            return;
        }

        DatabaseQuery dbQuery = getDatabaseQuery();
        if (dbQuery == null) {
            return;
        }

        Map<String, Object> emProperties = executionContext.getProperties();

        // CACHE_RETRIEVE_MODE only applies to ObjectLevelReadQuery (SELECT queries)
        if (dbQuery.isObjectLevelReadQuery() && emProperties.containsKey(CACHE_RETRIEVE_MODE)) {
            setHint(CACHE_RETRIEVE_MODE, emProperties.get(CACHE_RETRIEVE_MODE));
        }

        // CACHE_STORE_MODE applies to all query types:
        // - For ObjectLevelReadQuery: controls whether results are stored in cache after reading
        // - For ModifyQuery: controls whether cache is invalidated after UPDATE/DELETE
        if (emProperties.containsKey(CACHE_STORE_MODE)) {
            setHint(CACHE_STORE_MODE, emProperties.get(CACHE_STORE_MODE));
        }
    }

    /**
     * Non-standard method to return results of a ReadQuery that has a
     * containerPolicy that returns objects as a collection rather than a List
     *
     * @return Collection of results
     */
    @Override
    public Collection getResultCollection() {
        // bug51411440: need to throw IllegalStateException if query
        // executed on closed em
        this.executionContext.verifyOpenWithSetRollbackOnly();
        setAsSQLReadQuery();
        propagateResultProperties();
        // bug:4297903, check container policy class and throw exception if its
        // not the right type
        DatabaseQuery query = getDatabaseQueryInternal();
        try {
            if (query.isReadAllQuery()) {
                Class<?> containerClass = ((ReadAllQuery) getDatabaseQueryInternal()).getContainerPolicy().getContainerClass();
                if (!classImplementsInterface(containerClass, Collection_Class)) {
                    throw QueryException.invalidContainerClass(containerClass, Collection_Class);
                }
            } else if (query.isReadObjectQuery()) {
                List<Object> resultList = new ArrayList<>();
                Object result = executeReadQuery();
                if (result != null) {
                    resultList.add(result);
                }
                return resultList;
            } else if (!query.isReadQuery()) {
                throw new IllegalStateException(ExceptionLocalization.buildMessage("incorrect_query_for_get_result_collection"));
            }

            return (Collection) executeReadQuery();
        } catch (LockTimeoutException exception) {
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
     * Non-standard method to return results of a ReadQuery that uses a Cursor.
     *
     * @return Cursor on results, either a CursoredStream, or ScrollableCursor
     */
    @Override
    public Cursor getResultCursor() {
        // bug51411440: need to throw IllegalStateException if query executed on closed em
        this.executionContext.verifyOpenWithSetRollbackOnly();
        try {
            setAsSQLReadQuery();
            propagateResultProperties();

            // bug:4297903, check container policy class and throw exception if its
            // not the right type
            if (getDatabaseQueryInternal() instanceof ReadAllQuery) {
                if (!((ReadAllQuery) getDatabaseQueryInternal()).getContainerPolicy().isCursorPolicy()) {
                    Class<?> containerClass = ((ReadAllQuery) getDatabaseQueryInternal()).getContainerPolicy().getContainerClass();
                    throw QueryException.invalidContainerClass(containerClass, Cursor.class);
                }
            } else if (getDatabaseQueryInternal() instanceof ReadObjectQuery) {
                // bug:4300879, no support for ReadObjectQuery if a collection is required
                throw QueryException.incorrectQueryObjectFound(getDatabaseQueryInternal(), ReadAllQuery.class);
            } else if (!(getDatabaseQueryInternal() instanceof ReadQuery)) {
                throw new IllegalStateException(ExceptionLocalization.buildMessage("incorrect_query_for_get_result_collection"));
            }

            Cursor cursor = executeCursorReadQuery();
            if (cursor.isClosed()) {
                releaseCursorOwnerContext(cursor.getQuery());
            }
            if (!cursor.isClosed()) {
                cursors.add(cursor);
                executionContext.registerQuery(this);
                // Never attach owner state to the reusable query or a named-query
                // template. The cursor alone owns this execution-state clone.
                ReadQuery executed = cursor.getQuery();
                cursor.query = (ReadQuery) executed.clone();
                if (executionContext.isEntityAgent()) {
                    if (cursor.getPolicy() != null) {
                        cursor.policy = (org.eclipse.persistence.queries.CursorPolicy) cursor.getPolicy().clone(cursor.query);
                    }
                    releaseCursorOwnerContext(executed);
                }
                cursor.query.setProperty("eclipselink.cursor.resource-release", (Runnable) () -> {
                    releaseCursorOwnerContext(cursor.getQuery());
                    cursors.remove(cursor);
                    if (cursors.isEmpty()) {
                        executionContext.unregisterQuery(this);
                    }
                });
            }
            return cursor;
        } catch (LockTimeoutException e) {
            throw e;
        } catch (PersistenceException | IllegalStateException exception) {
            setRollbackOnly();
            throw exception;
        } catch (RuntimeException exception) {
            setRollbackOnly();
            throw new PersistenceException(exception);
        }
    }

    private void releaseCursorOwnerContext(DatabaseQuery query) {
        if (executionContext.isEntityAgent()) {
            query.removeProperty("eclipselink.cursor.row-materializer");
            query.removeProperty(ResultSetMappingTranslator.EXECUTION_CONTEXT_PROPERTY);
            query.removeProperty(ReportQueryResult.CONSTRUCTOR_ARGUMENT_PREPARER);
        }
    }

    private Cursor executeCursorReadQuery() {
        if (!executionContext.isEntityAgent()) {
            return (Cursor) executeReadQuery();
        }
        DatabaseQuery original = getDatabaseQueryInternal();
        boolean shared = this.isShared;
        LockModeType executionLock = this.lockMode;
        this.databaseQuery = (DatabaseQuery) original.clone();
        this.isShared = false;
        this.databaseQuery.setProperty("eclipselink.cursor.row-materializer",
                (BiFunction<DatabaseQuery, Supplier<Object>, Object>) (query, build) ->
                        executionContext.materializeCursorRow(query, build, executionLock));
        try {
            return (Cursor) executeReadQuery();
        } finally {
            this.databaseQuery = original;
            this.isShared = shared;
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * {@code QueryImpl} returns {@code List<Object>}, which does not satisfy the {@code List<X>}
     * required by {@link jakarta.persistence.TypedQuery}. This is the typed query, so it narrows the
     * result type here, exactly as {@link #getSingleResult()} and {@link #getSingleResultOrNull()} do.
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<X> getResultList() {
        return (List<X>) super.getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public X getSingleResult() {
        return (X) super.getSingleResult();
    }

    @Override
    @SuppressWarnings("unchecked")
    public X getSingleResultOrNull() {
        return (X) super.getSingleResultOrNull();
    }

    /**
     * Set the position of the first result to retrieve.
     *
     * @param startPosition
     *            position of the first result, numbered from 0
     * @return the same query instance
     */
    @Override
    public EJBQueryImpl<X> setFirstResult(int startPosition) {
        super.setFirstResult(startPosition);
        return this;
    }

    @Override
    public EJBQueryImpl<X> setQueryFlushMode(QueryFlushMode flushMode) {
        super.setQueryFlushMode(flushMode);
        recordOption(flushMode == null ? QueryFlushMode.DEFAULT : flushMode);
        return this;
    }

    /**
     * Set the flush mode type to be used for the query execution.
     *
     */
    @Override
    @Deprecated(since = "6.0")
    public EJBQueryImpl<X> setFlushMode(FlushModeType flushMode) {
        super.setFlushMode(flushMode);
        return this;
    }

    /**
     * Set the maximum number of results to retrieve.
     *
     * @return the same query instance
     */
    @Override
    public EJBQueryImpl<X> setMaxResults(int maxResult) {
        super.setMaxResults(maxResult);
        return this;
    }

    /**
     * Bind an instance of java.util.Calendar to a positional parameter.
     *
     * @return the same query instance
     */
    @Override
    @Deprecated(since = "3.2")
    public EJBQueryImpl<X> setParameter(int position, Calendar value, TemporalType temporalType) {
        executionContext.verifyOpenWithSetRollbackOnly();
        return setParameter(position, convertTemporalType(value, temporalType));
    }

    /**
     * Bind an instance of java.util.Date to a positional parameter.
     *
     * @return the same query instance
     */
    @Override
    @Deprecated(since = "3.2")
    public EJBQueryImpl<X> setParameter(int position, Date value, TemporalType temporalType) {
        executionContext.verifyOpenWithSetRollbackOnly();
        return setParameter(position, convertTemporalType(value, temporalType));
    }

    /**
     * Bind an argument to a positional parameter.
     *
     * @return the same query instance
     */
    @Override
    public EJBQueryImpl<X> setParameter(int position, Object value) {
        try {
            executionContext.verifyOpen();
            setParameterInternal(position, value);
            parameterConverters.remove(String.valueOf(position));
            return this;
        } catch (RuntimeException e) {
            setRollbackOnly();
            throw e;
        }
    }

    /**
     * Bind an instance of java.util.Calendar to a Parameter object.
     *
     * @return the same query instance
     * @throws IllegalArgumentException
     *             if position does not correspond to a parameter of the query
     */
    @Override
    @Deprecated(since = "3.2")
    public EJBQueryImpl<X> setParameter(Parameter<Calendar> param, Calendar value, TemporalType temporalType) {
        executionContext.verifyOpenWithSetRollbackOnly();
        if (param == null) {
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("NULL_PARAMETER_PASSED_TO_SET_PARAMETER"));
        }

        //bug 402686: type validation
        String position = getParameterId(param);
        Parameter<?> parameter = this.getInternalParameters().get(position);
        if (parameter == null ) {
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("NO_PARAMETER_WITH_NAME", new Object[] { param.toString(), this.databaseQuery }));
        }
        if (!parameter.getParameterType().equals(param.getParameterType())) {
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("INCORRECT_PARAMETER_TYPE", new Object[] { position, param.getParameterType() }));
        }

        return setParameter(position, value, temporalType);
    }

    /**
     * Bind an instance of java.util.Date to a Parameter object.
     *
     * @param param
     *            object
     * @return the same query instance
     * @throws IllegalArgumentException
     *             if position does not correspond to a parameter of the query
     */
    @Override
    @Deprecated(since = "3.2")
    public EJBQueryImpl<X> setParameter(Parameter<Date> param, Date value, TemporalType temporalType) {
        executionContext.verifyOpen();
        if (param == null) {
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("NULL_PARAMETER_PASSED_TO_SET_PARAMETER"));
        }

        //bug 402686: type validation
        String position = getParameterId(param);
        Parameter<?> parameter = this.getInternalParameters().get(position);
        if (parameter == null ) {
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("NO_PARAMETER_WITH_NAME", new Object[] { param.toString(), this.databaseQuery }));
        }
        if (!parameter.getParameterType().equals(param.getParameterType())) {
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("INCORRECT_PARAMETER_TYPE", new Object[] { position, param.getParameterType() }));
        }

        return setParameter(position, value, temporalType);
    }

    /**
     * Set the value of a Parameter object.
     *
     * @param param
     *            parameter to be set
     * @param value
     *            parameter value
     * @return query instance
     * @throws IllegalArgumentException
     *             if parameter does not correspond to a parameter of the query
     */
    @Override
    public <T> EJBQueryImpl<X> setParameter(Parameter<T> param, T value) {
        executionContext.verifyOpen();
        if (param == null) {
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("NULL_PARAMETER_PASSED_TO_SET_PARAMETER"));
        }

        //bug 402686: type validation
        String position = getParameterId(param);
        Parameter<?> parameter = this.getInternalParameters().get(position);
        if (parameter == null ) {
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("NO_PARAMETER_WITH_NAME", new Object[] { param.toString(), this.databaseQuery }));
        }
        if (!parameter.getParameterType().equals(param.getParameterType())) {
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("INCORRECT_PARAMETER_TYPE", new Object[] { position, param.getParameterType() }));
        }

        if (parameter instanceof ParameterExpressionImpl<?> criteriaParameter
                && criteriaParameter.getConverterClass() != null) {
            @SuppressWarnings("unchecked")
            Class<? extends AttributeConverter<T, ?>> converter =
                    (Class<? extends AttributeConverter<T, ?>>) criteriaParameter.getConverterClass();
            bindConvertedParameter(position, value, converter, param.getPosition() != null);
            return this;
        }
        return setParameter(position, value);
    }

    /**
     * Bind an instance of java.util.Calendar to a named parameter.
     *
     * @return the same query instance
     */
    @Override
    @Deprecated(since = "3.2")
    public EJBQueryImpl<X> setParameter(String name, Calendar value, TemporalType temporalType) {
        executionContext.verifyOpenWithSetRollbackOnly();
        return setParameter(name, convertTemporalType(value, temporalType));
    }

    /**
     * Bind an instance of java.util.Date to a named parameter.
     *
     * @return the same query instance
     */
    @Override
    @Deprecated(since = "3.2")
    public EJBQueryImpl<X> setParameter(String name, Date value, TemporalType temporalType) {
        executionContext.verifyOpenWithSetRollbackOnly();
        return setParameter(name, convertTemporalType(value, temporalType));
    }

    /**
     * Bind an argument to a named parameter.
     *
     * @param name
     *            the parameter name
     * @return the same query instance
     */
    @Override
    public EJBQueryImpl<X> setParameter(String name, Object value) {
        try {
            executionContext.verifyOpen();
            setParameterInternal(name, value, false);
            parameterConverters.remove(name);
            return this;
        } catch (RuntimeException e) {
            setRollbackOnly();
            throw e;
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + this.databaseQuery + ")";
    }

    @Override
    public void releaseResources() {
        RuntimeException failure = null;
        for (Cursor cursor : new ArrayList<>(cursors)) {
            try {
                cursor.close();
                cursors.remove(cursor);
            } catch (RuntimeException exception) {
                if (failure == null) {
                    failure = exception;
                } else {
                    failure.addSuppressed(exception);
                }
            }
        }
        if (failure != null) {
            throw failure;
        }
        executionContext.unregisterQuery(this);
    }

    @Override
    public long getResultCount() {
        executionContext.verifyOpen();
        DatabaseQuery original = getDatabaseQueryInternal();
        if (!original.isReadQuery()) {
            throw new IllegalStateException("A statement does not have a result count");
        }
        List<Object> values = processParameters();
        if (isFlushModeAUTO()) {
            performPreQueryFlush();
        }
        AbstractSession session = executionContext.getExecutionSession(original);
        ReadQuery read = (ReadQuery) original.clone();
        read.setFirstResult(0);
        read.setMaxRows(0);
        if (read instanceof ObjectLevelReadQuery objectRead) {
            objectRead.setOrderByExpressions(new ArrayList<>());
            objectRead.setLockModeType(LockModeType.NONE.name(), session);
        }
        read.prepareCall(session, new DatabaseRecord());
        read.setSession(session);
        DatabaseCall call;
        if (read.isObjectLevelReadQuery() && !read.isUserDefined()) {
            CountQueryMechanism mechanism = new CountQueryMechanism(read);
            SQLSelectStatement select = mechanism.buildCountSource();
            select.setUseUniqueFieldAliases(true);
            // Derived-table columns need names even when the selected value is
            // a function, constant or scalar subquery instead of a mapped field.
            List<Object> fields = select.getFields();
            for (int index = 0; index < fields.size(); index++) {
                if (fields.get(index) instanceof Expression expression && !expression.isDataExpression()
                        && !(expression instanceof FunctionExpression function
                                && function.getOperator().getSelector() == ExpressionOperator.As)) {
                    fields.set(index, expression.as("EL_COUNT_" + index));
                }
            }
            select.setOrderByExpressions(new ArrayList<>());
            call = select.buildCall(session, read);
        } else {
            call = (DatabaseCall) read.getDatasourceCall().clone();
        }
        // Preserve the prepared call's parameter fields: rebuilding SQL as a
        // native string would lose named, collection and converted bindings.
        call.setQueryString("SELECT COUNT(*) FROM (" + withoutOrderBy(call.getQueryString()) + ") EL_COUNT");
        call.setFields(null);
        call.prepare(session);
        DataReadQuery count = new DataReadQuery();
        count.setCall(call);
        count.setArguments(new ArrayList<>(original.getArguments()));
        count.setArgumentTypes(new ArrayList<>(original.getArgumentTypes()));
        count.setResultType(DataReadQuery.VALUE);
        count.setProperties(new HashMap<>(original.getProperties()));
        count.setIsPrepared(true);
        Object result = executionContext.executeQuery(count, values, null);
        return ((Number) result).longValue();
    }

    private static final class CountQueryMechanism extends ExpressionQueryMechanism {
        CountQueryMechanism(ReadQuery query) {
            super(query, query.getSelectionCriteria());
        }

        SQLSelectStatement buildCountSource() {
            return getQuery().isReportQuery() ? buildReportQuerySelectStatement(false) : buildNormalSelectStatement();
        }
    }

    private static String withoutOrderBy(String sql) {
        int depth = 0;
        char quote = 0;
        for (int index = 0; index < sql.length(); index++) {
            char character = sql.charAt(index);
            if (quote != 0) {
                if (character == quote) {
                    if (index + 1 < sql.length() && sql.charAt(index + 1) == quote) {
                        index++;
                    } else {
                        quote = 0;
                    }
                }
            } else if (character == '\'' || character == '"' || character == '`') {
                quote = character;
            } else if (character == '(') {
                depth++;
            } else if (character == ')') {
                depth--;
            } else if (depth == 0 && (index == 0 || Character.isWhitespace(sql.charAt(index - 1)))
                    && sql.regionMatches(true, index, "ORDER BY", 0, 8)) {
                return sql.substring(0, index).stripTrailing();
            }
        }
        return sql.stripTrailing().replaceFirst(";\\s*$", "");
    }

    @Override
    public EJBQueryImpl<X> setLockScope(PessimisticLockScope lockScope) {
        executionContext.verifyOpen();
        if (lockScope == null) {
            throw new IllegalArgumentException("Lock scope must not be null");
        }
        if (!getDatabaseQueryInternal().isObjectLevelReadQuery()) {
            throw new IllegalStateException("Lock scope requires an entity query");
        }
        setHint(PESSIMISTIC_LOCK_SCOPE, lockScope);
        getDatabaseQuery().setProperty(PESSIMISTIC_LOCK_SCOPE, lockScope);
        recordOption(lockScope);
        return this;
    }

    @Override
    public PessimisticLockScope getLockScope() {
        executionContext.verifyOpen();
        if (!getDatabaseQueryInternal().isObjectLevelReadQuery()) {
            throw new IllegalStateException("Lock scope requires an entity query");
        }
        Object value = getHints().get(PESSIMISTIC_LOCK_SCOPE);
        if (value instanceof PessimisticLockScope scope) {
            return scope;
        }
        return value instanceof String scope && !scope.isEmpty()
                ? PessimisticLockScope.valueOf(scope.toUpperCase(java.util.Locale.ROOT)) : PessimisticLockScope.NORMAL;
    }

    @Override
    public EJBQueryImpl<X> addOption(Option option) {
        executionContext.verifyOpen();
        if (option == null) {
            throw new IllegalArgumentException("Query option must not be null");
        }
        if (option instanceof CacheRetrieveMode mode) {
            setCacheRetrieveMode(mode);
        } else if (option instanceof CacheStoreMode mode) {
            setCacheStoreMode(mode);
        } else if (option instanceof LockModeType mode) {
            setLockMode(mode);
        } else if (option instanceof PessimisticLockScope scope) {
            setLockScope(scope);
        } else if (option instanceof QueryFlushMode mode) {
            setQueryFlushMode(mode);
        } else if (option instanceof Timeout timeout) {
            setTimeout(timeout);
        } else {
            recordOption(option);
        }
        return this;
    }

    @Override
    public Set<Option> getOptions() {
        executionContext.verifyOpen();
        return new LinkedHashSet<>(options.values());
    }

    private void recordOption(Option option) {
        options.put(option instanceof Enum<?> value ? value.getDeclaringClass() : option.getClass(), option);
    }

    int executeStatement() {
        getDatabaseQuery().setProperty(EntityManagerQueryContext.NATIVE_STATEMENT_PROPERTY, Boolean.TRUE);
        return executeUpdate();
    }

    @Override
    public Statement asStatement() {
        executionContext.verifyOpen();
        DatabaseQuery query = getDatabaseQueryInternal();
        if (!query.isModifyQuery() && query.getEJBQLString() != null) {
            throw new IllegalStateException("A SELECT query cannot be used as a Statement");
        }
        return new StatementImpl(this);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> TypedQuery<R> ofType(Class<R> resultType) {
        executionContext.verifyOpen();
        if (resultType == null) {
            throw new IllegalArgumentException("Query result type must not be null");
        }
        DatabaseQuery query = getDatabaseQueryInternal();
        if (!query.isReadQuery()) {
            throw new IllegalStateException("A statement cannot be used as a typed query");
        }
        if (query.getEJBQLString() == null && query.isUserDefined() && query.isDataReadQuery()) {
            DatabaseQuery typed = buildSQLDatabaseQuery(resultType, query.getSQLString(), QueryHintsHandler.get(query),
                    executionContext.getSession().getLoader(), executionContext.getSession());
            typed.setProperties(new HashMap<>(query.getProperties()));
            databaseQuery = typed;
            isShared = false;
        } else {
            Class<?> actualType = queryResultType(query);
            if (actualType != null && !resultType.isAssignableFrom(actualType)) {
                throw new IllegalArgumentException("Query result type " + actualType.getName()
                        + " is not assignable to " + resultType.getName());
            }
        }
        return (TypedQuery<R>) this;
    }

    /** Internal result metadata shared by query adapters and named-reference producers. */
    public static Class<?> queryResultType(DatabaseQuery query) {
        if (!query.isReadQuery()) {
            return null;
        }
        if (query instanceof ResultSetMappingQuery mappingQuery) {
            return mappingQuery.getResultClass();
        }
        if (query instanceof ReportQuery report) {
            if (report.getResultClass() != null) {
                return report.getResultClass();
            }
            if (report.getItems().size() != 1) {
                return Object[].class;
            }
            ReportItem item = report.getItems().get(0);
            if (item.getResultType() != null) {
                return item.getResultType();
            }
            if (item.getDescriptor() != null) {
                return item.getDescriptor().getJavaClass();
            }
            if (item.getMapping() != null) {
                return item.getMapping().getAttributeClassification();
            }
            return null;
        }
        return query.getReferenceClass();
    }

    @Override
    public <R> TypedQuery<R> withEntityGraph(EntityGraph<R> graph) {
        executionContext.verifyOpen();
        if (!(graph instanceof EntityGraphImpl<R> entityGraph)) {
            throw new IllegalArgumentException("The entity graph must belong to EclipseLink");
        }
        TypedQuery<R> query = ofType(entityGraph.getClassType());
        if (!getDatabaseQueryInternal().isObjectLevelReadQuery()) {
            throw new IllegalStateException("An entity graph requires an entity SELECT query");
        }
        setHint("jakarta.persistence.loadgraph", graph);
        return query;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> TypedQuery<R> withResultSetMapping(ResultSetMapping<R> mapping) {
        executionContext.verifyOpen();
        if (mapping == null) {
            throw new IllegalArgumentException("Result set mapping must not be null");
        }
        DatabaseQuery query = getDatabaseQueryInternal();
        if (query.getEJBQLString() != null || !query.isUserDefined()) {
            throw new IllegalStateException("A result set mapping requires a native SQL query");
        }
        DatabaseQuery mapped = buildSQLDatabaseQuery(ResultSetMappingTranslator.toEclipseLinkMapping(mapping, executionContext),
                query.getSQLString(), QueryHintsHandler.get(query), executionContext.getSession().getLoader(), executionContext.getSession());
        mapped.setProperties(new HashMap<>(query.getProperties()));
        mapped.setProperty(ResultSetMappingTranslator.MAPPING_LOCK_PROPERTY,
                ResultSetMappingTranslator.mappingRequiresTransaction(mapping));
        databaseQuery = mapped;
        isShared = false;
        return (TypedQuery<R>) this;
    }

    @Override
    public <P> EJBQueryImpl<X> setParameter(String name, P value, Class<P> type) {
        executionContext.verifyOpen();
        checkParameterType(value, type);
        setParameterInternal(name, value, type, false);
        parameterConverters.remove(name);
        return this;
    }

    @Override
    public <P> EJBQueryImpl<X> setParameter(String name, P value, Type<P> type) {
        executionContext.verifyOpen();
        if (type == null) {
            throw new IllegalArgumentException("Parameter type must not be null");
        }
        return setParameter(name, value, type.getJavaType());
    }

    @Override
    public <P> EJBQueryImpl<X> setConvertedParameter(String name, P value, Class<? extends AttributeConverter<P, ?>> converter) {
        bindConvertedParameter(name, value, converter, false);
        return this;
    }

    @Override
    public <P> EJBQueryImpl<X> setParameter(int position, P value, Class<P> type) {
        executionContext.verifyOpen();
        checkParameterType(value, type);
        setParameterInternal(String.valueOf(position), value, type, true);
        parameterConverters.remove(String.valueOf(position));
        return this;
    }

    @Override
    public <P> EJBQueryImpl<X> setParameter(int position, P value, Type<P> type) {
        executionContext.verifyOpen();
        if (type == null) {
            throw new IllegalArgumentException("Parameter type must not be null");
        }
        return setParameter(position, value, type.getJavaType());
    }

    @Override
    public <P> EJBQueryImpl<X> setConvertedParameter(int position, P value, Class<? extends AttributeConverter<P, ?>> converter) {
        bindConvertedParameter(String.valueOf(position), value, converter, true);
        return this;
    }

    private void checkParameterType(Object value, Class<?> type) {
        if (type == null || !isValidActualParameter(value, type)) {
            throw new IllegalArgumentException("Parameter value does not match its explicit type");
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private <P> void bindConvertedParameter(String name, P value,
            Class<? extends AttributeConverter<P, ?>> converter, boolean positional) {
        executionContext.verifyOpen();
        if (converter == null) {
            throw new IllegalArgumentException("Parameter converter must not be null");
        }
        ConverterClass converterClass = new ConverterClass(executionContext.getSession(), converter);
        AttributeConverter<Object, Object> instance = converterClass.getAttributeConverter();
        java.lang.reflect.Type relationalType = GenericTypes.getTypeArgument(instance, AttributeConverter.class, 1);
        if (!(relationalType instanceof Class<?> type)) {
            throw new IllegalArgumentException("Converter must declare a concrete database type");
        }
        java.lang.reflect.Type domainType = GenericTypes.getTypeArgument(instance, AttributeConverter.class, 0);
        if (!(domainType instanceof Class<?> domainClass)) {
            throw new IllegalArgumentException("Converter must declare a concrete domain type");
        }
        checkParameterType(value, domainClass);
        // Validate the identifier before recording the original domain value.
        // Conversion is deferred until execution, exactly once for each bind.
        setParameterInternal(name, null, type, positional);
        DatabaseQuery query = getDatabaseQueryInternal();
        int index = query.getArguments().indexOf(name);
        if (query.getEJBQLString() != null && index >= 0) {
            Class<?> expected = org.eclipse.persistence.internal.helper.ConversionManager.getObjectClass(
                    query.getArgumentTypes().get(index));
            if (!expected.isAssignableFrom(org.eclipse.persistence.internal.helper.ConversionManager.getObjectClass(type))
                    && !expected.isAssignableFrom(org.eclipse.persistence.internal.helper.ConversionManager.getObjectClass(domainClass))) {
                throw new IllegalArgumentException("Converted parameter type does not match the query parameter");
            }
        }
        parameterValues.put(name, value);
        parameterConverters.put(name, instance);
    }

    @Override
    public EJBQueryImpl<X> setParameters(Object... arguments) {
        executionContext.verifyOpen();
        if (arguments == null) {
            throw new IllegalArgumentException("Parameter arguments must not be null");
        }
        validatePositionalArgumentCount(arguments.length);
        for (int index = 0; index < arguments.length; index++) {
            setParameter(index + 1, arguments[index]);
        }
        return this;
    }

    @Override
    public EJBQueryImpl<X> setTimeout(Timeout timeout) {
        executionContext.verifyOpen();
        return setTimeout(timeout == null ? null : timeout.milliseconds());
    }
}
