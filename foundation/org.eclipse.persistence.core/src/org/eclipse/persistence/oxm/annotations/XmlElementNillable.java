/*******************************************************************************
 * Copyright (c) 2014, 2015  Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Martin Vojtek - July 7/2014
 ******************************************************************************/
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
 * @see javax.xml.bind.annotation.XmlElement
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
