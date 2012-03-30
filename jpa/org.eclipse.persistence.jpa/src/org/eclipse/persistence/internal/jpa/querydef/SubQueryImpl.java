/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
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
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CollectionJoin;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.MapJoin;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.persistence.criteria.SetJoin;
import javax.persistence.criteria.Subquery;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.metamodel.Type.PersistenceType;

import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.expressions.ConstantExpression;
import org.eclipse.persistence.internal.expressions.SubSelectExpression;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.jpa.metamodel.MetamodelImpl;
import org.eclipse.persistence.internal.jpa.metamodel.TypeImpl;
import org.eclipse.persistence.internal.localization.ExceptionLocalization;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
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
public class SubQueryImpl<T> extends AbstractQueryImpl<T> implements Subquery<T> , InternalExpression, InternalSelection{
    
    protected SelectionImpl<?> selection;
    protected SubSelectExpression currentNode;
    protected String alias;
    protected ReportQuery subQuery;
    protected Set<Join<?,?>> correlatedJoins;
    protected AbstractQuery parent;
    protected Set<FromImpl> processedJoins;
    protected Set<org.eclipse.persistence.expressions.Expression> correlations;
        


    public SubQueryImpl(Metamodel metamodel, Class result, CriteriaBuilderImpl queryBuilder, AbstractQuery parent){
        super(metamodel, ResultType.OTHER, queryBuilder, result);
        this.subQuery = new ReportQuery();
        TypeImpl queryType = ((MetamodelImpl)metamodel).getType(result);
        if (queryType != null && queryType.getPersistenceType() == PersistenceType.ENTITY){
            this.subQuery.setReferenceClass(result);
        }
        this.subQuery.setDistinctState(ObjectLevelReadQuery.DONT_USE_DISTINCT);
        this.correlatedJoins = new HashSet();
        this.correlations = new HashSet();
        this.currentNode = new SubSelectExpression(subQuery, new ExpressionBuilder());
        this.parent = parent;
    }

    /**
     * Specify the item that is to be returned in the query result.
     * Replaces the previously specified selection, if any.
     * @param selection  selection specifying the item that
     *        is to be returned in the query result
     * @return the modified query
     */
    public Subquery<T> select(Expression<T> selection) {
        findRootAndParameters(selection);
        if (((InternalSelection)selection).isFrom()){
            ((FromImpl)selection).isLeaf = false;
        }
        
        for (Iterator iterator = this.getRoots().iterator(); iterator.hasNext();){
            findJoins((FromImpl)iterator.next());
        }
        for (Iterator iterator = this.getCorrelatedJoins().iterator(); iterator.hasNext();){
            findJoins((FromImpl)iterator.next());
        }

        this.selection = (SelectionImpl) selection;
        this.queryType = selection.getJavaType();

        this.subQuery.getItems().clear();

        if (selection.isCompoundSelection()) {
            int count = 0;
            for (Selection select : selection.getCompoundSelectionItems()) {
                this.subQuery.addItem(String.valueOf(count), ((InternalSelection) select).getCurrentNode());
                ++count;
            }
            this.subQuery.setExpressionBuilder(((InternalSelection)selection.getCompoundSelectionItems().get(0)).getCurrentNode().getBuilder());
        } else {
            TypeImpl type = ((MetamodelImpl)this.metamodel).getType(selection.getJavaType());
            if (type != null && type.getPersistenceType().equals(PersistenceType.ENTITY)) {
                this.subQuery.addAttribute("", new ConstantExpression(1, ((InternalSelection)selection).getCurrentNode().getBuilder()));
                this.subQuery.addNonFetchJoinedAttribute(((InternalSelection)selection).getCurrentNode());
            } else {
                String itemName = selection.getAlias();
                if (itemName == null){
                    itemName = ((InternalSelection) selection).getCurrentNode().getName();
                }
                this.subQuery.addItem(itemName, ((InternalSelection) selection).getCurrentNode());
            }
            this.subQuery.setExpressionBuilder(((InternalSelection)selection).getCurrentNode().getBuilder());
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
    public Subquery<T> where(Expression<Boolean> restriction){
        super.where(restriction);
        org.eclipse.persistence.expressions.Expression currentNode = ((InternalSelection)this.where).getCurrentNode();
        for(org.eclipse.persistence.expressions.Expression exp: this.correlations){
            currentNode = currentNode.and(exp);
        }
        this.subQuery.setSelectionCriteria(currentNode);
        for (Iterator iterator = this.getRoots().iterator(); iterator.hasNext();){
            findJoins((FromImpl)iterator.next());
        }
        for (Iterator iterator = this.getCorrelatedJoins().iterator(); iterator.hasNext();){
            findJoins((FromImpl)iterator.next());
        }
        return this;
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
        super.where(restrictions);
        org.eclipse.persistence.expressions.Expression currentNode = ((InternalSelection)this.where).getCurrentNode();
        for(org.eclipse.persistence.expressions.Expression exp: this.correlations){
            currentNode = currentNode.and(exp);
        }
        this.subQuery.setSelectionCriteria(currentNode);
        for (Iterator iterator = this.getRoots().iterator(); iterator.hasNext();){
            findJoins((FromImpl)iterator.next());
        }
        for (Iterator iterator = this.getCorrelatedJoins().iterator(); iterator.hasNext();){
            findJoins((FromImpl)iterator.next());
        }
        return this;
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
        super.groupBy(grouping);
        this.subQuery.getGroupByExpressions().clear();
        for (Expression groupby: grouping){
            this.subQuery.addGrouping(((InternalSelection)groupby).getCurrentNode());
        }
        return this;
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
    public Subquery<T> groupBy(List<Expression<?>> grouping){
        super.groupBy(grouping);
        this.subQuery.getGroupByExpressions().clear();
        for (Expression groupby: grouping){
            this.subQuery.addGrouping(((InternalSelection)groupby).getCurrentNode());
        }
        return this;
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
        super.having(restriction);
        if (restriction != null){
            this.subQuery.setHavingExpression(((InternalSelection)restriction).getCurrentNode());
        }else{
            this.subQuery.setHavingExpression(null);
        }
        return this;
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
    public Subquery<T> having(Predicate... restrictions) {
        super.having(restrictions);
        if (this.havingClause != null){
            this.subQuery.setHavingExpression(((InternalSelection) this.havingClause).getCurrentNode());
        }else{
            this.subQuery.setHavingExpression(null);
        }
        return this;
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
        RootImpl root = new RootImpl(parentRoot.getModel(), metamodel, parentRoot.getJavaType(), internalCorrelate((FromImpl)parentRoot), parentRoot.getModel(), (FromImpl) parentRoot);
        integrateRoot(root);
        return root;
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
        this.correlatedJoins.add(parentJoin);
        JoinImpl join = new JoinImpl(parentJoin.getParentPath(), metamodel.managedType(parentJoin.getModel().getBindableJavaType()), metamodel, parentJoin.getJavaType(), internalCorrelate((FromImpl) parentJoin), parentJoin.getModel(), parentJoin.getJoinType(), (FromImpl) parentJoin);
        return join;
        
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
        this.correlatedJoins.add(parentCollection);
        return new CollectionJoinImpl(parentCollection.getParentPath(), metamodel.managedType(parentCollection.getModel().getBindableJavaType()), metamodel, parentCollection.getJavaType(), internalCorrelate((FromImpl) parentCollection), parentCollection.getModel(), parentCollection.getJoinType(), (FromImpl) parentCollection);
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
    public <X, Y> SetJoin<X, Y> correlate(SetJoin<X, Y> parentCollection){
        this.correlatedJoins.add(parentCollection);
        return new SetJoinImpl(parentCollection.getParentPath(), metamodel.managedType(parentCollection.getModel().getBindableJavaType()), metamodel, parentCollection.getJavaType(), ((InternalSelection)parentCollection).getCurrentNode(), parentCollection.getModel(), parentCollection.getJoinType(), (FromImpl) parentCollection);
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
    public <X, Y> ListJoin<X, Y> correlate(ListJoin<X, Y> parentCollection){
        this.correlatedJoins.add(parentCollection);
        return new ListJoinImpl(parentCollection.getParentPath(), metamodel.managedType(parentCollection.getModel().getBindableJavaType()), metamodel, parentCollection.getJavaType(), internalCorrelate((FromImpl) parentCollection), parentCollection.getModel(), parentCollection.getJoinType(), (FromImpl) parentCollection);
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
    public <X, K, V> MapJoin<X, K, V> correlate(MapJoin<X, K, V> parentCollection){
        this.correlatedJoins.add(parentCollection);
        return new MapJoinImpl(parentCollection.getParentPath(), metamodel.managedType(parentCollection.getModel().getBindableJavaType()), metamodel, parentCollection.getJavaType(), internalCorrelate((FromImpl) parentCollection), parentCollection.getModel(), parentCollection.getJoinType(), (FromImpl) parentCollection);
    }
    
    protected org.eclipse.persistence.expressions.Expression internalCorrelate(FromImpl from){
        org.eclipse.persistence.expressions.Expression expression = ((InternalSelection)from).getCurrentNode();
        ExpressionBuilder builder = new ExpressionBuilder(expression.getBuilder().getQueryClass());
        org.eclipse.persistence.expressions.Expression correlated = expression.rebuildOn(builder);
        expression = expression.equal(correlated);
        this.correlations.add(expression);
        org.eclipse.persistence.expressions.Expression selectionCriteria = expression.and(this.subQuery.getSelectionCriteria());
        this.subQuery.setSelectionCriteria(selectionCriteria);
        return correlated;

    }
    /**
     * Return the query of which this is a subquery.
     * @return the enclosing query or subquery
     */
    public AbstractQuery<?> getParent(){
        return this.parent;
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
        if (!distinct){
            this.subQuery.setDistinctState(ObjectLevelReadQuery.DONT_USE_DISTINCT);
        }else{
            this.subQuery.setDistinctState(ObjectLevelReadQuery.USE_DISTINCT);
        }        
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
    public java.util.Set<Join<?, ?>> getCorrelatedJoins(){
        return this.correlatedJoins;
    }

    public void addParameter(ParameterExpression<?> parameter){
        ((AbstractQueryImpl)this.getParent()).addParameter(parameter);
    }

    public void addJoin(FromImpl join){
        if (this.processedJoins == null ) {this.processedJoins = new HashSet<FromImpl>();}
        if (! this.processedJoins.contains(join)){
            this.processedJoins.add(join);
        this.subQuery.addNonFetchJoinedAttribute(join.getCurrentNode());
        }
    }

    //Expression
    public <X> Expression<X> as(Class<X> type) {
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
            if (!((InternalExpression)exp).isLiteral() && !((InternalExpression) exp).isParameter()){
                Object[] params = new Object[]{exp};
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage("CRITERIA_NON_LITERAL_PASSED_TO_IN",params));
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
        return new CompoundExpressionImpl(this.metamodel, this.currentNode.in(((InternalSelection)values).getCurrentNode()), list, "in");
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
    public boolean isConstructor(){
        return false;
    }
    public boolean isPredicate(){
        return false;
    }

    public boolean isParameter(){
        return false;
    }
    
    public boolean isRoot(){
        return false;
    }

    public boolean isSubquery(){
        return true;
    }

    protected void integrateRoot(RootImpl root) {
        if (this.roots.isEmpty()) {
            TypeImpl type = ((MetamodelImpl)this.metamodel).getType(this.queryType);
            if ((type != null && type.getPersistenceType() == PersistenceType.ENTITY) || queryType.equals(ClassConstants.OBJECT)) {
                // this is the first root, set return type and selection and
                // query
                // type
                if (this.selection == null) {
                    this.selection = root;
                    this.subQuery.getItems().clear();
                    this.subQuery.addAttribute("", new ConstantExpression(1, root.getCurrentNode().getBuilder()));
                    this.queryResult = ResultType.ENTITY;
                }
            }
            this.subQuery.setReferenceClass(root.getJavaType());
            this.subQuery.setExpressionBuilder(root.getCurrentNode().getBuilder());
            this.queryType = root.getJavaType();
            this.currentNode.setBaseExpression(((AbstractQueryImpl)this.parent).getBaseExpression());
        }
        if (!this.roots.contains(root)) {
            this.roots.add(root);
        }

    }

    public boolean isCompoundExpression(){
        return false;
    }
    public boolean isExpression(){
        return true;
    }
    
    public boolean isFrom(){
        return false;
    }
    
    public boolean isLiteral(){
        return false;
    }
    public void findRootAndParameters(AbstractQueryImpl query){
        for (Join join: this.correlatedJoins){
            ((JoinImpl)join).findRootAndParameters(query);
        }
    }

    protected org.eclipse.persistence.expressions.Expression getBaseExpression() {
        return this.currentNode.getSubQuery().getExpressionBuilder();
    }

}
