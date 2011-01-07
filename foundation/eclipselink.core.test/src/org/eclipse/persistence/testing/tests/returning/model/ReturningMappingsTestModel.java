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
package org.eclipse.persistence.testing.tests.returning.model;

import org.eclipse.persistence.testing.framework.TestModel;
import org.eclipse.persistence.testing.framework.TestSuite;
import org.eclipse.persistence.testing.framework.TestWarningException;

public class ReturningMappingsTestModel extends TestModel {
    ReturnObjectControl returnObjectControl;

    public ReturningMappingsTestModel() {
        setDescription("This model tests ReturningPolicy with various mapping.");
    }

    public ReturningMappingsTestModel(ReturnObjectControl returnObjectControl) {
        this();
        this.returnObjectControl = returnObjectControl;
    }

    public void addRequiredSystems() {
        if (!getSession().getPlatform().isOracle()) {
            throw new TestWarningException("ReturningMappingsTestModel runs on Oracle only.");
        }
        addRequiredSystem(new ReturningTestSystem());
    }

    public void addTests() {
        addTest(getTestSuite());
    }

    protected TestSuite getTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("Mappings Test Suite");
        suite.addTest(new ReturningInsertTestCase(new Class1(), false, returnObjectControl));
        suite.addTest(new ReturningInsertTestCase(new Class1(), true, returnObjectControl));
        suite.addTest(new ReturningInsertTestCase(new Class1(2, 1, new Class2(2, 1)), false, returnObjectControl));
        suite.addTest(new ReturningInsertTestCase(new Class1(2, 1, new Class2(2, 1)), true, returnObjectControl));

        suite.addTest(new ReturningUpdateTestCase(new Class1(), new Class1(2, 1, new Class2(2, 1)), false, returnObjectControl));
        suite.addTest(new ReturningUpdateTestCase(new Class1(), new Class1(2, 1, new Class2(2, 1)), true, returnObjectControl));
        suite.addTest(new ReturningUpdateTestCase(new Class1(2, 1, new Class2(2, 1)), new Class1(), false, returnObjectControl));
        suite.addTest(new ReturningUpdateTestCase(new Class1(2, 1, new Class2(2, 1)), new Class1(), true, returnObjectControl));

        return suite;
    }
}
