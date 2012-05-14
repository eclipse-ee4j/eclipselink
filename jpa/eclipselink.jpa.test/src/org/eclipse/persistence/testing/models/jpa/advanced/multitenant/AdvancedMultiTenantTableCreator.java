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
 *     06/1/2011-2.3 Guy Pelletier 
 *       - 337323: Multi-tenant with shared schema support (part 9)
 *     11/10/2011-2.4 Guy Pelletier 
 *       - 357474: Address primaryKey option from tenant discriminator column
 *     14/05/2012-2.4 Guy Pelletier  
 *       - 376603: Provide for table per tenant support for multitenant applications
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.advanced.multitenant;

import org.eclipse.persistence.testing.framework.TogglingFastTableCreator;
import org.eclipse.persistence.tools.schemaframework.*;

public class AdvancedMultiTenantTableCreator extends TogglingFastTableCreator {
    public AdvancedMultiTenantTableCreator() {
        setName("JPA Advanced Multi-Tenant Project");

        addTableDefinition(buildAddressTable());
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
        addTableDefinition(buildRewardTable());
        addTableDefinition(buildPhoneNumberTable());
        
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
    
    public TableDefinition buildAddressTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_MAFIOSO_ADDRESS");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ADDRESS_ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setSubSize(0);
        fieldID.setIsPrimaryKey(true);
        fieldID.setIsIdentity(false);
        fieldID.setUnique(false);
        fieldID.setShouldAllowNull(false);
        table.addField(fieldID);

        FieldDefinition fieldStreet = new FieldDefinition();
        fieldStreet.setName("STREET");
        fieldStreet.setTypeName("VARCHAR2");
        fieldStreet.setSize(60);
        fieldStreet.setSubSize(0);
        fieldStreet.setIsPrimaryKey(false);
        fieldStreet.setIsIdentity(false);
        fieldStreet.setUnique(false);
        fieldStreet.setShouldAllowNull(true);
        table.addField(fieldStreet);

        FieldDefinition fieldCity = new FieldDefinition();
        fieldCity.setName("CITY");
        fieldCity.setTypeName("VARCHAR2");
        fieldCity.setSize(60);
        fieldCity.setSubSize(0);
        fieldCity.setIsPrimaryKey(false);
        fieldCity.setIsIdentity(false);
        fieldCity.setUnique(false);
        fieldCity.setShouldAllowNull(true);
        table.addField(fieldCity);

        FieldDefinition fieldProvince = new FieldDefinition();
        fieldProvince.setName("PROVINCE");
        fieldProvince.setTypeName("VARCHAR2");
        fieldProvince.setSize(60);
        fieldProvince.setSubSize(0);
        fieldProvince.setIsPrimaryKey(false);
        fieldProvince.setIsIdentity(false);
        fieldProvince.setUnique(false);
        fieldProvince.setShouldAllowNull(true);
        table.addField(fieldProvince);

        FieldDefinition fieldPostalCode = new FieldDefinition();
        fieldPostalCode.setName("P_CODE");
        fieldPostalCode.setTypeName("VARCHAR2");
        fieldPostalCode.setSize(67);
        fieldPostalCode.setSubSize(0);
        fieldPostalCode.setIsPrimaryKey(false);
        fieldPostalCode.setIsIdentity(false);
        fieldPostalCode.setUnique(false);
        fieldPostalCode.setShouldAllowNull(true);
        table.addField(fieldPostalCode);

        FieldDefinition fieldCountry = new FieldDefinition();
        fieldCountry.setName("COUNTRY");
        fieldCountry.setTypeName("VARCHAR2");
        fieldCountry.setSize(60);
        fieldCountry.setSubSize(0);
        fieldCountry.setIsPrimaryKey(false);
        fieldCountry.setIsIdentity(false);
        fieldCountry.setUnique(false);
        fieldCountry.setShouldAllowNull(true);
        table.addField(fieldCountry);
        
        FieldDefinition fieldVersion = new FieldDefinition();
        fieldVersion.setName("VERSION");
        fieldVersion.setTypeName("NUMERIC");
        fieldVersion.setSize(15);
        fieldVersion.setIsPrimaryKey(false);
        fieldVersion.setUnique(false);
        fieldVersion.setIsIdentity(false);
        fieldVersion.setShouldAllowNull(true);
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
    
    public TableDefinition buildMafiaFamilyTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_MAFIA_FAMILY");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setIsPrimaryKey(true);
        fieldID.setIsIdentity(false);
        fieldID.setUnique(true);
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
        fieldTenantId.setIsPrimaryKey(true);
        fieldTenantId.setIsIdentity(false);
        fieldTenantId.setUnique(true);
        fieldTenantId.setShouldAllowNull(false);
        table.addField(fieldTenantId);
    
        return table;
    }
    
    public TableDefinition buildMafiaFamily_TagsTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_FAMILY_TAGS");

        FieldDefinition fieldFamilyId = new FieldDefinition();
        fieldFamilyId.setName("FAMILY_ID");
        fieldFamilyId.setTypeName("NUMERIC");
        fieldFamilyId.setSize(15);
        fieldFamilyId.setIsPrimaryKey(false);
        fieldFamilyId.setIsIdentity(false);
        fieldFamilyId.setUnique(false);
        fieldFamilyId.setShouldAllowNull(false);
        fieldFamilyId.setForeignKeyFieldName("JPA_MAFIA_FAMILY.ID");
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
        
        FieldDefinition fieldTenantId = new FieldDefinition();
        fieldTenantId.setName("TENANT_ID");
        fieldTenantId.setTypeName("VARCHAR2");
        fieldTenantId.setSize(10);
        fieldTenantId.setSubSize(0);
        fieldTenantId.setIsPrimaryKey(false);
        fieldTenantId.setIsIdentity(false);
        fieldTenantId.setUnique(false);
        fieldTenantId.setShouldAllowNull(false);
        fieldTenantId.setForeignKeyFieldName("JPA_MAFIA_FAMILY.TENANT_ID");
        table.addField(fieldTenantId);

        return table;
    }
    
    public TableDefinition buildMafiaFamily_RevenueTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_FAMILY_REVENUE");

        FieldDefinition fieldFamilyId = new FieldDefinition();
        fieldFamilyId.setName("FAMILY_ID");
        fieldFamilyId.setTypeName("NUMERIC");
        fieldFamilyId.setSize(15);
        fieldFamilyId.setIsPrimaryKey(true);
        fieldFamilyId.setIsIdentity(false);
        fieldFamilyId.setUnique(true);
        fieldFamilyId.setShouldAllowNull(false);
        fieldFamilyId.setForeignKeyFieldName("JPA_MAFIA_FAMILY.ID");
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
        
        FieldDefinition fieldTenantId = new FieldDefinition();
        fieldTenantId.setName("TENANT_ID");
        fieldTenantId.setTypeName("VARCHAR2");
        fieldTenantId.setSize(10);
        fieldTenantId.setSubSize(0);
        fieldTenantId.setIsPrimaryKey(true);
        fieldTenantId.setIsIdentity(false);
        fieldTenantId.setUnique(true);
        fieldTenantId.setShouldAllowNull(false);
        fieldTenantId.setForeignKeyFieldName("JPA_MAFIA_FAMILY.TENANT_ID");
        table.addField(fieldTenantId);
        
        return table;
    }
    
    public TableDefinition buildMafiosoTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_MAFIOSO");
        
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(true);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(false);
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
        //fieldFamily.setForeignKeyFieldName("JPA_MAFIA_FAMILY.ID");
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
    
        FieldDefinition fieldAddress = new FieldDefinition();
        fieldAddress.setName("ADDRESS_ID");
        fieldAddress.setTypeName("NUMERIC");
        fieldAddress.setSize(15);
        fieldAddress.setShouldAllowNull(true);
        fieldAddress.setIsPrimaryKey(false);
        fieldAddress.setUnique(false);
        fieldAddress.setIsIdentity(false);
        table.addField(fieldAddress);
        
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
        table.setName("JPA_MASON_A");
        
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
        fieldTrowelId.setForeignKeyFieldName("JPA_TROWEL_A.ID");
        table.addField(fieldTrowelId);
        
        return table;
    }
    
    public TableDefinition buildMasonTenantBTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_MASON_B");
        
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
        fieldTrowelId.setForeignKeyFieldName("JPA_TROWEL_B.ID");
        table.addField(fieldTrowelId);
        
        return table;
    }
    
    public TableDefinition buildMasonTenantCTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_MASON_C");
        
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
        fieldTrowelId.setForeignKeyFieldName("JPA_TROWEL_C.ID");
        table.addField(fieldTrowelId);
        
        return table;
    }
    
    public TableDefinition buildMasonAwardsTenantATable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_MASON_AWARDS_A");
    
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("MASON_ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(false);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(false);
        fieldID.setForeignKeyFieldName("JPA_MASON_A.ID");
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
        table.setName("JPA_MASON_AWARDS_B");
    
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("MASON_ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(false);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(false);
        fieldID.setForeignKeyFieldName("JPA_MASON_B.ID");
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
        table.setName("JPA_MASON_AWARDS_C");
    
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("MASON_ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setShouldAllowNull(false);
        fieldID.setIsPrimaryKey(false);
        fieldID.setUnique(false);
        fieldID.setIsIdentity(false);
        fieldID.setForeignKeyFieldName("JPA_MASON_C.ID");
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
        table.setName("JPA_TROWEL_A");
        
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
        table.setName("JPA_TROWEL_B");
        
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
        table.setName("JPA_TROWEL_C");
        
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
    
    public TableDefinition buildPhoneNumberTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_MT_PHONE_NUMBER");
    
        FieldDefinition fieldId = new FieldDefinition();
        fieldId.setName("ID");
        fieldId.setTypeName("NUMERIC");
        fieldId.setSize(15);
        fieldId.setShouldAllowNull(false);
        fieldId.setIsPrimaryKey(true);
        fieldId.setUnique(false);
        fieldId.setIsIdentity(false);
        table.addField(fieldId);
        
        FieldDefinition fieldType = new FieldDefinition();
        fieldType.setName("TYPE");
        fieldType.setTypeName("VARCHAR2");
        fieldType.setSize(15);
        fieldType.setShouldAllowNull(false);
        fieldType.setIsPrimaryKey(true);
        fieldType.setUnique(false);
        fieldType.setIsIdentity(false);
        table.addField(fieldType);
    
        FieldDefinition fieldAreaCode = new FieldDefinition();
        fieldAreaCode.setName("AREA_CODE");
        fieldAreaCode.setTypeName("VARCHAR2");
        fieldAreaCode.setSize(3);
        fieldAreaCode.setShouldAllowNull(true);
        fieldAreaCode.setIsPrimaryKey(false);
        fieldAreaCode.setUnique(false);
        fieldAreaCode.setIsIdentity(false);
        table.addField(fieldAreaCode);
    
        FieldDefinition fieldNumber = new FieldDefinition();
        fieldNumber.setName("NUMB");
        fieldNumber.setTypeName("VARCHAR2");
        fieldNumber.setSize(8);
        fieldNumber.setShouldAllowNull(true);
        fieldNumber.setIsPrimaryKey(false);
        fieldNumber.setUnique(false);
        fieldNumber.setIsIdentity(false);
        table.addField(fieldNumber);
        
        FieldDefinition fieldTenantId = new FieldDefinition();
        fieldTenantId.setName("TENANT_ID");
        fieldTenantId.setTypeName("VARCHAR2");
        fieldTenantId.setSize(10);
        fieldTenantId.setSubSize(0);
        fieldTenantId.setIsPrimaryKey(true);
        fieldTenantId.setIsIdentity(false);
        fieldTenantId.setUnique(false);
        fieldTenantId.setShouldAllowNull(false);
        table.addField(fieldTenantId);

        return table;
    }
    
    public TableDefinition buildBossTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_BOSS");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setIsPrimaryKey(true);
        fieldID.setIsIdentity(false);
        fieldID.setUnique(false);
        fieldID.setShouldAllowNull(false);
        fieldID.setForeignKeyFieldName("JPA_MAFIOSO.ID");
        table.addField(fieldID);

        FieldDefinition fieldUnderboss = new FieldDefinition();
        fieldUnderboss.setName("UNDERBOSS_ID");
        fieldUnderboss.setTypeName("NUMERIC");
        fieldUnderboss.setSize(15);
        fieldUnderboss.setShouldAllowNull(true);
        fieldUnderboss.setIsPrimaryKey(false);
        fieldUnderboss.setUnique(false);
        fieldUnderboss.setIsIdentity(false);
        fieldUnderboss.setForeignKeyFieldName("JPA_MAFIOSO.ID");
        table.addField(fieldUnderboss);
        
        return table;
    }

    public TableDefinition buildUnderbossTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_UNDERBOSS");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setIsPrimaryKey(true);
        fieldID.setIsIdentity(false);
        fieldID.setUnique(false);
        fieldID.setShouldAllowNull(false);
        fieldID.setForeignKeyFieldName("JPA_MAFIOSO.ID");
        table.addField(fieldID);
        
        return table;
    }
    
    public TableDefinition buildCandidateHonorsTenantATable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_CANDIDATE_HONORS_A");
    
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("Candidate_ID");
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
        table.setName("JPA_CANDIDATE_HONORS_B");
    
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("Candidate_ID");
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
        table.setName("JPA_CANDIDATE_HONORS_C");
    
        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("Candidate_ID");
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
        table.setName("JPA_CAN_SUP_A");
        
        FieldDefinition fieldCandidateId = new FieldDefinition();
        fieldCandidateId.setName("CANDIDATE_ID");
        fieldCandidateId.setTypeName("NUMERIC");
        fieldCandidateId.setSize(15);
        fieldCandidateId.setIsPrimaryKey(true);
        fieldCandidateId.setIsIdentity(false);
        fieldCandidateId.setUnique(false);
        fieldCandidateId.setShouldAllowNull(false);
        fieldCandidateId.setForeignKeyFieldName("JPA_CANDIDATE_A.ID");
        table.addField(fieldCandidateId);
        
        FieldDefinition fieldSupporterId = new FieldDefinition();
        fieldSupporterId.setName("SUPPORTER_ID");
        fieldSupporterId.setTypeName("NUMERIC");
        fieldSupporterId.setSize(15);
        fieldSupporterId.setIsPrimaryKey(true);
        fieldSupporterId.setIsIdentity(false);
        fieldSupporterId.setUnique(false);
        fieldSupporterId.setShouldAllowNull(false);
        fieldSupporterId.setForeignKeyFieldName("A_JPA_SUPPORTER.ID");
        table.addField(fieldSupporterId);
        
        return table;
    }
    
    public TableDefinition buildCandidateSupporterTenantBTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_CAN_SUP_B");
        
        FieldDefinition fieldCandidateId = new FieldDefinition();
        fieldCandidateId.setName("CANDIDATE_ID");
        fieldCandidateId.setTypeName("NUMERIC");
        fieldCandidateId.setSize(15);
        fieldCandidateId.setIsPrimaryKey(true);
        fieldCandidateId.setIsIdentity(false);
        fieldCandidateId.setUnique(false);
        fieldCandidateId.setShouldAllowNull(false);
        fieldCandidateId.setForeignKeyFieldName("JPA_CANDIDATE_B.ID");
        table.addField(fieldCandidateId);
        
        FieldDefinition fieldSupporterId = new FieldDefinition();
        fieldSupporterId.setName("SUPPORTER_ID");
        fieldSupporterId.setTypeName("NUMERIC");
        fieldSupporterId.setSize(15);
        fieldSupporterId.setIsPrimaryKey(true);
        fieldSupporterId.setIsIdentity(false);
        fieldSupporterId.setUnique(false);
        fieldSupporterId.setShouldAllowNull(false);
        fieldSupporterId.setForeignKeyFieldName("B_JPA_SUPPORTER.ID");
        table.addField(fieldSupporterId);
        
        return table;
    }
    
    public TableDefinition buildCandidateSupporterTenantCTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_CAN_SUP_C");
        
        FieldDefinition fieldCandidateId = new FieldDefinition();
        fieldCandidateId.setName("CANDIDATE_ID");
        fieldCandidateId.setTypeName("NUMERIC");
        fieldCandidateId.setSize(15);
        fieldCandidateId.setIsPrimaryKey(true);
        fieldCandidateId.setIsIdentity(false);
        fieldCandidateId.setUnique(false);
        fieldCandidateId.setShouldAllowNull(false);
        fieldCandidateId.setForeignKeyFieldName("JPA_CANDIDATE_C.ID");
        table.addField(fieldCandidateId);
        
        FieldDefinition fieldSupporterId = new FieldDefinition();
        fieldSupporterId.setName("SUPPORTER_ID");
        fieldSupporterId.setTypeName("NUMERIC");
        fieldSupporterId.setSize(15);
        fieldSupporterId.setIsPrimaryKey(true);
        fieldSupporterId.setIsIdentity(false);
        fieldSupporterId.setUnique(false);
        fieldSupporterId.setShouldAllowNull(false);
        fieldSupporterId.setForeignKeyFieldName("C_JPA_SUPPORTER.ID");
        table.addField(fieldSupporterId);
        
        return table;
    }
    
    public TableDefinition buildCandidateTenantATable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_CANDIDATE_A");
        
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
        fieldRidingId.setForeignKeyFieldName("JPA_RIDING.ID");
        table.addField(fieldRidingId);
        
        FieldDefinition fieldPartyId = new FieldDefinition();
        fieldPartyId.setName("PARTY_ID");
        fieldPartyId.setTypeName("NUMERIC");
        fieldPartyId.setSize(15);
        fieldPartyId.setIsPrimaryKey(false);
        fieldPartyId.setIsIdentity(false);
        fieldPartyId.setUnique(false);
        fieldPartyId.setShouldAllowNull(true);
        fieldPartyId.setForeignKeyFieldName("JPA_PARTY.ID");
        table.addField(fieldPartyId);
        
        return table;
    }
    
    public TableDefinition buildCandidateTenantBTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_CANDIDATE_B");
        
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
        fieldRidingId.setForeignKeyFieldName("JPA_RIDING.ID");
        table.addField(fieldRidingId);
        
        FieldDefinition fieldPartyId = new FieldDefinition();
        fieldPartyId.setName("PARTY_ID");
        fieldPartyId.setTypeName("NUMERIC");
        fieldPartyId.setSize(15);
        fieldPartyId.setIsPrimaryKey(false);
        fieldPartyId.setIsIdentity(false);
        fieldPartyId.setUnique(false);
        fieldPartyId.setShouldAllowNull(true);
        fieldPartyId.setForeignKeyFieldName("JPA_PARTY.ID");
        table.addField(fieldPartyId);
        
        return table;
    }
    
    public TableDefinition buildCandidateTenantCTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_CANDIDATE_C");
        
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
        fieldRidingId.setForeignKeyFieldName("JPA_RIDING.ID");
        table.addField(fieldRidingId);
        
        FieldDefinition fieldPartyId = new FieldDefinition();
        fieldPartyId.setName("PARTY_ID");
        fieldPartyId.setTypeName("NUMERIC");
        fieldPartyId.setSize(15);
        fieldPartyId.setIsPrimaryKey(false);
        fieldPartyId.setIsIdentity(false);
        fieldPartyId.setUnique(false);
        fieldPartyId.setShouldAllowNull(true);
        fieldPartyId.setForeignKeyFieldName("JPA_PARTY.ID");
        table.addField(fieldPartyId);
        
        return table;
    }
    
    public TableDefinition buildCapoTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_CAPO");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setIsPrimaryKey(true);
        fieldID.setIsIdentity(false);
        fieldID.setUnique(false);
        fieldID.setShouldAllowNull(false);
        fieldID.setForeignKeyFieldName("JPA_MAFIOSO.ID");
        table.addField(fieldID);
        
        FieldDefinition fieldUnderboss = new FieldDefinition();
        fieldUnderboss.setName("UNDERBOSS_ID");
        fieldUnderboss.setTypeName("NUMERIC");
        fieldUnderboss.setSize(15);
        fieldUnderboss.setShouldAllowNull(false);
        fieldUnderboss.setIsPrimaryKey(false);
        fieldUnderboss.setUnique(false);
        fieldUnderboss.setIsIdentity(false);
        fieldUnderboss.setForeignKeyFieldName("JPA_MAFIOSO.ID");
        table.addField(fieldUnderboss);
        
        return table;
    }
    
    public TableDefinition buildSalaryTenantATable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_CANDY_SALARY_A");

        FieldDefinition fieldCandidateId = new FieldDefinition();
        fieldCandidateId.setName("ID");
        fieldCandidateId.setTypeName("NUMERIC");
        fieldCandidateId.setSize(15);
        fieldCandidateId.setSubSize(0);
        fieldCandidateId.setIsPrimaryKey(true);
        fieldCandidateId.setIsIdentity(false);
        fieldCandidateId.setUnique(false);
        fieldCandidateId.setShouldAllowNull(false);
        fieldCandidateId.setForeignKeyFieldName("JPA_CANDIDATE_A.ID");
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
        table.setName("JPA_CANDY_SALARY_B");

        FieldDefinition fieldCandidateId = new FieldDefinition();
        fieldCandidateId.setName("ID");
        fieldCandidateId.setTypeName("NUMERIC");
        fieldCandidateId.setSize(15);
        fieldCandidateId.setSubSize(0);
        fieldCandidateId.setIsPrimaryKey(true);
        fieldCandidateId.setIsIdentity(false);
        fieldCandidateId.setUnique(false);
        fieldCandidateId.setShouldAllowNull(false);
        fieldCandidateId.setForeignKeyFieldName("JPA_CANDIDATE_B.ID");
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
        table.setName("JPA_CANDY_SALARY_C");

        FieldDefinition fieldCandidateId = new FieldDefinition();
        fieldCandidateId.setName("ID");
        fieldCandidateId.setTypeName("NUMERIC");
        fieldCandidateId.setSize(15);
        fieldCandidateId.setSubSize(0);
        fieldCandidateId.setIsPrimaryKey(true);
        fieldCandidateId.setIsIdentity(false);
        fieldCandidateId.setUnique(false);
        fieldCandidateId.setShouldAllowNull(false);
        fieldCandidateId.setForeignKeyFieldName("JPA_CANDIDATE_C.ID");
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
        table.setName("JPA_SOLDIER");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setIsPrimaryKey(true);
        fieldID.setIsIdentity(false);
        fieldID.setUnique(false);
        fieldID.setShouldAllowNull(false);
        fieldID.setForeignKeyFieldName("JPA_MAFIOSO.ID");
        table.addField(fieldID);
        
        FieldDefinition fieldCapo = new FieldDefinition();
        fieldCapo.setName("CAPO_ID");
        fieldCapo.setTypeName("NUMERIC");
        fieldCapo.setSize(15);
        fieldCapo.setShouldAllowNull(false);
        fieldCapo.setIsPrimaryKey(false);
        fieldCapo.setUnique(false);
        fieldCapo.setIsIdentity(false);
        fieldCapo.setForeignKeyFieldName("JPA_MAFIOSO.ID");
        table.addField(fieldCapo);
        
        return table;
    }
    
    public TableDefinition buildSupporterTenantATable() {
        TableDefinition table = new TableDefinition();
        table.setName("A_JPA_SUPPORTER");
        
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
        table.setName("B_JPA_SUPPORTER");
        
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
        table.setName("C_JPA_SUPPORTER");
        
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
        table.setName("JPA_CONTRACT");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setIsPrimaryKey(true);
        fieldID.setIsIdentity(false);
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
        table.setName("JPA_CONTRACT_SOLDIER");

        FieldDefinition fieldContractId = new FieldDefinition();
        fieldContractId.setName("CONTRACT_ID");
        fieldContractId.setTypeName("NUMERIC");
        fieldContractId.setSize(15);
        fieldContractId.setShouldAllowNull(false);
        fieldContractId.setIsPrimaryKey(true);
        fieldContractId.setUnique(false);
        fieldContractId.setIsIdentity(false);
        fieldContractId.setForeignKeyFieldName("JPA_CONTRACT.ID");
        table.addField(fieldContractId);
        
        FieldDefinition fieldSoldierId = new FieldDefinition();
        fieldSoldierId.setName("SOLDIER_ID");
        fieldSoldierId.setTypeName("NUMERIC");
        fieldSoldierId.setSize(15);
        fieldSoldierId.setShouldAllowNull(false);
        fieldSoldierId.setIsPrimaryKey(true);
        fieldSoldierId.setUnique(false);
        fieldSoldierId.setIsIdentity(false);
        fieldSoldierId.setForeignKeyFieldName("JPA_MAFIOSO.ID");
        table.addField(fieldSoldierId);
        
        return table;   
    }
    
    public TableDefinition buildRewardTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_REWARD");

        FieldDefinition fieldID = new FieldDefinition();
        fieldID.setName("ID");
        fieldID.setTypeName("NUMERIC");
        fieldID.setSize(15);
        fieldID.setIsPrimaryKey(true);
        fieldID.setIsIdentity(false);
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
        
        FieldDefinition fieldTenantId = new FieldDefinition();
        fieldTenantId.setName("T_ID");
        fieldTenantId.setTypeName("VARCHAR2");
        fieldTenantId.setSize(10);
        fieldTenantId.setSubSize(0);
        fieldTenantId.setIsPrimaryKey(false);
        fieldTenantId.setIsIdentity(false);
        fieldTenantId.setUnique(false);
        fieldTenantId.setShouldAllowNull(false);
        table.addField(fieldTenantId);
        
        FieldDefinition fieldMafiosoId = new FieldDefinition();
        fieldMafiosoId.setName("MAFIOSO_ID");
        fieldMafiosoId.setTypeName("NUMERIC");
        fieldMafiosoId.setSize(15);
        fieldMafiosoId.setShouldAllowNull(false);
        fieldMafiosoId.setIsPrimaryKey(true);
        fieldMafiosoId.setUnique(false);
        fieldMafiosoId.setIsIdentity(false);
        fieldMafiosoId.setForeignKeyFieldName("JPA_MAFIOSO.ID");
        table.addField(fieldMafiosoId);
        
        return table;
    }
    
    public TableDefinition buildRidingTable() {
        TableDefinition table = new TableDefinition();
        table.setName("JPA_RIDING");
        
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
        table.setName("JPA_PARTY");
        
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
