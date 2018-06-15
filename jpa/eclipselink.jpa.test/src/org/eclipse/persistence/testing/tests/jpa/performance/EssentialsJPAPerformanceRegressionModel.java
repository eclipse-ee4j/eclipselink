/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
 package org.eclipse.persistence.testing.tests.jpa.performance;

import java.util.HashMap;
import java.util.Map;
import javax.persistence.spi.PersistenceProvider;

import org.eclipse.persistence.testing.framework.TestProblemException;

/**
 * Performance tests that compare JPA performance.
 */
public class EssentialsJPAPerformanceRegressionModel extends JPAPerformanceRegressionModel {

    public EssentialsJPAPerformanceRegressionModel() {
        setDescription("Performance tests that compares TopLink Essentials JPA performance.");
    }

    /**
     * Setup the JPA provider.
     */
    public void setupProvider() {
        // Configure provider to be TopLink.
        String providerClass = "oracle.toplink.essentials.PersistenceProvider";
        PersistenceProvider provider = null;
        try {
            provider = (PersistenceProvider)Class.forName(providerClass).newInstance();
        } catch (Exception error) {
            throw new TestProblemException("Failed to create persistence provider.", error);
        }
        Map properties = new HashMap();
        properties.put("toplink.jdbc.driver", getSession().getLogin().getDriverClassName());
        properties.put("toplink.jdbc.url", getSession().getLogin().getConnectionString());
        properties.put("toplink.jdbc.user", getSession().getLogin().getUserName());
        properties.put("toplink.jdbc.password", getSession().getLogin().getPassword());
        properties.put("toplink.logging.level", getSession().getSessionLog().getLevelString());
        getExecutor().setEntityManagerFactory(provider.createEntityManagerFactory("performance", properties));
    }
}
