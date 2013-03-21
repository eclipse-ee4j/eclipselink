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
 * rbarkhouse - 2011 April 12 - 2.3 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.jaxb.xmlmodel;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
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
 *       &lt;attribute name="get-method" type="{http://www.w3.org/2001/XMLSchema}string" default="get" />
 *       &lt;attribute name="set-method" type="{http://www.w3.org/2001/XMLSchema}string" default="set" />
 *       &lt;attribute name="schema" type="{http://www.eclipse.org/eclipselink/xsds/persistence/oxm}xml-virtual-access-methods-schema" default="NODES" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "xml-virtual-access-methods")
public class XmlVirtualAccessMethods {

    @XmlAttribute(name = "get-method")
    protected String getMethod;
    @XmlAttribute(name = "set-method")
    protected String setMethod;
    @XmlAttribute
    protected XmlVirtualAccessMethodsSchema schema;

    /**
     * Gets the value of the getMethod property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGetMethod() {
        if (getMethod == null) {
            return "get";
        } else {
            return getMethod;
        }
    }

    /**
     * Sets the value of the getMethod property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGetMethod(String value) {
        this.getMethod = value;
    }

    /**
     * Gets the value of the setMethod property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSetMethod() {
        if (setMethod == null) {
            return "set";
        } else {
            return setMethod;
        }
    }

    /**
     * Sets the value of the setMethod property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSetMethod(String value) {
        this.setMethod = value;
    }

    /**
     * Gets the value of the schema property.
     * 
     * @return
     *     possible object is
     *     {@link XmlVirtualAccessMethodsSchema }
     *     
     */
    public XmlVirtualAccessMethodsSchema getSchema() {
        if (schema == null) {
            return XmlVirtualAccessMethodsSchema.NODES;
        } else {
            return schema;
        }
    }

    /**
     * Sets the value of the schema property.
     * 
     * @param value
     *     allowed object is
     *     {@link XmlVirtualAccessMethodsSchema }
     *     
     */
    public void setSchema(XmlVirtualAccessMethodsSchema value) {
        this.schema = value;
    }

}