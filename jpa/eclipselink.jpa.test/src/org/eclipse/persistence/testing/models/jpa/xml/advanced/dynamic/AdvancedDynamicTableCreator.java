/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     07/05/2010-2.1.1 Guy Pelletier
//       - 317708: Exception thrown when using LAZY fetch on VIRTUAL mapping
//     08/20/2012-2.4 Guy Pelletier
//       - 381079: EclipseLink dynamic entity does not support embedded-id
package org.eclipse.persistence.testing.models.jpa.xml.advanced.dynamic;

import org.eclipse.persistence.testing.framework.TogglingFastTableCreator;
import org.eclipse.persistence.tools.schemaframework.*;

public class AdvancedDynamicTableCreator extends TogglingFastTableCreator {

    public AdvancedDynamicTableCreator() {
        setName("AdvancedDynamicProject");

        addTableDefinition(buildADDRESSTable());
        addTableDefinition(buildCREDITCARDSTable());
        addTableDefinition(buildCREDITLINESTable());
        addTableDefinition(buildEMPLOYEETable());
        addTableDefinition(buildLARGEPROJECTTable());
        addTableDefinition(buildPHONENUMBERTable());
        addTableDefinition(buildPROJECT_EMPTable());
        addTableDefinition(buildPROJECTTable());
        addTableDefinition(buildRESPONSTable());
        addTableDefinition(buildSALARYTable());
        addTableDefinition(buildREADONLYCLASSTable());
        addTableDefinition(buildSHOVELTable());
        addTableDefinition(buildSHOVELDIGGERTable());
        addTableDefinition(buildSHOVELOWNERTable());
        addTableDefinition(buildSHOVELPROJECTTable());
        addTableDefinition(buildSHOVELPROJECTSTable());
        addTableDefinition(buildRUNNERTable());
        addTableDefinition(buildWALKERTable());
    }

    public static TableDefinition buildADDRESSTable() {
        TableDefinition table = new TableDefinition();
        table.setName("DYNAMIC_ADDRESS");

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

        return table;
    }

    public static TableDefinition buildCREDITCARDSTable() {
        TableDefinition table = new TableDefinition();
        table.setName("DynamicEmployee_CREDITCARDS");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("DynamicEmployee_EMP_ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(false);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(false);
        fieldID.setForeignKeyFieldName("DYNAMIC_EMPLOYEE.EMP_ID");
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
        table.setName("DYNAMIC_EMP_CREDITLINES");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("EMP_ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(false);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(false);
        fieldID.setForeignKeyFieldName("DYNAMIC_EMPLOYEE.EMP_ID");
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

    public static TableDefinition buildEMPLOYEETable() {
        TableDefinition table = new TableDefinition();
        table.setName("DYNAMIC_EMPLOYEE");

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

        FieldDefinition sin = new FieldDefinition();
        sin.setName("SIN");
        sin.setTypeName("VARCHAR");
        sin.setSize(10);
        sin.setShouldAllowNull(true);
        sin.setIsPrimaryKey(false);
        sin.setUnique(false);
        sin.setIsIdentity(false);
        table.addField(sin);

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
        field5.setSize(6);
        field5.setShouldAllowNull(true);
        field5.setIsPrimaryKey(false);
        field5.setUnique(false);
        field5.setIsIdentity(false);
        table.addField(field5);

        FieldDefinition field6 = new FieldDefinition();
        field6.setName("END_TIME");
        field6.setTypeName("TIME");
        field6.setSize(6);
        field6.setShouldAllowNull(true);
        field6.setIsPrimaryKey(false);
        field6.setUnique(false);
        field6.setIsIdentity(false);
        table.addField(field6);

        FieldDefinition fieldOvertimeStart = new FieldDefinition();
        fieldOvertimeStart.setName("START_OVERTIME");
        fieldOvertimeStart.setTypeName("TIME");
        fieldOvertimeStart.setSize(6);
        fieldOvertimeStart.setShouldAllowNull(true);
        fieldOvertimeStart.setIsPrimaryKey(false);
        fieldOvertimeStart.setUnique(false);
        fieldOvertimeStart.setIsIdentity(false);
        table.addField(fieldOvertimeStart);

        FieldDefinition fieldOvertimeEnd = new FieldDefinition();
        fieldOvertimeEnd.setName("END_OVERTIME");
        fieldOvertimeEnd.setTypeName("TIME");
        fieldOvertimeEnd.setSize(6);
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
        field8.setForeignKeyFieldName("DYNAMIC_ADDRESS.ADDRESS_ID");
        table.addField(field8);

        FieldDefinition field9 = new FieldDefinition();
        field9.setName("MANAGER_EMP_ID");
        field9.setTypeName("NUMERIC");
        field9.setSize(15);
        field9.setShouldAllowNull(true);
        field9.setIsPrimaryKey(false);
        field9.setUnique(false);
        field9.setIsIdentity(false);
        field9.setForeignKeyFieldName("DYNAMIC_EMPLOYEE.EMP_ID");
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

        FieldDefinition fieldPayScale = new FieldDefinition();
        fieldPayScale.setName("PAY_SCALE");
        fieldPayScale.setTypeName("VARCHAR");
        fieldPayScale.setSize(40);
        fieldPayScale.setIsPrimaryKey(false);
        fieldPayScale.setUnique(false);
        fieldPayScale.setIsIdentity(false);
        fieldPayScale.setShouldAllowNull(true);
        table.addField(fieldPayScale);

        return table;
    }

    public static TableDefinition buildLARGEPROJECTTable() {
        TableDefinition table = new TableDefinition();
        table.setName("DYNAMIC_LPROJECT");

        FieldDefinition fieldPROJID = new FieldDefinition();
        fieldPROJID.setName("PROJ_ID");
        fieldPROJID.setTypeName("NUMERIC");
        fieldPROJID.setSize(15);
        fieldPROJID.setShouldAllowNull(false);
        fieldPROJID.setIsPrimaryKey(true);
        fieldPROJID.setUnique(false);
        fieldPROJID.setIsIdentity(false);
        fieldPROJID.setForeignKeyFieldName("DYNAMIC_PROJECT.PROJ_ID");
        table.addField(fieldPROJID);

        FieldDefinition fieldBUDGET = new FieldDefinition();
        fieldBUDGET.setName("BUDGET");
        fieldBUDGET.setTypeName("DOUBLE PRECIS");
        fieldBUDGET.setSize(18);
        fieldBUDGET.setShouldAllowNull(true);
        fieldBUDGET.setIsPrimaryKey(false);
        fieldBUDGET.setUnique(false);
        fieldBUDGET.setIsIdentity(false);
        table.addField(fieldBUDGET);

        return table;
    }

    public static TableDefinition buildPHONENUMBERTable() {
        TableDefinition table = new TableDefinition();
        table.setName("DYNAMIC_PHONENUMBER");

        FieldDefinition field = new FieldDefinition();
        field.setName("OWNER_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(false);
        field.setForeignKeyFieldName("DYNAMIC_EMPLOYEE.EMP_ID");
        table.addField(field);

        FieldDefinition field1 = new FieldDefinition();
        field1.setName("TYPE");
        field1.setTypeName("VARCHAR");
        field1.setSize(15);
        field1.setShouldAllowNull(false);
        field1.setIsPrimaryKey(true);
        field1.setUnique(false);
        field1.setIsIdentity(false);
        table.addField(field1);

        FieldDefinition field2 = new FieldDefinition();
        field2.setName("AREA_CODE");
        field2.setTypeName("VARCHAR");
        field2.setSize(3);
        field2.setShouldAllowNull(true);
        field2.setIsPrimaryKey(false);
        field2.setUnique(false);
        field2.setIsIdentity(false);
        table.addField(field2);

        FieldDefinition field3 = new FieldDefinition();
        field3.setName("NUMB");
        field3.setTypeName("VARCHAR");
        field3.setSize(8);
        field3.setShouldAllowNull(true);
        field3.setIsPrimaryKey(false);
        field3.setUnique(false);
        field3.setIsIdentity(false);
        table.addField(field3);

        return table;
    }

    public static TableDefinition buildPROJECT_EMPTable() {
        TableDefinition table = new TableDefinition();
        table.setName("DYNAMIC_PROJ_EMP");

        FieldDefinition field = new FieldDefinition();
        field.setName("EMP_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(false);
        field.setForeignKeyFieldName("DYNAMIC_EMPLOYEE.EMP_ID");
        table.addField(field);

        FieldDefinition field1 = new FieldDefinition();
        field1.setName("PROJ_ID");
        field1.setTypeName("NUMERIC");
        field1.setSize(15);
        field1.setShouldAllowNull(false);
        field1.setIsPrimaryKey(true);
        field1.setUnique(false);
        field1.setIsIdentity(false);
        field1.setForeignKeyFieldName("DYNAMIC_PROJECT.PROJ_ID");
        table.addField(field1);

        return table;
    }

    public static TableDefinition buildPROJECTTable() {
        TableDefinition table = new TableDefinition();
        table.setName("DYNAMIC_PROJECT");

        FieldDefinition field = new FieldDefinition();
        field.setName("PROJ_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(true);
        table.addField(field);

        FieldDefinition field1 = new FieldDefinition();
        field1.setName("PROJ_TYPE");
        field1.setTypeName("VARCHAR");
        field1.setSize(1);
        field1.setShouldAllowNull(true);
        field1.setIsPrimaryKey(false);
        field1.setUnique(false);
        field1.setIsIdentity(false);
        table.addField(field1);

        FieldDefinition field2 = new FieldDefinition();
        field2.setName("PROJ_NAME");
        field2.setTypeName("VARCHAR");
        field2.setSize(30);
        field2.setShouldAllowNull(true);
        field2.setIsPrimaryKey(false);
        field2.setUnique(false);
        field2.setIsIdentity(false);
        table.addField(field2);

        FieldDefinition field3 = new FieldDefinition();
        field3.setName("DESCRIP");
        field3.setTypeName("VARCHAR");
        field3.setSize(200);
        field3.setShouldAllowNull(true);
        field3.setIsPrimaryKey(false);
        field3.setUnique(false);
        field3.setIsIdentity(false);
        table.addField(field3);

        FieldDefinition field4 = new FieldDefinition();
        field4.setName("LEADER_ID");
        field4.setTypeName("NUMERIC");
        field4.setSize(15);
        field4.setShouldAllowNull(true);
        field4.setIsPrimaryKey(false);
        field4.setUnique(false);
        field4.setIsIdentity(false);
        field4.setForeignKeyFieldName("DYNAMIC_EMPLOYEE.EMP_ID");
        table.addField(field4);

        FieldDefinition field5 = new FieldDefinition();
        field5.setName("VERSION");
        field5.setTypeName("NUMERIC");
        field5.setSize(15);
        field5.setShouldAllowNull(true);
        field5.setIsPrimaryKey(false);
        field5.setUnique(false);
        field5.setIsIdentity(false);
        table.addField(field5);

        return table;
    }

    public static TableDefinition buildREADONLYCLASSTable() {
        TableDefinition table = new TableDefinition();
        table.setName("DYNAMIC_READONLY");

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

        return table;
    }

    public static TableDefinition buildRESPONSTable() {
        TableDefinition table = new TableDefinition();
        table.setName("DYNAMIC_RESPONS");

        FieldDefinition field = new FieldDefinition();
        field.setName("EMP_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(false);
        field.setUnique(false);
        field.setIsIdentity(false);
        field.setForeignKeyFieldName("DYNAMIC_EMPLOYEE.EMP_ID");
        table.addField(field);

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
        table.setName("DYNAMIC_SALARY");

        FieldDefinition fieldEMP_ID = new FieldDefinition();
        fieldEMP_ID.setName("E_ID");
        fieldEMP_ID.setTypeName("NUMERIC");
        fieldEMP_ID.setSize(15);
        fieldEMP_ID.setSubSize(0);
        fieldEMP_ID.setIsPrimaryKey(true);
        fieldEMP_ID.setIsIdentity(false);
        fieldEMP_ID.setUnique(false);
        fieldEMP_ID.setShouldAllowNull(false);
        fieldEMP_ID.setForeignKeyFieldName("DYNAMIC_EMPLOYEE.EMP_ID");
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

    public static TableDefinition buildSHOVELDIGGERTable() {
        TableDefinition table = new TableDefinition();
        table.setName("DYNAMIC_SHOVEL_DIGGER");

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
        fieldNAME.setName("DIGGER_NAME");
        fieldNAME.setTypeName("VARCHAR");
        fieldNAME.setSize(40);
        fieldNAME.setShouldAllowNull(true);
        fieldNAME.setIsPrimaryKey(false);
        fieldNAME.setUnique(false);
        fieldNAME.setIsIdentity(false);
        table.addField(fieldNAME);

        FieldDefinition fieldSHOVEL = new FieldDefinition();
        fieldSHOVEL.setName("SHOVEL_ID");
        fieldSHOVEL.setTypeName("NUMERIC");
        fieldSHOVEL.setSize(15);
        fieldSHOVEL.setShouldAllowNull(true);
        fieldSHOVEL.setIsPrimaryKey(false);
        fieldSHOVEL.setUnique(false);
        fieldSHOVEL.setIsIdentity(false);
        fieldSHOVEL.setForeignKeyFieldName("DYNAMIC_SHOVEL.ID");
        table.addField(fieldSHOVEL);

        return table;
    }

    public static TableDefinition buildSHOVELOWNERTable() {
        TableDefinition table = new TableDefinition();
        table.setName("DYNAMIC_SHOVEL_OWNER");

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
        fieldNAME.setName("OWNER_NAME");
        fieldNAME.setTypeName("VARCHAR");
        fieldNAME.setSize(40);
        fieldNAME.setShouldAllowNull(true);
        fieldNAME.setIsPrimaryKey(false);
        fieldNAME.setUnique(false);
        fieldNAME.setIsIdentity(false);
        table.addField(fieldNAME);

        return table;
    }

    public static TableDefinition buildSHOVELPROJECTSTable() {
        TableDefinition table = new TableDefinition();
        table.setName("DYNAMIC_SHOVEL_PROJECTS");

        FieldDefinition fieldSHOVEL = new FieldDefinition();
        fieldSHOVEL.setName("SHOVEL_ID");
        fieldSHOVEL.setTypeName("NUMERIC");
        fieldSHOVEL.setSize(15);
        fieldSHOVEL.setShouldAllowNull(true);
        fieldSHOVEL.setIsPrimaryKey(false);
        fieldSHOVEL.setUnique(false);
        fieldSHOVEL.setIsIdentity(false);
        fieldSHOVEL.setForeignKeyFieldName("DYNAMIC_SHOVEL.ID");
        table.addField(fieldSHOVEL);

        FieldDefinition fieldPROJECT = new FieldDefinition();
        fieldPROJECT.setName("PROJECT_ID");
        fieldPROJECT.setTypeName("NUMERIC");
        fieldPROJECT.setSize(15);
        fieldPROJECT.setShouldAllowNull(true);
        fieldPROJECT.setIsPrimaryKey(false);
        fieldPROJECT.setUnique(false);
        fieldPROJECT.setIsIdentity(false);
        fieldPROJECT.setForeignKeyFieldName("DYNAMIC_SHOVEL_PROJECT.ID");
        table.addField(fieldPROJECT);

        return table;
    }

    public static TableDefinition buildSHOVELPROJECTTable() {
        TableDefinition table = new TableDefinition();
        table.setName("DYNAMIC_SHOVEL_PROJECT");

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

        FieldDefinition fieldDESCRIPTION = new FieldDefinition();
        fieldDESCRIPTION.setName("DESCRIP");
        fieldDESCRIPTION.setTypeName("VARCHAR");
        fieldDESCRIPTION.setSize(40);
        fieldDESCRIPTION.setShouldAllowNull(true);
        fieldDESCRIPTION.setIsPrimaryKey(false);
        fieldDESCRIPTION.setUnique(false);
        fieldDESCRIPTION.setIsIdentity(false);
        table.addField(fieldDESCRIPTION);

        return table;
    }

    public static TableDefinition buildSHOVELTable() {
        TableDefinition table = new TableDefinition();
        table.setName("DYNAMIC_SHOVEL");

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

        FieldDefinition fieldCOST = new FieldDefinition();
        fieldCOST.setName("COST");
        fieldCOST.setTypeName("DOUBLE PRECIS");
        fieldCOST.setSize(15);
        fieldCOST.setShouldAllowNull(true);
        fieldCOST.setIsPrimaryKey(false);
        fieldCOST.setUnique(false);
        fieldCOST.setIsIdentity(false);
        table.addField(fieldCOST);

        FieldDefinition fieldVERSION = new FieldDefinition();
        fieldVERSION.setName("VERSION");
        fieldVERSION.setTypeName("NUMERIC");
        fieldVERSION.setSize(15);
        fieldVERSION.setShouldAllowNull(true);
        fieldVERSION.setIsPrimaryKey(false);
        fieldVERSION.setUnique(false);
        fieldVERSION.setIsIdentity(false);
        table.addField(fieldVERSION);

        FieldDefinition fieldOWNERID = new FieldDefinition();
        fieldOWNERID.setName("OWNER_ID");
        fieldOWNERID.setTypeName("NUMERIC");
        fieldOWNERID.setSize(15);
        fieldOWNERID.setShouldAllowNull(true);
        fieldOWNERID.setIsPrimaryKey(false);
        fieldOWNERID.setUnique(false);
        fieldOWNERID.setIsIdentity(false);
        fieldOWNERID.setForeignKeyFieldName("DYNAMIC_SHOVEL_OWNER.ID");
        table.addField(fieldOWNERID);

        FieldDefinition fieldHANDLE = new FieldDefinition();
        fieldHANDLE.setName("HANDLE");
        fieldHANDLE.setTypeName("VARCHAR");
        fieldHANDLE.setSize(25);
        fieldHANDLE.setShouldAllowNull(true);
        fieldHANDLE.setIsPrimaryKey(false);
        fieldHANDLE.setUnique(false);
        fieldHANDLE.setIsIdentity(false);
        table.addField(fieldHANDLE);

        FieldDefinition fieldSHAFT = new FieldDefinition();
        fieldSHAFT.setName("SHAFT");
        fieldSHAFT.setTypeName("VARCHAR");
        fieldSHAFT.setSize(25);
        fieldSHAFT.setShouldAllowNull(true);
        fieldSHAFT.setIsPrimaryKey(false);
        fieldSHAFT.setUnique(false);
        fieldSHAFT.setIsIdentity(false);
        table.addField(fieldSHAFT);

        FieldDefinition fieldSCOOP = new FieldDefinition();
        fieldSCOOP.setName("SCOOP");
        fieldSCOOP.setTypeName("VARCHAR");
        fieldSCOOP.setSize(25);
        fieldSCOOP.setShouldAllowNull(true);
        fieldSCOOP.setIsPrimaryKey(false);
        fieldSCOOP.setUnique(false);
        fieldSCOOP.setIsIdentity(false);
        table.addField(fieldSCOOP);

        return table;
    }

    public static TableDefinition buildRUNNERTable() {
        TableDefinition table = new TableDefinition();
        table.setName("DYNAMIC_RUNNER");

        FieldDefinition fieldBIB = new FieldDefinition();
        fieldBIB.setName("BIB");
        fieldBIB.setTypeName("NUMERIC");
        fieldBIB.setSize(15);
        fieldBIB.setShouldAllowNull(false);
        fieldBIB.setIsPrimaryKey(true);
        fieldBIB.setUnique(false);
        fieldBIB.setIsIdentity(false);
        table.addField(fieldBIB);

        FieldDefinition fieldWORLDRANK = new FieldDefinition();
        fieldWORLDRANK.setName("WORLDRANK");
        fieldWORLDRANK.setTypeName("NUMERIC");
        fieldWORLDRANK.setSize(15);
        fieldWORLDRANK.setShouldAllowNull(false);
        fieldWORLDRANK.setIsPrimaryKey(true);
        fieldWORLDRANK.setUnique(false);
        fieldWORLDRANK.setIsIdentity(false);
        table.addField(fieldWORLDRANK);

        FieldDefinition fieldNAME = new FieldDefinition();
        fieldNAME.setName("NAME");
        fieldNAME.setTypeName("VARCHAR");
        fieldNAME.setSize(40);
        fieldNAME.setShouldAllowNull(true);
        fieldNAME.setIsPrimaryKey(false);
        fieldNAME.setUnique(false);
        fieldNAME.setIsIdentity(false);
        table.addField(fieldNAME);

        return table;
    }

    public static TableDefinition buildWALKERTable() {
        TableDefinition table = new TableDefinition();
        table.setName("DYNAMIC_WALKER");

        FieldDefinition fieldBIB = new FieldDefinition();
        fieldBIB.setName("ID");
        fieldBIB.setTypeName("NUMERIC");
        fieldBIB.setSize(15);
        fieldBIB.setShouldAllowNull(false);
        fieldBIB.setIsPrimaryKey(true);
        fieldBIB.setUnique(false);
        fieldBIB.setIsIdentity(false);
        table.addField(fieldBIB);

        FieldDefinition fieldSTYLE = new FieldDefinition();
        fieldSTYLE.setName("STYLE");
        fieldSTYLE.setTypeName("VARCHAR");
        fieldSTYLE.setSize(40);
        fieldSTYLE.setShouldAllowNull(false);
        fieldSTYLE.setIsPrimaryKey(true);
        fieldSTYLE.setUnique(false);
        fieldSTYLE.setIsIdentity(false);
        table.addField(fieldSTYLE);

        FieldDefinition fieldNAME = new FieldDefinition();
        fieldNAME.setName("NAME");
        fieldNAME.setTypeName("VARCHAR");
        fieldNAME.setSize(40);
        fieldNAME.setShouldAllowNull(true);
        fieldNAME.setIsPrimaryKey(false);
        fieldNAME.setUnique(false);
        fieldNAME.setIsIdentity(false);
        table.addField(fieldNAME);

        return table;
    }
}
