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

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;
import jakarta.xml.bind.annotation.XmlID;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>{@code
 * <complexType>
 *   <complexContent>
 *     <extension base="{http://www.w3.org/2001/XMLSchema}openAttrs">
 *       <choice maxOccurs="unbounded" minOccurs="0">
 *         <element ref="{http://www.w3.org/2001/XMLSchema}annotation"/>
 *         <group ref="{http://www.w3.org/2001/XMLSchema}redefinable"/>
 *       </choice>
 *       <attribute name="schemaLocation" use="required" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       <attribute name="id" type="{http://www.w3.org/2001/XMLSchema}ID" />
 *       <anyAttribute processContents='lax' namespace='##other'/>
 *     </extension>
 *   </complexContent>
 * </complexType>
 * }</pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "annotationOrSimpleTypeOrComplexType"
})
@XmlRootElement(name = "redefine")
public class Redefine
    extends OpenAttrs
{

    @XmlElements({
        @XmlElement(name = "attributeGroup", type = NamedAttributeGroup.class),
        @XmlElement(name = "group", type = NamedGroup.class),
        @XmlElement(name = "simpleType", type = TopLevelSimpleType.class),
        @XmlElement(name = "annotation", type = Annotation.class),
        @XmlElement(name = "complexType", type = TopLevelComplexType.class)
    })
    protected List<OpenAttrs> annotationOrSimpleTypeOrComplexType;
    @XmlAttribute(name = "schemaLocation", required = true)
    @XmlSchemaType(name = "anyURI")
    protected String schemaLocation;
    @XmlAttribute(name = "id")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;

    /**
     * Gets the value of the annotationOrSimpleTypeOrComplexType property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the annotationOrSimpleTypeOrComplexType property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAnnotationOrSimpleTypeOrComplexType().add(newItem);
     * }</pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link NamedAttributeGroup }
     * {@link NamedGroup }
     * {@link TopLevelSimpleType }
     * {@link Annotation }
     * {@link TopLevelComplexType }
     *
     *
     */
    public List<OpenAttrs> getAnnotationOrSimpleTypeOrComplexType() {
        if (annotationOrSimpleTypeOrComplexType == null) {
            annotationOrSimpleTypeOrComplexType = new ArrayList<OpenAttrs>();
        }
        return this.annotationOrSimpleTypeOrComplexType;
    }

    /**
     * Gets the value of the schemaLocation property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSchemaLocation() {
        return schemaLocation;
    }

    /**
     * Sets the value of the schemaLocation property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSchemaLocation(String value) {
        this.schemaLocation = value;
    }

    /**
     * Gets the value of the id property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setId(String value) {
        this.id = value;
    }

}
