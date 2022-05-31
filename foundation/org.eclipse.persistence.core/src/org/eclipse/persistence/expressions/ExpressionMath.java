/*
 * Copyright (c) 1998, 2022 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2022 IBM Corporation. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.expressions;

import org.eclipse.persistence.internal.helper.ClassConstants;

/**
 * <p>
 * <b>Purpose</b>: This class mirrors the java.lang.Math class to allow mathimetical function support within expressions.</p>
 * <p>Example:
 * <blockquote><pre>
 *  ExpressionBuilder builder = new ExpressionBuilder();
 *  Expression poorAndRich = ExpressionMath.abs(builder.get("netWorth")).greaterThan(1000000);
 *  session.readAllObjects(Company.class, poorAndRich);
 * </pre></blockquote>
 */
public class ExpressionMath {

    /**
     * PUBLIC:
     * Return a new expression that applies the function to the given expression.
     * <p>Example:
     * <blockquote><pre>
     *  Example: ExpressionMath.abs(builder.get("netWorth")).greaterThan(1000000);
     * </pre></blockquote>
     */
    public static Expression abs(Expression expression) {
        ExpressionOperator anOperator = Expression.getOperator(ExpressionOperator.Abs);
        return anOperator.expressionFor(expression);
    }

    /**
     * PUBLIC:
     * Return a new expression that applies the function to the given expression.
     */
    public static Expression acos(Expression expression) {
        ExpressionOperator anOperator = Expression.getOperator(ExpressionOperator.Acos);
        return anOperator.expressionFor(expression);
    }

    /**
     * PUBLIC:
     * Return a new expression that applies the function to the given expression.
     */
    public static Expression add(Expression left, int right) {
        return add(left, Integer.valueOf(right));
    }

    /**
     * PUBLIC:
     * Return a new expression that applies the function to the given expression.
     */
    public static Expression add(Expression right, Object left) {
        ExpressionOperator anOperator = Expression.getOperator(ExpressionOperator.Add);
        return anOperator.expressionFor(right, left);
    }

    /**
     * PUBLIC:
     * Return a new expression that applies the function to the given expression.
     */
    public static Expression asin(Expression expression) {
        ExpressionOperator anOperator = Expression.getOperator(ExpressionOperator.Asin);
        return anOperator.expressionFor(expression);
    }

    /**
     * PUBLIC:
     * Return a new expression that applies the function to the given expression.
     */
    public static Expression atan(Expression expression) {
        ExpressionOperator anOperator = expression.getOperator(ExpressionOperator.Atan);
        return anOperator.expressionFor(expression);
    }

    /**
     * PUBLIC:
     * Return a new expression that applies the function to the given expression.
     */
    public static Expression atan2(Expression expression, int value) {
        return atan2(expression, Integer.valueOf(value));
    }

    /**
     * PUBLIC:
     * Return a new expression that applies the function to the given expression.
     */
    public static Expression atan2(Expression expression, Object value) {
        ExpressionOperator anOperator = Expression.getOperator(ExpressionOperator.Atan2);
        return anOperator.expressionFor(expression, value);
    }

    /**
     * PUBLIC:
     * Return a new expression that applies the function to the given expression.
     */
    public static Expression atan2(Expression expression1, Expression expression2) {
        ExpressionOperator anOperator = Expression.getOperator(ExpressionOperator.Atan2);
        return anOperator.expressionFor(expression1, expression2);
    }

    /**
     * PUBLIC:
     * Return a new expression that applies the function to the given expression.
     */
    public static Expression ceil(Expression expression) {
        ExpressionOperator anOperator = Expression.getOperator(ExpressionOperator.Ceil);
        return anOperator.expressionFor(expression);
    }

    /**
     * PUBLIC:
     * Return a new expression that applies the function to the given expression.
     */
    public static Expression chr(Expression expression) {
        ExpressionOperator anOperator = Expression.getOperator(ExpressionOperator.Chr);
        return anOperator.expressionFor(expression);
    }

    /**
     * PUBLIC:
     * Return a new expression that applies the function to the given expression.
     */
    public static Expression cos(Expression expression) {
        ExpressionOperator anOperator = Expression.getOperator(ExpressionOperator.Cos);
        return anOperator.expressionFor(expression);
    }

    /**
     * PUBLIC:
     * Return a new expression that applies the function to the given expression.

     */
    public static Expression cosh(Expression expression) {
        ExpressionOperator anOperator = Expression.getOperator(ExpressionOperator.Cosh);
        return anOperator.expressionFor(expression);
    }

    /**
     * PUBLIC:
     * Return a new expression that applies the function to the given expression.
     */
    public static Expression cot(Expression expression) {
        ExpressionOperator anOperator = Expression.getOperator(ExpressionOperator.Cot);
        return anOperator.expressionFor(expression);
    }

    /**
     * PUBLIC:
     * Return a new expression that applies the function to the given expression.
     */
    public static Expression divide(Expression left, int right) {
        return divide(left, Integer.valueOf(right));
    }

    /**
     * PUBLIC:
     * Return a new expression that applies the function to the given expression.
     */
    public static Expression divide(Expression left, Object right) {
        ExpressionOperator anOperator = Expression.getOperator(ExpressionOperator.Divide);
        return anOperator.expressionFor(left, right);
    }

    /**
     * PUBLIC:
     * Return a new expression that applies the function to the given expression.
     */
    public static Expression exp(Expression expression) {
        ExpressionOperator anOperator = Expression.getOperator(ExpressionOperator.Exp);
        return anOperator.expressionFor(expression);
    }

    /**
     * PUBLIC:
     * Return a new expression that applies the function to the given expression.
     */
    public static Expression floor(Expression expression) {
        ExpressionOperator anOperator = Expression.getOperator(ExpressionOperator.Floor);
        return anOperator.expressionFor(expression);
    }

    /**
     * INTERNAL:
     * Return the operator.
     */
    public static ExpressionOperator getOperator(int selector) {
        ExpressionOperator result = Expression.getOperator(Integer.valueOf(selector));
        if (result != null) {
            return result;
        }

        // Make a temporary operator which we expect the platform
        // to supply later.
        result = new ExpressionOperator();
        result.setSelector(selector);
        result.setNodeClass(ClassConstants.FunctionExpression_Class);
        return result;
    }

    /**
     * PUBLIC:
     * Return a new expression that applies the function to the given expression.
     */
    public static Expression ln(Expression expression) {
        ExpressionOperator anOperator = Expression.getOperator(ExpressionOperator.Ln);
        return anOperator.expressionFor(expression);
    }

    /**
     * PUBLIC:
     * Return a new expression that applies the function to the given expression.
     */
    public static Expression log(Expression expression) {
        ExpressionOperator anOperator = Expression.getOperator(ExpressionOperator.Log);
        return anOperator.expressionFor(expression);
    }

    /**
     * PUBLIC:
     * Return a new expression that applies the function to the given expression.
     */
    public static Expression max(Expression left, int right) {
        return max(left, Integer.valueOf(right));
    }

    /**
     * PUBLIC:
     * Return a new expression that applies the function to the given expression.
     */
    public static Expression max(Expression left, Object right) {
        ExpressionOperator anOperator = Expression.getOperator(ExpressionOperator.Greatest);
        return anOperator.expressionFor(left, right);
    }

    /**
     * PUBLIC:
     * Return a new expression that applies the function to the given expression.
     */
    public static Expression min(Expression left, int right) {
        return min(left, Integer.valueOf(right));
    }

    /**
     * PUBLIC:
     * Return a new expression that applies the function to the given expression.
     */
    public static Expression min(Expression left, Object right) {
        ExpressionOperator anOperator = Expression.getOperator(ExpressionOperator.Least);
        return anOperator.expressionFor(left, right);
    }

    /**
     * PUBLIC:
     * Return a new expression that applies the function to the given expression.
     */
    public static Expression mod(Expression expression, int base) {
        return mod(expression, Integer.valueOf(base));
    }

    /**
     * PUBLIC:
     * Return a new expression that applies the function to the given expression.
     */
    public static Expression mod(Expression expression, Object base) {
        ExpressionOperator anOperator = Expression.getOperator(ExpressionOperator.Mod);
        return anOperator.expressionFor(expression, base);
    }

    /**
     * PUBLIC:
     * Return a new expression that applies the function to the given expression.
     */
    public static Expression multiply(Expression left, int right) {
        return multiply(left, Integer.valueOf(right));
    }

    /**
     * PUBLIC:
     * Return a new expression that applies the function to the given expression.
     */
    public static Expression multiply(Expression left, Object right) {
        ExpressionOperator anOperator = Expression.getOperator(ExpressionOperator.Multiply);
        return anOperator.expressionFor(left, right);
    }

    /**
     * PUBLIC:
     * Return a new expression that applies the function to the given expression.
     */
    public static Expression negate(Expression expression) {
        ExpressionOperator anOperator = Expression.getOperator(ExpressionOperator.Negate);
        return anOperator.expressionFor(expression);
    }

    /**
     * PUBLIC:
     * Return a new expression that applies the function to the given expression.
     */
    public static Expression power(Expression expression, int raised) {
        return power(expression, Integer.valueOf(raised));
    }

    /**
     * PUBLIC:
     * Return a new expression that applies the function to the given expression.
     */
    public static Expression power(Expression expression, Object raised) {
        ExpressionOperator anOperator = Expression.getOperator(ExpressionOperator.Power);
        return anOperator.expressionFor(expression, raised);
    }

    /**
     * PUBLIC:
     * Return a new expression that applies the function to the given expression.
     */
    public static Expression round(Expression expression, int decimalPlaces) {
        return round(expression, Integer.valueOf(decimalPlaces));
    }

    /**
     * PUBLIC:
     * Return a new expression that applies the function to the given expression.
     */
    public static Expression round(Expression expression, Object decimalPlaces) {
        ExpressionOperator anOperator = Expression.getOperator(ExpressionOperator.Round);
        return anOperator.expressionFor(expression, decimalPlaces);
    }

    /**
     * PUBLIC:
     * Return a new expression that applies the function to the given expression.
     */
    public static Expression sign(Expression expression) {
        ExpressionOperator anOperator = Expression.getOperator(ExpressionOperator.Sign);
        return anOperator.expressionFor(expression);
    }

    /**
     * PUBLIC:
     * Return a new expression that applies the function to the given expression.
     */
    public static Expression sin(Expression expression) {
        ExpressionOperator anOperator = Expression.getOperator(ExpressionOperator.Sin);
        return anOperator.expressionFor(expression);
    }

    /**
     * PUBLIC:
     * Return a new expression that applies the function to the given expression.
     */
    public static Expression sinh(Expression expression) {
        ExpressionOperator anOperator = Expression.getOperator(ExpressionOperator.Sinh);
        return anOperator.expressionFor(expression);
    }

    /**
     * PUBLIC:
     * Return a new expression that applies the function to the given expression.
     */
    public static Expression sqrt(Expression expression) {
        ExpressionOperator anOperator = Expression.getOperator(ExpressionOperator.Sqrt);
        return anOperator.expressionFor(expression);
    }

    /**
     * PUBLIC:
     * Return a new expression that applies the function to the given expression.
     */
    public static Expression subtract(Expression left, int right) {
        return subtract(left, Integer.valueOf(right));
    }

    /**
     * PUBLIC:
     * Return a new expression that applies the function to the given expression.
     */
    public static Expression subtract(Expression left, Object right) {
        ExpressionOperator anOperator = Expression.getOperator(ExpressionOperator.Subtract);
        return anOperator.expressionFor(left, right);
    }

    /**
     * PUBLIC:
     * Return a new expression that applies the function to the given expression.
     */
    public static Expression tan(Expression expression) {
        ExpressionOperator anOperator = Expression.getOperator(ExpressionOperator.Tan);
        return anOperator.expressionFor(expression);
    }

    /**
     * PUBLIC:
     * Return a new expression that applies the function to the given expression.
     */
    public static Expression tanh(Expression expression) {
        ExpressionOperator anOperator = Expression.getOperator(ExpressionOperator.Tanh);
        return anOperator.expressionFor(expression);
    }

    /**
     * PUBLIC:
     * Return a new expression that applies the function to the given expression.
     */
    public static Expression trunc(Expression expression, int decimalPlaces) {
        return trunc(expression, Integer.valueOf(decimalPlaces));
    }

    /**
     * PUBLIC:
     * Return a new expression that applies the function to the given expression.
     */
    public static Expression trunc(Expression expression, Object decimalPlaces) {
        ExpressionOperator anOperator = Expression.getOperator(ExpressionOperator.Trunc);
        return anOperator.expressionFor(expression, decimalPlaces);
    }
}
