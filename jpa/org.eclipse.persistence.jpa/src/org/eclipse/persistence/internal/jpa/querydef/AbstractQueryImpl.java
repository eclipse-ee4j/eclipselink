/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Gordon Yorke - Initial development
 *
 ******************************************************************************/
package org.eclipse.persistence.internal.jpa.querydef;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.eclipse.persistence.expressions.ExpressionBuilder;

/**
 * <p>
 * <b>Purpose</b>: Contains the implementation of the AbstractQuery interface of
 * the JPA criteria API.
 * <p>
 * <b>Description</b>: This is the container class for the components that
 * define a query. This is the superclass of both the CriteriaQuery and the
 * SubQuery.
 * <p>
 * 
 * @see javax.persistence.criteria CriteriaQuery
 * 
 * @author gyorke
 * @since EclipseLink 1.2
 */
public abstract class AbstractQueryImpl<T> extends CommonAbstractCriteriaImpl<T> implements AbstractQuery<T>, Serializable{


    protected ResultType queryResult;
    protected boolean distinct;
    protected Predicate havingClause;
    protected List<Expression<?>> groupBy;
    protected Set<Root<?>> roots;

    protected enum ResultType{
        UNKNOWN, OBJECT_ARRAY, PARTIAL, TUPLE, ENTITY, CONSTRUCTOR, OTHER
    }

    public AbstractQueryImpl(Metamodel metamodel, ResultType queryResult, CriteriaBuilderImpl queryBuilder, Class<T> resultType){
        super(metamodel, queryBuilder, resultType);
        this.roots = new HashSet<Root<?>>();
        this.queryResult = queryResult;

    }

    /**
     * Specify the expressions that are used to form groups over
     * the query results.
     * Replaces the previous specified grouping expressions, if any.
     * If no grouping expressions are specified, any previously 
     * added grouping expressions are simply removed.
     * @param grouping  list of zero or more grouping expressions
     * @return the modified query
     */
    public AbstractQuery<T> groupBy(List<Expression<?>> grouping){
        this.groupBy = grouping;
        return this;
    }


    /**
     * Specify the expressions that are used to form groups over the query
     * results. Replaces the previous specified grouping expressions, if any. If
     * no grouping expressions are specified, any previously added grouping
     * expressions are simply removed.
     * 
     * @param grouping
     *            zero or more grouping expressions
     * @return the modified query
     */
    public AbstractQuery<T> groupBy(Expression<?>... grouping){
        this.groupBy = new ArrayList<Expression<?>>();
        for (Expression<?> exp : grouping){
            this.groupBy.add(exp);
        }
        return this;
    }

    /**
     * Specify a restriction over the groups of the query. Replaces the previous
     * having restriction(s), if any.
     * 
     * @param restriction
     *            a simple or compound boolean expression
     * @return the modified query
     */
    public AbstractQuery<T> having(Expression<Boolean> restriction){
        if (((InternalExpression)restriction).isCompoundExpression() || ((InternalExpression)restriction).isPredicate()){
            this.havingClause = (Predicate) restriction;
        }else{
            this.havingClause = queryBuilder.isTrue(restriction);
        }

        return this;
    }

    /**
     * Specify restrictions over the groups of the query according the
     * conjunction of the specified restriction predicates. Replaces the
     * previously added restriction(s), if any. If no restrictions are
     * specified, any previously added restrictions are simply removed.
     * 
     * @param restrictions
     *            zero or more restriction predicates
     * @return the modified query
     */
    public AbstractQuery<T> having(Predicate... restrictions){
        if (restrictions != null && restrictions.length > 0) {
            Predicate conjunction = this.queryBuilder.conjunction();
            for (Predicate predicate : restrictions) {
                conjunction = this.queryBuilder.and(conjunction, predicate);
            }
            this.havingClause = conjunction;
        }
        return this;
    }

    public abstract void addJoin(FromImpl join);

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
    public AbstractQuery<T> distinct(boolean distinct){
        this.distinct= distinct;
        return this;
    }

    protected org.eclipse.persistence.expressions.Expression getBaseExpression() {
        if (this.roots.isEmpty()) {
            return new ExpressionBuilder();
        } else {
            return ((RootImpl)this.roots.iterator().next()).getCurrentNode();
        }
    }

    /**
     * Return a list of the grouping expressions
     * @return the list of grouping expressions
     */
    public List<Expression<?>> getGroupList(){
        if (this.groupBy == null){
            this.groupBy = new ArrayList<Expression<?>>();
        }
        return this.groupBy;
    }

    /**
     * Return the predicate that corresponds to the restriction(s) over the
     * grouping items.
     * 
     * @return having clause predicate
     */
    public Predicate getGroupRestriction(){
        return this.havingClause;
    }

    /**
     * Return the query roots.
     * 
     * @return the set of query roots
     */
    public Set<Root<?>> getRoots(){
        return this.roots;
    }

    protected void integrateRoot(RootImpl root) {
        if (!this.roots.contains(root)) {
            this.roots.add(root);
        }
    }

    /**
     * Return whether duplicate query results must be eliminated or retained.
     * 
     * @return boolean indicating whether duplicate query results must be
     *         eliminated
     */
    public boolean isDistinct(){
        return this.distinct;
    }

    protected void findJoins(FromImpl root) {
        root.findJoins((AbstractQueryImpl)this);
    }

    protected void findRootAndParameters(Selection<?> selection) {
        if (selection.isCompoundSelection()) {
            for (Selection subSelection : selection.getCompoundSelectionItems()) {
                findRootAndParameters(subSelection);
            }
        }
    }

    /**
     * Add a query root corresponding to the given entity, forming a Cartesian
     * product with any existing roots.
     * 
     * @param entity
     *            metamodel entity representing the entity of type X
     * @return query root corresponding to the given entity
     */
    public <X> Root<X> from(EntityType<X> entity) {
        return this.internalFrom(entity);
    }

    /**
     * Add a query root corresponding to the given entity, forming a Cartesian
     * product with any existing roots.
     * 
     * @param entityClass
     *            the entity class
     * @return query root corresponding to the given entity
     */
    public <X> Root<X> from(Class<X> entityClass) {
        return this.internalFrom(entityClass);
    }

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
    public AbstractQuery<T> where(Expression<Boolean> restriction){
        return (AbstractQuery<T>)super.where(restriction);
    }

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
    public AbstractQuery<T> where(Predicate... restrictions) {
        return (AbstractQuery<T>) super.where(restrictions);
    }

}
