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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.jpa.deadlock.diagnostic;

import org.eclipse.persistence.tools.schemaframework.FieldDefinition;
import org.eclipse.persistence.tools.schemaframework.ForeignKeyConstraint;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

public class DeadLockDiagnosticTableCreator extends org.eclipse.persistence.tools.schemaframework.TableCreator {

    public DeadLockDiagnosticTableCreator() {
        setName("DeadLockDiagnostic");

        addTableDefinition(buildCACHEDEADLOCK_MASTERTable());
        addTableDefinition(buildCACHEDEADLOCK_DETAILTable());
    }

    public TableDefinition buildCACHEDEADLOCK_MASTERTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CACHEDEADLOCK_MASTER");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMBER");
        fieldID.setSize(0);
        fieldID.setSubSize(0);
        fieldID.setIsPrimaryKey(true);
        fieldID.setIsIdentity(false);
        fieldID.setUnique(false);
        fieldID.setShouldAllowNull(false);
        table.addField(fieldID);

        FieldDefinition fieldNAME = new FieldDefinition();
        fieldNAME.setName("NAME");
        fieldNAME.setTypeName("VARCHAR");
        fieldNAME.setSize(400);
        fieldNAME.setShouldAllowNull(true);
        fieldNAME.setIsPrimaryKey(false);
        fieldNAME.setUnique(false);
        fieldNAME.setIsIdentity(false);
        table.addField(fieldNAME);

        return table;
    }

    public TableDefinition buildCACHEDEADLOCK_DETAILTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CACHEDEADLOCK_DETAIL");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMBER");
        fieldID.setSize(0);
        fieldID.setSubSize(0);
        fieldID.setIsPrimaryKey(true);
        fieldID.setIsIdentity(false);
        fieldID.setUnique(false);
        fieldID.setShouldAllowNull(false);
        table.addField(fieldID);

        FieldDefinition fieldNAME = new FieldDefinition();
        fieldNAME.setName("NAME");
        fieldNAME.setTypeName("VARCHAR");
        fieldNAME.setSize(400);
        fieldNAME.setShouldAllowNull(true);
        fieldNAME.setIsPrimaryKey(false);
        fieldNAME.setUnique(false);
        fieldNAME.setIsIdentity(false);
        table.addField(fieldNAME);

        FieldDefinition fieldCACHEDEADLOCK_MASTER_FK = new FieldDefinition();
        fieldCACHEDEADLOCK_MASTER_FK.setName("CACHEDEADLOCK_MASTER_FK");
        fieldCACHEDEADLOCK_MASTER_FK.setTypeName("NUMBER");
        fieldCACHEDEADLOCK_MASTER_FK.setSize(0);
        fieldCACHEDEADLOCK_MASTER_FK.setSubSize(0);
        fieldCACHEDEADLOCK_MASTER_FK.setIsPrimaryKey(false);
        fieldCACHEDEADLOCK_MASTER_FK.setIsIdentity(false);
        fieldCACHEDEADLOCK_MASTER_FK.setUnique(false);
        fieldCACHEDEADLOCK_MASTER_FK.setShouldAllowNull(true);
        table.addField(fieldCACHEDEADLOCK_MASTER_FK);

        ForeignKeyConstraint foreignKeyM_CACHEDEADLOCK_MASTER = new ForeignKeyConstraint();
        foreignKeyM_CACHEDEADLOCK_MASTER.setName("M_CACHEDEADLOCK_MASTER_FK");
        foreignKeyM_CACHEDEADLOCK_MASTER.setTargetTable("CACHEDEADLOCK_MASTER");
        foreignKeyM_CACHEDEADLOCK_MASTER.addSourceField("CACHEDEADLOCK_MASTER_FK");
        foreignKeyM_CACHEDEADLOCK_MASTER.addTargetField("ID");
        table.addForeignKeyConstraint(foreignKeyM_CACHEDEADLOCK_MASTER);

        return table;
    }
}
