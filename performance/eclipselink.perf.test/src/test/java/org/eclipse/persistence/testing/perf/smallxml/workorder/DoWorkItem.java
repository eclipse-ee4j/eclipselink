/*
 * Copyright (c) 2014, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Martin Vojtek - 2.6.0 - initial implementation
package org.eclipse.persistence.testing.perf.smallxml.workorder;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;


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
