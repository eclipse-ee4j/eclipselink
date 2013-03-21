/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * David McCann = 2.1 - Initial contribution
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
 *         &lt;element name="xml-element-decl" maxOccurs="unbounded">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="java-method" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="defaultValue" type="{http://www.w3.org/2001/XMLSchema}string" default="\u0000" />
 *                 &lt;attribute name="namespace" type="{http://www.w3.org/2001/XMLSchema}string" default="##default" />
 *                 &lt;attribute name="scope" type="{http://www.w3.org/2001/XMLSchema}string" default="javax.xml.bind.annotation.XmlElementDecl.GLOBAL" />
 *                 &lt;attribute name="substitutionHeadName" type="{http://www.w3.org/2001/XMLSchema}string" default="" />
 *                 &lt;attribute name="substitutionHeadNamespace" type="{http://www.w3.org/2001/XMLSchema}string" default="##default" />
 *                 &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" default="##default" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "xmlElementDecl"
})
@XmlRootElement(name = "xml-registry")
public class XmlRegistry {

    @XmlElement(name = "xml-element-decl", required = true)
    protected List<XmlRegistry.XmlElementDecl> xmlElementDecl;
    @XmlAttribute(name = "name")
    protected String name;

    /**
     * Gets the value of the xmlElementDecl property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the xmlElementDecl property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getXmlElementDecl().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link XmlRegistry.XmlElementDecl }
     * 
     * 
     */
    public List<XmlRegistry.XmlElementDecl> getXmlElementDecl() {
        if (xmlElementDecl == null) {
            xmlElementDecl = new ArrayList<XmlRegistry.XmlElementDecl>();
        }
        return this.xmlElementDecl;
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
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;attribute name="java-method" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="defaultValue" type="{http://www.w3.org/2001/XMLSchema}string" default="\u0000" />
     *       &lt;attribute name="namespace" type="{http://www.w3.org/2001/XMLSchema}string" default="##default" />
     *       &lt;attribute name="scope" type="{http://www.w3.org/2001/XMLSchema}string" default="javax.xml.bind.annotation.XmlElementDecl.GLOBAL" />
     *       &lt;attribute name="substitutionHeadName" type="{http://www.w3.org/2001/XMLSchema}string" default="" />
     *       &lt;attribute name="substitutionHeadNamespace" type="{http://www.w3.org/2001/XMLSchema}string" default="##default" />
     *       &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" default="##default" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class XmlElementDecl {

        @XmlAttribute(name = "java-method")
        protected String javaMethod;
        @XmlAttribute(name = "name", required = true)
        protected String name;
        @XmlAttribute(name = "defaultValue")
        protected String defaultValue;
        @XmlAttribute(name = "namespace")
        protected String namespace;
        @XmlAttribute(name = "scope")
        protected String scope;
        @XmlAttribute(name = "substitutionHeadName")
        protected String substitutionHeadName;
        @XmlAttribute(name = "substitutionHeadNamespace")
        protected String substitutionHeadNamespace;
        @XmlAttribute(name = "type")
        protected String type;

        /**
         * Gets the value of the javaMethod property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getJavaMethod() {
            return javaMethod;
        }

        /**
         * Sets the value of the javaMethod property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setJavaMethod(String value) {
            this.javaMethod = value;
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
         * Gets the value of the defaultValue property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getDefaultValue() {
            if (defaultValue == null) {
                return "\\u0000";
            } else {
                return defaultValue;
            }
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
         * Gets the value of the scope property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getScope() {
            if (scope == null) {
                return "javax.xml.bind.annotation.XmlElementDecl.GLOBAL";
            } else {
                return scope;
            }
        }

        /**
         * Sets the value of the scope property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setScope(String value) {
            this.scope = value;
        }

        /**
         * Gets the value of the substitutionHeadName property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getSubstitutionHeadName() {
            if (substitutionHeadName == null) {
                return "";
            } else {
                return substitutionHeadName;
            }
        }

        /**
         * Sets the value of the substitutionHeadName property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSubstitutionHeadName(String value) {
            this.substitutionHeadName = value;
        }

        /**
         * Gets the value of the substitutionHeadNamespace property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getSubstitutionHeadNamespace() {
            if (substitutionHeadNamespace == null) {
                return "##default";
            } else {
                return substitutionHeadNamespace;
            }
        }

        /**
         * Sets the value of the substitutionHeadNamespace property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSubstitutionHeadNamespace(String value) {
            this.substitutionHeadNamespace = value;
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
    }
}
