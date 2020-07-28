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

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import static org.eclipse.persistence.annotations.ChangeTrackingType.AUTO;

/**
 * The ChangeTracking annotation is used to specify the
 * org.eclipse.persistence.descriptors.changetracking.ObjectChangePolicy
 * which computes changes sets for EclipseLink's UnitOfWork commit process.
 * An ObjectChangePolicy is stored on an Entity's descriptor.
 *
 * A ChangeTracking annotation may be specified on an Entity,
 * MappedSuperclass or Embeddable.
 *
 * @see org.eclipse.persistence.annotations.ChangeTrackingType
 * @author Guy Pelletier
 * @since Oracle TopLink 11.1.1.0.0
 */
@Target({TYPE})
@Retention(RUNTIME)
public @interface ChangeTracking {
    /**
     * (Optional) The type of change tracking to use.
     */
    ChangeTrackingType value() default AUTO;
}
