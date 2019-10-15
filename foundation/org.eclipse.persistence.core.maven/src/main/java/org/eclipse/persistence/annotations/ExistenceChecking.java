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

import java.lang.annotation.Target;
import java.lang.annotation.Retention;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import static org.eclipse.persistence.annotations.ExistenceType.CHECK_CACHE;

/**
 * The ExistenceChecking annotation is used to specify the type of checking
 * EclipseLink should use when determining if an entity is new or existing.
 * It is specifically used on a merge() operation to determine if only the cache
 * should be used to determine if an object exists, or the object should
 * be read (from the database or cache).  By default the object is read.
 *
 * An existence-checking specification is supported on an Entity or
 * MappedSuperclass annotation.
 *
 * @see org.eclipse.persistence.annotations.ExistenceType
 * @see org.eclipse.persistence.queries.DoesExistQuery
 * @author Guy Pelletier
 * @since Eclipselink 1.0
 */
@Target({TYPE})
@Retention(RUNTIME)
public @interface ExistenceChecking {
    /**
     * (Optional) Set the existence check for determining
     * if an insert or update should occur for an object.
     */
    ExistenceType value() default CHECK_CACHE;
}
