/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//  - rbarkhouse - 04 May 2012 - 2.4 - Initial implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmlpath.predicate.adapter;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.eclipse.persistence.oxm.annotations.XmlPath;
import org.eclipse.persistence.testing.jaxb.annotations.xmlpath.predicate.Address;

@XmlRootElement
@XmlType(propOrder = {"id", "name", "address", "altAddress", "phoneNumber", "phoneNumbers"})
@XmlAccessorType(XmlAccessType.FIELD)
public class Customer {

    private String id;
    private String name;

    @XmlJavaTypeAdapter(LinkAdapter.class)
    @XmlPath("atomic:link[@rel='address']/@href")
    private Address address = new Address();

    @XmlJavaTypeAdapter(LinkAdapter.class)
    @XmlPath("atomic:link[@rel='address']/href/text()")
    private Address altAddress = new Address();

    @XmlJavaTypeAdapter(LinkAdapter.class)
    @XmlPath("atomic:link[@rel='phone']/@href")
    private PhoneNumber phoneNumber = new PhoneNumber();

    @XmlJavaTypeAdapter(LinkAdapter.class)
    @XmlPath("atom:link[@rel='phone']/@href")
    private ArrayList<PhoneNumber> phoneNumbers = new ArrayList<PhoneNumber>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<PhoneNumber> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(ArrayList<PhoneNumber> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Address getAltAddress() {
        return altAddress;
    }

    public void setAltAddress(Address altAddress) {
        this.altAddress = altAddress;
    }

    public PhoneNumber getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(PhoneNumber phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public boolean equals(Object obj) {
        Customer c = (Customer) obj;
        return this.getId().equals(c.getId()) && this.getName().equals(c.getName()) && this.getPhoneNumbers().equals(c.getPhoneNumbers());
    }
}
