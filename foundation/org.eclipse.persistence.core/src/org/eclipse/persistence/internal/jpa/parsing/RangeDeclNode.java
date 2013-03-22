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

import org.eclipse.persistence.exceptions.JPQLException;

/**
 * INTERNAL
 * <p><b>Purpose</b>: Represent a range identification variable
 * declaration as part of the FROM clause FROM Order o.
 * <p><b>Responsibilities</b>:<ul>
 * <li> Manage the abstract schema name range variable declaration. 
 * </ul>
 */
public class RangeDeclNode extends IdentificationVariableDeclNode {

    private String abstractSchemaName;
    
    /** */
    public String getAbstractSchemaName() {
        return abstractSchemaName;
    }
    
    /** */
    public void setAbstractSchemaName(String name) {
        abstractSchemaName = name;
    }
    
    /** 
     * INTERNAL 
     * Check for an unqualified field access. If abstractSchemaName does not
     * define a valid abstract schema name treat it as unqualified field
     * access. Then method qualifies the field access and use it as the path
     * expression of a new join variable declaration node returned by the
     * method. 
     */
    public Node qualifyAttributeAccess(ParseTreeContext context) {
        TypeHelper typeHelper = context.getTypeHelper();
        String name = abstractSchemaName;
        if (typeHelper.resolveSchema(name) == null) {
            // not a known abstract schema name => make it a join node with a
            // qualified attribute access as path expression 
            context.unregisterVariable(getCanonicalVariableName());
            NodeFactory factory = context.getNodeFactory();
            Node path = (Node)factory.newQualifiedAttribute(
                getLine(), getColumn(), context.getBaseVariable(), name);
            return (Node)factory.newVariableDecl(
                getLine(), getColumn(), path, getVariableName());
        }
        return this;
    }

    /**
     * INTERNAL
     * Validate node and calculate its type.
     */
    public void validate(ParseTreeContext context) {
        super.validate(context);
        TypeHelper typeHelper = context.getTypeHelper();
        Object type = typeHelper.resolveSchema(abstractSchemaName);
        if (type == null) {
            throw JPQLException.entityTypeNotFound2(
                context.getQueryInfo(), getLine(), getColumn(), abstractSchemaName);
        }
        setType(type);
    }
}
