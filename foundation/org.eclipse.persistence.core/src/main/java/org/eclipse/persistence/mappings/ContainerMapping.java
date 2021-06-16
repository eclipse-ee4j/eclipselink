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
package org.eclipse.persistence.mappings;

import org.eclipse.persistence.internal.queries.*;

/**
 * Interface used by clients to interact
 * with the assorted mappings that use <code>ContainerPolicy</code>.
 *
 * @see org.eclipse.persistence.internal.queries.ContainerPolicy
 *
 * @author Big Country
 * @since TOPLink/Java 4.0
 */
public interface ContainerMapping {

    /**
     * PUBLIC:
     * Return the mapping's container policy.
     */
    ContainerPolicy getContainerPolicy();

    /**
     * PUBLIC:
     * Set the mapping's container policy.
     */
    void setContainerPolicy(ContainerPolicy containerPolicy);

    /**
     * PUBLIC:
     * Configure the mapping to use an instance of the specified container class
     * to hold the target objects.
     * <p>The container class must implement (directly or indirectly) the
     * <code>java.util.Collection</code> interface.
     */
    void useCollectionClass(Class concreteClass);

    /**
     * PUBLIC:
     * Configure the mapping to use an instance of the specified container class
     * to hold the target objects.
     * <p>The container class must implement (directly or indirectly) the
     * <code>java.util.Collection</code> interface.
     */
    void useCollectionClassName(String concreteClass);

    /**
     * PUBLIC:
     * Configure the mapping to use an instance of the specified container class
     * to hold the target objects.
     * <p>The container class must implement (directly or indirectly) the
     * <code>java.util.List</code> interface.
     */
    void useListClassName(String concreteClass);

    /**
     * PUBLIC:
     * Configure the mapping to use an instance of the specified container class
     * to hold the target objects. The key used to index a value in the
     * <code>Map</code> is the value returned by a call to the specified
     * zero-argument method.
     * The method must be implemented by the class (or a superclass) of any
     * value to be inserted into the <code>Map</code>.
     * <p>The container class must implement (directly or indirectly) the
     * <code>java.util.Map</code> interface.
     * <p>To facilitate resolving the method, the mapping's referenceClass
     * must set before calling this method.
     */
    void useMapClass(Class concreteClass, String methodName);

    /**
     * PUBLIC:
     * Configure the mapping to use an instance of the specified container class
     * to hold the target objects. The key used to index a value in the
     * <code>Map</code> is the value returned by a call to the specified
     * zero-argument method.
     * The method must be implemented by the class (or a superclass) of any
     * value to be inserted into the <code>Map</code>.
     * <p>The container class must implement (directly or indirectly) the
     * <code>java.util.Map</code> interface.
     * <p>To facilitate resolving the method, the mapping's referenceClass
     * must set before calling this method.
     */
    void useMapClassName(String concreteClass, String methodName);
}
