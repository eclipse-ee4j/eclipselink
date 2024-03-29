/*
 * Copyright (c) 2014, 2024 Oracle and/or its affiliates. All rights reserved.
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
//     Martin Vojtek - July 7/2014
package org.eclipse.persistence.oxm.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Provide a means of setting nillable on type and package level,
 * which is not possible with standard {@linkplain jakarta.xml.bind.annotation.XmlElement} annotation.
 * <p>
 * Type level annotation overrides package level annotation.
 * <p>
 * Standard {@linkplain jakarta.xml.bind.annotation.XmlElement} with nillable attribute overrides
 * the value of XmlElementNillable annotation.
 *
 * @see jakarta.xml.bind.annotation.XmlElement
 */
@Target({ ElementType.TYPE, ElementType.PACKAGE })
@Retention(RetentionPolicy.RUNTIME)
public @interface XmlElementNillable {
    /**
     * Customize the element declaration to be nillable.
     * <p>
     * If nillable() is true, then the JavaBean property is mapped to an XML Schema nillable element declaration.
     */
    boolean nillable() default true;
}
