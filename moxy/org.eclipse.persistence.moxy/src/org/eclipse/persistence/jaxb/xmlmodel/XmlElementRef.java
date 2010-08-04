/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * dmccann - June 29/2009 - 2.0 - Initial implementation
 ******************************************************************************/
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
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.eclipse.org/eclipselink/xsds/persistence/oxm}java-attribute">
 *       &lt;all>
 *         &lt;element ref="{http://www.eclipse.org/eclipselink/xsds/persistence/oxm}xml-element-wrapper" minOccurs="0"/>
 *         &lt;element ref="{http://www.eclipse.org/eclipselink/xsds/persistence/oxm}xml-properties" minOccurs="0"/>
 *       &lt;/all>
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" default="##default" />
 *       &lt;attribute name="namespace" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" default="javax.xml.bind.annotation.XmlElementRef.DEFAULT" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "xmlElementWrapper",
    "xmlProperties"
})
public class XmlElementRef
    extends JavaAttribute
{

    @XmlElement(name = "xml-element-wrapper")
    protected XmlElementWrapper xmlElementWrapper;
    @XmlElement(name = "xml-properties")
    protected XmlProperties xmlProperties;
    @XmlAttribute(name = "name")
    protected String name;
    @XmlAttribute(name = "namespace")
    protected String namespace;
    @XmlAttribute(name = "type")
    protected String type;

    /**
     * Gets the value of the xmlElementWrapper property.
     * 
     * @return
     *     possible object is
     *     {@link XmlElementWrapper }
     *     
     */
    public XmlElementWrapper getXmlElementWrapper() {
        return xmlElementWrapper;
    }

    /**
     * Sets the value of the xmlElementWrapper property.
     * 
     * @param value
     *     allowed object is
     *     {@link XmlElementWrapper }
     *     
     */
    public void setXmlElementWrapper(XmlElementWrapper value) {
        this.xmlElementWrapper = value;
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
            return "";
        }
        return namespace;
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
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        if (type == null) {
            return "javax.xml.bind.annotation.XmlElementRef.DEFAULT";
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
