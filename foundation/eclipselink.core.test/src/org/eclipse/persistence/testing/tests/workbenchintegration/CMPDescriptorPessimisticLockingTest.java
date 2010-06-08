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
package org.eclipse.persistence.testing.tests.workbenchintegration;

import org.eclipse.persistence.descriptors.CMPPolicy;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.PessimisticLockingPolicy;
import org.eclipse.persistence.queries.ObjectLevelReadQuery;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.employee.domain.Address;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.models.employee.relational.EmployeeProject;
import org.eclipse.persistence.sessions.factories.XMLProjectReader;
import org.eclipse.persistence.sessions.factories.XMLProjectWriter;


/** @author Steven Vo*/
public class

CMPDescriptorPessimisticLockingTest extends AutoVerifyTestCase {
    public static final String PROJECT_FILE = "CustomCMPDescriptorWithPessLockingProject.xml";

    public CMPDescriptorPessimisticLockingTest() {
        this.setDescription("Validate bean-level pessimistic locking configured with the CMPDescriptor");
    }

    protected void setup() {
        // Modify the employee project to use custom SQL queries
        EmployeeProject project = new EmployeeProject();
        ClassDescriptor employeeDescriptor = project.getDescriptors().get(Employee.class);
        ClassDescriptor addressDescriptor = project.getDescriptors().get(Address.class);

        employeeDescriptor.setCMPPolicy(new CMPPolicy());
        addressDescriptor.setCMPPolicy(new CMPPolicy());
        employeeDescriptor.getCMPPolicy().setPessimisticLockingPolicy(new PessimisticLockingPolicy());
        addressDescriptor.getCMPPolicy().setPessimisticLockingPolicy(new PessimisticLockingPolicy());

        employeeDescriptor.getCMPPolicy().getPessimisticLockingPolicy().setLockingMode(ObjectLevelReadQuery.LOCK);
        addressDescriptor.getCMPPolicy().getPessimisticLockingPolicy().setLockingMode(ObjectLevelReadQuery.LOCK_NOWAIT);

        // write project to an XML project file
        XMLProjectWriter.write(PROJECT_FILE, project);
    }

    protected void test() {
        // test run time project that should contains cusomtom sql queries
        Project project = XMLProjectReader.read(PROJECT_FILE, getClass().getClassLoader());
        ClassDescriptor employeeDescriptor = project.getDescriptors().get(Employee.class);
        ClassDescriptor addressDescriptor = project.getDescriptors().get(Address.class);

        if (!employeeDescriptor.hasPessimisticLockingPolicy()) {
            throw new TestErrorException("'Employee descriptor was not written or read correctly");
        } else if (employeeDescriptor.getCMPPolicy().getPessimisticLockingPolicy().getLockingMode() != 
                   ObjectLevelReadQuery.LOCK) {
            throw new TestErrorException("'Employee descriptor was not written or read correctly with pessimistic locking LOCK mode");
        }
        if (!addressDescriptor.hasPessimisticLockingPolicy()) {
            throw new TestErrorException("'Address descriptor was not written or read correctly");
        } else if (addressDescriptor.getCMPPolicy().getPessimisticLockingPolicy().getLockingMode() != 
                   ObjectLevelReadQuery.LOCK_NOWAIT) {
            throw new TestErrorException("'Address descriptor was not written or read correctly with pessimistic locking LOCK_NOWAIT mode");
        }
    }

    public static void main(String[] args) {
        try {
            CMPDescriptorPessimisticLockingTest test = new CMPDescriptorPessimisticLockingTest();
            test.setup();
            test.test();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
