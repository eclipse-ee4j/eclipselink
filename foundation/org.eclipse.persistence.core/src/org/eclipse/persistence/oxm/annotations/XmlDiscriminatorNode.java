/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
// dmccann - August 25/2009 - 2.2 - Initial implementation
package org.eclipse.persistence.oxm.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * The XmlDiscriminatorNode annotation is used to specify the class
 * indicator field name when using inheritance.
 */
@Target({TYPE})
@Retention(RUNTIME)
public @interface XmlDiscriminatorNode {

    /**
     * (Required) Indicates the the class indicator field name.
     */
    String value();
}
