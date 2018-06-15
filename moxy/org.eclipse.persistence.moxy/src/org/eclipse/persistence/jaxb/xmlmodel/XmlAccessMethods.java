/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
// David McCann = 2.1 - Initial contribution
package org.eclipse.persistence.jaxb.xmlmodel;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for xml-access-methods complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="xml-access-methods"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="get-method" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="set-method" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "xml-access-methods")
public class XmlAccessMethods {

    @XmlAttribute(name = "get-method")
    protected String getMethod;
    @XmlAttribute(name = "set-method")
    protected String setMethod;

    /**
     * Gets the value of the getMethod property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getGetMethod() {
        return getMethod;
    }

    /**
     * Sets the value of the getMethod property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setGetMethod(String value) {
        this.getMethod = value;
    }

    /**
     * Gets the value of the setMethod property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSetMethod() {
        return setMethod;
    }

    /**
     * Sets the value of the setMethod property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSetMethod(String value) {
        this.setMethod = value;
    }

}
