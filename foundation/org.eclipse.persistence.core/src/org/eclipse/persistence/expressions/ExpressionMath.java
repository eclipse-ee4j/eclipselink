/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.expressions;

import org.eclipse.persistence.internal.helper.ClassConstants;

/**
 * <p>
 * <b>Purpose</b>: This class mirrors the java.lang.Math class to allow mathimetical function support within expressions.</p>
 * <p>Example:
 * <pre><blockquote>
 *  ExpressionBuilder builder = new ExpressionBuilder();
 *  Expression poorAndRich = ExpressionMath.abs(builder.get("netWorth")).greaterThan(1000000);
 *  session.readAllObjects(Company.class, poorAndRich);
 * </blockquote></pre></p>
 */
public class ExpressionMath {

    /**
     * PUBLIC:
     * Return a new expression that applies the function to the given expression.
     * <p>Example:
     * <pre><blockquote>
     *  Example: ExpressionMath.abs(builder.get("netWorth")).greaterThan(1000000);
     * </blockquote></pre>
     */
    public static Expression abs(Expression expression) {
        ExpressionOperator anOperator = expression.getOperator(ExpressionOperator.Abs);
        return anOperator.expressionFor(expression);
    }

    /**
     * PUBLIC:
     * Return a new expression that applies the function to the given expression.
     */
    public static Expression acos(Expression expression) {
        ExpressionOperator anOperator = expression.getOperator(ExpressionOperator.Acos);
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
        ExpressionOperator anOperator = right.getOperator(ExpressionOperator.Add);
        return anOperator.expressionFor(right, left);
    }

    /**
     * PUBLIC:
     * Return a new expression that applies the function to the given expression.
     */
    public static Expression asin(Expression expression) {
        ExpressionOperator anOperator = expression.getOperator(ExpressionOperator.Asin);
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
        ExpressionOperator anOperator = expression.getOperator(ExpressionOperator.Atan2);
        return anOperator.expressionFor(expression, value);
    }

    /**
     * PUBLIC:
     * Return a new expression that applies the function to the given expression.
     */
    public static Expression atan2(Expression expression1, Expression expression2) {
        ExpressionOperator anOperator = expression1.getOperator(ExpressionOperator.Atan2);
        return anOperator.expressionFor(expression1, expression2);
    }

    /**
     * PUBLIC:
     * Return a new expression that applies the function to the given expression.
     */
    public static Expression ceil(Expression expression) {
        ExpressionOperator anOperator = expression.getOperator(ExpressionOperator.Ceil);
        return anOperator.expressionFor(expression);
    }

    /**
     * PUBLIC:
     * Return a new expression that applies the function to the given expression.
     */
    public static Expression chr(Expression expression) {
        ExpressionOperator anOperator = expression.getOperator(ExpressionOperator.Chr);
        return anOperator.expressionFor(expression);
    }

    /**
     * PUBLIC:
     * Return a new expression that applies the function to the given expression.
     */
    public static Expression cos(Expression expression) {
        ExpressionOperator anOperator = expression.getOperator(ExpressionOperator.Cos);
        return anOperator.expressionFor(expression);
    }

    /**
     * PUBLIC:
     * Return a new expression that applies the function to the given expression.

     */
    public static Expression cosh(Expression expression) {
        ExpressionOperator anOperator = expression.getOperator(ExpressionOperator.Cosh);
        return anOperator.expressionFor(expression);
    }

    /**
     * PUBLIC:
     * Return a new expression that applies the function to the given expression.
     */
    public static Expression cot(Expression expression) {
        ExpressionOperator anOperator = expression.getOperator(ExpressionOperator.Cot);
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
        ExpressionOperator anOperator = left.getOperator(ExpressionOperator.Divide);
        return anOperator.expressionFor(left, right);
    }

    /**
     * PUBLIC:
     * Return a new expression that applies the function to the given expression.
     */
    public static Expression exp(Expression expression) {
        ExpressionOperator anOperator = expression.getOperator(ExpressionOperator.Exp);
        return anOperator.expressionFor(expression);
    }

    /**
     * PUBLIC:
     * Return a new expression that applies the function to the given expression.
     */
    public static Expression floor(Expression expression) {
        ExpressionOperator anOperator = expression.getOperator(ExpressionOperator.Floor);
        return anOperator.expressionFor(expression);
    }

    /**
     * INTERNAL:
     * Return the operator.
     */
    public static ExpressionOperator getOperator(int selector) {
        ExpressionOperator result = ExpressionOperator.getOperator(Integer.valueOf(selector));
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
        ExpressionOperator anOperator = expression.getOperator(ExpressionOperator.Ln);
        return anOperator.expressionFor(expression);
    }

    /**
     * PUBLIC:
     * Return a new expression that applies the function to the given expression.
     */
    public static Expression log(Expression expression) {
        ExpressionOperator anOperator = expression.getOperator(ExpressionOperator.Log);
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
        ExpressionOperator anOperator = left.getOperator(ExpressionOperator.Greatest);
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
        ExpressionOperator anOperator = left.getOperator(ExpressionOperator.Least);
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
        ExpressionOperator anOperator = expression.getOperator(ExpressionOperator.Mod);
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
        ExpressionOperator anOperator = left.getOperator(ExpressionOperator.Multiply);
        return anOperator.expressionFor(left, right);
    }

    /**
     * PUBLIC:
     * Return a new expression that applies the function to the given expression.
     */
    public static Expression negate(Expression expression) {
        ExpressionOperator anOperator = expression.getOperator(ExpressionOperator.Negate);
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
        ExpressionOperator anOperator = expression.getOperator(ExpressionOperator.Power);
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
        ExpressionOperator anOperator = expression.getOperator(ExpressionOperator.Round);
        return anOperator.expressionFor(expression, decimalPlaces);
    }

    /**
     * PUBLIC:
     * Return a new expression that applies the function to the given expression.
     */
    public static Expression sign(Expression expression) {
        ExpressionOperator anOperator = expression.getOperator(ExpressionOperator.Sign);
        return anOperator.expressionFor(expression);
    }

    /**
     * PUBLIC:
     * Return a new expression that applies the function to the given expression.
     */
    public static Expression sin(Expression expression) {
        ExpressionOperator anOperator = expression.getOperator(ExpressionOperator.Sin);
        return anOperator.expressionFor(expression);
    }

    /**
     * PUBLIC:
     * Return a new expression that applies the function to the given expression.
     */
    public static Expression sinh(Expression expression) {
        ExpressionOperator anOperator = expression.getOperator(ExpressionOperator.Sinh);
        return anOperator.expressionFor(expression);
    }

    /**
     * PUBLIC:
     * Return a new expression that applies the function to the given expression.
     */
    public static Expression sqrt(Expression expression) {
        ExpressionOperator anOperator = expression.getOperator(ExpressionOperator.Sqrt);
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
        ExpressionOperator anOperator = left.getOperator(ExpressionOperator.Subtract);
        return anOperator.expressionFor(left, right);
    }

    /**
     * PUBLIC:
     * Return a new expression that applies the function to the given expression.
     */
    public static Expression tan(Expression expression) {
        ExpressionOperator anOperator = expression.getOperator(ExpressionOperator.Tan);
        return anOperator.expressionFor(expression);
    }

    /**
     * PUBLIC:
     * Return a new expression that applies the function to the given expression.
     */
    public static Expression tanh(Expression expression) {
        ExpressionOperator anOperator = expression.getOperator(ExpressionOperator.Tanh);
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
        ExpressionOperator anOperator = expression.getOperator(ExpressionOperator.Trunc);
        return anOperator.expressionFor(expression, decimalPlaces);
    }
}
