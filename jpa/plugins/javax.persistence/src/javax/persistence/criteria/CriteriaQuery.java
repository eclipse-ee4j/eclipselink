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
 *     dclarke - Java Persistence 2.0 - Proposed Final Draft (March 13, 2009)
 *               Specification available from http://jcp.org/en/jsr/detail?id=317
 *     gyorke  - Post PFD updates
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
package javax.persistence.criteria;

import java.util.List;
import java.util.Set;

/**
 * The interface CriteriaQuery defines functionality that is specific to
 * top-level queries.
 * 
 * A top-level query has an ordered list of selections.
 */
public interface CriteriaQuery<T> extends AbstractQuery<T> {
    /**
     * Specify the item that is to be returned in the query result.
     * Replaces the previously specified selection, if any.
     * @param selection  selection specifying the item that
     *        is to be returned in the query result
     * @return the modified query
     */
    CriteriaQuery<T> select(Selection<? extends T> selection);

    /**
     * Specify the selection items that are to be returned in the 
     * query result.
     * Replaces the previously specified selection(s), if any.
     *
     * The type of the result of the query execution depends on
     * the specification of the type of the criteria query object 
     * created as well as the arguments to the multiselect method.
     * An argument to the multiselect method must not be a tuple-
     * or array-valued compound selection item. 
     * The semantics of this method are as follows:
     *
     * If the type of the criteria query is CriteriaQuery<Tuple>
     * (i.e., a criteria query object created by either the 
     * createTupleQuery method or by passing a Tuple class argument 
     * to the createQuery method), a Tuple object corresponding to 
     * the arguments of the multiselect method will be instantiated 
     * and returned for each row that results from the query execution.
     *
     * If the type of the criteria query is CriteriaQuery<X> for
     * some user-defined class X (i.e., a criteria query object
     * created by passing a X class argument to the createQuery 
     * method), then the arguments to the multiselect method will be 
     * passed to the X constructor and an instance of type X will be 
     * returned for each row.  
     *
     * If the type of the criteria query is CriteriaQuery<X[]> for
     * some class X, an instance of type X[] will be returned for 
     * each row.   The elements of the array will correspond to the 
     * arguments of the multiselect method.  
     *
     * If the type of the criteria query is CriteriaQuery<Object>
     * or if the criteria query was created without specifying a type,
     * and only a single argument is passed to the multiselect method,
     * an instance of type Object will be returned for each row.
     *
     * If the type of the criteria query is CriteriaQuery<Object>
     * or if the criteria query was created without specifying a type,
     * and more than one argument is passed to the multiselect method,
     * an instance of type Object[] will be instantiated and returned
     * for each row.  The elements of the array will correspond to the
     * arguments to the multiselect method.
     *
     * @param selections  selection items corresponding to the
     *        results to be returned by the query
     * @return the modified query
     */
    CriteriaQuery<T> multiselect(Selection<?>... selections);

    /**
     * Specify the selection items that are to be returned in the 
     * query result.
     * Replaces the previously specified selection(s), if any.
     *
     * The type of the result of the query execution depends on
     * the specification of the type of the criteria query object 
     * created as well as the argument to the multiselect method.
     * An element of the list passed to the multiselect method 
     * must not be a tuple- or array-valued compound selection item. 
     * The semantics of this method are as follows:
     *
     * If the type of the criteria query is CriteriaQuery<Tuple>
     * (i.e., a criteria query object created by either the 
     * createTupleQuery method or by passing a Tuple class argument 
     * to the createQuery method), a Tuple object corresponding to 
     * the elements of the list passed to the multiselect method 
     * will be instantiated and returned for each row that results 
     * from the query execution.
     *
     * If the type of the criteria query is CriteriaQuery<X> for
     * some user-defined class X (i.e., a criteria query object
     * created by passing a X class argument to the createQuery 
     * method), then the elements of the list passed to the
     * multiselect method will be passed to the X constructor and 
     * an instance of type X will be returned for each row.  
     *
     * If the type of the criteria query is CriteriaQuery<X[]> for
     * some class X, an instance of type X[] will be returned for 
     * each row.   The elements of the array will correspond to the 
     * elements of the list passed to the multiselect method.  
     *
     * If the type of the criteria query is CriteriaQuery<Object>
     * or if the criteria query was created without specifying a type,
     * and the list passed to the multiselect method contains only
     * a single element, an instance of type Object will be returned 
     * for each row.
     *
     * If the type of the criteria query is CriteriaQuery<Object>
     * or if the criteria query was created without specifying a type,
     * and the list passed to the multiselect method contains more
     * than one element, an instance of type Object[] will be 
     * instantiated and returned for each row.  The elements of the 
     * array will correspond to the elements of the list passed to
     * the multiselect method.
     *
     * @param selectionList  list of selection items corresponding 
     *        to the results to be returned by the query
     * @return the modified query
     */
    CriteriaQuery<T> multiselect(List<Selection<?>> selectionList);

    /**
     * Modify the query to restrict the query result according
     * to the specified boolean expression.
     * Replaces the previously added restriction(s), if any.
     * This method only overrides the return type of the 
     * corresponding AbstractQuery method.
     * @param restriction  a simple or compound boolean expression
     * @return the modified query
     */
    CriteriaQuery<T> where(Expression<Boolean> restriction);

    /**
     * Modify the query to restrict the query result according to the
     * conjunction of the specified restriction predicates. Replaces the
     * previously added restriction(s), if any. If no restrictions are
     * specified, any previously added restrictions are simply removed. This
     * method only overrides the return type of the corresponding AbstractQuery
     * method.
     * 
     * @param restrictions
     *            zero or more restriction predicates
     * @return the modified query
     */
    CriteriaQuery<T> where(Predicate... restrictions);

    /**
     * Specify the expressions that are used to form groups over the query
     * results. Replaces the previous specified grouping expressions, if any. If
     * no grouping expressions are specified, any previously added grouping
     * expressions are simply removed. This method only overrides the return
     * type of the corresponding AbstractQuery method.
     * 
     * @param grouping
     *            zero or more grouping expressions
     * @return the modified query
     */
    CriteriaQuery<T> groupBy(Expression<?>... grouping);

    /**
     * Specify a restriction over the groups of the query. Replaces the previous
     * having restriction(s), if any. This method only overrides the return type
     * of the corresponding AbstractQuery method.
     * 
     * @param restriction
     *            a simple or compound boolean expression
     * @return the modified query
     */
    CriteriaQuery<T> having(Expression<Boolean> restriction);

    /**
     * Specify restrictions over the groups of the query according the
     * conjunction of the specified restriction predicates. Replaces the
     * previously added restriction(s), if any. If no restrictions are
     * specified, any previously added restrictions are simply removed. This
     * method only overrides the return type of the corresponding AbstractQuery
     * method.
     * 
     * @param restrictions
     *            zero or more restriction predicates
     * @return the modified query
     */
    CriteriaQuery<T> having(Predicate... restrictions);

    /**
     * Specify the ordering expressions that are used to order the query
     * results. Replaces the previous ordering expressions, if any. If no
     * ordering expressions are specified, the previous ordering, if any, is
     * simply removed, and results will be returned in no particular order. The
     * left-to-right sequence of the ordering expressions determines the
     * precedence, whereby the leftmost has highest precedence.
     * 
     * @param o
     *            zero or more ordering expressions
     * @return the modified query.
     */
    CriteriaQuery<T> orderBy(Order... o);

    /**
     * Specify whether duplicate query results will be eliminated. A true value
     * will cause duplicates to be eliminated. A false value will cause
     * duplicates to be retained. If distinct has not been specified, duplicate
     * results must be retained. This method only overrides the return type of
     * the corresponding AbstractQuery method.
     * 
     * @param distinct
     *            boolean value specifying whether duplicate results must be
     *            eliminated from the query result or whether they must be
     *            retained
     * @return the modified query.
     */
    CriteriaQuery<T> distinct(boolean distinct);
 
    /**
     * Return the result type of the query.
     * If a result type was specified as an argument to the
     * createQuery method, that type will be returned.
     * If the query was created using the createTupleQuery
     * method, the result type is Tuple.
     * Otherwise, the result type is Object.
     * @return result type
     */
    Class<?> getResultType();
    
    /**
     * Return the ordering expressions in order of precedence.
     * 
     * @return the list of ordering expressions
     */
    List<Order> getOrderList();

    /**
     * Return the parameters of the query
     * 
     * @return the query parameters
     */
    Set<ParameterExpression<?>> getParameters();
}