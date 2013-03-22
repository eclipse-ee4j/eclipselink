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

// TopLink imports
import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.exceptions.JPQLException;

/**
 * INTERNAL
 * <p><b>Purpose</b>: Represent an IN in EJBQL
 * <p><b>Responsibilities</b>:<ul>
 * <li> Generate the correct expression for an IN
 * </ul>
 *    @author Jon Driscoll and Joel Lucuik
 *    @since TopLink 4.0
 */
public class InNode extends SimpleConditionalExpressionNode {

    private List theObjects = null;

    //Was NOT indicated? "WHERE emp.lastName NOT IN (...)
    private boolean notIndicated = false;
    
    private boolean isListParameterOrSubquery = false;

    /**
     * InNode constructor comment.
     */
    public InNode() {
        super();
    }

    /**
     * INTERNAL
     * Add the passed node value to the collection of object for this node
     */
    public void addNodeToTheObjects(Node theNode) {
        getTheObjects().add(theNode);
    }

    /**
     * INTERNAL
     * Validate the current node and calculates its type.
     */
    public void validate(ParseTreeContext context) {
        Object leftType = null;
        TypeHelper typeHelper = context.getTypeHelper();

        if (left != null) {
            left.validate(context);
            // check to see if the argument is a parameter
            if (isListParameterOrSubquery && !getTheObjects().isEmpty() && ((Node)getTheObjects().get(0)).isParameterNode()){
                leftType = Collection.class;
            } else {
                leftType = left.getType();
            }
        }
        for (Iterator i = getTheObjects().iterator(); i.hasNext();) {
            Node node = (Node)i.next();
            node.validate(context);
            node.validateParameter(context, leftType);
            Object nodeType = node.getType();
            if ((leftType != null) && !typeHelper.isAssignableFrom(leftType, nodeType))
                throw JPQLException.invalidExpressionArgument(
                    context.getQueryInfo(), node.getLine(), node.getColumn(),
                    "IN", node.getAsString(), typeHelper.getTypeName(leftType));
        }

        setType(typeHelper.getBooleanType());
    }

    /**
     * INTERNAL
     * Return the EclipseLink expression for this node
     */
    public Expression generateExpression(GenerationContext context) {
        Expression whereClause = getLeft().generateExpression(context);
        List arguments = getTheObjects();
        Node firstArg = (Node)arguments.get(0);
        if (firstArg.isSubqueryNode()) {
            SubqueryNode subqueryNode = (SubqueryNode)firstArg;
            ReportQuery reportQuery = subqueryNode.getReportQuery(context);
            if (notIndicated()) {
                whereClause = whereClause.notIn(reportQuery);
            }
            else {
                whereClause = whereClause.in(reportQuery);
            }
        } else if (isListParameterOrSubquery && firstArg.isParameterNode()) {
            if (notIndicated()) {
                whereClause = whereClause.notIn(firstArg.generateExpression(context));
            } else {
                whereClause = whereClause.in(firstArg.generateExpression(context));
            }
        } else {
            Vector inArguments = new Vector(arguments.size());
            for (Iterator iter = arguments.iterator(); iter.hasNext();) {
                Node nextNode = (Node)iter.next();
                inArguments.add(nextNode.generateExpression(context));
            }
            if (inArguments.size() > 0) {
                if (notIndicated()) {
                    whereClause = whereClause.notIn(inArguments);
                } else {
                    whereClause = whereClause.in(inArguments);
                }
            }
        }
        return whereClause;
    }

    /**
     * INTERNAL
     * Return the collection of the objects used as parameters for this node
     */
    public List getTheObjects() {
        if (theObjects == null) {
            setTheObjects(new Vector());
        }
        return theObjects;
    }

    /**
     * INTERNAL:
     * This method is called to indicate that the inNode has a single argument.  This will be either a
     * subquery or a single parameter that contains the list of items to test
     * @param isListParameterOrSubquery
     */
    public void setIsListParameterOrSubquery(boolean isListParameterOrSubquery){
        this.isListParameterOrSubquery = isListParameterOrSubquery;
    }
    
    /**
     * INTERNAL
     * Set this node's object collection to the passed value
     */
    public void setTheObjects(List newTheObjects) {
        theObjects = newTheObjects;
    }

    /**
     * INTERNAL
     * Indicate if a NOT was found in the WHERE clause.
     * Examples:
     *        ...WHERE ... NOT IN(...)
     */
    public void indicateNot() {
        notIndicated = true;
    }

    public boolean notIndicated() {
        return notIndicated;
    }
}
