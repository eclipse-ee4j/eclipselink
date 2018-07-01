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

import org.eclipse.persistence.expressions.*;
import org.eclipse.persistence.queries.ReportQuery;

/**
 * INTERNAL
 * <p><b>Purpose</b>: Represent an ALL subquery.
 */
public class AllNode extends Node{

    /**
     * Return a new AllNode.
     */
    public AllNode() {
        super();
    }

    /**
     * INTERNAL
     * Validate node and calculate its type.
     */
    public void validate(ParseTreeContext context) {
        if (left != null) {
            left.validate(context);
            setType(left.getType());
        }
    }

    /**
     * INTERNAL
     * Generate the EclipseLink expression for this node
     */
    public Expression generateExpression(GenerationContext context) {
        SubqueryNode subqueryNode = (SubqueryNode)getLeft();
        ReportQuery reportQuery = subqueryNode.getReportQuery(context);

        Expression expr = context.getBaseExpression();
        return expr.all(reportQuery);
    }
}

