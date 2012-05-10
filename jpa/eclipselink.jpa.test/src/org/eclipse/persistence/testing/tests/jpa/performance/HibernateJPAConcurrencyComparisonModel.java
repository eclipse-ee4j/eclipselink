/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     James Sutherland - initial impl
 ******************************************************************************/  
 package org.eclipse.persistence.testing.tests.jpa.performance;

import java.util.HashMap;
import java.util.Map;
import javax.persistence.spi.PersistenceProvider;

import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.testing.framework.TestProblemException;

/**
 * Performance tests that compare JPA concurrency.
 */
public class HibernateJPAConcurrencyComparisonModel extends JPAConcurrencyComparisonModel {

    public HibernateJPAConcurrencyComparisonModel() {
        setDescription("Performance tests that compares Hibernate JPA concurrency.");
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
        //properties.put("javax.persistence.nonJtaDataSource", "datasource");
                
        // For JSE testing.
        properties.put("hibernate.connection.driver_class", getSession().getLogin().getDriverClassName());
        properties.put("hibernate.connection.url", getSession().getLogin().getConnectionString());
        properties.put("hibernate.connection.username", getSession().getLogin().getUserName());
        properties.put("hibernate.connection.password", getSession().getLogin().getPassword());
        properties.put("hibernate.connection.pool_size", "32");
        
        /*/ For emulated connection testing.
        try {
            Class.forName(getSession().getLogin().getDriverClassName());
        } catch (Exception ignore) {}
        properties.put("hibernate.connection.driver_class", "org.eclipse.persistence.testing.tests.performance.emulateddb.EmulatedDriver");
        properties.put("hibernate.connection.url", "emulate:" + getSession().getLogin().getConnectionString());
        properties.put("hibernate.connection.username", getSession().getLogin().getUserName());
        properties.put("hibernate.connection.password", getSession().getLogin().getPassword()); 
        properties.put("hibernate.connection.pool_size", "32");*/
        
        properties.put("hibernate.jdbc.batch_size", "100");
        properties.put("hibernate.dialect", "org.hibernate.dialect.Oracle9Dialect");
        properties.put("hibernate.cache.provider_class", "org.hibernate.cache.EhCacheProvider");
        //properties.put("hibernate.cache.use_second_level_cache", "true");
        //properties.put("hibernate.cache.use_structured_entries", "true");
        if (getSession().getSessionLog().getLevel() <= SessionLog.FINE) {
            properties.put("hibernate.show_sql", "true");
        }
        getExecutor().setEntityManagerFactory(provider.createEntityManagerFactory("performance", properties));
    }
}
