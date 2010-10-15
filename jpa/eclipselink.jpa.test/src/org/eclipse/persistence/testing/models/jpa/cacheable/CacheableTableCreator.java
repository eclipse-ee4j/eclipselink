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
 *     06/16/2009-2.0 Guy Pelletier 
 *       - 277039: JPA 2.0 Cache Usage Settings
 *     07/16/2009-2.0 Guy Pelletier 
 *       - 277039: JPA 2.0 Cache Usage Settings
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.cacheable;

import org.eclipse.persistence.tools.schemaframework.*;

public class CacheableTableCreator extends TableCreator {
    public CacheableTableCreator() {
        setName("JPA Cacheable Project");

        addTableDefinition(buildCACHEABLE_FALSE_ENTITYTable());
        addTableDefinition(buildCACHEABLE_FALSE_DETAILTable());
        addTableDefinition(buildCACHEABLE_FALSE_TO_DETAILTable());
        addTableDefinition(buildCACHEABLE_TRUE_ENTITYTable());
        addTableDefinition(buildSUB_CACHEABLE_FALSE_ENTITYTable());
        addTableDefinition(buildSUB_CACHEABLE_NONE_ENTITYTable());
    }
    
    public static TableDefinition buildCACHEABLE_FALSE_ENTITYTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_CACHEABLE_FALSE");
    
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(true);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(true);
        table.addField(fieldID);
    
        return table;
    }
    
    public static TableDefinition buildCACHEABLE_FALSE_DETAILTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_CACHEABLE_FALSE_DETAIL");
    
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(true);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(true);
        table.addField(fieldID);
    
        FieldDefinition fieldDescription = new FieldDefinition();
        fieldDescription.setName("DESCRIPTION");
        fieldDescription.setTypeName("VARCHAR2");
        fieldDescription.setSize(15);
        fieldDescription.setShouldAllowNull(true);
        fieldDescription.setIsPrimaryKey(false);
        fieldDescription.setUnique(false);
        fieldDescription.setIsIdentity(false);
        table.addField(fieldDescription);

        return table;
    }
    
    public static TableDefinition buildCACHEABLE_FALSE_TO_DETAILTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_CACHEABLE_FALSE_TO_DETAIL");
    
        FieldDefinition fieldEntityID = new FieldDefinition();
        fieldEntityID.setName("ENTITY_ID");
        fieldEntityID.setTypeName("NUMERIC");
        fieldEntityID.setSize(15);
        fieldEntityID.setShouldAllowNull(false);
        fieldEntityID.setIsPrimaryKey(true);
        fieldEntityID.setUnique(false);
        fieldEntityID.setIsIdentity(false);
        table.addField(fieldEntityID);    

        FieldDefinition fieldDetailID = new FieldDefinition();
        fieldDetailID.setName("DETAIL_ID");
        fieldDetailID.setTypeName("NUMERIC");
        fieldDetailID.setSize(15);
        fieldDetailID.setShouldAllowNull(false);
        fieldDetailID.setIsPrimaryKey(true);
        fieldDetailID.setUnique(false);
        fieldDetailID.setIsIdentity(false);
        table.addField(fieldDetailID);    

        FieldDefinition fieldIndex = new FieldDefinition();
        fieldIndex.setName("IND");
        fieldIndex.setTypeName("NUMERIC");
        fieldIndex.setSize(15);
        fieldIndex.setShouldAllowNull(false);
        fieldIndex.setIsPrimaryKey(true);
        fieldIndex.setUnique(false);
        fieldIndex.setIsIdentity(false);
        table.addField(fieldIndex);    

        return table;
    }
    
    public static TableDefinition buildCACHEABLE_TRUE_ENTITYTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_CACHEABLE_TRUE");
    
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(true);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(true);
        table.addField(fieldID);
        
        FieldDefinition fieldNAME = new FieldDefinition();
        fieldNAME.setName("NAME");
        fieldNAME.setTypeName("VARCHAR");
        fieldNAME.setSize(75);
        fieldNAME.setShouldAllowNull(true);
        fieldNAME.setIsPrimaryKey(false);
        fieldNAME.setUnique(false);
        fieldNAME.setIsIdentity(false);
        table.addField(fieldNAME);
        
        FieldDefinition fieldDTYPE = new FieldDefinition();
        fieldDTYPE.setName("DTYPE");
        fieldDTYPE.setTypeName("VARCHAR2");
        fieldDTYPE.setSize(15);
        fieldDTYPE.setSubSize(0);
        fieldDTYPE.setIsPrimaryKey(false);
        fieldDTYPE.setIsIdentity(false);
        fieldDTYPE.setUnique(false);
        fieldDTYPE.setShouldAllowNull(true);
        table.addField(fieldDTYPE);
    
        return table;
    }
    
    public static TableDefinition buildSUB_CACHEABLE_FALSE_ENTITYTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_SUB_CACHEABLE_FALSE");
    
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(true);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(true);
        table.addField(fieldID);
    
        return table;
    }
    
    public static TableDefinition buildSUB_CACHEABLE_NONE_ENTITYTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_SUB_CACHEABLE_NONE");
    
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(true);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(true);
        table.addField(fieldID);
    
        return table;
    }
}

