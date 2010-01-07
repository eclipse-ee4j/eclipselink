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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.inheritance;


import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.descriptors.*;
import org.eclipse.persistence.mappings.*;

public class STI_EmployeeProject extends org.eclipse.persistence.sessions.Project {
    public STI_EmployeeProject() {
        setName("STI_Employee");
        applyLogin();

        addDescriptor(buildEmployeeDescriptor());
        addDescriptor(buildProjectDescriptor());
        addDescriptor(buildSmallProjectDescriptor());
        addDescriptor(buildLargeProjectDescriptor());
    }

    public void applyLogin() {
        DatabaseLogin login = new DatabaseLogin();
        login.usePlatform(new org.eclipse.persistence.platform.database.OraclePlatform());
        login.setDriverClassName("oracle.jdbc.OracleDriver");
        login.setConnectionString("jdbc:oracle:thin:@localhost:1521:orcl");
        login.setUserName("scott");
        login.setEncryptedPassword("3E20F8982C53F4ABA825E30206EC8ADE");

        // Configuration Properties.
        login.setShouldBindAllParameters(false);
        login.setShouldCacheAllStatements(false);
        login.setUsesByteArrayBinding(true);
        login.setUsesStringBinding(false);
        if (login.shouldUseByteArrayBinding()) {// Can only be used with binding.
            login.setUsesStreamsForBinding(false);
        }
        login.setShouldForceFieldNamesToUpperCase(false);
        login.setShouldOptimizeDataConversion(true);
        login.setShouldTrimStrings(true);
        login.setUsesExternalConnectionPooling(false);
        login.setUsesExternalTransactionController(false);

        setDatasourceLogin(login);
    }

    public ClassDescriptor buildEmployeeDescriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(STI_Employee.class);
        descriptor.addTableName("STI_EMPLOYEE");
        descriptor.addPrimaryKeyFieldName("STI_EMPLOYEE.EMP_ID");

        // Descriptor Properties.
        descriptor.useSoftCacheWeakIdentityMap();
        descriptor.setIdentityMapSize(100);
        descriptor.setSequenceNumberFieldName("STI_EMPLOYEE.EMP_ID");
        descriptor.setSequenceNumberName("STI_EMP_SEQ");
        VersionLockingPolicy lockingPolicy = new VersionLockingPolicy();
        lockingPolicy.setWriteLockFieldName("STI_EMPLOYEE.VERSION");
        descriptor.setOptimisticLockingPolicy(lockingPolicy);
        descriptor.setAlias("STI_Employee");

        // Cache Invalidation Policy
        // Query Manager.
        descriptor.getQueryManager().checkCacheForDoesExist();

        // Named Queries.	
        // Event Manager.
        // Mappings.
        DirectToFieldMapping firstNameMapping = new DirectToFieldMapping();
        firstNameMapping.setAttributeName("firstName");
        firstNameMapping.setFieldName("STI_EMPLOYEE.F_NAME");
        firstNameMapping.setNullValue("");
        descriptor.addMapping(firstNameMapping);

        DirectToFieldMapping idMapping = new DirectToFieldMapping();
        idMapping.setAttributeName("id");
        idMapping.setFieldName("STI_EMPLOYEE.EMP_ID");
        descriptor.addMapping(idMapping);

        DirectToFieldMapping lastNameMapping = new DirectToFieldMapping();
        lastNameMapping.setAttributeName("lastName");
        lastNameMapping.setFieldName("STI_EMPLOYEE.L_NAME");
        lastNameMapping.setNullValue("");
        descriptor.addMapping(lastNameMapping);

        //Joel:EJBQLTesting 
        OneToOneMapping managerMapping = new OneToOneMapping();
        managerMapping.setAttributeName("manager");
        managerMapping.setReferenceClass(STI_Employee.class);
        managerMapping.useBasicIndirection();
        managerMapping.addForeignKeyFieldName("STI_EMPLOYEE.MANAGER_ID", "STI_EMPLOYEE.EMP_ID");
        descriptor.addMapping(managerMapping);

        OneToManyMapping managedEmployeesMapping = new OneToManyMapping();
        managedEmployeesMapping.setAttributeName("managedEmployees");
        managedEmployeesMapping.setReferenceClass(STI_Employee.class);
        managedEmployeesMapping.useBasicIndirection();
        managedEmployeesMapping.addTargetForeignKeyFieldName("STI_EMPLOYEE.MANAGER_ID", "STI_EMPLOYEE.EMP_ID");
        descriptor.addMapping(managedEmployeesMapping);

        ManyToManyMapping projectsMapping = new ManyToManyMapping();
        projectsMapping.setAttributeName("projects");
        projectsMapping.setReferenceClass(STI_Project.class);
        projectsMapping.useBasicIndirection();
        projectsMapping.setRelationTableName("STI_PROJ_EMP");
        projectsMapping.addSourceRelationKeyFieldName("STI_PROJ_EMP.EMP_ID", "STI_EMPLOYEE.EMP_ID");
        projectsMapping.addTargetRelationKeyFieldName("STI_PROJ_EMP.PROJ_ID", "STI_PROJECT.PROJ_ID");
        descriptor.addMapping(projectsMapping);

        return descriptor;
    }

    public ClassDescriptor buildProjectDescriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(STI_Project.class);
        descriptor.addTableName("STI_PROJECT");
        descriptor.addPrimaryKeyFieldName("STI_PROJECT.PROJ_ID");

        // Inheritance Properties.
        descriptor.getInheritancePolicy().setClassIndicatorFieldName("STI_PROJECT.PROJ_TYPE");
        descriptor.getInheritancePolicy().addClassIndicator(STI_SmallProject.class, "S");
        descriptor.getInheritancePolicy().addClassIndicator(STI_LargeProject.class, "L");

        // Descriptor Properties.
        descriptor.useSoftCacheWeakIdentityMap();
        descriptor.setIdentityMapSize(100);
        descriptor.setSequenceNumberFieldName("STI_PROJECT.PROJ_ID");
        descriptor.setSequenceNumberName("STI_PROJ_SEQ");
        VersionLockingPolicy lockingPolicy = new VersionLockingPolicy();
        lockingPolicy.setWriteLockFieldName("STI_PROJECT.VERSION");
        descriptor.setOptimisticLockingPolicy(lockingPolicy);
        descriptor.setAlias("STI_Project");

        // Cache Invalidation Policy
        // Query Manager.
        descriptor.getQueryManager().checkCacheForDoesExist();

        // Named Queries.	
        // Event Manager.
        // Mappings.
        DirectToFieldMapping descriptionMapping = new DirectToFieldMapping();
        descriptionMapping.setAttributeName("description");
        descriptionMapping.setFieldName("STI_PROJECT.DESCRIP");
        descriptionMapping.setNullValue("");
        descriptor.addMapping(descriptionMapping);

        DirectToFieldMapping idMapping = new DirectToFieldMapping();
        idMapping.setAttributeName("id");
        idMapping.setFieldName("STI_PROJECT.PROJ_ID");
        descriptor.addMapping(idMapping);

        DirectToFieldMapping nameMapping = new DirectToFieldMapping();
        nameMapping.setAttributeName("name");
        nameMapping.setFieldName("STI_PROJECT.PROJ_NAME");
        nameMapping.setNullValue("");
        descriptor.addMapping(nameMapping);

        OneToOneMapping teamLeaderMapping = new OneToOneMapping();
        teamLeaderMapping.setAttributeName("teamLeader");
        teamLeaderMapping.setReferenceClass(STI_Employee.class);
        teamLeaderMapping.useBasicIndirection();
        teamLeaderMapping.addForeignKeyFieldName("STI_PROJECT.LEADER_ID", "STI_EMPLOYEE.EMP_ID");
        descriptor.addMapping(teamLeaderMapping);

        ManyToManyMapping teamMembersMapping = new ManyToManyMapping();
        teamMembersMapping.setAttributeName("teamMembers");
        teamMembersMapping.setReferenceClass(STI_Employee.class);
        teamMembersMapping.useBasicIndirection();
        teamMembersMapping.setRelationTableName("STI_PROJ_EMP");
        teamMembersMapping.addSourceRelationKeyFieldName("STI_PROJ_EMP.PROJ_ID", "STI_PROJECT.PROJ_ID");
        teamMembersMapping.addTargetRelationKeyFieldName("STI_PROJ_EMP.EMP_ID", "STI_EMPLOYEE.EMP_ID");
        teamMembersMapping.readOnly();
        descriptor.addMapping(teamMembersMapping);

        return descriptor;
    }

    public ClassDescriptor buildSmallProjectDescriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(STI_SmallProject.class);

        // Inheritance Properties.
        descriptor.getInheritancePolicy().setParentClass(STI_Project.class);
        descriptor.getInheritancePolicy().dontReadSubclassesOnQueries();

        // Descriptor Properties.
        descriptor.setAlias("STI_SmallProject");

        // Cache Invalidation Policy
        // Query Manager.
        descriptor.getQueryManager().checkCacheForDoesExist();

        // Named Queries.	
        // Event Manager.
        return descriptor;
    }

    public ClassDescriptor buildLargeProjectDescriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(STI_LargeProject.class);

        // Inheritance Properties.
        descriptor.getInheritancePolicy().setParentClass(STI_Project.class);
        descriptor.getInheritancePolicy().dontReadSubclassesOnQueries();

        // Descriptor Properties.
        descriptor.setAlias("STI_LargeProject");

        // Cache Invalidation Policy
        // Query Manager.
        descriptor.getQueryManager().checkCacheForDoesExist();

        // Named Queries.	
        // Event Manager.
        // Mappings.
        DirectToFieldMapping budgetMapping = new DirectToFieldMapping();
        budgetMapping.setAttributeName("budget");
        budgetMapping.setFieldName("STI_PROJECT.BUDGET");
        descriptor.addMapping(budgetMapping);

        return descriptor;
    }
}
