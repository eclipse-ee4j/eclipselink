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
//     Blaise Doughan - 2.3 - initial implementation
//     Praba Vijayaratnam - 2.3 - test automation
package org.eclipse.persistence.testing.jaxrs.model;

import org.eclipse.persistence.oxm.annotations.XmlPath;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.List;

@NamedQueries({
        @NamedQuery(name = "findCustomerById", query = "SELECT c "
                + "FROM Customer c " + "WHERE c.id = :id"),
        @NamedQuery(name = "findCustomerByName", query = "SELECT c "
                + "FROM Customer c " + "WHERE c.firstName = :firstName AND "
                + "      c.lastName = :lastName"),
        @NamedQuery(name = "findCustomerByCity", query = "SELECT c "
                + "FROM Customer c " + "WHERE c.address.city = :city ") })
@Entity
@XmlRootElement
@Table(name = "CUSTOMER")
public class Customer implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private long id;

    @Column(name = "FIRST_NAME")
    private String firstName;

    @Column(name = "LAST_NAME")
    private String lastName;

    @OneToMany(mappedBy = "customer", cascade = { CascadeType.ALL })
    private List<PhoneNumber> phoneNumbers;

    @OneToOne(mappedBy = "customer", cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
    private Address address;

    public Customer() {
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @XmlPath("moxy/firstName/text()")
    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<PhoneNumber> getPhoneNumbers() {
        return this.phoneNumbers;
    }

    public void setPhoneNumbers(List<PhoneNumber> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public Address getAddress() {
        return this.address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public boolean equals(Object object) {
        if (null == object || object.getClass() != this.getClass()) {
            return false;
        }
        Customer test = (Customer) object;
        if (!equals(id, test.getId())) {
            return false;
        }
        if (!equals(firstName, test.getFirstName())) {
            return false;
        }
        if (!equals(lastName, test.getLastName())) {
            return false;
        }
        if (null == test.getAddress() && null != address) {
            return false;
        }
        if (null != test.getPhoneNumbers() && null == phoneNumbers) {
            return false;
        }
        return true;
    }

    private boolean equals(Object control, Object test) {
        if (null == control) {
            return null == test;
        }
        return control.equals(test);
    }

}
