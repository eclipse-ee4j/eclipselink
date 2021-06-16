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
    @Override
    public void internalPropertyChange(PropertyChangeEvent evt) {
        hasChanges = true;
        this.parentListener.internalPropertyChange(new PropertyChangeEvent(evt.getSource(), this.parentAttributeName, evt.getOldValue(), evt.getNewValue()));
    }

}
