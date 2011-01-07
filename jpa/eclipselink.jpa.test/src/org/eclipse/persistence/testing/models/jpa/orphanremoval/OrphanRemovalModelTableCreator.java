/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     05/1/2009-2.0 Guy Pelletier/David Minsky
 *       - 249033: JPA 2.0 Orphan removal
 ******************************************************************************/ 
package org.eclipse.persistence.testing.models.jpa.orphanremoval;

import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.tools.schemaframework.*;

public class OrphanRemovalModelTableCreator extends TableCreator {

    public OrphanRemovalModelTableCreator() {
        setName("JPAOrphanRemovalModelTableCreator");
        addTableDefinition(buildVEHICLETable());
        addTableDefinition(buildCHASSISTable());
        addTableDefinition(buildENGINETable());
        addTableDefinition(buildSPARKPLUGTable());
        addTableDefinition(buildWHEELTable());
        addTableDefinition(buildWHEELRIMTable());
        addTableDefinition(buildWHEELNUTTable());
    }
    
    public static TableDefinition buildCHASSISTable() {
        // CREATE TABLE JPA_OR_CHASSIS (ID NUMBER(10) NOT NULL, SERIALNUMBER NUMBER(19) NULL, PRIMARY KEY (ID))
        TableDefinition table = new TableDefinition();
        table.setName("JPA_OR_CHASSIS");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(10);
        fieldID.setSubSize(0);
        fieldID.setIsPrimaryKey(true);
        fieldID.setIsIdentity(true);
        fieldID.setShouldAllowNull(false);
        fieldID.setUnique(false);
        table.addField(fieldID);
        
        FieldDefinition serialNumber = new FieldDefinition();
        serialNumber.setName("SERIALNUMBER");
        serialNumber.setTypeName("NUMERIC");
        serialNumber.setSize(18);
        serialNumber.setSubSize(0);
        serialNumber.setIsPrimaryKey(false);
        serialNumber.setIsIdentity(false);
        serialNumber.setShouldAllowNull(true);
        serialNumber.setUnique(false);
        table.addField(serialNumber);
        
        return table;
    }
    
    public static TableDefinition buildVEHICLETable() {
        // CREATE TABLE JPA_OR_VEHICLE (ID NUMBER(10) NOT NULL, MODEL VARCHAR2(255) NULL, CHASSIS_ID NUMBER(10) NULL, ENGINE_ID NUMBER(10) NULL, PRIMARY KEY (ID))
        TableDefinition table = new TableDefinition();
        table.setName("JPA_OR_VEHICLE");
        
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(10);
        fieldID.setSubSize(0);
        fieldID.setIsPrimaryKey(true);
        fieldID.setIsIdentity(true);
        fieldID.setShouldAllowNull(false);
        fieldID.setUnique(false);
        table.addField(fieldID);
        
        FieldDefinition fieldMODEL = new FieldDefinition();
        fieldMODEL.setName("MODEL");
        fieldMODEL.setTypeName("VARCHAR");
        fieldMODEL.setSize(60);
        fieldMODEL.setSubSize(0);
        fieldMODEL.setIsPrimaryKey(false);
        fieldMODEL.setIsIdentity(false);
        fieldMODEL.setShouldAllowNull(true);
        fieldMODEL.setUnique(false);
        table.addField(fieldMODEL);
        
        FieldDefinition fieldCHASSIS = new FieldDefinition();
        fieldCHASSIS.setName("CHASSIS_ID");
        fieldCHASSIS.setTypeName("NUMERIC");
        fieldCHASSIS.setSize(10);
        fieldCHASSIS.setSubSize(0);
        fieldCHASSIS.setIsPrimaryKey(false);
        fieldCHASSIS.setIsIdentity(false);
        fieldCHASSIS.setShouldAllowNull(true);
        fieldCHASSIS.setUnique(false);
        table.addField(fieldCHASSIS);
        
        FieldDefinition fieldENGINE = new FieldDefinition();
        fieldENGINE.setName("ENGINE_ID");
        fieldENGINE.setTypeName("NUMERIC");
        fieldENGINE.setSize(10);
        fieldENGINE.setSubSize(0);
        fieldENGINE.setIsPrimaryKey(false);
        fieldENGINE.setIsIdentity(false);
        fieldENGINE.setShouldAllowNull(true);
        fieldENGINE.setUnique(false);
        table.addField(fieldENGINE);
        
        // ALTER TABLE JPA_OR_VEHICLE ADD CONSTRAINT FK_JPA_OR_VEHICLE_ENGINE_ID FOREIGN KEY (ENGINE_ID) REFERENCES JPA_OR_ENGINE (ID)
        ForeignKeyConstraint foreignKeyVEHICLE_ENGINE = new ForeignKeyConstraint();
        foreignKeyVEHICLE_ENGINE.setName("FK_OR_VEH_ENG_ID");
        foreignKeyVEHICLE_ENGINE.setTargetTable("JPA_OR_ENGINE"); 
        foreignKeyVEHICLE_ENGINE.addSourceField("ENGINE_ID");
        foreignKeyVEHICLE_ENGINE.addTargetField("ID");
        table.addForeignKeyConstraint(foreignKeyVEHICLE_ENGINE);

        // ALTER TABLE JPA_OR_VEHICLE ADD CONSTRAINT FK_JPA_OR_VEHICLE_CHASSIS_ID FOREIGN KEY (CHASSIS_ID) REFERENCES JPA_OR_CHASSIS (ID)
        ForeignKeyConstraint foreignKeyVEHICLE_CHASSIS = new ForeignKeyConstraint();
        foreignKeyVEHICLE_CHASSIS.setName("FK_OR_VEH_CHAS_ID");
        foreignKeyVEHICLE_CHASSIS.setTargetTable("JPA_OR_CHASSIS"); 
        foreignKeyVEHICLE_CHASSIS.addSourceField("CHASSIS_ID");
        foreignKeyVEHICLE_CHASSIS.addTargetField("ID");
        table.addForeignKeyConstraint(foreignKeyVEHICLE_CHASSIS);
        
        return table;
    }
    
    public static TableDefinition buildENGINETable() {
        // CREATE TABLE JPA_OR_ENGINE (ID NUMBER(10) NOT NULL, SERIALNUMBER NUMBER(19) NULL, PRIMARY KEY (ID))
        TableDefinition table = new TableDefinition();
        table.setName("JPA_OR_ENGINE");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(10);
        fieldID.setSubSize(0);
        fieldID.setIsPrimaryKey(true);
        fieldID.setIsIdentity(true);
        fieldID.setShouldAllowNull(false);
        fieldID.setUnique(false);
        table.addField(fieldID);
        
        FieldDefinition fieldSERIALNUMBER = new FieldDefinition();
        fieldSERIALNUMBER.setName("SERIALNUMBER");
        fieldSERIALNUMBER.setTypeName("NUMERIC");
        fieldSERIALNUMBER.setSize(18);
        fieldSERIALNUMBER.setSubSize(0);
        fieldSERIALNUMBER.setIsPrimaryKey(false);
        fieldSERIALNUMBER.setIsIdentity(false);
        fieldSERIALNUMBER.setShouldAllowNull(true);
        fieldSERIALNUMBER.setUnique(false);
        table.addField(fieldSERIALNUMBER);
        
        return table;
    }
    
    public static TableDefinition buildSPARKPLUGTable() {
        // CREATE TABLE JPA_OR_SPARK_PLUG (ID NUMBER(10) NOT NULL, SERIALNUMBER NUMBER(19) NULL, ENGINE_ID NUMBER(10) NULL, PRIMARY KEY (ID))
        TableDefinition table = new TableDefinition();
        table.setName("JPA_OR_SPARK_PLUG");
        
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(10);
        fieldID.setSubSize(0);
        fieldID.setIsPrimaryKey(true);
        fieldID.setIsIdentity(true);
        fieldID.setShouldAllowNull(false);
        fieldID.setUnique(false);
        table.addField(fieldID);
        
        FieldDefinition fieldSERIALNUMBER = new FieldDefinition();
        fieldSERIALNUMBER.setName("SERIALNUMBER");
        fieldSERIALNUMBER.setTypeName("NUMERIC");
        fieldSERIALNUMBER.setSize(18);
        fieldSERIALNUMBER.setSubSize(0);
        fieldSERIALNUMBER.setIsPrimaryKey(false);
        fieldSERIALNUMBER.setIsIdentity(false);
        fieldSERIALNUMBER.setShouldAllowNull(true);
        fieldSERIALNUMBER.setUnique(false);
        table.addField(fieldSERIALNUMBER);
        
        FieldDefinition fieldENGINE = new FieldDefinition();
        fieldENGINE.setName("ENGINE_ID");
        fieldENGINE.setTypeName("NUMERIC");
        fieldENGINE.setSize(10);
        fieldENGINE.setSubSize(0);
        fieldENGINE.setIsPrimaryKey(false);
        fieldENGINE.setIsIdentity(false);
        fieldENGINE.setShouldAllowNull(true);
        fieldENGINE.setUnique(false);
        table.addField(fieldENGINE);
        
        // ALTER TABLE JPA_OR_SPARK_PLUG ADD CONSTRAINT JPA_OR_SPARK_PLUG_ENGINE_ID FOREIGN KEY (ENGINE_ID) REFERENCES JPA_OR_ENGINE (ID)
        ForeignKeyConstraint foreignKeySPARKPLUG_ENGINE = new ForeignKeyConstraint();
        foreignKeySPARKPLUG_ENGINE.setName("FK_SPK_PG_ENG_ID");
        foreignKeySPARKPLUG_ENGINE.setTargetTable("JPA_OR_ENGINE"); 
        foreignKeySPARKPLUG_ENGINE.addSourceField("ENGINE_ID");
        foreignKeySPARKPLUG_ENGINE.addTargetField("ID");
        table.addForeignKeyConstraint(foreignKeySPARKPLUG_ENGINE);
        
        return table;
    }
    
    public static TableDefinition buildWHEELTable() {
        // CREATE TABLE JPA_OR_WHEEL (ID NUMBER(10) NOT NULL, SERIALNUMBER NUMBER(19) NULL, WHEELRIM_ID NUMBER(10) NULL, 
        //    CHASSIS_ID NUMBER(10) NULL, MANUFACTURER VARCHAR2(255) NULL, TYPE VARCHAR2(255) NULL, PRIMARY KEY (ID))
        TableDefinition table = new TableDefinition();
        table.setName("JPA_OR_WHEEL");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(10);
        fieldID.setSubSize(0);
        fieldID.setIsPrimaryKey(true);
        fieldID.setIsIdentity(true);
        fieldID.setShouldAllowNull(false);
        fieldID.setUnique(false);
        table.addField(fieldID);
        
        FieldDefinition fieldSERIALNUMBER = new FieldDefinition();
        fieldSERIALNUMBER.setName("SERIALNUMBER");
        fieldSERIALNUMBER.setTypeName("NUMERIC");
        fieldSERIALNUMBER.setSize(18);
        fieldSERIALNUMBER.setSubSize(0);
        fieldSERIALNUMBER.setIsPrimaryKey(false);
        fieldSERIALNUMBER.setIsIdentity(false);
        fieldSERIALNUMBER.setShouldAllowNull(true);
        fieldSERIALNUMBER.setUnique(false);
        table.addField(fieldSERIALNUMBER);
        
        FieldDefinition fieldWHEELRIM = new FieldDefinition();
        fieldWHEELRIM.setName("WHEELRIM_ID");
        fieldWHEELRIM.setTypeName("NUMERIC");
        fieldWHEELRIM.setSize(10);
        fieldWHEELRIM.setSubSize(0);
        fieldWHEELRIM.setIsPrimaryKey(false);
        fieldWHEELRIM.setIsIdentity(false);
        fieldWHEELRIM.setShouldAllowNull(true);
        fieldWHEELRIM.setUnique(false);
        table.addField(fieldWHEELRIM);
        
        FieldDefinition fieldCHASSIS = new FieldDefinition();
        fieldCHASSIS.setName("CHASSIS_ID");
        fieldCHASSIS.setTypeName("NUMERIC");
        fieldCHASSIS.setSize(10);
        fieldCHASSIS.setSubSize(0);
        fieldCHASSIS.setIsPrimaryKey(false);
        fieldCHASSIS.setIsIdentity(false);
        fieldCHASSIS.setShouldAllowNull(true);
        fieldCHASSIS.setUnique(false);
        table.addField(fieldCHASSIS);
        
        FieldDefinition fieldMANUFACTURER = new FieldDefinition();
        fieldMANUFACTURER.setName("MANUFACTURER");
        fieldMANUFACTURER.setTypeName("VARCHAR");
        fieldMANUFACTURER.setSize(60);
        fieldMANUFACTURER.setSubSize(0);
        fieldMANUFACTURER.setIsPrimaryKey(false);
        fieldMANUFACTURER.setIsIdentity(false);
        fieldMANUFACTURER.setShouldAllowNull(true);
        fieldMANUFACTURER.setUnique(false);
        table.addField(fieldMANUFACTURER);
        
        FieldDefinition fieldTYPE = new FieldDefinition();
        fieldTYPE.setName("TYPE");
        fieldTYPE.setTypeName("VARCHAR");
        fieldTYPE.setSize(60);
        fieldTYPE.setSubSize(0);
        fieldTYPE.setIsPrimaryKey(false);
        fieldTYPE.setIsIdentity(false);
        fieldTYPE.setShouldAllowNull(true);
        fieldTYPE.setUnique(false);
        table.addField(fieldTYPE);
        
        // ALTER TABLE JPA_OR_WHEEL ADD CONSTRAINT FK_JPA_OR_WHEEL_WHEELRIM_ID FOREIGN KEY (WHEELRIM_ID) REFERENCES JPA_OR_WHEEL_RIM (ID)
        ForeignKeyConstraint foreignKeyWHEEL_WHEELRIM = new ForeignKeyConstraint();
        foreignKeyWHEEL_WHEELRIM.setName("FK_WHL_WHLRM_ID");
        foreignKeyWHEEL_WHEELRIM.setTargetTable("JPA_OR_WHEEL_RIM"); 
        foreignKeyWHEEL_WHEELRIM.addSourceField("WHEELRIM_ID");
        foreignKeyWHEEL_WHEELRIM.addTargetField("ID");
        table.addForeignKeyConstraint(foreignKeyWHEEL_WHEELRIM);
        
        // ALTER TABLE JPA_OR_WHEEL ADD CONSTRAINT FK_JPA_OR_WHEEL_CHASSIS_ID FOREIGN KEY (CHASSIS_ID) REFERENCES JPA_OR_CHASSIS (ID)
        ForeignKeyConstraint foreignKeyWHEEL_CHASSIS = new ForeignKeyConstraint();
        foreignKeyWHEEL_CHASSIS.setName("FK_WHL_CHAS_ID");
        foreignKeyWHEEL_CHASSIS.setTargetTable("JPA_OR_CHASSIS"); 
        foreignKeyWHEEL_CHASSIS.addSourceField("CHASSIS_ID");
        foreignKeyWHEEL_CHASSIS.addTargetField("ID");
        table.addForeignKeyConstraint(foreignKeyWHEEL_CHASSIS);
        
        return table;
    }
    
    public static TableDefinition buildWHEELRIMTable() {
        // CREATE TABLE JPA_OR_WHEEL_RIM (ID NUMBER(10) NOT NULL, PRIMARY KEY (ID))
        TableDefinition table = new TableDefinition();
        table.setName("JPA_OR_WHEEL_RIM");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(10);
        fieldID.setSubSize(0);
        fieldID.setIsPrimaryKey(true);
        fieldID.setIsIdentity(true);
        fieldID.setShouldAllowNull(false);
        fieldID.setUnique(false);
        table.addField(fieldID);
        
        return table;
    }
    
    public static TableDefinition buildWHEELNUTTable() {
        // CREATE TABLE JPA_OR_WHEEL_NUT (ID NUMBER(10) NOT NULL, WHEEL_ID NUMBER(10) NULL, PRIMARY KEY (ID))
        TableDefinition table = new TableDefinition();
        table.setName("JPA_OR_WHEEL_NUT");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(10);
        fieldID.setSubSize(0);
        fieldID.setIsPrimaryKey(true);
        fieldID.setIsIdentity(true);
        fieldID.setShouldAllowNull(false);
        fieldID.setUnique(false);
        table.addField(fieldID);
        
        FieldDefinition fieldWHEEL = new FieldDefinition();
        fieldWHEEL.setName("WHEEL_ID");
        fieldWHEEL.setTypeName("NUMERIC");
        fieldWHEEL.setSize(10);
        fieldWHEEL.setSubSize(0);
        fieldWHEEL.setIsPrimaryKey(false);
        fieldWHEEL.setIsIdentity(false);
        fieldWHEEL.setShouldAllowNull(true);
        fieldWHEEL.setUnique(false);
        table.addField(fieldWHEEL);
        
        // ALTER TABLE JPA_OR_WHEEL_NUT ADD CONSTRAINT FK_JPA_OR_WHEEL_NUT_WHEEL_ID FOREIGN KEY (WHEEL_ID) REFERENCES JPA_OR_WHEEL (ID)
        ForeignKeyConstraint foreignKeyWHEELNUT_WHEEL = new ForeignKeyConstraint();
        foreignKeyWHEELNUT_WHEEL.setName("FK_WH_NUT_WL_ID");
        foreignKeyWHEELNUT_WHEEL.setTargetTable("JPA_OR_WHEEL"); 
        foreignKeyWHEELNUT_WHEEL.addSourceField("WHEEL_ID");
        foreignKeyWHEELNUT_WHEEL.addTargetField("ID");
        table.addForeignKeyConstraint(foreignKeyWHEELNUT_WHEEL);
        
        return table;
    }
    
    /**
     * Dropping old foreign keys from schema change.
     */
    @Override
    public void replaceTables(DatabaseSession session) {
        if (session.getPlatform().supportsUniqueKeyConstraints()
                && !session.getPlatform().requiresUniqueConstraintCreationOnTableCreate()) {
            try {
                session.executeNonSelectingSQL("Alter table JPA_OR_SPARK_PLUG drop constraint JPA_OR_SPARK_PLUG_ENGINE_ID");
                session.executeNonSelectingSQL("Alter table JPA_OR_VEHICLE drop constraint FK_JPA_OR_VEHICLE_ENGINE_ID");
                session.executeNonSelectingSQL("Alter table JPA_OR_VEHICLE drop constraint FK_JPA_OR_VEHICLE_CHASSIS_ID");
                session.executeNonSelectingSQL("Alter table JPA_OR_WHEEL drop constraint FK_JPA_OR_WHEEL_WHEELRIM_ID");
                session.executeNonSelectingSQL("Alter table JPA_OR_WHEEL drop constraint FK_JPA_OR_WHEEL_CHASSIS_ID");
                session.executeNonSelectingSQL("Alter table JPA_OR_WHEEL_NUT drop constraint FK_JPA_OR_WHEEL_NUT_WHEEL_ID");
            } catch (Exception ignore) {}
        }
        super.replaceTables(session);
    }
    
}
