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

package org.eclipse.persistence.testing.models.jpa.xml.advanced.compositepk;

import org.eclipse.persistence.tools.schemaframework.*;

public class CompositePKTableCreator extends TableCreator {
    public CompositePKTableCreator() {
        setName("EJB3CompositePKProject");

        addTableDefinition(buildADMIN_CONTRACTTable());
        addTableDefinition(buildADMINTable());
        addTableDefinition(buildSCIENTISTTable());
        addTableDefinition(buildDEPARTMENTTable());
        addTableDefinition(buildDEPT_ADMINTable());
        addTableDefinition(buildCUBICLETable());
    }
    
    public static TableDefinition buildADMINTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_XML_ADMIN");
        
        FieldDefinition field = new FieldDefinition();
        field.setName("EMPLOYEE_EMP_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(true);
        table.addField(field);
    
        FieldDefinition field1 = new FieldDefinition();
        field1.setName("CONTRACT_COMPANY");
        field1.setTypeName("VARCHAR");
        field1.setSize(40);
        field1.setShouldAllowNull(true);
        field1.setIsPrimaryKey(false);
        field1.setUnique(false);
        field1.setIsIdentity(false);
        table.addField(field1);
        
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
    
    public static TableDefinition buildADMIN_CONTRACTTable(){
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_XML_ADMIN_CONTRACT");
        
        FieldDefinition field = new FieldDefinition();
        field.setName("EMPLOYEE_EMP_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(true);
        table.addField(field);
    
        FieldDefinition field4 = new FieldDefinition();
        field4.setName("END_DATE");
        field4.setTypeName("DATE");
        field4.setSize(23);
        field4.setShouldAllowNull(true);
        field4.setIsPrimaryKey(false);
        field4.setUnique(false);
        field4.setIsIdentity(false);
        table.addField(field4);

        return table;
    }

    public static TableDefinition buildSCIENTISTTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_XML_SCIENTIST");
    
        FieldDefinition ID_NUMBER_field = new FieldDefinition();
        ID_NUMBER_field.setName("ID_NUMBER");
        ID_NUMBER_field.setTypeName("NUMERIC");
        ID_NUMBER_field.setSize(15);
        ID_NUMBER_field.setShouldAllowNull(false);
        ID_NUMBER_field.setIsPrimaryKey(true);
        ID_NUMBER_field.setUnique(false);
        ID_NUMBER_field.setIsIdentity(true);
        table.addField(ID_NUMBER_field);
    
        FieldDefinition F_NAME_field = new FieldDefinition();
        F_NAME_field.setName("F_NAME");
        F_NAME_field.setTypeName("VARCHAR");
        F_NAME_field.setSize(40);
        F_NAME_field.setShouldAllowNull(false);
        F_NAME_field.setIsPrimaryKey(true);
        F_NAME_field.setUnique(false);
        F_NAME_field.setIsIdentity(true);
        table.addField(F_NAME_field);
    
        FieldDefinition L_NAME_Field = new FieldDefinition();
        L_NAME_Field.setName("L_NAME");
        L_NAME_Field.setTypeName("VARCHAR");
        L_NAME_Field.setSize(40);
        L_NAME_Field.setShouldAllowNull(false);
        L_NAME_Field.setIsPrimaryKey(true);
        L_NAME_Field.setUnique(false);
        L_NAME_Field.setIsIdentity(true);
        table.addField(L_NAME_Field);
    
        FieldDefinition CUBE_ID_field = new FieldDefinition();
        CUBE_ID_field.setName("CUBE_ID");
        CUBE_ID_field.setTypeName("NUMERIC");
        CUBE_ID_field.setSize(15);
        CUBE_ID_field.setShouldAllowNull(true);
        CUBE_ID_field.setIsPrimaryKey(false);
        CUBE_ID_field.setUnique(false);
        CUBE_ID_field.setIsIdentity(false);
        table.addField(CUBE_ID_field);
    
        FieldDefinition CUBE_CODE_field = new FieldDefinition();
        CUBE_CODE_field.setName("CUBE_CODE");
        CUBE_CODE_field.setTypeName("VARCHAR");
        CUBE_CODE_field.setSize(1);
        CUBE_CODE_field.setShouldAllowNull(true);
        CUBE_CODE_field.setIsPrimaryKey(false);
        CUBE_CODE_field.setUnique(false);
        CUBE_CODE_field.setIsIdentity(false);
        table.addField(CUBE_CODE_field);
    
        FieldDefinition DEPT_NAME_field = new FieldDefinition();
        DEPT_NAME_field.setName("DEPT_NAME");
        DEPT_NAME_field.setTypeName("VARCHAR");
        DEPT_NAME_field.setSize(40);
        DEPT_NAME_field.setShouldAllowNull(true);
        DEPT_NAME_field.setIsPrimaryKey(false);
        DEPT_NAME_field.setUnique(false);
        DEPT_NAME_field.setIsIdentity(false);
        table.addField(DEPT_NAME_field);
    
        FieldDefinition DEPT_ROLE_field = new FieldDefinition();
        DEPT_ROLE_field.setName("DEPT_ROLE");
        DEPT_ROLE_field.setTypeName("VARCHAR");
        DEPT_ROLE_field.setSize(40);
        DEPT_ROLE_field.setShouldAllowNull(true);
        DEPT_ROLE_field.setIsPrimaryKey(false);
        DEPT_ROLE_field.setUnique(false);
        DEPT_ROLE_field.setIsIdentity(false);
        table.addField(DEPT_ROLE_field);
    
        FieldDefinition DEPT_LOCATION_field = new FieldDefinition();
        DEPT_LOCATION_field.setName("DEPT_LOCATION");
        DEPT_LOCATION_field.setTypeName("VARCHAR");
        DEPT_LOCATION_field.setSize(40);
        DEPT_LOCATION_field.setShouldAllowNull(true);
        DEPT_LOCATION_field.setIsPrimaryKey(false);
        DEPT_LOCATION_field.setUnique(false);
        DEPT_LOCATION_field.setIsIdentity(false);
        table.addField(DEPT_LOCATION_field);
        
        FieldDefinition fieldDTYPE = new FieldDefinition();
        fieldDTYPE.setName("DTYPE");
        fieldDTYPE.setTypeName("VARCHAR2");
        fieldDTYPE.setSize(20);
        fieldDTYPE.setSubSize(0);
        fieldDTYPE.setIsPrimaryKey(false);
        fieldDTYPE.setIsIdentity(false);
        fieldDTYPE.setUnique(false);
        fieldDTYPE.setShouldAllowNull(true);
        table.addField(fieldDTYPE);

        ForeignKeyConstraint fkConstraint1 = new ForeignKeyConstraint();
        fkConstraint1.setName("XML_SC_CUB");
        fkConstraint1.addSourceField("CUBE_ID");
        fkConstraint1.addSourceField("CUBE_CODE");
        fkConstraint1.setTargetTable("CMP3_XML_CUBICLE");
        fkConstraint1.addTargetField("ID");
        fkConstraint1.addTargetField("CODE");
        table.addForeignKeyConstraint(fkConstraint1);

        ForeignKeyConstraint fkConstraint2 = new ForeignKeyConstraint();
        fkConstraint2.setName("CMP3_XML_SC_DEPT");
        fkConstraint2.addSourceField("DEPT_NAME");
        fkConstraint2.addSourceField("DEPT_ROLE");
        fkConstraint2.addSourceField("DEPT_LOCATION");
        fkConstraint2.setTargetTable("CMP3_XML_DEPARTMENT");
        fkConstraint2.addTargetField("NAME");
        fkConstraint2.addTargetField("DEPT_ROLE");
        fkConstraint2.addTargetField("LOCATION");
        table.addForeignKeyConstraint(fkConstraint2);
       
        return table;
    }
    
    public static TableDefinition buildDEPT_ADMINTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_XML_DEPT_ADMIN");

        // SECTION: FIELD
        FieldDefinition fieldName = new FieldDefinition();
        fieldName.setName("DEPT_NAME");
        fieldName.setTypeName("VARCHAR");
        fieldName.setSize(40);
        fieldName.setShouldAllowNull(false);
        fieldName.setIsPrimaryKey(true);
        fieldName.setUnique(false);
        fieldName.setIsIdentity(false);
        //fieldName.setForeignKeyFieldName("CMP3_XML_DEPARTMENT.NAME");
        table.addField(fieldName);
    
        FieldDefinition ROLE_field = new FieldDefinition();
        ROLE_field.setName("DEPT_ROLE");
        ROLE_field.setTypeName("VARCHAR");
        ROLE_field.setSize(40);
        ROLE_field.setShouldAllowNull(false);
        ROLE_field.setIsPrimaryKey(true);
        ROLE_field.setUnique(false);
        ROLE_field.setIsIdentity(false);
        //ROLE_field.setForeignKeyFieldName("CMP3_XML_DEPARTMENT.ROLE");
        table.addField(ROLE_field);
    
        FieldDefinition LOCATION_field = new FieldDefinition();
        LOCATION_field.setName("DEPT_LOCATION");
        LOCATION_field.setTypeName("VARCHAR");
        LOCATION_field.setSize(40);
        LOCATION_field.setShouldAllowNull(false);
        LOCATION_field.setIsPrimaryKey(true);
        LOCATION_field.setUnique(false);
        LOCATION_field.setIsIdentity(false);
        //LOCATION_field.setForeignKeyFieldName("CMP3_XML_DEPARTMENT.LOCATION");
        table.addField(LOCATION_field);
        
        // SECTION: FIELD
        FieldDefinition fieldEMP = new FieldDefinition();
        fieldEMP.setName("ADMIN_EMPLOYEE_EMP_ID");
        fieldEMP.setTypeName("NUMERIC");
        fieldEMP.setSize(15);
        fieldEMP.setShouldAllowNull(true);
        fieldEMP.setIsPrimaryKey(true);
        fieldEMP.setUnique(false);
        fieldEMP.setIsIdentity(false);
        //fieldEMP.setForeignKeyFieldName("CMP3_XML_EMPLOYEE.EMP_ID");
        table.addField(fieldEMP);

        return table;   
    }
    
    public static TableDefinition buildDEPARTMENTTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_XML_DEPARTMENT");

        FieldDefinition NAME_field = new FieldDefinition();
        NAME_field.setName("NAME");
        NAME_field.setTypeName("VARCHAR");
        NAME_field.setSize(40);
        NAME_field.setShouldAllowNull(false);
        NAME_field.setIsPrimaryKey(true);
        NAME_field.setUnique(false);
        NAME_field.setIsIdentity(true);
        table.addField(NAME_field);
    
        FieldDefinition ROLE_field = new FieldDefinition();
        ROLE_field.setName("DEPT_ROLE");
        ROLE_field.setTypeName("VARCHAR");
        ROLE_field.setSize(40);
        ROLE_field.setShouldAllowNull(false);
        ROLE_field.setIsPrimaryKey(true);
        ROLE_field.setUnique(false);
        ROLE_field.setIsIdentity(true);
        table.addField(ROLE_field);
    
        FieldDefinition LOCATION_field = new FieldDefinition();
        LOCATION_field.setName("LOCATION");
        LOCATION_field.setTypeName("VARCHAR");
        LOCATION_field.setSize(40);
        LOCATION_field.setShouldAllowNull(false);
        LOCATION_field.setIsPrimaryKey(true);
        LOCATION_field.setUnique(false);
        LOCATION_field.setIsIdentity(true);
        table.addField(LOCATION_field);

        return table;
    }

    public static TableDefinition buildCUBICLETable() {
        TableDefinition table = new TableDefinition();
        table.setName("CMP3_XML_CUBICLE");

        FieldDefinition ID_field = new FieldDefinition();
        ID_field.setName("ID");
        ID_field.setTypeName("NUMERIC");
        ID_field.setSize(15);
        ID_field.setShouldAllowNull(false);
        ID_field.setIsPrimaryKey(true);
        ID_field.setUnique(false);
        ID_field.setIsIdentity(true);
        table.addField(ID_field);
    
        FieldDefinition CODE_field = new FieldDefinition();
        CODE_field.setName("CODE");
        CODE_field.setTypeName("VARCHAR");
        CODE_field.setSize(1);
        CODE_field.setShouldAllowNull(false);
        CODE_field.setIsPrimaryKey(true);
        CODE_field.setUnique(false);
        CODE_field.setIsIdentity(true);
        table.addField(CODE_field);
    
        return table;
    }
}
