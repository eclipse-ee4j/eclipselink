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
 *       &lt;all>
 *         &lt;element ref="{http://www.eclipse.org/eclipselink/xsds/persistence/oxm}xml-schema" minOccurs="0"/>
 *         &lt;element ref="{http://www.eclipse.org/eclipselink/xsds/persistence/oxm}xml-schema-type" minOccurs="0"/>
 *         &lt;element ref="{http://www.eclipse.org/eclipselink/xsds/persistence/oxm}xml-schema-types" minOccurs="0"/>
 *         &lt;element ref="{http://www.eclipse.org/eclipselink/xsds/persistence/oxm}xml-java-type-adapters" minOccurs="0"/>
 *         &lt;element name="xml-registries" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.eclipse.org/eclipselink/xsds/persistence/oxm}xml-registry" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="xml-enums" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.eclipse.org/eclipselink/xsds/persistence/oxm}xml-enum" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="java-types" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.eclipse.org/eclipselink/xsds/persistence/oxm}java-type" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/all>
 *       &lt;attribute name="xml-accessor-type" type="{http://www.eclipse.org/eclipselink/xsds/persistence/oxm}xml-access-type" default="PUBLIC_MEMBER" />
 *       &lt;attribute name="xml-accessor-order" type="{http://www.eclipse.org/eclipselink/xsds/persistence/oxm}xml-access-order" default="UNDEFINED" />
 *       &lt;attribute name="xml-mapping-metadata-complete" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="package-name" type="{http://www.w3.org/2001/XMLSchema}string" default="##default" />
 *       &lt;attribute name="xml-name-transformer" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(javax.xml.bind.annotation.XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {

})
@XmlRootElement(name = "xml-bindings")
public class XmlBindings {

    @XmlElement(name = "xml-schema")
    protected XmlSchema xmlSchema;
    @XmlElement(name = "xml-schema-type")
    protected XmlSchemaType xmlSchemaType;
    @XmlElement(name = "xml-schema-types")
    protected XmlSchemaTypes xmlSchemaTypes;
    @XmlElement(name = "xml-java-type-adapters")
    protected XmlJavaTypeAdapters xmlJavaTypeAdapters;
    @XmlElement(name = "xml-registries")
    protected XmlBindings.XmlRegistries xmlRegistries;
    @XmlElement(name = "xml-enums")
    protected XmlBindings.XmlEnums xmlEnums;
    @XmlElement(name = "java-types")
    protected XmlBindings.JavaTypes javaTypes;
    @XmlAttribute(name = "xml-accessor-type")
    protected org.eclipse.persistence.jaxb.xmlmodel.XmlAccessType xmlAccessorType;
    @XmlAttribute(name = "xml-accessor-order")
    protected XmlAccessOrder xmlAccessorOrder;
    @XmlAttribute(name = "xml-mapping-metadata-complete")
    protected Boolean xmlMappingMetadataComplete;
    @XmlAttribute(name = "package-name")
    protected String packageName;
    @XmlAttribute(name = "xml-name-transformer")
    protected String xmlNameTransformer;

    /**
     * Gets the value of the xmlSchema property.
     * 
     * @return
     *     possible object is
     *     {@link XmlSchema }
     *     
     */
    public XmlSchema getXmlSchema() {
        return xmlSchema;
    }

    /**
     * Sets the value of the xmlSchema property.
     * 
     * @param value
     *     allowed object is
     *     {@link XmlSchema }
     *     
     */
    public void setXmlSchema(XmlSchema value) {
        this.xmlSchema = value;
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
     * Gets the value of the xmlSchemaTypes property.
     * 
     * @return
     *     possible object is
     *     {@link XmlSchemaTypes }
     *     
     */
    public XmlSchemaTypes getXmlSchemaTypes() {
        return xmlSchemaTypes;
    }

    /**
     * Sets the value of the xmlSchemaTypes property.
     * 
     * @param value
     *     allowed object is
     *     {@link XmlSchemaTypes }
     *     
     */
    public void setXmlSchemaTypes(XmlSchemaTypes value) {
        this.xmlSchemaTypes = value;
    }

    /**
     * Gets the value of the xmlJavaTypeAdapters property.
     * 
     * @return
     *     possible object is
     *     {@link XmlJavaTypeAdapters }
     *     
     */
    public XmlJavaTypeAdapters getXmlJavaTypeAdapters() {
        return xmlJavaTypeAdapters;
    }

    /**
     * Sets the value of the xmlJavaTypeAdapters property.
     * 
     * @param value
     *     allowed object is
     *     {@link XmlJavaTypeAdapters }
     *     
     */
    public void setXmlJavaTypeAdapters(XmlJavaTypeAdapters value) {
        this.xmlJavaTypeAdapters = value;
    }

    /**
     * Gets the value of the xmlRegistries property.
     * 
     * @return
     *     possible object is
     *     {@link XmlBindings.XmlRegistries }
     *     
     */
    public XmlBindings.XmlRegistries getXmlRegistries() {
        return xmlRegistries;
    }

    /**
     * Sets the value of the xmlRegistries property.
     * 
     * @param value
     *     allowed object is
     *     {@link XmlBindings.XmlRegistries }
     *     
     */
    public void setXmlRegistries(XmlBindings.XmlRegistries value) {
        this.xmlRegistries = value;
    }

    /**
     * Gets the value of the xmlEnums property.
     * 
     * @return
     *     possible object is
     *     {@link XmlBindings.XmlEnums }
     *     
     */
    public XmlBindings.XmlEnums getXmlEnums() {
        return xmlEnums;
    }

    /**
     * Sets the value of the xmlEnums property.
     * 
     * @param value
     *     allowed object is
     *     {@link XmlBindings.XmlEnums }
     *     
     */
    public void setXmlEnums(XmlBindings.XmlEnums value) {
        this.xmlEnums = value;
    }

    /**
     * Gets the value of the javaTypes property.
     * 
     * @return
     *     possible object is
     *     {@link XmlBindings.JavaTypes }
     *     
     */
    public XmlBindings.JavaTypes getJavaTypes() {
        return javaTypes;
    }

    /**
     * Sets the value of the javaTypes property.
     * 
     * @param value
     *     allowed object is
     *     {@link XmlBindings.JavaTypes }
     *     
     */
    public void setJavaTypes(XmlBindings.JavaTypes value) {
        this.javaTypes = value;
    }

    /**
     * Gets the value of the xmlAccessorType property.
     * 
     * @return
     *     possible object is
     *     {@link org.eclipse.persistence.jaxb.xmlmodel.XmlAccessType }
     *     
     */
    public org.eclipse.persistence.jaxb.xmlmodel.XmlAccessType getXmlAccessorType() {
        if (xmlAccessorType == null) {
            return org.eclipse.persistence.jaxb.xmlmodel.XmlAccessType.PUBLIC_MEMBER;
        } else {
            return xmlAccessorType;
        }
    }

    /**
     * Sets the value of the xmlAccessorType property.
     * 
     * @param value
     *     allowed object is
     *     {@link org.eclipse.persistence.jaxb.xmlmodel.XmlAccessType }
     *     
     */
    public void setXmlAccessorType(org.eclipse.persistence.jaxb.xmlmodel.XmlAccessType value) {
        this.xmlAccessorType = value;
    }

    /**
     * Indicates if XmlAccessorType is set, i.e. non-null.
     * 
     * @return true if xmlAccessorType is not null, false otherwise
     */
    public boolean isSetXmlAccessorType() {
        return xmlAccessorType != null;
    }

    /**
     * Gets the value of the xmlAccessorOrder property.
     * 
     * @return
     *     possible object is
     *     {@link XmlAccessOrder }
     *     
     */
    public XmlAccessOrder getXmlAccessorOrder() {
        if (xmlAccessorOrder == null) {
            return XmlAccessOrder.UNDEFINED;
        } else {
            return xmlAccessorOrder;
        }
    }

    /**
     * Sets the value of the xmlAccessorOrder property.
     * 
     * @param value
     *     allowed object is
     *     {@link XmlAccessOrder }
     *     
     */
    public void setXmlAccessorOrder(XmlAccessOrder value) {
        this.xmlAccessorOrder = value;
    }

    /**
     * Indicates if XmlAccessorOrder is set, i.e. non-null.
     * 
     * @return true if xmlAccessorOrder is not null, false otherwise
     */
    public boolean isSetXmlAccessorOrder() {
        return xmlAccessorOrder != null;
    }

    /**
     * Gets the value of the xmlMappingMetadataComplete property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isXmlMappingMetadataComplete() {
        if (xmlMappingMetadataComplete == null) {
            return false;
        } else {
            return xmlMappingMetadataComplete;
        }
    }

    /**
     * Sets the value of the xmlMappingMetadataComplete property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setXmlMappingMetadataComplete(Boolean value) {
        this.xmlMappingMetadataComplete = value;
    }

    /**
     * Gets the value of the packageName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPackageName() {
        if (packageName == null) {
            return "##default";
        } else {
            return packageName;
        }
    }

    /**
     * Sets the value of the packageName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPackageName(String value) {
        this.packageName = value;
    }

    /**
     * Gets the value of the xmlNameTransformer property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getXmlNameTransformer() {
        return xmlNameTransformer;
    }

    /**
     * Sets the value of the xmlNameTransformer property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setXmlNameTransformer(String value) {
        this.xmlNameTransformer = value;
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
     *       &lt;sequence>
     *         &lt;element ref="{http://www.eclipse.org/eclipselink/xsds/persistence/oxm}java-type" maxOccurs="unbounded" minOccurs="0"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(javax.xml.bind.annotation.XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "javaType"
    })
    public static class JavaTypes {

        @XmlElement(name = "java-type")
        protected List<JavaType> javaType;

        /**
         * Gets the value of the javaType property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the javaType property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getJavaType().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link JavaType }
         * 
         * 
         */
        public List<JavaType> getJavaType() {
            if (javaType == null) {
                javaType = new ArrayList<JavaType>();
            }
            return this.javaType;
        }

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
     *       &lt;sequence>
     *         &lt;element ref="{http://www.eclipse.org/eclipselink/xsds/persistence/oxm}xml-enum" maxOccurs="unbounded" minOccurs="0"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(javax.xml.bind.annotation.XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "xmlEnum"
    })
    public static class XmlEnums {

        @XmlElement(name = "xml-enum")
        protected List<XmlEnum> xmlEnum;

        /**
         * Gets the value of the xmlEnum property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the xmlEnum property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getXmlEnum().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link XmlEnum }
         * 
         * 
         */
        public List<XmlEnum> getXmlEnum() {
            if (xmlEnum == null) {
                xmlEnum = new ArrayList<XmlEnum>();
            }
            return this.xmlEnum;
        }

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
     *       &lt;sequence>
     *         &lt;element ref="{http://www.eclipse.org/eclipselink/xsds/persistence/oxm}xml-registry" maxOccurs="unbounded" minOccurs="0"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(javax.xml.bind.annotation.XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "xmlRegistry"
    })
    public static class XmlRegistries {

        @XmlElement(name = "xml-registry")
        protected List<XmlRegistry> xmlRegistry;

        /**
         * Gets the value of the xmlRegistry property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the xmlRegistry property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getXmlRegistry().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link XmlRegistry }
         * 
         * 
         */
        public List<XmlRegistry> getXmlRegistry() {
            if (xmlRegistry == null) {
                xmlRegistry = new ArrayList<XmlRegistry>();
            }
            return this.xmlRegistry;
        }

    }
    
    public boolean isSetXmlMappingMetadataComplete() {
        return this.xmlMappingMetadataComplete != null;
    }

}
