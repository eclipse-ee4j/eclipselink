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
package org.eclipse.persistence.testing.models.insurance;

import java.io.*;

/**
 * <p><b>Purpose</b>: Represents the mailing address of the PolicyHolder
 * <p><b>Description</b>: Held in a private 1:1 relationship from PolicyHolder
 * @see PolicyHolder
 * @since TOPLink/Java 1.0
 */
public class Address implements Serializable {
    private String street;
    private String city;
    private String state;
    private String zipCode;
    private String country;

    //Back referenec to PolicyHolder is needed in the relational model as target foreign key is used in
    //the 1:1 mapping, and the target object must have a relationship mapping to the source.
    //In the object-relational model, this is not required as in the alternative structure mapping, the
    //Address object is aggregately stored (as STRUCT type in Oracle8i) in the HOLDER (PolicyHolder) source table.
    private PolicyHolder policyHolder;

    /**
     * Initialize a new address.
     */
    public Address() {
        this.street = "";
        this.city = "";
        this.state = "";
        this.zipCode = "";
        this.country = "";
    }

    public static Address example1() {
        Address address = new Address();
        address.setStreet("4 Garden Way");
        address.setCity("Boston");
        address.setState("MA");
        address.setCountry("United States");
        address.setZipCode("28150");
        return address;
    }

    public static Address example2() {
        Address address = new Address();
        address.setStreet("10 Wall Street");
        address.setCity("Manhattan");
        address.setState("NY");
        address.setCountry("United States");
        address.setZipCode("50124");
        return address;
    }

    public static Address example3() {
        Address address = new Address();
        address.setStreet("5511 Capital Center Dr");
        address.setCity("Raleigh");
        address.setState("NC");
        address.setCountry("United States");
        address.setZipCode("27606");
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public PolicyHolder getPolicyHolder() {
        return policyHolder;
    }

    public String getState() {
        return state;
    }

    public String getStreet() {
        return street;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setPolicyHolder(PolicyHolder policyHolder) {
        this.policyHolder = policyHolder;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String toString() {
        return "Address: " + getStreet() + ", " + getCity() + "," + getState();
    }
}
