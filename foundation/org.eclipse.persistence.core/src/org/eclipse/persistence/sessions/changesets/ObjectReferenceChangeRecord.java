/*
 * Copyright (c) 1998, 2019 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.sessions.changesets;


/**
 * <p>
 * <b>Purpose</b>: Provides API for the ObjectReferenceChangeRecord.
 * <p>
 * <b>Description</b>: This Interface represents changes made in a one to one mapping and other single object reference mappings.
 */
public interface ObjectReferenceChangeRecord extends ChangeRecord {

    /**
     * ADVANCED:
     * Returns the new reference for this object
     * @return org.eclipse.persistence.sessions.changesets.ObjectChangeSet
     */
    ObjectChangeSet getNewValue();

    /**
     * Return the old value of the object reference.
     * This is used during the commit for private-owned references.
     */
    @Override
    Object getOldValue();
}
