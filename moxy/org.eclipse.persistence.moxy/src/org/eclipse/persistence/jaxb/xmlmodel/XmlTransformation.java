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
 * mmacivor - August 4th/2010 - 2.2 - Initial implementation
 ******************************************************************************/

package org.eclipse.persistence.jaxb.xmlmodel;

import java.util.ArrayList;
import java.util.List;
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
 *       &lt;sequence>
 *         &lt;element name="xml-access-methods" type="{http://www.eclipse.org/eclipselink/xsds/persistence/oxm}xml-access-methods" minOccurs="0"/>
 *         &lt;element ref="{http://www.eclipse.org/eclipselink/xsds/persistence/oxm}xml-properties" minOccurs="0"/>
 *         &lt;element name="xml-read-transformer">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="method" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="transformer-class" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="xml-write-transformer" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="method" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="xml-path" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="transformer-class" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="optional" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
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
    "xmlProperties",
    "xmlReadTransformer",
    "xmlWriteTransformer"
})
public class XmlTransformation
    extends JavaAttribute
{

    @XmlElement(name = "xml-access-methods")
    protected XmlAccessMethods xmlAccessMethods;
    @XmlElement(name = "xml-properties")
    protected XmlProperties xmlProperties;
    @XmlElement(name = "xml-read-transformer", required = true)
    protected XmlTransformation.XmlReadTransformer xmlReadTransformer;
    @XmlElement(name = "xml-write-transformer")
    protected List<XmlTransformation.XmlWriteTransformer> xmlWriteTransformer;
    @XmlAttribute(name = "optional")
    protected Boolean optional;

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
     * Indicates if an XmlReadTransformer has been set, i.e. is non-null.
     * 
     * @return
     */
    public boolean isSetXmlReadTransformer() {
        return xmlReadTransformer != null;
    }
    
    /**
     * Gets the value of the xmlReadTransformer property.
     * 
     * @return
     *     possible object is
     *     {@link XmlTransformation.XmlReadTransformer }
     *     
     */
    public XmlTransformation.XmlReadTransformer getXmlReadTransformer() {
        return xmlReadTransformer;
    }

    /**
     * Sets the value of the xmlReadTransformer property.
     * 
     * @param value
     *     allowed object is
     *     {@link XmlTransformation.XmlReadTransformer }
     *     
     */
    public void setXmlReadTransformer(XmlTransformation.XmlReadTransformer value) {
        this.xmlReadTransformer = value;
    }

    /**
     * Indicates if at least one XmlWriteTransformer has been 
     * set, i.e. the List of is XmlWriteTransformers is non-null
     * and non-empty.
     * 
     * @return
     */
    public boolean isSetXmlWriteTransformers() {
        return xmlWriteTransformer != null && xmlWriteTransformer.size() > 0;
    }

    /**
     * Gets the value of the xmlWriteTransformer property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the xmlWriteTransformer property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getXmlWriteTransformer().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link XmlTransformation.XmlWriteTransformer }
     * 
     * 
     */
    public List<XmlTransformation.XmlWriteTransformer> getXmlWriteTransformer() {
        if (xmlWriteTransformer == null) {
            xmlWriteTransformer = new ArrayList<XmlTransformation.XmlWriteTransformer>();
        }
        return this.xmlWriteTransformer;
    }

    /**
     * Gets the value of the optional property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isOptional() {
        if (optional == null) {
            return true;
        } else {
            return optional;
        }
    }

    /**
     * Sets the value of the optional property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setOptional(Boolean value) {
        this.optional = value;
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
     *       &lt;attribute name="method" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="transformer-class" type="{http://www.w3.org/2001/XMLSchema}string" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class XmlReadTransformer {

        @XmlAttribute(name = "method")
        protected String method;
        @XmlAttribute(name = "transformer-class")
        protected String transformerClass;

        /**
         * Gets the value of the method property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getMethod() {
            return method;
        }

        /**
         * Sets the value of the method property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setMethod(String value) {
            this.method = value;
        }
        
        /**
         * Indicates if a method has been set, i.e. the method property is non-null.
         * 
         * @return
         */
        public boolean isSetMethod() {
            return method != null;
        }

        /**
         * Gets the value of the transformerClass property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getTransformerClass() {
            return transformerClass;
        }

        /**
         * Sets the value of the transformerClass property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setTransformerClass(String value) {
            this.transformerClass = value;
        }
        
        /**
         * Indicates if a transformerClass has been set, i.e. the 
         * transformerClass property is non-null.
         * 
         * @return
         */
        public boolean isSetTransformerClass() {
           return transformerClass != null; 
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
     *       &lt;attribute name="method" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="xml-path" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="transformer-class" type="{http://www.w3.org/2001/XMLSchema}string" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class XmlWriteTransformer {

        @XmlAttribute(name = "method")
        protected String method;
        @XmlAttribute(name = "xml-path", required = true)
        protected String xmlPath;
        @XmlAttribute(name = "transformer-class")
        protected String transformerClass;

        /**
         * Gets the value of the method property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getMethod() {
            return method;
        }

        /**
         * Sets the value of the method property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setMethod(String value) {
            this.method = value;
        }

        /**
         * Indicates if a method has been set, i.e. the method property is non-null.
         * 
         * @return
         */
        public boolean isSetMethod() {
            return method != null;
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
         * Indicates if a xmlPath has been set, i.e. the xmlPath property is non-null.
         * 
         * @return
         */
        public boolean isSetXmlPath() {
            return xmlPath != null;
        }

        /**
         * Gets the value of the transformerClass property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getTransformerClass() {
            return transformerClass;
        }

        /**
         * Sets the value of the transformerClass property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setTransformerClass(String value) {
            this.transformerClass = value;
        }
        
        /**
         * Indicates if a transformerClass has been set, i.e. the 
         * transformerClass property is non-null.
         * 
         * @return
         */
        public boolean isSetTransformerClass() {
           return transformerClass != null; 
        }
    }
}
