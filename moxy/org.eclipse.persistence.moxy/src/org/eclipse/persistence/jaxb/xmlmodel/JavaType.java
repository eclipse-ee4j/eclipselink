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
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlList;


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
 *         &lt;element ref="{http://www.eclipse.org/eclipselink/xsds/persistence/oxm}xml-type" minOccurs="0"/>
 *         &lt;element ref="{http://www.eclipse.org/eclipselink/xsds/persistence/oxm}xml-root-element" minOccurs="0"/>
 *         &lt;element ref="{http://www.eclipse.org/eclipselink/xsds/persistence/oxm}xml-virtual-access-methods" minOccurs="0"/>
 *         &lt;element ref="{http://www.eclipse.org/eclipselink/xsds/persistence/oxm}xml-see-also" minOccurs="0"/>
 *         &lt;element ref="{http://www.eclipse.org/eclipselink/xsds/persistence/oxm}xml-java-type-adapter" minOccurs="0"/>
 *         &lt;element ref="{http://www.eclipse.org/eclipselink/xsds/persistence/oxm}xml-class-extractor" minOccurs="0"/>
 *         &lt;element ref="{http://www.eclipse.org/eclipselink/xsds/persistence/oxm}xml-properties" minOccurs="0"/>
 *         &lt;element name="java-attributes" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://www.eclipse.org/eclipselink/xsds/persistence/oxm}java-attribute" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element ref="{http://www.eclipse.org/eclipselink/xsds/persistence/oxm}xml-named-object-graphs" minOccurs="0"/>
 *       &lt;/all>
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="super-type" type="{http://www.w3.org/2001/XMLSchema}string" default="##default" />
 *       &lt;attribute name="xml-accessor-order" type="{http://www.eclipse.org/eclipselink/xsds/persistence/oxm}xml-access-order" default="UNDEFINED" />
 *       &lt;attribute name="xml-accessor-type" type="{http://www.eclipse.org/eclipselink/xsds/persistence/oxm}xml-access-type" default="PUBLIC_MEMBER" />
 *       &lt;attribute name="xml-customizer" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="xml-discriminator-node" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="xml-discriminator-value" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="xml-inline-binary-data" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="xml-transient" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="xml-name-transformer" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(javax.xml.bind.annotation.XmlAccessType.FIELD)
@javax.xml.bind.annotation.XmlType(name = "", propOrder = {

})
@javax.xml.bind.annotation.XmlRootElement(name = "java-type")
public class JavaType {

    @javax.xml.bind.annotation.XmlElement(name = "xml-type")
    protected org.eclipse.persistence.jaxb.xmlmodel.XmlType xmlType;
    @javax.xml.bind.annotation.XmlElement(name = "xml-root-element")
    protected org.eclipse.persistence.jaxb.xmlmodel.XmlRootElement xmlRootElement;
    @javax.xml.bind.annotation.XmlElement(name = "xml-virtual-access-methods")
    protected XmlVirtualAccessMethods xmlVirtualAccessMethods;
    @XmlList
    @javax.xml.bind.annotation.XmlElement(name = "xml-see-also")
    protected List<String> xmlSeeAlso;
    @javax.xml.bind.annotation.XmlElement(name = "xml-java-type-adapter")
    protected XmlJavaTypeAdapter xmlJavaTypeAdapter;
    @javax.xml.bind.annotation.XmlElement(name = "xml-class-extractor")
    protected XmlClassExtractor xmlClassExtractor;
    @javax.xml.bind.annotation.XmlElement(name = "xml-properties")
    protected XmlProperties xmlProperties;
    @javax.xml.bind.annotation.XmlElement(name = "java-attributes")
    protected JavaType.JavaAttributes javaAttributes;
    @javax.xml.bind.annotation.XmlElement(name = "xml-named-object-graphs")
    protected XmlNamedObjectGraphs xmlNamedObjectGraphs;
    @javax.xml.bind.annotation.XmlAttribute(name = "name")
    protected String name;
    @javax.xml.bind.annotation.XmlAttribute(name = "super-type")
    protected String superType;
    @javax.xml.bind.annotation.XmlAttribute(name = "xml-accessor-order")
    protected XmlAccessOrder xmlAccessorOrder;
    @javax.xml.bind.annotation.XmlAttribute(name = "xml-accessor-type")
    protected org.eclipse.persistence.jaxb.xmlmodel.XmlAccessType xmlAccessorType;
    @javax.xml.bind.annotation.XmlAttribute(name = "xml-customizer")
    protected String xmlCustomizer;
    @javax.xml.bind.annotation.XmlAttribute(name = "xml-discriminator-node")
    protected String xmlDiscriminatorNode;
    @javax.xml.bind.annotation.XmlAttribute(name = "xml-discriminator-value")
    protected String xmlDiscriminatorValue;
    @javax.xml.bind.annotation.XmlAttribute(name = "xml-inline-binary-data")
    protected Boolean xmlInlineBinaryData;
    @javax.xml.bind.annotation.XmlAttribute(name = "xml-transient")
    protected Boolean xmlTransient;
    @javax.xml.bind.annotation.XmlAttribute(name = "xml-name-transformer")
    protected String xmlNameTransformer;

    /**
     * Gets the value of the xmlType property.
     * 
     * @return
     *     possible object is
     *     {@link org.eclipse.persistence.jaxb.xmlmodel.XmlType }
     *     
     */
    public org.eclipse.persistence.jaxb.xmlmodel.XmlType getXmlType() {
        return xmlType;
    }

    /**
     * Sets the value of the xmlType property.
     * 
     * @param value
     *     allowed object is
     *     {@link org.eclipse.persistence.jaxb.xmlmodel.XmlType }
     *     
     */
    public void setXmlType(org.eclipse.persistence.jaxb.xmlmodel.XmlType value) {
        this.xmlType = value;
    }

    /**
     * Gets the value of the xmlRootElement property.
     * 
     * @return
     *     possible object is
     *     {@link org.eclipse.persistence.jaxb.xmlmodel.XmlRootElement }
     *     
     */
    public org.eclipse.persistence.jaxb.xmlmodel.XmlRootElement getXmlRootElement() {
        return xmlRootElement;
    }

    /**
     * Sets the value of the xmlRootElement property.
     * 
     * @param value
     *     allowed object is
     *     {@link org.eclipse.persistence.jaxb.xmlmodel.XmlRootElement }
     *     
     */
    public void setXmlRootElement(org.eclipse.persistence.jaxb.xmlmodel.XmlRootElement value) {
        this.xmlRootElement = value;
    }
    
    /**
     * Gets the value of the xmlVirtualAccessMethods property.
     * 
     * @return
     *     possible object is
     *     {@link XmlVirtualAccessMethods }
     *     
     */
    public XmlVirtualAccessMethods getXmlVirtualAccessMethods() {
        return xmlVirtualAccessMethods;
    }

    /**
     * Sets the value of the xmlVirtualAccessMethods property.
     * 
     * @param value
     *     allowed object is
     *     {@link XmlVirtualAccessMethods }
     *     
     */
    public void setXmlVirtualAccessMethods(XmlVirtualAccessMethods value) {
        this.xmlVirtualAccessMethods = value;
    }

    /**
     * Gets the value of the xmlSeeAlso property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the xmlSeeAlso property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getXmlSeeAlso().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getXmlSeeAlso() {
        if (xmlSeeAlso == null) {
            xmlSeeAlso = new ArrayList<String>();
        }
        return this.xmlSeeAlso;
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
     * Gets the value of the xmlClassExtractor property.
     * 
     * @return
     *     possible object is
     *     {@link XmlClassExtractor }
     *     
     */
    public XmlClassExtractor getXmlClassExtractor() {
        return xmlClassExtractor;
    }

    /**
     * Sets the value of the xmlClassExtractor property.
     * 
     * @param value
     *     allowed object is
     *     {@link XmlClassExtractor }
     *     
     */
    public void setXmlClassExtractor(XmlClassExtractor value) {
        this.xmlClassExtractor = value;
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
     * Gets the value of the javaAttributes property.
     * 
     * @return
     *     possible object is
     *     {@link JavaType.JavaAttributes }
     *     
     */
    public JavaType.JavaAttributes getJavaAttributes() {
        return javaAttributes;
    }

    /**
     * Sets the value of the javaAttributes property.
     * 
     * @param value
     *     allowed object is
     *     {@link JavaType.JavaAttributes }
     *     
     */
    public void setJavaAttributes(JavaType.JavaAttributes value) {
        this.javaAttributes = value;
    }

    /**
     * Gets the value of the xmlNamedObjectGraphs property.
     * 
     * @return
     *     possible object is
     *     {@link XmlNamedObjectGraphs }
     *     
     */
    public XmlNamedObjectGraphs getXmlNamedObjectGraphs() {
        return xmlNamedObjectGraphs;
    }

    /**
     * Sets the value of the xmlNamedObjectGraphs property.
     * 
     * @param value
     *     allowed object is
     *     {@link XmlNamedObjectGraphs }
     *     
     */
    public void setXmlNamedObjectGraphs(XmlNamedObjectGraphs value) {
        this.xmlNamedObjectGraphs = value;
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
        return name;
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
     * Gets the value of the superType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSuperType() {
        if (superType == null) {
            return "##default";
        } else {
            return superType;
        }
    }

    /**
     * Sets the value of the superType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSuperType(String value) {
        this.superType = value;
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
     * Indicates if xmlAccessorOrder has been set, i.e. is non-null.
     * 
     * @return true if xmlAccessorOrder is non-null, false otherwise
     */
    public boolean isSetXmlAccessorOrder() {
        return xmlAccessorOrder != null;
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
     * Indicates if xmlAccessorType has been set, i.e. is non-null.
     * 
     * @return true if xmlAccessorType is non-null, false otherwise
     */
    public boolean isSetXmlAccessorType() {
        return xmlAccessorType != null;
    }


    /**
     * Gets the value of the xmlCustomizer property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getXmlCustomizer() {
        return xmlCustomizer;
    }

    /**
     * Sets the value of the xmlCustomizer property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setXmlCustomizer(String value) {
        this.xmlCustomizer = value;
    }

    /**
     * Gets the value of the xmlDiscriminatorNode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getXmlDiscriminatorNode() {
        return xmlDiscriminatorNode;
    }

    /**
     * Sets the value of the xmlDiscriminatorNode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setXmlDiscriminatorNode(String value) {
        this.xmlDiscriminatorNode = value;
    }

    /**
     * Gets the value of the xmlDiscriminatorValue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getXmlDiscriminatorValue() {
        return xmlDiscriminatorValue;
    }

    /**
     * Sets the value of the xmlDiscriminatorValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setXmlDiscriminatorValue(String value) {
        this.xmlDiscriminatorValue = value;
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
     * Indicates if xmlInlineBinaryData has been set, i.e. is non-null.
     * 
     * @return true if xmlInlineBinaryData is non-null, false otherwise
     */
    public boolean isSetXmlInlineBinaryData() {
        return xmlInlineBinaryData != null;
    }

    /**
     * Gets the value of the xmlTransient property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isXmlTransient() {
        if (xmlTransient == null) {
            return false;
        } else {
            return xmlTransient;
        }
    }

    /**
     * Sets the value of the xmlTransient property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setXmlTransient(Boolean value) {
        this.xmlTransient = value;
    }
    
    /**
     * Indicates if xmlTransient has been set, i.e. is non-null.
     *  
     * @return true is xmlTransient is non-null, false otherwise
     */
    public boolean isSetXmlTransient() {
        return xmlTransient != null;
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
     *         &lt;element ref="{http://www.eclipse.org/eclipselink/xsds/persistence/oxm}java-attribute" maxOccurs="unbounded" minOccurs="0"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(javax.xml.bind.annotation.XmlAccessType.FIELD)
    @javax.xml.bind.annotation.XmlType(name = "", propOrder = {
        "javaAttribute"
    })
    public static class JavaAttributes {

        @javax.xml.bind.annotation.XmlElementRef(name = "java-attribute", namespace = "http://www.eclipse.org/eclipselink/xsds/persistence/oxm", type = JAXBElement.class)
        protected List<JAXBElement<? extends JavaAttribute>> javaAttribute;

        /**
         * Gets the value of the javaAttribute property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the javaAttribute property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getJavaAttribute().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link JAXBElement }{@code <}{@link org.eclipse.persistence.jaxb.xmlmodel.XmlAttribute }{@code >}
         * {@link JAXBElement }{@code <}{@link XmlJavaTypeAdapter }{@code >}
         * {@link JAXBElement }{@code <}{@link XmlElements }{@code >}
         * {@link JAXBElement }{@code <}{@link JavaAttribute }{@code >}
         * {@link JAXBElement }{@code <}{@link org.eclipse.persistence.jaxb.xmlmodel.XmlElement }{@code >}
         * {@link JAXBElement }{@code <}{@link XmlElementRefs }{@code >}
         * {@link JAXBElement }{@code <}{@link org.eclipse.persistence.jaxb.xmlmodel.XmlElementRef }{@code >}
         * {@link JAXBElement }{@code <}{@link XmlTransformation }{@code >}
         * {@link JAXBElement }{@code <}{@link XmlTransient }{@code >}
         * {@link JAXBElement }{@code <}{@link XmlInverseReference }{@code >}
         * {@link JAXBElement }{@code <}{@link XmlValue }{@code >}
         * {@link JAXBElement }{@code <}{@link XmlAnyElement }{@code >}
         * {@link JAXBElement }{@code <}{@link XmlAnyAttribute }{@code >}
         * 
         * 
         */
        public List<JAXBElement<? extends JavaAttribute>> getJavaAttribute() {
            if (javaAttribute == null) {
                javaAttribute = new ArrayList<JAXBElement<? extends JavaAttribute>>();
            }
            return this.javaAttribute;
        }

    }

}