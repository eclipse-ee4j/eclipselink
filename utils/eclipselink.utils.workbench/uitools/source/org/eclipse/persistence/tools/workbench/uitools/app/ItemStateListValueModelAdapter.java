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
package org.eclipse.persistence.tools.workbench.uitools.app;

import org.eclipse.persistence.tools.workbench.utility.Model;
import org.eclipse.persistence.tools.workbench.utility.events.StateChangeEvent;
import org.eclipse.persistence.tools.workbench.utility.events.StateChangeListener;


/**
 * Extend ItemAspectListValueModelAdapter to listen to the
 * "state" of each item in the wrapped list model.
 */
public class ItemStateListValueModelAdapter
    extends ItemAspectListValueModelAdapter
{
    /** Listener that listens to all the items in the list. */
    protected StateChangeListener itemStateListener;


    // ********** constructors **********

    /**
     * Construct an adapter for the item state.
     */
    public ItemStateListValueModelAdapter(ListValueModel listHolder) {
        super(listHolder);
    }

    /**
     * Construct an adapter for the item state.
     */
    public ItemStateListValueModelAdapter(CollectionValueModel collectionHolder) {
        super(collectionHolder);
    }


    // ********** initialization **********

    protected void initialize() {
        super.initialize();
        this.itemStateListener = this.buildItemStateListener();
    }

    protected StateChangeListener buildItemStateListener() {
        return new StateChangeListener() {
            public void stateChanged(StateChangeEvent e) {
                ItemStateListValueModelAdapter.this.itemAspectChanged(e);
            }
            public String toString() {
                return "item state listener";
            }
        };
    }


    // ********** behavior **********

    protected void startListeningToItem(Model item) {
        item.addStateChangeListener(this.itemStateListener);
    }

    protected void stopListeningToItem(Model item) {
        item.removeStateChangeListener(this.itemStateListener);
    }

}
