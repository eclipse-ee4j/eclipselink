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
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ItemType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ItemType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="itemID" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="itemTypeID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="quantity" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="branch" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="amount" type="{http://www.w3.org/2001/XMLSchema}float"/>
 *         &lt;element name="timeToDeliver" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ItemType", propOrder = {
    "itemID",
    "itemTypeID",
    "quantity",
    "branch",
    "amount",
    "timeToDeliver"
})
public class ItemType {

    protected int itemID;
    @XmlElement(required = true)
    protected String itemTypeID;
    protected int quantity;
    protected int branch;
    protected float amount;
    protected int timeToDeliver;

    /**
     * Gets the value of the itemID property.
     *
     */
    public int getItemID() {
        return itemID;
    }

    /**
     * Sets the value of the itemID property.
     *
     */
    public void setItemID(int value) {
        this.itemID = value;
    }

    /**
     * Gets the value of the itemTypeID property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getItemTypeID() {
        return itemTypeID;
    }

    /**
     * Sets the value of the itemTypeID property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setItemTypeID(String value) {
        this.itemTypeID = value;
    }

    /**
     * Gets the value of the quantity property.
     *
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Sets the value of the quantity property.
     *
     */
    public void setQuantity(int value) {
        this.quantity = value;
    }

    /**
     * Gets the value of the branch property.
     *
     */
    public int getBranch() {
        return branch;
    }

    /**
     * Sets the value of the branch property.
     *
     */
    public void setBranch(int value) {
        this.branch = value;
    }

    /**
     * Gets the value of the amount property.
     *
     */
    public float getAmount() {
        return amount;
    }

    /**
     * Sets the value of the amount property.
     *
     */
    public void setAmount(float value) {
        this.amount = value;
    }

    /**
     * Gets the value of the timeToDeliver property.
     *
     */
    public int getTimeToDeliver() {
        return timeToDeliver;
    }

    /**
     * Sets the value of the timeToDeliver property.
     *
     */
    public void setTimeToDeliver(int value) {
        this.timeToDeliver = value;
    }

}
