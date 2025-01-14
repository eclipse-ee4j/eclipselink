/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 1998, 2025 IBM Corporation. All rights reserved.
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
//     06/16/2009-2.0 Guy Pelletier
//       - 277039: JPA 2.0 Cache Usage Settings
//     01/19/2010-2.1 Guy Pelletier
//       - 211322: Add fetch-group(s) support to the EclipseLink-ORM.XML Schema
//     03/23/2011-2.3 Guy Pelletier
//       - 337323: Multi-tenant with shared schema support (part 1)
//     06/25/2014-2.5.2 Rick Curtis
//       - 438177: Test M2M map
//     08/11/2014-2.5 Rick Curtis
//       - 440594: Tolerate invalid NamedQuery at EntityManager creation.
//     08/18/2014-2.5 Jody Grassel (IBM Corporation)
//       - 440802: xml-mapping-metadata-complete does not exclude @Entity annotated entities
package org.eclipse.persistence.testing.tests.jpa;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.testing.tests.jpa.config.ConfigPUTestSuite;
import org.eclipse.persistence.testing.tests.jpa.unit.IsolatedHashMapTest;

public class FullRegressionTestSuite extends TestSuite {

    public static Test suite() {
        TestSuite fullSuite = new TestSuite();
        fullSuite.setName("FullRegressionTestSuite");

        // Advanced model
        TestSuite suite = new TestSuite();
        suite.setName("advanced");
        suite.addTest(ConfigPUTestSuite.suite());
        fullSuite.addTest(suite);

        fullSuite.addTest(IsolatedHashMapTest.suite());
        // OSGi Deployment
        //try {
        //    fullSuite.addTestSuite(CompositeEnumerationTest.class);
        //} catch (Throwable ignore) {} // OSgi may not be on classpath.

        return fullSuite;
    }
}
