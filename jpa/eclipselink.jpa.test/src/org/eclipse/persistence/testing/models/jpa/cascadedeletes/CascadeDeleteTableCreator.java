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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.cascadedeletes;

import org.eclipse.persistence.tools.schemaframework.*;

public class CascadeDeleteTableCreator extends org.eclipse.persistence.tools.schemaframework.TableCreator {

public CascadeDeleteTableCreator() {
    setName("CascadeDelete");
    
    addTableDefinition(buildBRANCHA_CDTable());
    addTableDefinition(buildBRANCHB_CDTable());
    addTableDefinition(buildLEAFA_CDTable());
    addTableDefinition(buildLEAFB_CDTable());
    addTableDefinition(buildROOTA_CDTable());
    addTableDefinition(buildROOTA_BRANCHATable());
    addTableDefinition(buildBRANCHB_LEAFBTable());
    addTableDefinition(buildBRANCHB_BRANCHBTable());
    addTableDefinition(buildMACHINESTATETable());
    addTableDefinition(buildTHREADINFOTable());
    addTableDefinition(buildMACHINESTATE_THREADINFOTable());
    addTableDefinition(buildBRANCHA_LEAFATable());
}

public TableDefinition buildMACHINESTATE_THREADINFOTable() {
    TableDefinition table = new TableDefinition();
    table.setName("MACHINESTATE_THREADINFO");
    
    FieldDefinition fieldBRANCHB_ID = new FieldDefinition();
    fieldBRANCHB_ID.setName("MachineState_ID");
    fieldBRANCHB_ID.setTypeName("NUMBER");
    fieldBRANCHB_ID.setSize(0);
    fieldBRANCHB_ID.setSubSize(0);
    fieldBRANCHB_ID.setIsPrimaryKey(true);
    fieldBRANCHB_ID.setIsIdentity(false);
    fieldBRANCHB_ID.setUnique(false);
    fieldBRANCHB_ID.setShouldAllowNull(false);
    table.addField(fieldBRANCHB_ID);
    
    FieldDefinition fieldBRANCHBS_ID = new FieldDefinition();
    fieldBRANCHBS_ID.setName("threads_ID");
    fieldBRANCHBS_ID.setTypeName("NUMBER");
    fieldBRANCHBS_ID.setSize(0);
    fieldBRANCHBS_ID.setSubSize(0);
    fieldBRANCHBS_ID.setIsPrimaryKey(true);
    fieldBRANCHBS_ID.setIsIdentity(false);
    fieldBRANCHBS_ID.setUnique(false);
    fieldBRANCHBS_ID.setShouldAllowNull(false);
    table.addField(fieldBRANCHBS_ID);
    
    ForeignKeyConstraint foreignKeyM_THREADINFO_THREADINFO = new ForeignKeyConstraint();
    foreignKeyM_THREADINFO_THREADINFO.setName("M_THREADINFO_THREADINFO");
    foreignKeyM_THREADINFO_THREADINFO.setTargetTable("THREADINFO");
    foreignKeyM_THREADINFO_THREADINFO.addSourceField("threads_ID");
    foreignKeyM_THREADINFO_THREADINFO.addTargetField("ID");
    table.addForeignKeyConstraint(foreignKeyM_THREADINFO_THREADINFO);
    
    ForeignKeyConstraint foreignKeyM_THREADINFO_MACHINESTATE = new ForeignKeyConstraint();
    foreignKeyM_THREADINFO_MACHINESTATE.setName("M_THREADINFO_MACHINESTATE");
    foreignKeyM_THREADINFO_MACHINESTATE.setTargetTable("MACHINESTATE");
    foreignKeyM_THREADINFO_MACHINESTATE.addSourceField("MachineState_ID");
    foreignKeyM_THREADINFO_MACHINESTATE.addTargetField("ID");
    table.addForeignKeyConstraint(foreignKeyM_THREADINFO_MACHINESTATE);
    return table;
}

public TableDefinition buildTHREADINFOTable() {
    TableDefinition table = new TableDefinition();
    table.setName("THREADINFO");
    
    FieldDefinition fieldID = new FieldDefinition();
    fieldID.setName("ID");
    fieldID.setTypeName("NUMBER");
    fieldID.setSize(0);
    fieldID.setSubSize(0);
    fieldID.setIsPrimaryKey(true);
    fieldID.setIsIdentity(false);
    fieldID.setUnique(false);
    fieldID.setShouldAllowNull(false);
    table.addField(fieldID);

    FieldDefinition fieldBRANCHA = new FieldDefinition();
    fieldBRANCHA.setName("NAME");
    fieldBRANCHA.setTypeName("VARCHAR");
    fieldBRANCHA.setSize(255);
    fieldBRANCHA.setSubSize(0);
    fieldBRANCHA.setIsPrimaryKey(false);
    fieldBRANCHA.setIsIdentity(false);
    fieldBRANCHA.setUnique(false);
    fieldBRANCHA.setShouldAllowNull(true);
    table.addField(fieldBRANCHA);

    return table;
}

public TableDefinition buildMACHINESTATETable() {
    TableDefinition table = new TableDefinition();
    table.setName("MACHINESTATE");
    
    FieldDefinition fieldID = new FieldDefinition();
    fieldID.setName("ID");
    fieldID.setTypeName("NUMBER");
    fieldID.setSize(0);
    fieldID.setSubSize(0);
    fieldID.setIsPrimaryKey(true);
    fieldID.setIsIdentity(false);
    fieldID.setUnique(false);
    fieldID.setShouldAllowNull(false);
    table.addField(fieldID);
    
    return table;
}
public TableDefinition buildBRANCHA_CDTable() {
    TableDefinition table = new TableDefinition();
    table.setName("BRANCHA");
    
    FieldDefinition fieldID = new FieldDefinition();
    fieldID.setName("ID");
    fieldID.setTypeName("NUMBER");
    fieldID.setSize(0);
    fieldID.setSubSize(0);
    fieldID.setIsPrimaryKey(true);
    fieldID.setIsIdentity(false);
    fieldID.setUnique(false);
    fieldID.setShouldAllowNull(false);
    table.addField(fieldID);
    
    FieldDefinition fieldBRANCHA = new FieldDefinition();
    fieldBRANCHA.setName("BRANCHA_ID");
    fieldBRANCHA.setTypeName("NUMBER");
    fieldBRANCHA.setSize(0);
    fieldBRANCHA.setSubSize(0);
    fieldBRANCHA.setIsPrimaryKey(false);
    fieldBRANCHA.setIsIdentity(false);
    fieldBRANCHA.setUnique(false);
    fieldBRANCHA.setShouldAllowNull(true);
    table.addField(fieldBRANCHA);
    
    return table;
}

public TableDefinition buildBRANCHB_BRANCHBTable() {
    TableDefinition table = new TableDefinition();
    table.setName("BRANCHB_BRANCHB");
    
    FieldDefinition fieldBRANCHB_ID = new FieldDefinition();
    fieldBRANCHB_ID.setName("BranchB_ID");
    fieldBRANCHB_ID.setTypeName("NUMBER");
    fieldBRANCHB_ID.setSize(0);
    fieldBRANCHB_ID.setSubSize(0);
    fieldBRANCHB_ID.setIsPrimaryKey(true);
    fieldBRANCHB_ID.setIsIdentity(false);
    fieldBRANCHB_ID.setUnique(false);
    fieldBRANCHB_ID.setShouldAllowNull(false);
    table.addField(fieldBRANCHB_ID);
    
    FieldDefinition fieldBRANCHBS_ID = new FieldDefinition();
    fieldBRANCHBS_ID.setName("branchBs_ID");
    fieldBRANCHBS_ID.setTypeName("NUMBER");
    fieldBRANCHBS_ID.setSize(0);
    fieldBRANCHBS_ID.setSubSize(0);
    fieldBRANCHBS_ID.setIsPrimaryKey(true);
    fieldBRANCHBS_ID.setIsIdentity(false);
    fieldBRANCHBS_ID.setUnique(false);
    fieldBRANCHBS_ID.setShouldAllowNull(false);
    table.addField(fieldBRANCHBS_ID);
    
    ForeignKeyConstraint foreignKeyBRANCHB_BRANCHB_BRANCHB = new ForeignKeyConstraint();
    foreignKeyBRANCHB_BRANCHB_BRANCHB.setName("BRANCHB_BRANCHB_BRANCHB");
    foreignKeyBRANCHB_BRANCHB_BRANCHB.setTargetTable("BRANCHB");
    foreignKeyBRANCHB_BRANCHB_BRANCHB.addSourceField("branchBs_ID");
    foreignKeyBRANCHB_BRANCHB_BRANCHB.addTargetField("ID");
    table.addForeignKeyConstraint(foreignKeyBRANCHB_BRANCHB_BRANCHB);
    
    ForeignKeyConstraint foreignKeyBRANCHB_BRANCHB_BRANCHB2 = new ForeignKeyConstraint();
    foreignKeyBRANCHB_BRANCHB_BRANCHB2.setName("BRANCHB_BRANCHB_BRANCHB2");
    foreignKeyBRANCHB_BRANCHB_BRANCHB2.setTargetTable("BRANCHB");
    foreignKeyBRANCHB_BRANCHB_BRANCHB2.addSourceField("BranchB_ID");
    foreignKeyBRANCHB_BRANCHB_BRANCHB2.addTargetField("ID");
    table.addForeignKeyConstraint(foreignKeyBRANCHB_BRANCHB_BRANCHB2);

    return table;
}

public TableDefinition buildBRANCHB_CDTable() {
    TableDefinition table = new TableDefinition();
    table.setName("BRANCHB");
    
    FieldDefinition fieldID = new FieldDefinition();
    fieldID.setName("ID");
    fieldID.setTypeName("NUMBER");
    fieldID.setSize(0);
    fieldID.setSubSize(0);
    fieldID.setIsPrimaryKey(true);
    fieldID.setIsIdentity(false);
    fieldID.setUnique(false);
    fieldID.setShouldAllowNull(false);
    table.addField(fieldID);
    
    return table;
}

public TableDefinition buildBRANCHB_LEAFBTable() {
    TableDefinition table = new TableDefinition();
    table.setName("BRANCHB_LEAFB");
    
    FieldDefinition fieldLEAFB_ID = new FieldDefinition();
    fieldLEAFB_ID.setName("leafBs_ID");
    fieldLEAFB_ID.setTypeName("NUMBER");
    fieldLEAFB_ID.setSize(0);
    fieldLEAFB_ID.setSubSize(0);
    fieldLEAFB_ID.setIsPrimaryKey(true);
    fieldLEAFB_ID.setIsIdentity(false);
    fieldLEAFB_ID.setUnique(false);
    fieldLEAFB_ID.setShouldAllowNull(false);
    table.addField(fieldLEAFB_ID);
    
    FieldDefinition fieldBRANCHB_ID = new FieldDefinition();
    fieldBRANCHB_ID.setName("BranchB_ID");
    fieldBRANCHB_ID.setTypeName("NUMBER");
    fieldBRANCHB_ID.setSize(0);
    fieldBRANCHB_ID.setSubSize(0);
    fieldBRANCHB_ID.setIsPrimaryKey(true);
    fieldBRANCHB_ID.setIsIdentity(false);
    fieldBRANCHB_ID.setUnique(false);
    fieldBRANCHB_ID.setShouldAllowNull(false);
    table.addField(fieldBRANCHB_ID);
    
    ForeignKeyConstraint foreignKeyBRANCHB_LEAFB_LEAFB = new ForeignKeyConstraint();
    foreignKeyBRANCHB_LEAFB_LEAFB.setName("BRANCHB_LEAFB_LEAFB");
    foreignKeyBRANCHB_LEAFB_LEAFB.setTargetTable("LEAFB");
    foreignKeyBRANCHB_LEAFB_LEAFB.addSourceField("leafBs_ID");
    foreignKeyBRANCHB_LEAFB_LEAFB.addTargetField("ID");
    table.addForeignKeyConstraint(foreignKeyBRANCHB_LEAFB_LEAFB);
    
    ForeignKeyConstraint foreignKeyBRANCHB_LEAFB_BRANCHB2 = new ForeignKeyConstraint();
    foreignKeyBRANCHB_LEAFB_BRANCHB2.setName("BRANCHB_LEAFB_BRANCHB2");
    foreignKeyBRANCHB_LEAFB_BRANCHB2.setTargetTable("BRANCHB");
    foreignKeyBRANCHB_LEAFB_BRANCHB2.addSourceField("BranchB_ID");
    foreignKeyBRANCHB_LEAFB_BRANCHB2.addTargetField("ID");
    table.addForeignKeyConstraint(foreignKeyBRANCHB_LEAFB_BRANCHB2);

    return table;
}

public TableDefinition buildBRANCHA_LEAFATable() {
    TableDefinition table = new TableDefinition();
    table.setName("BRANCHA_LEAFA");
    
    FieldDefinition fieldLEAFB_ID = new FieldDefinition();
    fieldLEAFB_ID.setName("secondSet_ID");
    fieldLEAFB_ID.setTypeName("NUMBER");
    fieldLEAFB_ID.setSize(0);
    fieldLEAFB_ID.setSubSize(0);
    fieldLEAFB_ID.setIsPrimaryKey(true);
    fieldLEAFB_ID.setIsIdentity(false);
    fieldLEAFB_ID.setUnique(false);
    fieldLEAFB_ID.setShouldAllowNull(false);
    table.addField(fieldLEAFB_ID);
    
    FieldDefinition fieldBRANCHB_ID = new FieldDefinition();
    fieldBRANCHB_ID.setName("BranchA_ID");
    fieldBRANCHB_ID.setTypeName("NUMBER");
    fieldBRANCHB_ID.setSize(0);
    fieldBRANCHB_ID.setSubSize(0);
    fieldBRANCHB_ID.setIsPrimaryKey(true);
    fieldBRANCHB_ID.setIsIdentity(false);
    fieldBRANCHB_ID.setUnique(false);
    fieldBRANCHB_ID.setShouldAllowNull(false);
    table.addField(fieldBRANCHB_ID);
    
    ForeignKeyConstraint foreignKeyBRANCHA_LEAFA_LEAFA = new ForeignKeyConstraint();
    foreignKeyBRANCHA_LEAFA_LEAFA.setName("BRANCHA_LEAFA_LEAFA");
    foreignKeyBRANCHA_LEAFA_LEAFA.setTargetTable("LEAFA");
    foreignKeyBRANCHA_LEAFA_LEAFA.addSourceField("secondSet_ID");
    foreignKeyBRANCHA_LEAFA_LEAFA.addTargetField("ID");
    table.addForeignKeyConstraint(foreignKeyBRANCHA_LEAFA_LEAFA);
    
    ForeignKeyConstraint foreignKeyBRANCHA_LEAFA_BRANCHA2 = new ForeignKeyConstraint();
    foreignKeyBRANCHA_LEAFA_BRANCHA2.setName("BRANCHA_LEAFA_BRANCHA2");
    foreignKeyBRANCHA_LEAFA_BRANCHA2.setTargetTable("BRANCHA");
    foreignKeyBRANCHA_LEAFA_BRANCHA2.addSourceField("BranchA_ID");
    foreignKeyBRANCHA_LEAFA_BRANCHA2.addTargetField("ID");
    table.addForeignKeyConstraint(foreignKeyBRANCHA_LEAFA_BRANCHA2);

    return table;
}

public TableDefinition buildLEAFA_CDTable() {
    TableDefinition table = new TableDefinition();
    table.setName("LEAFA");
    
    FieldDefinition fieldID = new FieldDefinition();
    fieldID.setName("ID");
    fieldID.setTypeName("NUMBER");
    fieldID.setSize(0);
    fieldID.setSubSize(0);
    fieldID.setIsPrimaryKey(true);
    fieldID.setIsIdentity(false);
    fieldID.setUnique(false);
    fieldID.setShouldAllowNull(false);
    table.addField(fieldID);
    
    FieldDefinition fieldBRANCHA = new FieldDefinition();
    fieldBRANCHA.setName("BRANCHA_ID");
    fieldBRANCHA.setTypeName("NUMBER");
    fieldBRANCHA.setSize(0);
    fieldBRANCHA.setSubSize(0);
    fieldBRANCHA.setIsPrimaryKey(false);
    fieldBRANCHA.setIsIdentity(false);
    fieldBRANCHA.setUnique(false);
    fieldBRANCHA.setShouldAllowNull(true);
    table.addField(fieldBRANCHA);
    
    return table;
}

public TableDefinition buildLEAFB_CDTable() {
    TableDefinition table = new TableDefinition();
    table.setName("LEAFB");
    
    FieldDefinition fieldID = new FieldDefinition();
    fieldID.setName("ID");
    fieldID.setTypeName("NUMBER");
    fieldID.setSize(0);
    fieldID.setSubSize(0);
    fieldID.setIsPrimaryKey(true);
    fieldID.setIsIdentity(false);
    fieldID.setUnique(false);
    fieldID.setShouldAllowNull(false);
    table.addField(fieldID);
    
    return table;
}

public TableDefinition buildROOTA_BRANCHATable() {
    TableDefinition table = new TableDefinition();
    table.setName("ROOTA_BRANCHA");
    
    FieldDefinition fieldBRANCHA_ID = new FieldDefinition();
    fieldBRANCHA_ID.setName("branchAs_ID");
    fieldBRANCHA_ID.setTypeName("NUMBER");
    fieldBRANCHA_ID.setSize(0);
    fieldBRANCHA_ID.setSubSize(0);
    fieldBRANCHA_ID.setIsPrimaryKey(true);
    fieldBRANCHA_ID.setIsIdentity(false);
    fieldBRANCHA_ID.setUnique(false);
    fieldBRANCHA_ID.setShouldAllowNull(false);
    table.addField(fieldBRANCHA_ID);
    
    FieldDefinition fieldROOTA_ID = new FieldDefinition();
    fieldROOTA_ID.setName("RootA_ID");
    fieldROOTA_ID.setTypeName("NUMBER");
    fieldROOTA_ID.setSize(0);
    fieldROOTA_ID.setSubSize(0);
    fieldROOTA_ID.setIsPrimaryKey(true);
    fieldROOTA_ID.setIsIdentity(false);
    fieldROOTA_ID.setUnique(false);
    fieldROOTA_ID.setShouldAllowNull(false);
    table.addField(fieldROOTA_ID);
    
    ForeignKeyConstraint foreignKeyROOTA_BRANCHA_BRANCHA = new ForeignKeyConstraint();
    foreignKeyROOTA_BRANCHA_BRANCHA.setName("ROOTA_BRANCHA_BRANCHA");
    foreignKeyROOTA_BRANCHA_BRANCHA.setTargetTable("BRANCHA");
    foreignKeyROOTA_BRANCHA_BRANCHA.addSourceField("branchAs_ID");
    foreignKeyROOTA_BRANCHA_BRANCHA.addTargetField("ID");
    table.addForeignKeyConstraint(foreignKeyROOTA_BRANCHA_BRANCHA);
    
    ForeignKeyConstraint foreignKeyROOTA_BRANCHA_ROOTA = new ForeignKeyConstraint();
    foreignKeyROOTA_BRANCHA_ROOTA.setName("ROOTA_BRANCHA_ROOTA");
    foreignKeyROOTA_BRANCHA_ROOTA.setTargetTable("ROOTA");
    foreignKeyROOTA_BRANCHA_ROOTA.addSourceField("RootA_ID");
    foreignKeyROOTA_BRANCHA_ROOTA.addTargetField("ID");
    table.addForeignKeyConstraint(foreignKeyROOTA_BRANCHA_ROOTA);

    return table;
}

public TableDefinition buildROOTA_CDTable() {
    TableDefinition table = new TableDefinition();
    table.setName("ROOTA");
    
    FieldDefinition fieldID = new FieldDefinition();
    fieldID.setName("ID");
    fieldID.setTypeName("NUMBER");
    fieldID.setSize(0);
    fieldID.setSubSize(0);
    fieldID.setIsPrimaryKey(true);
    fieldID.setIsIdentity(false);
    fieldID.setUnique(false);
    fieldID.setShouldAllowNull(false);
    table.addField(fieldID);
    
    FieldDefinition fieldBRANCHB = new FieldDefinition();
    fieldBRANCHB.setName("BRANCHB_ID");
    fieldBRANCHB.setTypeName("NUMBER");
    fieldBRANCHB.setSize(0);
    fieldBRANCHB.setSubSize(0);
    fieldBRANCHB.setIsPrimaryKey(false);
    fieldBRANCHB.setIsIdentity(false);
    fieldBRANCHB.setUnique(false);
    fieldBRANCHB.setShouldAllowNull(true);
    table.addField(fieldBRANCHB);
    
    return table;
}

}
