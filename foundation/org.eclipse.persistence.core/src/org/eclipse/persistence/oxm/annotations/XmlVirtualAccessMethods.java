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
// rbarkhouse - 2011 April 11 - 2.3 - Initial implementation
package org.eclipse.persistence.oxm.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * The XmlVirtualAccessMethods annotation is used to indicate that this class has
 * been configured to hold virtual properties.
 */
@Target({TYPE})
@Retention(RUNTIME)
public @interface XmlVirtualAccessMethods {

    /**
     * (Optional) Defines the name of the method used to retrieve virtual properties.
     */
    String getMethod() default "get";

    /**
     * (Optional) Defines the name of the method used to store virtual properties.
     */
    String setMethod() default "set";

    /**
     * (Optional) Configure the way that virtual properties will appear in generated schemas.<br><br>
     * <b>XmlExtensibleSchema.NODES</b> (default) - Virtual properties will appear as individual nodes<br>
     * <b>XmlExtensibleSchema.ANY</b> - An XSD &lt;any&gt; element will be written to the schema to represent all of the defined virtual properties
     */
    XmlVirtualAccessMethodsSchema schema() default XmlVirtualAccessMethodsSchema.NODES;

}
