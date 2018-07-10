/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced;

import java.sql.Date;
import java.io.*;
import javax.persistence.*;

/**
 * <p><b>Purpose</b>: Defines the period an Employee worked for the organization
 *    <p><b>Description</b>: The period holds the start date and optionally the
 *    end date if the employee has left (null otherwise). Maintained in an
 *    aggregate relationship of Employee
 *    @see Employee
 */
@Embeddable
@Table(name="CMP3_FA_EMPLOYEE")
public class EmploymentPeriod implements Serializable {
    @Column(name="S_DATE")
    private Date startDate;
    @Column(name="E_DATE")
    private Date endDate;

    public EmploymentPeriod() {}

    /**
     * Return a new employment period instance.
     * The constructor's purpose is to allow only valid instances of a class to
     * be created. Valid means that the get/set and clone/toString methods
     * should work on the instance. Arguments to constructors should be avoided
     * unless those arguments are required to put the instance into a valid
     * state, or represent the entire instance definition.
     */
    public EmploymentPeriod(Date theStartDate, Date theEndDate) {
        startDate = theStartDate;
        endDate = theEndDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date date) {
        this.startDate = date;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date date) {
        this.endDate = date;
    }

    /**
     * Print the start & end date
     */
    public String toString() {
        java.io.StringWriter writer = new java.io.StringWriter();
        writer.write("EmploymentPeriod: ");

        if (this.getStartDate() != null) {
            writer.write(this.getStartDate().toString());
        }

        writer.write("-");

        if (this.getEndDate() != null) {
            writer.write(this.getEndDate().toString());
        }

        return writer.toString();
    }

    public boolean equals(Object o) {
        if ((o == null) || (!(o instanceof EmploymentPeriod))) {
            return false;
        }
        EmploymentPeriod empPeriod = (EmploymentPeriod)o;
        if (startDate!=null){
            if (!startDate.equals(empPeriod.getStartDate())){
                return false;
            }
        } else if (empPeriod.getStartDate()!=null){
            return false;
        }

        if (endDate!=null){
            if (!endDate.equals(empPeriod.getEndDate())){
                return false;
            }
        } else if (empPeriod.getEndDate()!=null){
            return false;
        }

        return true;
    }
}
