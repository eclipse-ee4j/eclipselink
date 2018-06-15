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
//     Denise Smith - 2.4
package org.eclipse.persistence.testing.jaxb.json.namespaces;

import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.eclipse.persistence.oxm.annotations.XmlPath;

@XmlRootElement(namespace="namespace0")
public class Person {
    @XmlAttribute(namespace="namespace2")
    private int id;
    @XmlElement(namespace="namespace1")
    private String firstName;

    @XmlElement(namespace="namespace1")
    private List<String> middleNames;

    @XmlElement(namespace="namespace2")
    private String lastName;
    @XmlPath(value="ns3:contact/ns1:address")
    private Address address;

    @XmlElement(namespace="namespace1")
    public String a;

    @XmlElement(namespace="namespace1")
    public String aa;

    @XmlElement(namespace="namespace1")
    public String aaa;

    @XmlElement(namespace="namespace1")
    public Address aaaa;

    @XmlElement(namespace="namespace1")
    public List<Address> aaaaa;

    @XmlAttribute(namespace="namespace1")
    public String theattribute;

    public Person(){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

        if((theattribute == null && person.theattribute != null) || (theattribute != null && !theattribute.equals(person.theattribute))){
            return false;
        }

        if((a == null && person.a != null) || (a != null && !a.equals(person.a))){
            return false;
        }

        if((aa == null && person.aa != null) || (aa != null && !aa.equals(person.aa))){
            return false;
        }

        if((aaa == null && person.aaa != null) || (aaa != null && !aaa.equals(person.aaa))){
            return false;
        }
        if((aaaa == null && person.aaaa != null) || (aaaa != null && !aaaa.equals(person.aaaa))){
            return false;
        }
        if((aaaaa == null && person.aaaaa != null) || (aaaaa != null && !aaaaa.equals(person.aaaaa))){
            return false;
        }
        return getId()== person.getId() && firstName.equals(person.firstName) && lastName.equals(person.lastName);
    }
}
