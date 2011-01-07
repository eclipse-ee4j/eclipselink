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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.optimisticlocking.cascaded;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.descriptors.*;

public class BarProject extends Project {
    public BarProject(DatabaseSession session) {
        setName("Bar");
        applyLogin(session);
	
        addDescriptor(buildBarDescriptor());
        addDescriptor(buildBartenderDescriptor());
        addDescriptor(buildLicenseDescriptor());
        
        addDescriptor(buildQualificationDescriptor());
        addDescriptor(buildAwardDescriptor());
    }

    public void applyLogin(DatabaseSession session) {
        DatabaseLogin login = (DatabaseLogin)session.getLogin().clone();

        // Configuration properties.
        login.setShouldBindAllParameters(false);
        login.setShouldCacheAllStatements(false);
        login.setUsesByteArrayBinding(true);
        login.setUsesStringBinding(false);

        if (login.shouldUseByteArrayBinding()) {
            login.setUsesStreamsForBinding(false);
        }

        login.setShouldForceFieldNamesToUpperCase(false);
        login.setShouldOptimizeDataConversion(true);
        login.setShouldTrimStrings(true);
        login.setUsesBatchWriting(false);

        if (login.shouldUseBatchWriting()) {
            login.setUsesJDBCBatchWriting(true);
        }

        login.setUsesExternalConnectionPooling(false);
        login.setUsesExternalTransactionController(false);
        setLogin(login);
    }

    public ClassDescriptor buildAwardDescriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(Award.class);
        descriptor.addTableName("CASCADE_AWARD");
        descriptor.addPrimaryKeyFieldName("CASCADE_AWARD.ID");
        
        // Descriptor Properties.
        descriptor.useSoftCacheWeakIdentityMap();
        descriptor.setIdentityMapSize(100);
        descriptor.useRemoteSoftCacheWeakIdentityMap();
        descriptor.setRemoteIdentityMapSize(100);
        descriptor.setSequenceNumberFieldName("CASCADE_AWARD.ID");
        descriptor.setSequenceNumberName("CASCADE_AWARD_SEQ");
        descriptor.setAlias("Award");
        
        // Query Manager.
        descriptor.getQueryManager().checkCacheForDoesExist();
        descriptor.getQueryManager().setQueryTimeout(0);
        
        // Mappings.
        DirectToFieldMapping idMapping = new DirectToFieldMapping();
        idMapping.setAttributeName("id");
        idMapping.setFieldName("CASCADE_AWARD.ID");
        descriptor.addMapping(idMapping);
        
        DirectToFieldMapping descriptionMapping = new DirectToFieldMapping();
        descriptionMapping.setAttributeName("description");
        descriptionMapping.setFieldName("CASCADE_AWARD.DESCRIPTION");
        descriptor.addMapping(descriptionMapping);
        
        OneToOneMapping qualificationMapping = new OneToOneMapping();
        qualificationMapping.setAttributeName("qualification");
        qualificationMapping.setReferenceClass(Qualification.class);
        qualificationMapping.useBasicIndirection();
        qualificationMapping.addForeignKeyFieldName("CASCADE_AWARD.QUALIFICATION_ID", "CASCADE_QUALIFICATION.ID");
        descriptor.addMapping(qualificationMapping);
        
        return descriptor;
    }
    
    public ClassDescriptor buildBarDescriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(Bar.class);
        descriptor.addTableName("CASCADE_BAR");
        descriptor.addPrimaryKeyFieldName("CASCADE_BAR.ID");
        
        // Descriptor Properties.
        descriptor.useSoftCacheWeakIdentityMap();
        descriptor.setIdentityMapSize(100);
        descriptor.useRemoteSoftCacheWeakIdentityMap();
        descriptor.setRemoteIdentityMapSize(100);
        descriptor.setSequenceNumberFieldName("CASCADE_BAR.ID");
        descriptor.setSequenceNumberName("CASCADE_BAR_SEQ");
        descriptor.setAlias("Bar");
        
        VersionLockingPolicy lockingPolicy = new VersionLockingPolicy();
        lockingPolicy.setWriteLockFieldName("CASCADE_BAR.VERSION");
        lockingPolicy.setIsCascaded(true);
        descriptor.setOptimisticLockingPolicy(lockingPolicy);
        
        // Query Manager.
        descriptor.getQueryManager().checkCacheForDoesExist();
        descriptor.getQueryManager().setQueryTimeout(0);
        
        // Mappings.
        DirectToFieldMapping idMapping = new DirectToFieldMapping();
        idMapping.setAttributeName("id");
        idMapping.setFieldName("CASCADE_BAR.ID");
        descriptor.addMapping(idMapping);
        
        DirectToFieldMapping nameMapping = new DirectToFieldMapping();
        nameMapping.setAttributeName("name");
        nameMapping.setFieldName("CASCADE_BAR.NAME");
        nameMapping.setNullValue("");
        descriptor.addMapping(nameMapping);
        
        DirectToFieldMapping versionMapping = new DirectToFieldMapping();
        versionMapping.setAttributeName("version");
        versionMapping.setFieldName("CASCADE_BAR.VERSION");
        versionMapping.setIsReadOnly(true);
        descriptor.addMapping(versionMapping);
        
        OneToOneMapping licenseMapping = new OneToOneMapping();
        licenseMapping.setAttributeName("license");
        licenseMapping.setReferenceClass(License.class);
        licenseMapping.useBasicIndirection();
        licenseMapping.privateOwnedRelationship();
        licenseMapping.addForeignKeyFieldName("CASCADE_BAR.LICENSE_ID", "CASCADE_LICENSE.ID");
        descriptor.addMapping(licenseMapping);
        
        OneToManyMapping bartendersMapping = new OneToManyMapping();
        bartendersMapping.setAttributeName("bartenders");
        bartendersMapping.setReferenceClass(Bartender.class);
        bartendersMapping.useBasicIndirection();
        bartendersMapping.privateOwnedRelationship();
        bartendersMapping.addTargetForeignKeyFieldName("CASCADE_BARTENDER.BAR_ID", "CASCADE_BAR.ID");
        descriptor.addMapping(bartendersMapping);
        
        return descriptor;
    }
    
    public ClassDescriptor buildBartenderDescriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(Bartender.class);
        descriptor.addTableName("CASCADE_BARTENDER");
        descriptor.addPrimaryKeyFieldName("CASCADE_BARTENDER.ID");
        
        // Descriptor Properties.
        descriptor.useSoftCacheWeakIdentityMap();
        descriptor.setIdentityMapSize(100);
        descriptor.useRemoteSoftCacheWeakIdentityMap();
        descriptor.setRemoteIdentityMapSize(100);
        descriptor.setSequenceNumberFieldName("CASCADE_BARTENDER.ID");
        descriptor.setSequenceNumberName("CASCADE_BARTENDER_SEQ");
        descriptor.setAlias("Bartender");
        
        // Query Manager.
        descriptor.getQueryManager().checkCacheForDoesExist();
        descriptor.getQueryManager().setQueryTimeout(0);
        
        // Mappings.
        DirectToFieldMapping idMapping = new DirectToFieldMapping();
        idMapping.setAttributeName("id");
        idMapping.setFieldName("CASCADE_BARTENDER.ID");
        descriptor.addMapping(idMapping);
        
        DirectToFieldMapping firstNameMapping = new DirectToFieldMapping();
        firstNameMapping.setAttributeName("firstName");
        firstNameMapping.setFieldName("CASCADE_BARTENDER.F_NAME");
        descriptor.addMapping(firstNameMapping);
        
        DirectToFieldMapping lastNameMapping = new DirectToFieldMapping();
        lastNameMapping.setAttributeName("lastName");
        lastNameMapping.setFieldName("CASCADE_BARTENDER.L_NAME");
        descriptor.addMapping(lastNameMapping);
        
        OneToOneMapping barMapping = new OneToOneMapping();
        barMapping.setAttributeName("bar");
        barMapping.setReferenceClass(Bar.class);
        barMapping.useBasicIndirection();
        barMapping.addForeignKeyFieldName("CASCADE_BARTENDER.BAR_ID", "CASCADE_BAR.ID");
        descriptor.addMapping(barMapping);
        
        OneToOneMapping qualificationMapping = new OneToOneMapping();
        qualificationMapping.setAttributeName("qualification");
        qualificationMapping.setReferenceClass(Qualification.class);
        qualificationMapping.useBasicIndirection();
        qualificationMapping.privateOwnedRelationship();
        qualificationMapping.addForeignKeyFieldName("CASCADE_BARTENDER.QUALIFICATION_ID", "CASCADE_QUALIFICATION.ID");
        descriptor.addMapping(qualificationMapping);
        
        return descriptor;
    }
    
    public ClassDescriptor buildLicenseDescriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(License.class);
        descriptor.addTableName("CASCADE_LICENSE");
        descriptor.addPrimaryKeyFieldName("CASCADE_LICENSE.ID");
        
        // Descriptor Properties.
        descriptor.useSoftCacheWeakIdentityMap();
        descriptor.setIdentityMapSize(100);
        descriptor.useRemoteSoftCacheWeakIdentityMap();
        descriptor.setRemoteIdentityMapSize(100);
        descriptor.setSequenceNumberFieldName("CASCADE_LICENSE.ID");
        descriptor.setSequenceNumberName("CASCADE_LICENSE_SEQ");
        descriptor.setAlias("License");
        
        // Query Manager.
        descriptor.getQueryManager().checkCacheForDoesExist();
        descriptor.getQueryManager().setQueryTimeout(0);
        
        // Mappings.
        DirectToFieldMapping idMapping = new DirectToFieldMapping();
        idMapping.setAttributeName("id");
        idMapping.setFieldName("CASCADE_LICENSE.ID");
        descriptor.addMapping(idMapping);
        
        DirectToFieldMapping classMapping = new DirectToFieldMapping();
        classMapping.setAttributeName("licenseClass");
        classMapping.setFieldName("CASCADE_LICENSE.LICENSE_CLASS");
        descriptor.addMapping(classMapping);
        
        return descriptor;
    }
    
    public ClassDescriptor buildQualificationDescriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(Qualification.class);
        descriptor.addTableName("CASCADE_QUALIFICATION");
        descriptor.addPrimaryKeyFieldName("CASCADE_QUALIFICATION.ID");
        
        // Descriptor Properties.
        descriptor.useSoftCacheWeakIdentityMap();
        descriptor.setIdentityMapSize(100);
        descriptor.useRemoteSoftCacheWeakIdentityMap();
        descriptor.setRemoteIdentityMapSize(100);
        descriptor.setSequenceNumberFieldName("CASCADE_QUALIFICATION.ID");
        descriptor.setSequenceNumberName("CASCADE_QUALIFICATION_SEQ");
        descriptor.setAlias("Qualification");
        
        VersionLockingPolicy lockingPolicy = new VersionLockingPolicy();
        lockingPolicy.setWriteLockFieldName("CASCADE_QUALIFICATION.VERSION");
        descriptor.setOptimisticLockingPolicy(lockingPolicy);
        
        // Query Manager.
        descriptor.getQueryManager().checkCacheForDoesExist();
        descriptor.getQueryManager().setQueryTimeout(0);
        
        // Mappings.
        DirectToFieldMapping idMapping = new DirectToFieldMapping();
        idMapping.setAttributeName("id");
        idMapping.setFieldName("CASCADE_QUALIFICATION.ID");
        descriptor.addMapping(idMapping);
        
        DirectToFieldMapping yearsOfExperienceMapping = new DirectToFieldMapping();
        yearsOfExperienceMapping.setAttributeName("yearsOfExperience");
        yearsOfExperienceMapping.setFieldName("CASCADE_QUALIFICATION.YEARS");
        yearsOfExperienceMapping.setNullValue("");
        descriptor.addMapping(yearsOfExperienceMapping);
        
        DirectToFieldMapping versionMapping = new DirectToFieldMapping();
        versionMapping.setAttributeName("version");
        versionMapping.setFieldName("CASCADE_QUALIFICATION.VERSION");
        versionMapping.setIsReadOnly(true);
        descriptor.addMapping(versionMapping);
        
        OneToManyMapping awardsMapping = new OneToManyMapping();
        awardsMapping.setAttributeName("awards");
        awardsMapping.setReferenceClass(Award.class);
        awardsMapping.useBasicIndirection();
        awardsMapping.privateOwnedRelationship();
        awardsMapping.addTargetForeignKeyFieldName("CASCADE_AWARD.QUALIFICATION_ID", "CASCADE_QUALIFICATION.ID");
        descriptor.addMapping(awardsMapping);
        
        return descriptor;
    }
}
