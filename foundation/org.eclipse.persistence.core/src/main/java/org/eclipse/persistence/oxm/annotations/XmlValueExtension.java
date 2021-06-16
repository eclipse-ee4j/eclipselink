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
//     Martin Vojtek - 2.6 - Initial contribution
package org.eclipse.persistence.oxm.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * <b>Purpose:</b> Provide a way to allow classes, which contain property annotated with XmlValue to extend classes other than java.lang.Object.
 * By default, all classes containing property annotated with XmlValue annotation are restricted to extends java.lang.Object type.
 * <p>If XmlValueExtension annotation is used, there is no inheritance restriction.</p>
 * <p>Using XmlValueExtension provides a way how to achieve backward compatibility with EclipseLink 2.5.x and before.</p>
 * <p>When using xml bindings with XmlValue property, it has same behavior as when XmlValueExtension is used.
 * It means that there is no need to specify XmlValueExtension in xml, because the behavior is provided by default.
 * </p>
 * @see jakarta.xml.bind.annotation.XmlValue
 */
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface XmlValueExtension {
}
