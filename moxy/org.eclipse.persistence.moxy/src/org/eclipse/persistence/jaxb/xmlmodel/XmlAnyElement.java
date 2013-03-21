/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
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
 *         &lt;element ref="{http://www.eclipse.org/eclipselink/xsds/persistence/oxm}xml-access-methods" minOccurs="0"/>
 *         &lt;element ref="{http://www.eclipse.org/eclipselink/xsds/persistence/oxm}xml-java-type-adapter" minOccurs="0"/>
 *         &lt;element ref="{http://www.eclipse.org/eclipselink/xsds/persistence/oxm}xml-properties" minOccurs="0"/>
 *         &lt;element ref="{http://www.eclipse.org/eclipselink/xsds/persistence/oxm}xml-element-refs" minOccurs="0"/>
 *       &lt;/all>
 *       &lt;attribute name="xml-mixed" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="lax" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="dom-handler" type="{http://www.w3.org/2001/XMLSchema}string" default="javax.xml.bind.annotation.W3CDomHandler" />
 *       &lt;attribute name="read-only" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="write-only" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="xml-path" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="container-type" type="{http://www.w3.org/2001/XMLSchema}string" default="##default" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "xmlAccessMethods",
    "xmlJavaTypeAdapter",
    "xmlProperties",
    "xmlElementRefs"
})
public class XmlAnyElement
    extends JavaAttribute
{

    @XmlElement(name = "xml-access-methods")
    protected XmlAccessMethods xmlAccessMethods;
    @XmlElement(name = "xml-java-type-adapter")
    protected XmlJavaTypeAdapter xmlJavaTypeAdapter;
    @XmlElement(name = "xml-properties")
    protected XmlProperties xmlProperties;
    @XmlElement(name = "xml-element-refs")
    protected XmlElementRefs xmlElementRefs;
    @XmlAttribute(name = "xml-mixed")
    protected Boolean xmlMixed;
    @XmlAttribute(name = "lax")
    protected Boolean lax;
    @XmlAttribute(name = "dom-handler")
    protected String domHandler;
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
     * Gets the value of the xmlJavaTypeAdapter property.
     * 
     * @return
     *     possible object is
     *     {@link XmlJavaTypeAdapter }
     *     
     */
    public XmlJavaTypeAdapter getXmlJavaTypeAdapter() {
        return xmlJavaTypeAdapter;
    }

    /**
     * Sets the value of the xmlJavaTypeAdapter property.
     * 
     * @param value
     *     allowed object is
     *     {@link XmlJavaTypeAdapter }
     *     
     */
    public void setXmlJavaTypeAdapter(XmlJavaTypeAdapter value) {
        this.xmlJavaTypeAdapter = value;
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
     * Gets the value of the xmlElementRefs property.
     * 
     * @return
     *     possible object is
     *     {@link XmlElementRefs }
     *     
     */
    public XmlElementRefs getXmlElementRefs() {
        return xmlElementRefs;
    }

    /**
     * Sets the value of the xmlElementRefs property.
     * 
     * @param value
     *     allowed object is
     *     {@link XmlElementRefs }
     *     
     */
    public void setXmlElementRefs(XmlElementRefs value) {
        this.xmlElementRefs = value;
    }

    /**
     * Gets the value of the xmlMixed property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isXmlMixed() {
        if (xmlMixed == null) {
            return false;
        } else {
            return xmlMixed;
        }
    }

    /**
     * Sets the value of the xmlMixed property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setXmlMixed(Boolean value) {
        this.xmlMixed = value;
    }

    /**
     * Gets the value of the lax property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isLax() {
        if (lax == null) {
            return false;
        } else {
            return lax;
        }
    }

    /**
     * Sets the value of the lax property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setLax(Boolean value) {
        this.lax = value;
    }

    /**
     * Gets the value of the domHandler property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDomHandler() {
        if (domHandler == null) {
            return "javax.xml.bind.annotation.W3CDomHandler";
        } else {
            return domHandler;
        }
    }

    /**
     * Sets the value of the domHandler property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDomHandler(String value) {
        this.domHandler = value;
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
