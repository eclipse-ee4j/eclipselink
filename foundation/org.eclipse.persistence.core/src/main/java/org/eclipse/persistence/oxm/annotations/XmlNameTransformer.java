/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Denise Smith - EclipseLink 2.3 - Initial Implementation
package org.eclipse.persistence.oxm.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.eclipse.persistence.oxm.XMLNameTransformer;

/**
 * An XmlNameTransformer allows for a user defined class to transform names.
 * The class has the following restriction:
 *  - It must implement the org.eclipse.persistence.oxm.XmlNameTransformer interface
 *
 * This method will be used to decide what XML name to create from a Java class or attribute name
 *
 * The XmlNameTransformer must only be set on a package
 */
@Target({ElementType.PACKAGE, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface XmlNameTransformer {

    /**
     * (Required) Defines the name of the XML name transformer that should be
     * applied to names.
     */
    Class <? extends XMLNameTransformer> value();
}
