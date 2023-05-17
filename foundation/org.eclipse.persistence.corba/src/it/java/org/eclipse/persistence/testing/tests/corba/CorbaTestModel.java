/*
 * Copyright (c) 2018, 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.persistence.testing.tests.corba;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.testing.framework.TestModel;
import org.eclipse.persistence.testing.framework.TestSuite;
import org.eclipse.persistence.testing.tests.securitycorba.SecurityWhileConvertingToMethodTest;
import org.eclipse.persistence.testing.tests.securitycorba.SecurityWhileConvertingToMethodTest.ConvertMethodAbstractSession;
import org.eclipse.persistence.testing.tests.securitycorba.SecurityWhileConvertingToMethodTest.ConvertMethodNoArg;
import org.eclipse.persistence.testing.tests.securitycorba.SecurityWhileConvertingToMethodTest.ConvertMethodSession;
import org.eclipse.persistence.testing.tests.sessionsxml.corba.SessionsXMLSchemaSunCORBATransportConfigTest;

public class CorbaTestModel extends TestModel {

    @Override
    public void addTests() {
        if (!getTests().isEmpty()) {
            return;
        }
        addTest(getValidationSecurityTestSuite());
        addTest(new SessionsXMLSchemaSunCORBATransportConfigTest());
        List<String> tests = new ArrayList<>();
        if (!"com.sun.enterprise.naming.SerialInitContextFactory".equals(System.getProperty("java.naming.factory.initial"))) {
            // Running this model on GlassFish/with GlassFish-Corba implementation is not supported
            // as it uses jndi lookup
            // see https://www.eclipse.org/eclipselink/documentation/2.7/solutions/scaling002.htm#TLADG1228
            tests.add("org.eclipse.persistence.testing.tests.remote.rmi.IIOP.RMIIIOPRemoteModel");
        }
        tests.add("org.eclipse.persistence.testing.tests.remote.suncorba.SunCORBARemoteModel");

        for (int index = 0; index < tests.size(); ++index) {
            try {
                addTest((TestModel) Class.forName(tests.get(index)).getConstructor().newInstance());
            } catch (Throwable exception) {
                System.out.println("Failed to set up " + tests.get(index) + " \n" + exception);
                // exception.printStackTrace();
            }
        }

        // Sort the tests alphabetically.
        Collections.sort(this.getTests(), new Comparator<Object>() {
            @Override
            public int compare(Object left, Object right) {
                return Helper.getShortClassName(left.getClass()).compareTo(Helper.getShortClassName(right.getClass()));
            }
        });
    }

    /**
     * Return the JUnit suite to allow JUnit runner to find it.
     */
    public static junit.framework.TestSuite suite() {
        CorbaTestModel model = new CorbaTestModel();
        model.setName("CorbaTestModel");
        model.setDescription("This model runs all of the CORBA tests.");
        model.addTests();

        return model;
    }

    public static TestSuite getValidationSecurityTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("Validation Security Tests");
        suite.setDescription("This suite includes Validation tests on security (Tests originally in validation model)");

        suite.addTest(new SecurityWhileConvertingToMethodTest(ConvertMethodNoArg.class));//ian added
        suite.addTest(new SecurityWhileConvertingToMethodTest(ConvertMethodSession.class));//ian added
        suite.addTest(new SecurityWhileConvertingToMethodTest(ConvertMethodAbstractSession.class));//ian added

        return suite;
    }

}
