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
