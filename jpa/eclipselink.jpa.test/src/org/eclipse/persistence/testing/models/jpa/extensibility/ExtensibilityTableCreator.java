/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     tware - initial implementation as part of extensibility feature
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.extensibility;

import org.eclipse.persistence.testing.framework.TogglingFastTableCreator;
import org.eclipse.persistence.testing.models.jpa.relationships.RelationshipsTableManager;
import org.eclipse.persistence.tools.schemaframework.FieldDefinition;
import org.eclipse.persistence.tools.schemaframework.TableCreator;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

public class ExtensibilityTableCreator extends TogglingFastTableCreator {

    public static TableCreator tableCreator;

    public ExtensibilityTableCreator() {
        setName("Extensibility");
        addTableDefinition(buildEXTENS_EMPTable());
        addTableDefinition(buildEXTENS_PHONETable());
        addTableDefinition(buildEXTENS_ADDRTable());
        addTableDefinition(buildEXTENS_JOIN_TABLETable());
    }
        
    public static TableCreator getCreator(){
        if (RelationshipsTableManager.tableCreator == null){
            ExtensibilityTableCreator.tableCreator = new ExtensibilityTableCreator();
        }
        return ExtensibilityTableCreator.tableCreator;
    }
    
    public static TableDefinition buildEXTENS_EMPTable() {
        TableDefinition table = new TableDefinition();
        table.setName("EXTENS_EMP");
        
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(true);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(false);
        table.addField(fieldID);
        
        FieldDefinition fieldFIRST_NAME = new FieldDefinition();
        fieldFIRST_NAME.setName("FIRSTNAME");
        fieldFIRST_NAME.setTypeName("VARCHAR2");
        fieldFIRST_NAME.setSize(80);
        fieldFIRST_NAME.setSubSize(0);
        fieldFIRST_NAME.setIsPrimaryKey(false);
        fieldFIRST_NAME.setIsIdentity(false);
        fieldFIRST_NAME.setUnique(false);
        fieldFIRST_NAME.setShouldAllowNull(true);
        table.addField(fieldFIRST_NAME);
        
        FieldDefinition fieldLAST_NAME = new FieldDefinition();
        fieldLAST_NAME.setName("LASTNAME");
        fieldLAST_NAME.setTypeName("VARCHAR2");
        fieldLAST_NAME.setSize(80);
        fieldLAST_NAME.setSubSize(0);
        fieldLAST_NAME.setIsPrimaryKey(false);
        fieldLAST_NAME.setIsIdentity(false);
        fieldLAST_NAME.setUnique(false);
        fieldLAST_NAME.setShouldAllowNull(true);
        table.addField(fieldLAST_NAME);
        
        FieldDefinition fieldFLEX1 = new FieldDefinition();
        fieldFLEX1.setName("FLEX1");
        fieldFLEX1.setTypeName("VARCHAR2");
        fieldFLEX1.setSize(80);
        fieldFLEX1.setSubSize(0);
        fieldFLEX1.setIsPrimaryKey(false);
        fieldFLEX1.setIsIdentity(false);
        fieldFLEX1.setUnique(false);
        fieldFLEX1.setShouldAllowNull(true);
        table.addField(fieldFLEX1);
        
        FieldDefinition fieldFLEX2 = new FieldDefinition();
        fieldFLEX2.setName("FLEX2");
        fieldFLEX2.setTypeName("VARCHAR2");
        fieldFLEX2.setSize(80);
        fieldFLEX2.setSubSize(0);
        fieldFLEX2.setIsPrimaryKey(false);
        fieldFLEX2.setIsIdentity(false);
        fieldFLEX2.setUnique(false);
        fieldFLEX2.setShouldAllowNull(true);
        table.addField(fieldFLEX2);
        
        FieldDefinition fieldFLEX3 = new FieldDefinition();
        fieldFLEX3.setName("FLEX3");
        fieldFLEX3.setTypeName("VARCHAR2");
        fieldFLEX3.setSize(80);
        fieldFLEX3.setSubSize(0);
        fieldFLEX3.setIsPrimaryKey(false);
        fieldFLEX3.setIsIdentity(false);
        fieldFLEX3.setUnique(false);
        fieldFLEX3.setShouldAllowNull(true);
        table.addField(fieldFLEX3);
        
        FieldDefinition fieldADDRESS_ID = new FieldDefinition();
        fieldADDRESS_ID.setName("ADDRESS_ID");
        fieldADDRESS_ID.setTypeName("NUMERIC");
        fieldADDRESS_ID.setSize(15);
        fieldADDRESS_ID.setShouldAllowNull(true);
        fieldADDRESS_ID.setIsPrimaryKey(false);
        fieldADDRESS_ID.setUnique(false);
        fieldADDRESS_ID.setIsIdentity(false);
        fieldADDRESS_ID.setForeignKeyFieldName("EXTENS_ADDR.ID");
        table.addField(fieldADDRESS_ID);
        
        FieldDefinition fieldVERSION = new FieldDefinition();
        fieldVERSION.setName("VERSION");
        fieldVERSION.setTypeName("NUMERIC");
        fieldVERSION.setSize(15);
        fieldVERSION.setShouldAllowNull(true);
        fieldVERSION.setIsPrimaryKey(false);
        fieldVERSION.setUnique(false);
        fieldVERSION.setIsIdentity(false);
        table.addField(fieldVERSION);
        
        return table;
    }
    
    public static TableDefinition buildEXTENS_ADDRTable() {
        TableDefinition table = new TableDefinition();
        table.setName("EXTENS_ADDR");
        
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(true);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(false);
        table.addField(fieldID);
        
        FieldDefinition fieldSTREET = new FieldDefinition();
        fieldSTREET.setName("STREET");
        fieldSTREET.setTypeName("VARCHAR2");
        fieldSTREET.setSize(80);
        fieldSTREET.setSubSize(0);
        fieldSTREET.setIsPrimaryKey(false);
        fieldSTREET.setIsIdentity(false);
        fieldSTREET.setUnique(false);
        fieldSTREET.setShouldAllowNull(true);
        table.addField(fieldSTREET);
        
        FieldDefinition fieldCOUNTRY = new FieldDefinition();
        fieldCOUNTRY.setName("COUNTRY");
        fieldCOUNTRY.setTypeName("VARCHAR2");
        fieldCOUNTRY.setSize(80);
        fieldCOUNTRY.setSubSize(0);
        fieldCOUNTRY.setIsPrimaryKey(false);
        fieldCOUNTRY.setIsIdentity(false);
        fieldCOUNTRY.setUnique(false);
        fieldCOUNTRY.setShouldAllowNull(true);
        table.addField(fieldCOUNTRY);
        
        FieldDefinition fieldPOSTALCODE = new FieldDefinition();
        fieldPOSTALCODE.setName("POSTALCODE");
        fieldPOSTALCODE.setTypeName("VARCHAR2");
        fieldPOSTALCODE.setSize(80);
        fieldPOSTALCODE.setSubSize(0);
        fieldPOSTALCODE.setIsPrimaryKey(false);
        fieldPOSTALCODE.setIsIdentity(false);
        fieldPOSTALCODE.setUnique(false);
        fieldPOSTALCODE.setShouldAllowNull(true);
        table.addField(fieldPOSTALCODE);
        
        FieldDefinition fieldCITY = new FieldDefinition();
        fieldCITY.setName("CITY");
        fieldCITY.setTypeName("VARCHAR2");
        fieldCITY.setSize(80);
        fieldCITY.setSubSize(0);
        fieldCITY.setIsPrimaryKey(false);
        fieldCITY.setIsIdentity(false);
        fieldCITY.setUnique(false);
        fieldCITY.setShouldAllowNull(true);
        table.addField(fieldCITY);
        
        FieldDefinition fieldFLEX1 = new FieldDefinition();
        fieldFLEX1.setName("FLEX1");
        fieldFLEX1.setTypeName("VARCHAR2");
        fieldFLEX1.setSize(80);
        fieldFLEX1.setSubSize(0);
        fieldFLEX1.setIsPrimaryKey(false);
        fieldFLEX1.setIsIdentity(false);
        fieldFLEX1.setUnique(false);
        fieldFLEX1.setShouldAllowNull(true);
        table.addField(fieldFLEX1);
        
        FieldDefinition fieldFLEX2 = new FieldDefinition();
        fieldFLEX2.setName("FLEX2");
        fieldFLEX2.setTypeName("VARCHAR2");
        fieldFLEX2.setSize(80);
        fieldFLEX2.setSubSize(0);
        fieldFLEX2.setIsPrimaryKey(false);
        fieldFLEX2.setIsIdentity(false);
        fieldFLEX2.setUnique(false);
        fieldFLEX2.setShouldAllowNull(true);
        table.addField(fieldFLEX2);
        
        FieldDefinition fieldFLEX3 = new FieldDefinition();
        fieldFLEX3.setName("FLEX3");
        fieldFLEX3.setTypeName("VARCHAR2");
        fieldFLEX3.setSize(80);
        fieldFLEX3.setSubSize(0);
        fieldFLEX3.setIsPrimaryKey(false);
        fieldFLEX3.setIsIdentity(false);
        fieldFLEX3.setUnique(false);
        fieldFLEX3.setShouldAllowNull(true);
        table.addField(fieldFLEX3);
        
        FieldDefinition fieldVERSION = new FieldDefinition();
        fieldVERSION.setName("VERSION");
        fieldVERSION.setTypeName("NUMERIC");
        fieldVERSION.setSize(15);
        fieldVERSION.setShouldAllowNull(true);
        fieldVERSION.setIsPrimaryKey(false);
        fieldVERSION.setUnique(false);
        fieldVERSION.setIsIdentity(false);
        table.addField(fieldVERSION);
        
        return table;
    }
    
    public static TableDefinition buildEXTENS_PHONETable() {
        TableDefinition table = new TableDefinition();
        table.setName("EXTENS_PHONE");
        
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(true);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(false);
        table.addField(fieldID);
        
        FieldDefinition fieldSTREET = new FieldDefinition();
        fieldSTREET.setName("TYPE");
        fieldSTREET.setTypeName("VARCHAR2");
        fieldSTREET.setSize(80);
        fieldSTREET.setSubSize(0);
        fieldSTREET.setIsPrimaryKey(false);
        fieldSTREET.setIsIdentity(false);
        fieldSTREET.setUnique(false);
        fieldSTREET.setShouldAllowNull(true);
        table.addField(fieldSTREET);
        
        FieldDefinition fieldCOUNTRY = new FieldDefinition();
        fieldCOUNTRY.setName("NUMB");
        fieldCOUNTRY.setTypeName("VARCHAR2");
        fieldCOUNTRY.setSize(10);
        fieldCOUNTRY.setSubSize(0);
        fieldCOUNTRY.setIsPrimaryKey(false);
        fieldCOUNTRY.setIsIdentity(false);
        fieldCOUNTRY.setUnique(false);
        fieldCOUNTRY.setShouldAllowNull(true);
        table.addField(fieldCOUNTRY);
        
        FieldDefinition fieldPOSTALCODE = new FieldDefinition();
        fieldPOSTALCODE.setName("AREA_CODE");
        fieldPOSTALCODE.setTypeName("VARCHAR2");
        fieldPOSTALCODE.setSize(10);
        fieldPOSTALCODE.setSubSize(0);
        fieldPOSTALCODE.setIsPrimaryKey(false);
        fieldPOSTALCODE.setIsIdentity(false);
        fieldPOSTALCODE.setUnique(false);
        fieldPOSTALCODE.setShouldAllowNull(true);
        table.addField(fieldPOSTALCODE);
        
        FieldDefinition fieldFLEX1 = new FieldDefinition();
        fieldFLEX1.setName("FLEX1");
        fieldFLEX1.setTypeName("VARCHAR2");
        fieldFLEX1.setSize(80);
        fieldFLEX1.setSubSize(0);
        fieldFLEX1.setIsPrimaryKey(false);
        fieldFLEX1.setIsIdentity(false);
        fieldFLEX1.setUnique(false);
        fieldFLEX1.setShouldAllowNull(true);
        table.addField(fieldFLEX1);
        
        FieldDefinition fieldFLEX2 = new FieldDefinition();
        fieldFLEX2.setName("FLEX2");
        fieldFLEX2.setTypeName("VARCHAR2");
        fieldFLEX2.setSize(80);
        fieldFLEX2.setSubSize(0);
        fieldFLEX2.setIsPrimaryKey(false);
        fieldFLEX2.setIsIdentity(false);
        fieldFLEX2.setUnique(false);
        fieldFLEX2.setShouldAllowNull(true);
        table.addField(fieldFLEX2);
        
        FieldDefinition fieldFLEX3 = new FieldDefinition();
        fieldFLEX3.setName("FLEX3");
        fieldFLEX3.setTypeName("VARCHAR2");
        fieldFLEX3.setSize(80);
        fieldFLEX3.setSubSize(0);
        fieldFLEX3.setIsPrimaryKey(false);
        fieldFLEX3.setIsIdentity(false);
        fieldFLEX3.setUnique(false);
        fieldFLEX3.setShouldAllowNull(true);
        table.addField(fieldFLEX3);
        
        FieldDefinition fieldVERSION = new FieldDefinition();
        fieldVERSION.setName("VERSION");
        fieldVERSION.setTypeName("NUMERIC");
        fieldVERSION.setSize(15);
        fieldVERSION.setShouldAllowNull(true);
        fieldVERSION.setIsPrimaryKey(false);
        fieldVERSION.setUnique(false);
        fieldVERSION.setIsIdentity(false);
        table.addField(fieldVERSION);
        
        return table;
    }
    
    public static TableDefinition buildEXTENS_JOIN_TABLETable() {
        TableDefinition table = new TableDefinition();
        table.setName("EXTENS_JOIN_TABLE");
        
        FieldDefinition fieldID1 = new FieldDefinition();
        fieldID1.setName("ID1");
        fieldID1.setTypeName("NUMERIC");
        fieldID1.setSize(15);
        fieldID1.setShouldAllowNull(false);
        fieldID1.setIsPrimaryKey(true);
        fieldID1.setUnique(false);
        fieldID1.setIsIdentity(false);
        table.addField(fieldID1);
        
        FieldDefinition fieldID2 = new FieldDefinition();
        fieldID2.setName("ID2");
        fieldID2.setTypeName("NUMERIC");
        fieldID2.setSize(15);
        fieldID2.setShouldAllowNull(false);
        fieldID2.setIsPrimaryKey(true);
        fieldID2.setUnique(false);
        fieldID2.setIsIdentity(false);
        table.addField(fieldID2);
        
        return table;
    }

}
