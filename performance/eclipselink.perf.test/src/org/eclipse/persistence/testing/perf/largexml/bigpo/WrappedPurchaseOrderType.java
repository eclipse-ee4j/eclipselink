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
package org.eclipse.persistence.testing.perf.largexml.bigpo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.persistence.testing.perf.largexml.bigpo.order.OrderType;


/**
 * <p>Java class for WrappedPurchaseOrderType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="WrappedPurchaseOrderType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="firstMessage" type="{urn:oasis:names:specification:ubl:schema:xsd:Order-1.0}OrderType"/>
 *         &lt;element name="secondMessage" type="{http://org.eclipse.persistence.testing.perf/bigPurchaseOrderworkItem}BigPurchaseOrderType"/>
 *         &lt;element name="result" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WrappedPurchaseOrderType", propOrder = {
    "firstMessage",
    "secondMessage",
    "result"
})
public class WrappedPurchaseOrderType {

    @XmlElement(required = true)
    protected OrderType firstMessage;
    @XmlElement(required = true)
    protected BigPurchaseOrderType secondMessage;
    @XmlElement(required = true)
    protected String result;

    /**
     * Gets the value of the firstMessage property.
     *
     * @return
     *     possible object is
     *     {@link OrderType }
     *
     */
    public OrderType getFirstMessage() {
        return firstMessage;
    }

    /**
     * Sets the value of the firstMessage property.
     *
     * @param value
     *     allowed object is
     *     {@link OrderType }
     *
     */
    public void setFirstMessage(OrderType value) {
        this.firstMessage = value;
    }

    /**
     * Gets the value of the secondMessage property.
     *
     * @return
     *     possible object is
     *     {@link BigPurchaseOrderType }
     *
     */
    public BigPurchaseOrderType getSecondMessage() {
        return secondMessage;
    }

    /**
     * Sets the value of the secondMessage property.
     *
     * @param value
     *     allowed object is
     *     {@link BigPurchaseOrderType }
     *
     */
    public void setSecondMessage(BigPurchaseOrderType value) {
        this.secondMessage = value;
    }

    /**
     * Gets the value of the result property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getResult() {
        return result;
    }

    /**
     * Sets the value of the result property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setResult(String value) {
        this.result = value;
    }

}
