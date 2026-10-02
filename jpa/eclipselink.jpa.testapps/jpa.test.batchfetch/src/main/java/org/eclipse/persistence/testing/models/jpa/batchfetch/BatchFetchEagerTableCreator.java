/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation. All rights reserved.
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
//     09/27/2026 - Andreas Lemmer
//       - 2885: eager batch fetching in both directions must not overflow the stack
package org.eclipse.persistence.testing.models.jpa.batchfetch;

import org.eclipse.persistence.tools.schemaframework.FieldDefinition;
import org.eclipse.persistence.tools.schemaframework.TableCreator;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

/**
 * Parent and child tables of the eager ({@link EagerParent}), lazy ({@link LazyParent})
 * and eagerly woven ({@link WovenEagerParent}) models.
 */
public class BatchFetchEagerTableCreator extends TableCreator {
    public BatchFetchEagerTableCreator() {
        setName("BatchFetchEagerProject");

        for (String kind : new String[] {"EAGER", "LAZY", "WOVEN"}) {
            addTableDefinition(buildParentTable(kind));
            addTableDefinition(buildChildTable(kind));
        }
    }

    public TableDefinition buildParentTable(String kind) {
        TableDefinition table = new TableDefinition();
        table.setName("BATCH_IN_" + kind + "_PARENT");
        table.addField(buildIdField());
        return table;
    }

    public TableDefinition buildChildTable(String kind) {
        TableDefinition table = new TableDefinition();
        table.setName("BATCH_IN_" + kind + "_CHILD");
        table.addField(buildIdField());

        FieldDefinition fieldParent = new FieldDefinition();
        fieldParent.setName("PARENT_ID");
        fieldParent.setTypeName("NUMBER");
        fieldParent.setSize(19);
        fieldParent.setSubSize(0);
        fieldParent.setShouldAllowNull(false);
        fieldParent.setForeignKeyFieldName("BATCH_IN_" + kind + "_PARENT.ID");
        table.addField(fieldParent);

        return table;
    }

    private static FieldDefinition buildIdField() {
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMBER");
        fieldID.setSize(19);
        fieldID.setSubSize(0);
        fieldID.setIsPrimaryKey(true);
        fieldID.setShouldAllowNull(false);
        return fieldID;
    }
}
