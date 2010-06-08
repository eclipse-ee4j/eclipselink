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
package org.eclipse.persistence.testing.tests.proxyindirection;

import org.eclipse.persistence.testing.framework.TestModel;
import org.eclipse.persistence.testing.framework.TestSuite;

/**
 * Tests proxy indirection.
 */
public class ProxyIndirectionTestModel extends TestModel {
    public void addRequiredSystems() {
        addRequiredSystem(new ProxyIndirectionSystem());
    }

    public void addTests() {
        addTest(getReadTestSuite());
        addTest(getWriteTestSuite());
        addTest(getUnitOfWorkTestSuite());
        addTest(getDeleteTestSuite());
        addTest(getProxyObjectTestSuite());
    }

    public static TestSuite getDeleteTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("Delete Tests");
        suite.addTest(new DeleteTest());

        return suite;
    }

    public static TestSuite getReadTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("Read Tests");
        suite.addTest(new ReadWithExpressionTest());
        suite.addTest(new ReadAllTest());
        suite.addTest(new BatchReadTest());
        suite.addTest(new InterfaceProxyInvocationTest());
        suite.addTest(new ReadWithProxyEqualityExpression());
        suite.addTest(new InheritanceReadAllTest());
        return suite;
    }

    public static TestSuite getUnitOfWorkTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("UnitOfWork Tests");
        suite.addTest(new UnitOfWorkUpdateTest());
        suite.addTest(new UnitOfWorkUpdateWithNewObjectTest());
        suite.addTest(new UnitOfWorkCommitAndResume());
        suite.addTest(new UnitOfWorkUpdateFromCache());
        suite.addTest(new UnitOfWorkReplaceTest());

        return suite;
    }

    public static TestSuite getWriteTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("Write/Update Tests");
        suite.addTest(new InsertTest());
        suite.addTest(new UpdateTest());

        return suite;
    }

    public static TestSuite getProxyObjectTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("Proxy Object Tests");
        suite.addTest(new DeleteProxyObjectTest());
        suite.addTest(new DoesExistProxyObjectTest());
        suite.addTest(new ReadProxyObjectTest());
        suite.addTest(new UOWRegisterProxyObjectTest());

        return suite;
    }
}
