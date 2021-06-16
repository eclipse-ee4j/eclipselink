/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.jpa.composite.advanced.member_2;

import java.io.*;
import jakarta.persistence.*;

/**
 * <p><b>Purpose</b>: Defines the former employment of an Employee.
 *    <p><b>Description</b>: The former employment holds the name of the
 *    former company and an EmploymentPeriod. Maintained in an
 *    aggregate relationship of Employee
 *    @see Employee
 */
@Embeddable
public class FormerEmployment implements Serializable {
    private String company;
    private EmploymentPeriod period;

    public FormerEmployment() {}

    public FormerEmployment(String company, EmploymentPeriod period) {
        this.company = company;
        this.period = period;
    }

    @Basic
    public String getFormerCompany() {
        return company;
    }

    public void setFormerCompany(String company) {
        this.company = company;
    }

    @Embedded
    public EmploymentPeriod getPeriod() {
        return period;
    }

    public void setPeriod(EmploymentPeriod period) {
        this.period = period;
    }


    /**
     * Print the company and the period
     */
    public String toString() {
        java.io.StringWriter writer = new java.io.StringWriter();
        writer.write("FormerEmployment: ");

        if (this.getFormerCompany() != null) {
            writer.write(this.getFormerCompany().toString());
        }

        writer.write(",");

        if (this.getPeriod() != null) {
            writer.write(this.getPeriod().toString());
        }

        return writer.toString();
    }
}
