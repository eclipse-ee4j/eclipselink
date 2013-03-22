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

import org.eclipse.persistence.queries.*;
import org.eclipse.persistence.exceptions.*;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.descriptors.ClassDescriptor;

/**
 * INTERNAL:
 * ModifyNode is the superclass for UpdateNode and DeleteNode
 */
public abstract class ModifyNode extends QueryNode {

    private String abstractSchemaIdentifier;
    private String abstractSchemaName;

    /**
     * INTERNAL
     * Apply this node to the passed query.  This node does not change the query.
     */
    public void applyToQuery(DatabaseQuery theQuery, GenerationContext context) {
    }

    /**
     * INTERNAL
     * Validate node and calculate its type.
     */
    public void validate(ParseTreeContext context) {
        // If defined use the abstractSchemaIdentifier as the base variable,
        // otherwise use the abstractSchemaName 
        String baseVariable = getCanonicalAbstractSchemaIdentifier();
        context.setBaseVariable(baseVariable);
        super.validate(context);
    }
    
    /**
     * INTERNAL
     */
    public Expression generateExpression(GenerationContext context) {
        return null;
    }

    /**
     * INTERNAL
     */
    public String getAbstractSchemaName() {
        return abstractSchemaName;
    }

    /**
     * INTERNAL
     */
    public void setAbstractSchemaName(String abstractSchemaName) {
        this.abstractSchemaName = abstractSchemaName;
    }

    /**
     * INTERNAL
     */
    public String getAbstractSchemaIdentifier() {
        return abstractSchemaIdentifier;
    }

    /**
     * INTERNAL
     */
    public void setAbstractSchemaIdentifier(String identifierName) {
        abstractSchemaIdentifier = identifierName;
    }

    /**
     * INTERNAL:
     * Returns the canonical name of abstract schema identifier. 
     * If the identifier is not specified(unqualified attribute scenario),
     * the canonical name of abstract schema is returned. 
     */
    public String getCanonicalAbstractSchemaIdentifier() {
        String variable = abstractSchemaIdentifier != null ?
                abstractSchemaIdentifier : abstractSchemaName;
        return IdentificationVariableDeclNode.calculateCanonicalName(variable);
    }

    /**
     * resolveClass: Answer the class which corresponds to my variableName. This is the class for
     * an alias, where the variableName is registered to an alias.
     */
    public Class resolveClass(GenerationContext context) {
        String alias = abstractSchemaName;
        ClassDescriptor descriptor = context.getSession().getDescriptorForAlias(alias);
        if (descriptor == null) {
            throw JPQLException.entityTypeNotFound2(
                context.getParseTreeContext().getQueryInfo(), 
                getLine(), getColumn(), alias);
        }
        Class theClass = descriptor.getJavaClass();
        if (theClass == null) {
            throw JPQLException.resolutionClassNotFoundException2(
                context.getParseTreeContext().getQueryInfo(), 
                getLine(), getColumn(), alias);
        }
        return theClass;
    }
}
