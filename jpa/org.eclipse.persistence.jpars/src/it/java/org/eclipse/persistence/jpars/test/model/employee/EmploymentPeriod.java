/*
 * Copyright (c) 2011, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.persistence.jpars.test.model.employee;

import java.util.Calendar;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Embeddable
public class EmploymentPeriod {

    @Temporal(TemporalType.DATE)
    @SuppressWarnings("deprecation")
    private Calendar startDate;
    @Temporal(TemporalType.DATE)
    @SuppressWarnings("deprecation")
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
