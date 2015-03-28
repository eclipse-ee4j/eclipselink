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
 *     jlamande    - Initial API and implementation
 *     Tomas Kraus - EclipseLink jUnit tests integration
 ******************************************************************************/
package org.eclipse.persistence.testing.models.jpa.advanced.customer;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Second abstract {@link CustomerAddress} entity offspring.
 */
@Entity
@DiscriminatorValue("2")
public class AddressType2 extends CustomerAddress {

    /** Street name. */
    @Column(name="STREET2")
    private String street2;

    /**
     * Creates an instance of second {@link CustomerAddress} entity offspring. No entity attributes are set.
     */
    public AddressType2() {
    }

    /**
     * Creates an instance of second {@link CustomerAddress} entity offspring.
     * @param street Street name to be set.
     */
    public AddressType2(final String street) {
        this.street2 = street;
    }

    /**
     * Get street name.
     * @return Street name attribute value.
     */
    @Override
    public String getStreet() {
        return street2;
    }

    /**
     * Set street name.
     * @param street Street name to be set.
     */
    @Override
    public void setStreet(final String street) {
        street2 = street;
    }

    /**
     * Return second {@link CustomerAddress} entity offspring in human readable form.
     * @return Entity converted to {@link String} in human readable form.
     */
    @Override
    public String toString() {
        return "AddressType2 [" + street2 + "]";
    }
}
