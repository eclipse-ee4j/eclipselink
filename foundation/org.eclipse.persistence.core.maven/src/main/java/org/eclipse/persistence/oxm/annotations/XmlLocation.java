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
//  - rbarkhouse - 25 August 2011 - 2.4 - Initial implementation
package org.eclipse.persistence.oxm.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * <p>XmlLocation can be used on a property of type Locator, to provide information about
 * the XML location (line and column number, source location) that the owning object was unmarshalled from.</p>
 *
 * <p>This annotation can be used on:</p>
 * <ul>
 *      <li>a Field of type org.xml.sax.Locator</li>
 *      <li>a set or get Method that takes/returns a single parameter, of type org.xml.sax.Locator</li>
 * </ul>
 *
 * <p>If this property is also marked as XmlTransient, then no Location information
 * will be marshalled to XML, however the Location will be set on the object when unmarshalling.</p>
 *
 * @see org.xml.sax.Locator
 */
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface XmlLocation {}
