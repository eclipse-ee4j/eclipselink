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
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.eclipse.org/eclipselink/xsds/persistence/oxm}xml-abstract-null-policy">
 *       &lt;attribute name="is-set-performed-for-absent-node" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
public class XmlNullPolicy
    extends XmlAbstractNullPolicy
{

    @XmlAttribute(name = "is-set-performed-for-absent-node")
    protected Boolean isSetPerformedForAbsentNode;

    /**
     * Gets the value of the isSetPerformedForAbsentNode property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isIsSetPerformedForAbsentNode() {
        if (isSetPerformedForAbsentNode == null) {
            return true;
        } else {
            return isSetPerformedForAbsentNode;
        }
    }

    /**
     * Sets the value of the isSetPerformedForAbsentNode property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsSetPerformedForAbsentNode(Boolean value) {
        this.isSetPerformedForAbsentNode = value;
    }

}