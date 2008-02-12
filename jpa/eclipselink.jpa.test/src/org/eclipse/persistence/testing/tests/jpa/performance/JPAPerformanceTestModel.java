/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
 package org.eclipse.persistence.testing.tests.jpa.performance;

import org.eclipse.persistence.testing.framework.*;

/**
 * Performance tests that compare JPA performance.
 */
public class JPAPerformanceTestModel extends TestModel {

    public JPAPerformanceTestModel() {
        setDescription("Performance tests that compare JPA performance.");
        addTest(new JPAPerformanceRegressionModel());
        addTest(new JPAIsolatedPerformanceRegressionModel());
        addTest(new HibernateJPAPerformanceRegressionModel());
        addTest(new EssentialsJPAPerformanceRegressionModel());
    }
}