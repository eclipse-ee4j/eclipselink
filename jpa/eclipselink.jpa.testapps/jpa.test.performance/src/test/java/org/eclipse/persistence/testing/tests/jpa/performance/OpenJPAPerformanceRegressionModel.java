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

import jakarta.persistence.EntityManager;
import jakarta.persistence.spi.PersistenceProvider;
import org.eclipse.persistence.testing.framework.TestProblemException;
import org.eclipse.persistence.testing.models.jpa.performance.EmployeeTableCreator;

import java.util.HashMap;
import java.util.Map;

/**
 * Performance tests that compare JPA performance.
 */
public class OpenJPAPerformanceRegressionModel extends JPAPerformanceRegressionModel {

    public OpenJPAPerformanceRegressionModel() {
        setDescription("Performance tests that compares OpenJPA JPA performance.");
    }

    /**
     * Setup the JPA provider.
     */
    @Override
    public void setupProvider() {
        // Configure provider to be OpenJPA.
        String providerClass = "org.apache.openjpa.persistence.PersistenceProviderImpl";
        PersistenceProvider provider = null;
        try {
            provider = (PersistenceProvider)Class.forName(providerClass).getConstructor().newInstance();
        } catch (Exception error) {
            throw new TestProblemException("Failed to create persistence provider.", error);
        }
        Map<String, Object> properties = new HashMap<>();
        //properties.put("jakarta.persistence.nonJtaDataSource", "datasource");
        properties.put("openjpa.ConnectionDriverName", getSession().getLogin().getDriverClassName());
        properties.put("openjpa.ConnectionURL", getSession().getLogin().getConnectionString());
        properties.put("openjpa.ConnectionUserName", getSession().getLogin().getUserName());
        properties.put("openjpa.ConnectionPassword", getSession().getLogin().getPassword());
        if (getSession().shouldLogMessages()) {
            properties.put("openjpa.Log", "DefaultLevel=WARN,SQL=TRACE");
        }
        getExecutor().setEntityManagerFactory(provider.createEntityManagerFactory("performance", properties));
    }

    // Open JPA does not seem to support constraints correctly.
    @Override
    public void setupDatabase(EntityManager manager) {
        super.setupDatabase(manager);
        new EmployeeTableCreator().dropConstraints(getDatabaseSession());
    }
}
