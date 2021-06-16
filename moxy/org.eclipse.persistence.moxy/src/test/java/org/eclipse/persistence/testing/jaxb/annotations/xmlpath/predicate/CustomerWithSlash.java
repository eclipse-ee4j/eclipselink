/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Matt MacIvor - 2.4.1 - initial implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmlpath.predicate;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import org.eclipse.persistence.oxm.annotations.XmlPath;

/**
 * Tests that xpaths with a / in the predicate value (such as a url) work.
 */
@XmlRootElement(name="customer")
@XmlType(propOrder={"firstName", "lastName", "surname", "nicknames", "address", "phoneNumbers"})
public class CustomerWithSlash {

    private String firstName;
    private String lastName;
    private String surname;
    private List<String> nicknames;
    private Address address;
    private List<PhoneNumber> phoneNumbers;

    public CustomerWithSlash() {
        nicknames = new ArrayList<String>(2);
        phoneNumbers = new ArrayList<PhoneNumber>(2);
    }

    @XmlPath("personal-info[@pi-type='first/name']/name[@name-type='first']/text()")
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @XmlPath("personal-info[@pi-type='last/name']/name[@name-type='last']/text()")
    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    @XmlPath("personal-info[@pi-type='last/name']/name[@name-type='surname']/text()")
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @XmlPath("personal-info[@pi-type='nickname']/name[@name-type='nickname']/text()")
    public List<String> getNicknames() {
        return nicknames;
    }

    public void setNicknames(List<String> nickNames) {
        this.nicknames = nickNames;
    }

    @XmlPath("contact[@method=\"address\"]")
    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    @XmlPath("contact[@method=\"phone number\"]")
    public List<PhoneNumber> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(List<PhoneNumber> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    @Override
    public boolean equals(Object obj) {
        if(null == obj || obj.getClass() != this.getClass()) {
            return false;
        }
        CustomerWithSlash test = (CustomerWithSlash) obj;
        if(!equals(firstName, test.getFirstName())) {
            return false;
        }
        if(!equals(lastName, test.getLastName())) {
            return false;
        }
        if(!equals(surname, test.getSurname())) {
            return false;
        }
        return true;
    }

    private boolean equals(String control, String test) {
        if(null == control) {
            return null == test;
        }
        return control.equals(test);
    }
}

