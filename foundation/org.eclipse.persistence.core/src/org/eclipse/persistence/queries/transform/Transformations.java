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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.queries.DatabaseQueryMechanism;
import org.eclipse.persistence.queries.DatabaseQuery;

/**
 * SQL parameters delayed transformation container.
 */
public class Transformations {

    /** List of registered transformations for constant expressions. */
    private final List<ParameterTransformation> constant;

    /** List of registered transformations for parameter expressions. */
    private final Map<String, ParameterTransformation> parameter;

    /** Query containing parameters to transform. Must be set before executing the transformations (for both constant
     *  and parameter expressions). */
    protected DatabaseQuery query;

    /** Map of parameter values. Must be set before executing the transformations (for both constant
     *  and parameter expressions). */
    private Map<String, Object> paramValues;

    /**
     * Creates an instance of SQL parameters delayed transformation container.
     */
    public Transformations() {
        constant = new LinkedList<>();
        parameter = new HashMap<>();
        paramValues = null;
        query = null;
    }

    /**
     * Set query containing parameters to transform.
     * This setter must be called before executing any transformations.
     * @param query query containing parameters to transform
     */
    public void setQuery(DatabaseQuery query) {
        this.query = query;
    }

    /**
     * Set parameter names and values.
     * This setter must be called before executing any transformations.
     * Size of source lists and order of parameters must be the same in both lists.
     * @param paramFields list of parameter fields.
     * @param paramValuesList list of parameter values.
     */
    public void setParamValues(List<DatabaseField> paramFields, List paramValuesList) {
        int argumentsSize = paramFields.size();
        paramValues = new HashMap<>(argumentsSize);
        for (int index = 0; index < argumentsSize; index++) {
            String paramName = (String)paramFields.get(index).getName();
            paramValues.put(paramName, paramValuesList.get(index));
        }
    }

    /**
     * Checke whether at least one transformation is registered.
     * @return value of {@code true} when at least one transformation is registered or {@code false} otherwise.
     */
    public boolean isEmpty() {
        return constant.isEmpty() && parameter.isEmpty();
    }

    /**
     * Adds a transformation into this container.
     * @param transformation a transformation to be added
     */
    public void add(ParameterTransformation transformation) {
        if (transformation.isConstantExpression()) {
            constant.add(transformation);
        } else {
            parameter.put(transformation.getParameterName(), transformation);
        }
    }

    /**
     * Process all registered constant transformations.
     * All processed transformations are removed from the transformations {@link List}.
     */
    public void transformConstants() {
        DatabaseQueryMechanism queryMechanism = query.getQueryMechanism();
        if (queryMechanism != null && (
                queryMechanism.isCallQueryMechanism() || queryMechanism.isStatementQueryMechanism()
                || queryMechanism.isJPQLCallQueryMechanism())) {
            for (ParameterTransformation transformation : constant) {
                transformation.transformConstant(paramValues, query);
            }
        }
        // This helps garbage collector too.
        constant.clear();
    }

    /**
     * Process registered parameters transformation for specific parameter.
     * Transformation is removed from transformations {@link Map}.
     */
    public Object transformParam(String paramName) {
        ParameterTransformation transformation = parameter.remove(paramName);
        if (transformation != null) {
            return transformation.transformParam(paramName, paramValues, query);
        }
        return paramValues.get(paramName);
    }

}
