/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 *     Zoltan NAGY & tware - updated support for MaxRows
 *     11/01/2010-2.2 Guy Pelletier 
 *       - 322916: getParameter on Query throws NPE
 *     11/09/2010-2.1 Michael O'Brien 
 *       - 329089: PERF: EJBQueryImpl.setParamenterInternal() move indexOf check inside non-native block
 *     02/08/2012-2.4 Guy Pelletier 
 *       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
 ******************************************************************************/
package org.eclipse.persistence.internal.jpa;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.LockTimeoutException;
import javax.persistence.Parameter;
import javax.persistence.PersistenceException;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;

import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.internal.databaseaccess.DatasourcePlatform;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.internal.localization.ExceptionLocalization;
import org.eclipse.persistence.internal.queries.ContainerPolicy;
import org.eclipse.persistence.internal.queries.JPQLCallQueryMechanism;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.jpa.JpaQuery;
import org.eclipse.persistence.queries.Cursor;
import org.eclipse.persistence.queries.DataReadQuery;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.JPAQueryBuilder;
import org.eclipse.persistence.queries.JPAQueryBuilderManager;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReadObjectQuery;
import org.eclipse.persistence.queries.ReadQuery;
import org.eclipse.persistence.queries.ResultSetMappingQuery;
import org.eclipse.persistence.sessions.DatabaseRecord;

/**
 * Concrete JPA query class. The JPA query wraps a DatabaseQuery which is
 * executed.
 */
public class EJBQueryImpl<X> extends QueryImpl implements JpaQuery<X> {
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
    }

    /**
     * Build an EJBQueryImpl based on the given jpql string.
     */
    public EJBQueryImpl(String jpql, EntityManagerImpl entityManager) {
        this(jpql, entityManager, false);
    }

    /**
     * Create an EJBQueryImpl with either a query name or an jpql string.
     * 
     * @param isNamedQuery
     *            determines whether to treat the queryDescription as jpql or a
     *            query name.
     */
    public EJBQueryImpl(String queryDescription, EntityManagerImpl entityManager, boolean isNamedQuery) {
        super(entityManager);
        if (isNamedQuery) {
            this.queryName = queryDescription;
        } else {
            if (databaseQuery == null) {
                databaseQuery = buildEJBQLDatabaseQuery(queryDescription, this.entityManager.getDatabaseSession());
            }
        }
    }

    /**
     * Build a DatabaseQuery from an jpql string.
     * 
     * @param jpql
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
     * @param jpql
     *            the JPQL string.
     * @param flushOnExecute
     *            flush the unit of work before executing the query.
     * @param session
     *            the session to get the descriptors for this query for.
     * @param hints
     *            a list of hints to be applied to the query.
     * @return a DatabaseQuery representing the given jpql.
     */
    public static DatabaseQuery buildEJBQLDatabaseQuery(String queryName, String jpqlQuery, AbstractSession session, Enum lockMode, Map<String, Object> hints, ClassLoader classLoader) {
        // PERF: Check if the JPQL has already been parsed.
        // Only allow queries with default properties to be parse cached.
        boolean isCacheable = (queryName == null) && (hints == null);
        DatabaseQuery databaseQuery = null;
        if (isCacheable) {
            databaseQuery = (DatabaseQuery) session.getProject().getJPQLParseCache().get(jpqlQuery);
        }
        if ((databaseQuery == null) || (!databaseQuery.isPrepared())) {
            JPAQueryBuilder queryBuilder = JPAQueryBuilderManager.getQueryBuilder();
            databaseQuery = queryBuilder.buildQuery(jpqlQuery, session);
            
            // If the query uses fetch joins, need to use JPA default of not
            // filtering duplicates.
            if (databaseQuery.isReadAllQuery()) {
                ReadAllQuery readAllQuery = (ReadAllQuery) databaseQuery;
                if (readAllQuery.hasJoining() && (readAllQuery.getDistinctState() == ReadAllQuery.DONT_USE_DISTINCT)) {
                    readAllQuery.setShouldFilterDuplicates(false);
                }
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
                    throw new IllegalArgumentException(ExceptionLocalization.buildMessage("invalid_lock_query", (Object[]) null));
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
    public static DatabaseQuery buildSQLDatabaseQuery(Class resultClass, String sqlString, ClassLoader classLoader, AbstractSession session) {
        return buildSQLDatabaseQuery(resultClass, sqlString, null, classLoader, session);
    }
    
    /**
     * Build a ReadAllQuery for class and sql string.
     * 
     * @param hints
     *            a list of hints to be applied to the query.
     */
    public static DatabaseQuery buildSQLDatabaseQuery(Class resultClass, String sqlString, Map<String, Object> hints, ClassLoader classLoader, AbstractSession session) {
        ReadAllQuery query = new ReadAllQuery(resultClass);
        query.setCall(((DatasourcePlatform)session.getPlatform(resultClass)).buildNativeCall(sqlString));
        query.setIsUserDefined(true);

        // apply any query hints
        return applyHints(hints, query, classLoader, session);
    }
    
    /**
     * Build a DataReadQuery from a sql string.
     */
    public static DatabaseQuery buildSQLDatabaseQuery(String sqlString, ClassLoader classLoader, AbstractSession session) {
        return buildSQLDatabaseQuery(sqlString, new HashMap<String, Object>(), classLoader, session);
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
     * Set an implementation-specific hint. If the hint name is not recognized,
     * it is silently ignored.
     * 
     * @param hintName
     * @param value
     * @return the same query instance
     * @throws IllegalArgumentException
     *             if the second argument is not valid for the implementation
     */
    public TypedQuery<X> setHint(String hintName, Object value) {
        try {
            entityManager.verifyOpen();
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
     * @param lockMode
     * @throws IllegalStateException
     *             if not a Java Persistence query language SELECT query
     */
    public EJBQueryImpl setLockMode(LockModeType lockMode) {
        return (EJBQueryImpl) super.setLockMode(lockMode);
    }
    
    /**
     * Non-standard method to return results of a ReadQuery that has a
     * containerPolicy that returns objects as a collection rather than a List
     * 
     * @return Collection of results
     */
    public Collection getResultCollection() {
        // bug51411440: need to throw IllegalStateException if query executed on
        // closed em
        entityManager.verifyOpen();
        setAsSQLReadQuery();
        propagateResultProperties();
        // bug:4297903, check container policy class and throw exception if its
        // not the right type
        DatabaseQuery query = getDatabaseQueryInternal();
        if (query.isReadAllQuery()) {
            Class containerClass = ((ReadAllQuery) getDatabaseQueryInternal()).getContainerPolicy().getContainerClass();
            if (!Helper.classImplementsInterface(containerClass, ClassConstants.Collection_Class)) {
                throw QueryException.invalidContainerClass(containerClass, ClassConstants.Collection_Class);
            }
        } else if (query.isReadObjectQuery()) {
            List resultList = new ArrayList();
            Object result = executeReadQuery();
            if (result != null) {
                resultList.add(executeReadQuery());
            }
            return resultList;
        } else if (!query.isReadQuery()) {
            throw new IllegalStateException(ExceptionLocalization.buildMessage("incorrect_query_for_get_result_collection"));
        }

        try {
            return (Collection) executeReadQuery();
        } catch (LockTimeoutException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            setRollbackOnly();
            throw exception;
        }
    }
    
    /**
     * Non-standard method to return results of a ReadQuery that uses a Cursor.
     * 
     * @return Cursor on results, either a CursoredStream, or ScrollableCursor
     */
    public Cursor getResultCursor() {
        // bug51411440: need to throw IllegalStateException if query executed on
        // closed em
        entityManager.verifyOpen();
        setAsSQLReadQuery();
        propagateResultProperties();
        // bug:4297903, check container policy class and throw exception if its
        // not the right type
        if (getDatabaseQueryInternal() instanceof ReadAllQuery) {
            if (!((ReadAllQuery) getDatabaseQueryInternal()).getContainerPolicy().isCursorPolicy()) {
                Class containerClass = ((ReadAllQuery) getDatabaseQueryInternal()).getContainerPolicy().getContainerClass();
                throw QueryException.invalidContainerClass(containerClass, Cursor.class);
            }
        } else if (getDatabaseQueryInternal() instanceof ReadObjectQuery) {
            // bug:4300879, no support for ReadObjectQuery if a collection is required
            throw QueryException.incorrectQueryObjectFound(getDatabaseQueryInternal(), ReadAllQuery.class);
        } else if (!(getDatabaseQueryInternal() instanceof ReadQuery)) {
            throw new IllegalStateException(ExceptionLocalization.buildMessage("incorrect_query_for_get_result_collection"));
        }

        try {
            Object result = executeReadQuery();
            return (Cursor) result;
        } catch (LockTimeoutException e) {
            throw e;
        } catch (RuntimeException e) {
            setRollbackOnly();
            throw e;
        }
    }
    
    /**
     * Execute a query that returns a single result.
     * 
     * @return the result
     * @throws javax.persistence.EntityNotFoundException
     *             if there is no result
     * @throws javax.persistence.NonUniqueResultException
     *             if more than one result
     */
    @Override
    public X getSingleResult() {
        return (X) super.getSingleResult();
    }
    
    /**
     * Set the position of the first result to retrieve.
     * 
     * @param start
     *            position of the first result, numbered from 0
     * @return the same query instance
     */
    public EJBQueryImpl setFirstResult(int startPosition) {
        return (EJBQueryImpl) super.setFirstResult(startPosition);
    }
    
    /**
     * Set the flush mode type to be used for the query execution.
     * 
     * @param flushMode
     */
    public EJBQueryImpl setFlushMode(FlushModeType flushMode) {
        return (EJBQueryImpl) super.setFlushMode(flushMode);
    }
    
    /**
     * Set the maximum number of results to retrieve.
     * 
     * @param maxResult
     * @return the same query instance
     */
    public EJBQueryImpl setMaxResults(int maxResult) {
        return (EJBQueryImpl) super.setMaxResults(maxResult);
    }
    
    /**
     * Bind an instance of java.util.Calendar to a positional parameter.
     * 
     * @param position
     * @param value
     * @param temporalType
     * @return the same query instance
     */
    public TypedQuery setParameter(int position, Calendar value, TemporalType temporalType) {
        entityManager.verifyOpen();
        return setParameter(position, convertTemporalType(value, temporalType));
    }
    
    /**
     * Bind an instance of java.util.Date to a positional parameter.
     * 
     * @param position
     * @param value
     * @param temporalType
     * @return the same query instance
     */
    public TypedQuery setParameter(int position, Date value, TemporalType temporalType) {
        entityManager.verifyOpen();
        return setParameter(position, convertTemporalType(value, temporalType));
    }
    
    /**
     * Bind an argument to a positional parameter.
     * 
     * @param position
     * @param value
     * @return the same query instance
     */
    public TypedQuery setParameter(int position, Object value) {
        try {
            entityManager.verifyOpen();
            setParameterInternal(position, value);
            return this;
        } catch (RuntimeException e) {
            setRollbackOnly();
            throw e;
        }
    }
    
    /**
     * Bind an instance of java.util.Calendar to a Parameter object.
     * 
     * @param parameter
     * @param value
     * @param temporalType
     * @return the same query instance
     * @throws IllegalArgumentException
     *             if position does not correspond to a parameter of the query
     */
    public TypedQuery setParameter(Parameter<Calendar> param, Calendar value, TemporalType temporalType) {
        if (param == null)
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("NULL_PARAMETER_PASSED_TO_SET_PARAMETER"));
        return this.setParameter(param.getName(), value, temporalType);
    }
    
    /**
     * Bind an instance of java.util.Date to a Parameter object.
     * 
     * @param parameter
     *            object
     * @param value
     * @param temporalType
     * @return the same query instance
     * @throws IllegalArgumentException
     *             if position does not correspond to a parameter of the query
     */
    public TypedQuery setParameter(Parameter<Date> param, Date value, TemporalType temporalType) {
        if (param == null)
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("NULL_PARAMETER_PASSED_TO_SET_PARAMETER"));
        return this.setParameter(param.getName(), value, temporalType);
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
    public <T> TypedQuery setParameter(Parameter<T> param, T value) {
        if (param == null)
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("NULL_PARAMETER_PASSED_TO_SET_PARAMETER"));
        return this.setParameter(param.getName(), value);
    }
    
    /**
     * Bind an instance of java.util.Calendar to a named parameter.
     * 
     * @param name
     * @param value
     * @param temporalType
     * @return the same query instance
     */
    public TypedQuery setParameter(String name, Calendar value, TemporalType temporalType) {
        entityManager.verifyOpen();
        return setParameter(name, convertTemporalType(value, temporalType));
    }
    
    /**
     * Bind an instance of java.util.Date to a named parameter.
     * 
     * @param name
     * @param value
     * @param temporalType
     * @return the same query instance
     */
    public TypedQuery setParameter(String name, Date value, TemporalType temporalType) {
        entityManager.verifyOpen();
        return setParameter(name, convertTemporalType(value, temporalType));
    }
    
    /**
     * Bind an argument to a named parameter.
     * 
     * @param name
     *            the parameter name
     * @param value
     * @return the same query instance
     */
    public TypedQuery setParameter(String name, Object value) {
        try {
            entityManager.verifyOpen();
            setParameterInternal(name, value, false);
            return this;
        } catch (RuntimeException e) {
            setRollbackOnly();
            throw e;
        }
    }

    public String toString() {
        return getClass().getSimpleName() + "(" + String.valueOf(this.databaseQuery) + ")";
    }
}
