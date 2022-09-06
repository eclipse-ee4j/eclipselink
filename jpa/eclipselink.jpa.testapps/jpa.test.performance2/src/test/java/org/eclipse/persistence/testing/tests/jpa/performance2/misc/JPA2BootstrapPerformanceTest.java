/*
 * Copyright (c) 2011, 2022 Oracle and/or its affiliates. All rights reserved.
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
//     James Sutherland (Oracle) - initial API and implementation
 package org.eclipse.persistence.testing.tests.jpa.performance2.misc;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;
import org.eclipse.persistence.testing.framework.PerformanceRegressionTestCase;

import java.util.Map;

/**
 * This test compares the performance of EntityManagerFactory creation.
 * This only measure deploy, not predeploy.
 */
public class JPA2BootstrapPerformanceTest extends PerformanceRegressionTestCase {

    public JPA2BootstrapPerformanceTest() {
        setDescription("This tests the Jakarta Persistence deployment and bootstraping performance.");
    }

    /**
     * Rebuild EntityManagerFactory.
     */
    @Override
    public void test() {
        getExecutor().getEntityManagerFactory().close();
        getExecutor().setEntityManagerFactory(null);
        @SuppressWarnings({"unchecked"})
        Map<String, Object> properties = getExecutor().getEntityManagerProperties();
        getExecutor().setEntityManagerFactory(Persistence.createEntityManagerFactory("performance2", properties));
        EntityManager manager = createEntityManager();
        manager.close();
    }
}
