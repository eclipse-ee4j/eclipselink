/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates, IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
//     02/25/2009-2.0 Guy Pelletier
//       - 265359: JPA 2.0 Element Collections - Metadata processing portions
//     06/16/2010-2.2 Guy Pelletier
//       - 247078: eclipselink-orm.xml schema should allow lob and enumerated on version and id mappings
//     10/15/2010-2.2 Guy Pelletier
//       - 322008: Improve usability of additional criteria applied to queries at the session/EM
//     10/27/2010-2.2 Guy Pelletier
//       - 328114: @AttributeOverride does not work with nested embeddables having attributes of the same name
//     01/06/2015-2.6 Dalia Abo Sheasha
//       - 454917: Informix tables need to use INT fields when referencing SERIAL types, moved helper methods to parent class
//     01/15/2015-2.6 Mythily Parthasarathy
//       - 457480: NPE in  MethodAttributeAccessor.getAttributeValueFromObject
package org.eclipse.persistence.testing.models.jpa.advanced;

import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.internal.databaseaccess.DatabasePlatform;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.logging.SessionLogEntry;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.framework.TogglingFastTableCreator;
import org.eclipse.persistence.tools.schemaframework.FieldDefinition;
import org.eclipse.persistence.tools.schemaframework.ForeignKeyConstraint;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

public class AdvancedTableCreator extends TogglingFastTableCreator {
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
        addTableDefinition(buildGolfer_SPONSORDOLLARSTable());
        addTableDefinition(buildHUGEPROJECTTable());
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
        addTableDefinition(buildREADONLYISOLATED());
        addTableDefinition(buildENTITYBTable());
        addTableDefinition(buildENTITYCTable());
        addTableDefinition(buildENTITYATable());
        addTableDefinition(buildENTITYDTable());
        addTableDefinition(buildADVENTITYAENTITYDTable());
        addTableDefinition(buildENTITYETable());
        addTableDefinition(buildADVENTITYAENTITYETable());
        addTableDefinition(buildVIOLATIONTable());
        addTableDefinition(buildVIOLATIONCODETable());
        addTableDefinition(buildVIOLATIONCODESTable());
        addTableDefinition(buildSTUDENTTable());
        addTableDefinition(buildSCHOOLTable());
        addTableDefinition(buildBOLTTable());
        addTableDefinition(buildNUTTable());
        addTableDefinition(buildPERSONTable());
        addTableDefinition(buildEATERTable());
        addTableDefinition(buildFOODTable());
        addTableDefinition(buildSANDWICHTable());
        addTableDefinition(buildLOOTTable());
        addTableDefinition(buildADVSIMPLEENTITYTable());
        addTableDefinition(buildADVECSIMPLETable());
        addTableDefinition(buildADVSIMPLELANGUAGETable());
        addTableDefinition(buildADVSIMPLEENTITYLANGUAGETable());
        addTableDefinition(buildCMP3_JIGSAWTable());
        addTableDefinition(buildCMP3_JIGSAW_PIECETable());
        addTableDefinition(buildRABBITTable());
        addTableDefinition(buildRABBITFOOTTable());
        addTableDefinition(buildCMP3_HINGETable());//Bug#457480
        addTableDefinition(buildCMP3_ROOMTable());
        addTableDefinition(buildCMP3_DOORTable());
        addTableDefinition(buildCMP3_PRODUCTTable());
        addTableDefinition(buildCmp3EmbedVisitorTable());
        addTableDefinition(buildCMP3_CANOETable());
        addTableDefinition(buildCMP3_LAKETable());
        addTableDefinition(buildCMP3_OYSTERTable());
        addTableDefinition(buildCMP3_PEARLTable());
        addTableDefinition(buildCMP3_PEARL_HISTTable());
        addTableDefinition(buildJobTable());
        addTableDefinition(buildEventTable());
        addTableDefinition(buildCMP3_TODOLISTTable());
        addTableDefinition(buildCMP3_TODOLISTITEMTable());
        addTableDefinition(buildBILLTable());
        addTableDefinition(buildBILL_LINETable());
        addTableDefinition(buildBILL_LINEITEMTable());
        addTableDefinition(buildBILL_ACTIONTable());
        addTableDefinition(buildORD_ENTITYATable());
        addTableDefinition(buildORD_ENTITYZTable());
    }

    public TableDefinition buildADDRESSTable() {
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

        if (Boolean.valueOf(System.getProperty("sop"))) {
            FieldDefinition fieldSop = new FieldDefinition();
            fieldSop.setName("SOP");
            fieldSop.setTypeName("BLOB");
            fieldSop.setSize(0);
            fieldSop.setSubSize(0);
            fieldSop.setIsPrimaryKey(false);
            fieldSop.setIsIdentity(false);
            fieldSop.setUnique(false);
            fieldSop.setShouldAllowNull(true);
            table.addField(fieldSop);
        }

        return table;
    }

     public TableDefinition buildBUYERTable() {
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

     public TableDefinition buildCONCURRENCYATable() {
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

     public TableDefinition buildCONCURRENCYBTable() {
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

     public TableDefinition buildCONCURRENCYCTable() {
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

     public TableDefinition buildCREDITCARDSTable() {
        TableDefinition table = new TableDefinition();
        table.setName("Buyer_CREDITCARDS");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("BUYER_ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(true);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(false);
        fieldID.setForeignKeyFieldName("CMP3_BUYER.BUYER_ID");
        table.addField(fieldID);

        FieldDefinition fieldCARD = new FieldDefinition();
        fieldCARD.setName("CARD");
        fieldCARD.setTypeName("VARCHAR");
        fieldCARD.setSize(2);
        fieldCARD.setShouldAllowNull(false);
        fieldCARD.setIsPrimaryKey(true);
        fieldCARD.setUnique(false);
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

    public TableDefinition buildCREDITLINESTable() {
         TableDefinition table = new TableDefinition();
         table.setName("BUYER_CREDITLINES");

         FieldDefinition fieldID = new FieldDefinition();
         fieldID.setName("BUYER_ID");
         fieldID.setTypeName("NUMERIC");
         fieldID.setSize(15);
         fieldID.setShouldAllowNull(false);
         fieldID.setIsPrimaryKey(true);
         fieldID.setUnique(false);
         fieldID.setIsIdentity(false);
         fieldID.setForeignKeyFieldName("CMP3_BUYER.BUYER_ID");
         table.addField(fieldID);

         FieldDefinition fieldBANK = new FieldDefinition();
         fieldBANK.setName("BANK");
         fieldBANK.setTypeName("VARCHAR");
         fieldBANK.setSize(4);
         fieldBANK.setShouldAllowNull(false);
         fieldBANK.setIsPrimaryKey(true);
         fieldBANK.setUnique(false);
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

     public TableDefinition buildCUSTOMERTable() {
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

     public TableDefinition buildDEALERTable() {
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

     public TableDefinition buildDEPTTable() {
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

    public TableDefinition buildDEPT_EMPTable() {
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

    public TableDefinition buildEMPLOYEETable() {
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
        field5.setSize(0);
        field5.setShouldAllowNull(true);
        field5.setIsPrimaryKey(false);
        field5.setUnique(false);
        field5.setIsIdentity(false);
        table.addField(field5);

        FieldDefinition field6 = new FieldDefinition();
        field6.setName("END_TIME");
        field6.setTypeName("TIME");
        field6.setSize(0);
        field6.setShouldAllowNull(true);
        field6.setIsPrimaryKey(false);
        field6.setUnique(false);
        field6.setIsIdentity(false);
        table.addField(field6);

        FieldDefinition fieldOvertimeStart = new FieldDefinition();
        fieldOvertimeStart.setName("START_OVERTIME");
        fieldOvertimeStart.setTypeName("TIME");
        fieldOvertimeStart.setSize(0);
        fieldOvertimeStart.setShouldAllowNull(true);
        fieldOvertimeStart.setIsPrimaryKey(false);
        fieldOvertimeStart.setUnique(false);
        fieldOvertimeStart.setIsIdentity(false);
        table.addField(fieldOvertimeStart);

        FieldDefinition fieldOvertimeEnd = new FieldDefinition();
        fieldOvertimeEnd.setName("END_OVERTIME");
        fieldOvertimeEnd.setTypeName("TIME");
        fieldOvertimeEnd.setSize(0);
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

        // SECTION: FIELD
        FieldDefinition fieldFormerCompanyAddress = new FieldDefinition();
        fieldFormerCompanyAddress.setName("FORMER_COMPANY_ADDRESS_ID");
        fieldFormerCompanyAddress.setTypeName("NUMERIC");
        fieldFormerCompanyAddress.setSize(15);
        fieldFormerCompanyAddress.setShouldAllowNull(true);
        fieldFormerCompanyAddress.setIsPrimaryKey(false);
        fieldFormerCompanyAddress.setUnique(false);
        fieldFormerCompanyAddress.setIsIdentity(false);
        table.addField(fieldFormerCompanyAddress);

        // SECTION: FIELD
        FieldDefinition fieldCompanyAddress = new FieldDefinition();
        fieldCompanyAddress.setName("COMPANYADDRESS_ADDRESS_ID");
        fieldCompanyAddress.setTypeName("NUMERIC");
        fieldCompanyAddress.setSize(15);
        fieldCompanyAddress.setShouldAllowNull(true);
        fieldCompanyAddress.setIsPrimaryKey(false);
        fieldCompanyAddress.setUnique(false);
        fieldCompanyAddress.setIsIdentity(false);
        table.addField(fieldCompanyAddress);

        FieldDefinition fieldHugeProj = new FieldDefinition();
        fieldHugeProj.setName("HUGE_PROJ_ID");
        fieldHugeProj.setTypeName("NUMERIC");
        fieldHugeProj.setSize(15);
        fieldHugeProj.setShouldAllowNull(true);
        fieldHugeProj.setIsPrimaryKey(false);
        fieldHugeProj.setUnique(false);
        fieldHugeProj.setIsIdentity(false);
        fieldHugeProj.setForeignKeyFieldName("CMP3_PROJECT.PROJ_ID");
        table.addField(fieldHugeProj);

        if (Boolean.valueOf(System.getProperty("sop"))) {
            FieldDefinition fieldSop = new FieldDefinition();
            fieldSop.setName("SOP");
            fieldSop.setTypeName("BLOB");
            fieldSop.setSize(0);
            fieldSop.setSubSize(0);
            fieldSop.setIsPrimaryKey(false);
            fieldSop.setIsIdentity(false);
            fieldSop.setUnique(false);
            fieldSop.setShouldAllowNull(true);
            table.addField(fieldSop);
        }

        return table;
    }

    public TableDefinition buildEQUIPMENTTable() {
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

    public TableDefinition buildGOLFERTable() {
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
        foreignKeyGOLFER_WORLDRANK.setName("CMP3_GOLFER_CMP3_WORLDRANK");
        foreignKeyGOLFER_WORLDRANK.setTargetTable("CMP3_WORLDRANK");
        foreignKeyGOLFER_WORLDRANK.addSourceField("WORLDRANK_ID");
        foreignKeyGOLFER_WORLDRANK.addTargetField("ID");
        table.addForeignKeyConstraint(foreignKeyGOLFER_WORLDRANK);

        return table;
    }

    public TableDefinition buildGolfer_SPONSORDOLLARSTable() {
        TableDefinition table = new TableDefinition();
        table.setName("Golfer_SPONSORDOLLARS");

        FieldDefinition IDfield = new FieldDefinition();
        IDfield.setName("WORLDRANK_ID");
        IDfield.setTypeName("NUMERIC");
        IDfield.setSize(15);
        IDfield.setShouldAllowNull(false);
        IDfield.setIsPrimaryKey(true);
        IDfield.setUnique(false);
        IDfield.setIsIdentity(false);
        table.addField(IDfield);

        FieldDefinition sponsorValueField = new FieldDefinition();
        sponsorValueField.setName("SPONSOR_VALUE");
        sponsorValueField.setTypeName("NUMERIC");
        sponsorValueField.setSize(18);
        sponsorValueField.setShouldAllowNull(true);
        sponsorValueField.setIsPrimaryKey(false);
        sponsorValueField.setUnique(false);
        sponsorValueField.setIsIdentity(false);
        table.addField(sponsorValueField);

        FieldDefinition sponsorNameField = new FieldDefinition();
        sponsorNameField.setName("SPONSOR_NAME");
        sponsorNameField.setTypeName("VARCHAR2");
        sponsorNameField.setSize(100);
        sponsorNameField.setShouldAllowNull(false);
        sponsorNameField.setIsPrimaryKey(true);
        sponsorNameField.setUnique(false);
        sponsorNameField.setIsIdentity(false);
        table.addField(sponsorNameField);

        return table;
    }

    public TableDefinition buildEQUIPMENTCODETable() {
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

    public TableDefinition buildHUGEPROJECTTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_HPROJECT");

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
        field1.setName("EVANGELIST_ID");
        field1.setTypeName("NUMERIC");
        field1.setSize(15);
        field1.setShouldAllowNull(true);
        field1.setIsPrimaryKey(false);
        field1.setUnique(false);
        field1.setIsIdentity(false);
        field1.setForeignKeyFieldName("CMP3_EMPLOYEE.EMP_ID");
        table.addField(field1);

        return table;
    }

    public TableDefinition buildLARGEPROJECTTable() {
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

    public TableDefinition buildLOOTTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_LOOT");

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

        FieldDefinition fieldQUANTITY = new FieldDefinition();
        fieldQUANTITY.setName("QTY_VALUE");
        fieldQUANTITY.setTypeName("NUMERIC");
        fieldQUANTITY.setSize(15);
        fieldQUANTITY.setIsPrimaryKey(false);
        fieldQUANTITY.setIsIdentity(false);
        fieldQUANTITY.setUnique(false);
        fieldQUANTITY.setShouldAllowNull(true);
        table.addField(fieldQUANTITY);

        FieldDefinition fieldCOSTVALUE = new FieldDefinition();
        fieldCOSTVALUE.setName("COST_VALUE");
        fieldCOSTVALUE.setTypeName("NUMERIC");
        fieldCOSTVALUE.setSize(15);
        fieldCOSTVALUE.setIsPrimaryKey(false);
        fieldCOSTVALUE.setIsIdentity(false);
        fieldCOSTVALUE.setUnique(false);
        fieldCOSTVALUE.setShouldAllowNull(true);
        table.addField(fieldCOSTVALUE);

        return table;
    }

    public TableDefinition buildMANTable() {
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

    public TableDefinition buildPARTNERLINKTable() {
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

    public TableDefinition buildPHONENUMBERSTATUSTable() {
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

    public TableDefinition buildPHONENUMBERTable() {
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

    public TableDefinition buildPLATINUMBUYERTable() {
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

    public TableDefinition buildPROJECT_EMPTable() {
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

    public TableDefinition buildPROJECT_PROPSTable() {
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

    public TableDefinition buildPROJECTTable() {
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

        if (Boolean.valueOf(System.getProperty("sop"))) {
            FieldDefinition fieldSop = new FieldDefinition();
            fieldSop.setName("SOP");
            fieldSop.setTypeName("BLOB");
            fieldSop.setSize(0);
            fieldSop.setSubSize(0);
            fieldSop.setIsPrimaryKey(false);
            fieldSop.setIsIdentity(false);
            fieldSop.setUnique(false);
            fieldSop.setShouldAllowNull(true);
            table.addField(fieldSop);
        }

        return table;
    }

    public TableDefinition buildRESPONSTable() {
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

    public TableDefinition buildSALARYTable() {
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

    public TableDefinition buildRABBITTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_AC_RABBIT");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(true);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(true);
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

    public TableDefinition buildRABBITFOOTTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_AC_RABBIT_FOOT");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(true);
        fieldID.setIsIdentity(true);
        fieldID.setUnique(false);
        table.addField(fieldID);

        FieldDefinition fieldRABBITID = new FieldDefinition();
        fieldRABBITID.setName("RABBIT_ID");
        fieldRABBITID.setTypeName("NUMERIC");
        fieldRABBITID.setSize(15);
        fieldRABBITID.setShouldAllowNull(true);
        fieldRABBITID.setIsPrimaryKey(false);
        fieldRABBITID.setIsIdentity(false);
        fieldRABBITID.setUnique(false);
        table.addField(fieldRABBITID);

        FieldDefinition fieldNAME = new FieldDefinition();
        fieldNAME.setName("CAPTION");
        fieldNAME.setTypeName("VARCHAR2");
        fieldNAME.setSize(60);
        fieldNAME.setSubSize(0);
        fieldNAME.setShouldAllowNull(true);
        fieldNAME.setIsPrimaryKey(false);
        fieldNAME.setIsIdentity(false);
        fieldNAME.setUnique(false);
        table.addField(fieldNAME);

        return table;
    }

    public TableDefinition buildFOODTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_AC_FOOD");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("F_ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(true);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(true);
        table.addField(fieldID);

        FieldDefinition fieldDTYPE = new FieldDefinition();
        fieldDTYPE.setName("DTYPE");
        fieldDTYPE.setTypeName("VARCHAR");
        fieldDTYPE.setSize(10);
        fieldDTYPE.setShouldAllowNull(true);
        fieldDTYPE.setIsPrimaryKey(false);
        fieldDTYPE.setUnique(false);
        fieldDTYPE.setIsIdentity(false);
        table.addField(fieldDTYPE);

        return table;
    }

    public TableDefinition buildSANDWICHTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_AC_SANDWICH");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("S_ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(true);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(true);
        fieldID.setForeignKeyFieldName("JPA_AC_FOOD.F_ID");
        table.addField(fieldID);

        FieldDefinition fieldNAME = new FieldDefinition();
        fieldNAME.setName("S_NAME");
        fieldNAME.setTypeName("VARCHAR2");
        fieldNAME.setSize(60);
        fieldNAME.setSubSize(0);
        fieldNAME.setIsPrimaryKey(false);
        fieldNAME.setIsIdentity(false);
        fieldNAME.setUnique(false);
        fieldNAME.setShouldAllowNull(true);
        table.addField(fieldNAME);

        FieldDefinition fieldDESCRIPTION = new FieldDefinition();
        fieldDESCRIPTION.setName("S_DESCRIPTION");
        fieldDESCRIPTION.setTypeName("VARCHAR2");
        fieldDESCRIPTION.setSize(120);
        fieldDESCRIPTION.setSubSize(0);
        fieldDESCRIPTION.setIsPrimaryKey(false);
        fieldDESCRIPTION.setIsIdentity(false);
        fieldDESCRIPTION.setUnique(false);
        fieldDESCRIPTION.setShouldAllowNull(true);
        table.addField(fieldDESCRIPTION);

        return table;
    }

    public TableDefinition buildPERSONTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_AC_PERSON");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("P_ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(true);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(true);
        table.addField(fieldID);

        FieldDefinition fieldDTYPE = new FieldDefinition();
        fieldDTYPE.setName("DTYPE");
        fieldDTYPE.setTypeName("VARCHAR");
        fieldDTYPE.setSize(10);
        fieldDTYPE.setShouldAllowNull(true);
        fieldDTYPE.setIsPrimaryKey(false);
        fieldDTYPE.setUnique(false);
        fieldDTYPE.setIsIdentity(false);
        table.addField(fieldDTYPE);

        return table;
    }

    public TableDefinition buildEATERTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_AC_EATER");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("E_ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(true);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(true);
        fieldID.setForeignKeyFieldName("JPA_AC_PERSON.P_ID");
        table.addField(fieldID);

        FieldDefinition fieldNAME = new FieldDefinition();
        fieldNAME.setName("E_NAME");
        fieldNAME.setTypeName("VARCHAR2");
        fieldNAME.setSize(60);
        fieldNAME.setSubSize(0);
        fieldNAME.setIsPrimaryKey(false);
        fieldNAME.setIsIdentity(false);
        fieldNAME.setUnique(false);
        fieldNAME.setShouldAllowNull(true);
        table.addField(fieldNAME);

        FieldDefinition fieldSANDWICH = new FieldDefinition();
        fieldSANDWICH.setName("SANDWICH_ID");
        fieldSANDWICH.setTypeName("NUMERIC");
        fieldSANDWICH.setSize(15);
        fieldSANDWICH.setShouldAllowNull(true);
        fieldSANDWICH.setIsPrimaryKey(false);
        fieldSANDWICH.setUnique(false);
        fieldSANDWICH.setIsIdentity(false);
        //fieldSANDWICH.setForeignKeyFieldName("JPA_AC_SANDWICH.S_ID");
        table.addField(fieldSANDWICH);

        return table;
    }

    public TableDefinition buildSCHOOLTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_AC_SCHOOL");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(true);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(true);
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

    public TableDefinition buildSTUDENTTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_AC_STUDENT");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(true);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(true);
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

        FieldDefinition fieldSCHOOL_ID = new FieldDefinition();
        fieldSCHOOL_ID.setName("SCHOOL_ID");
        fieldSCHOOL_ID.setTypeName("NUMERIC");
        fieldSCHOOL_ID.setSize(15);
        fieldSCHOOL_ID.setIsPrimaryKey(false);
        fieldSCHOOL_ID.setIsIdentity(false);
        fieldSCHOOL_ID.setUnique(false);
        fieldSCHOOL_ID.setShouldAllowNull(false);
        fieldSCHOOL_ID.setForeignKeyFieldName("JPA_AC_SCHOOL.ID");
        table.addField(fieldSCHOOL_ID);

        return table;
    }

    public TableDefinition buildBOLTTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_AC_BOLT");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(true);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(true);
        table.addField(fieldID);

        FieldDefinition fieldNUT_ID = new FieldDefinition();
        fieldNUT_ID.setName("NUT_ID");
        fieldNUT_ID.setTypeName("NUMERIC");
        fieldNUT_ID.setSize(15);
        fieldNUT_ID.setIsPrimaryKey(false);
        fieldNUT_ID.setIsIdentity(false);
        fieldNUT_ID.setUnique(false);
        fieldNUT_ID.setShouldAllowNull(false);
        fieldNUT_ID.setForeignKeyFieldName("JPA_AC_NUT.ID");
        table.addField(fieldNUT_ID);

        return table;
    }

    public TableDefinition buildNUTTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_AC_NUT");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(true);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(true);
        table.addField(fieldID);

        FieldDefinition fieldCOLOR = new FieldDefinition();
        fieldCOLOR.setName("COLOR");
        fieldCOLOR.setTypeName("VARCHAR2");
        fieldCOLOR.setSize(60);
        fieldCOLOR.setSubSize(0);
        fieldCOLOR.setIsPrimaryKey(false);
        fieldCOLOR.setIsIdentity(false);
        fieldCOLOR.setUnique(false);
        fieldCOLOR.setShouldAllowNull(true);
        table.addField(fieldCOLOR);

        FieldDefinition fieldSIZE = new FieldDefinition();
        fieldSIZE.setName("B_SIZE");
        fieldSIZE.setTypeName("NUMERIC");
        fieldSIZE.setSize(15);
        fieldSIZE.setShouldAllowNull(true);
        fieldSIZE.setIsPrimaryKey(false);
        fieldSIZE.setUnique(false);
        fieldSIZE.setIsIdentity(false);
        table.addField(fieldSIZE);

        return table;
    }

    public TableDefinition buildVEGETABLETable() {
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

        FieldDefinition fieldTYPE = new FieldDefinition();
        fieldTYPE.setName("TYPE");
        fieldTYPE.setTypeName("CHAR");
        fieldTYPE.setSize(1);
        fieldTYPE.setShouldAllowNull(true);
        table.addField(fieldTYPE);

        return table;
    }

    public TableDefinition buildVIOLATIONTable() {
        TableDefinition table = new TableDefinition();
        table.setName("VIOLATION");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("VARCHAR");
        fieldID.setSize(2);
        fieldID.setSubSize(0);
        fieldID.setIsPrimaryKey(true);
        fieldID.setIsIdentity(true);
        fieldID.setUnique(false);
        fieldID.setShouldAllowNull(false);
        table.addField(fieldID);

        return table;
    }

    public TableDefinition buildVIOLATIONCODETable() {
        TableDefinition table = new TableDefinition();
        table.setName("VIOLATION_CODE");

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

        FieldDefinition fieldDESCRIP = new FieldDefinition();
        fieldDESCRIP.setName("DESCRIP");
        fieldDESCRIP.setTypeName("VARCHAR");
        fieldDESCRIP.setSize(100);
        fieldDESCRIP.setShouldAllowNull(true);
        fieldDESCRIP.setIsPrimaryKey(false);
        fieldDESCRIP.setUnique(false);
        fieldDESCRIP.setIsIdentity(false);
        table.addField(fieldDESCRIP);

        return table;
    }

    public TableDefinition buildVIOLATIONCODESTable() {
        TableDefinition table = new TableDefinition();
        table.setName("VIOLATION_CODES");

        FieldDefinition fieldVIOLATIONID = new FieldDefinition();
        fieldVIOLATIONID.setName("VIOLATION_ID");
        fieldVIOLATIONID.setTypeName("VARCHAR");
        fieldVIOLATIONID.setSize(2);
        fieldVIOLATIONID.setSubSize(0);
        fieldVIOLATIONID.setIsPrimaryKey(false);
        fieldVIOLATIONID.setIsIdentity(false);
        fieldVIOLATIONID.setUnique(false);
        fieldVIOLATIONID.setShouldAllowNull(false);
        fieldVIOLATIONID.setForeignKeyFieldName("VIOLATION.ID");
        table.addField(fieldVIOLATIONID);

        FieldDefinition fieldVIOLATIONCODEID = new FieldDefinition();
        fieldVIOLATIONCODEID.setName("VIOLATION_CODE_ID");
        fieldVIOLATIONCODEID.setTypeName("NUMERIC");
        fieldVIOLATIONCODEID.setSize(15);
        fieldVIOLATIONCODEID.setSubSize(0);
        fieldVIOLATIONCODEID.setIsPrimaryKey(false);
        fieldVIOLATIONCODEID.setIsIdentity(false);
        fieldVIOLATIONCODEID.setUnique(false);
        fieldVIOLATIONCODEID.setShouldAllowNull(false);
        fieldVIOLATIONCODEID.setForeignKeyFieldName("VIOLATION_CODE.ID");
        table.addField(fieldVIOLATIONCODEID);

        return table;
    }

    public TableDefinition buildWOMANTable() {
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

    public TableDefinition buildWORKWEEKTable() {
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

    public TableDefinition buildWORLDRANKTable() {
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

    public TableDefinition buildREADONLYISOLATED() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_READONLY_ISOLATED");

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
        fieldCODE.setSize(10);
        fieldCODE.setSubSize(0);
        fieldCODE.setIsPrimaryKey(false);
        fieldCODE.setIsIdentity(false);
        fieldCODE.setUnique(false);
        fieldCODE.setShouldAllowNull(false);
        table.addField(fieldCODE);

        return table;
    }

    public TableDefinition buildENTITYBTable() {
         TableDefinition table = new TableDefinition();
         table.setName("ADV_ENTYB");

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

    public TableDefinition buildENTITYATable() {
         TableDefinition table = new TableDefinition();
         table.setName("ADV_ENTYA");

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

         FieldDefinition fieldEntyB = new FieldDefinition();
         fieldEntyB.setName("ENTYB_ID");
         fieldEntyB.setTypeName("NUMERIC");
         fieldEntyB.setSize(15);
         fieldEntyB.setSubSize(0);
         fieldEntyB.setIsPrimaryKey(false);
         fieldEntyB.setIsIdentity(false);
         fieldEntyB.setUnique(false);
         fieldEntyB.setForeignKeyFieldName("ADV_ENTYB.ID");
         table.addField(fieldEntyB);

         FieldDefinition fieldEntyC = new FieldDefinition();
         fieldEntyC.setName("ENTYC_ID");
         fieldEntyC.setTypeName("NUMERIC");
         fieldEntyC.setSize(15);
         fieldEntyC.setSubSize(0);
         fieldEntyC.setIsPrimaryKey(false);
         fieldEntyC.setIsIdentity(false);
         fieldEntyC.setUnique(false);
         fieldEntyC.setForeignKeyFieldName("ADV_ENTYC.ID");
         table.addField(fieldEntyC);

         return table;
     }

    public TableDefinition buildENTITYDTable() {
         TableDefinition table = new TableDefinition();
         table.setName("ADV_ENTYD");

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

    public TableDefinition buildENTITYCTable() {
         TableDefinition table = new TableDefinition();
         table.setName("ADV_ENTYC");

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

     public TableDefinition buildADVENTITYAENTITYDTable() {
        TableDefinition table = new TableDefinition();

        table.setName("ADV_ENTYA_ADV_ENTYD");

        FieldDefinition fieldORDERID = new FieldDefinition();
        fieldORDERID.setName("EntyA_ID");
        fieldORDERID.setTypeName("NUMERIC");
        fieldORDERID.setSize(15);
        fieldORDERID.setShouldAllowNull(false);
        fieldORDERID.setIsPrimaryKey(false);
        fieldORDERID.setUnique(false);
        fieldORDERID.setIsIdentity(false);
        fieldORDERID.setForeignKeyFieldName("ADV_ENTYA.ID");
        table.addField(fieldORDERID);

        FieldDefinition fieldAUDITORID = new FieldDefinition();
        fieldAUDITORID.setName("entyDs_ID");
        fieldAUDITORID.setTypeName("NUMERIC");
        fieldAUDITORID.setSize(15);
        fieldAUDITORID.setShouldAllowNull(false);
        fieldAUDITORID.setIsPrimaryKey(false);
        fieldAUDITORID.setUnique(true);
        fieldAUDITORID.setIsIdentity(false);
        fieldAUDITORID.setForeignKeyFieldName("ADV_ENTYD.ID");
        table.addField(fieldAUDITORID);

        return table;
    }

     public TableDefinition buildENTITYETable() {
         TableDefinition table = new TableDefinition();
         table.setName("ADV_ENTYE");

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

     public TableDefinition buildADVENTITYAENTITYETable() {
        TableDefinition table = new TableDefinition();

        table.setName("ADV_ENTYA_ADV_ENTYE");

        FieldDefinition fieldORDERID = new FieldDefinition();
        fieldORDERID.setName("EntyA_ID");
        fieldORDERID.setTypeName("NUMERIC");
        fieldORDERID.setSize(15);
        fieldORDERID.setShouldAllowNull(false);
        fieldORDERID.setIsPrimaryKey(false);
        fieldORDERID.setUnique(false);
        fieldORDERID.setIsIdentity(false);
        fieldORDERID.setForeignKeyFieldName("ADV_ENTYA.ID");
        table.addField(fieldORDERID);

        FieldDefinition fieldAUDITORID = new FieldDefinition();
        fieldAUDITORID.setName("entyEs_ID");
        fieldAUDITORID.setTypeName("NUMERIC");
        fieldAUDITORID.setSize(15);
        fieldAUDITORID.setShouldAllowNull(false);
        fieldAUDITORID.setIsPrimaryKey(false);
        fieldAUDITORID.setUnique(true);
        fieldAUDITORID.setIsIdentity(false);
        fieldAUDITORID.setForeignKeyFieldName("ADV_ENTYE.ID");
        table.addField(fieldAUDITORID);

        return table;
    }

    public TableDefinition buildADVSIMPLEENTITYTable() {
        TableDefinition table = new TableDefinition();

        table.setName("ADV_SIMPLE_ENTITY");

        FieldDefinition fieldSIMPLEID = new FieldDefinition();
        fieldSIMPLEID.setName("SIMPLE_ID");
        fieldSIMPLEID.setTypeName("NUMERIC");
        fieldSIMPLEID.setSize(15);
        fieldSIMPLEID.setShouldAllowNull(false);
        fieldSIMPLEID.setIsPrimaryKey(true);
        fieldSIMPLEID.setUnique(false);
        fieldSIMPLEID.setIsIdentity(true);
        table.addField(fieldSIMPLEID);

        FieldDefinition fieldDESCRIPTION = new FieldDefinition();
        fieldDESCRIPTION.setName("DESCRIPTION");
        fieldDESCRIPTION.setTypeName("VARCHAR2");
        fieldDESCRIPTION.setSize(100);
        fieldDESCRIPTION.setShouldAllowNull(true);
        fieldDESCRIPTION.setIsPrimaryKey(false);
        fieldDESCRIPTION.setUnique(false);
        fieldDESCRIPTION.setIsIdentity(false);
        table.addField(fieldDESCRIPTION);

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

    public TableDefinition buildADVECSIMPLETable() {
         TableDefinition table = new TableDefinition();

        table.setName("ADV_EC_SIMPLE");

        FieldDefinition fieldSIMPLEID = new FieldDefinition();
        fieldSIMPLEID.setName("SIMPLE_ID");
        fieldSIMPLEID.setTypeName("NUMERIC");
        fieldSIMPLEID.setSize(15);
        fieldSIMPLEID.setShouldAllowNull(false);
        fieldSIMPLEID.setIsPrimaryKey(false);
        fieldSIMPLEID.setUnique(false);
        fieldSIMPLEID.setIsIdentity(false);
        fieldSIMPLEID.setForeignKeyFieldName("ADV_SIMPLE_ENTITY.SIMPLE_ID");
        table.addField(fieldSIMPLEID);

        FieldDefinition fieldDESCRIPTION = new FieldDefinition();
        fieldDESCRIPTION.setName("SIMPLE_NATURE");
        fieldDESCRIPTION.setTypeName("VARCHAR2");
        fieldDESCRIPTION.setSize(100);
        fieldDESCRIPTION.setShouldAllowNull(false);
        fieldDESCRIPTION.setIsPrimaryKey(false);
        fieldDESCRIPTION.setUnique(false);
        fieldDESCRIPTION.setIsIdentity(false);
        table.addField(fieldDESCRIPTION);

        return table;

    }

    public TableDefinition buildADVSIMPLELANGUAGETable() {
        TableDefinition table = new TableDefinition();

        table.setName("ADV_SIMPLE_LANGUAGE");

        FieldDefinition fieldCODE = new FieldDefinition();
        fieldCODE.setName("CODE");
        fieldCODE.setTypeName("VARCHAR2");
        fieldCODE.setSize(2);
        fieldCODE.setShouldAllowNull(false);
        fieldCODE.setIsPrimaryKey(true);
        fieldCODE.setUnique(false);
        fieldCODE.setIsIdentity(true);
        table.addField(fieldCODE);

        FieldDefinition fieldDESCRIPTION = new FieldDefinition();
        fieldDESCRIPTION.setName("DESCRIPTION");
        fieldDESCRIPTION.setTypeName("VARCHAR2");
        fieldDESCRIPTION.setSize(100);
        fieldDESCRIPTION.setShouldAllowNull(true);
        fieldDESCRIPTION.setIsPrimaryKey(false);
        fieldDESCRIPTION.setUnique(false);
        fieldDESCRIPTION.setIsIdentity(false);
        table.addField(fieldDESCRIPTION);

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

    public TableDefinition buildADVSIMPLEENTITYLANGUAGETable() {
        TableDefinition table = new TableDefinition();

        table.setName("ADV_SIMPLE_ENTITY_LANGUAGE");

        FieldDefinition fieldSIMPLEID = new FieldDefinition();
        fieldSIMPLEID.setName("SIMPLE_ID");
        fieldSIMPLEID.setTypeName("NUMERIC");
        fieldSIMPLEID.setSize(15);
        fieldSIMPLEID.setShouldAllowNull(false);
        fieldSIMPLEID.setIsPrimaryKey(false);
        fieldSIMPLEID.setUnique(false);
        fieldSIMPLEID.setIsIdentity(false);
        fieldSIMPLEID.setForeignKeyFieldName("ADV_SIMPLE_ENTITY.SIMPLE_ID");
        table.addField(fieldSIMPLEID);

        FieldDefinition fieldDESCRIPTION = new FieldDefinition();
        fieldDESCRIPTION.setName("LANG_DESCRIPTION");
        fieldDESCRIPTION.setTypeName("VARCHAR2");
        fieldDESCRIPTION.setSize(255);
        fieldDESCRIPTION.setShouldAllowNull(false);
        fieldDESCRIPTION.setIsPrimaryKey(false);
        fieldDESCRIPTION.setUnique(false);
        fieldDESCRIPTION.setIsIdentity(false);
        table.addField(fieldDESCRIPTION);

        FieldDefinition fieldLANG_CODE = new FieldDefinition();
        fieldLANG_CODE.setName("LANG_CODE");
        fieldLANG_CODE.setTypeName("VARCHAR2");
        fieldLANG_CODE.setSize(2);
        fieldLANG_CODE.setShouldAllowNull(false);
        fieldLANG_CODE.setIsPrimaryKey(false);
        fieldLANG_CODE.setUnique(false);
        fieldLANG_CODE.setIsIdentity(false);
        table.addField(fieldLANG_CODE);

        return table;
    }


    public TableDefinition buildCMP3_JIGSAWTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_JIGSAW");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMBER");
        fieldID.setSize(15);
        fieldID.setSubSize(0);
        fieldID.setIsPrimaryKey(true);
        fieldID.setIsIdentity(false);
        fieldID.setUnique(false);
        fieldID.setShouldAllowNull(false);
        table.addField(fieldID);

        return table;
    }

    public TableDefinition buildCMP3_JIGSAW_PIECETable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_JIGSAW_PIECE");

        FieldDefinition fieldFK_JIGSAW_ID = new FieldDefinition();
        fieldFK_JIGSAW_ID.setName("FK_JIGSAW_ID");
        fieldFK_JIGSAW_ID.setTypeName("NUMBER");
        fieldFK_JIGSAW_ID.setSize(15);
        fieldFK_JIGSAW_ID.setSubSize(0);
        fieldFK_JIGSAW_ID.setIsPrimaryKey(false);
        fieldFK_JIGSAW_ID.setIsIdentity(false);
        fieldFK_JIGSAW_ID.setUnique(false);
        fieldFK_JIGSAW_ID.setShouldAllowNull(true);
        table.addField(fieldFK_JIGSAW_ID);

        FieldDefinition fieldPIECE_NUMBER = new FieldDefinition();
        fieldPIECE_NUMBER.setName("PIECE_NUMBER");
        fieldPIECE_NUMBER.setTypeName("NUMBER");
        fieldPIECE_NUMBER.setSize(10);
        fieldPIECE_NUMBER.setSubSize(0);
        fieldPIECE_NUMBER.setIsPrimaryKey(false);
        fieldPIECE_NUMBER.setIsIdentity(false);
        fieldPIECE_NUMBER.setUnique(false);
        fieldPIECE_NUMBER.setShouldAllowNull(true);
        table.addField(fieldPIECE_NUMBER);

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMBER");
        fieldID.setSize(15);
        fieldID.setSubSize(0);
        fieldID.setIsPrimaryKey(true);
        fieldID.setIsIdentity(false);
        fieldID.setUnique(false);
        fieldID.setShouldAllowNull(false);
        table.addField(fieldID);

        ForeignKeyConstraint foreignKeyCMP3_JIGSAW_PIECE_CMP3_JIGSAW = new ForeignKeyConstraint();
        foreignKeyCMP3_JIGSAW_PIECE_CMP3_JIGSAW.setName("CMP3_JIGSAW_PIECE_CMP3_JIGSAW");
        foreignKeyCMP3_JIGSAW_PIECE_CMP3_JIGSAW.setTargetTable("CMP3_JIGSAW");
        foreignKeyCMP3_JIGSAW_PIECE_CMP3_JIGSAW.addSourceField("FK_JIGSAW_ID");
        foreignKeyCMP3_JIGSAW_PIECE_CMP3_JIGSAW.addTargetField("ID");
        table.addForeignKeyConstraint(foreignKeyCMP3_JIGSAW_PIECE_CMP3_JIGSAW);

        return table;
    }

    public TableDefinition buildCMP3_ROOMTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_ROOM");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMBER");
        fieldID.setSize(15);
        fieldID.setIsPrimaryKey(true);
        fieldID.setIsIdentity(false);
        fieldID.setUnique(false);
        fieldID.setShouldAllowNull(false);
        table.addField(fieldID);

        FieldDefinition fieldWIDTH = new FieldDefinition();
        fieldWIDTH.setName("WIDTH");
        fieldWIDTH.setTypeName("NUMBER");
        fieldWIDTH.setSize(10);
        fieldWIDTH.setIsPrimaryKey(false);
        fieldWIDTH.setIsIdentity(false);
        fieldWIDTH.setUnique(false);
        fieldWIDTH.setShouldAllowNull(true);
        table.addField(fieldWIDTH);

        FieldDefinition fieldLENGTH = new FieldDefinition();
        fieldLENGTH.setName("LENGTH");
        fieldLENGTH.setTypeName("NUMBER");
        fieldLENGTH.setSize(10);
        fieldLENGTH.setIsPrimaryKey(false);
        fieldLENGTH.setIsIdentity(false);
        fieldLENGTH.setUnique(false);
        fieldLENGTH.setShouldAllowNull(true);
        table.addField(fieldLENGTH);

        FieldDefinition fieldHEIGHT = new FieldDefinition();
        fieldHEIGHT.setName("HEIGHT");
        fieldHEIGHT.setTypeName("NUMBER");
        fieldHEIGHT.setSize(10);
        fieldHEIGHT.setIsPrimaryKey(false);
        fieldHEIGHT.setIsIdentity(false);
        fieldHEIGHT.setUnique(false);
        fieldHEIGHT.setShouldAllowNull(true);
        table.addField(fieldHEIGHT);

        return table;
    }

    public TableDefinition buildCMP3_PRODUCTTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_PRODUCT");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMBER");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(true);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(false);
        table.addField(fieldID);

        FieldDefinition fieldNAME = new FieldDefinition();
        fieldNAME.setName("NAME");
        fieldNAME.setTypeName("VARCHAR");
        fieldNAME.setSize(32);
        fieldNAME.setShouldAllowNull(false);
        fieldNAME.setIsPrimaryKey(false);
        fieldNAME.setUnique(false);
        fieldNAME.setIsIdentity(false);
        table.addField(fieldNAME);

        FieldDefinition fieldCOUNTRYCODE = new FieldDefinition();
        fieldCOUNTRYCODE.setName("COUNTRY_CODE");
        fieldCOUNTRYCODE.setTypeName("VARCHAR");
        fieldCOUNTRYCODE.setSize(3);
        fieldCOUNTRYCODE.setShouldAllowNull(false);
        fieldCOUNTRYCODE.setIsPrimaryKey(false);
        fieldCOUNTRYCODE.setUnique(false);
        fieldCOUNTRYCODE.setIsIdentity(false);
        table.addField(fieldCOUNTRYCODE);

        FieldDefinition fieldBARCODE1 = new FieldDefinition();
        fieldBARCODE1.setName("BARCODE1");
        fieldBARCODE1.setTypeName("VARCHAR");
        fieldBARCODE1.setSize(32);
        fieldBARCODE1.setShouldAllowNull(true);
        fieldBARCODE1.setIsPrimaryKey(false);
        fieldBARCODE1.setUnique(false);
        fieldBARCODE1.setIsIdentity(false);
        table.addField(fieldBARCODE1);

        FieldDefinition fieldBARCODE2 = new FieldDefinition();
        fieldBARCODE2.setName("BARCODE2");
        fieldBARCODE2.setTypeName("VARCHAR");
        fieldBARCODE2.setSize(32);
        fieldBARCODE2.setShouldAllowNull(true);
        fieldBARCODE2.setIsPrimaryKey(false);
        fieldBARCODE2.setUnique(false);
        fieldBARCODE2.setIsIdentity(false);
        table.addField(fieldBARCODE2);

        return table;
    }

    //Bug#457480
    public TableDefinition buildCMP3_HINGETable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_HINGE");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMBER");
        fieldID.setSize(15);
        fieldID.setIsPrimaryKey(true);
        fieldID.setIsIdentity(false);
        fieldID.setUnique(false);
        fieldID.setShouldAllowNull(false);
        table.addField(fieldID);

        FieldDefinition fieldDOOR_ID = new FieldDefinition();
        fieldDOOR_ID.setName("DOOR_ID");
        fieldDOOR_ID.setTypeName("NUMBER");
        fieldDOOR_ID.setSize(15);
        fieldDOOR_ID.setIsPrimaryKey(true);
        fieldDOOR_ID.setIsIdentity(false);
        fieldDOOR_ID.setUnique(false);
        fieldDOOR_ID.setShouldAllowNull(false);
        table.addField(fieldDOOR_ID);

        ForeignKeyConstraint foreignKeyCMP3_HINGE_CMP3_DOOR = new ForeignKeyConstraint();
        foreignKeyCMP3_HINGE_CMP3_DOOR.setName("CMP3_HINGE_CMP3_DOOR");
        foreignKeyCMP3_HINGE_CMP3_DOOR.setTargetTable("CMP3_DOOR");
        foreignKeyCMP3_HINGE_CMP3_DOOR.addSourceField("DOOR_ID");
        foreignKeyCMP3_HINGE_CMP3_DOOR.addTargetField("ID");
        table.addForeignKeyConstraint(foreignKeyCMP3_HINGE_CMP3_DOOR);

        return table;
    }

    public TableDefinition buildCMP3_DOORTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_DOOR");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMBER");
        fieldID.setSize(15);
        fieldID.setIsPrimaryKey(true);
        fieldID.setIsIdentity(false);
        fieldID.setUnique(false);
        fieldID.setShouldAllowNull(false);
        table.addField(fieldID);

        FieldDefinition fieldWIDTH = new FieldDefinition();
        fieldWIDTH.setName("WIDTH");
        fieldWIDTH.setTypeName("NUMBER");
        fieldWIDTH.setSize(10);
        fieldWIDTH.setIsPrimaryKey(false);
        fieldWIDTH.setIsIdentity(false);
        fieldWIDTH.setUnique(false);
        fieldWIDTH.setShouldAllowNull(true);
        table.addField(fieldWIDTH);

        FieldDefinition fieldHEIGHT = new FieldDefinition();
        fieldHEIGHT.setName("HEIGHT");
        fieldHEIGHT.setTypeName("NUMBER");
        fieldHEIGHT.setSize(10);
        fieldHEIGHT.setIsPrimaryKey(false);
        fieldHEIGHT.setIsIdentity(false);
        fieldHEIGHT.setUnique(false);
        fieldHEIGHT.setShouldAllowNull(true);
        table.addField(fieldHEIGHT);

        FieldDefinition fieldROOM_ID = new FieldDefinition();
        fieldROOM_ID.setName("ROOM_ID");
        fieldROOM_ID.setTypeName("NUMBER");
        fieldROOM_ID.setSize(15);
        fieldROOM_ID.setIsPrimaryKey(false);
        fieldROOM_ID.setIsIdentity(false);
        fieldROOM_ID.setUnique(false);
        fieldROOM_ID.setShouldAllowNull(true);
        table.addField(fieldROOM_ID);

        FieldDefinition fieldSALE_DATE = new FieldDefinition();
        fieldSALE_DATE.setName("SALE_DATE");
        fieldSALE_DATE.setTypeName("DATE");
        fieldSALE_DATE.setSize(23);
        fieldSALE_DATE.setShouldAllowNull(true);
        fieldSALE_DATE.setIsPrimaryKey(false);
        fieldSALE_DATE.setUnique(false);
        fieldSALE_DATE.setIsIdentity(false);
        table.addField(fieldSALE_DATE);

        ForeignKeyConstraint foreignKeyCMP3_DOOR_CMP3_ROOM = new ForeignKeyConstraint();
        foreignKeyCMP3_DOOR_CMP3_ROOM.setName("CMP3_DOOR_CMP3_ROOM");
        foreignKeyCMP3_DOOR_CMP3_ROOM.setTargetTable("CMP3_ROOM");
        foreignKeyCMP3_DOOR_CMP3_ROOM.addSourceField("ROOM_ID");
        foreignKeyCMP3_DOOR_CMP3_ROOM.addTargetField("ID");
        table.addForeignKeyConstraint(foreignKeyCMP3_DOOR_CMP3_ROOM);

        return table;
    }

    public static TableDefinition buildCmp3EmbedVisitorTable() {
        TableDefinition table = createTable("CMP3_EMBED_VISITOR");
        table.addField(createNumericPk("ID"));
        table.addField(createStringColumn("NAME"));
        table.addField(createStringColumn("CODE", 3, true));
        table.addField(createStringColumn("COUNTRY"));
        table.addField(createStringColumn("CONTINENT"));
        table.addField(createStringColumn("NOTE"));
        return table;
    }

    public TableDefinition buildCMP3_CANOETable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_CANOE");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMBER");
        fieldID.setSize(15);
        fieldID.setIsPrimaryKey(true);
        fieldID.setIsIdentity(false);
        fieldID.setUnique(false);
        fieldID.setShouldAllowNull(false);
        table.addField(fieldID);

        FieldDefinition fieldCOLOR = new FieldDefinition();
        fieldCOLOR.setName("COLOR");
        fieldCOLOR.setTypeName("VARCHAR");
        fieldCOLOR.setSize(32);
        fieldCOLOR.setShouldAllowNull(true);
        fieldCOLOR.setIsPrimaryKey(false);
        fieldCOLOR.setUnique(false);
        fieldCOLOR.setIsIdentity(false);
        table.addField(fieldCOLOR);

        FieldDefinition fieldLAKEID = new FieldDefinition();
        fieldLAKEID.setName("LAKE_ID");
        fieldLAKEID.setTypeName("NUMBER");
        fieldLAKEID.setSize(15);
        fieldLAKEID.setShouldAllowNull(true);
        fieldLAKEID.setIsPrimaryKey(false);
        fieldLAKEID.setUnique(false);
        fieldLAKEID.setIsIdentity(false);
        table.addField(fieldLAKEID);

        ForeignKeyConstraint fkCMP3_CANOE_CMP3_LAKE = new ForeignKeyConstraint();
        fkCMP3_CANOE_CMP3_LAKE.setName("CMP3_CANOE_CMP3_LAKE");
        fkCMP3_CANOE_CMP3_LAKE.setTargetTable("CMP3_LAKE");
        fkCMP3_CANOE_CMP3_LAKE.addSourceField("LAKE_ID");
        fkCMP3_CANOE_CMP3_LAKE.addTargetField("ID");
        table.addForeignKeyConstraint(fkCMP3_CANOE_CMP3_LAKE);

        return table;
    }

    public TableDefinition buildCMP3_LAKETable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_LAKE");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMBER");
        fieldID.setSize(15);
        fieldID.setIsPrimaryKey(true);
        fieldID.setIsIdentity(false);
        fieldID.setUnique(false);
        fieldID.setShouldAllowNull(false);
        table.addField(fieldID);

        FieldDefinition fieldNAME = new FieldDefinition();
        fieldNAME.setName("NAME");
        fieldNAME.setTypeName("VARCHAR");
        fieldNAME.setSize(32);
        fieldNAME.setShouldAllowNull(true);
        fieldNAME.setIsPrimaryKey(false);
        fieldNAME.setUnique(false);
        fieldNAME.setIsIdentity(false);
        table.addField(fieldNAME);

        return table;
    }
    
    public TableDefinition buildCMP3_OYSTERTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_OYSTER");
        
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMBER");
        fieldID.setSize(15);
        fieldID.setIsPrimaryKey(true);
        fieldID.setIsIdentity(false);
        fieldID.setUnique(false);
        fieldID.setShouldAllowNull(false);
        table.addField(fieldID);
        
        FieldDefinition fieldCOLOR = new FieldDefinition();
        fieldCOLOR.setName("COLOR");
        fieldCOLOR.setTypeName("VARCHAR");
        fieldCOLOR.setSize(32);
        fieldCOLOR.setShouldAllowNull(true);
        fieldCOLOR.setIsPrimaryKey(false);
        fieldCOLOR.setUnique(false);
        fieldCOLOR.setIsIdentity(false);
        table.addField(fieldCOLOR);
        
        return table;
    }
    
    public TableDefinition buildCMP3_PEARLTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_PEARL");
        
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMBER");
        fieldID.setSize(15);
        fieldID.setIsPrimaryKey(true);
        fieldID.setIsIdentity(false);
        fieldID.setUnique(false);
        fieldID.setShouldAllowNull(false);
        table.addField(fieldID);
        
        FieldDefinition fieldNAME = new FieldDefinition();
        fieldNAME.setName("NAME");
        fieldNAME.setTypeName("VARCHAR");
        fieldNAME.setSize(32);
        fieldNAME.setShouldAllowNull(true);
        fieldNAME.setIsPrimaryKey(false);
        fieldNAME.setUnique(false);
        fieldNAME.setIsIdentity(false);
        table.addField(fieldNAME);
        
        FieldDefinition fieldOYSTER_ID = new FieldDefinition();
        fieldOYSTER_ID.setName("OYSTER_ID");
        fieldOYSTER_ID.setTypeName("NUMBER");
        fieldOYSTER_ID.setSize(15);
        fieldOYSTER_ID.setIsPrimaryKey(false);
        fieldOYSTER_ID.setIsIdentity(false);
        fieldOYSTER_ID.setUnique(false);
        fieldOYSTER_ID.setShouldAllowNull(true);
        table.addField(fieldOYSTER_ID);
        
        return table;
    }
    
    public TableDefinition buildCMP3_PEARL_HISTTable() {
        TableDefinition table = buildCMP3_PEARLTable();
        table.setName(table.getName() + "_HIST");
        
        FieldDefinition fieldSTART = new FieldDefinition();
        fieldSTART.setName("START_DATE");
        fieldSTART.setTypeName("TIMESTAMP");
        fieldSTART.setIsPrimaryKey(true);
        fieldSTART.setIsIdentity(false);
        fieldSTART.setUnique(false);
        fieldSTART.setShouldAllowNull(false);
        table.addField(fieldSTART);
        
        FieldDefinition fieldEND = new FieldDefinition();
        fieldEND.setName("END_DATE");
        fieldEND.setTypeName("TIMESTAMP");
        fieldEND.setIsPrimaryKey(false);
        fieldEND.setIsIdentity(false);
        fieldEND.setUnique(false);
        fieldEND.setShouldAllowNull(true);
        table.addField(fieldEND);
        
        return table;
    }

    public TableDefinition buildJobTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JOB");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("BIGINT");
        fieldID.setSize(15);
        fieldID.setIsPrimaryKey(true);
        fieldID.setIsIdentity(false);
        fieldID.setUnique(false);
        fieldID.setShouldAllowNull(false);
        table.addField(fieldID);

        FieldDefinition fieldDTYPE = new FieldDefinition();
        fieldDTYPE.setName("DTYPE");
        fieldDTYPE.setTypeName("VARCHAR");
        fieldDTYPE.setSize(31);
        fieldDTYPE.setShouldAllowNull(true);
        fieldDTYPE.setIsPrimaryKey(false);
        fieldDTYPE.setUnique(false);
        fieldDTYPE.setIsIdentity(false);
        table.addField(fieldDTYPE);

        return table;
    }

    public TableDefinition buildEventTable() {
        TableDefinition table = new TableDefinition();
        table.setName("EVENT");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("BIGINT");
        fieldID.setSize(15);
        fieldID.setIsPrimaryKey(true);
        fieldID.setIsIdentity(false);
        fieldID.setUnique(false);
        fieldID.setShouldAllowNull(false);
        table.addField(fieldID);

        FieldDefinition fieldDATEF = new FieldDefinition();
        fieldDATEF.setName("DATEF");
        fieldDATEF.setTypeName("TIMESTAMP");
        fieldDATEF.setShouldAllowNull(true);
        fieldDATEF.setIsPrimaryKey(false);
        fieldDATEF.setUnique(false);
        fieldDATEF.setIsIdentity(false);
        table.addField(fieldDATEF);

        FieldDefinition fieldJOBID = new FieldDefinition();
        fieldJOBID.setName("JOB_ID");
        fieldJOBID.setTypeName("BIGINT");
        fieldJOBID.setSize(15);
        fieldJOBID.setIsPrimaryKey(false);
        fieldJOBID.setIsIdentity(false);
        fieldJOBID.setUnique(false);
        fieldJOBID.setShouldAllowNull(true);
        table.addField(fieldJOBID);

        ForeignKeyConstraint foreignKeyFK_EVENT_JOB_ID = new ForeignKeyConstraint();
        foreignKeyFK_EVENT_JOB_ID.setName("FK_EVENT_JOB_ID");
        foreignKeyFK_EVENT_JOB_ID.setTargetTable("JOB");
        foreignKeyFK_EVENT_JOB_ID.addSourceField("JOB_ID");
        foreignKeyFK_EVENT_JOB_ID.addTargetField("ID");
        table.addForeignKeyConstraint(foreignKeyFK_EVENT_JOB_ID);

        return table;
    }
    
    public TableDefinition buildCMP3_TODOLISTTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_TODOLIST");
        
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMBER");
        fieldID.setSize(15);
        fieldID.setIsPrimaryKey(true);
        fieldID.setIsIdentity(false);
        fieldID.setUnique(false);
        fieldID.setShouldAllowNull(false);
        table.addField(fieldID);
        
        FieldDefinition fieldNAME = new FieldDefinition();
        fieldNAME.setName("NAME");
        fieldNAME.setTypeName("VARCHAR");
        fieldNAME.setSize(32);
        fieldNAME.setShouldAllowNull(true);
        fieldNAME.setIsPrimaryKey(false);
        fieldNAME.setUnique(false);
        fieldNAME.setIsIdentity(false);
        table.addField(fieldNAME);
        
        return table;
    }
    
    public TableDefinition buildCMP3_TODOLISTITEMTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_TODOLISTITEM");
        
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("TODOLIST_ID");
        fieldID.setTypeName("NUMBER");
        fieldID.setSize(15);
        fieldID.setIsPrimaryKey(false);
        fieldID.setIsIdentity(false);
        fieldID.setUnique(false);
        fieldID.setShouldAllowNull(false);
        table.addField(fieldID);
        
        FieldDefinition fieldNAME = new FieldDefinition();
        fieldNAME.setName("ITEM_TEXT");
        fieldNAME.setTypeName("VARCHAR");
        fieldNAME.setSize(64);
        fieldNAME.setShouldAllowNull(true);
        fieldNAME.setIsPrimaryKey(false);
        fieldNAME.setUnique(false);
        fieldNAME.setIsIdentity(false);
        table.addField(fieldNAME);
        
        ForeignKeyConstraint fk_TODOLISTITEM_TODOLIST = new ForeignKeyConstraint();
        fk_TODOLISTITEM_TODOLIST.setName("FK_TODOLISTITEM_TODOLIST");
        fk_TODOLISTITEM_TODOLIST.setTargetTable("CMP3_TODOLIST");
        fk_TODOLISTITEM_TODOLIST.addSourceField("TODOLIST_ID");
        fk_TODOLISTITEM_TODOLIST.addTargetField("ID");
        table.addForeignKeyConstraint(fk_TODOLISTITEM_TODOLIST);
        
        return table;
    }
    
    public TableDefinition buildBILLTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_BILL");
        
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMBER");
        fieldID.setSize(19);
        fieldID.setIsPrimaryKey(true);
        fieldID.setIsIdentity(false);
        fieldID.setUnique(false);
        fieldID.setShouldAllowNull(false);
        table.addField(fieldID);
        
        FieldDefinition fieldORDERIDENTIFIER = new FieldDefinition();
        fieldORDERIDENTIFIER.setName("ORDERIDENTIFIER");
        fieldORDERIDENTIFIER.setTypeName("VARCHAR");
        fieldORDERIDENTIFIER.setSize(64);
        fieldORDERIDENTIFIER.setShouldAllowNull(true);
        fieldORDERIDENTIFIER.setIsPrimaryKey(false);
        fieldORDERIDENTIFIER.setUnique(false);
        fieldORDERIDENTIFIER.setIsIdentity(false);
        table.addField(fieldORDERIDENTIFIER);
        
        FieldDefinition fieldSTATUS = new FieldDefinition();
        fieldSTATUS.setName("STATUS");
        fieldSTATUS.setTypeName("VARCHAR");
        fieldSTATUS.setSize(64);
        fieldSTATUS.setShouldAllowNull(true);
        fieldSTATUS.setIsPrimaryKey(false);
        fieldSTATUS.setUnique(false);
        fieldSTATUS.setIsIdentity(false);
        table.addField(fieldSTATUS);
        
        return table;
    }
    
    public TableDefinition buildBILL_LINETable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_BILL_LINE");
        
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMBER");
        fieldID.setSize(19);
        fieldID.setIsPrimaryKey(true);
        fieldID.setIsIdentity(false);
        fieldID.setUnique(false);
        fieldID.setShouldAllowNull(false);
        table.addField(fieldID);
        
        FieldDefinition fieldQUANTITY = new FieldDefinition();
        fieldQUANTITY.setName("QUANTITY");
        fieldQUANTITY.setTypeName("NUMBER");
        fieldQUANTITY.setSize(19);
        fieldQUANTITY.setIsPrimaryKey(false);
        fieldQUANTITY.setIsIdentity(false);
        fieldQUANTITY.setUnique(false);
        fieldQUANTITY.setShouldAllowNull(false);
        table.addField(fieldQUANTITY);
        
        FieldDefinition fieldBILLID = new FieldDefinition();
        fieldBILLID.setName("BILL_ID");
        fieldBILLID.setTypeName("NUMBER");
        fieldBILLID.setSize(19);
        fieldBILLID.setIsPrimaryKey(false);
        fieldBILLID.setIsIdentity(false);
        fieldBILLID.setUnique(false);
        fieldBILLID.setShouldAllowNull(false);
        table.addField(fieldBILLID);
        
        ForeignKeyConstraint fk_JPA_BILL_LINE_BILL_ID = new ForeignKeyConstraint();
        fk_JPA_BILL_LINE_BILL_ID.setName("FK_JPA_BILL_LINE_BILL_ID");
        fk_JPA_BILL_LINE_BILL_ID.setTargetTable("JPA_BILL");
        fk_JPA_BILL_LINE_BILL_ID.addSourceField("BILL_ID");
        fk_JPA_BILL_LINE_BILL_ID.addTargetField("ID");
        table.addForeignKeyConstraint(fk_JPA_BILL_LINE_BILL_ID);
        
        return table;
    }
    
    public TableDefinition buildBILL_LINEITEMTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_BILL_LINEITEM");
        
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMBER");
        fieldID.setSize(19);
        fieldID.setIsPrimaryKey(true);
        fieldID.setIsIdentity(false);
        fieldID.setUnique(false);
        fieldID.setShouldAllowNull(false);
        table.addField(fieldID);
        
        FieldDefinition fieldITEMNAME = new FieldDefinition();
        fieldITEMNAME.setName("ITEMNAME");
        fieldITEMNAME.setTypeName("VARCHAR");
        fieldITEMNAME.setSize(255);
        fieldITEMNAME.setIsPrimaryKey(false);
        fieldITEMNAME.setIsIdentity(false);
        fieldITEMNAME.setUnique(false);
        fieldITEMNAME.setShouldAllowNull(false);
        table.addField(fieldITEMNAME);
        
        FieldDefinition fieldBILLLINE_ID = new FieldDefinition();
        fieldBILLLINE_ID.setName("BILLLINE_ID");
        fieldBILLLINE_ID.setTypeName("NUMBER");
        fieldBILLLINE_ID.setSize(19);
        fieldBILLLINE_ID.setIsPrimaryKey(false);
        fieldBILLLINE_ID.setIsIdentity(false);
        fieldBILLLINE_ID.setUnique(false);
        fieldBILLLINE_ID.setShouldAllowNull(false);
        table.addField(fieldBILLLINE_ID);
        
        ForeignKeyConstraint fk_JPA_BILL_LINE_ITEM_BILL_LINE_ID = new ForeignKeyConstraint();
        fk_JPA_BILL_LINE_ITEM_BILL_LINE_ID.setName("JPA_BILL_LINEITEM_BILLLINE_ID");
        fk_JPA_BILL_LINE_ITEM_BILL_LINE_ID.setTargetTable("JPA_BILL_LINE");
        fk_JPA_BILL_LINE_ITEM_BILL_LINE_ID.addSourceField("BILLLINE_ID");
        fk_JPA_BILL_LINE_ITEM_BILL_LINE_ID.addTargetField("ID");
        table.addForeignKeyConstraint(fk_JPA_BILL_LINE_ITEM_BILL_LINE_ID);
        
        return table;
    }
    
    public TableDefinition buildBILL_ACTIONTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_BILL_ACTION");
        
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMBER");
        fieldID.setSize(19);
        fieldID.setIsPrimaryKey(true);
        fieldID.setIsIdentity(false);
        fieldID.setUnique(false);
        fieldID.setShouldAllowNull(false);
        table.addField(fieldID);
        
        FieldDefinition fieldPRIORITY = new FieldDefinition();
        fieldPRIORITY.setName("PRIORITY");
        fieldPRIORITY.setTypeName("NUMBER");
        fieldPRIORITY.setSize(3);
        fieldPRIORITY.setIsPrimaryKey(false);
        fieldPRIORITY.setIsIdentity(false);
        fieldPRIORITY.setUnique(false);
        fieldPRIORITY.setShouldAllowNull(false);
        table.addField(fieldPRIORITY);
        
        FieldDefinition fieldBILLLINE_ID = new FieldDefinition();
        fieldBILLLINE_ID.setName("BILLLINE_ID");
        fieldBILLLINE_ID.setTypeName("NUMBER");
        fieldBILLLINE_ID.setSize(19);
        fieldBILLLINE_ID.setIsPrimaryKey(false);
        fieldBILLLINE_ID.setIsIdentity(false);
        fieldBILLLINE_ID.setUnique(false);
        fieldBILLLINE_ID.setShouldAllowNull(false);
        table.addField(fieldBILLLINE_ID);
        
        ForeignKeyConstraint fk_FK_JPA_BILL_ACTION_BILLLINE_ID = new ForeignKeyConstraint();
        fk_FK_JPA_BILL_ACTION_BILLLINE_ID.setName("FK_JPA_BILL_ACTION_BILLLINE_ID");
        fk_FK_JPA_BILL_ACTION_BILLLINE_ID.setTargetTable("JPA_BILL_LINE");
        fk_FK_JPA_BILL_ACTION_BILLLINE_ID.addSourceField("BILLLINE_ID");
        fk_FK_JPA_BILL_ACTION_BILLLINE_ID.addTargetField("ID");
        table.addForeignKeyConstraint(fk_FK_JPA_BILL_ACTION_BILLLINE_ID);
        
        return table;
    }
    
    public TableDefinition buildORD_ENTITYATable() {
        TableDefinition table = new TableDefinition();
        table.setName("ORD_ENTITY_A");
        
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMBER");
        fieldID.setSize(19);
        fieldID.setIsPrimaryKey(true);
        fieldID.setIsIdentity(false);
        fieldID.setUnique(false);
        fieldID.setShouldAllowNull(false);
        table.addField(fieldID);
        
        FieldDefinition fieldDESCRIPTION = new FieldDefinition();
        fieldDESCRIPTION.setName("DESCRIPTION");
        fieldDESCRIPTION.setTypeName("VARCHAR");
        fieldDESCRIPTION.setSize(255);
        fieldDESCRIPTION.setIsPrimaryKey(false);
        fieldDESCRIPTION.setIsIdentity(false);
        fieldDESCRIPTION.setUnique(false);
        fieldDESCRIPTION.setShouldAllowNull(false);
        table.addField(fieldDESCRIPTION);
        
        FieldDefinition fieldENTITYZ_ID = new FieldDefinition();
        fieldENTITYZ_ID.setName("ENTITYZ_ID");
        fieldENTITYZ_ID.setTypeName("NUMBER");
        fieldENTITYZ_ID.setSize(19);
        fieldENTITYZ_ID.setIsPrimaryKey(false);
        fieldENTITYZ_ID.setIsIdentity(false);
        fieldENTITYZ_ID.setUnique(false);
        fieldENTITYZ_ID.setShouldAllowNull(true);
        table.addField(fieldENTITYZ_ID);
        
        FieldDefinition fieldENTITYA_ORDER = new FieldDefinition();
        fieldENTITYA_ORDER.setName("ENTITYA_ORDER");
        fieldENTITYA_ORDER.setTypeName("NUMBER");
        fieldENTITYA_ORDER.setSize(19);
        fieldENTITYA_ORDER.setIsPrimaryKey(false);
        fieldENTITYA_ORDER.setIsIdentity(false);
        fieldENTITYA_ORDER.setUnique(false);
        fieldENTITYA_ORDER.setShouldAllowNull(true);
        table.addField(fieldENTITYA_ORDER);
        
        ForeignKeyConstraint fk_FK_ORD_ENTITYA_ORD_ENTITYZ = new ForeignKeyConstraint();
        fk_FK_ORD_ENTITYA_ORD_ENTITYZ.setName("FK_ORD_ENTITYA_ORD_ENTITYZ");
        fk_FK_ORD_ENTITYA_ORD_ENTITYZ.addSourceField("ENTITYZ_ID");
        fk_FK_ORD_ENTITYA_ORD_ENTITYZ.setTargetTable("ORD_ENTITY_Z");
        fk_FK_ORD_ENTITYA_ORD_ENTITYZ.addTargetField("ID");
        table.addForeignKeyConstraint(fk_FK_ORD_ENTITYA_ORD_ENTITYZ);
        
        return table;
    }
    
    public TableDefinition buildORD_ENTITYZTable() {
        TableDefinition table = new TableDefinition();
        table.setName("ORD_ENTITY_Z");
        
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMBER");
        fieldID.setSize(19);
        fieldID.setIsPrimaryKey(true);
        fieldID.setIsIdentity(false);
        fieldID.setUnique(false);
        fieldID.setShouldAllowNull(false);
        table.addField(fieldID);
        
        FieldDefinition fieldDESCRIPTION = new FieldDefinition();
        fieldDESCRIPTION.setName("DESCRIPTION");
        fieldDESCRIPTION.setTypeName("VARCHAR");
        fieldDESCRIPTION.setSize(255);
        fieldDESCRIPTION.setIsPrimaryKey(false);
        fieldDESCRIPTION.setIsIdentity(false);
        fieldDESCRIPTION.setUnique(false);
        fieldDESCRIPTION.setShouldAllowNull(false);
        table.addField(fieldDESCRIPTION);
        
        return table;
    }
    
    @Override
    public void replaceTables(DatabaseSession session) {
        DatabasePlatform dbPlatform = session.getPlatform();
        if (dbPlatform.isPervasive() || dbPlatform.isInformix()) {
            // In Informix, when using GenerationType.IDENTITY to generate values, fields referring to the generated fields
            // can't be non-integer. NUMERIC types map to DECIMAL which is incompatible with the generated value's SERIAL type.
            adjustForeignKeyFieldTypes(session);
        }
        try {
            super.replaceTables(session);
        } catch (DatabaseException de) {
            SessionLogEntry sle = new SessionLogEntry(null, SessionLog.WARNING, null, de);
            sle.setMessage("Test setup failed, retrying...");
            session.getSessionLog().log(sle);
            //give it one more try in case of some possibly random failure
            super.replaceTables(session);
        }
    }
}
