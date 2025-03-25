/*
 * Copyright (c) 2025 Oracle and/or its affiliates. All rights reserved.
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

public class BatchFetchCacheTableCreator extends TableCreator {
    public BatchFetchCacheTableCreator() {
        setName("BatchFetchCacheProject");

        addTableDefinition(buildParentTable());
        addTableDefinition(buildChildTable());
    }

    public TableDefinition buildParentTable() {
        TableDefinition table = new TableDefinition();
        table.setName("BATCH_IN_CACHE_PARENT");

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


    public TableDefinition buildChildTable() {
        TableDefinition table = new TableDefinition();
        table.setName("BATCH_IN_CACHE_CHILD");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMBER");
        fieldID.setSize(19);
        fieldID.setSubSize(0);
        fieldID.setIsPrimaryKey(true);
        fieldID.setIsIdentity(true);
        fieldID.setShouldAllowNull(false);
        table.addField(fieldID);

        FieldDefinition fieldParent = new FieldDefinition();
        fieldParent.setName("PARENT_ID");
        fieldParent.setTypeName("NUMBER");
        fieldParent.setSize(19);
        fieldParent.setSubSize(0);
        fieldParent.setIsPrimaryKey(false);
        fieldParent.setIsIdentity(false);
        fieldParent.setShouldAllowNull(false);
        fieldParent.setForeignKeyFieldName("BATCH_IN_CACHE_PARENT.ID");
        table.addField(fieldParent);

        return table;

    }

}
