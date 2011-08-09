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
 *     02/26/09 dminsky - initial API and implementation
 ******************************************************************************/ 
package org.eclipse.persistence.testing.models.jpa.privateowned;

import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.tools.schemaframework.*;

public class PrivateOwnedModelTableCreator extends TableCreator {

    public PrivateOwnedModelTableCreator() {
        setName("EJB3PrivateOwnedModelTableCreator");
        addTableDefinition(buildVEHICLETable());
        addTableDefinition(buildCHASSISTable());
        addTableDefinition(buildMOUNTTable());
        addTableDefinition(buildENGINETable());
        addTableDefinition(buildSPARKPLUGTable());
        addTableDefinition(buildWHEELTable());
        addTableDefinition(buildWHEELRIMTable());
        addTableDefinition(buildWHEELNUTTable());
    }
    
    public static TableDefinition buildCHASSISTable() {
        // CREATE TABLE CMP3_PO_CHASSIS (ID NUMBER(10) NOT NULL, SERIALNUMBER NUMBER(19) NULL, PRIMARY KEY (ID))
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_PO_CHASSIS");

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
        // CREATE TABLE CMP3_PO_VEHICLE (ID NUMBER(10) NOT NULL, MODEL VARCHAR2(255) NULL, CHASSIS_ID NUMBER(10) NULL, ENGINE_ID NUMBER(10) NULL, PRIMARY KEY (ID))

        TableDefinition table = new TableDefinition();
        table.setName("CMP3_PO_VEHICLE");
        
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
        
        FieldDefinition fieldDTYPE = new FieldDefinition();
        fieldDTYPE.setName("DTYPE");
        fieldDTYPE.setTypeName("VARCHAR");
        fieldDTYPE.setSize(60);
        fieldDTYPE.setSubSize(0);
        fieldDTYPE.setIsPrimaryKey(false);
        fieldDTYPE.setIsIdentity(false);
        fieldDTYPE.setShouldAllowNull(true);
        fieldDTYPE.setUnique(false);
        table.addField(fieldDTYPE);

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
        
        // ALTER TABLE CMP3_PO_VEHICLE ADD CONSTRAINT FK_CMP3_PO_VEHICLE_ENGINE_ID FOREIGN KEY (ENGINE_ID) REFERENCES CMP3_PO_ENGINE (ID)
        ForeignKeyConstraint foreignKeyVEHICLE_ENGINE = new ForeignKeyConstraint();
        foreignKeyVEHICLE_ENGINE.setName("FK_PO_VEH_ENG_ID");
        foreignKeyVEHICLE_ENGINE.setTargetTable("CMP3_PO_ENGINE"); 
        foreignKeyVEHICLE_ENGINE.addSourceField("ENGINE_ID");
        foreignKeyVEHICLE_ENGINE.addTargetField("ID");
        table.addForeignKeyConstraint(foreignKeyVEHICLE_ENGINE);

        // ALTER TABLE CMP3_PO_VEHICLE ADD CONSTRAINT FK_CMP3_PO_VEHICLE_CHASSIS_ID FOREIGN KEY (CHASSIS_ID) REFERENCES CMP3_PO_CHASSIS (ID)
        ForeignKeyConstraint foreignKeyVEHICLE_CHASSIS = new ForeignKeyConstraint();
        foreignKeyVEHICLE_CHASSIS.setName("FK_PO_VEH_CHAS_ID");
        foreignKeyVEHICLE_CHASSIS.setTargetTable("CMP3_PO_CHASSIS"); 
        foreignKeyVEHICLE_CHASSIS.addSourceField("CHASSIS_ID");
        foreignKeyVEHICLE_CHASSIS.addTargetField("ID");
        table.addForeignKeyConstraint(foreignKeyVEHICLE_CHASSIS);
        
        return table;
    }
    
    public static TableDefinition buildENGINETable() {
        // CREATE TABLE CMP3_PO_ENGINE (ID NUMBER(10) NOT NULL, SERIALNUMBER NUMBER(19) NULL, PRIMARY KEY (ID))
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_PO_ENGINE");

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
    
    public static TableDefinition buildMOUNTTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_PO_MOUNT");

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
        
        FieldDefinition fieldCHASSIS_ID = new FieldDefinition();
        fieldCHASSIS_ID.setName("ID2");
        fieldCHASSIS_ID.setTypeName("NUMERIC");
        fieldCHASSIS_ID.setSize(10);
        fieldCHASSIS_ID.setSubSize(0);
        fieldCHASSIS_ID.setIsPrimaryKey(true);
        fieldCHASSIS_ID.setIsIdentity(true);
        fieldCHASSIS_ID.setShouldAllowNull(false);
        fieldCHASSIS_ID.setUnique(false);
        table.addField(fieldCHASSIS_ID);
        
        FieldDefinition fieldVEHICLE_ID = new FieldDefinition();
        fieldVEHICLE_ID.setName("VEHICLE_ID");
        fieldVEHICLE_ID.setTypeName("NUMERIC");
        fieldVEHICLE_ID.setSize(10);
        fieldVEHICLE_ID.setSubSize(0);
        fieldVEHICLE_ID.setIsPrimaryKey(false);
        fieldVEHICLE_ID.setIsIdentity(false);
        fieldVEHICLE_ID.setShouldAllowNull(false);
        fieldVEHICLE_ID.setUnique(false);
        table.addField(fieldVEHICLE_ID);
        
        ForeignKeyConstraint foreignKeyMOUNT_VEHICLE = new ForeignKeyConstraint();
        foreignKeyMOUNT_VEHICLE.setName("PO_MOUNT_VEH_ID");
        foreignKeyMOUNT_VEHICLE.setTargetTable("CMP3_PO_VEHICLE"); 
        foreignKeyMOUNT_VEHICLE.addSourceField("VEHICLE_ID");
        foreignKeyMOUNT_VEHICLE.addTargetField("ID");
        table.addForeignKeyConstraint(foreignKeyMOUNT_VEHICLE);
        
        ForeignKeyConstraint foreignKeyMOUNT_CHASSIS = new ForeignKeyConstraint();
        foreignKeyMOUNT_CHASSIS.setName("PO_MOUNT_CHA_ID");
        foreignKeyMOUNT_CHASSIS.setTargetTable("CMP3_PO_CHASSIS"); 
        foreignKeyMOUNT_CHASSIS.addSourceField("ID2");
        foreignKeyMOUNT_CHASSIS.addTargetField("ID");
        table.addForeignKeyConstraint(foreignKeyMOUNT_CHASSIS);
        
        return table;
    }
    
    public static TableDefinition buildSPARKPLUGTable() {
        // CREATE TABLE CMP3_PO_SPARK_PLUG (ID NUMBER(10) NOT NULL, SERIALNUMBER NUMBER(19) NULL, ENGINE_ID NUMBER(10) NULL, PRIMARY KEY (ID))
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_PO_SPARK_PLUG");
        
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
        
        // ALTER TABLE CMP3_PO_SPARK_PLUG ADD CONSTRAINT CMP3_PO_SPARK_PLUG_ENGINE_ID FOREIGN KEY (ENGINE_ID) REFERENCES CMP3_PO_ENGINE (ID)
        ForeignKeyConstraint foreignKeySPARKPLUG_ENGINE = new ForeignKeyConstraint();
        foreignKeySPARKPLUG_ENGINE.setName("PO_SPK_PG_ENG_ID");
        foreignKeySPARKPLUG_ENGINE.setTargetTable("CMP3_PO_ENGINE"); 
        foreignKeySPARKPLUG_ENGINE.addSourceField("ENGINE_ID");
        foreignKeySPARKPLUG_ENGINE.addTargetField("ID");
        table.addForeignKeyConstraint(foreignKeySPARKPLUG_ENGINE);
        
        return table;
    }
    
    public static TableDefinition buildWHEELTable() {
        // CREATE TABLE CMP3_PO_WHEEL (ID NUMBER(10) NOT NULL, SERIALNUMBER NUMBER(19) NULL, WHEELRIM_ID NUMBER(10) NULL, 
        //    CHASSIS_ID NUMBER(10) NULL, MANUFACTURER VARCHAR2(255) NULL, TYPE VARCHAR2(255) NULL, PRIMARY KEY (ID))
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_PO_WHEEL");

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
        
        // ALTER TABLE CMP3_PO_WHEEL ADD CONSTRAINT FK_CMP3_PO_WHEEL_WHEELRIM_ID FOREIGN KEY (WHEELRIM_ID) REFERENCES CMP3_PO_WHEEL_RIM (ID)
        ForeignKeyConstraint foreignKeyWHEEL_WHEELRIM = new ForeignKeyConstraint();
        foreignKeyWHEEL_WHEELRIM.setName("FK_WL_WLRM_ID");
        foreignKeyWHEEL_WHEELRIM.setTargetTable("CMP3_PO_WHEEL_RIM"); 
        foreignKeyWHEEL_WHEELRIM.addSourceField("WHEELRIM_ID");
        foreignKeyWHEEL_WHEELRIM.addTargetField("ID");
        table.addForeignKeyConstraint(foreignKeyWHEEL_WHEELRIM);
        
        // ALTER TABLE CMP3_PO_WHEEL ADD CONSTRAINT FK_CMP3_PO_WHEEL_CHASSIS_ID FOREIGN KEY (CHASSIS_ID) REFERENCES CMP3_PO_CHASSIS (ID)
        ForeignKeyConstraint foreignKeyWHEEL_CHASSIS = new ForeignKeyConstraint();
        foreignKeyWHEEL_CHASSIS.setName("FK_WEL_CHAS_ID");
        foreignKeyWHEEL_CHASSIS.setTargetTable("CMP3_PO_CHASSIS"); 
        foreignKeyWHEEL_CHASSIS.addSourceField("CHASSIS_ID");
        foreignKeyWHEEL_CHASSIS.addTargetField("ID");
        table.addForeignKeyConstraint(foreignKeyWHEEL_CHASSIS);
        
        return table;
    }
    
    public static TableDefinition buildWHEELRIMTable() {
        // CREATE TABLE CMP3_PO_WHEEL_RIM (ID NUMBER(10) NOT NULL, PRIMARY KEY (ID))
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_PO_WHEEL_RIM");

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
        // CREATE TABLE CMP3_PO_WHEEL_NUT (ID NUMBER(10) NOT NULL, WHEEL_ID NUMBER(10) NULL, PRIMARY KEY (ID))
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_PO_WHEEL_NUT");

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
        
        // ALTER TABLE CMP3_PO_WHEEL_NUT ADD CONSTRAINT FK_CMP3_PO_WHEEL_NUT_WHEEL_ID FOREIGN KEY (WHEEL_ID) REFERENCES CMP3_PO_WHEEL (ID)
        ForeignKeyConstraint foreignKeyWHEELNUT_WHEEL = new ForeignKeyConstraint();
        foreignKeyWHEELNUT_WHEEL.setName("FK_WL_NUT_WHL_ID");
        foreignKeyWHEELNUT_WHEEL.setTargetTable("CMP3_PO_WHEEL"); 
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
                session.executeNonSelectingSQL("Alter table CMP3_PO_SPARK_PLUG drop constraint CMP3_PO_SPARK_PLUG_ENGINE_ID");
                session.executeNonSelectingSQL("Alter table CMP3_PO_VEHICLE drop constraint FK_CMP3_PO_VEHICLE_ENGINE_ID");
                session.executeNonSelectingSQL("Alter table CMP3_PO_VEHICLE drop constraint FK_CMP3_PO_VEHICLE_CHASSIS_ID");
                session.executeNonSelectingSQL("Alter table CMP3_PO_WHEEL drop constraint FK_CMP3_PO_WHEEL_WHEELRIM_ID");
                session.executeNonSelectingSQL("Alter table CMP3_PO_WHEEL drop constraint FK_CMP3_PO_WHEEL_CHASSIS_ID");
                session.executeNonSelectingSQL("Alter table CMP3_PO_WHEEL_NUT drop constraint FK_CMP3_PO_WHEEL_NUT_WHEEL_ID");
            } catch (Exception ignore) {}
        }
        super.replaceTables(session);
    }
    
}
