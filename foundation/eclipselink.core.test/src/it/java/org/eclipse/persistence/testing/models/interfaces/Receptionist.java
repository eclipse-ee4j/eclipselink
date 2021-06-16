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

public class Receptionist extends AdministrativeJob {
    public Integer wordsPerMinute;

    public static Receptionist example1() {
        Receptionist example = new Receptionist();

        example.setWordsPerMinute(new Integer(50));
        example.setSalary(new Float(22500.00));
        example.setMinimumSalary(new Float(22000.00));

        return example;
    }

    public static Receptionist example2() {
        Receptionist example = new Receptionist();

        example.setWordsPerMinute(new Integer(50));
        example.setSalary(new Float(23450.00));
        example.setMinimumSalary(new Float(22000.00));

        return example;
    }

    public static Receptionist example3() {
        Receptionist example = new Receptionist();

        example.setWordsPerMinute(new Integer(60));
        example.setSalary(new Float(26450.00));
        example.setMinimumSalary(new Float(22000.00));

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
