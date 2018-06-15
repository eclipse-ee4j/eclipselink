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

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * jUnit test data model to verify <code>@Cacheable</code> annotation functionality.
 * This class extends {@link ProductTrue} with <code>@Cacheable(true)</code>
 * and inherits <code>@Cacheable</code> settings from parent class.
 * Instances of this class are expected to be in the cache.
 */
@Entity
@DiscriminatorValue("SW")
public class ProductSoftwareTrue extends ProductTrue {

    // Some more attribute to have there.
    /** Version number. */
    @Column(name = "VNUMBER")
    private int versionNumber;

    /**
     * Constructs an instance of hardware product class with caching turned on.
     */
    public ProductSoftwareTrue() {
        super();
    }

    /**
     * Constructs an instance of hardware product class with caching turned on.
     * @param id Product ID.
     * @param quantity Product quantity.
     * @param versionNumber Hardware product version number.
     */
    public ProductSoftwareTrue(int id, int quantity, int versionNumber) {
        super(id, quantity);
        this.versionNumber = versionNumber;
    }

    /**
     * Get hardware product version number.
     * @return Hardware product version number.
     */
    public int getVersionNumber() {
        return versionNumber;
    }

    /**
     * Set hardware product version number.
     * @param versionNumber Hardware product version number.
     */
    public void setVersionNumber(int versionNumber) {
        this.versionNumber = versionNumber;
    }

    /**
     * Return {@link String} representation of this object in human readable form.
     * @return {@link String} representation of this object.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ProductHardwareFalse: [");
        sb.append("id: ").append(getId()).append(", ");
        sb.append("quantity: ").append(getQuantity()).append(", ");
        sb.append("versionNumber: ").append(versionNumber).append("]");
        return sb.toString();
    }

}
