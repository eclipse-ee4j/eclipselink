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

import java.util.Map;

/**
 * Performance tests that compare JPA performance.
 */
public class JPA2IsolatedPerformanceRegressionModel extends JPA2PerformanceRegressionModel {

    public JPA2IsolatedPerformanceRegressionModel() {
        setDescription("Performance tests that compares JPA performance.");
    }
    
    /**
     * Build the persistence properties.
     */
    public Map getPersistenceProperties() {    
        Map properties = super.getPersistenceProperties();
        properties.put("eclipselink.cache.shared.default", "false");
        return properties;
    }
}
