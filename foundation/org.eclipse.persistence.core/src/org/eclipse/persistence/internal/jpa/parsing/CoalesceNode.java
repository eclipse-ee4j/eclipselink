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
 *     tware - initial implementation as part of JPA 2.0 RI
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.parsing;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.ReportQuery;

/**
 * INTERNAL
 * <p><b>Purpose</b>: Represent an COALESCE in EJBQL
 * <p><b>Responsibilities</b>:<ul>
 * <li> Generate the correct expression for an COALESCE in EJBQL
 * </ul>
 *    @author tware
 *    @since EclipseLink 1.2
 */
public class CoalesceNode extends Node implements AliasableNode {

    private List clauses = null;

    public CoalesceNode(){
        super();
    }
    
    /**
     * INTERNAL
     * Apply this node to the passed query
     */
    public void applyToQuery(ObjectLevelReadQuery theQuery, GenerationContext generationContext) {
        if (theQuery instanceof ReportQuery) {
            ReportQuery reportQuery = (ReportQuery)theQuery;
            Expression expression = generateExpression(generationContext);
            reportQuery.addItem("Coalesce", expression);
        }
    }
    
    /**
     * INTERNAL
     * Generate the a new EclipseLink Coalesce expression for this node.
     */
    public Expression generateExpression(GenerationContext context) {
        List expressions = new ArrayList();
        Iterator i = clauses.iterator();
        while (i.hasNext()){
            expressions.add(((Node)i.next()).generateExpression(context));
        }
        
        Expression whereClause = context.getBaseExpression().coalesce(expressions);
        return whereClause;
    }
    
    public void validate(ParseTreeContext context) {
        TypeHelper typeHelper = context.getTypeHelper();
        Iterator i = clauses.iterator();
        Object type = null;
        while (i.hasNext()){
            Node node = ((Node)i.next());
            node.validate(context);
            if (type == null){
                type = node.getType();
            } else if (!type.equals(node.getType())){
                type = typeHelper.getObjectType();
            }
        }
        setType(((Node)clauses.get(0)).getType());
    }
    
    public List getClauses() {
        return clauses;
    }

    public void setClauses(List clauses) {
        this.clauses = clauses;
    }
    
    public boolean isAliasableNode(){
        return true;
    }
}
