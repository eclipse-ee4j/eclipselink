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

import java.util.Collection;

/**
 * <b>Purpose</b>:
 * Common interface to indirect collections.
 * <p></p>
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
