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
    PropertyChangeListener _persistence_getPropertyChangeListener();

    /**
     * PUBLIC:
     * Set the PropertyChangeListener for the object.
     */
    void _persistence_setPropertyChangeListener(PropertyChangeListener listener);
}
