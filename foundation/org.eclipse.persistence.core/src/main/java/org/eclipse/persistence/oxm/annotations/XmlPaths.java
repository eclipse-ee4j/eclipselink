/*
 * Copyright (c) 2011, 2024 Oracle and/or its affiliates. All rights reserved.
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
 * This annotation is used in conjunction with an XmlElements annotation to
 * specify an {@linkplain XmlPath} for each of the XmlElement annotations in the XmlElements.
 * The number of {@linkplain XmlPath} annotations must be the same as the number of XmlElement annotations
 * and the order must be the same.
 *
 * <p><b>Example:</b>
 * {@snippet :
 *  @XmlRootElement(name="customer")
 *  public class Customer {
 *      ...
 *      @XmlElements({
 *          @XmlElement(type=String.class),
 *          @XmlElement(type=Integer.class)
 *      })
 *      @XmlPaths({
 *          @XmlPath("choice-element/string/text()"),
 *          @XmlPath("choice-element/integer/text()")
 *      })
 *      public Object choice;
 *      ...
 *  }
 * }
 * Will create the following Schema:
 * {@snippet lang="XML":
 *  ...
 *    <xsd:choice>
 *      <xsd:element name="choice-element" minOccurs="0">
 *        <xsd:complexType>
 *          <xsd:choice>
 *            <xsd:element name="string" type="xsd:string" minOccurs="0"/>
 *            <xsd:element name="integer" type="xsd:int" minOccurs="0"/>
 *          </xsd:choice>
 *        </xsd:complexType>
 *       </xsd:element>
 *    </xsd:choice>
 *  ...
 * }
 *
 * @see jakarta.xml.bind.annotation.XmlElement
 * @see jakarta.xml.bind.annotation.XmlElements
 * @see XmlPath
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface XmlPaths {
    /**
     * An array of XmlPath annotations.
     */
    XmlPath[] value();
}
