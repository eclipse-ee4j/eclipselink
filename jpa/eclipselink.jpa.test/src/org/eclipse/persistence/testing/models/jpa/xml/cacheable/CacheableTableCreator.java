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
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.xml.cacheable;

import org.eclipse.persistence.tools.schemaframework.*;

public class CacheableTableCreator extends TableCreator {
    public CacheableTableCreator() {
        setName("JPA Cacheable Project");

        addTableDefinition(buildCACHEABLE_FALSE_ENTITYTable());
        addTableDefinition(buildCACHEABLE_TRUE_ENTITYTable());
        addTableDefinition(buildSUB_CACHEABLE_FALSE_ENTITYTable());
        addTableDefinition(buildSUB_CACHEABLE_NONE_ENTITYTable());
    }
    
    public static TableDefinition buildCACHEABLE_FALSE_ENTITYTable() {
        TableDefinition table = new TableDefinition();
        table.setName("XML_CACHEABLE_FALSE");
    
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
    
    public static TableDefinition buildCACHEABLE_TRUE_ENTITYTable() {
        TableDefinition table = new TableDefinition();
        table.setName("XML_CACHEABLE_TRUE");
    
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
    
    public static TableDefinition buildSUB_CACHEABLE_FALSE_ENTITYTable() {
        TableDefinition table = new TableDefinition();
        table.setName("XML_SUB_CACHEABLE_FALSE");
    
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
        table.setName("XML_SUB_CACHEABLE_NONE");
    
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

