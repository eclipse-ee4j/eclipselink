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

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BigPurchaseOrderType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="BigPurchaseOrderType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="poID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="branchID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="numberOfItems" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="items" type="{http://org.eclipse.persistence.testing.perf/bigPurchaseOrderworkItem}ItemType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BigPurchaseOrderType", propOrder = {
    "poID",
    "branchID",
    "numberOfItems",
    "items"
})
public class BigPurchaseOrderType {

    @XmlElement(required = true)
    protected String poID;
    @XmlElement(required = true)
    protected String branchID;
    protected int numberOfItems;
    @XmlElement(required = true)
    protected List<ItemType> items;

    /**
     * Gets the value of the poID property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getPoID() {
        return poID;
    }

    /**
     * Sets the value of the poID property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPoID(String value) {
        this.poID = value;
    }

    /**
     * Gets the value of the branchID property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getBranchID() {
        return branchID;
    }

    /**
     * Sets the value of the branchID property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setBranchID(String value) {
        this.branchID = value;
    }

    /**
     * Gets the value of the numberOfItems property.
     *
     */
    public int getNumberOfItems() {
        return numberOfItems;
    }

    /**
     * Sets the value of the numberOfItems property.
     *
     */
    public void setNumberOfItems(int value) {
        this.numberOfItems = value;
    }

    /**
     * Gets the value of the items property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the items property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getItems().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ItemType }
     *
     *
     */
    public List<ItemType> getItems() {
        if (items == null) {
            items = new ArrayList<ItemType>();
        }
        return this.items;
    }

}
