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
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;


/**
 *
 *           memberTypes attribute must be non-empty or there must be
 *           at least one simpleType child
 *
 *
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>{@code
 * <complexType>
 *   <complexContent>
 *     <extension base="{http://www.w3.org/2001/XMLSchema}annotated">
 *       <sequence>
 *         <element name="simpleType" type="{http://www.w3.org/2001/XMLSchema}localSimpleType" maxOccurs="unbounded" minOccurs="0"/>
 *       </sequence>
 *       <attribute name="memberTypes">
 *         <simpleType>
 *           <list itemType="{http://www.w3.org/2001/XMLSchema}QName" />
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
@XmlType(name = "", propOrder = {
    "simpleType"
})
@XmlRootElement(name = "union")
public class Union
    extends Annotated
{

    protected List<LocalSimpleType> simpleType;
    @XmlAttribute(name = "memberTypes")
    protected List<QName> memberTypes;

    /**
     * Gets the value of the simpleType property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the simpleType property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSimpleType().add(newItem);
     * }</pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link LocalSimpleType }
     *
     *
     */
    public List<LocalSimpleType> getSimpleType() {
        if (simpleType == null) {
            simpleType = new ArrayList<LocalSimpleType>();
        }
        return this.simpleType;
    }

    /**
     * Gets the value of the memberTypes property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the memberTypes property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMemberTypes().add(newItem);
     * }</pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link QName }
     *
     *
     */
    public List<QName> getMemberTypes() {
        if (memberTypes == null) {
            memberTypes = new ArrayList<QName>();
        }
        return this.memberTypes;
    }

}
