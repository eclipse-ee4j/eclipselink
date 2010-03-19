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
 *     02/25/2009-2.0 Guy Pelletier 
 *       - 265359: JPA 2.0 Element Collections - Metadata processing portions
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.advanced;

import org.eclipse.persistence.tools.schemaframework.*;

public class AdvancedTableCreator extends TableCreator {
    public AdvancedTableCreator() {
        setName("EJB3EmployeeProject");

        addTableDefinition(buildADDRESSTable());
        addTableDefinition(buildBUYERTable());
        addTableDefinition(buildCREDITCARDSTable());
        addTableDefinition(buildCREDITLINESTable());
        addTableDefinition(buildCUSTOMERTable());
        addTableDefinition(buildDEALERTable());
        addTableDefinition(buildDEPTTable());
        addTableDefinition(buildDEPT_EMPTable());
        addTableDefinition(buildEMPLOYEETable());
        addTableDefinition(buildEQUIPMENTTable());
        addTableDefinition(buildEQUIPMENTCODETable());
        addTableDefinition(buildGOLFERTable());
        addTableDefinition(buildLARGEPROJECTTable());
        addTableDefinition(buildMANTable());
        addTableDefinition(buildPARTNERLINKTable());
        addTableDefinition(buildPHONENUMBERTable());
        addTableDefinition(buildPHONENUMBERSTATUSTable());
        addTableDefinition(buildPLATINUMBUYERTable());
        addTableDefinition(buildPROJECT_EMPTable());
        addTableDefinition(buildPROJECT_PROPSTable());
        addTableDefinition(buildPROJECTTable());
        addTableDefinition(buildRESPONSTable());
        addTableDefinition(buildSALARYTable());
        addTableDefinition(buildVEGETABLETable());
        addTableDefinition(buildWOMANTable());
        addTableDefinition(buildWORKWEEKTable());
        addTableDefinition(buildWORLDRANKTable());
        addTableDefinition(buildCONCURRENCYATable());
        addTableDefinition(buildCONCURRENCYBTable());
        addTableDefinition(buildCONCURRENCYCTable());
        //addTableDefinition(buildEMPLOYEE_SEQTable());
    }
    
    public static TableDefinition buildADDRESSTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_ADDRESS");

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

        FieldDefinition fieldType = new FieldDefinition();
        fieldType.setName("TYPE");
        fieldType.setTypeName("VARCHAR2");
        fieldType.setSize(150);
        fieldType.setSubSize(0);
        fieldType.setIsPrimaryKey(false);
        fieldType.setIsIdentity(false);
        fieldType.setUnique(false);
        fieldType.setShouldAllowNull(true);
        table.addField(fieldType);
        
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
    
     public static TableDefinition buildBUYERTable() {
        TableDefinition table = new TableDefinition();

        table.setName("CMP3_BUYER");
        
        String[] unq1 = {"BUYER_ID", "BUYER_NAME"};
        String[] unq2 = {"BUYER_ID", "DESCRIP"};
        table.addUniqueKeyConstraint("UNQ_CMP3_BUYER_1", unq1);
        table.addUniqueKeyConstraint("UNQ_CMP3_BUYER_2", unq2);

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
    
     public static TableDefinition buildCONCURRENCYATable() {
         TableDefinition table = new TableDefinition();
         table.setName("CONCURRENCYA");

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

         FieldDefinition fieldConcB = new FieldDefinition();
         fieldConcB.setName("CONCURRENCYB_ID");
         fieldConcB.setTypeName("NUMERIC");
         fieldConcB.setSize(15);
         fieldConcB.setSubSize(0);
         fieldConcB.setIsPrimaryKey(false);
         fieldConcB.setIsIdentity(false);
         fieldConcB.setUnique(false);
         fieldConcB.setShouldAllowNull(true);
         table.addField(fieldConcB);

         FieldDefinition fieldConcC = new FieldDefinition();
         fieldConcC.setName("CONCURRENCYC_ID");
         fieldConcC.setTypeName("NUMERIC");
         fieldConcC.setSize(15);
         fieldConcC.setSubSize(0);
         fieldConcC.setIsPrimaryKey(false);
         fieldConcC.setIsIdentity(false);
         fieldConcC.setUnique(false);
         fieldConcC.setShouldAllowNull(true);
         table.addField(fieldConcC);
         
         return table;
     }

     public static TableDefinition buildCONCURRENCYBTable() {
         TableDefinition table = new TableDefinition();
         table.setName("CONCURRENCYB");

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

     public static TableDefinition buildCONCURRENCYCTable() {
         TableDefinition table = new TableDefinition();
         table.setName("CONCURRENCYC");

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

     public static TableDefinition buildCREDITCARDSTable() {
        TableDefinition table = new TableDefinition();
        table.setName("Buyer_CREDITCARDS");
    
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("BUYER_ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(false);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(false);
        fieldID.setForeignKeyFieldName("CMP3_BUYER.BUYER_ID");
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
     
    public static TableDefinition buildCREDITLINESTable() {
         TableDefinition table = new TableDefinition();
         table.setName("BUYER_CREDITLINES");
     
         FieldDefinition fieldID = new FieldDefinition();
         fieldID.setName("BUYER_ID");
         fieldID.setTypeName("NUMERIC");
         fieldID.setSize(15);
         fieldID.setShouldAllowNull(false);
         fieldID.setIsPrimaryKey(false);
         fieldID.setUnique(false);
         fieldID.setIsIdentity(false);
         fieldID.setForeignKeyFieldName("CMP3_BUYER.BUYER_ID");
         table.addField(fieldID);
     
         FieldDefinition fieldBANK = new FieldDefinition();
         fieldBANK.setName("BANK");
         fieldBANK.setTypeName("VARCHAR");
         fieldBANK.setSize(4);
         fieldBANK.setShouldAllowNull(false);
         fieldBANK.setIsPrimaryKey(false);
         fieldBANK.setUnique(true);
         fieldBANK.setIsIdentity(false);
         table.addField(fieldBANK);
         
         FieldDefinition fieldACCOUNT = new FieldDefinition();
         fieldACCOUNT.setName("ACCOUNT");
         fieldACCOUNT.setTypeName("VARCHAR");
         fieldACCOUNT.setSize(10);
         fieldACCOUNT.setShouldAllowNull(false);
         fieldACCOUNT.setIsPrimaryKey(false);
         fieldACCOUNT.setUnique(false);
         fieldACCOUNT.setIsIdentity(false);
         table.addField(fieldACCOUNT);
     
         return table;
     }
    
     public static TableDefinition buildCUSTOMERTable() {
         TableDefinition table = new TableDefinition();
         table.setName("CMP3_ADV_CUSTOMER");

         FieldDefinition field = new FieldDefinition();
         field.setName("CUSTOMER_ID");
         field.setTypeName("NUMERIC");
         field.setSize(15);
         field.setShouldAllowNull(false);
         field.setIsPrimaryKey(true);
         field.setUnique(false);
         field.setIsIdentity(true);
         table.addField(field);
     
         FieldDefinition field0 = new FieldDefinition();
         field0.setName("FK_DEALER_ID");
         field0.setTypeName("NUMERIC");
         field0.setSize(15);
         field0.setShouldAllowNull(true);
         field0.setIsPrimaryKey(false);
         field0.setUnique(false);
         field0.setIsIdentity(false);
         field0.setForeignKeyFieldName("CMP3_DEALER.DEALER_ID");
         table.addField(field0);
     
         FieldDefinition field1 = new FieldDefinition();
         field1.setName("F_NAME");
         field1.setTypeName("VARCHAR");
         field1.setSize(40);
         field1.setShouldAllowNull(true);
         field1.setIsPrimaryKey(false);
         field1.setUnique(false);
         field1.setIsIdentity(false);
         table.addField(field1);
     
         FieldDefinition field2 = new FieldDefinition();
         field2.setName("L_NAME");
         field2.setTypeName("VARCHAR");
         field2.setSize(40);
         field2.setShouldAllowNull(true);
         field2.setIsPrimaryKey(false);
         field2.setUnique(false);
         field2.setIsIdentity(false);
         table.addField(field2);
         
         FieldDefinition field3 = new FieldDefinition();
         field3.setName("BUDGET");
         field3.setTypeName("NUMERIC");
         field3.setSize(15);
         field3.setShouldAllowNull(true);
         field3.setIsPrimaryKey(false);
         field3.setUnique(false);
         field3.setIsIdentity(false);
         table.addField(field3);
         
         FieldDefinition field4 = new FieldDefinition();
         field4.setName("VERSION");
         field4.setTypeName("NUMERIC");
         field4.setSize(15);
         field4.setShouldAllowNull(true);
         field4.setIsPrimaryKey(false);
         field4.setUnique(false);
         field4.setIsIdentity(false);
         table.addField(field4);
         
         return table;
     }
     
     public static TableDefinition buildDEALERTable() {
         TableDefinition table = new TableDefinition();
         table.setName("CMP3_DEALER");

         FieldDefinition field = new FieldDefinition();
         field.setName("DEALER_ID");
         field.setTypeName("NUMERIC");
         field.setSize(15);
         field.setShouldAllowNull(false);
         field.setIsPrimaryKey(true);
         field.setUnique(false);
         field.setIsIdentity(true);
         table.addField(field);
     
         FieldDefinition field0 = new FieldDefinition();
         field0.setName("FK_EMP_ID");
         field0.setTypeName("NUMERIC");
         field0.setSize(15);
         field0.setShouldAllowNull(true);
         field0.setIsPrimaryKey(false);
         field0.setUnique(false);
         field0.setIsIdentity(false);
         field0.setForeignKeyFieldName("CMP3_EMPLOYEE.EMP_ID");
         table.addField(field0);
     
         FieldDefinition field1 = new FieldDefinition();
         field1.setName("F_NAME");
         field1.setTypeName("VARCHAR");
         field1.setSize(40);
         field1.setShouldAllowNull(true);
         field1.setIsPrimaryKey(false);
         field1.setUnique(false);
         field1.setIsIdentity(false);
         table.addField(field1);
     
         FieldDefinition field2 = new FieldDefinition();
         field2.setName("L_NAME");
         field2.setTypeName("VARCHAR");
         field2.setSize(40);
         field2.setShouldAllowNull(true);
         field2.setIsPrimaryKey(false);
         field2.setUnique(false);
         field2.setIsIdentity(false);
         table.addField(field2);
         
         FieldDefinition field3 = new FieldDefinition();
         field3.setName("STATUS");
         field3.setTypeName("VARCHAR");
         field3.setSize(40);
         field3.setShouldAllowNull(true);
         field3.setIsPrimaryKey(false);
         field3.setUnique(false);
         field3.setIsIdentity(false);
         table.addField(field3);
         
         FieldDefinition field4 = new FieldDefinition();
         field4.setName("VERSION");
         field4.setTypeName("NUMERIC");
         field4.setSize(15);
         field4.setShouldAllowNull(true);
         field4.setIsPrimaryKey(false);
         field4.setUnique(false);
         field4.setIsIdentity(false);
         table.addField(field4);
         
         return table;
     }
     
     public static TableDefinition buildDEPTTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_DEPT");

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

        FieldDefinition fieldHEAD = new FieldDefinition();
        fieldHEAD.setName("DEPT_HEAD");
        fieldHEAD.setTypeName("NUMERIC");
        fieldHEAD.setSize(15);
        fieldHEAD.setSubSize(0);
        fieldHEAD.setIsPrimaryKey(false);
        fieldHEAD.setIsIdentity(false);
        fieldHEAD.setUnique(false);
        fieldHEAD.setShouldAllowNull(true);
        table.addField(fieldHEAD);
        
        return table;
    }
    
    public static TableDefinition buildDEPT_EMPTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_DEPT_CMP3_EMPLOYEE");

        // SECTION: FIELD
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ADV_DEPT_ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(true);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(false);
        fieldID.setForeignKeyFieldName("CMP3_DEPT.ID");
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
        fieldEMP.setForeignKeyFieldName("CMP3_EMPLOYEE.EMP_ID");
        table.addField(fieldEMP);
        
        return table;   
    }

    public static TableDefinition buildEMPLOYEE_SEQTable() {
        TableDefinition table = new TableDefinition();
//        table.setName("SEQUENCE");
        table.setName("CMP3_EMPLOYEE_SEQ");

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
    
    public static TableDefinition buildEMPLOYEETable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_EMPLOYEE");
    
        FieldDefinition field = new FieldDefinition();
        field.setName("EMP_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(true);
        table.addField(field);
    
        FieldDefinition field1 = new FieldDefinition();
        field1.setName("F_NAME");
        field1.setTypeName("VARCHAR");
        field1.setSize(40);
        field1.setShouldAllowNull(true);
        field1.setIsPrimaryKey(false);
        field1.setUnique(false);
        field1.setIsIdentity(false);
        table.addField(field1);
    
        FieldDefinition field2 = new FieldDefinition();
        field2.setName("L_NAME");
        field2.setTypeName("VARCHAR");
        field2.setSize(40);
        field2.setShouldAllowNull(true);
        field2.setIsPrimaryKey(false);
        field2.setUnique(false);
        field2.setIsIdentity(false);
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
        field3.setName("START_DATE");
        field3.setTypeName("DATE");
        field3.setSize(23);
        field3.setShouldAllowNull(true);
        field3.setIsPrimaryKey(false);
        field3.setUnique(false);
        field3.setIsIdentity(false);
        table.addField(field3);
    
        FieldDefinition field4 = new FieldDefinition();
        field4.setName("END_DATE");
        field4.setTypeName("DATE");
        field4.setSize(23);
        field4.setShouldAllowNull(true);
        field4.setIsPrimaryKey(false);
        field4.setUnique(false);
        field4.setIsIdentity(false);
        table.addField(field4);
    
        FieldDefinition field5 = new FieldDefinition();
        field5.setName("START_TIME");
        field5.setTypeName("TIME");
        field5.setSize(23);
        field5.setShouldAllowNull(true);
        field5.setIsPrimaryKey(false);
        field5.setUnique(false);
        field5.setIsIdentity(false);
        table.addField(field5);

        FieldDefinition field6 = new FieldDefinition();
        field6.setName("END_TIME");
        field6.setTypeName("TIME");
        field6.setSize(23);
        field6.setShouldAllowNull(true);
        field6.setIsPrimaryKey(false);
        field6.setUnique(false);
        field6.setIsIdentity(false);
        table.addField(field6);

        FieldDefinition fieldOvertimeStart = new FieldDefinition();
        fieldOvertimeStart.setName("START_OVERTIME");
        fieldOvertimeStart.setTypeName("TIME");
        fieldOvertimeStart.setSize(23);
        fieldOvertimeStart.setShouldAllowNull(true);
        fieldOvertimeStart.setIsPrimaryKey(false);
        fieldOvertimeStart.setUnique(false);
        fieldOvertimeStart.setIsIdentity(false);
        table.addField(fieldOvertimeStart);

        FieldDefinition fieldOvertimeEnd = new FieldDefinition();
        fieldOvertimeEnd.setName("END_OVERTIME");
        fieldOvertimeEnd.setTypeName("TIME");
        fieldOvertimeEnd.setSize(23);
        fieldOvertimeEnd.setShouldAllowNull(true);
        fieldOvertimeEnd.setIsPrimaryKey(false);
        fieldOvertimeEnd.setUnique(false);
        fieldOvertimeEnd.setIsIdentity(false);
        table.addField(fieldOvertimeEnd);

        FieldDefinition field8 = new FieldDefinition();
        field8.setName("ADDR_ID");
        field8.setTypeName("NUMERIC");
        field8.setSize(15);
        field8.setShouldAllowNull(true);
        field8.setIsPrimaryKey(false);
        field8.setUnique(false);
        field8.setIsIdentity(false);
        field8.setForeignKeyFieldName("CMP3_ADDRESS.ADDRESS_ID");
        table.addField(field8);
    
        FieldDefinition field9 = new FieldDefinition();
        field9.setName("MANAGER_EMP_ID");
        field9.setTypeName("NUMERIC");
        field9.setSize(15);
        field9.setShouldAllowNull(true);
        field9.setIsPrimaryKey(false);
        field9.setUnique(false);
        field9.setIsIdentity(false);
        field9.setForeignKeyFieldName("CMP3_EMPLOYEE.EMP_ID");
        table.addField(field9);
    
        FieldDefinition field10 = new FieldDefinition();
        field10.setName("VERSION");
        field10.setTypeName("NUMERIC");
        field10.setSize(15);
        field10.setShouldAllowNull(true);
        field10.setIsPrimaryKey(false);
        field10.setUnique(false);
        field10.setIsIdentity(false);
        table.addField(field10);
        
        FieldDefinition fieldDEPT = new FieldDefinition();
        fieldDEPT.setName("DEPT_ID");
        fieldDEPT.setTypeName("NUMERIC");
        fieldDEPT.setSize(15);
        fieldDEPT.setShouldAllowNull(true);
        fieldDEPT.setIsPrimaryKey(false);
        fieldDEPT.setUnique(false);
        fieldDEPT.setIsIdentity(false);
        fieldDEPT.setForeignKeyFieldName("CMP3_DEPT.ID");
        table.addField(fieldDEPT);
        
        FieldDefinition fieldStatus = new FieldDefinition();
        fieldStatus.setName("STATUS");
        fieldStatus.setTypeName("NUMERIC");
        fieldStatus.setSize(15);
        fieldStatus.setIsPrimaryKey(false);
        fieldStatus.setUnique(false);
        fieldStatus.setIsIdentity(false);
        fieldStatus.setShouldAllowNull(true);
        table.addField(fieldStatus);
    
        FieldDefinition fieldPayScale = new FieldDefinition();
        fieldPayScale.setName("PAY_SCALE");
        fieldPayScale.setTypeName("VARCHAR");
        fieldPayScale.setSize(40);
        fieldPayScale.setIsPrimaryKey(false);
        fieldPayScale.setUnique(false);
        fieldPayScale.setIsIdentity(false);
        fieldPayScale.setShouldAllowNull(true);
        table.addField(fieldPayScale);
        
        FieldDefinition fieldRoomNumber = new FieldDefinition();
        fieldRoomNumber.setName("ROOM_NUM");
        fieldRoomNumber.setTypeName("NUMBER");
        fieldRoomNumber.setSize(15);
        fieldRoomNumber.setSubSize(0);
        fieldRoomNumber.setIsPrimaryKey(false);
        fieldRoomNumber.setIsIdentity(false);
        fieldRoomNumber.setUnique(false);
        fieldRoomNumber.setShouldAllowNull(true);
        table.addField(fieldRoomNumber);
        
        // SECTION: FIELD
        FieldDefinition fieldFormerCompany = new FieldDefinition();
        fieldFormerCompany.setName("FORMER_COMPANY");
        fieldFormerCompany.setTypeName("VARCHAR");
        fieldFormerCompany.setSize(40);
        fieldFormerCompany.setShouldAllowNull(true );
        fieldFormerCompany.setIsPrimaryKey(false );
        fieldFormerCompany.setUnique(false );
        fieldFormerCompany.setIsIdentity(false );
        table.addField(fieldFormerCompany);
    
        // SECTION: FIELD
        FieldDefinition fieldFormerStartDate = new FieldDefinition();
        fieldFormerStartDate.setName("FORMER_START_DATE");
        fieldFormerStartDate.setTypeName("DATE");
        fieldFormerStartDate.setSize(23);
        fieldFormerStartDate.setShouldAllowNull(true );
        fieldFormerStartDate.setIsPrimaryKey(false );
        fieldFormerStartDate.setUnique(false );
        fieldFormerStartDate.setIsIdentity(false );
        table.addField(fieldFormerStartDate);
    
        // SECTION: FIELD
        FieldDefinition fieldFormerEndDate = new FieldDefinition();
        fieldFormerEndDate.setName("FORMER_END_DATE");
        fieldFormerEndDate.setTypeName("DATE");
        fieldFormerEndDate.setSize(23);
        fieldFormerEndDate.setShouldAllowNull(true );
        fieldFormerEndDate.setIsPrimaryKey(false );
        fieldFormerEndDate.setUnique(false );
        fieldFormerEndDate.setIsIdentity(false );
        table.addField(fieldFormerEndDate);

        return table;
    }
    
    public static TableDefinition buildEQUIPMENTTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_ADV_EQUIP");

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
        fieldDEPTID.setForeignKeyFieldName("CMP3_DEPT.ID");
        table.addField(fieldDEPTID);
        
        FieldDefinition fieldCODEID = new FieldDefinition();
        fieldCODEID.setName("CODE_ID");
        fieldCODEID.setTypeName("NUMERIC");
        fieldCODEID.setSize(15);
        fieldCODEID.setShouldAllowNull(true);
        fieldCODEID.setIsPrimaryKey(false);
        fieldCODEID.setUnique(false);
        fieldCODEID.setIsIdentity(false);
        fieldCODEID.setForeignKeyFieldName("CMP3_ADV_EQUIP_CODE.ID");
        table.addField(fieldCODEID);

        return table;
    }
    
    public static TableDefinition buildGOLFERTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_GOLFER");
        
        FieldDefinition IDfield = new FieldDefinition();
        IDfield.setName("WORLDRANK_ID");
        IDfield.setTypeName("NUMERIC");
        IDfield.setSize(15);
        IDfield.setShouldAllowNull(false);
        IDfield.setIsPrimaryKey(true);
        IDfield.setUnique(false);
        IDfield.setIsIdentity(true);
        table.addField(IDfield);
        
        ForeignKeyConstraint foreignKeyGOLFER_WORLDRANK = new ForeignKeyConstraint();
        foreignKeyGOLFER_WORLDRANK.setName("WORLDRANK_ID");
        foreignKeyGOLFER_WORLDRANK.setTargetTable("CMP3_WORLDRANK"); 
        foreignKeyGOLFER_WORLDRANK.addSourceField("WORLDRANK_ID");
        foreignKeyGOLFER_WORLDRANK.addTargetField("ID");
        table.addForeignKeyConstraint(foreignKeyGOLFER_WORLDRANK);
        
        return table;
    }
    
    public static TableDefinition buildEQUIPMENTCODETable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_ADV_EQUIP_CODE");

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
        table.setName("CMP3_LPROJECT");

        // SECTION: FIELD
        FieldDefinition field = new FieldDefinition();
        field.setName("PROJ_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false );
        field.setIsPrimaryKey(true );
        field.setUnique(false );
        field.setIsIdentity(false );
        field.setForeignKeyFieldName("CMP3_PROJECT.PROJ_ID");
        table.addField(field);
    
        // SECTION: FIELD
        FieldDefinition field1 = new FieldDefinition();
        field1.setName("BUDGET");
        field1.setTypeName("DOUBLE PRECIS");
        field1.setSize(18);
        field1.setShouldAllowNull(true );
        field1.setIsPrimaryKey(false );
        field1.setUnique(false );
        field1.setIsIdentity(false );
        table.addField(field1);
    
        return table;
    }
    
    public static TableDefinition buildMANTable() {
        TableDefinition table = new TableDefinition();
        table.setName("MAN");

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

        FieldDefinition field1 = new FieldDefinition();
        field1.setName("F_NAME");
        field1.setTypeName("VARCHAR");
        field1.setSize(40);
        field1.setShouldAllowNull(true);
        field1.setIsPrimaryKey(false);
        field1.setUnique(false);
        field1.setIsIdentity(false);
        table.addField(field1);
    
        FieldDefinition field2 = new FieldDefinition();
        field2.setName("L_NAME");
        field2.setTypeName("VARCHAR");
        field2.setSize(40);
        field2.setShouldAllowNull(true);
        field2.setIsPrimaryKey(false);
        field2.setUnique(false);
        field2.setIsIdentity(false);
        table.addField(field2);        

        return table;
    }
    
    public static TableDefinition buildPARTNERLINKTable() {
        TableDefinition table = new TableDefinition();
        table.setName("MW");

        FieldDefinition fieldMID = new FieldDefinition();
        fieldMID.setName("M");
        fieldMID.setTypeName("NUMERIC");
        fieldMID.setSize(15);
        fieldMID.setSubSize(0);
        fieldMID.setIsPrimaryKey(false);
        fieldMID.setIsIdentity(false);
        fieldMID.setUnique(false);
        fieldMID.setShouldAllowNull(true);
        fieldMID.setForeignKeyFieldName("MAN.ID");
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
        fieldWID.setForeignKeyFieldName("WOMAN.ID");
        table.addField(fieldWID);
        
        return table;
    }
    
    public static TableDefinition buildPHONENUMBERSTATUSTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_PHONE_STATUS");
    
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
        table.setName("CMP3_PHONENUMBER");

        // SECTION: FIELD
        FieldDefinition field = new FieldDefinition();
        field.setName("OWNER_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false );
        field.setIsPrimaryKey(true );
        field.setUnique(false );
        field.setIsIdentity(false );
        field.setForeignKeyFieldName("CMP3_EMPLOYEE.EMP_ID");
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

        return table;
    }
    
    public static TableDefinition buildPLATINUMBUYERTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_PBUYER");

        // SECTION: FIELD
        FieldDefinition field = new FieldDefinition();
        field.setName("BUYER_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false );
        field.setIsPrimaryKey(true );
        field.setUnique(false );
        field.setIsIdentity(false );
        field.setForeignKeyFieldName("CMP3_BUYER.BUYER_ID");
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

        table.setName("CMP3_EMP_PROJ");

        // SECTION: FIELD
        FieldDefinition field = new FieldDefinition();
        field.setName("EMPLOYEES_EMP_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false );
        field.setIsPrimaryKey(true );
        field.setUnique(false );
        field.setIsIdentity(false );
        field.setForeignKeyFieldName("CMP3_EMPLOYEE.EMP_ID");
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
        field1.setForeignKeyFieldName("CMP3_PROJECT.PROJ_ID");
        table.addField(field1);

        return table;
    }

    public static TableDefinition buildPROJECT_PROPSTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_PROJ_PROPS");

        FieldDefinition projectIdField = new FieldDefinition();
        projectIdField.setName("PROJ_ID");
        projectIdField.setTypeName("NUMERIC");
        projectIdField.setSize(15);
        projectIdField.setShouldAllowNull(false);
        projectIdField.setIsPrimaryKey(false);
        projectIdField.setUnique(false);
        projectIdField.setIsIdentity(false);
        projectIdField.setForeignKeyFieldName("CMP3_PROJECT.PROJ_ID");
        table.addField(projectIdField);
    
        FieldDefinition propertiesField = new FieldDefinition();
        propertiesField.setName("PROPS");
        propertiesField.setTypeName("VARCHAR");
        propertiesField.setSize(45);
        propertiesField.setShouldAllowNull(true);
        propertiesField.setIsPrimaryKey(false);
        propertiesField.setUnique(false);
        propertiesField.setIsIdentity(false);
        table.addField(propertiesField);

        return table;
    }
    
    public static TableDefinition buildPROJECTTable() {
        TableDefinition table = new TableDefinition();

        table.setName("CMP3_PROJECT");

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
        field4.setForeignKeyFieldName("CMP3_EMPLOYEE.EMP_ID");
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

        return table;
    }

    public static TableDefinition buildRESPONSTable() {
        TableDefinition table = new TableDefinition();
        // SECTION: TABLE
        table.setName("CMP3_RESPONS");
    
        // SECTION: FIELD
        FieldDefinition field = new FieldDefinition();
        field.setName("EMP_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(false);
        field.setUnique(false);
        field.setIsIdentity(false);
        field.setForeignKeyFieldName("CMP3_EMPLOYEE.EMP_ID");
        table.addField(field);
    
        // SECTION: FIELD
        FieldDefinition field1 = new FieldDefinition();
        field1.setName("DESCRIPTION");
        field1.setTypeName("VARCHAR");
        field1.setSize(200);
        field1.setShouldAllowNull(false);
        field1.setIsPrimaryKey(false);
        field1.setUnique(false);
        field1.setIsIdentity(false);
        table.addField(field1);
    
        return table;
    }
    
    public static TableDefinition buildSALARYTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_SALARY");

        FieldDefinition fieldEMP_ID = new FieldDefinition();
        fieldEMP_ID.setName("EMP_ID");
        fieldEMP_ID.setTypeName("NUMERIC");
        fieldEMP_ID.setSize(15);
        fieldEMP_ID.setSubSize(0);
        fieldEMP_ID.setIsPrimaryKey(true);
        fieldEMP_ID.setIsIdentity(false);
        fieldEMP_ID.setUnique(false);
        fieldEMP_ID.setShouldAllowNull(false);
        fieldEMP_ID.setForeignKeyFieldName("CMP3_EMPLOYEE.EMP_ID");
        table.addField(fieldEMP_ID);

        FieldDefinition fieldSALARY = new FieldDefinition();
        fieldSALARY.setName("SALARY");
        fieldSALARY.setTypeName("NUMBER");
        fieldSALARY.setSize(15);
        fieldSALARY.setSubSize(0);
        fieldSALARY.setIsPrimaryKey(false);
        fieldSALARY.setIsIdentity(false);
        fieldSALARY.setUnique(false);
        fieldSALARY.setShouldAllowNull(true);
        table.addField(fieldSALARY);

        return table;
    }

	public static TableDefinition buildVEGETABLETable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_VEGETABLE");

        FieldDefinition fieldNAME = new FieldDefinition();
        fieldNAME.setName("VEGETABLE_NAME");
        fieldNAME.setTypeName("VARCHAR");
        fieldNAME.setSize(30);
        fieldNAME.setIsPrimaryKey(true);
        table.addField(fieldNAME);
        
        FieldDefinition fieldCOLOR = new FieldDefinition();
        fieldCOLOR.setName("VEGETABLE_COLOR");
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
        
        return table;
    }

    public static TableDefinition buildWOMANTable() {
        TableDefinition table = new TableDefinition();
        table.setName("WOMAN");

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

        FieldDefinition field1 = new FieldDefinition();
        field1.setName("F_NAME");
        field1.setTypeName("VARCHAR");
        field1.setSize(40);
        field1.setShouldAllowNull(true);
        field1.setIsPrimaryKey(false);
        field1.setUnique(false);
        field1.setIsIdentity(false);
        table.addField(field1);
    
        FieldDefinition field2 = new FieldDefinition();
        field2.setName("L_NAME");
        field2.setTypeName("VARCHAR");
        field2.setSize(40);
        field2.setShouldAllowNull(true);
        field2.setIsPrimaryKey(false);
        field2.setUnique(false);
        field2.setIsIdentity(false);
        table.addField(field2);        
        
        return table;
    }
    
    public static TableDefinition buildWORKWEEKTable() {
        TableDefinition table = new TableDefinition();
        table.setName("Employee_WORKWEEK");
    
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("EMP_ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(false);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(false);
        fieldID.setForeignKeyFieldName("CMP3_EMPLOYEE.EMP_ID");
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
    
        return table;
    }
    
	public static TableDefinition buildWORLDRANKTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_WORLDRANK");
        
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
