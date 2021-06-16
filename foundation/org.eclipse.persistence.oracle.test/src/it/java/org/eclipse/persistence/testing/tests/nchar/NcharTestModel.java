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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.nchar;

import org.eclipse.persistence.testing.framework.TestModel;
import org.eclipse.persistence.testing.framework.TestSuite;
import org.eclipse.persistence.testing.framework.TestVariation;
import org.eclipse.persistence.testing.framework.TestWarningException;

// This is Oracle9 specific test model.
// It won't run on any platform other than Oracle.
// Moreover, this model is Oracle9 specific.
// In setup each test verifies if the current platform is an instance of
// Oracle9Platform, and will attempt to usePlatform(Oracle9Platform) if not
// (reset returns back the originally used DatabasePlatform).
public class NcharTestModel extends TestModel {
    public NcharTestModel() {
        setDescription("This model tests TopLink NCHAR, NVARCHAR2, NCLOB support with Oracle.");
    }

    public void addRequiredSystems() {
        if (!getSession().getPlatform().isOracle()) {
            throw new TestWarningException("WARNING: This model is not supposed to be run on databases other than Oracle.");
        }
        addRequiredSystem(new NcharTestSystem());
    }

    public void addTests() {
        addTest(getTestSuite());
    }

    protected TestSuite getTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("NCHAR Test Suite");
        suite.addTest(new ReadNcharTest());
        String str = "shouldBindAllParameters";
        Object obj = getSession().getPlatform();
        suite.addTests(TestVariation.get(obj, str, new InsertNullNcharTest()));
        suite.addTests(TestVariation.get(obj, str, new InsertNcharTest()));
        suite.addTests(TestVariation.get(obj, str, new UpdateNullNcharTest()));
        suite.addTests(TestVariation.get(obj, str, new UpdateNcharTest()));

        return suite;
    }
}
