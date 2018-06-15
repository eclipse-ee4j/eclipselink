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
//     05/29/2008-1.0M8 Andrei Ilitchev.
//       - New file introduced to consolidate Oracle-specific JPA tests.
package org.eclipse.persistence.testing.tests;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.persistence.testing.tests.jpa.jgeometry.SpatialJPQLTestSuite;
import org.eclipse.persistence.testing.tests.jpa.structconverter.StructConverterTestSuite;

public class OracleJPASpatialTestSuite extends TestSuite{

    public static Test suite() {
        TestSuite fullSuite = new TestSuite();
        fullSuite.setName("OracleJPASpatialTestSuite");

        fullSuite.addTest(StructConverterTestSuite.suite());
        fullSuite.addTest(SpatialJPQLTestSuite.suite());
        return fullSuite;
    }
}
