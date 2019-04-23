/*
 * Copyright (c) 1998, 2019 Oracle and/or its affiliates. All rights reserved.
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
//     04/16/2019-3.0 Tomas Kraus
//       - 545940: Delayed parameter transformations
package org.eclipse.persistence.queries.transform;

import java.util.Map;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.internal.databaseaccess.DatasourceCall;
import org.eclipse.persistence.internal.expressions.ConstantExpression;
import org.eclipse.persistence.internal.expressions.ParameterExpression;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.queries.DatasourceCallQueryMechanism;
import org.eclipse.persistence.queries.DatabaseQuery;

/**
 * JPQL LIKE expression pattern parameter transformation.
 * Calls transformation registered as functional interface on LIKE pattern argument using escape character provided
 * as part of LIKE expression.
 * Source of transformation implementation is database plaform.
 */
public class LikePatternTransformation extends ParameterTransformation {

    /**
     * Lambda/functional interface for transformation method.
     */
    public static interface Function {
        /**
         * JPQL LIKE expression pattern definition contains only two wild cards:
         * '%' for any sequence of characters and '_' for any single character.
         * Any other characters with special meaning must be escaped if specific
         * database supports them.
         *
         * @param pattern source pattern to be escaped
         * @param escapeChar character used to escape pattern, first character from provided {@code String} is used
         * @return pattern with other characters with special meaning escaped.
         *         Default implementation does not recognize any characters to
         *         be escaped so unmodified pattern {@code String} is returned.
         */
        public String transform(final String pattern, final String escapeChar);
    }

    /** Escape character expression. Can be ConstantExpression or ParameterExpression. */
    private final Expression escape;

    /** Pattern expression. Can be ConstantExpression or ParameterExpression. */
    private final Expression pattern;

    /** Parameters list index of pattern value in DatasourceCall for ConstantExpression to allow modify it.
     *  Value of {@code -1} means that this value is not defined. */
    private final int patternIndex;

    /** Pattern transformation function. */
    private final Function transformation;

    /**
     * Creates an instance of JPQL LIKE expression pattern parameter transformation.
     * Constructor with parameters list index in DatasourceCall is required for ConstantExpression.
     * @param pattern source pattern expression
     * @param patternIndex parameters list index of pattern value in DatasourceCall
     * @param escape expression with escape character
     * @param transformation transformation method from database platform
     */
    public LikePatternTransformation(Expression pattern, int patternIndex, Expression escape, Function transformation) {
        this.pattern = pattern;
        this.patternIndex = patternIndex;
        this.escape = escape;
        this.transformation = transformation;
    }

    /**
     * Creates an instance of JPQL LIKE expression pattern parameter transformation.
     * Constructor without parameters list index in DatasourceCall is required for ParameterExpression.
     * Parameters list index of pattern value in DatasourceCall  is set to -1.
     * @param pattern source pattern expression
     * @param escape expression with escape character
     * @param transformation transformation method from database platform
     */
    public LikePatternTransformation(Expression pattern, Expression escape, Function transformation) {
        this(pattern, -1, escape, transformation);
    }

    /**
     * Retrieve escape character from escape character expression and provided parameter values.
     * @param paramValues query parameters values {@link Map}
     * @return escape character from escape character expression or {@code null} if no such value exists
     */
    private String escapeString(Map<String, Object> paramValues) {
        String escapeString = null;
        if (escape.isConstantExpression()) {
            escapeString = ((ConstantExpression)escape).getValue().toString();
        }
        if (escape.isParameterExpression()) {
            DatabaseField field = ((ParameterExpression)escape).getField();
            if (field != null) {
                Object escValue = paramValues.get(field.getName());
                if (escValue != null) {
                    escapeString = escValue.toString();
                }
            }
        }
        return escapeString;
    }

    /**
     * Transform parameter value from {@link org.eclipse.persistence.internal.expressions.ParameterExpression}.
     * @param paramName name of query parameter to transform
     * @param paramValues {@link Map} of parameter values
     * @param query query containing parameters to transform
     * @return transformed parameter value.
     */
    @Override
    public Object transformParam(String paramName, Map<String, Object> paramValues, DatabaseQuery query) {
        String escapeString = escapeString(paramValues);
        return transformation.transform(paramValues.get(paramName).toString(), escapeString);
    }

    /**
     * Transform constant values from {@link org.eclipse.persistence.internal.expressions.ConstantExpression}.
     * Transformation will update constant values directlry in {@link org.eclipse.persistence.queries.SQLCall} instance
     * @param paramValues {@link Map} of parameter values
     * @param query query containing parameters to transform
     */
    @Override
    public void transformConstant(Map<String, Object> paramValues, DatabaseQuery query) {
        DatasourceCall call = ((DatasourceCallQueryMechanism) query.getQueryMechanism()).getCall();
        String escapeString = escapeString(paramValues);
        String value = ((ConstantExpression) pattern).getValue().toString();
        String transformedvalue = transformation.transform(value, escapeString);
        call.getParameters().set(patternIndex, transformedvalue);
    }

    /**
     * Check whether pattern expression is ConstantExpression.
     * @return value of {@code true} when pattern expression is an instance of ConstantExpression
     *         or {@code false} otherwise.
     */
    @Override
    public boolean isConstantExpression() {
        return pattern.isConstantExpression();
    }

    /**
     * Check whether pattern expression is ParameterExpression.
     * @return value of {@code true} when pattern expression is an instance of ParameterExpression
     *         or {@code false} otherwise.
     */
    @Override
    public boolean isParameterExpression() {
        return pattern.isParameterExpression();
    }

    /**
     * Get name of parameter in ParameterExpression.
     * @return name of parameter of parameter expression is an instance of ParameterExpression
     *         or {@code null} otherwise.
     */
    @Override
    public String getParameterName() {
        if (isParameterExpression()) {
            DatabaseField field = ((ParameterExpression)pattern).getField();
            return field != null ? field.getName() : null;
        } else {
            return null;
        }
    }

}
