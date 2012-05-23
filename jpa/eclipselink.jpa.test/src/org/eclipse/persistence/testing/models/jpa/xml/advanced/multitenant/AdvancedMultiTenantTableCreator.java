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
 *     14/05/2012-2.4 Guy Pelletier   
 *       - 376603: Provide for table per tenant support for multitenant applications
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

	    // Table per tenant tables.
        addTableDefinition(buildRidingTable());
        addTableDefinition(buildPartyTable());
        addTableDefinition(buildCandidateTenantATable());
        addTableDefinition(buildCandidateTenantBTable());
        addTableDefinition(buildCandidateTenantCTable());
        addTableDefinition(buildSalaryTenantATable());
        addTableDefinition(buildSalaryTenantBTable());
        addTableDefinition(buildSalaryTenantCTable());
        addTableDefinition(buildCandidateSupporterTenantATable());
        addTableDefinition(buildCandidateSupporterTenantBTable());
        addTableDefinition(buildCandidateSupporterTenantCTable());
        addTableDefinition(buildCandidateHonorsTenantATable());
        addTableDefinition(buildCandidateHonorsTenantBTable());
        addTableDefinition(buildCandidateHonorsTenantCTable());
        addTableDefinition(buildSupporterTenantATable());
        addTableDefinition(buildSupporterTenantBTable());
        addTableDefinition(buildSupporterTenantCTable());
        addTableDefinition(buildMasonTenantATable());
        addTableDefinition(buildMasonTenantBTable());
        addTableDefinition(buildMasonTenantCTable());
        addTableDefinition(buildMasonAwardsTenantATable());
        addTableDefinition(buildMasonAwardsTenantBTable());
        addTableDefinition(buildMasonAwardsTenantCTable());
        addTableDefinition(buildTrowelTenantATable());
        addTableDefinition(buildTrowelTenantBTable());
        addTableDefinition(buildTrowelTenantCTable());
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

    public TableDefinition buildMasonTenantATable() {
        TableDefinition table = new TableDefinition();
        table.setName("XML_MASON_A");
        
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(true);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(true);
        table.addField(fieldID);
    
        FieldDefinition fieldName = new FieldDefinition();
        fieldName.setName("NAME");
        fieldName.setTypeName("VARCHAR");
        fieldName.setSize(30);
        fieldName.setShouldAllowNull(true);
        fieldName.setIsPrimaryKey(false);
        fieldName.setUnique(false);
        fieldName.setIsIdentity(false);
        table.addField(fieldName);
        
        FieldDefinition fieldTrowelId = new FieldDefinition();
        fieldTrowelId.setName("TROWEL_ID");
        fieldTrowelId.setTypeName("NUMERIC");
        fieldTrowelId.setSize(15);
        fieldTrowelId.setIsPrimaryKey(false);
        fieldTrowelId.setIsIdentity(false);
        fieldTrowelId.setUnique(false);
        fieldTrowelId.setShouldAllowNull(true);
        fieldTrowelId.setForeignKeyFieldName("XML_TROWEL_A.ID");
        table.addField(fieldTrowelId);
        
        return table;
    }
    
    public TableDefinition buildMasonTenantBTable() {
        TableDefinition table = new TableDefinition();
        table.setName("XML_MASON_B");
        
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(true);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(true);
        table.addField(fieldID);
    
        FieldDefinition fieldName = new FieldDefinition();
        fieldName.setName("NAME");
        fieldName.setTypeName("VARCHAR");
        fieldName.setSize(30);
        fieldName.setShouldAllowNull(true);
        fieldName.setIsPrimaryKey(false);
        fieldName.setUnique(false);
        fieldName.setIsIdentity(false);
        table.addField(fieldName);
        
        FieldDefinition fieldTrowelId = new FieldDefinition();
        fieldTrowelId.setName("TROWEL_ID");
        fieldTrowelId.setTypeName("NUMERIC");
        fieldTrowelId.setSize(15);
        fieldTrowelId.setIsPrimaryKey(false);
        fieldTrowelId.setIsIdentity(false);
        fieldTrowelId.setUnique(false);
        fieldTrowelId.setShouldAllowNull(true);
        fieldTrowelId.setForeignKeyFieldName("XML_TROWEL_B.ID");
        table.addField(fieldTrowelId);
        
        return table;
    }
    
    public TableDefinition buildMasonTenantCTable() {
        TableDefinition table = new TableDefinition();
        table.setName("XML_MASON_C");
        
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(true);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(true);
        table.addField(fieldID);
    
        FieldDefinition fieldName = new FieldDefinition();
        fieldName.setName("NAME");
        fieldName.setTypeName("VARCHAR");
        fieldName.setSize(30);
        fieldName.setShouldAllowNull(true);
        fieldName.setIsPrimaryKey(false);
        fieldName.setUnique(false);
        fieldName.setIsIdentity(false);
        table.addField(fieldName);
        
        FieldDefinition fieldTrowelId = new FieldDefinition();
        fieldTrowelId.setName("TROWEL_ID");
        fieldTrowelId.setTypeName("NUMERIC");
        fieldTrowelId.setSize(15);
        fieldTrowelId.setIsPrimaryKey(false);
        fieldTrowelId.setIsIdentity(false);
        fieldTrowelId.setUnique(false);
        fieldTrowelId.setShouldAllowNull(true);
        fieldTrowelId.setForeignKeyFieldName("XML_TROWEL_C.ID");
        table.addField(fieldTrowelId);
        
        return table;
    }
    
    public TableDefinition buildMasonAwardsTenantATable() {
        TableDefinition table = new TableDefinition();
        table.setName("XML_MASON_AWARDS_A");
    
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("MASON_ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(false);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(false);
        fieldID.setForeignKeyFieldName("XML_MASON_A.ID");
        table.addField(fieldID);
    
        FieldDefinition fieldAWARDDATE = new FieldDefinition();
        fieldAWARDDATE.setName("AWARD_DATE");
        fieldAWARDDATE.setTypeName("DATE");
        fieldAWARDDATE.setSize(23);
        fieldAWARDDATE.setShouldAllowNull(true);
        fieldAWARDDATE.setIsPrimaryKey(false);
        fieldAWARDDATE.setUnique(false);
        fieldAWARDDATE.setIsIdentity(false);
        table.addField(fieldAWARDDATE);

        FieldDefinition fieldAWARD = new FieldDefinition();
        fieldAWARD.setName("AWARD");
        fieldAWARD.setTypeName("VARCHAR");
        fieldAWARD.setSize(50);
        fieldAWARD.setShouldAllowNull(false);
        fieldAWARD.setIsPrimaryKey(false);
        fieldAWARD.setUnique(false);
        fieldAWARD.setIsIdentity(false);
        table.addField(fieldAWARD);
    
        return table;
    }
    
    public TableDefinition buildMasonAwardsTenantBTable() {
        TableDefinition table = new TableDefinition();
        table.setName("XML_MASON_AWARDS_B");
    
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("MASON_ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(false);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(false);
        fieldID.setForeignKeyFieldName("XML_MASON_B.ID");
        table.addField(fieldID);
    
        FieldDefinition fieldAWARDDATE = new FieldDefinition();
        fieldAWARDDATE.setName("AWARD_DATE");
        fieldAWARDDATE.setTypeName("DATE");
        fieldAWARDDATE.setSize(23);
        fieldAWARDDATE.setShouldAllowNull(true);
        fieldAWARDDATE.setIsPrimaryKey(false);
        fieldAWARDDATE.setUnique(false);
        fieldAWARDDATE.setIsIdentity(false);
        table.addField(fieldAWARDDATE);

        FieldDefinition fieldAWARD = new FieldDefinition();
        fieldAWARD.setName("AWARD");
        fieldAWARD.setTypeName("VARCHAR");
        fieldAWARD.setSize(50);
        fieldAWARD.setShouldAllowNull(false);
        fieldAWARD.setIsPrimaryKey(false);
        fieldAWARD.setUnique(false);
        fieldAWARD.setIsIdentity(false);
        table.addField(fieldAWARD);
    
        return table;
    }
    
    public TableDefinition buildMasonAwardsTenantCTable() {
        TableDefinition table = new TableDefinition();
        table.setName("XML_MASON_AWARDS_C");
    
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("MASON_ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(false);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(false);
        fieldID.setForeignKeyFieldName("XML_MASON_C.ID");
        table.addField(fieldID);
    
        FieldDefinition fieldAWARDDATE = new FieldDefinition();
        fieldAWARDDATE.setName("AWARD_DATE");
        fieldAWARDDATE.setTypeName("DATE");
        fieldAWARDDATE.setSize(23);
        fieldAWARDDATE.setShouldAllowNull(true);
        fieldAWARDDATE.setIsPrimaryKey(false);
        fieldAWARDDATE.setUnique(false);
        fieldAWARDDATE.setIsIdentity(false);
        table.addField(fieldAWARDDATE);

        FieldDefinition fieldAWARD = new FieldDefinition();
        fieldAWARD.setName("AWARD");
        fieldAWARD.setTypeName("VARCHAR");
        fieldAWARD.setSize(50);
        fieldAWARD.setShouldAllowNull(false);
        fieldAWARD.setIsPrimaryKey(false);
        fieldAWARD.setUnique(false);
        fieldAWARD.setIsIdentity(false);
        table.addField(fieldAWARD);
    
        return table;
    }
    
    public TableDefinition buildTrowelTenantATable() {
        TableDefinition table = new TableDefinition();
        table.setName("XML_TROWEL_A");
        
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(true);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(true);
        table.addField(fieldID);
    
        FieldDefinition fieldName = new FieldDefinition();
        fieldName.setName("TROWEL_TYPE");
        fieldName.setTypeName("VARCHAR");
        fieldName.setSize(30);
        fieldName.setShouldAllowNull(true);
        fieldName.setIsPrimaryKey(false);
        fieldName.setUnique(false);
        fieldName.setIsIdentity(false);
        table.addField(fieldName);
        
        return table;
    }
    
    public TableDefinition buildTrowelTenantBTable() {
        TableDefinition table = new TableDefinition();
        table.setName("XML_TROWEL_B");
        
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(true);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(true);
        table.addField(fieldID);
    
        FieldDefinition fieldName = new FieldDefinition();
        fieldName.setName("TROWEL_TYPE");
        fieldName.setTypeName("VARCHAR");
        fieldName.setSize(30);
        fieldName.setShouldAllowNull(true);
        fieldName.setIsPrimaryKey(false);
        fieldName.setUnique(false);
        fieldName.setIsIdentity(false);
        table.addField(fieldName);
        
        return table;
    }
    
    public TableDefinition buildTrowelTenantCTable() {
        TableDefinition table = new TableDefinition();
        table.setName("XML_TROWEL_C");
        
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(true);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(true);
        table.addField(fieldID);
    
        FieldDefinition fieldName = new FieldDefinition();
        fieldName.setName("TROWEL_TYPE");
        fieldName.setTypeName("VARCHAR");
        fieldName.setSize(30);
        fieldName.setShouldAllowNull(true);
        fieldName.setIsPrimaryKey(false);
        fieldName.setUnique(false);
        fieldName.setIsIdentity(false);
        table.addField(fieldName);
        
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
    
    public TableDefinition buildCandidateHonorsTenantATable() {
        TableDefinition table = new TableDefinition();
        table.setName("XML_CANDIDATE_HONORS_A");
    
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("XMLCandidate_ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(false);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(false);
        table.addField(fieldID);
    
        FieldDefinition field1 = new FieldDefinition();
        field1.setName("HONOR");
        field1.setTypeName("VARCHAR");
        field1.setSize(30);
        field1.setShouldAllowNull(false);
        field1.setIsPrimaryKey(false);
        field1.setUnique(false);
        field1.setIsIdentity(false);
        table.addField(field1);
    
        return table;
    }
    
    public TableDefinition buildCandidateHonorsTenantBTable() {
        TableDefinition table = new TableDefinition();
        table.setName("XML_CANDIDATE_HONORS_B");
    
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("XMLCandidate_ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(false);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(false);
        table.addField(fieldID);
    
        FieldDefinition field1 = new FieldDefinition();
        field1.setName("HONOR");
        field1.setTypeName("VARCHAR");
        field1.setSize(30);
        field1.setShouldAllowNull(false);
        field1.setIsPrimaryKey(false);
        field1.setUnique(false);
        field1.setIsIdentity(false);
        table.addField(field1);
    
        return table;
    }
    
    public TableDefinition buildCandidateHonorsTenantCTable() {
        TableDefinition table = new TableDefinition();
        table.setName("XML_CANDIDATE_HONORS_C");
    
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("XMLCandidate_ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(false);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(false);
        table.addField(fieldID);
    
        FieldDefinition field1 = new FieldDefinition();
        field1.setName("HONOR");
        field1.setTypeName("VARCHAR");
        field1.setSize(30);
        field1.setShouldAllowNull(false);
        field1.setIsPrimaryKey(false);
        field1.setUnique(false);
        field1.setIsIdentity(false);
        table.addField(field1);
    
        return table;
    }
    
    public TableDefinition buildCandidateSupporterTenantATable() {
        TableDefinition table = new TableDefinition();
        table.setName("XML_CAN_SUP_A");
        
        FieldDefinition fieldCandidateId = new FieldDefinition();
        fieldCandidateId.setName("CANDIDATE_ID");
        fieldCandidateId.setTypeName("NUMERIC");
        fieldCandidateId.setSize(15);
        fieldCandidateId.setIsPrimaryKey(true);
        fieldCandidateId.setIsIdentity(false);
        fieldCandidateId.setUnique(false);
        fieldCandidateId.setShouldAllowNull(false);
        fieldCandidateId.setForeignKeyFieldName("XML_CANDIDATE_A.ID");
        table.addField(fieldCandidateId);
        
        FieldDefinition fieldSupporterId = new FieldDefinition();
        fieldSupporterId.setName("SUPPORTER_ID");
        fieldSupporterId.setTypeName("NUMERIC");
        fieldSupporterId.setSize(15);
        fieldSupporterId.setIsPrimaryKey(true);
        fieldSupporterId.setIsIdentity(false);
        fieldSupporterId.setUnique(false);
        fieldSupporterId.setShouldAllowNull(false);
        fieldSupporterId.setForeignKeyFieldName("A_XML_SUPPORTER.ID");
        table.addField(fieldSupporterId);
        
        return table;
    }
    
    public TableDefinition buildCandidateSupporterTenantBTable() {
        TableDefinition table = new TableDefinition();
        table.setName("XML_CAN_SUP_B");
        
        FieldDefinition fieldCandidateId = new FieldDefinition();
        fieldCandidateId.setName("CANDIDATE_ID");
        fieldCandidateId.setTypeName("NUMERIC");
        fieldCandidateId.setSize(15);
        fieldCandidateId.setIsPrimaryKey(true);
        fieldCandidateId.setIsIdentity(false);
        fieldCandidateId.setUnique(false);
        fieldCandidateId.setShouldAllowNull(false);
        fieldCandidateId.setForeignKeyFieldName("XML_CANDIDATE_B.ID");
        table.addField(fieldCandidateId);
        
        FieldDefinition fieldSupporterId = new FieldDefinition();
        fieldSupporterId.setName("SUPPORTER_ID");
        fieldSupporterId.setTypeName("NUMERIC");
        fieldSupporterId.setSize(15);
        fieldSupporterId.setIsPrimaryKey(true);
        fieldSupporterId.setIsIdentity(false);
        fieldSupporterId.setUnique(false);
        fieldSupporterId.setShouldAllowNull(false);
        fieldSupporterId.setForeignKeyFieldName("B_XML_SUPPORTER.ID");
        table.addField(fieldSupporterId);
        
        return table;
    }
    
    public TableDefinition buildCandidateSupporterTenantCTable() {
        TableDefinition table = new TableDefinition();
        table.setName("XML_CAN_SUP_C");
        
        FieldDefinition fieldCandidateId = new FieldDefinition();
        fieldCandidateId.setName("CANDIDATE_ID");
        fieldCandidateId.setTypeName("NUMERIC");
        fieldCandidateId.setSize(15);
        fieldCandidateId.setIsPrimaryKey(true);
        fieldCandidateId.setIsIdentity(false);
        fieldCandidateId.setUnique(false);
        fieldCandidateId.setShouldAllowNull(false);
        fieldCandidateId.setForeignKeyFieldName("XML_CANDIDATE_C.ID");
        table.addField(fieldCandidateId);
        
        FieldDefinition fieldSupporterId = new FieldDefinition();
        fieldSupporterId.setName("SUPPORTER_ID");
        fieldSupporterId.setTypeName("NUMERIC");
        fieldSupporterId.setSize(15);
        fieldSupporterId.setIsPrimaryKey(true);
        fieldSupporterId.setIsIdentity(false);
        fieldSupporterId.setUnique(false);
        fieldSupporterId.setShouldAllowNull(false);
        fieldSupporterId.setForeignKeyFieldName("C_XML_SUPPORTER.ID");
        table.addField(fieldSupporterId);
        
        return table;
    }
    
    public TableDefinition buildCandidateTenantATable() {
        TableDefinition table = new TableDefinition();
        table.setName("XML_CANDIDATE_A");
        
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(true);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(true);
        table.addField(fieldID);
    
        FieldDefinition fieldName = new FieldDefinition();
        fieldName.setName("NAME");
        fieldName.setTypeName("VARCHAR");
        fieldName.setSize(30);
        fieldName.setShouldAllowNull(true);
        fieldName.setIsPrimaryKey(false);
        fieldName.setUnique(false);
        fieldName.setIsIdentity(false);
        table.addField(fieldName);
        
        FieldDefinition fieldRidingId = new FieldDefinition();
        fieldRidingId.setName("RIDING_ID");
        fieldRidingId.setTypeName("NUMERIC");
        fieldRidingId.setSize(15);
        fieldRidingId.setIsPrimaryKey(false);
        fieldRidingId.setIsIdentity(false);
        fieldRidingId.setUnique(false);
        fieldRidingId.setShouldAllowNull(true);
        fieldRidingId.setForeignKeyFieldName("XML_RIDING.ID");
        table.addField(fieldRidingId);
        
        FieldDefinition fieldPartyId = new FieldDefinition();
        fieldPartyId.setName("PARTY_ID");
        fieldPartyId.setTypeName("NUMERIC");
        fieldPartyId.setSize(15);
        fieldPartyId.setIsPrimaryKey(false);
        fieldPartyId.setIsIdentity(false);
        fieldPartyId.setUnique(false);
        fieldPartyId.setShouldAllowNull(true);
        fieldPartyId.setForeignKeyFieldName("XML_PARTY.ID");
        table.addField(fieldPartyId);
        
        return table;
    }
    
    public TableDefinition buildCandidateTenantBTable() {
        TableDefinition table = new TableDefinition();
        table.setName("XML_CANDIDATE_B");
        
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(true);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(true);
        table.addField(fieldID);
    
        FieldDefinition fieldName = new FieldDefinition();
        fieldName.setName("NAME");
        fieldName.setTypeName("VARCHAR");
        fieldName.setSize(30);
        fieldName.setShouldAllowNull(true);
        fieldName.setIsPrimaryKey(false);
        fieldName.setUnique(false);
        fieldName.setIsIdentity(false);
        table.addField(fieldName);
        
        FieldDefinition fieldRidingId = new FieldDefinition();
        fieldRidingId.setName("RIDING_ID");
        fieldRidingId.setTypeName("NUMERIC");
        fieldRidingId.setSize(15);
        fieldRidingId.setIsPrimaryKey(false);
        fieldRidingId.setIsIdentity(false);
        fieldRidingId.setUnique(false);
        fieldRidingId.setShouldAllowNull(true);
        fieldRidingId.setForeignKeyFieldName("XML_RIDING.ID");
        table.addField(fieldRidingId);
        
        FieldDefinition fieldPartyId = new FieldDefinition();
        fieldPartyId.setName("PARTY_ID");
        fieldPartyId.setTypeName("NUMERIC");
        fieldPartyId.setSize(15);
        fieldPartyId.setIsPrimaryKey(false);
        fieldPartyId.setIsIdentity(false);
        fieldPartyId.setUnique(false);
        fieldPartyId.setShouldAllowNull(true);
        fieldPartyId.setForeignKeyFieldName("XML_PARTY.ID");
        table.addField(fieldPartyId);
        
        return table;
    }
    
    public TableDefinition buildCandidateTenantCTable() {
        TableDefinition table = new TableDefinition();
        table.setName("XML_CANDIDATE_C");
        
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(true);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(true);
        table.addField(fieldID);
    
        FieldDefinition fieldName = new FieldDefinition();
        fieldName.setName("NAME");
        fieldName.setTypeName("VARCHAR");
        fieldName.setSize(30);
        fieldName.setShouldAllowNull(true);
        fieldName.setIsPrimaryKey(false);
        fieldName.setUnique(false);
        fieldName.setIsIdentity(false);
        table.addField(fieldName);
        
        FieldDefinition fieldRidingId = new FieldDefinition();
        fieldRidingId.setName("RIDING_ID");
        fieldRidingId.setTypeName("NUMERIC");
        fieldRidingId.setSize(15);
        fieldRidingId.setIsPrimaryKey(false);
        fieldRidingId.setIsIdentity(false);
        fieldRidingId.setUnique(false);
        fieldRidingId.setShouldAllowNull(true);
        fieldRidingId.setForeignKeyFieldName("XML_RIDING.ID");
        table.addField(fieldRidingId);
        
        FieldDefinition fieldPartyId = new FieldDefinition();
        fieldPartyId.setName("PARTY_ID");
        fieldPartyId.setTypeName("NUMERIC");
        fieldPartyId.setSize(15);
        fieldPartyId.setIsPrimaryKey(false);
        fieldPartyId.setIsIdentity(false);
        fieldPartyId.setUnique(false);
        fieldPartyId.setShouldAllowNull(true);
        fieldPartyId.setForeignKeyFieldName("XML_PARTY.ID");
        table.addField(fieldPartyId);
        
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
    
    public TableDefinition buildSalaryTenantATable() {
        TableDefinition table = new TableDefinition();
        table.setName("XML_CANDY_SALARY_A");

        FieldDefinition fieldCandidateId = new FieldDefinition();
        fieldCandidateId.setName("ID");
        fieldCandidateId.setTypeName("NUMERIC");
        fieldCandidateId.setSize(15);
        fieldCandidateId.setSubSize(0);
        fieldCandidateId.setIsPrimaryKey(true);
        fieldCandidateId.setIsIdentity(false);
        fieldCandidateId.setUnique(false);
        fieldCandidateId.setShouldAllowNull(false);
        fieldCandidateId.setForeignKeyFieldName("XML_CANDIDATE_A.ID");
        table.addField(fieldCandidateId);

        FieldDefinition fieldSalary = new FieldDefinition();
        fieldSalary.setName("SALARY");
        fieldSalary.setTypeName("NUMBER");
        fieldSalary.setSize(15);
        fieldSalary.setSubSize(0);
        fieldSalary.setIsPrimaryKey(false);
        fieldSalary.setIsIdentity(false);
        fieldSalary.setUnique(false);
        fieldSalary.setShouldAllowNull(true);
        table.addField(fieldSalary);

        return table;
    }
    
    public TableDefinition buildSalaryTenantBTable() {
        TableDefinition table = new TableDefinition();
        table.setName("XML_CANDY_SALARY_B");

        FieldDefinition fieldCandidateId = new FieldDefinition();
        fieldCandidateId.setName("ID");
        fieldCandidateId.setTypeName("NUMERIC");
        fieldCandidateId.setSize(15);
        fieldCandidateId.setSubSize(0);
        fieldCandidateId.setIsPrimaryKey(true);
        fieldCandidateId.setIsIdentity(false);
        fieldCandidateId.setUnique(false);
        fieldCandidateId.setShouldAllowNull(false);
        fieldCandidateId.setForeignKeyFieldName("XML_CANDIDATE_B.ID");
        table.addField(fieldCandidateId);

        FieldDefinition fieldSalary = new FieldDefinition();
        fieldSalary.setName("SALARY");
        fieldSalary.setTypeName("NUMBER");
        fieldSalary.setSize(15);
        fieldSalary.setSubSize(0);
        fieldSalary.setIsPrimaryKey(false);
        fieldSalary.setIsIdentity(false);
        fieldSalary.setUnique(false);
        fieldSalary.setShouldAllowNull(true);
        table.addField(fieldSalary);

        return table;
    }
    
    public TableDefinition buildSalaryTenantCTable() {
        TableDefinition table = new TableDefinition();
        table.setName("XML_CANDY_SALARY_C");

        FieldDefinition fieldCandidateId = new FieldDefinition();
        fieldCandidateId.setName("ID");
        fieldCandidateId.setTypeName("NUMERIC");
        fieldCandidateId.setSize(15);
        fieldCandidateId.setSubSize(0);
        fieldCandidateId.setIsPrimaryKey(true);
        fieldCandidateId.setIsIdentity(false);
        fieldCandidateId.setUnique(false);
        fieldCandidateId.setShouldAllowNull(false);
        fieldCandidateId.setForeignKeyFieldName("XML_CANDIDATE_C.ID");
        table.addField(fieldCandidateId);

        FieldDefinition fieldSalary = new FieldDefinition();
        fieldSalary.setName("SALARY");
        fieldSalary.setTypeName("NUMBER");
        fieldSalary.setSize(15);
        fieldSalary.setSubSize(0);
        fieldSalary.setIsPrimaryKey(false);
        fieldSalary.setIsIdentity(false);
        fieldSalary.setUnique(false);
        fieldSalary.setShouldAllowNull(true);
        table.addField(fieldSalary);

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
    
    public TableDefinition buildSupporterTenantATable() {
        TableDefinition table = new TableDefinition();
        table.setName("A_XML_SUPPORTER");
        
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setIsPrimaryKey(true);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(true);
        fieldID.setShouldAllowNull(false);
        table.addField(fieldID);
    
        FieldDefinition fieldName = new FieldDefinition();
        fieldName.setName("NAME");
        fieldName.setTypeName("VARCHAR");
        fieldName.setSize(30);
        fieldName.setIsPrimaryKey(false);
        fieldName.setUnique(false);
        fieldName.setIsIdentity(false);
        fieldName.setShouldAllowNull(false);
        table.addField(fieldName);
        
        return table;
    }
     
    public TableDefinition buildSupporterTenantBTable() {
        TableDefinition table = new TableDefinition();
        table.setName("B_XML_SUPPORTER");
        
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setIsPrimaryKey(true);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(true);
        fieldID.setShouldAllowNull(false);
        table.addField(fieldID);
    
        FieldDefinition fieldName = new FieldDefinition();
        fieldName.setName("NAME");
        fieldName.setTypeName("VARCHAR");
        fieldName.setSize(30);
        fieldName.setIsPrimaryKey(false);
        fieldName.setUnique(false);
        fieldName.setIsIdentity(false);
        fieldName.setShouldAllowNull(false);
        table.addField(fieldName);
        
        return table;
    }
    
    public TableDefinition buildSupporterTenantCTable() {
        TableDefinition table = new TableDefinition();
        table.setName("C_XML_SUPPORTER");
        
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setIsPrimaryKey(true);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(true);
        fieldID.setShouldAllowNull(false);
        table.addField(fieldID);
    
        FieldDefinition fieldName = new FieldDefinition();
        fieldName.setName("NAME");
        fieldName.setTypeName("VARCHAR");
        fieldName.setSize(30);
        fieldName.setIsPrimaryKey(false);
        fieldName.setUnique(false);
        fieldName.setIsIdentity(false);
        fieldName.setShouldAllowNull(false);
        table.addField(fieldName);
        
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
    public TableDefinition buildRidingTable() {
        TableDefinition table = new TableDefinition();
        table.setName("XML_RIDING");
        
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(true);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(true);
        table.addField(fieldID);
    
        FieldDefinition fieldName = new FieldDefinition();
        fieldName.setName("NAME");
        fieldName.setTypeName("VARCHAR");
        fieldName.setSize(30);
        fieldName.setShouldAllowNull(true);
        fieldName.setIsPrimaryKey(false);
        fieldName.setUnique(false);
        fieldName.setIsIdentity(false);
        table.addField(fieldName);
        
        return table;
    }
    
    public TableDefinition buildPartyTable() {
        TableDefinition table = new TableDefinition();
        table.setName("XML_PARTY");
        
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(true);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(true);
        table.addField(fieldID);
    
        FieldDefinition fieldName = new FieldDefinition();
        fieldName.setName("NAME");
        fieldName.setTypeName("VARCHAR");
        fieldName.setSize(30);
        fieldName.setShouldAllowNull(true);
        fieldName.setIsPrimaryKey(false);
        fieldName.setUnique(false);
        fieldName.setIsIdentity(false);
        table.addField(fieldName);
        
        return table;
    }
}
