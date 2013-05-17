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
 * Denise Smith - May 2013 - Initial implementation
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
 *         &lt;element ref="{http://www.eclipse.org/eclipselink/xsds/persistence/oxm}xml-element-wrapper" minOccurs="0"/>
 *         &lt;element ref="{http://www.eclipse.org/eclipselink/xsds/persistence/oxm}xml-properties" minOccurs="0"/>
 *       &lt;/all>
 *       &lt;attribute name="xml-path" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="read-only" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="write-only" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="container-type" type="{http://www.w3.org/2001/XMLSchema}string" default="##default" />
 *       &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" default="##default" />
 *       &lt;attribute name="isAttribute" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="java-variable-attribute" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="nillable" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="required" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
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
    "xmlElementWrapper",
    "xmlProperties"
})
public class XmlVariableNode
    extends JavaAttribute
{

    @XmlElement(name = "xml-access-methods")
    protected XmlAccessMethods xmlAccessMethods;
    @XmlElement(name = "xml-java-type-adapter")
    protected XmlJavaTypeAdapter xmlJavaTypeAdapter;
    @XmlElement(name = "xml-element-wrapper")
    protected XmlElementWrapper xmlElementWrapper;
    @XmlElement(name = "xml-properties")
    protected XmlProperties xmlProperties;
    @XmlAttribute(name = "xml-path")
    protected String xmlPath;
    @XmlAttribute(name = "read-only")
    protected Boolean readOnly;
    @XmlAttribute(name = "write-only")
    protected Boolean writeOnly;
    @XmlAttribute(name = "container-type")
    protected String containerType;
    @XmlAttribute(name = "type")
    protected String type;
    @XmlAttribute(name = "isAttribute")
    protected Boolean isAttribute;
    @XmlAttribute(name = "java-variable-attribute")
    protected String javaVariableAttribute;
    @XmlAttribute(name = "nillable")
    protected Boolean nillable;
    @XmlAttribute(name = "required")
    protected Boolean required;

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

    /**
     * Gets the value of the isAttribute property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isIsAttribute() {
        if (isAttribute == null) {
            return false;
        } else {
            return isAttribute;
        }
    }

    /**
     * Sets the value of the isAttribute property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsAttribute(Boolean value) {
        this.isAttribute = value;
    }

    /**
     * Gets the value of the javaVariableAttribute property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getJavaVariableAttribute() {
        return javaVariableAttribute;
    }

    /**
     * Sets the value of the javaVariableAttribute property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setJavaVariableAttribute(String value) {
        this.javaVariableAttribute = value;
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
