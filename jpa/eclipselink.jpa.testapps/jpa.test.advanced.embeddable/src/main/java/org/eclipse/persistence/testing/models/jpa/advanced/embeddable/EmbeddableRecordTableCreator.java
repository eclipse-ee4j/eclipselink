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
//     Oracle - Initial API and implementation.
package org.eclipse.persistence.testing.models.jpa.advanced.embeddable;

import org.eclipse.persistence.testing.framework.TogglingFastTableCreator;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

public class EmbeddableRecordTableCreator extends TogglingFastTableCreator {

    public EmbeddableRecordTableCreator() {
        setName("EmbeddableProject");
        addTableDefinition(buildCmp3EmbedRecVisitorTable());
    }

    public static TableDefinition buildCmp3EmbedRecVisitorTable() {
        TableDefinition table = createTable("CMP3_EMBED_REC_VISITOR");
        table.addField(createNumericPk("ID_INT"));
        table.addField(createStringColumn("ID_STRING"));
        table.addField(createStringColumn("NAME1"));
        table.addField(createStringColumn("NAME2"));
        table.addField(createStringColumn("NAME3"));
        table.addField(createStringColumn("NAME4"));
        return table;
    }
}
