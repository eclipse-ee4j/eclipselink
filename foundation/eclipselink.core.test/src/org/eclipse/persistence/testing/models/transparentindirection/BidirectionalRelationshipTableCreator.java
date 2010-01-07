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
package org.eclipse.persistence.testing.models.transparentindirection;

import org.eclipse.persistence.tools.schemaframework.*;

/**
 * Creates the necessary tables for the BidirectionalRelationshipProject.
 *
 * @author Guy Pelletier
 * @version 1.0
 * @date March 21, 2005
 */
public class BidirectionalRelationshipTableCreator extends TableCreator {
    public BidirectionalRelationshipTableCreator() {
        setName("BidirectionalRelationshipTableCreator");

        addTableDefinition(buildTEAMTable());
        addTableDefinition(buildPLAYERTable());
    }

    protected TableDefinition buildTEAMTable() {
        TableDefinition table = new TableDefinition();
        table.setName("TEAM");

        FieldDefinition field = new FieldDefinition();
        field.setName("ID");
        field.setTypeName("NUMBER");
        field.setSize(15);
        field.setSubSize(0);
        field.setIsPrimaryKey(true);
        field.setIsIdentity(false);
        field.setUnique(false);
        field.setShouldAllowNull(false);
        table.addField(field);

        return table;
    }

    protected TableDefinition buildPLAYERTable() {
        TableDefinition table = new TableDefinition();
        table.setName("PLAYER");

        FieldDefinition field1 = new FieldDefinition();
        field1.setName("ID");
        field1.setTypeName("NUMBER");
        field1.setSize(15);
        field1.setSubSize(0);
        field1.setIsPrimaryKey(true);
        field1.setIsIdentity(false);
        field1.setUnique(false);
        field1.setShouldAllowNull(false);
        table.addField(field1);

        FieldDefinition field2 = new FieldDefinition();
        field2.setName("TEAM_ID");
        field2.setTypeName("NUMBER");
        field2.setSize(15);
        field2.setSubSize(0);
        field2.setIsPrimaryKey(false);
        field2.setIsIdentity(false);
        field2.setUnique(false);
        field2.setShouldAllowNull(true);
        table.addField(field2);

        return table;
    }
}
