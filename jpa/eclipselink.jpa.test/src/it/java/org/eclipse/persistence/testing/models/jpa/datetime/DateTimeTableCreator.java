/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     07/07/2014-2.5.3 Rick Curtis
//       - 375101: Date and Calendar should not require @Temporal.
package org.eclipse.persistence.testing.models.jpa.datetime;

import org.eclipse.persistence.testing.framework.TogglingFastTableCreator;
import org.eclipse.persistence.tools.schemaframework.FieldDefinition;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

public class DateTimeTableCreator extends TogglingFastTableCreator {

    public DateTimeTableCreator() {
        setName("EJB3DateTimeProject");

        addTableDefinition(buildDateTimeTable());
        addTableDefinition(buildDateTimeSelfTable());
    }

    public static TableDefinition buildDateTimeTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_DATE_TIME");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("DT_ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setSubSize(0);
        fieldID.setIsPrimaryKey(true);
        fieldID.setIsIdentity(true);
        fieldID.setUnique(false);
        fieldID.setShouldAllowNull(false);
        table.addField(fieldID);

        FieldDefinition fieldSTREET = new FieldDefinition();
        fieldSTREET.setName("SQL_DATE");
        fieldSTREET.setTypeName("DATE");
        fieldSTREET.setIsPrimaryKey(false);
        fieldSTREET.setIsIdentity(false);
        fieldSTREET.setUnique(false);
        fieldSTREET.setShouldAllowNull(true);
        table.addField(fieldSTREET);

        FieldDefinition fieldCITY = new FieldDefinition();
        fieldCITY.setName("SQL_TIME");
        fieldCITY.setTypeName("TIME");
        fieldCITY.setSize(6);
        fieldCITY.setIsPrimaryKey(false);
        fieldCITY.setIsIdentity(false);
        fieldCITY.setUnique(false);
        fieldCITY.setShouldAllowNull(true);
        table.addField(fieldCITY);

        FieldDefinition fieldPROVINCE = new FieldDefinition();
        fieldPROVINCE.setName("SQL_TS");
        fieldPROVINCE.setTypeName("TIMESTAMP");
        fieldPROVINCE.setSize(6);
        fieldPROVINCE.setIsPrimaryKey(false);
        fieldPROVINCE.setIsIdentity(false);
        fieldPROVINCE.setUnique(false);
        fieldPROVINCE.setShouldAllowNull(true);
        table.addField(fieldPROVINCE);

        FieldDefinition fieldPOSTALCODE = new FieldDefinition();
        fieldPOSTALCODE.setName("UTIL_DATE");
        fieldPOSTALCODE.setTypeName("TIMESTAMP");
        fieldPOSTALCODE.setSize(6);
        fieldPOSTALCODE.setIsPrimaryKey(false);
        fieldPOSTALCODE.setIsIdentity(false);
        fieldPOSTALCODE.setUnique(false);
        fieldPOSTALCODE.setShouldAllowNull(true);
        table.addField(fieldPOSTALCODE);

        FieldDefinition fieldLocalDate = new FieldDefinition();
        fieldLocalDate.setName("LOCAL_DATE");
        fieldLocalDate.setTypeName("DATE");
        fieldLocalDate.setSize(6);
        fieldLocalDate.setIsPrimaryKey(false);
        fieldLocalDate.setIsIdentity(false);
        fieldLocalDate.setUnique(false);
        fieldLocalDate.setShouldAllowNull(true);
        table.addField(fieldLocalDate);

        FieldDefinition fieldLocalTime = new FieldDefinition();
        fieldLocalTime.setName("LOCAL_TIME");
        fieldLocalTime.setTypeName("TIME");
        fieldLocalTime.setSize(6);
        fieldLocalTime.setIsPrimaryKey(false);
        fieldLocalTime.setIsIdentity(false);
        fieldLocalTime.setUnique(false);
        fieldLocalTime.setShouldAllowNull(true);
        table.addField(fieldLocalTime);

        FieldDefinition fieldLocalDateTime = new FieldDefinition();
        fieldLocalDateTime.setName("LOCAL_DATE_TIME");
        fieldLocalDateTime.setTypeName("TIMESTAMP");
        fieldLocalDateTime.setSize(6);
        fieldLocalDateTime.setIsPrimaryKey(false);
        fieldLocalDateTime.setIsIdentity(false);
        fieldLocalDateTime.setUnique(false);
        fieldLocalDateTime.setShouldAllowNull(true);
        table.addField(fieldLocalDateTime);

        FieldDefinition fieldOffsetTime = new FieldDefinition();
        fieldOffsetTime.setName("OFFSET_TIME");
        fieldOffsetTime.setTypeName("TIME");
        fieldOffsetTime.setSize(6);
        fieldOffsetTime.setIsPrimaryKey(false);
        fieldOffsetTime.setIsIdentity(false);
        fieldOffsetTime.setUnique(false);
        fieldOffsetTime.setShouldAllowNull(true);
        table.addField(fieldOffsetTime);

        FieldDefinition fieldOffsetDateTime = new FieldDefinition();
        fieldOffsetDateTime.setName("OFFSET_DATE_TIME");
        fieldOffsetDateTime.setTypeName("TIMESTAMP");
        fieldOffsetDateTime.setSize(6);
        fieldOffsetDateTime.setIsPrimaryKey(false);
        fieldOffsetDateTime.setIsIdentity(false);
        fieldOffsetDateTime.setUnique(false);
        fieldOffsetDateTime.setShouldAllowNull(true);
        table.addField(fieldOffsetDateTime);

        FieldDefinition fieldCalToCal = new FieldDefinition();
        fieldCalToCal.setName("CAL");
        fieldCalToCal.setTypeName("TIMESTAMP");
        fieldCalToCal.setSize(6);
        fieldCalToCal.setIsPrimaryKey(false);
        fieldCalToCal.setIsIdentity(false);
        fieldCalToCal.setUnique(false);
        fieldCalToCal.setShouldAllowNull(true);
        table.addField(fieldCalToCal);

        return table;
    }

    public static TableDefinition buildDateTimeSelfTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_DATE_TIME_CMP3_DATE_TIME");

        FieldDefinition field0 = new FieldDefinition();
        field0.setName("DateTime_DT_ID");
        field0.setTypeName("NUMERIC");
        field0.setSize(15);
        field0.setShouldAllowNull(false);
        field0.setIsPrimaryKey(false);
        field0.setUnique(false);
        field0.setIsIdentity(false);
        table.addField(field0);

        FieldDefinition field1 = new FieldDefinition();
        field1.setName("UNISELFMAP_DT_ID");
        field1.setTypeName("NUMERIC");
        field1.setSize(15);
        field1.setShouldAllowNull(false);
        field1.setIsPrimaryKey(false);
        field1.setUnique(false);
        field1.setIsIdentity(false);
        table.addField(field1);

        FieldDefinition field2 = new FieldDefinition();
        field2.setName("UNISELFMAP_KEY");
        field2.setTypeName("TIMESTAMP");
        field2.setShouldAllowNull(true);
        field2.setIsPrimaryKey(false);
        field2.setUnique(false);
        field2.setIsIdentity(false);
        table.addField(field2);

        return table;
    }

}
