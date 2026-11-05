/*
 * Copyright (c) 2026 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.persistence.internal.jpa.querydef;

import java.io.Serial;
import java.util.List;

import jakarta.persistence.criteria.BooleanExpression;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Selection;

import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.expressions.ExpressionOperator;
import org.eclipse.persistence.internal.expressions.ConstantExpression;
import org.eclipse.persistence.internal.expressions.FunctionExpression;

/**
 * Boolean values with fluent logical and null-handling operations. Scalar
 * values retain their mapping context; predicates become scalar values only
 * when an operation requires one.
 */
public class BooleanExpressionImpl extends ComparableExpressionImpl<Boolean> implements BooleanExpression {

    @Serial
    private static final long serialVersionUID = 1L;

    private final boolean nativeBoolean;

    BooleanExpressionImpl(ExpressionImpl<Boolean> delegate) {
        this(delegate, delegate instanceof Predicate || delegate.isLiteral());
    }

    private BooleanExpressionImpl(ExpressionImpl<Boolean> delegate, boolean nativeBoolean) {
        super(asValue(delegate));
        this.nativeBoolean = nativeBoolean;
    }

    /**
     * SQL predicates are not scalar values on every supported database. The
     * second WHEN is necessary: an unknown predicate must produce NULL, not
     * FALSE, so coalesce and nullif retain SQL's three-valued semantics.
     */
    private static ExpressionImpl<Boolean> asValue(ExpressionImpl<Boolean> expression) {
        if (expression instanceof Predicate) {
            return booleanValue(expression, new CriteriaBuilderImpl(expression.metamodel));
        }
        return expression;
    }

    /**
     * Null-handling operates on Boolean domain values, not a column's possibly
     * converted representation (for example, Y/N). Comparisons keep the original
     * mapping while the CASE results share the platform's Boolean representation.
     */
    private static ExpressionImpl<Boolean> booleanValue(Expression<? extends Boolean> expression, CriteriaBuilderImpl builder) {
        if (expression instanceof ExpressionImpl<?> literal && literal.isLiteral()) {
            return booleanLiteral((Boolean) literal.literal, builder);
        }
        if (expression instanceof BooleanExpressionImpl value && value.nativeBoolean) {
            return value;
        }
        if (expression instanceof ParameterExpressionImpl<?> parameter && parameter.getConverterClass() == null) {
            // A plain parameter already has the Boolean domain representation.
            // Keep an inlined null in a CASE result position, where it is legal
            // on every supported platform, instead of comparing NULL to TRUE.
            org.eclipse.persistence.expressions.Expression node = parameter.getCurrentNode().clone();
            node.setLocalBase(new ExpressionBuilder());
            // The ELSE value supplies CASE's SQL type. Keep the parameter inside
            // a scalar expression so partial-binding policies need not inline
            // this already typed result, including when its value is null.
            // Do not add parentheses: Derby permits an inlined NULL in a THEN
            // result, but rejects the parenthesized form (NULL).
            FunctionExpression typedNode = (FunctionExpression) ExpressionOperator
                    .simpleFunctionNoParentheses(0, "").expressionFor(node);
            typedNode.setResultType(Boolean.class);
            ExpressionImpl<Boolean> value = new FunctionExpressionImpl<>(parameter.metamodel,
                    Boolean.class, typedNode, List.of(expression), "booleanParameter");
            return typedNullableValue(value, builder);
        }
        if (expression instanceof PredicateImpl junction && junction.isJunction()) {
            return booleanLiteral(junction.getJunctionValue(), builder);
        }
        return caseValue(expression, builder);
    }

    @SuppressWarnings("unchecked")
    private static ExpressionImpl<Boolean> caseValue(Expression<? extends Boolean> expression, CriteriaBuilderImpl builder) {
        Expression<Boolean> operand = (Expression<Boolean>) expression;
        if (operand instanceof PredicateImpl junction && junction.isJunction()) {
            operand = booleanLiteral(junction.getJunctionValue(), builder);
        }
        Predicate whenTrue;
        Predicate whenFalse;
        if (operand instanceof Predicate predicate) {
            whenTrue = predicate;
            whenFalse = predicate.not();
        } else {
            // These are generated Boolean constants, not caller parameters.
            // Inline them so SELECT DISTINCT and ORDER BY reuse identical SQL
            // expressions instead of acquiring different bind positions.
            whenTrue = builder.equal(operand, booleanLiteral(Boolean.TRUE, builder));
            whenFalse = builder.equal(operand, booleanLiteral(Boolean.FALSE, builder));
        }
        ExpressionImpl<Boolean> trueValue = booleanLiteral(Boolean.TRUE, builder);
        ExpressionImpl<Boolean> falseValue = booleanLiteral(Boolean.FALSE, builder);
        ExpressionImpl<Boolean> nullValue = booleanLiteral(null, builder);
        ExpressionBuilder valueBase = trueValue.getCurrentNode().getBuilder();
        ExpressionImpl<Boolean> result = (ExpressionImpl<Boolean>) builder.<Boolean>selectCase()
                .when(whenTrue, trueValue)
                .when(whenFalse, falseValue)
                .otherwise(nullValue);
        // CASE normally makes its result literals refer back to the CASE for
        // mapping conversion. These Boolean results need no mapped conversion.
        // Keep their independent base so an all-literal predicate can be rebuilt
        // on the query root without recursively rebuilding the containing CASE.
        trueValue.getCurrentNode().setLocalBase(valueBase);
        falseValue.getCurrentNode().setLocalBase(valueBase);
        nullValue.getCurrentNode().setLocalBase(valueBase);
        return result;
    }

    private static ExpressionImpl<Boolean> booleanLiteral(Boolean value, CriteriaBuilderImpl builder) {
        ExpressionImpl<Boolean> expression = (ExpressionImpl<Boolean>) (value == null
                ? builder.nullLiteral(Boolean.class) : builder.literal(value));
        // A Boolean value supplies its SQL type even in NULLIF's first argument
        // or CASE result branches, where an untyped bind marker is insufficient.
        ((ConstantExpression) expression.getCurrentNode()).setCanBind(false);
        return expression;
    }

    /** Give a nullable scalar a SQL type without changing its value. */
    private static ExpressionImpl<Boolean> typedNullableValue(ExpressionImpl<Boolean> value, CriteriaBuilderImpl builder) {
        ExpressionBuilder valueBase = value.getCurrentNode().getBuilder();
        ExpressionImpl<Boolean> falseValue = booleanLiteral(Boolean.FALSE, builder);
        Predicate alwaysTrue = builder.equal(booleanLiteral(Boolean.TRUE, builder), booleanLiteral(Boolean.TRUE, builder));
        ExpressionImpl<Boolean> result = (ExpressionImpl<Boolean>) builder.<Boolean>selectCase()
                .when(alwaysTrue, value).otherwise(falseValue);
        // Only newly created literals or cloned parameter nodes reach this method.
        value.getCurrentNode().setLocalBase(valueBase);
        falseValue.getCurrentNode().setLocalBase(valueBase);
        return result;
    }

    /**
     * Translate a selected predicate into a scalar SQL value without replacing
     * the caller's Selection, its alias, or its parameter/root traversal.
     */
    static org.eclipse.persistence.expressions.Expression selectionNode(Selection<?> selection) {
        org.eclipse.persistence.expressions.Expression node = ((InternalSelection) selection).getCurrentNode();
        if (selection instanceof ExpressionImpl<?> expression
                && (selection.getJavaType() == Boolean.class || selection.getJavaType() == boolean.class)
                && (selection instanceof Predicate || expression.isLiteral() || node.isFunctionExpression())) {
            @SuppressWarnings("unchecked")
            ExpressionImpl<Boolean> value = (ExpressionImpl<Boolean>) expression;
            if (selection instanceof Predicate || expression.isLiteral()) {
                CriteriaBuilderImpl builder = new CriteriaBuilderImpl(expression.metamodel);
                ExpressionImpl<Boolean> scalar = booleanValue(value, builder);
                node = expression.isLiteral() && expression.literal == null
                        ? typedNullableValue(scalar, builder).getCurrentNode() : scalar.getCurrentNode();
            }
            // Constants do not carry a typed database field, and a function may
            // fold to a constant. Parentheses preserve the scalar value while
            // retaining Boolean result typing for JDBC extraction. Only actual
            // predicates need a CASE conversion; constant junctions are literals.
            FunctionExpression result = (FunctionExpression) node.getFunction("");
            result.setResultType(Boolean.class);
            return result;
        }
        return node;
    }

    /** Normalize the new scalar wrapper only when it is used as a restriction. */
    static Expression<Boolean> restriction(Expression<Boolean> expression) {
        return expression instanceof BooleanExpressionImpl value ? value.builder().isTrue(value) : expression;
    }

    @Override
    public boolean isExpression() {
        return true;
    }

    @Override
    public boolean isCompoundExpression() {
        return false;
    }

    @Override
    public Predicate and(Expression<Boolean> y) {
        return builder().and(this, y);
    }

    @Override
    public Predicate or(Expression<Boolean> y) {
        return builder().or(this, y);
    }

    @Override
    public Predicate not() {
        // Numeric and character mappings need a comparison, rather than NOT column.
        return builder().isTrue(this).not();
    }

    @Override
    public BooleanExpression coalesce(Expression<? extends Boolean> y) {
        CriteriaBuilderImpl builder = builder();
        return new BooleanExpressionImpl((ExpressionImpl<Boolean>) builder.coalesce(booleanValue(this, builder), booleanValue(y, builder)), true);
    }

    @Override
    public BooleanExpression coalesce(Boolean y) {
        return coalesce(value(y));
    }

    @Override
    public BooleanExpression nullif(Expression<? extends Boolean> y) {
        CriteriaBuilderImpl builder = builder();
        return new BooleanExpressionImpl((ExpressionImpl<Boolean>) builder.nullif(booleanValue(this, builder), booleanValue(y, builder)), true);
    }

    @Override
    public BooleanExpression nullif(Boolean y) {
        return nullif(value(y));
    }
}
