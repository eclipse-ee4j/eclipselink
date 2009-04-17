/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved. 
 * 
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 * 
 * The API for this class and its comments are derived from the JPA 2.0 specification 
 * which is developed under the Java Community Process (JSR 317) and is copyright 
 * Sun Microsystems, Inc. 
 *
 * Contributors:
 *     dclarke - Java Persistence 2.0 - Proposed Final Draft (March 13, 2009)
 *     			 Specification and licensing terms available from
 *     		   	 http://jcp.org/en/jsr/detail?id=317
 *
 * EARLY ACCESS - PUBLIC DRAFT
 * This is an implementation of an early-draft specification developed under the 
 * Java Community Process (JCP) and is made available for testing and evaluation 
 * purposes only. The code is not compatible with any specification of the JCP.
 ******************************************************************************/

package javax.persistence;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Interface used to control query execution.
 * 
 * @since Java Persistence 2.0
 */
public interface Query {
    /**
     * Execute a query and return the results as a typed List.
     * 
     * @return a list of the results
     * @throws IllegalStateException
     *             if called for a Java Persistence query language UPDATE or
     *             DELETE statement
     * @throws QueryTimeoutException
     *             if the query execution exceeds the query timeout value set
     * @throws TransactionRequiredException
     *             if a lock mode has been set and there is no transaction
     * @throws PessimisticLockException
     *             if pessimistic locking fails and the transaction is rolled
     *             back
     * @throws LockTimeoutException
     *             if pessimistic locking fails and only the statement is rolled
     *             back
     */
    List<Result> getTypedResultList();

    /**
     * Execute a query that returns a single, typed result.
     * 
     * @return the result
     * @throws NoResultException
     *             if there is no result
     * @throws NonUniqueResultException
     *             if more than one result
     * @throws IllegalStateException
     *             if called for a Java Persistence query language UPDATE or
     *             DELETE statement
     * @throws QueryTimeoutException
     *             if the query execution exceeds the query timeout value set
     * @throws TransactionRequiredException
     *             if a lock mode has been set and there is no transaction
     * @throws PessimisticLockException
     *             if pessimistic locking fails and the transaction is rolled
     *             back
     * @throws LockTimeoutException
     *             if pessimistic locking fails and only the statement is rolled
     *             back
     */
    Result getTypedSingleResult();

    /**
     * Execute a SELECT query and return the query results as an untyped List.
     * 
     * @return a list of the results
     * @throws IllegalStateException
     *             if called for a Java Persistence query language UPDATE or
     *             DELETE statement
     * @throws QueryTimeoutException
     *             if the query execution exceeds the query timeout value set
     * @throws TransactionRequiredException
     *             if a lock mode has been set and there is no transaction
     * @throws PessimisticLockException
     *             if pessimistic locking fails and the transaction is rolled
     *             back
     * @throws LockTimeoutException
     *             if pessimistic locking fails and only the statement is rolled
     *             back
     */
    List getResultList();

    /**
     * Execute a SELECT query that returns a single untyped result.
     * 
     * @return the result
     * @throws NoResultException
     *             if there is no result
     * @throws NonUniqueResultException
     *             if more than one result
     * @throws IllegalStateException
     *             if called for a Java Persistence query language UPDATE or
     *             DELETE statement
     * @throws QueryTimeoutException
     *             if the query execution exceeds the query timeout value set
     * @throws TransactionRequiredException
     *             if a lock mode has been set and there is no transaction
     * @throws PessimisticLockException
     *             if pessimistic locking fails and the transaction is rolled
     *             back
     * @throws LockTimeoutException
     *             if pessimistic locking fails and only the statement is rolled
     *             back
     */
    Object getSingleResult();

    /**
     * Execute an update or delete statement.
     * 
     * @return the number of entities updated or deleted
     * @throws IllegalStateException
     *             if called for a Java Persistence query language SELECT
     *             statement or for a criteria query
     * @throws TransactionRequiredException
     *             if there is no transaction
     * @throws QueryTimeoutException
     *             if the statement execution exceeds the query timeout value
     *             set
     */
    int executeUpdate();

    /**
     * Set the maximum number of results to retrieve.
     * 
     * @param maxResult
     * @return the same query instance
     * @throws IllegalArgumentException
     *             if argument is negative
     */
    Query setMaxResults(int maxResult);

    /**
     * The maximum number of results the query object was set to retrieve.
     * Returns Integer.MAX_VALUE if setMaxResults was not applied to the query
     * object.
     * 
     * @return maximum number of results
     */
    int getMaxResults();

    /**
     * Set the position of the first result to retrieve.
     * 
     * @param start
     *            position of the first result, numbered from 0
     * @return the same query instance
     * @throws IllegalArgumentException
     *             if argument is negative
     */
    Query setFirstResult(int startPosition);

    /**
     * The position of the first result the query object was set to retrieve.
     * Returns 0 if setFirstResult was not applied to the query object.
     * 
     * @return position of first result
     */
    int getFirstResult();

    /**
     * Set a query hint. If a vendor-specific hint is not recognized, it is
     * silently ignored. Portable applications should not rely on the standard
     * timeout hint. Depending on the database in use and the locking mechanisms
     * used by the provider, the hint may or may not be observed.
     * 
     * @param hintName
     * @param value
     * @return the same query instance
     * @throws IllegalArgumentException
     *             if the second argument is not valid for the implementation
     */
    Query setHint(String hintName, Object value);

    /**
     * Get the hints and associated values that are in effect for the query
     * instance.
     * 
     * @return query hints
     */
    Map<String, Object> getHints();

    /**
     * Get the names of the hints that are supported for query objects. These
     * hints correspond to hints that may be passed to the methods of the Query
     * interface that take hints as arguments or used with the NamedQuery and
     * NamedNativeQuery annotations. These include all standard query hints as
     * well as vendor-specific hints supported by the provider. These hints may
     * or may not currently be in effect.
     * 
     * @return hints
     */
    Set<String> getSupportedHints();

    /**
     * Bind the value of a Parameter object.
     * 
     * @param param
     *            parameter to be set
     * @param value
     *            parameter value
     * @return query instance
     * @throws IllegalArgumentException
     *             if parameter does not correspond to a parameter of the query
     */
    <T> Query setParameter(javax.persistence.Parameter<T> param, T value);

    /**
     * Bind an argument to a named parameter.
     * 
     * @param name
     *            the parameter name
     * @param value
     * @return the same query instance
     * @throws IllegalArgumentException
     *             if parameter name does not correspond to a parameter of the
     *             query or if the argument is of incorrect type
     */
    Query setParameter(String name, Object value);

    /**
     * Bind an instance of java.util.Date to a named parameter.
     * 
     * @param name
     * @param value
     * @param temporalType
     * @return the same query instance
     * @throws IllegalArgumentException
     *             if parameter name does not correspond to a parameter of the
     *             query
     */
    Query setParameter(String name, Date value, TemporalType temporalType);

    /**
     * Bind an instance of java.util.Calendar to a named parameter.
     * 
     * @param name
     * @param value
     * @param temporalType
     * @return the same query instance
     * @throws IllegalArgumentException
     *             if parameter name does not correspond to a parameter of the
     *             query
     */
    Query setParameter(String name, Calendar value, TemporalType temporalType);

    /**
     * Bind an argument to a positional parameter.
     * 
     * @param position
     * @param value
     * @return the same query instance
     * @throws IllegalArgumentException
     *             if position does not correspond to a positional parameter of
     *             the query or if the argument is of incorrect type
     */
    Query setParameter(int position, Object value);

    /**
     * Bind an instance of java.util.Date to a positional parameter.
     * 
     * @param position
     * @param value
     * @param temporalType
     * @return the same query instance
     * @throws IllegalArgumentException
     *             if position does not correspond to a positional parameter of
     *             the query
     */
    Query setParameter(int position, Date value, TemporalType temporalType);

    /**
     * Bind an instance of java.util.Calendar to a positional parameter.
     * 
     * @param position
     * @param value
     * @param temporalType
     * @return the same query instance
     * @throws IllegalArgumentException
     *             if position does not correspond to a positional parameter of
     *             the query
     */
    Query setParameter(int position, Calendar value, TemporalType temporalType);

    /**
     * Get the query parameter objects. Returns empty set if the query has no
     * parameters.
     * 
     * @return parameter objects
     */
    Set<Parameter<?>> getParameters();

    /**
     * Get the parameter of the given name and type.
     * 
     * @return parameter object
     * @throws IllegalArgumentException
     *             if the parameter of the specified name and type doesn’t exist
     */
    <T> Parameter<T> getParameter(String name, Class<T> type);

    /**
     * Get the positional parameter with the given position and type.
     * 
     * @return parameter object
     * @throws IllegalArgumentException
     *             if the parameter with the specified position and type doesn’t
     *             exist
     */
    <T> Parameter<T> getParameter(int position, Class<T> type);

    /**
     * Return the value that has been bound to the parameter.
     * 
     * @param param
     *            parameter object
     * @return parameter value
     * @throws IllegalStateException
     *             if the parameter has not been been bound
     */
    <T> T getParameterValue(Parameter<T> param);

    /**
     * Get the result item with the given alias and type. If the type of the
     * specified item is not assignable to the specified type, the
     * IllegalArgumentException is thrown.
     * 
     * @return result item
     * @throws IllegalArgumentException
     *             if the result item of the specified name does not exist or is
     *             not assignable to the specified type
     */
    <T> ResultItem<T> getResultItem(String alias, Class<T> type);

    /**
     * Get the result item with the given position in the result list and type.
     * The first position is 0. If the type of the specified item is not
     * assignable to the specified type, the IllegalArgumentException is thrown.
     * 
     * @return result item object
     * @throws IllegalArgumentException
     *             if the result item with the specified position does not exist
     *             or is not assignable to the specified type
     */
    <T> ResultItem<T> getResultItem(int position, Class<T> type);

    /**
     * Get the result items of the query.
     * 
     * @return result item list
     */
    List<ResultItem<?>> getResultItems();

    /**
     * Set the flush mode type to be used for the query execution. The flush
     * mode type applies to the query regardless of the flush mode type in use
     * for the entity manager.
     * 
     * @param flushMode
     */
    Query setFlushMode(FlushModeType flushMode);

    /**
     * The flush mode in effect for the query execution. If a flush mode has not
     * been set for the query object, returns the flush mode in effect for the
     * entity manager.
     * 
     * @return flush mode
     */
    FlushModeType getFlushMode();

    /**
     * Set the lock mode type to be used for the query execution.
     * 
     * @param lockMode
     * @throws IllegalStateException
     *             if the query is found not to be a Java Persistence query
     *             language SELECT query or a Criteria API query
     */
    Query setLockMode(LockModeType lockMode);

    /**
     * Get the current lock mode for the query.
     * 
     * @return lock mode
     * @throws IllegalStateException
     *             if the query is found not to be a Java Persistence query
     *             language SELECT query or a Criteria API query
     */
    LockModeType getLockMode();

    /**
     * Return an object of the specified type to allow access to the
     * provider-specific API. If the provider's Query implementation does not
     * support the specified class, the PersistenceException is thrown.
     * 
     * @param cls
     *            the class of the object to be returned. This is normally
     *            either the underlying Query implementation class or an
     *            interface that it implements.
     * @return an instance of the specified class
     * @throws PersistenceException
     *             if the provider does not support the call.
     */
    <T> T unwrap(Class<T> cls);
}
