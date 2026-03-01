/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests;

import junit.framework.Test;
import org.eclipse.persistence.testing.framework.TestModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class OracleTestModel extends TestModel {
    protected List testList;

    public OracleTestModel() {
        setName("OracleTestModel");
        setDescription("This model runs all of the Oracle specific tests.");
        addTests();
    }

    @Override
    public void addTests() {
        if (!getTests().isEmpty()) {
            return;
        }
        List tests = new ArrayList();

        tests.add("org.eclipse.persistence.testing.tests.conversion.ConversionManagerOracleTestModel");
        // disabled due to javax.jms vs jakarta.jms conflict:
        // java.lang.ClassCastException: oracle.jms.AQjmsTopicConnectionFactory cannot be cast to jakarta.jms.TopicConnectionFactory
        // at org.eclipse.persistence.sessions.coordination.jms.JMSPublishingTransportManager.getTopicConnectionFactory(JMSPublishingTransportManager.java:191)
//        tests.add("org.eclipse.persistence.testing.tests.distributedservers.rcm.jms.JMSRCMDistributedServersModel");
        tests.add("org.eclipse.persistence.testing.tests.dbchangenotification.DbChangeNotificationTestModel");
        tests.add("org.eclipse.persistence.testing.tests.lob.LOBTestModel");
        tests.add("org.eclipse.persistence.testing.tests.lob.LOBSessionBrokerTestModel");
        tests.add("org.eclipse.persistence.testing.tests.nchar.NcharTestModel");
        tests.add("org.eclipse.persistence.testing.tests.queries.oracle.OracleSpecificTestModel");
        tests.add("org.eclipse.persistence.testing.tests.proxyauthentication.thin.ProxyAuthenticationCustomizerTestModel");
        tests.add("org.eclipse.persistence.testing.tests.types.OracleTIMESTAMPTypeTestModelWithAccessors");
        tests.add("org.eclipse.persistence.testing.tests.types.OracleTIMESTAMPTypeTestModelWithOutAccessors");
        tests.add("org.eclipse.persistence.testing.tests.unwrappedconnection.UnwrapConnectionNchartTestModel");
        tests.add("org.eclipse.persistence.testing.tests.unwrappedconnection.UnwrapConnectionLOBTestModel");
        tests.add("org.eclipse.persistence.testing.tests.unwrappedconnection.UnwrapConnectionNativeBatchWritingTestModel");
        tests.add("org.eclipse.persistence.testing.tests.unwrappedconnection.UnwrapConnectionTypeTestModel");
        tests.add("org.eclipse.persistence.testing.tests.unwrappedconnection.UnwrapConnectionTypeWithoutAccessorsTestModel");
        tests.add("org.eclipse.persistence.testing.tests.unwrappedconnection.UnwrapConnectionCustomSQLTestModel");
        tests.add("org.eclipse.persistence.testing.tests.unwrappedconnection.UnwrapConnectionInsuranceObjectRelationalTestModel");

        tests.add("org.eclipse.persistence.testing.tests.flashback.FlashbackTestModel");
        tests.add("org.eclipse.persistence.testing.tests.returning.ReturningPolicyTestModel");

        for (Object test : tests) {
            try {
                addTest((TestModel) Class.forName((String) test).getConstructor().newInstance());
            } catch (Throwable exception) {
                System.out.println("Failed to set up " + test + " \n" + exception);
            }
        }

        // Sort the tests alphabetically.
        Collections.sort(this.getTests(), new Comparator<Test>() {
                @Override
                public int compare(Test left, Test right) {
                    return left.getClass().getSimpleName().compareTo(right.getClass().getSimpleName());
                }
            }
        );
        testList = tests;
    }

    /**
     * Return the JUnit suite to allow JUnit runner to find it.
     */
    public static junit.framework.TestSuite suite() {
        return buildOracleTestModel();
    }

    public static OracleTestModel buildOracleTestModel() {
        OracleTestModel model = new OracleTestModel();
        return model;
    }
}
