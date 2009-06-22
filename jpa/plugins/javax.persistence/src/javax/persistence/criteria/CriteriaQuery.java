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
    CriteriaQuery<T> select(Selection<T> selection);

    /**
     * Specify the items that are to be returned in the query result,
     * Replaces the previously specified selections, if any.
     * @param selections  expressions specifying the items that
     *        are returned in the query result
     * @return the modified query
     */
    CriteriaQuery<T> multiselect(Selection<?>... selections);

    // override the return type only:
    /**
     * Modify the query to restrict the query result according to the specified
     * boolean expression. Replaces the previously added restriction(s), if any.
     * This method only overrides the return type of the corresponding
     * AbstractQuery method.
     * 
     * @param restriction
     *            a simple or compound boolean expression
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
     * Return the selection item of the query.  This will correspond to the query type.
     * @return the selection item of the query
     */
    Selection<T> getSelection();

    /**
     * Return the multiselection items of the query as a list
     * @return the selection items of the query as a list
     */
    List<Selection<?>> getSelectionList();
    
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