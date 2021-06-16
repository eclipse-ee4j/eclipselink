/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     05/29/2008-1.0M8 Andrei Ilitchev.
//       - New file introduced to consolidate Oracle-specific JPA tests.
package org.eclipse.persistence.testing.tests;

import junit.framework.TestSuite;
import junit.framework.Test;

import org.eclipse.persistence.testing.tests.jpa.customfeatures.CustomFeaturesJUnitTestSuite;
import org.eclipse.persistence.testing.tests.jpa.dcn.DCNTestSuite;
import org.eclipse.persistence.testing.tests.jpa.proxyauthentication.ProxyAuthenticationTestSuite;
import org.eclipse.persistence.testing.tests.jpa.timestamptz.TimeStampTZJUnitTestSuite;

public class OracleJPATestSuite extends TestSuite{

    public static Test suite() {
        TestSuite fullSuite = new TestSuite();
        fullSuite.setName("OracleJPATestSuite");

        fullSuite.addTest(ProxyAuthenticationTestSuite.suite());
        fullSuite.addTest(CustomFeaturesJUnitTestSuite.suite());
        fullSuite.addTest(TimeStampTZJUnitTestSuite.suite());
        fullSuite.addTest(DCNTestSuite.suite());
        return fullSuite;
    }
}
