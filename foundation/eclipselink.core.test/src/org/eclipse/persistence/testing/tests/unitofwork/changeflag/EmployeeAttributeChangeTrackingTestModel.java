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
package org.eclipse.persistence.testing.tests.unitofwork.changeflag;

import org.eclipse.persistence.descriptors.changetracking.AttributeChangeTrackingPolicy;
import org.eclipse.persistence.testing.framework.TestSuite;
import org.eclipse.persistence.testing.models.collections.Restaurant;
import org.eclipse.persistence.testing.models.employee.domain.Address;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.models.employee.domain.EmploymentPeriod;
import org.eclipse.persistence.testing.models.employee.domain.LargeProject;
import org.eclipse.persistence.testing.models.employee.domain.PhoneNumber;
import org.eclipse.persistence.testing.models.employee.domain.Project;
import org.eclipse.persistence.testing.models.employee.domain.SmallProject;
import org.eclipse.persistence.testing.tests.unitofwork.DeepMergeCloneSerializedObjectReferenceChangesTest;
import org.eclipse.persistence.testing.tests.unitofwork.DeepMergeCloneSerializedTest;
import org.eclipse.persistence.testing.tests.unitofwork.MultipleUnitOfWorkTest;
import org.eclipse.persistence.testing.tests.unitofwork.UnitOfWorkCommitResumeOnFailureNoFailureTest;
import org.eclipse.persistence.testing.tests.unitofwork.UnitOfWorkResumeOnFailureTest;
import org.eclipse.persistence.testing.tests.unitofwork.UnitOfWorkResumeTest;
import org.eclipse.persistence.testing.tests.unitofwork.UnitOfWorkRevertTest;
import org.eclipse.persistence.testing.tests.unitofwork.referencesettings.ChangeTrackedWeakReferenceTest;
import org.eclipse.persistence.testing.tests.unitofwork.referencesettings.DeferredChangeWeakReferenceTest;
import org.eclipse.persistence.testing.tests.unitofwork.referencesettings.ForceWeakReferenceTest;
import org.eclipse.persistence.testing.tests.unitofwork.referencesettings.HardReferenceTest;
import org.eclipse.persistence.testing.tests.unitofwork.referencesettings.WeakReferenceTest;
import org.eclipse.persistence.testing.tests.writing.ComplexUpdateTest;
import org.eclipse.persistence.testing.tests.writing.UpdateChangeNothingTest;
import org.eclipse.persistence.testing.tests.writing.UpdateChangeObjectTest;
import org.eclipse.persistence.testing.tests.writing.UpdateChangeValueTest;
import org.eclipse.persistence.testing.tests.writing.UpdateToNullTest;
import org.eclipse.persistence.tools.schemaframework.PopulationManager;


// This test model is used for attribute change tracking.  
public class EmployeeAttributeChangeTrackingTestModel extends EmployeeChangeFlagBasicTestModel {
    public EmployeeAttributeChangeTrackingTestModel() {
        setDescription("This model tests reading/writing/deleting using the employee demo with AttributeChangeTrackingPolicy flag.");
    }

    public void addRequiredSystems() {
        super.addRequiredSystems();
        addRequiredSystem(new ALCTEmployeeSystem());
    }

    public void setup() {
        // Save change policies for the all employee demo class in order to restore them at reset time.
        employeeChangePolicy = getSession().getDescriptor(Employee.class).getObjectChangePolicy();
        getSession().getDescriptor(Employee.class).setObjectChangePolicy(new AttributeChangeTrackingPolicy());

        addressChangePolicy = getSession().getDescriptor(Address.class).getObjectChangePolicy();
        getSession().getDescriptor(Address.class).setObjectChangePolicy(new AttributeChangeTrackingPolicy());

        projectChangePolicy = getSession().getDescriptor(Project.class).getObjectChangePolicy();
        getSession().getDescriptor(Project.class).setObjectChangePolicy(new AttributeChangeTrackingPolicy());

        smallProjectChangePolicy = getSession().getDescriptor(SmallProject.class).getObjectChangePolicy();
        getSession().getDescriptor(SmallProject.class).setObjectChangePolicy(new AttributeChangeTrackingPolicy());

        largeProjectChangePolicy = getSession().getDescriptor(LargeProject.class).getObjectChangePolicy();
        getSession().getDescriptor(LargeProject.class).setObjectChangePolicy(new AttributeChangeTrackingPolicy());

        employmentPeriodChangePolicy = getSession().getDescriptor(EmploymentPeriod.class).getObjectChangePolicy();
        getSession().getDescriptor(EmploymentPeriod.class).setObjectChangePolicy(new AttributeChangeTrackingPolicy());
        getSession().getDescriptor(Employee.class).getMappingForAttributeName("period").getReferenceDescriptor().setObjectChangePolicy(new AttributeChangeTrackingPolicy());

        phoneNumberChangePolicy = getSession().getDescriptor(PhoneNumber.class).getObjectChangePolicy();
        getSession().getDescriptor(PhoneNumber.class).setObjectChangePolicy(new AttributeChangeTrackingPolicy());

        restaurantChangePolicy = getSession().getDescriptor(Restaurant.class).getObjectChangePolicy();
        getSession().getDescriptor(Restaurant.class).setObjectChangePolicy(new AttributeChangeTrackingPolicy());
    }

    /**
     * Add a subset of the UnitOfWork tests in order to test the actual test flag functionality.
     */
    public TestSuite getUnitOfWorkTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("Unit Of Work Update Test Suite");
        suite.setDescription("This suite tests change flags for updates using UnitOfWork");
        PopulationManager manager = PopulationManager.getDefaultManager();
        Employee employee = (Employee)manager.getObject(Employee.class, "0001");

        // Tests with using unit of work.
        ComplexUpdateTest test = new UpdateToNullTest(employee);
        test.usesUnitOfWork = true;
        suite.addTest(test);

        test = new UpdateChangeValueTest(employee);
        test.usesUnitOfWork = true;
        suite.addTest(test);

        test = new UpdateChangeNothingTest(employee);
        test.usesUnitOfWork = true;
        suite.addTest(test);

        test = new UpdateChangeObjectTest(employee);
        test.usesUnitOfWork = true;
        suite.addTest(test);

        //suite.addTest(new NestedUnitOfWorkMultipleCommitTest(employee));
        suite.addTest(new MultipleUnitOfWorkTest(employee));
        suite.addTest(new UnitOfWorkResumeTest(employee));
        suite.addTest(new UnitOfWorkResumeOnFailureTest(employee));
        suite.addTest(new UnitOfWorkCommitResumeOnFailureNoFailureTest(employee));
        //suite.addTest(new UnitOfWorkCommitAndResume(employee));
        suite.addTest(new UnitOfWorkRevertTest(employee));
        suite.addTest(new ChangeFlagTest());
        //The test below should work, 
        //once AttributeChangeTrackingPolicy supports for aggregate, aggregate collection and direct collection.
        //suite.addTest(new ChangeEventTest());
        suite.addTest(new NestedUOWWithAttributeChangeTrackingTest(employee));
        suite.addTest(new WrongPropertyNameTest());

        suite.addTest(new DeepMergeCloneSerializedTest());
        suite.addTest(new DeepMergeCloneSerializedObjectReferenceChangesTest());
        suite.addTest(new TransparentMapTest());
        suite.addTest(new AggregateAttributeChangeTrackingTest());
        suite.addTest(new ChangeTrackedWeakReferenceTest());
        suite.addTest(new DeferredChangeWeakReferenceTest());
        suite.addTest(new ForceWeakReferenceTest());
        suite.addTest(new HardReferenceTest());
        suite.addTest(new WeakReferenceTest());

        return suite;
    }
}
