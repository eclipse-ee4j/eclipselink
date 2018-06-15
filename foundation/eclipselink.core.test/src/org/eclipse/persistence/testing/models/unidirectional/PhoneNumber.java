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
package org.eclipse.persistence.testing.models.unidirectional;

import java.io.*;

/**
 *
 */
public class PhoneNumber implements Serializable {
    /* Combination of areaCode and number is a PrimaryKey*/
    /** 3 digit number*/
    public String areaCode;
    /** 7 digit number with no hyphen, this is added during toString() only*/
    public String number;

    /** Holds values such as Home, Work, Cellular, Pager, Fax, etc.*/
    public String type;

    public PhoneNumber() {
        this("home", "###", "#######");
    }

    public PhoneNumber(String type, String theAreaCode, String theNumber) {
        this.type = type;
        this.areaCode = theAreaCode;
        this.number = theNumber;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public String getNumber() {
        return number;
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
        writer.write(this.getNumber().substring(0, Math.min(3, this.getNumber().length())));
        writer.write("-");
        writer.write(this.getNumber().substring(Math.min(3, this.getNumber().length()), Math.min(7, this.getNumber().length())));
        return writer.toString();
    }
}
