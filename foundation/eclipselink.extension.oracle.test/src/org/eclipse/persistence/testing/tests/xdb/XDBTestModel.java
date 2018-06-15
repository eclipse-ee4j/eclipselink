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
package org.eclipse.persistence.testing.tests.xdb;

import org.eclipse.persistence.platform.database.DatabasePlatform;
import org.eclipse.persistence.platform.database.OraclePlatform;
import org.eclipse.persistence.platform.database.oracle.Oracle12Platform;
import org.eclipse.persistence.testing.framework.TestModel;
import org.eclipse.persistence.testing.framework.TestProblemException;
import org.eclipse.persistence.testing.framework.TestSuite;

public class XDBTestModel extends TestModel {
    DatabasePlatform oldPlatform = null;

    public XDBTestModel() {
        setDescription("Tests Oracle XDB Specific features");
    }

    @Override
    public void addTests() {
        addTest(getXDBTestSuite());
    }

    @Override
    public void reset() {
        this.getSession().getLogin().setPlatform(oldPlatform);
        getDatabaseSession().logout();
        getDatabaseSession().login();
    }

    public static TestSuite getXDBTestSuite() {
        TestSuite testSuite = new TestSuite();
        testSuite.setName("XML Database Test Suite");

        testSuite.addTest(new ExistsNodeWithJoinTest());
        testSuite.addTest(new ExtractValueTest());
        testSuite.addTest(new GetNumberValTest());
        testSuite.addTest(new UpdateDocumentTest());
        testSuite.addTest(new ReportQueryTest());
        testSuite.addTest(new SessionBrokerTestCase());
        testSuite.addTest(new RemoteSessionTest());
        testSuite.addTest(new InsertWithNullTest());
        testSuite.addTest(new StoredFunctionXMLTypeTest());
        return testSuite;
    }

    @Override
    public void addForcedRequiredSystems() {
        addForcedRequiredSystem(new XMLTypeEmployeeSystem());
        oldPlatform = getSession().getPlatform();
        if (!(oldPlatform instanceof OraclePlatform)) {
            throw new TestProblemException("This model is intended for Oracle databases through OCI");
        }
        this.getSession().getLogin().setPlatform(new Oracle12Platform());
        getDatabaseSession().logout();
        getDatabaseSession().login();
        try {
            super.addForcedRequiredSystems();
        } catch (Exception ex) {
            String message = ex.getMessage();
            if ((message != null) && (message.indexOf("invalid datatype") > 0)) {
                throw new TestProblemException("This model will only work on Oracle 9i and up");
            } else {
                throw new TestProblemException("Error occurred", ex);
            }
        }
    }

    /**
     * Return the JUnit suite to allow JUnit runner to find it.
     */
    public static junit.framework.TestSuite suite() {
        XDBTestModel model = new XDBTestModel();
        model.addTests();
        return model;
    }
}
