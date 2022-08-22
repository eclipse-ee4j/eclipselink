/*
 * Copyright (c) 2011, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.jpa.nosql.mapped;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import org.eclipse.persistence.nosql.annotations.DataFormatType;
import org.eclipse.persistence.nosql.annotations.NoSql;


/**
 * Model address class, maps to ADDRESS record.
 */
@Embeddable
@NoSql(dataFormat=DataFormatType.MAPPED)
public class Address {
    @Column(name="addressee")
    public String addressee;
    @Column(name="street")
    public String street;
    @Column(name="city")
    public String city;
    @Column(name="state")
    public String state;
    @Column(name="country")
    public String country;
    @Column(name="zipcode")
    public String zipCode;

    public String toString() {
        return "Address(" + addressee + ", " + street + ", " + city + ", " + state + ", " + country + ", " + zipCode + ")";
    }
}
