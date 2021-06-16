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

/**
 * INTERNAL
 * <p><b>Purpose</b>: Superclass for Aggregate Nodes
 * <p><b>Responsibilities</b>:<ul>
 * <li> Apply itself to a query correctly
 * </ul>
 *    @author Jon Driscoll
 *    @since TopLink 5.0
 */

import org.eclipse.persistence.expressions.Expression;

public abstract class AggregateNode extends Node implements AliasableNode {

    private boolean distinct = false;

    /**
     * INTERNAL
     */
    @Override
    public String resolveAttribute() {
        Node arg = getLeft();
        return arg.isDotNode() ? ((DotNode)arg).resolveAttribute() : null;
    }

    /**
     * resolveClass: Answer the class associated with my left node.
     */
    @Override
    public Class resolveClass(GenerationContext context) {
        return getLeft().resolveClass(context);
    }

    /**
     * INTERNAL
     * Is this node an Aggregate node
     */
    @Override
    public boolean isAggregateNode() {
        return true;
    }

    /** */
    public boolean usesDistinct() {
        return distinct;
    }

    /** */
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    /**
     * INTERNAL
     * Return a EclipseLink expression generated using the left node
     */
    @Override
    public Expression generateExpression(GenerationContext context) {
        if (alias == null){
            alias = getAsString();
        }
        Expression aggregateExpr = context.expressionFor(alias);
        if (aggregateExpr == null) {
            Expression arg = getLeft().generateExpression(context);
            if (usesDistinct()) {
                arg = arg.distinct();
            }
            aggregateExpr = addAggregateExression(arg);
            context.addExpression(aggregateExpr, alias);
        }
        return aggregateExpr;
    }

    /**
     * INTERNAL
     */
    protected abstract Expression addAggregateExression(Expression expr);



    @Override
    public boolean isAliasableNode(){
        return true;
    }

}
