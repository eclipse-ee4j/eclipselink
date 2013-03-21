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
import org.eclipse.persistence.expressions.*;

/**
 * INTERNAL:
 * This node contains the information about what kind of query is represented it's tree
 * (e.g. Select, Update etc.)
 * Subclasses of this node will contain query specific behavior.
 */
public abstract class QueryNode extends MajorNode {
    private ParseTree parseTree;

    public QueryNode() {
        super();
    }

    /**
     * INTERNAL
     * Returns a DatabaseQuery instance according to the kind of the query the
     * owning ParseTree represents: SELECT, UPDATE or DELETE.
     */ 
    public abstract DatabaseQuery createDatabaseQuery(ParseTreeContext context);

    /**
     * INTERNAL
     * Apply this node to the passed query
     */
    public abstract void applyToQuery(DatabaseQuery theQuery, GenerationContext context);

    /**
     * INTERNAL
     * Return a EclipseLink expression generated using the left node
     */
    public abstract Expression generateExpression(GenerationContext context);


  /**
   * Compute the Reference class for this query 
   * @param context 
   * @return the class this query is querying for
   */
    public Class getReferenceClass(GenerationContext genContext) {
        return resolveClass(genContext);
    }

    public boolean isSelectNode() {
        return false;
    }

    public boolean isUpdateNode() {
        return false;
    }

    public boolean isDeleteNode() {
        return false;
    }

    /**
     * Return the class represented in this node.
     */
    public abstract Class resolveClass(GenerationContext context);

    /**
     * Set the parseTree
     */
    public void setParseTree(ParseTree parseTree) {
        this.parseTree = parseTree;
    }

    /** */
    public ParseTree getParseTree() {
        return parseTree;
    }
    
}
