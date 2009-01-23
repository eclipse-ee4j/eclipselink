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
 *     dclarke - Java Persistence API 2.0 Public Draft
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
 * @since Java Persistence API
 */
public interface Query {

	/**
	 * Execute a SELECT query and return the query results as a List.
	 * 
	 * @return a list of the results
	 * @throws IllegalStateException
	 *             if called for a Java Persistence query language UPDATE or
	 *             DELETE statement
	 */
	public List getResultList();

	/**
	 * Execute a SELECT query that returns a single result.
	 * 
	 * @return the result
	 * @throws NoResultException
	 *             if there is no result
	 * @throws NonUniqueResultException
	 *             if more than one result
	 * @throws IllegalStateException
	 *             if called for a Java Persistence query language UPDATE or
	 *             DELETE statement
	 */
	public Object getSingleResult();

	/**
	 * Execute an update or delete statement.
	 * 
	 * @return the number of entities updated or deleted
	 * @throws IllegalStateException
	 *             if called for a Java Persistence query language SELECT
	 *             statement
	 * @throws TransactionRequiredException
	 *             if there is no transaction
	 */
	public int executeUpdate();

	/**
	 * Set the maximum number of results to retrieve.
	 * 
	 * @param maxResult
	 * @return the same query instance
	 * @throws IllegalArgumentException
	 *             if argument is negative
	 */
	public Query setMaxResults(int maxResult);

	/**
	 * The maximum number of results the query object was set to retrieve.
	 * Returns Integer.MAX_VALUE if setMaxResults was not applied to the query
	 * object.
	 * 
	 * @return maximum number of results
	 * @since Java Persistence API 2.0
	 */
	public int getMaxResults();

	/**
	 * Set the position of the first result to retrieve.
	 * 
	 * @param startPosition
	 *            the start position of the first result, numbered from 0
	 * @return the same query instance
	 * @throws IllegalArgumentException
	 *             if argument is negative
	 */
	public Query setFirstResult(int startPosition);

	/**
	 * The position of the first result the query object was set to retrieve.
	 * Returns 0 if setFirstResult was not applied to the query object.
	 * 
	 * @return position of first result
	 * @since Java Persistence API 2.0
	 */
	public int getFirstResult();

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
	public Query setHint(String hintName, Object value);

	/**
	 * Get the hints and associated values that are in effect for the query
	 * instance.
	 * 
	 * @return query hints
	 * @since Java Persistence API 2.0
	 */
	public Map<String, Object> getHints();

	/**
	 * Get the names of the hints that are supported for query objects. These
	 * hints correspond to hints that may be passed to the methods of the Query
	 * interface that take hints as arguments or used with the NamedQuery and
	 * NamedNativeQuery annotations. These include all standard query hints as
	 * well as vendor-specific hints supported by the provider. These hints may
	 * or may not currently be in effect.
	 * 
	 * @return hints
	 * @since Java Persistence API 2.0
	 */
	public Set<String> getSupportedHints();

	/**
	 * Bind an argument to a named parameter.
	 * 
	 * @param name
	 *            the parameter name
	 * @param value
	 * @return the same query instance
	 * @throws IllegalArgumentException
	 *             if parameter name does not correspond to parameter in query
	 *             string or argument is of incorrect type
	 */
	public Query setParameter(String name, Object value);

	/**
	 * Bind an instance of java.util.Date to a named parameter.
	 * 
	 * @param name
	 * @param value
	 * @param temporalType
	 * @return the same query instance
	 * @throws IllegalArgumentException
	 *             if parameter name does not correspond to parameter in query
	 *             string
	 */
	public Query setParameter(String name, Date value, TemporalType temporalType);

	/**
	 * Bind an instance of java.util.Calendar to a named parameter.
	 * 
	 * @param name
	 * @param value
	 * @param temporalType
	 * @return the same query instance
	 * @throws IllegalArgumentException
	 *             if parameter name does not correspond to parameter in query
	 *             string
	 */
	public Query setParameter(String name, Calendar value, TemporalType temporalType);

	/**
	 * Bind an argument to a positional parameter.
	 * 
	 * @param position
	 * @param value
	 * @return the same query instance
	 * @throws IllegalArgumentException
	 *             if position does not correspond to positional parameter of
	 *             query or argument is of incorrect type
	 */
	public Query setParameter(int position, Object value);

	/**
	 * Bind an instance of java.util.Date to a positional parameter.
	 * 
	 * @param position
	 * @param value
	 * @param temporalType
	 * @return the same query instance
	 * @throws IllegalArgumentException
	 *             if position does not correspond to positional parameter of
	 *             query
	 */
	public Query setParameter(int position, Date value, TemporalType temporalType);

	/**
	 * Bind an instance of java.util.Calendar to a positional parameter.
	 * 
	 * @param position
	 * @param value
	 * @param temporalType
	 * @return the same query instance
	 * @throws IllegalArgumentException
	 *             if position does not correspond to positional parameter of
	 *             query
	 */
	public Query setParameter(int position, Calendar value, TemporalType temporalType);

	/**
	 * Get the parameters names and associated values of the parameters that are
	 * bound for the query instance. Returns empty map if no parameters have
	 * been bound or if the query does not use named parameters.
	 * 
	 * @return named parameters
	 * @since Java Persistence API 2.0
	 */
	public Map<String, Object> getNamedParameters();

	/**
	 * Get the values of the positional parameters that are bound for the query
	 * instance. Positional positions are listed in order of position. Returns
	 * empty list if no parameters have been bound or if the query does not use
	 * positional parameters.
	 * 
	 * @return positional parameters
	 * @since Java Persistence API 2.0
	 */
	public List getPositionalParameters();

	/**
	 * Set the flush mode type to be used for the query execution. The flush
	 * mode type applies to the query regardless of the flush mode type in use
	 * for the entity manager.
	 * 
	 * @param flushMode
	 */
	public Query setFlushMode(FlushModeType flushMode);

	/**
	 * The flush mode in effect for the query execution. If a flush mode has not
	 * been set for the query object, returns the flush mode in effect for the
	 * entity manager.
	 * 
	 * @return flush mode
	 * @since Java Persistence API 2.0
	 */
	public FlushModeType getFlushMode();

	/**
	 * Set the lock mode type to be used for the query execution.
	 * 
	 * @param lockMode
	 * @throws IllegalStateException
	 *             if not a Java Persistence query language SELECT query
	 * @since Java Persistence API 2.0
	 */
	public Query setLockMode(LockModeType lockMode);

	/**
	 * Get the current lock mode for the query.
	 * 
	 * @return lock mode
	 * @throws IllegalStateException
	 *             if not a Java Persistence query language SELECT query
	 * @since Java Persistence API 2.0
	 */
	public LockModeType getLockMode();

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
	 * @since Java Persistence API 2.0
	 */
	public <T> T unwrap(Class<T> cls);
}
