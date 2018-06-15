/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     01/23/2013-2.5 Guy Pelletier
//       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
package org.eclipse.persistence.testing.models.jpa22.advanced.xml;

import java.io.Serializable;
import java.sql.Date;

public class EmploymentPeriod implements Serializable, Cloneable {
    private Date startDate;
    private Date endDate;

    public EmploymentPeriod() {}

    public EmploymentPeriod(Date theStartDate, Date theEndDate) {
        startDate = theStartDate;
        endDate = theEndDate;
    }

    public boolean equals(Object o) {
        if ((o == null) || (!(o instanceof EmploymentPeriod))) {
            return false;
        }

        EmploymentPeriod empPeriod = (EmploymentPeriod) o;

        if (startDate != null){
            if (!startDate.equals(empPeriod.getStartDate())) {
                return false;
            }
        } else if (empPeriod.getStartDate()!=null){
            return false;
        }

        if (endDate != null) {
            if (!endDate.equals(empPeriod.getEndDate())) {
                return false;
            }
        } else if (empPeriod.getEndDate()!=null){
            return false;
        }

        return true;
    }

    public Date getEndDate() {
        return endDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setEndDate(Date date) {
        this.endDate = date;
    }

    public void setStartDate(Date date) {
        this.startDate = date;
    }

    public String toString() {
        java.io.StringWriter writer = new java.io.StringWriter();
        writer.write("EmploymentPeriod: ");

        if (getStartDate() != null) {
            writer.write(this.getStartDate().toString());
        }

        writer.write("-");

        if (getEndDate() != null) {
            writer.write(this.getEndDate().toString());
        }

        return writer.toString();
    }
}
