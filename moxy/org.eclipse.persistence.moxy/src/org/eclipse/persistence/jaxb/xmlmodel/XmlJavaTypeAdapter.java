/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
// dmccann - June 29/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.jaxb.xmlmodel;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.eclipse.org/eclipselink/xsds/persistence/oxm}java-attribute"&gt;
 *       &lt;attribute name="value" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" default="javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter.DEFAULT" /&gt;
 *       &lt;attribute name="value-type" type="{http://www.w3.org/2001/XMLSchema}string" default="javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter.DEFAULT" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
public class XmlJavaTypeAdapter
    extends JavaAttribute
{

    @XmlAttribute(required = true)
    protected String value;
    @XmlAttribute
    protected String type;
    @XmlAttribute(name = "value-type")
    protected String valueType;

    /**
     * Gets the value of the value property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the value of the type property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getType() {
        if (type == null) {
            return "javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter.DEFAULT";
        } else {
            return type;
        }
    }

    /**
     * Sets the value of the type property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Gets the value of the valueType property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getValueType() {
        if (valueType == null) {
            return "javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter.DEFAULT";
        } else {
            return valueType;
        }
    }

    /**
     * Sets the value of the valueType property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setValueType(String value) {
        this.valueType = value;
    }

}
