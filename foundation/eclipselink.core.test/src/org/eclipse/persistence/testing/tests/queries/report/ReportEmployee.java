/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.tests.queries.report;

import java.math.BigDecimal;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

public class ReportEmployee {
    public BigDecimal id;
    public String name;
    public History history;

    public ReportEmployee() {
    }

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal newId) {
        id = newId;
    }

    public String getName() {
        return name;
    }

    public void setName(String newName) {
        name = newName;
    }

    public History getHistory() {
        return history;
    }

    public void setHistory(History newHistory) {
        history = newHistory;
    }

    /**
     * Example 1
     */
    public static ReportEmployee example1() {
        ReportEmployee employee = new ReportEmployee();
        employee.name = "John Hooly";
        employee.history = History.example1();
        return employee;
    }

    /**
     * Return a platform independant definition of the database table.
     */
    public static TableDefinition tableDefinition() {
        TableDefinition definition = new TableDefinition();

        definition.setName("REPORT_EMPLOYEE");

        definition.addIdentityField("ID", java.math.BigDecimal.class, 15);
        definition.addField("HISTORY_ID", java.math.BigDecimal.class, 15);
        definition.addField("NAME", String.class, 40);

        return definition;
    }
}
