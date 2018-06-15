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
//              dclarke - initial JPA Employee example using XML (bug 217884)
//              mbraeuer - annotated version
package org.eclipse.persistence.testing.perf.jpa.model.basic;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Represents the phone number an employee.
 * This demonstrates the usage of a composite Id, and Id using a ManyToOne relationship.
 */
@Entity
@Table(name = "P2_PHONE")
@IdClass(PhoneNumber.ID.class)
public class PhoneNumber implements Serializable {

    @Id
    @Column(updatable = false)
    private String type;
    @Basic
    @Column(name = "AREA_CODE")
    private String areaCode;
    @Basic
    @Column(name = "P_NUMBER")
    private String number;
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EMP_ID")
    private Employee owner;

    public PhoneNumber() {
    }

    public PhoneNumber(String type, String areaCode, String number) {
        this();
        setType(type);
        setAreaCode(areaCode);
        setNumber(number);
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getNumber() {
        return this.number;
    }

    public void setNumber(String pNumber) {
        this.number = pNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Employee getOwner() {
        return this.owner;
    }

    protected void setOwner(Employee employee) {
        this.owner = employee;
    }

    public static class ID implements Serializable {
        public long owner;
        public String type;

        public ID() {
        }

        public ID(int empId, String type) {
            this.owner = empId;
            this.type = type;
        }

        @Override
        public boolean equals(Object other) {
            if (other instanceof ID) {
                final ID otherID = (ID) other;
                return otherID.owner == this.owner && otherID.type.equals(type);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return (int)this.owner + this.type.hashCode();
        }
    }
}
