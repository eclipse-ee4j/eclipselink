/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     dminsky - initial API and implementation
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.isolatedsession;

import java.io.Serializable;

import org.eclipse.persistence.indirection.ValueHolder;
import org.eclipse.persistence.indirection.ValueHolderInterface;
import org.eclipse.persistence.tools.schemaframework.FieldDefinition;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

public class IsolatedChild implements Serializable {
    
    protected String id;
    protected String serial;
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
        field1.setIsPrimaryKey(true);
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
        field2.setIsPrimaryKey(true);
        field2.setUnique(false);
        field2.setIsIdentity(false);
        tabledefinition.addField(field2);
        
        return tabledefinition;
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + " id: [" + getId() + "] hashcode: [" + System.identityHashCode(this) + "]";
    }

}

