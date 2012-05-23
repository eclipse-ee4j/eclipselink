/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.mappings.directcollection.union;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Person {
    private String firstName;
    private String lastName;
    private ArrayList items;

    public Person() {
        items = new ArrayList();
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

    public ArrayList getItems() {
        return items;
    }

    public void setItems(ArrayList newItems) {
        items = newItems;
    }

    public void addItem(Object newItem) {
        items.add(newItem);
    }

    public boolean equals(Object object) {
        try {
            Person person = (Person)object;

            if (!this.getFirstName().equals(person.getFirstName())) {
                return false;
            }
            if (!this.getLastName().equals(person.getLastName())) {
                return false;
            }
            if (this.getItems().size() != person.getItems().size()) {
                return false;
            }
            for (int i = 0; i < this.getItems().size(); i++) {
                Object nextDate = this.getItems().get(i);
                Object otherDate = person.getItems().get(i);
                if (!(nextDate.equals(otherDate))) {
                    return false;
                }
            }

            return true;
        } catch (ClassCastException e) {
            return false;
        }
    }

    public String toString() {
        String returnString = "fname:" + this.getFirstName() + " lname:" + this.getLastName();
        for (int i = 0; i < this.getItems().size(); i++) {
            Object nextDate = this.getItems().get(i);
            returnString += (" nextDate: " + nextDate);
            returnString += (" nextDateClass: " + nextDate.getClass());
        }
        return returnString;
    }
}
