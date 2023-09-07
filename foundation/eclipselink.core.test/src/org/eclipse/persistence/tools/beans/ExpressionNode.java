/*
 * Copyright (c) 1998, 2023 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.tools.beans;

import java.util.*;

import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.internal.expressions.*;

/**
 * Used for the tree view within expression editor.
 */
public class ExpressionNode {
    protected static String Equals = "(=) Equals";
    protected static String NotEquals = "(!=) Not Equals";
    protected static String LessThan = "(<) Less Than";
    protected static String LessThanEqual = "(<=) Less Than Equal";
    protected static String GreaterThan = "(>) Greater Than";
    protected static String GreaterThanEqual = "(>=) Greater Than Equal";
    protected static String Like = "Like";
    protected static String NotLike = "Not Like";
    protected static String In = "In";
    protected static String NotIn = "Not In";
    protected static String Between = "Between";
    protected static String NotBetween = "Not Between";
    protected static String Or = "Or";
    protected static String And = "And";
    protected static String Not = "Not";
    protected static String Upper = "Upper Case";
    protected static String Lower = "Lower Case";
    protected static String KeyWordAll = "All Key Words";
    protected static String KeyWordAny = "Any Key Words";
    protected static Hashtable operators;
    protected static Hashtable methods;
    protected Expression expression;

    public ExpressionNode(Expression expression) {
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }

    public static String getMethod(String method) {
        return (String)getMethods().get(method);
    }

    public static Hashtable getMethods() {
        if (methods == null) {
            methods = new Hashtable();
            methods.put(Equals, "equal");
            methods.put(NotEquals, "notEqual");
            methods.put(LessThan, "lessThan");
            methods.put(LessThanEqual, "lessThanEqual");
            methods.put(GreaterThan, "greaterThan");
            methods.put(GreaterThanEqual, "greaterThanEqual");
            methods.put(Like, "like");
            methods.put(NotLike, "notLike");
            methods.put(In, "in");
            methods.put(NotIn, "notIn");
            methods.put(Between, "between");
            methods.put(NotBetween, "notBetween");
            methods.put(Or, "or");
            methods.put(And, "and");
            methods.put(Not, "not");
            methods.put(Lower, "toLowerCase");
            methods.put(Upper, "toUpperCase");
            methods.put(KeyWordAny, "containsAnyKeyWords");
            methods.put(KeyWordAll, "containsAllKeyWords");
        }

        return methods;
    }

    public static String getOperator(int anOperator) {
        return (String)getOperators().get(Integer.valueOf(anOperator));
    }

    public static Hashtable getOperators() {
        if (operators == null) {
            operators = new Hashtable();
            operators.put(Integer.valueOf(ExpressionOperator.Equal), Equals);
            operators.put(Integer.valueOf(ExpressionOperator.NotEqual), NotEquals);
            operators.put(Integer.valueOf(ExpressionOperator.LessThan), LessThan);
            operators.put(Integer.valueOf(ExpressionOperator.LessThanEqual),
                          LessThanEqual);
            operators.put(Integer.valueOf(ExpressionOperator.GreaterThan),
                          GreaterThan);
            operators.put(Integer.valueOf(ExpressionOperator.GreaterThanEqual),
                          GreaterThanEqual);
            operators.put(Integer.valueOf(ExpressionOperator.Like), Like);
            operators.put(Integer.valueOf(ExpressionOperator.NotLike), NotLike);
            operators.put(Integer.valueOf(ExpressionOperator.In), In);
            operators.put(Integer.valueOf(ExpressionOperator.NotIn), NotIn);
            operators.put(Integer.valueOf(ExpressionOperator.Between), Between);
            operators.put(Integer.valueOf(ExpressionOperator.NotBetween),
                          NotBetween);
            operators.put(Integer.valueOf(ExpressionOperator.Or), Or);
            operators.put(Integer.valueOf(ExpressionOperator.And), And);
            operators.put(Integer.valueOf(ExpressionOperator.Not), Not);
            operators.put(Integer.valueOf(ExpressionOperator.ToLowerCase), Lower);
            operators.put(Integer.valueOf(ExpressionOperator.ToUpperCase), Upper);
        }

        return operators;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    public String toString() {
        if (getExpression() == null) {
            return "True";
        }

        if (getExpression() instanceof ConstantExpression) {
            return ((ConstantExpression)getExpression()).getValue().toString();
        } else if (getExpression() instanceof QueryKeyExpression) {
            return ((QueryKeyExpression)getExpression()).getName().toString();
        } else {
            String anOperator =
                getOperator(getExpression().getOperator().getSelector());
            if (anOperator == null) {
                return getExpression().getOperator().toString();
            } else {
                return anOperator;
            }
        }
    }
}
