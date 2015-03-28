/*******************************************************************************
 * Copyright (c) 2014, 2015  Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Martin Vojtek - 2.6.0 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.perf.smallxml.workorder;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for doWorkItemResponse complex type.</p>
 *
 * <p>The following schema fragment specifies the expected content contained within this class.</p>
 *
 * <pre>
 * &lt;complexType name="doWorkItemResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="return" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
@XmlType(name = "doWorkItemResponse", propOrder = {
    "_return"
})
public class DoWorkItemResponse {

    @XmlElement(name = "return")
    protected boolean _return;

    /**
     * Gets the value of the return property.
     *
     */
    public boolean isReturn() {
        return _return;
    }

    /**
     * Sets the value of the return property.
     *
     */
    public void setReturn(boolean value) {
        this._return = value;
    }

}
