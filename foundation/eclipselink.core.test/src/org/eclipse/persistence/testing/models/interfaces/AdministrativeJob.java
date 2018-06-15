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
package org.eclipse.persistence.testing.models.interfaces;

import org.eclipse.persistence.tools.schemaframework.*;

public abstract class AdministrativeJob implements Job, Unionized {
    public Float salary;
    public Float minimumSalary;
    public Number jobCode;

    public static TableDefinition administrativeJobTable() {
        TableDefinition table = new TableDefinition();

        table.setName("ADM_JOB");
        table.addField("CODE", java.math.BigDecimal.class, 15);
        table.addField("MIN_SAL", Float.class);
        table.addField("TYPE", Character.class);

        return table;
    }

    public Number getJobCode() {
        return jobCode;
    }

    public Float getMinimumSalary() {
        return minimumSalary;
    }

    public Float getSalary() {
        return salary;
    }

    public void setJobCode(Number jobCode) {
        this.jobCode = jobCode;
    }

    public void setMinimumSalary(Float minimumSalary) {
        this.minimumSalary = minimumSalary;
    }

    public void setSalary(Float salary) {
        this.salary = salary;
    }
}
