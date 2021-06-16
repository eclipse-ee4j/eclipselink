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
//              dclarke - initial JPA Employee example using XML (bug 217884)
//              mbraeuer - annotated version
package org.eclipse.persistence.testing.models.jpa.performance2;

import java.util.Calendar;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

/**
 * Represents the period of time an employee has worked for the company. A null
 * endDate indicates that the employee is current.
 *
 * @author djclarke
 */
@Embeddable
public class EmploymentPeriod {

    @Temporal(TemporalType.DATE)
    private Calendar startDate;
    @Temporal(TemporalType.DATE)
    private Calendar endDate;

    public Calendar getStartDate() {
        return startDate;
    }

    public void setStartDate(Calendar startDate) {
        this.startDate = startDate;
    }

    public void setStartDate(int year, int month, int day) {
        Calendar date = Calendar.getInstance();
        date.set(year, month + 1, day);
        setStartDate(date);
    }

    public Calendar getEndDate() {
        return endDate;
    }

    public void setEndDate(Calendar endDate) {
        this.endDate = endDate;
    }

    public void setEndDate(int year, int month, int day) {
        Calendar date = Calendar.getInstance();
        date.set(year, month + 1, day);
        setEndDate(date);
    }
}
