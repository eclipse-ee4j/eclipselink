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
package org.eclipse.persistence.testing.jaxb.xmlschematype;

import java.util.Calendar;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;

@XmlRootElement(name="employee")
public class EmployeeTwoDates {

    @XmlElement(name="start-date")
    @XmlSchemaType(name = "date")
    public java.util.Calendar startDate;

    @XmlElement(name="start-time")
    public Calendar startTime;

    public boolean equals(Object o) {
        if(!(o instanceof EmployeeTwoDates) || o == null) {
            return false;
        } else {
            return ((EmployeeTwoDates)o).startDate.getTimeInMillis() == this.startDate.getTimeInMillis() && ((EmployeeTwoDates)o).startTime.getTimeInMillis() == startTime.getTimeInMillis();
        }
    }

    public String toString() {
        return "EMPLOYEE(" + startDate + " - " + startTime + ")";
    }
}
