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

import java.util.*;

import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;

/**
 * INTERNAL
 * <p><b>Purpose</b>: Represent an GROUP BY
 * <p><b>Responsibilities</b>:<ul>
 * <li> Generate the correct expression for an GROUP BY
 * </ul>
 */
public class GroupByNode extends MajorNode {

    List groupByItems = null;

    /**
     * Return a new GroupByNode.
     */
    public GroupByNode() {
        super();
    }

    /**
     * INTERNAL
     * Validate the current node.
     */
    public void validate(ParseTreeContext context, SelectNode selectNode) {
        for (Iterator i = groupByItems.iterator(); i.hasNext(); ) {
            Node item = (Node)i.next();
            item.validate(context);
        }
    }

    /**
     * INTERNAL
     * Add the grouping expressions to the passed query
     */
    public void addGroupingToQuery(ObjectLevelReadQuery theQuery, GenerationContext context) {
        if (theQuery.isReportQuery()) {
            Iterator iter = getGroupByItems().iterator();
            while (iter.hasNext()) {
                Node nextNode = (Node)iter.next();
                ((ReportQuery)theQuery).addGrouping(nextNode.generateExpression(context));
            }
        }
    }

    /**
     * INTERNAL
     * Returns true if the sp
     */    
    public boolean isValidHavingExpr(Node expr) {
        if (expr.isDotNode() || expr.isVariableNode()) {
            return isGroupbyItem(expr);
        } else {
            // delegate to child node if any
            Node left = expr.getLeft();
            Node right = expr.getRight();
            return ((left == null) || isValidHavingExpr(left)) &&
                ((right == null) || isValidHavingExpr(right));
        }
    }

    /**
     * INTERNAL
     * Return true if the specified expr is a groupby item.
     */    
    private boolean isGroupbyItem(Node expr) {
        if (expr.isDotNode() || expr.isVariableNode()) {
            String exprRepr = expr.getAsString();
            for (Iterator i = groupByItems.iterator(); i.hasNext();) {
                Node item = (Node)i.next();
                String itemRepr = item.getAsString();
                if (exprRepr.equals(itemRepr)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * INTERNAL
     * Return the GROUP BY statements
     */
    public List getGroupByItems() {
        if (groupByItems == null) {
            setGroupByItems(new Vector());
        }
        return groupByItems;
    }

    /**
     * INTERNAL
     * Set the GROUP BY statements
     */
    public void setGroupByItems(List newItems) {
        groupByItems = newItems;
    }

    /** 
     * INTERNAL
     * Get the string representation of this node. 
     */
    public String getAsString() {
        StringBuffer repr = new StringBuffer();
        for (Iterator i = groupByItems.iterator(); i.hasNext(); ) {
            Node expr = (Node)i.next();
            if (repr.length() > 0) {
                repr.append(", ");
            }
            repr.append(expr.getAsString());
        }
        return "GROUP BY " + repr.toString();
    }
    
}
