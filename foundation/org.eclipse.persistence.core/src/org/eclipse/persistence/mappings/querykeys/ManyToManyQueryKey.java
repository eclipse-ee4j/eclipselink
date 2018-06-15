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
import org.eclipse.persistence.exceptions.QueryException;
import org.eclipse.persistence.internal.helper.DatabaseTable;


/** <p>
 * <b>Purpose</b>:Represents a m-m join query.
 */
public class ManyToManyQueryKey extends ForeignReferenceQueryKey {

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
     * override the isManyToManyQueryKey() method in the superclass to return true.
     * @return boolean
     */
    @Override
    public boolean isManyToManyQueryKey() {
        return true;
    }

    /**
     * PUBLIC:
     * Returns the reference table.
     */
    @Override
    public DatabaseTable getRelationTable(ClassDescriptor referenceDescriptor) {
        DatabaseTable relationTable = super.getRelationTable(referenceDescriptor);
        if(relationTable != null) {
            return relationTable;
        } else {
            throw QueryException.noRelationTableInManyToManyQueryKey(this, this.joinCriteria);
        }
    }
}
