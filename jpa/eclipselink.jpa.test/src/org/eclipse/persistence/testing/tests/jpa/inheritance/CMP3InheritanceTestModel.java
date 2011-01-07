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
package org.eclipse.persistence.testing.tests.jpa.inheritance;

import org.eclipse.persistence.testing.framework.JUnitTestCase;
import org.eclipse.persistence.testing.framework.TestSuite;
import org.eclipse.persistence.testing.tests.jpa.CMP3TestModel;
import org.eclipse.persistence.testing.models.jpa.inheritance.InheritanceTableCreator;

/**
 * <p><b>Purpose</b>: To collect the tests that will test specifics of our
 * EJB3.0 implementation through the use of the EntityContainer.  In order for
 * this test model to work correctly the EntityContainer must be initialized
 * thought the comandline agent.
 */
public class CMP3InheritanceTestModel extends CMP3TestModel{
    public void setup(){
        super.setup();
   		new InheritanceTableCreator().replaceTables(getServerSession());
    }

    public void addTests() {
        addTest(new MultipleTableInheritanceCreateTest());

        TestSuite tests = new TestSuite();
        tests.setName("ReportQueryMultipleReturnInheritanceTestSuite");
        tests.addTests(JUnitTestCase.suite(ReportQueryMultipleReturnInheritanceTestSuite.class));
        addTest(tests);
        tests = new TestSuite();
        tests.setName("JoinedAttributeInheritanceJunitTest");
        tests.addTests(JUnitTestCase.suite(JoinedAttributeInheritanceJunitTest.class));
        addTest(tests);
    }
}
