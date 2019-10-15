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
// rbarkhouse - 2011 April 12 - 2.3 - Initial implementation
package org.eclipse.persistence.jaxb.xmlmodel;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="get-method" type="{http://www.w3.org/2001/XMLSchema}string" default="get" /&gt;
 *       &lt;attribute name="set-method" type="{http://www.w3.org/2001/XMLSchema}string" default="set" /&gt;
 *       &lt;attribute name="schema" type="{http://www.eclipse.org/eclipselink/xsds/persistence/oxm}xml-virtual-access-methods-schema" default="NODES" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "xml-virtual-access-methods")
public class XmlVirtualAccessMethods {

    @XmlAttribute(name = "get-method")
    protected String getMethod;
    @XmlAttribute(name = "set-method")
    protected String setMethod;
    @XmlAttribute
    protected XmlVirtualAccessMethodsSchema schema;

    /**
     * Gets the value of the getMethod property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getGetMethod() {
        if (getMethod == null) {
            return "get";
        } else {
            return getMethod;
        }
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
        if (setMethod == null) {
            return "set";
        } else {
            return setMethod;
        }
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

    /**
     * Gets the value of the schema property.
     *
     * @return
     *     possible object is
     *     {@link XmlVirtualAccessMethodsSchema }
     *
     */
    public XmlVirtualAccessMethodsSchema getSchema() {
        if (schema == null) {
            return XmlVirtualAccessMethodsSchema.NODES;
        } else {
            return schema;
        }
    }

    /**
     * Sets the value of the schema property.
     *
     * @param value
     *     allowed object is
     *     {@link XmlVirtualAccessMethodsSchema }
     *
     */
    public void setSchema(XmlVirtualAccessMethodsSchema value) {
        this.schema = value;
    }

}
