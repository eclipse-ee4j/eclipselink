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
package org.eclipse.persistence.testing.models.mapping;

import org.eclipse.persistence.tools.schemaframework.FieldDefinition;
import org.eclipse.persistence.tools.schemaframework.TableCreator;
import org.eclipse.persistence.tools.schemaframework.TableDefinition;
import org.eclipse.persistence.tools.schemaframework.ForeignKeyConstraint;

public class MultipleTableTestTableMaker extends TableCreator {
    public MultipleTableTestTableMaker() {
        setName("MultipleTableTestCase");
        buildMUL2_EMPTable();
        buildMUL2_EMP_INFOTable();
    }

    protected void buildMUL2_EMP_INFOTable() {
        TableDefinition tabledefinition = new TableDefinition();

        // SECTION: TABLE
        tabledefinition.setName("MUL2_EMP_INFO");

        // SECTION: FIELD
        FieldDefinition field2 = new FieldDefinition();
        field2.setName("INFO");
        field2.setTypeName("VARCHAR");
        field2.setSize(30);
        field2.setShouldAllowNull(true);
        field2.setIsPrimaryKey(false);
        field2.setUnique(false);
        field2.setIsIdentity(false);
        tabledefinition.addField(field2);

        // SECTION: FIELD
        FieldDefinition field3 = new FieldDefinition();
        field3.setName("EMP_NUM");
        field3.setTypeName("NUMERIC");
        field3.setSize(10);
        field3.setShouldAllowNull(false);
        field3.setIsPrimaryKey(true);
        field3.setUnique(false);
        field3.setIsIdentity(false);
        tabledefinition.addField(field3);
        
        ForeignKeyConstraint foreignKeyEMP_INFO_EMP = new ForeignKeyConstraint();
        foreignKeyEMP_INFO_EMP.setName("EMP_INFO_EMP");
        foreignKeyEMP_INFO_EMP.setTargetTable("MUL2_EMP");
        foreignKeyEMP_INFO_EMP.addSourceField("EMP_NUM");
        foreignKeyEMP_INFO_EMP.addTargetField("EMP_NUM");
        tabledefinition.addForeignKeyConstraint(foreignKeyEMP_INFO_EMP);
        
        addTableDefinition(tabledefinition);
    }

    protected void buildMUL2_EMPTable() {
        TableDefinition tabledefinition = new TableDefinition();

        // SECTION: TABLE
        tabledefinition.setName("MUL2_EMP");

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
        field2.setName("EMP_NUM");
        field2.setTypeName("NUMERIC");
        field2.setSize(10);
        field2.setShouldAllowNull(false);
        field2.setIsPrimaryKey(true);
        field2.setUnique(true);
        field2.setIsIdentity(false);
        tabledefinition.addField(field2);

        // SECTION: FIELD
        FieldDefinition field3 = new FieldDefinition();
        field3.setName("FNAME");
        field3.setTypeName("VARCHAR");
        field3.setSize(30);
        field3.setShouldAllowNull(true);
        field3.setIsPrimaryKey(false);
        field3.setUnique(false);
        field3.setIsIdentity(false);
        tabledefinition.addField(field3);
        
        addTableDefinition(tabledefinition);

    }
}
