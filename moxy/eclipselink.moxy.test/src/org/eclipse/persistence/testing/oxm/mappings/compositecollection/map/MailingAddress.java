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
package org.eclipse.persistence.testing.oxm.mappings.compositecollection.map;

public class MailingAddress {
    public static String HOME_TYPE = "home";
    public static String WORK_TYPE = "work";
    private AddressInfo addressInfo;
    private String addressType;
    private String test;

    public MailingAddress() {
        super();
    }

    public String getAddressType() {
        return addressType;
    }

    public void setAddressType(String type) {
        addressType = type;

    }

    public String toString() {
        String returnString = " MailingAddress: " + getAddressType();
        if (getAddressInfo() != null) {
            returnString += (getAddressInfo().getStreet() + " " + getAddressInfo().getCity() + " " + getAddressInfo().getProvince() + " " + getAddressInfo().getPostalCode());
        }
        return returnString;
    }

    public boolean equals(Object object) {
        if (!(object instanceof MailingAddress)) {
            return false;
        }
        MailingAddress addressObject = (MailingAddress)object;
        if (!(this.getAddressType().equals(addressObject.getAddressType()))) {
            return false;
        }

        if ((this.getAddressInfo() == null) && (addressObject.getAddressInfo() == null)) {
            return true;
        }
        if (this.getAddressInfo().getCity().equals(addressObject.getAddressInfo().getCity())) {
            if (this.getAddressInfo().getStreet().equals(addressObject.getAddressInfo().getStreet())) {
                if (this.getAddressInfo().getProvince().equals(addressObject.getAddressInfo().getProvince())) {
                    if (this.getAddressInfo().getPostalCode().equals(addressObject.getAddressInfo().getPostalCode())) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public void setAddressInfo(AddressInfo addressInfo) {
        this.addressInfo = addressInfo;
    }

    public AddressInfo getAddressInfo() {
        return addressInfo;
    }

    public void setTest(String test) {
        this.test = test;
    }

    public String getTest() {
        return test;
    }
}
