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
//     Tomas Kraus - Initial API and implementation
package org.eclipse.persistence.testing.models.jpa.cacheable;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * jUnit test data model to verify <code>@Cacheable</code> annotation functionality.
 * This is model top level class with <code>@Cacheable(false)</code>.
 * Instances of this class are expected to not be in the cache.
 */
@Entity
@Cacheable(false)
@Table(name = "PRODUCT_FALSE")
public class ProductFalse {

    /** Primary key. */
    @Id
    @Column(name = "ID")
    private int id;

    // Some attribute to have there.
    /** Quantity. */
    @Column(name = "QUANTITY")
    private int quantity;

    /**
     * Constructs an instance of product class with caching turned off.
     */
    public ProductFalse() {
    }

    /**
     * Constructs an instance of product class with caching turned off.
     * @param id Product ID.
     * @param quantity Product quantity.
     */
    public ProductFalse(int id, int quantity) {
        this.id = id;
        this.quantity = quantity;
    }

    /**
     * Get product ID (primary key).
     * @return Product ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Set product ID (primary key).
     * @param id Product ID.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Get product quantity.
     * @return Product quantity.
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Set product quantity.
     * @param quantity Product quantity.
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * Return {@link String} representation of this object in human readable form.
     * @return {@link String} representation of this object.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ProductFalse: [");
        sb.append("id: ").append(id).append(", ");
        sb.append("quantity: ").append(quantity).append("]");
        return sb.toString();
    }

}
