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
//  04/15/2008-1.0M7 Guy Pelletier
//     - 226517: Add existence support to the EclipseLink-ORM.XML Schema
package org.eclipse.persistence.annotations;

/**
 * An enum that is used within the ExistenceChecking annotation.
 *
 * @see org.eclipse.persistence.annotations.ExistenceChecking
 * @see org.eclipse.persistence.queries.DoesExistQuery
 * @author Guy Pelletier
 * @since EclipseLink 1.0
 */
public enum ExistenceType {
    /**
     * Assume that if the objects primary key does not include null and it
     * is in the cache, then it must exist.
     */
    CHECK_CACHE,

    /**
     * Perform does exist check on the database.
     */
    CHECK_DATABASE,

    /**
     * Assume that if the objects primary key does not include null then it
     * must exist. This may be used if the application guarantees or does not
     * care about the existence check.
     */
    ASSUME_EXISTENCE,

    /**
     * Assume that the object does not exist. This may be used if the
     * application guarantees or does not care about the existence check.
     * This will always force an insert to be called.
     */
    ASSUME_NON_EXISTENCE
}
