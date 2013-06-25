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
 *     12/12/2008-1.1 Guy Pelletier 
 *       - 249860: Implement table per class inheritance support.
 ******************************************************************************/  
package org.eclipse.persistence.descriptors;

import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.queries.ReadAllQuery;
import org.eclipse.persistence.queries.ReadObjectQuery;

/**
 * <p><b>Purpose</b>: Provides the functionality to support a TABLE_PER_CLASS
 * inheritance strategy. Resolves relational mappings and querying.
 */
public class TablePerClassPolicy extends InterfacePolicy {
    
    /**
     * INTERNAL:
     * Create a new policy.
     * Only descriptors involved in inheritance should have a policy.
     */
    public TablePerClassPolicy(ClassDescriptor descriptor) {
        setDescriptor(descriptor);
    }
    
    /**
     * INTERNAL:
     */
    public boolean isTablePerClassPolicy() {
        return true;
    }
    
    /**
     * INTERNAL:
     * Select all objects for a concrete descriptor.
     */
    @Override
    protected Object selectAllObjects(ReadAllQuery query) { 
        if (this.descriptor.isAbstract()) {
            return query.getContainerPolicy().containerInstance();
        }
        return super.selectAllObjects(query);
    }
    
    /**
     * INTERNAL:
     * Select one object of any concrete subclass.
     */
    @Override
    protected Object selectOneObject(ReadObjectQuery query) throws DescriptorException {
        if (this.descriptor.isAbstract()) {
            return null;
        }
        return super.selectOneObject(query);
    }
}
