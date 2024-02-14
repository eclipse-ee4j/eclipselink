/*
 * Copyright (c) 2011, 2024 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle = 2.2 - Initial contribution
package org.eclipse.persistence.oxm.annotations;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation for a user defined property.
 * A single XmlProperty may be specified directly on a mapped attribute or its get/set method.
 * Multiple occurrences of XmlProperty can be wrapped into {@linkplain XmlProperties} annotation.
 */
@Target({METHOD, FIELD, TYPE})
@Retention(RUNTIME)
@Repeatable(XmlProperties.class)
public @interface XmlProperty {
    /**
     * Property name.
     */
    String name();

    /**
     * String representation of XmlProperty value,
     * converted to an instance of {@linkplain #valueType}.
     */
    String value();

    /**
     * Property value type.
     * <p>
     * The value converted to valueType by {@linkplain org.eclipse.persistence.internal.oxm.ConversionManager}.
     * <p>
     * If specified must be a simple type that could be handled by
     * {@linkplain org.eclipse.persistence.internal.oxm.ConversionManager}:
     * numerical, boolean, temporal.
     */
    Class<?> valueType() default String.class;
}
