/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * A CompositeMember annotation is ignored unless is in composite member persistence unit.
 * It may be used in conjunction with a ElementCollection and CollectionTable annotations.
 * It should be used if target type is a primitive type
 * and CollectionTable designates the table that belongs to
 * composite member persistence unit other than the source composite member persistence unit.
 * That allows the source and target to be mapped to different databases.
 *
 * @see jakarta.persistence.ElementCollection
 * @see jakarta.persistence.CollectionTable
 * @see org.eclipse.persistence.config.PersistenceUnitProperties#COMPOSITE_UNIT
 *
 * A CompositeMember can be specified on within an Entity, MappedSuperclass
 * and Embeddable class.
 *
 * @author Andrei Ilitchev
 * @since Eclipselink 2.3
 **/
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface CompositeMember {
    /**
     * The name of a target composite member persistence unit to which element table belongs (if differs from source composite member persistence unit)
     */
    String value();
}
