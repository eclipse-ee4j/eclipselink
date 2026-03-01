/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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

import jakarta.persistence.Column;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * The OptimisticLocking annotation is used to specify the type of optimistic locking
 * EclipseLink should use when updating or deleting entities.
 * <p>
 * An optimistic-locking specification is supported on an Entity or MappedSuperclass annotation.
 *
 * @see OptimisticLockingType
 * @author Guy Pelletier
 * @since Oracle TopLink 11.1.1.0.0
 */
@Target({TYPE})
@Retention(RUNTIME)
public @interface OptimisticLocking {
    /**
     * The type of optimistic locking policy to use.
     */
    OptimisticLockingType type() default OptimisticLockingType.VERSION_COLUMN;

    /**
     * For an optimistic locking policy of type {@linkplain OptimisticLockingType#SELECTED_COLUMNS},
     * this annotation member becomes a (Required) field.
     */
    Column[] selectedColumns() default {};

    /**
     * Specify where the optimistic locking policy should cascade lock.
     * <p>
     * Currently only supported with {@linkplain OptimisticLockingType#VERSION_COLUMN} locking.
     */
    boolean cascade() default false;
}
