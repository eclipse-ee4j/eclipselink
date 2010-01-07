/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.JPQLException;
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.mappings.DatabaseMapping;

/**
 * INTERNAL
 * <p><b>Purpose</b>: Represent a attribute.
 *
 * <p><b>Responsibilities</b>:<ul>
 * </ul>
 */
public class AttributeNode extends Node {

    /** The attribute name. */
    private String name;

    /** Flag indicating outer join */
    private boolean outerJoin;

    /** */
    private boolean requiresCollectionAttribute;

    /** */
    private DatabaseMapping mapping;

    /**
     * Create a new AttributeNode
     */
    public AttributeNode() {
        super();
    }

    /**
     * Create a new AttributeNode with the passed name
     * @param name the attribute name
     */
    public AttributeNode(String name) {
        setAttributeName(name);
    }

    /** 
     * INTERNAL 
     * If called this AttributeNode represents an unqualified field access. 
     * The method returns a DotNode representing a qualified field access with
     * the base variable as left child node and the attribute as right child
     * node. 
     */
    public Node qualifyAttributeAccess(ParseTreeContext context) {
        return (Node)context.getNodeFactory().newQualifiedAttribute(
            getLine(), getColumn(), context.getBaseVariable(), name); 
    }
    
    /**
     * INTERNAL
     * Validate the current node and calculates its type.
     */
    public void validate(ParseTreeContext context) {
        // The type is calculated in the parent DotNode.
    }

    /** */
    public Expression addToExpression(Expression parentExpression, GenerationContext context) {
        if (isCollectionAttribute()) {
            //special case for NOT MEMBER OF
            if (context.hasMemberOfNode()) {
                return parentExpression.noneOf(name, new ExpressionBuilder().equal(context.getMemberOfNode().getLeftExpression()));
            }
            return outerJoin ? parentExpression.anyOfAllowingNone(name) : 
                parentExpression.anyOf(name);
        } else {
            // check whether collection attribute is required
            if (requiresCollectionAttribute()) {
                throw JPQLException.invalidCollectionMemberDecl(
                    context.getParseTreeContext().getQueryInfo(), 
                    getLine(), getColumn(), name);
            }

            if (context.shouldUseOuterJoins() || isOuterJoin()) {
                return parentExpression.getAllowingNull(name);
            } else {
                return parentExpression.get(name);
            }
        }
    }

    /**
     * INTERNAL
     * Is this node an AttributeNode
     */
    public boolean isAttributeNode() {
        return true;
    }

    /** */
    public String getAttributeName() {
        return name;
    }

    /** */
    public void setAttributeName(String name) {
        this.name = name;
    }

    /** */
    public boolean isOuterJoin() {
        return outerJoin;
    }

    /** */
    public void setOuterJoin(boolean outerJoin) {
        this.outerJoin = outerJoin;
    }

    /** */
    public boolean requiresCollectionAttribute() {
        return requiresCollectionAttribute;
    }

    /** */
    public void setRequiresCollectionAttribute(boolean requiresCollectionAttribute) {
        this.requiresCollectionAttribute = requiresCollectionAttribute;
    }

    /** */
    public DatabaseMapping getMapping() {
        return mapping;
    }

    /** */
    public void setMapping(DatabaseMapping mapping) {
        this.mapping = mapping;
    }

    /** */
    public boolean isCollectionAttribute() {
        DatabaseMapping mapping = getMapping();
        return (mapping != null) && mapping.isCollectionMapping();
    }

    /**
     * resolveMapping: Answer the mapping which corresponds to my variableName.
     */
    public DatabaseMapping resolveMapping(GenerationContext context, Class ownerClass) {
        ClassDescriptor descriptor = context.getSession().getDescriptor(ownerClass);
        return (descriptor==null) ? null : descriptor.getMappingForAttributeName(getAttributeName());
    }

    /**
     * resolveClass: Answer the class for the mapping associated with the my variableName in the ownerClass.
     * Answer null if the node represents a mapping that doesn't exist
     */
    public Class resolveClass(GenerationContext context, Class ownerClass) {
        DatabaseMapping mapping;

        mapping = resolveMapping(context, ownerClass);

        // if we are working with a direct-to-field, or the mapping's null,
        // return the owner class
        // Returning the ownerClass when the mapping is null delegates error handling
        // to the query rather than me
        if ((mapping == null) || (mapping.isDirectToFieldMapping())) {
            return ownerClass;
        }

        ClassDescriptor descriptor = mapping.getReferenceDescriptor();
        return (descriptor==null) ? null : descriptor.getJavaClass();
        //return mapping.getReferenceDescriptor().getJavaClass();
    }

    public String toString(int indent) {
        StringBuffer buffer = new StringBuffer();
        toStringIndent(indent, buffer);
        buffer.append(toStringDisplayName() + "[" + getAttributeName() + "]");
        return buffer.toString();
    }

    /**
     * INTERNAL
     * Get the string representation of this node.
     */
    public String getAsString() {
        return getAttributeName();
    }
}
