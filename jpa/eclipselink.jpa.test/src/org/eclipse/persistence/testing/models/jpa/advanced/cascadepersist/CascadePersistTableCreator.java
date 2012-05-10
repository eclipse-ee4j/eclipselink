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
 *     Oracle - initial API and implementation.
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.advanced.cascadepersist;

import org.eclipse.persistence.tools.schemaframework.*;

public class CascadePersistTableCreator extends org.eclipse.persistence.tools.schemaframework.TableCreator {

    public CascadePersistTableCreator() {
        setName("CascadePersist");
        
        addTableDefinition(buildEntityX_CPTable());
        addTableDefinition(buildEntityZ_CPTable());
        addTableDefinition(buildEntityY_CPTable());
    }
    
    public TableDefinition buildEntityX_CPTable() {
        TableDefinition table = new TableDefinition();
        table.setName("ENTITYX_CP");
        
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMBER");
        fieldID.setSize(10);
        fieldID.setIsPrimaryKey(true);
        fieldID.setIsIdentity(false);
        fieldID.setUnique(false);
        fieldID.setShouldAllowNull(false);
        table.addField(fieldID);
        
        FieldDefinition fieldXNAME = new FieldDefinition();
        fieldXNAME.setName("XNAME");
        fieldXNAME.setTypeName("VARCHAR2");
        fieldXNAME.setSize(80);
        fieldXNAME.setIsPrimaryKey(false);
        fieldXNAME.setIsIdentity(false);
        fieldXNAME.setUnique(false);
        fieldXNAME.setShouldAllowNull(true);
        table.addField(fieldXNAME);
        
        return table;
    }
    
    public TableDefinition buildEntityY_CPTable() {
        TableDefinition table = new TableDefinition();
        table.setName("ENTITYY_CP");
        
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMBER");
        fieldID.setSize(10);
        fieldID.setIsPrimaryKey(true);
        fieldID.setIsIdentity(false);
        fieldID.setUnique(false);
        fieldID.setShouldAllowNull(false);
        table.addField(fieldID);
        
        FieldDefinition fieldYNAME = new FieldDefinition();
        fieldYNAME.setName("YNAME");
        fieldYNAME.setTypeName("VARCHAR2");
        fieldYNAME.setSize(80);
        fieldYNAME.setIsPrimaryKey(false);
        fieldYNAME.setIsIdentity(false);
        fieldYNAME.setUnique(false);
        fieldYNAME.setShouldAllowNull(true);
        table.addField(fieldYNAME);
        
        FieldDefinition fieldYZEntityRelationID = new FieldDefinition();
        fieldYZEntityRelationID.setName("YZENTITYRELATION_ID");
        fieldYZEntityRelationID.setTypeName("NUMBER");
        fieldYZEntityRelationID.setSize(10);
        fieldYZEntityRelationID.setIsPrimaryKey(false);
        fieldYZEntityRelationID.setIsIdentity(false);
        fieldYZEntityRelationID.setUnique(false);
        fieldYZEntityRelationID.setShouldAllowNull(false);
        table.addField(fieldYZEntityRelationID);
        
        FieldDefinition fieldEntityX_ID = new FieldDefinition();
        fieldEntityX_ID.setName("ENTITYX_ID");
        fieldEntityX_ID.setTypeName("NUMBER");
        fieldEntityX_ID.setSize(10);
        fieldEntityX_ID.setIsPrimaryKey(false);
        fieldEntityX_ID.setIsIdentity(false);
        fieldEntityX_ID.setUnique(false);
        fieldEntityX_ID.setShouldAllowNull(false);
        table.addField(fieldEntityX_ID);

        ForeignKeyConstraint FK_ENTITYY_YZENTITYRELATION_ID = new ForeignKeyConstraint();
        FK_ENTITYY_YZENTITYRELATION_ID.setName("FK_YZENTITYRELATION_ID");
        FK_ENTITYY_YZENTITYRELATION_ID.setTargetTable("ENTITYZ_CP");
        FK_ENTITYY_YZENTITYRELATION_ID.addSourceField("YZENTITYRELATION_ID");
        FK_ENTITYY_YZENTITYRELATION_ID.addTargetField("ID");
        table.addForeignKeyConstraint(FK_ENTITYY_YZENTITYRELATION_ID);
        
        ForeignKeyConstraint FK_ENTITYY_ENTITYX_ID = new ForeignKeyConstraint();
        FK_ENTITYY_ENTITYX_ID.setName("FK_ENTITYY_ENTITYX_ID");
        FK_ENTITYY_ENTITYX_ID.setTargetTable("ENTITYX_CP");
        FK_ENTITYY_ENTITYX_ID.addSourceField("ENTITYX_ID");
        FK_ENTITYY_ENTITYX_ID.addTargetField("ID");
        table.addForeignKeyConstraint(FK_ENTITYY_ENTITYX_ID);
        
        return table;
    }
    
    public TableDefinition buildEntityZ_CPTable() {
        TableDefinition table = new TableDefinition();
        table.setName("ENTITYZ_CP");
        
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMBER");
        fieldID.setSize(10);
        fieldID.setIsPrimaryKey(true);
        fieldID.setIsIdentity(false);
        fieldID.setUnique(false);
        fieldID.setShouldAllowNull(false);
        table.addField(fieldID);
        
        FieldDefinition fieldZNAME = new FieldDefinition();
        fieldZNAME.setName("ZNAME");
        fieldZNAME.setTypeName("VARCHAR2");
        fieldZNAME.setSize(80);
        fieldZNAME.setIsPrimaryKey(false);
        fieldZNAME.setIsIdentity(false);
        fieldZNAME.setUnique(false);
        fieldZNAME.setShouldAllowNull(true);
        table.addField(fieldZNAME);
    
        return table;
    }
}
