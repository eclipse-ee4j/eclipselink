/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Denise Smith  June 05, 2009 - Initial implementation
package org.eclipse.persistence.testing.jaxb.listofobjects;

import java.util.ArrayList;

import jakarta.xml.bind.annotation.*;

@XmlRootElement(name = "employee-data")
@XmlAccessorType(XmlAccessType.FIELD)
public class Employee implements Comparable {

    public static final int uid = 1234567;

    @XmlAttribute(name = "id")
    public int id;

    public String firstName;

    public String lastName;

    private transient String blah;

    public java.util.Calendar birthday;

    @XmlTransient
    public int age;

    @XmlElement(name = "responsibility")
    @XmlElementWrapper(name = "responsibilities")
    public java.util.Collection responsibilities = new ArrayList();

    public String get() {
        return null;
    }

    public boolean is() {
        return false;
    }

    public void set() {
    }

    public String toString() {
        return "EMPLOYEE: " + id + " " + firstName + " " + lastName + " "
                + birthday;
    }

    public boolean equals(Object object) {
        Employee emp = ((Employee) object);
        if ((emp.id != this.id) || (!(emp.firstName.equals(this.firstName)))
                || (!(emp.lastName.equals(this.lastName)))
                || (emp.age != this.age)) {
            return false;
        }

        if (emp.birthday != null && this.birthday != null) {
            if (!(emp.birthday.equals(this.birthday))) {
                return false;
            }
        }
        if ((emp.responsibilities == null) && (this.responsibilities != null)) {
            return false;
        }
        if ((this.responsibilities == null) && (emp.responsibilities != null)) {
            return false;
        }
        if (this.responsibilities != null) {
            if (!(this.responsibilities.containsAll(emp.responsibilities))) {
                return false;
            }
            if (!(emp.responsibilities.containsAll(this.responsibilities))) {
                return false;
            }
        }
        // need to compare responsibilities
        return true;
    }

    public void setBlah(String blah) {
        this.blah = blah;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int compareTo(Object o) {
        if (this.id < ((Employee) o).getId()) {
            return -1;
        } else if (this.id > ((Employee) o).getId()) {
            return 1;
        } else {
            return 0;
        }
    }
}
