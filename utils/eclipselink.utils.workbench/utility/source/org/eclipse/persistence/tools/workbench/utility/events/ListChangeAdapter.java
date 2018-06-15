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

/**
 * Convenience implementation of ListChangeListener.
 */
public class ListChangeAdapter implements ListChangeListener {

    /**
     * Default constructor.
     */
    public ListChangeAdapter() {
        super();
    }

    /**
     * @see ListChangeListener#itemsAdded(ListChangeEvent)
     */
    public void itemsAdded(ListChangeEvent e) {
        // do nothing
    }

    /**
     * @see ListChangeListener#itemsRemoved(ListChangeEvent)
     */
    public void itemsRemoved(ListChangeEvent e) {
        // do nothing
    }

    /**
     * @see ListChangeListener#itemsReplaced(ListChangeEvent)
     */
    public void itemsReplaced(ListChangeEvent e) {
        // do nothing
    }

    /**
     * @see ListChangeListener#listChanged(ListChangeEvent)
     */
    public void listChanged(ListChangeEvent e) {
        // do nothing
    }

}
