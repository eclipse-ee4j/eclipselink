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
package org.eclipse.persistence.testing.jaxb.xmlmarshaller;

public class Address  {

    private String street;
    private String city;
    private String state;
    private String zipCode;

    public Address() {
        super();
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String newStreet) {
        street = newStreet;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String newCity) {
        city = newCity;
    }

    public String getState() {
        return state;
    }

    public void setState(String newState) {
        state = newState;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String newZipCode) {
        zipCode = newZipCode;
    }

    public String toString() {
        return "Address: " + getStreet() + ", " +  getCity() + ", " + getState() + ", " + getZipCode();
    }

}
