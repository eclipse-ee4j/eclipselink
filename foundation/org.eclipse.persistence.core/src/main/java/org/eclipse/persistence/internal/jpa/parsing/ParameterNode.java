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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.jpa.parsing;

import org.eclipse.persistence.expressions.Expression;

/**
 * INTERNAL
 * <p><b>Purpose</b>: This node represnts a Parameter (?1) in an EJBQL
 * <p><b>Responsibilities</b>:<ul>
 * <li> Generate the correct expression for an AND in EJBQL
 * <li> Maintain a
 * <li>
 * </ul>
 *    @author Jon Driscoll and Joel Lucuik
 *    @since TopLink 4.0
 */
public class ParameterNode extends Node {

    /** */
    private String name;

    /**
     * Return a new ParameterNode.
     */
    public ParameterNode() {
        super();
    }

    /**
     * INTERNAL
     * Create a new ParameterNode with the passed string.
     * @param newParameterName java.lang.String
     */
    public ParameterNode(String newParameterName) {
        setParameterName(newParameterName);
    }

    /**
     * INTERNAL
     */
    @Override
    public void validateParameter(ParseTreeContext context, Object contextType) {
        context.defineParameterType(name, contextType, getLine(), getColumn());
        setType(context.getParameterType(name));
    }

    /**
     * Generate the result expression, must use the base builder
     * to avoid getting multiple builders.
     */
    @Override
    public Expression generateExpression(GenerationContext context) {
        Expression whereClause = context.getBaseExpression().getParameter(getParameterName(), getType());
        return whereClause;
    }

    /**
     * INTERNAL
     * Return the parameterName
     */
    @Override
    public String getAsString() {
        return getParameterName();
    }

    /**
     * INTERNAL
     * Return the parameter name
     */
    public String getParameterName() {
        return name;
    }

    /** */
    public void setParameterName(String name) {
        this.name = name;
    }

    /**
     * INTERNAL
     * Yes this is a Parameter node
     */
    @Override
    public boolean isParameterNode() {
        return true;
    }

}
