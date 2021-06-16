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
//     Denise Smith - 2.4
package org.eclipse.persistence.testing.jaxb.json.xmlvalue;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Person {

    private String firstName;
    private List<String> middleNames = new ArrayList<String>();
    private String lastName;
    private Address address;
    private PhoneNumber phoneNumber;

    public Person(){
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }


    public List<String> getMiddleNames() {
        return middleNames;
    }

    public void setMiddleNames(List<String> middleNames) {
        this.middleNames = middleNames;
    }


    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public PhoneNumber getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(PhoneNumber phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean equals(Object obj) {
        Person person;
        try {
            person = (Person) obj;
        } catch (ClassCastException cce) {
            return false;
        }

        if(address == null){
            if(person.address != null){
                return false;
            }
        } else if(!address.equals(person.address)){
            return false;
        }

        if(phoneNumber == null){
            if(person.phoneNumber != null){
                return false;
            }
        } else if(!phoneNumber.equals(person.phoneNumber)){
            return false;
        }

        if(middleNames == null){
            if(person.middleNames != null){
                return false;
            }
        }else {
            if(middleNames.size() != person.middleNames.size()){
                return false;
            }
            if(!(middleNames.containsAll(person.middleNames) && person.middleNames.containsAll(middleNames))){
                return false;
            }
        }

        return firstName.equals(person.firstName) && lastName.equals(person.lastName);
    }

}
