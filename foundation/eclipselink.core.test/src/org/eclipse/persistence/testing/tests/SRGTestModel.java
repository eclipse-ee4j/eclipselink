/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.tests.employee.EmployeeBasicTestModel;
import org.eclipse.persistence.testing.tests.inheritance.InheritanceTestModel;

public class SRGTestModel extends TestModel {
    public SRGTestModel() {
        setDescription("This model is a basic set of tests run by developers before checking in code.");
        boolean isSRG = true;
        addTest(new org.eclipse.persistence.testing.tests.feature.FeatureTestModel(isSRG));
        addTest(new EmployeeBasicTestModel(isSRG));
        addTest(new org.eclipse.persistence.testing.tests.writing.ComplexUpdateAndUnitOfWorkTestModel(isSRG));
        addTest(new org.eclipse.persistence.testing.tests.sessionsxml.SessionsXMLBasicTestModel(isSRG));
        addTest(new org.eclipse.persistence.testing.tests.aggregate.AggregateTestModel(isSRG));
        addTest(new InheritanceTestModel(isSRG));
        addTest(new org.eclipse.persistence.testing.tests.sessionsxml.SessionsXMLTestModel(isSRG));
    }

    /**
     * Return the JUnit suite to allow JUnit runner to find it.
     * Unfortunately JUnit only allows suite methods to be static,
     * so it is not possible to generically do this.
     */
    public static junit.framework.TestSuite suite() {
        return new SRGTestModel();
    }
}
