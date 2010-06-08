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
package org.eclipse.persistence.testing.tests.queries.options;

import java.math.BigDecimal;
import java.io.*;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

public class QueryOptionEmployee implements Serializable {
    public BigDecimal id;
    public String name;
    public QueryOptionHistory history;

    public QueryOptionEmployee() {
        this(null, null);
    }
    
    public QueryOptionEmployee(BigDecimal id, String name) {
        super();
        setId(id);
        setName(name);
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

    public QueryOptionHistory getHistory() {
        return history;
    }

    public void setHistory(QueryOptionHistory newHistory) {
        history = newHistory;
    }

    /**
     * Example 1
     */
    public static QueryOptionEmployee example1() {
        QueryOptionEmployee employee = new QueryOptionEmployee();
        employee.name = "John Hooly";
        employee.history = QueryOptionHistory.example1();
        return employee;
    }

    /**
     * Example 2
     */
    public static QueryOptionEmployee example2() {
        QueryOptionEmployee employee = new QueryOptionEmployee();
        employee.name = "Jan Smith";
        employee.history = QueryOptionHistory.example1();
        return employee;
    }

    /**
     * Return a platform independant definition of the database table.
     */
    public static TableDefinition tableDefinition() {
        TableDefinition definition = new TableDefinition();

        definition.setName("QUERY_OPTION_EMPLOYEE");

        definition.addIdentityField("ID", java.math.BigDecimal.class, 15);
        definition.addField("HISTORY_ID", java.math.BigDecimal.class, 15);
        definition.addField("NAME", String.class, 40);

        return definition;
    }
}
