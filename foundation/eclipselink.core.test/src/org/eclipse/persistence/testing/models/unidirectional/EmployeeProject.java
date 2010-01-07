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
 *     ailitchev - Uni-directional OneToMany
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.unidirectional;

import org.eclipse.persistence.descriptors.*;
import org.eclipse.persistence.mappings.*;

/**
 * 
 */
public class EmployeeProject extends org.eclipse.persistence.sessions.Project {
    public static boolean ownerIdIsInPK = false;
    public EmployeeProject() {
        setName("UnidirectionalEmployee");

        addDescriptor(buildEmployeeDescriptor());
        addDescriptor(buildPhoneNumberDescriptor());
    }

    public ClassDescriptor buildEmployeeDescriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(org.eclipse.persistence.testing.models.unidirectional.Employee.class);
        descriptor.addTableName("UNIDIR_EMPLOYEE");
        descriptor.addPrimaryKeyFieldName("UNIDIR_EMPLOYEE.EMP_ID");

        // Descriptor Properties.
        descriptor.useSoftCacheWeakIdentityMap();
        descriptor.setIdentityMapSize(100);
        descriptor.setSequenceNumberFieldName("UNIDIR_EMPLOYEE.EMP_ID");
        descriptor.setSequenceNumberName("UNIDIR_EMP_SEQ");
        VersionLockingPolicy lockingPolicy = new VersionLockingPolicy();
        lockingPolicy.setWriteLockFieldName("UNIDIR_EMPLOYEE.VERSION");
        //**temp_begin
        lockingPolicy.setIsCascaded(true);
        //**temp_end
        descriptor.setOptimisticLockingPolicy(lockingPolicy);
        descriptor.setAlias("UnidirectionalEmployee");

        // Cache Invalidation Policy
        // Query Manager.
        descriptor.getQueryManager().checkCacheForDoesExist();

        // Named Queries.   
        // Event Manager.
        // Mappings.
        DirectToFieldMapping firstNameMapping = new DirectToFieldMapping();
        firstNameMapping.setAttributeName("firstName");
        firstNameMapping.setFieldName("UNIDIR_EMPLOYEE.F_NAME");
        firstNameMapping.setNullValue("");
        descriptor.addMapping(firstNameMapping);

        DirectToFieldMapping idMapping = new DirectToFieldMapping();
        idMapping.setAttributeName("id");
        idMapping.setFieldName("UNIDIR_EMPLOYEE.EMP_ID");
        descriptor.addMapping(idMapping);

        DirectToFieldMapping lastNameMapping = new DirectToFieldMapping();
        lastNameMapping.setAttributeName("lastName");
        lastNameMapping.setFieldName("UNIDIR_EMPLOYEE.L_NAME");
        lastNameMapping.setNullValue("");
        descriptor.addMapping(lastNameMapping);

        UnidirectionalOneToManyMapping managedEmployeesMapping = new UnidirectionalOneToManyMapping();
        managedEmployeesMapping.setAttributeName("managedEmployees");
        managedEmployeesMapping.setReferenceClass(org.eclipse.persistence.testing.models.unidirectional.Employee.class);
        managedEmployeesMapping.useBasicIndirection();
        managedEmployeesMapping.addTargetForeignKeyFieldName("UNIDIR_EMPLOYEE.MANAGER_ID", "UNIDIR_EMPLOYEE.EMP_ID");
        descriptor.addMapping(managedEmployeesMapping);

        UnidirectionalOneToManyMapping phoneNumbersMapping = new UnidirectionalOneToManyMapping();
        phoneNumbersMapping.setAttributeName("phoneNumbers");
        phoneNumbersMapping.setReferenceClass(org.eclipse.persistence.testing.models.unidirectional.PhoneNumber.class);
        phoneNumbersMapping.useBasicIndirection();
        phoneNumbersMapping.privateOwnedRelationship();
        phoneNumbersMapping.addTargetForeignKeyFieldName("UNIDIR_PHONE.EMP_ID", "UNIDIR_EMPLOYEE.EMP_ID");
        descriptor.addMapping(phoneNumbersMapping);

        return descriptor;
    }

    public ClassDescriptor buildPhoneNumberDescriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(org.eclipse.persistence.testing.models.unidirectional.PhoneNumber.class);
        descriptor.addTableName("UNIDIR_PHONE");
        if(ownerIdIsInPK) {
            descriptor.addPrimaryKeyFieldName("UNIDIR_PHONE.EMP_ID");
            descriptor.addPrimaryKeyFieldName("UNIDIR_PHONE.TYPE");
        } else {
            descriptor.addPrimaryKeyFieldName("UNIDIR_PHONE.AREA_CODE");
            descriptor.addPrimaryKeyFieldName("UNIDIR_PHONE.P_NUMBER");
        }

        // Descriptor Properties.
        descriptor.useSoftCacheWeakIdentityMap();
        descriptor.setIdentityMapSize(100);
        descriptor.setAlias("PhoneNumber");

        // Cache Invalidation Policy
        // Query Manager.
        descriptor.getQueryManager().checkCacheForDoesExist();

        // Event Manager.
        // Query keys.
        // Mappings.
        if(ownerIdIsInPK) {
            DirectToFieldMapping ownerIdMapping = new DirectToFieldMapping();
            ownerIdMapping.setAttributeName("ownerId");
            ownerIdMapping.setFieldName("UNIDIR_PHONE.EMP_ID");
            descriptor.addMapping(ownerIdMapping);
        }
        
        DirectToFieldMapping areaCodeMapping = new DirectToFieldMapping();
        areaCodeMapping.setAttributeName("areaCode");
        areaCodeMapping.setFieldName("UNIDIR_PHONE.AREA_CODE");
        descriptor.addMapping(areaCodeMapping);

        DirectToFieldMapping numberMapping = new DirectToFieldMapping();
        numberMapping.setAttributeName("number");
        numberMapping.setFieldName("UNIDIR_PHONE.P_NUMBER");
        descriptor.addMapping(numberMapping);

        DirectToFieldMapping typeMapping = new DirectToFieldMapping();
        typeMapping.setAttributeName("type");
        typeMapping.setFieldName("UNIDIR_PHONE.TYPE");
        descriptor.addMapping(typeMapping);

        return descriptor;
    }
}
