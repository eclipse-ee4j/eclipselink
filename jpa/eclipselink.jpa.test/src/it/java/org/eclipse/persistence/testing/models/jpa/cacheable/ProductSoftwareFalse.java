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

/**
 * jUnit test data model to verify <code>@Cacheable</code> annotation functionality.
 * This class extends {@link ProductFalse} with <code>@Cacheable(false)</code>
 * and inherits <code>@Cacheable</code> settings from parent class.
 * Instances of this class are expected to not be in the cache.
 */
@Entity
@DiscriminatorValue("SW")
public class ProductSoftwareFalse extends ProductFalse {

    // Some more attribute to have there.
    /** Version number. */
    @Column(name = "VNUMBER")
    private int versionNumber;

    /**
     * Constructs an instance of hardware product class with caching turned off.
     */
    public ProductSoftwareFalse() {
        super();
    }

    /**
     * Constructs an instance of hardware product class with caching turned off.
     * @param id Product ID.
     * @param quantity Product quantity.
     * @param versionNumber Hardware product version number.
     */
    public ProductSoftwareFalse(int id, int quantity, int versionNumber) {
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
