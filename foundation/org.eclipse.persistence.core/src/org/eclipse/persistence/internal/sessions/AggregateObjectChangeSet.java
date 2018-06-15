/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.sessions;

import org.eclipse.persistence.descriptors.ClassDescriptor;

public class AggregateObjectChangeSet extends ObjectChangeSet {

    /**
     * The default constructor.
     */
    public AggregateObjectChangeSet() {
        super();
    }

    /**
     * This constructor is used to create an ObjectChangeSet that represents a regular object.
     */
    public AggregateObjectChangeSet(Object primaryKey, ClassDescriptor descriptor, Object cloneObject, UnitOfWorkChangeSet parent, boolean isNew) {
        super(primaryKey, descriptor, cloneObject, parent, isNew);
    }

    @Override
    public Object getId() {
        return null;
    }

    @Override
    public boolean isAggregate() {
        return true;
    }
}
