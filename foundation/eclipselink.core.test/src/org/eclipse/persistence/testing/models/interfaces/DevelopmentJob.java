/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.interfaces;

import org.eclipse.persistence.tools.schemaframework.*;

public abstract class DevelopmentJob implements Job, java.io.Serializable {
    public Float salary;
    public Number jobCode;

    public static TableDefinition developmentJobTable() {
        TableDefinition table = new TableDefinition();

        table.setName("DEV_JOB");
        table.addField("CODE", java.math.BigDecimal.class, 15);
        table.addField("TYPE", Character.class);
        table.addField("SALARY", Float.class);

        return table;
    }

    public Number getJobCode() {
        return jobCode;
    }

    public Float getSalary() {
        return salary;
    }

    public void setJobCode(Number jobCode) {
        this.jobCode = jobCode;
    }

    public void setSalary(Float salary) {
        this.salary = salary;
    }
}
