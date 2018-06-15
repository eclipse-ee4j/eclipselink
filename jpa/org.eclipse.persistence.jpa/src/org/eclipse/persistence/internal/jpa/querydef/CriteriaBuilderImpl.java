/*
 * Copyright (c) 2009, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Gordon Yorke - Initial development
//
package org.eclipse.persistence.internal.jpa.querydef;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Tuple;
import javax.persistence.criteria.CollectionJoin;
import javax.persistence.criteria.CompoundSelection;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.MapJoin;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Predicate.BooleanOperator;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.persistence.criteria.SetJoin;
import javax.persistence.criteria.Subquery;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.metamodel.Type.PersistenceType;

import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.expressions.ExpressionMath;
import org.eclipse.persistence.expressions.ExpressionOperator;
import org.eclipse.persistence.internal.expressions.ArgumentListFunctionExpression;
import org.eclipse.persistence.internal.expressions.ConstantExpression;
import org.eclipse.persistence.internal.expressions.FunctionExpression;
import org.eclipse.persistence.internal.expressions.SubSelectExpression;
import org.eclipse.persistence.internal.helper.BasicTypeHelperImpl;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.jpa.metamodel.MetamodelImpl;
import org.eclipse.persistence.internal.jpa.metamodel.TypeImpl;
import org.eclipse.persistence.internal.jpa.querydef.AbstractQueryImpl.ResultType;
import org.eclipse.persistence.internal.localization.ExceptionLocalization;
import org.eclipse.persistence.jpa.JpaCriteriaBuilder;
import org.eclipse.persistence.queries.ReportQuery;

public class CriteriaBuilderImpl implements JpaCriteriaBuilder, Serializable {

    public static final String CONCAT = "concat";
    public static final String SIZE = "size";

    protected Metamodel metamodel;

    public CriteriaBuilderImpl(Metamodel metamodel){
        this.metamodel = metamodel;
    }

    /**
     *  Create a Criteria query object.
     *  @return query object
     */
    @Override
    public CriteriaQuery<Object> createQuery(){
        return new CriteriaQueryImpl(this.metamodel, ResultType.UNKNOWN, ClassConstants.OBJECT, this);
    }

    /**
     *  Create a Criteria query object.
     *  @return query object
     */
    @Override
    public <T> CriteriaQuery<T> createQuery(Class<T> resultClass) {
        if (resultClass == null) {
            return (CriteriaQuery<T>) this.createQuery();
        }
        if (resultClass.equals(Tuple.class)) {
            return new CriteriaQueryImpl(this.metamodel, ResultType.TUPLE, resultClass, this);
        } else if (resultClass.equals(ClassConstants.AOBJECT)) {
            return new CriteriaQueryImpl<T>(this.metamodel, ResultType.OBJECT_ARRAY, resultClass, this);
        } else if (resultClass.isArray()) {
            return new CriteriaQueryImpl<T>(this.metamodel, ResultType.OBJECT_ARRAY, resultClass, this);
        } else {
            if (resultClass.equals(ClassConstants.OBJECT)) {
                return (CriteriaQuery<T>) this.createQuery();
            } else {
                if (resultClass.isPrimitive() || resultClass.equals(ClassConstants.STRING)|| BasicTypeHelperImpl.getInstance().isWrapperClass(resultClass) || BasicTypeHelperImpl.getInstance().isDateClass(resultClass)) {
                    return new CriteriaQueryImpl<T>(metamodel, ResultType.OTHER, resultClass, this);
                } else {
                    TypeImpl type = ((MetamodelImpl)this.metamodel).getType(resultClass);
                    if (type != null && type.getPersistenceType().equals(PersistenceType.ENTITY)) {
                        return new CriteriaQueryImpl(this.metamodel, ResultType.ENTITY, resultClass, this);
                    } else {
                        return new CriteriaQueryImpl(this.metamodel, ResultType.CONSTRUCTOR, resultClass, this);
                    }
                }
            }
        }

    }

    /**
     *  Create a Criteria query object that returns a tuple of
     *  objects as its result.
     *  @return query object
     */
    @Override
    public CriteriaQuery<Tuple> createTupleQuery(){
        return new CriteriaQueryImpl(this.metamodel, ResultType.TUPLE, Tuple.class, this);
    }

    /**
     * Define a select list item corresponding to a constructor.
     *
     * @param result
     *            class whose instance is to be constructed
     * @param selections
     *            arguments to the constructor
     * @return selection item
     */
    @Override
    public <Y> CompoundSelection<Y> construct(Class<Y> result, Selection<?>... selections){
        return new ConstructorSelectionImpl(result, selections);
    }


    @Override
    public CompoundSelection<Tuple> tuple(Selection<?>... selections){
        return new CompoundSelectionImpl(Tuple.class, selections, true);
    }

    /**
     * Create an array-valued selection item
     * @param selections  selection items
     * @return array-valued compound selection
     * @throws IllegalArgumentException if an argument is a tuple- or
     *          array-valued selection item
     */
    @Override
    public CompoundSelection<Object[]> array(Selection<?>... selections){
        return new CompoundSelectionImpl(ClassConstants.AOBJECT, selections, true);
    }

    /**
     * Create an ordering by the ascending value of the expression.
     *
     * @param x
     *            expression used to define the ordering
     * @return ascending ordering corresponding to the expression
     */
    @Override
    public Order asc(Expression<?> x){
        if (((InternalSelection)x).getCurrentNode() == null){
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("OPERATOR_EXPRESSION_IS_CONJUNCTION"));
        }
        return new OrderImpl(x);
    }

    /**
     * Create an ordering by the descending value of the expression.
     *
     * @param x
     *            expression used to define the ordering
     * @return descending ordering corresponding to the expression
     */
    @Override
    public Order desc(Expression<?> x){
        if (((InternalSelection)x).getCurrentNode() == null){
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("OPERATOR_EXPRESSION_IS_CONJUNCTION"));
        }
        OrderImpl order = new OrderImpl(x, false);
        return order;
    }

    // aggregate functions:
    /**
     * Create an expression applying the avg operation.
     *
     * @param x
     *            expression representing input value to avg operation
     * @return avg expression
     */
    @Override
    public <N extends Number> Expression<Double> avg(Expression<N> x){
        return new FunctionExpressionImpl<Double>(this.metamodel, ClassConstants.DOUBLE,((InternalSelection)x).getCurrentNode().average(), buildList(x),"AVG");
    }

    /**
     * Create an expression applying the sum operation.
     *
     * @param x
     *            expression representing input value to sum operation
     * @return sum expression
     */
    @Override
    public <N extends Number> Expression<N> sum(Expression<N> x){
        return new FunctionExpressionImpl<N>(this.metamodel, (Class<N>) x.getJavaType(), ((InternalSelection)x).getCurrentNode().sum(), buildList(x),"SUM");
    }

    /**
     * Create an expression applying the numerical max operation.
     *
     * @param x
     *            expression representing input value to max operation
     * @return max expression
     */
    @Override
    public <N extends Number> Expression<N> max(Expression<N> x){
        return new FunctionExpressionImpl<N>(this.metamodel, (Class<N>) x.getJavaType(), ((InternalSelection)x).getCurrentNode().maximum(), buildList(x),"MAX");
    }

    /**
     * Create an expression applying the numerical min operation.
     *
     * @param x
     *            expression representing input value to min operation
     * @return min expression
     */
    @Override
    public <N extends Number> Expression<N> min(Expression<N> x){
        return new FunctionExpressionImpl<N>(this.metamodel, (Class<N>) x.getJavaType(), ((InternalSelection)x).getCurrentNode().minimum(), buildList(x),"MIN");
    }

    /**
     * Create an aggregate expression for finding the greatest of the values
     * (strings, dates, etc).
     *
     * @param x
     *            expression representing input value to greatest operation
     * @return greatest expression
     */
    @Override
    public <X extends Comparable<? super X>> Expression<X> greatest(Expression<X> x){
        if (((InternalSelection)x).getCurrentNode() == null){
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("OPERATOR_EXPRESSION_IS_CONJUNCTION"));
        }
        return new ExpressionImpl(this.metamodel, x.getJavaType(),((InternalSelection)x).getCurrentNode().maximum());
    }

    /**
     * Create an aggregate expression for finding the least of the values
     * (strings, dates, etc).
     *
     * @param x
     *            expression representing input value to least operation
     * @return least expression
     */
    @Override
    public <X extends Comparable<? super X>> Expression<X> least(Expression<X> x){
        if (((InternalSelection)x).getCurrentNode() == null){
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("OPERATOR_EXPRESSION_IS_CONJUNCTION"));
        }
        return new ExpressionImpl(this.metamodel, x.getJavaType(),((InternalSelection)x).getCurrentNode().minimum());
    }

    /**
     * Create an expression applying the count operation.
     *
     * @param x
     *            expression representing input value to count operation
     * @return count expression
     */
    @Override
    public Expression<Long> count(Expression<?> x){
        if (((InternalSelection)x).getCurrentNode() == null){
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("OPERATOR_EXPRESSION_IS_CONJUNCTION"));
        }
        return new FunctionExpressionImpl(this.metamodel, ClassConstants.LONG, ((InternalSelection)x).getCurrentNode().count(), buildList(x),"COUNT");
    }

    /**
     * Create an expression applying the count distinct operation.
     *
     * @param x
     *            expression representing input value to count distinct
     *            operation
     * @return count distinct expression
     */
    @Override
    public Expression<Long> countDistinct(Expression<?> x){
        if (((InternalSelection)x).getCurrentNode() == null){
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("OPERATOR_EXPRESSION_IS_CONJUNCTION"));
        }
        return new FunctionExpressionImpl(this.metamodel, ClassConstants.LONG, ((InternalSelection)x).getCurrentNode().distinct().count(), buildList(x),"COUNT");
    }

    // subqueries:
    /**
     * Create a predicate testing the existence of a subquery result.
     *
     * @param subquery
     *            subquery whose result is to be tested
     * @return exists predicate
     */
    @Override
    public Predicate exists(Subquery<?> subquery){
        // Setting SubQuery's SubSelectExpression as a base for the expression created by operator allows setting a new ExpressionBuilder later in the SubSelectExpression (see integrateRoot method in SubQueryImpl).
        return new CompoundExpressionImpl(metamodel, ExpressionOperator.getOperator(Integer.valueOf(ExpressionOperator.Exists)).expressionFor(((SubQueryImpl)subquery).getCurrentNode()), buildList(subquery), "exists");
    }

    /**
     * Create a predicate corresponding to an all expression over the subquery
     * results.
     *
     * @param subquery
     * @return all expression
     */
    @Override
    public <Y> Expression<Y> all(Subquery<Y> subquery){
        return new FunctionExpressionImpl<Y>(metamodel, (Class<Y>) subquery.getJavaType(), new ExpressionBuilder().all(((InternalSelection)subquery).getCurrentNode()), buildList(subquery), "all");
    }

    /**
     * Create a predicate corresponding to a some expression over the subquery
     * results. This is equivalent to an any expression.
     *
     * @param subquery
     * @return all expression
     */
    @Override
    public <Y> Expression<Y> some(Subquery<Y> subquery){
        return new FunctionExpressionImpl<Y>(metamodel, (Class<Y>) subquery.getJavaType(), new ExpressionBuilder().some(((InternalSelection)subquery).getCurrentNode()), buildList(subquery), "some");
    }

    /**
     * Create a predicate corresponding to an any expression over the subquery
     * results. This is equivalent to a some expression.
     *
     * @param subquery
     * @return any expression
     */
    @Override
    public <Y> Expression<Y> any(Subquery<Y> subquery){
        return new FunctionExpressionImpl<Y>(metamodel, (Class<Y>) subquery.getJavaType(), new ExpressionBuilder().any(((InternalSelection)subquery).getCurrentNode()), buildList(subquery), "any");
   }

    // boolean functions:
    /**
     * Create a conjunction of the given boolean expressions.
     *
     * @param x
     *            boolean expression
     * @param y
     *            boolean expression
     * @return and predicate
     */
    @Override
    public Predicate and(Expression<Boolean> x, Expression<Boolean> y){
        CompoundExpressionImpl xp = null;
        CompoundExpressionImpl yp = null;


        if (((InternalExpression)x).isExpression()){
            xp = (CompoundExpressionImpl)this.isTrue(x);
        }else{
            xp = (CompoundExpressionImpl)x;
        }
        if (((InternalExpression)y).isExpression()){
            yp = (CompoundExpressionImpl)this.isTrue(y);
        }else{
            yp = (CompoundExpressionImpl)y;
        }

        //bug 413084
        if (yp.isJunction()){
            if ( ((PredicateImpl)yp).getJunctionValue()){
                return xp;//yp is true and so can be ignored/extracted
            }else{
                return yp;//yp is false so the statement is false
            }
        }
        if (xp.isJunction()){
            if (((PredicateImpl)xp).getJunctionValue()){
                return yp;
            }else{
                return xp;
            }
        }
        org.eclipse.persistence.expressions.Expression currentNode = xp.getCurrentNode().and(yp.getCurrentNode());
        xp.setParentNode(currentNode);
        yp.setParentNode(currentNode);
        return new PredicateImpl(this.metamodel, currentNode, buildList(xp,yp), BooleanOperator.AND);
    }

    /**
     * Create a disjunction of the given boolean expressions.
     *
     * @param x
     *            boolean expression
     * @param y
     *            boolean expression
     * @return or predicate
     */
    @Override
    public Predicate or(Expression<Boolean> x, Expression<Boolean> y){
        CompoundExpressionImpl xp = null;
        CompoundExpressionImpl yp = null;

        if (((InternalExpression)x).isExpression()){
            xp = (CompoundExpressionImpl)this.isTrue(x);
        }else{
            xp = (CompoundExpressionImpl)x;
        }
        if (((InternalExpression)y).isExpression()){
            yp = (CompoundExpressionImpl)this.isTrue(y);
        }else{
            yp = (CompoundExpressionImpl)y;
        }
        //bug 413084
        if (yp.isJunction()){
            if (((PredicateImpl)yp).getJunctionValue()){
                return yp;//yp is true so the statement is true
            }
            return xp;//yp is false so can be extracted.
        }
        if (xp.isJunction()){
            if (((PredicateImpl)xp).getJunctionValue()){
                return xp;
            }
            return yp;
        }
        org.eclipse.persistence.expressions.Expression parentNode = xp.getCurrentNode().or(yp.getCurrentNode());
        xp.setParentNode(parentNode);
        yp.setParentNode(parentNode);
        return new PredicateImpl(this.metamodel, parentNode, buildList(xp,yp), BooleanOperator.OR);
    }

    /**
     * Create a conjunction of the given restriction predicates. A conjunction
     * of zero predicates is true.
     *
     * @param restrictions
     *            zero or more restriction predicates
     * @return and predicate
     */
    @Override
    public Predicate and(Predicate... restrictions){
        int max = restrictions.length;
        if (max == 0){
            return this.conjunction();
        }
        Predicate a = restrictions[0];
        for (int i = 1; i < max; ++i){
            a = this.and(a, restrictions[i]);
        }
        return a;
    }

    /**
     * Create a disjunction of the given restriction predicates. A disjunction
     * of zero predicates is false.
     *
     * @param restrictions
     *            zero or more restriction predicates
     * @return and predicate
     */
    @Override
    public Predicate or(Predicate... restrictions){
        int max = restrictions.length;
        if (max == 0){
            return this.disjunction();
        }
        Predicate a = restrictions[0];
        for (int i = 1; i < max; ++i){
            a = this.or(a, restrictions[i]);
        }
        return a;
    }

    /**
     * Create a negation of the given restriction.
     *
     * @param restriction
     *            restriction expression
     * @return not predicate
     */
    @Override
    public Predicate not(Expression<Boolean> restriction){
        if (((InternalExpression)restriction).isPredicate()){
            return ((Predicate)restriction).not();
        }
        org.eclipse.persistence.expressions.Expression parentNode = null;
        List<Expression<?>> compoundExpressions = null;
        String name = "not";
        if (((InternalExpression)restriction).isCompoundExpression() && ((CompoundExpressionImpl)restriction).getOperation().equals("exists")){
            FunctionExpression exp = (FunctionExpression) ((InternalSelection)restriction).getCurrentNode();
            SubSelectExpression sub = (SubSelectExpression) exp.getChildren().get(0);
            parentNode = ExpressionOperator.getOperator(Integer.valueOf(ExpressionOperator.NotExists)).expressionFor(sub);
            name = "notExists";
            compoundExpressions = ((CompoundExpressionImpl)restriction).getChildExpressions();
        }else{
            parentNode = ((InternalSelection)restriction).getCurrentNode().not();
            compoundExpressions = buildList(restriction);
        }
        CompoundExpressionImpl expr = new CompoundExpressionImpl(this.metamodel, parentNode, compoundExpressions, name);
        expr.setIsNegated(true);
        return expr;
    }

    /**
     * Create a conjunction (with zero conjuncts). A conjunction with zero
     * conjuncts is true.
     *
     * @return and predicate
     */
    @Override
    public Predicate conjunction(){
        return new PredicateImpl(this.metamodel, null, null, BooleanOperator.AND);
    }

    /**
     * Create a disjunction (with zero disjuncts). A disjunction with zero
     * disjuncts is false.
     *
     * @return or predicate
     */
    @Override
    public Predicate disjunction(){
        return new PredicateImpl(this.metamodel, null, null, BooleanOperator.OR);
    }

    // turn Expression<Boolean> into a Predicate
    // useful for use with varargs methods
    /**
     * Create a predicate testing for a true value.
     *
     * @param x
     *            expression to be tested if true
     * @return predicate
     */
    @Override
    public Predicate isTrue(Expression<Boolean> x){
        if (((InternalExpression)x).isPredicate()){
            if (((InternalSelection)x).getCurrentNode() == null){
                return (Predicate)x;
            }else{
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage("PREDICATE_PASSED_TO_EVALUATION"));
            }
        }
        List list = new ArrayList();
        list.add(x);
        return new CompoundExpressionImpl(this.metamodel, ((InternalSelection)x).getCurrentNode().equal(true), list, "equals");
    }

    /**
     * Create a predicate testing for a false value.
     *
     * @param x
     *            expression to be tested if false
     * @return predicate
     */
    @Override
    public Predicate isFalse(Expression<Boolean> x){
        if (((InternalExpression)x).isPredicate()){
            if (((InternalSelection)x).getCurrentNode() == null){
                if (((Predicate)x).getOperator() == BooleanOperator.AND){
                    return (Predicate)x;
                }else{
                    return this.conjunction();
                }
            }else{
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage("PREDICATE_PASSED_TO_EVALUATION"));
            }
        }
        return new CompoundExpressionImpl(this.metamodel, ((InternalSelection)x).getCurrentNode().equal(false), buildList(x), "equals");
    }

    //null tests:
    /**
     * Create a predicate to test whether the expression is null.
     * @param x expression
     * @return predicate
     */
    @Override
    public Predicate isNull(Expression<?> x){
        return new PredicateImpl(this.metamodel, ((InternalSelection)x).getCurrentNode().isNull(), new ArrayList(), BooleanOperator.AND);
    }

    /**
     * Create a predicate to test whether the expression is not null.
     * @param x expression
     * @return predicate
     */
    @Override
    public Predicate isNotNull(Expression<?> x){
        return new PredicateImpl(this.metamodel, ((InternalSelection)x).getCurrentNode().notNull(),new ArrayList(), BooleanOperator.AND);
    }

    // equality:
    /**
     * Create a predicate for testing the arguments for equality.
     *
     * @param x
     *            expression
     * @param y
     *            expression
     * @return equality predicate
     */
    @Override
    public Predicate equal(Expression<?> x, Expression<?> y){

        List list = new ArrayList();
        list.add(x);
        list.add(y);
        return new CompoundExpressionImpl(this.metamodel, ((InternalSelection)x).getCurrentNode().equal(((InternalSelection)y).getCurrentNode()), list, "equals");
    }

    /**
     * Create a predicate for testing the arguments for inequality.
     *
     * @param x
     *            expression
     * @param y
     *            expression
     * @return inequality predicate
     */
    @Override
    public Predicate notEqual(Expression<?> x, Expression<?> y){
        if (((InternalSelection)x).getCurrentNode() == null || ((InternalSelection)y).getCurrentNode() == null){
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("OPERATOR_EXPRESSION_IS_CONJUNCTION"));
        }
        List list = new ArrayList();
        list.add(x);
        list.add(y);
        return new CompoundExpressionImpl(this.metamodel, ((InternalSelection)x).getCurrentNode().notEqual(((InternalSelection)y).getCurrentNode()), list, "not equal");
    }

    /**
     * Create a predicate for testing the arguments for equality.
     *
     * @param x
     *            expression
     * @param y
     *            object
     * @return equality predicate
     */
    @Override
    public Predicate equal(Expression<?> x, Object y){
        //parameter is not an expression.
        if (((InternalSelection)x).getCurrentNode() == null){
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("OPERATOR_EXPRESSION_IS_CONJUNCTION"));
        }
        if (y instanceof ParameterExpression) return this.equal(x, (ParameterExpression)y);

        List list = new ArrayList();
        list.add(x);
        list.add(this.internalLiteral(y));
        return new CompoundExpressionImpl(this.metamodel, ((InternalSelection)x).getCurrentNode().equal(y), list, "equal");
    }

    /**
     * Create a predicate for testing the arguments for inequality.
     *
     * @param x
     *            expression
     * @param y
     *            object
     * @return inequality predicate
     */
    @Override
    public Predicate notEqual(Expression<?> x, Object y){
        if (((InternalSelection)x).getCurrentNode() == null){
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("OPERATOR_EXPRESSION_IS_CONJUNCTION"));
        }
        if (y instanceof ParameterExpression) return this.notEqual(x, (ParameterExpression)y);
        List list = new ArrayList();
        list.add(x);
        list.add(this.internalLiteral(y));
        return new CompoundExpressionImpl(this.metamodel, ((InternalSelection)x).getCurrentNode().notEqual(y), list, "not equal");
    }

    // comparisons for generic (non-numeric) operands:
    /**
     * Create a predicate for testing whether the first argument is greater than
     * the second.
     *
     * @param x
     *            expression
     * @param y
     *            expression
     * @return greater-than predicate
     */
    @Override
    public <Y extends Comparable<? super Y>> Predicate greaterThan(Expression<? extends Y> x, Expression<? extends Y> y){
        if (((InternalSelection)x).getCurrentNode() == null || ((InternalSelection)y).getCurrentNode() == null){
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("OPERATOR_EXPRESSION_IS_CONJUNCTION"));
        }
        List list = new ArrayList();
        list.add(x);
        list.add(y);
        return new CompoundExpressionImpl(this.metamodel, ((InternalSelection)x).getCurrentNode().greaterThan(((InternalSelection)y).getCurrentNode()), list, "greaterThan");
    }

    /**
     * Create a predicate for testing whether the first argument is less than
     * the second.
     *
     * @param x
     *            expression
     * @param y
     *            expression
     * @return less-than predicate
     */
    @Override
    public <Y extends Comparable<? super Y>> Predicate lessThan(Expression<? extends Y> x, Expression<? extends Y> y){
        if (((InternalSelection)x).getCurrentNode() == null || ((InternalSelection)y).getCurrentNode() == null){
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("OPERATOR_EXPRESSION_IS_CONJUNCTION"));
        }
        return new CompoundExpressionImpl(this.metamodel, ((InternalSelection)x).getCurrentNode().lessThan(((InternalSelection)y).getCurrentNode()), buildList(x,y), "lessThan");
    }

    /**
     * Create a predicate for testing whether the first argument is greater than
     * or equal to the second.
     *
     * @param x
     *            expression
     * @param y
     *            expression
     * @return greater-than-or-equal predicate
     */
    @Override
    public <Y extends Comparable<? super Y>> Predicate greaterThanOrEqualTo(Expression<? extends Y> x, Expression<? extends Y> y){
        if (((ExpressionImpl)x).getCurrentNode() == null || ((ExpressionImpl)y).getCurrentNode() == null){
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("OPERATOR_EXPRESSION_IS_CONJUNCTION"));
        }
        List list = new ArrayList();
        list.add(x);
        list.add(y);
        return new CompoundExpressionImpl(this.metamodel, ((ExpressionImpl)x).getCurrentNode().greaterThanEqual(((ExpressionImpl)y).getCurrentNode()), list, "greaterThanEqual");
    }

    /**
     * Create a predicate for testing whether the first argument is less than or
     * equal to the second.
     *
     * @param x
     *            expression
     * @param y
     *            expression
     * @return less-than-or-equal predicate
     */
    @Override
    public <Y extends Comparable<? super Y>> Predicate lessThanOrEqualTo(Expression<? extends Y> x, Expression<? extends Y> y){
        if (((ExpressionImpl)x).getCurrentNode() == null || ((ExpressionImpl)y).getCurrentNode() == null){
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("OPERATOR_EXPRESSION_IS_CONJUNCTION"));
        }
        List list = new ArrayList();
        list.add(x);
        list.add(y);
        return new CompoundExpressionImpl(this.metamodel, ((ExpressionImpl)x).getCurrentNode().lessThanEqual(((ExpressionImpl)y).getCurrentNode()), list, "lessThanEqual");
    }

    /**
     * Create a predicate for testing whether the first argument is between the
     * second and third arguments in value.
     *
     * @param v
     *            expression
     * @param x
     *            expression
     * @param y
     *            expression
     * @return between predicate
     */
    @Override
    public <Y extends Comparable<? super Y>> Predicate between(Expression<? extends Y> v, Expression<? extends Y> x, Expression<? extends Y> y){

        List list = new ArrayList();
        list.add(v);
        list.add(x);
        list.add(y);
        return new CompoundExpressionImpl(this.metamodel, ((ExpressionImpl)v).getCurrentNode().between(((ExpressionImpl)x).getCurrentNode(), ((ExpressionImpl)y).getCurrentNode()), list, "between");
    }

    /**
     * Create a predicate for testing whether the first argument is greater than
     * the second.
     *
     * @param x
     *            expression
     * @param y
     *            value
     * @return greater-than predicate
     */
    @Override
    public <Y extends Comparable<? super Y>> Predicate greaterThan(Expression<? extends Y> x, Y y){
        Expression<Y> expressionY = this.internalLiteral(y);
        if (((ExpressionImpl)x).getCurrentNode() == null ){
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("OPERATOR_EXPRESSION_IS_CONJUNCTION"));
        }
        List list = new ArrayList();
        list.add(x);
        list.add(expressionY);
        return new CompoundExpressionImpl(this.metamodel, ((ExpressionImpl)x).getCurrentNode().greaterThan(((ExpressionImpl)expressionY).getCurrentNode()), list, "greaterThan");
    }

    /**
     * Create a predicate for testing whether the first argument is less than
     * the second.
     *
     * @param x
     *            expression
     * @param y
     *            value
     * @return less-than predicate
     */
    @Override
    public <Y extends Comparable<? super Y>> Predicate lessThan(Expression<? extends Y> x, Y y){
        Expression<Y> expressionY = this.internalLiteral(y);
        if (((ExpressionImpl)x).getCurrentNode() == null ){
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("OPERATOR_EXPRESSION_IS_CONJUNCTION"));
        }
        List list = new ArrayList();
        list.add(x);
        list.add(expressionY);
        return new CompoundExpressionImpl(this.metamodel, ((ExpressionImpl)x).getCurrentNode().lessThan(((ExpressionImpl)expressionY).getCurrentNode()), list, "lessThan");
    }

    /**
     * Create a predicate for testing whether the first argument is greater than
     * or equal to the second.
     *
     * @param x
     *            expression
     * @param y
     *            value
     * @return greater-than-or-equal predicate
     */
    @Override
    public <Y extends Comparable<? super Y>> Predicate greaterThanOrEqualTo(Expression<? extends Y> x, Y y){
        Expression<Y> expressionY = this.internalLiteral(y);
        if (((ExpressionImpl)x).getCurrentNode() == null ){
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("OPERATOR_EXPRESSION_IS_CONJUNCTION"));
        }
        List list = new ArrayList();
        list.add(x);
        list.add(expressionY);
        return new CompoundExpressionImpl(this.metamodel, ((ExpressionImpl)x).getCurrentNode().greaterThanEqual(((ExpressionImpl)expressionY).getCurrentNode()), list, "greaterThanEqual");
    }

    /**
     * Create a predicate for testing whether the first argument is less than or
     * equal to the second.
     *
     * @param x
     *            expression
     * @param y
     *            value
     * @return less-than-or-equal predicate
     */
    @Override
    public <Y extends Comparable<? super Y>> Predicate lessThanOrEqualTo(Expression<? extends Y> x, Y y){
        Expression<Y> expressionY = this.internalLiteral(y);
        if (((ExpressionImpl)x).getCurrentNode() == null ){
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("OPERATOR_EXPRESSION_IS_CONJUNCTION"));
        }
        List list = new ArrayList();
        list.add(x);
        list.add(expressionY);
        return new CompoundExpressionImpl(this.metamodel, ((ExpressionImpl)x).getCurrentNode().lessThanEqual(((ExpressionImpl)expressionY).getCurrentNode()), list, "lessThanEqual");
    }

    /**
     * Create a predicate for testing whether the first argument is between the
     * second and third arguments in value.
     *
     * @param v
     *            expression
     * @param x
     *            value
     * @param y
     *            value
     * @return between predicate
     */
    @Override
    public <Y extends Comparable<? super Y>> Predicate between(Expression<? extends Y> v, Y x, Y y){
        List list = new ArrayList();
        list.add(v);
        list.add(this.internalLiteral(x));
        list.add(this.internalLiteral(y));
        return new CompoundExpressionImpl(this.metamodel, ((ExpressionImpl)v).getCurrentNode().between(x, y), list, "between");
    }

    protected List<Expression<?>> buildList(Expression<?>... expressions){
        ArrayList list = new ArrayList();
        for(Expression<?> exp : expressions){
            list.add(exp);
        }
        return list;
    }

    // comparisons for numeric operands:
    /**
     * Create a predicate for testing whether the first argument is greater than
     * the second.
     *
     * @param x
     *            expression
     * @param y
     *            expression
     * @return greater-than predicate
     */
    @Override
    public Predicate gt(Expression<? extends Number> x, Expression<? extends Number> y){
        List list = new ArrayList();
        list.add(x);
        list.add(y);
        return new CompoundExpressionImpl(this.metamodel, ((InternalSelection)x).getCurrentNode().greaterThan(((InternalSelection)y).getCurrentNode()), list, "gt");
    }

    /**
     * Create a predicate for testing whether the first argument is less than
     * the second.
     *
     * @param x
     *            expression
     * @param y
     *            expression
     * @return less-than predicate
     */
    @Override
    public Predicate lt(Expression<? extends Number> x, Expression<? extends Number> y){
        return new CompoundExpressionImpl(this.metamodel, ((InternalSelection)x).getCurrentNode().lessThan(((InternalSelection)y).getCurrentNode()), buildList(x,y), "lessThan");
    }

    /**
     * Create a predicate for testing whether the first argument is greater than
     * or equal to the second.
     *
     * @param x
     *            expression
     * @param y
     *            expression
     * @return greater-than-or-equal predicate
     */
    @Override
    public Predicate ge(Expression<? extends Number> x, Expression<? extends Number> y){
        List list = new ArrayList();
        list.add(x);
        list.add(y);
        return new CompoundExpressionImpl(this.metamodel, ((InternalSelection)x).getCurrentNode().greaterThanEqual(((InternalSelection)y).getCurrentNode()), list, "ge");
    }

    /**
     * Create a predicate for testing whether the first argument is less than or
     * equal to the second.
     *
     * @param x
     *            expression
     * @param y
     *            expression
     * @return less-than-or-equal predicate
     */
    @Override
    public Predicate le(Expression<? extends Number> x, Expression<? extends Number> y){
        List list = new ArrayList();
        list.add(x);
        list.add(y);
        return new CompoundExpressionImpl(this.metamodel, ((InternalSelection)x).getCurrentNode().lessThanEqual(((InternalSelection)y).getCurrentNode()), list, "le");
    }

    /**
     * Create a predicate for testing whether the first argument is greater than
     * the second.
     *
     * @param x
     *            expression
     * @param y
     *            value
     * @return greater-than predicate
     */
    @Override
    public Predicate gt(Expression<? extends Number> x, Number y){
        List list = new ArrayList();
        list.add(x);
        list.add(internalLiteral(y));
        return new CompoundExpressionImpl(this.metamodel, ((InternalSelection)x).getCurrentNode().greaterThan(y), list, "gt");
    }

    /**
     * Create a predicate for testing whether the first argument is less than
     * the second.
     *
     * @param x
     *            expression
     * @param y
     *            value
     * @return less-than predicate
     */
    @Override
    public Predicate lt(Expression<? extends Number> x, Number y){
        return new CompoundExpressionImpl(this.metamodel, ((InternalSelection)x).getCurrentNode().lessThan(y), buildList(x,internalLiteral(y)), "lt");
    }

    /**
     * Create a predicate for testing whether the first argument is greater than
     * or equal to the second.
     *
     * @param x
     *            expression
     * @param y
     *            value
     * @return greater-than-or-equal predicate
     */
    @Override
    public Predicate ge(Expression<? extends Number> x, Number y){
        List list = new ArrayList();
        list.add(x);
        list.add(internalLiteral(y));
        return new CompoundExpressionImpl(this.metamodel, ((InternalSelection)x).getCurrentNode().greaterThanEqual(y), list, "ge");
    }

    /**
     * Create a predicate for testing whether the first argument is less than or
     * equal to the second.
     *
     * @param x
     *            expression
     * @param y
     *            value
     * @return less-than-or-equal predicate
     */
    @Override
    public Predicate le(Expression<? extends Number> x, Number y){
        return new CompoundExpressionImpl(this.metamodel, ((InternalSelection)x).getCurrentNode().lessThanEqual(y), buildList(x, internalLiteral(y)), "le");
    }

    // numerical operations:
    /**
     * Create an expression that returns the arithmetic negation of its
     * argument.
     *
     * @param x
     *            expression
     * @return negated expression
     */
    @Override
    public <N extends Number> Expression<N> neg(Expression<N> x){
        return new FunctionExpressionImpl(this.metamodel, ClassConstants.INTEGER, ExpressionMath.negate(((InternalSelection)x).getCurrentNode()), buildList(x), "neg");
    }

    /**
     * Create an expression that returns the absolute value of its argument.
     *
     * @param x
     *            expression
     * @return absolute value
     */
    @Override
    public <N extends Number> Expression<N> abs(Expression<N> x){
        return new FunctionExpressionImpl<N>(metamodel, (Class<N>) x.getJavaType(), ExpressionMath.abs(((InternalSelection)x).getCurrentNode()), buildList(x),"ABS");
    }

    /**
     * Create an expression that returns the sum of its arguments.
     *
     * @param x
     *            expression
     * @param y
     *            expression
     * @return sum
     */
    @Override
    public <N extends Number> Expression<N> sum(Expression<? extends N> x, Expression<? extends N> y){
        return new FunctionExpressionImpl(this.metamodel, (Class<N>)BasicTypeHelperImpl.getInstance().extendedBinaryNumericPromotion(x.getJavaType(), y.getJavaType()), ExpressionMath.add(((InternalSelection)x).getCurrentNode(),((InternalSelection)y).getCurrentNode()), buildList(x,y), "sum");
    }
    /**
     * Create an aggregate expression applying the sum operation to an
     * Integer-valued expression, returning a Long result.
     * @param x  expression representing input value to sum operation
     * @return sum expression
     */
    @Override
    public Expression<Long> sumAsLong(Expression<Integer> x) {
        return new FunctionExpressionImpl(this.metamodel, ClassConstants.LONG, ((InternalSelection)x).getCurrentNode().sum(), buildList(x),"SUM");
    }

    /**
     * Create an aggregate expression applying the sum operation to a
     * Float-valued expression, returning a Double result.
     * @param x  expression representing input value to sum operation
     * @return sum expression
     */
    @Override
    public Expression<Double> sumAsDouble(Expression<Float> x){
        return new FunctionExpressionImpl(this.metamodel, ClassConstants.DOUBLE, ((InternalSelection)x).getCurrentNode().sum(), buildList(x),"SUM");
    }
    /**
     * Create an expression that returns the product of its arguments.
     *
     * @param x
     *            expression
     * @param y
     *            expression
     * @return product
     */
    @Override
    public <N extends Number> Expression<N> prod(Expression<? extends N> x, Expression<? extends N> y){
        return new FunctionExpressionImpl(this.metamodel, (Class<N>)BasicTypeHelperImpl.getInstance().extendedBinaryNumericPromotion(x.getJavaType(), y.getJavaType()), ExpressionMath.multiply(((InternalSelection)x).getCurrentNode(),((InternalSelection)y).getCurrentNode()), buildList(x,y), "prod");
    }

    /**
     * Create an expression that returns the difference between its arguments.
     *
     * @param x
     *            expression
     * @param y
     *            expression
     * @return difference
     */
    @Override
    public <N extends Number> Expression<N> diff(Expression<? extends N> x, Expression<? extends N> y){
        List list = new ArrayList();
        list.add(x);
        list.add(y);
        return new FunctionExpressionImpl(this.metamodel, x.getJavaType(), ExpressionMath.subtract(((InternalSelection)x).getCurrentNode(), ((InternalSelection)y).getCurrentNode()), list, "diff");
    }

    /**
     * Create an expression that returns the sum of its arguments.
     *
     * @param x
     *            expression
     * @param y
     *            value
     * @return sum
     */
    @Override
    public <N extends Number> Expression<N> sum(Expression<? extends N> x, N y){
        return new FunctionExpressionImpl(this.metamodel, (Class<N>)BasicTypeHelperImpl.getInstance().extendedBinaryNumericPromotion(x.getJavaType(), y.getClass()), ExpressionMath.add(((InternalSelection)x).getCurrentNode(),y), buildList(x,internalLiteral(y)), "sum");
    }

    /**
     * Create an expression that returns the product of its arguments.
     *
     * @param x
     *            expression
     * @param y
     *            value
     * @return product
     */
    @Override
    public <N extends Number> Expression<N> prod(Expression<? extends N> x, N y){
        return new FunctionExpressionImpl(this.metamodel, (Class<N>)BasicTypeHelperImpl.getInstance().extendedBinaryNumericPromotion(x.getJavaType(), y.getClass()), ExpressionMath.multiply(((InternalSelection)x).getCurrentNode(),y), buildList(x,internalLiteral(y)), "prod");
    }

    /**
     * Create an expression that returns the difference between its arguments.
     *
     * @param x
     *            expression
     * @param y
     *            value
     * @return difference
     */
    @Override
    public <N extends Number> Expression<N> diff(Expression<? extends N> x, N y){
        List list = new ArrayList();
        list.add(x);
        list.add(this.internalLiteral(y));
        return new FunctionExpressionImpl(this.metamodel, y.getClass(), ExpressionMath.subtract(((InternalSelection)x).getCurrentNode(), y), list, "diff");
    }

    /**
     * Create an expression that returns the sum of its arguments.
     *
     * @param x
     *            value
     * @param y
     *            expression
     * @return sum
     */
    @Override
    public <N extends Number> Expression<N> sum(N x, Expression<? extends N> y){
        return new FunctionExpressionImpl(this.metamodel, (Class<N>)BasicTypeHelperImpl.getInstance().extendedBinaryNumericPromotion(x.getClass(), y.getJavaType()), ExpressionMath.add(new ConstantExpression(x, ((InternalSelection)y).getCurrentNode()),((InternalSelection)y).getCurrentNode()), buildList(internalLiteral(x),y), "sum");
    }

    /**
     * Create an expression that returns the product of its arguments.
     *
     * @param x
     *            value
     * @param y
     *            expression
     * @return product
     */
    @Override
    public <N extends Number> Expression<N> prod(N x, Expression<? extends N> y){
        return new FunctionExpressionImpl(this.metamodel, (Class<N>)BasicTypeHelperImpl.getInstance().extendedBinaryNumericPromotion(x.getClass(), y.getJavaType()), ExpressionMath.multiply(new ConstantExpression(x, ((InternalSelection)y).getCurrentNode()),((InternalSelection)y).getCurrentNode()), buildList(internalLiteral(x),y), "prod");
    }

    /**
     * Create an expression that returns the difference between its arguments.
     *
     * @param x
     *            value
     * @param y
     *            expression
     * @return difference
     */
    @Override
    public <N extends Number> Expression<N> diff(N x, Expression<? extends N> y){
        List list = new ArrayList();
        ExpressionImpl literal = (ExpressionImpl) this.internalLiteral(x);
        list.add(literal);
        list.add(y);
        return new FunctionExpressionImpl(this.metamodel, literal.getJavaType(), ExpressionMath.subtract(((InternalSelection)literal).getCurrentNode(), ((InternalSelection)y).getCurrentNode()), list, "diff");
    }

    /**
     * Create an expression that returns the quotient of its arguments.
     *
     * @param x
     *            expression
     * @param y
     *            expression
     * @return quotient
     */
    @Override
    public Expression<Number> quot(Expression<? extends Number> x, Expression<? extends Number> y){
        return new FunctionExpressionImpl(this.metamodel, (Class)BasicTypeHelperImpl.getInstance().extendedBinaryNumericPromotion(x.getJavaType(), y.getClass()), ExpressionMath.divide(((InternalSelection)x).getCurrentNode(),((InternalSelection)y).getCurrentNode()), buildList(x,y), "quot");
    }

    /**
     * Create an expression that returns the quotient of its arguments.
     *
     * @param x
     *            expression
     * @param y
     *            value
     * @return quotient
     */
    @Override
    public Expression<Number> quot(Expression<? extends Number> x, Number y){
        return new FunctionExpressionImpl(this.metamodel, (Class)BasicTypeHelperImpl.getInstance().extendedBinaryNumericPromotion(x.getJavaType(), y.getClass()), ExpressionMath.divide(((InternalSelection)x).getCurrentNode(),y), buildList(x,internalLiteral(y)), "quot");
    }

    /**
     * Create an expression that returns the quotient of its arguments.
     *
     * @param x
     *            value
     * @param y
     *            expression
     * @return quotient
     */
    @Override
    public Expression<Number> quot(Number x, Expression<? extends Number> y){
        return new FunctionExpressionImpl(this.metamodel, ClassConstants.NUMBER, ExpressionMath.divide(new ConstantExpression(x, ((InternalSelection)y).getCurrentNode()),((InternalSelection)y).getCurrentNode()), buildList(internalLiteral(x),y), "quot");
    }

    /**
     * Create an expression that returns the modulus of its arguments.
     *
     * @param x
     *            expression
     * @param y
     *            expression
     * @return modulus
     */
    @Override
    public Expression<Integer> mod(Expression<Integer> x, Expression<Integer> y){
        return new FunctionExpressionImpl(this.metamodel, ClassConstants.INTEGER, ExpressionMath.mod(((InternalSelection)x).getCurrentNode(),((InternalSelection)y).getCurrentNode()), buildList(x,y), "mod");
    }

    /**
     * Create an expression that returns the modulus of its arguments.
     *
     * @param x
     *            expression
     * @param y
     *            value
     * @return modulus
     */
    @Override
    public Expression<Integer> mod(Expression<Integer> x, Integer y){
        return new FunctionExpressionImpl(this.metamodel, ClassConstants.INTEGER, ExpressionMath.mod(((InternalSelection)x).getCurrentNode(),y), buildList(x,internalLiteral(y)), "mod");
    }

    /**
     * Create an expression that returns the modulus of its arguments.
     *
     * @param x
     *            value
     * @param y
     *            expression
     * @return modulus
     */
    @Override
    public Expression<Integer> mod(Integer x, Expression<Integer> y){
        Expression xExp = internalLiteral(x);
        return new FunctionExpressionImpl(this.metamodel, ClassConstants.INTEGER, ExpressionMath.mod(((InternalSelection)xExp).getCurrentNode(),((InternalSelection)y).getCurrentNode()), buildList(xExp,y), "mod");
    }

    /**
     * Create an expression that returns the square root of its argument.
     *
     * @param x
     *            expression
     * @return modulus
     */
    @Override
    public Expression<Double> sqrt(Expression<? extends Number> x){
        return new FunctionExpressionImpl(this.metamodel, ClassConstants.DOUBLE, ExpressionMath.sqrt(((InternalSelection)x).getCurrentNode()), buildList(x), "sqrt");
    }

    // typecasts:
    /**
     * Typecast.
     *
     * @param number
     *            numeric expression
     * @return Expression&lt;Long&gt;
     */
    @Override
    public Expression<Long> toLong(Expression<? extends Number> number){
        return (Expression<Long>) number;
    }

    /**
     * Typecast.
     *
     * @param number
     *            numeric expression
     * @return Expression&lt;Integer&gt;
     */
    @Override
    public Expression<Integer> toInteger(Expression<? extends Number> number){
        return (Expression<Integer>) number;
    }

    /**
     * Typecast.
     *
     * @param number
     *            numeric expression
     * @return Expression&lt;Float&gt;
     */
    @Override
    public Expression<Float> toFloat(Expression<? extends Number> number){
        return (Expression<Float>) number;
    }

    /**
     * Typecast.
     *
     * @param number
     *            numeric expression
     * @return Expression&lt;Double&gt;
     */
    @Override
    public Expression<Double> toDouble(Expression<? extends Number> number){
        return (Expression<Double>) number;
    }

    /**
     * Typecast.
     *
     * @param number
     *            numeric expression
     * @return Expression&lt;BigDecimal&gt;
     */
    @Override
    public Expression<BigDecimal> toBigDecimal(Expression<? extends Number> number){
        return (Expression<BigDecimal>) number;
    }

    /**
     * Typecast.
     *
     * @param number
     *            numeric expression
     * @return Expression&lt;BigInteger&gt;
     */
    @Override
    public Expression<BigInteger> toBigInteger(Expression<? extends Number> number){
        return (Expression<BigInteger>) number;
    }

    /**
     * Typecast.
     *
     * @param character
     *            expression
     * @return Expression&lt;String&gt;
     */
    @Override
    public Expression<String> toString(Expression<Character> character){
        ExpressionImpl impl = (ExpressionImpl) character;
        return impl;
    }

    // literals:
    /**
     * Create an expression literal.
     *
     * @param value
     * @return expression literal
     */
    @Override
    public <T> Expression<T> literal(T value){
        if (value == null) {
            throw new IllegalArgumentException( ExceptionLocalization.buildMessage("jpa_criteriaapi_null_literal_value", new Object[]{}));
        }
        return new ExpressionImpl<T>(metamodel, (Class<T>) (value.getClass()), new ConstantExpression(value, new ExpressionBuilder()), value);
    }

    /**
     * Create an expression for a null literal with the given type.
     *
     * @param resultClass  type of the null literal
     * @return null expression literal
     */
    @Override
    public <T> Expression<T> nullLiteral(Class<T> resultClass){
        return new ExpressionImpl<T>(metamodel, resultClass, new ConstantExpression(null, new ExpressionBuilder()), null);
    }

    /**
     * Create an expression literal but without null validation.
     *
     * @param value
     * @return expression literal
     */
    protected <T> Expression<T> internalLiteral(T value){
        return new ExpressionImpl<T>(metamodel, (Class<T>) (value == null? null: value.getClass()), new ConstantExpression(value, new ExpressionBuilder()), value);
    }

    // parameters:
    /**
     * Create a parameter.
     *
     * Create a parameter expression.
     * @param paramClass parameter class
     * @return parameter expression
     */
    @Override
    public <T> ParameterExpression<T> parameter(Class<T> paramClass){
        return new ParameterExpressionImpl<T>(metamodel, paramClass);
    }

    /**
     * Create a parameter expression with the given name.
     *
     * @param paramClass
     *            parameter class
     * @param name
     * @return parameter
     */
    @Override
    public <T> ParameterExpression<T> parameter(Class<T> paramClass, String name){
        return new ParameterExpressionImpl<T>(metamodel, paramClass, name);
    }

    // collection operations:
    /**
     * Create a predicate that tests whether a collection is empty.
     *
     * @param collection
     *            expression
     * @return predicate
     */
    @Override
    public <C extends Collection<?>> Predicate isEmpty(Expression<C> collection){
        if (((InternalExpression)collection).isLiteral()){
            if (((Collection)((ConstantExpression)((InternalSelection)collection).getCurrentNode()).getValue()).isEmpty()){
                return conjunction();
            }else{
                return disjunction();
            }
        }
        return new CompoundExpressionImpl(metamodel, ((InternalSelection)collection).getCurrentNode().size(ClassConstants.INTEGER).equal(0), buildList(collection), "isEmpty");
    }

    /**
     * Create a predicate that tests whether a collection is not empty.
     *
     * @param collection
     *            expression
     * @return predicate
     */
    @Override
    public <C extends Collection<?>> Predicate isNotEmpty(Expression<C> collection){
        return new CompoundExpressionImpl(metamodel, ((InternalSelection)collection).getCurrentNode().size(ClassConstants.INTEGER).equal(0).not(), buildList(collection), "isNotEmpty");
    }

    /**
     * Create an expression that tests the size of a collection.
     *
     * @param collection
     * @return size expression
     */
    @Override
    public <C extends Collection<?>> Expression<Integer> size(C collection){
        return internalLiteral(collection.size());
    }

    /**
     * Create an expression that tests the size of a collection.
     *
     * @param collection
     *            expression
     * @return size expression
     */
    @Override
    public <C extends java.util.Collection<?>> Expression<Integer> size(Expression<C> collection){
        return new FunctionExpressionImpl(metamodel, ClassConstants.INTEGER, ((InternalSelection)collection).getCurrentNode().size(ClassConstants.INTEGER), buildList(collection), SIZE);
    }

    /**
     * Create a predicate that tests whether an element is a member of a
     * collection.
     *
     * @param elem
     *            element
     * @param collection
     *            expression
     * @return predicate
     */

    @Override
    public <E, C extends Collection<E>> Predicate isMember(E elem, Expression<C> collection){
        return new CompoundExpressionImpl(metamodel, ((InternalSelection)collection).getCurrentNode().equal(elem), buildList(collection, internalLiteral(elem)), "isMember");
    }

    /**
     * Create a predicate that tests whether an element is not a member of a
     * collection.
     *
     * @param elem
     *            element
     * @param collection
     *            expression
     * @return predicate
     */
    @Override
    public <E, C extends Collection<E>> Predicate isNotMember(E elem, Expression<C> collection){
        return new CompoundExpressionImpl(metamodel, ((InternalSelection)collection).getCurrentNode().notEqual(elem), buildList(collection, internalLiteral(elem)), "isMember");

    }

    /**
     * Create a predicate that tests whether an element is a member of a
     * collection.
     *
     * @param elem
     *            element expression
     * @param collection
     *            expression
     * @return predicate
     */
    @Override
    public <E, C extends Collection<E>> Predicate isMember(Expression<E> elem, Expression<C> collection){
        return new CompoundExpressionImpl(metamodel, ((InternalSelection)collection).getCurrentNode().equal(((InternalSelection)elem).getCurrentNode()), buildList(collection, elem), "isMember");
    }

    /**
     * Create a predicate that tests whether an element is not a member of a
     * collection.
     *
     * @param elem
     *            element expression
     * @param collection
     *            expression
     * @return predicate
     */
    @Override
    public <E, C extends Collection<E>> Predicate isNotMember(Expression<E> elem, Expression<C> collection){
        ReportQuery subQuery = new ReportQuery();
        subQuery.setReferenceClass(((ExpressionImpl)elem).getJavaType());
        org.eclipse.persistence.expressions.ExpressionBuilder elemBuilder = new org.eclipse.persistence.expressions.ExpressionBuilder();
        org.eclipse.persistence.expressions.Expression collectionExp =((InternalSelection)collection).getCurrentNode();
        org.eclipse.persistence.expressions.Expression elemExp =((InternalSelection)elem).getCurrentNode();

        subQuery.setExpressionBuilder(elemBuilder);
        subQuery.setShouldRetrieveFirstPrimaryKey(true);
        subQuery.setSelectionCriteria(elemBuilder.equal(collectionExp).and(collectionExp.equal(elemExp)));

        return new CompoundExpressionImpl(metamodel, ((InternalSelection)elem).getCurrentNode().notExists(subQuery), buildList(elem, collection), "isNotMemeber");
    }

    // get the values and keys collections of the Map, which may then
    // be passed to size(), isMember(), isEmpty(), etc
    /**
     * Create an expression that returns the values of a map.
     *
     * @param map
     * @return collection expression
     */
    @Override
    public <V, M extends Map<?, V>> Expression<Collection<V>> values(M map){
        return internalLiteral(map.values());
    }

    /**
     * Create an expression that returns the keys of a map.
     *
     * @param map
     * @return set expression
     */
    @Override
    public <K, M extends Map<K, ?>> Expression<Set<K>> keys(M map){
        return internalLiteral(map.keySet());
    }

    // string functions:
    /**
     * Create a predicate for testing whether the expression satisfies the given
     * pattern.
     *
     * @param x
     *            string expression
     * @param pattern
     *            string expression
     * @return like predicate
     */
    @Override
    public Predicate like(Expression<String> x, Expression<String> pattern){
        List list = this.buildList(x, pattern);
        return new CompoundExpressionImpl(this.metamodel,
            ((InternalSelection)x).getCurrentNode().like(((InternalSelection)pattern).getCurrentNode()), list, "like");
    }

    /**
     * Create a predicate for testing whether the expression satisfies the given
     * pattern.
     *
     * @param x
     *            string expression
     * @param pattern
     *            string expression
     * @param escapeChar
     *            escape character expression
     * @return like predicate
     */
    @Override
    public Predicate like(Expression<String> x, Expression<String> pattern, Expression<Character> escapeChar){
        List list = this.buildList(x, pattern, escapeChar);
        return new CompoundExpressionImpl(this.metamodel,
            ((InternalSelection)x).getCurrentNode().like(((InternalSelection)pattern).getCurrentNode(), ((InternalSelection)escapeChar).getCurrentNode()), list, "like");
    }

    /**
     * Create a predicate for testing whether the expression satisfies the given
     * pattern.
     *
     * @param x
     *            string expression
     * @param pattern
     *            string expression
     * @param escapeChar
     *            escape character
     * @return like predicate
     */
    @Override
    public Predicate like(Expression<String> x, Expression<String> pattern, char escapeChar){

        return this.like(x, pattern, this.internalLiteral(escapeChar));
    }

    /**
     * Create a predicate for testing whether the expression satisfies the given
     * pattern.
     *
     * @param x
     *            string expression
     * @param pattern
     *            string
     * @return like predicate
     */
    @Override
    public Predicate like(Expression<String> x, String pattern){
        List list = this.buildList(x, this.internalLiteral(pattern));
        return new CompoundExpressionImpl(this.metamodel, ((InternalSelection)x).getCurrentNode().like(pattern), list, "like");
    }

    /**
     * Create a predicate for testing whether the expression satisfies the given
     * pattern.
     *
     * @param x
     *            string expression
     * @param pattern
     *            string
     * @param escapeChar
     *            escape character expression
     * @return like predicate
     */
    @Override
    public Predicate like(Expression<String> x, String pattern, Expression<Character> escapeChar){
        return this.like(x, this.internalLiteral(pattern), escapeChar);
    }

    /**
     * Create a predicate for testing whether the expression satisfies the given
     * pattern.
     *
     * @param x
     *            string expression
     * @param pattern
     *            string
     * @param escapeChar
     *            escape character
     * @return like predicate
     */
    @Override
    public Predicate like(Expression<String> x, String pattern, char escapeChar){
        List list = this.buildList(x, this.internalLiteral(pattern), this.internalLiteral(escapeChar));
        String escapeString = String.valueOf(escapeChar);

        return new CompoundExpressionImpl(this.metamodel,
            ((InternalSelection)x).getCurrentNode().like(pattern, escapeString), list, "like");
    }

    /**
     * Create a predicate for testing whether the expression does not satisfy
     * the given pattern.
     *
     * @param x
     *            string expression
     * @param pattern
     *            string expression
     * @return like predicate
     */
    @Override
    public Predicate notLike(Expression<String> x, Expression<String> pattern){
        List list = this.buildList(x, pattern);

        return new CompoundExpressionImpl(this.metamodel,
            ((InternalSelection)x).getCurrentNode().notLike(((InternalSelection)pattern).getCurrentNode()), list, "notLike");
    }

    /**
     * Create a predicate for testing whether the expression does not satisfy
     * the given pattern.
     *
     * @param x
     *            string expression
     * @param pattern
     *            string expression
     * @param escapeChar
     *            escape character expression
     * @return like predicate
     */
    @Override
    public Predicate notLike(Expression<String> x, Expression<String> pattern, Expression<Character> escapeChar){
        List list = this.buildList(x, pattern, escapeChar);

        return new CompoundExpressionImpl(this.metamodel,
            ((InternalSelection)x).getCurrentNode().notLike(((InternalSelection)pattern).getCurrentNode(),
            ((InternalSelection)escapeChar).getCurrentNode()), list, "like");
    }

    /**
     * Create a predicate for testing whether the expression does not satisfy
     * the given pattern.
     *
     * @param x
     *            string expression
     * @param pattern
     *            string expression
     * @param escapeChar
     *            escape character
     * @return like predicate
     */
    @Override
    public Predicate notLike(Expression<String> x, Expression<String> pattern, char escapeChar){
        return this.notLike(x, pattern, this.internalLiteral(escapeChar));
    }

    /**
     * Create a predicate for testing whether the expression does not satisfy
     * the given pattern.
     *
     * @param x
     *            string expression
     * @param pattern
     *            string
     * @return like predicate
     */
    @Override
    public Predicate notLike(Expression<String> x, String pattern){
        List list = new ArrayList();
        list.add(x);
        list.add(this.internalLiteral(pattern));

        return new CompoundExpressionImpl(this.metamodel,
            ((InternalSelection)x).getCurrentNode().notLike(pattern), list, "notLike");
    }

    /**
     * Create a predicate for testing whether the expression does not satisfy
     * the given pattern.
     *
     * @param x
     *            string expression
     * @param pattern
     *            string
     * @param escapeChar
     *            escape character expression
     * @return like predicate
     */
    @Override
    public Predicate notLike(Expression<String> x, String pattern, Expression<Character> escapeChar){
        return this.notLike(x, this.internalLiteral(pattern), escapeChar);
    }

    /**
     * Create a predicate for testing whether the expression does not satisfy
     * the given pattern.
     *
     * @param x
     *            string expression
     * @param pattern
     *            string
     * @param escapeChar
     *            escape character
     * @return like predicate
     */
    @Override
    public Predicate notLike(Expression<String> x, String pattern, char escapeChar){
        return this.notLike(x, this.internalLiteral(pattern), this.internalLiteral(escapeChar));
    }

    /**
     * String concatenation operation.
     *
     * @param x
     *            string expression
     * @param y
     *            string expression
     * @return expression corresponding to concatenation
     */
    @Override
    public Expression<String> concat(Expression<String> x, Expression<String> y){
        List list = new ArrayList();
        list.add(x);
        list.add(y);
        org.eclipse.persistence.expressions.Expression xNode = ((InternalSelection)x).getCurrentNode();
        org.eclipse.persistence.expressions.Expression yNode = ((InternalSelection)y).getCurrentNode();

        if (xNode.isParameterExpression() && yNode.isParameterExpression()) {
            //some database require the type when concatting two parameters.
            ((org.eclipse.persistence.internal.expressions.ParameterExpression)xNode).setType(ClassConstants.STRING);
    }
        return new FunctionExpressionImpl(this.metamodel, ClassConstants.STRING, xNode.concat(yNode), list, CONCAT);
    }

    /**
     * String concatenation operation.
     *
     * @param x
     *            string expression
     * @param y
     *            string
     * @return expression corresponding to concatenation
     */
    @Override
    public Expression<String> concat(Expression<String> x, String y){
        List list = new ArrayList();
        list.add(x);
        list.add(this.internalLiteral(y));
        return new FunctionExpressionImpl(this.metamodel, ClassConstants.STRING, ((InternalSelection)x).getCurrentNode().concat(y), list, CONCAT);

    }

    /**
     * String concatenation operation.
     *
     * @param x
     *            string
     * @param y
     *            string expression
     * @return expression corresponding to concatenation
     */
    @Override
    public Expression<String> concat(String x, Expression<String> y){
        List list = new ArrayList();
        ExpressionImpl literal = (ExpressionImpl) this.internalLiteral(x);
        list.add(literal);
        list.add(y);
        return new FunctionExpressionImpl(this.metamodel, ClassConstants.STRING, ((InternalSelection)literal).getCurrentNode().concat(((InternalSelection)y).getCurrentNode()), list, CONCAT);
    }

    /**
     * Substring extraction operation. Extracts a substring starting at
     * specified position through to end of the string. First position is 1.
     *
     * @param x
     *            string expression
     * @param from
     *            start position expression
     * @return expression corresponding to substring extraction
     */
    @Override
    public Expression<String> substring(Expression<String> x, Expression<Integer> from) {
        return new FunctionExpressionImpl<String>(metamodel, ClassConstants.STRING, ((InternalSelection) x).getCurrentNode().substring(((InternalSelection) from).getCurrentNode()), buildList(x, from), "subString");
    }

    /**
     * Substring extraction operation. Extracts a substring starting at
     * specified position through to end of the string. First position is 1.
     *
     * @param x
     *            string expression
     * @param from
     *            start position
     * @return expression corresponding to substring extraction
     */
    @Override
    public Expression<String> substring(Expression<String> x, int from){
        return substring(x, this.internalLiteral(from));

    }

    /**
     * Substring extraction operation. Extracts a substring of given length
     * starting at specified position. First position is 1.
     *
     * @param x
     *            string expression
     * @param from
     *            start position expression
     * @param len
     *            length expression
     * @return expression corresponding to substring extraction
     */
    @Override
    public Expression<String> substring(Expression<String> x, Expression<Integer> from, Expression<Integer> len){
        return new FunctionExpressionImpl<String>(metamodel, ClassConstants.STRING, ((InternalSelection) x).getCurrentNode().substring(((InternalSelection) from).getCurrentNode(), ((InternalSelection) len).getCurrentNode()), buildList(x, from, len), "subString");
    }

    /**
     * Substring extraction operation. Extracts a substring of given length
     * starting at specified position. First position is 1.
     *
     * @param x
     *            string expression
     * @param from
     *            start position
     * @param len
     *            length
     * @return expression corresponding to substring extraction
     */
    @Override
    public Expression<String> substring(Expression<String> x, int from, int len){
        return new FunctionExpressionImpl<String>(metamodel, ClassConstants.STRING, ((InternalSelection) x).getCurrentNode().substring(from, len), buildList(x, internalLiteral(from), internalLiteral(len)), "subString");
    }

    /**
     * Create expression to trim blanks from both ends of a string.
     *
     * @param x
     *            expression for string to trim
     * @return trim expression
     */
    @Override
    public Expression<String> trim(Expression<String> x){
        List list = this.buildList(x);
        return new FunctionExpressionImpl(this.metamodel, ClassConstants.STRING, ((InternalSelection)x).getCurrentNode().trim(), list, "trim");
    }

    /**
     * Create expression to trim blanks from a string.
     *
     * @param ts
     *            trim specification
     * @param x
     *            expression for string to trim
     * @return trim expression
     */
    @Override
    public Expression<String> trim(Trimspec ts, Expression<String> x){
        List list = this.buildList(x);

        if(ts == Trimspec.LEADING) {
            return new FunctionExpressionImpl(this.metamodel, ClassConstants.STRING, ((InternalSelection)x).getCurrentNode().leftTrim(), list, "leftTrim");
        } else if(ts == Trimspec.TRAILING) {
            return new FunctionExpressionImpl(this.metamodel, ClassConstants.STRING, ((InternalSelection)x).getCurrentNode().rightTrim(), list, "rightTrim");
    }
        return new FunctionExpressionImpl(this.metamodel, ClassConstants.STRING, ((InternalSelection)x).getCurrentNode().rightTrim().leftTrim(), list, "bothTrim");

    }

    /**
     * Create expression to trim character from both ends of a string.
     *
     * @param t
     *            expression for character to be trimmed
     * @param x
     *            expression for string to trim
     * @return trim expression
     */
    @Override
    public Expression<String> trim(Expression<Character> t, Expression<String> x){
        List list = this.buildList(x, t);
        return new FunctionExpressionImpl(this.metamodel, ClassConstants.STRING, ((InternalSelection)x).getCurrentNode().trim(((InternalSelection)t).getCurrentNode()), list, "trim");
    }

    /**
     * Create expression to trim character from a string.
     *
     * @param ts
     *            trim specification
     * @param t
     *            expression for character to be trimmed
     * @param x
     *            expression for string to trim
     * @return trim expression
     */
    @Override
    public Expression<String> trim(Trimspec ts, Expression<Character> t, Expression<String> x){
        List list = this.buildList(x, t);

        if(ts == Trimspec.LEADING) {
            return new FunctionExpressionImpl(this.metamodel, ClassConstants.STRING,
                ((InternalSelection)x).getCurrentNode().leftTrim(((InternalSelection)t).getCurrentNode()), list, "leftTrim");
        } else if(ts == Trimspec.TRAILING) {
            return new FunctionExpressionImpl(this.metamodel, ClassConstants.STRING,
                ((InternalSelection)x).getCurrentNode().rightTrim(((InternalSelection)t).getCurrentNode()), list, "rightTrim");
    }
        return new FunctionExpressionImpl(this.metamodel, ClassConstants.STRING,
            ((InternalSelection)x).getCurrentNode().rightTrim(((InternalSelection)t).getCurrentNode()).leftTrim(((InternalSelection)t).getCurrentNode()), list, "bothTrim");
    }

    /**
     * Create expression to trim character from both ends of a string.
     *
     * @param t
     *            character to be trimmed
     * @param x
     *            expression for string to trim
     * @return trim expression
     */
    @Override
    public Expression<String> trim(char t, Expression<String> x){
        return trim(this.internalLiteral(t), x);
    }

    /**
     * Create expression to trim character from a string.
     *
     * @param ts
     *            trim specification
     * @param t
     *            character to be trimmed
     * @param x
     *            expression for string to trim
     * @return trim expression
     */
    @Override
    public Expression<String> trim(Trimspec ts, char t, Expression<String> x){
        return trim(ts, this.internalLiteral(t), x);
    }

    /**
     * Create expression for converting a string to lowercase.
     *
     * @param x
     *            string expression
     * @return expression to convert to lowercase
     */
    @Override
    public Expression<String> lower(Expression<String> x){
        List list = this.buildList(x);
        return new FunctionExpressionImpl(this.metamodel, ClassConstants.STRING, ((InternalSelection)x).getCurrentNode().toLowerCase(), list, "lower");
    }

    /**
     * Create expression for converting a string to uppercase.
     *
     * @param x
     *            string expression
     * @return expression to convert to uppercase
     */
    @Override
    public Expression<String> upper(Expression<String> x){
        List list = this.buildList(x);
        return new FunctionExpressionImpl(this.metamodel, ClassConstants.STRING, ((InternalSelection)x).getCurrentNode().toUpperCase(), list, "upper");
    }

    /**
     * Create expression to return length of a string.
     *
     * @param x
     *            string expression
     * @return length expression
     */
    @Override
    public Expression<Integer> length(Expression<String> x){
        return new FunctionExpressionImpl(metamodel, ClassConstants.INTEGER, ((InternalSelection)x).getCurrentNode().length(), buildList(x), "length");
    }

    /**
     * Create expression to locate the position of one string within another,
     * returning position of first character if found. The first position in a
     * string is denoted by 1. If the string to be located is not found, 0 is
     * returned.
     *
     * @param x
     *            expression for string to be searched
     * @param pattern
     *            expression for string to be located
     * @return expression corresponding to position
     */
    @Override
    public Expression<Integer> locate(Expression<String> x, Expression<String> pattern){
        return new FunctionExpressionImpl<Integer>(metamodel, ClassConstants.INTEGER, ((InternalSelection)x).getCurrentNode().locate(((InternalSelection)pattern).getCurrentNode()), buildList(x, pattern),"locate");
    }

    /**
     * Create expression to locate the position of one string within another,
     * returning position of first character if found. The first position in a
     * string is denoted by 1. If the string to be located is not found, 0 is
     * returned.
     *
     * @param x
     *            expression for string to be searched
     * @param pattern
     *            expression for string to be located
     * @param from
     *            expression for position at which to start search
     * @return expression corresponding to position
     */
    @Override
    public Expression<Integer> locate(Expression<String> x, Expression<String> pattern, Expression<Integer> from){
        return new FunctionExpressionImpl<Integer>(metamodel, ClassConstants.INTEGER, ((InternalSelection)x).getCurrentNode().locate(((InternalSelection)pattern).getCurrentNode(), ((InternalSelection)from).getCurrentNode()), buildList(x, pattern, from),"locate");
    }

    /**
     * Create expression to locate the position of one string within another,
     * returning position of first character if found. The first position in a
     * string is denoted by 1. If the string to be located is not found, 0 is
     * returned.
     *
     * @param x
     *            expression for string to be searched
     * @param pattern
     *            string to be located
     * @return expression corresponding to position
     */
    @Override
    public Expression<Integer> locate(Expression<String> x, String pattern){
        return new FunctionExpressionImpl<Integer>(metamodel, ClassConstants.INTEGER, ((InternalSelection)x).getCurrentNode().locate(pattern), buildList(x, internalLiteral(pattern)),"locate");
    }

    /**
     * Create expression to locate the position of one string within another,
     * returning position of first character if found. The first position in a
     * string is denoted by 1. If the string to be located is not found, 0 is
     * returned.
     *
     * @param x
     *            expression for string to be searched
     * @param pattern
     *            string to be located
     * @param from
     *            position at which to start search
     * @return expression corresponding to position
     */
    @Override
    public Expression<Integer> locate(Expression<String> x, String pattern, int from){
        return new FunctionExpressionImpl<Integer>(metamodel, ClassConstants.INTEGER, ((InternalSelection)x).getCurrentNode().locate(pattern, from), buildList(x, internalLiteral(pattern), internalLiteral(from)),"locate");
    }

    // Date/time/timestamp functions:
    /**
     * Create expression to return current date.
     *
     * @return expression for current date
     */
    @Override
    public Expression<java.sql.Date> currentDate(){
        return new ExpressionImpl(metamodel, ClassConstants.SQLDATE, new ExpressionBuilder().currentDateDate());
    }

    /**
     * Create expression to return current timestamp.
     *
     * @return expression for current timestamp
     */
    @Override
    public Expression<java.sql.Timestamp> currentTimestamp(){
        return new ExpressionImpl(metamodel, ClassConstants.TIMESTAMP, new ExpressionBuilder().currentTimeStamp());
    }

    /**
     * Create expression to return current time.
     *
     * @return expression for current time
     */
    @Override
    public Expression<java.sql.Time> currentTime(){
        return new ExpressionImpl(metamodel, ClassConstants.TIME, new ExpressionBuilder().currentTime());
    }

    /**
     * Create predicate to test whether given expression is contained in a list
     * of values.
     *
     * @param expression
     *            to be tested against list of values
     * @return in predicate
     */
    @Override
    public <T> In<T> in(Expression<? extends T> expression){
        List list = new ArrayList();
        list.add(expression);
        return new InImpl(metamodel, (ExpressionImpl) expression, new ArrayList(), list);
    }

    // coalesce, nullif:
    /**
     * Create an expression that returns null if all its arguments evaluate to
     * null, and the value of the first non-null argument otherwise.
     *
     * @param x
     *            expression
     * @param y
     *            expression
     * @return expression corresponding to the given coalesce expression
     */
    @Override
    public <Y> Expression<Y> coalesce(Expression<? extends Y> x, Expression<? extends Y> y){
        ArgumentListFunctionExpression coalesce = ((InternalSelection)x).getCurrentNode().coalesce();
        coalesce.addChild(((InternalSelection)x).getCurrentNode());
        coalesce.addChild(((InternalSelection)y).getCurrentNode());
        return new CoalesceImpl(metamodel, x.getJavaType(), coalesce,  buildList(x, y), "coalesce");
    }

    /**
     * Create an expression that returns null if all its arguments evaluate to
     * null, and the value of the first non-null argument otherwise.
     *
     * @param x
     *            expression
     * @param y
     *            value
     * @return coalesce expression
     */
    @Override
    public <Y> Expression<Y> coalesce(Expression<? extends Y> x, Y y){
        ArgumentListFunctionExpression coalesce = ((InternalSelection)x).getCurrentNode().coalesce();
        coalesce.addChild(((InternalSelection)x).getCurrentNode());
        coalesce.addChild(((InternalSelection)y).getCurrentNode());
        return new CoalesceImpl(metamodel, x.getJavaType(), coalesce, buildList(x, internalLiteral(y)), "coalesce");
    }

    /**
     * Create an expression that tests whether its argument are equal, returning
     * null if they are and the value of the first expression if they are not.
     *
     * @param x
     *            expression
     * @param y
     *            expression
     * @return expression corresponding to the given nullif expression
     */
    @Override
    public <Y> Expression<Y> nullif(Expression<Y> x, Expression<?> y){
        return new FunctionExpressionImpl(metamodel, x.getJavaType(), ((InternalSelection)x).getCurrentNode().nullIf(((InternalSelection)y).getCurrentNode()), buildList(x, y), "nullIf");
    }

    /**
     * Create an expression that tests whether its argument are equal, returning
     * null if they are and the value of the first expression if they are not.
     *
     * @param x
     *            expression
     * @param y
     *            value
     * @return expression corresponding to the given nullif expression
     */
    @Override
    public <Y> Expression<Y> nullif(Expression<Y> x, Y y){
        return new FunctionExpressionImpl(metamodel, x.getJavaType(), ((InternalSelection)x).getCurrentNode().nullIf(y), buildList(x, internalLiteral(y)), "nullIf");
    }

    /**
     * Create a coalesce expression.
     *
     * @return coalesce expression
     */
    @Override
    public <T> Coalesce<T> coalesce(){
        ArgumentListFunctionExpression coalesce = new ExpressionBuilder().coalesce();
        return new CoalesceImpl(metamodel, Object.class, coalesce, new ArrayList());
    }

    /**
     * Create simple case expression.
     *
     * @param expression
     *            to be tested against the case conditions
     * @return simple case expression
     */
    @Override
    public <C, R> SimpleCase<C, R> selectCase(Expression<? extends C> expression){
        ArgumentListFunctionExpression caseStatement = new ExpressionBuilder().caseStatement();
        return new SimpleCaseImpl(metamodel, Object.class, caseStatement, new ArrayList(), expression);
    }

    /**
     * Create a general case expression.
     *
     * @return general case expression
     */
    @Override
    public <R> Case<R> selectCase(){
        ArgumentListFunctionExpression caseStatement = new ExpressionBuilder().caseConditionStatement();
        return new CaseImpl(metamodel, Object.class, caseStatement, new ArrayList());
    }

    /**
     * Create an expression for execution of a database function.
     *
     * @param name
     *            function name
     * @param type
     *            expected result type
     * @param args
     *            function arguments
     * @return expression
     */
    @Override
    public <T> Expression<T> function(String name, Class<T> type, Expression<?>... args){
        if (args != null && args.length > 0){
        List<org.eclipse.persistence.expressions.Expression> params = new ArrayList<org.eclipse.persistence.expressions.Expression>();
        for (int index = 1; index < args.length; ++index){
            Expression x = args[index];
            params.add(((InternalSelection)x).getCurrentNode());
        }

        return new FunctionExpressionImpl<T>(metamodel, type, ((InternalSelection)args[0]).getCurrentNode().getFunctionWithArguments(name, params), buildList(args), name);
        }else{
            return new FunctionExpressionImpl<T>(metamodel, type, new ExpressionBuilder().getFunction(name), new ArrayList(0), name);
        }
    }

    /**
     * ADVANCED:
     * Allow a Criteria Expression to be built from a EclipseLink native API Expression object.
     * This allows for an extended functionality supported in EclipseLink Expressions to be used in Criteria.
     */
    @Override
    public <T> Expression<T> fromExpression(org.eclipse.persistence.expressions.Expression expression, Class<T> type) {
        return new FunctionExpressionImpl<T>(this.metamodel, type, expression, new ArrayList(0));
    }

    /**
     * ADVANCED:
     * Allow a Criteria Expression to be built from a EclipseLink native API Expression object.
     * This allows for an extended functionality supported in EclipseLink Expressions to be used in Criteria.
     */
    @Override
    public Expression fromExpression(org.eclipse.persistence.expressions.Expression expression) {
        return new FunctionExpressionImpl(this.metamodel, Object.class, expression, new ArrayList(0));
    }

    /**
     * ADVANCED:
     * Allow a Criteria Expression to be converted to a EclipseLink native API Expression object.
     * This allows for roots and paths defined in the Criteria to be used with EclipseLink native API Expresions.
     */
    @Override
    public org.eclipse.persistence.expressions.Expression toExpression(Expression expression) {
        return ((SelectionImpl)expression).getCurrentNode();
    }

    /**
     *  Interface used to build coalesce expressions.
     *
     * A coalesce expression is equivalent to a case expression
     * that returns null if all its arguments evaluate to null,
     * and the value of its first non-null argument otherwise.
     */
    public static class CoalesceImpl<X> extends FunctionExpressionImpl<X> implements Coalesce<X>{

        protected <T> CoalesceImpl (Metamodel metamodel, Class<X> resultClass, org.eclipse.persistence.expressions.Expression expressionNode, List<Expression<?>> compoundExpressions){
            super(metamodel, resultClass, expressionNode, compoundExpressions);
        }

        protected <T> CoalesceImpl (Metamodel metamodel, Class<X> resultClass, org.eclipse.persistence.expressions.Expression expressionNode, List<Expression<?>> compoundExpressions, String operator){
            super(metamodel, resultClass, expressionNode, compoundExpressions, operator);
        }

         /**
          * Add an argument to the coalesce expression.
          * @param value  value
          * @return coalesce expression
          */
         @Override
        public Coalesce<X> value(X value){
             org.eclipse.persistence.expressions.Expression exp = org.eclipse.persistence.expressions.Expression.from(value, new ExpressionBuilder());
             ((FunctionExpression)currentNode).addChild(exp);
             return this;
         }

         /**
          * Add an argument to the coalesce expression.
          * @param value expression
          * @return coalesce expression
          */
         @Override
        public Coalesce<X> value(Expression<? extends X> value){
             org.eclipse.persistence.expressions.Expression exp = ((InternalSelection)value).getCurrentNode();
             exp = org.eclipse.persistence.expressions.Expression.from(exp, currentNode);
             ((FunctionExpression)currentNode).addChild(exp);
             return this;
         }
    }

    /**
     * Implementation of Case interface from Criteria Builder
     * @author tware
     *
     * @param <R>
     */
    public static class CaseImpl<R> extends FunctionExpressionImpl<R> implements Case<R>{

        protected <T> CaseImpl (Metamodel metamodel, Class<R> resultClass, org.eclipse.persistence.expressions.Expression expressionNode, List<Expression<?>> compoundExpressions){
            super(metamodel, resultClass, expressionNode, compoundExpressions);
        }

        protected <T> CaseImpl (Metamodel metamodel, Class<R> resultClass, org.eclipse.persistence.expressions.Expression expressionNode, List<Expression<?>> compoundExpressions, String operator){
            super(metamodel, resultClass, expressionNode, compoundExpressions, operator);
        }

        /**
         * Add a when/then clause to the case expression.
         * @param condition  "when" condition
         * @param result  "then" result value
         * @return general case expression
         */
        @Override
        public Case<R> when(Expression<Boolean> condition, R result){
            org.eclipse.persistence.expressions.Expression conditionExp = ((InternalSelection)condition).getCurrentNode();
            conditionExp = org.eclipse.persistence.expressions.Expression.from(conditionExp, currentNode);
            ((FunctionExpression)currentNode).addChild(conditionExp);
            org.eclipse.persistence.expressions.Expression resultExp = org.eclipse.persistence.expressions.Expression.from(result, new ExpressionBuilder());
            ((FunctionExpression)currentNode).addChild(resultExp);
            return this;
        }

        /**
         * Add a when/then clause to the case expression.
         * @param condition  "when" condition
         * @param result  "then" result expression
         * @return general case expression
         */
        @Override
        public Case<R> when(Expression<Boolean> condition, Expression<? extends R> result){
            org.eclipse.persistence.expressions.Expression conditionExp = ((InternalSelection)condition).getCurrentNode();
            conditionExp = org.eclipse.persistence.expressions.Expression.from(conditionExp, currentNode);
            ((FunctionExpression)currentNode).addChild(conditionExp);
            org.eclipse.persistence.expressions.Expression resultExp = ((InternalSelection)result).getCurrentNode();
            resultExp = org.eclipse.persistence.expressions.Expression.from(resultExp, currentNode);
            ((FunctionExpression)currentNode).addChild(resultExp);
            return this;
        }

        /**
         * Add an "else" clause to the case expression.
         * @param result  "else" result
         * @return expression
         */
        @Override
        public Expression<R> otherwise(R result){
              org.eclipse.persistence.expressions.Expression resultExp = org.eclipse.persistence.expressions.Expression.from(result, new ExpressionBuilder());
            ((ArgumentListFunctionExpression)currentNode).addRightMostChild(resultExp);
            return this;
        }

        /**
         * Add an "else" clause to the case expression.
         * @param result  "else" result expression
         * @return expression
         */
        @Override
        public Expression<R> otherwise(Expression<? extends R> result){
            org.eclipse.persistence.expressions.Expression resultExp = ((InternalSelection)result).getCurrentNode();
            resultExp = org.eclipse.persistence.expressions.Expression.from(resultExp, currentNode);
            ((ArgumentListFunctionExpression)currentNode).addRightMostChild(resultExp);
            return this;
        }
    }

    /**
     * Implementation of SimpleCase interface from CriteriaBuilder
     * @author tware
     *
     * @param <C>
     * @param <R>
     */
    public static class SimpleCaseImpl<C,R> extends FunctionExpressionImpl<R> implements SimpleCase<C, R>{

        private Expression<C> expression;

        protected <T> SimpleCaseImpl (Metamodel metamodel, Class<R> resultClass, FunctionExpression expressionNode, List<Expression<?>> compoundExpressions, Expression<C> expression){
            super(metamodel, resultClass, expressionNode, compoundExpressions);
            this.expression = expression;
            expressionNode.addChild(((InternalSelection)expression).getCurrentNode());
        }

        protected <T> SimpleCaseImpl (Metamodel metamodel, Class<R> resultClass, FunctionExpression expressionNode, List<Expression<?>> compoundExpressions, String operator, Expression<C> expression){
            super(metamodel, resultClass, expressionNode, compoundExpressions, operator);
            this.expression = expression;
            expressionNode.addChild(((InternalSelection)expression).getCurrentNode());
        }

        /**
         * Returns the expression to be tested against the
         * conditions.
         * @return expression
         */
        @Override
        public Expression<C> getExpression(){
            return expression;
        }

        /**
         * Add a when/then clause to the case expression.
         * @param condition  "when" condition
         * @param result  "then" result value
         * @return simple case expression
         */
        @Override
        public SimpleCase<C, R> when(C condition, R result){
            org.eclipse.persistence.expressions.Expression conditionExp = org.eclipse.persistence.expressions.Expression.from(condition, new ExpressionBuilder());
            ((FunctionExpression)currentNode).addChild(conditionExp);
            org.eclipse.persistence.expressions.Expression resultExp = org.eclipse.persistence.expressions.Expression.from(result, new ExpressionBuilder());
            ((FunctionExpression)currentNode).addChild(resultExp);
            return this;
        }

        /**
         * Add a when/then clause to the case expression.
         * @param condition  "when" condition
         * @param result  "then" result expression
         * @return simple case expression
         */
        @Override
        public SimpleCase<C, R> when(C condition, Expression<? extends R> result){
            org.eclipse.persistence.expressions.Expression conditionExp = org.eclipse.persistence.expressions.Expression.from(condition, new ExpressionBuilder());
            ((FunctionExpression)currentNode).addChild(conditionExp);
            org.eclipse.persistence.expressions.Expression resultExp = ((InternalSelection)result).getCurrentNode();
            resultExp = org.eclipse.persistence.expressions.Expression.from(resultExp, currentNode);
            ((FunctionExpression)currentNode).addChild(resultExp);
            return this;
        }

        /**
         * Add an "else" clause to the case expression.
         * @param result  "else" result
         * @return expression
         */
        @Override
        public Expression<R> otherwise(R result){
            org.eclipse.persistence.expressions.Expression resultExp = org.eclipse.persistence.expressions.Expression.from(result, new ExpressionBuilder());
            ((ArgumentListFunctionExpression)currentNode).addRightMostChild(resultExp);
            return this;
        }

        /**
         * Add an "else" clause to the case expression.
         * @param result  "else" result expression
         * @return expression
         */
        @Override
        public Expression<R> otherwise(Expression<? extends R> result){

            org.eclipse.persistence.expressions.Expression resultExp = ((InternalSelection)result).getCurrentNode();
            resultExp = org.eclipse.persistence.expressions.Expression.from(resultExp, currentNode);
            ((ArgumentListFunctionExpression)currentNode).addRightMostChild(resultExp);
            return this;
        }
    }

    @Override
    public <T> CriteriaDelete<T> createCriteriaDelete(Class<T> targetEntity) {
        if (targetEntity != null) {
            TypeImpl type = ((MetamodelImpl)this.metamodel).getType(targetEntity);
            if (type != null && type.getPersistenceType().equals(PersistenceType.ENTITY)) {
                return new CriteriaDeleteImpl(this.metamodel, this, targetEntity);
            }
        }
        throw new IllegalArgumentException(ExceptionLocalization.buildMessage("unknown_bean_class", new Object[] { targetEntity }));
    }

    @Override
    public <T> CriteriaUpdate<T> createCriteriaUpdate(Class<T> targetEntity) {
        if (targetEntity != null) {
            TypeImpl type = ((MetamodelImpl)this.metamodel).getType(targetEntity);
            if (type != null && type.getPersistenceType().equals(PersistenceType.ENTITY)) {
                return new CriteriaUpdateImpl(this.metamodel, this, targetEntity);
            }
        }
        throw new IllegalArgumentException(ExceptionLocalization.buildMessage("unknown_bean_class", new Object[] { targetEntity }));
    }

    @Override
    public <X, T, V extends T> Join<X, V> treat(Join<X, T> join, Class<V> type) {
        JoinImpl parentJoin = (JoinImpl)join;
        JoinImpl joinImpl = new JoinImpl<X, V>(parentJoin, this.metamodel.managedType(type), this.metamodel,
                type, parentJoin.currentNode.treat(type), parentJoin.getModel(), parentJoin.getJoinType());
        parentJoin.joins.add(joinImpl);
        joinImpl.isJoin = parentJoin.isJoin;
        parentJoin.isJoin = false;
        return joinImpl;
    }

    @Override
    public <X, T, E extends T> CollectionJoin<X, E> treat(CollectionJoin<X, T> join, Class<E> type) {
        CollectionJoinImpl parentJoin = (CollectionJoinImpl)join;
        CollectionJoin joinImpl = null;
        if (join instanceof BasicCollectionJoinImpl) {
            joinImpl = new BasicCollectionJoinImpl<X, E>(parentJoin, this.metamodel, type,
                    parentJoin.currentNode.treat(type), parentJoin.getModel(), parentJoin.getJoinType());
        } else {
            joinImpl = new CollectionJoinImpl<X, E>((Path)join, this.metamodel.managedType(type), this.metamodel,
                    type, parentJoin.currentNode.treat(type), parentJoin.getModel(), parentJoin.getJoinType());
        }
        parentJoin.joins.add(joinImpl);
        ((FromImpl)joinImpl).isJoin = parentJoin.isJoin;
        parentJoin.isJoin = false;
        return joinImpl;
    }

    @Override
    public <X, T, E extends T> SetJoin<X, E> treat(SetJoin<X, T> join, Class<E> type) {
        SetJoinImpl parentJoin = (SetJoinImpl)join;
        SetJoin joinImpl = null;
        if (join instanceof BasicSetJoinImpl) {
            joinImpl = new BasicSetJoinImpl<X, E>(parentJoin, this.metamodel, type,
                    parentJoin.currentNode.treat(type), parentJoin.getModel(), parentJoin.getJoinType());
        } else {
            joinImpl = new SetJoinImpl<X, E>((Path)join, this.metamodel.managedType(type), this.metamodel,
                    type, parentJoin.currentNode.treat(type), parentJoin.getModel(), parentJoin.getJoinType());
        }
        parentJoin.joins.add(joinImpl);
        ((FromImpl)joinImpl).isJoin = parentJoin.isJoin;
        parentJoin.isJoin = false;
        return joinImpl;
    }

    @Override
    public <X, T, E extends T> ListJoin<X, E> treat(ListJoin<X, T> join, Class<E> type) {
        ListJoinImpl parentJoin = (ListJoinImpl)join;
        ListJoin joinImpl = null;
        if (join instanceof BasicListJoinImpl) {
            joinImpl = new BasicListJoinImpl<X, E>(parentJoin, this.metamodel, type,
                    parentJoin.currentNode.treat(type), parentJoin.getModel(), parentJoin.getJoinType());
        } else {
            joinImpl = new ListJoinImpl<X, E>((Path)join, this.metamodel.managedType(type), this.metamodel,
                    type, parentJoin.currentNode.treat(type), parentJoin.getModel(), parentJoin.getJoinType());
        }
        parentJoin.joins.add(joinImpl);
        ((FromImpl)joinImpl).isJoin = parentJoin.isJoin;
        parentJoin.isJoin = false;
        return joinImpl;
    }

    @Override
    public <X, K, T, V extends T> MapJoin<X, K, V> treat(MapJoin<X, K, T> join, Class<V> type) {
        MapJoinImpl parentJoin = (MapJoinImpl)join;
        MapJoin joinImpl = null;
        if (join instanceof BasicMapJoinImpl) {
            joinImpl = new BasicMapJoinImpl<X, K, V>(parentJoin, this.metamodel, type,
                    parentJoin.currentNode.treat(type), parentJoin.getModel(), parentJoin.getJoinType());
        } else {
            joinImpl = new MapJoinImpl<X, K, V>((Path)join, this.metamodel.managedType(type), this.metamodel,
                    type, parentJoin.currentNode.treat(type), parentJoin.getModel(), parentJoin.getJoinType());
        }
        parentJoin.joins.add(joinImpl);
        ((FromImpl)joinImpl).isJoin = parentJoin.isJoin;
        parentJoin.isJoin = false;
        return joinImpl;
    }

    @Override
    public <X, T extends X> Path<T> treat(Path<X> path, Class<T> type) {
        //Handle all the paths that might get passed in.:
        PathImpl parentPath = (PathImpl)path;
        PathImpl newPath = (PathImpl)parentPath.clone();
        newPath.currentNode = newPath.currentNode.treat(type);
        newPath.pathParent = parentPath;
        newPath.javaType = type;
        newPath.modelArtifact = this.metamodel.managedType(type);
        return newPath;
    }

    @Override
    public <X, T extends X> Root<T> treat(Root<X> root, Class<T> type) {
        RootImpl parentRoot = (RootImpl)root;
        EntityType entity = this.metamodel.entity(type);
        return new RootImpl<T>(entity, this.metamodel, type, parentRoot.currentNode.treat(type), entity);
    }
}

