/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.oxm.xmlmarshaller.twoprojects;

public class Address {
    private String street;
    private String city;

    public Address() {
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public boolean equals(Object object) {
        try {
            if (null == object) {
                return false;
            }
            Address address = (Address)object;

            if (null == street) {
                if (null != address.getStreet()) {
                    return false;
                }
            } else {
                if (!street.equals(address.getStreet())) {
                    return false;
                }
            }

            if (null == city) {
                if (null != address.getCity()) {
                    return false;
                }
            } else {
                if (!city.equals(address.getCity())) {
                    return false;
                }
            }

            return true;
        } catch (ClassCastException e) {
            return false;
        }
    }

    public String toString() {
        String string = "Address(street='";
        string += street;
        string += "' city='";
        string += city;
        string += "')";
        return string;
    }
}
