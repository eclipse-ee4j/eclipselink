/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
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
 *         &lt;element ref="{http://www.eclipse.org/eclipselink/xsds/persistence/oxm}xml-schema-type"/>
 *         &lt;element ref="{http://www.eclipse.org/eclipselink/xsds/persistence/oxm}xml-element-wrapper"/>
 *         &lt;element ref="{http://www.eclipse.org/eclipselink/xsds/persistence/oxm}xml-java-type-adapter"/>
 *       &lt;/all>
 *       &lt;attribute ref="{http://www.eclipse.org/eclipselink/xsds/persistence/oxm}xml-inline-binary-data"/>
 *       &lt;attribute ref="{http://www.eclipse.org/eclipselink/xsds/persistence/oxm}xml-id"/>
 *       &lt;attribute ref="{http://www.eclipse.org/eclipselink/xsds/persistence/oxm}xml-idref"/>
 *       &lt;attribute ref="{http://www.eclipse.org/eclipselink/xsds/persistence/oxm}xml-attachment-ref"/>
 *       &lt;attribute ref="{http://www.eclipse.org/eclipselink/xsds/persistence/oxm}xml-list"/>
 *       &lt;attribute ref="{http://www.eclipse.org/eclipselink/xsds/persistence/oxm}xml-mime-type"/>
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" default="##default" />
 *       &lt;attribute name="namespace" type="{http://www.w3.org/2001/XMLSchema}string" default="##default" />
 *       &lt;attribute name="default-value" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="nillable" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="required" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" default="javax.xml.bind.annotation.XmlElement.DEFAULT" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "xmlSchemaType",
    "xmlElementWrapper",
    "xmlJavaTypeAdapter"
})
public class XmlElement
    extends JavaAttribute
{

    @javax.xml.bind.annotation.XmlElement(name = "xml-schema-type", required = true)
    protected XmlSchemaType xmlSchemaType;
    @javax.xml.bind.annotation.XmlElement(name = "xml-element-wrapper", required = true)
    protected XmlElementWrapper xmlElementWrapper;
    @javax.xml.bind.annotation.XmlElement(name = "xml-java-type-adapter", required = true)
    protected XmlJavaTypeAdapter xmlJavaTypeAdapter;
    @XmlAttribute(name = "xml-inline-binary-data", namespace = "http://www.eclipse.org/eclipselink/xsds/persistence/oxm")
    protected Boolean xmlInlineBinaryData;
    @XmlAttribute(name = "xml-id", namespace = "http://www.eclipse.org/eclipselink/xsds/persistence/oxm")
    protected Boolean xmlId;
    @XmlAttribute(name = "xml-idref", namespace = "http://www.eclipse.org/eclipselink/xsds/persistence/oxm")
    protected Boolean xmlIdref;
    @XmlAttribute(name = "xml-attachment-ref", namespace = "http://www.eclipse.org/eclipselink/xsds/persistence/oxm")
    protected Boolean xmlAttachmentRef;
    @XmlAttribute(name = "xml-list", namespace = "http://www.eclipse.org/eclipselink/xsds/persistence/oxm")
    protected Boolean xmlList;
    @XmlAttribute(name = "xml-mime-type", namespace = "http://www.eclipse.org/eclipselink/xsds/persistence/oxm")
    protected String xmlMimeType;
    @XmlAttribute
    protected String name;
    @XmlAttribute
    protected String namespace;
    @XmlAttribute(name = "default-value")
    protected String defaultValue;
    @XmlAttribute
    protected Boolean nillable;
    @XmlAttribute
    protected Boolean required;
    @XmlAttribute
    protected String type;

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

}
