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
package org.eclipse.persistence.testing.models.jpa.timestamptz;

import org.eclipse.persistence.tools.schemaframework.*;
import oracle.sql.*;

public class TimestampTableCreator extends TableCreator {
    public TimestampTableCreator() {
        setName("EJB3EmployeeProject");
        addTableDefinition(buildTIMESTAMPTable());
    }

     public static TableDefinition buildTIMESTAMPTable() {
         TableDefinition table = new TableDefinition();
         table.setName("TIME_STAMP");

         FieldDefinition fieldID = new FieldDefinition();
         fieldID.setName("ID");
         fieldID.setTypeName("NUMERIC");
         fieldID.setSize(15);
         fieldID.setSubSize(0);
         fieldID.setIsPrimaryKey(true);
         fieldID.setIsIdentity(true);
         fieldID.setUnique(false);
         fieldID.setShouldAllowNull(false);
         table.addField(fieldID);

         table.addField("NOZONE", TIMESTAMPTZ.class);
         table.addField("TSTZ", TIMESTAMPTZ.class);
         table.addField("TSLTZ", TIMESTAMPLTZ.class);

         return table;
     }

}
