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
package org.eclipse.persistence.descriptors.changetracking;

import java.beans.PropertyChangeListener;

/**
 * <p>
 * <b>Purpose</b>: Define an interface for any object that wishes to use attribute change tracking.
 * <p>
 * <b>Description</b>: Build a bridge between an object and its PropertyChangeListener.
 */
public interface ChangeTracker {

    /**
     * PUBLIC:
     * Return the PropertyChangeListener for the object.
     */
    public PropertyChangeListener _persistence_getPropertyChangeListener();

    /**
     * PUBLIC:
     * Set the PropertyChangeListener for the object.
     */
    public void _persistence_setPropertyChangeListener(PropertyChangeListener listener);
}
