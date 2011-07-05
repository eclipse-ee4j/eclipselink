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
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.partitioned;

import org.eclipse.persistence.testing.framework.TogglingFastTableCreator;
import org.eclipse.persistence.tools.schemaframework.*;

public class PartitionedTableCreator extends TogglingFastTableCreator {
    public PartitionedTableCreator() {
        setName("PART EmployeeProject");

        addTableDefinition(buildADDRESSTable());
        addTableDefinition(buildEMPLOYEETable());
        addTableDefinition(buildLARGEPROJECTTable());
        addTableDefinition(buildPHONENUMBERTable());
        addTableDefinition(buildPROJECT_EMPTable());
        addTableDefinition(buildPROJECTTable());
        addTableDefinition(buildRESPONSTable());
        addTableDefinition(buildDEPTTable());
        addTableDefinition(buildDEPT_EMPTable());
    }
    
    public TableDefinition buildADDRESSTable() {
        TableDefinition table = new TableDefinition();
        table.setName("PART_ADDRESS");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ADDRESS_ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setIsPrimaryKey(true);
        fieldID.setShouldAllowNull(false);
        table.addField(fieldID);

        FieldDefinition fieldSTREET = new FieldDefinition();
        fieldSTREET.setName("STREET");
        fieldSTREET.setTypeName("VARCHAR2");
        fieldSTREET.setSize(60);
        table.addField(fieldSTREET);

        FieldDefinition fieldCITY = new FieldDefinition();
        fieldCITY.setName("CITY");
        fieldCITY.setTypeName("VARCHAR2");
        fieldCITY.setSize(60);
        table.addField(fieldCITY);

        FieldDefinition fieldPROVINCE = new FieldDefinition();
        fieldPROVINCE.setName("PROVINCE");
        fieldPROVINCE.setTypeName("VARCHAR2");
        fieldPROVINCE.setSize(60);
        table.addField(fieldPROVINCE);

        FieldDefinition fieldPOSTALCODE = new FieldDefinition();
        fieldPOSTALCODE.setName("P_CODE");
        fieldPOSTALCODE.setTypeName("VARCHAR2");
        fieldPOSTALCODE.setSize(67);
        table.addField(fieldPOSTALCODE);

        FieldDefinition fieldCOUNTRY = new FieldDefinition();
        fieldCOUNTRY.setName("COUNTRY");
        fieldCOUNTRY.setTypeName("VARCHAR2");
        fieldCOUNTRY.setSize(60);
        table.addField(fieldCOUNTRY);

        FieldDefinition fieldType = new FieldDefinition();
        fieldType.setName("TYPE");
        fieldType.setTypeName("VARCHAR2");
        fieldType.setSize(150);
        table.addField(fieldType);
        
        FieldDefinition fieldVERSION = new FieldDefinition();
        fieldVERSION.setName("VERSION");
        fieldVERSION.setTypeName("NUMERIC");
        fieldVERSION.setSize(15);
        table.addField(fieldVERSION);
        
        return table;
    }

    public TableDefinition buildEMPLOYEETable() {
        TableDefinition table = new TableDefinition();
        table.setName("PART_EMPLOYEE");
    
        FieldDefinition field = new FieldDefinition();
        field.setName("EMP_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        table.addField(field);
        
        FieldDefinition location = new FieldDefinition();
        location.setName("LOCATION");
        location.setTypeName("VARCHAR");
        location.setSize(64);
        location.setShouldAllowNull(false);
        location.setIsPrimaryKey(true);
        table.addField(location);
    
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

        FieldDefinition field8 = new FieldDefinition();
        field8.setName("ADDR_ID");
        field8.setTypeName("NUMERIC");
        field8.setSize(15);
        //field8.setForeignKeyFieldName("PART_ADDRESS.ADDRESS_ID");
        table.addField(field8);
    
        FieldDefinition field9 = new FieldDefinition();
        field9.setName("MANAGER_EMP_ID");
        field9.setTypeName("NUMERIC");
        field9.setSize(15);
        //field9.setForeignKeyFieldName("PART_EMPLOYEE.EMP_ID");
        table.addField(field9);
        
        FieldDefinition mgrLocation = new FieldDefinition();
        mgrLocation.setName("MANAGER_LOCATION");
        mgrLocation.setTypeName("VARCHAR");
        mgrLocation.setSize(64);
        table.addField(mgrLocation);
    
        FieldDefinition field10 = new FieldDefinition();
        field10.setName("VERSION");
        field10.setTypeName("NUMERIC");
        field10.setSize(15);
        table.addField(field10);
        
        FieldDefinition fieldDEPT = new FieldDefinition();
        fieldDEPT.setName("DEPT_ID");
        fieldDEPT.setTypeName("NUMERIC");
        fieldDEPT.setSize(15);
        table.addField(fieldDEPT);
        
        return table;
    }
    
    
    public TableDefinition buildLARGEPROJECTTable() {
        TableDefinition table = new TableDefinition();
        table.setName("PART_LPROJECT");

        // SECTION: FIELD
        FieldDefinition field = new FieldDefinition();
        field.setName("PROJ_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false );
        field.setIsPrimaryKey(true );
        field.setForeignKeyFieldName("PART_PROJECT.PROJ_ID");
        table.addField(field);
    
        // SECTION: FIELD
        FieldDefinition field1 = new FieldDefinition();
        field1.setName("BUDGET");
        field1.setTypeName("DOUBLE PRECIS");
        field1.setSize(18);
        table.addField(field1);
    
        return table;
    }
        
    public TableDefinition buildPHONENUMBERTable() {
        TableDefinition table = new TableDefinition();
        table.setName("PART_PHONENUMBER");

        // SECTION: FIELD
        FieldDefinition field = new FieldDefinition();
        field.setName("OWNER_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false );
        field.setIsPrimaryKey(true );
        //field.setForeignKeyFieldName("PART_EMPLOYEE.EMP_ID");
        table.addField(field);
        
        FieldDefinition location = new FieldDefinition();
        location.setName("LOCATION");
        location.setTypeName("VARCHAR");
        location.setSize(64);
        location.setShouldAllowNull(false);
        location.setIsPrimaryKey(true);
        table.addField(location);
    
        // SECTION: FIELD
        FieldDefinition field1 = new FieldDefinition();
        field1.setName("TYPE");
        field1.setTypeName("VARCHAR");
        field1.setSize(15);
        field1.setShouldAllowNull(false );
        field1.setIsPrimaryKey(true );
        table.addField(field1);
    
        // SECTION: FIELD
        FieldDefinition field2 = new FieldDefinition();
        field2.setName("AREA_CODE");
        field2.setTypeName("VARCHAR");
        field2.setSize(3);
        table.addField(field2);
    
        // SECTION: FIELD
        FieldDefinition field3 = new FieldDefinition();
        field3.setName("NUMB");
        field3.setTypeName("VARCHAR");
        field3.setSize(8);
        table.addField(field3);
        
        ForeignKeyConstraint foreignKey = new ForeignKeyConstraint();
        foreignKey.setName("FK_PART_PHONE_OWNER");
        foreignKey.setTargetTable("PART_EMPLOYEE");
        foreignKey.addSourceField("OWNER_ID");
        foreignKey.addTargetField("EMP_ID");
        foreignKey.addSourceField("LOCATION");
        foreignKey.addTargetField("LOCATION");
        table.addForeignKeyConstraint(foreignKey);
        
        return table;
    }
        
    public TableDefinition buildPROJECT_EMPTable() {
        TableDefinition table = new TableDefinition();

        table.setName("PART_EMP_PROJ");

        // SECTION: FIELD
        FieldDefinition field = new FieldDefinition();
        field.setName("EMPLOYEES_EMP_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false );
        field.setIsPrimaryKey(true );
        //field.setForeignKeyFieldName("PART_EMPLOYEE.EMP_ID");
        table.addField(field);
        
        FieldDefinition location = new FieldDefinition();
        location.setName("LOCATION");
        location.setTypeName("VARCHAR");
        location.setSize(64);
        location.setShouldAllowNull(false);
        location.setIsPrimaryKey(true);
        table.addField(location);
    
        // SECTION: FIELD
        FieldDefinition field1 = new FieldDefinition();
        field1.setName("PROJECTS_PROJ_ID");
        field1.setTypeName("NUMERIC");
        field1.setSize(15);
        field1.setShouldAllowNull(false );
        field1.setIsPrimaryKey(true );
        //field1.setForeignKeyFieldName("PART_PROJECT.PROJ_ID");
        table.addField(field1);
        
        /*ForeignKeyConstraint foreignKey = new ForeignKeyConstraint();
        foreignKey.setName("FK_PART_PROJ_EMP");
        foreignKey.setTargetTable("PART_EMPLOYEE");
        foreignKey.addSourceField("EMPLOYEES_EMP_ID");
        foreignKey.addTargetField("EMP_ID");
        foreignKey.addSourceField("LOCATION");
        foreignKey.addTargetField("LOCATION");
        table.addForeignKeyConstraint(foreignKey);*/

        return table;
    }
    
    public TableDefinition buildPROJECTTable() {
        TableDefinition table = new TableDefinition();

        table.setName("PART_PROJECT");

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
        field1.setName("PROJ_TYPE");
        field1.setTypeName("VARCHAR");
        field1.setSize(1);
        table.addField(field1);
    
        // SECTION: FIELD
        FieldDefinition field2 = new FieldDefinition();
        field2.setName("PROJ_NAME");
        field2.setTypeName("VARCHAR");
        field2.setSize(30);
        table.addField(field2);
    
        // SECTION: FIELD
        FieldDefinition field3 = new FieldDefinition();
        field3.setName("DESCRIP");
        field3.setTypeName("VARCHAR");
        field3.setSize(200);
        table.addField(field3);
    
        // SECTION: FIELD
        FieldDefinition field4 = new FieldDefinition();
        field4.setName("LEADER_ID");
        field4.setTypeName("NUMERIC");
        field4.setSize(15);
        //field4.setForeignKeyFieldName("PART_EMPLOYEE.EMP_ID");
        table.addField(field4);
        
        FieldDefinition location = new FieldDefinition();
        location.setName("LEADER_LOCATION");
        location.setTypeName("VARCHAR");
        location.setSize(64);
        table.addField(location);
    
        // SECTION: FIELD
        FieldDefinition field5 = new FieldDefinition();
        field5.setName("VERSION");
        field5.setTypeName("NUMERIC");
        field5.setSize(15);
        table.addField(field5);

        return table;
    }

    public TableDefinition buildRESPONSTable() {
        TableDefinition table = new TableDefinition();
        // SECTION: TABLE
        table.setName("PART_RESPONS");
    
        // SECTION: FIELD
        FieldDefinition field = new FieldDefinition();
        field.setName("EMP_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        //field.setForeignKeyFieldName("PART_EMPLOYEE.EMP_ID");
        table.addField(field);
        
        FieldDefinition location = new FieldDefinition();
        location.setName("LOCATION");
        location.setTypeName("VARCHAR");
        location.setSize(64);
        location.setShouldAllowNull(false);
        location.setIsPrimaryKey(true);
        table.addField(location);
    
        // SECTION: FIELD
        FieldDefinition field1 = new FieldDefinition();
        field1.setName("DESCRIPTION");
        field1.setTypeName("VARCHAR");
        field1.setSize(200);
        field1.setShouldAllowNull(false);
        field1.setIsPrimaryKey(true);
        table.addField(field1);
        
        ForeignKeyConstraint foreignKey = new ForeignKeyConstraint();
        foreignKey.setName("FK_PART_RESPONS");
        foreignKey.setTargetTable("PART_EMPLOYEE");
        foreignKey.addSourceField("EMP_ID");
        foreignKey.addTargetField("EMP_ID");
        foreignKey.addSourceField("LOCATION");
        foreignKey.addTargetField("LOCATION");
        table.addForeignKeyConstraint(foreignKey);
    
        return table;
    }
    
    public TableDefinition buildDEPTTable() {
       TableDefinition table = new TableDefinition();
       table.setName("PART_DEPT");

       FieldDefinition fieldID = new FieldDefinition();
       fieldID.setName("ID");
       fieldID.setTypeName("NUMERIC");
       fieldID.setSize(15);
       fieldID.setIsPrimaryKey(true);
       fieldID.setShouldAllowNull(false);
       table.addField(fieldID);

       FieldDefinition fieldNAME = new FieldDefinition();
       fieldNAME.setName("NAME");
       fieldNAME.setTypeName("VARCHAR2");
       fieldNAME.setSize(60);
       fieldNAME.setSubSize(0);
       table.addField(fieldNAME);

       FieldDefinition fieldHEAD = new FieldDefinition();
       fieldHEAD.setName("DEPT_HEAD");
       fieldHEAD.setTypeName("NUMERIC");
       fieldHEAD.setSize(15);
       fieldHEAD.setSubSize(0);
       table.addField(fieldHEAD);

       FieldDefinition location = new FieldDefinition();
       location.setName("DEPT_HEAD_LOCATION");
       location.setTypeName("VARCHAR");
       location.setSize(64);
       table.addField(location);
       
       return table;
   }
   
   public TableDefinition buildDEPT_EMPTable() {
       TableDefinition table = new TableDefinition();
       table.setName("PART_DEPT_PART_EMPLOYEE");

       // SECTION: FIELD
       FieldDefinition fieldID = new FieldDefinition();
       fieldID.setName("Department_ID");
       fieldID.setTypeName("NUMERIC");
       fieldID.setSize(15);
       fieldID.setShouldAllowNull(false);
       fieldID.setIsPrimaryKey(true);
       fieldID.setUnique(false);
       fieldID.setIsIdentity(false);
       //fieldID.setForeignKeyFieldName("PART_DEPT.ID");
       table.addField(fieldID);
       
       // SECTION: FIELD
       FieldDefinition fieldEMP = new FieldDefinition();
       fieldEMP.setName("EMP_ID");
       fieldEMP.setTypeName("NUMERIC");
       fieldEMP.setSize(15);
       fieldEMP.setShouldAllowNull(false);
       fieldEMP.setIsPrimaryKey(true);
       fieldEMP.setUnique(false);
       fieldEMP.setIsIdentity(false);
       //fieldEMP.setForeignKeyFieldName("CMP3_EMPLOYEE.EMP_ID");
       table.addField(fieldEMP);
       
       FieldDefinition location = new FieldDefinition();
       location.setName("LOCATION");
       location.setTypeName("VARCHAR");
       location.setSize(64);
       location.setShouldAllowNull(false);
       location.setIsPrimaryKey(true);
       table.addField(location);
       
       return table;   
   }

}
