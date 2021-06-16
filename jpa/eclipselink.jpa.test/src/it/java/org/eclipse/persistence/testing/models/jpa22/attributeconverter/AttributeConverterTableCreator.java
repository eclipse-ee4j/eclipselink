/*
 * Copyright (c) 2015, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package org.eclipse.persistence.testing.models.jpa22.attributeconverter;

import org.eclipse.persistence.testing.framework.TogglingFastTableCreator;
import org.eclipse.persistence.tools.schemaframework.FieldDefinition;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

public class AttributeConverterTableCreator extends TogglingFastTableCreator {

    public AttributeConverterTableCreator(){
        setName("AttributeConverterTableCreator");
        addTableDefinition(buildAttributeConverterHolderTable());
    }

    public TableDefinition buildAttributeConverterHolderTable(){
        TableDefinition table = new TableDefinition();
        table.setName("ATT_CON_HOLD");

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

        FieldDefinition nameField = new FieldDefinition();
        nameField.setName("NAME");
        nameField.setTypeName("VARCHAR");
        nameField.setSize(255);
        nameField.setSubSize(0);
        nameField.setIsPrimaryKey(false);
        nameField.setIsIdentity(false);
        nameField.setUnique(false);
        nameField.setShouldAllowNull(true);
        table.addField(nameField);

        return table;
    }
}
