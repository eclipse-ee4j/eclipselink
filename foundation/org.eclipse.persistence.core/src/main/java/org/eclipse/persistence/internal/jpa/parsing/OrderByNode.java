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

import java.util.*;

import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;

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

    List orderByItems = null;

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
            Iterator iter = getOrderByItems().iterator();
            while (iter.hasNext()) {
                Node nextNode = (Node)iter.next();
                ((ReadAllQuery)theQuery).addOrdering(nextNode.generateExpression(context));
            }
        }
    }

    /**
     * INTERNAL
     * Validate node.
     */
    public void validate(ParseTreeContext context, SelectNode selectNode) {
        for (Iterator i = orderByItems.iterator(); i.hasNext(); ) {
            Node item = (Node)i.next();
            item.validate(context);
        }
    }

    /**
     * INTERNAL
     * Return the order by statements
     */
    public List getOrderByItems() {
        if (orderByItems == null) {
            setOrderByItems(new Vector());
        }
        return orderByItems;
    }

    /**
     * INTERNAL
     * Set the order by statements
     */
    public void setOrderByItems(List newItems) {
        orderByItems = newItems;
    }
}
