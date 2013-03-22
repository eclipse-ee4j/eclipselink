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
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="xml-ns" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="namespace-uri" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="prefix" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="attribute-form-default" type="{http://www.eclipse.org/eclipselink/xsds/persistence/oxm}xml-ns-form" default="UNSET" />
 *       &lt;attribute name="element-form-default" type="{http://www.eclipse.org/eclipselink/xsds/persistence/oxm}xml-ns-form" default="UNSET" />
 *       &lt;attribute name="location" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="namespace" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "xmlNs"
})
@XmlRootElement(name = "xml-schema")
public class XmlSchema {

    @XmlElement(name = "xml-ns")
    protected List<XmlSchema.XmlNs> xmlNs;
    @XmlAttribute(name = "attribute-form-default")
    protected XmlNsForm attributeFormDefault;
    @XmlAttribute(name = "element-form-default")
    protected XmlNsForm elementFormDefault;
    @XmlAttribute
    protected String location;
    @XmlAttribute
    protected String namespace;

    /**
     * Gets the value of the xmlNs property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the xmlNs property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getXmlNs().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link XmlSchema.XmlNs }
     * 
     * 
     */
    public List<XmlSchema.XmlNs> getXmlNs() {
        if (xmlNs == null) {
            xmlNs = new ArrayList<XmlSchema.XmlNs>();
        }
        return this.xmlNs;
    }

    /**
     * Gets the value of the attributeFormDefault property.
     * 
     * @return
     *     possible object is
     *     {@link XmlNsForm }
     *     
     */
    public XmlNsForm getAttributeFormDefault() {
        if (attributeFormDefault == null) {
            return XmlNsForm.UNSET;
        } else {
            return attributeFormDefault;
        }
    }

    /**
     * Sets the value of the attributeFormDefault property.
     * 
     * @param value
     *     allowed object is
     *     {@link XmlNsForm }
     *     
     */
    public void setAttributeFormDefault(XmlNsForm value) {
        this.attributeFormDefault = value;
    }

    /**
     * Gets the value of the elementFormDefault property.
     * 
     * @return
     *     possible object is
     *     {@link XmlNsForm }
     *     
     */
    public XmlNsForm getElementFormDefault() {
        if (elementFormDefault == null) {
            return XmlNsForm.UNSET;
        } else {
            return elementFormDefault;
        }
    }

    /**
     * Sets the value of the elementFormDefault property.
     * 
     * @param value
     *     allowed object is
     *     {@link XmlNsForm }
     *     
     */
    public void setElementFormDefault(XmlNsForm value) {
        this.elementFormDefault = value;
    }

    /**
     * Gets the value of the location property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLocation() {
        return location;
    }

    /**
     * Sets the value of the location property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLocation(String value) {
        this.location = value;
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
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;attribute name="namespace-uri" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="prefix" type="{http://www.w3.org/2001/XMLSchema}string" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class XmlNs {

        @XmlAttribute(name = "namespace-uri")
        protected String namespaceUri;
        @XmlAttribute
        protected String prefix;

        /**
         * Gets the value of the namespaceUri property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getNamespaceUri() {
            return namespaceUri;
        }

        /**
         * Sets the value of the namespaceUri property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setNamespaceUri(String value) {
            this.namespaceUri = value;
        }

        /**
         * Gets the value of the prefix property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getPrefix() {
            return prefix;
        }

        /**
         * Sets the value of the prefix property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setPrefix(String value) {
            this.prefix = value;
        }

    }

}
