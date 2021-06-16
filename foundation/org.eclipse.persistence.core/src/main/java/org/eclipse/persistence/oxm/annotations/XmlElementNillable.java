/*
 * Copyright (c) 2014, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Martin Vojtek - July 7/2014
package org.eclipse.persistence.oxm.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <b>Purpose:</b> Provide a means of setting nillable on type and package
 * level, which is not possible with standard XmlElement annotation.
 * <p>
 * Type level annotation overrides package level annotation.
 * </p>
 * <p>
 * Standard XmlElement with nillable attribute overrides the value of
 * XmlElementNillable annotation.
 * </p>
 *
 * @see jakarta.xml.bind.annotation.XmlElement
 */
@Target({ ElementType.TYPE, ElementType.PACKAGE })
@Retention(RetentionPolicy.RUNTIME)
public @interface XmlElementNillable {

    /**
     * (Optional) Customize the element declaration to be nillable.
     * If nillable() is true, then the JavaBean property is mapped to a XML Schema nillable element declaration.
     */
    boolean nillable() default true;
}
