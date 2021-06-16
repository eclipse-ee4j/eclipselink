/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.jpa.parsing;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.queries.DatabaseQuery;

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
    @Override
    public abstract Expression generateExpression(GenerationContext context);


  /**
   * Compute the Reference class for this query
   * @param genContext
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
    @Override
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
