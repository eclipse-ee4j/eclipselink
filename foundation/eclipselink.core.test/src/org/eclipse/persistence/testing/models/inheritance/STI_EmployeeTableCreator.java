/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.models.inheritance;


public class STI_EmployeeTableCreator extends org.eclipse.persistence.tools.schemaframework.TableCreator {

    public STI_EmployeeTableCreator() {
        applyPROJECT();
        buildEMPLOYEETable();
        buildPROJ_EMPTable();
        buildPROJECTTable();
    }

    protected void applyPROJECT() {
        setName("STI_Employee");
    }

    protected void buildEMPLOYEETable() {
        org.eclipse.persistence.tools.schemaframework.TableDefinition tabledefinition = new org.eclipse.persistence.tools.schemaframework.TableDefinition();

        // SECTION: TABLE
        tabledefinition.setName("STI_EMPLOYEE");

        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field.setName("EMP_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(true);
        tabledefinition.addField(field);

        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field1 = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field1.setName("F_NAME");
        field1.setTypeName("VARCHAR");
        field1.setSize(40);
        field1.setShouldAllowNull(true);
        field1.setIsPrimaryKey(false);
        field1.setUnique(false);
        field1.setIsIdentity(false);
        tabledefinition.addField(field1);

        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field2 = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field2.setName("L_NAME");
        field2.setTypeName("VARCHAR");
        field2.setSize(40);
        field2.setShouldAllowNull(true);
        field2.setIsPrimaryKey(false);
        field2.setUnique(false);
        field2.setIsIdentity(false);
        tabledefinition.addField(field2);

        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field9 = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field9.setName("MANAGER_ID");
        field9.setTypeName("NUMERIC");
        field9.setSize(15);
        field9.setShouldAllowNull(true);
        field9.setIsPrimaryKey(false);
        field9.setUnique(false);
        field9.setIsIdentity(false);
        field9.setForeignKeyFieldName("STI_EMPLOYEE.EMP_ID");
        tabledefinition.addField(field9);

        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field10 = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
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

    protected void buildPROJ_EMPTable() {
        org.eclipse.persistence.tools.schemaframework.TableDefinition tabledefinition = new org.eclipse.persistence.tools.schemaframework.TableDefinition();

        // SECTION: TABLE
        tabledefinition.setName("STI_PROJ_EMP");

        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field.setName("EMP_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(false);
        field.setForeignKeyFieldName("STI_EMPLOYEE.EMP_ID");
        tabledefinition.addField(field);

        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field1 = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field1.setName("PROJ_ID");
        field1.setTypeName("NUMERIC");
        field1.setSize(15);
        field1.setShouldAllowNull(false);
        field1.setIsPrimaryKey(true);
        field1.setUnique(false);
        field1.setIsIdentity(false);
        field1.setForeignKeyFieldName("STI_PROJECT.PROJ_ID");
        tabledefinition.addField(field1);
        addTableDefinition(tabledefinition);
    }

    protected void buildPROJECTTable() {
        org.eclipse.persistence.tools.schemaframework.TableDefinition tabledefinition = new org.eclipse.persistence.tools.schemaframework.TableDefinition();

        // SECTION: TABLE
        tabledefinition.setName("STI_PROJECT");

        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field.setName("PROJ_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(true);
        tabledefinition.addField(field);

        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field1 = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field1.setName("PROJ_TYPE");
        field1.setTypeName("VARCHAR");
        field1.setSize(1);
        field1.setShouldAllowNull(true);
        field1.setIsPrimaryKey(false);
        field1.setUnique(false);
        field1.setIsIdentity(false);
        tabledefinition.addField(field1);

        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field2 = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field2.setName("PROJ_NAME");
        field2.setTypeName("VARCHAR");
        field2.setSize(30);
        field2.setShouldAllowNull(true);
        field2.setIsPrimaryKey(false);
        field2.setUnique(false);
        field2.setIsIdentity(false);
        tabledefinition.addField(field2);

        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field3 = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field3.setName("DESCRIP");
        field3.setTypeName("VARCHAR");
        field3.setSize(200);
        field3.setShouldAllowNull(true);
        field3.setIsPrimaryKey(false);
        field3.setUnique(false);
        field3.setIsIdentity(false);
        tabledefinition.addField(field3);

        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field4 = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field4.setName("LEADER_ID");
        field4.setTypeName("NUMERIC");
        field4.setSize(15);
        field4.setShouldAllowNull(true);
        field4.setIsPrimaryKey(false);
        field4.setUnique(false);
        field4.setIsIdentity(false);
        field4.setForeignKeyFieldName("STI_EMPLOYEE.EMP_ID");
        tabledefinition.addField(field4);

        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field5 = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field5.setName("VERSION");
        field5.setTypeName("NUMERIC");
        field5.setSize(15);
        field5.setShouldAllowNull(true);
        field5.setIsPrimaryKey(false);
        field5.setUnique(false);
        field5.setIsIdentity(false);
        tabledefinition.addField(field5);
        addTableDefinition(tabledefinition);

        // SECTION: FIELD
        org.eclipse.persistence.tools.schemaframework.FieldDefinition field6 = new org.eclipse.persistence.tools.schemaframework.FieldDefinition();
        field6.setName("BUDGET");
        field6.setTypeName("DOUBLE PRECIS");
        field6.setSize(18);
        field6.setShouldAllowNull(true);
        field6.setIsPrimaryKey(false);
        field6.setUnique(false);
        field6.setIsIdentity(false);
        tabledefinition.addField(field6);
    }
}
