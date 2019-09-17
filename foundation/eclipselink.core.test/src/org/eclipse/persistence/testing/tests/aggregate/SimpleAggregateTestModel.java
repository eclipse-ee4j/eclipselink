/*
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.persistence.testing.tests.aggregate;

import org.eclipse.persistence.testing.framework.TestModel;
import org.eclipse.persistence.testing.framework.TestSuite;
import org.eclipse.persistence.testing.models.aggregate.SimpleAggregateSystem;

public class SimpleAggregateTestModel extends TestModel {
    public SimpleAggregateTestModel() {
        setDescription("This model tests reading/writing/deleting of the simple aggregate model.");
    }

    @Override
    public void addForcedRequiredSystems() {
        //We need to ensure that the correct database schema is created
        addForcedRequiredSystem(new SimpleAggregateSystem());
    }

    @Override
    public void addRequiredSystems() {
    }

    @Override
    public void addTests() {
        addTest(getUpdateObjectTestSuite());
    }

    public static TestSuite getUpdateObjectTestSuite() {
        TestSuite suite = new TestSuite();
        suite.setName("AggregateUpdateObjectTestSuite");
        suite.setDescription("This suite tests the updating of each object in the aggregate model.");

        return suite;
    }

    public static junit.framework.TestSuite suite() {
        return new SimpleAggregateTestModel();
    }
}
