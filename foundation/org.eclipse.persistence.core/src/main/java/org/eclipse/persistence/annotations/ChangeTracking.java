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
