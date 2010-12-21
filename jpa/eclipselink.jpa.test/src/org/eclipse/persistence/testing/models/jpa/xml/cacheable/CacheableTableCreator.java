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
        addTableDefinition(buildCACHEABLE_PROTECTED_ENTITYTable());
        addTableDefinition(buildCACHEABLE_FORCE_PROTECTED_ENTITYTable());
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
        
        FieldDefinition protectedFK = new FieldDefinition();
        protectedFK.setName("PROTECTED_FK");
        protectedFK.setTypeName("NUMERIC");
        protectedFK.setSize(15);
        protectedFK.setShouldAllowNull(true);
        protectedFK.setIsPrimaryKey(false);
        protectedFK.setUnique(false);
        protectedFK.setIsIdentity(false);
        table.addField(protectedFK);
    
        return table;
    }
    
    public static TableDefinition buildCACHEABLE_PROTECTED_ENTITYTable() {
        TableDefinition table = new TableDefinition();
        table.setName("XML_CACHEABLE_PROTECTED");
    
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
    
    public static TableDefinition buildCACHEABLE_FORCE_PROTECTED_ENTITYTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_CACHEABLE_FORCE_PROTECTED");
    
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
    
        FieldDefinition falseFK = new FieldDefinition();
        falseFK.setName("FALSE_FK");
        falseFK.setTypeName("NUMERIC");
        falseFK.setSize(15);
        falseFK.setShouldAllowNull(true);
        falseFK.setIsPrimaryKey(false);
        falseFK.setUnique(false);
        falseFK.setIsIdentity(false);
        table.addField(falseFK);

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

