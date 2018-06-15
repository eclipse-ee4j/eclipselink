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
package org.eclipse.persistence.mappings.querykeys;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.helper.DatabaseTable;


/**
 * <p>
 * <b>Purpose</b>:Represents a 1-m join query.
 */
public class OneToManyQueryKey extends ForeignReferenceQueryKey {

    /**
     * INTERNAL:
     * override the isCollectionQueryKey() method in the superclass to return true.
     * @return boolean
     */
    @Override
    public boolean isCollectionQueryKey() {
        return true;
    }

    /**
     * INTERNAL:
     * override the isOneToManyQueryKey() method in the superclass to return true.
     * @return boolean
     */
    @Override
    public boolean isOneToManyQueryKey() {
        return true;
    }

    /**
     * PUBLIC:
     * Returns the reference table.
     */
    @Override
    public DatabaseTable getRelationTable(ClassDescriptor referenceDescriptor) {
        return null;
    }
}
