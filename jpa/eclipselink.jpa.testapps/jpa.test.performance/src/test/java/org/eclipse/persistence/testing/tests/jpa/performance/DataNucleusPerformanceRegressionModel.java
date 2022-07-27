/*
 * Copyright (c) 1998, 2022 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.jpa.performance;

import jakarta.persistence.spi.PersistenceProvider;
import org.eclipse.persistence.testing.framework.TestProblemException;

import java.util.HashMap;
import java.util.Map;

/**
 * Performance tests that compare JPA performance.
 */
public class DataNucleusPerformanceRegressionModel extends JPAPerformanceRegressionModel {

    public DataNucleusPerformanceRegressionModel() {
        setDescription("Performance tests that compares DataNucleus JPA performance.");
    }

    /**
     * Setup the JPA provider.
     */
    @Override
    public void setupProvider() {
        // Configure provider to be DataNucleus.
        String providerClass = "org.datanucleus.jpa.PersistenceProviderImpl";
        PersistenceProvider provider = null;
        try {
            provider = (PersistenceProvider)Class.forName(providerClass).getConstructor().newInstance();
        } catch (Exception error) {
            throw new TestProblemException("Failed to create persistence provider.", error);
        }
        Map<String, Object> properties = new HashMap<>();
        //properties.put("jakarta.persistence.nonJtaDataSource", "datasource");
        properties.put("jakarta.persistence.jdbc.driver", getSession().getLogin().getDriverClassName());
        properties.put("jakarta.persistence.jdbc.url", getSession().getLogin().getConnectionString());
        properties.put("jakarta.persistence.jdbc.user", getSession().getLogin().getUserName());
        properties.put("jakarta.persistence.jdbc.password", getSession().getLogin().getPassword());
        if (getSession().shouldLogMessages()) {
            //properties.put("openjpa.Log", "DefaultLevel=WARN,SQL=TRACE");
        }
        getExecutor().setEntityManagerFactory(provider.createEntityManagerFactory("performance", properties));
    }
}
