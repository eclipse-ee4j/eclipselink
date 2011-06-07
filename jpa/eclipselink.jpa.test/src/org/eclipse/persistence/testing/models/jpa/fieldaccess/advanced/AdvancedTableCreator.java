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
 *     05/03/2009-1.2.1 Guy Pelletier 
 *       - 307547:  Exception in order by clause after migrating to eclipselink 1.2 release
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.fieldaccess.advanced;

import org.eclipse.persistence.testing.framework.TogglingFastTableCreator;
import org.eclipse.persistence.tools.schemaframework.*;

public class AdvancedTableCreator extends TogglingFastTableCreator {

    public AdvancedTableCreator() {
        setName("EJB3EmployeeProject");

        addTableDefinition(buildADDRESSTable());
        addTableDefinition(buildBUYERTable());
        addTableDefinition(buildCREDITCARDSTable());
        addTableDefinition(buildDEPTTable());
        addTableDefinition(buildDEPT_EMPTable());
        addTableDefinition(buildEQUIPMENTTable());
        addTableDefinition(buildEQUIPMENTCODETable());
        addTableDefinition(buildGOLFERTable());
        addTableDefinition(buildLARGEPROJECTTable());
        addTableDefinition(buildPARTNERLINKTable());
        addTableDefinition(buildMANTable());
        addTableDefinition(buildPHONENUMBERTable());
        addTableDefinition(buildPHONENUMBERSTATUSTable());
        addTableDefinition(buildPLATINUMBUYERTable());
        addTableDefinition(buildPROJECT_EMPTable());
        addTableDefinition(buildPROJECTTable());
        addTableDefinition(buildRESPONSTable());
        addTableDefinition(buildSALARYTable());
        addTableDefinition(buildVEGETABLETable());
        addTableDefinition(buildWOMANTable());
        addTableDefinition(buildWORKWEEKTable());
        addTableDefinition(buildWORLDRANKTable());
        addTableDefinition(buildPARENTTable());
        addTableDefinition(buildCHILDTable());
        addTableDefinition(buildEMPLOYEETable());
    }
    
    public static TableDefinition buildADDRESSTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_FA_ADDRESS");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ADDRESS_ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setSubSize(0);
        fieldID.setIsPrimaryKey(true);
        fieldID.setIsIdentity(true);
        fieldID.setUnique(false);
        fieldID.setShouldAllowNull(false);
        table.addField(fieldID);

        FieldDefinition fieldSTREET = new FieldDefinition();
        fieldSTREET.setName("STREET");
        fieldSTREET.setTypeName("VARCHAR2");
        fieldSTREET.setSize(60);
        fieldSTREET.setSubSize(0);
        fieldSTREET.setIsPrimaryKey(false);
        fieldSTREET.setIsIdentity(false);
        fieldSTREET.setUnique(false);
        fieldSTREET.setShouldAllowNull(true);
        table.addField(fieldSTREET);

        FieldDefinition fieldCITY = new FieldDefinition();
        fieldCITY.setName("CITY");
        fieldCITY.setTypeName("VARCHAR2");
        fieldCITY.setSize(60);
        fieldCITY.setSubSize(0);
        fieldCITY.setIsPrimaryKey(false);
        fieldCITY.setIsIdentity(false);
        fieldCITY.setUnique(false);
        fieldCITY.setShouldAllowNull(true);
        table.addField(fieldCITY);

        FieldDefinition fieldPROVINCE = new FieldDefinition();
        fieldPROVINCE.setName("PROVINCE");
        fieldPROVINCE.setTypeName("VARCHAR2");
        fieldPROVINCE.setSize(60);
        fieldPROVINCE.setSubSize(0);
        fieldPROVINCE.setIsPrimaryKey(false);
        fieldPROVINCE.setIsIdentity(false);
        fieldPROVINCE.setUnique(false);
        fieldPROVINCE.setShouldAllowNull(true);
        table.addField(fieldPROVINCE);

        FieldDefinition fieldPOSTALCODE = new FieldDefinition();
        fieldPOSTALCODE.setName("P_CODE");
        fieldPOSTALCODE.setTypeName("VARCHAR2");
        fieldPOSTALCODE.setSize(67);
        fieldPOSTALCODE.setSubSize(0);
        fieldPOSTALCODE.setIsPrimaryKey(false);
        fieldPOSTALCODE.setIsIdentity(false);
        fieldPOSTALCODE.setUnique(false);
        fieldPOSTALCODE.setShouldAllowNull(true);
        table.addField(fieldPOSTALCODE);

        FieldDefinition fieldCOUNTRY = new FieldDefinition();
        fieldCOUNTRY.setName("COUNTRY");
        fieldCOUNTRY.setTypeName("VARCHAR2");
        fieldCOUNTRY.setSize(60);
        fieldCOUNTRY.setSubSize(0);
        fieldCOUNTRY.setIsPrimaryKey(false);
        fieldCOUNTRY.setIsIdentity(false);
        fieldCOUNTRY.setUnique(false);
        fieldCOUNTRY.setShouldAllowNull(true);
        table.addField(fieldCOUNTRY);

        return table;
    }
    
     public static TableDefinition buildBUYERTable() {
        TableDefinition table = new TableDefinition();

        table.setName("CMP3_FA_BUYER");
        
        String[] unq1 = {"BUYER_ID", "BUYER_NAME"};
        String[] unq2 = {"BUYER_ID", "DESCRIP"};
        table.addUniqueKeyConstraint("UNQ_CMP3_FA_BUY_1", unq1);
        table.addUniqueKeyConstraint("UNQ_CMP3_FA_BUY_2", unq2);

        FieldDefinition field = new FieldDefinition();
        field.setName("BUYER_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false );
        field.setIsPrimaryKey(true );
        field.setUnique(false );
        field.setIsIdentity(true );
        table.addField(field);
    
        FieldDefinition field2 = new FieldDefinition();
        field2.setName("BUYER_NAME");
        field2.setTypeName("VARCHAR");
        field2.setSize(30);
        field2.setShouldAllowNull(false);
        field2.setIsPrimaryKey(false );
        field2.setUnique(false );
        field2.setIsIdentity(false );
        table.addField(field2);
        
        FieldDefinition fieldGender = new FieldDefinition();
        fieldGender.setName("GENDER");
        fieldGender.setTypeName("VARCHAR");
        fieldGender.setSize(1);
        fieldGender.setShouldAllowNull(true);
        fieldGender.setIsPrimaryKey(false);
        fieldGender.setUnique(false);
        fieldGender.setIsIdentity(false);
        table.addField(fieldGender);
    
        FieldDefinition field3 = new FieldDefinition();
        field3.setName("DESCRIP");
        field3.setTypeName("VARCHAR");
        field3.setSize(200);
        field3.setShouldAllowNull(false);
        field3.setIsPrimaryKey(false );
        field3.setUnique(false );
        field3.setIsIdentity(false );
        table.addField(field3);
    
        FieldDefinition field4 = new FieldDefinition();
        field4.setName("DTYPE");
        field4.setTypeName("VARCHAR");
        field4.setSize(200);
        field4.setShouldAllowNull(true );
        field4.setIsPrimaryKey(false );
        field4.setUnique(false );
        field4.setIsIdentity(false );
        table.addField(field4);
    
        FieldDefinition field5 = new FieldDefinition();
        field5.setName("VERSION");
        field5.setTypeName("NUMERIC");
        field5.setSize(15);
        field5.setShouldAllowNull(true );
        field5.setIsPrimaryKey(false );
        field5.setUnique(false );
        field5.setIsIdentity(false );
        table.addField(field5);
        
        FieldDefinition fieldBUYINGDAYS = new FieldDefinition();
        fieldBUYINGDAYS.setName("BUY_DAYS");
        fieldBUYINGDAYS.setTypeName("LONG RAW");
        fieldBUYINGDAYS.setSize(1000);
        fieldBUYINGDAYS.setSubSize(0);
        fieldBUYINGDAYS.setIsPrimaryKey(false);
        fieldBUYINGDAYS.setIsIdentity(false);
        fieldBUYINGDAYS.setUnique(false);
        fieldBUYINGDAYS.setShouldAllowNull(true);
        table.addField(fieldBUYINGDAYS);

        return table;
     }
    
     public static TableDefinition buildCHILDTable() {
         TableDefinition table = new TableDefinition();
         table.setName("FIELD_CHILD");

         FieldDefinition fieldID = new FieldDefinition();
         fieldID.setName("ID");
         fieldID.setTypeName("NUMERIC");
         fieldID.setSize(15);
         fieldID.setSubSize(0);
         fieldID.setIsPrimaryKey(true);
         fieldID.setIsIdentity(true);
         fieldID.setUnique(false);
         fieldID.setShouldAllowNull(false);
         table.addField(fieldID);

         FieldDefinition fieldVERSION = new FieldDefinition();
         fieldVERSION.setName("VERSION");
         fieldVERSION.setTypeName("NUMERIC");
         fieldVERSION.setSize(15);
         fieldVERSION.setShouldAllowNull(true);
         fieldVERSION.setIsPrimaryKey(false);
         fieldVERSION.setUnique(false);
         fieldVERSION.setIsIdentity(false);
         table.addField(fieldVERSION);
         
         FieldDefinition fieldCREATEDON = new FieldDefinition();
         fieldCREATEDON.setName("CREATEDON");
         fieldCREATEDON.setTypeName("DATETIME");
         fieldCREATEDON.setSize(23);
         fieldCREATEDON.setIsPrimaryKey(false);
         fieldCREATEDON.setUnique(false);
         fieldCREATEDON.setIsIdentity(false);
         fieldCREATEDON.setShouldAllowNull(true);
         table.addField(fieldCREATEDON);
         
         FieldDefinition fieldPARENTID = new FieldDefinition();
         fieldPARENTID.setName("PARENT_ID");
         fieldPARENTID.setTypeName("NUMERIC");
         fieldPARENTID.setSize(15);
         fieldPARENTID.setShouldAllowNull(false);
         fieldPARENTID.setIsPrimaryKey(false);
         fieldPARENTID.setUnique(false);
         fieldPARENTID.setIsIdentity(false);
         fieldPARENTID.setForeignKeyFieldName("FIELD_PARENT.ID");
         table.addField(fieldPARENTID);
         
         return table;
     }
     
     public static TableDefinition buildCREDITCARDSTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_FA_Buyer_CREDITCARDS");
    
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("BUYER_ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(false);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(false);
        fieldID.setForeignKeyFieldName("CMP3_FA_BUYER.BUYER_ID");
        table.addField(fieldID);
    
        FieldDefinition fieldCARD = new FieldDefinition();
        fieldCARD.setName("CARD");
        fieldCARD.setTypeName("VARCHAR");
        fieldCARD.setSize(2);
        fieldCARD.setShouldAllowNull(false);
        fieldCARD.setIsPrimaryKey(false);
        fieldCARD.setUnique(true);
        fieldCARD.setIsIdentity(false);
        table.addField(fieldCARD);
        
        FieldDefinition fieldNUMB = new FieldDefinition();
        fieldNUMB.setName("NUMB");
        fieldNUMB.setTypeName("VARCHAR");
        fieldNUMB.setSize(10);
        fieldNUMB.setShouldAllowNull(false);
        fieldNUMB.setIsPrimaryKey(false);
        fieldNUMB.setUnique(false);
        fieldNUMB.setIsIdentity(false);
        table.addField(fieldNUMB);
    
        return table;
    }
    
     public static TableDefinition buildDEPTTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_FA_DEPT");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setSubSize(0);
        fieldID.setIsPrimaryKey(true);
        fieldID.setIsIdentity(true);
        fieldID.setUnique(false);
        fieldID.setShouldAllowNull(false);
        table.addField(fieldID);

        FieldDefinition fieldNAME = new FieldDefinition();
        fieldNAME.setName("NAME");
        fieldNAME.setTypeName("VARCHAR2");
        fieldNAME.setSize(60);
        fieldNAME.setSubSize(0);
        fieldNAME.setIsPrimaryKey(false);
        fieldNAME.setIsIdentity(false);
        fieldNAME.setUnique(false);
        fieldNAME.setShouldAllowNull(true);
        table.addField(fieldNAME);

        return table;
    }
    
    public static TableDefinition buildDEPT_EMPTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_FA_DEPT_CMP3_FA_EMPLOYEE");

        // SECTION: FIELD
        FieldDefinition fieldID = new FieldDefinition();
        //fieldID.setName("ADV_DEPT_ID");
		fieldID.setName("Department_ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(true);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(false);
        fieldID.setForeignKeyFieldName("CMP3_FA_DEPT.ID");
        table.addField(fieldID);
        
        // SECTION: FIELD
        FieldDefinition fieldEMP = new FieldDefinition();
        fieldEMP.setName("managers_EMP_ID");
        fieldEMP.setTypeName("NUMERIC");
        fieldEMP.setSize(15);
        fieldEMP.setShouldAllowNull(false);
        fieldEMP.setIsPrimaryKey(true);
        fieldEMP.setUnique(false);
        fieldEMP.setIsIdentity(false);
        fieldEMP.setForeignKeyFieldName("CMP3_FA_EMPLOYEE.EMP_ID");
        table.addField(fieldEMP);
        
        return table;   
    }
    
    public static TableDefinition buildEMPLOYEETable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_FA_EMPLOYEE");
    
        FieldDefinition field = new FieldDefinition();
        field.setName("EMP_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setIsIdentity(true);
        table.addField(field);
    
        FieldDefinition field1 = new FieldDefinition();
        field1.setName("F_NAME");
        field1.setTypeName("VARCHAR");
        field1.setSize(40);
        table.addField(field1);
    
        FieldDefinition field2 = new FieldDefinition();
        field2.setName("L_NAME");
        field2.setTypeName("VARCHAR");
        field2.setSize(40);
        table.addField(field2);
        
        FieldDefinition fieldGender = new FieldDefinition();
        fieldGender.setName("GENDER");
        fieldGender.setTypeName("VARCHAR");
        fieldGender.setSize(1);
        table.addField(fieldGender);
    
        FieldDefinition field3 = new FieldDefinition();
        field3.setName("START_DATE");
        field3.setTypeName("DATE");
        field3.setSize(23);
        table.addField(field3);
    
        FieldDefinition field4 = new FieldDefinition();
        field4.setName("END_DATE");
        field4.setTypeName("DATE");
        field4.setSize(23);
        table.addField(field4);
    
        FieldDefinition field8 = new FieldDefinition();
        field8.setName("ADDR_ID");
        field8.setTypeName("NUMERIC");
        field8.setSize(15);
        field8.setForeignKeyFieldName("CMP3_FA_ADDRESS.ADDRESS_ID");
        table.addField(field8);
    
        FieldDefinition field9 = new FieldDefinition();
        field9.setName("MANAGER_EMP_ID");
        field9.setTypeName("NUMERIC");
        field9.setSize(15);
        field9.setForeignKeyFieldName("CMP3_FA_EMPLOYEE.EMP_ID");
        table.addField(field9);
    
        FieldDefinition field10 = new FieldDefinition();
        field10.setName("VERSION");
        field10.setTypeName("NUMERIC");
        field10.setSize(15);
        table.addField(field10);
        
        FieldDefinition fieldDEPT = new FieldDefinition();
        fieldDEPT.setName("DEPT_ID");
        fieldDEPT.setTypeName("NUMERIC");
        fieldDEPT.setSize(15);
        fieldDEPT.setForeignKeyFieldName("CMP3_FA_DEPT.ID");
        table.addField(fieldDEPT);
        
        FieldDefinition fieldStatus = new FieldDefinition();
        fieldStatus.setName("STATUS");
        fieldStatus.setTypeName("NUMERIC");
        fieldStatus.setSize(15);
        table.addField(fieldStatus);
    
        FieldDefinition fieldPayScale = new FieldDefinition();
        fieldPayScale.setName("PAY_SCALE");
        fieldPayScale.setTypeName("VARCHAR");
        fieldPayScale.setSize(40);
        table.addField(fieldPayScale);
        
        FieldDefinition fieldRoomNumber = new FieldDefinition();
        fieldRoomNumber.setName("ROOM_NUM");
        fieldRoomNumber.setTypeName("NUMBER");
        fieldRoomNumber.setSize(15);
        fieldRoomNumber.setSubSize(0);
        table.addField(fieldRoomNumber);
        
        // SECTION: FIELD
        FieldDefinition fieldFormerCompany = new FieldDefinition();
        fieldFormerCompany.setName("FORMER_COMPANY");
        fieldFormerCompany.setTypeName("VARCHAR");
        fieldFormerCompany.setSize(40);
        table.addField(fieldFormerCompany);
    
        // SECTION: FIELD
        FieldDefinition fieldFormerStartDate = new FieldDefinition();
        fieldFormerStartDate.setName("FORMER_START_DATE");
        fieldFormerStartDate.setTypeName("DATE");
        fieldFormerStartDate.setSize(23);
        table.addField(fieldFormerStartDate);
    
        // SECTION: FIELD
        FieldDefinition fieldFormerEndDate = new FieldDefinition();
        fieldFormerEndDate.setName("FORMER_END_DATE");
        fieldFormerEndDate.setTypeName("DATE");
        fieldFormerEndDate.setSize(23);
        table.addField(fieldFormerEndDate);

        FieldDefinition fieldISMANAGER = new FieldDefinition();
        fieldISMANAGER.setName("ISMANAGER");
        fieldISMANAGER.setTypeName("VARCHAR");
        fieldISMANAGER.setSize(5);
        table.addField(fieldISMANAGER);
        
        FieldDefinition fieldGETMANAGER = new FieldDefinition();
        fieldGETMANAGER.setName("GETMANAGER");
        fieldGETMANAGER.setTypeName("VARCHAR");
        fieldGETMANAGER.setSize(5);
        table.addField(fieldGETMANAGER);
        
        FieldDefinition fieldSETMANAGER = new FieldDefinition();
        fieldSETMANAGER.setName("SETMANAGER");
        fieldSETMANAGER.setTypeName("VARCHAR");
        fieldSETMANAGER.setSize(40);
        table.addField(fieldSETMANAGER);
        
/*        ForeignKeyConstraint foreignKeyEMPLOYEE_ADDRESS = new ForeignKeyConstraint();
        foreignKeyEMPLOYEE_ADDRESS.setName("EMPLOYEE_ADDRESS");
        foreignKeyEMPLOYEE_ADDRESS.setTargetTable("CMP3_FA_ADDRESS");
        foreignKeyEMPLOYEE_ADDRESS.addSourceField("ADDR_ID");
        foreignKeyEMPLOYEE_ADDRESS.addTargetField("ID");
        table.addForeignKeyConstraint(foreignKeyEMPLOYEE_ADDRESS);

        ForeignKeyConstraint foreignKeyEMPLOYEE_MANAGER = new ForeignKeyConstraint();
        foreignKeyEMPLOYEE_MANAGER.setName("EMPLOYEE_MANAGER");
        foreignKeyEMPLOYEE_MANAGER.setTargetTable("CMP3_FA_EMPLOYEE");
        foreignKeyEMPLOYEE_MANAGER.addSourceField("MANAGER_ID");
        foreignKeyEMPLOYEE_MANAGER.addTargetField("EMP_ID");
        table.addForeignKeyConstraint(foreignKeyEMPLOYEE_MANAGER);

 */       return table;
    }
    
    public static TableDefinition buildEQUIPMENTTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_FA_ADV_EQUIP");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setSubSize(0);
        fieldID.setIsPrimaryKey(true);
        fieldID.setIsIdentity(true);
        fieldID.setUnique(false);
        fieldID.setShouldAllowNull(false);
        table.addField(fieldID);

        FieldDefinition fieldNAME = new FieldDefinition();
        fieldNAME.setName("DESCRIP");
        fieldNAME.setTypeName("VARCHAR2");
        fieldNAME.setSize(100);
        fieldNAME.setSubSize(0);
        fieldNAME.setIsPrimaryKey(false);
        fieldNAME.setIsIdentity(false);
        fieldNAME.setUnique(false);
        fieldNAME.setShouldAllowNull(true);
        table.addField(fieldNAME);
        
        FieldDefinition fieldDEPTID = new FieldDefinition();
        fieldDEPTID.setName("DEPT_ID");
        fieldDEPTID.setTypeName("NUMERIC");
        fieldDEPTID.setSize(15);
        fieldDEPTID.setShouldAllowNull(true);
        fieldDEPTID.setIsPrimaryKey(false);
        fieldDEPTID.setUnique(false);
        fieldDEPTID.setIsIdentity(false);
        fieldDEPTID.setForeignKeyFieldName("CMP3_FA_DEPT.ID");
        table.addField(fieldDEPTID);
        
        FieldDefinition fieldCODEID = new FieldDefinition();
        fieldCODEID.setName("CODE_ID");
        fieldCODEID.setTypeName("NUMERIC");
        fieldCODEID.setSize(15);
        fieldCODEID.setShouldAllowNull(true);
        fieldCODEID.setIsPrimaryKey(false);
        fieldCODEID.setUnique(false);
        fieldCODEID.setIsIdentity(false);
        fieldCODEID.setForeignKeyFieldName("CMP3_FA_ADV_EQUIP_CODE.ID");
        table.addField(fieldCODEID);

        return table;
    }
    
    public static TableDefinition buildGOLFERTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_FA_GOLFER");
        
        FieldDefinition IDfield = new FieldDefinition();
        IDfield.setName("ID");
        IDfield.setTypeName("NUMERIC");
        IDfield.setSize(15);
        IDfield.setShouldAllowNull(false);
        IDfield.setIsPrimaryKey(true);
        IDfield.setUnique(false);
        IDfield.setIsIdentity(true);
        table.addField(IDfield);

        ForeignKeyConstraint foreignKeyGOLFER_WORLDRANK = new ForeignKeyConstraint();
        foreignKeyGOLFER_WORLDRANK.setName("WORLDRANK_ID_FA");
        foreignKeyGOLFER_WORLDRANK.setTargetTable("CMP3_FA_WORLDRANK"); 
        foreignKeyGOLFER_WORLDRANK.addSourceField("ID");
        foreignKeyGOLFER_WORLDRANK.addTargetField("ID");
        table.addForeignKeyConstraint(foreignKeyGOLFER_WORLDRANK);
        
        return table;
    }
    
    public static TableDefinition buildEQUIPMENTCODETable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_FA_ADV_EQUIP_CODE");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setSubSize(0);
        fieldID.setIsPrimaryKey(true);
        fieldID.setIsIdentity(true);
        fieldID.setUnique(false);
        fieldID.setShouldAllowNull(false);
        table.addField(fieldID);

        FieldDefinition fieldCODE = new FieldDefinition();
        fieldCODE.setName("CODE");
        fieldCODE.setTypeName("VARCHAR2");
        fieldCODE.setSize(1);
        fieldCODE.setSubSize(0);
        fieldCODE.setIsPrimaryKey(false);
        fieldCODE.setIsIdentity(false);
        fieldCODE.setUnique(false);
        fieldCODE.setShouldAllowNull(false);
        table.addField(fieldCODE);

        return table;
    }

    public static TableDefinition buildLARGEPROJECTTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_FA_LPROJECT");

        // SECTION: FIELD
        FieldDefinition field = new FieldDefinition();
        field.setName("PROJ_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false );
        field.setIsPrimaryKey(true );
        table.addField(field);
    
        // SECTION: FIELD
        FieldDefinition field1 = new FieldDefinition();
        field1.setName("BUDGET");
        field1.setTypeName("DOUBLE PRECIS");
        field1.setSize(18);
        table.addField(field1);
        
        ForeignKeyConstraint foreignKey = new ForeignKeyConstraint();
        foreignKey.setName("FA_LPROJ_PROJ_FK");
        foreignKey.setTargetTable("CMP3_FA_PROJECT");
        foreignKey.addSourceField("PROJ_ID");
        foreignKey.addTargetField("PROJ_ID");
        foreignKey.setShouldCascadeOnDelete(true);
        table.addForeignKeyConstraint(foreignKey);
    
        return table;
    }
    
    public static TableDefinition buildMANTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_FA_MAN");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setSubSize(0);
        fieldID.setIsPrimaryKey(true);
        fieldID.setIsIdentity(true);
        fieldID.setUnique(false);
        fieldID.setShouldAllowNull(false);
        table.addField(fieldID);
        
        FieldDefinition fieldNAME = new FieldDefinition();
        fieldNAME.setName("NAME");
        fieldNAME.setTypeName("VARCHAR2");
        fieldNAME.setSize(100);
        fieldNAME.setSubSize(0);
        fieldNAME.setIsPrimaryKey(false);
        fieldNAME.setIsIdentity(false);
        fieldNAME.setUnique(false);
        fieldNAME.setShouldAllowNull(true);
        table.addField(fieldNAME);
        
        return table;
    }
    
    public static TableDefinition buildPARENTTable() {
        TableDefinition table = new TableDefinition();
        table.setName("FIELD_PARENT");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setSubSize(0);
        fieldID.setIsPrimaryKey(true);
        fieldID.setIsIdentity(true);
        fieldID.setUnique(false);
        fieldID.setShouldAllowNull(false);
        table.addField(fieldID);
        
        FieldDefinition fieldVERSION = new FieldDefinition();
        fieldVERSION.setName("VERSION");
        fieldVERSION.setTypeName("NUMERIC");
        fieldVERSION.setSize(15);
        fieldVERSION.setShouldAllowNull(true);
        fieldVERSION.setIsPrimaryKey(false);
        fieldVERSION.setUnique(false);
        fieldVERSION.setIsIdentity(false);
        table.addField(fieldVERSION);

        FieldDefinition fieldSTREET = new FieldDefinition();
        fieldSTREET.setName("SERIALNUMBER");
        fieldSTREET.setTypeName("VARCHAR2");
        fieldSTREET.setSize(60);
        fieldSTREET.setSubSize(0);
        fieldSTREET.setIsPrimaryKey(false);
        fieldSTREET.setIsIdentity(false);
        fieldSTREET.setUnique(false);
        fieldSTREET.setShouldAllowNull(true);
        table.addField(fieldSTREET);

        return table;
    }
    
    public static TableDefinition buildPARTNERLINKTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_FA_MW");

        FieldDefinition fieldMID = new FieldDefinition();
        fieldMID.setName("M");
        fieldMID.setTypeName("NUMERIC");
        fieldMID.setSize(15);
        fieldMID.setSubSize(0);
        fieldMID.setIsPrimaryKey(false);
        fieldMID.setIsIdentity(false);
        fieldMID.setUnique(false);
        fieldMID.setShouldAllowNull(true);
        table.addField(fieldMID);
        
        FieldDefinition fieldWID = new FieldDefinition();
        fieldWID.setName("W");
        fieldWID.setTypeName("NUMERIC");
        fieldWID.setSize(15);
        fieldWID.setSubSize(0);
        fieldWID.setIsPrimaryKey(false);
        fieldWID.setIsIdentity(false);
        fieldWID.setUnique(false);
        fieldWID.setShouldAllowNull(true);
        table.addField(fieldWID);
        
        ForeignKeyConstraint foreignKey = new ForeignKeyConstraint();
        foreignKey.setName("FA_LINK_WN_FK");
        foreignKey.setTargetTable("CMP3_FA_WOMAN");
        foreignKey.addSourceField("W");
        foreignKey.addTargetField("ID");
        foreignKey.setShouldCascadeOnDelete(true);
        table.addForeignKeyConstraint(foreignKey);
        
        foreignKey = new ForeignKeyConstraint();
        foreignKey.setName("FA_LINK_MN_FK");
        foreignKey.setTargetTable("CMP3_FA_MAN");
        foreignKey.addSourceField("M");
        foreignKey.addTargetField("ID");
        foreignKey.setShouldCascadeOnDelete(true);
        table.addForeignKeyConstraint(foreignKey);
        
        return table;
    }
    
    public static TableDefinition buildPHONENUMBERSTATUSTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_FA_PHONE_STATUS");
    
        FieldDefinition fieldOWNERID = new FieldDefinition();
        fieldOWNERID.setName("OWNER_ID");
        fieldOWNERID.setTypeName("NUMERIC");
        fieldOWNERID.setSize(15);
        fieldOWNERID.setShouldAllowNull(false);
        fieldOWNERID.setIsPrimaryKey(false);
        fieldOWNERID.setUnique(false);
        fieldOWNERID.setIsIdentity(false);
        table.addField(fieldOWNERID);
        
        FieldDefinition fieldTYPE = new FieldDefinition();
        fieldTYPE.setName("TYPE");
        fieldTYPE.setTypeName("VARCHAR");
        fieldTYPE.setSize(15);
        fieldTYPE.setShouldAllowNull(false);
        fieldTYPE.setIsPrimaryKey(false);
        fieldTYPE.setUnique(false);
        fieldTYPE.setIsIdentity(false);
        table.addField(fieldTYPE);
    
        FieldDefinition fieldSTATUS = new FieldDefinition();
        fieldSTATUS.setName("STATUS");
        fieldSTATUS.setTypeName("VARCHAR");
        fieldSTATUS.setSize(20);
        fieldSTATUS.setShouldAllowNull(false);
        fieldSTATUS.setIsPrimaryKey(false);
        fieldSTATUS.setUnique(false);
        fieldSTATUS.setIsIdentity(false);
        table.addField(fieldSTATUS);
    
        return table;
    }
    
    public static TableDefinition buildPHONENUMBERTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_FA_PHONENUMBER");

        // SECTION: FIELD
        FieldDefinition field = new FieldDefinition();
        field.setName("OWNER_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false );
        field.setIsPrimaryKey(true );
        field.setUnique(false );
        field.setIsIdentity(false );
        table.addField(field);
    
        // SECTION: FIELD
        FieldDefinition field1 = new FieldDefinition();
        field1.setName("TYPE");
        field1.setTypeName("VARCHAR");
        field1.setSize(15);
        field1.setShouldAllowNull(false );
        field1.setIsPrimaryKey(true );
        field1.setUnique(false );
        field1.setIsIdentity(false );
        table.addField(field1);
    
        // SECTION: FIELD
        FieldDefinition field2 = new FieldDefinition();
        field2.setName("AREA_CODE");
        field2.setTypeName("VARCHAR");
        field2.setSize(3);
        field2.setShouldAllowNull(true );
        field2.setIsPrimaryKey(false );
        field2.setUnique(false );
        field2.setIsIdentity(false );
        table.addField(field2);
    
        // SECTION: FIELD
        FieldDefinition field3 = new FieldDefinition();
        field3.setName("NUMB");
        field3.setTypeName("VARCHAR");
        field3.setSize(8);
        field3.setShouldAllowNull(true );
        field3.setIsPrimaryKey(false );
        field3.setUnique(false );
        field3.setIsIdentity(false );
        table.addField(field3);

        ForeignKeyConstraint foreignKeyPHONE_OWNER = new ForeignKeyConstraint();
        foreignKeyPHONE_OWNER.setName("FA_PHONE_OW_FK");
        foreignKeyPHONE_OWNER.setTargetTable("CMP3_FA_EMPLOYEE");
        foreignKeyPHONE_OWNER.addSourceField("OWNER_ID");
        foreignKeyPHONE_OWNER.addTargetField("EMP_ID");
        foreignKeyPHONE_OWNER.setShouldCascadeOnDelete(true);
        table.addForeignKeyConstraint(foreignKeyPHONE_OWNER);

        return table;
    }
    
    public static TableDefinition buildPLATINUMBUYERTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_FA_PBUYER");

        // SECTION: FIELD
        FieldDefinition field = new FieldDefinition();
        field.setName("BUYER_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false );
        field.setIsPrimaryKey(true );
        field.setUnique(false );
        field.setIsIdentity(false );
        field.setForeignKeyFieldName("CMP3_FA_BUYER.BUYER_ID");
        table.addField(field);
    
        // SECTION: FIELD
        FieldDefinition field1 = new FieldDefinition();
        field1.setName("PURCHASES");
        field1.setTypeName("DOUBLE PRECIS");
        field1.setSize(18);
        field1.setShouldAllowNull(true );
        field1.setIsPrimaryKey(false );
        field1.setUnique(false );
        field1.setIsIdentity(false );
        table.addField(field1);
    
        return table;
    }
    
    public static TableDefinition buildPROJECT_EMPTable() {
        TableDefinition table = new TableDefinition();

        table.setName("CMP3_FA_EMP_PROJ");

        // SECTION: FIELD
        FieldDefinition field = new FieldDefinition();
        field.setName("EMPLOYEES_EMP_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false );
        field.setIsPrimaryKey(true );
        field.setUnique(false );
        field.setIsIdentity(false );
        table.addField(field);
    
        // SECTION: FIELD
        FieldDefinition field1 = new FieldDefinition();
        field1.setName("projects_PROJ_ID");
        field1.setTypeName("NUMERIC");
        field1.setSize(15);
        field1.setShouldAllowNull(false );
        field1.setIsPrimaryKey(true );
        field1.setUnique(false );
        field1.setIsIdentity(false );
        table.addField(field1);

        ForeignKeyConstraint foreignKeyPROJECT_EMPLOYEE = new ForeignKeyConstraint();
        foreignKeyPROJECT_EMPLOYEE.setName("FA_PROJ_EMP_FK");
        foreignKeyPROJECT_EMPLOYEE.setTargetTable("CMP3_FA_EMPLOYEE");
        foreignKeyPROJECT_EMPLOYEE.addSourceField("EMPLOYEES_EMP_ID");
        foreignKeyPROJECT_EMPLOYEE.addTargetField("EMP_ID");
        foreignKeyPROJECT_EMPLOYEE.setShouldCascadeOnDelete(true);
        table.addForeignKeyConstraint(foreignKeyPROJECT_EMPLOYEE);

        ForeignKeyConstraint foreignKeyEMPLOYEE_PROJECT = new ForeignKeyConstraint();
        foreignKeyEMPLOYEE_PROJECT.setName("FA_EMP_PROJP_FK");
        foreignKeyEMPLOYEE_PROJECT.setTargetTable("CMP3_FA_PROJECT");
        foreignKeyEMPLOYEE_PROJECT.addSourceField("projects_PROJ_ID");
        foreignKeyEMPLOYEE_PROJECT.addTargetField("PROJ_ID");
        table.addForeignKeyConstraint(foreignKeyEMPLOYEE_PROJECT);

        return table;
    }
    
    public static TableDefinition buildPROJECTTable() {
        TableDefinition table = new TableDefinition();

        table.setName("CMP3_FA_PROJECT");

        // SECTION: FIELD
        FieldDefinition field = new FieldDefinition();
        field.setName("PROJ_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false );
        field.setIsPrimaryKey(true );
        field.setUnique(false );
        field.setIsIdentity(true );
        table.addField(field);
    
        // SECTION: FIELD
        FieldDefinition field1 = new FieldDefinition();
        field1.setName("PROJ_TYPE");
        field1.setTypeName("VARCHAR");
        field1.setSize(1);
        field1.setShouldAllowNull(true );
        field1.setIsPrimaryKey(false );
        field1.setUnique(false );
        field1.setIsIdentity(false );
        table.addField(field1);
    
        // SECTION: FIELD
        FieldDefinition field2 = new FieldDefinition();
        field2.setName("PROJ_NAME");
        field2.setTypeName("VARCHAR");
        field2.setSize(30);
        field2.setShouldAllowNull(true );
        field2.setIsPrimaryKey(false );
        field2.setUnique(false );
        field2.setIsIdentity(false );
        table.addField(field2);
    
        // SECTION: FIELD
        FieldDefinition field3 = new FieldDefinition();
        field3.setName("DESCRIP");
        field3.setTypeName("VARCHAR");
        field3.setSize(200);
        field3.setShouldAllowNull(true );
        field3.setIsPrimaryKey(false );
        field3.setUnique(false );
        field3.setIsIdentity(false );
        table.addField(field3);
    
        // SECTION: FIELD
        FieldDefinition field4 = new FieldDefinition();
        field4.setName("LEADER_ID");
        field4.setTypeName("NUMERIC");
        field4.setSize(15);
        field4.setShouldAllowNull(true );
        field4.setIsPrimaryKey(false );
        field4.setUnique(false );
        field4.setIsIdentity(false );
        field4.setForeignKeyFieldName("CMP3_FA_EMPLOYEE.EMP_ID");
        table.addField(field4);
    
        // SECTION: FIELD
        FieldDefinition field5 = new FieldDefinition();
        field5.setName("VERSION");
        field5.setTypeName("NUMERIC");
        field5.setSize(15);
        field5.setShouldAllowNull(true );
        field5.setIsPrimaryKey(false );
        field5.setUnique(false );
        field5.setIsIdentity(false );
        table.addField(field5);

/*        ForeignKeyConstraint foreignKeyPROJECT_LEADER = new ForeignKeyConstraint();
        foreignKeyPROJECT_LEADER.setName("PROJECT_LEADER");
        foreignKeyPROJECT_LEADER.setTargetTable("CMP3_FA_EMPLOYEE");
        foreignKeyPROJECT_LEADER.addSourceField("LEADER_ID");
        foreignKeyPROJECT_LEADER.addTargetField("EMP_ID");
        table.addForeignKeyConstraint(foreignKeyPROJECT_LEADER);
*/
        return table;
    }

    public static TableDefinition buildRESPONSTable() {
        TableDefinition table = new TableDefinition();
        // SECTION: TABLE
        table.setName("CMP3_FA_RESPONS");
    
        // SECTION: FIELD
        FieldDefinition field = new FieldDefinition();
        field.setName("EMP_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        table.addField(field);
    
        // SECTION: FIELD
        FieldDefinition field1 = new FieldDefinition();
        field1.setName("DESCRIPTION");
        field1.setTypeName("VARCHAR");
        field1.setSize(200);
        field1.setShouldAllowNull(false);
        table.addField(field1);
        
        ForeignKeyConstraint foreignKey = new ForeignKeyConstraint();
        foreignKey.setName("FA_EMP_RESP_FK");
        foreignKey.setTargetTable("CMP3_FA_EMPLOYEE");
        foreignKey.addSourceField("EMP_ID");
        foreignKey.addTargetField("EMP_ID");
        //foreignKey.setShouldCascadeOnDelete(true);
        table.addForeignKeyConstraint(foreignKey);
    
        return table;
    }
    
    public static TableDefinition buildSALARYTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_FA_SALARY");

        FieldDefinition fieldEMP_ID = new FieldDefinition();
        fieldEMP_ID.setName("EMP_ID");
        fieldEMP_ID.setTypeName("NUMERIC");
        fieldEMP_ID.setSize(15);
        fieldEMP_ID.setSubSize(0);
        fieldEMP_ID.setIsPrimaryKey(true);
        table.addField(fieldEMP_ID);

        FieldDefinition fieldSALARY = new FieldDefinition();
        fieldSALARY.setName("SALARY");
        fieldSALARY.setTypeName("NUMBER");
        fieldSALARY.setSize(15);
        fieldSALARY.setSubSize(0);
        table.addField(fieldSALARY);
        
        ForeignKeyConstraint foreignKey = new ForeignKeyConstraint();
        foreignKey.setName("FA_EMP_SAL_FK");
        foreignKey.setTargetTable("CMP3_FA_EMPLOYEE");
        foreignKey.addSourceField("EMP_ID");
        foreignKey.addTargetField("EMP_ID");
        foreignKey.setShouldCascadeOnDelete(true);
        table.addForeignKeyConstraint(foreignKey);

        return table;
    }

	public static TableDefinition buildVEGETABLETable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_FA_VEGETABLE");

        FieldDefinition fieldNAME = new FieldDefinition();
        fieldNAME.setName("vegetable_name");
        fieldNAME.setTypeName("VARCHAR");
        fieldNAME.setSize(30);
        fieldNAME.setIsPrimaryKey(true);
        table.addField(fieldNAME);
        
        FieldDefinition fieldCOLOR = new FieldDefinition();
        fieldCOLOR.setName("vegetable_color");
        fieldCOLOR.setTypeName("VARCHAR");
        fieldCOLOR.setSize(30);
        fieldCOLOR.setIsPrimaryKey(true);
        table.addField(fieldCOLOR);
        
        FieldDefinition fieldCOST = new FieldDefinition();
        fieldCOST.setName("COST");
        fieldCOST.setTypeName("DOUBLE PRECIS");
        fieldCOST.setSize(18);
        table.addField(fieldCOST);

        FieldDefinition fieldTAGS = new FieldDefinition();
        fieldTAGS.setName("TAGS");
        fieldTAGS.setTypeName("BLOB");
        table.addField(fieldTAGS);
        
        FieldDefinition fieldTYPE = new FieldDefinition();
        fieldTYPE.setName("TYPE");
        fieldTYPE.setTypeName("CHAR");
        fieldTYPE.setSize(1);
        fieldTYPE.setShouldAllowNull(true);
        table.addField(fieldTYPE);
        
        return table;
    }

    public static TableDefinition buildWOMANTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_FA_WOMAN");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setSubSize(0);
        fieldID.setIsPrimaryKey(true);
        fieldID.setIsIdentity(true);
        fieldID.setUnique(false);
        fieldID.setShouldAllowNull(false);
        table.addField(fieldID);
        
        FieldDefinition fieldNAME = new FieldDefinition();
        fieldNAME.setName("NAME");
        fieldNAME.setTypeName("VARCHAR2");
        fieldNAME.setSize(100);
        fieldNAME.setSubSize(0);
        fieldNAME.setIsPrimaryKey(false);
        fieldNAME.setIsIdentity(false);
        fieldNAME.setUnique(false);
        fieldNAME.setShouldAllowNull(true);
        table.addField(fieldNAME);
        
        return table;
    }
    
	public static TableDefinition buildWORKWEEKTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_FA_EMP_WORKWEEK");
    
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("EMP_ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(false);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(false);
        table.addField(fieldID);
    
        FieldDefinition fieldWORKWEEK = new FieldDefinition();
        fieldWORKWEEK.setName("WORKWEEK");
        fieldWORKWEEK.setTypeName("NUMERIC");
        fieldWORKWEEK.setSize(1);
        fieldWORKWEEK.setIsPrimaryKey(false);
        fieldWORKWEEK.setUnique(false);
        fieldWORKWEEK.setIsIdentity(false);
        fieldWORKWEEK.setShouldAllowNull(false);
        table.addField(fieldWORKWEEK);
        
        ForeignKeyConstraint foreignKey = new ForeignKeyConstraint();
        foreignKey.setName("FA_EMP_WW_FK");
        foreignKey.setTargetTable("CMP3_FA_EMPLOYEE");
        foreignKey.addSourceField("EMP_ID");
        foreignKey.addTargetField("EMP_ID");
        foreignKey.setShouldCascadeOnDelete(true);
        table.addForeignKeyConstraint(foreignKey);
    
        return table;
    }

	public static TableDefinition buildWORLDRANKTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_FA_WORLDRANK");
        
        FieldDefinition IDfield = new FieldDefinition();
        IDfield.setName("ID");
        IDfield.setTypeName("NUMERIC");
        IDfield.setSize(15);
        IDfield.setShouldAllowNull(false);
        IDfield.setIsPrimaryKey(true);
        IDfield.setUnique(false );
        IDfield.setIsIdentity(true);
        table.addField(IDfield);
        
        return table;
    }
}
