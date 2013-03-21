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

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
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
 *         &lt;element ref="{http://www.eclipse.org/eclipselink/xsds/persistence/oxm}xml-abstract-null-policy" minOccurs="0"/>
 *         &lt;element ref="{http://www.eclipse.org/eclipselink/xsds/persistence/oxm}xml-access-methods" minOccurs="0"/>
 *         &lt;element ref="{http://www.eclipse.org/eclipselink/xsds/persistence/oxm}xml-element-wrapper" minOccurs="0"/>
 *         &lt;element name="xml-inverse-reference" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="mapped-by" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element ref="{http://www.eclipse.org/eclipselink/xsds/persistence/oxm}xml-java-type-adapter" minOccurs="0"/>
 *         &lt;element ref="{http://www.eclipse.org/eclipselink/xsds/persistence/oxm}xml-map" minOccurs="0"/>
 *         &lt;element ref="{http://www.eclipse.org/eclipselink/xsds/persistence/oxm}xml-properties" minOccurs="0"/>
 *         &lt;element ref="{http://www.eclipse.org/eclipselink/xsds/persistence/oxm}xml-schema-type" minOccurs="0"/>
 *       &lt;/all>
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" default="##default" />
 *       &lt;attribute name="namespace" type="{http://www.w3.org/2001/XMLSchema}string" default="##default" />
 *       &lt;attribute name="default-value" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="nillable" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="required" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="container-type" type="{http://www.w3.org/2001/XMLSchema}string" default="##default" />
 *       &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" default="javax.xml.bind.annotation.XmlElement.DEFAULT" />
 *       &lt;attribute name="xml-id" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="xml-idref" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="xml-key" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="xml-list" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="xml-inline-binary-data" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="xml-attachment-ref" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="xml-mime-type" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="read-only" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="write-only" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="cdata" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="xml-path" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="xml-location" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "xmlAbstractNullPolicy",
    "xmlAccessMethods",
    "xmlElementWrapper",
    "xmlInverseReference",
    "xmlJavaTypeAdapter",
    "xmlMap",
    "xmlProperties",
    "xmlSchemaType"
})
public class XmlElement
    extends JavaAttribute
{

    @XmlElementRef(name = "xml-abstract-null-policy", namespace = "http://www.eclipse.org/eclipselink/xsds/persistence/oxm", type = JAXBElement.class)
    protected JAXBElement<? extends XmlAbstractNullPolicy> xmlAbstractNullPolicy;
    @javax.xml.bind.annotation.XmlElement(name = "xml-access-methods")
    protected XmlAccessMethods xmlAccessMethods;
    @javax.xml.bind.annotation.XmlElement(name = "xml-element-wrapper")
    protected XmlElementWrapper xmlElementWrapper;
    @javax.xml.bind.annotation.XmlElement(name = "xml-inverse-reference")
    protected XmlElement.XmlInverseReference xmlInverseReference;
    @javax.xml.bind.annotation.XmlElement(name = "xml-java-type-adapter")
    protected XmlJavaTypeAdapter xmlJavaTypeAdapter;
    @javax.xml.bind.annotation.XmlElement(name = "xml-map")
    protected XmlMap xmlMap;
    @javax.xml.bind.annotation.XmlElement(name = "xml-properties")
    protected XmlProperties xmlProperties;
    @javax.xml.bind.annotation.XmlElement(name = "xml-schema-type")
    protected XmlSchemaType xmlSchemaType;
    @XmlAttribute(name = "name")
    protected String name;
    @XmlAttribute(name = "namespace")
    protected String namespace;
    @XmlAttribute(name = "default-value")
    protected String defaultValue;
    @XmlAttribute(name = "nillable")
    protected Boolean nillable;
    @XmlAttribute(name = "required")
    protected Boolean required;
    @XmlAttribute(name = "container-type")
    protected String containerType;
    @XmlAttribute(name = "type")
    protected String type;
    @XmlAttribute(name = "xml-id")
    protected Boolean xmlId;
    @XmlAttribute(name = "xml-idref")
    protected Boolean xmlIdref;
    @XmlAttribute(name = "xml-key")
    protected Boolean xmlKey;
    @XmlAttribute(name = "xml-list")
    protected Boolean xmlList;
    @XmlAttribute(name = "xml-inline-binary-data")
    protected Boolean xmlInlineBinaryData;
    @XmlAttribute(name = "xml-attachment-ref")
    protected Boolean xmlAttachmentRef;
    @XmlAttribute(name = "xml-mime-type")
    protected String xmlMimeType;
    @XmlAttribute(name = "read-only")
    protected Boolean readOnly;
    @XmlAttribute(name = "write-only")
    protected Boolean writeOnly;
    @XmlAttribute(name = "cdata")
    protected Boolean cdata;
    @XmlAttribute(name = "xml-path")
    protected String xmlPath;
    @XmlAttribute(name = "xml-location")
    protected Boolean xmlLocation;

    /**
     * Gets the value of the xmlAbstractNullPolicy property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link XmlNullPolicy }{@code >}
     *     {@link JAXBElement }{@code <}{@link XmlAbstractNullPolicy }{@code >}
     *     {@link JAXBElement }{@code <}{@link XmlIsSetNullPolicy }{@code >}
     *     
     */
    public JAXBElement<? extends XmlAbstractNullPolicy> getXmlAbstractNullPolicy() {
        return xmlAbstractNullPolicy;
    }

    /**
     * Sets the value of the xmlAbstractNullPolicy property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link XmlNullPolicy }{@code >}
     *     {@link JAXBElement }{@code <}{@link XmlAbstractNullPolicy }{@code >}
     *     {@link JAXBElement }{@code <}{@link XmlIsSetNullPolicy }{@code >}
     *     
     */
    public void setXmlAbstractNullPolicy(JAXBElement<? extends XmlAbstractNullPolicy> value) {
        this.xmlAbstractNullPolicy = ((JAXBElement<? extends XmlAbstractNullPolicy> ) value);
    }

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
     * Gets the value of the xmlInverseReference property.
     * 
     * @return
     *     possible object is
     *     {@link XmlElement.XmlInverseReference }
     *     
     */
    public XmlElement.XmlInverseReference getXmlInverseReference() {
        return xmlInverseReference;
    }

    /**
     * Sets the value of the xmlInverseReference property.
     * 
     * @param value
     *     allowed object is
     *     {@link XmlElement.XmlInverseReference }
     *     
     */
    public void setXmlInverseReference(XmlElement.XmlInverseReference value) {
        this.xmlInverseReference = value;
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
     * Gets the value of the xmlMap property.
     * 
     * @return
     *     possible object is
     *     {@link XmlMap }
     *     
     */
    public XmlMap getXmlMap() {
        return xmlMap;
    }

    /**
     * Sets the value of the xmlMap property.
     * 
     * @param value
     *     allowed object is
     *     {@link XmlMap }
     *     
     */
    public void setXmlMap(XmlMap value) {
        this.xmlMap = value;
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
     * Gets the value of the xmlSchemaType property.
     * 
     * @return
     *     possible object is
     *     {@link XmlSchemaType }
     *     
     */
    public XmlSchemaType getXmlSchemaType() {
        return xmlSchemaType;
    }

    /**
     * Sets the value of the xmlSchemaType property.
     * 
     * @param value
     *     allowed object is
     *     {@link XmlSchemaType }
     *     
     */
    public void setXmlSchemaType(XmlSchemaType value) {
        this.xmlSchemaType = value;
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
     * Gets the value of the defaultValue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDefaultValue() {
        if (defaultValue == null) {
            defaultValue = "\u0000";
        }
        return defaultValue;
    }

    /**
     * Sets the value of the defaultValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDefaultValue(String value) {
        this.defaultValue = value;
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
            return "javax.xml.bind.annotation.XmlElement.DEFAULT";
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
     * Gets the value of the xmlId property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isXmlId() {
        if (xmlId == null) {
            return false;
        } else {
            return xmlId;
        }
    }

    /**
     * Sets the value of the xmlId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setXmlId(Boolean value) {
        this.xmlId = value;
    }

    /**
     * Gets the value of the xmlIdref property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isXmlIdref() {
        if (xmlIdref == null) {
            return false;
        } else {
            return xmlIdref;
        }
    }

    /**
     * Sets the value of the xmlIdref property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setXmlIdref(Boolean value) {
        this.xmlIdref = value;
    }

    /**
     * Gets the value of the xmlKey property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isXmlKey() {
        if (xmlKey == null) {
            return false;
        } else {
            return xmlKey;
        }
    }

    /**
     * Sets the value of the xmlKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setXmlKey(Boolean value) {
        this.xmlKey = value;
    }

    /**
     * Gets the value of the xmlList property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isXmlList() {
        if (xmlList == null) {
            return false;
        } else {
            return xmlList;
        }
    }

    /**
     * Sets the value of the xmlList property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setXmlList(Boolean value) {
        this.xmlList = value;
    }

    /**
     * Indicates if xmlList has been set, i.e. is non-null.
     * 
     * @return true if xmlList is not null, false otherwise
     */
    public boolean isSetXmlList() {
        return xmlList != null;
    }
    
    /**
     * Gets the value of the xmlInlineBinaryData property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isXmlInlineBinaryData() {
        if (xmlInlineBinaryData == null) {
            return false;
        } else {
            return xmlInlineBinaryData;
        }
    }

    /**
     * Sets the value of the xmlInlineBinaryData property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setXmlInlineBinaryData(Boolean value) {
        this.xmlInlineBinaryData = value;
    }

    /**
     * Gets the value of the xmlAttachmentRef property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isXmlAttachmentRef() {
        if (xmlAttachmentRef == null) {
            return false;
        } else {
            return xmlAttachmentRef;
        }
    }

    /**
     * Sets the value of the xmlAttachmentRef property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setXmlAttachmentRef(Boolean value) {
        this.xmlAttachmentRef = value;
    }

    /**
     * Gets the value of the xmlMimeType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getXmlMimeType() {
        return xmlMimeType;
    }

    /**
     * Sets the value of the xmlMimeType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setXmlMimeType(String value) {
        this.xmlMimeType = value;
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
     * Indicates if the isReadOnly flag was set.
     * 
     * @return
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
     * Indicates if the isWriteOnly flag was set.
     * 
     * @return
     */
    public boolean isSetWriteOnly() {
        return this.writeOnly != null;
    }

    /**
     * Gets the value of the cdata property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isCdata() {
        if (cdata == null) {
            return false;
        } else {
            return cdata;
        }
    }

    /**
     * Sets the value of the cdata property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setCdata(Boolean value) {
        this.cdata = value;
    }

    /**
     * Indicates if the cdata field has been set, i.e. is not null.
     * 
     * @return true if this.cdata is not null, false otherwise
     */
    public boolean isSetCdata() {
        return this.cdata != null;
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
     * Gets the value of the xmlLocation property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isXmlLocation() {
        if (xmlLocation == null) {
            return false;
        } else {
            return xmlLocation;
        }
    }

    /**
     * Sets the value of the xmlLocation property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setXmlLocation(Boolean value) {
        this.xmlLocation = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;attribute name="mapped-by" type="{http://www.w3.org/2001/XMLSchema}string" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class XmlInverseReference {

        @XmlAttribute(name = "mapped-by")
        protected String mappedBy;

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

    }

}
