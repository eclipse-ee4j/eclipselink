/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.tools.workbench.mappingsmodel.query.relational;

import java.util.Iterator;
import java.util.ListIterator;

import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQuery;
import org.eclipse.persistence.tools.workbench.mappingsmodel.query.MWQueryable;



public interface MWOrderableQuery extends MWQuery {

    Ordering addOrderingItem(MWQueryable queryable);
    Ordering addOrderingItem(Iterator queryables);
    Ordering addOrderingItem(Iterator queryables, Iterator allowsNull);
    Ordering addOrderingItem(int index, Iterator queryables, Iterator allowsNull);

    void removeOrderingItem(Ordering orderingItem);
    void removeOrderingItem(int index);
    ListIterator orderingItems();
    int orderingItemsSize();
    int indexOfOrderingItem(Ordering orderingItem);
    void moveOrderingItemUp(Ordering item);
    void moveOrderingItemDown(Ordering item);
        String ORDERING_ITEMS_LIST = "orderingItems";

}
