/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.3 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.annotations.xmlpath.predicate;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.persistence.oxm.annotations.XmlPath;

@XmlRootElement
@XmlType(propOrder={"firstName", "lastName", "surname", "nicknames", "address", "phoneNumbers"})
public class Customer {

    private String firstName;
    private String lastName;
    private String surname;
    private List<String> nicknames;
    private Address address;
    private List<PhoneNumber> phoneNumbers;

    public Customer() {
        nicknames = new ArrayList<String>(2);
        phoneNumbers = new ArrayList<PhoneNumber>(2);
    }

    @XmlPath("personal-info[@pi-type='first-name']/name[@name-type='first']/text()")
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @XmlPath("personal-info[@pi-type='last-name']/name[@name-type='last']/text()")
    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    @XmlPath("personal-info[@pi-type='last-name']/name[@name-type='surname']/text()")
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
        Customer test = (Customer) obj;
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