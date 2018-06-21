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
//     Oracle = 2.2 - Initial contribution
package org.eclipse.persistence.oxm.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *<p><b>Purpose</b>: This annotation is used in conjunction with an XmlElements annotation to
 * specify an XmlPath for each of the XmlElement annotations in the XmlElements.
 * The number of XmlPath annotations must be the same as the number of XmlElement annotations
 * and the order must be the same.
 *
 * <p><b>Example:</b><br>
 * <code>
 * &nbsp;@XmlRootElement(name="customer")<br>
 * &nbsp;public class Customer {<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;...<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;@XmlElements({@literal {@XmlElement(type=String.class), @XmlElement(type=Integer.class)}})<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;@XmlPaths({@literal {@XmlPath("choice-element/string/text()"), @XmlPath("choice-element/integer/text()")}})<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;public Object choice<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;...<br>
 * &nbsp;}</code><br><br>
 * Will create the following Schema:<br>
 *
 * <pre>
 *    ...
 *    &lt;xsd:choice&gt;
 *       &lt;xsd:element name="choice-element" minOccurs="0"&gt;
 *          &lt;xsd:complexType&gt;
 *             &lt;xsd:choice&gt;
 *                &lt;xsd:element name="string" type="xsd:string" minOccurs="0"/&gt;
 *                &lt;xsd:element name="integer" type="xsd:int" minOccurs="0"/&gt;
 *             &lt;/xsd:choice&gt;
 *          &lt;/xsd:complexType&gt;
 *       &lt;/xsd:element&gt;
 *    &lt;/xsd:choice&gt;
 *    ...
 * </pre>
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface XmlPaths {
    XmlPath[] value();
}
