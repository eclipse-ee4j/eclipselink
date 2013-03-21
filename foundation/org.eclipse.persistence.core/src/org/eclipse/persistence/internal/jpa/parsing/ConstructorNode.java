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

// Java imports
import java.util.*;

import org.eclipse.persistence.exceptions.JPQLException;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.ReportQuery;

/**
 * INTERNAL
 * <p><b>Purpose</b>: Represent a constructor node (NEW)
 * <p><b>Responsibilities</b>:<ul>
 * <li> Generate the correct expression for a constructor 
 * </ul>
 */
public class ConstructorNode extends Node implements AliasableNode {

    /** The name of the constructor class. */
    private String className = null;
    
    /** The list of constructor call argument nodes */
    public List constructorItems = new ArrayList();

    /**
     * Return a new ConstructorNode
     */
    public ConstructorNode(String className) {
        this.className = className;
    }

    /**
     * INTERNAL
     * Apply this node to the passed query
     */
    public void applyToQuery(ObjectLevelReadQuery theQuery, GenerationContext context) {
        if (theQuery instanceof ReportQuery) {
            SelectGenerationContext selectContext = (SelectGenerationContext)context;
            ReportQuery reportQuery = (ReportQuery)theQuery;
            reportQuery.beginAddingConstructorArguments(
                getConstructorClass(context.getParseTreeContext()));
            for (Iterator i = constructorItems.iterator(); i.hasNext();) {
                Node node = (Node)i.next();
                if (selectingRelationshipField(node, context)) {
                    selectContext.useOuterJoins();
                }
                node.applyToQuery(reportQuery, context);
                selectContext.dontUseOuterJoins();
            }
            reportQuery.endAddingToConstructorItem();
        }
    }
    
    /**
     * INTERNAL
     * Validate node and calculate its type.
     */
    public void validate(ParseTreeContext context) {
        for (Iterator i = constructorItems.iterator(); i.hasNext();) {
            Node item = (Node)i.next();
            item.validate(context);
        }

        // Resolve constructor class
        TypeHelper typeHelper = context.getTypeHelper();
        Object type = typeHelper.resolveTypeName(className);
        if (type == null) {
            String name = className;
            // check for inner classes
            int index = name.lastIndexOf('.');
            if (index != -1) {
                name = name.substring(0, index) + '$' + name.substring(index+1);
                type = typeHelper.resolveTypeName(name);
            }
        }
        setType(type);
    }
    
    /**
     * INTERNAL
     * Is this node a ConstructorNode
     */
    public boolean isConstructorNode() {
        return true;
    }

    /**
     * INTERNAL
     * Add an Order By Item to this node
     */
    public void addConstructorItem(Object theNode) {
        constructorItems.add(theNode);
    }

    /**
     * INTERNAL
     * Set the list of constructor items of this node.
     */
    public void setConstructorItems(List items) {
        this.constructorItems = items;
    }

    /**
     * INTERNAL
     * Get the list of constructor items of this node.
     */
    public List getConstructorItems() {
        return this.constructorItems;
    }

    /**
     * Check the specific constructor class and return its class instance. 
     * @exception JPQLException if the specified constructor class could not
     * be found.
     */
    private Class getConstructorClass(ParseTreeContext context) {
        Object type = getType();
        if (type == null) {
            throw JPQLException.constructorClassNotFound(
                context.getQueryInfo(), getLine(), getColumn(), className);
        }
        return (Class)type;
    }

    /**
     * INTERNAL
     */
    private boolean selectingRelationshipField(Node node, GenerationContext context) {
        if ((node == null) || !node.isDotNode()) {
            return false;
        }

        TypeHelper typeHelper = context.getParseTreeContext().getTypeHelper();
        Node path = node.getLeft();
        AttributeNode attribute = (AttributeNode)node.getRight();
        return typeHelper.isRelationship(path.getType(), 
                                         attribute.getAttributeName());
    }

    /**
     * INTERNAL
     * Get the string representation of this node.
     */
    public String getAsString() {
        StringBuffer repr = new StringBuffer();
        repr.append("NEW ").append(className);
        repr.append("(");
        for (Iterator i = constructorItems.iterator(); i.hasNext();) {
            Node node = (Node)i.next();
            repr.append(node.getAsString());
            if (i.hasNext()) {
                repr.append(", ");
            }
        }
        repr.append(")");
        return repr.toString();
    }
    
    public boolean isAliasableNode(){
        return true;
    }
}
