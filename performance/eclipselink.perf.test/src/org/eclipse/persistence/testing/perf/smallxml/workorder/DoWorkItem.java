/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Martin Vojtek - 2.6.0 - initial implementation
package org.eclipse.persistence.testing.perf.smallxml.workorder;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for doWorkItem complex type.</p>
 *
 * <p>The following schema fragment specifies the expected content contained within this class.</p>
 *
 * <pre>
 * &lt;complexType name="doWorkItem">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="workLocation" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="wid" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
@XmlType(name = "doWorkItem", propOrder = {
    "workLocation",
    "wid"
})
public class DoWorkItem {

    protected int workLocation;
    protected int wid;

    /**
     * Gets the value of the workLocation property.
     *
     */
    public int getWorkLocation() {
        return workLocation;
    }

    /**
     * Sets the value of the workLocation property.
     *
     */
    public void setWorkLocation(int value) {
        this.workLocation = value;
    }

    /**
     * Gets the value of the wid property.
     *
     */
    public int getWid() {
        return wid;
    }

    /**
     * Sets the value of the wid property.
     *
     */
    public void setWid(int value) {
        this.wid = value;
    }

}
