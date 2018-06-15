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
 * A "CollectionChange" event gets fired whenever a model changes a "bound"
 * collection. You can register a CollectionChangeListener with a source
 * model so as to be notified of any bound collection updates.
 */
public interface CollectionChangeListener extends EventListener {

    /**
     * This method gets called when items are added to a bound collection.
     *
     * @param e A CollectionChangeEvent object describing the event source,
     * the collection that changed, and the items that were added.
     */
    void itemsAdded(CollectionChangeEvent e);

    /**
     * This method gets called when items are removed from a bound collection.
     *
     * @param e A CollectionChangeEvent object describing the event source,
     * the collection that changed, and the items that were removed.
     */
    void itemsRemoved(CollectionChangeEvent e);

    /**
     * This method gets called when a bound collection is changed in a manner
     * that is not easily characterized by the other methods in this interface.
     *
     * @param e A CollectionChangeEvent object describing the event source
     * and the collection that changed.
     */
    void collectionChanged(CollectionChangeEvent e);

}
