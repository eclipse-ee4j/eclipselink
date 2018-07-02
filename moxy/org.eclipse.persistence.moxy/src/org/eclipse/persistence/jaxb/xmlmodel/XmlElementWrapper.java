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
// dmccann - June 29/2009 - 2.0 - Initial implementation
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
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" default="##default" /&gt;
 *       &lt;attribute name="namespace" type="{http://www.w3.org/2001/XMLSchema}string" default="##default" /&gt;
 *       &lt;attribute name="nillable" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" /&gt;
 *       &lt;attribute name="required" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "xml-element-wrapper")
public class XmlElementWrapper {

    @XmlAttribute
    protected String name;
    @XmlAttribute
    protected String namespace;
    @XmlAttribute
    protected Boolean nillable;
    @XmlAttribute
    protected Boolean required;

    /**
     * Gets the value of the name property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getName() {
        if (name == null) {
            return "##default";
        } else {
            return name;
        }
    }

    /**
     * Sets the value of the name property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the namespace property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getNamespace() {
        if (namespace == null) {
            return "##default";
        } else {
            return namespace;
        }
    }

    /**
     * Sets the value of the namespace property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setNamespace(String value) {
        this.namespace = value;
    }

    /**
     * Gets the value of the nillable property.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public boolean isNillable() {
        if (nillable == null) {
            return false;
        } else {
            return nillable;
        }
    }

    /**
     * Sets the value of the nillable property.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setNillable(Boolean value) {
        this.nillable = value;
    }

    /**
     * Gets the value of the required property.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public boolean isRequired() {
        if (required == null) {
            return false;
        } else {
            return required;
        }
    }

    /**
     * Sets the value of the required property.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setRequired(Boolean value) {
        this.required = value;
    }

}
