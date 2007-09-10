/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.multipletable;

import org.eclipse.persistence.tools.schemaframework.*;

/**
 * Creates the necessary tables for the MultipleTableProject.
 *
 * @auther Guy Pelletier
 * @version 1.0
 * @date June 17, 2005
 */
public class MultipleTableTableCreator extends TableCreator {
    public MultipleTableTableCreator() {
        setName("MultipleTableTableCreator");

        addTableDefinition(buildCOWTable());
        addTableDefinition(buildCALFSTable());
        
        addTableDefinition(buildHORSETable());
        addTableDefinition(buildFOALSTable());
        
        addTableDefinition(buildHUMANTable());
        addTableDefinition(buildKIDSTable());
        
        addTableDefinition(buildSWANTable());
        addTableDefinition(buildCYGNETSTable());
    }

    protected TableDefinition buildCOWTable() {
        TableDefinition table = new TableDefinition();
        table.setName("MULTI_COW");

        FieldDefinition field = new FieldDefinition();
        field.setName("ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(false);
        table.addField(field);

        FieldDefinition field1 = new FieldDefinition();
        field1.setName("CALFS_ID");
        field1.setTypeName("NUMERIC");
        field1.setSize(15);
        field1.setShouldAllowNull(false);
        field1.setIsPrimaryKey(false);
        field1.setUnique(false);
        field1.setIsIdentity(false);
        table.addField(field1);

        FieldDefinition field2 = new FieldDefinition();
        field2.setName("NAME");
        field2.setTypeName("VARCHAR");
        field2.setSize(40);
        field2.setShouldAllowNull(true);
        field2.setIsPrimaryKey(false);
        field2.setUnique(false);
        field2.setIsIdentity(false);
        table.addField(field2);
        
        ForeignKeyConstraint foreignKeyCOW_CALFS = new ForeignKeyConstraint();
        foreignKeyCOW_CALFS.setName("COW_CALFS");
        foreignKeyCOW_CALFS.setTargetTable("MULTI_CALFS");
        foreignKeyCOW_CALFS.addSourceField("CALFS_ID");
        foreignKeyCOW_CALFS.addTargetField("ID");
        table.addForeignKeyConstraint(foreignKeyCOW_CALFS);

        return table;
    }

    protected TableDefinition buildCALFSTable() {
        TableDefinition table = new TableDefinition();
        table.setName("MULTI_CALFS");

        FieldDefinition field = new FieldDefinition();
        field.setName("ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(false);
        table.addField(field);

        FieldDefinition field1 = new FieldDefinition();
        field1.setName("CALFS");
        field1.setTypeName("NUMERIC");
        field1.setSize(10);
        field1.setShouldAllowNull(true);
        field1.setIsPrimaryKey(false);
        field1.setUnique(false);
        field1.setIsIdentity(false);
        table.addField(field1);

        return table;
    }
    
    protected TableDefinition buildHORSETable() {
        TableDefinition table = new TableDefinition();
        table.setName("MULTI_HORSE");

        FieldDefinition field = new FieldDefinition();
        field.setName("ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(false);
        table.addField(field);

        FieldDefinition field1 = new FieldDefinition();
        field1.setName("NAME");
        field1.setTypeName("VARCHAR");
        field1.setSize(40);
        field1.setShouldAllowNull(true);
        field1.setIsPrimaryKey(false);
        field1.setUnique(false);
        field1.setIsIdentity(false);
        table.addField(field1);

        return table;
    }

    protected TableDefinition buildFOALSTable() {
        TableDefinition table = new TableDefinition();
        table.setName("MULTI_FOALS");

        FieldDefinition field = new FieldDefinition();
        field.setName("HORSE_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(false);
        table.addField(field);

        FieldDefinition field1 = new FieldDefinition();
        field1.setName("FOALS");
        field1.setTypeName("NUMERIC");
        field1.setSize(10);
        field1.setShouldAllowNull(true);
        field1.setIsPrimaryKey(false);
        field1.setUnique(false);
        field1.setIsIdentity(false);
        table.addField(field1);
        
        ForeignKeyConstraint foreignKeyFOALS_HORSE = new ForeignKeyConstraint();
        foreignKeyFOALS_HORSE.setName("FOALS_HORSE");
        foreignKeyFOALS_HORSE.setTargetTable("MULTI_HORSE");
        foreignKeyFOALS_HORSE.addSourceField("HORSE_ID");
        foreignKeyFOALS_HORSE.addTargetField("ID");
        table.addForeignKeyConstraint(foreignKeyFOALS_HORSE);

        return table;
    }
    
    protected TableDefinition buildHUMANTable() {
        TableDefinition table = new TableDefinition();
        table.setName("MULTI_HUMAN");

        FieldDefinition field = new FieldDefinition();
        field.setName("ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(false);
        table.addField(field);

        FieldDefinition field1 = new FieldDefinition();
        field1.setName("NAME");
        field1.setTypeName("VARCHAR");
        field1.setSize(40);
        field1.setShouldAllowNull(true);
        field1.setIsPrimaryKey(false);
        field1.setUnique(false);
        field1.setIsIdentity(false);
        table.addField(field1);

        return table;
    }

    protected TableDefinition buildKIDSTable() {
        TableDefinition table = new TableDefinition();
        table.setName("MULTI_KIDS");

        FieldDefinition field = new FieldDefinition();
        field.setName("ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(false);
        table.addField(field);

        FieldDefinition field1 = new FieldDefinition();
        field1.setName("KIDS");
        field1.setTypeName("NUMERIC");
        field1.setSize(10);
        field1.setShouldAllowNull(true);
        field1.setIsPrimaryKey(false);
        field1.setUnique(false);
        field1.setIsIdentity(false);
        table.addField(field1);
        
        ForeignKeyConstraint foreignKeyKIDS_HUMAN = new ForeignKeyConstraint();
        foreignKeyKIDS_HUMAN.setName("KIDS_HUMAN");
        foreignKeyKIDS_HUMAN.setTargetTable("MULTI_HUMAN");
        foreignKeyKIDS_HUMAN.addSourceField("ID");
        foreignKeyKIDS_HUMAN.addTargetField("ID");
        table.addForeignKeyConstraint(foreignKeyKIDS_HUMAN);

        return table;
    }
    
    protected TableDefinition buildSWANTable() {
        TableDefinition table = new TableDefinition();
        table.setName("MULTI_SWAN");

        FieldDefinition field = new FieldDefinition();
        field.setName("ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(false);
        table.addField(field);

        FieldDefinition field1 = new FieldDefinition();
        field1.setName("NAME");
        field1.setTypeName("VARCHAR");
        field1.setSize(40);
        field1.setShouldAllowNull(true);
        field1.setIsPrimaryKey(false);
        field1.setUnique(false);
        field1.setIsIdentity(false);
        table.addField(field1);
        
        ForeignKeyConstraint foreignKeySWAN_CYGNET = new ForeignKeyConstraint();
        foreignKeySWAN_CYGNET.setName("SWAN_CYGNET");
        foreignKeySWAN_CYGNET.setTargetTable("MULTI_CYGNETS");
        foreignKeySWAN_CYGNET.addSourceField("ID");
        foreignKeySWAN_CYGNET.addTargetField("SWAN_ID");
        table.addForeignKeyConstraint(foreignKeySWAN_CYGNET);

        return table;
    }

    protected TableDefinition buildCYGNETSTable() {
        TableDefinition table = new TableDefinition();
        table.setName("MULTI_CYGNETS");

        FieldDefinition field = new FieldDefinition();
        field.setName("SWAN_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(false);
        table.addField(field);

        FieldDefinition field1 = new FieldDefinition();
        field1.setName("CYGNETS");
        field1.setTypeName("NUMERIC");
        field1.setSize(10);
        field1.setShouldAllowNull(true);
        field1.setIsPrimaryKey(false);
        field1.setUnique(false);
        field1.setIsIdentity(false);
        table.addField(field1);

        return table;
    }
}