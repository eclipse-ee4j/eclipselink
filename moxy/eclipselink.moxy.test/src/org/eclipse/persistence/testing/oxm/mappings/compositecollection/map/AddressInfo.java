/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.mappings.compositecollection.map;

public class AddressInfo {
    private String street;
    private String city;
    private String province;
    private String postalCode;

    public AddressInfo() {
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

    public String getProvince() {
        return province;
    }

    public void setProvince(String newProvince) {
        province = newProvince;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String newPostalCode) {
        postalCode = newPostalCode;
    }
}
