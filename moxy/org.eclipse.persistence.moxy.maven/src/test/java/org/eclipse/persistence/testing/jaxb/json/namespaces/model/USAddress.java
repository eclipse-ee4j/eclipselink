/*
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
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
//     Radek Felcman - January 2019 - Initial implementation
package org.eclipse.persistence.testing.jaxb.json.namespaces.model;

import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "USAddress", propOrder = {
        "name",
        "street",
        "city",
        "state",
        "zip"
})
public class USAddress {

    @XmlElement(required = true)
    protected String name;
    @XmlElement(required = true)
    protected String street;
    @XmlElement(required = true)
    protected String city;
    @XmlElement(required = true)
    protected String state;
    @XmlElement(required = true)
    protected Integer zip;

    public String getName() {
        return name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String value) {
        this.street = value;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String value) {
        this.city = value;
    }

    public String getState() {
        return state;
    }

    public void setState(String value) {
        this.state = value;
    }

    public Integer getZip() {
        return zip;
    }

    public void setZip(Integer value) {
        this.zip = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        USAddress usAddress = (USAddress) o;
        return Objects.equals(getName(), usAddress.getName()) &&
                Objects.equals(getStreet(), usAddress.getStreet()) &&
                Objects.equals(getCity(), usAddress.getCity()) &&
                Objects.equals(getState(), usAddress.getState()) &&
                Objects.equals(getZip(), usAddress.getZip());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getName(), getStreet(), getCity(), getState(), getZip());
    }
}
