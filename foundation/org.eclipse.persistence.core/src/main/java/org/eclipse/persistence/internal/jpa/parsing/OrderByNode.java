/*
 * Copyright (c) 1998, 2023 Oracle and/or its affiliates. All rights reserved.
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

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * INTERNAL
 * <p><b>Purpose</b>: Represent an ORDER BY
 * <p><b>Responsibilities</b>:<ul>
 * <li> Generate the correct expression for an ORDER BY
 * </ul>
 *    @author Jon Driscoll
 *    @since TopLink 5.0
 */
public class OrderByNode extends MajorNode {

    List<Node> orderByItems = null;

    /**
     * Return a new OrderByNode.
     */
    public OrderByNode() {
        super();
    }

    /**
     * INTERNAL
     * Add the ordering expressions to the passed query
     */
    public void addOrderingToQuery(ObjectLevelReadQuery theQuery, GenerationContext context) {
        if (theQuery.isReadAllQuery()) {
            Iterator<Node> iter = getOrderByItems().iterator();
            while (iter.hasNext()) {
                Node nextNode = iter.next();
                theQuery.addOrdering(nextNode.generateExpression(context));
            }
        }
    }

    /**
     * INTERNAL
     * Validate node.
     */
    public void validate(ParseTreeContext context, SelectNode selectNode) {
        for (Iterator<Node> i = orderByItems.iterator(); i.hasNext(); ) {
            Node item = i.next();
            item.validate(context);
        }
    }

    /**
     * INTERNAL
     * Return the order by statements
     */
    public List<Node> getOrderByItems() {
        if (orderByItems == null) {
            setOrderByItems(new Vector<>());
        }
        return orderByItems;
    }

    /**
     * INTERNAL
     * Set the order by statements
     */
    public void setOrderByItems(List<Node> newItems) {
        orderByItems = newItems;
    }
}
