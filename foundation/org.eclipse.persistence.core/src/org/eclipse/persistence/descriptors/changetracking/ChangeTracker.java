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
