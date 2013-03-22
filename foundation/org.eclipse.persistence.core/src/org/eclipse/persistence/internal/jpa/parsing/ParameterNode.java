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
package org.eclipse.persistence.internal.jpa.parsing;

import org.eclipse.persistence.expressions.*;

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
     * @param newVariableName java.lang.String
     */
    public ParameterNode(String newParameterName) {
        setParameterName(newParameterName);
    }

    /**
     * INTERNAL 
     */
    public void validateParameter(ParseTreeContext context, Object contextType) {
        context.defineParameterType(name, contextType, getLine(), getColumn());
        setType(context.getParameterType(name));
    }

    /**
     * Generate the result expression, must use the base builder
     * to avoid getting multiple builders.
     */
    public Expression generateExpression(GenerationContext context) {
        Expression whereClause = context.getBaseExpression().getParameter(getParameterName(), getType());
        return whereClause;
    }

    /**
     * INTERNAL
     * Return the parameterName
     */
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
    public boolean isParameterNode() {
        return true;
    }

}
