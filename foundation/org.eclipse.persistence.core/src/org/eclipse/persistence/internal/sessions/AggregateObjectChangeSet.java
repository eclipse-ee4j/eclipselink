/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.internal.sessions;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import java.util.Vector;

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
    public AggregateObjectChangeSet(Vector primaryKey, ClassDescriptor descriptor, Object cloneObject, UnitOfWorkChangeSet parent, boolean isNew) {
        super(primaryKey, descriptor, cloneObject, parent, isNew);
    }

    public CacheKey CacheKey() {
        return null;
    }

    public boolean isAggregate() {
        return true;
    }
}
