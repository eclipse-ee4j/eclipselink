/*
 * Copyright (c) 1998, 2019 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.forceupdate;

import java.io.*;

import org.eclipse.persistence.indirection.*;

/**
 * <p><b>Purpose</b>: Describes an Employee's phone number.
 * <p><b>Description</b>: Used in a 1:M relationship from an employee. Since many people have various numbers
 *                                they can be contacted at the type describes where the phone number could reach the
 *                                Employee.
 */
public class PhoneNumberVLIO implements Serializable {
    /** Holds values such as Home, Work, Cellular, Pager, Fax, etc.  Since the combination of the Employee's ID and
    the type field are what makes the entry in the database unique the type fields must be unique within an
    Employee's Vector of PhoneNumbers.*/
    public String type;
    public String areaCode;

    /** 7 digit number with no hyphen, this is added during toString() only*/
    public String number;

    /** Owner maintains the 1:1 mapping to an Employee (required for 1:M relationship in Employee) */
    public ValueHolderInterface<EmployeeVLIO> owner;

    public PhoneNumberVLIO() {
        this("home", "###", "#######");
    }

    public PhoneNumberVLIO(String type, String theAreaCode, String theNumber) {
        this.type = type;
        this.areaCode = theAreaCode;
        this.number = theNumber;
        this.owner = new ValueHolder<>();
    }

    public String getAreaCode() {
        return areaCode;
    }

    public String getNumber() {
        return number;
    }

    public org.eclipse.persistence.testing.models.forceupdate.EmployeeVLIO getOwner() {
        return owner.getValue();
    }

    public String getType() {
        return type;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setOwner(org.eclipse.persistence.testing.models.forceupdate.EmployeeVLIO owner) {
        this.owner.setValue(owner);
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * Print the phone.
     * Example: Phone[Work]: (613) 225-8812
     */
    public String toString() {
        StringWriter writer = new StringWriter();

        writer.write("PhoneNumber [");
        writer.write(getType());
        writer.write("]: (");
        writer.write(this.getAreaCode());
        writer.write(")");
        writer.write(this.getNumber().substring(0, 3));
        writer.write("-");
        writer.write(this.getNumber().substring(3, 7));
        return writer.toString();
    }
}
