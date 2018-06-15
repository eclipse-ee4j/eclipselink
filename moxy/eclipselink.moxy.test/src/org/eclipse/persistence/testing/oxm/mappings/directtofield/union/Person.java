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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.mappings.directtofield.union;

import java.util.Calendar;
import java.util.Date;

public class Person {
    private String firstName;
    private String lastName;
    private Date anniversaryDate;
    private Object age;

    public Person() {
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String newFirstName) {
        firstName = newFirstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String newLastName) {
        lastName = newLastName;
    }

    public Object getAge() {
        return age;
    }

    public void setAge(Object newAge) {
        age = newAge;
        //age = 10;
    }

    public Date getAnniversaryDate() {
        return anniversaryDate;
    }

    public void setAnniversaryDate(Date value) {
        anniversaryDate = value;
    }

    public boolean equals(Object object) {
        try {
            Person person = (Person)object;
            if (!this.getAge().equals(person.getAge())) {
                return false;
            }
            if (!this.getFirstName().equals(person.getFirstName())) {
                return false;
            }
            if (!this.getLastName().equals(person.getLastName())) {
                return false;
            }
            if ((this.getAnniversaryDate() == null) && (person.getAnniversaryDate() == null)) {
                //do nothing
            } else if (!this.getAnniversaryDate().equals(person.getAnniversaryDate())) {
                return false;
            }
            return true;
        } catch (ClassCastException e) {
            return false;
        }
    }

    public String toString() {
        return "Person-->Age:" + this.getAge().getClass().getName() + " " + this.getAge().toString() + " fname:" + this.getFirstName() + " lname:" + this.getLastName() + " anniversary:" + this.getAnniversaryDate();
    }

    /*
        public Object getPostalCode() {
            return postalCode;
        }

        public Object setPostalCode(Object newPostalCode) {
            postalCode = newPostalCode;
        }*/
}
