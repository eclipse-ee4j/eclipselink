/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.models.jpa.diagnostic;

import org.eclipse.persistence.tools.schemaframework.FieldDefinition;
import org.eclipse.persistence.tools.schemaframework.ForeignKeyConstraint;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

public class DiagnosticTableCreator extends org.eclipse.persistence.tools.schemaframework.TableCreator {

    public DiagnosticTableCreator() {
        setName("Diagnostic");

        addTableDefinition(buildBRANCHA_CDTable());
        addTableDefinition(buildBRANCHB_CDTable());
    }

    public TableDefinition buildBRANCHA_CDTable() {
        TableDefinition table = new TableDefinition();
        table.setName("BRANCHA_DIAGNOSTIC");

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

        return table;
    }

    public TableDefinition buildBRANCHB_CDTable() {
        TableDefinition table = new TableDefinition();
        table.setName("BRANCHB_DIAGNOSTIC");

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

        FieldDefinition fieldBRANCHA = new FieldDefinition();
        fieldBRANCHA.setName("BRANCHA_FK");
        fieldBRANCHA.setTypeName("NUMBER");
        fieldBRANCHA.setSize(0);
        fieldBRANCHA.setSubSize(0);
        fieldBRANCHA.setIsPrimaryKey(false);
        fieldBRANCHA.setIsIdentity(false);
        fieldBRANCHA.setUnique(false);
        fieldBRANCHA.setShouldAllowNull(true);
        table.addField(fieldBRANCHA);

        ForeignKeyConstraint foreignKeyM_BRANCHA_FK = new ForeignKeyConstraint();
        foreignKeyM_BRANCHA_FK.setName("M_BRANCHA_FK");
        foreignKeyM_BRANCHA_FK.setTargetTable("BRANCHA_DIAGNOSTIC");
        foreignKeyM_BRANCHA_FK.addSourceField("BRANCHA_FK");
        foreignKeyM_BRANCHA_FK.addTargetField("ID");
        table.addForeignKeyConstraint(foreignKeyM_BRANCHA_FK);

        return table;
    }
}
