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
package org.eclipse.persistence.testing.tests.forceupdate;

import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.forceupdate.*;

public class FUVLTestModel extends TestModel {
    // No state.

    public FUVLTestModel() {
        setName("ForceUpdateVersionLockingTestModel");
        setDescription("This model tests Force Update Version Locking feature.");
    }

    public void setup() {
        try {
            Thread.sleep(1000);
        } catch (Exception ignore) {
        }
    }

    public void addRequiredSystems() {
        addRequiredSystem(new FUVLSystem());
    }

    public void addTests() {
        addTest(getFUVLTestSuite());
        addTest(getFUVLNopTestSuite());
    }

    public static TestSuite getFUVLTestSuite() {
        TestSuite suite;

        suite = new TestSuite();
        suite.setName("ForceUpdateVersionLockingTestSuite");
        suite.addTest(new FUVLTimestampLockInCacheTest(1));
        suite.addTest(new FUVLTimestampLockInCacheTest(2));
        suite.addTest(new FUVLTimestampLockInCacheTest(3));
        suite.addTest(new FUVLTimestampLockInCacheTest(4));
        suite.addTest(new FUVLTimestampLockInObjectTest(1));
        suite.addTest(new FUVLTimestampLockInObjectTest(2));
        suite.addTest(new FUVLTimestampLockInObjectTest(3));
        suite.addTest(new FUVLTimestampLockInObjectTest(4));
        suite.addTest(new FUVLVersionLockInCacheTest(1));
        suite.addTest(new FUVLVersionLockInCacheTest(2));
        suite.addTest(new FUVLVersionLockInCacheTest(3));
        suite.addTest(new FUVLVersionLockInCacheTest(4));
        suite.addTest(new FUVLVersionLockInObjectTest(1));
        suite.addTest(new FUVLVersionLockInObjectTest(2));
        suite.addTest(new FUVLVersionLockInObjectTest(3));
        suite.addTest(new FUVLVersionLockInObjectTest(4));

        return suite;
    }

    public static TestSuite getFUVLNopTestSuite() {
        TestSuite suite;

        suite = new TestSuite();
        suite.setName("ForceUpdateVersionLockingNopTestSuite");
        suite.addTest(new FUVLNopTimestampLockInCacheTest(1));
        suite.addTest(new FUVLNopTimestampLockInCacheTest(2));
        suite.addTest(new FUVLNopTimestampLockInCacheTest(3));
        suite.addTest(new FUVLNopTimestampLockInCacheTest(4));
        suite.addTest(new FUVLNopTimestampLockInObjectTest(1));
        suite.addTest(new FUVLNopTimestampLockInObjectTest(2));
        suite.addTest(new FUVLNopTimestampLockInObjectTest(3));
        suite.addTest(new FUVLNopTimestampLockInObjectTest(4));
        suite.addTest(new FUVLNopVersionLockInCacheTest(1));
        suite.addTest(new FUVLNopVersionLockInCacheTest(2));
        suite.addTest(new FUVLNopVersionLockInCacheTest(3));
        suite.addTest(new FUVLNopVersionLockInCacheTest(4));
        suite.addTest(new FUVLNopVersionLockInObjectTest(1));
        suite.addTest(new FUVLNopVersionLockInObjectTest(2));
        suite.addTest(new FUVLNopVersionLockInObjectTest(3));
        suite.addTest(new FUVLNopVersionLockInObjectTest(4));

        return suite;
    }

}
