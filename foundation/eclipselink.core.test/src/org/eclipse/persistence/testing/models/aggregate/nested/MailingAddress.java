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
package org.eclipse.persistence.testing.models.aggregate.nested;

public class MailingAddress {
    protected String street;
    protected String city;
    protected String province;
    protected String country;
    protected String postalCode;

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
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

    public void setCity(String param1) {
        city = param1;
    }

    public void setCountry(String param1) {
        country = param1;
    }

    public void setPostalCode(String param1) {
        postalCode = param1;
    }

    public void setProvince(String param1) {
        province = param1;
    }

    public void setStreet(String param1) {
        street = param1;
    }
}
