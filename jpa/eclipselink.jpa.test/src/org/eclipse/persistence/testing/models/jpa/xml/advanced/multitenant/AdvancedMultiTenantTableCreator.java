/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     03/24/2011-2.3 Guy Pelletier 
 *       - 337323: Multi-tenant with shared schema support (part 1)
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.xml.advanced.multitenant;

import org.eclipse.persistence.testing.framework.TogglingFastTableCreator;
import org.eclipse.persistence.tools.schemaframework.*;

public class AdvancedMultiTenantTableCreator extends TogglingFastTableCreator {
    public AdvancedMultiTenantTableCreator() {
        setName("JPA XML Advanced Multi-Tenant Project");

        addTableDefinition(buildMafiaFamilyTable());
        addTableDefinition(buildMafiaFamily_TagsTable());
        addTableDefinition(buildMafiaFamily_RevenueTable());
        addTableDefinition(buildMafiosoTable());
        addTableDefinition(buildBossTable());
        addTableDefinition(buildUnderbossTable());
        addTableDefinition(buildCapoTable());
        addTableDefinition(buildSoldierTable());
        addTableDefinition(buildContractTable());
        addTableDefinition(buildContract_SoldierTable());
    }
    
    public TableDefinition buildMafiaFamilyTable() {
        TableDefinition table = new TableDefinition();
        table.setName("XML_MAFIA_FAMILY");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setIsPrimaryKey(true);
        fieldID.setIsIdentity(true);
        fieldID.setUnique(false);
        fieldID.setShouldAllowNull(false);
        table.addField(fieldID);

        FieldDefinition fieldName = new FieldDefinition();
        fieldName.setName("NAME");
        fieldName.setTypeName("VARCHAR2");
        fieldName.setSize(60);
        fieldName.setSubSize(0);
        fieldName.setIsPrimaryKey(false);
        fieldName.setIsIdentity(false);
        fieldName.setUnique(false);
        fieldName.setShouldAllowNull(true);
        table.addField(fieldName);
        
        FieldDefinition fieldTenantId = new FieldDefinition();
        fieldTenantId.setName("TENANT_ID");
        fieldTenantId.setTypeName("VARCHAR2");
        fieldTenantId.setSize(10);
        fieldTenantId.setSubSize(0);
        fieldTenantId.setIsPrimaryKey(false);
        fieldTenantId.setIsIdentity(false);
        fieldTenantId.setUnique(false);
        fieldTenantId.setShouldAllowNull(false);
        table.addField(fieldTenantId);
    
        return table;
    }
    
    public TableDefinition buildMafiaFamily_TagsTable() {
        TableDefinition table = new TableDefinition();
        table.setName("XML_FAMILY_TAGS");

        FieldDefinition fieldFamilyId = new FieldDefinition();
        fieldFamilyId.setName("FAMILY_ID");
        fieldFamilyId.setTypeName("NUMERIC");
        fieldFamilyId.setSize(15);
        fieldFamilyId.setIsPrimaryKey(false);
        fieldFamilyId.setIsIdentity(false);
        fieldFamilyId.setUnique(false);
        fieldFamilyId.setShouldAllowNull(false);
        fieldFamilyId.setForeignKeyFieldName("XML_MAFIA_FAMILY.ID");
        table.addField(fieldFamilyId);

        FieldDefinition fieldTag = new FieldDefinition();
        fieldTag.setName("TAG");
        fieldTag.setTypeName("VARCHAR2");
        fieldTag.setSize(60);
        fieldTag.setSubSize(0);
        fieldTag.setIsPrimaryKey(false);
        fieldTag.setIsIdentity(false);
        fieldTag.setUnique(false);
        fieldTag.setShouldAllowNull(true);
        table.addField(fieldTag);

        return table;
    }
    
    public TableDefinition buildMafiaFamily_RevenueTable() {
        TableDefinition table = new TableDefinition();
        table.setName("XML_FAMILY_REVENUE");

        FieldDefinition fieldFamilyId = new FieldDefinition();
        fieldFamilyId.setName("ID");
        fieldFamilyId.setTypeName("NUMERIC");
        fieldFamilyId.setSize(15);
        fieldFamilyId.setIsPrimaryKey(false);
        fieldFamilyId.setIsIdentity(false);
        fieldFamilyId.setUnique(false);
        fieldFamilyId.setShouldAllowNull(false);
        fieldFamilyId.setForeignKeyFieldName("XML_MAFIA_FAMILY.ID");
        table.addField(fieldFamilyId);

        FieldDefinition fieldRevenue = new FieldDefinition();
        fieldRevenue.setName("REVENUE");
        fieldRevenue.setTypeName("NUMBER");
        fieldRevenue.setSize(15);
        fieldRevenue.setSubSize(0);
        fieldRevenue.setIsPrimaryKey(false);
        fieldRevenue.setIsIdentity(false);
        fieldRevenue.setUnique(false);
        fieldRevenue.setShouldAllowNull(true);
        table.addField(fieldRevenue);

        return table;
    }
    
    public TableDefinition buildMafiosoTable() {
        TableDefinition table = new TableDefinition();
        table.setName("XML_MAFIOSO");
        
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(true);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(true);
        table.addField(fieldID);
    
        FieldDefinition fieldFirstName = new FieldDefinition();
        fieldFirstName.setName("FIRSTNAME");
        fieldFirstName.setTypeName("VARCHAR");
        fieldFirstName.setSize(30);
        fieldFirstName.setShouldAllowNull(true);
        fieldFirstName.setIsPrimaryKey(false);
        fieldFirstName.setUnique(false);
        fieldFirstName.setIsIdentity(false);
        table.addField(fieldFirstName);
        
        FieldDefinition fieldLastName = new FieldDefinition();
        fieldLastName.setName("LASTNAME");
        fieldLastName.setTypeName("VARCHAR");
        fieldLastName.setSize(30);
        fieldLastName.setShouldAllowNull(true);
        fieldLastName.setIsPrimaryKey(false);
        fieldLastName.setUnique(false);
        fieldLastName.setIsIdentity(false);
        table.addField(fieldLastName);
        
        FieldDefinition fieldNickname = new FieldDefinition();
        fieldNickname.setName("NICKNAME");
        fieldNickname.setTypeName("VARCHAR");
        fieldNickname.setSize(30);
        fieldNickname.setShouldAllowNull(true);
        fieldNickname.setIsPrimaryKey(false);
        fieldNickname.setUnique(false);
        fieldNickname.setIsIdentity(false);
        table.addField(fieldNickname);
        
        FieldDefinition fieldGender = new FieldDefinition();
        fieldGender.setName("GENDER");
        fieldGender.setTypeName("VARCHAR");
        fieldGender.setSize(1);
        fieldGender.setShouldAllowNull(true);
        fieldGender.setIsPrimaryKey(false);
        fieldGender.setUnique(false);
        fieldGender.setIsIdentity(false);
        table.addField(fieldGender);
        
        FieldDefinition fieldFamily = new FieldDefinition();
        fieldFamily.setName("FAMILY_ID");
        fieldFamily.setTypeName("NUMERIC");
        fieldFamily.setSize(15);
        fieldFamily.setShouldAllowNull(true);
        fieldFamily.setIsPrimaryKey(false);
        fieldFamily.setUnique(false);
        fieldFamily.setIsIdentity(false);
        //fieldFamily.setForeignKeyFieldName("XML_MAFIA_FAMILY.ID");
        table.addField(fieldFamily);
        
        FieldDefinition fielDiscriminatorType = new FieldDefinition();
        fielDiscriminatorType.setName("DTYPE");
        fielDiscriminatorType.setTypeName("VARCHAR");
        fielDiscriminatorType.setSize(20);
        fielDiscriminatorType.setShouldAllowNull(false);
        fielDiscriminatorType.setIsPrimaryKey(false);
        fielDiscriminatorType.setUnique(false);
        fielDiscriminatorType.setIsIdentity(false);
        table.addField(fielDiscriminatorType);
    
        FieldDefinition fieldTenantId = new FieldDefinition();
        fieldTenantId.setName("TENANT_ID");
        fieldTenantId.setTypeName("VARCHAR2");
        fieldTenantId.setSize(10);
        fieldTenantId.setSubSize(0);
        fieldTenantId.setIsPrimaryKey(false);
        fieldTenantId.setIsIdentity(false);
        fieldTenantId.setUnique(false);
        fieldTenantId.setShouldAllowNull(false);
        table.addField(fieldTenantId);
        
        return table;
    }

    public TableDefinition buildBossTable() {
        TableDefinition table = new TableDefinition();
        table.setName("XML_BOSS");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setIsPrimaryKey(true);
        fieldID.setIsIdentity(true);
        fieldID.setUnique(false);
        fieldID.setShouldAllowNull(false);
        //fieldID.setForeignKeyFieldName("XML_MAFIOSO.ID");
        table.addField(fieldID);

        FieldDefinition fieldUnderboss = new FieldDefinition();
        fieldUnderboss.setName("UNDERBOSS_ID");
        fieldUnderboss.setTypeName("NUMERIC");
        fieldUnderboss.setSize(15);
        fieldUnderboss.setShouldAllowNull(true);
        fieldUnderboss.setIsPrimaryKey(false);
        fieldUnderboss.setUnique(false);
        fieldUnderboss.setIsIdentity(false);
        //fieldBoss.setForeignKeyFieldName("XML_BOSS.ID");
        table.addField(fieldUnderboss);
        
        return table;
    }

    public TableDefinition buildUnderbossTable() {
        TableDefinition table = new TableDefinition();
        table.setName("XML_UNDERBOSS");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setIsPrimaryKey(true);
        fieldID.setIsIdentity(true);
        fieldID.setUnique(false);
        fieldID.setShouldAllowNull(false);
        //fieldID.setForeignKeyFieldName("XML_MAFIOSO.ID");
        table.addField(fieldID);
        
        return table;
    }
    
    public TableDefinition buildCapoTable() {
        TableDefinition table = new TableDefinition();
        table.setName("XML_CAPO");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setIsPrimaryKey(true);
        fieldID.setIsIdentity(true);
        fieldID.setUnique(false);
        fieldID.setShouldAllowNull(false);
        //fieldID.setForeignKeyFieldName("XML_MAFIOSO.ID");
        table.addField(fieldID);
        
        FieldDefinition fieldUnderboss = new FieldDefinition();
        fieldUnderboss.setName("UNDERBOSS_ID");
        fieldUnderboss.setTypeName("NUMERIC");
        fieldUnderboss.setSize(15);
        fieldUnderboss.setShouldAllowNull(false);
        fieldUnderboss.setIsPrimaryKey(false);
        fieldUnderboss.setUnique(false);
        fieldUnderboss.setIsIdentity(false);
        //fieldUnderboss.setForeignKeyFieldName("XML_UNDERBOSS.ID");
        table.addField(fieldUnderboss);
        
        return table;
    }
    
    public TableDefinition buildSoldierTable() {
        TableDefinition table = new TableDefinition();
        table.setName("XML_SOLDIER");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setIsPrimaryKey(true);
        fieldID.setIsIdentity(true);
        fieldID.setUnique(false);
        fieldID.setShouldAllowNull(false);
        //fieldID.setForeignKeyFieldName("XML_MAFIOSO.ID");
        table.addField(fieldID);
        
        FieldDefinition fieldCapo = new FieldDefinition();
        fieldCapo.setName("CAPO_ID");
        fieldCapo.setTypeName("NUMERIC");
        fieldCapo.setSize(15);
        fieldCapo.setShouldAllowNull(false);
        fieldCapo.setIsPrimaryKey(false);
        fieldCapo.setUnique(false);
        fieldCapo.setIsIdentity(false);
        //fieldCapo.setForeignKeyFieldName("XML_CAPO.ID");
        table.addField(fieldCapo);
        
        return table;
    }
         
    public TableDefinition buildContractTable() {
        TableDefinition table = new TableDefinition();
        table.setName("XML_CONTRACT");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setIsPrimaryKey(true);
        fieldID.setIsIdentity(true);
        fieldID.setUnique(false);
        fieldID.setShouldAllowNull(false);
        table.addField(fieldID);

        FieldDefinition fieldDescription = new FieldDefinition();
        fieldDescription.setName("DESCRIP");
        fieldDescription.setTypeName("VARCHAR2");
        fieldDescription.setSize(60);
        fieldDescription.setSubSize(0);
        fieldDescription.setIsPrimaryKey(false);
        fieldDescription.setIsIdentity(false);
        fieldDescription.setUnique(false);
        fieldDescription.setShouldAllowNull(true);
        table.addField(fieldDescription);
        
        FieldDefinition fieldVersion = new FieldDefinition();
        fieldVersion.setName("VERSION");
        fieldVersion.setTypeName("NUMERIC");
        fieldVersion.setSize(15);
        fieldVersion.setShouldAllowNull(true);
        fieldVersion.setIsPrimaryKey(false);
        fieldVersion.setUnique(false);
        fieldVersion.setIsIdentity(false);
        table.addField(fieldVersion);
        
        FieldDefinition fieldTenantId = new FieldDefinition();
        fieldTenantId.setName("TENANT_ID");
        fieldTenantId.setTypeName("VARCHAR2");
        fieldTenantId.setSize(10);
        fieldTenantId.setSubSize(0);
        fieldTenantId.setIsPrimaryKey(false);
        fieldTenantId.setIsIdentity(false);
        fieldTenantId.setUnique(false);
        fieldTenantId.setShouldAllowNull(false);
        table.addField(fieldTenantId);
        
        return table;
    }
    
    public TableDefinition buildContract_SoldierTable() {
        TableDefinition table = new TableDefinition();
        table.setName("XML_CONTRACT_SOLDIER");

        FieldDefinition fieldContractId = new FieldDefinition();
        fieldContractId.setName("CONTRACT_ID");
        fieldContractId.setTypeName("NUMERIC");
        fieldContractId.setSize(15);
        fieldContractId.setShouldAllowNull(false);
        fieldContractId.setIsPrimaryKey(true);
        fieldContractId.setUnique(false);
        fieldContractId.setIsIdentity(false);
        fieldContractId.setForeignKeyFieldName("XML_CONTRACT.ID");
        table.addField(fieldContractId);
        
        FieldDefinition fieldSoldierId = new FieldDefinition();
        fieldSoldierId.setName("SOLDIER_ID");
        fieldSoldierId.setTypeName("NUMERIC");
        fieldSoldierId.setSize(15);
        fieldSoldierId.setShouldAllowNull(false);
        fieldSoldierId.setIsPrimaryKey(true);
        fieldSoldierId.setUnique(false);
        fieldSoldierId.setIsIdentity(false);
        fieldSoldierId.setForeignKeyFieldName("XML_SOLDIER.ID");
        table.addField(fieldSoldierId);
        
        return table;   
    }
}
