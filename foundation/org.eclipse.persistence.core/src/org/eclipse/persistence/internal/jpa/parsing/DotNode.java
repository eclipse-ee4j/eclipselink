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
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.internal.expressions.*;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.foundation.AbstractDirectMapping;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.ReportQuery;

/**
 * INTERNAL
 * <p><b>Purpose</b>: This node represents an 'DOT' (i.e. '.') on the input
 * stream. The left and right will depend on the input stream.
 * <p><b>Responsibilities</b>:<ul>
 * </ul>
 *    @author Jon Driscoll and Joel Lucuik
 *    @since TopLink 4.0
 */
public class DotNode extends LogicalOperatorNode implements AliasableNode {

    private Object enumConstant;

    /**
     * INTERNAL
     * Apply this node to the passed query
     */
    public void applyToQuery(ObjectLevelReadQuery theQuery, GenerationContext context) {
        if (theQuery.isReportQuery()){
            ReportQuery reportQuery = (ReportQuery)theQuery;
            reportQuery.addAttribute(resolveAttribute(), generateExpression(context));
            reportQuery.dontRetrievePrimaryKeys();
        }
    }

    /** 
     * INTERNAL 
     * Check the left child node for an unqualified field access. The method
     * delegates to the left most expression of multi-navigation path
     * expression. 
     */
    public Node qualifyAttributeAccess(ParseTreeContext context) {
        if (getLeft() != null) {
            setLeft(getLeft().qualifyAttributeAccess(context));
        }
        return this;
    }

    /**
     * INTERNAL
     * Validate node and calculate its type.
     * Check for enum literals.
     */
    public void validate(ParseTreeContext context) {
        TypeHelper typeHelper = context.getTypeHelper();
        String name = ((AttributeNode)right).getAttributeName();
        // check for fully qualified type names
        Node leftMost = getLeftMostNode();
        if (isDeclaredVariable(leftMost, context)) {
            left.validate(context);
            checkNavigation(left, context);
            Object type = null;
            if (left.isVariableNode()){
                Node path = context.pathForVariable(((VariableNode)left).getVariableName());
                if (path != null){
                    type = path.getType();
                    type = typeHelper.resolveAttribute(type, name);
                }
            }
            if (type == null){
                type = typeHelper.resolveAttribute(left.getType(), name);
            }
            if (type == null) {
                // could not resolve attribute
                throw JPQLException.unknownAttribute(
                    context.getQueryInfo(), right.getLine(), right.getColumn(), 
                    name, typeHelper.getTypeName(left.getType()));
            }
            if (right.isAttributeNode()){
                type = ((AttributeNode)right).computeActualType(type, typeHelper);
                ((AttributeNode)right).checkForQueryKey(left.getType(), typeHelper);
            }
            setType(type);
            right.setType(type);
        } else {
            // Check for enum literal access
            String typeName = left.getAsString();
            Object type = resolveEnumTypeName(typeName, typeHelper);
            if ((type != null) && typeHelper.isEnumType(type)) {
                enumConstant = typeHelper.resolveEnumConstant(type, name);
                if (enumConstant == null) {
                    throw JPQLException.invalidEnumLiteral(context.getQueryInfo(),
                        right.getLine(), right.getColumn(), typeName, name);
                }
            } else {
                // left most node is not an identification variable and
                // dot expression does not denote an enum literal access =>
                // unknown identification variable
                throw JPQLException.aliasResolutionException(
                    context.getQueryInfo(), leftMost.getLine(), 
                    leftMost.getColumn(), leftMost.getAsString());
            }
            setType(type);
            right.setType(type);
        }
    }

    /** 
     * INTERNAL
     * Checks whether the left hand side of this dot node is navigable.
     */
    private void checkNavigation(Node node, ParseTreeContext context) {
        TypeHelper typeHelper = context.getTypeHelper();
        // Checks whether the type of the dot node allows a navigation.
        Object type = node.getType();
        if (!typeHelper.isEntityClass(type) && 
            !typeHelper.isEmbeddable(type) &&
            !typeHelper.isEnumType(type)) {
            throw JPQLException.invalidNavigation(
                context.getQueryInfo(), node.getLine(), node.getColumn(),
                this.getAsString(), node.getAsString(), 
                typeHelper.getTypeName(type));
        }
        // Special check to disallow collection valued relationships
        if (node.isDotNode()) {
            Node left = node.getLeft();
            AttributeNode right = (AttributeNode)node.getRight();
            if (typeHelper.isCollectionValuedRelationship(
                    left.getType(), right.getAttributeName())) {
                throw JPQLException.invalidCollectionNavigation(
                    context.getQueryInfo(), right.getLine(), right.getColumn(),
                    this.getAsString(), right.getAttributeName());
            }
        }
    }
    
    /** */
    private boolean isDeclaredVariable(Node node, ParseTreeContext context) {
        if (node.isVariableNode()) {
            String name = ((VariableNode)node).getCanonicalVariableName();
            return context.isVariable(name);
        }
        return false;
    }

    /**
     * INTERNAL
     * Return a TopLink expression by getting the required variables using the
     * left and right nodes
     * "emp.address.city" = builder.get("address").get("city")
     */
    public Expression generateExpression(GenerationContext context) {
        Node right = getRight();

        if (enumConstant != null) {
            // enum literal access
            return new ConstantExpression(enumConstant, new ExpressionBuilder());
        } else {
            // Get the left expression
            Expression whereClause = getLeft().generateExpression(context);
            
            // Calculate the mapping and pass it to the right expression
            if (right.isAttributeNode()) {
                ((AttributeNode)right).setMapping(resolveMapping(context));
            }
            
            // Or it with whatever the right expression is
            whereClause = right.addToExpression(whereClause, context);
            
            if (alias != null){
                context.addExpression(whereClause, alias);
            }
            // and return the expression...
            return whereClause;
        }
    }

    /**
     * INTERNAL
     * Yes, this is a dot node
     */
    public boolean isDotNode() {
        return true;
    }

    /**
     * INTERNAL
     * ():
     * Answer true if the SELECTed node has a left and right, and the right represents
     * a direct-to-field mapping.
     */
    public boolean endsWithDirectToField(GenerationContext context) {
        DatabaseMapping mapping = resolveMapping(context);
        return (mapping != null) && mapping.isDirectToFieldMapping();
    }

    /**
     * INTERNAL
     * Returns the attribute type if the right represents a direct-to-field mapping.
     */
    public Class getTypeOfDirectToField(GenerationContext context) {
        DatabaseMapping mapping = resolveMapping(context);
        if ((mapping != null) && mapping.isDirectToFieldMapping()) {
            return ((AbstractDirectMapping)mapping).getAttributeClassification();
        }
        return null;
    }

    public Object getTypeForMapKey(ParseTreeContext context){
        Object type = null;
        String name = ((AttributeNode)right).getAttributeName();
        Node leftMost = getLeftMostNode();
        if (isDeclaredVariable(leftMost, context)) {
            type = context.getTypeHelper().resolveMapKey(left.getType(), name);
        }
        return type;
    }
    /**
     * INTERNAL
     * ():
     * Answer true if the node has a left and right, and the right represents
     * a collection mapping.
     */
    public boolean endsWithCollectionField(GenerationContext context) {
        DatabaseMapping mapping = resolveMapping(context);
        return (mapping != null) && mapping.isCollectionMapping();
    }

    /**
     * INTERNAL
     * Answer the name of the attribute which is represented by the receiver's
     * right node.
     */
    public String resolveAttribute() {
        return ((AttributeNode)getRight()).getAttributeName();
    }

    /**
     * INTERNAL
     * Answer the mapping resulting from traversing the receiver's nodes
     */
    public DatabaseMapping resolveMapping(GenerationContext context) {
        Class leftClass = getLeft().resolveClass(context);
        return getRight().resolveMapping(context, leftClass);
    }

    /**
    * resolveClass: Answer the class which results from traversing the mappings for the receiver's nodes
    */
    public Class resolveClass(GenerationContext context) {
        Class leftClass = getLeft().resolveClass(context);
        return getRight().resolveClass(context, leftClass);
    }
    
    /**
     * INTERNAL
     * Get the string representation of this node.
     */
    public String getAsString() {
        return left.getAsString() + "." + right.getAsString();
    }

    /**
     * INTERNAL
     * Return the left most node of a dot expr, so return 'a' for 'a.b.c'.
     */
    public Node getLeftMostNode() {
        if (left.isDotNode()){
            return ((DotNode)left).getLeftMostNode();
        } else if (left.isMapKeyNode()){
            return ((MapKeyNode)left).getLeftMostNode();
        }
        return left;
    }
    
    /**
     * INTERNAL
     * Return the right most node of a dot expr, so return 'c' for 'a.b.c'.
     */
    public Node getRightMostNode() {
        if (right.isDotNode()){
            return ((DotNode)right).getRightMostNode();
        }
        return right;
    }

    /**
     * INTERNAL
     * Returns the type representation for the specified type name. The method
     * looks for inner classes if it cannot resolve the type name.
     */
    private Object resolveEnumTypeName(String name, TypeHelper helper) {
        Object type = helper.resolveTypeName(name);
        if (type == null) {
            // check for inner enum type
            int index = name.lastIndexOf('.');
            if (index != -1) {
                name = name.substring(0, index) + '$' + name.substring(index+1);
                type = helper.resolveTypeName(name);
            }
        }
        return type;
    }
    
    public boolean isAliasableNode(){
        return true;
    }
}
