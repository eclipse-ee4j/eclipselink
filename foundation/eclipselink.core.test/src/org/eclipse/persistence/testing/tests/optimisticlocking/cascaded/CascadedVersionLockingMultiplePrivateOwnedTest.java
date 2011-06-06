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
 *     dminsky - initial implementation
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.optimisticlocking.cascaded;

import org.eclipse.persistence.testing.framework.TestCase;

import org.eclipse.persistence.testing.models.employee.relational.EmployeeProject;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.models.employee.domain.PhoneNumber;
import org.eclipse.persistence.testing.models.employee.domain.Child;
import org.eclipse.persistence.testing.models.employee.domain.Project;

import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.VersionLockingPolicy;
import org.eclipse.persistence.mappings.ForeignReferenceMapping;

/**
 * Project --\
 *            ---> Employee --> PhoneNumber
 * Child ----/
 * 
 * EL bug 342632
 */
public class CascadedVersionLockingMultiplePrivateOwnedTest extends TestCase {
    
    protected EmployeeProject project;
    protected ClassDescriptor employeeDescriptor;
    protected ClassDescriptor projectDescriptor;
    protected ClassDescriptor childDescriptor;
    protected ClassDescriptor phoneNumberDescriptor;
    protected DatabaseSession customSession;
    
    public CascadedVersionLockingMultiplePrivateOwnedTest() {
        setDescription("Test the correct number of CascadeLockingPolicy objects are created for multiple private ownership");
    }
    
    @Override
    public void setup() {
        // Customise an EmployeeProject with private-ownership, version locking, and cascade locking
        project = new EmployeeProject();
        
        employeeDescriptor = project.getDescriptor(Employee.class);
        projectDescriptor = project.getDescriptor(Project.class);
        childDescriptor = project.getDescriptor(Child.class);
        phoneNumberDescriptor = project.getDescriptor(PhoneNumber.class);
        
        ClassDescriptor[] descriptors = new ClassDescriptor[] { 
            employeeDescriptor, projectDescriptor, childDescriptor, phoneNumberDescriptor };
        
        for (ClassDescriptor descriptor : descriptors) {
            // configure version locking
            VersionLockingPolicy lockingPolicy = new VersionLockingPolicy();
            String versionFieldName = descriptor.getTableName() + ".VERSION";
            lockingPolicy.setWriteLockFieldName(versionFieldName);
            lockingPolicy.storeInCache();
            descriptor.setOptimisticLockingPolicy(lockingPolicy);
        }
        
        // cascade Project and Child descriptors, referencing the Employee Descriptor
        ((VersionLockingPolicy)projectDescriptor.getOptimisticLockingPolicy()).setIsCascaded(true);
        ((VersionLockingPolicy)childDescriptor.getOptimisticLockingPolicy()).setIsCascaded(true);
        
        // configure private ownership
        ForeignReferenceMapping childrenMapping = (ForeignReferenceMapping) employeeDescriptor.getMappingForAttributeName("children");
        childrenMapping.setIsPrivateOwned(false); // Configure false for this specific scenario, Normally Employee->Child is P-O
        
        ForeignReferenceMapping teamLeaderMapping = (ForeignReferenceMapping) projectDescriptor.getMappingForAttributeName("teamLeader");
        teamLeaderMapping.setIsPrivateOwned(true); // referencing Employee P-O
        
        ForeignReferenceMapping parentMapping = (ForeignReferenceMapping) childDescriptor.getMappingForAttributeName("parent");
        parentMapping.setIsPrivateOwned(true); // referencing Employee P-O
        
        ForeignReferenceMapping phoneNumbersMapping = (ForeignReferenceMapping) employeeDescriptor.getMappingForAttributeName("phoneNumbers");
        phoneNumbersMapping.setIsPrivateOwned(true); // referencing PhoneNumber P-O
        
        // use a new session 
        customSession = project.createDatabaseSession();
        customSession.setLogin(getSession().getLogin().clone());
    }
    
    @Override
    public void test() {
        customSession.login();
    }
    
    @Override
    public void verify() {
        // project and child should have no cascade locking policies
        assertEquals("Project should have 0 policies", 0, projectDescriptor.getCascadeLockingPolicies().size());
        assertEquals("Child should have 0 policies", 0, childDescriptor.getCascadeLockingPolicies().size());
        // employee should have two policies (1 from employee, 1 from child)
        assertEquals("Employee should have 2 policies", 2, employeeDescriptor.getCascadeLockingPolicies().size());
        // phoneNumber should have one policy (1 from employee)
        // (in the original bug, this was the issue that was reported)
        assertEquals("PhoneNumber should have 1 policy", 1, phoneNumberDescriptor.getCascadeLockingPolicies().size());
    }
    
    @Override
    public void reset() {
        project = null;
        projectDescriptor = null;
        childDescriptor = null;
        employeeDescriptor = null;
        phoneNumberDescriptor = null;
        if (customSession != null && customSession.isConnected()) {
            customSession.logout();
        }
        customSession = null;
    }

}
