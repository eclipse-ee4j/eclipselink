/*
 * Copyright (c) 1998, 2023 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.models.interfaces;

import org.eclipse.persistence.tools.schemaframework.*;

public class Receptionist extends AdministrativeJob {
    public Integer wordsPerMinute;

    public static Receptionist example1() {
        Receptionist example = new Receptionist();

        example.setWordsPerMinute(50);
        example.setSalary(22500.00f);
        example.setMinimumSalary(22000.00f);

        return example;
    }

    public static Receptionist example2() {
        Receptionist example = new Receptionist();

        example.setWordsPerMinute(50);
        example.setSalary(23450.00f);
        example.setMinimumSalary(22000.00f);

        return example;
    }

    public static Receptionist example3() {
        Receptionist example = new Receptionist();

        example.setWordsPerMinute(Integer.valueOf(60));
        example.setSalary(26450.00f);
        example.setMinimumSalary(22000.00f);

        return example;
    }

    public Integer getWordsPerMinute() {
        return wordsPerMinute;
    }

    public static TableDefinition receptionistTable() {
        TableDefinition table = new TableDefinition();

        table.setName("RECEP");
        table.addField("CODE", java.math.BigDecimal.class, 15);
        table.addField("SALARY", Float.class);
        table.addField("WPM", Integer.class);

        return table;
    }

    public void setWordsPerMinute(Integer wordsPerMinute) {
        this.wordsPerMinute = wordsPerMinute;
    }

    public String toString() {
        return new String("Receptionist: " + getJobCode());
    }
}
