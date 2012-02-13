/*******************************************************************************
 * Copyright (c) 2008 - 2012 Oracle Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Linda DeMichiel - Java Persistence 2.1
 *     Linda DeMichiel - Java Persistence 2.0
 *
 ******************************************************************************/ 
package javax.persistence;

import java.util.Calendar;
import java.util.Date;

/**
 * Interface used to control stored procedure query execution.
 *
 * @see Query
 * @see Parameter
 *
 * @since Java Persistence 2.1
 */
public interface StoredProcedureQuery extends Query {

    /**
     * Set a query property or hint. The hints elements may be used 
     * to specify query properties and hints. Properties defined by
     * this specification must be observed by the provider. 
     * Vendor-specific hints that are not recognized by a provider
     * must be silently ignored. Portable applications should not
     * rely on the standard timeout hint. Depending on the database
     * in use, this hint may or may not be observed.
     * @param hintName  name of the property or hint
     * @param value  value for the property or hint
     * @return the same query instance
     * @throws IllegalArgumentException if the second argument is not
     *         valid for the implementation
     */
    StoredProcedureQuery setHint(String hintName, Object value);

    /**
     * Bind the value of a <code>Parameter</code> object.
     * @param param  parameter object
     * @param value  parameter value
     * @return the same query instance
     * @throws IllegalArgumentException if the parameter does not
     *         correspond to a parameter of the query
     */
    <T> StoredProcedureQuery setParameter(Parameter<T> param, 
                                          T value);

    /**
     * Bind an instance of <code>java.util.Calendar</code> to a <code>Parameter</code> object.
     * @param param parameter object
     * @param value  parameter value
     * @param temporalType  temporal type
     * @return the same query instance
     * @throws IllegalArgumentException if the parameter does not
     *         correspond to a parameter of the query
     */
    StoredProcedureQuery setParameter(Parameter<Calendar> param,
                                      Calendar value, 
                                      TemporalType temporalType);

    /**
     * Bind an instance of <code>java.util.Date</code> to a <code>Parameter</code> object.
     * @param param parameter object
     * @param value  parameter value
     * @param temporalType  temporal type
     * @return the same query instance
     * @throws IllegalArgumentException if the parameter does not
     *         correspond to a parameter of the query
     */
    StoredProcedureQuery setParameter(Parameter<Date> param, 
                                      Date value, 
                                      TemporalType temporalType);

    /**
     * Bind an argument value to a named parameter.
     * @param name  parameter name
     * @param value  parameter value
     * @return the same query instance
     * @throws IllegalArgumentException if the parameter name does 
     *         not correspond to a parameter of the query or if the
     *         argument is of incorrect type
     */
    StoredProcedureQuery setParameter(String name, Object value);

    /**
     * Bind an instance of <code>java.util.Calendar</code> to a named parameter.
     * @param name  parameter name
     * @param value  parameter value
     * @param temporalType  temporal type
     * @return the same query instance
     * @throws IllegalArgumentException if the parameter name does 
     *         not correspond to a parameter of the query or if the
     *         value argument is of incorrect type
     */
    StoredProcedureQuery setParameter(String name, 
                                      Calendar value, 
                                      TemporalType temporalType);

    /**
     * Bind an instance of <code>java.util.Date</code> to a named parameter.
     * @param name  parameter name
     * @param value  parameter value
     * @param temporalType  temporal type
     * @return the same query instance
     * @throws IllegalArgumentException if the parameter name does 
     *         not correspond to a parameter of the query or if the
     *         value argument is of incorrect type
     */
    StoredProcedureQuery setParameter(String name, 
                                      Date value, 
                                      TemporalType temporalType);

    /**
     * Bind an argument value to a positional parameter.
     * @param position  position
     * @param value  parameter value
     * @return the same query instance
     * @throws IllegalArgumentException if position does not
     *         correspond to a positional parameter of the query
     *         or if the argument is of incorrect type
     */
    StoredProcedureQuery setParameter(int position, Object value);

    /**
     * Bind an instance of <code>java.util.Calendar</code> to a positional
     * parameter.
     * @param position  position
     * @param value  parameter value
     * @param temporalType  temporal type
     * @return the same query instance
     * @throws IllegalArgumentException if position does not
     *         correspond to a positional parameter of the query or
     *         if the value argument is of incorrect type
     */
    StoredProcedureQuery setParameter(int position, 
                                      Calendar value,  
                                      TemporalType temporalType);

    /**
     * Bind an instance of <code>java.util.Date</code> to a positional parameter.
     * @param position  position
     * @param value  parameter value
     * @param temporalType  temporal type
     * @return the same query instance
     * @throws IllegalArgumentException if position does not
     *         correspond to a positional parameter of the query or
     *         if the value argument is of incorrect type
     */
    StoredProcedureQuery setParameter(int position, 
                                      Date value,  
                                      TemporalType temporalType);

    /**
     * Set the flush mode type to be used for the query execution.
     * The flush mode type applies to the query regardless of the
     * flush mode type in use for the entity manager.
     * @param flushMode  flush mode
     * @return the same query instance
     */
    StoredProcedureQuery setFlushMode(FlushModeType flushMode);

    /**
     * Register a positional parameter.
     * All parameters must be registered.
     * @param position  parameter position
     * @param type  type of the parameter
     * @param mode  parameter mode 
     * @return the same query instance
     */
    StoredProcedureQuery registerStoredProcedureParameter(
	  int position,
	  Class type,
	  ParameterMode mode);

    /**
     * Register a named parameter.
     * When using parameter names, all parameters must be registered
     * in the order in which they occur in the parameter list of the 
     * stored procedure.
     * @param parameterName  name of the parameter as registered or
     *             specified in metadata
     * @param type  type of the parameter
     * @param mode  parameter mode 
     * @return the same query instance
     */
    StoredProcedureQuery registerStoredProcedureParameter(
	  String parameterName,
	  Class type,
	  ParameterMode mode);

    /**
     *  Retrieve a value passed back from the procedure
     *  through an INOUT or OUT parameter.
     *  For portability, all results corresponding to result sets
     *  and update counts must be retrieved before the values of 
     *  output parameters.
     *  @param position  parameter position
     *  @return the result that is passed back through the parameter
     *  @throws IllegalArgumentException if the position does
     *          not correspond to a parameter of the query or is
     *          not an INOUT or OUT parameter
     */
    Object getOutputParameterValue(int position);

    /**
     *  Retrieve a value passed back from the procedure
     *  through an INOUT or OUT parameter.
     *  For portability, all results corresponding to result sets
     *  and update counts must be retrieved before the values of 
     *  output parameters.
     *  @param parameterName  name of the parameter as registered or
     *              specified in metadata
     *  @return the result that is passed back through the parameter
     *  @throws IllegalArgumentException if the parameter name does
     *          not correspond to a parameter of the query or is
     *          not an INOUT or OUT parameter
     */
    Object getOutputParameterValue(String parameterName);

    /**
     * Return true if the first result corresponds to a result set,
     * and false if it is an update count or if there are no results
     * other than through INOUT and OUT parameters, if any.
     * @return  true if first result corresponds to result set
     * @throws QueryTimeoutException if the query execution exceeds
     *         the query timeout value set and only the statement is
     *         rolled back
     * @throws PersistenceException if the query execution exceeds 
     *         the query timeout value set and the transaction 
     *         is rolled back 
     */
    boolean execute();

    /**
     * Return true if the next result corresponds to a result set,
     * and false if it is an update count or if there are no results
     * other than through INOUT and OUT parameters, if any.
     * @return  true if next result corresponds to result set
     * @throws QueryTimeoutException if the query execution exceeds
     *         the query timeout value set and only the statement is
     *         rolled back
     * @throws PersistenceException if the query execution exceeds 
     *         the query timeout value set and the transaction 
     *         is rolled back 
     */
    boolean hasMoreResults();

    /**
     * Return the update count or  -1 if there is no pending result
     * or if the next result is not an update count.
     * @return  update count or -1 if there is no pending result or if
     *          the next result is not an update count
     * @throws QueryTimeoutException if the query execution exceeds
     *         the query timeout value set and only the statement is
     *         rolled back
     * @throws PersistenceException if the query execution exceeds 
     *         the query timeout value set and the transaction 
     *         is rolled back 
     */
    int getUpdateCount();

}
