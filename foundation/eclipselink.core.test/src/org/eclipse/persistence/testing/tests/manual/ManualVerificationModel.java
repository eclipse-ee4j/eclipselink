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
package org.eclipse.persistence.testing.tests.manual;

import org.eclipse.persistence.testing.tests.unitofwork.UnregisterUnitOfWorkTest;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.mapping.*;
import org.eclipse.persistence.testing.models.insurance.InsuranceSystem;
import org.eclipse.persistence.testing.models.inheritance.InheritanceSystem;
import org.eclipse.persistence.testing.models.readonly.ReadOnlySystem;
import org.eclipse.persistence.testing.models.employee.relational.EmployeeSystem;

public class ManualVerificationModel extends TestModel {
    public ManualVerificationModel() {
        setDescription("This model tests reading/writing/deleting of the complex aggregate model.");
    }

    public void addRequiredSystems() {
        addRequiredSystem(new MappingSystem());
        addRequiredSystem(new EmployeeSystem());
        addRequiredSystem(new InheritanceSystem());
        addRequiredSystem(new InsuranceSystem());
        addRequiredSystem(new ReadOnlySystem());
    }

    public void addTests() {
        addTest(getTestSuite());
    }

    public static TestSuite getTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("ManualVerificationTestSuite");
        suite.setDescription("This suite test runs various test cases which needs to be verified manually");

        suite.addTest(new NewObjectDeleteTest());
        suite.addTest(new BidirectionalUnitOfWorkManualTest());
        suite.addTest(new UnregisterUnitOfWorkTest());
        suite.addTest(new ReadToSeeMultipleIDsTest());
        suite.addTest(new ReadToSeeMultipleFieldsTest());
        suite.addTest(new BidirectionalMMDeleteTest());
        suite.addTest(new SQLLogToFileTest());
        suite.addTest(new CommitOrderTest());
        suite.addTest(new DeleteOrderUnitOfWorkTest());
        return suite;
    }
}
