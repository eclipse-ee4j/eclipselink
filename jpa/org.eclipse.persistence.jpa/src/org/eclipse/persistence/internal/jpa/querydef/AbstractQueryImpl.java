/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.persistence.criteria.Subquery;
import javax.persistence.criteria.Predicate.BooleanOperator;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.localization.ExceptionLocalization;

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
 * @since EclipseLink 2.0
 */
public class AbstractQueryImpl<T> implements AbstractQuery<T> {
    
    
    protected Metamodel metamodel;
    protected Set<Root<?>> roots;
    protected Expression<Boolean> where; 
    protected ResultType queryResult;
    protected QueryBuilderImpl queryBuilder;
    protected boolean distinct;

    protected enum ResultType{
        OBJECT_ARRAY, PARTIAL, TUPLE, ENTITY, CONSTRUCTOR, OTHER
    }
    
    public AbstractQueryImpl(Metamodel metamodel, ResultType queryResult, QueryBuilderImpl queryBuilder){
        this.roots = new HashSet<Root<?>>();
        this.metamodel = metamodel;
        this.queryResult = queryResult;
        this.queryBuilder = queryBuilder;
    }
    
    /**
     * Add a query root corresponding to the given entity, forming a cartesian
     * product with any existing roots.
     * 
     * @param entity
     *            metamodel entity representing the entity of type X
     * @return query root corresponding to the given entity
     */
    public <X> Root<X> from(EntityType<X> entity){
        Root root = new RootImpl<X>(entity, this.metamodel, entity.getBindableJavaType(), new ExpressionBuilder(entity.getBindableJavaType()), entity);
        this.roots.add(root);
        return root;
    }

    /**
     * Add a query root corresponding to the given entity, forming a cartesian
     * product with any existing roots.
     * 
     * @param entityClass
     *            the entity class
     * @return query root corresponding to the given entity
     */
    public <X> Root<X> from(Class<X> entityClass) {
        EntityType<X> entity = this.metamodel.entity(entityClass);
        Root root = new RootImpl<X>(entity, this.metamodel, entity.getBindableJavaType(), new ExpressionBuilder(entity.getBindableJavaType()), entity);
        this.roots.add(root);
        return root;
    }

    /**
     * Return the query roots.
     * 
     * @return the set of query roots
     */
    public Set<Root<?>> getRoots(){
        return this.roots;
    }

    /**
     * Modify the query to restrict the query results according to the specified
     * boolean expression. Replaces the previously added restriction(s), if any.
     * 
     * @param restriction
     *            a simple or compound boolean expression
     * @return the modified query
     */
    public AbstractQuery<T> where(Expression<Boolean> restriction){
        this.where = restriction;
        return this;
    }

    /**
     * Modify the query to restrict the query results according to the
     * conjunction of the specified restriction predicates. Replaces the
     * previously added restriction(s), if any. If no restrictions are
     * specified, any previously added restrictions are simply removed.
     * 
     * @param restrictions
     *            zero or more restriction predicates
     * @return the modified query
     */
    public AbstractQuery<T> where(Predicate... restrictions){
        if (restrictions == null || restrictions.length == 0){
            this.where = null;
        }
        Predicate predicate = this.queryBuilder.and(restrictions);
        integrateRoot(predicate);
        this.where = predicate;
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
        throw new UnsupportedOperationException();
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
        throw new UnsupportedOperationException();
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
        throw new UnsupportedOperationException();
    }
    
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

    /**
     * Return the selection of the query
     * @return the item to be returned in the query result
     */
    public Selection<T> getSelection(){
        throw new UnsupportedOperationException();
    }
    
    /**
     * Return a list of the grouping expressions
     * @return the list of grouping expressions
     */
    public List<Expression<?>> getGroupList(){
        throw new UnsupportedOperationException();
    }
    /**
     * Return the predicate that corresponds to the where clause restriction(s).
     * 
     * @return where clause predicate
     */
    public Expression<Boolean> getRestriction(){
        return this.where;
    }
    
    /**
     * Return the predicate that corresponds to the restriction(s) over the
     * grouping items.
     * 
     * @return having clause predicate
     */
    public Predicate getGroupRestriction(){
        throw new UnsupportedOperationException();
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
    
    /**
     * Specify that the query is to be used as a subquery having the specified
     * return type.
     * 
     * @return subquery corresponding to the query
     */
    public <U> Subquery<U> subquery(Class<U> type){
        throw new UnsupportedOperationException();
    }
    
    /**
     *  Used to use a root from a different query.
     */
    
    protected void integrateRoot(Expression<?> predicate){
        Set<Root<?>> newRoots = new HashSet<Root<?>>();
        ((ExpressionImpl)predicate).findRoot(newRoots);
        if (this.roots.isEmpty()){
            this.roots.addAll(newRoots);
        }else{
            boolean found = false;
            for (Root root : newRoots){
                if (this.roots.contains(root)){
                    this.roots.addAll(newRoots);
                    found = true;
                    break;
                }
            }
            if (!found){
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage("EXPRESSION_USES_UNKOWN_ROOT_TODO"));
            }
        }
    }

    protected void integrateRoot(Selection<?> selection){
        if (selection.isCompoundSelection()){
            for (Selection subSelection: selection.getCompoundSelectionItems()){
                integrateRoot(subSelection);
            }
        }
    }
}
