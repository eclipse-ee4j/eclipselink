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
 *     James Sutherland - initial API and implementation
 ******************************************************************************/  
package org.eclipse.persistence.internal.descriptors;

/**
 * Used with weaving to create "empty" instances without using reflection.
 */
public class PersistenceObjectInstantiationPolicy extends InstantiationPolicy {
    /** The factory is an instance of the domain class. */
    protected PersistenceObject factory;

    protected PersistenceObjectInstantiationPolicy() {        
    }
    
    public PersistenceObjectInstantiationPolicy(PersistenceObject factory) {
        this.factory = factory;
    }
    
    /**
     * Build and return a new instance, using the factory
     */
    public Object buildNewInstance() {
        return factory._persistence_new(factory);
    }
}
