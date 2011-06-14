/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
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
