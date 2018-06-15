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
package org.eclipse.persistence.tools.workbench.utility.events;

import java.util.EventListener;

/**
 * A "ListChange" event gets fired whenever a model changes a "bound"
 * list. You can register a ListChangeListener with a source
 * model so as to be notified of any bound list updates.
 */
public interface ListChangeListener extends EventListener {

    /**
     * This method gets called when items are added to a bound list.
     *
     * @param e A ListChangeEvent object describing the event source,
     * the list that changed, the items that were added, and the index
     * at which the items were added.
     */
    void itemsAdded(ListChangeEvent e);

    /**
     * This method gets called when items are removed from a bound list.
     *
     * @param e A ListChangeEvent object describing the event source,
     * the list that changed, the items that were removed, and the index
     * at which the items were removed.
     */
    void itemsRemoved(ListChangeEvent e);

    /**
     * This method gets called when items in a bound list are replaced.
     *
     * @param e A ListChangeEvent object describing the event source,
     * the list that changed, the items that were added, the items that were
     * replaced, and the index at which the items were replaced.
     */
    void itemsReplaced(ListChangeEvent e);

    /**
     * This method gets called when a bound list is changed in a manner
     * that is not easily characterized by the other methods in this interface.
     *
     * @param e A ListChangeEvent object describing the event source
     * and the list that changed.
     */
    void listChanged(ListChangeEvent e);

}
