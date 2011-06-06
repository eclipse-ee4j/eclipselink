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
package org.eclipse.persistence.testing.models.mapping;

import org.eclipse.persistence.tools.schemaframework.FieldDefinition;
import org.eclipse.persistence.tools.schemaframework.ForeignKeyConstraint;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;

public class LegacyTableMaker extends org.eclipse.persistence.tools.schemaframework.TableCreator {

    public LegacyTableMaker() {
        setName("LegacyTestCase");
     
        buildMUL_ADDRTable();
        buildMUL_EMPTable();
        buildMUL_CTRYTable();
    }

    protected void buildMUL_ADDRTable() {
        TableDefinition tabledefinition = new TableDefinition();

        // SECTION: TABLE
        tabledefinition.setName("MUL_ADDR");

        // SECTION: FIELD
        FieldDefinition field = new FieldDefinition();
        field.setName("CITY");
        field.setTypeName("VARCHAR");
        field.setSize(10);
        field.setShouldAllowNull(true);
        field.setIsPrimaryKey(false);
        field.setUnique(false);
        field.setIsIdentity(false);
        tabledefinition.addField(field);

        // SECTION: FIELD
        FieldDefinition field1 = new FieldDefinition();
        field1.setName("CNTRY_ID");
        field1.setTypeName("NUMERIC");
        field1.setSize(5);
        field1.setShouldAllowNull(true);
        field1.setIsPrimaryKey(false);
        field1.setUnique(false);
        field1.setIsIdentity(false);
        tabledefinition.addField(field1);

        // SECTION: FIELD
        FieldDefinition field2 = new FieldDefinition();
        field2.setName("PROVINCE");
        field2.setTypeName("VARCHAR");
        field2.setSize(10);
        field2.setShouldAllowNull(true);
        field2.setIsPrimaryKey(false);
        field2.setUnique(false);
        field2.setIsIdentity(false);
        tabledefinition.addField(field2);

        // SECTION: FIELD
        FieldDefinition field3 = new FieldDefinition();
        field3.setName("ADDR_ID");
        field3.setTypeName("NUMERIC");
        field3.setSize(10);
        field3.setShouldAllowNull(false);
        field3.setIsPrimaryKey(true);
        field3.setUnique(false);
        field3.setIsIdentity(false);
        tabledefinition.addField(field3);
        
        ForeignKeyConstraint foreignKeyADDRESS_COUNTRY = new ForeignKeyConstraint();
        foreignKeyADDRESS_COUNTRY.setName("ADDRESS_COUNTRY");
        foreignKeyADDRESS_COUNTRY.setTargetTable("MUL_CTRY");
        foreignKeyADDRESS_COUNTRY.addSourceField("CNTRY_ID");
        foreignKeyADDRESS_COUNTRY.addTargetField("CNTRY_ID");
        tabledefinition.addForeignKeyConstraint(foreignKeyADDRESS_COUNTRY);
        
        addTableDefinition(tabledefinition);
    }

    protected void buildMUL_CTRYTable() {
        TableDefinition tabledefinition = new TableDefinition();

        // SECTION: TABLE
        tabledefinition.setName("MUL_CTRY");

        // SECTION: FIELD
        FieldDefinition field = new FieldDefinition();
        field.setName("COUNTRY");
        field.setTypeName("VARCHAR");
        field.setSize(10);
        field.setShouldAllowNull(true);
        field.setIsPrimaryKey(false);
        field.setUnique(false);
        field.setIsIdentity(false);
        tabledefinition.addField(field);

        // SECTION: FIELD
        FieldDefinition field1 = new FieldDefinition();
        field1.setName("CNTRY_ID");
        field1.setTypeName("NUMERIC");
        field1.setSize(5);
        field1.setShouldAllowNull(false);
        field1.setIsPrimaryKey(true);
        field1.setUnique(false);
        field1.setIsIdentity(false);
        tabledefinition.addField(field1);
        addTableDefinition(tabledefinition);

    }

    protected void buildMUL_EMPTable() {
        TableDefinition tabledefinition = new TableDefinition();

        // SECTION: TABLE
        tabledefinition.setName("MUL_EMP");

        // SECTION: FIELD
        FieldDefinition field = new FieldDefinition();
        field.setName("FIRST_NAME");
        field.setTypeName("VARCHAR");
        field.setSize(10);
        field.setShouldAllowNull(true);
        field.setIsPrimaryKey(false);
        field.setUnique(false);
        field.setIsIdentity(false);
        tabledefinition.addField(field);

        // SECTION: FIELD
        FieldDefinition field1 = new FieldDefinition();
        field1.setName("EMP_ID");
        field1.setTypeName("NUMERIC");
        field1.setSize(10);
        field1.setShouldAllowNull(false);
        field1.setIsPrimaryKey(true);
        field1.setUnique(false);
        field1.setIsIdentity(false);
        tabledefinition.addField(field1);

        // SECTION: FIELD
        FieldDefinition field2 = new FieldDefinition();
        field2.setName("LAST_NAME");
        field2.setTypeName("VARCHAR");
        field2.setSize(10);
        field2.setShouldAllowNull(true);
        field2.setIsPrimaryKey(false);
        field2.setUnique(false);
        field2.setIsIdentity(false);
        tabledefinition.addField(field2);

        // SECTION: FIELD
        FieldDefinition field3 = new FieldDefinition();
        field3.setName("SEX");
        field3.setTypeName("VARCHAR");
        field3.setSize(10);
        field3.setShouldAllowNull(true);
        field3.setIsPrimaryKey(false);
        field3.setUnique(false);
        field3.setIsIdentity(false);
        tabledefinition.addField(field3);

        // SECTION: FIELD
        FieldDefinition field4 = new FieldDefinition();
        field4.setName("ADDR_ID");
        field4.setTypeName("NUMERIC");
        field4.setSize(10);
        field4.setShouldAllowNull(true);
        field4.setIsPrimaryKey(false);
        field4.setUnique(false);
        field4.setIsIdentity(false);
        tabledefinition.addField(field4);
        addTableDefinition(tabledefinition);
        
        ForeignKeyConstraint foreignKeyEMPLOYEE_ADDRESS = new ForeignKeyConstraint();
        foreignKeyEMPLOYEE_ADDRESS.setName("EMPLOYEE_ADDRESS");
        foreignKeyEMPLOYEE_ADDRESS.setTargetTable("MUL_ADDR");
        foreignKeyEMPLOYEE_ADDRESS.addSourceField("ADDR_ID");
        foreignKeyEMPLOYEE_ADDRESS.addTargetField("ADDR_ID");
        tabledefinition.addForeignKeyConstraint(foreignKeyEMPLOYEE_ADDRESS);
    }
}
