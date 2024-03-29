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
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for wildcard complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>{@code
 * <complexType name="wildcard">
 *   <complexContent>
 *     <extension base="{http://www.w3.org/2001/XMLSchema}annotated">
 *       <attribute name="namespace" type="{http://www.w3.org/2001/XMLSchema}namespaceList" default="##any" />
 *       <attribute name="processContents" default="strict">
 *         <simpleType>
 *           <restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *             <enumeration value="skip"/>
 *             <enumeration value="lax"/>
 *             <enumeration value="strict"/>
 *           </restriction>
 *         </simpleType>
 *       </attribute>
 *       <anyAttribute processContents='lax' namespace='##other'/>
 *     </extension>
 *   </complexContent>
 * </complexType>
 * }</pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "wildcard")
@XmlSeeAlso({
    Any.class
})
public class Wildcard
    extends Annotated
{

    @XmlAttribute(name = "namespace")
    @XmlSchemaType(name = "namespaceList")
    protected List<String> namespace;
    @XmlAttribute(name = "processContents")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String processContents;

    /**
     * Gets the value of the namespace property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the namespace property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNamespace().add(newItem);
     * }</pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     *
     *
     */
    public List<String> getNamespace() {
        if (namespace == null) {
            namespace = new ArrayList<String>();
        }
        return this.namespace;
    }

    /**
     * Gets the value of the processContents property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getProcessContents() {
        if (processContents == null) {
            return "strict";
        } else {
            return processContents;
        }
    }

    /**
     * Sets the value of the processContents property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setProcessContents(String value) {
        this.processContents = value;
    }

}
