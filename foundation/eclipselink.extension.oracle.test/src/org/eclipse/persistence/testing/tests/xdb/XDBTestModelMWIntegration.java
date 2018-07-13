/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.xdb;

import org.eclipse.persistence.platform.database.DatabasePlatform;
import org.eclipse.persistence.platform.database.OraclePlatform;
import org.eclipse.persistence.testing.framework.*;

public class XDBTestModelMWIntegration extends TestModel {
    DatabasePlatform oldPlatform = null;

    public XDBTestModelMWIntegration() {
        setDescription("Tests Oracle XDB Specific features");
    }

    public void addTests() {
        addTest(getXDBTestSuite());
    }

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
        return testSuite;
    }

    public void addForcedRequiredSystems() {
        addForcedRequiredSystem(new XMLTypeEmployeeSystemXML());
        oldPlatform = getSession().getPlatform();
        if (!(oldPlatform instanceof OraclePlatform)) {
            throw new TestProblemException("This model is intended for Oracle databases through OCI");
        }
        this.getSession().getLogin().setPlatform(new org.eclipse.persistence.platform.database.oracle.Oracle9Platform());
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
        XDBTestModelMWIntegration model = new XDBTestModelMWIntegration();
        model.addTests();
        return model;
    }
}
