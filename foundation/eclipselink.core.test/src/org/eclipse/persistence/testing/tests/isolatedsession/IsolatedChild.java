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

import java.io.Serializable;

import org.eclipse.persistence.indirection.ValueHolder;
import org.eclipse.persistence.indirection.ValueHolderInterface;
import org.eclipse.persistence.tools.schemaframework.FieldDefinition;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

public class IsolatedChild implements Serializable {

    protected String id;
    protected String serial;
    protected String deleted = "N";
    protected ValueHolderInterface parent;

    public IsolatedChild() {
        super();
        this.parent = new ValueHolder();
    }

    public void setParent(IsolatedParent parent) {
        this.parent.setValue(parent);
    }

    public IsolatedParent getParent() {
        return (IsolatedParent) this.parent.getValue();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getDeleted() {
        return this.deleted;
    }

    public void setDeleted(String deleted) {
        this.deleted = deleted;
    }

    public static TableDefinition buildISOLATEDCHILDTable() {
        TableDefinition tabledefinition = new TableDefinition();

        // SECTION: TABLE
        tabledefinition.setName("ISOLATED_CHILD");

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
        field1.setName("PARENT_ID");
        field1.setTypeName("NUMERIC");
        field1.setSize(15);
        field1.setShouldAllowNull(false);
        field1.setIsPrimaryKey(false);
        field1.setUnique(false);
        field1.setIsIdentity(false);
        field1.setForeignKeyFieldName("ISOLATED_PARENT.ID");
        tabledefinition.addField(field1);

        // SECTION: FIELD
        FieldDefinition field2 = new FieldDefinition();
        field2.setName("SERIAL");
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

    @Override
    public String toString() {
        return getClass().getSimpleName() + " id: [" + getId() + "] hashcode: [" + System.identityHashCode(this) + "]";
    }

}

