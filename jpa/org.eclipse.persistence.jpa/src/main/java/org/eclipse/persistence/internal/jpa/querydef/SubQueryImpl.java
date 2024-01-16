/*
 * Copyright (c) 2011, 2024 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2011, 2024 IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Gordon Yorke - Initial development
//     02/03/2017 - Dalia Abo Sheasha
//       - 509693 : EclipseLink generates inconsistent SQL statements for SubQuery

package org.eclipse.persistence.internal.jpa.querydef;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import jakarta.persistence.criteria.AbstractQuery;
import jakarta.persistence.criteria.CollectionJoin;
import jakarta.persistence.criteria.CommonAbstractCriteria;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.ListJoin;
import jakarta.persistence.criteria.MapJoin;
import jakarta.persistence.criteria.ParameterExpression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Selection;
import jakarta.persistence.criteria.SetJoin;
import jakarta.persistence.criteria.Subquery;
import jakarta.persistence.metamodel.Metamodel;
import jakarta.persistence.metamodel.Type.PersistenceType;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.internal.expressions.ConstantExpression;
import org.eclipse.persistence.internal.expressions.SubSelectExpression;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.jpa.metamodel.MetamodelImpl;
import org.eclipse.persistence.internal.jpa.metamodel.TypeImpl;
import org.eclipse.persistence.internal.localization.ExceptionLocalization;
import org.eclipse.persistence.queries.DatabaseQuery;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.ReportQuery;

/**
 * <p>
 * <b>Purpose</b>: Contains the implementation of the SubQuery interface of the JPA
 * criteria API.
 * <p>
 * <b>Description</b>: This is the container class for the components that define a query to
 * be used in a sub select expression.
 *
 * @see jakarta.persistence.criteria CriteriaQuery
 * @see jakarta.persistence.criteria SubQuery
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
    protected CommonAbstractCriteria parent;
    protected Set<FromImpl> processedJoins;
    protected Set<org.eclipse.persistence.expressions.Expression> correlations;



    public SubQueryImpl(Metamodel metamodel, Class<T> result, CriteriaBuilderImpl queryBuilder, CommonAbstractCriteria parent){
        super(metamodel, ResultType.OTHER, queryBuilder, result);
        this.subQuery = new ReportQuery();
        TypeImpl queryType = ((MetamodelImpl)metamodel).getType(result);
        if (queryType != null && queryType.getPersistenceType() == PersistenceType.ENTITY){
            this.subQuery.setReferenceClass(result);
        }
        this.subQuery.setDistinctState(ObjectLevelReadQuery.DONT_USE_DISTINCT);
        this.correlatedJoins = new HashSet<>();
        this.correlations = new HashSet<>();
        this.currentNode = new SubSelectExpression(subQuery, new ExpressionBuilder());
        this.parent = parent;
    }

    // Allows complete copy of CommonAbstractCriteriaImpl. Required for cast implementation and shall remain private.
    private SubQueryImpl(Metamodel metamodel, Expression<Boolean> where, CriteriaBuilderImpl queryBuilder,
                      Class<T> queryType, Set<ParameterExpression<?>> parameters,
                      ResultType queryResult, boolean distinct, Predicate havingClause,List<Expression<?>> groupBy,
                      Set<Root<?>> roots, org.eclipse.persistence.expressions.Expression baseExpression,
                      SelectionImpl<?> selection,SubSelectExpression currentNode, String alias, ReportQuery subQuery,
                      Set<Join<?,?>> correlatedJoins, CommonAbstractCriteria parent, Set<FromImpl> processedJoins,
                      Set<org.eclipse.persistence.expressions.Expression> correlations) {
        super(metamodel, where, queryBuilder, queryType, parameters,
              queryResult, distinct, havingClause, groupBy, roots, baseExpression);
        this.selection = selection;
        this.currentNode = currentNode;
        this.alias = alias;
        this.subQuery = subQuery;
        this.correlatedJoins = correlatedJoins;
        this.parent = parent;
        this.processedJoins = processedJoins;
        this.correlations = correlations;
    }

        /**
         * Specify the item that is to be returned in the query result.
         * Replaces the previously specified selection, if any.
         * @param selection  selection specifying the item that
         *        is to be returned in the query result
         * @return the modified query
         */
    @Override
    public Subquery<T> select(Expression<T> selection) {
        findRootAndParameters(selection);
        for (Iterator<Root<?>> iterator = this.getRoots().iterator(); iterator.hasNext();){
            findJoins((FromImpl)iterator.next());
        }
        for (Iterator<Join<?, ?>> iterator = this.getCorrelatedJoins().iterator(); iterator.hasNext();){
            findJoins((FromImpl)iterator.next());
        }

        this.selection = (SelectionImpl) selection;
        this.queryType = (Class<T>) selection.getJavaType();

        this.subQuery.getItems().clear();

        if (selection.isCompoundSelection()) {
            int count = 0;
            for (Selection select : selection.getCompoundSelectionItems()) {
                this.subQuery.addItem(String.valueOf(count), ((InternalSelection) select).getCurrentNode());
                ++count;
            }
            this.subQuery.setExpressionBuilder(((InternalSelection)selection.getCompoundSelectionItems().get(0)).getCurrentNode().getBuilder());
        } else {
            TypeImpl<? extends T> type = ((MetamodelImpl)this.metamodel).getType(selection.getJavaType());
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
    @Override
    public Subquery<T> where(Expression<Boolean> restriction){
        super.where(restriction);
        setWhereInternal();
        return this;
    }

    @Override
    public Subquery<T> where(Predicate... restrictions) {
        return where(restrictions != null ? List.of(restrictions) : null);
    }

    @Override
    public Subquery<T> where(List<Predicate> restrictions) {
        super.where(restrictions);
        setWhereInternal();
        return this;
    }

    private void setWhereInternal() {
        org.eclipse.persistence.expressions.Expression currentNode = ((InternalSelection)this.where).getCurrentNode();
        for(org.eclipse.persistence.expressions.Expression exp: this.correlations){
            currentNode = currentNode.and(exp);
        }
        this.subQuery.setSelectionCriteria(currentNode);
        for (Iterator<Root<?>> iterator = this.getRoots().iterator(); iterator.hasNext();){
            findJoins((FromImpl)iterator.next());
        }
        for (Iterator<Join<?, ?>> iterator = this.getCorrelatedJoins().iterator(); iterator.hasNext();){
            findJoins((FromImpl)iterator.next());
        }
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
    @Override
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
    @Override
    public Subquery<T> groupBy(List<Expression<?>> grouping){
        super.groupBy(grouping);
        this.subQuery.getGroupByExpressions().clear();
        for (Expression groupby: grouping){
            this.subQuery.addGrouping(((InternalSelection)groupby).getCurrentNode());
        }
        return this;
    }

    @Override
    public Subquery<T> having(Expression<Boolean> restriction){
        super.having(restriction);
        setHavingClauseInternal(((InternalSelection)restriction).getCurrentNode());
        return this;
    }

    @Override
    public Subquery<T> having(Predicate... restrictions) {
        return having(restrictions != null ? List.of(restrictions) : null);
    }

    @Override
    public Subquery<T> having(List<Predicate> restrictions) {
        super.having(restrictions);
        setHavingClauseInternal(((InternalSelection) this.havingClause).getCurrentNode());
        return this;
    }

    private void setHavingClauseInternal(org.eclipse.persistence.expressions.Expression currentNode) {
        if (this.havingClause != null) {
            this.subQuery.setHavingExpression(currentNode);
        } else {
            this.subQuery.setHavingExpression(null);
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
    @Override
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
    @Override
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
    @Override
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
    @Override
    public <X, Y> SetJoin<X, Y> correlate(SetJoin<X, Y> parentSet){
        this.correlatedJoins.add(parentSet);
        return new SetJoinImpl(parentSet.getParentPath(), metamodel.managedType(parentSet.getModel().getBindableJavaType()), metamodel, parentSet.getJavaType(), ((InternalSelection)parentSet).getCurrentNode(), parentSet.getModel(), parentSet.getJoinType(), (FromImpl) parentSet);
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
    @Override
    public <X, Y> ListJoin<X, Y> correlate(ListJoin<X, Y> parentList){
        this.correlatedJoins.add(parentList);
        return new ListJoinImpl(parentList.getParentPath(), metamodel.managedType(parentList.getModel().getBindableJavaType()), metamodel, parentList.getJavaType(), internalCorrelate((FromImpl) parentList), parentList.getModel(), parentList.getJoinType(), (FromImpl) parentList);
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
    @Override
    public <X, K, V> MapJoin<X, K, V> correlate(MapJoin<X, K, V> parentMap){
        this.correlatedJoins.add(parentMap);
        return new MapJoinImpl(parentMap.getParentPath(), metamodel.managedType(parentMap.getModel().getBindableJavaType()), metamodel, parentMap.getJavaType(), internalCorrelate((FromImpl) parentMap), parentMap.getModel(), parentMap.getJoinType(), (FromImpl) parentMap);
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

    @Override
    public Set<ParameterExpression<?>> getParameters() {
        return ((CommonAbstractCriteriaImpl)this.getContainingQuery()).getParameters();
    }

    /**
     * Return the query of which this is a subquery.
     * @return the enclosing query or subquery
     */
    @Override
    public AbstractQuery<?> getParent(){
        if (parent == null || parent instanceof AbstractQuery) {
            return (AbstractQuery) this.parent;
        }
        throw new IllegalStateException("TODO.. write a better message");
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
    @Override
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
    @Override
    public org.eclipse.persistence.expressions.Expression getCurrentNode() {
        return currentNode;
    }

    /**
     * Return the selection item of the query.  This will correspond to the query type.
     * @return the selection item of the query
     */
    @Override
    public Expression<T> getSelection(){
        return (Expression<T>) this.selection;
    }

    /**
     * Return the joins that have been made from the subquery.
     *
     * @return joins made from this type
     */
    @Override
    public java.util.Set<Join<?, ?>> getCorrelatedJoins(){
        return this.correlatedJoins;
    }

    @Override
    public void addParameter(ParameterExpression<?> parameter){
        ((CommonAbstractCriteriaImpl)this.getContainingQuery()).addParameter(parameter);
    }

    @Override
    public void addJoin(FromImpl join){
        if (this.processedJoins == null ) {
            this.processedJoins = new HashSet<>();
        }
        if (! this.processedJoins.contains(join)){
            this.processedJoins.add(join);
            this.subQuery.addNonFetchJoinedAttribute(join.getCurrentNode());
        }
    }

    //Expression
    @Override
    public <X> Expression<X> as(Class<X> type) {
        return (Expression<X>) this;
    }

    @Override
    public <X> Expression<X> cast(Class<X> aClass) {
        // JPA spec: New instance with provided Java type
        return new SubQueryImpl<>(
                metamodel, where, queryBuilder, aClass, parameters,
                queryResult, distinct, havingClause, groupBy, roots, baseExpression,
                selection, currentNode, alias, subQuery, correlatedJoins, parent, processedJoins, correlations);
    }

    @Override
    public Predicate in(Object... values) {
        List<Expression<?>> list = new ArrayList<>();
        list.add(this);
        return new CompoundExpressionImpl(this.metamodel, this.currentNode.in(values), list, "in");
    }

    /**
     * Apply a predicate to test whether the expression is a member
     * of the argument list.
     * @return predicate testing for membership
     */
    @Override
    public Predicate in(Expression<?>... values) {
        List<Expression<?>> list = new ArrayList<>();
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
    @Override
    public Predicate in(Collection<?> values) {
        List<Expression<?>> list = new ArrayList<>();
        list.add(this);
        return new CompoundExpressionImpl(this.metamodel, this.currentNode.in(values), list, "in");
    }
    /**
     * Apply a predicate to test whether the expression is a member
     * of the collection.
     * @param values expression corresponding to collection
     * @return predicate testing for membership
     */
    @Override
    public Predicate in(Expression<Collection<?>> values) {
        List<Expression<?>> list = new ArrayList<>();
        list.add(this);
        return new CompoundExpressionImpl(this.metamodel, this.currentNode.in(((InternalSelection)values).getCurrentNode()), list, "in");
    }

    @Override
    public Predicate isNotNull() {
        List<Expression<?>> list = new ArrayList<>();
        list.add(this);
        return new CompoundExpressionImpl(this.metamodel, this.currentNode.notNull(), list, "not null");
    }

    @Override
    public Predicate equalTo(Expression<?> value) {
        return new CompoundExpressionImpl(
                this.metamodel,
                this.currentNode.equal(value),
                List.of(this, value),
                "equals");
    }

    @Override
    public Predicate equalTo(Object value) {
        return new CompoundExpressionImpl(
                this.metamodel,
                this.currentNode.equal(value),
                List.of(this, ExpressionImpl.createLiteral(value, metamodel)),
                "equals");
    }

    @Override
    public Predicate notEqualTo(Expression<?> value) {
        return new CompoundExpressionImpl(
                this.metamodel,
                this.currentNode.equal(value),
                List.of(this, value),
                "not equal");
    }

    @Override
    public Predicate notEqualTo(Object value) {
        return new CompoundExpressionImpl(
                this.metamodel,
                this.currentNode.equal(value),
                List.of(this, ExpressionImpl.createLiteral(value, metamodel)),
                "not equal");
    }

    @Override
    public Predicate isNull() {
        List<Expression<?>> list = new ArrayList<>();
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
    @Override
    public Selection<T> alias(String name) {
        this.alias = name;
        return this;
    }

    @Override
    public String getAlias() {
        return this.alias;
    }

    @Override
    public Class<T> getJavaType() {
        return this.queryType;
    }
    /**
     * Return selection items composing a compound selection
     * @return list of selection items
     * @throws IllegalStateException if selection is not a compound
     *           selection
     */
    @Override
    public List<Selection<?>> getCompoundSelectionItems(){
        throw new IllegalStateException(ExceptionLocalization.buildMessage("CRITERIA_NOT_A_COMPOUND_SELECTION"));
    }

    /**
     * Whether the selection item is a compound selection
     * @return boolean
     */
    @Override
    public boolean isCompoundSelection(){
        return false;
    }
    @Override
    public boolean isConstructor(){
        return false;
    }
    @Override
    public boolean isJunction(){
        return false;
    }
    @Override
    public boolean isPredicate(){
        return false;
    }

    @Override
    public boolean isParameter(){
        return false;
    }

    @Override
    public boolean isRoot(){
        return false;
    }

    @Override
    public boolean isSubquery(){
        return true;
    }

    @Override
    protected void integrateRoot(RootImpl root) {
        if (this.roots.isEmpty()) {
            TypeImpl type = ((MetamodelImpl)this.metamodel).getType(this.queryType);
            if ((type != null && type.getPersistenceType() == PersistenceType.ENTITY) || queryType.equals(ClassConstants.OBJECT)) {
                // this is the first root, set return type and selection and query type
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
            this.currentNode.setBaseExpression(((CommonAbstractCriteriaImpl)this.parent).getBaseExpression());
        }
        // If the parent of this SubQuery is a CriteriaQuery and the CriteriaQuery has multiple roots, 
        // assign the baseExpression based on the SubQuery's roots - Bug 509693
        if (!this.roots.contains(root) && this.parent instanceof CriteriaQueryImpl && 
                ((CriteriaQueryImpl) this.parent).getRoots().size() > 1) {
                this.currentNode.setBaseExpression(((CriteriaQueryImpl) this.parent).getBaseExpression(root));
        }
        super.integrateRoot(root);
    }

    @Override
    public boolean isCompoundExpression(){
        return false;
    }
    @Override
    public boolean isExpression(){
        return true;
    }

    @Override
    public boolean isFrom(){
        return false;
    }

    @Override
    public boolean isLiteral(){
        return false;
    }

    @Override
    public void findRootAndParameters(CommonAbstractCriteriaImpl query){
        for (Join join: this.correlatedJoins){
            ((JoinImpl)join).findRootAndParameters(query);
        }
    }

    @Override
    protected org.eclipse.persistence.expressions.Expression getBaseExpression() {
        return this.currentNode.getSubQuery().getExpressionBuilder();
    }

    @Override
    public CommonAbstractCriteria getContainingQuery() {
        return this.parent;
    }

    @Override
    public DatabaseQuery getDatabaseQuery() {
        return this.subQuery;
    }

}
