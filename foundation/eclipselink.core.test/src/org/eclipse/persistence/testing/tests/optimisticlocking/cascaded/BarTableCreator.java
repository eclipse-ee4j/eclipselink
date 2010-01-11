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
package org.eclipse.persistence.testing.tests.optimisticlocking.cascaded;

import org.eclipse.persistence.tools.schemaframework.*;

public class BarTableCreator extends TableCreator {
    public BarTableCreator() {
        setName("Bar");
        
        addTableDefinition(buildAWARDTable());
        addTableDefinition(buildBARTable());
        addTableDefinition(buildBARTENDERTable());
        addTableDefinition(buildLICENSETable());
        addTableDefinition(buildQUALIFICATIONTable());
    }
    
     protected TableDefinition buildAWARDTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CASCADE_AWARD");
        
        FieldDefinition field = new FieldDefinition();
        field.setName("ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(true);
        table.addField(field);
    
        FieldDefinition field1 = new FieldDefinition();
        field1.setName("DESCRIPTION");
        field1.setTypeName("VARCHAR");
        field1.setSize(50);
        field1.setShouldAllowNull(false);
        field1.setIsPrimaryKey(false);
        field1.setUnique(false);
        field1.setIsIdentity(false);
        table.addField(field1);
        
        FieldDefinition field2 = new FieldDefinition();
        field2.setName("QUALIFICATION_ID");
        field2.setTypeName("NUMERIC");
        field2.setSize(15);
        field2.setShouldAllowNull(false);
        field2.setIsPrimaryKey(false);
        field2.setUnique(false);
        field2.setIsIdentity(false);
        field2.setForeignKeyFieldName("CASCADE_QUALIFICATION.ID");
        table.addField(field2);
        
        return table;
    }
    
    protected TableDefinition buildBARTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CASCADE_BAR");

        FieldDefinition field = new FieldDefinition();
        field.setName("ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(true);
        table.addField(field);

        FieldDefinition field1 = new FieldDefinition();
        field1.setName("NAME");
        field1.setTypeName("VARCHAR");
        field1.setSize(40);
        field1.setShouldAllowNull(true);
        field1.setIsPrimaryKey(false);
        field1.setUnique(false);
        field1.setIsIdentity(false);
        table.addField(field1);

        FieldDefinition field2 = new FieldDefinition();
        field2.setName("LICENSE_ID");
        field2.setTypeName("NUMERIC");
        field2.setSize(15);
        field2.setShouldAllowNull(true);
        field2.setIsPrimaryKey(false);
        field2.setUnique(false);
        field2.setIsIdentity(false);
        field2.setForeignKeyFieldName("CASCADE_LICENSE.ID");
        table.addField(field2);

        FieldDefinition field3 = new FieldDefinition();
        field3.setName("VERSION");
        field3.setTypeName("NUMERIC");
        field3.setSize(15);
        field3.setShouldAllowNull(false);
        field3.setIsPrimaryKey(false);
        field3.setUnique(false);
        field3.setIsIdentity(false);
        table.addField(field3);
        
        return table;
    }
    
    protected TableDefinition buildBARTENDERTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CASCADE_BARTENDER");
        
        FieldDefinition field = new FieldDefinition();
        field.setName("ID");
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
        field1.setSize(20);
        field1.setShouldAllowNull(false);
        field1.setIsPrimaryKey(false);
        field1.setUnique(false);
        field1.setIsIdentity(false);
        table.addField(field1);
    
        FieldDefinition field2 = new FieldDefinition();
        field2.setName("L_NAME");
        field2.setTypeName("VARCHAR");
        field2.setSize(20);
        field2.setShouldAllowNull(true);
        field2.setIsPrimaryKey(false);
        field2.setUnique(false);
        field2.setIsIdentity(false);
        table.addField(field2);
        
        FieldDefinition field3 = new FieldDefinition();
        field3.setName("BAR_ID");
        field3.setTypeName("NUMERIC");
        field3.setSize(15);
        field3.setShouldAllowNull(false);
        field3.setIsPrimaryKey(false);
        field3.setUnique(false);
        field3.setIsIdentity(false);
        field3.setForeignKeyFieldName("CASCADE_BAR.ID");
        table.addField(field3);
        
        FieldDefinition field4 = new FieldDefinition();
        field4.setName("QUALIFICATION_ID");
        field4.setTypeName("NUMERIC");
        field4.setSize(15);
        field4.setShouldAllowNull(true);
        field4.setIsPrimaryKey(false);
        field4.setUnique(false);
        field4.setIsIdentity(false);
        field4.setForeignKeyFieldName("CASCADE_QUALIFICATION.ID");
        table.addField(field4);

        
        return table;
    }
    
    protected TableDefinition buildLICENSETable() {
        TableDefinition table = new TableDefinition();
        table.setName("CASCADE_LICENSE");
        
        FieldDefinition field = new FieldDefinition();
        field.setName("ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(true);
        table.addField(field);

        FieldDefinition field1 = new FieldDefinition();
        field1.setName("LICENSE_CLASS");
        field1.setTypeName("VARCHAR");
        field1.setSize(20);
        field1.setShouldAllowNull(true);
        field1.setIsPrimaryKey(false);
        field1.setUnique(false);
        field1.setIsIdentity(false);
        table.addField(field1);
        
        return table;
    }
    
     protected TableDefinition buildQUALIFICATIONTable() {
        TableDefinition table = new TableDefinition();
        table.setName("CASCADE_QUALIFICATION");

        FieldDefinition field = new FieldDefinition();
        field.setName("ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false);
        field.setIsPrimaryKey(true);
        field.setUnique(false);
        field.setIsIdentity(true);
        table.addField(field);

        FieldDefinition field1 = new FieldDefinition();
        field1.setName("YEARS");
        field1.setTypeName("NUMERIC");
        field1.setSize(15);
        field1.setShouldAllowNull(true);
        field1.setIsPrimaryKey(false);
        field1.setUnique(false);
        field1.setIsIdentity(false);
        table.addField(field1);

        FieldDefinition field2 = new FieldDefinition();
        field2.setName("VERSION");
        field2.setTypeName("NUMERIC");
        field2.setSize(15);
        field2.setShouldAllowNull(false);
        field2.setIsPrimaryKey(false);
        field2.setUnique(false);
        field2.setIsIdentity(false);
        table.addField(field2);
        
        return table;
    }
}
