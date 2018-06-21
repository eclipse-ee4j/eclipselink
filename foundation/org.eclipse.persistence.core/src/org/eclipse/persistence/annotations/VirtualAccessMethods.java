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
//     tware - initial implementation as part of extensibility feature
package org.eclipse.persistence.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Specifies that this class contains virtual attributes.
 * This annotation is used in an EclipseLink-specific way to define
 * access methods used by mappings with accessType=VIRTUAL.
 * The xml-equivalent is the {@literal <access-methods>} tag
 */
@Documented
@Target(TYPE)
@Retention(RUNTIME)
public @interface VirtualAccessMethods {

    /**
     * (Optional) The name of the getter method to use for the virtual property
     * This method must take a single java.lang.String parameter and return a java.lang.Object.
     * If setMethod is specified, getMethod must be specified
     */
    String get() default "get";

    /**
     * (Optional) The name of the setter method to use for the virtual property
     * This method must take a java.lang.String parameter and a java.lang.Object parameter.
     * If getMethod is specified, setMethod must be specified
     */
    String set() default "set";
}
