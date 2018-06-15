/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     tware - initial contribution for Bug 366748 - JPA 2.1 Injectable Entity Listeners
package org.eclipse.persistence.testing.models.jpa22.entitylistener;

import org.eclipse.persistence.testing.framework.TogglingFastTableCreator;
import org.eclipse.persistence.tools.schemaframework.FieldDefinition;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

public class EntityListenerTableCreator extends TogglingFastTableCreator {

    public EntityListenerTableCreator(){
        setName("EntityListenerTableCreator");
        addTableDefinition(buildEntityListenerHolderTable());
    }

    public TableDefinition buildEntityListenerHolderTable(){
        TableDefinition table = new TableDefinition();
        table.setName("ENT_LIS_HOLD");

        FieldDefinition field = new FieldDefinition();
        field.setName("ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setSubSize(0);
        field.setIsPrimaryKey(true);
        field.setIsIdentity(true);
        field.setUnique(false);
        field.setShouldAllowNull(false);
        table.addField(field);

        return table;
    }
}
