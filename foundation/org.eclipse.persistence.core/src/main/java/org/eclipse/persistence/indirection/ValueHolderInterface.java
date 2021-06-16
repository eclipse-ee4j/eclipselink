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
package org.eclipse.persistence.indirection;


/**
 * <b>Purpose</b>: Interface to allow lazy loading of an object's relationships from the database.
 *
 * @see ValueHolder
 * @see org.eclipse.persistence.internal.indirection.DatabaseValueHolder
 */
public interface ValueHolderInterface<T> extends Cloneable {

    /** Can be used to have transparent indirection toString instantiate the objects. */
    boolean shouldToStringInstantiate = false;

    /**
     * PUBLIC:
     * Copy the value holder (but not its' reference, shallow).
     */
    Object clone();

    /**
     * PUBLIC:
     * Return the value.
     */
    T getValue();

    /**
     * PUBLIC:
     * Return whether the contents have been read from the database.
     * This is used periodically by the indirection policy to determine whether
     * to trigger the database read.
     */
    boolean isInstantiated();

    /**
     * PUBLIC:
     * Set the value.
     */
    void setValue(T value);
}
