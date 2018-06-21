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
//     Matt MacIvor - 2.5.1 - Initial Implementation
package org.eclipse.persistence.oxm.annotations;

/**
 * <p><b>Purpose</b>: This annotation indicates that the value of a specified attribute on a
 * referenced class should be used as the tag name when marshalling/unmarshalling the object(s)
 * by EclipseLink.
 */
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({METHOD, FIELD})
@Retention(RUNTIME)

public @interface XmlVariableNode {

    /**
     * The name of the attribute on the referenced type to be used as the tag name
     */
    String value() default "##default";

    /**
     * The class being referenced
     */
    Class type() default DEFAULT.class;

    /**
     * Used in {@link XmlVariableNode#type()} to
     * signal that the type be inferred from the signature
     * of the property.
     */
    static final class DEFAULT {}

    boolean attribute() default false;

}
