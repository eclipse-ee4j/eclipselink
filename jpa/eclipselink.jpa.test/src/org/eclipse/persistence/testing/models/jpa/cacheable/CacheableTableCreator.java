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
        addTableDefinition(buildCACHEABLE_FALSE_DETAIL_BPTable());
        addTableDefinition(buildCACHEABLE_FALSE_TO_DETAILTable());
        addTableDefinition(buildCACHEABLE_TRUE_ENTITYTable());
        addTableDefinition(buildSUB_CACHEABLE_FALSE_ENTITYTable());
        addTableDefinition(buildSUB_CACHEABLE_NONE_ENTITYTable());
        addTableDefinition(buildCACHEABLE_PROTECTED_ENTITYTable());
        addTableDefinition(buildCACHEABLE_FORCE_PROTECTED_ENTITYTable());
        addTableDefinition(buildCACHEABLE_FORCE_PROTECTED_ENTITY_WITH_COMPOSITTable());
        addTableDefinition(buildCACHEABLE_RELATIONSHIPS_ENTITYTable());
        addTableDefinition(buildCACHEABLEREL_PROTECTEDTable());
        addTableDefinition(buildCACHEABLEREL_FALSEDETAILTable());
        addTableDefinition(buildCACHEABLEREL_PROTECTEMBEDDABLETable());
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
        
        FieldDefinition protectedFK = new FieldDefinition();
        protectedFK.setName("PROTECTED_FK");
        protectedFK.setTypeName("NUMERIC");
        protectedFK.setSize(15);
        protectedFK.setShouldAllowNull(true);
        protectedFK.setIsPrimaryKey(false);
        protectedFK.setUnique(false);
        protectedFK.setIsIdentity(false);
        table.addField(protectedFK);
        FieldDefinition cacheableFSFK = new FieldDefinition();
        cacheableFSFK.setName("SHARED_ISOLATED_REL_ID");
        cacheableFSFK.setTypeName("NUMERIC");
        cacheableFSFK.setSize(15);
        cacheableFSFK.setShouldAllowNull(true);
        cacheableFSFK.setIsPrimaryKey(false);
        cacheableFSFK.setUnique(false);
        cacheableFSFK.setIsIdentity(false);
        table.addField(cacheableFSFK);
    
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
    
    public static TableDefinition buildCACHEABLE_FALSE_DETAIL_BPTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_CACHEABLE_FALSE_DETAIL_BP");
    
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
        
        FieldDefinition fieldEntity = new FieldDefinition();
        fieldEntity.setName("ENTITY_ID");
        fieldEntity.setTypeName("NUMERIC");
        fieldEntity.setSize(15);
        fieldEntity.setShouldAllowNull(true);
        fieldEntity.setIsPrimaryKey(false);
        fieldEntity.setUnique(false);
        fieldEntity.setIsIdentity(false);
        table.addField(fieldEntity);

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
    
    public static TableDefinition buildCACHEABLE_PROTECTED_ENTITYTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_CACHEABLE_PROTECTED");
    
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

        FieldDefinition forcedFK = new FieldDefinition();
        forcedFK.setName("FORCEDPROTECTED_ID");
        forcedFK.setTypeName("NUMERIC");
        forcedFK.setSize(15);
        forcedFK.setShouldAllowNull(true);
        forcedFK.setIsPrimaryKey(false);
        forcedFK.setUnique(false);
        forcedFK.setIsIdentity(false);
        table.addField(forcedFK);

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
        
        FieldDefinition embNAME = new FieldDefinition();
        embNAME.setName("SE_NAME");
        embNAME.setTypeName("VARCHAR");
        embNAME.setSize(75);
        embNAME.setShouldAllowNull(true);
        embNAME.setIsPrimaryKey(false);
        embNAME.setUnique(false);
        embNAME.setIsIdentity(false);
        table.addField(embNAME);
        
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
    
    public static TableDefinition buildCACHEABLE_FORCE_PROTECTED_ENTITY_WITH_COMPOSITTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_CACHEABLE_F_P_W_C");
    
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
        
        FieldDefinition fieldSE_NAME = new FieldDefinition();
        fieldSE_NAME.setName("SE_NAME");
        fieldSE_NAME.setTypeName("VARCHAR");
        fieldSE_NAME.setSize(75);
        fieldSE_NAME.setShouldAllowNull(true);
        fieldSE_NAME.setIsPrimaryKey(false);
        fieldSE_NAME.setUnique(false);
        fieldSE_NAME.setIsIdentity(false);
        table.addField(fieldSE_NAME);
        
        FieldDefinition fieldEMBNAME = new FieldDefinition();
        fieldEMBNAME.setName("EMB_NAME");
        fieldEMBNAME.setTypeName("VARCHAR");
        fieldEMBNAME.setSize(75);
        fieldEMBNAME.setShouldAllowNull(true);
        fieldEMBNAME.setIsPrimaryKey(false);
        fieldEMBNAME.setUnique(false);
        fieldEMBNAME.setIsIdentity(false);
        table.addField(fieldEMBNAME);
        

        FieldDefinition falseFK = new FieldDefinition();
        falseFK.setName("PROTECTED_FK");
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
	public static TableDefinition buildCACHEABLE_RELATIONSHIPS_ENTITYTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_CACHEREL");
    
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(true);
        fieldID.setUnique(true);
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

        FieldDefinition field = new FieldDefinition();
        field.setName("FORCE_PROTECTED_FK");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false );
        field.setIsPrimaryKey(true );
        field.setUnique(false );
        field.setIsIdentity(false );
        field.setForeignKeyFieldName("JPA_CACHEABLE_FORCE_PROTECTED.ID");
        table.addField(field);
    
        return table;
    }

    public TableDefinition buildCACHEABLEREL_PROTECTEDTable() {
        TableDefinition table = new TableDefinition();

        table.setName("SHARED_PROTECTED_REL");

        // SECTION: FIELD
        FieldDefinition field = new FieldDefinition();
        field.setName("CACHBLE_REL_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false );
        field.setIsPrimaryKey(true );
        field.setUnique(false);
        field.setIsIdentity(false );
        field.setForeignKeyFieldName("JPA_CACHEREL.ID");
        table.addField(field);
    
        // SECTION: FIELD
        FieldDefinition field1 = new FieldDefinition();
        field1.setName("CACHBLE_PRT_ID");
        field1.setTypeName("NUMERIC");
        field1.setSize(15);
        field1.setShouldAllowNull(false );
        field1.setIsPrimaryKey(true );
        field1.setUnique(false );
        field1.setIsIdentity(false );
        field1.setForeignKeyFieldName("JPA_CACHEABLE_PROTECTED.ID");
        table.addField(field1);

        return table;
    }

    public TableDefinition buildCACHEABLEREL_FALSEDETAILTable() {
        TableDefinition table = new TableDefinition();

        table.setName("SHARED_FALSEDETAIL_REL");

        // SECTION: FIELD
        FieldDefinition field = new FieldDefinition();
        field.setName("CACHBLE_REL_ID");
        field.setTypeName("NUMERIC");
        field.setSize(15);
        field.setShouldAllowNull(false );
        field.setIsPrimaryKey(true );
        field.setUnique(false);
        field.setIsIdentity(false );
        field.setForeignKeyFieldName("JPA_CACHEREL.ID");
        table.addField(field);
    
        // SECTION: FIELD
        FieldDefinition field1 = new FieldDefinition();
        field1.setName("CACHBLE_FLDETAIL_ID");
        field1.setTypeName("NUMERIC");
        field1.setSize(15);
        field1.setShouldAllowNull(false );
        field1.setIsPrimaryKey(true );
        field1.setUnique(false );
        field1.setIsIdentity(false );
        field1.setForeignKeyFieldName("JPA_CACHEABLE_FALSE_DETAIL.ID");
        table.addField(field1);

        return table;
    }

    public static TableDefinition buildCACHEABLEREL_PROTECTEMBEDDABLETable() {
        TableDefinition table = new TableDefinition();
        table.setName("CACHREL_PROTECTEMB");
    
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(false);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(false);
        fieldID.setForeignKeyFieldName("JPA_CACHEREL.ID");
        table.addField(fieldID);

        FieldDefinition fieldNAME = new FieldDefinition();
        fieldNAME.setName("EMB_NAME");
        fieldNAME.setTypeName("VARCHAR");
        fieldNAME.setSize(75);
        fieldNAME.setShouldAllowNull(true);
        fieldNAME.setIsPrimaryKey(false);
        fieldNAME.setUnique(false);
        fieldNAME.setIsIdentity(false);
        table.addField(fieldNAME);

        FieldDefinition falseFK = new FieldDefinition();
        falseFK.setName("PROTECTED_FK");
        falseFK.setTypeName("NUMERIC");
        falseFK.setSize(15);
        falseFK.setShouldAllowNull(true);
        falseFK.setIsPrimaryKey(false);
        falseFK.setUnique(false);
        falseFK.setIsIdentity(false);
        table.addField(falseFK);
    
        return table;
    }
}

