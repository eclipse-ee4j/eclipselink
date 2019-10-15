/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
// David McCann = 2.1 - Initial contribution
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
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.eclipse.org/eclipselink/xsds/persistence/oxm}xml-abstract-null-policy"&gt;
 *       &lt;attribute name="is-set-performed-for-absent-node" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" /&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
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
