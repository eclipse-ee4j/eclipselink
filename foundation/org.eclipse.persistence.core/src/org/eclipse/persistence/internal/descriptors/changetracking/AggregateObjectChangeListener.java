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
package org.eclipse.persistence.internal.descriptors.changetracking;

import java.beans.*;

/**
 * <p>
 * <b>Purpose</b>: Define a listener for object change tracking.
 * <p>
 * <b>Description</b>: Listener is notified on a PropertyChangeEvent from the object it belongs to.
 * <p>
 * <b>Responsibilities</b>: Set the flag to true when there is any change in the object.
 * <ul>
 * </ul>
 */
public class AggregateObjectChangeListener extends ObjectChangeListener {
    protected ObjectChangeListener parentListener;
    protected String parentAttributeName;

    /**
     * INTERNAL:
     * Create a ObjectChangeListener
     */
    public AggregateObjectChangeListener(ObjectChangeListener parentListener, String parentAttribute) {
        super();
        this.parentListener = parentListener;
        this.parentAttributeName = parentAttribute;
    }

    /**
     * PUBLIC:
     * This method turns marks the object as changed.
     */
    public void internalPropertyChange(PropertyChangeEvent evt) {
        hasChanges = true;
        this.parentListener.internalPropertyChange(new PropertyChangeEvent(evt.getSource(), this.parentAttributeName, evt.getOldValue(), evt.getNewValue()));
    }

}
