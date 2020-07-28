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

/**
 * <p>
 * <b>Purpose</b>: Define an interface for any collection that wishes to use attribute change track.
 * <p>
 * <b>Description</b>: Build a bridge between an object and its PropertyChangeListener.  Which will be
 * The listener of the parent object.
 */
public interface CollectionChangeTracker extends ChangeTracker{

    /**
     * PUBLIC:
     * Return the Attribute name this collection is mapped under.
     */
    public String getTrackedAttributeName();

    /**
     * PUBLIC:
     * Set the Attribute name this collection is mapped under.
     */
    public void setTrackedAttributeName(String attributeName);
}
