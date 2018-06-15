/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     01/23/2013-2.5 Guy Pelletier
//       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
//     02/13/2013-2.5 Guy Pelletier
//       - 397772: JPA 2.1 Entity Graph Support (XML support)
package org.eclipse.persistence.testing.models.jpa22.advanced.xml;

import org.eclipse.persistence.testing.framework.TogglingFastTableCreator;

import org.eclipse.persistence.tools.schemaframework.FieldDefinition;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

public class AdvancedTableCreator extends TogglingFastTableCreator {
    public AdvancedTableCreator() {
        setName("JPA22XMLEmployeeProject");

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
        addTableDefinition(buildPROJECT_EMPTable());
        addTableDefinition(buildPROJECT_PROPSTable());
        addTableDefinition(buildRACETable());
        addTableDefinition(buildRESPONSTable());
        addTableDefinition(buildRUNNERTable());
        addTableDefinition(buildRUNNER_PBSTable());
        addTableDefinition(buildRUNNER_ACSTable());
        addTableDefinition(buildRUNNERS_RACESTable());
        addTableDefinition(buildSALARYTable());
        addTableDefinition(buildSHOETable());
        addTableDefinition(buildSHOETAGTable());
        addTableDefinition(buildSPRINTERTable());
    }

    public TableDefinition buildADDRESSTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA22_XML_ADDRESS");

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
        table.setName("JPA22_XML_DEPT_EMP");

        FieldDefinition field = new FieldDefinition();
        field.setName("DEPT_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(false);
        field.setForeignKeyFieldName("JPA22_XML_DEPT.ID");
        table.addField(field);

        field = new FieldDefinition();
        field.setName("MANAGERS_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(false);
        field.setForeignKeyFieldName("JPA22_XML_EMPLOYEE.EMP_ID");
        table.addField(field);

        return table;
    }

    public TableDefinition buildDEPTTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA22_XML_DEPT");

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
        table.setName("JPA22_XML_EMPLOYEE");

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
        field.setName("START_DATE");
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
        field.setForeignKeyFieldName("JPA22_XML_ADDRESS.ADDRESS_ID");
        table.addField(field);

        field = new FieldDefinition();
        field.setName("MANAGER_EMP_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(true);
        field.setIsPrimaryKey(false);
        field.setUnique(false);
        field.setIsIdentity(false);
        field.setForeignKeyFieldName("JPA22_XML_EMPLOYEE.EMP_ID");
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
        field.setForeignKeyFieldName("JPA22_XML_DEPT.ID");
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

    public TableDefinition buildENDORSEMENTSTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA22_XML_ENDORSEMENTS");

        FieldDefinition field = new FieldDefinition();
        field.setName("ATHLETE_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(true);
        field.setForeignKeyFieldName("JPA22_XML_RUNNER.ID");
        table.addField(field);

        field = new FieldDefinition();
        field.setName("ENDORSER_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(true);
        field.setForeignKeyFieldName("JPA22_XML_ENDORSER.ID");
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
        table.setName("JPA22_XML_ENDORSER");

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
        table.setName("JPA22_XML_LPROJECT");

        FieldDefinition field = new FieldDefinition();
        field.setName("PROJ_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(false);
        field.setForeignKeyFieldName("JPA22_XML_PROJECT.PROJ_ID");
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
        field.setForeignKeyFieldName("JPA22_XML_EMPLOYEE.EMP_ID");
        table.addField(field);

        return table;
    }

    public TableDefinition buildORGANIZERTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA22_XML_ORGANIZER");

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
        field.setForeignKeyFieldName("JPA22_XML_RACE.ID");
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
        table.setName("JPA22_XML_PHONENUMBER");

        FieldDefinition field = new FieldDefinition();
        field.setName("OWNER_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(false);
        field.setForeignKeyFieldName("JPA22_XML_EMPLOYEE.EMP_ID");
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

    public TableDefinition buildPROJECT_EMPTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA22_XML_EMP_PROJ");

        FieldDefinition field = new FieldDefinition();
        field.setName("EMPLOYEES_EMP_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(false);
        field.setForeignKeyFieldName("JPA22_XML_EMPLOYEE.EMP_ID");
        table.addField(field);

        field = new FieldDefinition();
        field.setName("projects_PROJ_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(false);
        field.setForeignKeyFieldName("JPA22_XML_PROJECT.PROJ_ID");
        table.addField(field);

        return table;
    }

    public TableDefinition buildPROJECT_PROPSTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA22_XML_PROJ_PROPS");

        FieldDefinition field = new FieldDefinition();
        field.setName("XMLProject_PROJ_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(false);
        field.setUnique(false);
        field.setIsIdentity(false);
        field.setForeignKeyFieldName("JPA22_XML_PROJECT.PROJ_ID");
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
        table.setName("JPA22_XML_PROJECT");

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
        field.setForeignKeyFieldName("JPA22_XML_EMPLOYEE.EMP_ID");
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
        table.setName("JPA22_XML_RACE");

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
        table.setName("JPA22_XML_RESPONS");

        FieldDefinition field = new FieldDefinition();
        field.setName("EMP_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(false);
        field.setUnique(false);
        field.setIsIdentity(false);
        field.setForeignKeyFieldName("JPA22_XML_EMPLOYEE.EMP_ID");
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
        table.setName("JPA22_XML_RUNNER_ACS");

        FieldDefinition field = new FieldDefinition();
        field.setName("ATHLETE_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(false);
        field.setUnique(false);
        field.setIsIdentity(false);
        field.setForeignKeyFieldName("JPA22_XML_RUNNER.ID");
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
        table.setName("JPA22_XML_RUNNER_PBS");

        FieldDefinition field = new FieldDefinition();
        field.setName("RUNNER_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(false);
        field.setUnique(false);
        field.setIsIdentity(false);
        field.setForeignKeyFieldName("JPA22_XML_RUNNER.ID");
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
        table.setName("RUNNERS_RACES");

        FieldDefinition field = new FieldDefinition();
        field.setName("RUNNER_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(false);
        field.setUnique(false);
        field.setIsIdentity(false);
        field.setForeignKeyFieldName("JPA22_XML_RUNNER.ID");
        table.addField(field);

        field = new FieldDefinition();
        field.setName("RACE_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(false);
        field.setUnique(false);
        field.setIsIdentity(false);
        field.setForeignKeyFieldName("JPA22_XML_RACE.ID");
        table.addField(field);

        return table;
    }

    public TableDefinition buildRUNNERTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA22_XML_RUNNER");

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

        return table;
    }

    public TableDefinition buildSALARYTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA22_XML_SALARY");

        FieldDefinition field = new FieldDefinition();
        field.setName("EMP_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setSubSize(0);
        field.setIsPrimaryKey(true);
        field.setIsIdentity(false);
        field.setUnique(false);
        field.setShouldAllowNull(false);
        field.setForeignKeyFieldName("JPA22_XML_EMPLOYEE.EMP_ID");
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

    public TableDefinition buildSHOETable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA22_XML_SHOE");

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
        field.setForeignKeyFieldName("JPA22_XML_SHOE_TAG.ID");
        table.addField(field);

        field = new FieldDefinition();
        field.setName("RUNNER_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setIsPrimaryKey(false);
        field.setUnique(false);
        field.setIsIdentity(false);
        field.setShouldAllowNull(true);
        field.setForeignKeyFieldName("JPA22_XML_RUNNER.ID");
        table.addField(field);

        return table;
    }

    public TableDefinition buildSHOETAGTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA22_XML_SHOE_TAG");

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
        table.setName("JPA22_XML_SPRINTER");

        FieldDefinition field = new FieldDefinition();
        field.setName("SPRINTER_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(true);
        field.setForeignKeyFieldName("JPA22_XML_RUNNER.ID");
        table.addField(field);

        return table;
    }
}
