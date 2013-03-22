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
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for xml-abstract-null-policy complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="xml-abstract-null-policy">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="xsi-nil-represents-null" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="empty-node-represents-null" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="null-representation-for-xml" type="{http://www.eclipse.org/eclipselink/xsds/persistence/oxm}xml-marshal-null-representation" default="ABSENT_NODE" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "xml-abstract-null-policy")
@XmlSeeAlso({
    XmlNullPolicy.class,
    XmlIsSetNullPolicy.class
})
public abstract class XmlAbstractNullPolicy {

    @XmlAttribute(name = "xsi-nil-represents-null")
    protected Boolean xsiNilRepresentsNull;
    @XmlAttribute(name = "empty-node-represents-null")
    protected Boolean emptyNodeRepresentsNull;
    @XmlAttribute(name = "null-representation-for-xml")
    protected XmlMarshalNullRepresentation nullRepresentationForXml;

    /**
     * Gets the value of the xsiNilRepresentsNull property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isXsiNilRepresentsNull() {
        if (xsiNilRepresentsNull == null) {
            return false;
        } else {
            return xsiNilRepresentsNull;
        }
    }

    /**
     * Sets the value of the xsiNilRepresentsNull property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setXsiNilRepresentsNull(Boolean value) {
        this.xsiNilRepresentsNull = value;
    }

    /**
     * Gets the value of the emptyNodeRepresentsNull property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isEmptyNodeRepresentsNull() {
        if (emptyNodeRepresentsNull == null) {
            return false;
        } else {
            return emptyNodeRepresentsNull;
        }
    }

    /**
     * Sets the value of the emptyNodeRepresentsNull property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setEmptyNodeRepresentsNull(Boolean value) {
        this.emptyNodeRepresentsNull = value;
    }

    /**
     * Gets the value of the nullRepresentationForXml property.
     * 
     * @return
     *     possible object is
     *     {@link XmlMarshalNullRepresentation }
     *     
     */
    public XmlMarshalNullRepresentation getNullRepresentationForXml() {
        if (nullRepresentationForXml == null) {
            return XmlMarshalNullRepresentation.ABSENT_NODE;
        } else {
            return nullRepresentationForXml;
        }
    }

    /**
     * Sets the value of the nullRepresentationForXml property.
     * 
     * @param value
     *     allowed object is
     *     {@link XmlMarshalNullRepresentation }
     *     
     */
    public void setNullRepresentationForXml(XmlMarshalNullRepresentation value) {
        this.nullRepresentationForXml = value;
    }

}