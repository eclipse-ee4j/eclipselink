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
//     Denise Smith - 2.3
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmladapter.packagelevel.unspecified;

import java.math.BigDecimal;

import javax.xml.bind.annotation.*;

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
