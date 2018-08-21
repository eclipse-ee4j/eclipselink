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

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
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
 *       &lt;sequence&gt;
 *         &lt;element ref="{http://www.eclipse.org/eclipselink/xsds/persistence/oxm}xml-enum-value" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attribute name="java-enum" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="value" type="{http://www.w3.org/2001/XMLSchema}string" default="java.lang.String" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "xmlEnumValue"
})
@XmlRootElement(name = "xml-enum")
public class XmlEnum {

    @XmlElement(name = "xml-enum-value")
    protected List<XmlEnumValue> xmlEnumValue;
    @XmlAttribute(name = "java-enum", required = true)
    protected String javaEnum;
    @XmlAttribute
    protected String value;

    /**
     * Gets the value of the xmlEnumValue property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the xmlEnumValue property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getXmlEnumValue().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link XmlEnumValue }
     *
     *
     */
    public List<XmlEnumValue> getXmlEnumValue() {
        if (xmlEnumValue == null) {
            xmlEnumValue = new ArrayList<XmlEnumValue>();
        }
        return this.xmlEnumValue;
    }

    /**
     * Gets the value of the javaEnum property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getJavaEnum() {
        return javaEnum;
    }

    /**
     * Sets the value of the javaEnum property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setJavaEnum(String value) {
        this.javaEnum = value;
    }

    /**
     * Gets the value of the value property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getValue() {
        if (value == null) {
            return "java.lang.String";
        } else {
            return value;
        }
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

}
