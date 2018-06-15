/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.eclipse.persistence.jpars.test.model.employee;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "JPARS_PHONENUMBER")
@IdClass(PhoneNumber.ID.class)
public class PhoneNumber implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "EMP_ID", updatable = false, insertable = false)
    private int id;
    @Id
    @Column(name = "PHONE_TYPE", updatable = false)
    private String _type;
    @Basic
    @Column(name = "AREA_CODE")
    private String areaCode;
    @Basic
    @Column(name = "P_NUMBER")
    private String number;
    @ManyToOne
    @JoinColumn(name = "EMP_ID")
    private Employee employee;

    public PhoneNumber() {
    }

    public PhoneNumber(String type, String areaCode, String number) {
        this();
        set_type(type);
        setAreaCode(areaCode);
        setNumber(number);
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int empId) {
        this.id = empId;
    }

    public String getNumber() {
        return this.number;
    }

    public void setNumber(String pNumber) {
        this.number = pNumber;
    }

    public String get_type() {
        return _type;
    }

    public void set_type(String _type) {
        this._type = _type;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
        this.id = employee.getId();
    }

    @Override
    public String toString() {
        return "id=" + id + ", type=" + _type + ", areaCode=" + areaCode + ", number=" + number;
    }

    public static class ID implements Serializable {
        private static final long serialVersionUID = 1L;

        public int id;
        public String _type;

        public ID() {
        }

        public ID(int empId, String _type) {
            this.id = empId;
            this._type = _type;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String get_type() {
            return _type;
        }

        public void set_type(String _type) {
            this._type = _type;
        }

        public boolean equals(Object other) {
            if (other instanceof ID) {
                final ID otherID = (ID) other;
                return otherID.id == id && otherID._type.equals(_type);
            }
            return false;
        }

        public int hashCode() {
            return super.hashCode();
        }
    }
}
