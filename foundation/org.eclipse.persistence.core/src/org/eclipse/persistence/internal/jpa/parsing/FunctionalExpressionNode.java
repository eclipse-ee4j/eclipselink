/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.jpa.parsing;

import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.queries.ReportQuery;


/**
 * INTERNAL
 * <p><b>Purpose</b>: The superclass of the Functional Expression nodes
 * <p><b>Responsibilities</b>:<ul>
 * <li> None
 * </ul>
 *    @author Jon Driscoll and Joel Lucuik
 *    @since TopLink 4.0
 */
public class FunctionalExpressionNode extends Node implements AliasableNode {

    /**
     * FunctionalExpressionNode constructor comment.
     */
    public FunctionalExpressionNode() {
        super();
    }

    /**
     * INTERNAL
     * Apply this node to the passed query
     */
    public void applyToQuery(ObjectLevelReadQuery theQuery, GenerationContext context) {
        if (theQuery.isReportQuery()) {
            ReportQuery reportQuery = (ReportQuery)theQuery;
            reportQuery.addAttribute(resolveAttribute(), generateExpression(context), (Class)getType());
        }
    }

    public boolean isAliasableNode(){
        return true;
    }
}
