/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  

package org.eclipse.persistence.testing.models.jpa.virtualattribute;

import org.eclipse.persistence.tools.schemaframework.*;

public class VirtualAttributeTableCreator extends org.eclipse.persistence.tools.schemaframework.TableCreator {

    public VirtualAttributeTableCreator() {
        setName("VirtualAttributeProject");

        addTableDefinition(buildVIRTUALATTRIBUTETable());
        addTableDefinition(buildOOVIRTUALATTRIBUTETable());
        //addTableDefinition(buildVIRTUAL_SEQTable());
    }
    
    public static TableDefinition buildVIRTUALATTRIBUTETable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_VIRTUAL");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("CMP3_VIRTUALID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setSubSize(0);
        fieldID.setIsPrimaryKey(true);
        fieldID.setIsIdentity(true);
        fieldID.setUnique(false);
        fieldID.setShouldAllowNull(false);
        table.addField(fieldID);

        FieldDefinition fieldDESC = new FieldDefinition();
        fieldDESC.setName("DESCRIPTION");
        fieldDESC.setTypeName("VARCHAR2");
        fieldDESC.setSize(60);
        fieldDESC.setSubSize(0);
        fieldDESC.setIsPrimaryKey(false);
        fieldDESC.setIsIdentity(false);
        fieldDESC.setUnique(false);
        fieldDESC.setShouldAllowNull(true);
        table.addField(fieldDESC);

        return table;
    }

    public static TableDefinition buildOOVIRTUALATTRIBUTETable() {
        TableDefinition table = new TableDefinition();
        // SECTION: TABLE
        table.setName("O_O_VIRTUAL");
    
        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field.setName("O_O_VIRTUALID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false );
        field.setIsPrimaryKey(true );
        field.setUnique(false );
        field.setIsIdentity(true );
        table.addField(field);
    
        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field8 = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field8.setName("VIRTUAL_ID");
        field8.setTypeName("NUMERIC");
        field8.setSize(15);
        field8.setShouldAllowNull(true );
        field8.setIsPrimaryKey(false );
        field8.setUnique(false );
        field8.setIsIdentity(false );
        field8.setForeignKeyFieldName("CMP3_VIRTUAL.CMP3_VIRTUALID");
        table.addField(field8);
        
        return table;
    }
    
    public static TableDefinition buildVIRTUAL_SEQTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_VIRTUAL_SEQ");

        FieldDefinition fieldSEQ_COUNT = new FieldDefinition();
        fieldSEQ_COUNT.setName("SEQ_COUNT");
        fieldSEQ_COUNT.setTypeName("NUMBER");
        fieldSEQ_COUNT.setSize(15);
        fieldSEQ_COUNT.setSubSize(0);
        fieldSEQ_COUNT.setIsPrimaryKey(false);
        fieldSEQ_COUNT.setIsIdentity(false);
        fieldSEQ_COUNT.setUnique(false);
        fieldSEQ_COUNT.setShouldAllowNull(false);
        table.addField(fieldSEQ_COUNT);

        FieldDefinition fieldSEQ_NAME = new FieldDefinition();
        fieldSEQ_NAME.setName("SEQ_NAME");
        fieldSEQ_NAME.setTypeName("VARCHAR2");
        fieldSEQ_NAME.setSize(80);
        fieldSEQ_NAME.setSubSize(0);
        fieldSEQ_NAME.setIsPrimaryKey(true);
        fieldSEQ_NAME.setIsIdentity(false);
        fieldSEQ_NAME.setUnique(false);
        fieldSEQ_NAME.setShouldAllowNull(false);
        table.addField(fieldSEQ_NAME);

        return table;
    }
    
}
