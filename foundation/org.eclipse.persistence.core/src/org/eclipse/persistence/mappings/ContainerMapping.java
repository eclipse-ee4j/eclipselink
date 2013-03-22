/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
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
