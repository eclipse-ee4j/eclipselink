/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
// Denise Smith - 2.3
package org.eclipse.persistence.testing.jaxb.xmlschema.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;


/**
 * <p>Java class for attribute complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>{@code
 * <complexType name="attribute">
 *   <complexContent>
 *     <extension base="{http://www.w3.org/2001/XMLSchema}annotated">
 *       <sequence>
 *         <element name="simpleType" type="{http://www.w3.org/2001/XMLSchema}localSimpleType" minOccurs="0"/>
 *       </sequence>
 *       <attGroup ref="{http://www.w3.org/2001/XMLSchema}defRef"/>
 *       <attribute name="type" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *       <attribute name="use" default="optional">
 *         <simpleType>
 *           <restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *             <enumeration value="prohibited"/>
 *             <enumeration value="optional"/>
 *             <enumeration value="required"/>
 *           </restriction>
 *         </simpleType>
 *       </attribute>
 *       <attribute name="default" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       <attribute name="fixed" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       <attribute name="form" type="{http://www.w3.org/2001/XMLSchema}formChoice" />
 *       <anyAttribute processContents='lax' namespace='##other'/>
 *     </extension>
 *   </complexContent>
 * </complexType>
 * }</pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "attribute", propOrder = {
    "simpleType"
})
@XmlSeeAlso({
    TopLevelAttribute.class
})
public class Attribute
    extends Annotated
{

    protected LocalSimpleType simpleType;
    @XmlAttribute(name = "type")
    protected QName type;
    @XmlAttribute(name = "use")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String use;
    @XmlAttribute(name = "default")
    protected String _default;
    @XmlAttribute(name = "fixed")
    protected String fixed;
    @XmlAttribute(name = "form")
    protected FormChoice form;
    @XmlAttribute(name = "name")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String name;
    @XmlAttribute(name = "ref")
    protected QName ref;

    /**
     * Gets the value of the simpleType property.
     *
     * @return
     *     possible object is
     *     {@link LocalSimpleType }
     *
     */
    public LocalSimpleType getSimpleType() {
        return simpleType;
    }

    /**
     * Sets the value of the simpleType property.
     *
     * @param value
     *     allowed object is
     *     {@link LocalSimpleType }
     *
     */
    public void setSimpleType(LocalSimpleType value) {
        this.simpleType = value;
    }

    /**
     * Gets the value of the type property.
     *
     * @return
     *     possible object is
     *     {@link QName }
     *
     */
    public QName getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     *
     * @param value
     *     allowed object is
     *     {@link QName }
     *
     */
    public void setType(QName value) {
        this.type = value;
    }

    /**
     * Gets the value of the use property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getUse() {
        if (use == null) {
            return "optional";
        } else {
            return use;
        }
    }

    /**
     * Sets the value of the use property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setUse(String value) {
        this.use = value;
    }

    /**
     * Gets the value of the default property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDefault() {
        return _default;
    }

    /**
     * Sets the value of the default property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDefault(String value) {
        this._default = value;
    }

    /**
     * Gets the value of the fixed property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getFixed() {
        return fixed;
    }

    /**
     * Sets the value of the fixed property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setFixed(String value) {
        this.fixed = value;
    }

    /**
     * Gets the value of the form property.
     *
     * @return
     *     possible object is
     *     {@link FormChoice }
     *
     */
    public FormChoice getForm() {
        return form;
    }

    /**
     * Sets the value of the form property.
     *
     * @param value
     *     allowed object is
     *     {@link FormChoice }
     *
     */
    public void setForm(FormChoice value) {
        this.form = value;
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
     * Gets the value of the ref property.
     *
     * @return
     *     possible object is
     *     {@link QName }
     *
     */
    public QName getRef() {
        return ref;
    }

    /**
     * Sets the value of the ref property.
     *
     * @param value
     *     allowed object is
     *     {@link QName }
     *
     */
    public void setRef(QName value) {
        this.ref = value;
    }

}
