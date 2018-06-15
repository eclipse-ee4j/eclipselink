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
package org.eclipse.persistence.testing.tests;

import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.testing.framework.TestModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class OracleSpatialTestModel extends TestModel {
    protected List testList;

    public OracleSpatialTestModel() {
        setName("OracleSpatialTestModel");
        setDescription("This model runs all of the Oracle specific spatial tests.");
        addTests();
    }

    public void addTests() {
        if (!getTests().isEmpty()) {
            return;
        }
        List tests = new ArrayList();

        tests.add("org.eclipse.persistence.testing.tests.spatial.jgeometry.SimpleJGeometryTestModel");
        tests.add("org.eclipse.persistence.testing.tests.spatial.jgeometry.wrapped.WrappedJGeometryTestModel");
        tests.add("org.eclipse.persistence.testing.tests.unwrappedconnection.UnwrapConnectionJGeometryTestModel");
        tests.add("org.eclipse.persistence.testing.tests.unwrappedconnection.UnwrapConnectionJGeometryWrappedTestModel");

        for (int index = 0; index < tests.size(); ++index) {
            try {
                addTest((TestModel)Class.forName((String)tests.get(index)).newInstance());
            } catch (Throwable exception) {
                System.out.println("Failed to set up " + tests.get(index) + " \n" + exception);
            }
        }

        // Sort the tests alphabetically.
        Collections.sort(this.getTests(), new Comparator() {
                public int compare(Object left, Object right) {
                    return Helper.getShortClassName(left.getClass()).compareTo(Helper.getShortClassName(right.getClass()));
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

    public static OracleSpatialTestModel buildOracleTestModel() {
        OracleSpatialTestModel model = new OracleSpatialTestModel();
        return model;
    }
}
