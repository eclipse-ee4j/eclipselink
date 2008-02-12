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

import java.util.Map;

/**
 * Performance tests that compare JPA performance.
 */
public class JPAIsolatedPerformanceRegressionModel extends JPAPerformanceRegressionModel {

    public JPAIsolatedPerformanceRegressionModel() {
        setDescription("Performance tests that compares JPA performance.");
    }
    
    /**
     * Build the persistence properties.
     */
    public Map getPersistenceProperties() {    
        Map properties = super.getPersistenceProperties();
        properties.put("eclipselink.cache.shared.default", "false");
        properties.put("eclipselink.jdbc.batch-writing", "JDBC");
        return properties;
    }
}