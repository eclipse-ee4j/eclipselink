/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.models.multipletable;

import org.eclipse.persistence.tools.schemaframework.*;

/**
 * Creates the necessary tables for the MultipleTableProject.
 *
 * @author Guy Pelletier
 * @version 1.0
 * @date June 17, 2005
 */
public class MultipleTableTableCreator extends TableCreator {
    public MultipleTableTableCreator() {
        setName("MultipleTableTableCreator");

        addTableDefinition(buildCOWTable());
        addTableDefinition(buildCALFSTable());
        addTableDefinition(buildCOW_AGETable());
        addTableDefinition(buildCOW_WEIGHTTable());
        addTableDefinition(buildSUPER_COWTable());
        
        addTableDefinition(buildHORSETable());
        addTableDefinition(buildFOALSTable());
        addTableDefinition(buildHORSE_AGETable());
        addTableDefinition(buildHORSE_WEIGHTTable());
        addTableDefinition(buildSUPER_HORSETable());
        
        addTableDefinition(buildHUMANTable());
        addTableDefinition(buildKIDSTable());
        
        addTableDefinition(buildSWANTable());
        addTableDefinition(buildCYGNETSTable());
        addTableDefinition(buildSWAN_AGETable());
        addTableDefinition(buildSWAN_WEIGHTTable());
        addTableDefinition(buildSUPER_SWANTable());
        addTableDefinition(buildSWAN_WINGSPANTable());
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

        FieldDefinition typeField = new FieldDefinition();
        typeField.setName("TYPE");
        typeField.setTypeName("VARCHAR");
        typeField.setSize(3);
        typeField.setShouldAllowNull(true);
        typeField.setIsPrimaryKey(false);
        typeField.setUnique(false);
        typeField.setIsIdentity(false);
        table.addField(typeField);

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
        
        FieldDefinition field3 = new FieldDefinition();
        field3.setName("AGE_ID");
        field3.setTypeName("NUMERIC");
        field3.setSize(15);
        field3.setShouldAllowNull(false);
        field3.setIsPrimaryKey(false);
        field3.setUnique(false);
        field3.setIsIdentity(false);
        table.addField(field3);

        FieldDefinition field4 = new FieldDefinition();
        field4.setName("WEIGHT_ID");
        field4.setTypeName("NUMERIC");
        field4.setSize(15);
        field4.setShouldAllowNull(false);
        field4.setIsPrimaryKey(false);
        field4.setUnique(false);
        field4.setIsIdentity(false);
        table.addField(field4);

        ForeignKeyConstraint foreignKeyCOW_CALFS = new ForeignKeyConstraint();
        foreignKeyCOW_CALFS.setName("COW_CALFS");
        foreignKeyCOW_CALFS.setTargetTable("MULTI_CALFS");
        foreignKeyCOW_CALFS.addSourceField("CALFS_ID");
        foreignKeyCOW_CALFS.addTargetField("C_ID");
        table.addForeignKeyConstraint(foreignKeyCOW_CALFS);

        ForeignKeyConstraint foreignKeyCOW_AGE = new ForeignKeyConstraint();
        foreignKeyCOW_AGE.setName("COW_AGE");
        foreignKeyCOW_AGE.setTargetTable("MULTI_COW_AGE");
        foreignKeyCOW_AGE.addSourceField("AGE_ID");
        foreignKeyCOW_AGE.addTargetField("A_ID");
        table.addForeignKeyConstraint(foreignKeyCOW_AGE);

        ForeignKeyConstraint foreignKeyCOW_WEIGHT = new ForeignKeyConstraint();
        foreignKeyCOW_WEIGHT.setName("COW_WEIGHT");
        foreignKeyCOW_WEIGHT.setTargetTable("MULTI_COW_WEIGHT");
        foreignKeyCOW_WEIGHT.addSourceField("WEIGHT_ID");
        foreignKeyCOW_WEIGHT.addTargetField("W_ID");
        table.addForeignKeyConstraint(foreignKeyCOW_WEIGHT);

        return table;
    }

    protected TableDefinition buildCALFSTable() {
        TableDefinition table = new TableDefinition();
        table.setName("MULTI_CALFS");

        FieldDefinition field = new FieldDefinition();
        field.setName("C_ID");
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
    
    protected TableDefinition buildCOW_AGETable() {
        TableDefinition table = new TableDefinition();
        table.setName("MULTI_COW_AGE");

        FieldDefinition field = new FieldDefinition();
        field.setName("A_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(false);
        table.addField(field);

        FieldDefinition field1 = new FieldDefinition();
        field1.setName("AGE");
        field1.setTypeName("NUMERIC");
        field1.setSize(10);
        field1.setShouldAllowNull(true);
        field1.setIsPrimaryKey(false);
        field1.setUnique(false);
        field1.setIsIdentity(false);
        table.addField(field1);

        return table;
    }
    
    protected TableDefinition buildCOW_WEIGHTTable() {
        TableDefinition table = new TableDefinition();
        table.setName("MULTI_COW_WEIGHT");

        FieldDefinition field = new FieldDefinition();
        field.setName("W_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(false);
        table.addField(field);

        FieldDefinition field1 = new FieldDefinition();
        field1.setName("WEIGHT");
        field1.setTypeName("NUMERIC");
        field1.setSize(10);
        field1.setShouldAllowNull(true);
        field1.setIsPrimaryKey(false);
        field1.setUnique(false);
        field1.setIsIdentity(false);
        table.addField(field1);

        return table;
    }
    
    protected TableDefinition buildSUPER_COWTable() {
        TableDefinition table = new TableDefinition();
        table.setName("MULTI_SUPER_COW");

        FieldDefinition field = new FieldDefinition();
        field.setName("SC_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(false);
        field.setForeignKeyFieldName("MULTI_COW.ID");
        table.addField(field);

        FieldDefinition sppedField = new FieldDefinition();
        sppedField.setName("SPEED");
        sppedField.setTypeName("NUMERIC");
        sppedField.setSize(15);
        sppedField.setShouldAllowNull(false);
        sppedField.setIsPrimaryKey(false);
        sppedField.setUnique(false);
        sppedField.setIsIdentity(false);
        table.addField(sppedField);

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

        FieldDefinition typeField = new FieldDefinition();
        typeField.setName("TYPE");
        typeField.setTypeName("VARCHAR");
        typeField.setSize(3);
        typeField.setShouldAllowNull(true);
        typeField.setIsPrimaryKey(false);
        typeField.setUnique(false);
        typeField.setIsIdentity(false);
        table.addField(typeField);

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
        field.setName("F_HORSE_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(true);
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
        foreignKeyFOALS_HORSE.addSourceField("F_HORSE_ID");
        foreignKeyFOALS_HORSE.addTargetField("ID");
        table.addForeignKeyConstraint(foreignKeyFOALS_HORSE);

        return table;
    }
    
    protected TableDefinition buildHORSE_AGETable() {
        TableDefinition table = new TableDefinition();
        table.setName("MULTI_HORSE_AGE");

        FieldDefinition field = new FieldDefinition();
        field.setName("A_HORSE_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(false);
        table.addField(field);

        FieldDefinition field1 = new FieldDefinition();
        field1.setName("AGE");
        field1.setTypeName("NUMERIC");
        field1.setSize(10);
        field1.setShouldAllowNull(true);
        field1.setIsPrimaryKey(false);
        field1.setUnique(false);
        field1.setIsIdentity(false);
        table.addField(field1);

        ForeignKeyConstraint foreignKeyHORSE_AGE = new ForeignKeyConstraint();
        foreignKeyHORSE_AGE.setName("AGE_HORSE");
        foreignKeyHORSE_AGE.setTargetTable("MULTI_HORSE");
        foreignKeyHORSE_AGE.addSourceField("A_HORSE_ID");
        foreignKeyHORSE_AGE.addTargetField("ID");
        table.addForeignKeyConstraint(foreignKeyHORSE_AGE);

        return table;
    }
    
    protected TableDefinition buildHORSE_WEIGHTTable() {
        TableDefinition table = new TableDefinition();
        table.setName("MULTI_HORSE_WEIGHT");

        FieldDefinition field = new FieldDefinition();
        field.setName("W_HORSE_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(false);
        table.addField(field);

        FieldDefinition field1 = new FieldDefinition();
        field1.setName("WEIGHT");
        field1.setTypeName("NUMERIC");
        field1.setSize(10);
        field1.setShouldAllowNull(true);
        field1.setIsPrimaryKey(false);
        field1.setUnique(false);
        field1.setIsIdentity(false);
        table.addField(field1);

        ForeignKeyConstraint foreignKeyHORSE_WEIGHT = new ForeignKeyConstraint();
        foreignKeyHORSE_WEIGHT.setName("WEIGHT_HORSE");
        foreignKeyHORSE_WEIGHT.setTargetTable("MULTI_HORSE");
        foreignKeyHORSE_WEIGHT.addSourceField("W_HORSE_ID");
        foreignKeyHORSE_WEIGHT.addTargetField("ID");
        table.addForeignKeyConstraint(foreignKeyHORSE_WEIGHT);

        return table;
    }
    
    protected TableDefinition buildSUPER_HORSETable() {
        TableDefinition table = new TableDefinition();
        table.setName("MULTI_SUPER_HORSE");

        FieldDefinition field = new FieldDefinition();
        field.setName("SH_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(false);
        field.setForeignKeyFieldName("MULTI_HORSE.ID");
        table.addField(field);

        FieldDefinition sppedField = new FieldDefinition();
        sppedField.setName("SPEED");
        sppedField.setTypeName("NUMERIC");
        sppedField.setSize(15);
        sppedField.setShouldAllowNull(false);
        sppedField.setIsPrimaryKey(false);
        sppedField.setUnique(false);
        sppedField.setIsIdentity(false);
        table.addField(sppedField);

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

        FieldDefinition typeField = new FieldDefinition();
        typeField.setName("TYPE");
        typeField.setTypeName("VARCHAR");
        typeField.setSize(3);
        typeField.setShouldAllowNull(true);
        typeField.setIsPrimaryKey(false);
        typeField.setUnique(false);
        typeField.setIsIdentity(false);
        table.addField(typeField);

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
        foreignKeySWAN_CYGNET.addTargetField("C_SWAN_ID");
        table.addForeignKeyConstraint(foreignKeySWAN_CYGNET);

        return table;
    }

    protected TableDefinition buildCYGNETSTable() {
        TableDefinition table = new TableDefinition();
        table.setName("MULTI_CYGNETS");

        FieldDefinition field = new FieldDefinition();
        field.setName("C_SWAN_ID");
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

    protected TableDefinition buildSWAN_AGETable() {
        TableDefinition table = new TableDefinition();
        table.setName("MULTI_SWAN_AGE");

        FieldDefinition field = new FieldDefinition();
        field.setName("A_SWAN_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(false);
        table.addField(field);

        FieldDefinition field1 = new FieldDefinition();
        field1.setName("AGE");
        field1.setTypeName("NUMERIC");
        field1.setSize(10);
        field1.setShouldAllowNull(true);
        field1.setIsPrimaryKey(false);
        field1.setUnique(false);
        field1.setIsIdentity(false);
        table.addField(field1);

        ForeignKeyConstraint foreignKeySWAN_AGE = new ForeignKeyConstraint();
        foreignKeySWAN_AGE.setName("AGE_SWAN");
        foreignKeySWAN_AGE.setTargetTable("MULTI_SWAN");
        foreignKeySWAN_AGE.addSourceField("A_SWAN_ID");
        foreignKeySWAN_AGE.addTargetField("ID");
        table.addForeignKeyConstraint(foreignKeySWAN_AGE);

        return table;
    }
    
    protected TableDefinition buildSWAN_WINGSPANTable() {
        TableDefinition table = new TableDefinition();
        table.setName("MULTI_SWAN_WINGSPAN");

        FieldDefinition field = new FieldDefinition();
        field.setName("A_SWAN_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(false);
        table.addField(field);

        FieldDefinition field1 = new FieldDefinition();
        field1.setName("WING_SPAN");
        field1.setTypeName("NUMERIC");
        field1.setSize(10);
        field1.setShouldAllowNull(true);
        field1.setIsPrimaryKey(false);
        field1.setUnique(false);
        field1.setIsIdentity(false);
        table.addField(field1);

        ForeignKeyConstraint foreignKeySWAN_WING_SPAN = new ForeignKeyConstraint();
        foreignKeySWAN_WING_SPAN.setName("WING_SPAN");
        foreignKeySWAN_WING_SPAN.setTargetTable("MULTI_SUPER_SWAN");
        foreignKeySWAN_WING_SPAN.addSourceField("A_SWAN_ID");
        foreignKeySWAN_WING_SPAN.addTargetField("SS_ID");
        table.addForeignKeyConstraint(foreignKeySWAN_WING_SPAN);

        return table;
    }
    
    protected TableDefinition buildSWAN_WEIGHTTable() {
        TableDefinition table = new TableDefinition();
        table.setName("MULTI_SWAN_WEIGHT");

        FieldDefinition field = new FieldDefinition();
        field.setName("W_SWAN_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(false);
        table.addField(field);

        FieldDefinition field1 = new FieldDefinition();
        field1.setName("WEIGHT");
        field1.setTypeName("NUMERIC");
        field1.setSize(10);
        field1.setShouldAllowNull(true);
        field1.setIsPrimaryKey(false);
        field1.setUnique(false);
        field1.setIsIdentity(false);
        table.addField(field1);

        ForeignKeyConstraint foreignKeySWAN_WEIGHT = new ForeignKeyConstraint();
        foreignKeySWAN_WEIGHT.setName("WIGHT_SWAN");
        foreignKeySWAN_WEIGHT.setTargetTable("MULTI_SWAN");
        foreignKeySWAN_WEIGHT.addSourceField("W_SWAN_ID");
        foreignKeySWAN_WEIGHT.addTargetField("ID");
        table.addForeignKeyConstraint(foreignKeySWAN_WEIGHT);

        return table;
    }    

    protected TableDefinition buildSUPER_SWANTable() {
        TableDefinition table = new TableDefinition();
        table.setName("MULTI_SUPER_SWAN");

        FieldDefinition field = new FieldDefinition();
        field.setName("SS_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(false);
        field.setForeignKeyFieldName("MULTI_SWAN.ID");
        table.addField(field);

        FieldDefinition sppedField = new FieldDefinition();
        sppedField.setName("SPEED");
        sppedField.setTypeName("NUMERIC");
        sppedField.setSize(15);
        sppedField.setShouldAllowNull(false);
        sppedField.setIsPrimaryKey(false);
        sppedField.setUnique(false);
        sppedField.setIsIdentity(false);
        table.addField(sppedField);
        
        return table;
    }
}
