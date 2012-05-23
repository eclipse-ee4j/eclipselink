/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     James Sutherland (Oracle) - initial API and implementation
 ******************************************************************************/  
 package org.eclipse.persistence.testing.tests.jpa.performance.misc;

import java.util.Map;

import javax.persistence.*;

import org.eclipse.persistence.testing.framework.*;

/**
 * This test compares the performance of EntityManagerFactory creation.
 * This only measure deploy, not predeploy.
 */
public class JPA2BootstrapPerformanceTest extends PerformanceRegressionTestCase {

    public JPA2BootstrapPerformanceTest() {
        setDescription("This tests the JPA deployment and bootstraping performance.");
    }

    /**
     * Rebuild EntityManagerFactory.
     */
    public void test() throws Exception {
        getExecutor().getEntityManagerFactory().close();
        getExecutor().setEntityManagerFactory(null);
        Map properties = getExecutor().getEntityManagerProperties();
        getExecutor().setEntityManagerFactory(Persistence.createEntityManagerFactory("performance2", properties));
        EntityManager manager = createEntityManager();
        manager.close();
    }
}
