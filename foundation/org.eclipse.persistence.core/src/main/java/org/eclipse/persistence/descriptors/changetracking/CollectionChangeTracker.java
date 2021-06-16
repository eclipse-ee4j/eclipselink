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
    String getTrackedAttributeName();

    /**
     * PUBLIC:
     * Set the Attribute name this collection is mapped under.
     */
    void setTrackedAttributeName(String attributeName);
}
