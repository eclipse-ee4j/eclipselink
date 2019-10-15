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
//     06/12/2017-2.7 Lukas Jungmann
//       - 518155: [jpa22] add support for repeatable annotations
package org.eclipse.persistence.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Repeatable;

/**
 * A Converter is used to customize the values during the reading from the
 * database into the object model as well as during the writing back of changes
 * into the database. This annotation allows developers to define a named
 * converter that can be used in their mappings. A converter can be defined on
 * an entity class, method, or field.
 *
 * A Converter must be be uniquely identified by name and can be defined at
 * the class, field and property level and can be specified within an Entity,
 * MappedSuperclass and Embeddable class.
 *
 * The usage of a Converter is always specified via the Convert annotation and
 * is supported on a Basic, or ElementCollection mapping.
 *
 * @see org.eclipse.persistence.annotations.Convert
 * @author Guy Pelletier
 * @since Oracle TopLink 11.1.1.0.0
 */
@Target({TYPE, METHOD, FIELD})
@Retention(RUNTIME)
@Repeatable(Converters.class)
public @interface Converter {
    /**
     * (Required) Name this converter. The name should be unique across the
     * whole persistence unit.
     */
    String name();

    /**
     * (Required) The converter class to be used. This class must implement the
     *  org.eclipse.persistence.mappings.converters.Converter interface.
     */
    Class converterClass();
}
