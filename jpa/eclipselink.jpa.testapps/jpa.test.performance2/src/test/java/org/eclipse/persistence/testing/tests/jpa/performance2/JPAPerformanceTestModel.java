/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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
 package org.eclipse.persistence.testing.tests.jpa.performance2;

import org.eclipse.persistence.testing.framework.TestModel;
import org.eclipse.persistence.testing.tests.jpa.performance.EssentialsJPAPerformanceRegressionModel;
import org.eclipse.persistence.testing.tests.jpa.performance.HibernateJPAConcurrencyComparisonModel;
import org.eclipse.persistence.testing.tests.jpa.performance.HibernateJPAPerformanceRegressionModel;
import org.eclipse.persistence.testing.tests.jpa.performance.JPAConcurrencyComparisonModel;
import org.eclipse.persistence.testing.tests.jpa.performance.JPAEmulatedIsolatedPerformanceRegressionModel;
import org.eclipse.persistence.testing.tests.jpa.performance.JPAEmulatedPerformanceRegressionModel;
import org.eclipse.persistence.testing.tests.jpa.performance.JPAIsolatedPerformanceRegressionModel;
import org.eclipse.persistence.testing.tests.jpa.performance.JPAPerformanceComparisonModel;
import org.eclipse.persistence.testing.tests.jpa.performance.JPAPerformanceRegressionModel;
import org.eclipse.persistence.testing.tests.jpa.performance.OpenJPAPerformanceRegressionModel;
import org.eclipse.persistence.testing.tests.jpa.performance.TopLinkJPAPerformanceRegressionModel;

/**
 * Performance tests that compare JPA performance.
 */
public class JPAPerformanceTestModel extends TestModel {

    public JPAPerformanceTestModel() {
        setDescription("Performance tests that compare Jakarta Persistence performance.");
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
