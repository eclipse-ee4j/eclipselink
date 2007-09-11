/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
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