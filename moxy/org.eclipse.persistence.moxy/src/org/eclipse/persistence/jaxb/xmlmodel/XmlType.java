/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
// dmccann - June 29/2009 - 2.0 - Initial implementation
package org.eclipse.persistence.jaxb.xmlmodel;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" default="##default" /&gt;
 *       &lt;attribute name="namespace" type="{http://www.w3.org/2001/XMLSchema}string" default="##default" /&gt;
 *       &lt;attribute name="factory-class" type="{http://www.w3.org/2001/XMLSchema}string" default="javax.xml.bind.annotation.XmlType.DEFAULT" /&gt;
 *       &lt;attribute name="factory-method" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="prop-order"&gt;
 *         &lt;simpleType&gt;
 *           &lt;list itemType="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@javax.xml.bind.annotation.XmlType(name = "")
@XmlRootElement(name = "xml-type")
public class XmlType {

    @XmlAttribute
    protected String name;
    @XmlAttribute
    protected String namespace;
    @XmlAttribute(name = "factory-class")
    protected String factoryClass;
    @XmlAttribute(name = "factory-method")
    protected String factoryMethod;
    @XmlAttribute(name = "prop-order")
    protected List<String> propOrder;

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
     * Gets the value of the factoryClass property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getFactoryClass() {
        if (factoryClass == null) {
            return "javax.xml.bind.annotation.XmlType.DEFAULT";
        } else {
            return factoryClass;
        }
    }

    /**
     * Sets the value of the factoryClass property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setFactoryClass(String value) {
        this.factoryClass = value;
    }

    /**
     * Gets the value of the factoryMethod property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getFactoryMethod() {
        return factoryMethod;
    }

    /**
     * Sets the value of the factoryMethod property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setFactoryMethod(String value) {
        this.factoryMethod = value;
    }

    /**
     * Gets the value of the propOrder property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the propOrder property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPropOrder().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getPropOrder() {
        if (propOrder == null) {
            propOrder = new ArrayList<String>();
        }
        return this.propOrder;
    }

    public boolean isSetPropOrder() {
        return this.propOrder != null;
    }
}
