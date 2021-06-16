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
//     12/12/2008-1.1 Guy Pelletier
//       - 249860: Implement table per class inheritance support.
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
    @Override
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
