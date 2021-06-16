/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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

package org.eclipse.persistence.jpars.test.model.employee;

import static jakarta.persistence.TemporalType.DATE;

import java.util.Calendar;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Temporal;

@Embeddable
public class EmploymentPeriod {

    @Temporal(DATE)
    private Calendar startDate;
    @Temporal(DATE)
    private Calendar endDate;

    public Calendar getStartDate() {
        return startDate;
    }

    public void setStartDate(Calendar startDate) {
        this.startDate = startDate;
    }

    public void setStartDate(int year, int month, int date) {
        if (this.startDate == null) {
            setStartDate(Calendar.getInstance());
        }
        getStartDate().set(year, month, date);
    }

    public Calendar getEndDate() {
        return endDate;
    }

    public void setEndDate(Calendar endDate) {
        this.endDate = endDate;
    }

    public void setEndDate(int year, int month, int date) {
        if (this.endDate == null) {
            setEndDate(Calendar.getInstance());
        }
        getEndDate().set(year, month, date);
    }
}
