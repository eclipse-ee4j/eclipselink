/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle = 2.2 - Initial contribution
package org.eclipse.persistence.oxm.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Annotation for a user defined property.
 * A single Property may be specified directly on a mapped attribute or its
 * get/set method. Multiple Properties should be wrapped into Properties
 * annotation.
 */
@Target({METHOD, FIELD, TYPE})
@Retention(RUNTIME)
public @interface XmlProperty {
    /**
     * Property name.
     */
    String name();

    /**
     * String representation of Property value,
     * converted to an instance of valueType.
     */
    String value();

    /**
     * Property value type.
     * The value converted to valueType by ConversionManager.
     * If specified must be a simple type that could be handled by ConversionManager:
     * numerical, boolean, temporal.
     */
    Class valueType() default String.class;
}
