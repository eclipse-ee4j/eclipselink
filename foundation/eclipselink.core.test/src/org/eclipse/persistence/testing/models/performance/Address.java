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
package org.eclipse.persistence.testing.models.performance;

import java.io.*;

/**
 * <p><b>Purpose</b>: Represents the mailing address on an Employee
 * <p><b>Description</b>: Held in a private 1:1 relationship from Employee
 * @see Employee
 */
public class Address implements Serializable {
    protected long id;
    protected String street;
    protected String city;
    protected String province;
    protected String postalCode;
    protected String country;

    public Address() {
        this.city = "";
        this.province = "";
        this.postalCode = "";
        this.street = "";
        this.country = "";
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    /**
     * Return the persistent identifier of the receiver.
     */
    public long getId() {
        return id;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getProvince() {
        return province;
    }

    public String getStreet() {
        return street;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Set the persistent identifier of the receiver.
     */
    public void setId(long id) {
        this.id = id;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public void internalSetStreet(String street) {
        this.street = street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    /**
     * Print the address city and province.
     */
    public String toString() {
        StringWriter writer = new StringWriter();

        writer.write("Address: ");
        writer.write(getStreet());
        writer.write(", ");
        writer.write(getCity());
        writer.write(", ");
        writer.write(getProvince());
        writer.write(", ");
        writer.write(getCountry());
        return writer.toString();
    }
}
