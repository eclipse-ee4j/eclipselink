/*
 * Copyright (c) 1998, 2019 Oracle and/or its affiliates. All rights reserved.
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
