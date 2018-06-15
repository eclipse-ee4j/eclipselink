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
//     tware - initial implementation as part of extensibility feature
package org.eclipse.persistence.testing.models.jpa.extensibility;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

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
