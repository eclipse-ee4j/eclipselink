/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     tware - initial implementation as part of extensibility feature
package org.eclipse.persistence.testing.models.jpa.extensibility;

import java.util.HashMap;
import java.util.Map;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;

import org.eclipse.persistence.annotations.VirtualAccessMethods;

@Entity(name="ExtensibilityEmployee")
@Table(name="EXTENS_EMP")
@VirtualAccessMethods(get="getExt", set="putExt")
public class Employee {

    @Id
    @GeneratedValue
    private int id;

    private String firstName;

    private String lastName;

    @ManyToOne(cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    private Address address;

    @Version
    private long version;

    @Transient
    private Map<String, Object> extensions = new HashMap<String, Object>();

    public Employee() {
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

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }


    protected Map<String, Object> getExtensions() {
        return this.extensions;
    }

    @SuppressWarnings("unchecked")
    public <T> T getExt(String name) {
        return (T) getExtensions().get(name);
    }

    public Object putExt(String name, Object value)
    {
        return getExtensions().put(name, value);
    }

    public String toString(){
        return "Employee " + id + " - " + firstName + " " + lastName;
    }
}
