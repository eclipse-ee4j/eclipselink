/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.internal.sessions;

import org.eclipse.persistence.internal.identitymaps.CacheKey;
import java.util.Vector;

public class AggregateObjectChangeSet extends ObjectChangeSet {

    /**
     * The default constructor is used only by SDK XML project for mapping ObjectChangeSet
     */
    public AggregateObjectChangeSet() {
        super();
    }

    /**
     * This constructor is used to create an ObjectChangeSet that represents an aggregate object.
     */
    public AggregateObjectChangeSet(Object cloneObject, UnitOfWorkChangeSet parent, boolean isNew) {
        super(cloneObject, parent, isNew);
    }

    /**
     * This constructor is used to create an ObjectChangeSet that represents a regular object.
     */
    public AggregateObjectChangeSet(Vector primaryKey, Class classType, Object cloneObject, UnitOfWorkChangeSet parent, boolean isNew) {
        super(primaryKey, classType, cloneObject, parent, isNew);
    }

    public CacheKey CacheKey() {
        return null;
    }

    public boolean isAggregate() {
        return true;
    }
}