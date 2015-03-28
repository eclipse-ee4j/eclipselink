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
 *     Martin Vojtek - 2.6 - Initial contribution
 ******************************************************************************/
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
 * @see javax.xml.bind.annotation.XmlValue
 */
@Target({METHOD, FIELD})
@Retention(RUNTIME)
public @interface XmlValueExtension {
}
