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

import org.eclipse.persistence.queries.DatabaseQuery;

/**
 * SQL parameters delayed transformation.
 */
public abstract class ParameterTransformation {

    /**
     * Transform parameter value from {@link org.eclipse.persistence.internal.expressions.ParameterExpression}.
     * @param paramName name of query parameter to transform
     * @param paramValues {@link Map} of parameter values
     * @param query query containing parameters to transform
     * @return transformed parameter value.
     */
    public abstract Object transformParam(String name, Map<String, Object> paramValues, DatabaseQuery query);

    /**
     * Transform constant values from {@link org.eclipse.persistence.internal.expressions.ConstantExpression}.
     * Transformation will update constant values directlry in {@link org.eclipse.persistence.queries.SQLCall} instance
     * @param paramValues {@link Map} of parameter values
     * @param query query containing parameters to transform
     */
    public abstract void transformConstant(Map<String, Object> paramValues, DatabaseQuery query);

    /**
     * Check whether pattern expression is ConstantExpression.
     * @return value of {@code true} when pattern expression is an instance of ConstantExpression
     *         or {@code false} otherwise.
     */
    public abstract boolean isConstantExpression();

    /**
     * Check whether pattern expression is ParameterExpression.
     * @return value of {@code true} when pattern expression is an instance of ParameterExpression
     *         or {@code false} otherwise.
     */
    public abstract boolean isParameterExpression();

    /**
     * Get name of parameter in ParameterExpression.
     * @return name of parameter of parameter expression is an instance of ParameterExpression
     *         or {@code null} otherwise.
     */
    public abstract String getParameterName();


}
