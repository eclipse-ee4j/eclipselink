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

import java.util.HashMap;
import java.util.Map;
import javax.persistence.spi.PersistenceProvider;

import org.eclipse.persistence.testing.framework.TestProblemException;

/**
 * Performance tests that compare JPA performance.
 */
public class HibernateJPAPerformanceRegressionModel extends JPAPerformanceRegressionModel {

    public HibernateJPAPerformanceRegressionModel() {
        setDescription("Performance tests that compares Hibernate JPA performance.");
    }

    /**
     * Setup the JPA provider.
     */
    public void setupProvider() {
        // Configure provider to be Hibernate.
        String providerClass = "org.hibernate.ejb.HibernatePersistence";
        PersistenceProvider provider = null;
        try {
            provider = (PersistenceProvider)Class.forName(providerClass).newInstance();
        } catch (Exception error) {
            throw new TestProblemException("Failed to create persistence provider.", error);
        }
        Map properties = new HashMap();
        properties.put("hibernate.dialect", "org.hibernate.dialect.Oracle9Dialect");
        properties.put("hibernate.connection.driver_class", getSession().getLogin().getDriverClassName());
        properties.put("hibernate.connection.url", getSession().getLogin().getConnectionString());
        properties.put("hibernate.connection.username", getSession().getLogin().getUserName());
        properties.put("hibernate.connection.password", getSession().getLogin().getPassword());
        properties.put("hibernate.connection.pool_size", "10");
        if (getSession().shouldLogMessages()) {
            properties.put("hibernate.show_sql", "true");
        }
        getExecutor().setEntityManagerFactory(provider.createEntityManagerFactory("performance", properties));
    }
}