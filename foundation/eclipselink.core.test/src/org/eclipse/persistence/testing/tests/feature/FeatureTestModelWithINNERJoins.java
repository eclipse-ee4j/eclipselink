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
package org.eclipse.persistence.testing.tests.feature;

/**
 * Test the feature tests with print INNER joins in the FROM clause.
 */

public class FeatureTestModelWithINNERJoins extends FeatureTestModel {

    public FeatureTestModelWithINNERJoins() {
        setDescription("Test the feature tests with print INNER joins in the FROM clause.");
    }

    public void reset() {
        getSession().getPlatform().setPrintInnerJoinInWhereClause(true);
    }

    public void setup() {
        getSession().getPlatform().setPrintInnerJoinInWhereClause(false);
    }

}

