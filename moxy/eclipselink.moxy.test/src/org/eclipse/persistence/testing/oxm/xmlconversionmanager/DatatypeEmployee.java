/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.persistence.testing.oxm.xmlconversionmanager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

import org.eclipse.persistence.internal.oxm.XMLConversionManager;

public class DatatypeEmployee {

    public String name;
    public int deptNumber;
    public Calendar birthDate;
    public XMLGregorianCalendar hireDate;
    public Duration vacationTaken;

    public boolean equals(Object anObject) {
        if (anObject == null) {
            return false;
        }
        if (!(anObject instanceof DatatypeEmployee)) {
            return false;
        }

        DatatypeEmployee test = (DatatypeEmployee) anObject;
        if (!test.name.equals(this.name)) {
            System.err.println("name NOT EQUAL");
            return false;
        }
        if (test.deptNumber != this.deptNumber) {
            System.err.println("deptNumber NOT EQUAL");
            return false;
        }
        if (!(test.birthDate.getTimeInMillis() == this.birthDate.getTimeInMillis())) {
            System.out.println("TEST:");
            System.out.println(test.birthDate);
            System.out.println("\nCTRL:");
            System.out.println(this.birthDate);
            return false;
        }
        if (!test.hireDate.equals(this.hireDate)) {
            System.err.println("hireDate NOT EQUAL");
            return false;
        }
        if (!test.vacationTaken.equals(this.vacationTaken)) {
            System.err.println("vacationTaken NOT EQUAL");
            return false;
        }

        return true;
    }

    public String toString() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss Z");

        String toString = "\nDatatypeEmployee@" + Integer.toHexString(hashCode());
        toString += "\n   name:          " + name;
        toString += "\n   deptNumber:    " + deptNumber;
        toString += "\n   birthDate:     " + birthDate; //format.format(birthDate.getTime());
        toString += "\n   hireDate:      " + hireDate; //format.format(hireDate.toGregorianCalendar().getTime());
        toString += "\n   vacationTaken: " + vacationTaken.toString() + "\n";

        return toString;
    }

}
