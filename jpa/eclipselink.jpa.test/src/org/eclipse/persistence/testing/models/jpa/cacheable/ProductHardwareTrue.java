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
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * jUnit test data model to verify <code>@Cacheable</code> annotation functionality.
 * This class extends {@link ProductFalse} with <code>@Cacheable(false)</code>
 * but also changes it's own settings to <code>@Cacheable(true)</code>.
 * Instances of this class are expected to be in the cache.
 */
@Entity
@Cacheable(true)
@DiscriminatorValue("HW")
public class ProductHardwareTrue extends ProductFalse {

    // Some more attribute to have there.
    /** Model number. */
    @Column(name = "MNUMBER")
    private int modelNumber;

    /**
     * Constructs an instance of hardware product class with caching turned on.
     */
    public ProductHardwareTrue() {
        super();
    }

    /**
     * Constructs an instance of hardware product class with caching turned on.
     * @param id Product ID.
     * @param quantity Product quantity.
     * @param modelNumber Hardware product model number.
     */
    public ProductHardwareTrue(int id, int quantity, int modelNumber) {
        super(id, quantity);
        this.modelNumber = modelNumber;
    }

    /**
     * Get hardware product model number.
     * @return Hardware product model number.
     */
    public int getModelNumber() {
        return modelNumber;
    }

    /**
     * Set hardware product model number.
     * @param modelNumber Hardware product model number.
     */
    public void setModelNumber(int modelNumber) {
        this.modelNumber = modelNumber;
    }

    /**
     * Return {@link String} representation of this object in human readable form.
     * @return {@link String} representation of this object.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ProductHardwareTrue: [");
        sb.append("id: ").append(getId()).append(", ");
        sb.append("quantity: ").append(getQuantity()).append(", ");
        sb.append("modelNumber: ").append(modelNumber).append("]");
        return sb.toString();
    }

}
