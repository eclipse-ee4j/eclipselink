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
    @Override
    public Object buildNewInstance() {
        return factory._persistence_new(factory);
    }
}
