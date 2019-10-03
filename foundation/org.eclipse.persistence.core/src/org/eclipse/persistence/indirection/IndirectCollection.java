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

import java.util.Collection;

/**
 * <b>Purpose</b>:
 * Common interface to indirect collections.
 * <p>
 *
 * @author James Sutherland
 * @since TopLink 10.1.3
 */
public interface IndirectCollection<E, C> extends IndirectContainer<C> {

    /**
     * INTERNAL:
     * clear any changes that have been deferred to instantiation.
     * Indirect collections with change tracking avoid instantiation on add/remove.
     */
    void clearDeferredChanges();

    /**
     * INTERNAL:
     * Return if the collection has any changes that have been deferred to instantiation.
     * Indirect collections with change tracking avoid instantiation on add/remove.
     */
    boolean hasDeferredChanges();

    /**
     * INTERNAL:
     * Return if the collection has any elements added that have been deferred to instantiation.
     * Indirect collections with change tracking avoid instantiation on add/remove.
     */
    Collection<E> getAddedElements();

    /**
     * INTERNAL:
     * Return if the collection has any elements removed that have been deferred to instantiation.
     * Indirect collections with change tracking avoid instantiation on add/remove.
     */
    Collection<E> getRemovedElements();

    /**
     * INTERNAL:
     * Return the real collection object.
     * This will force instantiation.
     */
    C getDelegateObject();

    /**
     * INTERNAL
     * Set whether this collection should attempt do deal with adds and removes without retrieving the
     * collection from the dB
     */
    void setUseLazyInstantiation(boolean useLazyInstantiation);
}
