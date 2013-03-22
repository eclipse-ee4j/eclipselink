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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for xml-access-methods complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="xml-access-methods">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="get-method" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="set-method" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "xml-access-methods")
public class XmlAccessMethods {

    @XmlAttribute(name = "get-method")
    protected String getMethod;
    @XmlAttribute(name = "set-method")
    protected String setMethod;

    /**
     * Gets the value of the getMethod property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGetMethod() {
        return getMethod;
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
        return setMethod;
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

}