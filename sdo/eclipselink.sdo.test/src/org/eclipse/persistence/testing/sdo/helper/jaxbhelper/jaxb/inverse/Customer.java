/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     bdoughan - May 19/2010 - 2.1 - Initial implementation
package org.eclipse.persistence.testing.sdo.helper.jaxbhelper.jaxb.inverse;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;

public class Customer {

    private Address address;

    @XmlElement(name="phoneNumber")
    private List<PhoneNumber> phoneNumbers;

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public List<PhoneNumber> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(List<PhoneNumber> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

}
