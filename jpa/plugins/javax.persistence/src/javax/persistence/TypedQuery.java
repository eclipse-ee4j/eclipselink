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
 * Contributors:
 *     gyorke - Java Persistence 2.0 - Post Proposed Final Draft (March 13, 2009) Updates
 *               Specification available from http://jcp.org/en/jsr/detail?id=317
 *
 * Java(TM) Persistence API, Version 2.0 - EARLY ACCESS
 * This is an implementation of an early-draft specification developed under the 
 * Java Community Process (JCP).  The code is untested and presumed not to be a  
 * compatible implementation of JSR 317: Java(TM) Persistence API, Version 2.0.   
 * We encourage you to migrate to an implementation of the Java(TM) Persistence 
 * API, Version 2.0 Specification that has been tested and verified to be compatible 
 * as soon as such an implementation is available, and we encourage you to retain 
 * this notice in any implementation of Java(TM) Persistence API, Version 2.0 
 * Specification that you distribute.
 ******************************************************************************/
package javax.persistence;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public interface TypedQuery<X> extends Query {
	
    /**
     * Execute a SELECT query and return the query results
     * as a typed List.
     * @return a list of the results
     * @throws IllegalStateException if called for a Java
     *		  Persistence query language UPDATE or DELETE statement
     * @throws QueryTimeoutException if the query execution exceeds
     *	       the query timeout value set
     * @throws TransactionRequiredException if a lock mode has
     *         been set and there is no transaction
     * @throws PessimisticLockException if pessimistic locking
     *         fails and the transaction is rolled back
     * @throws LockTimeoutException if pessimistic locking
     *         fails and only the statement is rolled back
     */
    List<X> getResultList();

    /**
     * Execute a SELECT query that returns a single result.
     * @return the result
     * @throws NoResultException if there is no result
     * @throws NonUniqueResultException if more than one result
     * @throws IllegalStateException if called for a Java
     * 		  Persistence query language UPDATE or DELETE statement
     * @throws QueryTimeoutException if the query execution exceeds
     *		  the query timeout value set
     * @throws TransactionRequiredException if a lock mode has
     *         been set and there is no transaction
     * @throws PessimisticLockException if pessimistic locking
     *         fails and the transaction is rolled back
     * @throws LockTimeoutException if pessimistic locking
     *         fails and only the statement is rolled back
     */
    X getSingleResult();

    /**
     * Set the maximum number of results to retrieve.
     * @param maxResult
     * @return the same query instance
     * @throws IllegalArgumentException if argument is negative
     */
    TypedQuery<X> setMaxResults(int maxResult);

    /**
     * Set the position of the first result to retrieve.
     * @param start position of the first result, numbered from 0
     * @return the same query instance
     * @throws IllegalArgumentException if argument is negative
     */
    TypedQuery<X> setFirstResult(int startPosition);

    /**
     * Set a query hint.
     * If a vendor-specific hint is not recognized, it is silently
     * ignored.  
     * Portable applications should not rely on the standard timeout
     * hint. Depending on the database in use and the locking
     * mechanisms used by the provider, the hint may or may not
     * be observed.
     * @param hintName
     * @param value
     * @return the same query instance
     * @throws IllegalArgumentException if the second argument is not
     * 					valid for the implementation
     */
    TypedQuery<X> setHint(String hintName, Object value);

    /**
     * Set the value of a Parameter object.
     * @param param  parameter to be set
     * @param value  parameter value
     * @return query instance
     * @throws IllegalArgumentException if parameter
     *         does not correspond to a parameter of the
     *         query
     */
    <T> TypedQuery<X> setParameter(Parameter<T> param, T value);

    /**
     * Bind an instance of java.util.Date to a Parameter object.
     * @param parameter object
     * @param value
     * @param temporalType
     * @return the same query instance
     * @throws IllegalArgumentException if position does not
     *         correspond to a parameter of the query
     */
    TypedQuery<X> setParameter(Parameter<Date> param, Date value,  TemporalType temporalType);


    /**
     * Bind an instance of java.util.Calendar to a Parameter object.
     * @param parameter
     * @param value
     * @param temporalType
     * @return the same query instance
     * @throws IllegalArgumentException if position does not
     *         correspond to a parameter of the query
     */
    TypedQuery<X> setParameter(Parameter<Calendar> param, Calendar value,  TemporalType temporalType);

    /**
     * Bind an argument to a named parameter.
     * @param name the parameter name
     * @param value
     * @return the same query instance
     * @throws IllegalArgumentException if parameter name does not
     *         correspond to a parameter of the query or if
     *         the argument is of incorrect type
     */
    TypedQuery<X> setParameter(String name, Object value);

    /**
     * Bind an instance of java.util.Date to a named parameter.
     * @param name
     * @param value
     * @param temporalType
     * @return the same query instance
     * @throws IllegalArgumentException if parameter name does not
     *         correspond to a parameter of the query
     */
    TypedQuery<X> setParameter(String name, Date value, TemporalType temporalType);

    /**
     * Bind an instance of java.util.Calendar to a named parameter.
     * @param name
     * @param value
     * @param temporalType
     * @return the same query instance
     * @throws IllegalArgumentException if parameter name does not
     *         correspond to a parameter of the query 
     */
    TypedQuery<X> setParameter(String name, Calendar value, TemporalType temporalType);

    /**
     * Bind an argument to a positional parameter.
     * @param position
     * @param value
     * @return the same query instance
     * @throws IllegalArgumentException if position does not
     *         correspond to a positional parameter of the
     *         query or if the argument is of incorrect type
     */
    TypedQuery<X> setParameter(int position, Object value);

    /**
     * Bind an instance of java.util.Date to a positional parameter.
     * @param position
     * @param value
     * @param temporalType
     * @return the same query instance
     * @throws IllegalArgumentException if position does not
     *         correspond to a positional parameter of the query
     */
    TypedQuery<X> setParameter(int position, Date value,  TemporalType temporalType);

    /**
     * Bind an instance of java.util.Calendar to a positional
     * parameter.
     * @param position
     * @param value
     * @param temporalType
     * @return the same query instance
     * @throws IllegalArgumentException if position does not
     *         correspond to a positional parameter of the query
     */
    TypedQuery<X> setParameter(int position, Calendar value,  TemporalType temporalType);

    /**
     * Get the parameter of the given name and type.
     * @param name 
     * @param type
     * @return parameter object
     * @throws IllegalArgumentException if the parameter of the
     *         specified name does not exist or is not assignable
     *         to the type
     */
    <T> Parameter<T> getParameter(String name, Class<T> type);

    /**
     * Get the positional parameter with the given position and type.
     * @param position 
     * @param type
     * @return parameter object
     * @throws IllegalArgumentException if the parameter with the
     *         specified position does not exist or is not assignable
     *         to the type
     */
    <T> Parameter<T> getParameter(int position, Class<T> type);

     /**
      * Set the flush mode type to be used for the query execution.
      * The flush mode type applies to the query regardless of the
      * flush mode type in use for the entity manager.
      * @param flushMode
      */
     TypedQuery<X> setFlushMode(FlushModeType flushMode);

     /**
      * Set the lock mode type to be used for the query execution.
      * @param lockMode
      * @throws IllegalStateException if the query is found not to be 
      *		  a Java Persistence query language SELECT query
      * 		  or a Criteria API query
      */
     TypedQuery<X> setLockMode(LockModeType lockMode);

}
