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
package org.eclipse.persistence.testing.tests.jpql;

import org.eclipse.persistence.testing.models.employee.domain.*;
import org.eclipse.persistence.testing.models.inheritance.InheritanceSystem;

/**
 * This model tests EJBQL usage from the query framework. * 
 */

public class JPQLTestModel extends org.eclipse.persistence.testing.framework.TestModel {
    public void addRequiredSystems() {
        addRequiredSystem(new JPQLSystem());
        addRequiredSystem(new InheritanceSystem());
    }

    public void addTests() {
        addTest(new JPQLUnitTestSuite());
        addTest(new JPQLSimpleTestSuite());
        // Run the simple test suite twice to test the EJBQL parse cache.
        addTest(new JPQLSimpleTestSuite());
        addTest(new JPQLComplexTestSuite());
        addTest(new JPQLSimpleSelectTestSuite());
        addTest(new JPQLComplexSelectTestSuite());
        addTest(new JPQLValidationTestSuite());
    }

    public void reset() {
        // Remove the Aliases added
        getAbstractSession().getAliasDescriptors().remove("Employee");
        getAbstractSession().getAliasDescriptors().remove("$Employee");
        getAbstractSession().getAliasDescriptors().remove("_Employee");
        getAbstractSession().getAliasDescriptors().remove("Address");
        getAbstractSession().getAliasDescriptors().remove("Phone");
        getAbstractSession().getAliasDescriptors().remove("Project");
        getAbstractSession().getAliasDescriptors().remove("SmallProject");
        getAbstractSession().getAliasDescriptors().remove("LargeProject");
        getAbstractSession().getAliasDescriptors().remove("EmploymentPeriod");
        //remove aliases for IBMPC for the Boolean (= TRUE) tests
        getAbstractSession().getAliasDescriptors().remove("IBMPC");
    }

    public void setup() {
        // Ensure all the aliases are added to the session.
        getAbstractSession().addAlias("Employee", getSession().getDescriptor(Employee.class));
        getAbstractSession().addAlias("$Employee", getSession().getDescriptor(Employee.class));
        getAbstractSession().addAlias("_Employee", getSession().getDescriptor(Employee.class));
        getAbstractSession().addAlias("Address", getSession().getDescriptor(Address.class));
        getAbstractSession().addAlias("EmploymentPeriod", getSession().getDescriptor(EmploymentPeriod.class));
        getAbstractSession().addAlias("Phone", getSession().getDescriptor(PhoneNumber.class));
        getAbstractSession().addAlias("Project", getSession().getDescriptor(Project.class));
        getAbstractSession().addAlias("LargeProject", getSession().getDescriptor(LargeProject.class));
        getAbstractSession().addAlias("SmallProject", getSession().getDescriptor(SmallProject.class));
        //add aliases for IBMPC for the Boolean (= TRUE) tests
        getAbstractSession().addAlias("IBMPC", getSession().getDescriptor(org.eclipse.persistence.testing.models.inheritance.IBMPC.class));
    }
}
