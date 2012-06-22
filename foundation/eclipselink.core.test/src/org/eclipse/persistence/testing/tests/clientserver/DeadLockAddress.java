/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.clientserver;

import java.math.*;
import java.io.*;

/**
 * <p><b>Purpose</b>: Represents the mailing address on an Employee
 * <p><b>Description</b>: Held in a private 1:1 relationship from Employee
 * @see Employee
 */
public class DeadLockAddress implements Serializable {
    public BigDecimal id;
    public String street;
    public String city;
    public String province;
    public String postalCode;
    public String country;

    public DeadLockAddress() {
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
    public BigDecimal getId() {
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
    public void setId(BigDecimal id) {
        this.id = id;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public void setProvince(String province) {
        this.province = province;
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
