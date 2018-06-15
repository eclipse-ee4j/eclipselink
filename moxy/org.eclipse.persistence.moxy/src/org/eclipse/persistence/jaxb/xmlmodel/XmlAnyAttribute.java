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
import javax.xml.bind.annotation.XmlElement;
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
 *       &lt;all&gt;
 *         &lt;element ref="{http://www.eclipse.org/eclipselink/xsds/persistence/oxm}xml-access-methods" minOccurs="0"/&gt;
 *         &lt;element ref="{http://www.eclipse.org/eclipselink/xsds/persistence/oxm}xml-properties" minOccurs="0"/&gt;
 *       &lt;/all&gt;
 *       &lt;attribute name="read-only" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" /&gt;
 *       &lt;attribute name="write-only" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" &gt;
 *       &lt;attribute name="xml-path" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="container-type" type="{http://www.w3.org/2001/XMLSchema}string" default="##default" /&gt;
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
public class XmlAnyAttribute
    extends JavaAttribute
{

    @XmlElement(name = "xml-access-methods")
    protected XmlAccessMethods xmlAccessMethods;
    @XmlElement(name = "xml-properties")
    protected XmlProperties xmlProperties;
    @XmlAttribute(name = "read-only")
    protected Boolean readOnly;
    @XmlAttribute(name = "write-only")
    protected Boolean writeOnly;
    @XmlAttribute(name = "xml-path")
    protected String xmlPath;
    @XmlAttribute(name = "container-type")
    protected String containerType;

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
     * Gets the value of the readOnly property.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public boolean isReadOnly() {
        if (readOnly == null) {
            return false;
        } else {
            return readOnly;
        }
    }

    /**
     * Sets the value of the readOnly property.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setReadOnly(Boolean value) {
        this.readOnly = value;
    }

    /**
     * Indicates if readOnly has been set, i.e. is non-null.
     *
     * @return true if readOnly is non-null, false otherwise
     */
    public boolean isSetReadOnly() {
        return this.readOnly != null;
    }

    /**
     * Gets the value of the writeOnly property.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public boolean isWriteOnly() {
        if (writeOnly == null) {
            return false;
        } else {
            return writeOnly;
        }
    }

    /**
     * Sets the value of the writeOnly property.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setWriteOnly(Boolean value) {
        this.writeOnly = value;
    }

    /**
     * Indicates if writeOnly has been set, i.e. is non-null.
     *
     * @return true if writeOnly is non-null, false otherwise
     */
    public boolean isSetWriteOnly() {
        return this.writeOnly != null;
    }

    /**
     * Gets the value of the xmlPath property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getXmlPath() {
        return xmlPath;
    }

    /**
     * Sets the value of the xmlPath property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setXmlPath(String value) {
        this.xmlPath = value;
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

}
