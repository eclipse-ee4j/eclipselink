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
package org.eclipse.persistence.testing.tests.queries.options;

import java.math.BigDecimal;
import java.util.Calendar;
import java.io.*;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

public class QueryOptionHistory implements Serializable {
    public BigDecimal id;

  public Calendar startDate;

    public QueryOptionHistory() {
    }

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal newId) {
        id = newId;
    }

  public Calendar getStartDate() {
        return startDate;
    }

  public void setStartDate(Calendar newStartDate) {
        startDate = newStartDate;
    }

    /**
     * Example1
     */
    public static QueryOptionHistory example1() {
        QueryOptionHistory history = new QueryOptionHistory();
    history.startDate = Calendar.getInstance();
        return history;
    }

    /**
     * Return a platform independant definition of the database table.
     */
    public static TableDefinition tableDefinition() {
        TableDefinition definition = new TableDefinition();

        definition.setName("QUERY_OPTION_HISTORY");

        definition.addIdentityField("ID", java.math.BigDecimal.class, 15);
        definition.addField("STARTDATE", java.sql.Timestamp.class);
        //      definition.addField("NAME", String.class, 40);
        return definition;
    }
}
