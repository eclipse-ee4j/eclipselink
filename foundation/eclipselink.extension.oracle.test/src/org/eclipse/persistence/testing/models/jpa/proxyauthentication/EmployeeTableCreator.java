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
package org.eclipse.persistence.testing.models.jpa.proxyauthentication;

import org.eclipse.persistence.tools.schemaframework.*;

public class EmployeeTableCreator extends TableCreator {
    public EmployeeTableCreator() {
        setName("EmployeeTableCreator");

        addTableDefinition(buildEMPLOYEETable());
        addTableDefinition(buildEMPLOYEE_SEQTable());
    }

    public static TableDefinition buildEMPLOYEETable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_PROXY_EMPLOYEE");
    
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

        return table;
    }

    public static TableDefinition buildEMPLOYEE_SEQTable() {
        TableDefinition table = new TableDefinition();
        table.setName("PROXY_EMPLOYEE_SEQ");

        FieldDefinition fieldSEQ_COUNT = new FieldDefinition();
        fieldSEQ_COUNT.setName("SEQ_COUNT");
        fieldSEQ_COUNT.setTypeName("NUMBER");
        fieldSEQ_COUNT.setSize(15);
        fieldSEQ_COUNT.setSubSize(0);
        fieldSEQ_COUNT.setIsPrimaryKey(false);
        fieldSEQ_COUNT.setIsIdentity(false);
        fieldSEQ_COUNT.setUnique(false);
        fieldSEQ_COUNT.setShouldAllowNull(false);
        table.addField(fieldSEQ_COUNT);

        FieldDefinition fieldSEQ_NAME = new FieldDefinition();
        fieldSEQ_NAME.setName("SEQ_NAME");
        fieldSEQ_NAME.setTypeName("VARCHAR2");
        fieldSEQ_NAME.setSize(80);
        fieldSEQ_NAME.setSubSize(0);
        fieldSEQ_NAME.setIsPrimaryKey(true);
        fieldSEQ_NAME.setIsIdentity(false);
        fieldSEQ_NAME.setUnique(false);
        fieldSEQ_NAME.setShouldAllowNull(false);
        table.addField(fieldSEQ_NAME);

        return table;
    }
    
}
