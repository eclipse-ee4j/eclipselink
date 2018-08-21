/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     tware - added for  Bug 324406 - Wrong Index in ReportItem when @Embeddable Objects are used in ReportQuery
package org.eclipse.persistence.testing.models.jpa.composite.advanced.member_2;

/**
 * Used by constructorQuery tests to allow use of a constructor query with parts of Employee
 * @author tware
 *
 */
public class EmployeeHolder {

    protected Integer id;
    protected EmploymentPeriod period;
    protected String name;

    public EmployeeHolder(Integer id, EmploymentPeriod period, String name){
        this.id = id;
        this.period = period;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public EmploymentPeriod getPeriod() {
        return period;
    }

    public void setPeriod(EmploymentPeriod period) {
        this.period = period;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
