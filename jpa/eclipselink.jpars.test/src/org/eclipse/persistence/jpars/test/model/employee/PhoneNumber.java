/*******************************************************************************
 * Copyright (c) 2011, 2013 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 ******************************************************************************/
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
    @Column(updatable = false)
    private String type;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
        this.id = employee.getId();
    }

    public static class ID implements Serializable {
        private static final long serialVersionUID = 1L;

        public int id;
        public String type;

        public ID() {
        }

        public ID(int empId, String type) {
            this.id = empId;
            this.type = type;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public boolean equals(Object other) {
            if (other instanceof ID) {
                final ID otherID = (ID) other;
                return otherID.id == id && otherID.type.equals(type);
            }
            return false;
        }

        public int hashCode() {
            return super.hashCode();
        }
    }
}
