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

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.persistence.Tuple;
import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CollectionJoin;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.MapJoin;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.persistence.criteria.SetJoin;
import javax.persistence.criteria.Subquery;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.metamodel.Type.PersistenceType;

import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.expressions.SubSelectExpression;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.localization.ExceptionLocalization;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedGetConstructorFor;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReportQuery;

/**
 * <p>
 * <b>Purpose</b>: Contains the implementation of the SubQuery interface of the JPA
 * criteria API.
 * <p>
 * <b>Description</b>: This is the container class for the components that define a query to
 * be used in a sub select expression.
 * <p>
 * 
 * @see javax.persistence.criteria CriteriaQuery
 * @see javax.persistence.criteria SubQuery
 * 
 * @author gyorke
 * @since EclipseLink 2.0
 */
public class SubQueryImpl<T> extends AbstractQueryImpl<T> implements Subquery<T> {
    
    protected SelectionImpl<?> selection;
    protected SubSelectExpression currentNode;
    protected String alias;
        


    public SubQueryImpl(Metamodel metamodel, ResultType queryResult, Class result, QueryBuilderImpl queryBuilder){
        super(metamodel, queryResult, queryBuilder, result);
    }

    /**
     * Specify the item that is to be returned in the query result.
     * Replaces the previously specified selection, if any.
     * @param selection  selection specifying the item that
     *        is to be returned in the query result
     * @return the modified query
     */
    public Subquery<T> select(Expression<T> selection) {
        this.selection = (SelectionImpl) selection;
        this.queryType = selection.getJavaType();
        ManagedType type = this.metamodel.type(this.queryType);
        if (type != null && type.getPersistenceType().equals(PersistenceType.ENTITY)) {
            if (!((EntityType)type).hasSingleIdAttribute()){
                this.selection = (SelectionImpl) ((Path)this.selection).get(((EntityType)type).getId(Object.class));
            }else{
                this.selection = (SelectionImpl<?>) queryBuilder.construct(ClassConstants.AOBJECT, (Selection[])((EntityType)type).getIdClassAttributes().toArray());
            }
        }
        this.queryResult = ResultType.OTHER;
        integrateRoot(selection);
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
    public Subquery<T> where(Expression<Boolean> restriction){
        return (Subquery<T>) super.where(restriction);
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
    public Subquery<T> where(Predicate... restrictions){
        return (Subquery<T>) super.where(restrictions);
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
    public Subquery<T> groupBy(Expression<?>... grouping){
        return (Subquery<T>) super.groupBy(grouping);
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
    public Subquery<T> having(Expression<Boolean> restriction){
        return (Subquery<T>)super.having(restriction);
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
    public Subquery<T> having(Predicate... restrictions){
        return (Subquery<T>)super.having(restrictions);
    }
    
    protected void initialRoot(RootImpl root, EntityType entity){
        if (this.roots.isEmpty() && (this.queryResult.equals(ResultType.ENTITY) || this.queryType.equals(ClassConstants.Object_Class))){
            //this is the first root, set return type and selection and query type
            select(root);
        }
    }

    /**
     * Correlates a root of the enclosing query to a root of the subquery and
     * returns the subquery root.
     * 
     * @param parentRoot
     *            a root of the containing query
     * @return subquery root
     */
    public <Y> Root<Y> correlate(Root<Y> parentRoot){
        //TODO
        throw new UnsupportedOperationException();
    }

    /**
     * Correlates a join object of the enclosing query to a join object of the
     * subquery and returns the subquery join object.
     * 
     * @param parentJoin
     *            join target of the containing query
     * @return subquery join
     */
    public <X, Y> Join<X, Y> correlate(Join<X, Y> parentJoin){
        //TODO
        throw new UnsupportedOperationException();
    }
    /**
     * Correlates a join to a Collection-valued association or element
     * collection in the enclosing query to a join object of the subquery and
     * returns the subquery join object.
     * 
     * @param parentCollection
     *            join target of the containing query
     * @return subquery join
     */
    public <X, Y> CollectionJoin<X, Y> correlate(CollectionJoin<X, Y> parentCollection){
        //TODO
        throw new UnsupportedOperationException();
    }
    
    /**
     * Correlates a join to a Set-valued association or element collection in
     * the enclosing query to a join object of the subquery and returns the
     * subquery join object.
     * 
     * @param parentSet
     *            join target of the containing query
     * @return subquery join
     */
    public <X, Y> SetJoin<X, Y> correlate(SetJoin<X, Y> parentSet){
        //TODO
        throw new UnsupportedOperationException();
    }
    
    
    /**
     * Correlates a join to a List-valued association or element collection in
     * the enclosing query to a join object of the subquery and returns the
     * subquery join object.
     * 
     * @param parentList
     *            join target of the containing query
     * @return subquery join
     */
    public <X, Y> ListJoin<X, Y> correlate(ListJoin<X, Y> parentList){
        //TODO
        throw new UnsupportedOperationException();
    }
    
    /**
     * Correlates a join to a Map-valued association or element collection in
     * the enclosing query to a join object of the subquery and returns the
     * subquery join object.
     * 
     * @param parentMap
     *            join target of the containing query
     * @return subquery join
     */
    public <X, K, V> MapJoin<X, K, V> correlate(MapJoin<X, K, V> parentMap){
        //TODO
        throw new UnsupportedOperationException();
    }
    
    /**
     * Return the query of which this is a subquery.
     * @return the enclosing query or subquery
     */
    public AbstractQuery<?> getParent(){
        //TODO
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
    public Subquery<T> distinct(boolean distinct){
        super.distinct(distinct);
        return this;
    }

    /**
     * Returns the current EclipseLink expression at this node in the criteria expression tree
     * @return the currentNode
     */
    public org.eclipse.persistence.expressions.Expression getCurrentNode() {
        return currentNode;
    }

    /**
     * Return the selection item of the query.  This will correspond to the query type.
     * @return the selection item of the query
     */
    public Expression<T> getSelection(){
        return (Expression<T>) this.selection;
    }

    /**
     * Return the joins that have been made from the subquery.
     * 
     * @return joins made from this type
     */
    public java.util.Set<Join<?, ?>> getJoins(){
        //TODO
        throw new UnsupportedOperationException();
    }

    /**
     * Translates from the criteria query to a EclipseLink Database Query.
     */
    public DatabaseQuery translate() {
        
        ReportQuery reportQuery = new ReportQuery();
        reportQuery.setShouldReturnWithoutReportQueryResult(true);

        if (this.selection != null) {
            if (this.selection.isCompoundSelection()) {
                for (Selection nested : this.selection.getCompoundSelectionItems()) {
                    reportQuery.addAttribute(nested.getAlias(), ((SelectionImpl) nested).getCurrentNode(), nested.getJavaType());
                }
            } else {
                reportQuery.addAttribute(this.selection.getAlias(), ((SelectionImpl) this.selection).getCurrentNode(), this.selection.getJavaType());
            }
        }
        if (this.where != null && ((ExpressionImpl) this.where).getCurrentNode() != null) {
            reportQuery.setReferenceClass(((ExpressionImpl) this.where).getCurrentNode().getBuilder().getQueryClass());
        } else {
            reportQuery.setReferenceClass(this.getRoots().iterator().next().getJavaType());
        }
        if (this.where != null) {
            reportQuery.setSelectionCriteria(((ExpressionImpl) this.where).getCurrentNode());
        }
        if (this.distinct) {
            reportQuery.setDistinctState(ObjectLevelReadQuery.USE_DISTINCT);
        } else {
            reportQuery.setDistinctState(ObjectLevelReadQuery.DONT_USE_DISTINCT);

        }
        
        return reportQuery;
    }
    
    
    //Expression
    public <X> Expression<X> as(Class<X> type) {
        // TODO Auto-generated method stub
        return (Expression<X>) this;
    }
    
    public Predicate in(Object... values) {
        List list = new ArrayList();
        list.add(this);
        return new CompoundExpressionImpl(this.metamodel, this.currentNode.in(values), list, "in");
    }

    /**
     * Apply a predicate to test whether the expression is a member
     * of the argument list.
     * @param values
     * @return predicate testing for membership
     */
    public Predicate in(Expression<?>... values) {
        List list = new ArrayList();
        list.add(this);
        for (Expression exp: values){
            if (!((ExpressionImpl)exp).isLiteral()){
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage("CRITERIA_NON_LITERAL_PASSED_TO_IN_TODO"));
            }
        }
        
        return new CompoundExpressionImpl(this.metamodel, this.currentNode.in(values), list, "in");
    }

    /**
     * Apply a predicate to test whether the expression is a member
     * of the collection.
     * @param values collection
     * @return predicate testing for membership
     */
    public Predicate in(Collection<?> values) {
        List list = new ArrayList();
        list.add(this);
        return new CompoundExpressionImpl(this.metamodel, this.currentNode.in(values), list, "in");
    }
    /**
     * Apply a predicate to test whether the expression is a member
     * of the collection.
     * @param values expression corresponding to collection
     * @return predicate testing for membership
     */
    public Predicate in(Expression<Collection<?>> values) {
        List list = new ArrayList();
        list.add(this);
        return new CompoundExpressionImpl(this.metamodel, this.currentNode.in(((ExpressionImpl)values).getCurrentNode()), list, "in");
    }
    
    public Predicate isNotNull() {
        List list = new ArrayList();
        list.add(this);
        return new CompoundExpressionImpl(this.metamodel, this.currentNode.notNull(), list, "not null");
    }

    
    public Predicate isNull() {
        List list = new ArrayList();
        list.add(this);
        return new CompoundExpressionImpl(this.metamodel, this.currentNode.isNull(), list, "is null");
    }

    //SELECTION
    /**
     * Assign an alias to the selection.
     * 
     * @param name
     *            alias
     */
    public Selection<T> alias(String name) {
        this.alias = name;
        return this;
    }
    public String getAlias() {
        return this.alias;
    }

    public Class<T> getJavaType() {
        return this.queryType;
    }
    /**
     * Return selection items composing a compound selection
     * @return list of selection items
     * @throws IllegalStateException if selection is not a compound
     *           selection
     */
    public List<Selection<?>> getCompoundSelectionItems(){
        throw new IllegalStateException(ExceptionLocalization.buildMessage("CRITERIA_NOT_A_COMPOUND_SELECTION"));
    }
    
    /**
     * Whether the selection item is a compound selection
     * @return boolean 
     */
    public boolean isCompoundSelection(){
        return false;
    }

    
    

}
