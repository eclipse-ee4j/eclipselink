/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle = 2.2 - Initial contribution
 ******************************************************************************/
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
 * &nbsp;&nbsp;&nbsp;&nbsp;@XmlElements({@XmlElement(type=String.class), @XmlElement(type=Integer.class)})<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;@XmlPaths({@XmlPath("choice-element/string/text()"), @XmlPath("choice-element/integer/text()")})<br> 
 * &nbsp;&nbsp;&nbsp;&nbsp;public Object choice<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;...<br>
 * &nbsp;}<br><br> 
 * Will create the following Schema:<br>
 * 
 * <pre>
 *    ...
 *    &lt;xsd:choice>
 *       &lt;xsd:element name="choice-element" minOccurs="0">
 *          &lt;xsd:complexType>
 *             &lt;xsd:choice>
 *                &lt;xsd:element name="string" type="xsd:string" minOccurs="0"/>
 *                &lt;xsd:element name="integer" type="xsd:int" minOccurs="0"/>
 *             &lt;/xsd:choice>
 *          &lt;/xsd:complexType>
 *       &lt;/xsd:element>
 *    &lt;/xsd:choice>
 *    ...
 * </pre> 
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface XmlPaths {
    XmlPath[] value();
}
