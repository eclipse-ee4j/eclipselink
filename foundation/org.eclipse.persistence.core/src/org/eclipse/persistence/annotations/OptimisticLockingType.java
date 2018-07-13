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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.annotations;

/**
 * An enum that is used within the OptimisticLocking annotation.
 *
 * @see org.eclipse.persistence.annotations.OptimisticLocking
 * @author Guy Pelletier
 * @since Oracle TopLink 11.1.1.0.0
 */
public enum OptimisticLockingType {
    /**
     * Using this type of locking policy compares every field in the table
     * in the WHERE clause when doing an update or a delete. If any field
     * has been changed, an optimistic locking exception will be thrown.
     */
    ALL_COLUMNS,

    /**
     * Using this type of locking policy compares only the changed fields
     * in the WHERE clause when doing an update. If any field has been
     * changed, an optimistic locking exception will be thrown. A delete
     * will only compare the primary key.
     */
    CHANGED_COLUMNS,

    /**
     * Using this type of locking compares selected fields in the WHERE
     * clause when doing an update or a delete. If any field has been
     * changed, an optimistic locking exception will be thrown. Note that
     * the fields specified must be mapped and not be primary keys.
     */
    SELECTED_COLUMNS,

    /**
     * Using this type of locking policy compares a single version number
     * in the where clause when doing an update. The version field must be
     * mapped and not be the primary key.
     */
    VERSION_COLUMN
}

