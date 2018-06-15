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
//     bdoughan - August 25/2009 - 1.2 - Initial implementation
package org.eclipse.persistence.testing.oxm.xmlcontext.byxpath;

public class Address {

    private String street1;
    private String street2;
    private String city;

    public String getStreet1() {
        return street1;
    }

    public void setStreet1(String street) {
        this.street1 = street;
    }

    public String getStreet2() {
        return street2;
    }

    public void setStreet2(String street) {
        this.street2 = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

}
