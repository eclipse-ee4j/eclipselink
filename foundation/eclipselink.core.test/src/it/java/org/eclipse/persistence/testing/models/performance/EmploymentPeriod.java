/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.models.performance;

import java.io.Serializable;
import java.io.StringWriter;
import java.sql.Date;

/**
 * <p><b>Purpose</b>: Defines the period an Employee worked for the organization
 * <p><b>Description</b>: The period holds the start date and optionally the end date if the employee has left (null otherwise).
 * Maintained in an aggregate relationship of Employee
 * @see Employee
 */
public class EmploymentPeriod implements Serializable {
    protected Date startDate;
    protected Date endDate;

    public EmploymentPeriod() {
    }

    /**
     * Return a new employment period instance.
     * The constructor's purpose is to allow only valid instances of a class to be created.
     * Valid means that the get/set and clone/toString methods should work on the instance.
     * Arguments to constructors should be avoided unless those arguments are required to put
     * the instance into a valid state, or represent the entire instance definition.
     */
    public EmploymentPeriod(Date startDate, Date endDate) {
        this.startDate = startDate;
        this.endDate = endDate;

    }

    public Date getEndDate() {
        return endDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * Print the start &amp; end date
     */
    public String toString() {
        StringWriter writer = new StringWriter();

        writer.write("EmploymentPeriod: ");
        if (getStartDate() != null) {
            writer.write(getStartDate().toString());
        }
        writer.write("-");
        if (getEndDate() != null) {
            writer.write(getEndDate().toString());
        }
        return writer.toString();
    }
}
