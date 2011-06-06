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

import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.spi.PersistenceProvider;

import org.eclipse.persistence.testing.framework.TestProblemException;
import org.eclipse.persistence.testing.models.jpa.performance.EmployeeTableCreator;

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
    public void setupProvider() {
        // Configure provider to be OpenJPA.
        String providerClass = "org.apache.openjpa.persistence.PersistenceProviderImpl";
        PersistenceProvider provider = null;
        try {
            provider = (PersistenceProvider)Class.forName(providerClass).newInstance();
        } catch (Exception error) {
            throw new TestProblemException("Failed to create persistence provider.", error);
        }
        Map properties = new HashMap();
        //properties.put("javax.persistence.nonJtaDataSource", "datasource");
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
    public void setupDatabase(EntityManager manager) {
        super.setupDatabase(manager);
        new EmployeeTableCreator().dropConstraints(getDatabaseSession());
    }
}
