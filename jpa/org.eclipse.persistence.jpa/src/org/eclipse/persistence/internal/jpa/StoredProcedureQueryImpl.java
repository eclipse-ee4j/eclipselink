/*******************************************************************************
 * Copyright (c) 2012 Oracle. All rights reserved.
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

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Parameter;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceException;
import javax.persistence.QueryTimeoutException;
import javax.persistence.StoredProcedureQuery;
import javax.persistence.TemporalType;

import org.eclipse.persistence.internal.localization.ExceptionLocalization;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.queries.DataReadQuery;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ResultSetMappingQuery;
import org.eclipse.persistence.queries.SQLResultSetMapping;
import org.eclipse.persistence.queries.StoredProcedureCall;

/**
 * Concrete JPA query class. The JPA query wraps a StoredProcesureQuery which 
 * is executed.
 */
public class StoredProcedureQueryImpl extends QueryImpl implements StoredProcedureQuery {
    protected List resultList;
    
    /**
     * Base constructor for EJBQueryImpl. Initializes basic variables.
     */
    protected StoredProcedureQueryImpl(EntityManagerImpl entityManager) {
        super(entityManager);
    }

    /**
     * Create an EJBQueryImpl with a DatabaseQuery.
     */
    public StoredProcedureQueryImpl(DatabaseQuery query, EntityManagerImpl entityManager) {
        super(query, entityManager);
    }
    
    /**
     * Create an EJBQueryImpl with either a query name or an jpql string.
     * 
     * @param isNamedQuery
     *            determines whether to treat the queryDescription as jpql or a
     *            query name.
     */
    public StoredProcedureQueryImpl(String name, EntityManagerImpl entityManager) {
        super(entityManager);
        this.queryName = name;
    }
    
    /**
     * Build a ResultSetMappingQuery from a sql result set mapping name and a
     * stored procedure call.
     * 
     * This is called from a named stored procedure that employs result set
     * mapping name(s) which should be available from the session.
     */
    public static DatabaseQuery buildResultSetMappingNameQuery(List<String> resultSetMappingNames, StoredProcedureCall call, Map<String, Object> hints, ClassLoader classLoader, AbstractSession session) {
        ResultSetMappingQuery query = new ResultSetMappingQuery();
        call.setReturnMultipleResultSetCollections(call.hasMultipleResultSets());
        query.setCall(call);
        query.setIsUserDefined(true);
        query.setSQLResultSetMappingNames(resultSetMappingNames);
        
        // apply any query hints
        DatabaseQuery hintQuery = applyHints(hints, query, classLoader, session);

        // apply any query arguments
        applyArguments(call, hintQuery);

        return hintQuery;
    }
    
    /**
     * Build a ResultSetMappingQuery from the sql result set mappings given
     *  a stored procedure call.
     * 
     * This is called from a named stored procedure query that employs result
     * class name(s). The resultSetMappings are build from these class name(s)
     * and are not available from the session.
     */
    public static DatabaseQuery buildResultSetMappingQuery(List<SQLResultSetMapping> resultSetMappings, StoredProcedureCall call, Map<String, Object> hints, ClassLoader classLoader, AbstractSession session) {
        ResultSetMappingQuery query = new ResultSetMappingQuery();
        call.setReturnMultipleResultSetCollections(call.hasMultipleResultSets());
        query.setCall(call);
        query.setIsUserDefined(true);
        query.setSQLResultSetMappings(resultSetMappings);

        // apply any query hints
        DatabaseQuery hintQuery = applyHints(hints, query, classLoader, session);

        // apply any query arguments
        applyArguments(call, hintQuery);

        return hintQuery;
    }
    
    /**
     * Build a ReadAllQuery from a class and stored procedure call.
     */
    public static DatabaseQuery buildStoredProcedureQuery(Class resultClass, StoredProcedureCall call, Map<String, Object> hints, ClassLoader classLoader, AbstractSession session) {
        DatabaseQuery query = new ReadAllQuery(resultClass);
        query.setCall(call);
        query.setIsUserDefined(true);

        // apply any query hints
        query = applyHints(hints, query, classLoader, session);

        // apply any query arguments
        applyArguments(call, query);

        return query;
    }

    /**
     * Build a ResultSetMappingQuery from a sql result set mapping name and a
     * stored procedure call.
     */
    public static DatabaseQuery buildStoredProcedureQuery(StoredProcedureCall call, Map<String, Object> hints, ClassLoader classLoader, AbstractSession session) {
        DataReadQuery query = new DataReadQuery();
        query.setResultType(DataReadQuery.AUTO);

        query.setCall(call);
        query.setIsUserDefined(true);

        // apply any query hints
        DatabaseQuery hintQuery = applyHints(hints, query, classLoader, session);

        // apply any query arguments
        applyArguments(call, hintQuery);

        return hintQuery;
    }

    /**
     * Build a ResultSetMappingQuery from a sql result set mapping name and a
     * stored procedure call.
     */
    public static DatabaseQuery buildStoredProcedureQuery(String sqlResultSetMappingName, StoredProcedureCall call, Map<String, Object> hints, ClassLoader classLoader, AbstractSession session) {
        ResultSetMappingQuery query = new ResultSetMappingQuery();
        query.setSQLResultSetMappingName(sqlResultSetMappingName);
        query.setCall(call);
        query.setIsUserDefined(true);

        // apply any query hints
        DatabaseQuery hintQuery = applyHints(hints, query, classLoader, session);

        // apply any query arguments
        applyArguments(call, hintQuery);

        return hintQuery;
    }
    
    /**
     * Returns true if the first result corresponds to a result set,
     * and false if it is an update count or if there are no results
     * other than through INOUT and OUT parameters, if any.
     * @return true if first result corresponds to result set
     * @throws QueryTimeoutException if the query execution exceeds
     * the query timeout value set and only the statement is
     * rolled back
     * @throws PersistenceException if the query execution exceeds
     * the query timeout value set and the transaction
     * is rolled back
     */
    public boolean execute() {
        // TODO: to implement
        return false;
    }
    
    /**
     * Used to retrieve the values passed back from the procedure
     * through INOUT and OUT parameters.
     * For portability, all results corresponding to result sets
     * and update counts must be retrieved before the values of
     * output parameters.
     * @param position parameter position
     * @return the result that is passed back through the parameter
     * @throws IllegalArgumentException if the position does
     * not correspond to a parameter of the query or is
     * not an INOUT or OUT parameter
     */
    public Object getOutputParameterValue(int position) {
        // TODO: too implement
        return null;
    }

    /**
     * Used to retrieve the values passed back from the procedure
     * through INOUT and OUT parameters.
     * For portability, all results corresponding to result sets
     * and update counts must be retrieved before the values of
     * output parameters.
     * @param parameterName name of the parameter as registered or
     * specified in metadata
     * @return the result that is passed back through the parameter
     * @throws IllegalArgumentException if the parameter name does
     * not correspond to a parameter of the query or is
     * not an INOUT or OUT parameter
     */
    public Object getOutputParameterValue(String parameterName) {
        // TODO: too implement
        return null;
    }
    
    /**
     * Execute the query and return the query results as a List.
     * @return a list of the results
     */
    @Override
    public List getResultList() {
        if (resultList == null) {
            resultList = super.getResultList();
        }
        
        return (List) resultList.remove(0);
    }
    
    /**
     * Returns the update count or -1 if there is no pending result
     * or if the next result is not an update count.
     * @return update count or -1 if there is no pending result or
     * if the next result is not an update count
     * @throws QueryTimeoutException if the query execution exceeds
     * the query timeout value set and only the statement is
     * rolled back
     * @throws PersistenceException if the query execution exceeds
     * the query timeout value set and the transaction
     * is rolled back
     */
    public int getUpdateCount() {
        // TODO: to implement
        return -1;
    }
    

    /**
     * Returns true if the next result corresponds to a result set,
     * and false if it is an update count or if there are no results
     * other than through INOUT and OUT parameters, if any.
     * @return true if next result corresponds to result set
     * @throws QueryTimeoutException if the query execution exceeds
     * the query timeout value set and only the statement is
     * rolled back
     * @throws PersistenceException if the query execution exceeds
     * the query timeout value set and the transaction
     * is rolled back
     */
    public boolean hasMoreResults() {
        return resultList != null && ! resultList.isEmpty();
    }
    
    /**
     * Register a positional parameter.
     * All positional parameters must be registered.
     * @param position parameter position
     * @param type type of the parameter
     * @param mode parameter mode
     * @return the same query instance
     */
    public StoredProcedureQuery registerStoredProcedureParameter(int position, Class type, ParameterMode mode) {
        // TODO: to implement
        return null;
    }

    /**
     * Register a named parameter.
     * When using parameter names, all parameters must be registered
     * in the order in which they occur in the parameter list of the
     * stored procedure.
     * @param parameterName name of the parameter as registered or
     * specified in metadata
     * @param type type of the parameter
     * @param mode parameter mode
     * @return the same query instance
     */
    public StoredProcedureQuery registerStoredProcedureParameter(String parameterName, Class type, ParameterMode mode) {
        // TODO: to implement
        return null;
    }
    
    /**
     * Set the position of the first result to retrieve.
     * 
     * @param start
     *            position of the first result, numbered from 0
     * @return the same query instance
     */
    public StoredProcedureQueryImpl setFirstResult(int startPosition) {
        return (StoredProcedureQueryImpl) super.setFirstResult(startPosition);
    }
    
    /**
     * Set the flush mode type to be used for the query execution.
     * The flush mode type applies to the query regardless of the
     * flush mode type in use for the entity manager.
     * @param flushMode flush mode
     * @return the same query instance
     */
    public StoredProcedureQueryImpl setFlushMode(FlushModeType flushMode) {
        return (StoredProcedureQueryImpl) super.setFlushMode(flushMode);
    }
    
    /**
     * Set a query property or hint. The hints elements may be used to specify 
     * query properties and hints. Properties defined by this specification must 
     * be observed by the provider. Vendor-specific hints that are not 
     * recognized by a provider must be silently ignored. Portable applications 
     * should not rely on the standard timeout hint. Depending on the database
     * in use, this hint may or may not be observed.
     * 
     * @param hintName name of the property or hint
     * @param value value for the property or hint
     * @return the same query instance
     * @throws IllegalArgumentException if the second argument is not valid for 
     * the implementation
     */
    public StoredProcedureQuery setHint(String hintName, Object value) {
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
    public StoredProcedureQueryImpl setLockMode(LockModeType lockMode) {
        return (StoredProcedureQueryImpl) super.setLockMode(lockMode);
    }
    
    /**
     * Set the maximum number of results to retrieve.
     * 
     * @param maxResult
     * @return the same query instance
     */
    public StoredProcedureQueryImpl setMaxResults(int maxResult) {
        return (StoredProcedureQueryImpl) super.setMaxResults(maxResult);
    }

    /**
     * Bind an instance of java.util.Calendar to a positional parameter.
     * 
     * @param position
     * @param value
     * @param temporalType
     * @return the same query instance
     * @throws IllegalArgumentException if position does not correspond to a 
     * positional parameter of the query or if the value argument is of 
     * incorrect type
     */
    public StoredProcedureQuery setParameter(int position, Calendar value, TemporalType temporalType) {
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
     * @throws IllegalArgumentException if position does not correspond to a 
     * positional parameter of the query or if the value argument is of 
     * incorrect type
     */
    public StoredProcedureQuery setParameter(int position, Date value, TemporalType temporalType) {
        entityManager.verifyOpen();
        return setParameter(position, convertTemporalType(value, temporalType));
    }
    
    /**
     * Bind an argument to a positional parameter.
     * 
     * @param position
     * @param value
     * @return the same query instance
     * @throws IllegalArgumentException if position does not correspond to a 
     * positional parameter of the query or if the argument is of incorrect type
     */
    public StoredProcedureQuery setParameter(int position, Object value) {
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
     * @param param 
     * @param value 
     * @param temporalType
     * @return the same query instance
     * @throws IllegalArgumentException if the parameter does not correspond to 
     * a parameter of the query
     */
    public StoredProcedureQuery setParameter(Parameter<Calendar> param, Calendar value, TemporalType temporalType) {
        if (param == null) {
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("NULL_PARAMETER_PASSED_TO_SET_PARAMETER"));
        }
        
        return this.setParameter(param.getName(), value, temporalType);
    }

    /**
     * Bind an instance of java.util.Date to a Parameter object.
     * 
     * @param param 
     * @param value 
     * @param temporalType 
     * @return the same query instance
     * @throws IllegalArgumentException if the parameter does not correspond to 
     * a parameter of the query
     */
    public StoredProcedureQuery setParameter(Parameter<Date> param, Date value, TemporalType temporalType) {
        if (param == null) {
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("NULL_PARAMETER_PASSED_TO_SET_PARAMETER"));
        }
        
        return this.setParameter(param.getName(), value, temporalType);
    }
    
    /**
     * Bind the value of a Parameter object.
     * 
     * @param param
     * @param value
     * @return the same query instance
     * @throws IllegalArgumentException if the parameter does not correspond to 
     * a parameter of the query
     */
    public <T> StoredProcedureQuery setParameter(Parameter<T> param, T value) {
        if (param == null) {
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("NULL_PARAMETER_PASSED_TO_SET_PARAMETER"));
        }
        
        return this.setParameter(param.getName(), value);
    }

    /**
     * Bind an instance of java.util.Calendar to a named parameter.
     * 
     * @param name
     * @param value
     * @param temporalType
     * @return the same query instance
     * @throws IllegalArgumentException if the parameter name does not 
     * correspond to a parameter of the query or if the value argument is of 
     * incorrect type
     */
    public StoredProcedureQuery setParameter(String name, Calendar value, TemporalType temporalType) {
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
     * @throws IllegalArgumentException if the parameter name does not 
     * correspond to a parameter of the query or if the value argument is of 
     * incorrect type
     */
    public StoredProcedureQuery setParameter(String name, Date value, TemporalType temporalType) {
        entityManager.verifyOpen();
        return setParameter(name, convertTemporalType(value, temporalType));
    }
    
    /**
     * Bind an argument to a named parameter.
     * 
     * @param name 
     * @param value
     * @return the same query instance
     * @throws IllegalArgumentException if the parameter name does not 
     * correspond to a parameter of the query or if the argument is of incorrect 
     * type
     */
    public StoredProcedureQuery setParameter(String name, Object value) {
        try {
            entityManager.verifyOpen();
            setParameterInternal(name, value, false);
            return this;
        } catch (RuntimeException e) {
            setRollbackOnly();
            throw e;
        }
    }
}

