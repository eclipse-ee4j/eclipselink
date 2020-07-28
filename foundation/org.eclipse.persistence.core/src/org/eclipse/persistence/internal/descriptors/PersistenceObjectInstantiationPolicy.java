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
//     James Sutherland - initial API and implementation
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
