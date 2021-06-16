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
//     Denise Smith - 2.3
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.packagelevel.unspecified;

import java.math.BigDecimal;

import jakarta.xml.bind.annotation.*;

import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.classlevel.MyCalendar;

@XmlRootElement(name="employee")
@XmlAccessorType(XmlAccessType.FIELD)
public class Employee {

    public static final int uid = 1234567;

    public BigDecimal id;

    public String firstName;

    @XmlElement(name = "lastname")
    public String lastName;

    public MyCalendar birthday;

    @XmlTransient
    public int age;

    public Address address;


    public String toString() {
        return "EMPLOYEE: " + id + " " + firstName + " " + lastName + " "
                + birthday;
    }

    public boolean equals(Object object) {
        Employee emp = ((Employee)object);
        if(!id.equals(emp.id)){
            return false;
        }
        if((!(emp.firstName.equals(this.firstName))) || (!(emp.lastName.equals(this.lastName))) ||(emp.age != this.age)){
            return false;
        }

        if(!(emp.birthday.equals(this.birthday))){
              return false;
          }

        if((this.address == null) && (emp.address != null)){
            return false;
        }
        if((emp.address == null) && (this.address != null)){
            return false;
        }
        if(!address.equals(emp.address)){
            return false;
        }
        return true;
    }

}
