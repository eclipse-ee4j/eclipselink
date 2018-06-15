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
 package org.eclipse.persistence.testing.tests.jpa.performance;

import org.eclipse.persistence.testing.framework.*;

/**
 * Performance tests that compare JPA performance.
 */
public class JPAPerformanceTestModel extends TestModel {

    public JPAPerformanceTestModel() {
        setDescription("Performance tests that compare JPA performance.");
        addTest(new JPAPerformanceRegressionModel());
        addTest(new JPA2PerformanceRegressionModel());
        addTest(new JPAPerformanceComparisonModel());
        addTest(new JPAIsolatedPerformanceRegressionModel());
        addTest(new JPAEmulatedPerformanceRegressionModel());
        addTest(new JPAEmulatedIsolatedPerformanceRegressionModel());
        addTest(new JPAConcurrencyComparisonModel());
        addTest(new HibernateJPAPerformanceRegressionModel());
        addTest(new HibernateJPAConcurrencyComparisonModel());
        addTest(new TopLinkJPAPerformanceRegressionModel());
        addTest(new EssentialsJPAPerformanceRegressionModel());
        addTest(new OpenJPAPerformanceRegressionModel());
    }
}
