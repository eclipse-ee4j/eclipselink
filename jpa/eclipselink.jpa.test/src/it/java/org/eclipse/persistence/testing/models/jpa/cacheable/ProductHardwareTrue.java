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
//     Tomas Kraus - Initial API and implementation
package org.eclipse.persistence.testing.models.jpa.cacheable;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

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
