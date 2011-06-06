/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
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
