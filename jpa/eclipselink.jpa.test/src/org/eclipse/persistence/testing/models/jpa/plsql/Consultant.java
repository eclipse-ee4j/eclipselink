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
//     James - initial impl
package org.eclipse.persistence.testing.models.jpa.plsql;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Array;
import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.Structure;
import org.eclipse.persistence.annotations.TypeConverter;

/**
 * Used to test relational usage of object-relational data-types.
 *
 * @author James
 */
@Entity
@Table(name="PLSQL_CONSULTANT")
public class Consultant {
    @Id
    @Column(name="EMP_ID")
    protected BigDecimal id;
    protected String name;
    @TypeConverter(name="bool", dataType=Integer.class)
    @Convert("bool")
    protected boolean active;
    @Structure
    protected Address address;
    @Array(databaseType="PLSQL_P_PLSQL_PHONE_LIST")
    @Column(name="PHONES")
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
