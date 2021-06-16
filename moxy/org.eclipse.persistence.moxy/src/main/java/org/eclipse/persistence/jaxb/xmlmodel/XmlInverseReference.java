/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
// Matt MacIvor = 2.1 - Initial contribution
package org.eclipse.persistence.jaxb.xmlmodel;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.eclipse.org/eclipselink/xsds/persistence/oxm}java-attribute"&gt;
 *       &lt;all&gt;
 *         &lt;element ref="{http://www.eclipse.org/eclipselink/xsds/persistence/oxm}xml-access-methods" minOccurs="0"/&gt;
 *         &lt;element ref="{http://www.eclipse.org/eclipselink/xsds/persistence/oxm}xml-properties" minOccurs="0"/&gt;
 *       &lt;/all&gt;
 *       &lt;attribute name="mapped-by" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="container-type" type="{http://www.w3.org/2001/XMLSchema}string" default="##default" /&gt;
 *       &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" default="##default" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "xmlAccessMethods",
    "xmlProperties"
})
public class XmlInverseReference
    extends JavaAttribute
{

    @XmlElement(name = "xml-access-methods")
    protected XmlAccessMethods xmlAccessMethods;
    @XmlElement(name = "xml-properties")
    protected XmlProperties xmlProperties;
    @XmlAttribute(name = "mapped-by", required = true)
    protected String mappedBy;
    @XmlAttribute(name = "container-type")
    protected String containerType;
    @XmlAttribute(name = "type")
    protected String type;

    /**
     * Gets the value of the xmlAccessMethods property.
     *
     * @return
     *     possible object is
     *     {@link XmlAccessMethods }
     *
     */
    public XmlAccessMethods getXmlAccessMethods() {
        return xmlAccessMethods;
    }

    /**
     * Sets the value of the xmlAccessMethods property.
     *
     * @param value
     *     allowed object is
     *     {@link XmlAccessMethods }
     *
     */
    public void setXmlAccessMethods(XmlAccessMethods value) {
        this.xmlAccessMethods = value;
    }

    /**
     * Gets the value of the xmlProperties property.
     *
     * @return
     *     possible object is
     *     {@link XmlProperties }
     *
     */
    public XmlProperties getXmlProperties() {
        return xmlProperties;
    }

    /**
     * Sets the value of the xmlProperties property.
     *
     * @param value
     *     allowed object is
     *     {@link XmlProperties }
     *
     */
    public void setXmlProperties(XmlProperties value) {
        this.xmlProperties = value;
    }

    /**
     * Gets the value of the mappedBy property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getMappedBy() {
        return mappedBy;
    }

    /**
     * Sets the value of the mappedBy property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setMappedBy(String value) {
        this.mappedBy = value;
    }

    /**
     * Gets the value of the containerType property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getContainerType() {
        if (containerType == null) {
            return "##default";
        } else {
            return containerType;
        }
    }

    /**
     * Sets the value of the containerType property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setContainerType(String value) {
        this.containerType = value;
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
            return "##default";
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

}
