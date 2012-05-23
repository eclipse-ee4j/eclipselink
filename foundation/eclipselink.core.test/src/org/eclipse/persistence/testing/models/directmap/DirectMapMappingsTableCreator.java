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
package org.eclipse.persistence.testing.models.directmap;

import org.eclipse.persistence.tools.schemaframework.*;

/**
 * Creates the necessary tables for the DirectMapMappingsProject.
 *
 * @author Guy Pelletier
 * @version 1.0
 * @date March 04, 2003
 */
public class DirectMapMappingsTableCreator extends TableCreator {
    public DirectMapMappingsTableCreator() {
        setName("DirectMapMappingsTableCreator");

        addTableDefinition(buildDirectMapMappingsTable());
        addTableDefinition(buildDirectMapTable(1));
        addTableDefinition(buildDirectMapTable(2));
        addTableDefinition(buildDirectMapTable(3));
        addTableDefinition(buildDirectMapTable(4));
        addTableDefinition(buildDirectMapBlobTable());
    }

    public TableDefinition buildDirectMapMappingsTable() {
        TableDefinition table = new TableDefinition();
        table.setName("DIRECTMAPMAPPINGS");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMBER");
        // size 38 is too big for SQLServer, DB2 - use 15 instead
        fieldID.setSize(15);
        fieldID.setSubSize(0);
        fieldID.setIsPrimaryKey(true);
        fieldID.setIsIdentity(false);
        fieldID.setUnique(false);
        fieldID.setShouldAllowNull(false);
        table.addField(fieldID);

        return table;
    }

    protected TableDefinition buildDirectMapTable(int tableNumber) {
        TableDefinition table = new TableDefinition();
        table.setName("DIRECTMAP" + tableNumber);

        FieldDefinition field = new FieldDefinition();
        field.setName("ID");
        field.setTypeName("NUMBER");
        // size 38 is too big for SQLServer, DB2 - use 15 instead
        field.setSize(15);
        field.setSubSize(0);
        field.setIsPrimaryKey(true);
        field.setIsIdentity(false);
        field.setUnique(false);
        field.setShouldAllowNull(false);
        table.addField(field);

        FieldDefinition fieldKEY = new FieldDefinition();

        //"KEY" is a reserved keyword for SyBase, it can't be used as a field name
        fieldKEY.setName("KEY_FIELD");
        fieldKEY.setTypeName("NUMBER");
        // size 38 is too big for SQLServer, DB2 - use 15 instead
        fieldKEY.setSize(15);
        fieldKEY.setSubSize(0);
        fieldKEY.setIsPrimaryKey(true);
        fieldKEY.setIsIdentity(false);
        fieldKEY.setUnique(false);
        fieldKEY.setShouldAllowNull(false);
        table.addField(fieldKEY);

        FieldDefinition fieldVALUE = new FieldDefinition();
        fieldVALUE.setName("VAL");
        fieldVALUE.setTypeName("VARCHAR2");
        fieldVALUE.setSize(100);
        fieldVALUE.setSubSize(0);
        fieldVALUE.setIsPrimaryKey(false);
        fieldVALUE.setIsIdentity(false);
        fieldVALUE.setUnique(false);
        fieldVALUE.setShouldAllowNull(true);
        table.addField(fieldVALUE);

        return table;
    }

    protected TableDefinition buildDirectMapBlobTable() {
        TableDefinition table = new TableDefinition();
        table.setName("DIRECTMAPBLOB");

        FieldDefinition field = new FieldDefinition();
        field.setName("ID");
        field.setTypeName("NUMBER");
        // size 38 is too big for SQLServer, DB2 - use 15 instead
        field.setSize(15);
        field.setSubSize(0);
        field.setIsPrimaryKey(true);
        field.setIsIdentity(false);
        field.setUnique(false);
        field.setShouldAllowNull(false);
        table.addField(field);

        FieldDefinition fieldKEY = new FieldDefinition();

        fieldKEY.setName("KEY_FIELD");
        fieldKEY.setTypeName("NUMBER");
        fieldKEY.setSize(15);
        fieldKEY.setSubSize(0);
        fieldKEY.setIsPrimaryKey(true);
        fieldKEY.setIsIdentity(false);
        fieldKEY.setUnique(false);
        fieldKEY.setShouldAllowNull(false);
        table.addField(fieldKEY);

        FieldDefinition fieldVALUE = new FieldDefinition();
        fieldVALUE.setName("VAL");
        fieldVALUE.setTypeName("BLOB");
        fieldVALUE.setIsPrimaryKey(false);
        fieldVALUE.setIsIdentity(false);
        fieldVALUE.setUnique(false);
        fieldVALUE.setShouldAllowNull(true);
        table.addField(fieldVALUE);

        return table;
    }
}
