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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;

import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReportQuery;

/**
 * <p>
 * <b>Purpose</b>: Contains the implementation of the CriteriaQuery interface of the JPA
 * criteria API.
 * <p>
 * <b>Description</b>: This is the container class for the components that define a query.
 * <p>
 * 
 * @see javax.persistence.criteria CriteriaQuery
 * 
 * @author gyorke
 * @since EclipseLink 2.0
 */
public class CriteriaQueryImpl<T> extends AbstractQueryImpl<T> implements CriteriaQuery<T> {
    
    protected List<Selection<?>> selections;
    protected Selection<T> selection;
    protected Class<T> queryType;
    
    protected boolean distinct;

    public CriteriaQueryImpl(Metamodel metamodel, ResultType queryResult, Selection<T> initSelection){
        super(metamodel, queryResult);
        this.selections = new ArrayList<Selection<?>>();
        this.selection = initSelection;
    }

    /**
     * Specify the item that is to be returned in the query result.
     * Replaces the previously specified selection, if any.
     * @param selection  selection specifying the item that
     *        is to be returned in the query result
     * @return the modified query
     */
    public CriteriaQuery<T> select(Selection<T> selection){
        this.selection = selection;
        return this;
    }

    /**
     * Specify the items that are to be returned in the query result,
     * Replaces the previously specified selections, if any.
     * @param selections  expressions specifying the items that
     *        are returned in the query result
     * @return the modified query
     */
    public CriteriaQuery<T> multiselect(Selection<?>... selections){
        this.selections.clear();
        for(Selection selection: selections){
            this.selections.add(selection);
        }
        return this;
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
    public CriteriaQuery<T> where(Expression<Boolean> restriction){
        return (CriteriaQuery<T>) super.where(restriction);
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
    public CriteriaQuery<T> where(Predicate... restrictions){
        return (CriteriaQuery<T>) super.where(restrictions);
    }

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
    public CriteriaQuery<T> groupBy(Expression<?>... grouping){
        throw new UnsupportedOperationException();
    }

    /**
     * Specify a restriction over the groups of the query. Replaces the previous
     * having restriction(s), if any. This method only overrides the return type
     * of the corresponding AbstractQuery method.
     * 
     * @param restriction
     *            a simple or compound boolean expression
     * @return the modified query
     */
    public CriteriaQuery<T> having(Expression<Boolean> restriction){
        throw new UnsupportedOperationException();
    }

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
    public CriteriaQuery<T> having(Predicate... restrictions){
        throw new UnsupportedOperationException();
    }

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
    public CriteriaQuery<T> orderBy(Order... o){
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
    public CriteriaQuery<T> distinct(boolean distinct){
        this.distinct= distinct;
        return this;
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
     * Return the ordering expressions in order of precedence.
     * 
     * @return the list of ordering expressions
     */
    public List<Order> getOrderList(){
        throw new UnsupportedOperationException();
    }

    /**
     * Return the parameters of the query
     * 
     * @return the query parameters
     */
    public Set<ParameterExpression<?>> getParameters(){
        throw new UnsupportedOperationException();
    }
    /**
     * Return the selection item of the query.  This will correspond to the query type.
     * @return the selection item of the query
     */
    public Selection<T> getSelection(){
        return this.selection;
    }

    /**
     * Return the selection items of the query as a list
     * @return the selection items of the query as a list
     */
    public List<Selection<?>> getSelectionList(){
        return this.selections;
    }
    
    /**
     * Translates from the criteria query to a EclipseLink
     * Database Query.
     */
    public DatabaseQuery translate(){
        ObjectLevelReadQuery query = null;
        if (this.queryResult.equals(ResultType.CONSTRUCTOR)){
            ReportQuery rq = new ReportQuery();
            rq.addConstructorReportItem(((ConstructorSelectionImpl)this.selection).translate());
            rq.setShouldReturnSingleAttribute(true);
            query = rq;
        }else if (this.queryResult.equals(ResultType.ENTITY)){
            query = new ReadAllQuery(this.getSelection().getJavaType());
        }else if (this.queryResult.equals(ResultType.PARTIAL)){
            ReadAllQuery raq = new ReadAllQuery(this.getSelection().getJavaType());
            for (Selection selection: getSelectionList()){
                raq.addPartialAttribute(((SelectionImpl)selection).currentNode);
            }
            query = raq;
        }else{
            ReportQuery reportQuery = null;
            if (this.queryResult.equals(ResultType.TUPLE)){
                reportQuery = new TupleQuery(this.getSelectionList());
            }else{
                reportQuery = new ReportQuery();
                reportQuery.setShouldReturnWithoutReportQueryResult(true);
            }
            for (Selection selection: getSelectionList()){
                if (((SelectionImpl)selection).isConstructor()){
                    reportQuery.addConstructorReportItem(((ConstructorSelectionImpl)selection).translate());
                }else{
                    reportQuery.addItem(selection.getAlias(), ((SelectionImpl)selection).getCurrentNode());
                }
            }
            query = reportQuery;
        }
        query.setExpressionBuilder(((ExpressionImpl)this.where).getCurrentNode().getBuilder());
        query.setSelectionCriteria(((ExpressionImpl)this.where).getCurrentNode().getBuilder());
        if (this.distinct) query.setDistinctState(ObjectLevelReadQuery.USE_DISTINCT);
        return query;
    }

}
