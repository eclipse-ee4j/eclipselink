/*******************************************************************************
 * Copyright (c) 2012 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     02/08/2012-2.4 Guy Pelletier 
 *       - 350487: JPA 2.1 Specification defined support for Stored Procedure Calls
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa21.advanced;

import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.framework.TogglingFastTableCreator;

import org.eclipse.persistence.tools.schemaframework.FieldDefinition;
import org.eclipse.persistence.tools.schemaframework.SchemaManager;
import org.eclipse.persistence.tools.schemaframework.StoredProcedureDefinition;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

public class AdvancedTableCreator extends TogglingFastTableCreator {
    public AdvancedTableCreator() {
        setName("JPA21EmployeeProject");

        addTableDefinition(buildADDRESSTable());
        addTableDefinition(buildDEPTTable());
        addTableDefinition(buildDEPT_EMPTable());
        addTableDefinition(buildEMPLOYEETable());
        addTableDefinition(buildLARGEPROJECTTable());
        addTableDefinition(buildPHONENUMBERTable());
        addTableDefinition(buildPROJECT_EMPTable());
        addTableDefinition(buildPROJECT_PROPSTable());
        addTableDefinition(buildPROJECTTable());
        addTableDefinition(buildRESPONSTable());
        addTableDefinition(buildSALARYTable());
    }
    
    public TableDefinition buildADDRESSTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA21_ADDRESS");

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
    
    public TableDefinition buildDEPTTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA21_DEPT");

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
    
    public TableDefinition buildDEPT_EMPTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA21_DEPT_JPA21_EMPLOYEE");

        FieldDefinition field = new FieldDefinition();
        field.setName("ADV_DEPT_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(false);
        field.setForeignKeyFieldName("JPA21_DEPT.ID");
        table.addField(field);
        
        field = new FieldDefinition();
        field.setName("managers_EMP_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(false);
        field.setForeignKeyFieldName("JPA21_EMPLOYEE.EMP_ID");
        table.addField(field);
        
        return table;   
    }

    public TableDefinition buildEMPLOYEETable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA21_EMPLOYEE");
    
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
        field.setForeignKeyFieldName("JPA21_ADDRESS.ADDRESS_ID");
        table.addField(field);
    
        field = new FieldDefinition();
        field.setName("MANAGER_EMP_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(true);
        field.setIsPrimaryKey(false);
        field.setUnique(false);
        field.setIsIdentity(false);
        field.setForeignKeyFieldName("JPA21_EMPLOYEE.EMP_ID");
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
        field.setForeignKeyFieldName("JPA21_DEPT.ID");
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
    
    public TableDefinition buildLARGEPROJECTTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA21_LPROJECT");

        FieldDefinition field = new FieldDefinition();
        field.setName("PROJ_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(false);
        field.setForeignKeyFieldName("JPA21_PROJECT.PROJ_ID");
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
    
        return table;
    }
    
    public TableDefinition buildPHONENUMBERTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA21_PHONENUMBER");

        FieldDefinition field = new FieldDefinition();
        field.setName("OWNER_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(false);
        field.setForeignKeyFieldName("JPA21_EMPLOYEE.EMP_ID");
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
        table.setName("JPA21_EMP_PROJ");

        FieldDefinition field = new FieldDefinition();
        field.setName("EMPLOYEES_EMP_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(false);
        field.setForeignKeyFieldName("JPA21_EMPLOYEE.EMP_ID");
        table.addField(field);
    
        field = new FieldDefinition();
        field.setName("projects_PROJ_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(false);
        field.setForeignKeyFieldName("JPA21_PROJECT.PROJ_ID");
        table.addField(field);

        return table;
    }

    public TableDefinition buildPROJECT_PROPSTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA21_PROJ_PROPS");

        FieldDefinition field = new FieldDefinition();
        field.setName("PROJ_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(false);
        field.setUnique(false);
        field.setIsIdentity(false);
        field.setForeignKeyFieldName("JPA21_PROJECT.PROJ_ID");
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
        table.setName("JPA21_PROJECT");

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
        field.setForeignKeyFieldName("JPA21_EMPLOYEE.EMP_ID");
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

    public TableDefinition buildRESPONSTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA21_RESPONS");
    
        FieldDefinition field = new FieldDefinition();
        field.setName("EMP_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(false);
        field.setUnique(false);
        field.setIsIdentity(false);
        field.setForeignKeyFieldName("JPA21_EMPLOYEE.EMP_ID");
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
    
    public TableDefinition buildSALARYTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA21_SALARY");

        FieldDefinition field = new FieldDefinition();
        field.setName("EMP_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setSubSize(0);
        field.setIsPrimaryKey(true);
        field.setIsIdentity(false);
        field.setUnique(false);
        field.setShouldAllowNull(false);
        field.setForeignKeyFieldName("JPA21_EMPLOYEE.EMP_ID");
        table.addField(field);

        field = new FieldDefinition();
        field.setName("SALARY");
        field.setTypeName("NUMBER");
        field.setSize(15);
        field.setSubSize(0);
        field.setIsPrimaryKey(false);
        field.setIsIdentity(false);
        field.setUnique(false);
        field.setShouldAllowNull(true);
        table.addField(field);

        return table;
    }
}
