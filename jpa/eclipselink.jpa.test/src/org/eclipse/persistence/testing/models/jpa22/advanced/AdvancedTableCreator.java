/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates, IBM Corporation. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     02/08/2012-2.4 Guy Pelletier
//       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
//     10/09/2012-2.5 Guy Pelletier
//       - 374688: JPA 2.1 Converter support
//     10/25/2012-2.5 Guy Pelletier
//       - 374688: JPA 2.1 Converter support
//     11/19/2012-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support (foreign key metadata support)
//     11/28/2012-2.5 Guy Pelletier
//       - 374688: JPA 2.1 Converter support
//     01/23/2013-2.5 Guy Pelletier
//       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
//     02/06/2013-2.5 Guy Pelletier
//       - 382503: Use of @ConstructorResult with createNativeQuery(sqlString, resultSetMapping) results in NullPointerException
//     07/16/2013-2.5.1 Guy Pelletier
//       - 412384: Applying Converter for parameterized basic-type for joda-time's DateTime does not work
//     06/20/2014-2.5.2 Rick Curtis
//       - 437760: AttributeOverride with no column name defined doesn't work.
package org.eclipse.persistence.testing.models.jpa22.advanced;

import org.eclipse.persistence.testing.framework.TogglingFastTableCreator;

import org.eclipse.persistence.tools.schemaframework.FieldDefinition;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

public class AdvancedTableCreator extends TogglingFastTableCreator {
    public AdvancedTableCreator() {
        setName("JPA22EmployeeProject");

        addTableDefinition(buildADDRESSTable());
        addTableDefinition(buildDEPTTable());
        addTableDefinition(buildDEPT_EMPTable());
        addTableDefinition(buildEMPLOYEETable());
        addTableDefinition(buildENDORSEMENTSTable());
        addTableDefinition(buildENDORSERTable());
        addTableDefinition(buildLARGEPROJECTTable());
        addTableDefinition(buildORGANIZERTable());
        addTableDefinition(buildPROJECTTable());
        addTableDefinition(buildPHONENUMBERTable());
        addTableDefinition(buildPHONENUMBERDETAILSTable());
        addTableDefinition(buildPROJECT_EMPTable());
        addTableDefinition(buildPROJECT_PROPSTable());
        addTableDefinition(buildRACETable());
        addTableDefinition(buildRESPONSTable());
        addTableDefinition(buildRUNNERTable());
        addTableDefinition(buildRUNNER_PBSTable());
        addTableDefinition(buildRUNNER_ACSTable());
        addTableDefinition(buildRUNNERS_RACESTable());
        addTableDefinition(buildRUNNER_VICTORIES_THIS_YEARable());
        addTableDefinition(buildRUNNER_VICTORIES_LAST_YEARable());
        addTableDefinition(buildSALARYTable());
        addTableDefinition(buildSHOETable());
        addTableDefinition(buildSHOETAGTable());
        addTableDefinition(buildSPRINTERTable());
        addTableDefinition(buildSTATUSTable());
        addTableDefinition(buildITEMTable());
        addTableDefinition(buildORDERTable());
    }

    public TableDefinition buildADDRESSTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA22_ADDRESS");

        FieldDefinition field = new FieldDefinition();
        field.setName("ADDRESS_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setSubSize(0);
        field.setIsPrimaryKey(true);
        field.setIsIdentity(true);
        field.setUnique(false);
        field.setShouldAllowNull(false);
        table.addField(field);

        field = new FieldDefinition();
        field.setName("STREET");
        field.setTypeName("VARCHAR2");
        field.setSize(60);
        field.setSubSize(0);
        field.setIsPrimaryKey(false);
        field.setIsIdentity(false);
        field.setUnique(false);
        field.setShouldAllowNull(true);
        table.addField(field);

        field = new FieldDefinition();
        field.setName("CITY");
        field.setTypeName("VARCHAR2");
        field.setSize(60);
        field.setSubSize(0);
        field.setIsPrimaryKey(false);
        field.setIsIdentity(false);
        field.setUnique(false);
        field.setShouldAllowNull(true);
        table.addField(field);

        field = new FieldDefinition();
        field.setName("PROVINCE");
        field.setTypeName("VARCHAR2");
        field.setSize(60);
        field.setSubSize(0);
        field.setIsPrimaryKey(false);
        field.setIsIdentity(false);
        field.setUnique(false);
        field.setShouldAllowNull(true);
        table.addField(field);

        field = new FieldDefinition();
        field.setName("P_CODE");
        field.setTypeName("VARCHAR2");
        field.setSize(67);
        field.setSubSize(0);
        field.setIsPrimaryKey(false);
        field.setIsIdentity(false);
        field.setUnique(false);
        field.setShouldAllowNull(true);
        table.addField(field);

        field = new FieldDefinition();
        field.setName("COUNTRY");
        field.setTypeName("VARCHAR2");
        field.setSize(60);
        field.setSubSize(0);
        field.setIsPrimaryKey(false);
        field.setIsIdentity(false);
        field.setUnique(false);
        field.setShouldAllowNull(true);
        table.addField(field);

        field = new FieldDefinition();
        field.setName("TYPE");
        field.setTypeName("VARCHAR2");
        field.setSize(150);
        field.setSubSize(0);
        field.setIsPrimaryKey(false);
        field.setIsIdentity(false);
        field.setUnique(false);
        field.setShouldAllowNull(true);
        table.addField(field);

        field = new FieldDefinition();
        field.setName("VERSION");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(true);
        field.setIsPrimaryKey(false);
        field.setUnique(false);
        field.setIsIdentity(false);
        table.addField(field);

        return table;
    }

    public TableDefinition buildDEPT_EMPTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA22_DEPT_JPA22_EMPLOYEE");

        FieldDefinition field = new FieldDefinition();
        field.setName("Department_DEPT_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(false);
        field.setForeignKeyFieldName("JPA22_DEPT.ID");
        table.addField(field);

        field = new FieldDefinition();
        field.setName("managers_EMP_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(false);
        field.setForeignKeyFieldName("JPA22_EMPLOYEE.EMP_ID");
        table.addField(field);

        return table;
    }

    public TableDefinition buildDEPTTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA22_DEPT");

        FieldDefinition field = new FieldDefinition();
        field.setName("ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setSubSize(0);
        field.setIsPrimaryKey(true);
        field.setIsIdentity(true);
        field.setUnique(false);
        field.setShouldAllowNull(false);
        table.addField(field);

        field = new FieldDefinition();
        field.setName("NAME");
        field.setTypeName("VARCHAR2");
        field.setSize(60);
        field.setSubSize(0);
        field.setIsPrimaryKey(false);
        field.setIsIdentity(false);
        field.setUnique(false);
        field.setShouldAllowNull(true);
        table.addField(field);

        field = new FieldDefinition();
        field.setName("DEPT_HEAD");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setSubSize(0);
        field.setIsPrimaryKey(false);
        field.setIsIdentity(false);
        field.setUnique(false);
        field.setShouldAllowNull(true);
        table.addField(field);

        return table;
    }

    public TableDefinition buildEMPLOYEETable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA22_EMPLOYEE");

        FieldDefinition field = new FieldDefinition();
        field.setName("EMP_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(true);
        table.addField(field);

        field = new FieldDefinition();
        field.setName("F_NAME");
        field.setTypeName("VARCHAR");
        field.setSize(40);
        field.setShouldAllowNull(true);
        field.setIsPrimaryKey(false);
        field.setUnique(false);
        field.setIsIdentity(false);
        table.addField(field);

        field = new FieldDefinition();
        field.setName("L_NAME");
        field.setTypeName("VARCHAR");
        field.setSize(40);
        field.setShouldAllowNull(true);
        field.setIsPrimaryKey(false);
        field.setUnique(false);
        field.setIsIdentity(false);
        table.addField(field);

        field = new FieldDefinition();
        field.setName("GENDER");
        field.setTypeName("VARCHAR");
        field.setSize(1);
        field.setShouldAllowNull(true);
        field.setIsPrimaryKey(false);
        field.setUnique(false);
        field.setIsIdentity(false);
        table.addField(field);

        field = new FieldDefinition();
        // use 'default' column name based off field of startDate
        field.setName("STARTDATE");
        field.setTypeName("DATE");
        field.setSize(23);
        field.setShouldAllowNull(true);
        field.setIsPrimaryKey(false);
        field.setUnique(false);
        field.setIsIdentity(false);
        table.addField(field);

        field = new FieldDefinition();
        field.setName("END_DATE");
        field.setTypeName("DATE");
        field.setSize(23);
        field.setShouldAllowNull(true);
        field.setIsPrimaryKey(false);
        field.setUnique(false);
        field.setIsIdentity(false);
        table.addField(field);

        field = new FieldDefinition();
        field.setName("ADDR_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(true);
        field.setIsPrimaryKey(false);
        field.setUnique(false);
        field.setIsIdentity(false);
        field.setForeignKeyFieldName("JPA22_ADDRESS.ADDRESS_ID");
        table.addField(field);

        field = new FieldDefinition();
        field.setName("MANAGER_EMP_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(true);
        field.setIsPrimaryKey(false);
        field.setUnique(false);
        field.setIsIdentity(false);
        field.setForeignKeyFieldName("JPA22_EMPLOYEE.EMP_ID");
        table.addField(field);

        field = new FieldDefinition();
        field.setName("VERSION");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(true);
        field.setIsPrimaryKey(false);
        field.setUnique(false);
        field.setIsIdentity(false);
        table.addField(field);

        field = new FieldDefinition();
        field.setName("DEPT_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(true);
        field.setIsPrimaryKey(false);
        field.setUnique(false);
        field.setIsIdentity(false);
        field.setForeignKeyFieldName("JPA22_DEPT.ID");
        table.addField(field);

        return table;
    }

    public TableDefinition buildENDORSEMENTSTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA22_ENDORSEMENTS");

        FieldDefinition field = new FieldDefinition();
        field.setName("ATHLETE_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(true);
        field.setForeignKeyFieldName("JPA22_RUNNER.ID");
        table.addField(field);

        field = new FieldDefinition();
        field.setName("ENDORSER_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(true);
        field.setForeignKeyFieldName("JPA22_ENDORSER.ID");
        table.addField(field);

        field = new FieldDefinition();
        field.setName("ENDORSEMENT");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(true);
        field.setIsPrimaryKey(false);
        field.setUnique(false);
        field.setIsIdentity(false);
        table.addField(field);

        return table;
    }

    public TableDefinition buildENDORSERTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA22_ENDORSER");

        FieldDefinition field = new FieldDefinition();
        field.setName("ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(true);
        table.addField(field);

        field = new FieldDefinition();
        field.setName("NAME");
        field.setTypeName("VARCHAR");
        field.setSize(40);
        field.setShouldAllowNull(true);
        field.setIsPrimaryKey(false);
        field.setUnique(false);
        field.setIsIdentity(false);
        table.addField(field);

        return table;
    }

    public TableDefinition buildITEMTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA22_ITEM");

        FieldDefinition field = new FieldDefinition();
        field.setName("ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(true);
        table.addField(field);

        field = new FieldDefinition();
        field.setName("NAME");
        field.setTypeName("VARCHAR");
        field.setSize(40);
        field.setShouldAllowNull(true);
        field.setIsPrimaryKey(false);
        field.setUnique(false);
        field.setIsIdentity(false);
        table.addField(field);

        return table;
    }

    public TableDefinition buildLARGEPROJECTTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA22_LPROJECT");

        FieldDefinition field = new FieldDefinition();
        field.setName("PROJ_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(false);
        field.setForeignKeyFieldName("JPA22_PROJECT.PROJ_ID");
        table.addField(field);

        field = new FieldDefinition();
        field.setName("BUDGET");
        field.setTypeName("DOUBLE PRECIS");
        field.setSize(18);
        field.setShouldAllowNull(true);
        field.setIsPrimaryKey(false);
        field.setUnique(false);
        field.setIsIdentity(false);
        table.addField(field);

        field = new FieldDefinition();
        field.setName("EXEC_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(true);
        field.setIsPrimaryKey(false);
        field.setUnique(false);
        field.setIsIdentity(false);
        field.setForeignKeyFieldName("JPA22_EMPLOYEE.EMP_ID");
        table.addField(field);

        return table;
    }

    public TableDefinition buildORDERTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA22_ORDER");

        FieldDefinition field = new FieldDefinition();
        field.setName("ORDER_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(false);
        table.addField(field);

        field = new FieldDefinition();
        field.setName("QUANTITY");
        field.setTypeName("NUMBER");
        field.setSize(15);
        field.setSubSize(0);
        field.setIsPrimaryKey(false);
        field.setIsIdentity(false);
        field.setUnique(false);
        field.setShouldAllowNull(false);
        table.addField(field);

        field = new FieldDefinition();
        field.setName("ITEM_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(true);
        field.setIsPrimaryKey(false);
        field.setUnique(false);
        field.setIsIdentity(false);
        field.setForeignKeyFieldName("JPA22_ITEM.ID");
        table.addField(field);

        field = new FieldDefinition();
        field.setName("ITEM_PAIR_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(true);
        field.setIsPrimaryKey(false);
        field.setUnique(false);
        field.setIsIdentity(false);
        field.setForeignKeyFieldName("JPA22_ITEM.ID");
        table.addField(field);

        return table;
    }

    public TableDefinition buildORGANIZERTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA22_ORGANIZER");

        FieldDefinition field = new FieldDefinition();
        field.setName("ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(true);
        table.addField(field);

        field = new FieldDefinition();
        field.setName("NAME");
        field.setTypeName("VARCHAR");
        field.setSize(40);
        field.setShouldAllowNull(true);
        field.setIsPrimaryKey(false);
        field.setUnique(false);
        field.setIsIdentity(false);
        table.addField(field);

        field = new FieldDefinition();
        field.setName("RACE_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(false);
        field.setUnique(false);
        field.setIsIdentity(false);
        field.setForeignKeyFieldName("JPA22_RACE.ID");
        table.addField(field);

        field = new FieldDefinition();
        field.setName("UNIQUEIDENTIFIER");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(true);
        field.setIsPrimaryKey(false);
        field.setUnique(false);
        field.setIsIdentity(false);
        table.addField(field);

        field = new FieldDefinition();
        field.setName("DESCRIPTION");
        field.setTypeName("VARCHAR");
        field.setSize(40);
        field.setShouldAllowNull(true);
        field.setIsPrimaryKey(false);
        field.setUnique(false);
        field.setIsIdentity(false);
        table.addField(field);

        return table;
    }

    public TableDefinition buildPHONENUMBERTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA22_PHONENUMBER");

        FieldDefinition field = new FieldDefinition();
        field.setName("OWNER_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(false);
        field.setForeignKeyFieldName("JPA22_EMPLOYEE.EMP_ID");
        table.addField(field);

        field = new FieldDefinition();
        field.setName("TYPE");
        field.setTypeName("VARCHAR");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(false);
        table.addField(field);

        field = new FieldDefinition();
        field.setName("AREA_CODE");
        field.setTypeName("VARCHAR");
        field.setSize(3);
        field.setShouldAllowNull(true);
        field.setIsPrimaryKey(false);
        field.setUnique(false);
        field.setIsIdentity(false);
        table.addField(field);

        field = new FieldDefinition();
        field.setName("NUMB");
        field.setTypeName("VARCHAR");
        field.setSize(8);
        field.setShouldAllowNull(true);
        field.setIsPrimaryKey(false);
        field.setUnique(false);
        field.setIsIdentity(false);
        table.addField(field);

        return table;
    }

    public TableDefinition buildPHONENUMBERDETAILSTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA22_PHONE_NUMBER_DETAILS");

        FieldDefinition field = new FieldDefinition();
        field.setName("OWNER_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(false);
        field.setForeignKeyFieldName("JPA22_EMPLOYEE.EMP_ID");
        table.addField(field);

        field = new FieldDefinition();
        field.setName("TYPE");
        field.setTypeName("VARCHAR");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(false);
        table.addField(field);

        return table;
    }

    public TableDefinition buildPROJECT_EMPTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA22_EMP_PROJ");

        FieldDefinition field = new FieldDefinition();
        field.setName("EMPLOYEES_EMP_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(false);
        field.setForeignKeyFieldName("JPA22_EMPLOYEE.EMP_ID");
        table.addField(field);

        field = new FieldDefinition();
        field.setName("projects_PROJ_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(false);
        field.setForeignKeyFieldName("JPA22_PROJECT.PROJ_ID");
        table.addField(field);

        return table;
    }

    public TableDefinition buildPROJECT_PROPSTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA22_PROJ_PROPS");

        FieldDefinition field = new FieldDefinition();
        field.setName("Project_PROJ_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(false);
        field.setUnique(false);
        field.setIsIdentity(false);
        field.setForeignKeyFieldName("JPA22_PROJECT.PROJ_ID");
        table.addField(field);

        field = new FieldDefinition();
        field.setName("PROPS");
        field.setTypeName("VARCHAR");
        field.setSize(45);
        field.setShouldAllowNull(true);
        field.setIsPrimaryKey(false);
        field.setUnique(false);
        field.setIsIdentity(false);
        table.addField(field);

        return table;
    }

    public TableDefinition buildPROJECTTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA22_PROJECT");

        FieldDefinition field = new FieldDefinition();
        field.setName("PROJ_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(true);
        table.addField(field);

        field = new FieldDefinition();
        field.setName("PROJ_TYPE");
        field.setTypeName("VARCHAR");
        field.setSize(1);
        field.setShouldAllowNull(true);
        field.setIsPrimaryKey(false);
        field.setUnique(false);
        field.setIsIdentity(false);
        table.addField(field);

        field = new FieldDefinition();
        field.setName("PROJ_NAME");
        field.setTypeName("VARCHAR");
        field.setSize(30);
        field.setShouldAllowNull(true);
        field.setIsPrimaryKey(false);
        field.setUnique(false);
        field.setIsIdentity(false);
        table.addField(field);

        field = new FieldDefinition();
        field.setName("DESCRIP");
        field.setTypeName("VARCHAR");
        field.setSize(200);
        field.setShouldAllowNull(true);
        field.setIsPrimaryKey(false);
        field.setUnique(false);
        field.setIsIdentity(false);
        table.addField(field);

        field = new FieldDefinition();
        field.setName("LEADER_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(true);
        field.setIsPrimaryKey(false);
        field.setUnique(false);
        field.setIsIdentity(false);
        field.setForeignKeyFieldName("JPA22_EMPLOYEE.EMP_ID");
        table.addField(field);

        field = new FieldDefinition();
        field.setName("VERSION");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(true);
        field.setIsPrimaryKey(false);
        field.setUnique(false);
        field.setIsIdentity(false);
        table.addField(field);

        return table;
    }

    public TableDefinition buildRACETable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA22_RACE");

        FieldDefinition field = new FieldDefinition();
        field.setName("ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(true);
        table.addField(field);

        field = new FieldDefinition();
        field.setName("NAME");
        field.setTypeName("VARCHAR");
        field.setSize(40);
        field.setShouldAllowNull(true);
        field.setIsPrimaryKey(false);
        field.setUnique(false);
        field.setIsIdentity(false);
        table.addField(field);

        return table;
    }

    public TableDefinition buildRESPONSTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA22_RESPONS");

        FieldDefinition field = new FieldDefinition();
        field.setName("EMP_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(false);
        field.setUnique(false);
        field.setIsIdentity(false);
        field.setForeignKeyFieldName("JPA22_EMPLOYEE.EMP_ID");
        table.addField(field);

        field = new FieldDefinition();
        field.setName("DESCRIPTION");
        field.setTypeName("VARCHAR");
        field.setSize(200);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(false);
        field.setUnique(false);
        field.setIsIdentity(false);
        table.addField(field);

        return table;
    }

    public TableDefinition buildRUNNER_ACSTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA22_RUNNER_ACS");

        FieldDefinition field = new FieldDefinition();
        field.setName("ATHLETE_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(false);
        field.setUnique(false);
        field.setIsIdentity(false);
        field.setForeignKeyFieldName("JPA22_RUNNER.ID");
        table.addField(field);

        field = new FieldDefinition();
        field.setName("ACCOMPLISHMENT");
        field.setTypeName("VARCHAR");
        field.setSize(50);
        field.setShouldAllowNull(true);
        field.setIsPrimaryKey(false);
        field.setUnique(false);
        field.setIsIdentity(false);
        table.addField(field);

        field = new FieldDefinition();
        field.setName("THE_DATE");
        field.setTypeName("NUMERIC");
        field.setSize(23);
        field.setShouldAllowNull(true);
        field.setIsPrimaryKey(false);
        field.setUnique(false);
        field.setIsIdentity(false);
        table.addField(field);

        return table;
    }

    public TableDefinition buildRUNNER_PBSTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA22_RUNNER_PBS");

        FieldDefinition field = new FieldDefinition();
        field.setName("RUNNER_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(false);
        field.setUnique(false);
        field.setIsIdentity(false);
        field.setForeignKeyFieldName("JPA22_RUNNER.ID");
        table.addField(field);

        field = new FieldDefinition();
        field.setName("DISTANCE");
        field.setTypeName("VARCHAR");
        field.setSize(25);
        field.setShouldAllowNull(true);
        field.setIsPrimaryKey(false);
        field.setUnique(false);
        field.setIsIdentity(false);
        table.addField(field);

        field = new FieldDefinition();
        field.setName("TIME");
        field.setTypeName("VARCHAR");
        field.setSize(20);
        field.setShouldAllowNull(true);
        field.setIsPrimaryKey(false);
        field.setUnique(false);
        field.setIsIdentity(false);
        table.addField(field);

        return table;
    }

    public TableDefinition buildRUNNERS_RACESTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA22_RUNNERS_RACES");

        FieldDefinition field = new FieldDefinition();
        field.setName("RUNNER_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(false);
        field.setUnique(false);
        field.setIsIdentity(false);
        field.setForeignKeyFieldName("JPA22_RUNNER.ID");
        table.addField(field);

        field = new FieldDefinition();
        field.setName("RACE_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(false);
        field.setUnique(false);
        field.setIsIdentity(false);
        field.setForeignKeyFieldName("JPA22_RACE.ID");
        table.addField(field);

        return table;
    }

    public TableDefinition buildRUNNER_VICTORIES_THIS_YEARable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA22_RUNNER_VTY");
        table.addField(createNumericFk("RUNNER_ID", "JPA22_RUNNER.ID"));
        table.addField(createStringColumn("COMPETITION", 64, true));
        table.addField(createDateColumn("VDATE"));
        table.addField(createNumericPk("ID"));
        table.addField(createStringColumn("NAME"));
        return table;
    }

    public TableDefinition buildRUNNER_VICTORIES_LAST_YEARable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA22_RUNNER_VLY");
        table.addField(createNumericFk("RUNNER_ID", "JPA22_RUNNER.ID"));
        table.addField(createStringColumn("COMPETITION", 64, true));
        table.addField(createDateColumn("VDATE"));
        table.addField(createNumericPk("ID"));
        table.addField(createStringColumn("NAME"));
        return table;
    }

    public TableDefinition buildRUNNERTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA22_RUNNER");

        FieldDefinition field = new FieldDefinition();
        field.setName("ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(true);
        table.addField(field);

        field = new FieldDefinition();
        field.setName("F_NAME");
        field.setTypeName("VARCHAR");
        field.setSize(40);
        field.setShouldAllowNull(true);
        field.setIsPrimaryKey(false);
        field.setUnique(false);
        field.setIsIdentity(false);
        table.addField(field);

        field = new FieldDefinition();
        field.setName("L_NAME");
        field.setTypeName("VARCHAR");
        field.setSize(40);
        field.setShouldAllowNull(true);
        field.setIsPrimaryKey(false);
        field.setUnique(false);
        field.setIsIdentity(false);
        table.addField(field);

        field = new FieldDefinition();
        field.setName("GENDER");
        field.setTypeName("VARCHAR");
        field.setSize(1);
        field.setShouldAllowNull(true);
        field.setIsPrimaryKey(false);
        field.setUnique(false);
        field.setIsIdentity(false);
        table.addField(field);

        field = new FieldDefinition();
        field.setName("AGE");
        field.setTypeName("INTEGER");
        field.setSize(3);
        field.setIsPrimaryKey(false);
        field.setUnique(false);
        field.setIsIdentity(false);
        field.setShouldAllowNull(true);
        table.addField(field);

        field = new FieldDefinition();
        field.setName("R_LEVEL");
        field.setTypeName("VARCHAR");
        field.setSize(20);
        field.setIsPrimaryKey(false);
        field.setUnique(false);
        field.setIsIdentity(false);
        field.setShouldAllowNull(true);
        table.addField(field);

        field = new FieldDefinition();
        field.setName("R_HEALTH");
        field.setTypeName("VARCHAR");
        field.setSize(20);
        field.setShouldAllowNull(true);
        field.setIsPrimaryKey(false);
        field.setUnique(false);
        field.setIsIdentity(false);
        table.addField(field);

        field = new FieldDefinition();
        field.setName("R_STATUS");
        field.setTypeName("VARCHAR");
        field.setSize(20);
        field.setShouldAllowNull(true);
        field.setIsPrimaryKey(false);
        field.setUnique(false);
        field.setIsIdentity(false);
        table.addField(field);

        field = new FieldDefinition();
        field.setName("DTYPE");
        field.setTypeName("VARCHAR");
        field.setSize(15);
        field.setShouldAllowNull(true);
        field.setIsPrimaryKey(false);
        field.setUnique(false);
        field.setIsIdentity(false);
        table.addField(field);

        field = new FieldDefinition();
        field.setName("TAGS");
        field.setTypeName("VARCHAR");
        field.setSize(100);
        field.setShouldAllowNull(true);
        field.setIsPrimaryKey(false);
        field.setUnique(false);
        field.setIsIdentity(false);
        table.addField(field);

        field = new FieldDefinition();
        field.setName("SERIALS");
        field.setTypeName("BLOB");
        field.setShouldAllowNull(true);
        field.setIsPrimaryKey(false);
        field.setUnique(false);
        field.setIsIdentity(false);
        table.addField(field);

        return table;
    }

    public TableDefinition buildSALARYTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA22_SALARY");

        FieldDefinition field = new FieldDefinition();
        field.setName("EMP_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setSubSize(0);
        field.setIsPrimaryKey(true);
        field.setIsIdentity(false);
        field.setUnique(false);
        field.setShouldAllowNull(false);
        field.setForeignKeyFieldName("JPA22_EMPLOYEE.EMP_ID");
        table.addField(field);

        field = new FieldDefinition();
        field.setName("SALARY");
        field.setTypeName("VARCHAR");
        field.setSize(15);
        field.setSubSize(0);
        field.setIsPrimaryKey(false);
        field.setIsIdentity(false);
        field.setUnique(false);
        field.setShouldAllowNull(true);
        table.addField(field);

        field = new FieldDefinition();
        field.setName("PREVIOUSSALARY");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setSubSize(0);
        field.setIsPrimaryKey(false);
        field.setIsIdentity(false);
        field.setUnique(false);
        field.setShouldAllowNull(true);
        table.addField(field);

        return table;
    }

    public TableDefinition buildSTATUSTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA22_STATUS");

        FieldDefinition field = new FieldDefinition();
        field.setName("EMP_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setSubSize(0);
        field.setIsPrimaryKey(true);
        field.setIsIdentity(false);
        field.setUnique(false);
        field.setShouldAllowNull(false);
        field.setForeignKeyFieldName("JPA22_EMPLOYEE.EMP_ID");
        table.addField(field);

        field = new FieldDefinition();
        field.setName("STATUS");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setIsPrimaryKey(false);
        field.setUnique(false);
        field.setIsIdentity(false);
        field.setShouldAllowNull(true);
        table.addField(field);

        return table;
    }

    public TableDefinition buildSHOETable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA22_SHOE");

        FieldDefinition field = new FieldDefinition();
        field.setName("ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(true);
        table.addField(field);

        field = new FieldDefinition();
        field.setName("BRAND");
        field.setTypeName("VARCHAR");
        field.setSize(25);
        field.setShouldAllowNull(true);
        field.setIsPrimaryKey(false);
        field.setUnique(false);
        field.setIsIdentity(false);
        table.addField(field);

        field = new FieldDefinition();
        field.setName("MODEL");
        field.setTypeName("VARCHAR");
        field.setSize(25);
        field.setShouldAllowNull(true);
        field.setIsPrimaryKey(false);
        field.setUnique(false);
        field.setIsIdentity(false);
        table.addField(field);

        field = new FieldDefinition();
        field.setName("SIZZE");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(true);
        field.setIsPrimaryKey(false);
        field.setUnique(false);
        field.setIsIdentity(false);
        table.addField(field);

        field = new FieldDefinition();
        field.setName("TAG_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setIsPrimaryKey(false);
        field.setUnique(false);
        field.setIsIdentity(false);
        field.setShouldAllowNull(true);
        field.setForeignKeyFieldName("JPA22_SHOE_TAG.ID");
        table.addField(field);

        field = new FieldDefinition();
        field.setName("RUNNER_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setIsPrimaryKey(false);
        field.setUnique(false);
        field.setIsIdentity(false);
        field.setShouldAllowNull(true);
        field.setForeignKeyFieldName("JPA22_RUNNER.ID");
        table.addField(field);

        return table;
    }

    public TableDefinition buildSHOETAGTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA22_SHOE_TAG");

        FieldDefinition field = new FieldDefinition();
        field.setName("ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(true);
        table.addField(field);

        field = new FieldDefinition();
        field.setName("TAG");
        field.setTypeName("VARCHAR");
        field.setSize(25);
        field.setShouldAllowNull(true);
        field.setIsPrimaryKey(false);
        field.setUnique(false);
        field.setIsIdentity(false);
        table.addField(field);

        return table;
    }

    public TableDefinition buildSPRINTERTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA22_SPRINTER");

        FieldDefinition field = new FieldDefinition();
        field.setName("SPRINTER_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(true);
        field.setForeignKeyFieldName("JPA22_RUNNER.ID");
        table.addField(field);

        return table;
    }
}
