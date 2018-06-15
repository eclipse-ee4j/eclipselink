/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     James - initial impl
package org.eclipse.persistence.testing.models.plsql;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Used to test simple PLSQL record types.
 *
 * @author James
 */
public class Employee {
    protected BigDecimal id;
    protected String name;
    protected boolean active;
    protected Address address;
    protected List<Phone> phones = new ArrayList<Phone>();

    public BigDecimal getId() {
        return id;
    }
    public void setId(BigDecimal id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Address getAddress() {
        return address;
    }
    public void setAddress(Address address) {
        this.address = address;
    }
    public List<Phone> getPhones() {
        return phones;
    }
    public void setPhones(List<Phone> phones) {
        this.phones = phones;
    }

    public boolean equals(Object object) {
        if (!(object instanceof Employee)) {
            return false;
        }
        Employee employee = (Employee)object;
        if (this.id != null && !this.id.equals(employee.id)) {
            return false;
        }
        if (this.name != null && !this.name.equals(employee.name)) {
            return false;
        }
        if (this.address != null && !this.address.equals(employee.address)) {
            return false;
        }
        return true;
    }
    public boolean isActive() {
        return active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }
}
