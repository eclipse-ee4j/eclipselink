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
//     Blaise Doughan - 2.3 - initial implementation
//     Praba Vijayaratnam - 2.3 - test automation
package org.eclipse.persistence.testing.jaxrs.model;

import org.eclipse.persistence.oxm.annotations.XmlInverseReference;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@Entity
@XmlRootElement
@Table(name = "ADDRESS")
public class Address implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private long id;

    private String city;

    private String street;

    @OneToOne
    @PrimaryKeyJoinColumn
    private Customer customer;

    public Address() {
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return this.street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    @XmlInverseReference(mappedBy = "address")
    public Customer getCustomer() {
        return this.customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public boolean equals(Object object) {
        if (null == object || object.getClass() != this.getClass()) {
            return false;
        }
        Address test = (Address) object;
        if (!equals(id, test.getId())) {
            return false;
        }
        if (!equals(city, test.getCity())) {
            return false;
        }
        if (!equals(street, test.getStreet())) {
            return false;
        }
        if (null == test.getCustomer() && null != customer) {
            return false;
        }
        if (null != test.getCustomer() && null == customer) {
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
