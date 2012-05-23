/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
public class DataNucleusPerformanceRegressionModel extends JPAPerformanceRegressionModel {

    public DataNucleusPerformanceRegressionModel() {
        setDescription("Performance tests that compares DataNucleus JPA performance.");
    }

    /**
     * Setup the JPA provider.
     */
    public void setupProvider() {
        // Configure provider to be DataNucleus.
        String providerClass = "org.datanucleus.jpa.PersistenceProviderImpl";
        PersistenceProvider provider = null;
        try {
            provider = (PersistenceProvider)Class.forName(providerClass).newInstance();
        } catch (Exception error) {
            throw new TestProblemException("Failed to create persistence provider.", error);
        }
        Map properties = new HashMap();
        //properties.put("javax.persistence.nonJtaDataSource", "datasource");
        properties.put("javax.persistence.jdbc.driver", getSession().getLogin().getDriverClassName());
        properties.put("javax.persistence.jdbc.url", getSession().getLogin().getConnectionString());
        properties.put("javax.persistence.jdbc.user", getSession().getLogin().getUserName());
        properties.put("javax.persistence.jdbc.password", getSession().getLogin().getPassword());
        if (getSession().shouldLogMessages()) {
            //properties.put("openjpa.Log", "DefaultLevel=WARN,SQL=TRACE");
        }
        getExecutor().setEntityManagerFactory(provider.createEntityManagerFactory("performance", properties));
    }
}
