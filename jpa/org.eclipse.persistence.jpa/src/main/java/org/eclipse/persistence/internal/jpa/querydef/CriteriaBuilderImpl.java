/*
 * Copyright (c) 2009, 2025 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2020, 2024 IBM Corporation. All rights reserved.
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
//     02/01/2022: Tomas Kraus
//       - Issue 1442: Implement New Jakarta Persistence 3.1 Features
//     04/19/2022: Jody Grassel
//       - Issue 579726: CriteriaBuilder neg() only returns Integer type, instead of it's argument expression type.
//     08/22/2023: Tomas Kraus
//       - New Jakarta Persistence 3.2 Features
package org.eclipse.persistence.internal.jpa.querydef;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CollectionJoin;
import jakarta.persistence.criteria.CompoundSelection;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaSelect;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.ListJoin;
import jakarta.persistence.criteria.MapJoin;
import jakarta.persistence.criteria.Nulls;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.ParameterExpression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Predicate.BooleanOperator;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Selection;
import jakarta.persistence.criteria.SetJoin;
import jakarta.persistence.criteria.Subquery;
import jakarta.persistence.criteria.TemporalField;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.Metamodel;
import jakarta.persistence.metamodel.Type.PersistenceType;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.expressions.ExpressionMath;
import org.eclipse.persistence.expressions.ExpressionOperator;
import org.eclipse.persistence.internal.core.helper.CoreClassConstants;
import org.eclipse.persistence.internal.expressions.ArgumentListFunctionExpression;
import org.eclipse.persistence.internal.expressions.ConstantExpression;
import org.eclipse.persistence.internal.expressions.FunctionExpression;
import org.eclipse.persistence.internal.expressions.SubSelectExpression;
import org.eclipse.persistence.internal.helper.BasicTypeHelperImpl;
import org.eclipse.persistence.internal.helper.ClassConstants;
import org.eclipse.persistence.internal.jpa.metamodel.MetamodelImpl;
import org.eclipse.persistence.internal.jpa.metamodel.TypeImpl;
import org.eclipse.persistence.internal.jpa.querydef.AbstractQueryImpl.ResultType;
import org.eclipse.persistence.internal.jpa.querydef.CriteriaMultiSelectImpl.Union;
import org.eclipse.persistence.internal.localization.ExceptionLocalization;
import org.eclipse.persistence.jpa.JpaCriteriaBuilder;
import org.eclipse.persistence.queries.ReportQuery;

import static org.eclipse.persistence.internal.jpa.querydef.ExpressionImpl.currentNode;

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
        return new CriteriaQueryImpl<>(this.metamodel, ResultType.UNKNOWN, CoreClassConstants.OBJECT, this);
    }

    /**
     *  Create a Criteria query object.
     *  @return query object
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> CriteriaQuery<T> createQuery(Class<T> resultClass) {
        if (resultClass == null) {
            return (CriteriaQuery<T>) this.createQuery();
        }
        if (resultClass.equals(Tuple.class)) {
            return new CriteriaQueryImpl<>(this.metamodel, ResultType.TUPLE, resultClass, this);
        } else if (resultClass.equals(ClassConstants.AOBJECT)) {
            return new CriteriaQueryImpl<>(this.metamodel, ResultType.OBJECT_ARRAY, resultClass, this);
        } else if (resultClass.isArray()) {
            return new CriteriaQueryImpl<>(this.metamodel, ResultType.OBJECT_ARRAY, resultClass, this);
        } else {
            if (resultClass.equals(CoreClassConstants.OBJECT)) {
                return (CriteriaQuery<T>) this.createQuery();
            } else {
                if (resultClass.isPrimitive() || resultClass.equals(CoreClassConstants.STRING)|| BasicTypeHelperImpl.getInstance().isWrapperClass(resultClass) || BasicTypeHelperImpl.getInstance().isDateClass(resultClass)) {
                    return new CriteriaQueryImpl<>(metamodel, ResultType.OTHER, resultClass, this);
                } else {
                    TypeImpl<T> type = ((MetamodelImpl)this.metamodel).getType(resultClass);
                    if (type != null && type.getPersistenceType().equals(PersistenceType.ENTITY)) {
                        return new CriteriaQueryImpl<>(this.metamodel, ResultType.ENTITY, resultClass, this);
                    } else {
                        return new CriteriaQueryImpl<>(this.metamodel, ResultType.CONSTRUCTOR, resultClass, this);
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
        return new CriteriaQueryImpl<>(this.metamodel, ResultType.TUPLE, Tuple.class, this);
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
        return new ConstructorSelectionImpl<>(result, selections);
    }


    @Override
    public CompoundSelection<Tuple> tuple(Selection<?>... selections){
        return new CompoundSelectionImpl<>(Tuple.class, selections, true);
    }

    @Override
    public CompoundSelection<Tuple> tuple(List<Selection<?>> selections) {
        return tuple(selections != null
                             ? selections.toArray(new Selection<?>[0])
                             : null);
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
        return new CompoundSelectionImpl<>((Class<? extends Object[]>) ClassConstants.AOBJECT, selections, true);
    }

    @Override
    public CompoundSelection<Object[]> array(List<Selection<?>> selections) {
        return array(selections != null
                             ? selections.toArray(new Selection<?>[0])
                             : null);
    }

    @Override
    public Order asc(Expression<?> expression) {
        return asc(expression, Nulls.NONE);
    }

    // TODO-API-3.2 - Nulls added to OrderImpl and ObjectLevelReadQuery in CriteriaQueryImpl, but no tests exist
    @Override
    public Order asc(Expression<?> expression, Nulls nullPrecedence) {
        if (currentNode(expression) == null){
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("OPERATOR_EXPRESSION_IS_CONJUNCTION"));
        }
        return new OrderImpl(expression, true, nullPrecedence);
    }

    @Override
    public Order desc(Expression<?> expression){
        return desc(expression, Nulls.NONE);
    }

    // TODO-API-3.2 - Nulls added to OrderImpl and ObjectLevelReadQuery in CriteriaQueryImpl, but no tests exist
    @Override
    public Order desc(Expression<?> expression, Nulls nullPrecedence) {
        if (currentNode(expression) == null){
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("OPERATOR_EXPRESSION_IS_CONJUNCTION"));
        }
        return new OrderImpl(expression, false, nullPrecedence);
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
        return new FunctionExpressionImpl<>(this.metamodel, CoreClassConstants.DOUBLE, currentNode(x).average(), buildList(x), "AVG");
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
        return new FunctionExpressionImpl<>(this.metamodel, x.getJavaType(), currentNode(x).sum(), buildList(x), "SUM");
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
        return new FunctionExpressionImpl<>(this.metamodel, x.getJavaType(), currentNode(x).maximum(), buildList(x), "MAX");
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
        return new FunctionExpressionImpl<>(this.metamodel, x.getJavaType(), currentNode(x).minimum(), buildList(x), "MIN");
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
        if (currentNode(x) == null){
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("OPERATOR_EXPRESSION_IS_CONJUNCTION"));
        }
        return new ExpressionImpl<>(this.metamodel, x.getJavaType(), currentNode(x).maximum());
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
        if (currentNode(x) == null){
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("OPERATOR_EXPRESSION_IS_CONJUNCTION"));
        }
        return new ExpressionImpl<>(this.metamodel, x.getJavaType(),currentNode(x).minimum());
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
        if (currentNode(x) == null){
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("OPERATOR_EXPRESSION_IS_CONJUNCTION"));
        }
        return new FunctionExpressionImpl<>(this.metamodel, CoreClassConstants.LONG, currentNode(x).count(), buildList(x),"COUNT");
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
        if (currentNode(x) == null){
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("OPERATOR_EXPRESSION_IS_CONJUNCTION"));
        }
        return new FunctionExpressionImpl<>(this.metamodel, CoreClassConstants.LONG, currentNode(x).distinct().count(), buildList(x),"COUNT");
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
        return new CompoundExpressionImpl(
                metamodel,
                org.eclipse.persistence.expressions.Expression.getOperator(ExpressionOperator.Exists)
                        .expressionFor(((SubQueryImpl<?>)subquery).getCurrentNode()),
                buildList(subquery),
                "exists");
    }

    /**
     * Create a predicate corresponding to an all expression over the subquery
     * results.
     *
     * @return all expression
     */
    @Override
    public <Y> Expression<Y> all(Subquery<Y> subquery){
        return new FunctionExpressionImpl<>(metamodel, subquery.getJavaType(), new ExpressionBuilder().all(currentNode(subquery)), buildList(subquery), "all");
    }

    /**
     * Create a predicate corresponding to a some expression over the subquery
     * results. This is equivalent to an any expression.
     *
     * @return all expression
     */
    @Override
    public <Y> Expression<Y> some(Subquery<Y> subquery){
        return new FunctionExpressionImpl<>(metamodel, subquery.getJavaType(), new ExpressionBuilder().some(currentNode(subquery)), buildList(subquery), "some");
    }

    /**
     * Create a predicate corresponding to an any expression over the subquery
     * results. This is equivalent to a some expression.
     *
     * @return any expression
     */
    @Override
    public <Y> Expression<Y> any(Subquery<Y> subquery){
        return new FunctionExpressionImpl<>(metamodel, subquery.getJavaType(), new ExpressionBuilder().any(currentNode(subquery)), buildList(subquery), "any");
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
     * Create a conjunction of the given restriction predicates.
     * A conjunction of {@code null} or zero predicates is {@code true}.
     *
     * @param restrictions zero or more restriction predicates
     * @return and predicate
     */
    @Override
    public Predicate and(Predicate... restrictions) {
        return and(restrictions != null ?  List.of(restrictions) : null);
    }

    @Override
    public Predicate and(List<Predicate> restrictions) {
        // PERF: Build simple cases directly
        switch (restrictions != null ? restrictions.size() : 0) {
            case 0:
                return this.conjunction();
            case 1:
                return restrictions.get(0);
            case 2:
                return and(restrictions.get(0), restrictions.get(1));
            default:
                Predicate predicate = restrictions.get(0);
                for (int i = 1; i < restrictions.size(); i++) {
                    predicate = and(predicate, restrictions.get(i));
                }
                return predicate;
        }
    }

    /**
     * Create a disjunction of the given restriction predicates.
     * A disjunction of {@code null} or zero predicates is {@code false}.
     *
     * @param restrictions zero or more restriction predicates
     * @return and predicate
     */
    @Override
    public Predicate or(Predicate... restrictions) {
        return or(restrictions != null ?  List.of(restrictions) : null);
    }

    @Override
    public Predicate or(List<Predicate> restrictions) {
        // PERF: Build simple cases directly
        switch (restrictions != null ? restrictions.size() : 0) {
            case 0:
                return this.disjunction();
            case 1:
                return restrictions.get(0);
            case 2:
                return or(restrictions.get(0), restrictions.get(1));
            default:
                Predicate predicate = restrictions.get(0);
                for (int i = 1; i < restrictions.size(); i++) {
                    predicate = or(predicate, restrictions.get(i));
                }
                return predicate;
        }
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
            FunctionExpression exp = (FunctionExpression) currentNode(restriction);
            SubSelectExpression sub = (SubSelectExpression) exp.getChildren().get(0);
            parentNode = org.eclipse.persistence.expressions.Expression.getOperator(ExpressionOperator.NotExists).expressionFor(sub);
            name = "notExists";
            compoundExpressions = ((CompoundExpressionImpl)restriction).getChildExpressions();
        }else{
            parentNode = currentNode(restriction).not();
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
            if (currentNode(x) == null){
                return (Predicate)x;
            }else{
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage("PREDICATE_PASSED_TO_EVALUATION"));
            }
        }
        return new CompoundExpressionImpl(this.metamodel, currentNode(x).equal(true), buildList(x), "equals");
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
            if (currentNode(x) == null){
                if (((Predicate)x).getOperator() == BooleanOperator.AND){
                    return (Predicate)x;
                }else{
                    return this.conjunction();
                }
            }else{
                throw new IllegalArgumentException(ExceptionLocalization.buildMessage("PREDICATE_PASSED_TO_EVALUATION"));
            }
        }
        return new CompoundExpressionImpl(this.metamodel, currentNode(x).equal(false), buildList(x), "equals");
    }

    //null tests:
    /**
     * Create a predicate to test whether the expression is null.
     * @param x expression
     * @return predicate
     */
    @Override
    public Predicate isNull(Expression<?> x){
        return new PredicateImpl(this.metamodel, currentNode(x).isNull(), buildList(x), BooleanOperator.AND);
    }

    /**
     * Create a predicate to test whether the expression is not null.
     * @param x expression
     * @return predicate
     */
    @Override
    public Predicate isNotNull(Expression<?> x){
        return new PredicateImpl(this.metamodel, currentNode(x).notNull(), buildList(x), BooleanOperator.AND);
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
        return new CompoundExpressionImpl(this.metamodel, currentNode(x).equal(currentNode(y)), buildList(x, y), "equals");
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
        if (currentNode(x) == null || currentNode(y) == null){
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("OPERATOR_EXPRESSION_IS_CONJUNCTION"));
        }
        return new CompoundExpressionImpl(this.metamodel, currentNode(x).notEqual(currentNode(y)), buildList(x, y), "not equal");
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
        if (currentNode(x) == null){
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("OPERATOR_EXPRESSION_IS_CONJUNCTION"));
        }
        if (y instanceof ParameterExpression) {
            return this.equal(x, (ParameterExpression<?>) y);
        }
        return new CompoundExpressionImpl(this.metamodel, currentNode(x).equal(y), buildList(x, internalLiteral(y)), "equal");
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
        if (currentNode(x) == null){
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("OPERATOR_EXPRESSION_IS_CONJUNCTION"));
        }
        if (y instanceof ParameterExpression)
            return this.notEqual(x, (ParameterExpression<?>)y);

        return new CompoundExpressionImpl(this.metamodel, currentNode(x).notEqual(y), buildList(x, internalLiteral(y)), "not equal");
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
        if (currentNode(x) == null || currentNode(y) == null){
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("OPERATOR_EXPRESSION_IS_CONJUNCTION"));
        }
        return new CompoundExpressionImpl(this.metamodel, currentNode(x).greaterThan(currentNode(y)), buildList(x, y), "greaterThan");
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
        if (currentNode(x) == null || currentNode(y) == null){
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("OPERATOR_EXPRESSION_IS_CONJUNCTION"));
        }
        return new CompoundExpressionImpl(this.metamodel, currentNode(x).lessThan(currentNode(y)), buildList(x,y), "lessThan");
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
        if (currentNode(x) == null || currentNode(y) == null){
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("OPERATOR_EXPRESSION_IS_CONJUNCTION"));
        }
        return new CompoundExpressionImpl(this.metamodel, currentNode(x).greaterThanEqual(currentNode(y)), buildList(x, y), "greaterThanEqual");
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
        if (currentNode(x) == null || currentNode(y) == null){
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("OPERATOR_EXPRESSION_IS_CONJUNCTION"));
        }
        return new CompoundExpressionImpl(this.metamodel, currentNode(x).lessThanEqual(currentNode(y)), buildList(x, y), "lessThanEqual");
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
        return new CompoundExpressionImpl(this.metamodel, currentNode(v).between(currentNode(x), currentNode(y)), buildList(v, x, y), "between");
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
        if (currentNode(x) == null ){
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("OPERATOR_EXPRESSION_IS_CONJUNCTION"));
        }
        Expression<Y> expressionY = internalLiteral(y);
        return new CompoundExpressionImpl(this.metamodel, currentNode(x).greaterThan(currentNode(expressionY)), buildList(x, expressionY), "greaterThan");
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
        if (currentNode(x) == null ){
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("OPERATOR_EXPRESSION_IS_CONJUNCTION"));
        }
        Expression<Y> expressionY = internalLiteral(y);
        return new CompoundExpressionImpl(this.metamodel, currentNode(x).lessThan(currentNode(expressionY)), buildList(x, expressionY), "lessThan");
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
        if (currentNode(x) == null ){
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("OPERATOR_EXPRESSION_IS_CONJUNCTION"));
        }
        Expression<Y> expressionY = internalLiteral(y);
        return new CompoundExpressionImpl(this.metamodel, currentNode(x).greaterThanEqual(currentNode(expressionY)), buildList(x, expressionY), "greaterThanEqual");
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
        if (currentNode(x) == null ){
            throw new IllegalArgumentException(ExceptionLocalization.buildMessage("OPERATOR_EXPRESSION_IS_CONJUNCTION"));
        }
        Expression<Y> expressionY = internalLiteral(y);
        return new CompoundExpressionImpl(this.metamodel, currentNode(x).lessThanEqual(currentNode(expressionY)), buildList(x, expressionY), "lessThanEqual");
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
        return new CompoundExpressionImpl(this.metamodel, currentNode(v).between(x, y), buildList(v, internalLiteral(x), internalLiteral(y)), "between");
    }

    protected static List<Expression<?>> buildList(Expression<?>... expressions) {
        // Immutable List causes test failures.
        // Those lists are usually small (size 1-2) and modifications are rare. Default list size is too much.
        List<Expression<?>> list = new ArrayList<>(expressions.length + 2);
        Collections.addAll(list, expressions);
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
        return new CompoundExpressionImpl(this.metamodel, currentNode(x).greaterThan(currentNode(y)), buildList(x, y), "gt");
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
        return new CompoundExpressionImpl(this.metamodel, currentNode(x).lessThan(currentNode(y)), buildList(x,y), "lessThan");
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
        return new CompoundExpressionImpl(this.metamodel, currentNode(x).greaterThanEqual(currentNode(y)), buildList(x, y), "ge");
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
        return new CompoundExpressionImpl(this.metamodel, currentNode(x).lessThanEqual(currentNode(y)), buildList(x, y), "le");
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
        return new CompoundExpressionImpl(this.metamodel, currentNode(x).greaterThan(y), buildList(x, internalLiteral(y)), "gt");
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
        return new CompoundExpressionImpl(this.metamodel, currentNode(x).lessThan(y), buildList(x, internalLiteral(y)), "lt");
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
        return new CompoundExpressionImpl(this.metamodel, currentNode(x).greaterThanEqual(y), buildList(x, internalLiteral(y)), "ge");
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
        return new CompoundExpressionImpl(this.metamodel, currentNode(x).lessThanEqual(y), buildList(x, internalLiteral(y)), "le");
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
        return new FunctionExpressionImpl<>(this.metamodel, x.getJavaType(), ExpressionMath.negate(currentNode(x)), buildList(x), "neg");
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
        return new FunctionExpressionImpl<>(metamodel, x.getJavaType(), ExpressionMath.abs(currentNode(x)), buildList(x), "ABS");
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
        return new FunctionExpressionImpl<>(
                this.metamodel,
                BasicTypeHelperImpl.getInstance().extendedBinaryNumericPromotionClass(x.getJavaType(), y.getJavaType()),
                ExpressionMath.add(currentNode(x),currentNode(y)),
                buildList(x,y),
                "sum");
    }
    /**
     * Create an aggregate expression applying the sum operation to an
     * Integer-valued expression, returning a Long result.
     * @param x  expression representing input value to sum operation
     * @return sum expression
     */
    @Override
    public Expression<Long> sumAsLong(Expression<Integer> x) {
        return new FunctionExpressionImpl<>(this.metamodel, CoreClassConstants.LONG, currentNode(x).sum(), buildList(x),"SUM");
    }

    /**
     * Create an aggregate expression applying the sum operation to a
     * Float-valued expression, returning a Double result.
     * @param x  expression representing input value to sum operation
     * @return sum expression
     */
    @Override
    public Expression<Double> sumAsDouble(Expression<Float> x){
        return new FunctionExpressionImpl<>(this.metamodel, CoreClassConstants.DOUBLE, currentNode(x).sum(), buildList(x),"SUM");
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
        return new FunctionExpressionImpl<>(
                this.metamodel,
                BasicTypeHelperImpl.getInstance().extendedBinaryNumericPromotionClass(x.getJavaType(), y.getJavaType()),
                ExpressionMath.multiply(currentNode(x),currentNode(y)),
                buildList(x,y),
                "prod");
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
        return new FunctionExpressionImpl<>(this.metamodel, x.getJavaType(), ExpressionMath.subtract(currentNode(x), currentNode(y)), buildList(x, y), "diff");
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
        return new FunctionExpressionImpl<>(
                this.metamodel,
                BasicTypeHelperImpl.getInstance().extendedBinaryNumericPromotionClass(x.getJavaType(), y.getClass()),
                ExpressionMath.add(currentNode(x),y),
                buildList(x,internalLiteral(y)),
                "sum");
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
        return new FunctionExpressionImpl<>(
                this.metamodel,
                BasicTypeHelperImpl.getInstance().extendedBinaryNumericPromotionClass(x.getJavaType(), y.getClass()),
                ExpressionMath.multiply(currentNode(x),y),
                buildList(x,internalLiteral(y)),
                "prod");
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
    @SuppressWarnings("unchecked") // y.getClass() cast
    public <N extends Number> Expression<N> diff(Expression<? extends N> x, N y){
        return new FunctionExpressionImpl<>(
                this.metamodel,
                (Class<? extends N>) y.getClass(),
                ExpressionMath.subtract(currentNode(x), y),
                buildList(x, internalLiteral(y)),
                "diff");
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
        return new FunctionExpressionImpl<>(
                this.metamodel,
                BasicTypeHelperImpl.getInstance().extendedBinaryNumericPromotionClass(x.getClass(), y.getJavaType()),
                ExpressionMath.add(new ConstantExpression(x, currentNode(y)),currentNode(y)),
                buildList(internalLiteral(x),y),
                "sum");
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
        return new FunctionExpressionImpl<>(
                this.metamodel,
                BasicTypeHelperImpl.getInstance().extendedBinaryNumericPromotionClass(x.getClass(), y.getJavaType()),
                ExpressionMath.multiply(new ConstantExpression(x, currentNode(y)),currentNode(y)),
                buildList(internalLiteral(x),y),
                "prod");
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
        Expression<N> literal = internalLiteral(x);
        return new FunctionExpressionImpl<>(
                this.metamodel,
                literal.getJavaType(),
                ExpressionMath.subtract(currentNode(literal), currentNode(y)),
                buildList(literal, y), "diff");
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
        return new FunctionExpressionImpl<>(
                this.metamodel,
                BasicTypeHelperImpl.getInstance().extendedBinaryNumericPromotionClass(x.getJavaType(), y.getClass()),
                ExpressionMath.divide(currentNode(x),currentNode(y)),
                buildList(x,y),
                "quot");
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
        return new FunctionExpressionImpl<>(
                this.metamodel,
                BasicTypeHelperImpl.getInstance().extendedBinaryNumericPromotionClass(x.getJavaType(), y.getClass()),
                ExpressionMath.divide(currentNode(x),y),
                buildList(x,internalLiteral(y)),
                "quot");
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
        return new FunctionExpressionImpl<>(this.metamodel, CoreClassConstants.NUMBER, ExpressionMath.divide(new ConstantExpression(x, currentNode(y)),currentNode(y)), buildList(internalLiteral(x),y), "quot");
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
        return new FunctionExpressionImpl<>(this.metamodel, CoreClassConstants.INTEGER, ExpressionMath.mod(currentNode(x),currentNode(y)), buildList(x,y), "mod");
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
        return new FunctionExpressionImpl<>(this.metamodel, CoreClassConstants.INTEGER, ExpressionMath.mod(currentNode(x),y), buildList(x,internalLiteral(y)), "mod");
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
        Expression<Integer> xExp = internalLiteral(x);
        return new FunctionExpressionImpl<>(this.metamodel, CoreClassConstants.INTEGER, ExpressionMath.mod(currentNode(xExp),currentNode(y)), buildList(xExp,y), "mod");
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
        return new FunctionExpressionImpl<>(this.metamodel, CoreClassConstants.DOUBLE, ExpressionMath.sqrt(currentNode(x)), buildList(x), "sqrt");
    }

    /**
     * Create an expression that returns the sign of its argument, that is, {@code 1} if its argument is
     * positive, {@code -1} if its argument is negative, or {@code 0} if its argument is exactly zero.
     *
     * @param x expression
     * @return sign
     */
    @Override
    public Expression<Integer> sign(Expression<? extends Number> x) {
        return new FunctionExpressionImpl<>(this.metamodel, CoreClassConstants.INTEGER,
                ExpressionMath.sign(currentNode(x)), buildList(x), "sign");
    }

    /**
     * Create an expression that returns the ceiling of its argument, that is, the smallest integer greater than
     * or equal to its argument.
     *
     * @param x expression
     * @return ceiling
     */
    @Override
    public <N extends Number> Expression<N> ceiling(Expression<N> x) {
        return new FunctionExpressionImpl<>(this.metamodel, x.getJavaType(),
                ExpressionMath.ceil(currentNode(x)), buildList(x), "ceiling");
    }

    /**
     * Create an expression that returns the floor of its argument, that is, the largest integer smaller than
     * or equal to its argument.
     *
     * @param x expression
     * @return floor
     */
    @Override
    public <N extends Number> Expression<N> floor(Expression<N> x) {
        return new FunctionExpressionImpl<>(this.metamodel, x.getJavaType(),
                ExpressionMath.floor(currentNode(x)), buildList(x), "floor");
    }

    /**
     * Create an expression that returns the exponential of its argument, that is, Euler's number <i>e</i>
     * raised to the power of its argument.
     *
     * @param x expression
     * @return exponential
     */
    @Override
    public Expression<Double> exp(Expression<? extends Number> x) {
        return new FunctionExpressionImpl<>(this.metamodel, CoreClassConstants.DOUBLE,
                ExpressionMath.exp(currentNode(x)), buildList(x), "exp");
    }
    /**
     * Create an expression that returns the natural logarithm of its argument.
     *
     * @param x expression
     * @return natural logarithm
     */
    @Override
    public Expression<Double> ln(Expression<? extends Number> x) {
        return new FunctionExpressionImpl<>(this.metamodel, CoreClassConstants.DOUBLE,
                ExpressionMath.ln(currentNode(x)), buildList(x), "ln");
    }

    /**
     * Create an expression that returns the first argument raised to the power of its second argument.
     *
     * @param x base
     * @param y exponent
     * @return the base raised to the power of the exponent
     */
    @Override
    public Expression<Double> power(Expression<? extends Number> x, Expression<? extends Number> y) {
        return new FunctionExpressionImpl<>(this.metamodel, CoreClassConstants.DOUBLE,
                ExpressionMath.power(currentNode(x),
                currentNode(y)), buildList(x,y), "power");
    }

    /**
     * Create an expression that returns the first argument raised to the power of its second argument.
     *
     * @param x base
     * @param y exponent
     * @return the base raised to the power of the exponent
     */
    @Override
    public Expression<Double> power(Expression<? extends Number> x, Number y) {
        return new FunctionExpressionImpl<>(this.metamodel, CoreClassConstants.DOUBLE,
                ExpressionMath.power(currentNode(x), y),
                buildList(x, internalLiteral(y)), "power");
    }

    /**
     * Create an expression that returns the first argument rounded to the number of decimal places given by the
     * second argument.
     *
     * @param x base
     * @param n number of decimal places
     * @return the rounded value
     */
    @Override
    public <T extends Number> Expression<T> round(Expression<T> x, Integer n) {
        return new FunctionExpressionImpl<>(this.metamodel, x.getJavaType(),
                ExpressionMath.round(currentNode(x), n),
                buildList(x, internalLiteral(n)), "round");
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
    @SuppressWarnings("unchecked")
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
    @SuppressWarnings("unchecked")
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
    @SuppressWarnings("unchecked")
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
    @SuppressWarnings("unchecked")
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
    @SuppressWarnings("unchecked")
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
    @SuppressWarnings("unchecked")
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
    @SuppressWarnings("unchecked")
    public Expression<String> toString(Expression<Character> character) {
        return (Expression<String>)(Expression<?>) character;
    }

    // literals:
    /**
     * Create an expression literal.
     *
     * @return expression literal
     */
    @Override
    public <T> Expression<T> literal(T value){
        if (value == null) {
            throw new IllegalArgumentException( ExceptionLocalization.buildMessage("jpa_criteriaapi_null_literal_value", new Object[]{}));
        }
        return ExpressionImpl.createLiteral(value, metamodel);
    }

    /**
     * Create an expression for a null literal with the given type.
     *
     * @param resultClass  type of the null literal
     * @return null expression literal
     */
    @Override
    public <T> Expression<T> nullLiteral(Class<T> resultClass) {
        return ExpressionImpl.createLiteral(null, metamodel, resultClass);
    }

    /**
     * Create an expression literal but without null validation.
     *
     * @return expression literal
     */
    protected <T> Expression<T> internalLiteral(T value){
        return ExpressionImpl.createLiteral(value, metamodel);
    }

    // parameters:
    /**
     * Create a parameter.
     * Create a parameter expression.
     *
     * @param paramClass parameter class
     * @return parameter expression
     */
    @Override
    public <T> ParameterExpression<T> parameter(Class<T> paramClass){
        return new ParameterExpressionImpl<>(metamodel, paramClass);
    }

    /**
     * Create a parameter expression with the given name.
     *
     * @param paramClass
     *            parameter class
     * @return parameter
     */
    @Override
    public <T> ParameterExpression<T> parameter(Class<T> paramClass, String name){
        return new ParameterExpressionImpl<>(metamodel, paramClass, name);
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
            if (((Collection<?>)((ConstantExpression)currentNode(collection)).getValue()).isEmpty()){
                return conjunction();
            }else{
                return disjunction();
            }
        }
        return new CompoundExpressionImpl(metamodel, currentNode(collection).size(CoreClassConstants.INTEGER).equal(0), buildList(collection), "isEmpty");
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
        return new CompoundExpressionImpl(metamodel, currentNode(collection).size(CoreClassConstants.INTEGER).equal(0).not(), buildList(collection), "isNotEmpty");
    }

    /**
     * Create an expression that tests the size of a collection.
     *
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
        return new FunctionExpressionImpl<>(metamodel, CoreClassConstants.INTEGER, currentNode(collection).size(CoreClassConstants.INTEGER), buildList(collection), SIZE);
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
        return new CompoundExpressionImpl(metamodel, currentNode(collection).equal(elem), buildList(collection, internalLiteral(elem)), "isMember");
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
        return new CompoundExpressionImpl(metamodel, currentNode(collection).notEqual(elem), buildList(collection, internalLiteral(elem)), "isMember");

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
        return new CompoundExpressionImpl(metamodel, currentNode(collection).equal(currentNode(elem)), buildList(collection, elem), "isMember");
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
        subQuery.setReferenceClass(((ExpressionImpl<?>)elem).getJavaType());
        org.eclipse.persistence.expressions.ExpressionBuilder elemBuilder = new org.eclipse.persistence.expressions.ExpressionBuilder();
        org.eclipse.persistence.expressions.Expression collectionExp =currentNode(collection);
        org.eclipse.persistence.expressions.Expression elemExp =currentNode(elem);

        subQuery.setExpressionBuilder(elemBuilder);
        subQuery.setShouldRetrieveFirstPrimaryKey(true);
        subQuery.setSelectionCriteria(elemBuilder.equal(collectionExp).and(collectionExp.equal(elemExp)));

        return new CompoundExpressionImpl(metamodel, currentNode(elem).notExists(subQuery), buildList(elem, collection), "isNotMemeber");
    }

    // get the values and keys collections of the Map, which may then
    // be passed to size(), isMember(), isEmpty(), etc
    /**
     * Create an expression that returns the values of a map.
     *
     * @return collection expression
     */
    @Override
    public <V, M extends Map<?, V>> Expression<Collection<V>> values(M map){
        return internalLiteral(map.values());
    }

    /**
     * Create an expression that returns the keys of a map.
     *
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
        return new CompoundExpressionImpl(this.metamodel,
            currentNode(x).like(currentNode(pattern)), buildList(x, pattern), "like");
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
        return new CompoundExpressionImpl(this.metamodel,
            currentNode(x).like(currentNode(pattern), currentNode(escapeChar)), buildList(x, pattern, escapeChar), "like");
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
        return new CompoundExpressionImpl(this.metamodel, currentNode(x).like(pattern), buildList(x, internalLiteral(pattern)), "like");
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
        String escapeString = String.valueOf(escapeChar);

        return new CompoundExpressionImpl(this.metamodel,
            currentNode(x).like(pattern, escapeString), buildList(x, internalLiteral(pattern), internalLiteral(escapeChar)), "like");
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
        return new CompoundExpressionImpl(this.metamodel,
            currentNode(x).notLike(currentNode(pattern)), buildList(x, pattern), "notLike");
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
        return new CompoundExpressionImpl(this.metamodel,
            currentNode(x).notLike(currentNode(pattern),
            currentNode(escapeChar)), buildList(x, pattern, escapeChar), "like");
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
        return new CompoundExpressionImpl(this.metamodel,
            currentNode(x).notLike(pattern), buildList(x, internalLiteral(pattern)), "notLike");
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

    @Override
    public Expression<String> concat(List<Expression<String>> expressions) {
        switch(expressions != null ? expressions.size() : 0) {
            case 0:
                return literal("");
            case 1:
                return expressions.get(0);
            case 2:
                return concat(expressions.get(0), expressions.get(1));
            default:
                Expression<String> expression = expressions.get(0);
                for (int i = 1; i < expressions.size(); i++) {
                    expression = concat(expression, expressions.get(i));
                }
                return expression;
        }
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
        org.eclipse.persistence.expressions.Expression xNode = currentNode(x);
        org.eclipse.persistence.expressions.Expression yNode = currentNode(y);

        if (xNode.isParameterExpression() && yNode.isParameterExpression()) {
            //some database require the type when concatting two parameters.
            ((org.eclipse.persistence.internal.expressions.ParameterExpression)xNode).setType(CoreClassConstants.STRING);
        }
        return new FunctionExpressionImpl<>(this.metamodel, CoreClassConstants.STRING, xNode.concat(yNode), buildList(x, y), CONCAT);
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
        return new FunctionExpressionImpl<>(this.metamodel, CoreClassConstants.STRING, currentNode(x).concat(y), buildList(x, internalLiteral(y)), CONCAT);

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
        Expression<String> literal = internalLiteral(x);
        return new FunctionExpressionImpl<>(this.metamodel, CoreClassConstants.STRING, currentNode(literal).concat(currentNode(y)), buildList(literal, y), CONCAT);
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
        return new FunctionExpressionImpl<>(metamodel, CoreClassConstants.STRING, currentNode( x).substring(currentNode( from)), buildList(x, from), "subString");
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
        return new FunctionExpressionImpl<>(metamodel, CoreClassConstants.STRING, currentNode(x).substring(currentNode(from), currentNode(len)), buildList(x, from, len), "subString");
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
        return new FunctionExpressionImpl<>(metamodel, CoreClassConstants.STRING, currentNode(x).substring(from, len), buildList(x, internalLiteral(from), internalLiteral(len)), "subString");
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
        return new FunctionExpressionImpl<>(this.metamodel, CoreClassConstants.STRING, currentNode(x).trim(), buildList(x), "trim");
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
        if(ts == Trimspec.LEADING) {
            return new FunctionExpressionImpl<>(this.metamodel, CoreClassConstants.STRING, currentNode(x).leftTrim(), buildList(x), "leftTrim");
        } else if(ts == Trimspec.TRAILING) {
            return new FunctionExpressionImpl<>(this.metamodel, CoreClassConstants.STRING, currentNode(x).rightTrim(), buildList(x), "rightTrim");
    }
        return new FunctionExpressionImpl<>(this.metamodel, CoreClassConstants.STRING, currentNode(x).rightTrim().leftTrim(), buildList(x), "bothTrim");

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
        return new FunctionExpressionImpl<>(this.metamodel, CoreClassConstants.STRING, currentNode(x).trim(currentNode(t)), buildList(x, t), "trim");
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
        if(ts == Trimspec.LEADING) {
            return new FunctionExpressionImpl<>(this.metamodel, CoreClassConstants.STRING,
                currentNode(x).leftTrim(currentNode(t)), buildList(x, t), "leftTrim");
        } else if(ts == Trimspec.TRAILING) {
            return new FunctionExpressionImpl<>(this.metamodel, CoreClassConstants.STRING,
                currentNode(x).rightTrim(currentNode(t)), buildList(x, t), "rightTrim");
    }
        return new FunctionExpressionImpl<>(this.metamodel, CoreClassConstants.STRING,
            currentNode(x).rightTrim(currentNode(t)).leftTrim(currentNode(t)), buildList(x, t), "bothTrim");
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
        return new FunctionExpressionImpl<>(this.metamodel, CoreClassConstants.STRING, currentNode(x).toLowerCase(), buildList(x), "lower");
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
        return new FunctionExpressionImpl<>(this.metamodel, CoreClassConstants.STRING, currentNode(x).toUpperCase(), buildList(x), "upper");
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
        return new FunctionExpressionImpl<>(metamodel, CoreClassConstants.INTEGER, currentNode(x).length(), buildList(x), "length");
    }

    @Override
    public Expression<String> left(Expression<String> expression, int len) {
        return new FunctionExpressionImpl<>(
                this.metamodel, CoreClassConstants.STRING,
                currentNode(expression).left(len), buildList(expression), "left");
    }

    @Override
    public Expression<String> left(Expression<String> expression, Expression<Integer> len) {
        return new FunctionExpressionImpl<>(
                this.metamodel, CoreClassConstants.STRING,
                currentNode(expression).left(currentNode(len)), buildList(expression), "left");
    }

    @Override
    public Expression<String> right(Expression<String> expression, int len) {
        return new FunctionExpressionImpl<>(
                this.metamodel, CoreClassConstants.STRING,
                currentNode(expression).right(len), buildList(expression), "right");
    }

    @Override
    public Expression<String> right(Expression<String> expression, Expression<Integer> len) {
        return new FunctionExpressionImpl<>(
                this.metamodel, CoreClassConstants.STRING,
                currentNode(expression).right(currentNode(len)), buildList(expression), "right");
    }

    @Override
    public Expression<String> replace(Expression<String> expression, Expression<String> substring, Expression<String> replacement) {
        return new FunctionExpressionImpl<>(
                this.metamodel, CoreClassConstants.STRING,
                currentNode(expression).replace(currentNode(substring), currentNode(replacement)),
                buildList(expression), "replace");
    }

    @Override
    public Expression<String> replace(Expression<String> expression, String substring, Expression<String> replacement) {
        return new FunctionExpressionImpl<>(
                this.metamodel, CoreClassConstants.STRING,
                currentNode(expression).replace(substring, currentNode(replacement)),
                buildList(expression), "replace");
    }

    @Override
    public Expression<String> replace(Expression<String> expression, Expression<String> substring, String replacement) {
        return new FunctionExpressionImpl<>(
                this.metamodel, CoreClassConstants.STRING,
                currentNode(expression).replace(currentNode(substring), replacement),
                buildList(expression), "replace");
    }

    @Override
    public Expression<String> replace(Expression<String> expression, String substring, String replacement) {
        return new FunctionExpressionImpl<>(
                this.metamodel, CoreClassConstants.STRING,
                currentNode(expression).replace(substring, replacement),
                buildList(expression), "replace");
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
        return new FunctionExpressionImpl<>(metamodel, CoreClassConstants.INTEGER, currentNode(x).locate(currentNode(pattern)), buildList(x, pattern), "locate");
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
        return new FunctionExpressionImpl<>(metamodel, CoreClassConstants.INTEGER, currentNode(x).locate(currentNode(pattern), currentNode(from)), buildList(x, pattern, from), "locate");
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
        return new FunctionExpressionImpl<>(metamodel, CoreClassConstants.INTEGER, currentNode(x).locate(pattern), buildList(x, internalLiteral(pattern)), "locate");
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
        return new FunctionExpressionImpl<>(metamodel, CoreClassConstants.INTEGER, currentNode(x).locate(pattern, from), buildList(x, internalLiteral(pattern), internalLiteral(from)), "locate");
    }

    // Date/time/timestamp functions:
    /**
     * Create expression to return current date.
     *
     * @return expression for current date
     */
    @Override
    public Expression<java.sql.Date> currentDate(){
        return new ExpressionImpl<>(metamodel, ClassConstants.SQLDATE, new ExpressionBuilder().currentDateDate());
    }

    /**
     * Create expression to return current timestamp.
     *
     * @return expression for current timestamp
     */
    @Override
    public Expression<java.sql.Timestamp> currentTimestamp(){
        return new ExpressionImpl<>(metamodel, ClassConstants.TIMESTAMP, new ExpressionBuilder().currentTimeStamp());
    }

    /**
     * Create expression to return current time.
     *
     * @return expression for current time
     */
    @Override
    public Expression<java.sql.Time> currentTime(){
        return new ExpressionImpl<>(metamodel, ClassConstants.TIME, new ExpressionBuilder().currentTime());
    }

    /**
     * Create expression to return local datetime.
     *
     * @return expression for local timestamp
     */
    @Override
    public Expression<java.time.LocalDateTime> localDateTime() {
        return new ExpressionImpl<>(metamodel, CoreClassConstants.LOCAL_DATETIME, new ExpressionBuilder().localDateTime());
    }

    /**
     * Create expression to return local date.
     *
     * @return expression for local date
     */
    @Override
    public Expression<java.time.LocalDate> localDate() {
        return new ExpressionImpl<>(metamodel, CoreClassConstants.LOCAL_DATE, new ExpressionBuilder().localDate());
    }

    /**
     * Create expression to return local time.
     *
     * @return expression for local time
     */
    @Override
    public Expression<java.time.LocalTime> localTime() {
        return new ExpressionImpl<>(metamodel, CoreClassConstants.LOCAL_TIME, new ExpressionBuilder().localTime());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <N, T extends Temporal> Expression<N> extract(TemporalField<N, T> field, Expression<T> temporal) {
        return new FunctionExpressionImpl<>(
                // Java type took from Expression, but not sure whether this will work for everything
                this.metamodel, (Class<N>) temporal.getJavaType(),
                // JPA API: field.toString() returns name of the part to be extracted
                currentNode(temporal).extract(field.toString()), buildList(temporal), "extract");
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
    @SuppressWarnings("unchecked") // Expression passed as ExpressionImpl to the constructor
    public <T> In<T> in(Expression<? extends T> expression) {
        return new InImpl<>(metamodel, (ExpressionImpl<? extends T>) expression, new ArrayList<>(), buildList(expression));
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
    public <Y> Expression<Y> coalesce(Expression<? extends Y> x, Expression<? extends Y> y) {
        ArgumentListFunctionExpression coalesce = currentNode(x).coalesce();
        org.eclipse.persistence.expressions.Expression expX = currentNode(x);
        expX = org.eclipse.persistence.expressions.Expression.from(expX, coalesce);
        coalesce.addChild(expX);
        org.eclipse.persistence.expressions.Expression expY = currentNode(y);
        expY = org.eclipse.persistence.expressions.Expression.from(expY, coalesce);
        coalesce.addChild(expY);
        return new CoalesceImpl<>(metamodel, x.getJavaType(), coalesce,  buildList(x, y), "coalesce");
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
    public <Y> Expression<Y> coalesce(Expression<? extends Y> x, Y y) {
        ArgumentListFunctionExpression coalesce = currentNode(x).coalesce();
        org.eclipse.persistence.expressions.Expression expX = currentNode(x);
        expX = org.eclipse.persistence.expressions.Expression.from(expX, coalesce);
        coalesce.addChild(expX);
        org.eclipse.persistence.expressions.Expression expY = org.eclipse.persistence.expressions.Expression.from(y, new ExpressionBuilder());
        coalesce.addChild(expY);
        return new CoalesceImpl<>(metamodel, x.getJavaType(), coalesce, buildList(x, internalLiteral(y)), "coalesce");
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
        return new FunctionExpressionImpl<>(metamodel, x.getJavaType(), currentNode(x).nullIf(currentNode(y)), buildList(x, y), "nullIf");
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
        return new FunctionExpressionImpl<>(metamodel, x.getJavaType(), currentNode(x).nullIf(y), buildList(x, internalLiteral(y)), "nullIf");
    }

    /**
     * Create a coalesce expression.
     *
     * @return coalesce expression
     */
    @Override
    @SuppressWarnings("unchecked") // Java type cast
    public <T> Coalesce<T> coalesce() {
        ArgumentListFunctionExpression coalesce = new ExpressionBuilder().coalesce();
        return new CoalesceImpl<>(metamodel, (Class<? extends T>) Object.class, coalesce, new ArrayList<>());
    }

    /**
     * Create simple case expression.
     *
     * @param expression
     *            to be tested against the case conditions
     * @return simple case expression
     */
    @Override
    @SuppressWarnings("unchecked") // Java type cast
    public <C, R> SimpleCase<C, R> selectCase(Expression<? extends C> expression){
        ArgumentListFunctionExpression caseStatement = new ExpressionBuilder().caseStatement();
        return new SimpleCaseImpl<>(metamodel, (Class<? extends R>) Object.class, caseStatement, new ArrayList<>(), expression);
    }

    /**
     * Create a general case expression.
     *
     * @return general case expression
     */
    @Override
    public <R> Case<R> selectCase(){
        ArgumentListFunctionExpression caseStatement = new ExpressionBuilder().caseConditionStatement();
        return new CaseImpl(metamodel, Object.class, caseStatement, new ArrayList<>());
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
        List<org.eclipse.persistence.expressions.Expression> params = new ArrayList<>();
        for (int index = 1; index < args.length; ++index){
            Expression<?> x = args[index];
            params.add(currentNode(x));
        }

        return new FunctionExpressionImpl<>(metamodel, type, currentNode(args[0]).getFunctionWithArguments(name, params), buildList(args), name);
        }else{
            return new FunctionExpressionImpl<>(metamodel, type, new ExpressionBuilder().getFunction(name), new ArrayList<>(0), name);
        }
    }

    /**
     * ADVANCED:
     * Allow a Criteria Expression to be built from a EclipseLink native API Expression object.
     * This allows for an extended functionality supported in EclipseLink Expressions to be used in Criteria.
     */
    @Override
    public <T> Expression<T> fromExpression(org.eclipse.persistence.expressions.Expression expression, Class<T> type) {
        return new FunctionExpressionImpl<>(this.metamodel, type, expression, new ArrayList<>(0));
    }

    /**
     * ADVANCED:
     * Allow a Criteria Expression to be built from a EclipseLink native API Expression object.
     * This allows for an extended functionality supported in EclipseLink Expressions to be used in Criteria.
     */
    @Override
    public Expression fromExpression(org.eclipse.persistence.expressions.Expression expression) {
        return new FunctionExpressionImpl<>(this.metamodel, Object.class, expression, new ArrayList<>(0));
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
     * <p>
     * A coalesce expression is equivalent to a case expression
     * that returns null if all its arguments evaluate to null,
     * and the value of its first non-null argument otherwise.
     */
    public class CoalesceImpl<X> extends FunctionExpressionImpl<X> implements Coalesce<X> {

        protected <T> CoalesceImpl (Metamodel metamodel, Class<? extends X> resultClass, org.eclipse.persistence.expressions.Expression expressionNode, List<Expression<?>> compoundExpressions) {
            super(metamodel, resultClass, expressionNode, compoundExpressions);
        }

        protected <T> CoalesceImpl (Metamodel metamodel, Class<? extends X> resultClass, org.eclipse.persistence.expressions.Expression expressionNode, List<Expression<?>> compoundExpressions, String operator) {
            super(metamodel, resultClass, expressionNode, compoundExpressions, operator);
        }

        /**
         * Add an argument to the coalesce expression.
         * @param value  value
         * @return coalesce expression
         */
        @Override
        public Coalesce<X> value(X value) {
            org.eclipse.persistence.expressions.Expression exp = org.eclipse.persistence.expressions.Expression.from(value, new ExpressionBuilder());
            ((FunctionExpression)currentNode).addChild(exp);
            Expression<? extends X> valueLiteral = internalLiteral(value);
            this.expressions.add(valueLiteral);
            return this;
        }

        /**
         * Add an argument to the coalesce expression.
         * @param value expression
         * @return coalesce expression
         */
        @Override
        public Coalesce<X> value(Expression<? extends X> value) {
            org.eclipse.persistence.expressions.Expression exp = currentNode(value);
            exp = org.eclipse.persistence.expressions.Expression.from(exp, currentNode);
            ((FunctionExpression)currentNode).addChild(exp);
            this.expressions.add(value);
            return this;
        }
    }

    /**
     * Implementation of Case interface from Criteria Builder
     * @author tware
     *
     * @param <R>
     */
    public class CaseImpl<R> extends FunctionExpressionImpl<R> implements Case<R> {

        // Track the else expression separate to the when expressions as there should only be one
        private Expression<? extends R> elseExpression;

        protected <T> CaseImpl (Metamodel metamodel, Class<R> resultClass, org.eclipse.persistence.expressions.Expression expressionNode, List<Expression<?>> compoundExpressions) {
            super(metamodel, resultClass, expressionNode, compoundExpressions);
        }

        protected <T> CaseImpl (Metamodel metamodel, Class<R> resultClass, org.eclipse.persistence.expressions.Expression expressionNode, List<Expression<?>> compoundExpressions, String operator) {
            super(metamodel, resultClass, expressionNode, compoundExpressions, operator);
        }

        /**
         * Add a when/then clause to the case expression.
         * @param condition  "when" condition
         * @param result  "then" result value
         * @return general case expression
         */
        @Override
        public Case<R> when(Expression<Boolean> condition, R result) {
            org.eclipse.persistence.expressions.Expression conditionExp = currentNode(condition);
            conditionExp = org.eclipse.persistence.expressions.Expression.from(conditionExp, currentNode);
            ((FunctionExpression)currentNode).addChild(conditionExp);
            this.expressions.add(condition);

            org.eclipse.persistence.expressions.Expression resultExp = org.eclipse.persistence.expressions.Expression.from(result, new ExpressionBuilder());
            ((FunctionExpression)currentNode).addChild(resultExp);
            Expression<R> resultLiteral = internalLiteral(result);
            this.expressions.add(resultLiteral);

            setJavaType(resultLiteral.getJavaType());
            return this;
        }

        /**
         * Add a when/then clause to the case expression.
         * @param condition  "when" condition
         * @param result  "then" result expression
         * @return general case expression
         */
        @Override
        public Case<R> when(Expression<Boolean> condition, Expression<? extends R> result) {
            org.eclipse.persistence.expressions.Expression conditionExp = currentNode(condition);
            conditionExp = org.eclipse.persistence.expressions.Expression.from(conditionExp, currentNode);
            ((FunctionExpression)currentNode).addChild(conditionExp);
            this.expressions.add(condition);

            org.eclipse.persistence.expressions.Expression resultExp = currentNode(result);
            resultExp = org.eclipse.persistence.expressions.Expression.from(resultExp, currentNode);
            ((FunctionExpression)currentNode).addChild(resultExp);
            this.expressions.add(result);

            setJavaType(result.getJavaType());
            return this;
        }

        /**
         * Add an "else" clause to the case expression.
         * @param result  "else" result
         * @return expression
         */
        @Override
        public Expression<R> otherwise(R result) {
            org.eclipse.persistence.expressions.Expression resultExp = org.eclipse.persistence.expressions.Expression.from(result, new ExpressionBuilder());
            ((ArgumentListFunctionExpression)currentNode).addRightMostChild(resultExp);
            Expression<R> resultLiteral = internalLiteral(result);
            this.elseExpression = resultLiteral;

            setJavaType(resultLiteral.getJavaType());
            return this;
        }

        /**
         * Add an "else" clause to the case expression.
         * @param result  "else" result expression
         * @return expression
         */
        @Override
        public Expression<R> otherwise(Expression<? extends R> result) {
            org.eclipse.persistence.expressions.Expression resultExp = currentNode(result);
            resultExp = org.eclipse.persistence.expressions.Expression.from(resultExp, currentNode);
            ((ArgumentListFunctionExpression)currentNode).addRightMostChild(resultExp);
            this.elseExpression = result;

            setJavaType(result.getJavaType());
            return this;
        }

        @Override
        public void findRootAndParameters(CommonAbstractCriteriaImpl<?> query) {
            super.findRootAndParameters(query);
            if (this.elseExpression != null){
                ((InternalSelection)elseExpression).findRootAndParameters(query);
            }
        }
    }

    /**
     * Implementation of SimpleCase interface from CriteriaBuilder
     * @author tware
     *
     * @param <C>
     * @param <R>
     */
    public class SimpleCaseImpl<C, R> extends FunctionExpressionImpl<R> implements SimpleCase<C, R> {

        private final Expression<? extends C> expression;

        // Track the else expression separate to the when expressions as there should only be one
        private Expression<? extends R> elseExpression;

        protected  SimpleCaseImpl(Metamodel metamodel, Class<? extends R> resultClass, FunctionExpression expressionNode, List<Expression<?>> compoundExpressions, Expression<? extends C> expression) {
            super(metamodel, resultClass, expressionNode, compoundExpressions);
            this.expression = expression;
            expressionNode.addChild(currentNode(expression));
        }

        protected SimpleCaseImpl(Metamodel metamodel, Class<? extends R> resultClass, FunctionExpression expressionNode, List<Expression<?>> compoundExpressions, String operator, Expression<? extends C> expression) {
            super(metamodel, resultClass, expressionNode, compoundExpressions, operator);
            this.expression = expression;
            expressionNode.addChild(currentNode(expression));
        }

        /**
         * Returns the expression to be tested against the
         * conditions.
         * @return expression
         */
        @Override
        @SuppressWarnings("unchecked") // JPA API generics clash
        public Expression<C> getExpression(){
            return (Expression<C>) expression;
        }

        /**
         * Add a when/then clause to the case expression.
         * @param condition  "when" condition
         * @param result  "then" result value
         * @return simple case expression
         */
        @Override
        public SimpleCase<C, R> when(C condition, R result) {
            org.eclipse.persistence.expressions.Expression conditionExp = org.eclipse.persistence.expressions.Expression.from(condition, new ExpressionBuilder());
            ((FunctionExpression)currentNode).addChild(conditionExp);
            Expression<C> conditionLiteral = internalLiteral(condition);
            this.expressions.add(conditionLiteral);

            org.eclipse.persistence.expressions.Expression resultExp = org.eclipse.persistence.expressions.Expression.from(result, new ExpressionBuilder());
            ((FunctionExpression)currentNode).addChild(resultExp);
            Expression<R> resultLiteral = internalLiteral(result);
            this.expressions.add(resultLiteral);

            setJavaType(resultLiteral.getJavaType());
            return this;
        }

        /**
         * Add a when/then clause to the case expression.
         * @param condition  "when" condition
         * @param result  "then" result expression
         * @return simple case expression
         */
        @Override
        public SimpleCase<C, R> when(C condition, Expression<? extends R> result) {
            org.eclipse.persistence.expressions.Expression conditionExp = org.eclipse.persistence.expressions.Expression.from(condition, new ExpressionBuilder());
            ((FunctionExpression)currentNode).addChild(conditionExp);
            Expression<C> conditionLiteral = internalLiteral(condition);
            this.expressions.add(conditionLiteral);

            org.eclipse.persistence.expressions.Expression resultExp = currentNode(result);
            resultExp = org.eclipse.persistence.expressions.Expression.from(resultExp, currentNode);
            ((FunctionExpression)currentNode).addChild(resultExp);
            this.expressions.add(result);

            setJavaType(result.getJavaType());
            return this;
        }

        /**
         * Add a when/then clause to the case expression.
         * @param condition  "when" condition
         * @param result  "then" result value
         * @return simple case expression
         */
        @Override
        public SimpleCase<C, R> when(Expression<? extends C> condition, R result) {
            org.eclipse.persistence.expressions.Expression conditionExp = currentNode(condition);
            conditionExp = org.eclipse.persistence.expressions.Expression.from(conditionExp, currentNode);
            ((FunctionExpression)currentNode).addChild(conditionExp);
            this.expressions.add(condition);

            org.eclipse.persistence.expressions.Expression resultExp = org.eclipse.persistence.expressions.Expression.from(result, new ExpressionBuilder());
            ((FunctionExpression)currentNode).addChild(resultExp);
            Expression<R> resultLiteral = internalLiteral(result);
            this.expressions.add(resultLiteral);

            setJavaType(resultLiteral.getJavaType());
            return this;
        }

        /**
         * Add a when/then clause to the case expression.
         * @param condition  "when" condition
         * @param result  "then" result expression
         * @return simple case expression
         */
        @Override
        public SimpleCase<C, R> when(Expression<? extends C> condition, Expression<? extends R> result) {
            org.eclipse.persistence.expressions.Expression conditionExp = currentNode(condition);
            conditionExp = org.eclipse.persistence.expressions.Expression.from(conditionExp, currentNode);
            ((FunctionExpression)currentNode).addChild(conditionExp);
            this.expressions.add(condition);

            org.eclipse.persistence.expressions.Expression resultExp = currentNode(result);
            resultExp = org.eclipse.persistence.expressions.Expression.from(resultExp, currentNode);
            ((FunctionExpression)currentNode).addChild(resultExp);
            this.expressions.add(result);

            setJavaType(result.getJavaType());
            return this;
        }

        /**
         * Add an "else" clause to the case expression.
         * @param result  "else" result
         * @return expression
         */
        @Override
        public Expression<R> otherwise(R result) {
            org.eclipse.persistence.expressions.Expression resultExp = org.eclipse.persistence.expressions.Expression.from(result, new ExpressionBuilder());
            ((ArgumentListFunctionExpression)currentNode).addRightMostChild(resultExp);
            Expression<R> resultLiteral = internalLiteral(result);
            this.elseExpression = resultLiteral;

            setJavaType(resultLiteral.getJavaType());
            return this;
        }

        /**
         * Add an "else" clause to the case expression.
         * @param result  "else" result expression
         * @return expression
         */
        @Override
        public Expression<R> otherwise(Expression<? extends R> result) {
            org.eclipse.persistence.expressions.Expression resultExp = currentNode(result);
            resultExp = org.eclipse.persistence.expressions.Expression.from(resultExp, currentNode);
            ((ArgumentListFunctionExpression)currentNode).addRightMostChild(resultExp);
            this.elseExpression = result;

            setJavaType(result.getJavaType());
            return this;
        }

        @Override
        public void findRootAndParameters(CommonAbstractCriteriaImpl<?> query) {
            super.findRootAndParameters(query);
            if(expression != null) {
                ((InternalSelection)expression).findRootAndParameters(query);
            }
            if (this.elseExpression != null){
                ((InternalSelection)elseExpression).findRootAndParameters(query);
            }
        }
    }

    @Override
    public <T> CriteriaDelete<T> createCriteriaDelete(Class<T> targetEntity) {
        if (targetEntity != null) {
            TypeImpl<T> type = ((MetamodelImpl)this.metamodel).getType(targetEntity);
            if (type != null && type.getPersistenceType().equals(PersistenceType.ENTITY)) {
                return new CriteriaDeleteImpl<>(this.metamodel, this, targetEntity);
            }
        }
        throw new IllegalArgumentException(ExceptionLocalization.buildMessage("unknown_bean_class", new Object[] { targetEntity }));
    }

    @Override
    public <T> CriteriaUpdate<T> createCriteriaUpdate(Class<T> targetEntity) {
        if (targetEntity != null) {
            TypeImpl<T> type = ((MetamodelImpl)this.metamodel).getType(targetEntity);
            if (type != null && type.getPersistenceType().equals(PersistenceType.ENTITY)) {
                return new CriteriaUpdateImpl<>(this.metamodel, this, targetEntity);
            }
        }
        throw new IllegalArgumentException(ExceptionLocalization.buildMessage("unknown_bean_class", new Object[] { targetEntity }));
    }

    @Override
    public <X, T, V extends T> Join<X, V> treat(Join<X, T> join, Class<V> type) {
        JoinImpl parentJoin = (JoinImpl)join;
        JoinImpl<X, V> joinImpl = new JoinImpl<X, V>(parentJoin, this.metamodel.managedType(type), this.metamodel,
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
        EntityType<T> entity = this.metamodel.entity(type);
        return new RootImpl<>(entity, this.metamodel, type, parentRoot.currentNode.treat(type), entity);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> CriteriaSelect<T> union(CriteriaSelect<? extends T> first, CriteriaSelect<? extends T> second) {
        return new CriteriaMultiSelectImpl<>((CriteriaSelect<T>) first, second, Union.UNION);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> CriteriaSelect<T> unionAll(CriteriaSelect<? extends T> first, CriteriaSelect<? extends T> second) {
        return new CriteriaMultiSelectImpl<>((CriteriaSelect<T>) first, second, Union.UNION_ALL);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> CriteriaSelect<T> intersect(CriteriaSelect<? super T> first, CriteriaSelect<? super T> second) {
        return new CriteriaMultiSelectImpl<>((CriteriaSelect<T>) first, second, Union.INTERSECT);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> CriteriaSelect<T> intersectAll(CriteriaSelect<? super T> first, CriteriaSelect<? super T> second) {
        return new CriteriaMultiSelectImpl<>((CriteriaSelect<T>) first, second, Union.INTERSECT_ALL);
    }

    @Override
    public <T> CriteriaSelect<T> except(CriteriaSelect<T> first, CriteriaSelect<?> second) {
        return new CriteriaMultiSelectImpl<>(first, second, Union.EXCEPT);
    }

    @Override
    public <T> CriteriaSelect<T> exceptAll(CriteriaSelect<T> first, CriteriaSelect<?> second) {
        return new CriteriaMultiSelectImpl<>(first, second, Union.EXCEPT_ALL);
    }

}

