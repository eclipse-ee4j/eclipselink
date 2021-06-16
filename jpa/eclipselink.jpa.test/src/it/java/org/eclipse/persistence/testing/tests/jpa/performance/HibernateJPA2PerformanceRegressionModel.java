/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
 package org.eclipse.persistence.testing.tests.jpa.performance;

import java.util.HashMap;
import java.util.Map;
import jakarta.persistence.spi.PersistenceProvider;

import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.testing.framework.TestProblemException;

/**
 * Performance tests that compare JPA performance.
 */
public class HibernateJPA2PerformanceRegressionModel extends JPA2PerformanceRegressionModel {

    public HibernateJPA2PerformanceRegressionModel() {
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

        // For DataSource testing.
        //properties.put("jakarta.persistence.nonJtaDataSource", "datasource");

        // For JSE testing.
        properties.put("hibernate.connection.driver_class", getSession().getLogin().getDriverClassName());
        properties.put("hibernate.connection.url", getSession().getLogin().getConnectionString());
        properties.put("hibernate.connection.username", getSession().getLogin().getUserName());
        properties.put("hibernate.connection.password", getSession().getLogin().getPassword());
        properties.put("hibernate.connection.pool_size", "10");

        /*/ For emulated connection testing.
        try {
            Class.forName(getSession().getLogin().getDriverClassName());
        } catch (Exception ignore) {}
        properties.put("hibernate.connection.driver_class", "org.eclipse.persistence.testing.tests.performance.emulateddb.EmulatedDriver");
        properties.put("hibernate.connection.url", "emulate:" + getSession().getLogin().getConnectionString());
        properties.put("hibernate.connection.username", getSession().getLogin().getUserName());
        properties.put("hibernate.connection.password", getSession().getLogin().getPassword());
        properties.put("hibernate.connection.pool_size", "10");*/

        properties.put("hibernate.jdbc.batch_size", "100");
        properties.put("hibernate.dialect", "org.hibernate.dialect.Oracle9Dialect");
        properties.put("hibernate.cache.provider_class", "org.hibernate.cache.EhCacheProvider");
        if (getSession().getSessionLog().getLevel() <= SessionLog.FINE) {
            properties.put("hibernate.show_sql", "true");
        }
        getExecutor().setEntityManagerFactory(provider.createEntityManagerFactory("performance2", properties));
    }
}
