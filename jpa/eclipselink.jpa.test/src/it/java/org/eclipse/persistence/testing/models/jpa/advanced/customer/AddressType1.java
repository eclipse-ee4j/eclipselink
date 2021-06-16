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
//     jlamande    - Initial API and implementation
//     Tomas Kraus - EclipseLink jUnit tests integration
package org.eclipse.persistence.testing.models.jpa.advanced.customer;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * First abstract {@link CustomerAddress} entity offspring.
 */
@Entity
@DiscriminatorValue("1")
public class AddressType1 extends CustomerAddress {

    /** Street name. */
    @Column(name="STREET1")
    private String street1;

    /**
     * Creates an instance of first {@link CustomerAddress} entity offspring. No entity attributes are set.
     */
    public AddressType1() {
    }

    /**
     * Creates an instance of first {@link CustomerAddress} entity offspring.
     * @param street Street name to be set.
     */
    public AddressType1(final String street) {
        street1 = street;
    }

    /**
     * Get street name.
     * @return Street name attribute value.
     */
    @Override
    public String getStreet() {
        return street1;
    }

    /**
     * Set street name.
     * @param street Street name to be set.
     */
    @Override
    public void setStreet(final String street) {
        street1 = street;
    }

    /**
     * Return first {@link CustomerAddress} entity offspring in human readable form.
     * @return Entity converted to {@link String} in human readable form.
     */
    @Override
    public String toString() {
        return "AddressType1 [" + street1 + "]";
    }

}
