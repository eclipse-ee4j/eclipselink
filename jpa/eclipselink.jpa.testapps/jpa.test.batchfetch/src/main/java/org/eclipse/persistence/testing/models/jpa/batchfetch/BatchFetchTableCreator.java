/*
 * Copyright (c) 2024 Oracle and/or its affiliates. All rights reserved.
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

package org.eclipse.persistence.testing.models.jpa.batchfetch;

import org.eclipse.persistence.tools.schemaframework.FieldDefinition;
import org.eclipse.persistence.tools.schemaframework.TableCreator;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

public class BatchFetchTableCreator extends TableCreator {
    public BatchFetchTableCreator() {
        setName("BatchFetchProject");

        addTableDefinition(buildCompanyTable());
        addTableDefinition(buildEmployeeTable());
        addTableDefinition(buildRecordTable());
    }

    public TableDefinition buildRecordTable() {
        TableDefinition table = new TableDefinition();
        table.setName("BATCH_IN_RECORD");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMBER");
        fieldID.setSize(19);
        fieldID.setSubSize(0);
        fieldID.setIsPrimaryKey(true);
        fieldID.setIsIdentity(true);
        fieldID.setShouldAllowNull(false);
        table.addField(fieldID);

        FieldDefinition fieldUSER = new FieldDefinition();
        fieldUSER.setName("EMPLOYEE_ID");
        fieldUSER.setTypeName("NUMBER");
        fieldUSER.setSize(19);
        fieldUSER.setSubSize(0);
        fieldUSER.setIsPrimaryKey(false);
        fieldUSER.setIsIdentity(false);
        fieldUSER.setShouldAllowNull(false);
        fieldUSER.setForeignKeyFieldName("BATCH_IN_EMPLOYEE.ID");
        table.addField(fieldUSER);

        return table;
    }

    public TableDefinition buildCompanyTable() {
        TableDefinition table = new TableDefinition();
        table.setName("BATCH_IN_COMPANY");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMBER");
        fieldID.setSize(19);
        fieldID.setSubSize(0);
        fieldID.setIsPrimaryKey(true);
        fieldID.setIsIdentity(true);
        fieldID.setShouldAllowNull(false);
        table.addField(fieldID);

        return table;
    }


    public TableDefinition buildEmployeeTable() {
        TableDefinition table = new TableDefinition();
        table.setName("BATCH_IN_EMPLOYEE");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMBER");
        fieldID.setSize(19);
        fieldID.setSubSize(0);
        fieldID.setIsPrimaryKey(true);
        fieldID.setIsIdentity(true);
        fieldID.setShouldAllowNull(false);
        table.addField(fieldID);

        FieldDefinition fieldCompany = new FieldDefinition();
        fieldCompany.setName("COMPANY_ID");
        fieldCompany.setTypeName("NUMBER");
        fieldCompany.setSize(19);
        fieldCompany.setSubSize(0);
        fieldCompany.setIsPrimaryKey(false);
        fieldCompany.setIsIdentity(false);
        fieldCompany.setShouldAllowNull(false);
        fieldCompany.setForeignKeyFieldName("BATCH_IN_COMPANY.ID");
        table.addField(fieldCompany);

        return table;

    }

}
