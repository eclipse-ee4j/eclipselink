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
//     dminsky - initial API and implementation
package org.eclipse.persistence.testing.tests.isolatedsession;

import org.eclipse.persistence.indirection.ValueHolder;
import org.eclipse.persistence.indirection.ValueHolderInterface;
import org.eclipse.persistence.tools.schemaframework.FieldDefinition;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

public class IsolatedBone {

    protected String id;
    protected String color;
    protected String deleted;
    protected ValueHolderInterface owner;

    public IsolatedBone() {
        super();
        this.owner = new ValueHolder();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public IsolatedDog getOwner() {
        return (IsolatedDog)owner.getValue();
    }

    public void setOwner(IsolatedDog owner) {
        this.owner.setValue(owner);
    }

    public String getDeleted() {
        return deleted;
    }

    public void setDeleted(String deleted) {
        this.deleted = deleted;
    }

    public static TableDefinition buildISOLATEDBONETable() {
        TableDefinition tabledefinition = new TableDefinition();

        // SECTION: TABLE
        tabledefinition.setName("ISOLATED_BONE");

        // SECTION: FIELD
        FieldDefinition field = new FieldDefinition();
        field.setName("ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(true);
        tabledefinition.addField(field);

        // SECTION: FIELD
        FieldDefinition field1 = new FieldDefinition();
        field1.setName("DOG_ID");
        field1.setTypeName("NUMERIC");
        field1.setSize(15);
        field1.setShouldAllowNull(false);
        field1.setIsPrimaryKey(false);
        field1.setUnique(false);
        field1.setIsIdentity(false);
        field1.setForeignKeyFieldName("ISOLATED_DOG.ID");
        tabledefinition.addField(field1);

        // SECTION: FIELD
        FieldDefinition field2 = new FieldDefinition();
        field2.setName("COLOR");
        field2.setTypeName("VARCHAR");
        field2.setSize(100);
        field2.setShouldAllowNull(false);
        field2.setIsPrimaryKey(false);
        field2.setUnique(false);
        field2.setIsIdentity(false);
        tabledefinition.addField(field2);

        // SECTION: FIELD
        FieldDefinition field3 = new FieldDefinition();
        field3.setName("DELETED");
        field3.setTypeName("VARCHAR");
        field3.setSize(1);
        field3.setShouldAllowNull(false);
        field3.setIsPrimaryKey(false);
        field3.setUnique(false);
        field3.setIsIdentity(false);
        tabledefinition.addField(field3);

        return tabledefinition;
    }

    public String toString() {
        return getClass().getSimpleName() + " [" + System.identityHashCode(this) + "]";
    }

}
