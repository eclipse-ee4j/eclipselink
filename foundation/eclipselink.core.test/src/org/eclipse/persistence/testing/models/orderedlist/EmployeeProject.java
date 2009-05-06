/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     05/05/2009 Andrei Ilitchev 
 *       - JPA 2.0 - OrderedList support.
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.orderedlist;

import java.util.ArrayList;
import java.util.Vector;

import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.testing.models.orderedlist.EmployeeSystem.ChangeTracking;
import org.eclipse.persistence.testing.models.orderedlist.EmployeeSystem.JoinFetchOrBatchRead;
import org.eclipse.persistence.descriptors.*;
import org.eclipse.persistence.mappings.*;

public class EmployeeProject extends org.eclipse.persistence.sessions.Project {

    boolean useListOrderField;
    boolean useIndirection;
    ChangeTracking changeTracking;
    JoinFetchOrBatchRead joinFetchOrBatchRead;
    
    public EmployeeProject(boolean useListOrderField, boolean useIndirection, ChangeTracking changeTracking, JoinFetchOrBatchRead joinFetchOrBatchRead) {
        this.useListOrderField = useListOrderField;
        this.useIndirection = useIndirection;
        this.changeTracking = changeTracking;
        this.joinFetchOrBatchRead = joinFetchOrBatchRead;
        
        setName("OL_Employee");
        setDatasourceLogin(new DatabaseLogin());

        addDescriptor(buildChildDescriptor());
        addDescriptor(buildEmployeeDescriptor());
        addDescriptor(buildLargeProjectDescriptor());
        // currently attribute change tracking is incompatible with AggregateCollectionMapping
        if(changeTracking != ChangeTracking.ATTRIBUTE) {
            addDescriptor(buildPhoneNumberDescriptor());
        }
        addDescriptor(buildProjectDescriptor());
        addDescriptor(buildSmallProjectDescriptor());
    }

    public ClassDescriptor buildChildDescriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(Child.class);
        descriptor.addTableName("OL_CHILD");
        descriptor.addPrimaryKeyFieldName("CHILD_ID");
        
        descriptor.useSoftCacheWeakIdentityMap();
        descriptor.setIdentityMapSize(50);
        descriptor.setSequenceNumberFieldName("CHILD_ID");
        descriptor.setSequenceNumberName("OL_CHILD_SEQ");
        descriptor.setAlias("OL_Child");
        
        // Query Manager.
        descriptor.getQueryManager().checkCacheForDoesExist();
        
        // Mappings.
        DirectToFieldMapping firstNameMapping = new DirectToFieldMapping();
        firstNameMapping.setAttributeName("firstName");
        firstNameMapping.setFieldName("F_NAME");
        firstNameMapping.setNullValue("");
        descriptor.addMapping(firstNameMapping);

        DirectToFieldMapping lastNameMapping = new DirectToFieldMapping();
        lastNameMapping.setAttributeName("lastName");
        lastNameMapping.setFieldName("L_NAME");
        lastNameMapping.setNullValue("");
        descriptor.addMapping(lastNameMapping);

        DirectToFieldMapping idMapping = new DirectToFieldMapping();
        idMapping.setAttributeName("id");
        idMapping.setFieldName("CHILD_ID");
        descriptor.addMapping(idMapping);
        
        return descriptor;        
    }

    public ClassDescriptor buildEmployeeDescriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(Employee.class);
        descriptor.addTableName("OL_EMPLOYEE");
        descriptor.addPrimaryKeyFieldName("OL_EMPLOYEE.EMP_ID");

        // Descriptor Properties.
        descriptor.useSoftCacheWeakIdentityMap();
        descriptor.setIdentityMapSize(100);
        descriptor.setSequenceNumberFieldName("EMP_ID");
        descriptor.setSequenceNumberName("OL_EMP_SEQ");
        VersionLockingPolicy lockingPolicy = new VersionLockingPolicy();
        lockingPolicy.setWriteLockFieldName("VERSION");
        descriptor.setOptimisticLockingPolicy(lockingPolicy);
        descriptor.setAlias("OL_Employee");

        // Cache Invalidation Policy
        // Query Manager.
        descriptor.getQueryManager().checkCacheForDoesExist();

        // Named Queries.   
        // Event Manager.
        // Mappings.
        DirectToFieldMapping firstNameMapping = new DirectToFieldMapping();
        firstNameMapping.setAttributeName("firstName");
        firstNameMapping.setFieldName("F_NAME");
        firstNameMapping.setNullValue("");
        descriptor.addMapping(firstNameMapping);

        DirectToFieldMapping idMapping = new DirectToFieldMapping();
        idMapping.setAttributeName("id");
        idMapping.setFieldName("EMP_ID");
        descriptor.addMapping(idMapping);

        DirectToFieldMapping lastNameMapping = new DirectToFieldMapping();
        lastNameMapping.setAttributeName("lastName");
        lastNameMapping.setFieldName("L_NAME");
        lastNameMapping.setNullValue("");
        descriptor.addMapping(lastNameMapping);

        DirectCollectionMapping responsibilitiesListMapping = new DirectCollectionMapping();
        responsibilitiesListMapping.setAttributeName("responsibilitiesList");
        if(useListOrderField) {
            responsibilitiesListMapping.setListOrderFieldName("OL_RESPONS.RESPONS_ORDER");
        }
        if(useIndirection) {
            responsibilitiesListMapping.useTransparentList();
        } else {
            responsibilitiesListMapping.useCollectionClass(ArrayList.class);
            responsibilitiesListMapping.dontUseIndirection();
        }
        responsibilitiesListMapping.setReferenceTableName("OL_RESPONS");
        responsibilitiesListMapping.setDirectFieldName("OL_RESPONS.DESCRIP");
        responsibilitiesListMapping.addReferenceKeyFieldName("OL_RESPONS.EMP_ID", "EMP_ID");
        setJoinFetchOrBatchRead(responsibilitiesListMapping);
        descriptor.addMapping(responsibilitiesListMapping);

        OneToOneMapping managerMapping = new OneToOneMapping();
        managerMapping.setAttributeName("manager");
        managerMapping.setReferenceClass(Employee.class);
        managerMapping.useBasicIndirection();
        managerMapping.addForeignKeyFieldName("MANAGER_ID", "EMP_ID");
        descriptor.addMapping(managerMapping);

        OneToManyMapping managedEmployeesMapping = new OneToManyMapping();
        managedEmployeesMapping.setAttributeName("managedEmployees");
        managedEmployeesMapping.setReferenceClass(Employee.class);
        if(useListOrderField) {
            managedEmployeesMapping.setListOrderFieldName("OL_EMPLOYEE.MANAGED_ORDER");
        }
        if(useIndirection) {
            managedEmployeesMapping.useTransparentList();
        } else {
            managedEmployeesMapping.useCollectionClass(ArrayList.class);
            managedEmployeesMapping.dontUseIndirection();
        }
        managedEmployeesMapping.addTargetForeignKeyFieldName("MANAGER_ID", "EMP_ID");
        setJoinFetchOrBatchRead(managedEmployeesMapping);
        descriptor.addMapping(managedEmployeesMapping);
        
        OneToManyMapping childrenMapping = new UnidirectionalOneToManyMapping();
        childrenMapping.setAttributeName("children");
        childrenMapping.setReferenceClass(Child.class);
        if(useListOrderField) {
            childrenMapping.setListOrderFieldName("OL_CHILD.CHILDREN_ORDER");
        }
        if(useIndirection) {
            childrenMapping.useTransparentList();
        } else {
            childrenMapping.useCollectionClass(Vector.class);
            childrenMapping.dontUseIndirection();
        }
        childrenMapping.privateOwnedRelationship();
        childrenMapping.addTargetForeignKeyFieldName("OL_CHILD.PARENT_ID", "EMP_ID");
        setJoinFetchOrBatchRead(childrenMapping);
        descriptor.addMapping(childrenMapping);

        // currently attribute change tracking is incompatible with AggregateCollectionMapping
        if(changeTracking != ChangeTracking.ATTRIBUTE) {
            AggregateCollectionMapping phoneNumbersMapping = new AggregateCollectionMapping();
            phoneNumbersMapping.setAttributeName("phoneNumbers");
            phoneNumbersMapping.setReferenceClass(PhoneNumber.class);
            phoneNumbersMapping.addTargetForeignKeyFieldName("OL_PHONE.OWNER_ID", "OL_EMPLOYEE.EMP_ID");
            phoneNumbersMapping.addFieldNameTranslation("OL_PHONE.OWNER_ID", "OWNER_ID");
            phoneNumbersMapping.addFieldNameTranslation("OL_PHONE.AREA_CODE", "AREA_CODE");
            phoneNumbersMapping.addFieldNameTranslation("OL_PHONE.PNUMBER", "PNUMBER");
            phoneNumbersMapping.addFieldNameTranslation("OL_PHONE.TYPE", "TYPE");
            if(useListOrderField) {
                phoneNumbersMapping.setListOrderFieldName("OL_PHONE.PHONE_ORDER");
            }
            if(useIndirection) {
                phoneNumbersMapping.useTransparentList();
            } else {
                phoneNumbersMapping.useCollectionClass(ArrayList.class);
                phoneNumbersMapping.dontUseIndirection();
            }
            setJoinFetchOrBatchRead(phoneNumbersMapping);
            descriptor.addMapping(phoneNumbersMapping);
        }

        ManyToManyMapping projectsMapping = new ManyToManyMapping();
        projectsMapping.setAttributeName("projects");
        projectsMapping.setReferenceClass(Project.class);
        if(useListOrderField) {
            projectsMapping.setListOrderFieldName("OL_PROJ_EMP.PROJ_ORDER");
        }
        if(useIndirection) {
            projectsMapping.useTransparentList();
        } else {
            projectsMapping.useCollectionClass(ArrayList.class);
            projectsMapping.dontUseIndirection();
        }
        projectsMapping.setRelationTableName("OL_PROJ_EMP");
        projectsMapping.addSourceRelationKeyFieldName("OL_PROJ_EMP.EMP_ID", "EMP_ID");
        projectsMapping.addTargetRelationKeyFieldName("OL_PROJ_EMP.PROJ_ID", "OL_PROJECT.PROJ_ID");
        setJoinFetchOrBatchRead(projectsMapping);
        descriptor.addMapping(projectsMapping);

        OneToManyMapping projectsLedMapping = new OneToManyMapping();
        projectsLedMapping.setAttributeName("projectsLed");
        projectsLedMapping.setReferenceClass(Project.class);
        if(useListOrderField) {
            projectsLedMapping.setListOrderFieldName("OL_PROJECT.PROJECTS_LED_ORDER");
        }
        if(useIndirection) {
            projectsLedMapping.useTransparentList();
        } else {
            projectsLedMapping.useCollectionClass(ArrayList.class);
            projectsLedMapping.dontUseIndirection();
        }
        projectsLedMapping.addTargetForeignKeyFieldName("OL_PROJECT.LEADER_ID", "EMP_ID");
        setJoinFetchOrBatchRead(projectsLedMapping);
        descriptor.addMapping(projectsLedMapping);

        return descriptor;
    }

    public ClassDescriptor buildPhoneNumberDescriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(PhoneNumber.class);
        descriptor.addTableName("OL_AGGREGATE_PHONE");
//        descriptor.addPrimaryKeyFieldName("AREA_CODE");
//        descriptor.addPrimaryKeyFieldName("P_NUMBER");

        // Descriptor Properties.
        descriptor.descriptorIsAggregate();
//        descriptor.useSoftCacheWeakIdentityMap();
//        descriptor.setIdentityMapSize(100);
//        descriptor.setAlias("OL_PhoneNumber");

        // Cache Invalidation Policy
        // Query Manager.
//        descriptor.getQueryManager().checkCacheForDoesExist();

        // Mappings.
        DirectToFieldMapping areaCodeMapping = new DirectToFieldMapping();
        areaCodeMapping.setAttributeName("areaCode");
        areaCodeMapping.setFieldName("AREA_CODE");
        descriptor.addMapping(areaCodeMapping);

        DirectToFieldMapping numberMapping = new DirectToFieldMapping();
        numberMapping.setAttributeName("number");
        numberMapping.setFieldName("P_NUMBER");
        descriptor.addMapping(numberMapping);

        DirectToFieldMapping typeMapping = new DirectToFieldMapping();
        typeMapping.setAttributeName("type");
        typeMapping.setFieldName("TYPE");
        descriptor.addMapping(typeMapping);

/*        OneToOneMapping ownerMapping = new OneToOneMapping();
        ownerMapping.setAttributeName("owner");
        ownerMapping.setReferenceClass(Employee.class);
        ownerMapping.useBasicIndirection();
        ownerMapping.addForeignKeyFieldName("OWNER_ID", "OL_EMPLOYEE.EMP_ID");
        descriptor.addMapping(ownerMapping);*/

        return descriptor;
    }

    public ClassDescriptor buildProjectDescriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(Project.class);
        descriptor.addTableName("OL_PROJECT");
        descriptor.addPrimaryKeyFieldName("OL_PROJECT.PROJ_ID");

        // Inheritance Properties.
        descriptor.getInheritancePolicy().setClassIndicatorFieldName("PROJ_TYPE");
        descriptor.getInheritancePolicy().addClassIndicator(SmallProject.class, "S");
        descriptor.getInheritancePolicy().addClassIndicator(LargeProject.class, "L");

        // Descriptor Properties.
        descriptor.useSoftCacheWeakIdentityMap();
        descriptor.setIdentityMapSize(100);
        descriptor.setSequenceNumberFieldName("PROJ_ID");
        descriptor.setSequenceNumberName("OL_PROJ_SEQ");
        VersionLockingPolicy lockingPolicy = new VersionLockingPolicy();
        lockingPolicy.setWriteLockFieldName("VERSION");
        descriptor.setOptimisticLockingPolicy(lockingPolicy);
        descriptor.setAlias("OL_Project");

        // Cache Invalidation Policy
        // Query Manager.
        descriptor.getQueryManager().checkCacheForDoesExist();

        // Named Queries.   
        // Event Manager.
        // Mappings.
        DirectToFieldMapping descriptionMapping = new DirectToFieldMapping();
        descriptionMapping.setAttributeName("description");
        descriptionMapping.setFieldName("DESCRIP");
        descriptionMapping.setNullValue("");
        descriptor.addMapping(descriptionMapping);

        DirectToFieldMapping idMapping = new DirectToFieldMapping();
        idMapping.setAttributeName("id");
        idMapping.setFieldName("PROJ_ID");
        descriptor.addMapping(idMapping);

        DirectToFieldMapping nameMapping = new DirectToFieldMapping();
        nameMapping.setAttributeName("name");
        nameMapping.setFieldName("PROJ_NAME");
        nameMapping.setNullValue("");
        descriptor.addMapping(nameMapping);

        OneToOneMapping teamLeaderMapping = new OneToOneMapping();
        teamLeaderMapping.setAttributeName("teamLeader");
        teamLeaderMapping.setReferenceClass(Employee.class);
        teamLeaderMapping.useBasicIndirection();
        teamLeaderMapping.addForeignKeyFieldName("LEADER_ID", "OL_EMPLOYEE.EMP_ID");
        descriptor.addMapping(teamLeaderMapping);

        ManyToManyMapping employeesMapping = new ManyToManyMapping();
        employeesMapping.setAttributeName("employees");
        employeesMapping.setReferenceClass(Employee.class);
        if(useListOrderField) {
            employeesMapping.setListOrderFieldName("OL_PROJ_EMP.EMP_ORDER");
        }
        if(useIndirection) {
            employeesMapping.useTransparentList();
        } else {
            employeesMapping.useCollectionClass(ArrayList.class);
            employeesMapping.dontUseIndirection();
        }
        employeesMapping.readOnly();
        employeesMapping.setRelationTableName("OL_PROJ_EMP");
        employeesMapping.addSourceRelationKeyFieldName("OL_PROJ_EMP.EMP_ID", "PROJ_ID");
        employeesMapping.addTargetRelationKeyFieldName("OL_PROJ_EMP.PROJ_ID", "OL_EMPLOYEE.EMP_ID");
        descriptor.addMapping(employeesMapping);

        return descriptor;
    }

    public ClassDescriptor buildSmallProjectDescriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(SmallProject.class);

        // Inheritance Properties.
        descriptor.getInheritancePolicy().setParentClass(Project.class);
        descriptor.getInheritancePolicy().dontReadSubclassesOnQueries();

        // Descriptor Properties.
        descriptor.setAlias("OL_SmallProject");

        // Cache Invalidation Policy
        // Query Manager.
        descriptor.getQueryManager().checkCacheForDoesExist();

        // Named Queries.   
        // Event Manager.
        return descriptor;
    }
    
    public ClassDescriptor buildLargeProjectDescriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(LargeProject.class);
        descriptor.addTableName("OL_LPROJECT");

        // Inheritance Properties.
        descriptor.getInheritancePolicy().setParentClass(Project.class);
        descriptor.getInheritancePolicy().dontReadSubclassesOnQueries();

        // Descriptor Properties.
        descriptor.setAlias("OL_LargeProject");

        // Cache Invalidation Policy
        // Query Manager.
        descriptor.getQueryManager().checkCacheForDoesExist();

        // Named Queries.   
        // Event Manager.
        // Mappings.
        DirectToFieldMapping budgetMapping = new DirectToFieldMapping();
        budgetMapping.setAttributeName("budget");
        budgetMapping.setFieldName("OL_LPROJECT.BUDGET");
        descriptor.addMapping(budgetMapping);

        DirectToFieldMapping milestoneVersionMapping = new DirectToFieldMapping();
        milestoneVersionMapping.setAttributeName("milestoneVersion");
        milestoneVersionMapping.setFieldName("OL_LPROJECT.MILESTONE");
        descriptor.addMapping(milestoneVersionMapping);

        return descriptor;
    }
    
    void setJoinFetchOrBatchRead(ForeignReferenceMapping mapping) {
        if(joinFetchOrBatchRead == JoinFetchOrBatchRead.INNER_JOIN) {
            mapping.setJoinFetch(ForeignReferenceMapping.INNER_JOIN);
        } else if(joinFetchOrBatchRead == JoinFetchOrBatchRead.OUTER_JOIN) {
            mapping.setJoinFetch(ForeignReferenceMapping.OUTER_JOIN);
        } else if(joinFetchOrBatchRead == JoinFetchOrBatchRead.BATCH_READ) {
            mapping.setUsesBatchReading(true);
        }
    }
}
