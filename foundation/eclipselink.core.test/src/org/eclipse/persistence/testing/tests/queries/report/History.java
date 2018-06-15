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
package org.eclipse.persistence.testing.tests.queries.report;

import java.math.BigDecimal;
import java.util.Calendar;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

public class History {
    public BigDecimal id;

    //Changed date type because TimesTen driver cannot convert Timestamp to DATE ({ts '...'} cannot be written to a DATE field)
    public java.sql.Date startDate;

    public History() {
    }

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal newId) {
        id = newId;
    }

    public java.sql.Date getStartDate() {
        return startDate;
    }

    public void setStartDate(java.sql.Date newStartDate) {
        startDate = newStartDate;
    }

    /**
     * Example1
     */
    public static History example1() {
        History history = new History();
        history.startDate = new java.sql.Date(Calendar.getInstance().getTime().getTime());
        return history;
    }

    /**
     * Return a platform independant definition of the database table.
     */
    public static TableDefinition tableDefinition() {
        TableDefinition definition = new TableDefinition();

        definition.setName("REPORT_HISTORY");

        definition.addIdentityField("ID", java.math.BigDecimal.class, 15);
        definition.addField("STARTDATE", java.sql.Date.class);
        definition.addField("NAME", String.class, 40);

        return definition;
    }
}
