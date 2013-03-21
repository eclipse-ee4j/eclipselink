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

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.internal.expressions.MapEntryExpression;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.ReportQuery;

/**
 * INTERNAL
 * <p><b>Purpose</b>: Represent an KEY in EJBQL
 * <p><b>Responsibilities</b>:<ul>
 * <li> Generate the correct expression for an KEY in EJBQL
 * </ul>
 *    @author tware
 *    @since EclipseLink 1.2
 */
public class MapKeyNode extends Node implements AliasableNode {

    public MapKeyNode(){
        super();
    }
    
    /**
     * INTERNAL
     * Is this node a MapKey node
     */
    public boolean isMapKeyNode() {
        return true;
    }
    
    /**
     * INTERNAL
     * Apply this node to the passed query
     */
    public void applyToQuery(ObjectLevelReadQuery theQuery, GenerationContext generationContext) {
        if (theQuery instanceof ReportQuery) {
            ReportQuery reportQuery = (ReportQuery)theQuery;
            Expression expression = generateExpression(generationContext);
            reportQuery.addItem(left.resolveAttribute() + "MapKey", expression);
        }
    }
    
    /**
     * INTERNAL
     * Generate the a new EclipseLink TableEntryExpression for this node.
     */
    public Expression generateExpression(GenerationContext context) {
        Expression owningExpression = getLeft().generateExpression(context);
        MapEntryExpression whereClause = new MapEntryExpression(owningExpression);
        return whereClause;
    }
    
    /**
     * INTERNAL
     * Return the left most node of a dot expr, so return 'a' for 'a.b.c'.
     */
    public Node getLeftMostNode() {
        if (left.isDotNode()){
            return ((DotNode)left).getLeftMostNode();
        }
        return left;
    }
    
    public void validate(ParseTreeContext context) {
        left.validate(context);
        if (left.isVariableNode()){
            setType(((VariableNode)left).getTypeForMapKey(context));
        } else if (left.isDotNode()){
            setType(((DotNode)left).getTypeForMapKey(context));
        }
    }
    
    public boolean isAliasableNode(){
        return true;
    }
}
