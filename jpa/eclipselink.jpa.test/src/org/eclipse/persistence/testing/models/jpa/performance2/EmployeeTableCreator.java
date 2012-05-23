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
package org.eclipse.persistence.testing.models.jpa.performance2;

import org.eclipse.persistence.tools.schemaframework.*;

/**
 * This class will create tables for the Employee model.
 */
public class EmployeeTableCreator extends TableCreator {

    public EmployeeTableCreator() {
        applyPROJECT();
        buildADDRESSTable();
        buildEMPLOYEETable();
        buildSALARYTable();
        buildEMAILTable();
        buildEMP_JOBTable();
        buildJOBTITLETable();
        buildRESPONSTable();
        buildSPROJECTTable();
        buildLPROJECTTable();
        buildPHONETable();
        buildPROJ_EMPTable();
        buildDEGREETable();
    }

    protected void applyPROJECT() {
        setName("Employee");
    }

    protected void buildADDRESSTable() {
        TableDefinition tabledefinition = new TableDefinition();

        // SECTION: TABLE
        tabledefinition.setName("P2_ADDRESS");

        // SECTION: FIELD
        FieldDefinition field = new FieldDefinition();
        field.setName("ADDRESS_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(true);
        tabledefinition.addField(field);

        // SECTION: FIELD
        FieldDefinition field1 = new FieldDefinition();
        field1.setName("P_CODE");
        field1.setTypeName("VARCHAR");
        field1.setSize(20);
        field1.setShouldAllowNull(true);
        field1.setIsPrimaryKey(false);
        field1.setUnique(false);
        field1.setIsIdentity(false);
        tabledefinition.addField(field1);

        // SECTION: FIELD
        FieldDefinition field2 = new FieldDefinition();
        field2.setName("CITY");
        field2.setTypeName("VARCHAR");
        field2.setSize(80);
        field2.setShouldAllowNull(true);
        field2.setIsPrimaryKey(false);
        field2.setUnique(false);
        field2.setIsIdentity(false);
        tabledefinition.addField(field2);

        // SECTION: FIELD
        FieldDefinition field3 = new FieldDefinition();
        field3.setName("PROVINCE");
        field3.setTypeName("VARCHAR");
        field3.setSize(80);
        field3.setShouldAllowNull(true);
        field3.setIsPrimaryKey(false);
        field3.setUnique(false);
        field3.setIsIdentity(false);
        tabledefinition.addField(field3);

        // SECTION: FIELD
        FieldDefinition field4 = new FieldDefinition();
        field4.setName("STREET");
        field4.setTypeName("VARCHAR");
        field4.setSize(80);
        field4.setShouldAllowNull(true);
        field4.setIsPrimaryKey(false);
        field4.setUnique(false);
        field4.setIsIdentity(false);
        tabledefinition.addField(field4);

        // SECTION: FIELD
        FieldDefinition field5 = new FieldDefinition();
        field5.setName("COUNTRY");
        field5.setTypeName("VARCHAR");
        field5.setSize(80);
        field5.setShouldAllowNull(true);
        field5.setIsPrimaryKey(false);
        field5.setUnique(false);
        field5.setIsIdentity(false);
        tabledefinition.addField(field5);
        addTableDefinition(tabledefinition);
    }

    protected void buildEMPLOYEETable() {
        TableDefinition tabledefinition = new TableDefinition();

        // SECTION: TABLE
        tabledefinition.setName("P2_EMPLOYEE");

        // SECTION: FIELD
        FieldDefinition field = new FieldDefinition();
        field.setName("EMP_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(true);
        tabledefinition.addField(field);

        // SECTION: FIELD
        FieldDefinition field1 = new FieldDefinition();
        field1.setName("F_NAME");
        field1.setTypeName("VARCHAR");
        field1.setSize(40);
        field1.setShouldAllowNull(true);
        field1.setIsPrimaryKey(false);
        field1.setUnique(false);
        field1.setIsIdentity(false);
        tabledefinition.addField(field1);

        // SECTION: FIELD
        FieldDefinition field2 = new FieldDefinition();
        field2.setName("L_NAME");
        field2.setTypeName("VARCHAR");
        field2.setSize(40);
        field2.setShouldAllowNull(true);
        field2.setIsPrimaryKey(false);
        field2.setUnique(false);
        field2.setIsIdentity(false);
        tabledefinition.addField(field2);

        // SECTION: FIELD
        FieldDefinition salary = new FieldDefinition();
        salary.setName("SALARY");
        salary.setTypeName("INT");
        salary.setSize(10);
        salary.setShouldAllowNull(true);
        salary.setIsPrimaryKey(false);
        salary.setUnique(false);
        salary.setIsIdentity(false);
        tabledefinition.addField(salary);
        
        // SECTION: FIELD
        FieldDefinition field3 = new FieldDefinition();
        field3.setName("START_DATE");
        field3.setTypeName("DATE");
        field3.setSize(23);
        field3.setShouldAllowNull(true);
        field3.setIsPrimaryKey(false);
        field3.setUnique(false);
        field3.setIsIdentity(false);
        tabledefinition.addField(field3);

        // SECTION: FIELD
        FieldDefinition field4 = new FieldDefinition();
        field4.setName("END_DATE");
        field4.setTypeName("DATE");
        field4.setSize(23);
        field4.setShouldAllowNull(true);
        field4.setIsPrimaryKey(false);
        field4.setUnique(false);
        field4.setIsIdentity(false);
        tabledefinition.addField(field4);

        // SECTION: FIELD
        FieldDefinition field7 = new FieldDefinition();
        field7.setName("GENDER");
        field7.setTypeName("VARCHAR");
        field7.setSize(10);
        field7.setShouldAllowNull(true);
        field7.setIsPrimaryKey(false);
        field7.setUnique(false);
        field7.setIsIdentity(false);
        tabledefinition.addField(field7);

        // SECTION: FIELD
        FieldDefinition field8 = new FieldDefinition();
        field8.setName("ADDR_ID");
        field8.setTypeName("NUMERIC");
        field8.setSize(15);
        field8.setShouldAllowNull(true);
        field8.setIsPrimaryKey(false);
        field8.setUnique(false);
        field8.setIsIdentity(false);
        field8.setForeignKeyFieldName("P2_ADDRESS.ADDRESS_ID");
        tabledefinition.addField(field8);

        // SECTION: FIELD
        FieldDefinition field9 = new FieldDefinition();
        field9.setName("MANAGER_ID");
        field9.setTypeName("NUMERIC");
        field9.setSize(15);
        field9.setShouldAllowNull(true);
        field9.setIsPrimaryKey(false);
        field9.setUnique(false);
        field9.setIsIdentity(false);
        field9.setForeignKeyFieldName("P2_EMPLOYEE.EMP_ID");
        tabledefinition.addField(field9);

        // SECTION: FIELD
        FieldDefinition field10 = new FieldDefinition();
        field10.setName("VERSION");
        field10.setTypeName("NUMERIC");
        field10.setSize(15);
        field10.setShouldAllowNull(true);
        field10.setIsPrimaryKey(false);
        field10.setUnique(false);
        field10.setIsIdentity(false);
        tabledefinition.addField(field10);
        addTableDefinition(tabledefinition);
    }

    protected void buildSALARYTable() {
        TableDefinition tabledefinition = new TableDefinition();

        // SECTION: TABLE
        tabledefinition.setName("P2_SALARY");

        // SECTION: FIELD
        FieldDefinition field = new FieldDefinition();
        field.setName("EMP_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setForeignKeyFieldName("P2_EMPLOYEE.EMP_ID");
        tabledefinition.addField(field);

        // SECTION: FIELD
        FieldDefinition salary = new FieldDefinition();
        salary.setName("SALARY");
        salary.setTypeName("INT");
        salary.setSize(10);
        tabledefinition.addField(salary);
        addTableDefinition(tabledefinition);
    }

    protected void buildEMP_JOBTable() {
        TableDefinition tabledefinition = new TableDefinition();

        // SECTION: TABLE
        tabledefinition.setName("P2_EMP_JOB");

        // SECTION: FIELD
        FieldDefinition field = new FieldDefinition();
        field.setName("EMP_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setForeignKeyFieldName("P2_EMPLOYEE.EMP_ID");
        tabledefinition.addField(field);

        // SECTION: FIELD
        field = new FieldDefinition();
        field.setName("TITLE_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setForeignKeyFieldName("P2_JOBTITLE.JOB_ID");
        tabledefinition.addField(field);
        addTableDefinition(tabledefinition);
    }

    protected void buildEMAILTable() {
        TableDefinition tabledefinition = new TableDefinition();

        // SECTION: TABLE
        tabledefinition.setName("P2_EMAIL");

        // SECTION: FIELD
        FieldDefinition field = new FieldDefinition();
        field.setName("EMP_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setForeignKeyFieldName("P2_EMPLOYEE.EMP_ID");
        tabledefinition.addField(field);

        // SECTION: FIELD
        field = new FieldDefinition();
        field.setName("EMAIL_TYPE");
        field.setTypeName("VARCHAR");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        tabledefinition.addField(field);

        // SECTION: FIELD
        field = new FieldDefinition();
        field.setName("EMAIL_ADDRESS");
        field.setTypeName("VARCHAR");
        field.setSize(100);
        tabledefinition.addField(field);
        addTableDefinition(tabledefinition);
    }

    protected void buildRESPONSTable() {
        TableDefinition tabledefinition = new TableDefinition();

        // SECTION: TABLE
        tabledefinition.setName("P2_RESPONS");

        // SECTION: FIELD
        FieldDefinition field = new FieldDefinition();
        field.setName("EMP_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setForeignKeyFieldName("P2_EMPLOYEE.EMP_ID");
        tabledefinition.addField(field);

        // SECTION: FIELD
        field = new FieldDefinition();
        field.setName("PRIORITY");
        field.setTypeName("NUMERIC");
        field.setSize(5);
        field.setShouldAllowNull(false);
        tabledefinition.addField(field);

        // SECTION: FIELD
        field = new FieldDefinition();
        field.setName("RESPONSIBILITY");
        field.setTypeName("VARCHAR");
        field.setSize(100);
        tabledefinition.addField(field);
        addTableDefinition(tabledefinition);
    }

    protected void buildJOBTITLETable() {
        TableDefinition tabledefinition = new TableDefinition();

        // SECTION: TABLE
        tabledefinition.setName("P2_JOBTITLE");

        // SECTION: FIELD
        FieldDefinition field = new FieldDefinition();
        field.setName("JOB_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setIsIdentity(true);
        tabledefinition.addField(field);

        // SECTION: FIELD
        FieldDefinition field1 = new FieldDefinition();
        field1.setName("TITLE");
        field1.setTypeName("VARCHAR");
        field1.setSize(100);
        tabledefinition.addField(field1);
        addTableDefinition(tabledefinition);
    }

    protected void buildDEGREETable() {
        TableDefinition tabledefinition = new TableDefinition();

        // SECTION: TABLE
        tabledefinition.setName("P2_DEGREE");

        // SECTION: FIELD
        FieldDefinition field = new FieldDefinition();
        field.setName("DEGREE_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setIsIdentity(true);
        tabledefinition.addField(field);
        
        // SECTION: FIELD
        FieldDefinition field2 = new FieldDefinition();
        field2.setName("EMP_ID");
        field2.setTypeName("NUMERIC");
        field2.setSize(15);
        tabledefinition.addField(field2);

        // SECTION: FIELD
        FieldDefinition field1 = new FieldDefinition();
        field1.setName("NAME");
        field1.setTypeName("VARCHAR");
        field1.setSize(100);
        tabledefinition.addField(field1);
        addTableDefinition(tabledefinition);
    }

    protected void buildLPROJECTTable() {
        TableDefinition tabledefinition = new TableDefinition();

        // SECTION: TABLE
        tabledefinition.setName("P2_LPROJECT");

        // SECTION: FIELD
        FieldDefinition field = new FieldDefinition();
        field.setName("PROJ_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(false);
        tabledefinition.addField(field);
        // SECTION: FIELD
        FieldDefinition field2 = new FieldDefinition();
        field2.setName("PROJ_NAME");
        field2.setTypeName("VARCHAR");
        field2.setSize(30);
        field2.setShouldAllowNull(true);
        field2.setIsPrimaryKey(false);
        field2.setUnique(false);
        field2.setIsIdentity(false);
        tabledefinition.addField(field2);

        // SECTION: FIELD
        FieldDefinition field3 = new FieldDefinition();
        field3.setName("DESCRIP");
        field3.setTypeName("VARCHAR");
        field3.setSize(200);
        field3.setShouldAllowNull(true);
        field3.setIsPrimaryKey(false);
        field3.setUnique(false);
        field3.setIsIdentity(false);
        tabledefinition.addField(field3);

        // SECTION: FIELD
        FieldDefinition field4 = new FieldDefinition();
        field4.setName("LEADER_ID");
        field4.setTypeName("NUMERIC");
        field4.setSize(15);
        field4.setShouldAllowNull(true);
        field4.setIsPrimaryKey(false);
        field4.setUnique(false);
        field4.setIsIdentity(false);
        field4.setForeignKeyFieldName("P2_EMPLOYEE.EMP_ID");
        tabledefinition.addField(field4);

        // SECTION: FIELD
        FieldDefinition field5 = new FieldDefinition();
        field5.setName("VERSION");
        field5.setTypeName("NUMERIC");
        field5.setSize(15);
        field5.setShouldAllowNull(true);
        field5.setIsPrimaryKey(false);
        field5.setUnique(false);
        field5.setIsIdentity(false);
        tabledefinition.addField(field5);

        // SECTION: FIELD
        FieldDefinition field6 = new FieldDefinition();
        field6.setName("BUDGET");
        field6.setTypeName("DOUBLE PRECIS");
        field6.setSize(18);
        field6.setShouldAllowNull(true);
        field6.setIsPrimaryKey(false);
        field6.setUnique(false);
        field6.setIsIdentity(false);
        tabledefinition.addField(field6);

        // SECTION: FIELD
        FieldDefinition field7 = new FieldDefinition();
        field7.setName("MILESTONE");
        field7.setTypeName("DATETIME");
        field7.setSize(23);
        field7.setShouldAllowNull(true);
        field7.setIsPrimaryKey(false);
        field7.setUnique(false);
        field7.setIsIdentity(false);
        tabledefinition.addField(field7);
        addTableDefinition(tabledefinition);
    }

    protected void buildSPROJECTTable() {
        TableDefinition tabledefinition = new TableDefinition();

        // SECTION: TABLE
        tabledefinition.setName("P2_SPROJECT");

        // SECTION: FIELD
        FieldDefinition field = new FieldDefinition();
        field.setName("PROJ_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setIsIdentity(true);
        tabledefinition.addField(field);

        // SECTION: FIELD
        FieldDefinition field2 = new FieldDefinition();
        field2.setName("PROJ_NAME");
        field2.setTypeName("VARCHAR");
        field2.setSize(30);
        field2.setShouldAllowNull(true);
        field2.setIsPrimaryKey(false);
        field2.setUnique(false);
        field2.setIsIdentity(false);
        tabledefinition.addField(field2);

        // SECTION: FIELD
        FieldDefinition field3 = new FieldDefinition();
        field3.setName("DESCRIP");
        field3.setTypeName("VARCHAR");
        field3.setSize(200);
        field3.setShouldAllowNull(true);
        field3.setIsPrimaryKey(false);
        field3.setUnique(false);
        field3.setIsIdentity(false);
        tabledefinition.addField(field3);

        // SECTION: FIELD
        FieldDefinition field4 = new FieldDefinition();
        field4.setName("LEADER_ID");
        field4.setTypeName("NUMERIC");
        field4.setSize(15);
        field4.setShouldAllowNull(true);
        field4.setIsPrimaryKey(false);
        field4.setUnique(false);
        field4.setIsIdentity(false);
        field4.setForeignKeyFieldName("P2_EMPLOYEE.EMP_ID");
        tabledefinition.addField(field4);

        // SECTION: FIELD
        FieldDefinition field5 = new FieldDefinition();
        field5.setName("VERSION");
        field5.setTypeName("NUMERIC");
        field5.setSize(15);
        field5.setShouldAllowNull(true);
        field5.setIsPrimaryKey(false);
        field5.setUnique(false);
        field5.setIsIdentity(false);
        tabledefinition.addField(field5);
        addTableDefinition(tabledefinition);
    }

    protected void buildPHONETable() {
        TableDefinition tabledefinition = new TableDefinition();

        // SECTION: TABLE
        tabledefinition.setName("P2_PHONE");
        
        // SECTION: FIELD
        FieldDefinition field = new FieldDefinition();
        field.setName("EMP_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(false);
        field.setUnique(false);
        field.setIsIdentity(false);
        field.setForeignKeyFieldName("P2_EMPLOYEE.EMP_ID");
        tabledefinition.addField(field);

        // SECTION: FIELD
        FieldDefinition field1 = new FieldDefinition();
        field1.setName("TYPE");
        field1.setTypeName("VARCHAR");
        field1.setSize(15);
        field1.setShouldAllowNull(false);
        field1.setIsPrimaryKey(false);
        field1.setUnique(false);
        field1.setIsIdentity(false);
        tabledefinition.addField(field1);

        // SECTION: FIELD
        FieldDefinition field2 = new FieldDefinition();
        field2.setName("AREA_CODE");
        field2.setTypeName("VARCHAR");
        field2.setSize(3);
        field2.setShouldAllowNull(true);
        field2.setIsPrimaryKey(false);
        field2.setUnique(false);
        field2.setIsIdentity(false);
        tabledefinition.addField(field2);

        // SECTION: FIELD
        FieldDefinition field3 = new FieldDefinition();
        field3.setName("P_NUMBER");
        field3.setTypeName("VARCHAR");
        field3.setSize(7);
        field3.setShouldAllowNull(true);
        field3.setIsPrimaryKey(false);
        field3.setUnique(false);
        field3.setIsIdentity(false);
        tabledefinition.addField(field3);
        addTableDefinition(tabledefinition);
    }

    protected void buildPROJ_EMPTable() {
        TableDefinition tabledefinition = new TableDefinition();

        // SECTION: TABLE
        tabledefinition.setName("P2_PROJ_EMP");

        // SECTION: FIELD
        FieldDefinition field = new FieldDefinition();
        field.setName("EMP_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setForeignKeyFieldName("P2_EMPLOYEE.EMP_ID");
        tabledefinition.addField(field);

        // SECTION: FIELD
        FieldDefinition field1 = new FieldDefinition();
        field1.setName("PROJ_ID");
        field1.setTypeName("NUMERIC");
        field1.setSize(15);
        field1.setShouldAllowNull(false);
        field1.setIsPrimaryKey(true);
        tabledefinition.addField(field1);
        addTableDefinition(tabledefinition);
    }

}
