/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.unitofwork.changeflag;

import org.eclipse.persistence.testing.framework.TestModel;


/**
 * This test model is to test the use of ObjectChangeTrackingPolicy, AttributeChangeTrackingPolicy,.
 */
public class

EmployeeChangeTrackingTestModel extends TestModel {

    public EmployeeChangeTrackingTestModel() {
        setDescription("This model tests reading/writing/deleting using the employee demo with ObjectChangeTrackingPolicy flag.");
    }

    public void addTests() {
        addTest(new EmployeeChangeFlagBasicTestModel());
        addTest(new EmployeeAttributeChangeTrackingTestModel());
        addTest(new EmployeeHybridChangeTrackingTestModel());
        addTest(new TransparentIndirectionChangeFlagBasicTestModel());
    }
}
