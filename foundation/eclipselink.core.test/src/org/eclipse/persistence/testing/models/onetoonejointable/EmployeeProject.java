/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     07/16/2009 Andrei Ilitchev 
 *       - Bug 282553: JPA 2.0 JoinTable support for OneToOne and ManyToOne
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.onetoonejointable;

import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.descriptors.*;
import org.eclipse.persistence.mappings.*;
import org.eclipse.persistence.mappings.converters.*;

public class EmployeeProject extends org.eclipse.persistence.sessions.Project {
    public EmployeeProject() {
        // OTOJT stands for OneToOneJoinTable
        setName("OTOJT_Employee");
        setDatasourceLogin(new DatabaseLogin());

        addDescriptor(buildAddressDescriptor());
        addDescriptor(buildChildDescriptor());
        addDescriptor(buildEmployeeDescriptor());
        addDescriptor(buildLargeProjectDescriptor());
        addDescriptor(buildProjectDescriptor());
        addDescriptor(buildSmallProjectDescriptor());
    }

    public ClassDescriptor buildAddressDescriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(Address.class);
        descriptor.addTableName("OTOJT_ADDRESS");
        descriptor.addPrimaryKeyFieldName("OTOJT_ADDRESS.ADDRESS_ID");

        // Descriptor Properties.
        descriptor.useSoftCacheWeakIdentityMap();
        descriptor.setIdentityMapSize(100);
        descriptor.setSequenceNumberFieldName("OTOJT_ADDRESS.ADDRESS_ID");
        descriptor.setSequenceNumberName("OTOJT_ADDRESS_SEQ");
        descriptor.setAlias("OTOJT_Address");

        // Cache Invalidation Policy
        // Query Manager.
        descriptor.getQueryManager().checkCacheForDoesExist();

        // Named Queries.
        // Event Manager.
        // Mappings.
        DirectToFieldMapping cityMapping = new DirectToFieldMapping();
        cityMapping.setAttributeName("city");
        cityMapping.setFieldName("OTOJT_ADDRESS.CITY");
        descriptor.addMapping(cityMapping);

        DirectToFieldMapping countryMapping = new DirectToFieldMapping();
        countryMapping.setAttributeName("country");
        countryMapping.setFieldName("OTOJT_ADDRESS.COUNTRY");
        descriptor.addMapping(countryMapping);

        DirectToFieldMapping idMapping = new DirectToFieldMapping();
        idMapping.setAttributeName("id");
        idMapping.setFieldName("OTOJT_ADDRESS.ADDRESS_ID");
        descriptor.addMapping(idMapping);

        DirectToFieldMapping postalCodeMapping = new DirectToFieldMapping();
        postalCodeMapping.setAttributeName("postalCode");
        postalCodeMapping.setFieldName("OTOJT_ADDRESS.P_CODE");
        descriptor.addMapping(postalCodeMapping);

        DirectToFieldMapping provinceMapping = new DirectToFieldMapping();
        provinceMapping.setAttributeName("province");
        provinceMapping.setFieldName("OTOJT_ADDRESS.PROVINCE");
        descriptor.addMapping(provinceMapping);

        DirectToFieldMapping streetMapping = new DirectToFieldMapping();
        streetMapping.setAttributeName("street");
        streetMapping.setFieldName("OTOJT_ADDRESS.STREET");
        descriptor.addMapping(streetMapping);

        return descriptor;
    }
    
    public ClassDescriptor buildChildDescriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(Child.class);
        descriptor.addTableName("OTOJT_CHILD");
        descriptor.addPrimaryKeyFieldName("OTOJT_CHILD.CHILD_ID");
        
        descriptor.useSoftCacheWeakIdentityMap();
        descriptor.setIdentityMapSize(50);
        descriptor.setSequenceNumberFieldName("OTOJT_CHILD.CHILD_ID");
        descriptor.setSequenceNumberName("OTOJT_CHILD_SEQ");
        descriptor.setAlias("OTOJT_Child");
        
        // Query Manager.
        descriptor.getQueryManager().checkCacheForDoesExist();
        
        // Mappings.
        DirectToFieldMapping firstNameMapping = new DirectToFieldMapping();
        firstNameMapping.setAttributeName("firstName");
        firstNameMapping.setFieldName("F_NAME");
        firstNameMapping.setNullValue("");
        descriptor.addMapping(firstNameMapping);

        DirectToFieldMapping idMapping = new DirectToFieldMapping();
        idMapping.setAttributeName("id");
        idMapping.setFieldName("CHILD_ID");
        descriptor.addMapping(idMapping);

        DirectToFieldMapping lastNameMapping = new DirectToFieldMapping();
        lastNameMapping.setAttributeName("lastName");
        lastNameMapping.setFieldName("L_NAME");
        lastNameMapping.setNullValue("");
        descriptor.addMapping(lastNameMapping);
        
        DirectToFieldMapping genderMapping = new DirectToFieldMapping();
        genderMapping.setAttributeName("gender");
        genderMapping.setFieldName("GENDER");
        ObjectTypeConverter genderMappingConverter = new ObjectTypeConverter();
        genderMappingConverter.addConversionValue("F", "Female");
        genderMappingConverter.addConversionValue("M", "Male");
        genderMapping.setConverter(genderMappingConverter);
        descriptor.addMapping(genderMapping);
        
        DirectToFieldMapping birthdayMapping = new DirectToFieldMapping();
        birthdayMapping.setAttributeName("birthday");
        birthdayMapping.setFieldName("BIRTHDAY");
        descriptor.addMapping(birthdayMapping);
        
        OneToOneMapping parentMapping = new OneToOneMapping();
        parentMapping.setAttributeName("parent");
        parentMapping.setReferenceClass(Employee.class);
        parentMapping.dontUseIndirection();
        parentMapping.setRelationTableMechanism(new RelationTableMechanism());
        parentMapping.getRelationTableMechanism().setRelationTableName("OTOJT_CHILD_PARENT");
        parentMapping.getRelationTableMechanism().addSourceRelationKeyFieldName("OTOJT_CHILD_PARENT.CHILD_ID", "OTOJT_CHILD.CHILD_ID");
        parentMapping.getRelationTableMechanism().addTargetRelationKeyFieldName("OTOJT_CHILD_PARENT.EMP_ID", "OTOJT_EMPLOYEE.EMP_ID");
//        parentMapping.addForeignKeyFieldName("OTOJT_CHILD.PARENT_EMP_ID", "OTOJT_EMPLOYEE.EMP_ID");
        parentMapping.readOnly();
        descriptor.addMapping(parentMapping);
        
        
        return descriptor;        
    }

    public ClassDescriptor buildEmployeeDescriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(Employee.class);
        descriptor.addTableName("OTOJT_EMPLOYEE");
        descriptor.addTableName("OTOJT_SALARY");
        descriptor.addPrimaryKeyFieldName("OTOJT_EMPLOYEE.EMP_ID");

        // Descriptor Properties.
        descriptor.useSoftCacheWeakIdentityMap();
        descriptor.setIdentityMapSize(100);
        descriptor.setSequenceNumberFieldName("OTOJT_EMPLOYEE.EMP_ID");
        descriptor.setSequenceNumberName("OTOJT_EMP_SEQ");
        VersionLockingPolicy lockingPolicy = new VersionLockingPolicy();
        lockingPolicy.setWriteLockFieldName("OTOJT_EMPLOYEE.VERSION");
        descriptor.setOptimisticLockingPolicy(lockingPolicy);
        descriptor.setAlias("OTOJT_Employee");

        // Cache Invalidation Policy
        // Query Manager.
        descriptor.getQueryManager().checkCacheForDoesExist();

        // Named Queries.	
        // Event Manager.
        // Mappings.
        DirectToFieldMapping firstNameMapping = new DirectToFieldMapping();
        firstNameMapping.setAttributeName("firstName");
        firstNameMapping.setFieldName("OTOJT_EMPLOYEE.F_NAME");
        firstNameMapping.setNullValue("");
        descriptor.addMapping(firstNameMapping);

        DirectToFieldMapping idMapping = new DirectToFieldMapping();
        idMapping.setAttributeName("id");
        idMapping.setFieldName("OTOJT_EMPLOYEE.EMP_ID");
        descriptor.addMapping(idMapping);

        DirectToFieldMapping lastNameMapping = new DirectToFieldMapping();
        lastNameMapping.setAttributeName("lastName");
        lastNameMapping.setFieldName("OTOJT_EMPLOYEE.L_NAME");
        lastNameMapping.setNullValue("");
        descriptor.addMapping(lastNameMapping);

        DirectToFieldMapping salaryMapping = new DirectToFieldMapping();
        salaryMapping.setAttributeName("salary");
        salaryMapping.setFieldName("OTOJT_SALARY.SALARY");
        descriptor.addMapping(salaryMapping);

        DirectToFieldMapping genderMapping = new DirectToFieldMapping();
        genderMapping.setAttributeName("gender");
        genderMapping.setFieldName("OTOJT_EMPLOYEE.GENDER");
        ObjectTypeConverter genderMappingConverter = new ObjectTypeConverter();
        genderMappingConverter.addConversionValue("F", "Female");
        genderMappingConverter.addConversionValue("M", "Male");
        genderMapping.setConverter(genderMappingConverter);
        descriptor.addMapping(genderMapping);

        DirectCollectionMapping responsibilitiesListMapping = new DirectCollectionMapping();
        responsibilitiesListMapping.setAttributeName("responsibilitiesList");
        responsibilitiesListMapping.useTransparentList();
        responsibilitiesListMapping.setReferenceTableName("OTOJT_RESPONS");
        responsibilitiesListMapping.setDirectFieldName("OTOJT_RESPONS.DESCRIP");
        responsibilitiesListMapping.addReferenceKeyFieldName("OTOJT_RESPONS.EMP_ID", "OTOJT_EMPLOYEE.EMP_ID");
        descriptor.addMapping(responsibilitiesListMapping);

        OneToOneMapping addressMapping = new OneToOneMapping();
        addressMapping.setAttributeName("address");
        addressMapping.setReferenceClass(Address.class);
        addressMapping.useBasicIndirection();
        addressMapping.privateOwnedRelationship();
//        addressMapping.addForeignKeyFieldName("OTOJT_EMPLOYEE.ADDR_ID", "OTOJT_ADDRESS.ADDRESS_ID");
        addressMapping.setRelationTableMechanism(new RelationTableMechanism());
        addressMapping.getRelationTableMechanism().setRelationTableName("OTOJT_EMP_ADDRESS");
        addressMapping.getRelationTableMechanism().addSourceRelationKeyFieldName("OTOJT_EMP_ADDRESS.EMP_ID", "OTOJT_EMPLOYEE.EMP_ID");
        addressMapping.getRelationTableMechanism().addTargetRelationKeyFieldName("OTOJT_EMP_ADDRESS.ADDR_ID", "OTOJT_ADDRESS.ADDRESS_ID");
        descriptor.addMapping(addressMapping);

        //Joel:EJBQLTesting 
        OneToOneMapping managerMapping = new OneToOneMapping();
        managerMapping.setAttributeName("manager");
        managerMapping.setReferenceClass(Employee.class);
        managerMapping.useBasicIndirection();
//        managerMapping.addForeignKeyFieldName("OTOJT_EMPLOYEE.MANAGER_ID", "OTOJT_EMPLOYEE.EMP_ID");
        managerMapping.setRelationTableMechanism(new RelationTableMechanism());
        managerMapping.getRelationTableMechanism().setRelationTableName("OTOJT_EMP_MANAGER");
        managerMapping.getRelationTableMechanism().addSourceRelationKeyFieldName("OTOJT_EMP_MANAGER.EMP_ID", "OTOJT_EMPLOYEE.EMP_ID");
        managerMapping.getRelationTableMechanism().addTargetRelationKeyFieldName("OTOJT_EMP_MANAGER.MANAGER_ID", "OTOJT_EMPLOYEE.EMP_ID");
        descriptor.addMapping(managerMapping);

//        OneToManyMapping managedEmployeesMapping = new OneToManyMapping();
        ManyToManyMapping managedEmployeesMapping = new ManyToManyMapping();
        managedEmployeesMapping.setAttributeName("managedEmployees");
        managedEmployeesMapping.setReferenceClass(Employee.class);
        managedEmployeesMapping.useTransparentList();
//        managedEmployeesMapping.addTargetForeignKeyFieldName("OTOJT_EMPLOYEE.MANAGER_ID", "OTOJT_EMPLOYEE.EMP_ID");
        managedEmployeesMapping.setRelationTableName("OTOJT_EMP_MANAGER");
        managedEmployeesMapping.addSourceRelationKeyFieldName("OTOJT_EMP_MANAGER.MANAGER_ID", "OTOJT_EMPLOYEE.EMP_ID");
        managedEmployeesMapping.addTargetRelationKeyFieldName("OTOJT_EMP_MANAGER.EMP_ID", "OTOJT_EMPLOYEE.EMP_ID");
        managedEmployeesMapping.readOnly();
        descriptor.addMapping(managedEmployeesMapping);
        
//        OneToManyMapping childrenMapping = new OneToManyMapping();
        ManyToManyMapping childrenMapping = new ManyToManyMapping();
        childrenMapping.setAttributeName("children");
        childrenMapping.setReferenceClass(Child.class);
        childrenMapping.addAscendingOrdering("birthday");
        childrenMapping.useTransparentList();
        childrenMapping.privateOwnedRelationship();
//        childrenMapping.addTargetForeignKeyFieldName("OTOJT_CHILD.PARENT_EMP_ID", "OTOJT_EMPLOYEE.EMP_ID");
        childrenMapping.setRelationTableName("OTOJT_CHILD_PARENT");
        childrenMapping.addSourceRelationKeyFieldName("OTOJT_CHILD_PARENT.EMP_ID", "OTOJT_EMPLOYEE.EMP_ID");
        childrenMapping.addTargetRelationKeyFieldName("OTOJT_CHILD_PARENT.CHILD_ID", "OTOJT_CHILD.CHILD_ID");
        descriptor.addMapping(childrenMapping);

        ManyToManyMapping projectsMapping = new ManyToManyMapping();
        projectsMapping.setAttributeName("projects");
        projectsMapping.setReferenceClass(Project.class);
        projectsMapping.useTransparentList();
        projectsMapping.setRelationTableName("OTOJT_PROJ_EMP");
        projectsMapping.addSourceRelationKeyFieldName("OTOJT_PROJ_EMP.EMP_ID", "OTOJT_EMPLOYEE.EMP_ID");
        projectsMapping.addTargetRelationKeyFieldName("OTOJT_PROJ_EMP.PROJ_ID", "OTOJT_PROJECT.PROJ_ID");
        descriptor.addMapping(projectsMapping);

        OneToOneMapping projectLedMapping = new OneToOneMapping();
        projectLedMapping.setAttributeName("projectLed");
        projectLedMapping.setReferenceClass(Project.class);
        projectLedMapping.useBasicIndirection();
        projectLedMapping.setRelationTableMechanism(new RelationTableMechanism());
        projectLedMapping.getRelationTableMechanism().setRelationTableName("OTOJT_PROJ_LEADER");
        projectLedMapping.getRelationTableMechanism().addSourceRelationKeyFieldName("OTOJT_PROJ_LEADER.EMP_ID", "OTOJT_EMPLOYEE.EMP_ID");
        projectLedMapping.getRelationTableMechanism().addTargetRelationKeyFieldName("OTOJT_PROJ_LEADER.PROJ_ID", "PROJ_ID");
        projectLedMapping.readOnly();
        descriptor.addMapping(projectLedMapping);

        return descriptor;
    }

    public ClassDescriptor buildLargeProjectDescriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(LargeProject.class);
        descriptor.addTableName("OTOJT_LPROJECT");

        // Inheritance Properties.
        descriptor.getInheritancePolicy().setParentClass(Project.class);
        descriptor.getInheritancePolicy().dontReadSubclassesOnQueries();

        // Descriptor Properties.
        descriptor.setAlias("OTOJT_LargeProject");

        // Cache Invalidation Policy
        // Query Manager.
        descriptor.getQueryManager().checkCacheForDoesExist();

        // Named Queries.	
        // Event Manager.
        // Mappings.
        DirectToFieldMapping budgetMapping = new DirectToFieldMapping();
        budgetMapping.setAttributeName("budget");
        budgetMapping.setFieldName("OTOJT_LPROJECT.BUDGET");
        descriptor.addMapping(budgetMapping);

        DirectToFieldMapping milestoneVersionMapping = new DirectToFieldMapping();
        milestoneVersionMapping.setAttributeName("milestoneVersion");
        milestoneVersionMapping.setFieldName("OTOJT_LPROJECT.MILESTONE");
        descriptor.addMapping(milestoneVersionMapping);

        return descriptor;
    }

    public ClassDescriptor buildProjectDescriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(Project.class);
        descriptor.addTableName("OTOJT_PROJECT");
        descriptor.addPrimaryKeyFieldName("OTOJT_PROJECT.PROJ_ID");

        // Inheritance Properties.
        descriptor.getInheritancePolicy().setClassIndicatorFieldName("OTOJT_PROJECT.PROJ_TYPE");
        descriptor.getInheritancePolicy().addClassIndicator(SmallProject.class, "S");
        descriptor.getInheritancePolicy().addClassIndicator(LargeProject.class, "L");

        // Descriptor Properties.
        descriptor.useSoftCacheWeakIdentityMap();
        descriptor.setIdentityMapSize(100);
        descriptor.setSequenceNumberFieldName("OTOJT_PROJECT.PROJ_ID");
        descriptor.setSequenceNumberName("OTOJT_PROJ_SEQ");
        VersionLockingPolicy lockingPolicy = new VersionLockingPolicy();
        lockingPolicy.setWriteLockFieldName("OTOJT_PROJECT.VERSION");
        descriptor.setOptimisticLockingPolicy(lockingPolicy);
        descriptor.setAlias("OTOJT_Project");

        // Cache Invalidation Policy
        // Query Manager.
        descriptor.getQueryManager().checkCacheForDoesExist();

        // Named Queries.	
        // Event Manager.
        // Mappings.
        DirectToFieldMapping descriptionMapping = new DirectToFieldMapping();
        descriptionMapping.setAttributeName("description");
        descriptionMapping.setFieldName("OTOJT_PROJECT.DESCRIP");
        descriptionMapping.setNullValue("");
        descriptor.addMapping(descriptionMapping);

        DirectToFieldMapping idMapping = new DirectToFieldMapping();
        idMapping.setAttributeName("id");
        idMapping.setFieldName("OTOJT_PROJECT.PROJ_ID");
        descriptor.addMapping(idMapping);

        DirectToFieldMapping nameMapping = new DirectToFieldMapping();
        nameMapping.setAttributeName("name");
        nameMapping.setFieldName("OTOJT_PROJECT.PROJ_NAME");
        nameMapping.setNullValue("");
        descriptor.addMapping(nameMapping);

        OneToOneMapping teamLeaderMapping = new OneToOneMapping();
        teamLeaderMapping.setAttributeName("teamLeader");
        teamLeaderMapping.setReferenceClass(Employee.class);
        teamLeaderMapping.useBasicIndirection();
//        teamLeaderMapping.addForeignKeyFieldName("OTOJT_PROJECT.LEADER_ID", "OTOJT_EMPLOYEE.EMP_ID");
        teamLeaderMapping.setRelationTableMechanism(new RelationTableMechanism());
        teamLeaderMapping.getRelationTableMechanism().setRelationTableName("OTOJT_PROJ_LEADER");
        teamLeaderMapping.getRelationTableMechanism().addSourceRelationKeyFieldName("OTOJT_PROJ_LEADER.PROJ_ID", "PROJ_ID");
        teamLeaderMapping.getRelationTableMechanism().addTargetRelationKeyFieldName("OTOJT_PROJ_LEADER.EMP_ID", "OTOJT_EMPLOYEE.EMP_ID");
        descriptor.addMapping(teamLeaderMapping);

        ManyToManyMapping employeesMapping = new ManyToManyMapping();
        employeesMapping.setAttributeName("employees");
        employeesMapping.setReferenceClass(Employee.class);
        employeesMapping.readOnly();
        employeesMapping.useTransparentList();
        employeesMapping.setRelationTableName("OTOJT_PROJ_EMP");
        employeesMapping.addSourceRelationKeyFieldName("OTOJT_PROJ_EMP.PROJ_ID", "PROJ_ID");
        employeesMapping.addTargetRelationKeyFieldName("OTOJT_PROJ_EMP.EMP_ID", "OTOJT_EMPLOYEE.EMP_ID");
        descriptor.addMapping(employeesMapping);

        return descriptor;
    }

    public ClassDescriptor buildSmallProjectDescriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(SmallProject.class);
        descriptor.addTableName("OTOJT_PROJECT");

        // Inheritance Properties.
        descriptor.getInheritancePolicy().setParentClass(Project.class);
        descriptor.getInheritancePolicy().dontReadSubclassesOnQueries();

        // Descriptor Properties.
        descriptor.setAlias("OTOJT_SmallProject");

        // Cache Invalidation Policy
        // Query Manager.
        descriptor.getQueryManager().checkCacheForDoesExist();

        // Named Queries.	
        // Event Manager.
        return descriptor;
    }
}